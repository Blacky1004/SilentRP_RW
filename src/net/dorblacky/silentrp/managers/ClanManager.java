package net.dorblacky.silentrp.managers;

import net.dorblacky.silentrp.SilentRP;
import net.dorblacky.silentrp.classes.SilentRpPlayer;
import net.dorblacky.silentrp.models.SilentRPClan;
import net.dorblacky.silentrp.models.SilentRPClanMember;
import net.dorblacky.silentrp.models.SilentRPConfig;
import net.risingworld.api.Plugin;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class ClanManager extends SilentRP {
    private final Plugin plugin;
    private SilentRPConfig config;
    public static List<SilentRPClan>  clanList;
    public ClanManager(Plugin plugin, SilentRPConfig config) {
        this.plugin = plugin;
        this.config = config;

        ClanManager.clanList = DatabaseManager.getClanList();
        System.out.println("[SILENTRP-CLMGR] Clanmanager has been initialized with "+ClanManager.clanList.size()+" clans.");
    }

    public SilentRPClan getClanById(int id) {
        for (SilentRPClan clan : ClanManager.clanList) {
            if(clan.getId() == id)
                return clan;
        }

        return null;
    }

    public static SilentRPClan createClan(String clanName, String clanDescription, SilentRpPlayer clanOwner){
        SilentRPClan clan = DatabaseManager.createClan(clanName, clanDescription, clanOwner, true);
        if(clan != null && clan.getId() > 0){
            SilentRPClanMember member = DatabaseManager.createClanMember(clan.getId(), clanOwner.getPlayer_guid(), getClanMemberTypeNumber(clanMemberType.MEMBER));
            if(member != null && member.getId() > 0){
                clan.setMembers(DatabaseManager.getClanMembers(clan.getId()));
            }
            
            clanList.add(clan);
        }
        return clan;
    }

    public SilentRPClan getPlayerClan(String playerUid){
        SilentRPClan clan = null;

        return clan;
    }
    public boolean changeClanOwner(int clanID, SilentRpPlayer clanOwner){
        for (SilentRPClan clan : ClanManager.clanList) {
            if(clan.getId() == clanID){
                clan.setClan_owner(clanOwner);
                return true;
            }
        }
        return false;
    }

    public static boolean addClanMember(int clanId, String playerUid, clanMemberType type){
        try{
            silentDatabase.executeUpdate("INSERT INTO `srp_clanmembers` (`clan_id`, `player_uid`, `rank`) VALUES ("+clanId+", '"+playerUid+"', "+getClanMemberTypeNumber(type)+");");
            return true;
        } catch(Exception e){
            System.out.println("[SILENTRP-CLMGR] ERROR in function addClanMember: "+e.getMessage());
        }

        return false;
    }

    public static boolean removeClanMember(int clanId, String playerUid, String reason){
        for (SilentRPClan clan : ClanManager.clanList) {
            if(clan.getId() == clanId){

                return true;
            }
        }
        return false;
    }

    public boolean changeClanMember(int clanId, String playerUid, clanMemberType type){
        for (SilentRPClan clan : ClanManager.clanList) {
            if(clan.getId() == clanId){

                return true;
            }
        }
        return false;
    }

    public enum clanMemberType {
        MASTER,
        VIZE,
        OFFICER,
        MEMBER,
        TRIAL
    }

    private static int getClanMemberTypeNumber(clanMemberType type){
        switch (type){
            default:
            case TRIAL: return 0;
            case OFFICER: return 2;
            case MEMBER: return 1;
            case VIZE: return 3;
            case MASTER: return 4;
        }
    }
}
