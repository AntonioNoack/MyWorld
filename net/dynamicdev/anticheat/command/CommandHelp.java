package net.dynamicdev.anticheat.command;

import net.dynamicdev.anticheat.util.*;
import org.bukkit.command.*;

public class CommandHelp extends CommandBase {
    //private static final String NAME = "AntiCheat+ Help";
    //private static final String COMMAND = "help";
    //private static final String USAGE = "anticheat help";
    private static final Permission PERMISSION;
    private static final String[] HELP;
    
    public CommandHelp() {
        super("AntiCheat+ Help", "help", "anticheat help", CommandHelp.HELP, CommandHelp.PERMISSION);
    }
    
    @Override
    protected void execute(final CommandSender cs, final String[] args) {
        this.sendHelp(cs);
    }
    
    static {
        PERMISSION = Permission.SYSTEM_HELP;
        HELP = new String[] { CommandHelp.GRAY + "/anti " + CommandHelp.AQUA + "help", CommandHelp.GRAY + "/anti " + CommandHelp.AQUA + "reload", CommandHelp.GRAY + "/anti " + CommandHelp.AQUA + "update", CommandHelp.GRAY + "/anti " + CommandHelp.AQUA + "debug " + CommandHelp.AQUA + "<user>", CommandHelp.GRAY + "/anti " + CommandHelp.AQUA + "check " + CommandHelp.GOLD + "[check] [on/off]", CommandHelp.GRAY + "/anti " + CommandHelp.AQUA + "log " + CommandHelp.GOLD + "[file/console] [on/off]", CommandHelp.GRAY + "/anti " + CommandHelp.AQUA + "report " + CommandHelp.GOLD + "[group/user]", CommandHelp.GRAY + "/anti " + CommandHelp.AQUA + "reset " + CommandHelp.GOLD + "[user]", CommandHelp.GRAY + "/anti " + CommandHelp.AQUA + "xray " + CommandHelp.GOLD + "[user]", CommandHelp.GRAY + "/anti " + CommandHelp.AQUA + "spy " + CommandHelp.GOLD + "[user]" };
    }
}
