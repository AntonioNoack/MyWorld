package net.dynamicdev.anticheat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import net.dynamicdev.anticheat.command.CommandHandler;
import net.dynamicdev.anticheat.manage.*;
import net.dynamicdev.anticheat.event.*;
import net.dynamicdev.anticheat.util.Group;
import net.dynamicdev.anticheat.util.User;
import net.dynamicdev.anticheat.util.Utilities;

import org.bukkit.*;

public class AntiCheat {
	
	private me.corperateraider.myworld.Plugin plug = me.corperateraider.myworld.Plugin.instance;
	
	// # Valid actions = NONE,KICK,BAN,COMMAND[command]
	public static String reasonOne="KICK", reasonTwo="BAN";
	public static CommandHandler commandHandler;
	
    private static AntiCheatManager manager;
    private static List<Listener> eventList = new ArrayList<Listener>();
    private static boolean developer;
    //private static final int PROJECT_ID = 88146;
    private static Long loadTime;
    
    public void onEnable() {
        loadTime = System.currentTimeMillis();
        manager = new AntiCheatManager(this, plug.getLogger());
        eventList.add(new PlayerListener());
        eventList.add(new BlockListener());
        eventList.add(new EntityListener());
        eventList.add(new VehicleListener());
        eventList.add(new InventoryListener());
        setupXray();
        setupEvents();
        setupCommands();
        restoreLevels();
    }
    
    public void onDisable() {
        saveLevelsFromUsers(getManager().getUserManager().getUsers());
        AntiCheatManager.close();
        plug.getServer().getScheduler().cancelTasks(plug);
        this.cleanup();
    }
    
    private void setupXray() {
        final XRayTracker xtracker = AntiCheat.manager.getXRayTracker();
        final int time = 60 * 20;//XRayinterval
        if (true) {
            AntiCheat.eventList.add(new XRayListener());
            if (true) {
                plug.getServer().getScheduler().runTaskTimerAsynchronously(plug, new Runnable() {
                    @Override
                    public void run() {
                        for (final Player player : plug.getServer().getOnlinePlayers()) {
                            final String name = player.getName();
                            if (!xtracker.hasAlerted(name) && xtracker.sufficientData(name) && xtracker.hasAbnormal(name)) {
                                final List<String> alert = new ArrayList<String>();
                                alert.add(ChatColor.YELLOW + "[ALERT] " + ChatColor.WHITE + name + ChatColor.YELLOW + " might be using xray.");
                                alert.add(ChatColor.YELLOW + "[ALERT] Please check their xray stats using " + ChatColor.WHITE + "/anticheat xray " + name + ChatColor.YELLOW + ".");
                                Utilities.alert(alert);
                                xtracker.logAlert(name);
                            }
                        }
                    }
                }, (long)time, (long)time);
            }
        }
    }
    
    private void setupEvents() {
        for (final Listener listener : AntiCheat.eventList) {
            plug.getServer().getPluginManager().registerEvents(listener, plug);
        }
    }
    
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		return commandHandler.onCommand(sender, cmd, label, args);
    }
    
    private void setupCommands() {
        /*plug.getCommand("anticheat").setExecutor((CommandExecutor)(commandHandler = new CommandHandler()));*/
    	commandHandler = new CommandHandler();
    }
    
    private void restoreLevels() {
        for (final Player player : plug.getServer().getOnlinePlayers()) {
            final String name = player.getName();
            final User user = new User(name);
            user.setIsWaitingOnLevelSync(true);
            loadLevelToUser(user);
            AntiCheat.manager.getUserManager().addUser(user);
        }
    }
    
    public static AntiCheatManager getManager() {
        return AntiCheat.manager;
    }
    
    private void cleanup() {
        AntiCheat.eventList = null;
        AntiCheat.manager = null;
    }
    
    public static boolean developerMode() {
        return AntiCheat.developer;
    }
    
    public static void setDeveloperMode(final boolean b) {
        AntiCheat.developer = b;
    }
    
    public static void debugLog(final String string) {
        Bukkit.getScheduler().runTask(me.corperateraider.myworld.Plugin.instance, (Runnable)new Runnable() {
            @Override
            public void run() {
                if (AntiCheat.developer) {
                    AntiCheat.manager.debugLog("[DEBUG] " + string);
                }
            }
        });
    }
    
    public void verboseLog(final String string) {
    	System.out.println("[AC+] "+string);
    }
    
    public Long getLoadTime() {
        return AntiCheat.loadTime;
    }
    
private static final String file = "data/crimes.sec";
	
	private static HashMap<String, User> toSave = new HashMap<>();	
	
	public static void load(File folder) throws IOException{
		File f = new File(folder, file);
		if(f.exists()){
			BufferedReader reader = new BufferedReader(new FileReader(f));
			
			int level=0;
			for(String s = reader.readLine(); s!=null; s=reader.readLine()){
				if(s.startsWith("#")){
					level = Integer.parseInt(s.substring(1));
				} else {
					User u = new User(s);
					u.setLevel(level);
					toSave.put(s, u);
				}
			}
			
			reader.close();
		}
	}
	// from Levels :)
	public static void save(File folder) throws IOException{
		int max = Group.getHighestLevel();
		ArrayList<ArrayList<String>> users = new ArrayList<ArrayList<String>>();
		for(int i=0;i<=max;i++){
			users.add(new ArrayList<String>());
		}
		for(User u:AntiCheat.getManager().getUserManager().getUsers()){
			int level = u.getLevel();
			if(level<0 || level>max){
				System.out.println("Illegal user: "+u.getName()+" with "+level+" levels");
				users.get(level<0?0:max).add(u.getName());
			} else {
				users.get(level).add(u.getName());
			}
		}
		
		FileWriter write = new FileWriter(new File(folder, file));
		for(int i=1;i<=max;i++){
			ArrayList<String> list = users.get(i);
			if(list.size()>0){
				write.write("#"+i+"\n");
				for(String s:list){
					write.write(s+"\n");
				}
			}
		}
		write.flush();
		write.close();
	}
	
    public static void loadLevelToUser(User u) {
    	if(toSave.containsKey(u.getName())){
    		u.setLevel(toSave.get(u.getName()).getLevel());
    	}
	}
    
    public static void saveLevelFromUser(User u) {
    	toSave.put(u.getName(), u);
	}
    
    public static void saveLevelsFromUsers(List<User> us) {
    	for(User u:us){
    		saveLevelFromUser(u);
    	}
	}
}
