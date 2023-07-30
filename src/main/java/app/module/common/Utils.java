package app.module.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

public class Utils {

    public static boolean contains(String[] array, String value) {
        for (String ele : array) {
            if (value.equals(ele)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 取首行中一个字段的值
     */
    public static String fetchOne(DataSource dataSource, String sql, String field) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString(field);
                    }
                }
            }
        }
        return "";
    }

    /**
     * 取首行首字段的值
     */
    public static String fetchFirst(Connection connection, String sql) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(1);
                }
            }
        }
        return "";
    }

    /**
     * 取首行第idx列字段的值
     */
    public static String fetchIndexColumn(Connection connection, String sql, int idx) throws SQLException {
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(idx);
                }
            }
        }
        return "";
    }

    public static int sqlUpdate(String sql, DataSource dataSource) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                return preparedStatement.executeUpdate();
            }
        }
    }

}
