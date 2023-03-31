package app.module;

import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import app.module.common.Field;
import app.module.common.Table;

/**
 * tlog日志表对比工具
 * 新增,修改,删除表字段;添加新表
 */
public class TableComparator {
    private static final Logger logger = LoggerFactory.getLogger(TableComparator.class);

    /**
     * 对比,有新表就创建,有新字段就添加,字段类型有修改就更新,剔除字段就删掉
     */
    public static void compareAndAdd(DBLoader dbLoader, XMLParser xmlParser, DataSource dataSource, FixedSqlParser fixedSqlParser)
            throws SQLException {
        processFixedSql(dbLoader, dataSource, fixedSqlParser);

        logger.info("开始用tlog表 对比 数据库表");
        for (Table tlogTable : xmlParser.tables.values()) {
            //创建表
            if (!dbLoader.tables.containsKey(tlogTable.name)) {
                createTable(tlogTable, dataSource);
                continue;
            }
            //已有表则对比字段
            Table dbTable = dbLoader.tables.get(tlogTable.name);
            dbTable.resetFieldIdx();
            //以新表tlogTable为主视角, dbTable为对比组
            Field tlogPre = null;
            Field compareField = dbTable.getFirstField();
            for (Field tlogField : tlogTable.fields.values()) {
                //先单独过滤删除字段逻辑
                while (compareField != null && !Objects.equals(tlogField.name, compareField.name) &&
                        !tlogTable.fields.containsKey(compareField.name)) {
                    deleteField(compareField, dataSource, dbTable.name);
                    compareField = dbTable.nextField();
                }

                if (compareField == null) {
                    //在表末尾添加字段
                    if (!dbTable.fields.containsKey(tlogField.name)) {
                        //新增字段
                        addField(tlogField, dataSource, tlogTable.name, (tlogPre != null) ? tlogPre.name : null);
                    }
                } else if (Objects.equals(tlogField.name, compareField.name) && !Objects.equals(tlogField.type, compareField.type)) {
                    //更新字段类型
                    alterField(tlogField, dataSource, tlogTable.name, compareField.type);
                    compareField = dbTable.nextField();
                } else if (!Objects.equals(tlogField.name, compareField.name)) {
                    //新增字段
                    addField(tlogField, dataSource, tlogTable.name, (tlogPre != null) ? tlogPre.name : null);
                } else {
                    //指向下一个
                    compareField = dbTable.nextField();
                }
                tlogPre = tlogField;
            }
            //对比完后,对比表仍有字段,则是队尾需要删除的字段
            while (compareField != null) {
                deleteField(compareField, dataSource, dbTable.name);
                compareField = dbTable.nextField();
            }
        }
    }

    public static void createTable(Table table, DataSource dataSource) throws SQLException {
        logger.info("创建tlog.xml中表: {}", table.name);
        String sql = buildTableSql(table);
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                int i = ps.executeUpdate();
            }
        }
    }

    private static String buildTableSql(Table table) {
        StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS `" + table.name + "` (\n");
        //第1个字段是固定的主键id,bigint
        builder.append("`id` bigint unsigned NOT NULL AUTO_INCREMENT,\n");
        String _default = "DEFAULT NULL";
        for (Field field : table.fields.values()) {
            //拼类型和长度
            StringBuilder typeConcat = new StringBuilder(field.type);
            if ("varchar".equals(field.type)) {
                typeConcat.append('(').append(field.size).append(") CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci");
            }
            builder.append('`').append(field.name).append("` ").append(typeConcat).append(" ").append(_default).append(",\n");
        }
        //最后字段是固定主键dt,date
        builder.append("`dt` date NOT NULL,\n");
        //拼主键,引擎,编码
        builder.append("PRIMARY KEY (`id`,`dt`) USING BTREE\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3");
        return builder.toString();
    }

    private static void addField(Field field, DataSource dataSource, String tableName, String afterWho) throws SQLException {
        logger.info("{}表, 新增字段: {}", tableName, field.name);
        StringBuilder builder = new StringBuilder("ALTER TABLE " + tableName + " ADD " + field.name);
        StringBuilder typeConcat = new StringBuilder(field.type);
        if ("varchar".equals(field.type)) {
            typeConcat.append('(').append(field.size).append(") CHARACTER SET utf8mb3 COLLATE utf8mb3_general_ci");
        }
        builder.append(" ").append(typeConcat);
        if (!Strings.isNullOrEmpty(afterWho)) {
            builder.append(" after ").append(afterWho);
        } else {
            //按理说是不会走到这里
            builder.append(" first");
        }
        String sql = builder.toString();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                int i = preparedStatement.executeUpdate();
            }
        }
    }

    private static void alterField(Field field, DataSource dataSource, String tableName, String oldType) throws SQLException {
        logger.info("{}表, {}字段类型变更: {} --> {}", tableName, field.name, oldType, field.type);
        StringBuilder builder = new StringBuilder("ALTER TABLE " + tableName + " MODIFY " + field.name);
        StringBuilder typeConcat = new StringBuilder(field.type);
        if ("varchar".equals(field.type)) {
            typeConcat.append('(').append(field.size).append(')');
        }
        builder.append(" ").append(typeConcat);
        String sql = builder.toString();
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                int i = preparedStatement.executeUpdate();
            }
        }
    }

    private static void deleteField(Field field, DataSource dataSource, String tableName) throws SQLException {
        logger.info("{}表, 删除字段: {}", tableName, field.name);
        sqlUpdate("ALTER TABLE " + tableName + " DROP " + field.name, dataSource);
    }

    private static void processFixedSql(DBLoader dbLoader, DataSource dataSource, FixedSqlParser fixedSqlParser) throws SQLException {
        logger.info("开始检查固定sql模块");
        for (Entry<String, String> entry : fixedSqlParser.dropSql.entrySet()) {
            String tableName = entry.getKey();
            logger.info("删除fixed.sql指定表: {}", tableName);
            sqlUpdate(entry.getValue(), dataSource);
        }
        for (Entry<String, String> entry : fixedSqlParser.createSql.entrySet()) {
            String tableName = entry.getKey();
            if (!dbLoader.tables.containsKey(tableName) || fixedSqlParser.dropSql.containsKey(tableName)) {
                //创建表
                logger.info("创建fixed.sql内的表: {}", tableName);
                sqlUpdate(entry.getValue(), dataSource);
                Pattern pt = Pattern.compile(tableName, Pattern.CASE_INSENSITIVE);
                //遍历执行该表的所有DML语句
                for (String query : fixedSqlParser.dml) {
                    Matcher matcher = pt.matcher(query);
                    if (matcher.find()) {
                        logger.info("执行fixed.sql中DML语句: {}", query);
                        sqlUpdate(query, dataSource);
                    }
                }
            }
        }
    }

    private static int sqlUpdate(String sql, DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                return preparedStatement.executeUpdate();
            }
        }
    }
}
