package net.dorblacky.silentrp.managers;

import net.dorblacky.silentrp.SilentRP;
import net.dorblacky.silentrp.classes.SilentRpPlayer;
import net.dorblacky.silentrp.models.SilentRPClan;
import net.dorblacky.silentrp.models.SilentRPConfig;
import net.risingworld.api.Plugin;
import net.risingworld.api.Server;
import net.risingworld.api.World;
import net.risingworld.api.database.WorldDatabase;
import net.risingworld.api.definitions.Definitions;
import net.risingworld.api.definitions.Items;
import net.risingworld.api.definitions.Npcs;
import net.risingworld.api.events.EventMethod;
import net.risingworld.api.events.Listener;
import net.risingworld.api.events.Threading;
import net.risingworld.api.events.player.PlayerCommandEvent;
import net.risingworld.api.events.player.PlayerKeyEvent;
import net.risingworld.api.events.player.PlayerMouseButtonEvent;
import net.risingworld.api.objects.Area;
import net.risingworld.api.objects.Item;
import net.risingworld.api.objects.Player;
import net.risingworld.api.objects.WorldItem;
import net.risingworld.api.utils.AreaSelectionResult;
import net.risingworld.api.utils.Key;
import net.risingworld.api.utils.MouseButton;
import net.risingworld.api.utils.Vector3f;

public class CommandManager extends SilentRP implements Listener {
    private final Plugin plugin;
    private final SilentRPConfig config;
    private boolean isAreaToolEnabled = false;
    public CommandManager(Plugin plugin, SilentRPConfig config) {
        this.plugin = plugin;
        this.config = config;
        registerEventListener(this);
    }

    @EventMethod
    public void onPlayerCommand(PlayerCommandEvent event) {
        Player player = event.getPlayer();
        String command = event.getCommand();
        try {
            String[] cmdArr = command.split(" ");
            if(cmdArr.length > 0){
                switch (cmdArr[0].toLowerCase()){
                    default:
                        player.sendTextMessage("FEHLER! der Befehl '"+cmdArr[0]+"' nicht gefunden!"); break;
                    case "/create_clan": createClanCommand(player, command); break;
                    case "/create_trader": createTrader(player); break;
                    case "/save_trader": saveTrader(player); break;
                }
            }
        } catch(Exception e) {
            System.out.println("[SilentRP-CMD] ERROR  " + e.getMessage());
        }
    }

    private void createClanCommand(Player p, String command){
        if(p.hasAttribute("player_clan")){
            SilentRPClan clan = (SilentRPClan) p.getAttribute("player_clan");
            p.sendTextMessage("Du gehörst derzeitig bereits einen Clan an. Bitte verlasse den Clan erst bevor Du einen neuen Clan anlegen kannst.");
            return;
        }

        String[] splits = command.split(" ",2);
        if(splits.length <= 1){
            p.sendTextMessage("Bitte gib einen Namen des neuen Clans ein");
            return;
        }
        String clanName = splits[1].replace("\"","");

        boolean exist = ClanManager.clanList.stream()
                .anyMatch(clan -> clan.getName().equalsIgnoreCase(clanName));
        if(exist){
            p.sendTextMessage("Es gibt bereits einen Clan mit diesem Namen");
            return;
        }

        SilentRPClan clan = ClanManager.createClan(clanName, "", (SilentRpPlayer) p.getAttribute("srp_player"));
        if(clan != null){
            p.setAttribute("player_clan", clan);
            p.sendTextMessage("Glückwunsch dein Clan wurde erstellt.");
        }
    }

    private void createTrader(Player p){
        p.sendTextMessage("Du kannst");
        if(p.isAdmin()){
            p.registerKeys(Key.F4);
            p.setListenForKeyInput(true);
            p.setAttribute("areatool", true);
            p.enableAreaSelectionTool(false);
            isAreaToolEnabled = true;
        } else{
            p.sendTextMessage("<color=\"red\">Du bist nicht berechtigt diesen Befehl auszuführen!</color>");
        }
    }


    private void saveTrader(Player p) {
        p.getAreaSelectionData((AreaSelectionResult area) -> {
            Vector3f start = Vector3f.ZERO;
            Vector3f end = Vector3f.ZERO;
            if(area == null){
                System.out.println("Area´selection tool is not active!");
            } else{
                if(area.isStartPositionSet()){
                    start = area.getStartPosition();
                }

                if(area.isEndPositionSet()){
                    end = area.getEndPosition();
                }
                short typeId = TradingManager.getNextTraderId();
                var npc = World.spawnNpc(typeId, 0 ,start, p.getRotation(), true);

                if(npc != null){
                    npc.setLocked(true);
                    npc.setStatic(true);
                    npc.setName("Neuer Trader");
                    Server.saveNpcs();
                }
            }
        });
        p.disableAreaSelectionTool();
    }

    @EventMethod
    public  void onPlayerKey(PlayerKeyEvent e){
        Player p =e.getPlayer();
        if(isAreaToolEnabled && e.getKey() == Key.F4 && e.isPressed()){
            saveTrader(p);
        }
    }

    @EventMethod
    public void onPlayerMouseButton(PlayerMouseButtonEvent event) {
        Player player = event.getPlayer();

        if (event.getButton() == MouseButton.Middle && event.isPressed()) {
            player.disableAreaSelectionTool();
            player.sendTextMessage("Arealauswahl abgebrochen.");
        }
    }
}
