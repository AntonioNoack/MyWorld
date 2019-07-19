package net.dynamicdev.anticheat.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.*;
import java.util.logging.Formatter;
import java.util.*;

public class FileFormatter extends Formatter {
	
    private static final DateFormat FORMAT;
    
    @Override
    public String format(final LogRecord record) {
    	return "["+record.getLevel()+"|"+FileFormatter.FORMAT.format(new Date(record.getMillis()))+"]: "+record.getMessage()+" \n";
    }
    
    static {
        FORMAT = new SimpleDateFormat("h:mm:ss");
    }
}
