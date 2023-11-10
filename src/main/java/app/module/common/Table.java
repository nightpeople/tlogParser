package app.module.common;

import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import static app.module.DBLoader.FIXED_FIELDS;

/**
 * 表信息
 */
public class Table {

    private static final Logger logger = LoggerFactory.getLogger(Table.class);

    public static final Pattern TABLE_CHARACTER_PATTERN = Pattern.compile("DEFAULT CHARSET=utf8mb3", Pattern.CASE_INSENSITIVE); //忽略大小写

    /**
     * 所有普通索引的匹配模式
     * 索引名 - pattern
     */
    public static final HashMap<String, Pattern> INDEX_PATTERN_MAP = new HashMap<>();

    /**
     * 表名,可能不区分大小写
     */
    public String name;

    /**
     * tlog.xml中原始表名
     * 仅用于输出fluentd配置
     */
    public String rawName;

    public String desc;

    /**
     * 字符集, null说明用默认值或者从tlog.xml初始化
     * 如果db中类型为utf8mb3,则会强制转换为utf8mb4及utf8mb4_0900_ai_ci
     */
    public String character;

    /**
     * 创表语句, 从DBLoader实例化的Table才有
     */
    public String createTableStr;

    /**
     * 用于fluentd数据库output配置
     * int类型的字段
     */
    public ArrayList<String> intFields;

    public LinkedHashMap<String, Field> fields;

    public static final HashMap<String, String> needBiggerMemoryCacheTables = new HashMap<>();

    public static final StringBuilder outputBiggerMemoryCacheNode = new StringBuilder();

    /**
     * 普通索引配置
     * 索引名 - [索引字段及顺序]
     * a.table没有该索引 -> 判断索引字段是否都在table中 -> 加索引
     * b.新建表table -> 判断索引字段是否都在table中 -> 添加索引
     */
    public static final HashMap<String, String[]> indexConf = new HashMap<>();

    /**
     * 表的首字段
     */
    private Field firstField;

    /**
     * 当前索引指向的字段
     */
    private Field idxField;

    static {
        needBiggerMemoryCacheTables.put("PropFlow", "PropFlow");
        needBiggerMemoryCacheTables.put("MoneyFlow", "MoneyFlow");

        outputBiggerMemoryCacheNode.append("  <buffer tag>\n");
        outputBiggerMemoryCacheNode.append("    @type memory\n");
        outputBiggerMemoryCacheNode.append("    total_limit_size 1GB\n");
        outputBiggerMemoryCacheNode.append("  </buffer>\n");

        indexConf.put("index_dt_iworldid", new String[]{"dt", "iWorldId"});
        indexConf.put("index_iworldid", new String[]{"iWorldId"});
        indexConf.put("index_imergeid", new String[]{"iMergeId"});

        for (String indexName : indexConf.keySet()) {
            Pattern pattern = Pattern.compile("KEY `" + indexName + '`', Pattern.CASE_INSENSITIVE);//忽略大小写
            INDEX_PATTERN_MAP.put(indexName, pattern);
        }
    }

    public Table(String name, String desc, boolean lowerCase, String rawName) {
        if (lowerCase) {
            this.name = name.toLowerCase();
        } else {
            this.name = name;
        }
        this.desc = desc;
        intFields = new ArrayList<>();
        fields = new LinkedHashMap<>();
        this.rawName = rawName;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public void setCreateTableStr(String createTableStr) {
        this.createTableStr = createTableStr;
    }

    public Field addFields(String name, Field field) {
        return fields.put(name, field);
    }

    public boolean addIntFields(String field) {
        return intFields.add(field);
    }

    public void inputBuild(StringBuilder builder) {
        builder.append("<filter tlog.").append(rawName).append(">\n");
        builder.append("  @type parser\n");
        builder.append("  key_name content\n");
        builder.append("  <parse>\n");
        builder.append("    @type csv\n");
        builder.append("    keys ").append(String.join(",", fields.keySet())).append("\n");
        builder.append("    delimiter |\n");
        builder.append("    parser_type fast\n");
        builder.append("    types ");
        for (String fld : intFields) {
            builder.append(fld).append(":integer,");
        }
        //删最后一个逗号
        builder.deleteCharAt(builder.length() - 1);
        builder.append("\n");
        builder.append("  </parse>\n");
        builder.append("</filter>\n");
        //留空行
        builder.append("\n");
    }

    public void outputBuild(StringBuilder builder) {
        builder.append("<match tlog.").append(rawName).append(">\n");
        builder.append("  @type mysql_bulk\n");
        builder.append("  @include config.d/outputDB.conf\n");
        builder.append("  column_names ").append(String.join(",", fields.keySet())).append(",dt\n"); //加上dt分区字段
        builder.append("  table ").append(rawName).append("\n");
        if (needBiggerMemoryCacheTables.containsKey(rawName)) {
            builder.append(outputBiggerMemoryCacheNode);
        }
        builder.append("</match>\n");

        builder.append("\n");
    }

    /**
     * 指到下个字段
     * 跳过id,dt字段
     */
    public Field nextField() {
        if (idxField == null) {
            return null;
        }
        idxField = idxField.next;
        while (idxField != null && Utils.contains(FIXED_FIELDS, idxField.name)) {
            idxField = idxField.next;
        }
        return idxField;
    }

    public Field getCurField() {
        return idxField;
    }

    public void resetFieldIdx() {
        idxField = getFirstField();
    }

    /**
     * 获取第一个非id字段
     */
    public Field getFirstField() {
        if (firstField == null) {
            Iterator<Entry<String, Field>> iterator = fields.entrySet().iterator();
            firstField = iterator.next().getValue();
            if ("id".equals(firstField.name)) {
                firstField = iterator.next().getValue();
            }
        }
        return firstField;
    }

    public void compareAndAlter(DataSource dataSource) throws SQLException {
        //若表的字符编码是utf8mb3则改为utf8mb4/排序utf8mb4_0900_ai_ci
        if (!Strings.isNullOrEmpty(character) && "utf8mb3".equals(character)) {
            logger.info("{}表, 默认字符集变更: utf8mb3 --> utf8mb4", name);
            String sql = "ALTER TABLE `" + name + "` CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci";
            Utils.sqlUpdate(sql, dataSource);
        }

        //添加普通索引
        for (Entry<String, String[]> entry : indexConf.entrySet()) {
            String indexName = entry.getKey();
            String[] indexFields = entry.getValue();
            Pattern pattern = INDEX_PATTERN_MAP.get(indexName);
            if (pattern == null) {
                continue;
            }
            Matcher matcher = pattern.matcher(createTableStr);
            if (!matcher.find()) {
                String indexField = buildIndexField(indexFields);
                //返回空字符串, 说明有索引字段在表中不存在
                if (!Strings.isNullOrEmpty(indexField)) {
                    logger.info("{}表, 添加普通索引: {}", name, indexName);
                    String sql = "ALTER TABLE `" + name + "` ADD INDEX `" + indexName + "` " + indexField;
                    Utils.sqlUpdate(sql, dataSource);
                }
            }
        }

        //其他可能要修复的...

    }

    public String buildIndexField(String[] indexFields) {
        StringBuilder builder = new StringBuilder("(");
        for (String field : indexFields) {
            //索引字段必须都在表中,否则会报错
            String dbField = Utils.equalsIgnoreCaseAndGet(field, this.fields.keySet().iterator());
            if (Strings.isNullOrEmpty(dbField)) {
                return "";
            }
            builder.append('`').append(dbField).append("`,");
        }
        //删最后一个逗号
        builder.deleteCharAt(builder.length() - 1);
        builder.append(')');
        return builder.toString();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(rawName).append(": ").append(desc).append("\n");
        for (Field field : fields.values()) {
            builder.append(field.toString());
        }
        return builder.toString();
    }

}
