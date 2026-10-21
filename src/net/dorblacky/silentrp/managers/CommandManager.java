package net.dorblacky.silentrp.managers;

import net.dorblacky.silentrp.SilentRP;
import net.dorblacky.silentrp.classes.SilentRpPlayer;
import net.dorblacky.silentrp.models.SilentRPClan;
import net.dorblacky.silentrp.models.SilentRPConfig;
import net.risingworld.api.Plugin;
import net.risingworld.api.World;
import net.risingworld.api.database.WorldDatabase;
import net.risingworld.api.definitions.Definitions;
import net.risingworld.api.definitions.Items;
import net.risingworld.api.events.EventMethod;
import net.risingworld.api.events.Listener;
import net.risingworld.api.events.Threading;
import net.risingworld.api.events.player.PlayerCommandEvent;
import net.risingworld.api.objects.Item;
import net.risingworld.api.objects.Player;
import net.risingworld.api.objects.WorldItem;

public class CommandManager extends SilentRP implements Listener {
    private final Plugin plugin;
    private final SilentRPConfig config;
    public CommandManager(Plugin plugin, SilentRPConfig config) {
        this.plugin = plugin;
        this.config = config;
        registerEventListener(this);
    }

    @EventMethod(Threading.Sync)
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


}
