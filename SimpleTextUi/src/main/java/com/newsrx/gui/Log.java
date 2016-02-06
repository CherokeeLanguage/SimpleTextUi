package com.newsrx.gui;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Log {
	private static String LOG_TAG = "[LOGGER] ";
	public static String getLOG_TAG() {
		return LOG_TAG;
	}
	
	public static void setLOG_TAG(String lOG_TAG) {
		LOG_TAG = lOG_TAG;
	}

	private static final ConsoleHandler handler = new ConsoleHandler();
	
	private static final Formatter f1 = new Formatter() {
		private final SimpleDateFormat sdf;
		{
			sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss z");
			sdf.setTimeZone(TimeZone.getTimeZone("EST5EDT"));
		}
		@Override
		public String format(LogRecord record) {
			StringBuilder sb = new StringBuilder(256);
			sb.append(LOG_TAG);
			sb.append(sdf.format(new java.util.Date(record.getMillis())));
			appendSourceClassName(sb, record);
			sb.append(", ");
			sb.append(record.getMessage());
			sb.append(System.lineSeparator());
			return sb.toString();
		}

	};

	private static void appendSourceClassName(StringBuilder sb, LogRecord record) {
		String sourceClassName = record.getSourceClassName();
		if (sourceClassName != null) {
			sb.append(" ");
			sb.append(sourceClassName);
		}
	}

	public static Logger getLogger() {
		return getLogger(LOG_TAG);
	}

	public static Logger getLogger(String tag) {
		Logger log = Logger.getLogger(tag);
		handler.setFormatter(f1);
		log.setUseParentHandlers(false);
		log.addHandler(handler);
		return log;
	}

}