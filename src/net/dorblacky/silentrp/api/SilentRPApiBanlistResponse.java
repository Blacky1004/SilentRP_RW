package net.dorblacky.silentrp.api;

import net.dorblacky.silentrp.classes.GlobalBanEntry;

public class SilentRPApiBanlistResponse {
    private GlobalBanEntry[] entrys;

    public GlobalBanEntry[] getEntrys() {
        return entrys;
    }

    public void setEntrys(GlobalBanEntry[] entrys) {
        this.entrys = entrys;
    }
}
