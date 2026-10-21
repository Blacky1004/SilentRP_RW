package net.dorblacky.silentrp.managers;

import net.dorblacky.silentrp.SilentRP;
import net.dorblacky.silentrp.classes.GlobalBanEntry;
import net.dorblacky.silentrp.classes.SilentRpPlayer;
import net.dorblacky.silentrp.models.SilentRPClan;
import net.dorblacky.silentrp.models.SilentRPClanMember;
import net.dorblacky.silentrp.models.SilentRPConfig;
import net.risingworld.api.Plugin;
import net.risingworld.api.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DatabaseManager extends SilentRP {
    private final Plugin plugin;
    private final SilentRPConfig silentConfig;

    public DatabaseManager(Plugin plugin, SilentRPConfig configContent) {
        this.plugin = plugin;
        this.silentConfig = configContent;
    }

    public void checkDatabaseStructure() {
        System.out.println("[SILENTRP-DBM] initialize SilentRP database management");
        String worldName = World.getName();
        String dbPath = this.plugin.getPath() + "/" + worldName + "/silentrp.db";

        silentDatabase = this.getSQLiteConnection(dbPath);
        //Pluginversion Tabelle
        silentDatabase.execute("CREATE TABLE IF NOT EXISTS `srp_version` (`version` VARCHAR(50) NOT NULL DEFAULT '0.0.1', `version_date` DATETIME DEFAULT CURRENT_TIMESTAMP);");

        //Playertabelle
        silentDatabase.execute("CREATE TABLE IF NOT EXISTS `srp_players` (`player_uid` VARCHAR(36) PRIMARY KEY NOT NULL ,`player_money` INTEGER NOT NULL DEFAULT 0, " +
                "`player_level` INTEGER NOT NULL DEFAULT 1, `player_xp` INTEGER NOT NULL default 0, `player_clan` INTEGER NULL);");
        //Clan Tabelle
        silentDatabase.execute("CREATE TABLE IF NOT EXISTS `srp_clans` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `clan_name` VARCHAR(50) NOT NULL, `clan_owner` VARCHAR(36) NOT NULL,`clan_tag` VARCHAR(20) NULL," +
                "`clan_description` TEXT NULL, `public_invite` BOOLEAN NOT NULL default false, `clan_rank` INTEGER NOT NULL DEFAULT 1, `clan_xp` INTEGER NOT NULL DEFAULT 0, " +
                "`clan_money` INTEGER NOT NULL DEFAULT 0, `clan_fee` INTEGER NOT NULL DEFAULT 0)");

        //Clanmitglieder Tabelle
        silentDatabase.execute("CREATE TABLE IF NOT EXISTS `srp_clanmembers` (`clan_id` INTEGER NOT NULL, `player_uid` VARCHAR(36), `rank` INTEGER NOT NULL DEFAULT 0, `invite_date` DATETIME NOT null DEFAULT CURRENT_TIMESTAMP)");

        //Global Ban Tabelle
        silentDatabase.execute("CREATE TABLE IF NOT EXISTS `srp_globalbans` (`player_uid` VARCHAR(36) PRIMARY KEY NOT NULL, `reason` TEXT NULL, `last_banaction` DATETIME NOT NULL, `ban_point` INTEGER NOT NULL DEFAULT 0);");

        //Plugin Spawnpoints Tabelle
        silentDatabase.execute("CREATE TABLE IF NOT EXISTS `srp_spawns`(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `x` FLOAT NOT NULL, `y` FLOAT NOT NULL, `z` FLOAT NOT NULL , `name` varchar(255) NOT NULL, `description` TEXT, `thumbnail` varchar(255) null);");

        try(ResultSet result = silentDatabase.executeQuery("SELECT `version` FROM srp_version")) {
            if(!result.next()) {
                System.out.println("[SILENTRP-DBM] generate database system");
                silentDatabase.executeUpdate("INSERT INTO `srp_version` (`version`) VALUES ('" + SilentRP.silentVersion + "');");
                if(silentConfig.isEnable_global_ban()) {
                    System.out.println("[SILENTRP-DBM] global ban is aktivated. Load banlist from hive..");

                } else {
                    System.out.println("[SILENTRP-DBM] global ban is not activated. Ignore banlist.");
                }
            } else {
                var thisversion = result.getString("version");
            }
            System.out.println("[SilentRP-DBM] SilentRP database management initialized.");
        } catch(SQLException ex) {
            System.out.println("[SILENTRP-DBM] ERROR: " + ex.getMessage());
        }
    }

    public void addGlobalBann(String player_uid, String reason, Date last_action, int ban_point) {
        silentDatabase.executeUpdate("INSERT INTO `srp_globalbans` (`player_uid`, `reason`, `last_banaction`, `ban_point`) VALUES ('" + player_uid +"', '" + reason +"', "
                + "'" + last_action + "', " + ban_point +")");
    }

    public GlobalBanEntry checkPlayerBannedGlobal(String player_uid) {
       try(ResultSet result = silentDatabase.executeQuery("SELECT * FROM `srp_globabans` WHERE `player_uid`='"+player_uid+"';")){
           if(!result.next()){
               return null;
           } else {
               GlobalBanEntry entry = new GlobalBanEntry();
               entry.setBan_points(result.getInt("ban_point"));
               entry.setPlayer_uid(result.getString("player_uid"));
               entry.setLast_action(result.getDate("last_banaction"));
               entry.setReason(result.getString("reason"));
               return entry;
           }
       } catch(SQLException ex){
           System.out.println("[SILENTRP-DBM] ERROR in checkPlayerPlayerGlobalBanned: "+ex.getMessage());
       }
       return null;
    }

    public SilentRpPlayer getPlayerData(String player_uid) {
        SilentRpPlayer playerdata = new SilentRpPlayer();
        try(ResultSet result = silentDatabase.executeQuery("SELECT * FROM `srp_players` WHERE `player_uid` ='"+player_uid+"';")) {
            if(!result.next()){
                playerdata.setLevel(1);
                playerdata.setMoney(silentConfig.getPlayer_startmoney());
                playerdata.setNewPlayer(true);
                playerdata.setXp(0);
                playerdata.setId(player_uid);
                silentDatabase.executeUpdate("INSERT INTO `srp_players` (`player_uid`, `player_money`, `player_level`, `player_xp`) VALUES ('"+player_uid+"', "+playerdata.getMoney()+", "+playerdata.getLevel()+", "+playerdata.getXp()+")");
                System.out.println("[SILENTRP-DBM] New player '"+playerdata.getId()+"' created.");
            } else{
                playerdata.setId(result.getString("player_uid"));
                playerdata.setXp(result.getInt("player_xp"));
                playerdata.setNewPlayer(false);
                playerdata.setLevel(result.getInt("player_level"));
                playerdata.setMoney(result.getInt("player_money"));
                System.out.println("[SILENTRP-DBM] Player '"+playerdata.getId()+"' loaded.");
            }

        } catch (SQLException ex) {
            System.out.println("[SILENTRP-DBM] ERROR in getPlayerData: "+ex.getMessage());
        }
        return playerdata;
    }
    public static SilentRpPlayer getSilentPlayer(String playerUid){
        SilentRpPlayer playerdata = new SilentRpPlayer();
        try(ResultSet result = silentDatabase.executeQuery("SELECT * FROM `srp_players` WHERE `player_uid` ='"+playerUid+"';")) {
            if(!result.next()){
                playerdata.setLevel(1);
                playerdata.setMoney(SilentRP.silentConfigData.getPlayer_startmoney());
                playerdata.setNewPlayer(true);
                playerdata.setXp(0);
                playerdata.setId(playerUid);
                silentDatabase.executeUpdate("INSERT INTO `srp_players` (`player_uid`, `player_money`, `player_level`, `player_xp`) VALUES ('"+playerUid+"', "+playerdata.getMoney()+", "+playerdata.getLevel()+", "+playerdata.getXp()+")");
                System.out.println("[SILENTRP-DBM] New player '"+playerdata.getId()+"' created.");
            } else{
                playerdata.setId(result.getString("player_uid"));
                playerdata.setXp(result.getInt("player_xp"));
                playerdata.setNewPlayer(false);
                playerdata.setLevel(result.getInt("player_level"));
                playerdata.setMoney(result.getInt("player_money"));
                System.out.println("[SILENTRP-DBM] Player '"+playerdata.getId()+"' loaded.");
            }

        } catch (SQLException ex) {
            System.out.println("[SILENTRP-DBM] ERROR in getPlayerData: "+ex.getMessage());
        }
        return playerdata;
    }

    public static SilentRPClanMember[] getClanMembers(int clanId){
        SilentRPClanMember[]  members = null;
        try(ResultSet result = silentDatabase.executeQuery("SELECT * FROM `srp_clanmembers` WHERE id="+clanId+";")) {
            List<SilentRPClanMember> membersList = new ArrayList<>();
            while(result.next()) {
                SilentRPClanMember member = new SilentRPClanMember();
                member.setClan_id(result.getInt("clan_id"));
                member.setPlayer_uid(result.getString("player_uid"));
                member.setInvite_date(result.getDate("invite_date"));
                member.setRank(result.getInt("rank"));
                membersList.add(member);
            }
            if(!membersList.isEmpty()){
                members = (SilentRPClanMember[]) membersList.toArray();
            }
        } catch(SQLException e){
            System.out.println("[SILENTRP-DBM] ERROR in getClanMembers: "+e.getMessage());
        }
        return members;
    }

    public static List<SilentRPClan> getClanList(){
        List<SilentRPClan> clanlist = new ArrayList<>();

        try (ResultSet result = silentDatabase.executeQuery("SELECT * FROM `srp_clans`;")){
            if(!result.next()){ return clanlist;}
            while (result.next()) {
                SilentRPClan clan = new SilentRPClan();
                clan.setId(result.getInt("id"));
                clan.setClan_owner(getSilentPlayer(result.getString("clan_owner")));
                clan.setClan_description(result.getString("clan_description"));
                clan.setClan_money(result.getInt("clan_money"));
                clan.setClan_fee(result.getInt("clan_fee"));
                clan.setClan_rank(result.getInt("clan_rank"));
                clan.setClan_xp(result.getInt("clan_xp"));
                clan.setPublic_invite(result.getBoolean("public_invite"));
                clan.setMembers(getClanMembers(clan.getId()));
                clanlist.add(clan);
            }
        } catch (SQLException e) {
            System.out.println("[SILENTRP-DBM] ERROR in getClanList: "+e.getMessage());
        }
        return  clanlist;
    }
}
