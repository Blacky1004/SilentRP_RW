package net.dorblacky.silentrp.models;

import java.util.Map;

public class SilentRPPriceConfig {
    private float vat_percent;
    private float purchase_percent;

    private int goldprice_min;
    private int goldprice_max;
    private Map<String, Integer> items;

    public float getVat_percent() {
        return vat_percent;
    }

    public void setVat_percent(float vat_percent) {
        this.vat_percent = vat_percent;
    }

    public float getPurchase_percent() {
        return purchase_percent;
    }

    public void setPurchase_percent(float purchase_percent) {
        this.purchase_percent = purchase_percent;
    }

    public int getGoldpriceMin() {
        return goldprice_min;
    }

    public void setGoldpriceMin(int goldpriceMin) {
        this.goldprice_min = goldprice_min;
    }

    public int getGoldprice_max() {
        return goldprice_max;
    }

    public void setGoldprice_max(int goldprice_max) {
        this.goldprice_max = goldprice_max;
    }

    public Map<String, Integer> getItems() {
        return items;
    }

    public void setItems(Map<String,Integer> items) {
        this.items = items;
    }
}
