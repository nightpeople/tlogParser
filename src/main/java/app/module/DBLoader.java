package app.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;

import javax.sql.DataSource;

import app.module.common.Field;
import app.module.common.Table;

/**
 * 加载mysql库所有表信息
 */
public class DBLoader {
    private static final Logger logger = LoggerFactory.getLogger(DBLoader.class);
    public static final String[] FIXED_FIELDS = new String[]{"id", "dt"};

    private final DataSource dataSource;
    public final boolean lowerCase;
    /**
     * 加载的数据库表信息
     * 表名 - Table结构
     */
    public LinkedHashMap<String, Table> tables = new LinkedHashMap<>();

    public DBLoader(DataSource dataSource, boolean lowerCase) {
        this.dataSource = dataSource;
        this.lowerCase = lowerCase;
    }

    public void load() throws SQLException {
        //从数据库读取全部表信息
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement("show tables")) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String tableName = rs.getString(1);
                        Table table = buildTable(tableName, connection);
                        tables.put(tableName, table);
                    }
                }
            }
        }
    }

    private Table buildTable(String tableName, Connection connection) throws SQLException {
        Table table = new Table(tableName, "", lowerCase, tableName);
        try (PreparedStatement ps = connection.prepareStatement("desc " + tableName)) {
            try (ResultSet rs = ps.executeQuery()) {
                Field pre = null;
                while (rs.next()) {
                    String fieldName = rs.getString("Field");
                    String typeConf = rs.getString("Type");
                    boolean notNull = "NO".equalsIgnoreCase(rs.getString("Null"));
                    String key = rs.getString("Key");
                    String _default = rs.getString("Default");
                    String extra = rs.getString("Extra");
                    Field field = new Field(fieldName, typeConf, notNull, key, _default, extra, tableName);
                    if (pre != null) {
                        pre.next = field;
                    }
                    table.addFields(fieldName, field);
                    pre = field;
                }
            }
        }
        return table;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        int size = tables.size();
        int count = 0;
        for (Table table : tables.values()) {
            builder.append(table.toString());
            count++;
            if (count < size) {
                builder.append("--------------------\n\n");
            }
        }
        return builder.toString();
    }
}
