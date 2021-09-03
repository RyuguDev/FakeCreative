/*
 * FakeCreative
 * Copyright (C) CraftationGaming <https://www.craftationgaming.com/>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.RockinChaos.fakecreative.handlers;

import me.RockinChaos.fakecreative.FakeCreative;
import me.RockinChaos.fakecreative.utils.ServerUtils;
import me.RockinChaos.fakecreative.utils.StringUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collection;

public class UpdateHandler {
    private final String HOST = "https://api.github.com/repos/RockinChaos/" + FakeCreative.getInstance().getName().toLowerCase() + "/releases/latest";
    private String versionExact = FakeCreative.getInstance().getDescription().getVersion();
    private String localeVersion = this.versionExact.split("-")[0];
    private String latestVersion;
    private boolean betaVersion = this.versionExact.contains("-SNAPSHOT") || this.versionExact.contains("-BETA") || this.versionExact.contains("-ALPHA");
    private boolean devVersion = this.localeVersion.equals("${project.version}");
    
    private File jarRef;
    
    private boolean updatesAllowed = ConfigHandler.getConfig().getFile("config.yml").getBoolean("General.CheckforUpdates");
    
    private static UpdateHandler updater;
        
   /**
    * Initializes the UpdateHandler and Checks for Updates upon initialization.
    *
    */
    public UpdateHandler() {
       this.jarRef = FakeCreative.getInstance().getPlugin();
       this.checkUpdates(FakeCreative.getInstance().getServer().getConsoleSender(), true);
    }
    
   /**
    * If the GitHub host has an available update, attenots to download the jar file.
    * Downloads and write the new data to the plugin jar file.
    * 
    * @param sender - The executor of the update checking.
    */
    public void forceUpdates(final CommandSender sender) {
    	if (this.updateNeeded(sender, false)) {
    		ServerUtils.messageSender(sender, "&aAn update has been found!");
    		ServerUtils.messageSender(sender, "&aUpgrading has been temporarily disabled due to Cloudflare, please manually download from spigot.");
    	} else {
    		if (this.betaVersion) {
    			ServerUtils.messageSender(sender, "&aYou are running a SNAPSHOT!");
    			ServerUtils.messageSender(sender, "&aIf you find any bugs please report them!");
    		}
    		ServerUtils.messageSender(sender, "&aYou are up to date!");
    	}
    }
    
   /**
    * Checks to see if an update is required, notifying the console window and online op players.
    * 
    * @param sender - The executor of the update checking.
    * @param onStart - If it is checking for updates on start.
    */
    public void checkUpdates(final CommandSender sender, final boolean onStart) {
    	if (this.updateNeeded(sender, onStart) && this.updatesAllowed) {
    		if (this.betaVersion) {
    			ServerUtils.messageSender(sender, "&cYour current version: &bv" + this.localeVersion + "-SNAPSHOT");
    			ServerUtils.messageSender(sender, "&cThis &bSNAPSHOT &cis outdated and a release version is now available.");
    		} else {
    			ServerUtils.messageSender(sender, "&cYour current version: &bv" + this.localeVersion + "-RELEASE");
    		}
    		ServerUtils.messageSender(sender, "&cA new version is available: " + "&av" + this.latestVersion + "-RELEASE");
    		ServerUtils.messageSender(sender, "&aGet it from: https://github.com/RockinChaos/" + FakeCreative.getInstance().getName().toLowerCase() + "/releases/latest");
    		ServerUtils.messageSender(sender, "&aIf you wish to auto update, please type /" + FakeCreative.getInstance().getName() + " Upgrade");
    		this.sendNotifications();
    	} else if (this.updatesAllowed) {
    		if (this.betaVersion) {
    			ServerUtils.messageSender(sender, "&aYou are running a SNAPSHOT!");
    			ServerUtils.messageSender(sender, "&aIf you find any bugs please report them!");
    		} else if (this.devVersion) {
    			ServerUtils.messageSender(sender, "&aYou are running a DEVELOPER SNAPSHOT!");
    			ServerUtils.messageSender(sender, "&aIf you find any bugs please report them!");
    			ServerUtils.messageSender(sender, "&aYou will not receive any updates requiring you to manually update.");
    		}
    		ServerUtils.messageSender(sender, "&aYou are up to date!");
    	}
    }
    
   /**
    * Directly checks to see if the GitHub host has an update available.
    * 
    * @param sender - The executor of the update checking.
    * @param onStart - If it is checking for updates on start.
    * @return If an update is needed.
    */
    private boolean updateNeeded(final CommandSender sender, final boolean onStart) {
    	if (this.updatesAllowed) {
    		if (!onStart) { ServerUtils.messageSender(sender, "&aChecking for updates..."); }
    		try {
    			URLConnection connection = new URL(this.HOST + "?_=" + System.currentTimeMillis()).openConnection();
    			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
    			String JsonString = StringUtils.toString(reader); 
			    JSONObject objectReader = (JSONObject) JSONValue.parseWithException(JsonString);
			    String gitVersion = objectReader.get("tag_name").toString();
    			reader.close();
    			if (gitVersion.length() <= 7) {
    				this.latestVersion = gitVersion.replaceAll("[a-z]", "").replace("-SNAPSHOT", "").replace("-BETA", "").replace("-ALPHA", "").replace("-RELEASE", "");
    				String[] latestSplit = this.latestVersion.split("\\.");
    				String[] localeSplit = this.localeVersion.split("\\.");
    				if (this.devVersion) {
    					return false;
    				} else if ((Integer.parseInt(latestSplit[0]) > Integer.parseInt(localeSplit[0]) || Integer.parseInt(latestSplit[1]) > Integer.parseInt(localeSplit[1]) || Integer.parseInt(latestSplit[2]) > Integer.parseInt(localeSplit[2]))
    						|| (this.betaVersion && (Integer.parseInt(latestSplit[0]) == Integer.parseInt(localeSplit[0]) && Integer.parseInt(latestSplit[1]) == Integer.parseInt(localeSplit[1]) && Integer.parseInt(latestSplit[2]) == Integer.parseInt(localeSplit[2])))) {
    					return true;
    				}
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    			ServerUtils.messageSender(sender, "&cFailed to check for updates, connection could not be made.");
    			return false;
    		}
    	} else if (!onStart) {
    		ServerUtils.messageSender(sender, "&cUpdate checking is currently disabled in the config.yml");
    		ServerUtils.messageSender(sender, "&cIf you wish to use the auto update feature, you will need to enable it.");
        }
    	return false;
    }
    
   /**
    * Sends out notifications to all online op players that 
    * an update is available at the time of checking for updates.
    * 
    */
    private void sendNotifications() {
    	try {
    		Collection < ? > playersOnline = null;
    		Player[] playersOnlineOld = null;
    		if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
    			if (Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).getReturnType() == Collection.class) {
    				playersOnline = ((Collection < ? > ) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
    				for (Object objPlayer: playersOnline) {
    					if (((Player) objPlayer).isOp()) {
    						ServerUtils.messageSender(((Player) objPlayer), "&eAn update has been found!");
    						ServerUtils.messageSender(((Player) objPlayer), "&ePlease update to the latest version: v" + this.latestVersion);
    					}
    				}
    			}
    		} else {
    			playersOnlineOld = ((Player[]) Bukkit.class.getMethod("getOnlinePlayers", new Class < ? > [0]).invoke(null, new Object[0]));
    			for (Player objPlayer: playersOnlineOld) {
    				if (objPlayer.isOp()) {
						ServerUtils.messageSender(objPlayer, "&eAn update has been found!");
						ServerUtils.messageSender(objPlayer, "&ePlease update to the latest version: v" + this.latestVersion);
    				}
    			}
    		}
    	} catch (Exception e) { ServerUtils.sendDebugTrace(e); }
    }
    
    
   /**
    * Gets the exact string version from the plugin yml file.
    * 
    * @return The exact server version.
    */
    public String getVersion() {
    	return this.versionExact;
    }
    
   /**
    * Gets the plugin jar file directly.
    * 
    * @return The plugins jar file.
    */
    public File getJarReference() {
    	return this.jarRef;
    }
    
   /**
    * Gets the instance of the UpdateHandler.
    * 
    * @param regen - If the instance should be regenerated.
    * @return The UpdateHandler instance.
    */
    public static UpdateHandler getUpdater(boolean regen) { 
        if (updater == null || regen) { updater = new UpdateHandler(); }
        return updater; 
    } 
}