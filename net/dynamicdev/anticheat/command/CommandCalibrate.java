package net.dynamicdev.anticheat.command;

import net.dynamicdev.anticheat.util.Calibrator;
import net.dynamicdev.anticheat.util.Permission;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandCalibrate extends CommandBase {
    private static final Permission PERMISSION;
    private static final String[] HELP;
    
    public CommandCalibrate() {
        super("AntiCheat Calibration", "calibrate", "anticheat calibrate", CommandCalibrate.HELP, CommandCalibrate.PERMISSION);
    }
    
    @Override
    protected void execute(final CommandSender cs, final String[] args) {
        new Calibrator((Player)cs);
    }
    
    static {
        PERMISSION = Permission.SYSTEM_CALIBRATE;
        HELP = new String[] { CommandCalibrate.GRAY + "Use: " + CommandCalibrate.AQUA + "/anticheat calibrate" + CommandCalibrate.GRAY + " to calibrate AntiCheat settings" };
    }
}
