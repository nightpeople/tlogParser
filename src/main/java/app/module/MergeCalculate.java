package app.module;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import app.module.common.MergeTask;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MergeCalculate {

    private final DataSource dataSource;

    private final boolean lowerCase;

    private static final String loadSql = "select id, mergeRange, topActiveDay, middleActiveDay from mergecalculate where state = 0 order by id asc";

    public MergeCalculate(DataSource dataSource, boolean lowerCase) {
        this.dataSource = dataSource;
        this.lowerCase = lowerCase;
    }

    public void calc() throws SQLException {
        loadFromDB();
    }

    private void loadFromDB() throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(loadSql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        int id = rs.getInt("id");
                        String mergeRange = rs.getString("mergeRange");
                        int topActiveDay = rs.getInt("topActiveDay");
                        int middleActiveDay = rs.getInt("middleActiveDay");
                        MergeTask mergeTask = new MergeTask(dataSource, id, mergeRange, topActiveDay, middleActiveDay);
                        mergeTask.loadUnits();
                    }
                }
            }
        }
    }
}
