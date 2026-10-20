package net.dorblacky.silentrp.api;

public class SilentRPApiConnectRequest {
    private String api_key;
    private String server_ip;
    private int server_port;

    public String getApi_key() {
        return api_key;
    }

    public void setApi_key(String api_key) {
        this.api_key = api_key;
    }

    public String getServer_ip() {
        return server_ip;
    }

    public void setServer_ip(String server_ip) {
        this.server_ip = server_ip;
    }

    public int getServer_port() {
        return server_port;
    }

    public void setServer_port(int server_port) {
        this.server_port = server_port;
    }
}
