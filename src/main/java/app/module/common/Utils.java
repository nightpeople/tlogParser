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
    
}
