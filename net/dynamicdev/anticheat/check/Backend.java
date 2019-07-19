package net.dynamicdev.anticheat.check;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import net.dynamicdev.anticheat.config.providers.Magic;
import net.dynamicdev.anticheat.manage.AntiCheatManager;

public class Backend {
    public Map<String, Long> animated;
    public Map<String, Integer> interactionCount;
    public Map<String, Integer> blockPunches;
    private MovementCheck movementCheck;
    private BlockCheck blockCheck;
    private ChatCheck chatCheck;
    private EntityCheck entityCheck;
    private InventoryCheck inventoryCheck;
    
    public Backend(final AntiCheatManager instance) {
        this.animated = new HashMap<String, Long>();
        this.interactionCount = new HashMap<String, Integer>();
        this.blockPunches = new HashMap<String, Integer>();
        this.movementCheck = new MovementCheck(instance);
        this.blockCheck = new BlockCheck(instance);
        this.chatCheck = new ChatCheck(instance);
        this.entityCheck = new EntityCheck(instance);
        this.inventoryCheck = new InventoryCheck(instance);
    }
    
    public MovementCheck getMovementCheck() {
        return this.movementCheck;
    }
    
    public BlockCheck getBlockCheck() {
        return this.blockCheck;
    }
    
    public EntityCheck getEntityCheck() {
        return this.entityCheck;
    }
    
    public ChatCheck getChatCheck() {
        return this.chatCheck;
    }
    
    public InventoryCheck getInventoryCheck() {
        return this.inventoryCheck;
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
    
    public void resetAnimation(final Player player) {
        this.animated.remove(player.getName());
        this.blockPunches.put(player.getName(), 0);
    }
    
    public boolean justAnimated(final Player player) {
        final String name = player.getName();
        if (!this.animated.containsKey(name)) {
            return false;
        }
        final long time = System.currentTimeMillis() - this.animated.get(name);
        final int count = this.interactionCount.get(player.getName()) + 1;
        this.interactionCount.put(player.getName(), count);
        if (count > Magic.ANIMATION_INTERACT_MAX) {
            this.animated.remove(player.getName());
            return false;
        }
        return time < Magic.ANIMATION_MIN;
    }
    
    public void logDamage(final Player player, final int type) {
        long time = 0L;
        switch (type) {
            case 1: {
                time = Magic.DAMAGE_TIME;
                break;
            }
            case 2: {
                time = Magic.KNOCKBACK_DAMAGE_TIME;
                break;
            }
            case 3: {
                time = Magic.EXPLOSION_DAMAGE_TIME;
                break;
            }
            default: {
                time = Magic.DAMAGE_TIME;
                break;
            }
        }
        this.movementCheck.getMovingExempt().put(player.getName(), System.currentTimeMillis() + time);
    }
    
    public void logEnterExit(final Player player) {
        this.movementCheck.getMovingExempt().put(player.getName(), System.currentTimeMillis() + Magic.ENTERED_EXITED_TIME);
    }
    
    public void logToggleSneak(final Player player) {
        this.movementCheck.getSneakExempt().put(player.getName(), System.currentTimeMillis() + Magic.SNEAK_TIME);
    }
    
    public void logTeleport(final Player player) {
        this.movementCheck.getMovingExempt().put(player.getName(), System.currentTimeMillis() + Magic.TELEPORT_TIME);
        this.movementCheck.logTeleport(player);
    }
    
    public void logExitFly(final Player player) {
        this.movementCheck.getMovingExempt().put(player.getName(), System.currentTimeMillis() + Magic.EXIT_FLY_TIME);
    }
    
    public void logJoin(final Player player) {
        this.movementCheck.getMovingExempt().put(player.getName(), System.currentTimeMillis() + Magic.JOIN_TIME);
        if (player.getLocation().getBlock().getType() == Material.AIR) {
            Location setLocation = player.getLocation();
            final int x = player.getLocation().getBlockX();
            final int z = player.getLocation().getBlockZ();
            for (int y = player.getLocation().getBlockY(); y > 0; --y) {
                if (player.getWorld().getBlockAt(x, y, z).getType().isSolid()) {
                    setLocation = new Location(player.getWorld(), (double)x, (double)(y + 1), (double)z);
                    break;
                }
            }
            player.teleport(setLocation);
        }
        else if (player.getEyeLocation().getBlock().getType().isSolid()) {
            player.teleport(player.getWorld().getHighestBlockAt(player.getLocation()).getLocation());
        }
    }
    
    public boolean isMovingExempt(final Player player) {
        return this.isDoing(player, this.movementCheck.getMovingExempt(), -1.0);
    }
    
    public boolean isSneakExempt(final Player player) {
        return this.isDoing(player, this.movementCheck.getSneakExempt(), -1.0);
    }
    
    public boolean isSpeedExempt(final Player player) {
        return this.isMovingExempt(player) || this.movementCheck.justVelocity(player);
    }
}
