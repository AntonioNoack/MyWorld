package net.dynamicdev.anticheat.event;

import net.dynamicdev.anticheat.*;
import net.dynamicdev.anticheat.check.*;

import org.bukkit.event.*;
import org.bukkit.event.entity.*;
import org.bukkit.enchantments.*;

import net.dynamicdev.anticheat.util.*;

import org.bukkit.entity.*;

public class EntityListener extends EventListener {
	
    @EventHandler
    public void onEntityShootBow(final EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player)event.getEntity();
            if (EventListener.getCheckManager().willCheck(player, CheckType.FAST_BOW)) {
                final CheckResult result = EventListener.getBackend().getEntityCheck().checkFastBow(player, event.getForce());
                if (result.failed()) {
                    event.setCancelled(!EventListener.silentMode());
                    EventListener.log(result.getMessage(), player, CheckType.FAST_BOW);
                } else {
                    EventListener.decrease(player);
                }
            }
        }
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onEntityRegainHealth(final EntityRegainHealthEvent event) {
        if (event.getEntity() instanceof Player && event.getRegainReason() == EntityRegainHealthEvent.RegainReason.SATIATED) {
            final Player player = (Player)event.getEntity();
            if (EventListener.getCheckManager().willCheck(player, CheckType.FAST_HEAL)) {
                final CheckResult result = EventListener.getBackend().getEntityCheck().checkFastHeal(player);
                if (result.failed()) {
                    event.setCancelled(!EventListener.silentMode());
                    EventListener.log(result.getMessage(), player, CheckType.FAST_HEAL);
                } else {
                    EventListener.decrease(player);
                    EventListener.getBackend().getEntityCheck().logHeal(player);
                }
            }
        }
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onFoodLevelChange(final FoodLevelChangeEvent event) {
        if (event.getEntity() instanceof Player) {
            final Player player = (Player)event.getEntity();
            if (player.getFoodLevel() < event.getFoodLevel() && EventListener.getCheckManager().willCheck(player, CheckType.FAST_EAT)) {
                final CheckResult result = EventListener.getBackend().getEntityCheck().checkFastEat(player);
                if (result.failed()) {
                    event.setCancelled(!EventListener.silentMode());
                    EventListener.log(result.getMessage(), player, CheckType.FAST_EAT);
                } else {
                    EventListener.decrease(player);
                }
            }
        }
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler(ignoreCancelled = true)
    public void onEntityDamage(final EntityDamageEvent event) {
        boolean noHack = true;
        if (event instanceof EntityDamageByEntityEvent) {
            final EntityDamageByEntityEvent e = (EntityDamageByEntityEvent)event;
            if (event.getEntity() instanceof Player) {
                final Player player = (Player)event.getEntity();
                if (e.getDamager() instanceof Arrow) {
                    final Arrow arrow = (Arrow)e.getDamager();
                    if (arrow.getShooter() instanceof Player && event.getEntity() == arrow.getShooter()) {
                        event.setCancelled(true);
                    }
                }
                if (Utilities.hasArmorEnchantment(player, Enchantment.THORNS)) {
                    EventListener.getBackend().getBlockCheck().logAnimation(player);
                }
                if (e.getDamager() instanceof Player) {
                    final Player p = (Player)e.getDamager();
                    EventListener.getBackend().logDamage(p, 1);
                    final int value = p.getInventory().getItemInHand().containsEnchantment(Enchantment.KNOCKBACK) ? 2 : 1;
                    EventListener.getBackend().logDamage(player, value);
                    if (EventListener.getCheckManager().willCheck(p, CheckType.LONG_REACH)) {
                        final Distance distance = new Distance(player.getLocation(), p.getLocation());
                        final CheckResult result = EventListener.getBackend().getEntityCheck().checkLongReachDamage(player, distance.getXDifference(), distance.getYDifference(), distance.getZDifference());
                        if (result.failed()) {
                            event.setCancelled(!EventListener.silentMode());
                            EventListener.log(result.getMessage(), p, CheckType.LONG_REACH);
                            noHack = false;
                        }
                    }
                }
                else if (e.getDamager() instanceof TNTPrimed || e.getDamager() instanceof Creeper) {
                    EventListener.getBackend().logDamage(player, 3);
                }
                else {
                    EventListener.getBackend().logDamage(player, 1);
                }
            }
            if (e.getDamager() instanceof Player) {
                final Player player = (Player)e.getDamager();
                EventListener.getBackend().logDamage(player, 1);
                if (EventListener.getCheckManager().willCheck(player, CheckType.AUTOTOOL)) {
                    final CheckResult result2 = EventListener.getBackend().getBlockCheck().checkAutoTool(player);
                    if (result2.failed()) {
                        event.setCancelled(!EventListener.silentMode());
                        EventListener.log(result2.getMessage(), player, CheckType.AUTOTOOL);
                        noHack = false;
                    }
                }
                if (EventListener.getCheckManager().willCheck(player, CheckType.FORCEFIELD)) {
                    final CheckResult result2 = EventListener.getBackend().getEntityCheck().checkFightSpeed(player);
                    if (result2.failed()) {
                        event.setCancelled(!EventListener.silentMode());
                        EventListener.log(result2.getMessage(), player, CheckType.AUTOTOOL);
                        noHack = false;
                    }
                }
                if (EventListener.getCheckManager().willCheck(player, CheckType.FORCEFIELD)) {
                    final CheckResult result2 = EventListener.getBackend().getEntityCheck().checkSprintDamage(player);
                    if (result2.failed()) {
                        event.setCancelled(!EventListener.silentMode());
                        EventListener.log(result2.getMessage(), player, CheckType.FORCEFIELD);
                        noHack = false;
                    }
                }
                if (EventListener.getCheckManager().willCheck(player, CheckType.DIRECTION) && event.getEntity() instanceof LivingEntity) {
                    final LivingEntity damaged = (LivingEntity)event.getEntity();
                    final CheckResult result3 = EventListener.getBackend().getEntityCheck().checkFightRotation(player, damaged);
                    if (result3.failed()) {
                        event.setCancelled(!EventListener.silentMode());
                        EventListener.log(result3.getMessage(), player, CheckType.DIRECTION);
                        noHack = false;
                    }
                }
                if (EventListener.getCheckManager().willCheck(player, CheckType.FORCEFIELD) && event.getEntity() instanceof LivingEntity) {
                    final LivingEntity damaged = (LivingEntity)event.getEntity();
                    final CheckResult result3 = EventListener.getBackend().getEntityCheck().checkFightDistance(player, damaged);
                    if (result3.failed()) {
                        event.setCancelled(!EventListener.silentMode());
                        EventListener.log(result3.getMessage(), player, CheckType.FORCEFIELD);
                        noHack = false;
                    }
                }
                if (EventListener.getCheckManager().willCheck(player, CheckType.NO_SWING)) {
                    final CheckResult result2 = EventListener.getBackend().getEntityCheck().checkAnimation(player, event.getEntity());
                    if (result2.failed()) {
                        event.setCancelled(!EventListener.silentMode());
                        EventListener.log(result2.getMessage(), player, CheckType.NO_SWING);
                        noHack = false;
                    }
                }
                if (EventListener.getCheckManager().willCheck(player, CheckType.FORCEFIELD)) {
                    final CheckResult result2 = EventListener.getBackend().getEntityCheck().checkSight(player, e.getEntity());
                    if (result2.failed()) {
                        event.setCancelled(!EventListener.silentMode());
                        EventListener.log(result2.getMessage(), player, CheckType.FORCEFIELD);
                        noHack = false;
                    }
                }
                if (noHack) {
                    EventListener.decrease(player);
                }
            }
        }
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
}
