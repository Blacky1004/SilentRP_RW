package net.dorblacky.silentrp;

import net.dorblacky.silentrp.managers.ConfigManager;
import net.dorblacky.silentrp.managers.*;
import net.dorblacky.silentrp.models.SilentRPConfig;
import net.risingworld.api.Plugin;
import net.risingworld.api.assets.AssetBundle;
import net.risingworld.api.database.Database;
import net.risingworld.api.events.Listener;

public class SilentRP extends Plugin implements Listener {
    public final String anonymousApiKey = "9d448347-ab70-4036-9a1a-680b987df301";
    public static int serverAccountType = 1;
    protected static Database silentDatabase;
    protected static String silentVersion = "0.0.3";
    protected String silentPath = this.getPath();
    public static SilentRPConfig silentConfigData;
    public static AssetBundle silentAsstsBudle;
    private ConfigManager silentConfig;
    public  PriceManager priceManager;

    @Override
    public void onEnable() {
        try {
            silentConfig = new ConfigManager(this);
            this.silentConfigData = silentConfig.readConfig();
            //silentAsstsBudle = AssetBundle.loadFromFile(this.getPath() + "/prefab/silentrp.bundle");
//            if(Objects.equals(this.silentConfigData.getApiKey(), this.anonymousApiKey)) {
//                System.out.println("[SILENTRP-MAIN] Please register this Plugin under https://silentrp.de or entry your ApiKey in configfile!");
//            }
//            var connectResponse = new SilentRPHive(silentConfigData).connect();
//            if(connectResponse != null) {
//                if(Objects.equals(silentVersion, connectResponse.getAktual_plugin_version())){
//                    System.out.println("[SILENTRP-MAIN] WARNING this plugin is outdated. Please update this Plugin to Version '"+connectResponse.getAktual_plugin_version()+"'");
//                }
//                SilentRP.serverAccountType = connectResponse.account_type;
//
//            }
            new DatabaseManager(this, silentConfigData).checkDatabaseStructure();
            new AdminManager(this, silentConfigData);
            new CommandManager(this, silentConfigData);
            new PlayerManager(this, silentConfigData);
            new ClanManager(this, silentConfigData);
            new TradingManager(this, silentConfigData);
            priceManager = new PriceManager(this, silentConfigData);
            priceManager.startPriceTimer();

            new TaxOfficeManager(this, silentConfigData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        if(priceManager != null){
            priceManager.stopPriceTimer();
            priceManager = null;
        }
        TaxOfficeManager.disablePayTimer();
    }

}
