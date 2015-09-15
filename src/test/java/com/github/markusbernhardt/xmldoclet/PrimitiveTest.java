package com.github.markusbernhardt.xmldoclet;

import org.junit.Test;

/**
 * Unit test group for Primitives
 */
public class PrimitiveTest extends AbstractTestParent {

	/**
	 * Rigourous Parser :-)
	 */
	@Test
	public void test() {
		executeJavadoc(null, new String[] { "./src/test/java" }, null, null, new String[] { "com" },
				new String[] { "-dryrun" });
	}
}
