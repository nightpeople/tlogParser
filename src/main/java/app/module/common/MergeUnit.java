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
    public final MergeTask mergeTask;
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

    public double score1;
    public double score2;
    public double score3;
    public double score4;
    public double score5;
    public double score6;

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
     * 真实总充值
     */
    public long totalRealRecharge;

    /**
     * 充值道具总数
     */
    public long totalItemNum;

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

    public MergeUnit(int iWorldId, int country, MergeTask mergeTask) {
        this.mergeTask = mergeTask;
        this.country = country;
        this.iWorldId = iWorldId;
    }

    public void addMiddlePlayer(Player player) {
        middlePlayers.add(player);
        totalFightPower += player.fightPower;
        totalRechargeVal += player.rmb + player.itemNum * 1;
        totalRealRecharge += player.rmb;
        totalItemNum += player.itemNum;
        totalCurRecharge += player.curRmb;
        totalCoin += player.coin;
    }

    public void putTopHero(Hero hero) {
        topHeroes.put(hero.roleId + "-" + hero.heroId, hero);
        localTopHeroPower += getLocalTopPower(hero);
    }

    public double calcScore(MergeTask task) {
        score1 = task.allTopHeroesPower != 0 ? (double) localTopHeroPower / task.allTopHeroesPower : 0;
        score2 = task.allMiddleNum != 0 ? (double) middlePlayers.size() * getRight2() / task.allMiddleNum : 0;
        score3 = task.totalFightPower != 0 ? (double) totalFightPower * getRight3() / task.totalFightPower : 0;
        score4 = task.totalRechargeVal != 0 ? (double) totalRechargeVal * getRight4() / task.totalRechargeVal : 0;
        score5 = task.totalCurRecharge != 0 ? (double) totalCurRecharge * getRight5() / task.totalCurRecharge : 0;
        score6 = task.totalCoin != 0 ? (double) totalCoin * getRight6() / task.totalCoin : 0;
        this.score = score1 + score2 + score3 + score4 + score5 + score6;
        task.output.append(iWorldId).append('-').append(country).append(" score: ").append(String.format("%.2f", score)).append("  score1:")
                .append(String.format("%.2f", score1)).append(", score2:").append(String.format("%.2f", score2)).append(", score3: ")
                .append(String.format("%.2f", score3)).append(", score4:").append(String.format("%.2f", score4)).append(", score5")
                .append(String.format("%.2f", score5)).append(", score6:").append(String.format("%.2f", score6)).append('\n');
        log.info("{}-{} score:{}  score1:{}, score2:{}, score3:{}, score4:{}, score5:{}, score6:{}\n", iWorldId, country,
                String.format("%.2f", score), String.format("%.2f", score1), String.format("%.2f", score2), String.format("%.2f", score3),
                String.format("%.2f", score4), String.format("%.2f", score5), String.format("%.2f", score6));
        return score;
    }

    public long getLocalTopPower(Hero hero) {
        return hero.score * getRight1(hero.rank);
    }

    public int getRight1(int rank) {
        int[] right1 = getRight1();
        int min = Math.min(rank, right1.length);
        return right1[min - 1];
    }

    public int[] getRight1() {
        return mergeTask.right1 != null ? mergeTask.right1 : RIGHT1;
    }

    public int getRight2() {
        return mergeTask.right2 != 0 ? mergeTask.right2 : RIGHT2;
    }

    public int getRight3() {
        return mergeTask.right3 != 0 ? mergeTask.right3 : RIGHT3;
    }

    public int getRight4() {
        return mergeTask.right4 != 0 ? mergeTask.right4 : RIGHT4;
    }

    public int getRight5() {
        return mergeTask.right5 != 0 ? mergeTask.right5 : RIGHT5;
    }

    public int getRight6() {
        return mergeTask.right6 != 0 ? mergeTask.right6 : RIGHT6;
    }

    public void unitFormat() {
        System.out.println("合服单元:" + iWorldId + "-" + country + " 活跃人数:" + middlePlayers.size() + " 活跃战力" + totalFightPower + " 总充值" +
                totalRealRecharge + " 虚拟充值" + totalItemNum + " 高战人数" + topHeroes.size() + '\n');
    }
}
