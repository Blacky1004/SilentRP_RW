package net.dorblacky.silentrp.api;

public class SilentRPApiBanlistRequest {
    private String api_key;
    private int ban_points;

    public String getApi_key() {
        return api_key;
    }
    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }
    public int getBan_points() {
        return ban_points;
    }
    public void setBan_points(int ban_points) {
        this.ban_points = ban_points;
    }
}
