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

    /**
     * 总充值(元)
     */
    public int rmb;

    /**
     * 近期(15日)充值(元)
     */
    public int curRmb;

    /**
     * 充值道具数量
     */
    public int itemNum;

    public Player(int worldId, long roleId, int country, int score) {
        this.worldId = worldId;
        this.roleId = roleId;
        this.country = country;
        this.score = score;
    }

    public Player(int worldId, long roleId, int country, int score, int fightPower, int homeLv, int coin, int rmb, int curRmb, int itemNum) {
        this.worldId = worldId;
        this.roleId = roleId;
        this.country = country;
        this.score = score;
        this.fightPower = fightPower;
        this.homeLv = homeLv;
        this.coin = coin;
        this.rmb=rmb;
        this.curRmb=curRmb;
        this.itemNum=itemNum;
    }
}
