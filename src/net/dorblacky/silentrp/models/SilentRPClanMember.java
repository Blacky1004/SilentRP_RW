package net.dorblacky.silentrp.models;

import java.util.Date;

public class SilentRPClanMember {
    private int clan_id;
    private String player_uid;
    private int rank;
    private Date invite_date;

    public int getClan_id() {
        return clan_id;
    }

    public void setClan_id(int clan_id) {
        this.clan_id = clan_id;
    }

    public String getPlayer_uid() {
        return player_uid;
    }

    public void setPlayer_uid(String player_uid) {
        this.player_uid = player_uid;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public Date getInvite_date() {
        return invite_date;
    }

    public void setInvite_date(Date invite_date) {
        this.invite_date = invite_date;
    }
}
