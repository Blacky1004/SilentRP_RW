package net.dorblacky.silentrp.classes;

import net.dorblacky.silentrp.models.SilentRPClanMember;

public class SilentRpPlayer {
    private String player_guid;
    private int money;
    private int level;
    private int xp;
    private boolean isNewPlayer;
    private SilentRPClanMember clan;

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

    public String getPlayer_guid() {
        return player_guid;
    }

    public void setPlayer_guid(String player_guid) {
        this.player_guid = player_guid;
    }

    public SilentRPClanMember getClan() {
        return clan;
    }

    public void setClan(SilentRPClanMember clan) {
        this.clan = clan;
    }
}