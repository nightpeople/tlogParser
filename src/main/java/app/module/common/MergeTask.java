package app.module.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import static app.module.common.RangeType.PATTERN_NORMAL;

@Slf4j
public class MergeTask {

    private final DataSource dataSource;

    /**
     * worldId-country
     */
    public ArrayList<String> bigThreeUnit = new ArrayList<>(3);
    public LinkedHashMap<String, MergeUnit> unitMap;
    public ArrayList<MergeUnit> unitList = new ArrayList<>();

    private final int id;
    private final String mergeRange;

    private final int topActiveDay;
    private final int middleActiveDay;

    /**
     * 高战前15名英雄
     */
    public LinkedHashMap<String, Hero> topHeroes = new LinkedHashMap<>();

    /**
     * 高战英雄对应角色
     */
    public LinkedHashMap<Long, Player> topPlayers = new LinkedHashMap<>();

    /**
     * 高战英雄总战力
     */
    public long allTopHeroesPower;

    /**
     * 活跃中坚总角色数
     */
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
     * <<>, <>, <>>
     * 存放unitMap的key
     */
    private final List<List<String>> result = new ArrayList<>(3);

    /**
     * 结果排名,存前30名
     */
    public ArrayList<MergeResult> rank = new ArrayList<>();

    public int[] right1 = null;
    public int right2;
    public int right3;
    public int right4;
    public int right5;
    public int right6;

    /**
     * 输出结果
     */
    public StringBuilder output = new StringBuilder();

    public MergeTask(DataSource dataSource, int id, String mergeRange, int topActiveDay, int middleActiveDay, int[] right1, int right2, int right3,
            int right4, int right5, int right6) {
        this.dataSource = dataSource;
        this.unitMap = new LinkedHashMap<>();
        this.id = id;
        this.mergeRange = mergeRange;
        this.topActiveDay = topActiveDay;
        this.middleActiveDay = middleActiveDay;
        this.right1 = right1;
        this.right2 = right2;
        this.right3 = right3;
        this.right4 = right4;
        this.right5 = right5;
        this.right6 = right6;
        for (int i = 0; i < 3; i++) {
            result.add(new ArrayList<>());
        }
        // 活跃中坚角色反推每个合服单元不一定齐全, 先初始化一遍unitMap
        for (String rowSid : mergeRange.split(",")) {
            int sid = Integer.parseInt(rowSid.trim());
            for (int i = 0; i < 3; i++) {
                MergeUnit mergeUnit = new MergeUnit(sid, i, this);
                this.unitMap.put(sid + "-" + i, mergeUnit);
            }
        }
    }

    public void process() throws SQLException {
        LocalDate curDate = LocalDate.now();
        String ymd = PATTERN_NORMAL.format(curDate);
        /*
        String topPlayerSql =
                "select iWorldId, iRoleId, iCountry, max(iScore) as max from (select iWorldId, iRoleId, iCountry, iScore from ranklog where dt = '" +
                        ymd + "' and iWorldId in (" + mergeRange +
                        ") and iRankType=6 order by iScore desc limit 15 ) a group by iWorldId, iRoleId, iCountry order by max desc";
        */
        String topSql = "select iWorldId, iRoleId, iCountry, iScore, vHeroId from ranklog where dt = ? and iWorldId in (" + mergeRange +
                ") and iRankType = 6 order by iScore desc limit 15";
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
                        long iScore = rs.getLong("iScore");
                        String vHeroId = rs.getString("vHeroId");
                        Hero hero = new Hero(iWorldId, iRoleId, iCountry, iScore, vHeroId);
                        hero.rank = count;
                        topHeroes.put(iRoleId + "-" + vHeroId, hero);
                        allTopHeroesPower += iScore;
                        if (!topPlayers.containsKey(iRoleId)) {
                            Player player = new Player(iWorldId, iRoleId, iCountry, iScore);
                            topPlayers.put(iRoleId, player);
                        }
                        // 三巨头合服单元
                        String key = iWorldId + "-" + iCountry;
                        if (bigThreeUnit.size() < 3 && !bigThreeUnit.contains(key)) {
                            bigThreeUnit.add(key);
                        }
                    }
                }
            }
        }
        initUnits();
        loadUnitTopHero();
        initGlobalParam();
        calcUnitScore();
        for (MergeUnit unit : unitMap.values()) {
            unit.unitFormat();
        }
        combination();
        //formatResult();
    }

    private void formatResult() {
        for (MergeResult mergeResult : rank) {
            mergeResult.format(this);
        }
    }

    /**
     * 初始化每个合服单元
     */
    private void initUnits() throws SQLException {
        LocalDate now = LocalDate.now();
        LocalDate minusDate = now.minusDays(middleActiveDay);

        String middleSql = "select a.*, b.rmb, b.curRmb, c.itemNum from \n" + "(select * from \n" +
                "(select iWorldId, iRoleId, iCountry, iHomeLv, iCoin, iFightPower, \n" +
                "row_number() over (partition by iRoleId order by dtEventTime desc) as iRank \n" + "from rolelogin where dt >= ? and iWorldId in (" +
                mergeRange + ") and iHomeLv >= 15) tmp \n" + "where iRank = 1) a \n" + "left join \n" +
                "(select iRoleId, sum(iPayDelta) as rmb, sum(if(dt >= ?, iPayDelta, 0)) as curRmb \n" + "from recharge where iWorldId in (" +
                mergeRange + ") group by iRoleId) b \n" + "on a.iRoleId = b.iRoleId \n" + "left join \n" +
                "(select iRoleId, sum(regexp_replace(accept, '.*item810:(\\\\d+).*', '$1')) as itemNum \n" + "from mail where iWorldId in (" +
                mergeRange + ") and accept like '%item810%' group by iRoleId) c \n" + "on a.iRoleId = c.iRoleId";
        System.out.println(middleSql);
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
                        long iCoin = rs.getLong("iCoin");
                        long iFightPower = rs.getLong("iFightPower");
                        int rmb = rs.getInt("rmb");
                        int curRmb = rs.getInt("curRmb");
                        int itemNum = rs.getInt("itemNum");
                        Player player = new Player(iWorldId, iRoleId, iCountry, 0, iFightPower, iHomeLv, iCoin, rmb, curRmb, itemNum);
                        MergeUnit mergeUnit = unitMap.get(iWorldId + "-" + iCountry);
                        if (mergeUnit == null) {
                            mergeUnit = new MergeUnit(iWorldId, iCountry, this);
                            unitMap.put(iWorldId + "-" + iCountry, mergeUnit);
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

    /**
     * 把高战英雄分配到对应和服单元内
     */
    private void loadUnitTopHero() {
        for (Hero hero : topHeroes.values()) {
            MergeUnit mergeUnit = unitMap.get(hero.worldId + "-" + hero.country);
            mergeUnit.putTopHero(hero);
        }
    }

    private void initGlobalParam() {
        for (MergeUnit mergeUnit : unitMap.values()) {
            allMiddleNum += mergeUnit.middlePlayers.size();
            if (!bigThreeUnit.contains(mergeUnit.iWorldId + "-" + mergeUnit.country)) {
                unitList.add(mergeUnit);
            }
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
        //三巨头合服单元首先单独分配
        for (int i = 0; i < 3; i++) {
            this.result.get(i).add(bigThreeUnit.get(i));
        }
        deal(0);
        log.info("最优组合如下所示: ");
        System.out.println(rank);
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
            builder.deleteCharAt(builder.length() - 1);
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
        if (rank.size() > 30) {
            rank.remove(rank.size() - 1);
        }
    }

    public void buildOutput(String add) {
        output.append(add);
    }
}
