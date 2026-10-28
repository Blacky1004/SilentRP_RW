package net.dorblacky.silentrp.managers;

import net.dorblacky.silentrp.SilentRP;
import net.dorblacky.silentrp.models.*;
import net.risingworld.api.Plugin;
import net.risingworld.api.Server;
import net.risingworld.api.Timer;
import net.risingworld.api.World;
import net.risingworld.api.assets.TextureAsset;
import net.risingworld.api.definitions.Npcs;
import net.risingworld.api.events.EventMethod;
import net.risingworld.api.events.Listener;
import net.risingworld.api.events.Threading;
import net.risingworld.api.events.general.ReadyEvent;
import net.risingworld.api.events.npc.NpcDeathEvent;
import net.risingworld.api.events.player.PlayerHitNpcEvent;
import net.risingworld.api.events.player.PlayerNpcInteractionEvent;
import net.risingworld.api.events.player.ui.PlayerUIElementClickEvent;
import net.risingworld.api.objects.Clothes;
import net.risingworld.api.objects.Npc;
import net.risingworld.api.objects.Player;
import net.risingworld.api.objects.Skin;
import net.risingworld.api.ui.UIElement;
import net.risingworld.api.ui.UILabel;
import net.risingworld.api.ui.style.*;
import net.risingworld.api.utils.Key;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TradingManager extends SilentRP implements Listener {
    private final Plugin plugin;
    private final SilentRPConfig silentConfig;

    public static List<SilentTrader> traders;
    private Timer tradingTimer;
    private static SilentTradingTypeConfig tradingTypesDatas;

    private final static Map<UIElement, SilentTrader.traderType> radioButtons = new HashMap<>();
    private static  SilentTrader.traderType selectedType = SilentTrader.traderType.SHOP;
    // Speichert den aktuell ausgewählten Typen


    private final static int COLOR_SELECTED = 0x3399FFFF;   // Blau
    private final static int COLOR_DESELECTED = 0x333333FF; // Dunkelgrau

    public TradingManager(Plugin plugin, SilentRPConfig configContent) {
        this.plugin = plugin;
        this.silentConfig = configContent;
        //Holen aller NPC aus der Db und in die Welt platzieren
        traders = DatabaseManager.getTraderNPCs();
        if (traders != null) {
            for (SilentTrader trader : traders) {
                trader.setClothes(DatabaseManager.getTraderClothes(trader.getId()));
                trader.setSkin(DatabaseManager.getTraderSkin(trader.getId()));
            }
        }
        if (silentConfig.isEnable_robberysystem() && traders != null) {
            tradingTimer = new Timer(60, 0, -1, this::doTimerWork);

        }

        registerEventListener(this);
    }

    private void doTimerWork() {
        float aktualTime = Server.getRunningTime();
        for (SilentTrader trader : traders.stream().filter(d -> d.isDeath()).toList()) {
            float deathTime = aktualTime - trader.getDeathTime();
            if (deathTime > silentConfig.getTrading_respawn_timer() * 60) {
                var npc = World.getNpc(trader.getNpc_typeid());
                if (npc != null) {
                    npc.setPosition(trader.getTraderPosition().getPosition());
                    npc.setRotation(trader.getTraderPosition().getRotation());
                    npc.setHealth(100);
                    npc.setLocked(true);
                    npc.setAlerted(false);
                    npc.setInvisible(false);
                } else {
                    trader.setDeath(false);
                    trader.setDeathTime(0);
                    npc = World.spawnNpc((short) 200, 0, trader.getTraderPosition().getPosition(), trader.getTraderPosition().getRotation(), true);
                    trader.setNpc_typeid(npc.getGlobalID());
                    npc.setName(trader.getName());
                    var allClothes = DatabaseManager.getTraderClothes(trader.getId());
                    if (allClothes != null) {
                        for (SilentTraderClothe clothe : allClothes) {
                            npc.getClothes().add((short) clothe.getClothe_id(), clothe.getColor_id());
                        }
                    }

                    if (trader.getSkin() != null) {
                        npc.getSkin().setBeard((byte) trader.getSkin().getBeard());
                        npc.getSkin().setSkinColor(trader.getSkin().getSkinColor());
                        npc.getSkin().setHairstyle(trader.getSkin().getHairStyle());
                        npc.getSkin().setEyeColor(trader.getSkin().getEyeColor());
                        npc.getSkin().setHairColor(trader.getSkin().getHairColor());
                        npc.getSkin().setGender(trader.getSkin().getGender() == 0 ? Skin.Gender.Male : Skin.Gender.Female);
                    }
                    npc.setBehaviour(Npcs.Behaviour.Dummy);
                    World.getNpc(npc.getGlobalID()).setLocked(true);
                }
            }
        }
    }

    //<editor-fold description="Methoden">
    public static void createTrader(Player player, Npc npc, SilentTrader.traderType type) {
        if (npc != null) {
            SilentTrader trader = new SilentTrader();
            trader.setTraderType(type);
            trader.setTraded_money(0);
            trader.setTraderPosition(npc.getPosition(), npc.getRotation());
            trader.setNpc_typeid(getNextTraderId());
            trader.setName(npc.getName());

            trader = DatabaseManager.createTrader(trader);
            if (trader != null && trader.getId() > 0) {
                traders.add(trader);
            }
        }
    }

    public static short getNextTraderId() {
        return (short) Arrays.stream(World.getAllNpcs()).mapToInt(Npc::getTypeID).max().orElse(0);
    }

    //</editor-fold>

    //<editor-fold description="Events">
    @EventMethod
    public void serverReady(ReadyEvent event) {
        if (Server.getType() == Server.Type.Dedicated) {
            for (SilentTrader trader : traders) {
                var npc = World.spawnNpc((short) 200, 0, trader.getTraderPosition().getPosition(), trader.getTraderPosition().getRotation(), true);
                if (npc != null) {
                    trader.setNpc_typeid(npc.getGlobalID());
                    trader.setDeath(false);
                    npc.setName(trader.getName());
                    npc.setLocked(true);
                    npc.setAttribute("trader", trader.getId());

                    var clothes = DatabaseManager.getTraderClothes(trader.getId());
                    if (clothes != null && clothes.size() > 0) {
                        for (SilentTraderClothe clothe : clothes) {
                            npc.getClothes().add((short) clothe.getClothe_id(), clothe.getColor_id());
                        }
                    }
                    DatabaseManager.updateTrader(trader);
                    System.out.println("[SILENTRP-TRMGM] Trader NpcID: " + npc.getGlobalID() + "; und DBID: " + trader.getNpc_typeid() + "; gespawnt");
                }
            }

            if (tradingTimer != null && silentConfig.isEnable_robberysystem()) {
                System.out.println("[SILENTRP-TRMGM] Roberysystem ist aktiviert starte Tradingtimer...");
                tradingTimer.start();
            }
            if(silentConfig.isEnable_paychecksystem()){
                TaxOfficeManager.startPayTimer();
            }

        }
    }

    @EventMethod(Threading.Sync)
    public void onTraderInterAction(PlayerNpcInteractionEvent event) {
        Npcs.InteractionType interactionType = event.getInteractionType();
        Npc npc = event.getNpc();
        Player player = event.getPlayer();

        java.util.Optional<SilentTrader> foundTrader = traders.stream().filter(trader -> trader.getNpc_typeid() == npc.getGlobalID()).findFirst();
        if (foundTrader.isPresent()) {
            SilentTrader trader = foundTrader.get();
            if (npc.hasAttribute("trade_ui")) {
                if (trader.getTraded_money() <= 0) {
                    //Der NPC kann nichts mehr ankaufen
                } else {

                }
            }
        } else {
            System.out.println("[SILENTRP-TRMGM] No SilentTrader found in List wid npcTypeID: " + npc.getTypeID());
        }
    }

    @EventMethod(Threading.Sync)
    public void onTraderAttack(PlayerHitNpcEvent event) {
        Npc npc = event.getNpc();
        Player player = event.getPlayer();
        SilentRpPlayer sPlayer = (SilentRpPlayer) player.getAttribute("srp_player");
        int damage = event.getDamage();

        if (damage > 0 && !npc.isInvincible()) {
            if (npc.hasAttribute("robbery_players")) {
                List<SilentRpPlayer> roberies = (List<SilentRpPlayer>) npc.getAttribute("robbery_players");
                if (!roberies.contains(sPlayer)) {
                    roberies.add(sPlayer);
                }
            } else {
                List<SilentRpPlayer> roberies = new ArrayList<SilentRpPlayer>();
                roberies.add(sPlayer);
                npc.setAttribute("robbery_players", roberies);
            }
            if (!npc.isAlerted()) {
                npc.setAlerted(true);
                if (npc.isLocked()) npc.setLocked(false);

                npc.setAttackReaction(Npcs.AttackReaction.Escape);
            }
        }

    }

    @EventMethod(Threading.Sync)
    public void onTraderDeath(NpcDeathEvent event) {
        Npc npc = event.getNpc();

        if (silentConfigData.isEnable_robberysystem()) {
            List<SilentRpPlayer> roberies = (ArrayList<SilentRpPlayer>) npc.getAttribute("robbery_players");
            int dropMOney = 0;
            if (npc.hasAttribute("trader", Integer.class)) {
                int trader_id = (int) npc.getAttribute("trader");
                SilentTrader trader = traders.stream().filter(t -> t.getId() == trader_id).findFirst().orElse(null);
                if (trader != null) {
                    dropMOney = trader.getTraded_money();
                    if (dropMOney > 0) {
                        Set<String> rpPlayers = roberies.stream().map(SilentRpPlayer::getId).collect(Collectors.toSet());
                        Player[] players = Arrays.stream(Server.getAllPlayers())
                                .filter(player -> rpPlayers.contains(player.getUID())).toArray(Player[]::new);

                        int money_perPlayer = (int) Math.round((float) dropMOney / (double) rpPlayers.size());
                        if (money_perPlayer > 0) {
                            for (Player p : players) {
                                if (p.hasAttribute("srp_player")) {
                                    SilentRpPlayer rpPlayer = (SilentRpPlayer) p.getAttribute("srp_player");
                                    rpPlayer.setMoney(rpPlayer.getMoney() + money_perPlayer);
                                    //Datenbank updaten
                                    var updatedPlayer = DatabaseManager.updatePlayer(rpPlayer);
                                    if (updatedPlayer != null) {
                                        p.setAttribute("srp_player", updatedPlayer);
                                        p.sendTextMessage("Du hast " + money_perPlayer + " € von '" + npc.getName() + "' erbeutet.");
                                    }
                                }
                            }
                            trader.setTraded_money(0);
                            DatabaseManager.updateTrader(trader);
                        }
                    }
                    npc.setInvisible(true);
                    trader.setDeath(true);
                    trader.setDeathTime(Server.getRunningTime());
                }
            }
        }
    }

    //</editor-fold>
    public static void editTrader(Player player, SilentTrader trader, Npc npc, Plugin plugin) {
        if (!player.isAdmin()) return;

        player.disableClientsideKeys(Key.Escape);
        player.setMouseCursorVisible(true);
        player.setListenForMouseInput(true);
        UIElement ui = new UIElement();
        ui.setPosition(50, 50, true);
        ui.setSize(634, 949, false);
        ui.setPivot(Pivot.MiddleCenter);
        TextureAsset bg = TextureAsset.loadFromFile(plugin.getPath() + "/resources/images/tradermenu_bg.png");
        ui.style.backgroundImage.set(bg);
        selectedType = trader.getTraderType(trader.getTrader_type());
        UILabel title = new UILabel("Funktion von " + trader.getName());
        title.setPosition(0,0, false);
        title.setSize(634, 63, false);
        title.setFontSize(28);
        title.setTextAlign(TextAnchor.MiddleCenter);
        title.setFontColor(0x000000FF);
        ui.addChild(title);

        UILabel description = new UILabel(SilentTradingTypeConfig.getDescription(selectedType, plugin.getPath()));
        description.setSize(600, 350, false);
        description.setFontSize(24, Unit.Pixel);
        description.setRichTextEnabled(true);
        description.style.alignContent.set(Align.Auto);
        description.style.flexWrap.set(Wrap.Wrap);
        description.style.whiteSpace.set(WhiteSpace.Normal);
        description.setBorder(1);
        description.setBorderColor(0xffffffff);
        description.setPosition(3, 40, true);
        ui.addChild(description);

        int boxSize = 24;      // Größe des Radio-Quadrats (Breite & Höhe)
        int labelOffset = 35;  // Abstand vom Quadrat zum Text-Anfang
        int rowSpacing = 35;   // Vertikaler Abstand von Reihe zu Reihe
        int startX = 200;
        int startY = 70;
        int currentY = startY;
        for (SilentTrader.traderType type : SilentTrader.traderType.values()) {

            // 1. Radio-Button (Klickfläche) erstellen
            UIElement radioBox = new UIElement() {
                @Override
                public void onClick(PlayerUIElementClickEvent event) {
                    var clickedElement = event.getUIElement();

                    if(radioButtons.containsKey(clickedElement)){
                        selectedType = radioButtons.get(clickedElement);
                        description.setText(SilentTradingTypeConfig.getDescription(selectedType, plugin.getPath()));
                        for(Map.Entry<UIElement, SilentTrader.traderType> entry : radioButtons.entrySet()){
                            UIElement panel = entry.getKey();
                            SilentTrader.traderType type = entry.getValue();

                            if(type == selectedType){
                                panel.setBackgroundColor(COLOR_SELECTED);
                            } else{
                                panel.setBackgroundColor(COLOR_DESELECTED);
                            }
                        }

                        trader.setTraderType(selectedType);
                    }
                }
            };
            radioBox.setSize(boxSize, boxSize, false);
            radioBox.setPosition(startX, currentY, false);
            radioBox.setClickable(true);

            // Standardmäßig das als 'selectedType' definierte Element einfärben
            if (type == selectedType) {
                radioBox.setBackgroundColor(COLOR_SELECTED);
            } else {
                radioBox.setBackgroundColor(COLOR_DESELECTED);
            }

            // 2. Text-Beschriftung erstellen
            UILabel label = new UILabel(type.getTradername());
            label.setPosition(startX + labelOffset, currentY, false);
            label.setFontSize(24, Unit.Pixel);
            label.setTextAlign(TextAnchor.MiddleLeft);

            // In der Map speichern, um beim Klick zu wissen, welche Box zu welchem Typ gehört
            radioButtons.put(radioBox, type);

            // Elemente dem Spieler anzeigen
            ui.addChild(radioBox);
            ui.addChild(label);

            // Y-Koordinate für die nächste Zeile nach unten verschieben
            currentY += rowSpacing;
        }
        TextureAsset btnBg = TextureAsset.loadFromFile(plugin.getPath() + "/resources/images/meuButtonBg.png");
        UILabel btnExit = new UILabel("Abbrechen") {
            @Override
            public void onClick(PlayerUIElementClickEvent event) {
                player.enableClientsideKeys(Key.Escape);
                player.setMouseCursorVisible(false);
                player.setListenForMouseInput(false);
                player.removeUIElement(this.getParent());
            }
        };
        btnExit.setPosition(2, 93, true);
        btnExit.setSize(220,50, false);
        btnExit.setTextAlign(TextAnchor.MiddleCenter);
        btnExit.setFontColor(0x000000FF);
        btnExit.setFontSize(20, Unit.Pixel);
        btnExit.style.backgroundImage.set(btnBg);
        btnExit.setClickable(true);
        ui.addChild(btnExit);

        UILabel btnSave = new UILabel("Speichern") {
            @Override
            public void onClick(PlayerUIElementClickEvent event){
                DatabaseManager.deleteAllTraderClothes(trader.getId());
                List<SilentTraderClothe> clothes = new ArrayList<SilentTraderClothe>();
                for(Clothes.Garment g: npc.getClothes().getAll()){
                    SilentTraderClothe clothe = new SilentTraderClothe();
                    clothe.setTrader_id(trader.getId());
                    clothe.setColor_id(g.getColor());
                    clothe.setClothe_id(g.getID());
                    clothe = DatabaseManager.addTraderClothe(clothe);
                    if(clothe!= null && clothe.getId() > 0) {
                        clothes.add(clothe);
                    }
                }
                trader.setClothes(clothes);

                DatabaseManager.deleteAllTraderSkins(trader.getId());
                SilentTraderSkin skin = new SilentTraderSkin();
                skin.setTraderId(trader.getId());
                skin.setBeard(npc.getSkin().getBeard());
                skin.setHairColor(npc.getSkin().getHairColor());
                skin.setSkinColor(npc.getSkin().getSkinColor());
                skin.setGender(npc.getSkin().getGender() == Skin.Gender.Male ? 0 :1);
                skin.setHair(npc.getSkin().getHairstyle());
                skin.setEyeColor(npc.getSkin().getEyeColor());
                skin = DatabaseManager.addTraderSkin(skin);
                trader.setSkin(skin);
                IntStream.range(0, traders.size())
                                .filter(i -> traders.get(i).getId() == trader.getId())
                                        .findFirst()
                                                .ifPresent(index -> traders.set(index, trader));

                DatabaseManager.updateTrader(trader);
                player.enableClientsideKeys(Key.Escape);
                player.setMouseCursorVisible(false);
                player.setListenForMouseInput(false);
                player.removeUIElement(this.getParent());

            }
        };
        btnSave.setClickable(true);
        btnSave.setPosition(63,93, true);
        btnSave.setSize(220,50, false);
        btnSave.setTextAlign(TextAnchor.MiddleCenter);
        btnSave.setFontColor(0x000000FF);
        btnSave.setFontSize(24, Unit.Pixel);
        btnSave.style.backgroundImage.set(btnBg);

        ui.addChild(btnSave);
        player.setAttribute("ui_editTrader", ui);
        player.addUIElement(ui);
    }


    //<editor-fold description="Tradermenüs">
}
