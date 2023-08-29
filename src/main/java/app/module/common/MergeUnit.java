package app.module.common;

import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;

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
     * 高战玩家
     */
    private final ArrayList<Player> topPlayers = new ArrayList<>();

    /**
     * 活跃中坚玩家
     */
    private final ArrayList<Player> middlePlayers = new ArrayList<>();

    /**
     * 当前战力
     */
    public int curPower;

    /**
     * 发展潜力
     */
    public int potential;

    /**
     * 综合评分
     */
    public int score;

    public MergeUnit(int iWorldId, int country) {
        this.country = country;
        this.iWorldId = iWorldId;
    }

    public void addMiddlePlayer(Player player) {
        middlePlayers.add(player);
    }
}
