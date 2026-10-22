package net.dorblacky.silentrp.models;

import net.risingworld.api.definitions.Definitions;
import net.risingworld.api.definitions.Items;
import net.risingworld.api.objects.Item;
import net.risingworld.api.utils.ItemData;

import java.util.Date;
import java.util.UUID;

public class SilentRPAuction {
    private UUID uuid;
    private String itemId;
    private int startPrice;
    private int betPrice;
    private Date startDate;
    private Date endDate;
    private boolean buyItNow;
    private String betPlayer;
    private String sellerId;

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public int getStartPrice() {
        return startPrice;
    }

    public void setStartPrice(int startPrice) {
        this.startPrice = startPrice;
    }

    public int getBetPrice() {
        return betPrice;
    }

    public void setBetPrice(int betPrice) {
        this.betPrice = betPrice;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public boolean isBuyItNow() {
        return buyItNow;
    }

    public void setBuyItNow(boolean buyItNow) {
        this.buyItNow = buyItNow;
    }

    public String getBetPlayer() {
        return betPlayer;
    }

    public void setBetPlayer(String betPlayer) {
        this.betPlayer = betPlayer;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }


}
