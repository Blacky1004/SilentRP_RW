package net.dorblacky.silentrp.models;

public class SilentRPConfig {
    private String apikey;
    private boolean visible_watermark;
    private String watermark_url;
    private boolean enable_global_ban;
    private int global_ban_atpoints;
    private boolean enable_guildsystem;
    private boolean enable_levelsystem;
    private boolean enable_robberysystem;
    private String hive_url;
    private int player_startmoney;
    private boolean enable_paychecksystem;
    private int paychecksystem_interval;
    private int paycheck_money;
    private float auctions_fee_percent;
    private String taxbank_name;
    private int create_guild_price;
    private int trading_respawn_timer;

    public String getApiKey() {
        return apikey;
    }

    public boolean isEnable_global_ban() {
        return enable_global_ban;
    }

    public void setEnable_global_ban(boolean enable_global_ban) {
        this.enable_global_ban = enable_global_ban;
    }

    public int getGlobal_ban_atpoints() {
        return global_ban_atpoints;
    }

    public void setGlobal_ban_atpoints(int global_ban_atpoints) {
        this.global_ban_atpoints = global_ban_atpoints;
    }

    public boolean isEnable_levelsystem() {
        return enable_levelsystem;
    }

    public void setEnable_levelsystem(boolean enable_levelsystem) {
        this.enable_levelsystem = enable_levelsystem;
    }

    public boolean isEnable_robberysystem() {
        return enable_robberysystem;
    }

    public int getPlayer_startmoney() {
        return player_startmoney;
    }

    public boolean isVisible_watermark() {
        return visible_watermark;
    }

    public void setVisible_watermark(boolean visible_watermark) {
        this.visible_watermark = visible_watermark;
    }

    public void setPlayer_startmoney(int player_startmoney) {
        this.player_startmoney = player_startmoney;
    }

    public boolean isEnable_paychecksystem() {
        return enable_paychecksystem;
    }

    public void setEnable_paychecksystem(boolean enable_paychecksystem) {
        this.enable_paychecksystem = enable_paychecksystem;
    }

    public int getPaychecksystem_interval() {
        return paychecksystem_interval;
    }

    public void setPaychecksystem_interval(int paychecksystem_interval) {
        this.paychecksystem_interval = paychecksystem_interval;
    }

    public void setEnable_robberysystem(boolean enable_robberysystem) {
        this.enable_robberysystem = enable_robberysystem;
    }

    public void setApiKey(String apiKey) {
        this.apikey = apiKey;
    }

    public String getWatermark_url() {
        return watermark_url;
    }

    public void setWatermark_url(String watermark_url) {
        this.watermark_url = watermark_url;
    }

    public String getHive_url() {
        return hive_url;
    }

    public void setHive_url(String hive_url) {
        this.hive_url = hive_url;
    }

    public float getAuctions_fee_percent() {
        return auctions_fee_percent;
    }

    public void setAuctions_fee_percent(float auctions_fee_percent) {
        this.auctions_fee_percent = auctions_fee_percent;
    }

    public String getTaxbank_name() {
        return taxbank_name;
    }

    public void setTaxbank_name(String taxbank_name) {
        this.taxbank_name = taxbank_name;
    }

    public boolean isEnable_guildsystem() {
        return enable_guildsystem;
    }

    public void setEnable_guildsystem(boolean enable_guildsystem) {
        this.enable_guildsystem = enable_guildsystem;
    }

    public int getCreate_guild_price() {
        return create_guild_price;
    }

    public void setCreate_guild_price(int create_guild_price) {
        this.create_guild_price = create_guild_price;
    }

    public int getTrading_respawn_timer() {
        return trading_respawn_timer;
    }

    public void setTrading_respawn_timer(int trading_respawn_timer) {
        this.trading_respawn_timer = trading_respawn_timer;
    }

    public int getPaycheck_money() {
        return paycheck_money;
    }

    public void setPaycheck_money(int paycheck_money) {
        this.paycheck_money = paycheck_money;
    }
}
