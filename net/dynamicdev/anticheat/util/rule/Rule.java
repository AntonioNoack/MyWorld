package net.dynamicdev.anticheat.util.rule;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import net.dynamicdev.anticheat.AntiCheat;
import net.dynamicdev.anticheat.check.CheckType;
import net.dynamicdev.anticheat.util.Group;
import net.dynamicdev.anticheat.util.User;
import net.dynamicdev.anticheat.util.Utilities;

import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;

public class Rule {
	static ArrayList<Rule> rules;
	public static List<Rule> getRules(){
		if(rules == null){
			rules = new ArrayList<>();
			rules.add(new Rule("Check_SPIDER < 0 ? Player.KICK : null", Type.CONDITIONAL));
		}
		return rules;
	}
	
    private static String string;
    private Type type;
    
    public Rule(final String string, final Type type) {
        Rule.string = Utilities.removeWhitespace(string).toLowerCase();
        this.type = type;
    }
    
    public boolean check(final User user, final CheckType type) {
        return true;
    }
    
    public static Rule load(final String string) {
        for (final Type type : Type.values()) {
            if (type.matches(string)) {
                try {
                    final Class<?> c = Class.forName(type.getInstance());
                    final Constructor<?> con = c.getConstructor(String.class);
                    return (Rule) con.newInstance(string);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        return null;
    }
    
    public Type getType() {
        return this.type;
    }
    
    protected String getString() {
        return Rule.string;
    }
    
    protected SortedMap<String, Object> getVariables(final User user, final CheckType type) {
        final SortedMap<String, Object> map = new TreeMap<String, Object>();
        map.put("player_check", type.name().toLowerCase());
        map.put("player_level", user.getLevel());
        map.put("player_group", (user.getGroup() != null) ? user.getGroup().getName().toLowerCase() : "low");
        map.put("player_name", user.getName().toLowerCase());
        map.put("player_gamemode", user.getPlayer().getGameMode().toString());
        map.put("player_world", user.getPlayer().getWorld().getName());
        map.put("player_health", ((CraftPlayer)user.getPlayer()).getHandle().getHealth());
        for (final CheckType t : CheckType.values()) {
            map.put("check_" + t.name().toLowerCase(), t.getUses(user.getName()));
        }
        return map;
    }
    
    protected void setVariable(final String variable, final String value, final User user) {
        if (variable.equals("player_level") && Utilities.isInt(value)) {
            user.setLevel(Integer.parseInt(value));
        } else if (variable.equals("player_group") && Utilities.isInt(value)) {
            for (final Group group : Group.getGroups()) {
                if (group.getName().equalsIgnoreCase(value)) {
                    user.setLevel(group.getLevel());
                }
            }
        } else if (variable.equals("player_gamemode")) {
            try {
                final GameMode mode = GameMode.valueOf(value);
                user.getPlayer().setGameMode(mode);
            } catch (IllegalArgumentException ex) {}
        } else if (variable.equals("player_health") && Utilities.isDouble(value)) {
            user.getPlayer().setHealth(Double.parseDouble(value));
        }
    }
    
    protected void doFunction(String text, final CheckType type, final User user) {
        if (text.toLowerCase().startsWith("player")) {
            AntiCheat.getManager().getUserManager().execute(user, new String[]{text.split("\\.")[1]}, type);
        }
    }
    
    protected boolean isFunction(final String string) {
        return string.matches(".*\\..*");
    }
    
    protected boolean isVariableSet(final String string) {
        return string.matches(".*(_).*=.*");
    }
    
    @Override
    public String toString() {
        return this.type + "{" + Rule.string + "}";
    }
    
    public enum Type {
        CONDITIONAL(".*[?]*.*:.*", "net.gravitydevelopment.anticheat.util.rule.ConditionalRule");
        
        private String regex;
        private String c;
        
        private Type(final String regex, final String c) {
            this.regex = regex;
            this.c = c;
        }
        
        public boolean matches(final String s) {
            return s.matches(this.regex);
        }
        
        public String getInstance() {
            return this.c;
        }
    }
}
