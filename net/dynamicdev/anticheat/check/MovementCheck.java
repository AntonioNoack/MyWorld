package net.dynamicdev.anticheat.check;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.potion.PotionEffectType;

import net.dynamicdev.anticheat.AntiCheat;
import net.dynamicdev.anticheat.config.providers.Magic;
import net.dynamicdev.anticheat.manage.AntiCheatManager;
import net.dynamicdev.anticheat.util.Distance;
import net.dynamicdev.anticheat.util.SimpleLocation;
import net.dynamicdev.anticheat.util.TimedLocation;
import net.dynamicdev.anticheat.util.Utilities;

public class MovementCheck extends AntiCheatCheck {
    private List<String> isInWater;
    private List<String> isInWaterCache;
    private List<String> isAscending;
    private Map<String, Integer> ascensionCount;
    private Map<String, Double> blocksOverFlight;
    private Map<String, Integer> nofallViolation;
    private Map<String, Integer> speedViolation;
    private Map<String, Integer> yAxisViolations;
    private Map<String, Long> yAxisLastViolation;
    private Map<String, Double> lastYcoord;
    private Map<String, Long> lastYtime;
    private Map<String, Integer> waterAscensionViolation;
    private Map<String, Integer> waterSpeedViolation;
    private Map<String, Long> velocitized;
    private Map<String, Long> stepTime;
    private Map<String, Integer> hoverTicks;
    private Map<String, Integer> velocityFail;
    private Map<String, TimedLocation> timedLoc;
    private Map<String, Integer> verticalCount;
    private Map<String, Boolean> canMoveVert;
    private Map<String, Long> timeInWater;
    private Map<String, Integer> velocitytrack;
    private Map<String, Long> movingExempt;
    private Map<String, Long> sneakExempt;
    private Map<String, Integer> timerBuffer;
    private Map<String, Integer> glideBuffer;
    private Map<String, Double> lastYDelta;
    private Map<String, SimpleLocation> lastTickLocation;
    
    public MovementCheck(final AntiCheatManager instance) {
        super(instance);
        this.isInWater = new ArrayList<String>();
        this.isInWaterCache = new ArrayList<String>();
        this.isAscending = new ArrayList<String>();
        this.ascensionCount = new HashMap<String, Integer>();
        this.blocksOverFlight = new HashMap<String, Double>();
        this.nofallViolation = new HashMap<String, Integer>();
        this.speedViolation = new HashMap<String, Integer>();
        this.yAxisViolations = new HashMap<String, Integer>();
        this.yAxisLastViolation = new HashMap<String, Long>();
        this.lastYcoord = new HashMap<String, Double>();
        this.lastYtime = new HashMap<String, Long>();
        this.waterAscensionViolation = new HashMap<String, Integer>();
        this.waterSpeedViolation = new HashMap<String, Integer>();
        this.velocitized = new HashMap<String, Long>();
        new HashMap<String, Integer>();
        this.stepTime = new HashMap<String, Long>();
        new HashMap<String, Long>();
        this.hoverTicks = new HashMap<String, Integer>();
        this.velocityFail = new HashMap<String, Integer>();
        this.timedLoc = new HashMap<String, TimedLocation>();
        this.verticalCount = new HashMap<String, Integer>();
        this.canMoveVert = new HashMap<String, Boolean>();
        this.timeInWater = new HashMap<String, Long>();
        this.velocitytrack = new HashMap<String, Integer>();
        this.movingExempt = new HashMap<String, Long>();
        this.sneakExempt = new HashMap<String, Long>();
        this.timerBuffer = new HashMap<String, Integer>();
        this.glideBuffer = new HashMap<String, Integer>();
        this.lastYDelta = new HashMap<String, Double>();
        this.lastTickLocation = new HashMap<String, SimpleLocation>();
    }
    
    public Map<String, Long> getMovingExempt() {
        return this.movingExempt;
    }
    
    public Map<String, Long> getSneakExempt() {
        return this.sneakExempt;
    }
    
    public boolean hasJumpPotion(final Player player) {
        return player.hasPotionEffect(PotionEffectType.JUMP);
    }
    
    public boolean hasSpeedPotion(final Player player) {
        return player.hasPotionEffect(PotionEffectType.SPEED);
    }
    
    public boolean isAscending(final Player player) {
        return this.isAscending.contains(player.getName());
    }
    
    public void logTeleport(final Player player) {
        this.nofallViolation.remove(player.getName());
        this.blocksOverFlight.remove(player.getName());
        this.yAxisViolations.remove(player.getName());
        this.yAxisLastViolation.remove(player.getName());
        this.lastYcoord.remove(player.getName());
        this.lastYtime.remove(player.getName());
        this.lastTickLocation.put(player.getName(), new SimpleLocation(player.getLocation()));
        this.timedLoc.put(player.getName(), new TimedLocation(player.getLocation(), System.currentTimeMillis()));
    }
    
    public boolean isHoveringOverWaterAfterViolation(final Player player) {
        return this.waterSpeedViolation.containsKey(player.getName()) && this.waterSpeedViolation.get(player.getName()) >= Magic.WATER_SPEED_VIOLATION_MAX && Utilities.isHoveringOverWater(player.getLocation());
    }
    
    public void logVelocity(final Player player) {
        this.velocitized.put(player.getName(), System.currentTimeMillis());
    }
    
    public boolean justVelocity(final Player player) {
        return this.velocitized.containsKey(player.getName()) && System.currentTimeMillis() - this.velocitized.get(player.getName()) < Magic.VELOCITY_CHECKTIME;
    }
    
    public boolean extendVelocityTime(final Player player) {
        if (this.velocitytrack.containsKey(player.getName())) {
            this.velocitytrack.put(player.getName(), this.velocitytrack.get(player.getName()) + 1);
            if (this.velocitytrack.get(player.getName()) > Magic.VELOCITY_MAXTIMES) {
                this.velocitized.put(player.getName(), System.currentTimeMillis() + Magic.VELOCITY_PREVENT);
                me.corperateraider.myworld.Plugin.instance.getServer().getScheduler().scheduleSyncDelayedTask(me.corperateraider.myworld.Plugin.instance, new Runnable() {
                    @Override
                    public void run() {
                        MovementCheck.this.velocitytrack.put(player.getName(), 0);
                    }
                }, Magic.VELOCITY_SCHETIME * 20L);
                return true;
            }
        }
        else {
            this.velocitytrack.put(player.getName(), 0);
        }
        return false;
    }
    
    public CheckResult checkFreeze(final Player player, final double from, final double to) {
        return MovementCheck.PASS;
    }
    
    public CheckResult checkSpider(final Player player, final double y) {
        final String name = player.getName();
        if (!this.verticalCount.containsKey(name)) {
            this.verticalCount.put(name, 0);
        }
        if (y <= Magic.LADDER_Y_MAX && y >= Magic.LADDER_Y_MIN && !Utilities.isClimbableBlock(player.getLocation().getBlock())) {
            this.verticalCount.put(name, this.verticalCount.get(name) + 1);
            if (this.verticalCount.get(name) > Magic.Y_MAXVIOLATIONS) {
                return new CheckResult(CheckResult.Result.FAILED, player.getName() + " tried to climb a non-ladder (" + player.getLocation().getBlock().getType() + ")");
            }
        }
        this.verticalCount.put(name, this.verticalCount.get(name) - 1);
        return MovementCheck.PASS;
    }
    
    public CheckResult checkYSpeed(final Player player, final double y) {
        final double multiPlier = (player.getEyeLocation().getBlock().getType() == Material.WEB) ? Magic.XZ_SPEED_WEB_MULTIPLIER : 1.0;
        if (!AntiCheat.getManager().getBackend().isMovingExempt(player) && !player.isInsideVehicle() && !player.isSleeping() && y > Magic.Y_SPEED_MAX * multiPlier && !this.isDoing(player, this.velocitized, Magic.VELOCITY_TIME) && !player.hasPotionEffect(PotionEffectType.JUMP)) {
            return new CheckResult(CheckResult.Result.FAILED, player.getName() + "'s y speed was too high (speed=" + y + ", max=" + Magic.Y_SPEED_MAX + ")");
        }
        return MovementCheck.PASS;
    }
    
    public CheckResult checkNoFall(final Player player, final double y) {
        final String name = player.getName();
        if (player.getGameMode() == GameMode.CREATIVE || player.isInsideVehicle() || player.isSleeping() || AntiCheat.getManager().getBackend().isMovingExempt(player) || AntiCheat.getManager().getBackend().getBlockCheck().justPlaced(player) || Utilities.isInWater(player) || Utilities.isInWeb(player)) {
            return MovementCheck.PASS;
        }
        if (player.getFallDistance() != 0.0f) {
            this.nofallViolation.put(name, 0);
            return MovementCheck.PASS;
        }
        if (this.nofallViolation.get(name) == null) {
            this.nofallViolation.put(name, 1);
        } else {
            this.nofallViolation.put(name, this.nofallViolation.get(player.getName()) + 1);
        }
        final int i = this.nofallViolation.get(name);
        if (i >= Magic.NOFALL_LIMIT) {
            this.nofallViolation.put(player.getName(), 1);
            return new CheckResult(CheckResult.Result.FAILED, player.getName() + " tried to avoid fall damage (fall distance = 0 " + i + " times in a row, max=" + Magic.NOFALL_LIMIT + ")");
        }
        return MovementCheck.PASS;
    }
    
    public CheckResult checkVelocitized(final Player player, final Distance theDistance) {
        final String name = player.getName();
        if (!this.velocityFail.containsKey(name)) {
            this.velocityFail.put(name, 0);
        }
        if (!AntiCheat.getManager().getBackend().isMovingExempt(player) && player.getVehicle() == null && this.justVelocity(player)) {
            final double multi = player.hasPotionEffect(PotionEffectType.SLOW) ? 0.75 : 1.0;
            if (theDistance.getXDifference() < Magic.VELOCITY_MIN_DISTANCE * multi || theDistance.getZDifference() < Magic.VELOCITY_MIN_DISTANCE * multi) {
                this.velocityFail.put(name, this.velocityFail.get(name) + 1);
                if (this.velocityFail.get(name) > Magic.VELOCITY_DISTANCE_COUNT) {}
            }
            else {
                this.velocityFail.put(name, 0);
            }
        }
        return MovementCheck.PASS;
    }
    
    public CheckResult checkXZSpeed(final Player player, final double x, final double z) {
        if (!this.speedViolation.containsKey(player.getName())) {
            this.speedViolation.put(player.getName(), 1);
        }
        if (!this.lastTickLocation.containsKey(player.getName())) {
            this.lastTickLocation.put(player.getName(), new SimpleLocation(player.getLocation()));
        }
        final SimpleLocation lastLocation = this.lastTickLocation.get(player.getName());
        final SimpleLocation currentLocation = new SimpleLocation(player.getLocation());
        this.lastTickLocation.put(player.getName(), new SimpleLocation(player.getLocation()));
        if (!AntiCheat.getManager().getBackend().isSpeedExempt(player) && player.getVehicle() == null) {
            String reason = "";
            double max = Magic.XZ_SPEED_MAX;
            if (player.getLocation().getBlock().getType() == Material.SOUL_SAND) {
                if (player.isSprinting()) {
                    reason = "on soulsand while sprinting ";
                    max = Magic.XZ_SPEED_MAX_SOULSAND_SPRINT;
                }
                else if (player.hasPotionEffect(PotionEffectType.SPEED)) {
                    reason = "on soulsand with speed potion ";
                    max = Magic.XZ_SPEED_MAX_SOULSAND_POTION;
                }
                else {
                    reason = "on soulsand ";
                    max = Magic.XZ_SPEED_MAX_SOULSAND;
                }
            }
            else if (player.isFlying()) {
                reason = "while flying ";
                max = Magic.XZ_SPEED_MAX_FLY;
            }
            else if (player.hasPotionEffect(PotionEffectType.SPEED)) {
                if (player.isSprinting()) {
                    reason = "with speed potion while sprinting ";
                    max = Magic.XZ_SPEED_MAX_POTION_SPRINT;
                }
                else {
                    reason = "with speed potion ";
                    max = Magic.XZ_SPEED_MAX_POTION;
                }
            }
            else if (player.isSprinting()) {
                reason = "while sprinting ";
                max = Magic.XZ_SPEED_MAX_SPRINT;
            }
            if (!this.timeInWater.containsKey(player.getName())) {
                this.timeInWater.put(player.getName(), System.currentTimeMillis());
            }
            final double multiPerLevel = 1.55;
            final int level = Utilities.getLevelForEnchantment(player, "DEPTH_STRIDER");
            if (level != -1) {
                max *= level * multiPerLevel;
            }
            final float speed = player.getWalkSpeed();
            max += ((speed > 0.0f) ? (player.getWalkSpeed() - 0.2f) : 0.0);
            final boolean isEating = false;
            if (player.getLocation().getBlock().getType() == Material.ICE) {
                max *= Magic.XZ_SPEED_ICE_MULTIPLIER;
            } else if ((Utilities.isInWeb(player) || player.isBlocking() || isEating) && player.getGameMode() != GameMode.CREATIVE && !player.isFlying() && player.getLocation().getBlock().getType() != Material.SOUL_SAND) {
                max *= Magic.XZ_SPEED_WEB_MULTIPLIER;
            }
            if (x > max || z > max || Utilities.getHorizontalDistance(lastLocation, currentLocation) > Magic.XZ_TICK_MAX) {
                final int num = this.increment(player, this.speedViolation, Magic.SPEED_MAX);
                if (num >= Magic.SPEED_MAX) {
                    return new CheckResult(CheckResult.Result.FAILED, player.getName() + "'s speed was too high " + reason + num + " times in a row (max=" + Magic.SPEED_MAX + ", speed=" + ((x > z) ? x : z) + ", max speed=" + max + ")");
                }
            } else if (this.speedViolation.get(player.getName()) > 1) {
                this.speedViolation.put(player.getName(), this.speedViolation.get(player.getName()) - 1);
            }
        }
        return MovementCheck.PASS;
    }
    
    public CheckResult checkSneak(final Player player, final double x, final double z) {
        if (!player.isSneaking() || player.isFlying() || AntiCheat.getManager().getBackend().isSneakExempt(player) || player.isInsideVehicle()) {
            return MovementCheck.PASS;
        }
        final double i = (x > Magic.XZ_SPEED_MAX_SNEAK) ? x : ((z > Magic.XZ_SPEED_MAX_SNEAK) ? z : -1.0);
        if (i != -1.0) {
            return new CheckResult(CheckResult.Result.FAILED, player.getName() + " was sneaking too fast (speed=" + i + ", max=" + Magic.XZ_SPEED_MAX_SNEAK + ")");
        }
        return MovementCheck.PASS;
    }
    
    public CheckResult checkSprintHungry(final PlayerToggleSprintEvent event) {
        final Player player = event.getPlayer();
        if (event.isSprinting() && player.getGameMode() != GameMode.CREATIVE && player.getFoodLevel() <= Magic.SPRINT_FOOD_MIN) {
            return new CheckResult(CheckResult.Result.FAILED, player.getName() + " sprinted while hungry (food=" + player.getFoodLevel() + ", min=" + Magic.SPRINT_FOOD_MIN + ")");
        }
        return MovementCheck.PASS;
    }
    
    public CheckResult checkSprintStill(final Player player, final Location from, final Location to) {
        return MovementCheck.PASS;
    }
    
    public CheckResult checkWaterWalk(final Player player, final double x, final double y, final double z) {
        final Block block = player.getLocation().getBlock();
        if (player.getVehicle() == null && !player.isFlying()) {
            if (block.isLiquid()) {
                if (!this.isInWater.contains(player.getName())) {
                    this.isInWater.add(player.getName());
                    return MovementCheck.PASS;
                }
                if (!this.isInWaterCache.contains(player.getName())) {
                    this.isInWaterCache.add(player.getName());
                    return MovementCheck.PASS;
                }
                if (player.getNearbyEntities(1.0, 1.0, 1.0).isEmpty()) {
                    boolean b;
                    if (!Utilities.sprintFly(player)) {
                        b = (x > Magic.XZ_SPEED_MAX_WATER || z > Magic.XZ_SPEED_MAX_WATER);
                    }
                    else {
                        b = (x > Magic.XZ_SPEED_MAX_WATER_SPRINT || z > Magic.XZ_SPEED_MAX_WATER_SPRINT);
                    }
                    if (!b && !Utilities.isFullyInWater(player.getLocation()) && Utilities.isHoveringOverWater(player.getLocation(), 1) && y == 0.0 && !block.getType().equals((Object)Material.WATER_LILY)) {
                        b = true;
                    }
                    if (b) {
                        if (this.waterSpeedViolation.containsKey(player.getName())) {
                            final int v = this.waterSpeedViolation.get(player.getName());
                            if (v >= Magic.WATER_SPEED_VIOLATION_MAX) {
                                this.waterSpeedViolation.put(player.getName(), 0);
                                return new CheckResult(CheckResult.Result.FAILED, player.getName() + " stood on water " + v + " times (can't stand on " + block.getType() + " or " + block.getRelative(BlockFace.DOWN).getType() + ")");
                            }
                            this.waterSpeedViolation.put(player.getName(), v + 1);
                        }
                        else {
                            this.waterSpeedViolation.put(player.getName(), 1);
                        }
                    }
                }
            } else if (block.getRelative(BlockFace.DOWN).isLiquid() && this.isAscending(player) && Utilities.cantStandAt(block) && Utilities.cantStandAt(block.getRelative(BlockFace.DOWN))) {
                if (this.waterAscensionViolation.containsKey(player.getName())) {
                    final int v2 = this.waterAscensionViolation.get(player.getName());
                    if (v2 >= Magic.WATER_ASCENSION_VIOLATION_MAX) {
                        this.waterAscensionViolation.put(player.getName(), 0);
                        return new CheckResult(CheckResult.Result.FAILED, player.getName() + " stood on water " + v2 + " times (can't stand on " + block.getType() + " or " + block.getRelative(BlockFace.DOWN).getType() + ")");
                    }
                    this.waterAscensionViolation.put(player.getName(), v2 + 1);
                }
                else {
                    this.waterAscensionViolation.put(player.getName(), this.waterAscensionViolation.get(player.getName()) - 1);
                }
            }
            else {
                this.isInWater.remove(player.getName());
                this.isInWaterCache.remove(player.getName());
            }
        }
        return MovementCheck.PASS;
    }
    
    public CheckResult checkNoclip(final Player player) {
        final Block block = player.getEyeLocation().getBlock();
        final Block otherBlock = player.getLocation().getBlock();
        if ((!Utilities.canStandWithin(block) && !Utilities.canStandWithin(otherBlock)) || (!Utilities.canStandWithin(block) && !AntiCheat.getManager().getBackend().isMovingExempt(player))) {
            return new CheckResult(CheckResult.Result.FAILED, player.getName() + " attempted to pass through a solid block.");
        }
        return MovementCheck.PASS;
    }
    
    public CheckResult checkVClip(final Player player, final Distance distance) {
        final double from = Math.round(distance.fromY());
        final double to = Math.round(distance.toY());
        if (player.isInsideVehicle() || from == to || from < to || Math.round(distance.getYDifference()) < 2L) {
            return MovementCheck.PASS;
        }
        for (int i = 0; i < Math.round(distance.getYDifference()) + 1L; ++i) {
            final Block block = new Location(player.getWorld(), player.getLocation().getX(), to + i, player.getLocation().getZ()).getBlock();
            if (block.getType() != Material.AIR && block.getType().isSolid()) {
                return new CheckResult(CheckResult.Result.FAILED, player.getName() + " tried to move through a solid block", (int)from + 3);
            }
        }
        return MovementCheck.PASS;
    }
    
    public static void tp(Player p, Location l){
    	int y = l.getBlockY()-1;
    	int x=l.getBlockX(), z=l.getBlockZ();
    	p.teleport(new Location(l.getWorld(), x+0.5, y+(needStep(l.getWorld(),x,y,z)?0.55:1.05), z+0.5));
    }
    
    /**
     * braucht man einen Schritt höher?
     * */
    @SuppressWarnings("deprecation")
	public static boolean needStep(World w, int x, int y, int z){
		int id=w.getBlockAt(x, y, z).getTypeId(), data=w.getBlockAt(x, y, z).getData();
    	return (id==44 || id==126) && data<8;
    }
    
    @SuppressWarnings("deprecation")
	public CheckResult checkYAxis(final Player player, final Distance distance) {
        final String name = player.getName();
        if (!this.canMoveVert.containsKey(name)) {
            this.canMoveVert.put(name, true);
        }
        if (distance.getYDifference() > Magic.TELEPORT_MIN || distance.getYDifference() < 0.0) {
            return MovementCheck.PASS;
        }
        if (!AntiCheat.getManager().getBackend().isMovingExempt(player) && !Utilities.isClimbableBlock(player.getLocation().getBlock()) && !Utilities.isClimbableBlock(player.getLocation().add(0.0, -1.0, 0.0).getBlock()) && !player.isInsideVehicle() && !Utilities.isInWater(player) && !this.hasJumpPotion(player)) {
            final double y1 = player.getLocation().getY();
            final double lastDelta = distance.getYActual();
            if (player.getLocation().getBlock().getType() != Material.AIR || player.isOnGround()) {
                this.canMoveVert.put(name, true);
            }
            else if (this.canMoveVert.get(name)) {
                if (lastDelta > 0.0) {
                    this.canMoveVert.put(name, false);
                }
            }
            else if (lastDelta < 0.0) {
                if (!this.yAxisViolations.containsKey(name)) {
                    this.yAxisViolations.put(name, 0);
                }
                this.yAxisViolations.put(name, this.yAxisViolations.get(name) + 1);
                if (this.yAxisViolations.get(name) > Magic.Y_MAXVIOLATIONS) {
                    final Location g = player.getLocation();
                    if (!this.silentMode()) {
                        g.setY((double)this.lastYcoord.get(name));
                        this.sendFormattedMessage(player, "Fly hacking on the y-axis detected.");
                        if (g.getBlock().getType() == Material.AIR) {
                        	tp(player, g);
                        }
                    }
                    return new CheckResult(CheckResult.Result.FAILED, player.getName() + " tried to ascend on the y-axis without hitting the ground.");
                }
            }
            if (!this.lastYcoord.containsKey(name) || !this.lastYtime.containsKey(name) || !this.yAxisLastViolation.containsKey(name) || !this.yAxisLastViolation.containsKey(name)) {
                this.lastYcoord.put(name, y1);
                this.yAxisViolations.put(name, 0);
                this.yAxisLastViolation.put(name, 0L);
                this.lastYtime.put(name, System.currentTimeMillis());
            }
            else {
                if (!this.hoverTicks.containsKey(name)) {
                    this.hoverTicks.put(name, 0);
                }
                final boolean overAir = Utilities.cantStandAtBetter(player.getLocation().getBlock()) && !player.isSneaking();
                if (Math.abs(y1 - this.lastYcoord.get(name)) <= Magic.Y_HOVER_BUFFER * 0.75 && overAir) {
                    this.hoverTicks.put(name, this.hoverTicks.get(name) + 1);
                    if (this.hoverTicks.get(name) > Magic.Y_HOVER_TIME) {
                        final Location g2 = player.getLocation();
                        if (!this.silentMode()) {
                            g2.setY((double)this.lastYcoord.get(name));
                            this.sendFormattedMessage(player, "Fly hacking on the y-axis detected.");
                            if (g2.getBlock().getType() == Material.AIR) {
                            	tp(player, g2);
                            }
                        }
                        return new CheckResult(CheckResult.Result.FAILED, player.getName() + " tried to fly (hover) on y-axis " + this.hoverTicks.get(name) + " times (max =" + Magic.Y_HOVER_TIME + ")");
                    }
                }
                else {
                    this.hoverTicks.put(name, 0);
                }
                if (y1 > this.lastYcoord.get(name) && this.yAxisViolations.get(name) > Magic.Y_MAXVIOLATIONS && System.currentTimeMillis() - this.yAxisLastViolation.get(name) < Magic.Y_MAXVIOTIME) {
                    final Location g2 = player.getLocation();
                    this.yAxisViolations.put(name, this.yAxisViolations.get(name) + 1);
                    this.yAxisLastViolation.put(name, System.currentTimeMillis());
                    if (!this.silentMode()) {
                        g2.setY((double)this.lastYcoord.get(name));
                        this.sendFormattedMessage(player, "Fly hacking on the y-axis detected.  Please wait 5 seconds to prevent getting damage.");
                        if (g2.getBlock().getType() == Material.AIR) {
                        	tp(player, g2);
                        }
                    }
                    return new CheckResult(CheckResult.Result.FAILED, player.getName() + " tried to fly on y-axis " + this.yAxisViolations.get(name) + " times (max =" + Magic.Y_MAXVIOLATIONS + ")");
                }
                if (this.yAxisViolations.get(name) > Magic.Y_MAXVIOLATIONS && System.currentTimeMillis() - this.yAxisLastViolation.get(name) > Magic.Y_MAXVIOTIME) {
                    this.yAxisViolations.put(name, this.yAxisViolations.get(name) - 1);
                    this.yAxisLastViolation.put(name, 0L);
                }
                final long i = System.currentTimeMillis() - this.lastYtime.get(name);
                final double diff = Magic.Y_MAXDIFF + (Utilities.isStair(player.getLocation().add(0.0, -1.0, 0.0).getBlock()) ? 0.5 : 0.0);
                if (y1 - this.lastYcoord.get(name) > diff && i < Magic.Y_TIME) {
                    if (player != null) {
                        final Location g3 = player.getLocation();
                        this.yAxisViolations.put(name, this.yAxisViolations.get(name) + 1);
                        this.yAxisLastViolation.put(name, System.currentTimeMillis());
                        if (!this.silentMode()) {
                            g3.setY((double)this.lastYcoord.get(name));
                            if (g3.getBlock().getType() == Material.AIR) {
                            	tp(player, g3);
                            }
                        }
                    }
                    return new CheckResult(CheckResult.Result.FAILED, player.getName() + " tried to fly on y-axis in " + i + " ms (min =" + Magic.Y_TIME + ")");
                }
                if (y1 - this.lastYcoord.get(name) > Magic.Y_MAXDIFF + 1.0 || System.currentTimeMillis() - this.lastYtime.get(name) > Magic.Y_TIME) {
                    this.lastYtime.put(name, System.currentTimeMillis());
                    this.lastYcoord.put(name, y1);
                }
            }
        }
        return MovementCheck.PASS;
    }
    
    public CheckResult checkTimer(final Player player) {
        if (player == null || player.getName() == null) {
            return MovementCheck.PASS;
        }
        final String name = player.getName();
        if (!this.stepTime.containsKey(name)) {
            this.stepTime.put(name, System.currentTimeMillis());
        }
        if (!this.timerBuffer.containsKey(name)) {
            this.timerBuffer.put(name, Magic.TIMER_STEP_CHECK * 3);
        }
        this.timerBuffer.put(name, this.timerBuffer.get(name) - 1);
        if (!AntiCheat.getManager().getBackend().isMovingExempt(player)) {
            if (this.timerBuffer.get(name) < 0) {
                if (!this.silentMode()) {
                    this.sendFormattedMessage(player, "Modification of game timer detected. Please stand still for a bit.");
                }
                this.incrementTimerBuffer(name);
                return new CheckResult(CheckResult.Result.FAILED, name + " attempted to send packets too fast!");
            }
            this.incrementTimerBuffer(name);
        }
        return MovementCheck.PASS;
    }
    
    private void incrementTimerBuffer(final String name) {
        final double timeSince = (System.currentTimeMillis() - this.stepTime.get(name)) / 1000L;
        if (timeSince > 1.0) {
            final double allowedPackets = timeSince * Magic.TIMER_TIMEMIN;
            if (this.timerBuffer.get(name) > 65) {
                this.timerBuffer.put(name, 65);
            }
            else {
                this.timerBuffer.put(name, (int)(this.timerBuffer.get(name) + allowedPackets));
            }
            this.stepTime.put(name, System.currentTimeMillis());
        }
    }
    
    public CheckResult checkGlide(final Player player) {
        final String name = player.getName();
        if (!this.glideBuffer.containsKey(name)) {
            this.glideBuffer.put(name, 0);
        }
        if (!this.lastYDelta.containsKey(name)) {
            this.lastYDelta.put(name, 0.0);
        }
        if (!this.lastYcoord.containsKey(name)) {
            this.lastYcoord.put(name, player.getLocation().getY());
        }
        final double currentY = player.getLocation().getY();
        final double math = currentY - this.lastYcoord.get(name);
        if (math < 0.0 && math > -3.4 && !AntiCheat.getManager().getBackend().isMovingExempt(player)) {
            if (math <= this.lastYDelta.get(name) && player.getEyeLocation().getBlock().getType() != Material.LADDER && !Utilities.isInWater(player) && !Utilities.isInWeb(player) && Utilities.cantStandAtSingle(player.getLocation().getBlock())) {
                if (math <= 0.3) {
                    final int currentBuffer = this.glideBuffer.get(name);
                    this.glideBuffer.put(name, currentBuffer + 1);
                    if (currentBuffer + 1 >= Magic.FLIGHT_LIMIT) {
                        if (!this.silentMode()) {
                            this.sendFormattedMessage(player, "Fly hacking on the y-axis detected.");
                        }
                        this.lastYDelta.put(name, math);
                        return new CheckResult(CheckResult.Result.FAILED, name + " attempted to fall too slowly!");
                    }
                }
            }
            else {
                final int currentBuffer = this.glideBuffer.get(name) - 1;
                this.glideBuffer.put(name, (currentBuffer > 0) ? currentBuffer : 0);
            }
        }
        this.lastYDelta.put(name, math);
        return MovementCheck.PASS;
    }
    
    public CheckResult checkFlight(final Player player, final Distance distance) {
        if (distance.getYDifference() > Magic.TELEPORT_MIN) {
            return MovementCheck.PASS;
        }
        final String name = player.getName();
        final double y1 = distance.fromY();
        final double y2 = distance.toY();
        if (!AntiCheat.getManager().getBackend().isMovingExempt(player) && !Utilities.isHoveringOverWater(player.getLocation(), 1) && Utilities.cantStandAtExp(player.getLocation()) && Utilities.blockIsnt(player.getLocation().getBlock().getRelative(BlockFace.DOWN), new Material[] { Material.FENCE, Material.FENCE_GATE, Material.COBBLE_WALL })) {
            if (!this.blocksOverFlight.containsKey(name)) {
                this.blocksOverFlight.put(name, 0.0);
            }
            this.blocksOverFlight.put(name, this.blocksOverFlight.get(name) + distance.getXDifference() + distance.getYDifference() + distance.getZDifference());
            if (y1 > y2) {
                this.blocksOverFlight.put(name, this.blocksOverFlight.get(name) - distance.getYDifference());
            }
            if (this.blocksOverFlight.get(name) > Magic.FLIGHT_BLOCK_LIMIT && y1 <= y2) {
                return new CheckResult(CheckResult.Result.FAILED, player.getName() + " flew over " + this.blocksOverFlight.get(name) + " blocks (max=" + Magic.FLIGHT_BLOCK_LIMIT + ")");
            }
        }
        else {
            this.blocksOverFlight.put(name, 0.0);
        }
        return MovementCheck.PASS;
    }
    
    public void logAscension(final Player player, final double y1, final double y2) {
        final String name = player.getName();
        if (y1 < y2 && !this.isAscending.contains(name)) {
            this.isAscending.add(name);
        }
        else {
            this.isAscending.remove(name);
        }
    }
    
    public CheckResult checkAscension(final Player player, final double y1, final double y2) {
        int max = Magic.ASCENSION_COUNT_MAX;
        String string = "";
        if (player.hasPotionEffect(PotionEffectType.JUMP)) {
            max += 12;
            string = " with jump potion";
        }
        final Block block = player.getLocation().getBlock();
        if (!AntiCheat.getManager().getBackend().isMovingExempt(player) && !Utilities.isInWater(player) && !AntiCheat.getManager().getBackend().getBlockCheck().justBroke(player) && !Utilities.isClimbableBlock(player.getLocation().getBlock()) && !player.isInsideVehicle()) {
            final String name = player.getName();
            if (y1 < y2) {
                if (!block.getRelative(BlockFace.NORTH).isLiquid() && !block.getRelative(BlockFace.SOUTH).isLiquid() && !block.getRelative(BlockFace.EAST).isLiquid() && !block.getRelative(BlockFace.WEST).isLiquid()) {
                    this.increment(player, this.ascensionCount, max);
                    if (this.ascensionCount.get(name) >= max) {
                        return new CheckResult(CheckResult.Result.FAILED, player.getName() + " ascended " + this.ascensionCount.get(name) + " times in a row (max = " + max + string + ")");
                    }
                }
            }
            else {
                this.ascensionCount.put(name, 0);
            }
        }
        return MovementCheck.PASS;
    }
}
