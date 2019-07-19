package net.dynamicdev.anticheat.util.rule;

import net.dynamicdev.anticheat.util.*;
import net.dynamicdev.anticheat.check.*;
import javax.script.*;
import java.util.*;

public class ConditionalRule extends Rule {
	
    private static ScriptEngineManager factory;
    private static ScriptEngine engine;
    private static final Type TYPE;
    
    public ConditionalRule(final String string) {
        super(string, ConditionalRule.TYPE);
    }
    
    @Override
    public boolean check(final User user, final CheckType type) {
        try {
            final SortedMap<String, Object> map = this.getVariables(user, type);
            for (final String key : map.keySet()) {
                ConditionalRule.engine.put(key, map.get(key));
            }
            final boolean value = (boolean)ConditionalRule.engine.eval(this.getString().split("\\?")[0]);
            final String next = value ? this.getString().split("\\?")[1].split(":")[0] : this.getString().split("\\?")[1].split(":")[1];
            this.execute(next, user, type);
            return value;
        }
        catch (ScriptException e) {
            e.printStackTrace();
            return true;
        }
    }
    
    private void execute(final String string, final User user, final CheckType type) {
        if (string.equalsIgnoreCase("null") || string.equalsIgnoreCase("none")) {
            return;
        }
        if (ConditionalRule.TYPE.matches(string)) {
            new ConditionalRule(string).check(user, type);
        }
        else if (this.isVariableSet(string)) {
            this.setVariable(string.split("=")[0], string.split("=")[1], user);
        }
        else if (this.isFunction(string)) {
            this.doFunction(string, type, user);
        }
    }
    
    static {
        ConditionalRule.factory = new ScriptEngineManager();
        ConditionalRule.engine = ConditionalRule.factory.getEngineByName("js");
        TYPE = Type.CONDITIONAL;
    }
}
