package com.github.markusbernhardt.xmldoclet;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.markusbernhardt.xmldoclet.xjc.Root;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;

public class XmlDoclet {

	private final static Logger log = LoggerFactory.getLogger(Parser.class);

	/**
	 * The parsed object model. Used in unit tests.
	 */
	public static Root root;

	/**
	 * The Options instance to parse command line strings.
	 */
	public final static Options options;

	static {
		options = new Options();

		OptionBuilder.withArgName("directory");
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("Destination directory for output file.\nDefault: .");
		options.addOption(OptionBuilder.create("d"));

		OptionBuilder.withArgName("encoding");
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("Encoding of the output file.\nDefault: UTF8");
		options.addOption(OptionBuilder.create("docencoding"));

		OptionBuilder.withArgName("dryrun");
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArgs(0);
		OptionBuilder.withDescription("Parse javadoc, but don't write output file.\nDefault: false");
		options.addOption(OptionBuilder.create("dryrun"));

		OptionBuilder.withArgName("filename");
		OptionBuilder.isRequired(false);
		OptionBuilder.hasArg();
		OptionBuilder.withDescription("Name of the output file.\nDefault: javadoc.xml");
		options.addOption(OptionBuilder.create("filename"));
	}

	/**
	 * Check for doclet-added options. Returns the number of arguments you must
	 * specify on the command line for the given option. For example, "-d docs"
	 * would return 2.
	 * <P>
	 * This method is required if the doclet contains any options. If this
	 * method is missing, Javadoc will print an invalid flag error for every
	 * option.
	 * 
	 * @see com.sun.javadoc.Doclet#optionLength(String)
	 * 
	 * @param optionName
	 *            The name of the option.
	 * @return number of arguments on the command line for an option including
	 *         the option name itself. Zero return means option not known.
	 *         Negative value means error occurred.
	 */
	public static int optionLength(String optionName) {
		Option option = options.getOption(optionName);
		if (option == null) {
			return 0;
		}
		return option.getArgs() + 1;
	}

	/**
	 * Check that options have the correct arguments.
	 * <P>
	 * This method is not required, but is recommended, as every option will be
	 * considered valid if this method is not present. It will default
	 * gracefully (to true) if absent.
	 * <P>
	 * Printing option related error messages (using the provided
	 * DocErrorReporter) is the responsibility of this method.
	 * 
	 * @see com.sun.javadoc.Doclet#validOptions(String[][], DocErrorReporter)
	 * 
	 * @param options
	 *            The two dimensional array of options.
	 * @param reporter
	 *            The error reporter.
	 * 
	 * @return <code>true</code> if the options are valid.
	 */
	public static boolean validOptions(String optionsArrayArray[][], DocErrorReporter reporter) {
		return null != parseCommandLine(optionsArrayArray);
	}

	/**
	 * Processes the JavaDoc documentation.
	 * <p>
	 * This method is required for all doclets.
	 * 
	 * @see com.sun.javadoc.Doclet#start(RootDoc)
	 * 
	 * @param root
	 *            The root of the documentation tree.
	 * 
	 * @return <code>true</code> if processing was successful.
	 */
	public static boolean start(RootDoc rootDoc) {
		CommandLine commandLine = parseCommandLine(rootDoc.options());
		Parser parser = new Parser();
		root = parser.parseRootDoc(rootDoc);
		save(commandLine, root);
		return true;
	}

	/**
	 * Save XML object model to a file via JAXB.
	 * 
	 * @param commandLine
	 * @param root
	 */
	public static void save(CommandLine commandLine, Root root) {
		if (commandLine.hasOption("dryrun")) {
			return;
		}

		FileOutputStream fileOutputStream = null;
		BufferedOutputStream bufferedOutputStream = null;
		try {
			JAXBContext contextObj = JAXBContext.newInstance(Root.class);

			Marshaller marshaller = contextObj.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			if (commandLine.hasOption("docencoding")) {
				marshaller.setProperty(Marshaller.JAXB_ENCODING, commandLine.getOptionValue("docencoding"));
			}

			String filename = "javadoc.xml";
			if (commandLine.hasOption("filename")) {
				filename = commandLine.getOptionValue("filename");
			}
			if (commandLine.hasOption("d")) {
				filename = commandLine.getOptionValue("d") + File.separator + filename;
			}

			fileOutputStream = new FileOutputStream(filename);
			bufferedOutputStream = new BufferedOutputStream(fileOutputStream, 1024 * 1024);

			marshaller.marshal(root, bufferedOutputStream);
			bufferedOutputStream.flush();
			fileOutputStream.flush();

		} catch (JAXBException e) {
			log.error(e.getMessage(), e);
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (bufferedOutputStream != null) {
					bufferedOutputStream.close();
				}
				if (fileOutputStream != null) {
					fileOutputStream.close();
				}
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
	}

	/**
	 * Return the version of the Java Programming Language supported by this
	 * doclet.
	 * <p>
	 * This method is required by any doclet supporting a language version newer
	 * than 1.1.
	 * <p>
	 * This Doclet supports Java 5.
	 * 
	 * @see com.sun.javadoc.Doclet#languageVersion()
	 * 
	 * @return {@value LanguageVersion#JAVA_1_5}
	 */
	public static LanguageVersion languageVersion() {
		return LanguageVersion.JAVA_1_5;
	}

	/**
	 * Parse the given options.
	 * 
	 * @param optionsArrayArray
	 * @return
	 * @throws ParseException
	 */
	public static CommandLine parseCommandLine(String[][] optionsArrayArray) {
		try {
			List<String> argumentList = new ArrayList<String>();
			for (String[] optionsArray : optionsArrayArray) {
				argumentList.addAll(Arrays.asList(optionsArray));
			}

			CommandLineParser commandLineParser = new BasicParser() {
				@Override
				protected void processOption(final String arg, @SuppressWarnings("rawtypes") final ListIterator iter)
						throws ParseException {
					boolean hasOption = getOptions().hasOption(arg);
					if (hasOption) {
						super.processOption(arg, iter);
					}
				}
			};
			CommandLine commandLine = commandLineParser.parse(options, argumentList.toArray(new String[] {}));
			return commandLine;
		} catch (ParseException e) {
			LoggingOutputStream loggingOutputStream = new LoggingOutputStream(log, LoggingLevelEnum.INFO);
			PrintWriter printWriter = new PrintWriter(loggingOutputStream);

			HelpFormatter helpFormatter = new HelpFormatter();
			helpFormatter.printHelp(printWriter, 74, "javadoc -doclet " + XmlDoclet.class.getName() + " [options]",
					null, options, 1, 3, null, false);
			return null;
		}
	}
}
