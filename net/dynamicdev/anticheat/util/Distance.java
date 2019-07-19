package net.dynamicdev.anticheat.util;

import org.bukkit.*;

public class Distance {
	
    private final double l1Y;
    private final double l2Y;
    private final double XDiff;
    private final double YDiff;
    private final double ZDiff;
    private final double yDelta;
    
    public Distance(final Location from, final Location to) {
        this.l1Y = to.getY();
        this.l2Y = from.getY();
        this.yDelta = from.getY() - to.getY();
        this.XDiff = Math.abs(to.getX() - from.getX());
        this.ZDiff = Math.abs(to.getZ() - from.getZ());
        this.YDiff = Math.abs(this.l1Y - this.l2Y);
    }
    
    public double fromY() {
        return this.l2Y;
    }
    
    public double toY() {
        return this.l1Y;
    }
    
    public double getXDifference() {
        return this.XDiff;
    }
    
    public double getZDifference() {
        return this.ZDiff;
    }
    
    public double getYDifference() {
        return this.YDiff;
    }
    
    public double getYActual() {
        return this.yDelta;
    }
}
