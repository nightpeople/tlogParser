package app.module.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import static app.module.common.RangeType.PATTERN_NORMAL;

@Slf4j
public class MergeTask {

    private final DataSource dataSource;

    public HashMap<String, MergeUnit> unitMap;
    public ArrayList<MergeUnit> unitList = new ArrayList<>();

    private final int id;
    private final String mergeRange;

    private final int topActiveDay;
    private final int middleActiveDay;

    public LinkedHashMap<String, Hero> topHeroes = new LinkedHashMap<>();
    public LinkedHashMap<Long, Player> topPlayers = new LinkedHashMap<>();

    public long allTopHeroesPower;
    public int allMiddleNum;

    /**
     * 活跃总战力
     */
    public long totalFightPower;

    /**
     * 总充值和充值道具
     * 换算成元
     */
    public long totalRechargeVal;

    /**
     * 近期(15日)充值(元)总和
     */
    public long totalCurRecharge;

    /**
     * 活跃背包金币总数
     */
    public long totalCoin;

    public double avgMergeCountryScore;

    /**
     * 每次组合的结果
     * [[], [], []]
     * 存放unitMap的key
     */
    private List<List<String>> result = new ArrayList<>(3);

    /**
     * 结果排名,存前20名
     */
    public ArrayList<MergeResult> rank = new ArrayList<>();

    public MergeTask(DataSource dataSource, int id, String mergeRange, int topActiveDay, int middleActiveDay) {
        this.dataSource = dataSource;
        unitMap = new HashMap<>();
        this.id = id;
        this.mergeRange = mergeRange;
        this.topActiveDay = topActiveDay;
        this.middleActiveDay = middleActiveDay;
        for (int i = 0; i < 3; i++) {
            result.add(new ArrayList<>());
        }
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
                    int count = 0;
                    while (rs.next()) {
                        count++;
                        int iWorldId = rs.getInt("iWorldId");
                        long iRoleId = rs.getLong("iRoleId");
                        int iCountry = rs.getInt("iCountry");
                        int iScore = rs.getInt("iScore");
                        String vHeroId = rs.getString("vHeroId");
                        Hero hero = new Hero(iWorldId, iRoleId, iCountry, iScore, vHeroId);
                        hero.rank = count;
                        topHeroes.put(iRoleId + vHeroId, hero);
                        allTopHeroesPower += iScore;
                        if (!topPlayers.containsKey(iRoleId)) {
                            Player player = new Player(iWorldId, iRoleId, iCountry, iScore);
                            topPlayers.put(iRoleId, player);
                        }
                    }
                }
            }
        }
        initUnits();
        loadUnitTopHero();

    }

    private void initUnits() throws SQLException {
        LocalDate now = LocalDate.now();
        LocalDate minusDate = now.minusDays(3);
        //TODO sql有问题
        String middleSql =
                "select a.*, b.rmb, b.curRmb, c.itemNum from" + "(select * from (select iWorldId, iRoleId, iCountry, iHomeLv, iCoin, iFightPower, " +
                        "row_number() over (partition by iRoleId order by dtEventTime desc) as iRank from rolelogin where dt >= ? and iWorldId in (" +
                        mergeRange + ") and iHomeLv > 23) tmp where iRank = 1) a" + "left join" +
                        "(select iRoleId, sum(iPayDelta) as rmb, sum(if(dt >= ?, iPayDelta, 0)) as curRmb from recharge group by iRoleId) b" +
                        "on a.iRoleId=b.iRoleId" + "left join" +
                        "(select iRoleId, sum(regexp_replace(accept, '.*item810:(\\d+).*', '$1')) as itemNum from mail where iWorldId in (" +
                        mergeRange + ") and accept like'%item810%' group by iRoleId) c" + "on a.iRoleId=c.iRoleId";
        try (Connection connection = dataSource.getConnection()) {
            try (PreparedStatement ps = connection.prepareStatement(middleSql)) {
                ps.setString(1, PATTERN_NORMAL.format(minusDate));
                ps.setString(2, PATTERN_NORMAL.format(now.minusDays(15)));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int iWorldId = rs.getInt("iWorldId");
                        long iRoleId = rs.getLong("iRoleId");
                        int iCountry = rs.getInt("iCountry");
                        int iHomeLv = rs.getInt("iHomeLv");
                        int iCoin = rs.getInt("iCoin");
                        int iFightPower = rs.getInt("iFightPower");
                        int rmb = rs.getInt("rmb");
                        int curRmb = rs.getInt("curRmb");
                        int itemNum = rs.getInt("itemNum");
                        Player player = new Player(iWorldId, iRoleId, iCountry, 0, iFightPower, iHomeLv, iCoin, rmb, curRmb, itemNum);
                        MergeUnit mergeUnit = unitMap.get(iWorldId + "-" + iCountry);
                        if (mergeUnit == null) {
                            mergeUnit = new MergeUnit(iWorldId, iCountry);
                            unitMap.put(iWorldId + "-" + iCountry, mergeUnit);
                            unitList.add(mergeUnit);
                        }
                        mergeUnit.addMiddlePlayer(player);
                        totalFightPower += player.fightPower;
                        totalRechargeVal += rmb + itemNum * 1;
                        totalCurRecharge += curRmb;
                        totalCoin += iCoin;
                    }
                }
            }
        }
    }

    private void loadUnitTopHero() {
        for (Hero hero : topHeroes.values()) {
            MergeUnit mergeUnit = unitMap.get(hero.worldId + "-" + hero.country);
            mergeUnit.putTopHero(hero);
        }
    }

    private void initGlobalParam() {
        for (MergeUnit mergeUnit : unitMap.values()) {
            allMiddleNum += mergeUnit.middlePlayers.size();
        }

    }

    private void calcUnitScore() {
        double totalScore = 0;
        for (MergeUnit mergeUnit : unitMap.values()) {
            totalScore += mergeUnit.calcScore(this);
        }
        avgMergeCountryScore = totalScore / 3;
    }

    /**
     * 穷举所有排列组合, 并计算方差
     */
    private void combination() {
        deal(0);
    }

    /**
     * 递归
     */
    private void deal(int idx) {
        if (idx >= unitList.size()) {
            //结算,计算出本次的排序组合
            MergeResult mergeResult = settle();
            log.info(mergeResult.toString());
            //保存最优解
            addRank(mergeResult);

            return;
        }
        for (int i = 0; i < 3; i++) {
            MergeUnit mergeUnit = unitList.get(idx);
            result.get(i).add(mergeUnit.iWorldId + "-" + mergeUnit.country);
            deal(idx + 1);
            //记录完组合后删除刚添加的内容
            List<String> curGroup = result.get(i);
            curGroup.remove(curGroup.size() - 1);
        }
    }

    private MergeResult settle() {
        String[] copy = new String[3];
        double[] scoreInfo = new double[3]; // 分数累计 0-2 魏蜀吴
        for (int i = 0; i < 3; i++) {
            //取合服后国家的所有合并单元
            List<String> list = result.get(i);
            StringBuilder builder = new StringBuilder();
            for (String unitKey : list) {
                MergeUnit mergeUnit = unitMap.get(unitKey);
                scoreInfo[i] += mergeUnit.score;
                builder.append(unitKey).append(',');
            }
            copy[i] = builder.toString();
        }
        double val = (scoreInfo[0] - avgMergeCountryScore) * (scoreInfo[0] - avgMergeCountryScore) +
                (scoreInfo[1] - avgMergeCountryScore) * (scoreInfo[1] - avgMergeCountryScore) +
                (scoreInfo[2] - avgMergeCountryScore) * (scoreInfo[2] - avgMergeCountryScore);
        return new MergeResult(val, copy);
    }

    private void addRank(MergeResult mergeResult) {
        if (rank.isEmpty()) {
            rank.add(mergeResult);
            return;
        }
        if (rank.get(0).value > mergeResult.value) {
            rank.add(0, mergeResult);
        } else {
            for (int i = rank.size() - 1; i >= 0; i--) {
                MergeResult other = rank.get(i);
                if (mergeResult.value >= other.value) {
                    rank.add(i + 1, mergeResult);
                    break;
                }
            }
        }
        if (rank.size() > 20) {
            rank.remove(rank.size() - 1);
        }
    }
}
