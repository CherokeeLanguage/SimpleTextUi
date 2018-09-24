package com.cherokeelessons.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;

import com.cherokeelessons.log.Log;

public class MainWindow implements Runnable {

	public static abstract class Config {

		protected boolean autoExit = true;
		protected boolean autoExitOnError = false;
		protected boolean running = true;
		protected boolean singleInstance = true;
		protected int statusCode = -1;
		private File logfile;

		public abstract Runnable getApp(String... args) throws Exception;

		public abstract String getApptitle();

		public File getReportPathFile() {
			File f;
			f = new File(System.getProperty("user.home", "") + "/JavaPrograms");
			if (f.isDirectory()) {
				return new File(f, "reports");
			}
			return new File("reports");
		}

		public int getStatusCode() {
			return statusCode;
		}

		public boolean isAutoExit() {
			return autoExit;
		}

		public boolean isAutoExitOnError() {
			return autoExitOnError;
		}

		public boolean isRunning() {
			return running;
		}

		public boolean isSingleInstance() {
			return singleInstance;
		}

		public void setAutoExit(boolean autoExit) {
			this.autoExit = autoExit;
		}

		public void setAutoExitOnError(boolean autoExitOnError) {
			this.autoExitOnError = autoExitOnError;
		}

		public void setRunning(boolean running) {
			this.running = running;
		}

		public void setSingleInstance(boolean singleInstance) {
			this.singleInstance = singleInstance;
		}

		public void setStatusCode(int statusCode) {
			this.statusCode = statusCode;
		}

		public void setLogfile(File logfile) {
			this.logfile = logfile;
		}

		public File getLogfile() {
			return logfile;
		}

	}

	public static PrintStream logfilestream;

	public static void init(Config config, String... args) {
		EventQueue.invokeLater(new MainWindow(config, args));
	}

	private String[] args;
	private final Config config;
	private JFrame frame;

	private final Logger log;

	/**
	 * Create the application.<br/>
	 * Use: EventQueue.invokeLater(new MainWindow(config, args));
	 * 
	 * @param args
	 */
	public MainWindow(Config config, String... args) {
		log = Log.getLogger(this);
		if (args != null) {
			this.args = args;
		} else {
			this.args = new String[0];
		}
		this.config = config;
	}

	/**
	 * @return the frame
	 */
	public JFrame getFrame() {
		return frame;
	}

	protected final JScrollPane scrollPane = new JScrollPane();
	protected final JTextPane txtpnStartup = new JTextPane();
	private File logfile;

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	@SuppressWarnings("resource")
	private void initialize() {
		GregorianCalendar cal = new GregorianCalendar();
		Date today = cal.getTime();
		SimpleDateFormat date_format = new SimpleDateFormat("yyyyMMdd-HHmm");


		try {
			String tag = config.getApptitle();
			if (tag == null) {
				tag = "";
			}
			tag = tag.replaceAll(" ", "_");
			tag = tag.toLowerCase();
			tag = tag.replaceAll("[^a-z0-9\\-_]", "");
			if (!tag.isEmpty()) {
				tag = tag + "-";
			}
			config.getReportPathFile().mkdirs();
			logfile = new File(config.getReportPathFile(), tag + date_format.format(today) + "-log.odt");
			config.setLogfile(logfile);
			logfilestream = new PrintStream(logfile, "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logfilestream = System.err;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logfilestream = System.err;
		}
		
		TeeStream tee_stdout = new TeeStream(System.out, logfilestream);
		TeeStream tee_stderr = new TeeStream(System.err, logfilestream);

		boolean headless=GraphicsEnvironment.isHeadless();

		if (headless) {
			return;
		}
		
		frame = new JFrame();
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int screen_width = gd.getDisplayMode().getWidth();
		int width = screen_width * 90 / 100;
		int screen_height = gd.getDisplayMode().getHeight();
		int height = screen_height * 85 / 100;
		log.info("display size: " + screen_width + "x" + screen_height);
		log.info("frame size: " + width + "x" + height);

		frame.setTitle(config.getApptitle());
		frame.setBounds((screen_width - width) / 2, (screen_height - height) / 2, width, height);
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.setVisible(false);
				frame.dispose();
				System.exit(config.getStatusCode());
			}
		});
		scrollPane.setViewportView(txtpnStartup);

		mc = new MessageConsole(txtpnStartup);

		mc.redirectOut(Color.BLUE, tee_stdout);
		mc.redirectErr(Color.RED, tee_stderr);

		frame.setVisible(true);
		frame.validate();
		frame.repaint();
		frame.requestFocusInWindow();
	}

	protected MessageConsole mc;

	@Override
	public void run() {
		initialize();
		if (config.isSingleInstance()) {
			if (SingleInstance.isAlreadyRunning()) {
				if (config.isAutoExitOnError()) {
					new Thread(() -> {
						// fail safe exit timer
						new Timer().schedule(new TimerTask() {
							@Override
							public void run() {
								System.exit(config.statusCode);
							}
						}, 1000l * 30l);
					}).start();
				}
				throw new RuntimeException("This program is already running!");
			}
		}
		Runnable app;
		try {
			app = config.getApp(args);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		if (app instanceof AbstractApp) {
			((AbstractApp) app).setFontSizeHandler(mc);
			((AbstractApp) app).setjFrame(getFrame());
		}
		new Thread(app).start();
	}

	public File getLogfile() {
		return logfile;
	}

	public void setLogfile(File logfile) {
		this.logfile = logfile;
	}
}
