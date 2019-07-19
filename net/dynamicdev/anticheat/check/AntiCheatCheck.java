package net.dynamicdev.anticheat.check;

import java.util.Map;

import net.dynamicdev.anticheat.manage.AntiCheatManager;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AntiCheatCheck {
	
    protected AntiCheatManager manager;
    protected static final CheckResult PASS;
    protected static final String DEPTH_STRIDER_ENCHANT = "DEPTH_STRIDER";
    
    public AntiCheatCheck(final AntiCheatManager instance) {
        this.manager = null;
        this.manager = instance;
    }
    
    protected boolean isDoing(final Player player, final Map<String, Long> map, final double max) {
        if (!map.containsKey(player.getName())) {
            return false;
        }
        if (max != -1.0) {
            if ((System.currentTimeMillis() - map.get(player.getName())) / 1000L > max) {
                map.remove(player.getName());
                return false;
            }
            return true;
        }
        else {
            if (map.get(player.getName()) < System.currentTimeMillis()) {
                map.remove(player.getName());
                return false;
            }
            return true;
        }
    }
    
    public int increment(final Player player, final Map<String, Integer> map, final int num) {
        final String name = player.getName();
        if (map.get(name) == null) {
            map.put(name, 1);
            return 1;
        }
        final int amount = map.get(name) + 1;
        if (amount < num + 1) {
            map.put(name, amount);
            return amount;
        }
        map.put(name, num);
        return num;
    }
    
    public boolean silentMode() {
        return false;
    }
    
    //!!
    public void sendFormattedMessage(final Player player, final String message) {
        player.sendMessage(ChatColor.RED + "[AntiCheat+] §2[Srry if u're no  hacker... Still in test :)]§4" + message);
    }
    
    static {
        PASS = new CheckResult(CheckResult.Result.PASSED);
    }
}
