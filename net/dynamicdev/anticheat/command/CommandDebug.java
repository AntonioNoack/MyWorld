package net.dynamicdev.anticheat.command;

import org.bukkit.command.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import net.dynamicdev.anticheat.util.*;

/**
 * erstellt einen Bericht und sendet ihn an PasteBin
 * */
public class CommandDebug extends CommandBase {
    //private static final String NAME = "AntiCheat Debug Reporting";
    //private static final String COMMAND = "debug";
    //private static final String USAGE = "anticheat debug <user>";
    private static final Permission PERMISSION;
    private static final String[] HELP;
    
    public CommandDebug() {
        super("AntiCheat Debug Reporting", "debug", "anticheat debug <user>", CommandDebug.HELP, CommandDebug.PERMISSION);
    }
    
    @Override
    protected void execute(final CommandSender cs, final String[] args) {
        Player player = null;
        if (args.length == 1) {
            player = Bukkit.getPlayer(args[0]);
        } else if (cs instanceof Player) {
            player = (Player)cs;
        }
        cs.sendMessage(CommandDebug.GRAY + "Please wait while I collect some data...");
        final PastebinReport report = new PastebinReport(cs, player);
        cs.sendMessage(CommandDebug.GREEN + "Debug information posted to: " + CommandDebug.WHITE + report.getURL());
        cs.sendMessage(CommandDebug.GREEN + "Please include this link when making bug reports.");
    }
    
    static {
        PERMISSION = Permission.SYSTEM_DEBUG;
        HELP = new String[] { CommandDebug.GRAY + "Use: " + CommandDebug.AQUA + "/anticheat debug" + CommandDebug.GRAY + " to create a debug report", CommandDebug.GRAY + "Use: " + CommandDebug.AQUA + "/anticheat debug <user>" + CommandDebug.GRAY + " to create a debug report for a user" };
    }
}
