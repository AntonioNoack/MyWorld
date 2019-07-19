package net.dynamicdev.anticheat.manage;

import java.util.ArrayList;
import java.util.List;

import net.dynamicdev.anticheat.AntiCheat;
import net.dynamicdev.anticheat.check.CheckType;
import net.dynamicdev.anticheat.config.providers.Lang;
import net.dynamicdev.anticheat.util.Group;
import net.dynamicdev.anticheat.util.User;
import net.dynamicdev.anticheat.util.Utilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class UserManager {
	
    private List<User> users = new ArrayList<>();
    private static AntiCheatManager manager;
    private static final ChatColor GRAY;
    private static final ChatColor GOLD;
    private static final ChatColor RED;
    
    public UserManager(final AntiCheatManager manager) {
        UserManager.manager = manager;
    }
    
    public User getUser(final String name) {
        for (final User user : this.users) {
            if (user.getName().equalsIgnoreCase(name)) {
                return user;
            }
        }
        final User user2 = new User(name);
        user2.setIsWaitingOnLevelSync(true);
        AntiCheat.loadLevelToUser(user2);
        return user2;
    }
    
    public List<User> getUsers() {
        return this.users;
    }
    
    public void addUser(final User user) {
        this.users.add(user);
    }
    
    public void remove(final User user) {
        this.users.remove(user);
    }
    
    public void saveLevel(final User user) {
    	AntiCheat.saveLevelFromUser(user);
    }
    
    public List<User> getUsersInGroup(final Group group) {
        final List<User> list = new ArrayList<User>();
        for (final User u : this.users) {
            if (u.getGroup() == group) {
                list.add(u);
            }
        }
        return list;
    }
    
    public int safeGetLevel(final String name) {
        final User user = this.getUser(name);
        if (user == null) {
            return 0;
        }
        return user.getLevel();
    }
    
    public void safeSetLevel(final String name, final int level) {
        final User user = this.getUser(name);
        if (user != null) {
            user.setLevel(level);
        }
    }
    
    public void safeReset(final String name) {
        final User user = this.getUser(name);
        if (user != null) {
            user.resetLevel();
        }
    }
    
    public String[] getAlert() {
        return Lang.ALERT;
    }
    
    public void alert(final User user, final Group group, final CheckType type) {
        final ArrayList<String> messageArray = new ArrayList<String>();
        final String[] alert = this.getAlert();
        for (int i = 0; i < alert.length; i++) {
            String message = alert[i];
            if (!message.equals("")) {
                message = message.replaceAll("&player", UserManager.GOLD + user.getName() + UserManager.GRAY);
                message = message.replaceAll("&check", UserManager.GOLD + CheckType.getName(type) + UserManager.GRAY);
                message = message.replaceAll("&group", group.getColor() + group.getName() + UserManager.GRAY);
                message = message.replaceAll("&level", "" + user.getLevel() + UserManager.GRAY);
                messageArray.add(message);
            }
        }
        Utilities.alert(messageArray);
        this.execute(user, group.getActions(), type);
    }
    
    public void execute(final User user, final String[] actions, final CheckType type) {
        this.execute(user, actions, type, Lang.KICK_REASON, Lang.WARNING, Lang.BAN_REASON);
    }
    
    public void execute(final User user, final String[] actions, final CheckType type, final String kickReason, final String[] warning, final String banReason) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(me.corperateraider.myworld.Plugin.instance, new Runnable() {
            @SuppressWarnings("deprecation")
			@Override
            public void run() {
                final String name = user.getName();
                for (String event : actions) {
                    event = event.replaceAll("&player", name).replaceAll("&world", user.getPlayer().getWorld().getName()).replaceAll("&check", type.name());
                    if (event.startsWith("COMMAND[")) {
                        for (final String cmd : Utilities.getCommands(event)) {
                            Bukkit.getServer().dispatchCommand((CommandSender)Bukkit.getConsoleSender(), cmd);
                        }
                    } else if (event.equalsIgnoreCase("KICK")) {
                        if (AntiCheat.developerMode()) {
                            continue;
                        }
                        user.getPlayer().kickPlayer(UserManager.RED + kickReason);
                        final String msg = UserManager.RED + Lang.KICK_BROADCAST.replaceAll("&player", name) + " (" + CheckType.getName(type) + ")";
                        if (msg.equals("")) {
                            continue;
                        }
                        UserManager.manager.log(msg);
                        UserManager.manager.playerLog(msg);
                    } else if (event.equalsIgnoreCase("WARN")) {
                        for (final String string : warning) {
                            if (!string.equals("")) {
                                user.getPlayer().sendMessage(UserManager.RED + string);
                            }
                        }
                    } else if (event.equalsIgnoreCase("BAN")) {
                        if (AntiCheat.developerMode()) {
                            continue;
                        }
                        user.getPlayer().setBanned(true);
                        user.getPlayer().kickPlayer(UserManager.RED + banReason);
                        final String msg = UserManager.RED + Lang.BAN_BROADCAST.replaceAll("&player", name) + " (" + CheckType.getName(type) + ")";
                        if (msg.equals("")) {
                            continue;
                        }
                        UserManager.manager.log(msg);
                        UserManager.manager.playerLog(msg);
                    } else {
                        if (!event.equalsIgnoreCase("RESET")) {
                            continue;
                        }
                        user.resetLevel();
                    }
                }
            }
        });
    }
    
    static {
        GRAY = ChatColor.GRAY;
        GOLD = ChatColor.GOLD;
        RED = ChatColor.RED;
    }
}
