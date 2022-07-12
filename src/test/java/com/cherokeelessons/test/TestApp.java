package com.cherokeelessons.test;

import com.cherokeelessons.gui.MainWindow.Config;
import com.cherokeelessons.log.Log;

import java.util.logging.Logger;

public class TestApp implements Runnable {

    protected Logger log = Log.getLogger(this);
    private boolean keepRunning = true;

    public TestApp(Config config) {
    }

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
        log.warning("setKeepRunning: " + keepRunning);
        this.keepRunning = keepRunning;
    }

}
