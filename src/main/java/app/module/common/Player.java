package app.module.common;

public class Player {
    public int worldId;
    public long roleId;
    public int country;

    /**
     * 英雄战力
     */
    public int score;

    /**
     * 角色战力
     */
    public int fightPower;

    public int homeLv;
    public int coin;

    public Player(int worldId, long roleId, int country, int score) {
        this.worldId = worldId;
        this.roleId = roleId;
        this.country = country;
        this.score = score;
    }

    public Player(int worldId, long roleId, int country, int score, int fightPower, int homeLv, int coin) {
        this.worldId = worldId;
        this.roleId = roleId;
        this.country = country;
        this.score = score;
        this.fightPower = fightPower;
        this.homeLv = homeLv;
        this.coin = coin;
    }
}
