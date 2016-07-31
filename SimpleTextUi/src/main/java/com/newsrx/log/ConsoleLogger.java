package com.newsrx.log;

import java.io.PrintStream;
import java.util.logging.ErrorManager;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

class ConsoleLogger extends Handler {
	private boolean useStdErr=false;
	public boolean isUseStdErr() {
		return useStdErr;
	}
	public void setUseStdErr(boolean useStdErr) {
		this.useStdErr = useStdErr;
	}

	public PrintStream out() {
		return isUseStdErr()?System.err:System.out;
	}

	private ErrorManager em = new ErrorManager() {
		@Override
		public synchronized void error(String msg, Exception ex, int code) {
			super.error(msg, ex, code);
			System.err.print(msg);
			ex.printStackTrace(System.err);
		}
	};

	public ConsoleLogger() {
		super();
		setLevel(Level.ALL);
		setErrorManager(em);
	}

	@Override
	public void publish(LogRecord record) {
		if (!isLoggable(record)) {
			return;
		}
		out().print(getFormatter().format(record));
		out().println();
	}

	@Override
	public void flush() {
		out().flush();
	}

	@Override
	public void close() throws SecurityException {
		flush();
	}
}