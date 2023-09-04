package app.module.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;

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

    public static String equalsIgnoreCaseAndGet(String val, Iterator<String> iterator) {
        while (iterator.hasNext()) {
            String compare = iterator.next();
            if (val.equalsIgnoreCase(compare)) {
                return compare;
            }
        }
        return "";
    }

    public static int[] toIntArray(String str, String delimiter) {
        if (str == null) {
            return null;
        }
        String[] split = str.split(delimiter);
        int[] ints = new int[split.length];
        for (int i = 0; i < split.length; i++) {
            ints[i] = Integer.parseInt(split[i]);
        }
        return ints;
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
