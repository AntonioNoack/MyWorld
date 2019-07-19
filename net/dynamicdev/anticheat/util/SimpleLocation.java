package net.dynamicdev.anticheat.util;

import org.bukkit.*;

public class SimpleLocation {
    private int x;
    private int y;
    private int z;
    
    public SimpleLocation(final Location l) {
        this(l.getBlockX(), l.getBlockY(), l.getBlockZ());
    }
    
    public SimpleLocation(final int x, final int y, final int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public int getZ() {
        return this.z;
    }
}
