package net.dynamicdev.anticheat.util;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class Calibrator {
    private final me.corperateraider.myworld.Plugin plugin;
    private final Server server;
    private final Player player;
    
    public Calibrator(final Player player) {
        this.plugin = me.corperateraider.myworld.Plugin.instance;
        this.server = plugin.getServer();
        (this.player = player).sendMessage("You have entered calibration mode.");
    }
    
    public Player getPlayer() {
        return this.player;
    }
    
    private void registerEvents(final CalibrationStep step) {
        this.server.getPluginManager().registerEvents((Listener)step, (Plugin) this.plugin);
    }
    
    private void unregisterEvents(final CalibrationStep step) {
        HandlerList.unregisterAll((Listener)step);
    }
    
    public class CalibrationStep implements Listener {
        private final String key;
        private int trials;
        public static final int MAX_TRIALS = 10;
        
        public CalibrationStep(final String key, final String instruction) {
            this.trials = 0;
            this.key = key;
            Calibrator.this.registerEvents(this);
            Calibrator.this.getPlayer().sendMessage(instruction);
        }
        
        public void end() {
            Calibrator.this.unregisterEvents(this);
        }
        
        public long getTime() {
            return System.currentTimeMillis();
        }
        
        public String getKey() {
            return this.key;
        }
        
        public int addTrial() {
            return this.trials++;
        }
    }
    
    public class MinimumCalibrationStep extends CalibrationStep {
        public MinimumCalibrationStep(final String key, final String instruction) {
            super(key, instruction);
        }
        
        public void value(){}
    }
}
