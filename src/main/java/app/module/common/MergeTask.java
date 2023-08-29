package app.module.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.sql.DataSource;

import static app.module.common.RangeType.PATTERN_NORMAL;

public class MergeTask {

    private final DataSource dataSource;

    public HashMap<String, MergeUnit> unitMap;

    private final int id;
    private final String mergeRange;

    private final int topActiveDay;
    private final int middleActiveDay;

    public LinkedHashMap<String, Hero> topHeroes = new LinkedHashMap<>();
    public LinkedHashMap<Long, Player> topPlayers = new LinkedHashMap<>();

    public MergeTask(DataSource dataSource, int id, String mergeRange, int topActiveDay, int middleActiveDay) {
        this.dataSource = dataSource;
        unitMap = new HashMap<>();
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
                ps.setString(1, ymd);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int iWorldId = rs.getInt("iWorldId");
                        long iRoleId = rs.getLong("iRoleId");
                        int iCountry = rs.getInt("iCountry");
                        int iScore = rs.getInt("iScore");
                        String vHeroId = rs.getString("vHeroId");
                        Hero hero = new Hero(iWorldId, iRoleId, iCountry, iScore, vHeroId);
                        topHeroes.put(iRoleId + vHeroId, hero);
                        if (!topPlayers.containsKey(iRoleId)) {
                            Player player = new Player(iWorldId, iRoleId, iCountry, iScore);
                            topPlayers.put(iRoleId, player);
                        }
                    }
                }
            }
        }


    }

    private void initUnits() throws SQLException {
        LocalDate now = LocalDate.now();
        LocalDate minusDate = now.minusDays(3);
        String middleSql = "select iWorldId, iRoleId, iCountry, iHomeLv, iCoin, iFightPower, " +
                "rank() over (partition by iRoleId order by dtEventTime desc) as rank from rolelogin where dt >= ? and iWorldId in (" + mergeRange +
                ") and iHomeLv > 23 and rank = 1";
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(middleSql)) {
                ps.setString(1, PATTERN_NORMAL.format(minusDate));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int iWorldId = rs.getInt("iWorldId");
                        long iRoleId = rs.getLong("iRoleId");
                        int iCountry = rs.getInt("iCountry");
                        int iHomeLv = rs.getInt("iHomeLv");
                        int iCoin = rs.getInt("iCoin");
                        int iFightPower = rs.getInt("iFightPower");
                        Player player = new Player(iWorldId, iRoleId, iCountry, 0, iFightPower, iHomeLv, iCoin);
                        MergeUnit mergeUnit = unitMap.get(iWorldId + "-" + iCountry);
                        if (mergeUnit == null) {
                            mergeUnit = new MergeUnit(iWorldId, iCountry);
                            unitMap.put(iWorldId + "-" + iCountry, mergeUnit);
                        }
                        mergeUnit.addMiddlePlayer(player);
                    }
                }
            }
        }
    }
}
