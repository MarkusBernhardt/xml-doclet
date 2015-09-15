package com.github.markusbernhardt.xmldoclet;

import org.slf4j.Logger;

/**
 * Enumeration implementing the LoggingLevel interface and sending all messages
 * to a SLF4j logger.
 * 
 * @author markus
 */
public enum LoggingLevelEnum implements LoggingLevel {
	/**
	 * Log level: INFO
	 */
	INFO {
		@Override
		public void log(Logger log, String message) {
			log.info(message);
		}
	},
	/**
	 * Log level: WARN
	 */
	WARN {
		@Override
		public void log(Logger log, String message) {
			log.warn(message);
		}
	},
	/**
	 * Log level: ERROR
	 */
	ERROR {
		@Override
		public void log(Logger log, String message) {
			log.error(message);
		}
	};
}