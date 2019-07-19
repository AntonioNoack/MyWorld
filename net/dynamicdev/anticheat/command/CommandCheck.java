package net.dynamicdev.anticheat.command;

import org.bukkit.command.CommandSender;

import net.dynamicdev.anticheat.util.Permission;
import net.dynamicdev.anticheat.check.CheckType;;

public class CommandCheck extends CommandBase {
	
    //private static final String NAME = "AntiCheat Check Management";
    //private static final String COMMAND = "check";
   // private static final String USAGE = "anticheat check [check] [on/off]";
    private static final Permission PERMISSION;
    private static final String[] HELP;
    
    public CommandCheck() {
        super("AntiCheat Check Management", "check", "anticheat check [check] [on/off]", CommandCheck.HELP, CommandCheck.PERMISSION);
    }
    
    @Override
    protected void execute(final CommandSender cs, final String[] args) {
        /*if (args.length == 2) {
            for (final CheckType type : CheckType.values()) {
                if (type.toString().equalsIgnoreCase(args[0]) || type.toString().replaceAll("_", "").equalsIgnoreCase(args[0])) {
                    final boolean value = CommandCheck.CHECK_MANAGER.isActive(type);
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
                    final String strValue = newValue ? " activated" : " deactivated";
                    if (value == newValue) {
                        cs.sendMessage(CommandCheck.GREEN + type.toString() + " is already" + strValue + "!");
                    }
                    else {
                        if (newValue) {
                            CommandCheck.CHECK_MANAGER.activateCheck(type, cs.getName());
                            final List<String> checks = CommandCheck.CONFIG.getConfig().disabledChecks.getValue();
                            if (checks.contains(type.toString())) {
                                checks.remove(type.toString());
                                CommandCheck.CONFIG.getConfig().disabledChecks.setValue(checks);
                            }
                        }
                        else {
                            CommandCheck.CHECK_MANAGER.deactivateCheck(type, cs.getName());
                            final List<String> checks = CommandCheck.CONFIG.getConfig().disabledChecks.getValue();
                            checks.add(type.toString());
                            CommandCheck.CONFIG.getConfig().disabledChecks.setValue(checks);
                        }
                        cs.sendMessage(CommandCheck.GREEN + type.toString() + strValue + ".");
                    }
                    return;
                }
            }
        }
        this.sendHelp(cs);*/
    }
    
    static {
        PERMISSION = Permission.SYSTEM_CHECK;
        (HELP = new String[3])[0] = CommandCheck.GRAY + "Use: " + CommandCheck.AQUA + "/anticheat check [check] on" + CommandCheck.GRAY + " to enable a check";
        CommandCheck.HELP[1] = CommandCheck.GRAY + "Use: " + CommandCheck.AQUA + "/anticheat check [check] off" + CommandCheck.GRAY + " to disable a check";
        final StringBuilder builder = new StringBuilder();
        builder.append(CommandCheck.GRAY + "Checks: ");
        for (int i = 0; i < CheckType.values().length; ++i) {
            builder.append(CheckType.values()[i]);
            if (i < CheckType.values().length - 1) {
                builder.append(", ");
            }
        }
        CommandCheck.HELP[2] = builder.toString();
    }
}
