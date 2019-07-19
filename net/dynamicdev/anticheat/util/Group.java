package net.dynamicdev.anticheat.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;

public class Group {
	
	public static ArrayList<Group> groups = new ArrayList<>();
	
	public static Group
		MEDIUM = new Group("MEDIUM", 30, ChatColor.YELLOW.toString(),	new String[]{"WARN"}),
		HIGH = new Group("HIGH", 100, ChatColor.RED.toString(),			new String[]{"KICK"}),
		CUSTOM = new Group("CUSTOM", -1, ChatColor.GOLD.toString(),		new String[]{"COMMAND[ban &player;say hello world]"});
	
	public static List<Group> getGroups(){
		return groups;
	}

	public static int getHighestLevel() {
		return 100;
	}
	
    private String name;
    private int level;
    private ChatColor color;
    private String[] actions;
    
    public Group(final String name, final int level, final String color, final String[] actions) {
        this.color = ChatColor.RED;
        this.name = name;
        this.level = level;
        this.actions = actions;
        groups.add(this);
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public ChatColor getColor() {
        return this.color;
    }
    
    public String[] getActions() {
        return this.actions;
    }
    
    @Override
    public String toString() {
        return this.name + " : " + this.level + " : " + this.color.name() + " : " + Utilities.arrayToCommaString(this.actions);
    }
}
