package net.dynamicdev.anticheat.manage;

import net.dynamicdev.anticheat.*;
import net.dynamicdev.anticheat.util.FileFormatter;
import net.dynamicdev.anticheat.util.Permission;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * eigener Log für Anticheat -> unnötig!
 * */
public class LoggingManager {
	
    private final Logger fileLogger;
    private static Handler fileHandler;
    private static List<String> logs;
    
    public LoggingManager(final AntiCheat plugin, final Logger logger) {
        this.fileLogger = Logger.getLogger("net.gravitydevelopment.anticheat.AntiCheat");
        try {
            final File file = new File(me.corperateraider.myworld.Plugin.instance.getDataFolder(), "log");
            if (!file.exists()) {
                file.mkdir();
            }
            (fileHandler = new FileHandler(me.corperateraider.myworld.Plugin.instance.getDataFolder() + "/log/anticheat.log", true)).setFormatter(new FileFormatter());
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        this.fileLogger.setUseParentHandlers(false);
        this.fileLogger.addHandler(fileHandler);
    }
    
    public void log(final String message) {
        if(true){
            this.logToConsole(message);
        }
        if(true){
            this.logToFile(message);
        }
        this.logToLogs(message);
    }
    
    public void debugLog(final String message) {
        Bukkit.getConsoleSender().sendMessage("[AntiCheat+] " + ChatColor.GRAY + message);
        this.logToLogs(message);
    }
    
    public void logToConsole(final String message) {
        Bukkit.getConsoleSender().sendMessage("[AntiCheat+] " + ChatColor.RED + message);
    }
    
    public void logToFile(final String message) {
        this.fileLogger.info(ChatColor.stripColor(message));
    }
    
    public void logFineInfo(final String message) {
        this.logToFile(message);
        this.logToLogs(message);
    }
    
    public void logToPlayers(final String message) {
        for (final Player player : Bukkit.getOnlinePlayers()) {
            if (Permission.SYSTEM_NOTICE.get((CommandSender)player)) {
                player.sendMessage(message);
            }
        }
    }
    
    private void logToLogs(final String message) {
        LoggingManager.logs.add(ChatColor.stripColor(message));
    }
    
    public List<String> getLastLogs() {
        final List<String> log = new CopyOnWriteArrayList<String>();
        if (LoggingManager.logs.size() < 30) {
            return LoggingManager.logs;
        }
        for (int i = LoggingManager.logs.size() - 1; i >= 0; --i) {
            log.add(LoggingManager.logs.get(i));
        }
        LoggingManager.logs.clear();
        return log;
    }
    
    public void closeHandler() {
        LoggingManager.fileHandler.close();
    }
    
    static {
        LoggingManager.logs = new CopyOnWriteArrayList<String>();
    }
}
