package com.github.markusbernhardt.xmldoclet;

import org.slf4j.Logger;

public enum LoggingLevelEnum implements LoggingLevel {
	INFO {
		@Override
		public void log(Logger log, String message) {
			log.info(message);
		}
	},
	WARN {
		@Override
		public void log(Logger log, String message) {
			log.warn(message);
		}
	},
	ERROR {
		@Override
		public void log(Logger log, String message) {
			log.error(message);
		}
	};
}