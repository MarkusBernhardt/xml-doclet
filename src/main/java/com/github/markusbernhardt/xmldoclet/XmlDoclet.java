package com.github.markusbernhardt.xmldoclet;

import com.github.markusbernhardt.xmldoclet.xjc.Root;
import com.lexicalscope.jewel.cli.Cli;
import com.lexicalscope.jewel.cli.CliFactory;
import com.sun.javadoc.DocErrorReporter;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;

public class XmlDoclet {

	/**
	 * The Cli instance to parse command line strings.
	 */
	private final static Cli<XmlDocletCli> xmlDocletCli = CliFactory.createCli(XmlDocletCli.class);

	/**
	 * Returns the number of arguments required for the given option.
	 * 
	 * @see com.sun.javadoc.Doclet#optionLength(String)
	 * 
	 * @param option
	 *            The name of the option.
	 * 
	 * @return The number of arguments for that option.
	 */
	public static int optionLength(String option) {
		xmlDocletCli.parseArguments(option);

		// TODO
		return 0;
	}

	/**
	 * Check that options have the correct arguments.
	 * 
	 * <p>
	 * This method is not required, but is recommended, as every option will be
	 * considered valid if this method is not present. It will default
	 * gracefully (to true) if absent.
	 * 
	 * <p>
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
	public static boolean validOptions(String o[][], DocErrorReporter reporter) {
		// options = Options.toOptions(o, reporter);

		// TODO
		return true;
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
		Parser parser = new Parser();
		Root root = parser.parseRootDoc(rootDoc);

		// TODO
		// Save the output XML
		// save(nodes);

		return true;
	}

	/**
	 * Returns the version of the Java Programming Language supported by this
	 * Doclet.
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
}
