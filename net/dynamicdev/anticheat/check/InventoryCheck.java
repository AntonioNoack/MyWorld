package net.dynamicdev.anticheat.check;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import net.dynamicdev.anticheat.config.providers.Magic;
import net.dynamicdev.anticheat.manage.AntiCheatManager;

public class InventoryCheck extends AntiCheatCheck {
	
    private Map<String, Integer> blocksDropped;
    private Map<String, Long> inventoryTime;
    private Map<String, Integer> inventoryClicks;
    private Map<String, Long> blockTime;
    
    public InventoryCheck(final AntiCheatManager instance) {
        super(instance);
        this.blocksDropped = new HashMap<String, Integer>();
        this.inventoryTime = new HashMap<String, Long>();
        this.inventoryClicks = new HashMap<String, Integer>();
        this.blockTime = new HashMap<String, Long>();
    }
    
    public CheckResult checkFastDrop(final Player player) {
        this.increment(player, this.blocksDropped, 10);
        if (!this.blockTime.containsKey(player.getName())) {
            this.blockTime.put(player.getName(), System.currentTimeMillis());
            return new CheckResult(CheckResult.Result.PASSED);
        }
        if (this.blocksDropped.get(player.getName()) == Magic.DROP_CHECK) {
            final long time = System.currentTimeMillis() - this.blockTime.get(player.getName());
            this.blockTime.remove(player.getName());
            this.blocksDropped.remove(player.getName());
            if (time < Magic.DROP_TIME_MIN) {
                return new CheckResult(CheckResult.Result.FAILED, player.getName() + " dropped an item too fast (actual time=" + time + ", min time=" + Magic.DROP_TIME_MIN + ")");
            }
        }
        return InventoryCheck.PASS;
    }
    
    public CheckResult checkInventoryClicks(final Player player) {
        if (player.getGameMode() == GameMode.CREATIVE) {
            return InventoryCheck.PASS;
        }
        final String name = player.getName();
        int clicks = 1;
        if (this.inventoryClicks.containsKey(name)) {
            clicks = this.inventoryClicks.get(name) + 1;
        }
        this.inventoryClicks.put(name, clicks);
        if (clicks == 1) {
            this.inventoryTime.put(name, System.currentTimeMillis());
        }
        else if (clicks == Magic.INVENTORY_CHECK) {
            final long time = System.currentTimeMillis() - this.inventoryTime.get(name);
            this.inventoryClicks.put(name, 0);
            if (time < Magic.INVENTORY_TIMEMIN) {
                return new CheckResult(CheckResult.Result.FAILED, player.getName() + " clicked inventory slots " + clicks + " times in " + time + " ms (max=" + Magic.INVENTORY_CHECK + " in " + Magic.INVENTORY_TIMEMIN + " ms)");
            }
        }
        return InventoryCheck.PASS;
    }
}
