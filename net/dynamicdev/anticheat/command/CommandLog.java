package net.dynamicdev.anticheat.command;

import net.dynamicdev.anticheat.util.*;
import org.bukkit.command.*;

public class CommandLog extends CommandBase {
	
    //private static final String NAME = "AntiCheat Logging";
    //private static final String COMMAND = "log";
    //private static final String USAGE = "anticheat log [file/console] [on/off]";
    private static final Permission PERMISSION;
    private static final String[] HELP;
    
    public CommandLog() {
        super("AntiCheat Logging", "log", "anticheat log [file/console] [on/off]", CommandLog.HELP, CommandLog.PERMISSION);
    }
    
    @Override
    protected void execute(final CommandSender cs, final String[] args) {
        /*if (args.length == 2) {
            ConfigurationFile.ConfigValue<Boolean> value;
            String name;
            if (args[0].equalsIgnoreCase("file")) {
                value = CommandLog.CONFIG.getConfig().logToFile;
                name = "File logging";
            }
            else {
                if (!args[0].equalsIgnoreCase("console")) {
                    this.sendHelp(cs);
                    return;
                }
                value = CommandLog.CONFIG.getConfig().logToConsole;
                name = "Console logging";
            }
            boolean newValue;
            if (args[1].equalsIgnoreCase("on") || args[1].equalsIgnoreCase("enable")) {
                newValue = true;
            }
            else {
                if (!args[1].equalsIgnoreCase("off") && !args[1].equalsIgnoreCase("disable")) {
                    this.sendHelp(cs);
                    return;
                }
                newValue = false;
            }
            final String strValue = newValue ? " enabled" : " disabled";
            if (value.getValue() == newValue) {
                cs.sendMessage(CommandLog.GREEN + name + " is already " + strValue + "!");
            }
            else {
                value.setValue(newValue);
                cs.sendMessage(CommandLog.GREEN + name + strValue + ".");
                CommandLog.CONFIG.getConfig().reload();
            }
        }
        else {
            this.sendHelp(cs);
        }*/
    }
    
    static {
        PERMISSION = Permission.SYSTEM_LOG;
        HELP = new String[] { CommandLog.GRAY + "Use: " + CommandLog.AQUA + "/anticheat log console on" + CommandLog.GRAY + " to enable console logging", CommandLog.GRAY + "Use: " + CommandLog.AQUA + "/anticheat log file off" + CommandLog.GRAY + " to disable file logging" };
    }
}
