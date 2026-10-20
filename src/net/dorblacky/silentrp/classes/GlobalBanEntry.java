package net.dorblacky.silentrp.classes;

import java.util.Date;

public class GlobalBanEntry {
    private String player_uid;
    private int ban_points;
    private String reason;
    private Date last_action;

    public String getPlayer_uid() {
        return player_uid;
    }
    public void setPlayer_uid(String player_uid) {
        this.player_uid = player_uid;
    }
    public int getBan_points() {
        return ban_points;
    }
    public void setBan_points(int ban_points) {
        this.ban_points = ban_points;
    }
    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }
    public Date getLast_action() {
        return last_action;
    }
    public void setLast_action(Date last_action) {
        this.last_action = last_action;
    }
}
