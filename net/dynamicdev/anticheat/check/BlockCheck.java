package net.dynamicdev.anticheat.check;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import net.dynamicdev.anticheat.AntiCheat;
import net.dynamicdev.anticheat.manage.AntiCheatManager;
import net.dynamicdev.anticheat.util.Utilities;
import net.dynamicdev.anticheat.config.providers.Magic;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.util.Vector;

public class BlockCheck extends AntiCheatCheck {
    private Map<String, Integer> fastBreakViolation;
    private Map<String, Integer> fastBreaks;
    private Map<String, Boolean> blockBreakHolder;
    private Map<String, Long> lastBlockBroken;
    private Map<String, Integer> fastPlaceViolation;
    private Map<String, Long> lastBlockPlaced;
    private Map<String, Long> lastBlockPlaceTime;
    private Map<String, Long> instantBreakExempt;
    private Map<String, Material> itemInHand;
    private HashSet<Byte> transparent;
    private Map<String, Long> brokenBlock;
    private Map<String, Long> placedBlock;
    
    public BlockCheck(final AntiCheatManager instance) {
        super(instance);
        this.fastBreakViolation = new HashMap<String, Integer>();
        this.fastBreaks = new HashMap<String, Integer>();
        this.blockBreakHolder = new HashMap<String, Boolean>();
        this.lastBlockBroken = new HashMap<String, Long>();
        this.fastPlaceViolation = new HashMap<String, Integer>();
        this.lastBlockPlaced = new HashMap<String, Long>();
        this.lastBlockPlaceTime = new HashMap<String, Long>();
        this.instantBreakExempt = new HashMap<String, Long>();
        this.itemInHand = new HashMap<String, Material>();
        this.transparent = new HashSet<Byte>();
        this.brokenBlock = new HashMap<String, Long>();
        this.placedBlock = new HashMap<String, Long>();
        this.transparent.add((byte)(-1));
    }
    
    public boolean justPlaced(final Player player) {
        return this.isDoing(player, this.placedBlock, Magic.BLOCK_PLACE_MIN);
    }
    
    public void logBlockPlace(final Player player) {
        this.placedBlock.put(player.getName(), System.currentTimeMillis());
    }
    
    public void logInstantBreak(final Player player) {
        this.instantBreakExempt.put(player.getName(), System.currentTimeMillis());
    }
    
    public boolean isInstantBreakExempt(final Player player) {
        return this.isDoing(player, this.instantBreakExempt, Magic.INSTANT_BREAK_TIME);
    }
    
    public void logBlockBreak(final Player player) {
        this.brokenBlock.put(player.getName(), System.currentTimeMillis());
        AntiCheat.getManager().getBackend().resetAnimation(player);
    }
    
    public boolean justBroke(final Player player) {
        return this.isDoing(player, this.brokenBlock, Magic.BLOCK_BREAK_MIN);
    }
    
    public void logAnimation(final Player player) {
        AntiCheat.getManager().getBackend().animated.put(player.getName(), System.currentTimeMillis());
        this.increment(player, AntiCheat.getManager().getBackend().blockPunches, Magic.BLOCK_PUNCH_MIN);
        this.itemInHand.put(player.getName(), player.getItemInHand().getType());
        AntiCheat.getManager().getBackend().interactionCount.put(player.getName(), 0);
    }
    
    public CheckResult checkLongReachBlock(final Player player, final double x, final double y, final double z) {
        if (this.isInstantBreakExempt(player)) {
            return new CheckResult(CheckResult.Result.PASSED);
        }
        final String string = player.getName() + " reached too far for a block";
        final double distance = (player.getGameMode() == GameMode.CREATIVE) ? Magic.BLOCK_MAX_DISTANCE_CREATIVE : ((player.getLocation().getDirection().getY() > 0.9) ? Magic.BLOCK_MAX_DISTANCE_CREATIVE : Magic.BLOCK_MAX_DISTANCE);
        final double i = (x >= distance) ? x : ((y > distance) ? y : ((z > distance) ? z : -1.0));
        if (i != -1.0) {
            return new CheckResult(CheckResult.Result.FAILED, string + " (distance=" + i + ", max=" + Magic.BLOCK_MAX_DISTANCE + ")");
        }
        return BlockCheck.PASS;
    }
    
    public CheckResult checkSwing(final Player player, final Block block) {
        final String name = player.getName();
        if (!this.isInstantBreakExempt(player) && !player.getInventory().getItemInHand().containsEnchantment(Enchantment.DIG_SPEED) && (player.getInventory().getItemInHand().getType() != Material.SHEARS || block.getType() != Material.LEAVES) && AntiCheat.getManager().getBackend().blockPunches.get(name) != null && player.getGameMode() != GameMode.CREATIVE) {
            final int i = AntiCheat.getManager().getBackend().blockPunches.get(name);
            if (i < Magic.BLOCK_PUNCH_MIN) {
                return new CheckResult(CheckResult.Result.FAILED, player.getName() + " tried to break a block of " + block.getType() + " after only " + i + " punches (min=" + Magic.BLOCK_PUNCH_MIN + ")");
            }
            AntiCheat.getManager().getBackend().blockPunches.put(name, 0);
        }
        return BlockCheck.PASS;
    }
    
    public CheckResult checkFastBreak(final Player player, final Block block) {
        int violations = Magic.FASTBREAK_MAXVIOLATIONS;
        long timemax = this.isInstantBreakExempt(player) ? 0L : Utilities.calcSurvivalFastBreak(player.getInventory().getItemInHand(), block.getType());
        if (player.getGameMode() == GameMode.CREATIVE) {
            violations = Magic.FASTBREAK_MAXVIOLATIONS_CREATIVE;
            timemax = Magic.FASTBREAK_TIMEMAX_CREATIVE;
        }
        final String name = player.getName();
        if (!this.fastBreakViolation.containsKey(name)) {
            this.fastBreakViolation.put(name, 0);
        }
        else {
            final Long math = System.currentTimeMillis() - this.lastBlockBroken.get(name);
            final int i = this.fastBreakViolation.get(name);
            if (i > violations && math < Magic.FASTBREAK_MAXVIOLATIONTIME) {
                this.lastBlockBroken.put(name, System.currentTimeMillis());
                if (!this.silentMode()) {
                    this.sendFormattedMessage(player, "Fastbreaking detected. Please wait 10 seconds before breaking blocks.");
                }
                return new CheckResult(CheckResult.Result.FAILED, player.getName() + " broke blocks too fast " + i + " times in a row (max=" + violations + ")");
            }
            if (this.fastBreakViolation.get(name) > 0 && math > Magic.FASTBREAK_MAXVIOLATIONTIME) {
                this.fastBreakViolation.put(name, 0);
            }
        }
        if (!this.fastBreaks.containsKey(name) || !this.lastBlockBroken.containsKey(name)) {
            if (!this.lastBlockBroken.containsKey(name)) {
                this.lastBlockBroken.put(name, System.currentTimeMillis());
            }
            if (!this.fastBreaks.containsKey(name)) {
                this.fastBreaks.put(name, 0);
            }
        }
        else {
            final Long math = System.currentTimeMillis() - this.lastBlockBroken.get(name);
            if (math != 0L && timemax != 0L) {
                if (math < timemax) {
                    if (this.fastBreakViolation.containsKey(name) && this.fastBreakViolation.get(name) > 0) {
                        this.fastBreakViolation.put(name, this.fastBreakViolation.get(name) + 1);
                    }
                    else {
                        this.fastBreaks.put(name, this.fastBreaks.get(name) + 1);
                    }
                    this.blockBreakHolder.put(name, false);
                }
                if (this.fastBreaks.get(name) >= Magic.FASTBREAK_LIMIT && math < timemax) {
                    final int i = this.fastBreaks.get(name);
                    this.fastBreaks.put(name, 0);
                    this.fastBreakViolation.put(name, this.fastBreakViolation.get(name) + 1);
                    return new CheckResult(CheckResult.Result.FAILED, player.getName() + " tried to break " + i + " blocks in " + math + " ms (max=" + Magic.FASTBREAK_LIMIT + " in " + timemax + " ms)");
                }
                if (this.fastBreaks.get(name) >= Magic.FASTBREAK_LIMIT || this.fastBreakViolation.get(name) > 0) {
                    if (!this.blockBreakHolder.containsKey(name) || !this.blockBreakHolder.get(name)) {
                        this.blockBreakHolder.put(name, true);
                    }
                    else {
                        this.fastBreaks.put(name, this.fastBreaks.get(name) - 1);
                        if (this.fastBreakViolation.get(name) > 0) {
                            this.fastBreakViolation.put(name, this.fastBreakViolation.get(name) - 1);
                        }
                        this.blockBreakHolder.put(name, false);
                    }
                }
            }
        }
        this.lastBlockBroken.put(name, System.currentTimeMillis());
        return BlockCheck.PASS;
    }
    
    public CheckResult checkFastPlace(final Player player) {
        final int violations = (player.getGameMode() == GameMode.CREATIVE) ? Magic.FASTPLACE_MAXVIOLATIONS_CREATIVE : Magic.FASTPLACE_MAXVIOLATIONS;
        final long time = System.currentTimeMillis();
        final String name = player.getName();
        if (!this.lastBlockPlaceTime.containsKey(name) || !this.fastPlaceViolation.containsKey(name)) {
            this.lastBlockPlaceTime.put(name, 0L);
            if (!this.fastPlaceViolation.containsKey(name)) {
                this.fastPlaceViolation.put(name, 0);
            }
        }
        else if (this.fastPlaceViolation.containsKey(name) && this.fastPlaceViolation.get(name) > violations) {
            AntiCheat.debugLog("Noted that fastPlaceViolation contains key " + name + " with value " + this.fastPlaceViolation.get(name));
            final Long math = System.currentTimeMillis() - this.lastBlockPlaced.get(name);
            AntiCheat.debugLog("Player lastBlockPlaced value = " + this.lastBlockPlaced + ", diff=" + math);
            final double multiplier = 0.75;
            if (this.lastBlockPlaced.get(name) > 0L && math < Magic.FASTPLACE_MAXVIOLATIONTIME * multiplier) {
                this.lastBlockPlaced.put(name, time);
                if (!this.silentMode()) {
                    this.sendFormattedMessage(player, "Fastplacing detected. Please wait 10 seconds before placing blocks.");
                }
                return new CheckResult(CheckResult.Result.FAILED, player.getName() + " placed blocks too fast " + this.fastBreakViolation.get(name) + " times in a row (max=" + violations + ")");
            }
            if (this.lastBlockPlaced.get(name) > 0L && math > Magic.FASTPLACE_MAXVIOLATIONTIME) {
                AntiCheat.debugLog("Reset facePlaceViolation for " + name);
                this.fastPlaceViolation.put(name, 0);
            }
        }
        else if (this.lastBlockPlaced.containsKey(name)) {
            final long last = this.lastBlockPlaced.get(name);
            final long lastTime = this.lastBlockPlaceTime.get(name);
            final long thisTime = time - last;
            if (lastTime != 0L && thisTime < Magic.FASTPLACE_TIMEMIN) {
                this.lastBlockPlaceTime.put(name, time - last);
                this.lastBlockPlaced.put(name, time);
                this.fastPlaceViolation.put(name, this.fastPlaceViolation.get(name) + 1);
                return new CheckResult(CheckResult.Result.FAILED, player.getName() + " tried to place a block " + thisTime + " ms after the last one (min=" + Magic.FASTPLACE_TIMEMIN + " ms)");
            }
            this.lastBlockPlaceTime.put(name, time - last);
        }
        this.lastBlockPlaced.put(name, time);
        return BlockCheck.PASS;
    }
    
    public CheckResult checkAutoTool(final Player player) {
        if (this.itemInHand.containsKey(player.getName()) && this.itemInHand.get(player.getName()) != player.getItemInHand().getType()) {
            return new CheckResult(CheckResult.Result.FAILED, player.getName() + " switched tools too fast (had " + this.itemInHand.get(player.getName()) + ", has " + player.getItemInHand().getType() + ")");
        }
        return BlockCheck.PASS;
    }
    
    public CheckResult checkBlockRotation(final Player player, final BlockBreakEvent event) {
        double offset = 0.0;
        final Location blockLoc = event.getBlock().getLocation().add(0.5, 0.6, 0.5);
        final Location playerLoc = player.getLocation().add(0.0, player.getEyeHeight(), 0.0);
        final Vector playerRotation = new Vector(playerLoc.getYaw(), playerLoc.getPitch(), 0.0f);
        final Vector expectedRotation = Utilities.getRotation(playerLoc, blockLoc);
        final double deltaYaw = Utilities.clamp180(playerRotation.getX() - expectedRotation.getX());
        final double deltaPitch = Utilities.clamp180(playerRotation.getY() - expectedRotation.getY());
        final double horizontalDistance = Utilities.getHorizontalDistance(playerLoc, blockLoc);
        final double distance = Utilities.getDistance3D(playerLoc, blockLoc);
        final double offsetX = deltaYaw * horizontalDistance * distance;
        final double offsetY = deltaPitch * Math.abs(blockLoc.getY() - playerLoc.getY()) * distance;
        offset += Math.abs(offsetX);
        offset += Math.abs(offsetY);
        if (offset > Magic.DIRECTION_MAX_BUFFER) {
            return BlockCheck.PASS;
        }
        return BlockCheck.PASS;
    }
}
