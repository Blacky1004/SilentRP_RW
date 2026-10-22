package net.dorblacky.silentrp.managers;

import net.dorblacky.silentrp.SilentRP;
import net.dorblacky.silentrp.models.SilentRPConfig;
import net.dorblacky.silentrp.models.SilentRPTaxOffice;
import net.risingworld.api.Plugin;
import net.risingworld.api.Server;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class TaxOfficeManager extends SilentRP {
    private final Plugin plugin;
    private final SilentRPConfig siilentConfig;
    private static final byte[] keyBytes = new byte[]{10, 4, 17, 9, 8, 9, 73, 76, 11, 14, 10, 11, 12, 13, 14, 15};
    private static final SecretKeySpec key = new SecretKeySpec(keyBytes, "RawBytes");
    private static final Cipher cipher;
    public static SilentRPTaxOffice  taxOffice;

    static {
        try {
            cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        } catch (Exception e) {
            throw new RuntimeException("Fehler beim Initialisieren des Ciphers", e);
        }
    }
    
    public TaxOfficeManager(Plugin plugin, SilentRPConfig configContent) throws  InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        this.plugin = plugin;
        this.siilentConfig = configContent;
        taxOffice = DatabaseManager.getTaxOffice();
        boolean mustUpdate = false;
        if(taxOffice.stateAsstes.isEmpty()){
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] plainAssetText = "0".getBytes(StandardCharsets.UTF_8);
            byte[] cipherText = cipher.doFinal(plainAssetText);

            taxOffice.setStateAsstes(Arrays.toString(cipherText));
            mustUpdate = true;
        }

        if(taxOffice.getBankName().isEmpty()){
            if(silentConfigData.getTaxbank_name().isEmpty()){
                if(!Server.getName().isEmpty()){
                    taxOffice.setBankName("Zentralbank von " +Server.getName());
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

    public static void addToStateAsset(int brutto) throws IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        double vat = PriceManager.priceConfig.getVat_percent();
        double netto = brutto /(1 + (vat / 100.0));

        double tax = brutto - netto;
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] cipherActual = taxOffice.getStateAsstes().getBytes(StandardCharsets.UTF_8);
        double aktualTy = Double.parseDouble(Arrays.toString(cipherActual));

        aktualTy += tax;

        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] taxText = String.format("%.0f", aktualTy).getBytes(StandardCharsets.UTF_8);
        byte[] cipherTExt =  cipher.doFinal(taxText);
        taxOffice.setStateAsstes(Arrays.toString(cipherTExt));
        DatabaseManager.updateTaxeState(taxOffice.getStateAsstes());
    }
}
