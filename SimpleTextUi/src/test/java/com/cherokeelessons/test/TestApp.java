package com.cherokeelessons.test;

import java.util.logging.Logger;

import com.newsrx.gui.MainWindow.Config;
import com.newsrx.log.Log;

public class TestApp implements Runnable {
	
	protected Logger log = Log.getLogger(this);
	
	public TestApp(Config config) {
	}

	private boolean keepRunning=true;
	@Override
	public void run() {
		Logger log = Log.getLogger("LoggingTest");
		log.info("log.info#LoggingTest");
		log.warning("log.warning#LoggingTest");
		while (isKeepRunning()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		log.warning("run#done");
	}
	public boolean isKeepRunning() {
		return keepRunning;
	}
	public void setKeepRunning(boolean keepRunning) {
		log.warning("setKeepRunning: "+keepRunning);
		this.keepRunning = keepRunning;
	}

}
