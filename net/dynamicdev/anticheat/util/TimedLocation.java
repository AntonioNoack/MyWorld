package net.dynamicdev.anticheat.util;

import org.bukkit.*;

public class TimedLocation
{
    private Location location;
    private long timestamp;
    
    public TimedLocation(final Location location, final long time) {
        this.location = location;
        this.timestamp = time;
    }
    
    public Location getLocation() {
        return this.location;
    }
    
    public long getTimestamp() {
        return this.timestamp;
    }
    
    public long getTimeDeltaFromNow() {
        return System.currentTimeMillis() - this.timestamp;
    }
    
    public double getDistanceXFrom(final Location loc) {
        return Utilities.getXDelta(this.getLocation(), loc);
    }
    
    public double getDistanceZFrom(final Location loc) {
        return Utilities.getZDelta(this.getLocation(), loc);
    }
}
