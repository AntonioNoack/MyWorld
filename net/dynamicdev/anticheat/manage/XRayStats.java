package net.dynamicdev.anticheat.manage;

import java.util.*;

public class XRayStats {
    private double t;
    private double d;
    private double g;
    private double i;
    private double l;
    private double r;
    private double o;
    
    public XRayStats(final String player, final Map<String, Integer> diamond, final Map<String, Integer> gold, final Map<String, Integer> iron, final Map<String, Integer> lapis, final Map<String, Integer> redstone, final Map<String, Integer> other, final Map<String, Integer> total) {
        this.t = 1.0;
        this.d = 0.0;
        this.g = 0.0;
        this.i = 0.0;
        this.l = 0.0;
        this.r = 0.0;
        this.o = 0.0;
        if (total.get(player) != null) {
            this.t = total.get(player);
        }
        if (diamond.get(player) != null) {
            this.d = diamond.get(player) / this.t * 100.0;
        }
        if (gold.get(player) != null) {
            this.g = gold.get(player) / this.t * 100.0;
        }
        if (iron.get(player) != null) {
            this.i = iron.get(player) / this.t * 100.0;
        }
        if (lapis.get(player) != null) {
            this.l = lapis.get(player) / this.t * 100.0;
        }
        if (redstone.get(player) != null) {
            this.r = redstone.get(player) / this.t * 100.0;
        }
        if (other.get(player) != null) {
            this.o = other.get(player) / this.t * 100.0;
        }
    }
    
    public double getTotal() {
        return this.t;
    }
    
    public double getDiamond() {
        return this.d;
    }
    
    public double getGold() {
        return this.g;
    }
    
    public double getIron() {
        return this.i;
    }
    
    public double getLapis() {
        return this.l;
    }
    
    public double getRedstone() {
        return this.r;
    }
    
    public double getOther() {
        return this.o;
    }
}
