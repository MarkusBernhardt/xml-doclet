package com.github.markusbernhardt.xmldoclet;

import com.github.markusbernhardt.xmldoclet.xjc.Root;
import com.sun.javadoc.LanguageVersion;
import com.sun.javadoc.RootDoc;

public class XmlDoclet {
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
