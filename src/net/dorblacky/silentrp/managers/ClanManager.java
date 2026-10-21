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

        clanList = DatabaseManager.getClanList();
        System.out.println("[SILENTRP-CLMGR] Clanmanager has been initialized with "+clanList.size()+" clans.");
    }

    public SilentRPClan getClanById(int id) {
        for (SilentRPClan clan : ClanManager.clanList) {
            if(clan.getId() == id)
                return clan;
        }

        return null;
    }

    public static SilentRPClan createClan(String clanName, String clanDescription, SilentRpPlayer clanOwner){
        SilentRPClan clan = null;
        silentDatabase.executeUpdate("INSERT INTO `srp_clans` (`clan_name`, `clan_owner`) VALUES ('"+clanName+"', "+clanOwner.getId()+"); SELECT last_insert_rowid();");
        try(ResultSet result =silentDatabase.executeQuery("SELECT id FROM `srp_clans` WHERE `clan_name` = '"+clanName+"';")) {
            if(result.next()) {
                clan = new SilentRPClan();
                clan.setId(result.getInt("id"));
                clan.setClan_owner(clanOwner);
                clan.setName(clanName);

                if(addClanMember(clan.getId(), clanOwner.getPlayer_guid(), clanMemberType.MASTER)){
                    clan.setMembers(DatabaseManager.getClanMembers(clan.getId()));
                }
                clanList.add(clan);
            }
        }catch (SQLException e) {
            System.out.println("[SILENTRP-CLMGR] Cannot create clan. :" + e.getMessage());
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
        for (SilentRPClan clan : ClanManager.clanList) {
            if(clan.getId() == clanId){
                silentDatabase.executeUpdate("INSERT INTO `srp_clanmembers` (`clan_id`, `player_uid`, `rank`) VALUES ("+clanId+", '"+playerUid+"', "+getClanMemberTypeNumber(type)+");");
                SilentRPClanMember member = new SilentRPClanMember();

                return true;
            }
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
