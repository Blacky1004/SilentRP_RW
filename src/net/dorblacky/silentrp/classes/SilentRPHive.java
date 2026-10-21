package net.dorblacky.silentrp.classes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.dorblacky.silentrp.SilentRP;
import net.dorblacky.silentrp.api.SilentRPApiBanlistRequest;
import net.dorblacky.silentrp.api.SilentRPApiBanlistResponse;
import net.dorblacky.silentrp.api.SilentRPApiConnectRequest;
import net.dorblacky.silentrp.api.SilentRPApiConnectResponse;
import net.dorblacky.silentrp.models.SilentRPConfig;
import net.risingworld.api.Server;

import javax.net.ssl.*;
import java.io.*;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class SilentRPHive extends SilentRP {
    private final SilentRPConfig silentConfig;
    public SilentRPHive(SilentRPConfig contentData) {
        this.silentConfig = contentData;
    }
    public SilentRPApiConnectResponse connect() {
        String apiUrl = this.silentConfig.getHive_url() + "plugin_connect";
        try {
            configureTrustAllSSL();
            URL url = new URL(apiUrl);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            var requestData = new SilentRPApiConnectRequest();
            requestData.setApi_key(this.silentConfig.getApiKey());

            String serverIP = Server.getOption("Server_IP");
            if(serverIP.isEmpty()) {
                serverIP = InetAddress.getLocalHost().getHostAddress();
            }
            int serverPort = 4255;
            if(!Server.getOption("Server_Port").isEmpty()) {
                serverPort = Integer.parseInt(Server.getOption("Server_Port"));
            }
            requestData.setServer_ip(serverIP);
            requestData.setServer_port(serverPort);

            Gson json = new Gson();
            String jsonString = json.toJson(requestData);
            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }
            int status = conn.getResponseCode();
            switch(status) {
                default:
                case 400:{

                }break;
                case 200: {
                    try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine = null;
                        while((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }

                        if(response.toString().isEmpty()) {
                            throw new Exception("[SILENTRP-HIVE] empty response reqeuested!");
                        }

                        Gson rjson = new Gson();
                        System.out.println(response.toString());
                        return (SilentRPApiConnectResponse) rjson.fromJson(response.toString().trim(), SilentRPApiConnectResponse.class);
                    }
                }
            }
            conn.disconnect();
        }
        catch(MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public List<GlobalBanEntry> getglobalBans() {
        String apiUrl = this.silentConfig.getHive_url() + "get_globalbans";
        List<GlobalBanEntry> entryList = null;
        try {
            configureTrustAllSSL();
            URL url = new URL(apiUrl);

            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);

            var requestContent = new SilentRPApiBanlistRequest();
            requestContent.setApi_key(this.silentConfig.getApiKey());
            requestContent.setBan_points(this.silentConfig.getGlobal_ban_atpoints());

            Gson json = new Gson();
            String jsonString = json.toJson(requestContent);
            try(OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int status = conn.getResponseCode();
            switch(status) {
                default:
                case 400: {

                }break;
                case 200:{
                    List<SilentRPApiBanlistResponse> bannlist = new ArrayList<SilentRPApiBanlistResponse>();
                    try(BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"))) {
                        StringBuilder response = new StringBuilder();
                        String responseLine = null;
                        while((responseLine = br.readLine()) != null) {
                            response.append(responseLine.trim());
                        }

                        if(response.toString().isEmpty()) {
                            throw new Exception("[SILENTRP-HIVE] empty response reqeuested!");
                        }

                        SilentRPApiBanlistResponse bann_contens = new GsonBuilder().create().fromJson(response.toString(), SilentRPApiBanlistResponse.class);
                        if(bann_contens.getEntrys() != null) {
                            entryList = new ArrayList<GlobalBanEntry>();
                            for(GlobalBanEntry entry : bann_contens.getEntrys()) {
                                entryList.add(entry);
                            }
                        }
                    }
                    return entryList;
                }
            }
        }
        catch(MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
        catch(Exception ex) {
            System.out.println("[SILENTRP-HIVE] ERROR: " + ex.getMessage());
        }
        return null;
    }
    private void configureTrustAllSSL() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    @Override
                    public X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }
                    @Override
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        // Do nothing - trust all clients
                    }
                    @Override
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        // Do nothing - trust all servers
                    }
                }
        };

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

        HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
    }
}
