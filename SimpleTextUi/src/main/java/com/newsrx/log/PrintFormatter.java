package com.newsrx.log;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

class PrintFormatter extends Formatter {
	private final SimpleDateFormat sdf;

	public PrintFormatter() {
		sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss z");
		sdf.setTimeZone(TimeZone.getTimeZone("EST5EDT"));
	}
	
	@Override
	public String format(LogRecord record) {
		String format_date;
		StringBuilder sb = new StringBuilder();
		synchronized (sdf) {
			format_date = sdf.format(new java.util.Date(record.getMillis()));
		}
		sb.append(format_date);
		appendSourceClassName(sb, record);
		sb.append(", ");
		sb.append(record.getMessage());
		return sb.toString();
	}

	private void appendSourceClassName(StringBuilder sb, LogRecord record) {
		String sourceClassName = record.getSourceClassName();
		if (sourceClassName != null) {
			sb.append(" ");
			sb.append(sourceClassName);
		}
	}
}