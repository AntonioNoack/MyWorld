package net.dynamicdev.anticheat.util;

import org.bukkit.*;

public enum ToolHardness
{
    WOOD(0.75), 
    STONE(0.4), 
    IRON(0.25), 
    DIAMOND(0.2), 
    SHEARS(0.55), 
    GOLD(0.15);
    
    double hardness;
    
    private ToolHardness(final double hard) {
        this.hardness = hard;
    }
    
    public static double getToolHardness(final Material tool) {
        for (final ToolHardness e : values()) {
            if (tool.name().contains(e.name())) {
                return e.hardness;
            }
        }
        return 1.5;
    }
}
