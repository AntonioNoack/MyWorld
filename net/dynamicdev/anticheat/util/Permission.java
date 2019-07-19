package net.dynamicdev.anticheat.util;

import org.bukkit.command.*;

public enum Permission {
	CHECK_REPORT_SPAM,
    CHECK_EXEMPT, 
    CHECK_ZOMBE_FLY, 
    CHECK_ZOMBE_NOCLIP, 
    CHECK_ZOMBE_CHEAT, 
    CHECK_FLY, 
    CHECK_WATERWALK, 
    CHECK_NOSWING, 
    CHECK_FASTBREAK, 
    CHECK_FASTPLACE, 
    CHECK_CHATSPAM, 
    CHECK_COMMANDSPAM, 
    CHECK_SPRINT, 
    CHECK_SNEAK, 
    CHECK_SPEED, 
    CHECK_VCLIP, 
    CHECK_SPIDER, 
    CHECK_NOFALL, 
    CHECK_FASTBOW, 
    CHECK_FASTEAT, 
    CHECK_FASTHEAL, 
    CHECK_FORCEFIELD, 
    CHECK_XRAY, 
    CHECK_LONGREACH, 
    CHECK_FASTPROJECTILE, 
    CHECK_ITEMSPAM, 
    CHECK_VISUAL, 
    CHECK_VELOCITY, 
    CHECK_FASTINVENTORY, 
    CHECK_AUTOTOOL, 
    CHECK_MOREPACKETS, 
    CHECK_DIRECTION, 
    SYSTEM_LOG, 
    SYSTEM_XRAY, 
    SYSTEM_RESET, 
    SYSTEM_SPY, 
    SYSTEM_HELP, 
    SYSTEM_UPDATE, 
    SYSTEM_REPORT, 
    SYSTEM_ALERT, 
    SYSTEM_NOTICE, 
    SYSTEM_CALIBRATE, 
    SYSTEM_CHECK, 
    SYSTEM_DEBUG, 
    SYSTEM_ALERTALL, 
    SYSTEM_RELOAD;
    
    public static boolean getCommandExempt(final CommandSender cs, final String commandLabel) {
        return cs.hasPermission(Permission.CHECK_COMMANDSPAM.toString() + commandLabel);
    }
    
    public boolean get(final CommandSender cs) {
        return ((this == Permission.CHECK_CHATSPAM || this == Permission.CHECK_COMMANDSPAM) && cs.hasPermission("anticheat.check.spam")) || cs.hasPermission(this.toString()) || cs.hasPermission(this.getBase()) || cs.hasPermission("anticheat.*");
    }
    
    public String getBase() {
        return "anticheat." + this.name().toLowerCase().split("_")[0] + ".*";
    }
    
    public String whichPermission(final CommandSender cs) {
        for (final String s : new String[] {"anticheat.*", this.getBase(), this.toString()}){
            if(cs.hasPermission(s)) {
                return s;
            }
        }
        return null;
    }
    
    @Override
    public String toString() {
        return "anticheat." + this.name().toLowerCase().replace("_", ".");
    }
}
