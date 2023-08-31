package app.module.common;

import java.util.ArrayList;
import java.util.Collections;

public class MergeResult implements Comparable<MergeResult> {
    /**
     * 计算的方差
     */
    double value;

    /**
     * 方案,合服后每个国家合并进来的worldId-country
     */
    String[] plan = new String[3];

    public MergeResult(double value, String[] plan) {
        this.value = value;
        this.plan = plan;
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

        return "MergeResult{" + "value=" + value + '}' + builder.toString();
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
