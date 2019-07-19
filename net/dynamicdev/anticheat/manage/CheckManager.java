package net.dynamicdev.anticheat.manage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.dynamicdev.anticheat.AntiCheat;
import net.dynamicdev.anticheat.check.CheckType;
import net.dynamicdev.anticheat.util.Permission;

public class CheckManager {
	
    private AntiCheatManager manager;
    private static List<CheckType> checkIgnoreList;
    private static Map<String, List<CheckType>> exemptList;
    
    public CheckManager(final AntiCheatManager manager) {
        this.manager = null;
        this.manager = manager;
        // keine ausgeschalteten Checks! sowieso nicht, da per Console und so ja nichts zugängig sein soll
        /*for (final String string : this.config.getConfig().disabledChecks.getValue()) {
            for (final CheckType type : CheckType.values()) {
                if (type.toString().equalsIgnoreCase(string)) {
                    CheckManager.checkIgnoreList.add(type);
                    break;
                }
            }
        }*/
    }
    
    public void activateCheck(final CheckType type, final String className) {
        if (!this.isActive(type)) {
            this.manager.getLoggingManager().logFineInfo("The " + type.toString() + " check was activated by " + className + ".");
            CheckManager.checkIgnoreList.remove(type);
        }
    }
    
    public void deactivateCheck(final CheckType type, final String className) {
        if (this.isActive(type)) {
            this.manager.getLoggingManager().logFineInfo("The " + type.toString() + " check was deactivated by " + className + ".");
            CheckManager.checkIgnoreList.add(type);
        }
    }
    
    public boolean isActive(final CheckType type) {
        return !CheckManager.checkIgnoreList.contains(type);
    }
    
    public void exemptPlayer(final Player player, final CheckType type, final String className) {
        if (!this.isExempt(player, type)) {
            if (!CheckManager.exemptList.containsKey(player.getName())) {
                CheckManager.exemptList.put(player.getName(), new ArrayList<CheckType>());
            }
            this.manager.getLoggingManager().logFineInfo(player.getName() + " was exempted from the " + type.toString() + " check by " + className + ".");
            CheckManager.exemptList.get(player.getName()).add(type);
        }
    }
    
    public void unexemptPlayer(final Player player, final CheckType type, final String className) {
        if (this.isExempt(player, type)) {
            this.manager.getLoggingManager().logFineInfo(player.getName() + " was unexempted from the " + type.toString() + " check by " + className + ".");
            CheckManager.exemptList.get(player.getName()).remove(type);
        }
    }
    
    public boolean isExempt(final Player player, final CheckType type) {
        return CheckManager.exemptList.containsKey(player.getName()) && CheckManager.exemptList.get(player.getName()).contains(type);
    }
    
    // verschone Leute mit OP?
    public boolean isOpExempt(final Player player) {
        return this.manager.exemptOp && player.isOp();
    }
    
    public boolean checkInWorld(final Player player) {
        return true;
    }
    
    public boolean willCheckQuick(final Player player, final CheckType type) {
        return this.isActive(type) && !this.isExempt(player, type) && !type.checkPermission(player) && !player.hasPermission(Permission.CHECK_EXEMPT.toString());
    }
    
    public boolean willCheck(final Player player, final CheckType type) {
        final boolean check = this.isActive(type) && this.checkInWorld(player) && !this.isExempt(player, type) && !type.checkPermission(player) && !this.isOpExempt(player) && !player.hasPermission(Permission.CHECK_EXEMPT.toString());
        AntiCheat.debugLog("Check " + type + (check ? " run " : " not run ") + "on " + player.getName());
        return check;
    }
    
    public boolean isOnline(final Player player) {
        for (final Player p : Bukkit.getOnlinePlayers()) {
            if (p.getName().equals(player.getName())) {
                return true;
            }
        }
        return false;
    }
    
    static {
        CheckManager.checkIgnoreList = new ArrayList<CheckType>();
        CheckManager.exemptList = new HashMap<String, List<CheckType>>();
    }
}
