package app.module.common;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import lombok.extern.slf4j.Slf4j;

import static app.module.MergeCalculate.RIGHT1;
import static app.module.MergeCalculate.RIGHT2;
import static app.module.MergeCalculate.RIGHT3;
import static app.module.MergeCalculate.RIGHT4;
import static app.module.MergeCalculate.RIGHT5;
import static app.module.MergeCalculate.RIGHT6;

/**
 * 合服单元
 * 1个服的1个国家
 */
@Slf4j
public class MergeUnit {

    public final int iWorldId;

    /**
     * 012 魏蜀吴
     */
    public final int country;

    /**
     * 高战英雄
     */
    public final LinkedHashMap<String, Hero> topHeroes = new LinkedHashMap<>();

    /**
     * 活跃中坚玩家
     */
    public final ArrayList<Player> middlePlayers = new ArrayList<>();

    /**
     * 综合评分
     */
    public double score;

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

    /**
     * 本国高战英雄战力
     */
    public long localTopHeroPower;

    public MergeUnit(int iWorldId, int country) {
        this.country = country;
        this.iWorldId = iWorldId;
    }

    public void addMiddlePlayer(Player player) {
        middlePlayers.add(player);
        totalFightPower += player.fightPower;
        totalRechargeVal += player.rmb + player.itemNum * 1;
        totalCurRecharge += player.curRmb;
        totalCoin += player.coin;
    }

    public void putTopHero(Hero hero) {
        topHeroes.put(hero.roleId + hero.heroId, hero);
        localTopHeroPower += getLocalTopPower(hero);
    }

    public double calcScore(MergeTask task) {
        double score = 0;
        score += (double) localTopHeroPower / task.allTopHeroesPower;
        score += (double) middlePlayers.size() * RIGHT2 / task.allMiddleNum;
        score += (double) totalFightPower * RIGHT3 / task.totalFightPower;
        score += (double) totalRechargeVal * RIGHT4 / task.totalRechargeVal;
        score += (double) totalCurRecharge * RIGHT5 / task.totalCurRecharge;
        score += (double) totalCoin * RIGHT6 / task.totalCoin;
        this.score = score;
        return score;
    }

    public long getLocalTopPower(Hero hero) {
        return hero.score * getRight1(hero.rank);
    }

    public int getRight1(int rank) {
        int min = Math.min(rank, RIGHT1.length);
        return RIGHT1[min - 1];
    }
}
