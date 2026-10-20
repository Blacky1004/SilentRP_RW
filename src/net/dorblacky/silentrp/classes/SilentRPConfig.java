package net.dorblacky.silentrp.classes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dorblacky.silentrp.SilentRP;
import net.dorblacky.silentrp.models.SilentRPConfigContent;
import net.risingworld.api.Plugin;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class SilentRPConfig extends SilentRP{
    private final Gson myConfig;
    private final Plugin plugin;
    private final String configFileName = "silentrp_config.json";

    public SilentRPConfig(Plugin plugin) throws Exception {
        this.myConfig = new GsonBuilder().setPrettyPrinting().create();
        this.plugin = plugin;
    }

    public SilentRPConfigContent readConfig() throws IOException {
        String configFullName = this.plugin.getPath() + "/" + this.configFileName;
        SilentRPConfigContent configContent = new SilentRPConfigContent();
        Path configPath = Path.of(configFullName);

        if(!Files.exists(configPath)) {
            configContent.setApiKey(this.anonymousApiKey);
            configContent.setEnable_global_ban(false);
            configContent.setEnable_levelsystem(false);
            configContent.setEnable_robberysystem(false);
            configContent.setGlobal_ban_atpoints(50);
            configContent.setWatermark_url("");
            configContent.setVisible_watermark(true);
            configContent.setHive_url("https://localhost:7166/api/");
            saveConfig(configContent);
        } else {
            String fileContent = Files.readString(configPath);
            configContent = myConfig.fromJson(fileContent, SilentRPConfigContent.class);
        }

        return configContent;
    }

    public void saveConfig(SilentRPConfigContent configContent) throws IOException {
        String fileContent = myConfig.toJson(configContent);
        String configFullName = this.plugin.getPath() + "/" + this.configFileName;

        BufferedWriter writer = new BufferedWriter(new FileWriter(configFullName));
        writer.write(fileContent);
        writer.close();
        System.out.println("[SILENTRP-CFG] config file succesfuly written.");
    }
}
