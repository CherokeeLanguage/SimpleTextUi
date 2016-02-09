package com.newsrx.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

public class MainWindow implements Runnable {

	public static abstract class Config {
		public abstract Thread getApp(String... args) throws Exception;

		public File getReportPathFile() {
			File f = new File(System.getProperty("user.home","")+"/JavaPrograms");
			if (f.isDirectory()) {
				return new File(f, "reports");
			}
			f = new File("F:\\JavaPrograms\\");
			if (f.isDirectory()) {
				return new File(f, "reports");
			}
			f = new File("E:\\JavaPrograms\\");
			if (f.isDirectory()) {
				return new File(f, "reports");
			}
			f = new File("F:\\");
			if (f.isDirectory()) {
				return new File(f, "reports");
			}
			f = new File("E:\\");
			if (f.isDirectory()) {
				return new File(f, "reports");
			}
			return new File("reports");
		}

		public abstract String getApptitle();
	}

	private JFrame frame;
	/**
	 * @return the frame
	 */
	public JFrame getFrame() {
		return frame;
	}

	private final Config config;
	private String[] args;
	private final FakeLogger log;

	/**
	 * Create the application.<br/>
	 * Use: EventQueue.invokeLater(new MainWindow(config, args));
	 * 
	 * @param args
	 */
	public MainWindow(Config config, String... args) {
		log=new FakeLogger();
		if (args != null) {
			this.args = args;
		} else {
			this.args = new String[0];
		}
		this.config = config;
		initialize();
	}

	public static PrintStream logfile;

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @throws FileNotFoundException
	 * @throws UnsupportedEncodingException
	 */
	private void initialize() {
		Calendar cal = Calendar.getInstance();
		Date today = cal.getTime();
		SimpleDateFormat date_format = new SimpleDateFormat("yyyyMMdd-HHmm");

		JScrollPane scrollPane = new JScrollPane();
		JTextPane txtpnStartup = new JTextPane();

		frame = new JFrame();
		frame.setVisible(true);

		try {
			String tag = config.getApptitle();
			if (tag==null) {
				tag="";
			}
			tag=tag.replaceAll(" ", "_");
			tag=tag.toLowerCase();
			tag=tag.replaceAll("[^a-z0-9\\-_]", "");
			if (!tag.isEmpty()) {
				tag=tag+"-";
			}
			config.getReportPathFile().mkdirs();
			File file = new File(config.getReportPathFile(), tag + date_format.format(today) + "-log.odt");
			logfile = new PrintStream(file, "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logfile = System.err;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			logfile = System.err;

		}
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int screen_width = gd.getDisplayMode().getWidth();
		int width = screen_width * 75 / 100;
		int screen_height = gd.getDisplayMode().getHeight();
		int height = screen_height * 75 / 100;
		log.info("display size: " + screen_width + "x" + screen_height);
		log.info("frame size: " + width + "x" + height);

		frame.setTitle(config.getApptitle());
		frame.setBounds((screen_width - width) / 2, (screen_height - height) / 2, width, height);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
		scrollPane.setViewportView(txtpnStartup);
		
		TeeStream tee_stdout = new TeeStream(System.out, logfile);
		TeeStream tee_stderr = new TeeStream(System.err, logfile);
		
		MessageConsole mc = new MessageConsole(txtpnStartup);
		
		mc.redirectOut(Color.BLUE, tee_stdout);
		mc.redirectErr(Color.RED, tee_stderr);
		
		log.warning("");
		log.warning("= " + config.getApptitle());
		log.warning("");
	}

	@Override
	public void run() {
		log.info("MainWindow#run");
		Thread app;
		try {
			app = config.getApp(args);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			System.out.flush();
			System.err.flush();
		}
		app.start();
	}
}
