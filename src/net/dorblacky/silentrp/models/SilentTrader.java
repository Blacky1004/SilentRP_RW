package net.dorblacky.silentrp.models;

import com.google.gson.Gson;
import net.risingworld.api.utils.Quaternion;
import net.risingworld.api.utils.Vector3f;

import java.util.List;

public class SilentTrader {
    public enum traderType{
        HOTEL, ASSOCIATION, BROKER, SHOP, CITIZENSERVICE, PROFESSIONS, CITIZEN, AUCTIONER;

        public int getTypeID(){
            if(this == ASSOCIATION ) return 2;
            else if(this == BROKER ) return 3;
            else if(this == SHOP ) return 4;
            else if(this == CITIZENSERVICE ) return 5;
            else if(this == PROFESSIONS ) return 6;
            else if(this == CITIZEN ) return 7;
            else if(this == AUCTIONER ) return 8;
            else return 1;
        }

        public traderType getTraderType(int x){
            switch (x){
                default:
                case 1: return traderType.HOTEL;
                case 2: return traderType.ASSOCIATION;
                case 3: return traderType.BROKER;
                case 4: return traderType.SHOP;
                case 5: return traderType.CITIZENSERVICE;
                case 6: return traderType.PROFESSIONS;
                case 7: return traderType.CITIZEN;
                case 8: return traderType.AUCTIONER;
            }
        }

        public String getTradername() {
            if(this == ASSOCIATION ) return "Vereinsregister";
            else if(this == BROKER ) return "Makler";
            else if(this == SHOP ) return "Verkäufer";
            else if(this == CITIZENSERVICE ) return "Bürgerservice";
            else if(this == PROFESSIONS ) return "Berufe";
            else if(this == CITIZEN ) return "Einwohner";
            else if(this == AUCTIONER ) return "Auktionator";
            else return "Hotel/Unterkünfte";
        }
    }

    private int id;
    private long npc_typeid;
    private String name;
    private int trader_type;
    private String position;
    private int traded_money;
    private boolean isDeath = false;
    private List<SilentTraderClothe> clothes;
    private SilentTraderSkin skin;
    private float deathTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getNpc_typeid() {
        return npc_typeid;
    }

    public void setNpc_typeid(long npc_typeid) {
        this.npc_typeid = npc_typeid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getTrader_type() {
        return trader_type;
    }

    public void setTrader_type(int trader_type) {
        this.trader_type = trader_type;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public int getTraded_money() {
        return traded_money;
    }

    public void setTraded_money(int traded_money) {
        this.traded_money = traded_money;
    }

    public void setTraderType(traderType traderType){
        this.trader_type = traderType.getTypeID();
    }

    public traderType getTraderType(int x){
        switch (x){
            default:
            case 1: return traderType.HOTEL;
            case 2: return traderType.ASSOCIATION;
            case 3: return traderType.BROKER;
            case 4: return traderType.SHOP;
            case 5: return traderType.CITIZENSERVICE;
            case 6: return traderType.PROFESSIONS;
            case 7: return traderType.CITIZEN;
        }
    }

    public TraderPosition getTraderPosition(){
        TraderPosition pos = null;
        try {
            if(!this.position.isEmpty() && this.position != "{}"){
                Gson posGson = new Gson();
                pos = posGson.fromJson(this.position, TraderPosition.class);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return pos;
    }

    public void setTraderPosition(Vector3f position, Quaternion rotation){
        String pos = "{}";
        try{
            TraderPosition tPos = new TraderPosition();
            tPos.setPosition(position);
            tPos.setRotation(rotation);
            this.position = new Gson().toJson(tPos);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public boolean isDeath() {
        return isDeath;
    }

    public void setDeath(boolean death) {
        isDeath = death;
    }

    public List<SilentTraderClothe> getClothes() {
        return clothes;
    }

    public void setClothes(List<SilentTraderClothe> clothes) {
        this.clothes = clothes;
    }

    public SilentTraderSkin getSkin() {
        return skin;
    }

    public void setSkin(SilentTraderSkin skin) {
        this.skin = skin;
    }

    public float getDeathTime() {
        return deathTime;
    }

    public void setDeathTime(float deathTime) {
        this.deathTime = deathTime;
    }
}
