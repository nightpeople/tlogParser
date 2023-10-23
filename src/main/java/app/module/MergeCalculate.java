package app.module;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import app.module.common.MergeTask;
import app.module.common.Utils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MergeCalculate {

    private final DataSource dataSource;

    private final boolean lowerCase;

    private String[] params = new String[0];

    private static final String loadSql =
            "select id, mergeRange, topActiveDay, middleActiveDay, right1, right2, right3, right4, right5, right6 from mergecalculate where state = 0 order by id asc limit 1";
    public static final int[] RIGHT1 = {200, 180, 160, 140, 120, 100, 100, 80, 80, 80, 60, 60, 60, 60, 60};
    public static final int RIGHT2 = 100;
    public static final int RIGHT3 = 60;
    public static final int RIGHT4 = 70;
    public static final int RIGHT5 = 30;
    public static final int RIGHT6 = 30;

    /**
     * 合服单元数量差距修正
     */
    public static final double[] DIFF_VALUE = {1, 1.2, 1.4, 1.6, 1.8, 2};

    public MergeCalculate(DataSource dataSource, boolean lowerCase, String[] params) {
        this.dataSource = dataSource;
        this.lowerCase = lowerCase;
        this.params = params;
    }

    public void calc() throws SQLException {
        loadFromDB();
    }

    private void loadFromDB() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(loadSql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        String mergeRange = rs.getString("mergeRange");
                        int topActiveDay = rs.getInt("topActiveDay");
                        int middleActiveDay = rs.getInt("middleActiveDay");
                        int[] right1 = Utils.toIntArray(rs.getString("right1"), "\\,");
                        int right2 = rs.getInt("right2");
                        int right3 = rs.getInt("right3");
                        int right4 = rs.getInt("right4");
                        int right5 = rs.getInt("right5");
                        int right6 = rs.getInt("right6");
                        MergeTask mergeTask =
                                new MergeTask(dataSource, id, mergeRange, topActiveDay, middleActiveDay, right1, right2, right3, right4, right5,
                                        right6, this.params);
                        log.info("合服评估任务 id:{} -> 合服范围:{}", id, mergeRange);
                        mergeTask.process();
                    }
                }
            }
        }
    }
}
