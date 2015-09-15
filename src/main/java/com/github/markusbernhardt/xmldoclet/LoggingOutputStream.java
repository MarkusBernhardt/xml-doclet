package com.github.markusbernhardt.xmldoclet;

import java.io.IOException;

import org.slf4j.Logger;

class LoggingOutputStream extends java.io.OutputStream {

	protected Logger log;
	protected LoggingLevelEnum loggingLevel;

	/**
	 * Used to maintain the contract of {@link #close()}.
	 */
	protected boolean hasBeenClosed = false;

	/**
	 * The internal buffer where data is stored.
	 */
	protected StringBuffer buffer = new StringBuffer();

	/**
	 * Creates the LoggingOutputStream to flush to the given Category.
	 * 
	 * @param log
	 *            the Logger to write to
	 * 
	 * @param loggingLevel
	 *            the log level
	 * 
	 * @exception IllegalArgumentException
	 *                if cat == null or priority == null
	 */
	public LoggingOutputStream(Logger log, LoggingLevelEnum loggingLevel) throws IllegalArgumentException {
		if (log == null) {
			throw new IllegalArgumentException("log == null");
		}

		this.loggingLevel = loggingLevel;
		this.log = log;
	}

	/**
	 * Closes this output stream and releases any system resources associated
	 * with this stream. The general contract of <code>close</code> is that it
	 * closes the output stream. A closed stream cannot perform output
	 * operations and cannot be reopened.
	 */
	@Override
	public void close() {
		flush();
		hasBeenClosed = true;
	}

	/**
	 * Writes the specified byte to this output stream. The general contract for
	 * <code>write</code> is that one byte is written to the output stream. The
	 * byte to be written is the eight low-order bits of the argument
	 * <code>b</code>. The 24 high-order bits of <code>b</code> are ignored.
	 * 
	 * @param b
	 *            the <code>byte</code> to write
	 */
	@Override
	public void write(final int b) throws IOException {
		if (hasBeenClosed) {
			throw new IOException("The stream has been closed.");
		}

		byte[] bytes = new byte[1];
		bytes[0] = (byte) (b & 0xff);
		String s = new String(bytes);
		if (s.equals("\n")) {
			flush();
		} else {
			buffer.append(s);
		}
	}

	/**
	 * Flushes this output stream and forces any buffered output bytes to be
	 * written out. The general contract of <code>flush</code> is that calling
	 * it is an indication that, if any bytes previously written have been
	 * buffered by the implementation of the output stream, such bytes should
	 * immediately be written to their intended destination.
	 */
	@Override
	public void flush() {
		String message = buffer.toString().trim();
		if (message.length() > 0) {
			loggingLevel.log(log, message);
		}
		reset();
	}

	private void reset() {
		buffer = new StringBuffer();
	}
}