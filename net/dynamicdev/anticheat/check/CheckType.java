package net.dynamicdev.anticheat.check;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import net.dynamicdev.anticheat.CheckFailEvent;
import net.dynamicdev.anticheat.util.Permission;
import net.dynamicdev.anticheat.util.User;

public enum CheckType {
	REPORT_SPAM(Permission.CHECK_REPORT_SPAM),
    ZOMBE_FLY(Permission.CHECK_ZOMBE_FLY), 
    ZOMBE_NOCLIP(Permission.CHECK_ZOMBE_NOCLIP), 
    ZOMBE_CHEAT(Permission.CHECK_ZOMBE_CHEAT), 
    FLY(Permission.CHECK_FLY), 
    WATER_WALK(Permission.CHECK_WATERWALK), 
    NO_SWING(Permission.CHECK_NOSWING), 
    FAST_BREAK(Permission.CHECK_FASTBREAK), 
    FAST_PLACE(Permission.CHECK_FASTPLACE), 
    CHAT_SPAM(Permission.CHECK_CHATSPAM), 
    COMMAND_SPAM(Permission.CHECK_COMMANDSPAM), 
    SPRINT(Permission.CHECK_SPRINT), 
    SNEAK(Permission.CHECK_SNEAK), 
    SPEED(Permission.CHECK_SPEED), 
    VCLIP(Permission.CHECK_VCLIP), 
    SPIDER(Permission.CHECK_SPIDER), 
    NOFALL(Permission.CHECK_NOFALL), 
    FAST_BOW(Permission.CHECK_FASTBOW), 
    FAST_EAT(Permission.CHECK_FASTEAT), 
    FAST_HEAL(Permission.CHECK_FASTHEAL), 
    FORCEFIELD(Permission.CHECK_FORCEFIELD), 
    XRAY(Permission.CHECK_XRAY), 
    LONG_REACH(Permission.CHECK_LONGREACH), 
    FAST_PROJECTILE(Permission.CHECK_FASTPROJECTILE), 
    ITEM_SPAM(Permission.CHECK_ITEMSPAM), 
    FAST_INVENTORY(Permission.CHECK_FASTINVENTORY), 
    AUTOTOOL(Permission.CHECK_AUTOTOOL), 
    VELOCITY(Permission.CHECK_VELOCITY), 
    MOREPACKETS(Permission.CHECK_MOREPACKETS), 
    DIRECTION(Permission.CHECK_DIRECTION);
    
    private final Permission permission;
    private final Map<String, Integer> level;
    
    private CheckType(final Permission perm) {
        this.level = new HashMap<String, Integer>();
        this.permission = perm;
    }
    
    public boolean checkPermission(final Player player) {
        return this.permission.get((CommandSender)player);
    }
    
    public void logUse(final User user) {
        final int amount = (this.level.get(user.getName()) == null) ? 1 : (this.level.get(user.getName()) + 1);
        this.level.put(user.getName(), amount);
        Bukkit.getServer().getPluginManager().callEvent((Event)new CheckFailEvent(user, this));
    }
    
    public void clearUse(final String name) {
        this.level.put(name, 0);
    }
    
    public int getUses(final String name) {
        return (this.level.get(name) != null) ? this.level.get(name) : 0;
    }
    
    public static String getName(final CheckType type) {
        final char[] chars = type.toString().replaceAll("_", " ").toLowerCase().toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);
        return new String(chars);
    }
}
