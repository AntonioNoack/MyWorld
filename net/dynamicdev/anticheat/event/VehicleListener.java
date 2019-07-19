package net.dynamicdev.anticheat.event;

import org.bukkit.entity.*;
import net.dynamicdev.anticheat.*;
import org.bukkit.event.*;
import org.bukkit.event.vehicle.*;

public class VehicleListener extends EventListener
{
    @EventHandler(ignoreCancelled = true)
    public void onVehicleEnter(final VehicleEnterEvent event) {
        if (event.getEntered() instanceof Player) {
            EventListener.getBackend().logEnterExit((Player)event.getEntered());
        }
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onVehicleExit(final VehicleExitEvent event) {
        if (event.getExited() instanceof Player) {
            EventListener.getBackend().logEnterExit((Player)event.getExited());
        }
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
    
    @EventHandler(ignoreCancelled = true)
    public void onVehicleDestroy(final VehicleDestroyEvent event) {
        if (event.getVehicle().getPassenger() != null && event.getVehicle().getPassenger() instanceof Player) {
            EventListener.getBackend().logEnterExit((Player)event.getVehicle().getPassenger());
        }
        AntiCheat.getManager().addEvent(event.getEventName(), event.getHandlers().getRegisteredListeners());
    }
}
