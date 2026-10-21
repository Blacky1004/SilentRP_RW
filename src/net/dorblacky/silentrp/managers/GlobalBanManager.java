package net.dorblacky.silentrp.managers;

import net.dorblacky.silentrp.SilentRP;
import net.risingworld.api.Plugin;

public class GlobalBanManager extends SilentRP {
    private final int bannAtPoints;
    private final Plugin plugin;
    public GlobalBanManager(Plugin plugin, int bannAtPoints){
        this.bannAtPoints = bannAtPoints;
        this.plugin = plugin;
    }
}
