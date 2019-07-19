package net.dynamicdev.anticheat.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dynamicdev.anticheat.AntiCheat;
import net.dynamicdev.anticheat.check.CheckType;
import net.dynamicdev.anticheat.manage.CheckManager;
import net.dynamicdev.anticheat.util.Group;
import net.dynamicdev.anticheat.util.Permission;
import net.dynamicdev.anticheat.util.User;
import net.dynamicdev.anticheat.util.Utilities;

public class CommandReport extends CommandBase {
	
    //private static final String NAME = "AntiCheatPlus Reports";
    //private static final String COMMAND = "report";
   // private static final String USAGE = "anticheat report [group/user]";
    private static final Permission PERMISSION;
    private static final String[] HELP;
    
    public CommandReport() {
        super("AntiCheatPlus Reports", "report", "anticheat report [group/user]", CommandReport.HELP, CommandReport.PERMISSION);
    }
    
    @Override
    protected void execute(final CommandSender cs, final String[] args) {
        if (args.length >= 1) {
            int page = 1;
            if (args.length == 2) {
                if (Utilities.isInt(args[1])) {
                    page = Integer.parseInt(args[1]);
                } else {
                    cs.sendMessage(CommandReport.RED + "Not a valid page number: " + CommandReport.WHITE + args[1]);
                }
            }
            if ("low".equalsIgnoreCase(args[0])) {
                this.groupReport(cs, null, page);
            } else if ("all".equalsIgnoreCase(args[0])) {
                cs.sendMessage(CommandReport.GREEN + "Low: " + CommandReport.WHITE + CommandReport.USER_MANAGER.getUsersInGroup(null).size() + " players");
                for (final Group g : Group.getGroups()) {
                    final int numPlayers = CommandReport.USER_MANAGER.getUsersInGroup(g).size();
                    cs.sendMessage(g.getColor() + g.getName() + CommandReport.WHITE + ": " + numPlayers + " players");
                }
                cs.sendMessage(CommandReport.GRAY + "Use " + CommandReport.AQUA + "/anticheat report [group]" + CommandReport.GRAY + " for a list of players in each group.");
            } else {
                for (final Group group : Group.getGroups()) {
                    if (group.getName().equalsIgnoreCase(args[0])) {
                        this.groupReport(cs, group, page);
                        return;
                    }
                }
                for (final Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().equalsIgnoreCase(args[0])) {
                        final User user = AntiCheat.getManager().getUserManager().getUser(args[0]);
                        this.playerReport(cs, user, page);
                        return;
                    }
                }
                cs.sendMessage(CommandReport.RED + "Not a valid group or user: " + CommandReport.WHITE + args[0]);
            }
        } else {
            this.sendHelp(cs);
        }
    }
    
    private void groupReport(final CommandSender cs, final Group group, final int page) {
        final List<User> users = CommandReport.USER_MANAGER.getUsersInGroup(group);
        final ChatColor color = (group == null) ? CommandReport.GREEN : group.getColor();
        final String groupName = (group == null) ? "Low" : group.getName();
        final int pages = (int)Math.ceil(users.size() / 7.0f);
        if (page <= pages && page > 0) {
            cs.sendMessage("--------------------[" + CommandReport.GREEN + "REPORT[" + page + "/" + pages + "]" + CommandReport.WHITE + "]---------------------");
            cs.sendMessage(CommandReport.GRAY + "Group: " + color + groupName);
            for (int x = 0; x < 7; ++x) {
                final int index = (page - 1) * 6 + (x + (page - 1) * 1);
                if (index < users.size()) {
                    final String player = users.get(index).getName();
                    cs.sendMessage(CommandReport.GRAY + player);
                }
            }
            cs.sendMessage("-----------------------------------------------------");
        }
        else if (pages == 0) {
            cs.sendMessage("--------------------[" + CommandReport.GREEN + "REPORT[1/1]" + CommandReport.WHITE + "]---------------------");
            cs.sendMessage(CommandReport.GRAY + "Group: " + color + groupName);
            cs.sendMessage(CommandReport.GRAY + "There are no users in this group.");
            cs.sendMessage("-----------------------------------------------------");
        }
        else {
            cs.sendMessage(CommandReport.RED + "Page not found. Requested " + CommandReport.WHITE + page + CommandReport.RED + ", Max " + CommandReport.WHITE + pages);
        }
    }
    
    private void playerReport(final CommandSender cs, final User user, final int page) {
        final List<CheckType> types = new ArrayList<CheckType>();
        for (final CheckType type : CheckType.values()) {
            if (type.getUses(user.getName()) > 0) {
                types.add(type);
            }
        }
        final String name = user.getName();
        final int pages = (int) Math.ceil(types.size() / 6.0f);
        final Group group = user.getGroup();
        String groupString = CommandReport.GREEN + "Low";
        if (group != null) {
            groupString = group.getColor() + group.getName();
        }
        groupString = groupString + " (" + user.getLevel() + ")";
        if (page <= pages && page > 0) {
            cs.sendMessage("--------------------[" + CommandReport.GREEN + "REPORT[" + page + "/" + pages + "]" + CommandReport.WHITE + "]---------------------");
            cs.sendMessage(CommandReport.GRAY + "Player: " + CommandReport.WHITE + name);
            cs.sendMessage(CommandReport.GRAY + "Group: " + groupString);
            for (int x = 0; x < 6; ++x) {
                final int index = (page - 1) * 5 + (x + (page - 1));
                if (index < types.size()) {
                    final CheckType type2 = types.get(index);
                    final int use = type2.getUses(name);
                    ChatColor color = CommandReport.WHITE;
                    if (use >= 20) {
                        color = CommandReport.YELLOW;
                    }
                    else if (use > 50) {
                        color = CommandReport.RED;
                    }
                    cs.sendMessage(CommandReport.GRAY + CheckType.getName(type2) + ": " + color + use);
                }
            }
            cs.sendMessage("-----------------------------------------------------");
        }
        else if (pages == 0 && page == 1) {
            cs.sendMessage("--------------------[" + CommandReport.GREEN + "REPORT[1/1]" + CommandReport.WHITE + "]---------------------");
            cs.sendMessage(CommandReport.GRAY + "Player: " + CommandReport.WHITE + name);
            cs.sendMessage(CommandReport.GRAY + "Group: " + groupString);
            cs.sendMessage(CommandReport.GRAY + "This user has not failed any checks.");
            cs.sendMessage("-----------------------------------------------------");
        }
        else {
            cs.sendMessage(CommandReport.RED + "Page not found. Requested " + CommandReport.WHITE + page + CommandReport.RED + ", Max " + CommandReport.WHITE + pages + 1);
        }
        if (AntiCheat.developerMode()) {
            int permission = 0;
            int check = 0;
            for (final Permission perm : Permission.values()) {
                if (perm.get((CommandSender)user.getPlayer())) {
                    ++permission;
                }
            }
            final CheckManager manager = AntiCheat.getManager().getCheckManager();
            for (final CheckType type3 : CheckType.values()) {
                if (manager.willCheck(user.getPlayer(), type3)) {
                    ++check;
                }
            }
            cs.sendMessage(ChatColor.GOLD + "User has " + permission + "/" + Permission.values().length + " permissions");
            cs.sendMessage(ChatColor.GOLD + "User will be checked for " + check + "/" + CheckType.values().length + " checks");
        }
    }
    
    static {
        PERMISSION = Permission.SYSTEM_REPORT;
        HELP = new String[] { CommandReport.GRAY + "Use: " + CommandReport.AQUA + "/anticheat report [group]" + CommandReport.GRAY + " to see all users in a given group", CommandReport.GRAY + "Use: " + CommandReport.AQUA + "/anticheat report [user]" + CommandReport.GRAY + " to see a single user's report", CommandReport.GRAY + "Use: " + CommandReport.AQUA + "/anticheat report [user/group] [num]" + CommandReport.GRAY + " to see pages of a report" };
    }
}
