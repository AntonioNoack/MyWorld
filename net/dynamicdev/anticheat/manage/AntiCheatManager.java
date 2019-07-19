package net.dynamicdev.anticheat.manage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import org.bukkit.plugin.RegisteredListener;

import net.dynamicdev.anticheat.AntiCheat;
import net.dynamicdev.anticheat.check.Backend;

public class AntiCheatManager {
    private static AntiCheat plugin;
    
    // nehme OP Leute aus der Rechnung raus... für Tests so gut, aber später können besonders vertrauendwürdige Personen vllt dch raus genommen werden :)
    public boolean exemptOp = false;
    
    //private static Configuration configuration;
    private static XRayTracker xrayTracker;
    private static UserManager userManager;
    private static CheckManager checkManager;
    private static LoggingManager loggingManager;
    private static Backend backend;
    private static Map<String, RegisteredListener[]> eventchains;
    private static Map<String, Long> eventcache;
    
    public AntiCheatManager(final AntiCheat instance, final Logger logger) {
        AntiCheatManager.plugin = instance;
        //AntiCheatManager.configuration = new Configuration(AntiCheatManager.plugin, this);
        AntiCheatManager.loggingManager = new LoggingManager(AntiCheatManager.plugin, logger);
        AntiCheatManager.xrayTracker = new XRayTracker();
        AntiCheatManager.userManager = new UserManager(this);
        AntiCheatManager.checkManager = new CheckManager(this);
        AntiCheatManager.backend = new Backend(this);
    }
    
    public void log(final String message) {
        AntiCheatManager.loggingManager.log(message);
    }
    
    public void debugLog(final String message) {
        AntiCheatManager.loggingManager.debugLog(message);
    }
    
    public void playerLog(final String message) {
        AntiCheatManager.loggingManager.logToPlayers(message);
    }
    
    public void addEvent(final String e, final RegisteredListener[] arr) {
        //if (!AntiCheatManager.configuration.getConfig().eventChains.getValue()) {
        //    return;
       // }
        if (!AntiCheatManager.eventcache.containsKey(e) || AntiCheatManager.eventcache.get(e) > 30000L) {
            AntiCheatManager.eventchains.put(e, arr);
            AntiCheatManager.eventcache.put(e, System.currentTimeMillis());
        }
    }
    
    public String getEventChainReport() {
        String gen = "";
        //if (!AntiCheatManager.configuration.getConfig().eventChains.getValue()) {
        //    return "Event Chains is disabled by the configuration.\n";
        //}
        if (AntiCheatManager.eventchains.entrySet().size() == 0) {
            return "No event chains found.\n";
        }
        for (final Map.Entry<String, RegisteredListener[]> e : AntiCheatManager.eventchains.entrySet()) {
            String toadd = "";
            final String ename = e.getKey();
            toadd = toadd + ename + ":" + '\n';
            final RegisteredListener[] arr$ = e.getValue();
            for (final RegisteredListener plug : arr$) {
                String pluginname = plug.getPlugin().getName();
                if (pluginname.equals("AntiCheat")) {
                    pluginname = "self";
                }
                toadd = toadd + "- " + pluginname + '\n';
            }
            gen = gen + toadd + '\n';
        }
        return gen;
    }
    
    public AntiCheat getPlugin() {
        return AntiCheatManager.plugin;
    }
    
    public XRayTracker getXRayTracker() {
        return AntiCheatManager.xrayTracker;
    }
    
    public UserManager getUserManager() {
        return AntiCheatManager.userManager;
    }
    
    public CheckManager getCheckManager() {
        return AntiCheatManager.checkManager;
    }
    
    public Backend getBackend() {
        return AntiCheatManager.backend;
    }
    
    public LoggingManager getLoggingManager() {
        return AntiCheatManager.loggingManager;
    }
    
    public static void close() {
        AntiCheatManager.loggingManager.closeHandler();
        // auch in der Config eigentlich auf false
        //if (AntiCheatManager.configuration.getConfig().enterprise.getValue()) {
        //    AntiCheatManager.configuration.getEnterprise().database.shutdown();
        //}
    }
    
    static {
        AntiCheatManager.plugin = null;
        AntiCheatManager.xrayTracker = null;
        AntiCheatManager.userManager = null;
        AntiCheatManager.checkManager = null;
        AntiCheatManager.loggingManager = null;
        AntiCheatManager.backend = null;
        AntiCheatManager.eventchains = new ConcurrentHashMap<String, RegisteredListener[]>();
        AntiCheatManager.eventcache = new ConcurrentHashMap<String, Long>();
    }
}
