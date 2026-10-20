package net.dorblacky.silentrp.api;

public class SilentRPApiConnectResponse {
    private String owner;
    private String aktual_plugin_version;
    private String database_update;
    public int account_type;

    public int getAccount_type() {
        return account_type;
    }

    public void setAccount_type(int account_type) {
        this.account_type = account_type;
    }

    public String getOwner() {
        return owner;
    }
    public void setOwner(String owner) {
        this.owner = owner;
    }
    public String getAktual_plugin_version() {
        return aktual_plugin_version;
    }
    public void setAktual_plugin_version(String aktual_plugin_version) {
        this.aktual_plugin_version = aktual_plugin_version;
    }
    public String getDatabase_update() {
        return database_update;
    }
    public void setDatabase_update(String database_update) {
        this.database_update = database_update;
    }
}
