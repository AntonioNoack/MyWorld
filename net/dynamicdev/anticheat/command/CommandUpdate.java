package net.dynamicdev.anticheat.command;

import net.dynamicdev.anticheat.util.*;
import org.bukkit.command.*;

public class CommandUpdate extends CommandBase {
	
   // private static final String NAME = "AntiCheat Update Checking";
  //  private static final String COMMAND = "update";
  //  private static final String USAGE = "anticheat update";
    private static final Permission PERMISSION;
    private static final String[] HELP;
    
    public CommandUpdate() {
        super("AntiCheat Update Checking", "update", "anticheat update", CommandUpdate.HELP, CommandUpdate.PERMISSION);
    }
    
    @Override
    protected void execute(final CommandSender cs, final String[] args) {
        /*cs.sendMessage(CommandUpdate.GRAY + "Running AntiCheat v" + CommandUpdate.GREEN + AntiCheat.getVersion());
        if (CommandUpdate.CONFIG.getConfig().autoUpdate.getValue()) {
            cs.sendMessage(CommandUpdate.GRAY + "Up to date: " + (AntiCheat.isUpdated() ? (CommandUpdate.GREEN + "YES") : (CommandUpdate.RED + "NO")));
            if (!AntiCheat.isUpdated()) {
                cs.sendMessage(CommandUpdate.GRAY + "Newest version: " + CommandUpdate.GREEN + AntiCheat.getUpdateDetails());
                cs.sendMessage(CommandUpdate.GOLD + "The newest version will be automatically installed on next launch.");
            }
        }
        else {
            cs.sendMessage(CommandUpdate.GRAY + "Your config settings have disabled update checking.");
            cs.sendMessage(CommandUpdate.GRAY + "Please enable this setting or visit http://dev.bukkit.org/bukkit-plugins/anticheatplus/");
        }*/
    	// kein Autoupdate!, da es mir meine Datei wohl löscheln würde...
    }
    
    static {
        PERMISSION = Permission.SYSTEM_UPDATE;
        HELP = new String[] { CommandUpdate.GRAY + "Use: " + CommandUpdate.AQUA + "/anticheat update" + CommandUpdate.GRAY + " to view the system's update status" };
    }
}
