package app.module.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.sql.DataSource;

import static app.module.common.RangeType.PATTERN_NORMAL;

public class MergeTask {

    private final DataSource dataSource;

    public ArrayList<MergeUnit> mergeUnits;

    private final int id;
    private final String mergeRange;

    private final int topActiveDay;
    private final int middleActiveDay;

    public LinkedHashMap<Long, Hero> topHeroes = new LinkedHashMap<>();
    public LinkedHashMap<Long, Player> topPlayers = new LinkedHashMap<>();

    public MergeTask(DataSource dataSource, int id, String mergeRange, int topActiveDay, int middleActiveDay) {
        this.dataSource = dataSource;
        mergeUnits = new ArrayList<>();
        this.id = id;
        this.mergeRange = mergeRange;
        this.topActiveDay = topActiveDay;
        this.middleActiveDay = middleActiveDay;
    }

    public void loadUnits() throws SQLException {
        LocalDate curDate = LocalDate.now();
        String ymd = PATTERN_NORMAL.format(curDate);
        /*
        String topPlayerSql =
                "select iWorldId, iRoleId, iCountry, max(iScore) as max from (select iWorldId, iRoleId, iCountry, iScore from ranklog where dt = '" +
                        ymd + "' and iWorldId in (" + mergeRange +
                        ") and iRankType=6 order by iScore desc limit 15 ) a group by iWorldId, iRoleId, iCountry order by max desc";
        */
        String topSql = "select iWorldId, iRoleId, iCountry, iScore, vHeroId from ranklog where dt = ? and iWorldId in (" + mergeRange +
                ") and iRankType=6 order by iScore desc limit 15";
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(topSql)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {

                    }
                }
            }
        }

    }
}
