/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.modwiz.displayPrefixes;

import com.modwiz.modperms.ModPermsPlugin;
import com.modwiz.modperms.players.PermPlayer;
import com.modwiz.modperms.players.PlayerManager;
import com.modwiz.modworlds.ModWorlds;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Starbuck
 */
public class DisplayPrefixesPlugin extends JavaPlugin implements Listener{
    private ModPermsPlugin plugin = null;
    private String format = "";
    private ModWorlds mw = null;
    
    @Override
    public void onEnable() {
        plugin = (ModPermsPlugin) getServer().getPluginManager().getPlugin("ModPerms");
        mw = (ModWorlds) getServer().getPluginManager().getPlugin("ModWorlds");
        if (plugin == null) {
            getLogger().severe("Error!!! ModPerms not found. Not hooking in for chat prefixes");
        } else {
            
            getServer().getPluginManager().registerEvents(this, this);
            load();
        }
    }
    
    @Override
    public void onDisable() {
    }
    
    @EventHandler()
    public void onChat(AsyncPlayerChatEvent event) {
        PlayerManager playerMan;
        playerMan = plugin.getPlayerManager();
        if (event.isCancelled()) {
            getLogger().fine("Event cancelled , not handling.");
        } else {
            Player player = event.getPlayer();
            PermPlayer pp = playerMan.getPlayer(player);
            String prefix = pp.getPrefix();
            String suffix = pp.getSuffix();
            if (prefix == null) {
                prefix = pp.getGroup().getPrefix();
                if (prefix == null) {
                    prefix = "";
                }
            }
            prefix = ChatColor.translateAlternateColorCodes('&', prefix);
            if (suffix == null) {
                suffix = pp.getGroup().getSuffix();
                if (suffix == null) {
                    suffix = "";
                }
            }
            
            suffix = ChatColor.translateAlternateColorCodes('&', suffix);
            
            String messageFormat;
            messageFormat = ChatColor.translateAlternateColorCodes('&', format);
            messageFormat = messageFormat.replaceAll("%prefix", prefix);
            messageFormat = messageFormat.replaceAll("%suffix", suffix);
            messageFormat = messageFormat.replaceAll("%dn", player.getDisplayName());
            messageFormat = messageFormat.replaceAll("%name", player.getName());
            if (mw != null) {
                String colorizedWorld = mw.getConfig().getString("worlds." + player.getWorld().getName() + ".displayName", player.getWorld().getName());
                messageFormat = messageFormat.replaceAll("%world", colorizedWorld);
            } else {
                messageFormat = messageFormat.replaceAll("%world", player.getWorld().getName());
            }
            
            
            
            if (pp.hasPermission("displayPrefix.color")) {
                messageFormat = messageFormat.replaceAll("%m", ChatColor.translateAlternateColorCodes('&', event.getMessage()));
            } else {
                messageFormat = messageFormat.replaceAll("%m", event.getMessage());
            }
            
           // messageFormat = messageFormat.replaceAll("[^\\[]\\[SUFFIX\\]",suffix);
           // messageFormat = messageFormat.replaceAll("[^\\[]\\[DISPLAYNAME\\]",player.getDisplayName());
           // messageFormat = messageFormat.replaceAll("[^\\[]\\[MESSAGE\\]", messageFormat);
            event.setFormat(messageFormat);
        }
    }
    
    public void load() {
        reloadConfig();
        if (getConfig() == null) {
            save();
        }
        
        FileConfiguration config = getConfig();
        format = config.getString("format", "%prefix%dn%suffix: %m");
        save();
    }
    
    public void save() {
        getConfig().set("format", format);
        saveConfig();
    }
}
