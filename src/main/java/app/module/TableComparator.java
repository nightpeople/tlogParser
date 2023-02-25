package app.module;

import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;

import javax.sql.DataSource;

import app.module.common.Field;
import app.module.common.Table;

/**
 * tlog日志表对比工具
 * 新增,修改表字段;添加新表
 */
public class TableComparator {
    private static final Logger logger = LoggerFactory.getLogger(TableComparator.class);

    /**
     * 对比,有新表就创建,有新字段就添加,字段类型有修改就更新
     */
    public static void compareAndAdd(DBLoader dbLoader, XMLParser xmlParser, DataSource dataSource) throws SQLException {
        for (Table tlogTable : xmlParser.tables.values()) {
            //创建表
            if (!dbLoader.tables.containsKey(tlogTable.name)) {
                createTable(tlogTable, dataSource);
                continue;
            }
            //已有表则对比字段
            Table dbTable = dbLoader.tables.get(tlogTable.name);
            //以新表tlogTable为主视角, dbTable为对比组
            Field tlogPre = null;
            Field compareField = dbTable.getFirstField();
            for (Field tlogField : tlogTable.fields.values()) {
                if (compareField == null) {
                    //在表末尾添加字段
                    if (dbTable.fields.containsKey(tlogField.name)) {
                        //新增字段
                        addField(tlogField, dataSource, tlogTable.name, (tlogPre != null) ? tlogPre.name : null);
                    }
                } else if (Objects.equals(tlogField.name, compareField.name) && !Objects.equals(tlogField.type, compareField.type)) {
                    //更新字段类型
                    alterField(tlogField, dataSource, tlogTable.name);
                    compareField = dbTable.nextField();
                } else if (!Objects.equals(tlogField.name, compareField.name)) {
                    //新增字段
                    addField(tlogField, dataSource, tlogTable.name, (tlogPre != null) ? tlogPre.name : null);
                } else {
                    compareField = dbTable.nextField();
                }
                tlogPre = tlogField;
            }

        }
    }

    public static void createTable(Table table, DataSource dataSource) throws SQLException {
        String sql = buildTableSql(table);
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                int i = ps.executeUpdate();
            }
        }
    }

    private static String buildTableSql(Table table) {
        StringBuilder builder = new StringBuilder("CREATE TABLE IF NOT EXISTS `" + table.name + "`(\n");
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
        StringBuilder builder = new StringBuilder("ALTER TABLE " + tableName + " ADD " + field.name);
        StringBuilder typeConcat = new StringBuilder(field.type);
        if ("varchar".equals(field.type)) {
            typeConcat.append('(').append(field.size).append(')');
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

    private static void alterField(Field field, DataSource dataSource, String tableName) throws SQLException {
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

}
