package net.dorblacky.silentrp.models;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class SilentTradingTypeConfig {
    private String hotel;
    private String broker;
    private String verein;
    private String buergerservice;
    private String berufe;
    private String einwohner;
    private String auktionator;
    private String shop;

    public String getHotel() {
        return hotel;
    }

    public void setHotel(String hotel) {
        this.hotel = hotel;
    }

    public String getBroker() {
        return broker;
    }

    public void setBroker(String broker) {
        this.broker = broker;
    }

    public String getVerein() {
        return verein;
    }

    public void setVerein(String verein) {
        this.verein = verein;
    }

    public String getBuergerservice() {
        return buergerservice;
    }

    public void setBuergerservice(String buergerservice) {
        this.buergerservice = buergerservice;
    }

    public String getBerufe() {
        return berufe;
    }

    public void setBerufe(String berufe) {
        this.berufe = berufe;
    }

    public String getEinwohner() {
        return einwohner;
    }

    public void setEinwohner(String einwohner) {
        this.einwohner = einwohner;
    }

    public String getAuktionator() {
        return auktionator;
    }

    public void setAuktionator(String auktionator) {
        this.auktionator = auktionator;
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
    }

    public static String getDescription(SilentTrader.traderType type, String pluginPath) {
        String result = "";
        try {
            Gson json = new Gson();
            String jsonFile = pluginPath+"/resources/data/trader_type.json";
            JsonReader reader = new JsonReader(new FileReader(jsonFile));
            SilentTradingTypeConfig cfg = json.fromJson(reader, SilentTradingTypeConfig.class);
            if(cfg != null){
                result = switch (type) {
                    default -> cfg.getHotel();
                    case ASSOCIATION -> cfg.getVerein();
                    case AUCTIONER -> cfg.getAuktionator();
                    case BROKER -> cfg.getBroker();
                    case SHOP -> cfg.getShop();
                    case CITIZEN -> cfg.getEinwohner();
                    case PROFESSIONS -> cfg.getBerufe();
                    case CITIZENSERVICE -> cfg.getEinwohner();
                };
            }
        } catch (FileNotFoundException e){
            System.out.println("[SILENTRP-MAIN] Fehler in SilentTradingTypeConfig: "+e.getMessage());
            result = "";
        }
        return result;
    }
}
