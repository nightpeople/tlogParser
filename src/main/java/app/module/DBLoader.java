package app.module;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Properties;

import javax.sql.DataSource;

import app.module.common.Field;
import app.module.common.Table;

/**
 * 加载mysql库所有表信息
 */
public class DBLoader {

    public final Properties properties;
    private final DataSource dataSource;

    /**
     * 加载的数据库表信息
     * 表名 - Table结构
     */
    public LinkedHashMap<String, Table> tables = new LinkedHashMap<>();

    public DBLoader(Properties properties) throws Exception {
        this.properties = properties;
        dataSource = DruidDataSourceFactory.createDataSource(properties);
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
        Table table = new Table(tableName, "");
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
        int size = tables.values().size();
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
