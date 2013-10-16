package com.github.markusbernhardt.xmldoclet;

import org.junit.Test;

public class PrimitiveTest extends AbstractTestParent {

	@Test
	public void test() {
		executeJavadoc(null, new String[] { "./src/test/java" }, null, null, new String[] { "com" }, new String[] {
				"-dryrun" });
	}
}
