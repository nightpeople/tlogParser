package app.module.common;

import java.util.ArrayList;
import java.util.Collections;

public class MergeResult implements Comparable<MergeResult> {
    /**
     * 计算的方差
     */
    public double value;

    /**
     * 方案,合服后每个国家合并进来的worldId-country
     */
    public String[] plan = new String[3];

    public MergeResult(double value, String[] plan) {
        this.value = value;
        this.plan = plan;
    }

    /**
     * 输出合服后每个国家的数值
     */
    public void format(MergeTask task) {
        System.out.println(this);
        int activeNum;
        long activePower;
        long countryRecharge;
        long virtualRecharge;
        int topNum;
        for (int i = 0; i < plan.length; i++) {
            //每个合服后国家
            activeNum = 0;
            activePower = 0;
            countryRecharge = 0;
            virtualRecharge = 0;
            topNum = 0;
            for (String unit : plan[i].split(",")) {
                MergeUnit mergeUnit = task.unitMap.get(unit);
                activeNum += mergeUnit.middlePlayers.size();
                activePower += mergeUnit.totalFightPower;
                countryRecharge += mergeUnit.totalRealRecharge;
                virtualRecharge += mergeUnit.totalItemNum;
                topNum += mergeUnit.topHeroes.size();
            }
            System.out.println("国家" + i + "活跃人数:" + activeNum + ", 活跃战力:" + activePower + ", 总充值:" + countryRecharge + ", 虚拟充值:" +
                    virtualRecharge + ", 高战人数:" + topNum + '\n');
        }
        System.out.println("------------------------------------------------\n");
    }

    @Override
    public int compareTo(MergeResult o) {
        double diff = value - o.value;
        if (diff == 0) {
            return 0;
        } else if (diff > 0) {
            return 1;
        } else {
            return -1;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String s : plan) {
            builder.append('[').append(s).append(']');
        }

        return "MergeResult{" + "value=" + (int) value + '}' + builder + "\n";
    }

    public static void main(String[] args) {
        ArrayList<MergeResult> list = new ArrayList<>();
        list.add(new MergeResult(86.6, null));
        list.add(new MergeResult(39.6, null));
        list.add(new MergeResult(99.6, null));
        list.add(new MergeResult(9.6, null));
        list.add(new MergeResult(13.6, null));
        list.add(new MergeResult(1.6, null));
        Collections.sort(list);
        System.out.println(list);
    }
}
