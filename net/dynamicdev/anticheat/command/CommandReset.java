package net.dynamicdev.anticheat.command;

import org.bukkit.command.*;
import net.dynamicdev.anticheat.*;
import net.dynamicdev.anticheat.util.*;

public class CommandReset extends CommandBase {
    private static final Permission PERMISSION;
    private static final String[] HELP;
    
    public CommandReset() {
        super("AntiCheat Resetting", "reset", "anticheat reset [user]", CommandReset.HELP, CommandReset.PERMISSION);
    }
    
    @Override
    protected void execute(final CommandSender cs, final String[] args) {
        if (args.length == 1) {
            final User user = CommandReset.USER_MANAGER.getUser(args[0]);
            if (user != null) {
                user.resetLevel();
                CommandReset.XRAY_TRACKER.reset(args[0]);
                user.clearMessages();
                AntiCheat.getManager().getBackend().getChatCheck().resetChatLevel(user);
                cs.sendMessage(args[0] + CommandReset.GREEN + " has been reseted.");
            }
            else {
                cs.sendMessage(CommandReset.RED + "Player: " + args[0] + " not found.");
            }
        }
        else {
            this.sendHelp(cs);
        }
    }
    
    static {
        PERMISSION = Permission.SYSTEM_RESET;
        HELP = new String[] { CommandReset.GRAY + "Use: " + CommandReset.AQUA + "/anticheat reset [user]" + CommandReset.GRAY + " to reset this user's hack level" };
    }
}
