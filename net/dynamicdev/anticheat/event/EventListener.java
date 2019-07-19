package net.dynamicdev.anticheat.event;

import net.dynamicdev.anticheat.check.*;
import net.dynamicdev.anticheat.*;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import net.dynamicdev.anticheat.util.*;
import net.dynamicdev.anticheat.manage.*;

import java.util.*;

public class EventListener implements Listener {
	
    private static final Map<CheckType, Integer> USAGE_LIST;
    private static final Map<String, Integer> DECREASE_LIST;
    private static final CheckManager CHECK_MANAGER;
    private static final Backend BACKEND;
    private static final AntiCheat PLUGIN;
    private static final UserManager USER_MANAGER;
    
    public static void log(final String message, final Player player, final CheckType type) {
        final User user = getUserManager().getUser(player.getName());
        if (user != null) {
            logCheat(type, user);
            if (user.increaseLevel(type) && message != null) {
                AntiCheat.getManager().log(message);
                for (final Player thePlayer : Bukkit.getServer().getOnlinePlayers()) {
                    if (Permission.SYSTEM_ALERTALL.get((CommandSender)thePlayer) && !silentMode() && AntiCheat.developerMode()) {
                        thePlayer.sendMessage(ChatColor.RED + "[AntiCheat+] " + message);
                    }
                }
            }
            removeDecrease(user);
        }
    }
    
    private static void logCheat(final CheckType type, final User user) {
        EventListener.USAGE_LIST.put(type, getCheats(type) + 1);
        if (user != null && user.getName() != null) {
            type.logUse(user);
            //if (EventListener.CONFIG.getConfig().enterprise.getValue() && EventListener.CONFIG.getEnterprise().loggingEnabled.getValue()) {
            //    EventListener.CONFIG.getEnterprise().database.logEvent(user, type);
            //}
        }
    }
    
    public void resetCheck(final CheckType type) {
        EventListener.USAGE_LIST.put(type, 0);
    }
    
    public static int getCheats(final CheckType type) {
        int x = 0;
        if (EventListener.USAGE_LIST.get(type) != null) {
            x = EventListener.USAGE_LIST.get(type);
        }
        return x;
    }
    
    private static void removeDecrease(final User user) {
        int x = 0;
        if (user.getName() != null) {
            if (EventListener.DECREASE_LIST.get(user.getName()) != null) {
                x = EventListener.DECREASE_LIST.get(user.getName());
                x -= 2;
                if (x < 0) {
                    x = 0;
                }
            }
            EventListener.DECREASE_LIST.put(user.getName(), x);
        }
    }
    
    public static void decrease(final Player player) {
        final User user = getUserManager().getUser(player.getName());
        if (user.getName() != null) {
            int x = 0;
            if (EventListener.DECREASE_LIST.get(user.getName()) != null) {
                x = EventListener.DECREASE_LIST.get(user.getName());
            }
            x++;
            EventListener.DECREASE_LIST.put(user.getName(), x);
            if (x >= 10) {
                user.decreaseLevel();
                EventListener.DECREASE_LIST.put(user.getName(), 0);
            }
        }
    }
    
    public static CheckManager getCheckManager() {
        return EventListener.CHECK_MANAGER;
    }
    
    public static AntiCheatManager getManager() {
        return AntiCheat.getManager();
    }
    
    public static Backend getBackend() {
        return EventListener.BACKEND;
    }
    
    public static UserManager getUserManager() {
        return EventListener.USER_MANAGER;
    }
    
    public static AntiCheat getPlugin() {
        return EventListener.PLUGIN;
    }
    
    public static boolean silentMode() {
        return false;
    }
    
    static {
        USAGE_LIST = new EnumMap<CheckType, Integer>(CheckType.class);
        DECREASE_LIST = new HashMap<String, Integer>();
        CHECK_MANAGER = AntiCheat.getManager().getCheckManager();
        BACKEND = AntiCheat.getManager().getBackend();
        PLUGIN = AntiCheat.getManager().getPlugin();
        USER_MANAGER = AntiCheat.getManager().getUserManager();
    }
}
