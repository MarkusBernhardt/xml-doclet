package com.github.markusbernhardt.xmldoclet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.github.markusbernhardt.xmldoclet.xjc.Class;
import com.github.markusbernhardt.xmldoclet.xjc.Package;
import com.github.markusbernhardt.xmldoclet.xjc.Root;

/**
 * Unit test group for Tags
 */
public class TagTest extends AbstractTestParent {

	/**
	 * testing a simple tags
	 */
	@Test
	public void testTag1() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Tag1.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Class classNode = packageNode.getClazz().get(0);

		assertEquals(rootNode.getPackage().size(), 1);
		assertNull(packageNode.getComment());
		assertEquals(packageNode.getName(), "com.github.markusbernhardt.xmldoclet.simpledata");
		assertEquals(packageNode.getAnnotation().size(), 0);
		assertEquals(packageNode.getEnum().size(), 0);
		assertEquals(packageNode.getInterface().size(), 0);
		assertEquals(packageNode.getClazz().size(), 1);

		assertEquals(classNode.getTag().size(), 7);
		assertEquals(classNode.getMethod().get(0).getTag().size(), 3);
	}

}
