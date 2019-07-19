package net.dynamicdev.anticheat.manage;

import org.bukkit.*;

import java.util.*;

import org.bukkit.command.*;

public class XRayTracker
{
    private Map<String, Integer> diamond;
    private Map<String, Integer> gold;
    private Map<String, Integer> iron;
    private Map<String, Integer> lapis;
    private Map<String, Integer> redstone;
    private Map<String, Integer> block;
    private Map<String, Integer> totalblock;
    private List<String> alerted;
    private static final ChatColor GREEN;
    private static final ChatColor WHITE;
    private static final ChatColor RED;
    private static final ChatColor GRAY;
    
    public XRayTracker() {
        this.diamond = new HashMap<String, Integer>();
        this.gold = new HashMap<String, Integer>();
        this.iron = new HashMap<String, Integer>();
        this.lapis = new HashMap<String, Integer>();
        this.redstone = new HashMap<String, Integer>();
        this.block = new HashMap<String, Integer>();
        this.totalblock = new HashMap<String, Integer>();
        this.alerted = new ArrayList<String>();
    }
    
    public boolean sufficientData(final String player) {
        return this.totalblock.get(player) != null && this.totalblock.get(player) >= 100;
    }
    
    public void calculate(final CommandSender cs, final String player, final double x, final double b, final String type) {
        ChatColor color = XRayTracker.WHITE;
        if (x >= b / 3.0) {
            color = XRayTracker.RED;
        }
        cs.sendMessage(XRayTracker.GRAY + "Percent " + type + " ore: " + color + this.round(x) + "%");
    }
    
    public boolean calculate(final String player, final double x, final double b) {
        return x >= b / 3.0;
    }
    
    public boolean hasAbnormal(final String player) {
        final XRayStats stats = new XRayStats(player, this.diamond, this.gold, this.iron, this.lapis, this.redstone, this.block, this.totalblock);
        final double total = stats.getOther();
        return this.calculate(player, stats.getDiamond(), total) || this.calculate(player, stats.getGold(), total) || this.calculate(player, stats.getIron(), total) || this.calculate(player, stats.getLapis(), total) || this.calculate(player, stats.getRedstone(), total);
    }
    
    public boolean hasAlerted(final String player) {
        return this.alerted.contains(player);
    }
    
    public void logAlert(final String player) {
        this.alerted.add(player);
    }
    
    public void sendStats(final CommandSender cs, final String player) {
        this.getStats(cs, player);
    }
    
    public void getStats(final CommandSender cs, final String player) {
        final XRayStats stats = new XRayStats(player, this.diamond, this.gold, this.iron, this.lapis, this.redstone, this.block, this.totalblock);
        final double total = stats.getOther();
        cs.sendMessage("--------------------[" + XRayTracker.GREEN + "X-Ray Stats" + XRayTracker.WHITE + "]---------------------");
        cs.sendMessage(XRayTracker.GRAY + "Player: " + XRayTracker.WHITE + player);
        cs.sendMessage(XRayTracker.GRAY + "Total blocks broken: " + XRayTracker.WHITE + stats.getTotal());
        this.calculate(cs, player, stats.getDiamond(), total, "diamond");
        this.calculate(cs, player, stats.getGold(), total, "gold");
        this.calculate(cs, player, stats.getIron(), total, "iron");
        this.calculate(cs, player, stats.getLapis(), total, "lapis");
        this.calculate(cs, player, stats.getRedstone(), total, "redstone");
        cs.sendMessage(XRayTracker.GRAY + "Percent all other blocks: " + XRayTracker.WHITE + this.round(stats.getOther()) + "%");
        cs.sendMessage("-----------------------------------------------------");
    }
    
    public void addDiamond(final String player) {
        this.addOre(player, this.diamond);
    }
    
    public void addGold(final String player) {
        this.addOre(player, this.gold);
    }
    
    public void addIron(final String player) {
        this.addOre(player, this.iron);
    }
    
    public void addLapis(final String player) {
        this.addOre(player, this.lapis);
    }
    
    public void addRedstone(final String player) {
        this.addOre(player, this.redstone);
    }
    
    public void addBlock(final String player) {
        this.addOre(player, this.block);
    }
    
    public void addTotal(final String player) {
        this.addOre(player, this.totalblock);
    }
    
    private void addOre(final String player, final Map<String, Integer> map) {
        if (map.get(player) == null || map.get(player) == 0) {
            map.put(player, 1);
        }
        else {
            final int playerLevel = map.get(player);
            map.put(player, playerLevel + 1);
        }
    }
    
    private float round(final double num) {
        final float p = (float)Math.pow(10.0, 1.0);
        final double number = num * p;
        final float tmp = Math.round(number);
        return tmp / p;
    }
    
    public void reset(final String player) {
        this.totalblock.put(player, 1);
        this.diamond.put(player, 0);
        this.iron.put(player, 0);
        this.gold.put(player, 0);
        this.redstone.put(player, 0);
        this.lapis.put(player, 0);
        this.totalblock.put(player, 0);
    }
    
    static {
        GREEN = ChatColor.GREEN;
        WHITE = ChatColor.WHITE;
        RED = ChatColor.RED;
        GRAY = ChatColor.GRAY;
    }
}
