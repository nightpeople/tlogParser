package app.module.common;

import java.util.ArrayList;

import lombok.extern.slf4j.Slf4j;

/**
 * 合服单元
 * 1个服的1个国家
 */
@Slf4j
public class MergeUnit {

    /**
     * 012 魏蜀吴
     */
    public final int country;

    public final int iWorldId;

    private final ArrayList<MergePlayer> topPlayers = new ArrayList<>();

    private final ArrayList<MergePlayer> middlePlayers = new ArrayList<>();

    public MergeUnit(int country, int iWorldId) {
        this.country = country;
        this.iWorldId = iWorldId;
    }
}
