package net.dynamicdev.anticheat.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.dynamicdev.anticheat.AntiCheat;
import net.dynamicdev.anticheat.manage.CheckManager;
import net.dynamicdev.anticheat.manage.UserManager;
import net.dynamicdev.anticheat.manage.XRayTracker;
import net.dynamicdev.anticheat.util.Permission;

public class CommandBase {
	
    public static final UserManager USER_MANAGER;
    public static final CheckManager CHECK_MANAGER;
    public static final XRayTracker XRAY_TRACKER;
    public static final ChatColor RED;
    public static final ChatColor YELLOW;
    public static final ChatColor GREEN;
    public static final ChatColor WHITE;
    public static final ChatColor GRAY;
    public static final ChatColor GOLD;
    public static final ChatColor AQUA;
    public static final Server SERVER;
    public static final String PERMISSIONS_ERROR;
    public static final String MENU_END = "-----------------------------------------------------";
    private final String name;
    private final String command;
    private final String usage;
    private final String[] help;
    private final Permission permission;
    
    public CommandBase(final String name, final String command, final String usage, final String[] help, final Permission permission) {
        this.name = name;
        this.command = command;
        this.usage = usage;
        this.help = help;
        this.permission = permission;
    }
    
    public void run(final CommandSender cs, final String[] args) {
        if (this.permission.get(cs)) {
            this.execute(cs, args);
        }
        else {
            cs.sendMessage(CommandBase.PERMISSIONS_ERROR + " (" + CommandBase.WHITE + this.permission.toString() + CommandBase.RED + ")");
        }
    }
    
    protected void execute(final CommandSender cs, final String[] args) {
    }
    
    public void sendHelp(final CommandSender cs) {
        cs.sendMessage(CommandBase.GREEN + "== " + CommandBase.GRAY + this.getName() + CommandBase.GREEN + " ==");
        cs.sendMessage(CommandBase.GREEN + "Usage: " + CommandBase.GRAY + ((cs instanceof Player) ? "/" : "") + this.getUsage());
        cs.sendMessage(CommandBase.GREEN + "Permission: " + CommandBase.GRAY + this.getPermission().toString());
        for (final String string : this.getHelp()) {
            cs.sendMessage(string);
        }
    }
    
    public String getName() {
        return this.name;
    }
    
    public String getCommand() {
        return this.command;
    }
    
    public String getUsage() {
        return this.usage;
    }
    
    public String[] getHelp() {
        return this.help;
    }
    
    public Permission getPermission() {
        return this.permission;
    }
    
    static {
        USER_MANAGER = AntiCheat.getManager().getUserManager();
        CHECK_MANAGER = AntiCheat.getManager().getCheckManager();
        XRAY_TRACKER = AntiCheat.getManager().getXRayTracker();
        RED = ChatColor.RED;
        YELLOW = ChatColor.YELLOW;
        GREEN = ChatColor.GREEN;
        WHITE = ChatColor.WHITE;
        GRAY = ChatColor.GRAY;
        GOLD = ChatColor.GOLD;
        AQUA = ChatColor.AQUA;
        SERVER = Bukkit.getServer();
        PERMISSIONS_ERROR = CommandBase.RED + "Insufficient Permissions.";
    }
}
