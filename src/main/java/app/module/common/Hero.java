package app.module.common;

/**
 * RankLog英雄实例
 */
public class Hero {
    public int worldId;
    public long roleId;
    public int country;
    public int score;
    public String HeroId;

    public Hero(int worldId, long roleId, int country, int score, String heroId) {
        this.worldId = worldId;
        this.roleId = roleId;
        this.country = country;
        this.score = score;
        HeroId = heroId;
    }
}
