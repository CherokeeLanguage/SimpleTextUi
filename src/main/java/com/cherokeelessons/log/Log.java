package com.cherokeelessons.log;

import java.util.HashMap;
import java.util.logging.*;

public class Log {
    private static final HashMap<String, Logger> loggers = new HashMap<>();

    public static Logger getLogger() {
        return getLogger("");
    }

    public static Logger getLogger(Object _class) {
        if (_class == null) {
            return getLogger();
        }
        return getLogger(_class.getClass().getName());
    }

    public static Logger getLogger(String tag) {
        if (tag == null) {
            return getLogger();
        }
        if (!loggers.containsKey(tag)) {
            Logger logger = Logger.getLogger(tag);
            logger.setUseParentHandlers(false);
            for (Handler handler : logger.getHandlers()) {
                logger.removeHandler(handler);
            }
            ConsoleLogger err = new ConsoleLogger();
            err.setUseStdErr(true);
            err.setFormatter(new PrintFormatter());
            err.setLevel(Level.ALL);
            err.setFilter(new Filter() {
                @Override
                public boolean isLoggable(LogRecord r) {
                    return r.getLevel().intValue() > Level.INFO.intValue();
                }
            });
            logger.addHandler(err);
            ConsoleLogger info = new ConsoleLogger();
            info.setUseStdErr(false);
            info.setLevel(Level.ALL);
            info.setFormatter(new PrintFormatter());
            info.setFilter(new Filter() {
                @Override
                public boolean isLoggable(LogRecord r) {
                    return r.getLevel().intValue() < Level.WARNING.intValue();
                }
            });
            logger.addHandler(info);
            loggers.put(tag, logger);
        }
        return loggers.get(tag);
    }
}