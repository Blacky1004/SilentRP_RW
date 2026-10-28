package net.dorblacky.silentrp.models;

import java.util.Arrays;

public class SilentRPClan {
    private int id;
    private String name;
    private String clan_description;
    private SilentRpPlayer clan_owner;
    private int clan_rank;
    private int clan_xp;
    private int clan_money;
    private SilentRPClanMember[] members;
    private int clan_fee;
    private boolean public_invite;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClan_description() {
        return clan_description;
    }

    public void setClan_description(String clan_description) {
        this.clan_description = clan_description;
    }

    public SilentRpPlayer getClan_owner() {
        return clan_owner;
    }

    public void setClan_owner(SilentRpPlayer clan_owner) {
        this.clan_owner = clan_owner;
    }

    public int getClan_rank() {
        return clan_rank;
    }

    public void setClan_rank(int clan_rank) {
        this.clan_rank = clan_rank;
    }

    public int getClan_xp() {
        return clan_xp;
    }

    public void setClan_xp(int clan_xp) {
        this.clan_xp = clan_xp;
    }

    public int getClan_money() {
        return clan_money;
    }


    public void setClan_money(int clan_money) {
        this.clan_money = clan_money;
    }

    public SilentRPClanMember[] getMembers() {
        return members;
    }

    public void setMembers(SilentRPClanMember[] members) {
        this.members = members;
    }

    public void addClanMember(SilentRPClanMember member) {
        boolean add = Arrays.stream(this.members).toList().add((member));
    }

    public void removeClanMember(SilentRPClanMember member) {
        boolean remove = Arrays.stream(this.members).toList().remove((member));
    }

    public int getClan_fee() {
        return clan_fee;
    }

    public void setClan_fee(int clan_fee) {
        this.clan_fee = clan_fee;
    }

    public boolean isPublic_invite() {
        return public_invite;
    }

    public void setPublic_invite(boolean public_invite) {
        this.public_invite = public_invite;
    }
}
