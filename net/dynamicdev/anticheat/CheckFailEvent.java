package net.dynamicdev.anticheat;

import org.bukkit.event.*;
import net.dynamicdev.anticheat.util.*;
import net.dynamicdev.anticheat.check.*;

public class CheckFailEvent extends Event {
	
    private static final HandlerList handlers;
    private final User user;
    private final CheckType type;
    
    public CheckFailEvent(final User user, final CheckType type) {
        this.user = user;
        this.type = type;
    }
    
    public User getUser() {
        return this.user;
    }
    
    public CheckType getCheck() {
        return this.type;
    }
    
    public HandlerList getHandlers() {
        return CheckFailEvent.handlers;
    }
    
    static {
        handlers = new HandlerList();
    }
}
