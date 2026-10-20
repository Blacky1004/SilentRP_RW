package net.dorblacky.silentrp.classes;

import net.dorblacky.silentrp.SilentRP;
import net.risingworld.api.Plugin;

public class GlobalBanSystem extends SilentRP {
    private final int bannAtPoints;
    private final Plugin plugin;
    public GlobalBanSystem(Plugin plugin, int bannAtPoints){
        this.bannAtPoints = bannAtPoints;
        this.plugin = plugin;
    }
}
