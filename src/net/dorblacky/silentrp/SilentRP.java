package net.dorblacky.silentrp;

import com.google.gson.Gson;

import net.dorblacky.silentrp.classes.SilentRPConfig;
import net.dorblacky.silentrp.classes.SilentRPHive;
import net.dorblacky.silentrp.classes.SilentRpPlayer;
import net.dorblacky.silentrp.classes.SrpDatabaseManager;
import net.dorblacky.silentrp.models.SilentRPConfigContent;
import net.risingworld.api.Plugin;
import net.risingworld.api.Server;
import net.risingworld.api.database.Database;
import net.risingworld.api.events.Listener;

import javax.net.ssl.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.cert.X509Certificate;
import java.util.Objects;

public class SilentRP extends Plugin implements Listener {
    public final String anonymousApiKey = "9d448347-ab70-4036-9a1a-680b987df301";
    public static int serverAccountType = 1;
    protected static Database silentDatabase;
    protected static String silentVersion = "0.0.3";
    protected String silentPath = this.getPath();
    public SilentRPConfigContent silentConfigData;
    private SilentRPConfig silentConfig;

    @Override
    public void onEnable() {
        try {
            silentConfig = new SilentRPConfig(this);
            this.silentConfigData = silentConfig.readConfig();
            if(Objects.equals(this.silentConfigData.getApiKey(), this.anonymousApiKey)) {
                System.out.println("[SILENTRP-MAIN] Please register this Plugin under https://silentrp.de or entry your ApiKey in configfile!");
            }
            var connectResponse = new SilentRPHive(silentConfigData).connect();
            if(connectResponse != null) {
                if(Objects.equals(silentVersion, connectResponse.getAktual_plugin_version())){
                    System.out.println("[SILENTRP-MAIN] WARNING this plugin is outdated. Please update this Plugin to Version '"+connectResponse.getAktual_plugin_version()+"'");
                }
                SilentRP.serverAccountType = connectResponse.account_type;
                new SrpDatabaseManager(this, silentConfigData).checkDatabaseStructure(connectResponse.getAktual_plugin_version(), connectResponse.getDatabase_update());
            }

            new SilentRpPlayer(this, silentConfigData);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {

    }

}
