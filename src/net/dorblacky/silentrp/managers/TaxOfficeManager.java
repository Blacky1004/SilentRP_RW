package net.dorblacky.silentrp.managers;

import net.dorblacky.silentrp.SilentRP;
import net.dorblacky.silentrp.models.SilentRPConfig;
import net.dorblacky.silentrp.models.SilentRPTaxOffice;
import net.dorblacky.silentrp.models.SilentRpPlayer;
import net.risingworld.api.Plugin;
import net.risingworld.api.Server;
import net.risingworld.api.Timer;
import net.risingworld.api.World;
import net.risingworld.api.objects.Player;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class TaxOfficeManager extends SilentRP {
    private final Plugin plugin;
    private final SilentRPConfig siilentConfig;
    private static final String ALGORITHM = "AES";
    private static final String SECRET_KEY = "TrSilentRPTaxofficeKeyTR";
    public static SilentRPTaxOffice  taxOffice;
    public static Timer payTimer;
    public static long stateMoney = 0;
    public TaxOfficeManager(Plugin plugin, SilentRPConfig configContent) throws Exception {
        this.plugin = plugin;
        this.siilentConfig = configContent;
        taxOffice = DatabaseManager.getTaxOffice();
        boolean mustUpdate = false;
        if(taxOffice.stateAsstes == null || taxOffice.stateAsstes.isEmpty()){

            taxOffice.setStateAsstes(encryptNumber(0));
            mustUpdate = true;
        }

        if(taxOffice.getBankName() == null || taxOffice.getBankName().isEmpty()){
            if(silentConfigData.getTaxbank_name() == null || silentConfigData.getTaxbank_name().isEmpty()){
                if(!World.getName().isEmpty()){
                    taxOffice.setBankName("Zentralbank von " + World.getName());
                }
            } else{
                taxOffice.setBankName(silentConfigData.getTaxbank_name());
            }
            mustUpdate = true;
        }

        if(mustUpdate){
            DatabaseManager.createTaxOffice(taxOffice);
        }
    }

    public static int getNettoPrice(int brutto) {
        double vat = PriceManager.priceConfig.getVat_percent();
        double netto = brutto /(1 + (vat / 100.0));
        return (int) netto;
    }

    public static void addToStateAsset(int brutto) throws Exception {
        double vat = PriceManager.priceConfig.getVat_percent();
        double netto = brutto /(1 + (vat / 100.0));

        double tax = brutto - netto;
        double aktualTy = decryptNumber(taxOffice.getStateAsstes());
        aktualTy += tax;


        taxOffice.setStateAsstes(encryptNumber(aktualTy));
        DatabaseManager.updateTaxeState(taxOffice.getStateAsstes());
    }

    public static String encryptNumber(double number) throws Exception {
        // 1. Zahl in String umwandeln
        String plainText = String.valueOf(number);

        // 2. Cipher initialisieren
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        // 3. Verschlüsseln
        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        // 4. Als Base64-String codieren (sicher für Datenbanken)
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    public static double decryptNumber(String encryptedBase64) throws Exception {
        // 1. Base64-String zurück in Bytes decodieren
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedBase64);

        // 2. Cipher im Entschlüsselungsmodus initialisieren
        SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), ALGORITHM);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec);

        // 3. Entschlüsseln
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        String decryptedString = new String(decryptedBytes, StandardCharsets.UTF_8);

        // 4. Zurück in Zahl konvertieren
        return Long.parseLong(decryptedString);
    }

    public static void startPayTimer(){
        System.out.println("[SILENTRP-TAXMGM] Paychecksystem is enabled. Starting Paychecktimer.");;
        payTimer = new Timer(silentConfigData.getPaychecksystem_interval() * 60,0,-1, TaxOfficeManager::doPayCheck);
        payTimer.start();
    }

    public static void doPayCheck() {
        System.out.println("[SILENTRP-TAXMGM] Running paycheck....");
        int paycheckPlayers = 0;
        for (Player p: Server.getAllPlayers()){
            if(!p.isDead() && p.hasAttribute("paycheck") && Server.getRunningTime() - (float)p.getAttribute("paycheck") >= (silentConfigData.getPaychecksystem_interval()) * 60){
                SilentRpPlayer rp = DatabaseManager.getSilentPlayer(p.getUID());
                int aktMoney = rp.getMoney();
                rp.setMoney(aktMoney + silentConfigData.getPaycheck_money());
                DatabaseManager.updatePlayer(rp);
                p.sendTextMessage("Du hast eine Sozialleistung in Höhe von "+silentConfigData.getPaycheck_money()+"€ erhalten");
                stateMoney -= silentConfigData.getPaycheck_money();
                paycheckPlayers += 1;
                p.setAttribute("paycheck", Server.getRunningTime());
            }
        }
        System.out.println("[SILENTRP-TAXMGM] Paychecks given vor "+paycheckPlayers+" Players. StateMoney now: "+stateMoney+"€.");;
    }

    public static void disablePayTimer() {
        if(payTimer != null){
            payTimer.kill();
        }
    }
}
