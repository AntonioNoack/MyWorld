package net.dynamicdev.anticheat.event;

import org.bukkit.entity.*;
import net.dynamicdev.anticheat.*;
import net.dynamicdev.anticheat.check.*;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import net.dynamicdev.anticheat.util.*;

public class InventoryListener extends EventListener
{
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!event.isRightClick() && !event.isShiftClick() && event.getWhoClicked() instanceof Player) {
            final Player player = (Player)event.getWhoClicked();
            if (EventListener.getCheckManager().willCheck(player, CheckType.FAST_INVENTORY)) {
                final CheckResult result = EventListener.getBackend().getInventoryCheck().checkInventoryClicks(player);
                if (result.failed()) {
                    if (!EventListener.silentMode()) {
                        player.getInventory().clear();
                        event.setCancelled(true);
                    }
                    EventListener.log(result.getMessage(), player, CheckType.FAST_INVENTORY);
                }
                else {
                    EventListener.decrease(player);
                }
            }
        }
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler
    public void onInventoryOpen(final InventoryOpenEvent event) {
        if (event.getInventory().getType() != InventoryType.BEACON) {
            EventListener.getUserManager().getUser(event.getPlayer().getName()).setInventorySnapshot(event.getInventory().getContents());
        }
    }
    
    @EventHandler
    public void onInventoryClose(final InventoryCloseEvent event) {
        final User user = EventListener.getUserManager().getUser(event.getPlayer().getName());
        if (user != null) {
            user.removeInventorySnapshot();
        }
    }
}
