package net.dorblacky.silentrp.managers;

import net.dorblacky.silentrp.SilentRP;
import net.dorblacky.silentrp.models.SilentRPConfig;
import net.dorblacky.silentrp.models.SilentTrader;
import net.risingworld.api.Plugin;
import net.risingworld.api.assets.TextureAsset;
import net.risingworld.api.events.EventMethod;
import net.risingworld.api.events.Listener;
import net.risingworld.api.events.player.PlayerKeyEvent;
import net.risingworld.api.objects.Npc;
import net.risingworld.api.objects.Player;
import net.risingworld.api.ui.UIElement;
import net.risingworld.api.ui.style.Pivot;
import net.risingworld.api.ui.style.ScaleMode;
import net.risingworld.api.utils.Key;

import java.util.Optional;

public class AdminManager extends SilentRP implements Listener {
    private final Plugin plugin;
    private final SilentRPConfig silentConfig;
    public AdminManager(Plugin plugin, SilentRPConfig config) {
        this.plugin = plugin;
        this.silentConfig = config;
        registerEventListener(this);

    }

    @EventMethod
    public void onPlayerKeyEvent(PlayerKeyEvent e) {
        Player p = e.getPlayer();
        if(!p.isAdmin()) return;
        if(p.isAdmin() && e.getKey() == Key.F11 && e.isPressed()){
            UIElement adminMenu;
            if(p.hasAttribute("admin_ui") &&p.hasAttribute("admin_ui_status")){
                UIElement oldElem = (UIElement) p.getAttribute("admin_ui");
                oldElem.setVisible(false);
                p.removeUIElement(oldElem);
                p.getNpcInLineOfSight(10, (npc) -> {
                   Optional<SilentTrader> trader =  TradingManager.traders.stream().filter(f->f.getNpc_typeid() == npc.getGlobalID()).findFirst();
                   if(trader.isPresent()){
                       if(npc.getGlobalID() == trader.get().getNpc_typeid()){
                           showTraderMenu(p,npc);
                       }
                   }
                });
            }
            else{
                p.getNpcInLineOfSight(10, (npc) -> {
                    Optional<SilentTrader> trader =  TradingManager.traders.stream().filter(f->f.getNpc_typeid() == npc.getGlobalID()).findFirst();
                    if(trader.isPresent()){
                        if(npc.getGlobalID() == trader.get().getNpc_typeid()){
                            showTraderMenu(p, npc);
                        }
                    }
                });
            }
        }
    }

    private void showTraderMenu(Player p, Npc npc){

        UIElement tMenu = new UIElement();
        TextureAsset bg = TextureAsset.loadFromFile(plugin.getPath() + "/resources/images/tradermenu_bg.png");
        tMenu.setSize(634,949,false);
        tMenu.setPosition(60, 50, true);
        tMenu.style.backgroundImage.set(bg);
        tMenu.setPivot(Pivot.MiddleCenter);
        tMenu.setVisible(true);
        p.addUIElement(tMenu);
        p.setAttribute("admin_ui", tMenu);
        p.setAttribute("admin_ui_status", true);
    }
}
