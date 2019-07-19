package net.dynamicdev.anticheat.manage;

import net.dynamicdev.anticheat.AntiCheat;
import net.dynamicdev.anticheat.check.CheckType;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class XRayListener implements Listener {
    private XRayTracker tracker;
    private CheckManager checkManager;
    
    public XRayListener() {
        this.tracker = AntiCheat.getManager().getXRayTracker();
        this.checkManager = AntiCheat.getManager().getCheckManager();
    }
    
    @EventHandler
    public void onBlockBreak(final BlockBreakEvent event) {
        if (true) {
            final Player p = event.getPlayer();
            if (p.getGameMode() == GameMode.CREATIVE) {
                return;
            }
            final String player = p.getName();
            if (this.checkManager.willCheck(p, CheckType.XRAY)) {
                final Material m = event.getBlock().getType();
                if (m == Material.DIAMOND_ORE) {
                    this.tracker.addDiamond(player);
                } else if (m == Material.IRON_ORE) {
                    this.tracker.addIron(player);
                } else if (m == Material.GOLD_ORE) {
                    this.tracker.addGold(player);
                } else if (m == Material.LAPIS_ORE) {
                    this.tracker.addLapis(player);
                } else if (m == Material.REDSTONE_ORE || m == Material.GLOWING_REDSTONE_ORE) {
                    this.tracker.addRedstone(player);
                } else if (m == Material.GOLD_ORE) {
                    this.tracker.addGold(player);
                } else {
                    this.tracker.addBlock(player);
                }
                this.tracker.addTotal(player);
            }
        }
    }
}
