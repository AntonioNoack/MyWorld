package net.dynamicdev.anticheat.util;

import org.bukkit.*;

public class SpyState
{
    private boolean allowFlight;
    private boolean flying;
    private Location location;
    
    public SpyState(final boolean allowFlight, final boolean flying, final Location location) {
        this.allowFlight = allowFlight;
        this.flying = flying;
        this.location = location;
    }
    
    public boolean getAllowFlight() {
        return this.allowFlight;
    }
    
    public boolean getFlying() {
        return this.flying;
    }
    
    public Location getLocation() {
        return this.location;
    }
}
