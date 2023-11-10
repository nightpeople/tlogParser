package app.module.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import lombok.extern.slf4j.Slf4j;

import static app.module.MergeCalculate.DIFF_VALUE;
import static app.module.common.RangeType.PATTERN_NORMAL;

@Slf4j
public class MergeTask {
    public static final Pattern MERGE_RESULT_PATTERN = Pattern.compile("\\[(.*?)\\]", Pattern.CASE_INSENSITIVE); //忽略大小写
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

    public String[] params = new String[0];

    /**
     * 输出结果
     */
    public StringBuilder output = new StringBuilder();

    public MergeTask(DataSource dataSource, int id, String mergeRange, int topActiveDay, int middleActiveDay, int[] right1, int right2, int right3,
            int right4, int right5, int right6, String[] params) {
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
        this.params = params;
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
        if (this.params.length > 0) {
            switch (this.params[0]) {
                case "formatInput": {
                    formatInput();
                    return;
                }
            }
            return;
        }
        combination();
        formatResult();
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

    /**
     * 输出一种方案
     */
    private MergeResult settle() {
        String[] copy = new String[3]; // 复制一份结果
        double[] scoreInfo = new double[3]; // 分数累计 0-2 魏蜀吴
        int[] countryCount = new int[3]; // 合服后每个国家合并的合服单元计数

        ArrayList<HashMap<Integer, Integer>> sourceCount = new ArrayList<>(); // 合服后将同一个服分到一个国家数量计数
        for (int i = 0; i < 3; i++) {
            sourceCount.add(new HashMap<>());
        }

        for (int i = 0; i < 3; i++) {
            //取合服后国家的所有合并单元
            List<String> list = result.get(i); //一个合服后国家
            StringBuilder builder = new StringBuilder(); //国家的合服单元字符串

            HashMap<Integer, Integer> countMap = sourceCount.get(i);

            //遍历国家的合服单元
            for (String unitKey : list) {
                MergeUnit mergeUnit = unitMap.get(unitKey);
                scoreInfo[i] += mergeUnit.score;
                builder.append(unitKey).append(',');

                countryCount[i]++; // 计数合服后国家合服单元数量

                //计数服分到同一个国家的数量
                if (countMap.containsKey(mergeUnit.iWorldId)) {
                    countMap.put(mergeUnit.iWorldId, countMap.get(mergeUnit.iWorldId) + 1);
                } else {
                    countMap.put(mergeUnit.iWorldId, 1);
                }
            }
            builder.deleteCharAt(builder.length() - 1);
            copy[i] = builder.toString();
        }

        double v1 = (scoreInfo[0] - avgMergeCountryScore) * (scoreInfo[0] - avgMergeCountryScore);
        double v2 = (scoreInfo[1] - avgMergeCountryScore) * (scoreInfo[1] - avgMergeCountryScore);
        double v3 = (scoreInfo[2] - avgMergeCountryScore) * (scoreInfo[2] - avgMergeCountryScore);
        double val = v1 + v2 + v3;

        //-----额外算法规则开始-----
        //合服后国家合服单元数量最大差,修正方差
        Arrays.sort(countryCount);
        int diff = countryCount[2] - countryCount[0];
        double diffValue = getDiffValue(diff);

        //将一个服分到同一个国家, 修正方差
        double multi = 1;
        for (HashMap<Integer, Integer> map : sourceCount) {
            if (map.containsValue(3)) {
                multi = 2;
                break;
            }
        }
        val = val * diffValue * multi;
        //-----额外算法规则结束-----

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

    private void formatInput() {
        ArrayList<MergeResult> input = new ArrayList<>();
        String str =
                "[2059-1,2057-1,2055-1,2058-2,2056-1,2060-2][2060-1,2058-0,2055-2,2057-0,2059-0,2056-0][2057-2,2055-0,2060-0,2056-2,2059-2,2058-1]";
        for (String row : str.split("\n")) {
            String[] copy = new String[3];
            Matcher matcher = MERGE_RESULT_PATTERN.matcher(row);
            int count = 0;
            while (matcher.find()) {
                String group = matcher.group(1);
                copy[count] = group;
                count++;
            }
            MergeResult mergeResult = new MergeResult(-1, copy);
            mergeResult.format(this);
        }

    }

    /**
     * 格式化排行结果
     */
    private void formatResult() {
        for (MergeResult mergeResult : rank) {
            mergeResult.format(this);
        }
    }

    public static double getDiffValue(int diff) {
        int min = Math.min(DIFF_VALUE.length - 1, diff);
        return DIFF_VALUE[min];
    }
}
