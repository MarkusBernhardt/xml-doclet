package com.github.markusbernhardt.xmldoclet;

import org.slf4j.Logger;

/**
 * Public interface for a logging level class.
 * 
 * @author markus
 */
public interface LoggingLevel {

	/**
	 * Intefrace mehtod.
	 * 
	 * @param log
	 *            the logger to log to
	 * @param message
	 *            the message to log to the given logger.
	 */
	public void log(Logger log, String message);
}
