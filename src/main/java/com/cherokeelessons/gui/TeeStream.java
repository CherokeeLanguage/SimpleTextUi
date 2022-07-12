package com.cherokeelessons.gui;

import java.io.PrintStream;

/**
 * Based loosely on: {@link http://stackoverflow.com/questions/1356706/copy-stdout-to-file-without-stopping-it-showing-onscreen}
 */
public class TeeStream extends PrintStream {
	private final PrintStream tee;

	public TeeStream(PrintStream first, PrintStream tee) {
		super(first, true);
		this.tee = tee;
	}

	@Override
	public boolean checkError() {
		return super.checkError() || tee.checkError();
	}

	@Override
	public void close() {
		super.close();
		tee.close();
	}

	@Override
	public void flush() {
		super.flush();
		tee.flush();
	}

	@Override
	public void write(byte[] x, int o, int l) {
		super.write(x, o, l);
		tee.write(x, o, l);
	}

	@Override
	public void write(int x) {
		super.write(x);
		tee.write(x);
	}
}
