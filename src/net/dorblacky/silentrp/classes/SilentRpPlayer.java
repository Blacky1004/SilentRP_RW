package net.dorblacky.silentrp.classes;

import net.dorblacky.silentrp.SilentRP;
import net.dorblacky.silentrp.models.SilentRPConfigContent;
import net.risingworld.api.Plugin;
import net.risingworld.api.Server;
import net.risingworld.api.assets.TextureAsset;
import net.risingworld.api.events.EventMethod;
import net.risingworld.api.events.Listener;
import net.risingworld.api.events.player.PlayerConnectEvent;
import net.risingworld.api.events.player.PlayerSpawnEvent;
import net.risingworld.api.objects.Player;
import net.risingworld.api.ui.UIElement;

import java.util.Arrays;

public class SilentRpPlayer extends SilentRP implements Listener {
    private final Plugin plugin;
    private final SilentRPConfigContent silentConfig;

    public SilentRpPlayer(Plugin plugin, SilentRPConfigContent configContent) {
        this.plugin = plugin;
        this.silentConfig = configContent;
        registerEventListener(this);
    }
    @EventMethod
    public void  onPlayerConnectEvent(PlayerConnectEvent event){
        Player player = event.getPlayer();
        if(player != null){
            System.out.println("[SILENTRP-PLAYER] Player '"+player.getName()+"' connected to this Server.");
            if(this.silentConfig.isEnable_global_ban()){
                //prüfen ob der Player global gebannt wurde
                var banEntry = new SrpDatabaseManager(plugin, silentConfig).checkPlayerBannedGlobal(player.getUID());
                if(banEntry != null){
                    var onlineAdmins = Server.getAllPlayers();

                    if(banEntry.getBan_points() >= silentConfig.getGlobal_ban_atpoints()){
                        if(!Server.isPlayerBanned(player.getUID())) {
                            player.ban(banEntry.getReason());
                        }
                    }
                    else{

                        player.setAttribute("global_banned", true);
                        player.setAttribute("bann_points", banEntry.getBan_points());
                        player.setAttribute("ban_reason", banEntry.getReason());
                        if(Arrays.stream(onlineAdmins).findAny().isPresent()) {
                            for (Player admin : onlineAdmins){
                                admin.sendTextMessage("WARNING!! Player '"+player.getName()+"("+player.getUID()+")' was global banned with banpoints "+banEntry.getBan_points()+" !");
                            }
                        }
                        player.setName(player.getName() + "[GlobalBan marked]");
                        System.out.println("[SILENTRP-BAN] WARNING!! Player '"+player.getName()+"("+player.getUID()+")' was global banned with banpoints "+banEntry.getBan_points()+" !");
                    }
                }
            }

            var playerData = new SrpDatabaseManager(plugin, silentConfig).getPlayerData(player.getUID());
            if(playerData == null){
                player.kick("PluginFehler '235' => Bitte wende dich an einen Administrator!");
            }
            System.out.println("Player found in db");
            assert playerData != null;
            player.setAttribute("silentrp_player", playerData);
        }
    }

    @EventMethod
    public void onPlayerPlayerSpanEvent(PlayerSpawnEvent event){
        Player player = event.getPlayer();
        if(player.hasAttribute("global_banned") && player.getAttribute("global_banned").equals(true)){
            player.sendTextMessage("Achtung Du wurdest als global Gebant makiert, erfüllst aber für einen Ban auf diesem Server nicht die eingestellten Anforderungen!");
            player.sendTextMessage("Solltest Du mit dem globalen Ban einverstanden sein, so wende dich bitte an http://silentrp.de/checkban");
        }
        else {
            player.sendTextMessage("Hallo");

        }

        UIElement watermarkElem = new UIElement();
        watermarkElem.setBackgroundColor(0, 0, 0, 0);
        watermarkElem.setSize(300, 70, false);
        watermarkElem.setClickable(false);
        watermarkElem.setPosition(1.0F, 87.0F, true);
        System.out.println("View Watermark vor accountTYpe "+ SilentRP.serverAccountType);
        if(SilentRP.serverAccountType > 2) {
            if(silentConfig.isVisible_watermark()){
                System.out.println("Watermark is on");
                TextureAsset watermark = null;
                if(!silentConfig.getWatermark_url().isEmpty()){
                    watermark = TextureAsset.loadFromURL(silentConfig.getWatermark_url());
                }
                else {
                    watermark = TextureAsset.loadFromFile(plugin.getPath() + "/Assets/images/watermark.png");
                }
                watermarkElem.style.backgroundImage.set(watermark);
                watermarkElem.setVisible(true);
                player.addUIElement(watermarkElem);
            }
        } else {
            TextureAsset image = TextureAsset.loadFromPlugin(plugin, "/resources/images/watermarklogo.png");
            watermarkElem.style.backgroundImage.set(image);
            watermarkElem.setVisible(true);
            player.addUIElement(watermarkElem);
        }

    }
}
