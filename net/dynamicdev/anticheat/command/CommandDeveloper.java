package net.dynamicdev.anticheat.command;

import net.dynamicdev.anticheat.util.*;
import org.bukkit.command.*;
import net.dynamicdev.anticheat.*;

public class CommandDeveloper extends CommandBase {
    //private static final String NAME = "AntiCheat Developer Mode";
    //private static final String COMMAND = "developer";
    //private static final String USAGE = "anticheat developer";
    private static final Permission PERMISSION;
    private static final String[] HELP;
    
    public CommandDeveloper() {
        super("AntiCheat Developer Mode", "developer", "anticheat developer", CommandDeveloper.HELP, CommandDeveloper.PERMISSION);
    }
    
    @Override
    protected void execute(final CommandSender cs, final String[] args) {
        AntiCheat.setDeveloperMode(!AntiCheat.developerMode());
        cs.sendMessage(CommandDeveloper.GREEN + "Developer mode " + (AntiCheat.developerMode() ? "ON" : "OFF"));
    }
    
    static {
        PERMISSION = Permission.SYSTEM_DEBUG;
        HELP = new String[] { CommandDeveloper.GRAY + "Use: " + CommandDeveloper.AQUA + "/anticheat developer" + CommandDeveloper.GRAY + " to turn on developer mode" };
    }
}
