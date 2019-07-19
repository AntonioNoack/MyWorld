package net.dynamicdev.anticheat.command;

import net.dynamicdev.anticheat.util.*;
import org.bukkit.command.*;

public class CommandReload extends CommandBase {
    //private static final String NAME = "AntiCheat Reload";
    //private static final String COMMAND = "reload";
    //private static final String USAGE = "anticheat reload";
    private static final Permission PERMISSION;
    private static final String[] HELP;
    
    public CommandReload() {
        super("AntiCheat Reload", "reload", "anticheat reload", CommandReload.HELP, CommandReload.PERMISSION);
    }
    
    @Override
    protected void execute(final CommandSender cs, final String[] args) {
        //CommandReload.CONFIG.load();
        //cs.sendMessage(CommandReload.GREEN + "AntiCheat configuration reloaded.");
    }
    
    static {
        PERMISSION = Permission.SYSTEM_RELOAD;
        HELP = new String[] { CommandReload.GRAY + "Use: " + CommandReload.AQUA + "/anticheat reload" + CommandReload.GRAY + " to reload AntiCheat settings" };
    }
}
