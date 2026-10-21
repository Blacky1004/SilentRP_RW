package net.dorblacky.silentrp.models;

import java.util.Map;

public class SilentRPPriceConfig {
    private float vat_percent;
    private float purchase_percent;
    private int goldpriceMin;
    private int goldpriceMax;
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
        return goldpriceMin;
    }

    public void setGoldpriceMin(int goldpriceMin) {
        this.goldpriceMin = goldpriceMin;
    }

    public int getGoldpriceMax() {
        return goldpriceMax;
    }

    public void setGoldpriceMax(int goldpriceMax) {
        this.goldpriceMax = goldpriceMax;
    }

    public Map<String, Integer> getItems() {
        return items;
    }

    public void setItems(Map<String,Integer> items) {
        this.items = items;
    }
}
