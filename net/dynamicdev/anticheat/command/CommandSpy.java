package net.dynamicdev.anticheat.command;

import org.bukkit.command.*;
import org.bukkit.*;
import net.dynamicdev.anticheat.util.*;
import org.bukkit.metadata.*;
import org.bukkit.entity.*;

public class CommandSpy extends CommandBase {
    private static final Permission PERMISSION;
    private static final String[] HELP;
    
    public CommandSpy() {
        super("AntiCheat Spying", "spy", "anticheat spy [user]", CommandSpy.HELP, CommandSpy.PERMISSION);
    }
    
    @Override
    protected void execute(final CommandSender cs, final String[] args) {
        if (args.length == 1) {
            if (cs instanceof Player) {
                final Player sender = (Player)cs;
                if (!args[0].equalsIgnoreCase("off")) {
                    final Player player = Bukkit.getPlayer(args[0]);
                    if (player != null) {
                        for (final Player p : cs.getServer().getOnlinePlayers()) {
                            if (!Permission.SYSTEM_SPY.get((CommandSender)p)) {
                                p.hidePlayer(sender);
                            }
                        }
                        if (!sender.hasMetadata("ac-spydata")) {
                            final SpyState state = new SpyState(sender.getAllowFlight(), sender.isFlying(), sender.getLocation());
                            sender.setMetadata("ac-spydata", (MetadataValue)new FixedMetadataValue(me.corperateraider.myworld.Plugin.instance, (Object)state));
                        }
                        sender.setAllowFlight(true);
                        sender.setFlying(true);
                        sender.teleport((Entity)player);
                        sender.sendMessage(CommandSpy.GREEN + "You have been teleported to " + player.getName() + " and made invisible.");
                        sender.sendMessage(CommandSpy.GREEN + "To stop spying, type " + CommandSpy.WHITE + " /anti spy off");
                    } else {
                        cs.sendMessage(CommandSpy.RED + "Player: " + args[0] + " not found.");
                    }
                } else if (sender.hasMetadata("ac-spydata")) {
                    final SpyState state2 = (SpyState)sender.getMetadata("ac-spydata").get(0).value();
                    sender.setAllowFlight(state2.getAllowFlight());
                    sender.setFlying(state2.getFlying());
                    sender.teleport(state2.getLocation());
                    sender.removeMetadata("ac-spydata", me.corperateraider.myworld.Plugin.instance);
                    for (final Player p : cs.getServer().getOnlinePlayers()) {
                        p.showPlayer(sender);
                    }
                    sender.sendMessage(CommandSpy.GREEN + "Done spying! Brought you back to where you started!");
                } else {
                    sender.sendMessage(CommandSpy.RED + "You were not spying.");
                }
            } else {
                cs.sendMessage(CommandSpy.RED + "Sorry, but you can't spy on a player from the console.");
            }
        }
        else {
            this.sendHelp(cs);
        }
    }
    
    static {
        PERMISSION = Permission.SYSTEM_SPY;
        HELP = new String[] { CommandSpy.GRAY + "Use: " + CommandSpy.AQUA + "/anticheat spy [user]" + CommandSpy.GRAY + " to spy on a user" };
    }
}
