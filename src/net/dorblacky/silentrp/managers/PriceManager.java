package net.dorblacky.silentrp.managers;

import com.google.gson.Gson;
import net.dorblacky.silentrp.SilentRP;
import net.dorblacky.silentrp.models.SilentRPConfig;
import net.dorblacky.silentrp.models.SilentRPPriceConfig;
import net.risingworld.api.Plugin;
import net.risingworld.api.Server;
import net.risingworld.api.Timer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.ThreadLocalRandom;

public class PriceManager extends SilentRP {
    private final Plugin plugin;
    private final SilentRPConfig config;
    private final Timer timer;
    public static int aktualGoldPrice = 0;
    public static SilentRPPriceConfig priceConfig;

    public PriceManager(Plugin plugin, SilentRPConfig config) {
        this.plugin = plugin;
        this.config = config;
        this.timer = new Timer(60, 0,-1, this::doWork);
        String configFullName = this.plugin.getPath() + "/prices.json";
        SilentRPPriceConfig configContent = new SilentRPPriceConfig();
        Path configPath = Path.of(configFullName);
        if(Files.exists(configPath)) try {
            String fileContent = Files.readString(configPath);
            Gson gson = new Gson();
            priceConfig = gson.fromJson(fileContent, SilentRPPriceConfig.class);
        } catch (IOException e){
            System.out.println("[SILENTRP-PRICEMGM] Error in PriceManager : " + e.getMessage());
        }
    }

    public void startPriceTimer()
    {
        PriceManager.aktualGoldPrice = ThreadLocalRandom.current().nextInt(priceConfig.getGoldpriceMin(), priceConfig.getGoldpriceMax());
        System.out.println("[SILENTRP-PRICEMGM] Pricemanager started width gold price: " + PriceManager.aktualGoldPrice);
        timer.start();
    }
    private void doWork(){
        int oldGoldPrice = aktualGoldPrice;
        aktualGoldPrice = ThreadLocalRandom.current().nextInt(priceConfig.getGoldpriceMin(), priceConfig.getGoldpriceMax());
        if(oldGoldPrice != aktualGoldPrice){
            double prozentAenderung = ((oldGoldPrice - aktualGoldPrice) / aktualGoldPrice) * 100;

            if(oldGoldPrice < config.getGoldpriceMin()){
                Server.broadcastYellMessage("Der aktuelle Goldpreis ist um "+Math.abs(prozentAenderung)+" gestiegen", 100, true);
            } else{
                Server.broadcastYellMessage("Der aktuelle Goldpreis ist um "+Math.abs(prozentAenderung)+" gesunken", 100, true);
            }
        }
        System.out.println("[SILENTRP-PRICEMGM] Der Golpreis wurde auf "+aktualGoldPrice+" € aktualisiert.");
    }

    public void stopPriceTimer(){
        timer.kill();
    }

    public static int geSellingPrice(String itemName){
        if(itemName.equals("goldingot")){ return aktualGoldPrice;}
        int item = priceConfig.getItems().get(itemName);
        double grundpreis = aktualGoldPrice * (item / 1000.0);
        double mwstAufschlag = grundpreis * (priceConfig.getVat_percent() / 100.0);
        double endpreisUnverundet = grundpreis + mwstAufschlag;

        return (int) Math.round(endpreisUnverundet);
    }

    public static int getPurchasePrice(String itemName) {
        if(itemName.equals("goldingot")){ return aktualGoldPrice;}
        int item = priceConfig.getItems().get(itemName);
        double grundpreis = aktualGoldPrice * (item / 1000.0);
        double abzug = grundpreis * (priceConfig.getPurchase_percent() / 100.0);

        return (int) Math.round(grundpreis - abzug);
    }
}
