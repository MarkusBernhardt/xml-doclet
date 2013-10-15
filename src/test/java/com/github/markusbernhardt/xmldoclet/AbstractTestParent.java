package com.github.markusbernhardt.xmldoclet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractTestParent {

	private final static Logger log = LoggerFactory.getLogger(AbstractTestParent.class);

	/**
	 * Processes the source code using javadoc.
	 * 
	 * @param extendedClassPath
	 *            Any classpath information required to help along javadoc.
	 *            Javadoc will actualy compile the source code you specify; so
	 *            if there are any jars or classes that are referenced by the
	 *            source code to process, then including those compiled items in
	 *            the classpath will give you more complete data in the
	 *            resulting XML.
	 * @param sourcePaths
	 *            Usually sourcePaths is specified in conjuction with
	 *            either/both packages & subpackages. The sourcepaths value
	 *            should be the path of the source files right before the
	 *            standard package-based folder layout of projects begins. For
	 *            example, if you have code that exists in package foo.bar, and
	 *            your code is physically in /MyFolder/foo/bar/ , then the
	 *            sourcePaths would be /MyFolder
	 * @param packages
	 *            Use if you want to detail specific packages to process
	 *            (contrast with subpackages, which is probably the easiest/most
	 *            brute force way of using xml-doclet). If you have within your
	 *            code two packages, foo.bar and bar.foo, but only wanted
	 *            foo.bar processed, then specify just 'foo.bar' for this
	 *            argument.
	 * @param sourceFiles
	 *            You can specify source files individually. This usually is
	 *            used instead of sourcePaths/subPackages/packages. If you use
	 *            this parameter, specify the full path of any java file you
	 *            want processed.
	 * @param subPackages
	 *            You can specify 'subPackages', which simply gives one an easy
	 *            way to specify the root package, and have javadoc recursively
	 *            look through everything under that package. So for instance,
	 *            if you had foo.bar, foo.bar.bar, and bar.foo, specifying 'foo'
	 *            will process foo.bar and foo.bar.bar packages, but not bar.foo
	 *            (unless you specify 'bar' as a subpackage, too)
	 * @return XStream compatible data structure
	 */
	public void executeJavadoc(String extendedClassPath, String[] sourcePaths, String[] packages, String[] sourceFiles,
			String[] subPackages) {
		try {
			OutputStream errors = new LoggingOutputStream(log, LoggingLevelEnum.ERROR);
			OutputStream warnings = new LoggingOutputStream(log, LoggingLevelEnum.WARN);
			OutputStream notices = new LoggingOutputStream(log, LoggingLevelEnum.INFO);

			PrintWriter errorWriter = new PrintWriter(errors, false);
			PrintWriter warningWriter = new PrintWriter(warnings, false);
			PrintWriter noticeWriter = new PrintWriter(notices, false);

			// aggregate arguments and packages
			ArrayList<String> argumentList = new ArrayList<String>();

			// by setting this to 'private', nothing is omitted in the parsing
			argumentList.add("-private");

			String classPath = System.getProperty("java.class.path", ".");
			if (extendedClassPath != null) {
				classPath += File.pathSeparator + extendedClassPath;
			}
			argumentList.add("-classpath");
			argumentList.add(classPath);

			String concatedSourcePaths = join(File.pathSeparator, sourcePaths);
			if (concatedSourcePaths.length() > 0) {
				argumentList.add("-sourcepath");
				argumentList.add(concatedSourcePaths);
			}

			String concatedSubPackages = join(";", subPackages);
			if (concatedSubPackages.length() > 0) {
				argumentList.add("-subpackages");
				argumentList.add(concatedSubPackages);
			}

			if (packages != null) {
				argumentList.addAll(java.util.Arrays.asList(packages));
			}

			if (sourceFiles != null) {
				argumentList.addAll(Arrays.asList(sourceFiles));
			}

			log.info("Executing doclet with arguments: " + join(" ", argumentList));

			String[] arguments = argumentList.toArray(new String[] {});
			com.sun.tools.javadoc.Main.execute("xmldoclet", errorWriter, warningWriter, noticeWriter,
					XmlDoclet.class.getName(), arguments);

			errors.close();
			warnings.close();
			notices.close();

			log.info("done with doclet processing");
		} catch (Exception e) {
			log.error("doclet exception", e);
		} catch (Error e) {
			log.error("doclet error", e);
		}
	}

	public static String join(String glue, String[] strings) {
		return join(glue, Arrays.asList(strings));
	}

	public static String join(String glue, Iterable<String> strings) {
		if (strings == null) {
			return null;
		}

		StringBuilder stringBuilder = new StringBuilder();
		String verkett = "";
		for (String string : strings) {
			stringBuilder.append(verkett);
			stringBuilder.append(string);
			verkett = glue;
		}
		return stringBuilder.toString();
	}

	private static interface LoggingLevel {
		public void log(Logger log, String message);
	}

	private static enum LoggingLevelEnum implements LoggingLevel {
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

	private static class LoggingOutputStream extends java.io.OutputStream {

		protected static final String LINE_SEPERATOR = System.getProperty("line.separator");

		protected Logger log;
		protected LoggingLevelEnum loggingLevel;

		/**
		 * Used to maintain the contract of {@link #close()}.
		 */
		protected boolean hasBeenClosed = false;

		/**
		 * The internal buffer where data is stored.
		 */
		protected byte[] buffer;

		/**
		 * The number of valid bytes in the buffer. This value is always in the
		 * range <tt>0</tt> through <tt>buf.length</tt>; elements
		 * <tt>buf[0]</tt> through <tt>buf[count-1]</tt> contain valid byte
		 * data.
		 */
		protected int count;

		/**
		 * Remembers the size of the buffer for speed.
		 */
		private int bufferLength;

		/**
		 * The default number of bytes in the buffer. =2048
		 */
		public static final int DEFAULT_BUFFER_LENGTH = 2048;

		/**
		 * Creates the LoggingOutputStream to flush to the given Category.
		 * 
		 * @param log
		 *            the Logger to write to
		 * 
		 * @param isError
		 *            the if true write to error, else info
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
			bufferLength = DEFAULT_BUFFER_LENGTH;
			buffer = new byte[DEFAULT_BUFFER_LENGTH];
			count = 0;
		}

		/**
		 * Closes this output stream and releases any system resources
		 * associated with this stream. The general contract of
		 * <code>close</code> is that it closes the output stream. A closed
		 * stream cannot perform output operations and cannot be reopened.
		 */
		@Override
		public void close() {
			flush();
			hasBeenClosed = true;
		}

		/**
		 * Writes the specified byte to this output stream. The general contract
		 * for <code>write</code> is that one byte is written to the output
		 * stream. The byte to be written is the eight low-order bits of the
		 * argument <code>b</code>. The 24 high-order bits of <code>b</code> are
		 * ignored.
		 * 
		 * @param b
		 *            the <code>byte</code> to write
		 */
		@Override
		public void write(final int b) throws IOException {
			if (hasBeenClosed) {
				throw new IOException("The stream has been closed.");
			}

			// don't log nulls
			if (b == 0) {
				return;
			}

			// would this be writing past the buffer?
			if (count == bufferLength) {
				// grow the buffer
				final int newBufLength = bufferLength + DEFAULT_BUFFER_LENGTH;
				final byte[] newBuf = new byte[newBufLength];

				System.arraycopy(buffer, 0, newBuf, 0, bufferLength);

				buffer = newBuf;
				bufferLength = newBufLength;
			}

			buffer[count] = (byte) b;
			count++;
		}

		/**
		 * Flushes this output stream and forces any buffered output bytes to be
		 * written out. The general contract of <code>flush</code> is that
		 * calling it is an indication that, if any bytes previously written
		 * have been buffered by the implementation of the output stream, such
		 * bytes should immediately be written to their intended destination.
		 */
		@Override
		public void flush() {

			if (count == 0) {
				return;
			}

			// don't print out blank lines; flushing from PrintStream puts out
			// these
			if (count == LINE_SEPERATOR.length()) {
				if (((char) buffer[0]) == LINE_SEPERATOR.charAt(0) && ((count == 1) || // <-
																						// Unix
																						// &
																						// Mac,
																						// ->
																						// Windows
						((count == 2) && ((char) buffer[1]) == LINE_SEPERATOR.charAt(1)))) {
					reset();
					return;
				}
			}

			final byte[] theBytes = new byte[count];

			System.arraycopy(buffer, 0, theBytes, 0, count);

			loggingLevel.log(log, new String(theBytes));

			reset();
		}

		private void reset() {
			// not resetting the buffer -- assuming that if it grew that it
			// will likely grow similarly again
			count = 0;
		}
	}
}
