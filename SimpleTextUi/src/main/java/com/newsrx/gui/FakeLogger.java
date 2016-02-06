package com.newsrx.gui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class FakeLogger {
	private final SimpleDateFormat sdf;
	private final String caller;
	public FakeLogger() {
		String tmp = "";
		sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
		sdf.setTimeZone(TimeZone.getTimeZone("EST5EDT"));
		StackTraceElement[] t = Thread.currentThread().getStackTrace();
		if (t!=null && t.length>3) {
			tmp = t[3].getClassName();
			if (tmp!=null && tmp.contains(".")){
				tmp=tmp.substring(tmp.lastIndexOf(".")+1);
			}
		}
		caller = tmp;
	}
	public void info(String msg) {
		System.out.println(sdf.format(new Date())+" ["+caller+"], "+msg);
	}
	public void warning(String msg) {
		System.out.println(sdf.format(new Date())+" ["+caller+"], "+msg);		
	}
	public void severe(String msg) {
		System.err.println(sdf.format(new Date())+" ["+caller+"], "+msg);		
	}
	public void info() {
		info("");
	}
	
}
