package net.dynamicdev.anticheat.command;

import net.dynamicdev.anticheat.util.*;
import org.bukkit.command.*;
import org.bukkit.*;
import org.bukkit.entity.*;

public class CommandXray extends CommandBase {
  //  private static final String NAME = "AntiCheat XRAY Stats";
  //  private static final String COMMAND = "xray";
  //  private static final String USAGE = "anticheat xray [user]";
    private static final Permission PERMISSION;
    private static final String[] HELP;
    
    public CommandXray() {
        super("AntiCheat XRAY Stats", "xray", "anticheat xray [user]", CommandXray.HELP, CommandXray.PERMISSION);
    }
    
    @Override
    protected void execute(final CommandSender cs, final String[] args) {
        if (args.length == 1) {
            if (true) {//checkXray?
                final Player player = Bukkit.getPlayer(args[0]);
                if (player != null) {
                    if (CommandXray.XRAY_TRACKER.sufficientData(player.getName())) {
                        CommandXray.XRAY_TRACKER.sendStats(cs, player.getName());
                    }
                    else {
                        cs.sendMessage(CommandXray.RED + "Insufficient data collected from " + CommandXray.WHITE + args[0] + CommandXray.RED + ".");
                        cs.sendMessage(CommandXray.RED + "Please wait until more info is collected before predictions are calculated.");
                    }
                }
                else if (CommandXray.XRAY_TRACKER.sufficientData(args[0])) {
                    CommandXray.XRAY_TRACKER.sendStats(cs, args[1]);
                }
                else {
                    cs.sendMessage(CommandXray.RED + "Insufficient data collected from " + CommandXray.WHITE + args[0] + CommandXray.RED + ".");
                    cs.sendMessage(CommandXray.RED + "Please wait until more info is collected before predictions are calculated.");
                }
            }
        } else {
            this.sendHelp(cs);
        }
    }
    
    static {
        PERMISSION = Permission.SYSTEM_XRAY;
        HELP = new String[] { CommandXray.GRAY + "Use: " + CommandXray.AQUA + "/anticheat xray [user]" + CommandXray.GRAY + " to view xray statistics for a user" };
    }
}
