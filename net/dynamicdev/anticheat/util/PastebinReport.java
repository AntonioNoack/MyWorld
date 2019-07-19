package net.dynamicdev.anticheat.util;

import org.bukkit.command.*;
import org.bukkit.entity.*;
import java.net.*;
import java.io.*;

public class PastebinReport {
    //private static final String DATE;
    //private static final String API_KEY = "e86d2991b6119f0e16ebb870ceed1d4b";
    private StringBuilder report;
    private String url;
    
    /*public PastebinReport(final CommandSender cs) {
        this.report = new StringBuilder();
        this.url = "";
        Player player = null;
        if (cs instanceof Player) {
            player = (Player)cs;
        }
        this.createReport(player);
        try {
            this.writeReport();
        }
        catch (IOException ex) {}
        this.postReport();
    }
    */
    public PastebinReport(final CommandSender cs, final Player tp) {
        this.report = new StringBuilder();
        this.url = "";
        this.createReport(tp);
        try {
            this.writeReport();
        }
        catch (IOException ex) {}
        this.postReport();
    }
    
    public String getURL() {
        return this.url;
    }
    
    /*private void appendPermissionsTester(final Player player) {
        if (player == null) {
            this.append("No player defined.");
            return;
        }
        for (final Permission node : Permission.values()) {
            this.report.append(player.getName() + ": " + node.toString() + " " + node.get((CommandSender)player));
            if (node.get((CommandSender)player) && !node.whichPermission((CommandSender)player).equals(node.toString())) {
                this.report.append(" (Applied by " + node.whichPermission((CommandSender)player) + ")");
            }
            this.report.append('\n');
        }
    }*/
    
    private void createReport(final Player player) {
        /*if (Bukkit.getPluginManager().getPlugin("NoCheatPlus") != null) {
            this.append("------------ WARNING! ------------");
            this.append("This report was run with NoCheatPlus enabled. Results may be inaccurate.\n");
        }
        this.append("------------ AntiCheatPlus Report - " + PastebinReport.DATE + " ------------");
        this.appendSystemInfo();
        this.append("------------Last 30 logs------------");
        this.appendLogs();
        this.append("------------Permission Tester------------");
        this.appendPermissionsTester(player);
        this.append("------------Event Chains------------");
        this.appendEventHandlers();
        this.append("------------Magic Diff------------");
        this.appendMagicDiff();
        this.append("-----------End Of Report------------");*/
    }
    
    /*private void appendLogs() {
        final List<String> logs = AntiCheat.getManager().getLoggingManager().getLastLogs();
        if (logs.size() == 0) {
            this.append("No recent logs.");
            return;
        }
        for (final String log : logs) {
            this.append(log);
        }
    }*/
    
    /*private void appendSystemInfo() {
        final Runtime runtime = Runtime.getRuntime();
        final Configuration config = AntiCheat.getManager().getConfiguration();
        this.append("AntiCheat Version: " + AntiCheat.getVersion() + (AntiCheat.isUpdated() ? "" : " (OUTDATED)"));
        this.append("Server Version: " + Bukkit.getVersion());
        this.append("Server Implementation: " + Bukkit.getName());
        this.append("Server ID: " + Bukkit.getServerId());
        this.append("Java Version: " + System.getProperty("java.vendor") + " " + System.getProperty("java.version"));
        this.append("OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
        this.append("Free Memory: " + runtime.freeMemory() / 1024L / 1024L + "MB");
        this.append("Max Memory: " + runtime.maxMemory() / 1024L / 1024L + "MB");
        this.append("Total Memory: " + runtime.totalMemory() / 1024L / 1024L + "MB");
        this.append("Online Mode: " + Bukkit.getOnlineMode());
        this.append("Players: " + Bukkit.getOnlinePlayers().length + "/" + Bukkit.getMaxPlayers());
        this.append("Plugin Count: " + Bukkit.getPluginManager().getPlugins().length);
        //this.append("Plugin Uptime: " + (System.currentTimeMillis() - AntiCheat.getPlugin().getLoadTime()) / 1000L / 60L + " minutes");
        this.append("Enterprise: " + config.getConfig().enterprise.getValue());
        if (config.getConfig().enterprise.getValue()) {
            this.append("- Server name: " + config.getEnterprise().serverName.getValue());
            this.append("- Database type: " + config.getEnterprise().database.getType());
            this.append("- Groups source: " + (config.getEnterprise().configGroups.getValue() ? "Database" : "Flatfile"));
            this.append("- Rules source: " + (config.getEnterprise().configRules.getValue() ? "Database" : "Flatfile"));
            this.append("- Levels source: " + (config.getEnterprise().syncLevels.getValue() ? "Database" : "Flatfile"));
        }
    }*/
    
    /*private void appendMagicDiff() {
        final Magic magic = AntiCheat.getManager().getConfiguration().getMagic();
        final FileConfiguration file = (FileConfiguration)YamlConfiguration.loadConfiguration(me.corperateraider.myworld.Plugin.instance.getResource("magic.yml"));
        boolean changed = false;
        for (final Field field : Magic.class.getFields()) {
            final Object defaultValue = file.get(field.getName());
            try {
                final Field value = magic.getClass().getDeclaredField(field.getName());
                final String s1 = value.get(magic).toString();
                final String s2 = defaultValue.toString();
                if (!s1.equals(s2) && !s1.equals(s2 + ".0")) {
                    changed = true;
                    this.append(field.getName() + ": " + s1 + " (Default: " + s2 + ")");
                }
            }
            catch (NoSuchFieldException ex) {}
            catch (IllegalAccessException ex2) {}
        }
        if (!changed) {
            this.append("No changes from default.");
        }
    }*/
    
    /*private void appendEventHandlers() {
        this.report.append(AntiCheat.getManager().getEventChainReport());
    }*/
    
    private void writeReport() throws IOException {
        final File f = new File(me.corperateraider.myworld.Plugin.instance.getDataFolder() + "/report.txt");
        final FileWriter r = new FileWriter(f);
        final BufferedWriter writer = new BufferedWriter(r);
        writer.write(this.report.toString());
        writer.close();
    }
    
    private void postReport() {
        try {
            final URL urls = new URL("http://pastebin.com/api/api_post.php");
            final HttpURLConnection conn = (HttpURLConnection)urls.openConnection();
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("POST");
            conn.addRequestProperty("Content-type", "application/x-www-form-urlencoded");
            conn.setInstanceFollowRedirects(false);
            conn.setDoOutput(true);
            final OutputStream out = conn.getOutputStream();
            out.write(("api_option=paste&api_dev_key=" + URLEncoder.encode("e86d2991b6119f0e16ebb870ceed1d4b", "utf-8") + "&api_paste_code=" + URLEncoder.encode(this.report.toString(), "utf-8") + "&api_paste_private=" + URLEncoder.encode("1", "utf-8") + "&api_paste_name=" + URLEncoder.encode("", "utf-8") + "&api_paste_expire_date=" + URLEncoder.encode("1M", "utf-8") + "&api_paste_format=" + URLEncoder.encode("text", "utf-8") + "&api_user_key=" + URLEncoder.encode("", "utf-8")).getBytes());
            out.flush();
            out.close();
            if (conn.getResponseCode() == 200) {
                final InputStream receive = conn.getInputStream();
                final BufferedReader reader = new BufferedReader(new InputStreamReader(receive));
                final StringBuffer response = new StringBuffer();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                    response.append("\r\n");
                }
                reader.close();
                final String result = response.toString().trim();
                if (!result.contains("http://")) {
                    this.url = "Failed to post.  Check report.txt";
                }
                else {
                    this.url = result.trim();
                }
            } else {
                this.url = "Failed to post.  Check report.txt";
            }
        } catch (Exception e) {
            this.url = "Failed to post.  Check report.txt";
        }
    }
    
    //private void append(final String s) {
    //    this.report.append(s + '\n');
    //}
    
    //static {
        //DATE = new SimpleDateFormat("yyyy-MM-dd kk:mm Z").format(new Date());
    //}
}
