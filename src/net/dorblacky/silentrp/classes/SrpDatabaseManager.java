package net.dorblacky.silentrp.classes;

import net.dorblacky.silentrp.SilentRP;
import net.dorblacky.silentrp.models.SilentRPConfigContent;
import net.risingworld.api.Plugin;
import net.risingworld.api.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;

public class SrpDatabaseManager extends SilentRP {
    private final Plugin plugin;
    private final SilentRPConfigContent silentConfig;

    public SrpDatabaseManager(Plugin plugin, SilentRPConfigContent configContent) {
        this.plugin = plugin;
        this.silentConfig = configContent;
    }

    public void checkDatabaseStructure(String aktualHiveVersion, String databasUpdate) {
        System.out.println("[SILENTRP-DBM] initialize SilentRP database management");
        String worldName = World.getName();
        String dbPath = this.plugin.getPath() + "/" + worldName + "/silentrp.db";

        silentDatabase = this.getSQLiteConnection(dbPath);
        silentDatabase.execute("CREATE TABLE IF NOT EXISTS `srp_players` (`player_uid` VARCHAR(36) PRIMARY KEY NOT NULL ,`player_money` INTEGER NOT NULL DEFAULT 0, `player_level` INTEGER NOT NULL DEFAULT 1, `player_xp` INTEGER NOT NULL default 0);");
        silentDatabase.execute("CREATE TABLE IF NOT EXISTS `srp_version` (`version` VARCHAR(50) NOT NULL DEFAULT '0.0.1', `version_date` DATETIME DEFAULT CURRENT_TIMESTAMP);");
        silentDatabase.execute("CREATE TABLE IF NOT EXISTS `srp_globalbans` (`player_uid` VARCHAR(36) PRIMARY KEY NOT NULL, `reason` TEXT NULL, `last_banaction` DATETIME NOT NULL, `ban_point` INTEGER NOT NULL DEFAULT 0);");
        silentDatabase.execute("CREATE TABLE IF NOT EXISTS `srp_spans`(`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `x` FLOAT NOT NULL, `y` FLOAT NOT NULL, `z` FLOAT NOT NULL , `name` varchar(255) NOT NULL, `description` TEXT, `thumbnail` varchar(255) null);");

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
                if(!Objects.equals(aktualHiveVersion, thisversion)){
                    if(!databasUpdate.isEmpty()){

                    }

                    silentDatabase.executeUpdate("UPDATE `srp_version` SET `version`='" + aktualHiveVersion +"';");
                    System.out.println("[SILENTRP-DBM] database aktualized from Version '"+thisversion+"' to '"+aktualHiveVersion+"'");
                } else{
                    System.out.println("[SILENTRP-DBM] Database version is actual.");
                }
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

    public SilentRpPlayerData getPlayerData(String player_uid) {
        SilentRpPlayerData playerdata = new SilentRpPlayerData();
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
}
