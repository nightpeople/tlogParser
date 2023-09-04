package app.module.common;

/**
 * RankLog英雄实例
 */
public class Hero {
    public int worldId;
    public long roleId;
    public int country;
    public long score;
    public String heroId;

    /**
     * 全局高战英雄排名
     */
    public int rank;

    public Hero(int worldId, long roleId, int country, long score, String heroId) {
        this.worldId = worldId;
        this.roleId = roleId;
        this.country = country;
        this.score = score;
        this.heroId = heroId;
    }
}
