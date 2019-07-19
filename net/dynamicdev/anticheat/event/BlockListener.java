package net.dynamicdev.anticheat.event;

import net.dynamicdev.anticheat.*;
import org.bukkit.entity.*;
import net.dynamicdev.anticheat.check.*;
import org.bukkit.event.*;
import org.bukkit.event.block.*;
import net.dynamicdev.anticheat.util.*;
import org.bukkit.block.*;

public class BlockListener extends EventListener {
	
    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockDamage(final BlockDamageEvent event) {
        final Player player = event.getPlayer();
        if (event.getInstaBreak() || Utilities.isInstantBreak(event.getBlock().getType())) {
            EventListener.getBackend().getBlockCheck().logInstantBreak(player);
        }
        if (EventListener.getCheckManager().willCheck(player, CheckType.AUTOTOOL)) {
            final CheckResult result = EventListener.getBackend().getBlockCheck().checkAutoTool(player);
            if (result.failed()) {
                event.setCancelled(!EventListener.silentMode());
                EventListener.log(result.getMessage(), player, CheckType.AUTOTOOL);
            }
        }
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockPlace(final BlockPlaceEvent event) {
        final Player player = event.getPlayer();
        if (player != null && EventListener.getCheckManager().willCheck(player, CheckType.FAST_PLACE)) {
            final CheckResult result = EventListener.getBackend().getBlockCheck().checkFastPlace(player);
            if (result.failed()) {
                event.setCancelled(!EventListener.silentMode());
                EventListener.log(result.getMessage(), player, CheckType.FAST_PLACE);
            }
            else {
                EventListener.decrease(player);
                EventListener.getBackend().getBlockCheck().logBlockPlace(player);
            }
        }
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event) {
        final Player player = event.getPlayer();
        final Block block = event.getBlock();
        boolean noHack = true;
        if (player != null) {
            if (EventListener.getCheckManager().willCheck(player, CheckType.FAST_BREAK)) {
                final CheckResult result = EventListener.getBackend().getBlockCheck().checkFastBreak(player, block);
                if (result.failed()) {
                    event.setCancelled(!EventListener.silentMode());
                    EventListener.log(result.getMessage(), player, CheckType.FAST_BREAK);
                    noHack = false;
                }
            }
            if (EventListener.getCheckManager().willCheck(player, CheckType.NO_SWING)) {
                final CheckResult result = EventListener.getBackend().getBlockCheck().checkSwing(player, block);
                if (result.failed()) {
                    event.setCancelled(!EventListener.silentMode());
                    EventListener.log(result.getMessage(), player, CheckType.NO_SWING);
                    noHack = false;
                }
            }
            if (EventListener.getCheckManager().willCheck(player, CheckType.LONG_REACH)) {
                final Distance distance = new Distance(player.getLocation(), block.getLocation());
                final CheckResult result = EventListener.getBackend().getBlockCheck().checkLongReachBlock(player, distance.getXDifference(), distance.getYDifference(), distance.getZDifference());
                if (result.failed()) {
                    event.setCancelled(!EventListener.silentMode());
                    EventListener.log(result.getMessage(), player, CheckType.LONG_REACH);
                    noHack = false;
                }
            }
            if (EventListener.getCheckManager().willCheck(player, CheckType.DIRECTION)) {
                final CheckResult result = EventListener.getBackend().getBlockCheck().checkBlockRotation(player, event);
                if (result.failed()) {
                    event.setCancelled(!EventListener.silentMode());
                    EventListener.log(result.getMessage(), player, CheckType.DIRECTION);
                    noHack = false;
                }
            }
        }
        if (noHack) {
            EventListener.decrease(player);
        }
        EventListener.getBackend().getBlockCheck().logBlockBreak(player);
    }
}
