package net.dorblacky.silentrp.managers;

import com.google.gson.Gson;
import net.dorblacky.silentrp.SilentRP;
import net.dorblacky.silentrp.models.SilentRPConfig;
import net.dorblacky.silentrp.models.SilentRPPriceConfig;
import net.risingworld.api.Plugin;
import net.risingworld.api.Server;
import net.risingworld.api.Timer;
import net.risingworld.api.objects.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.ThreadLocalRandom;

public class PriceManager extends SilentRP {
    private final Plugin plugin;
    private final SilentRPConfig config;
    private final Timer timer;
    public static int aktualGoldPrice = 0;
    public static SilentRPPriceConfig priceConfig;
    public static Queue<Integer> statistiken = new ArrayDeque<>();

    public PriceManager(Plugin plugin, SilentRPConfig config) {
        this.plugin = plugin;
        this.config = config;
        this.timer = new Timer(1200, 0,-1, this::doWork);
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
        PriceManager.aktualGoldPrice = ThreadLocalRandom.current().nextInt(priceConfig.getGoldpriceMin(), priceConfig.getGoldprice_max());
        System.out.println("[SILENTRP-PRICEMGM] Pricemanager started width gold price: " + PriceManager.aktualGoldPrice);
        statistiken.add(PriceManager.aktualGoldPrice);
        timer.start();
    }
    private void doWork(){
        int oldGoldPrice = aktualGoldPrice;
        aktualGoldPrice = ThreadLocalRandom.current().nextInt(priceConfig.getGoldpriceMin(), priceConfig.getGoldprice_max());
        if(oldGoldPrice != aktualGoldPrice){
            double prozentAenderung = ((double)(aktualGoldPrice - oldGoldPrice) / oldGoldPrice) * 100;
            String priceMessage = "";
            if(oldGoldPrice < aktualGoldPrice){
                priceMessage= "Der aktuelle <color=\"#c9a214\">Goldpreis</color> ist um <color=\"#20af0a\">"+String.format("%.2f", Math.abs(prozentAenderung))+"%</color> gestiegen";
            } else{
                priceMessage= "Der aktuelle <color=\"#c9a214\">Goldpreis</color> ist um <color=\"#c94e22\">"+String.format("%.2f", Math.abs(prozentAenderung))+"%</color> gesunken";
            }
            for (Player p : Server.getAllPlayers()){
                p.sendTextMessage(priceMessage);
            }
        }
        if(statistiken.size() == 10){
            statistiken.poll();
        }
        statistiken.add(aktualGoldPrice);
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
