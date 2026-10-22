package net.dorblacky.silentrp.managers;

import net.dorblacky.silentrp.SilentRP;
import net.dorblacky.silentrp.classes.SilentRpPlayer;
import net.dorblacky.silentrp.models.SilentRPConfig;
import net.dorblacky.silentrp.models.SilentTrader;
import net.risingworld.api.Plugin;
import net.risingworld.api.World;
import net.risingworld.api.definitions.Npcs;
import net.risingworld.api.events.EventMethod;
import net.risingworld.api.events.Listener;
import net.risingworld.api.events.Threading;
import net.risingworld.api.events.npc.NpcDeathEvent;
import net.risingworld.api.events.player.PlayerDamageEvent;
import net.risingworld.api.events.player.PlayerHitNpcEvent;
import net.risingworld.api.events.player.PlayerNpcInteractionEvent;
import net.risingworld.api.objects.Npc;
import net.risingworld.api.objects.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class TradingManager extends SilentRP implements Listener {
    private final Plugin plugin;
    private final SilentRPConfig silentConfig;
    public static List<SilentTrader> traders;

    public TradingManager(Plugin plugin, SilentRPConfig configContent) {
        this.plugin = plugin;
        this.silentConfig = configContent;
        //Holen aller NPC aus der Db und in die Welt platzieren
        traders = DatabaseManager.getTraderNPCs();
        registerEventListener(this);
    }

    public static void createTrader(Npc npc, SilentTrader.traderType type) {
        if(npc != null) {
            SilentTrader trader = new SilentTrader();
            trader.setTraderType(type);
            trader.setTraded_money(0);
            trader.setTraderPosition(npc.getPosition(), npc.getRotation());
            trader.setNpc_typeid(getNextTraderId());
            trader.setName(npc.getName());

            trader = DatabaseManager.createTrader(trader);
            if(trader != null && trader.getId() > 0) {
                traders.add(trader);
            }
        }
    }

    public static short getNextTraderId(){
        return (short) Arrays.stream(World.getAllNpcs()).mapToInt(Npc::getTypeID).max().orElse(0);
    }

    @EventMethod(Threading.Sync)
    public void onTraderInterAction(PlayerNpcInteractionEvent event) {
        Npcs.InteractionType interactionType = event.getInteractionType();
        Npc npc = event.getNpc();
        Player player = event.getPlayer();

        java.util.Optional<SilentTrader> foundTrader = traders.stream().filter(trader-> trader.getNpc_typeid() == npc.getTypeID()).findFirst();
        if(foundTrader.isPresent()) {
            SilentTrader trader = foundTrader.get();
            if(npc.hasAttribute("trade_ui")){

            }
        }
        else {
            System.out.println("[SILENTRP-TRMGM] No SilentTrader found in List wid npcTypeID: " + npc.getTypeID());
        }
    }

    @EventMethod(Threading.Sync)
    public void onTraderAttack(PlayerHitNpcEvent  event) {
        Npc npc  =  event.getNpc();
        Player player = event.getPlayer();
        SilentRpPlayer sPlayer = (SilentRpPlayer) player.getAttribute("silent_player");
        int damage = event.getDamage();

        if(damage > 0 && !npc.isInvincible()){
            if(npc.hasAttribute("robbery_players"))
            {
                List<SilentRpPlayer> roberies  = (List<SilentRpPlayer>) npc.getAttribute("robbery_players");
                if(!roberies.contains(sPlayer)){
                    roberies.add(sPlayer);
                }
            }
            if(!npc.isAlerted()){
                npc.setAlerted(true);
                if(npc.isLocked()) npc.setLocked(false);

                npc.setAttackReaction(Npcs.AttackReaction.Escape);
            }
        }

    }

    @EventMethod(Threading.Sync)
    public void onTraderDamage(NpcDeathEvent event) {
        Npc npc = event.getNpc();

        if(silentConfigData.isEnable_robberysystem()){
            //npc.setLocked(true);
            //npc.kill();
        }
    }


}
