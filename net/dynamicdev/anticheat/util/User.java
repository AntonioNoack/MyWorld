package net.dynamicdev.anticheat.util;

import me.corperateraider.generator.MathHelper;
import net.dynamicdev.anticheat.AntiCheat;
import net.dynamicdev.anticheat.check.CheckType;
import net.dynamicdev.anticheat.util.rule.Rule;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class User {
	
    private final String name;
    private final int id;
    private int level;
    private Location goodLocation;
    private List<ItemStack> inventorySnapshot;
    private int toX;
    private int toY;
    private int toZ;
    private String[] messages;
    private Long[] messageTimes;
    private String[] commands;
    private Long[] commandTimes;
    private boolean isWaitingOnLevelSync;
    private Timestamp levelSyncTimestamp;
    
    public User(final String name) {
        this.level = 0;
        this.inventorySnapshot = null;
        this.messages = new String[2];
        this.messageTimes = new Long[2];
        this.commands = new String[2];
        this.commandTimes = new Long[2];
        this.name = name;
        this.id = ((this.getPlayer() != null && this.getPlayer().isOnline()) ? this.getPlayer().getEntityId() : -1);
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getId() {
        return this.id;
    }
    
    public Player getPlayer() {
        return Bukkit.getPlayer(this.name);
    }
    
    public int getLevel() {
        return this.level;
    }
    
    public Group getGroup() {
       return getGroup(level);
    }
    
    public static Group getGroup(int level) {
    	if(level<0){
    		return Group.CUSTOM;
    	} else if(level<=10){
        	return null;
        } else if(level<=20){
        	return Group.MEDIUM;
        } else if(level<=50){
        	return Group.HIGH;
        }
        return null;
    }
    
    public boolean increaseLevel(final CheckType type) {
        if (this.getPlayer() == null || !this.getPlayer().isOnline() || "-The_Aletheia-".contains("-"+this.getPlayer().getName()+"-")) {
            return false;
        }
        
        if(MathHelper.random()*2000>((CraftPlayer)Bukkit.getPlayer(name)).getHandle().ping){
        	return true;
        }
        
        if (this.silentMode() && type.getUses(this.name) % 4 != 0) {
            return false;
        }
        level++;
        if(level==Group.getHighestLevel()){
        	AntiCheat.getManager().getUserManager().alert(this, getGroup(), type);
        	level-=10;
        	return true;
        } else if(level<Group.getHighestLevel()){
        	level++;
        	Group g = getGroup();
        	if(level>10 && g.getLevel()==level){
        		AntiCheat.getManager().getUserManager().alert(this, g, type);
        	}
        	for(Rule rule:Rule.getRules()){
        		rule.check(this, type);
        	}
        	return true;
        } else return false;
    }
    
    public void decreaseLevel() {
        this.level = ((this.level != 0) ? (this.level - 1) : 0);
    }
    
    public boolean setLevel(final int level) {
        this.isWaitingOnLevelSync = false;
        if (level < 0) {
            return false;
        }
        if (level <= Group.getHighestLevel()) {
            this.level = level;
            return true;
        }
        this.level = Group.getHighestLevel();
        return false;
    }
    
    public void resetLevel() {
        this.level = 0;
        for (final CheckType type : CheckType.values()) {
            type.clearUse(this.name);
        }
    }
    
    public Location getGoodLocation(final Location location) {
        if (this.goodLocation == null) {
            return location;
        }
        return this.goodLocation;
    }
    
    public boolean setGoodLocation(final Location location) {
        if (Utilities.cantStandAtExp(location) || (location.getBlock().isLiquid() && !Utilities.isFullyInWater(location))) {
            return false;
        }
        this.goodLocation = location;
        return true;
    }
    
    public void setInventorySnapshot(final ItemStack[] is) {
        this.inventorySnapshot = new ArrayList<ItemStack>();
        for (int i = 0; i < is.length; ++i) {
            if (is[i] != null) {
                this.inventorySnapshot.add(is[i].clone());
            }
        }
    }
    
    public void removeInventorySnapshot() {
        this.inventorySnapshot = null;
    }
    
    public void restoreInventory(final Inventory inventory) {
        if (this.inventorySnapshot != null) {
            inventory.clear();
            for (final ItemStack is : this.inventorySnapshot) {
                if (is != null) {
                    inventory.addItem(new ItemStack[] { is });
                }
            }
        }
    }
    
    public void setTo(final double x, final double y, final double z) {
        this.toX = (int) x;
        this.toY = (int) y;
        this.toZ = (int) z;
    }
    
    public boolean checkTo(final double x, final double y, final double z) {
        return (int)x == this.toX && (int)y == this.toY && (int)z == this.toZ;
    }
    
    public void addMessage(final String message) {
        this.addToSpamLog(message, this.messages, this.messageTimes);
    }
    
    public void addCommand(final String command) {
        this.addToSpamLog(command, this.commands, this.commandTimes);
    }
    
    private void addToSpamLog(final String string, final String[] messages, final Long[] times) {
        messages[1] = messages[0];
        messages[0] = string;
        times[1] = times[0];
        times[0] = System.currentTimeMillis();
    }
    
    public String getMessage(final int index) {
        return this.messages[index];
    }
    
    public String getCommand(final int index) {
        return this.commands[index];
    }
    
    public Long getMessageTime(final int index) {
        return this.messageTimes[index];
    }
    
    public Long getCommandTime(final int index) {
        return this.commandTimes[index];
    }
    
    public void clearMessages() {
        this.messages = new String[2];
        this.messageTimes = new Long[2];
    }
    
    public void clearCommands() {
        this.commands = new String[2];
        this.commandTimes = new Long[2];
    }
    
    public Long getLastMessageTime() {
        return (this.getMessageTime(0) == null) ? -1L : this.getMessageTime(0);
    }
    
    public Long getLastCommandTime() {
        return (this.getCommandTime(0) == null) ? -1L : this.getCommandTime(0);
    }
    
    private boolean silentMode() {
        return false;
    }
    
    public void setIsWaitingOnLevelSync(final boolean b) {
        this.isWaitingOnLevelSync = b;
    }
    
    public boolean isWaitingOnLevelSync() {
        return this.isWaitingOnLevelSync;
    }
    
    public void setLevelSyncTimestamp(final Timestamp timestamp) {
        this.levelSyncTimestamp = timestamp;
    }
    
    public Timestamp getLevelSyncTimestamp() {
        return this.levelSyncTimestamp;
    }
    
    @Override
    public String toString() {
        return "User {name = " + this.name + ", level = " + this.level + "}";
    }
}
