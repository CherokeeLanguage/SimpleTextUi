package com.cherokeelessons.gui;

import com.cherokeelessons.gui.MainWindow.Config;
import com.cherokeelessons.log.Log;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

public abstract class AbstractApp implements Runnable {
    protected final Logger log = Log.getLogger(this);
    protected final Config config;
    protected final String[] args;
    protected JFrame jFrame;
    protected Runnable onSuccess = null;
    protected Runnable onError = null;
    private FontSizeHandler fontResizer;

    public AbstractApp(MainWindow.Config config, String[] args) {
        this.config = config;
        this.args = args;
    }

    protected int getFontSize() {
        if (fontResizer == null) {
            return -1;
        }
        return fontResizer.getFontSize();
    }

    protected void setFontSize(int size) {
        if (fontResizer == null) {
            return;
        }
        fontResizer.setFontSize(size);
    }

    @Override
    public void run() {

        try {
            parseArgs(Arrays.asList(args).iterator());
            execute();
            config.statusCode = 0;
            System.out.flush();
            System.err.flush();
            if (onSuccess != null) {
                EventQueue.invokeAndWait(onSuccess);
            }
            config.running = false;
            if (config.isAutoExit()) {
                System.exit(config.getStatusCode());
            }
        } catch (Exception e) {
            config.statusCode = -1;
            System.out.flush();
            System.err.flush();
            e.printStackTrace();
            System.err.flush();
            if (onError != null) {
                try {
                    EventQueue.invokeAndWait(onError);
                } catch (InvocationTargetException | InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
            config.running = false;
            if (config.isAutoExitOnError()) {
                System.exit(config.getStatusCode());
            }
            throw new RuntimeException("FATAL ERROR");
        }
    }

    protected void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
        }
    }

    protected abstract void parseArgs(Iterator<String> iargs);

    protected abstract void execute() throws IOException, SecurityException, Exception;

    /**
     * 4 hour timer with exit with last recorded status.
     *
     * @return
     */
    public Runnable failsafeExitTimer() {
        return new Runnable() {
            @Override
            public void run() {
                // fail safe exit timer
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        System.exit(config.statusCode);
                    }
                }, 1000l * 60l * 60l * 4l);
            }
        };
    }

    public void setFontSizeHandler(FontSizeHandler handler) {
        this.fontResizer = handler;
    }

    public JFrame getjFrame() {
        return jFrame;
    }

    public void setjFrame(JFrame jFrame) {
        this.jFrame = jFrame;
    }

    public Runnable getOnSuccess() {
        return onSuccess;
    }

    public void setOnSuccess(Runnable onSuccess) {
        this.onSuccess = onSuccess;
    }

    public Runnable getOnError() {
        return onError;
    }

    public void setOnError(Runnable onError) {
        this.onError = onError;
    }

}
