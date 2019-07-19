package net.dynamicdev.anticheat.check;

import java.util.HashMap;
import java.util.Map;

import net.dynamicdev.anticheat.AntiCheat;
import net.dynamicdev.anticheat.config.providers.Magic;
import net.dynamicdev.anticheat.manage.AntiCheatManager;
import net.dynamicdev.anticheat.util.Utilities;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class EntityCheck extends AntiCheatCheck {
    private Map<String, Integer> projectilesShot;
    private Map<String, Long> startEat;
    private Map<String, Long> lastHeal;
    private Map<String, Long> projectileTime;
    private Map<String, Long> bowWindUp;
    private Map<String, Long> sprinted;
    private Map<String, Long> lastAttack;
    
    public EntityCheck(final AntiCheatManager instance) {
        super(instance);
        this.projectilesShot = new HashMap<String, Integer>();
        this.startEat = new HashMap<String, Long>();
        this.lastHeal = new HashMap<String, Long>();
        this.projectileTime = new HashMap<String, Long>();
        this.bowWindUp = new HashMap<String, Long>();
        this.sprinted = new HashMap<String, Long>();
        this.lastAttack = new HashMap<String, Long>();
    }
    
    public void logSprint(final Player player) {
        this.sprinted.put(player.getName(), System.currentTimeMillis());
    }
    
    public CheckResult checkFastBow(final Player player, final float force) {
        final int ticks = (int)((System.currentTimeMillis() - this.bowWindUp.get(player.getName())) * 20L / 1000L + 3L);
        this.bowWindUp.remove(player.getName());
        float f = ticks / 20.0f;
        f = (f * f + f * 2.0f) / 3.0f;
        f = ((f > 1.0f) ? 1.0f : f);
        if (Math.abs(force - f) > Magic.BOW_ERROR) {
            return new CheckResult(CheckResult.Result.FAILED, player.getName() + " fired their bow too fast (actual force=" + force + ", calculated force=" + f + ")");
        }
        return EntityCheck.PASS;
    }
    
    public CheckResult checkProjectile(final Player player) {
        this.increment(player, this.projectilesShot, 10);
        if (!this.projectileTime.containsKey(player.getName())) {
            this.projectileTime.put(player.getName(), System.currentTimeMillis());
            return new CheckResult(CheckResult.Result.PASSED);
        }
        if (this.projectilesShot.get(player.getName()) == Magic.PROJECTILE_CHECK) {
            final long time = System.currentTimeMillis() - this.projectileTime.get(player.getName());
            this.projectileTime.remove(player.getName());
            this.projectilesShot.remove(player.getName());
            if (time < Magic.PROJECTILE_TIME_MIN) {
                return new CheckResult(CheckResult.Result.FAILED, player.getName() + " wound up a bow too fast (actual time=" + time + ", min time=" + Magic.PROJECTILE_TIME_MIN + ")");
            }
        }
        return EntityCheck.PASS;
    }
    
    public CheckResult checkLongReachDamage(final Player player, final double x, final double y, final double z) {
        final String string = player.getName() + " reached too far for an entity";
        final double i = (x >= Magic.ENTITY_MAX_DISTANCE) ? x : ((y > Magic.ENTITY_MAX_DISTANCE) ? y : ((z > Magic.ENTITY_MAX_DISTANCE) ? z : -1.0));
        if (i != -1.0) {
            return new CheckResult(CheckResult.Result.FAILED, string + " (distance=" + i + ", max=" + Magic.ENTITY_MAX_DISTANCE + ")");
        }
        return EntityCheck.PASS;
    }
    
    public CheckResult checkSight(final Player player, final Entity entity) {
        return EntityCheck.PASS;
    }
    
    public void logBowWindUp(final Player player) {
        this.bowWindUp.put(player.getName(), System.currentTimeMillis());
    }
    
    public void logEatingStart(final Player player) {
        this.startEat.put(player.getName(), System.currentTimeMillis());
    }
    
    public void logHeal(final Player player) {
        this.lastHeal.put(player.getName(), System.currentTimeMillis());
    }
    
    public CheckResult checkSprintDamage(final Player player) {
        if (this.isDoing(player, this.sprinted, Magic.SPRINT_MIN)) {
            return new CheckResult(CheckResult.Result.FAILED, player.getName() + " sprinted and damaged an entity too fast (min sprint=" + Magic.SPRINT_MIN + " ms)");
        }
        return EntityCheck.PASS;
    }
    
    public CheckResult checkFightSpeed(final Player player) {
        final String name = player.getName();
        if (!this.lastAttack.containsKey(name)) {
            this.lastAttack.put(name, System.currentTimeMillis());
        }
        final long math = System.currentTimeMillis() - this.lastAttack.get(name);
        if (math < Magic.FIGHT_TIME_MIN) {
            return new CheckResult(CheckResult.Result.FAILED, name + " attempted to attack faster than normal. (min=" + Magic.FIGHT_TIME_MIN + " | them=" + math);
        }
        return EntityCheck.PASS;
    }
    
    public CheckResult checkFightDistance(final Player player, final LivingEntity damaged) {
        final String name = player.getName();
        final Location entityLoc = damaged.getLocation().add(0.0, damaged.getEyeHeight(), 0.0);
        final Location playerLoc = player.getLocation().add(0.0, player.getEyeHeight(), 0.0);
        final double distance = Utilities.getDistance3D(entityLoc, playerLoc);
        if (distance > Magic.FIGHT_MIN_DISTANCE) {
            return new CheckResult(CheckResult.Result.FAILED, name + " attempted to attack something too far away. (min=" + Magic.FIGHT_MIN_DISTANCE + " | them=" + distance);
        }
        return EntityCheck.PASS;
    }
    
    public CheckResult checkFightRotation(final Player player, final LivingEntity damaged) {
        double offset = 0.0;
        final Location entityLoc = damaged.getLocation().add(0.0, damaged.getEyeHeight(), 0.0);
        final Location playerLoc = player.getLocation().add(0.0, player.getEyeHeight(), 0.0);
        final Vector playerRotation = new Vector(playerLoc.getYaw(), playerLoc.getPitch(), 0.0f);
        final Vector expectedRotation = Utilities.getRotation(playerLoc, entityLoc);
        final double deltaYaw = Utilities.clamp180(playerRotation.getX() - expectedRotation.getX());
        final double deltaPitch = Utilities.clamp180(playerRotation.getY() - expectedRotation.getY());
        final double horizontalDistance = Utilities.getHorizontalDistance(playerLoc, entityLoc);
        final double distance = Utilities.getDistance3D(playerLoc, entityLoc);
        final double offsetX = deltaYaw * horizontalDistance * distance;
        final double offsetY = deltaPitch * Math.abs(entityLoc.getY() - playerLoc.getY()) * distance;
        offset += Math.abs(offsetX);
        offset += Math.abs(offsetY);
        if (offset > Magic.DIRECTION_MAX_BUFFER) {
            return new CheckResult(CheckResult.Result.FAILED, player.getName() + " attempted to attack something without looking at it.");
        }
        return EntityCheck.PASS;
    }
    
    public CheckResult checkAnimation(final Player player, final Entity e) {
        if (!AntiCheat.getManager().getBackend().justAnimated(player)) {
            return new CheckResult(CheckResult.Result.FAILED, player.getName() + " didn't animate before damaging a " + e.getType());
        }
        return EntityCheck.PASS;
    }
    
    public CheckResult checkFastHeal(final Player player) {
        if (this.lastHeal.containsKey(player.getName())) {
            final long l = this.lastHeal.get(player.getName());
            this.lastHeal.remove(player.getName());
            if (System.currentTimeMillis() - l < Magic.HEAL_TIME_MIN) {
                return new CheckResult(CheckResult.Result.FAILED, player.getName() + " healed too quickly (time=" + (System.currentTimeMillis() - l) + " ms, min=" + Magic.HEAL_TIME_MIN + " ms)");
            }
        }
        return EntityCheck.PASS;
    }
    
    public CheckResult checkFastEat(final Player player) {
        if (this.startEat.containsKey(player.getName())) {
            final long l = this.startEat.get(player.getName());
            this.startEat.remove(player.getName());
            if (System.currentTimeMillis() - l < Magic.EAT_TIME_MIN) {
                return new CheckResult(CheckResult.Result.FAILED, player.getName() + " ate too quickly (time=" + (System.currentTimeMillis() - l) + " ms, min=" + Magic.EAT_TIME_MIN + " ms)");
            }
        }
        return EntityCheck.PASS;
    }
}
