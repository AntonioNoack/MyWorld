package net.dynamicdev.anticheat;

import java.util.List;

import org.bukkit.entity.Player;

import net.dynamicdev.anticheat.check.CheckType;
import net.dynamicdev.anticheat.manage.AntiCheatManager;
import net.dynamicdev.anticheat.manage.CheckManager;
import net.dynamicdev.anticheat.manage.UserManager;
import net.dynamicdev.anticheat.manage.XRayTracker;
import net.dynamicdev.anticheat.util.Group;

public class AntiCheatAPI {
    private static CheckManager chk;
    private static UserManager umr;
    private static XRayTracker xtracker;
    
    public static void activateCheck(final CheckType type) {
        AntiCheatAPI.chk.activateCheck(type, "AntiCheatAPI");
    }
    
    public static void deactivateCheck(final CheckType type) {
        AntiCheatAPI.chk.deactivateCheck(type, "AntiCheatAPI");
    }
    
    public static boolean isActive(final CheckType type) {
        return AntiCheatAPI.chk.isActive(type);
    }
    
    public static void exemptPlayer(final Player player, final CheckType type) {
        AntiCheatAPI.chk.exemptPlayer(player, type, "AntiCheatAPI");
    }
    
    public static void unexemptPlayer(final Player player, final CheckType type) {
        AntiCheatAPI.chk.unexemptPlayer(player, type, "AntiCheatAPI");
    }
    
    public static boolean isExempt(final Player player, final CheckType type) {
        return AntiCheatAPI.chk.isExempt(player, type);
    }
    
    public boolean willCheck(final Player player, final CheckType type) {
        return AntiCheatAPI.chk.willCheck(player, type);
    }
    
    @Deprecated
    public static int getLevel(final Player player) {
        return AntiCheatAPI.umr.safeGetLevel(player.getName());
    }
    
    @Deprecated
    public static void setLevel(final Player player, final int level) {
        AntiCheatAPI.umr.safeSetLevel(player.getName(), level);
    }
    
    public static void resetPlayer(final Player player) {
        AntiCheatAPI.umr.getUser(player.getName()).resetLevel();
    }
    
    public static Group getGroup(final Player player) {
        return AntiCheatAPI.umr.getUser(player.getName()).getGroup();
    }
    
    public static List<Group> getGroups() {
        return Group.getGroups();
    }
    
    public static boolean isXrayer(final Player player) {
        final String name = player.getName();
        return AntiCheatAPI.xtracker.sufficientData(name) && AntiCheatAPI.xtracker.hasAbnormal(name);
    }
    
    public static AntiCheatManager getManager() {
        return AntiCheat.getManager();
    }
    
    static {
        AntiCheatAPI.chk = AntiCheat.getManager().getCheckManager();
        AntiCheatAPI.umr = AntiCheat.getManager().getUserManager();
        AntiCheatAPI.xtracker = AntiCheat.getManager().getXRayTracker();
    }
}
