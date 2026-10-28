package net.dorblacky.silentrp.managers;

import net.dorblacky.silentrp.SilentRP;
import net.dorblacky.silentrp.models.SilentRpPlayer;
import net.dorblacky.silentrp.models.*;
import net.risingworld.api.Plugin;
import net.risingworld.api.events.EventMethod;
import net.risingworld.api.events.Listener;
import net.risingworld.api.events.player.PlayerCommandEvent;
import net.risingworld.api.events.player.PlayerKeyEvent;
import net.risingworld.api.objects.*;
import net.risingworld.api.utils.*;

import java.util.ArrayList;
import java.util.List;

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
//                    case "/create_trader": createTrader(player); break;
//                    case "/save_trader": saveTrader(player); break;
                    case "/update_trader": updateTrader(player); break;
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

    private void updateTrader(Player p){
        p.getNpcInLineOfSight(10,(npc) -> {
            if(npc != null && npc.hasAttribute("trader", Integer.class)){
                int trader_id = (int) npc.getAttribute("trader");
                SilentTrader trader = TradingManager.traders.stream().filter(t ->t.getId()  == trader_id).findFirst().orElse(null);
                if(trader != null){
                    TradingManager.editTrader(p, trader, npc, plugin);
                }
            } else if(npc != null && !npc.hasAttribute("trader", Integer.class)) {
                SilentTrader trader = new SilentTrader();
                trader.setDeathTime(0);
                trader.setTraded_money(0);
                trader.setNpc_typeid(npc.getGlobalID());
                trader.setName(npc.getName());
                trader.setTraderType(SilentTrader.traderType.SHOP);
                trader.setTraderPosition(npc.getPosition(), npc.getRotation());
                trader = DatabaseManager.createTrader(trader);
                assert trader != null;
                if(trader.getId() > 0) {
                    TradingManager.traders.add(trader);
                    TradingManager.editTrader(p, trader, npc, plugin);
                }
            }
        });


    }
    private void addTrader(Player p) {
//        p.getAreaSelectionData((AreaSelectionResult area) -> {
//            Vector3f start = Vector3f.ZERO;
//            Vector3f end = Vector3f.ZERO;
//            if(area == null){
//                System.out.println("Area´selection tool is not active!");
//            } else{
//                if(area.isStartPositionSet()){
//                    start = area.getStartPosition();
//                }
//
//                if(area.isEndPositionSet()){
//                    end = area.getEndPosition();
//                }
//                Vector3f direction = p.getPosition().subtract(p.getPosition()).normalizeLocal();
//
//                //Calculate rotation (quaternion)
//                Quaternion rotation = new Quaternion();
//                rotation.lookAt(direction);
//                Npc npc = World.spawnNpc((short) 200, 0 ,start, rotation, true);
//
//                if(npc != null){
//                    SilentTrader trader = new SilentTrader();
//                    trader.setNpc_typeid(npc.getGlobalID());
//                    trader.setTraderType(SilentTrader.traderType.SHOP);
//                    trader.setTraderPosition(npc.getPosition(), npc.getRotation());
//                    trader.setName("Neuer Trader");
//                    trader.setTraded_money(0);
//
//                    npc.setName("Neuer Trader");
//                    npc.setAttribute("trader",  trader.getNpc_typeid());
//                    npc.setBehaviour (Npcs.Behaviour.Dummy);
//                    World.getNpc(npc.getGlobalID()).setLocked(true);
//                    Server.saveNpcs();
//
//                    TradingManager.editTrader(p, trader, npc,plugin);
//                }
//            }
//        });
//        isAreaToolEnabled = false;
//        p.disableAreaSelectionTool();
        p.getNpcInLineOfSight(10, (npc) -> {
            if(npc != null){
                //Haben wir bereits einen Trader mit der npcid
            }
        });
    }

    @EventMethod
    public  void onPlayerKey(PlayerKeyEvent e){
        Player p =e.getPlayer();
        if(isAreaToolEnabled && e.getKey() == Key.Enter && e.isPressed()){
            //saveTrader(p);
        } else if(isAreaToolEnabled && e.getKey() == Key.Delete && e.isPressed()) {
            isAreaToolEnabled = false;
            p.disableAreaSelectionTool();
        }
    }


}
