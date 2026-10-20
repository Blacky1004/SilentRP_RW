package net.dorblacky.silentrp.classes;

public class SilentRpPlayerData {
    private String player_guid;
    private int money;
    private int level;
    private int xp;
    private boolean isNewPlayer;

    public String getId() {
        return player_guid;
    }

    public void setId(String id) {
        this.player_guid = id;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getXp() {
        return xp;
    }

    public void setXp(int xp) {
        this.xp = xp;
    }

    public boolean isNewPlayer() {
        return isNewPlayer;
    }

    public void setNewPlayer(boolean newPlayer) {
        isNewPlayer = newPlayer;
    }
}