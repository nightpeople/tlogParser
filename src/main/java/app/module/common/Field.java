package app.module.common;

import com.google.common.base.Strings;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import app.module.TableComparator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Field {

    private static final Pattern SIZE_PATTERN = Pattern.compile("\\((.+?)\\)");

    public String table;

    public String name;

    /**
     * 当前类型只有
     * varchar, int, tinyint, bigint, datetime
     */
    public String type;

    public int size;

    public boolean unsigned;

    public boolean notNull;

    public String key;

    /**
     * 字段默认值
     * NULL是没有默认值
     */
    public String _default;

    public boolean autoIncrement;

    public String desc;

    /**
     * 字符集, null说明用默认值或者从tlog.xml初始化
     * 如果db中类型为utf8mb3,则会强制转换为utf8mb4及utf8mb4_0900_ai_ci
     */
    public String character;

    /**
     * tlog type属性 mysql字段类型对照表
     * tlogType : mysqlType
     */
    public static final HashMap<String, String> typeParser = new HashMap<>();

    /**
     * mysql type 对应的长度
     * type : size
     */
    public static final HashMap<String, Integer> typeSize = new HashMap<>();

    public static final HashMap<String, String> convert2Integer = new HashMap<>();

    /**
     * 下个字段
     * 目前只实现了从DBLoader构建这个字段
     * TODO 从xmlParser构建这个字段
     */
    public Field next;

    static {
        typeParser.put("string", "varchar");
        typeParser.put("uint", "int");
        typeParser.put("int", "int");
        typeParser.put("utinyint", "tinyint");
        typeParser.put("tinyint", "tinyint");
        typeParser.put("bigint", "bigint");
        typeParser.put("datetime", "datetime");
        typeParser.put("date", "date");

        typeSize.put("varchar", 255);
        typeSize.put("int", 11);
        typeSize.put("tinyint", 3);
        typeSize.put("bigint", 20);
        typeSize.put("datetime", 0); //0不注明长度
        typeSize.put("date", 0);

        convert2Integer.put("int", "int");
        convert2Integer.put("tinyint", "tinyint");
        convert2Integer.put("bigint", "bigint");
    }

    /**
     * 用于tlog table实例化
     */
    public Field(String name, String rawType, String desc, String table) {
        this.name = name;
        this.desc = desc;
        this.table = table;
        parseType(rawType, name, table);
    }

    /**
     * 用于mysql表实例化
     * desc tableName;字段
     */
    public Field(String fieldName, String typeConf, boolean notNull, String key, String _default, String extra, String tableName) {
        this.name = fieldName;
        this.unsigned = typeConf.contains("unsigned");
        String rawType = typeConf;
        Matcher matcher = SIZE_PATTERN.matcher(typeConf);
        if (matcher.find()) {
            this.size = Integer.parseInt(matcher.group(1));
            //去掉size内容
            rawType = matcher.replaceFirst("");
        }
        String[] split = rawType.split(" ");
        this.type = parseType(split[0]);

        this.notNull = notNull;
        this.key = key;
        this._default = _default;
        this.autoIncrement = extra.contains("auto_increment");
        this.table = tableName;
    }

    public void setCharacter(String character) {
        this.character = character;
    }

    public void parseType(String rawType, String name, String table) {
        this.type = parseType(rawType);

        Integer size = typeSize.get(type);
        if (size == null) {
            log.error("{}类型没有指定对应的size长度", type);
            throw new IllegalArgumentException(type + "类型没有指定对应的size长度");
        }
        this.size = size;
    }

    public String parseType(String rawType) {
        String type;
        if (typeSize.containsKey(rawType)) {
            type = rawType;
        } else {
            type = typeParser.get(rawType);
        }
        if (type == null) {
            log.error("tlog {}表 {}字段, type有未识别的类型: {}", table, name, rawType);
            throw new IllegalArgumentException("tlog " + table + "表 " + name + "字段, type有未识别的类型: " + rawType);
        }
        return type;
    }

    public void dealSetCharacter(String createTableStr) {
        if ("varchar".equals(type)) {
            Pattern compile = Pattern.compile('`' + name + "` .* CHARACTER SET utf8mb3", Pattern.CASE_INSENSITIVE); //忽略大小写
            Matcher matcher = compile.matcher(createTableStr);
            if (matcher.find()) {
                setCharacter("utf8mb3");
            }
        }
    }

    public void compareAndAlter(DataSource dataSource) throws SQLException {
        //表字段的字符编码是utf8mb3则改为utf8mb4/排序utf8mb4_0900_ai_ci
        if (!Strings.isNullOrEmpty(character) && "utf8mb3".equals(character)) {
            TableComparator.alterField(this, dataSource, table, type);
        }

        //其他可能需要修复的...
    }

    @Override
    public String toString() {
        return name + "  " + type + (size > 0 ? "(" + size + ")" : "") + " " + (unsigned ? "unsigned" : "") + "  " + (notNull ? "not Null" : "") +
                "  " + ("PRI".equalsIgnoreCase(key) ? "primaryKey  " : "") +
                (!Strings.isNullOrEmpty(_default) && !"NULL".equalsIgnoreCase(_default) ? "Default: " + _default + "  " : "") +
                (autoIncrement ? "auto_increment" : "") + "\n";
    }
}
