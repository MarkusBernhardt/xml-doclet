package com.github.markusbernhardt.xmldoclet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.github.markusbernhardt.xmldoclet.xjc.Class;
import com.github.markusbernhardt.xmldoclet.xjc.Package;
import com.github.markusbernhardt.xmldoclet.xjc.Root;

public class TagTest extends AbstractTestParent {

	/**
	 * testing a simple tags
	 */
	@Test
	public void testTag1() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Tag1.java"
				,"./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Class1.java"
		};
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { });//"-dryrun" });
/*
		Package packageNode = rootNode.getPackages().get(0);
		Class classNode = packageNode.getClasses().get(0);

		assertEquals(rootNode.getPackages().size(), 1);
		assertNull(packageNode.getComment());
		assertEquals(packageNode.getName(), "com.github.markusbernhardt.xmldoclet.simpledata");
		assertEquals(packageNode.getAnnotations().size(), 0);
		assertEquals(packageNode.getEnums().size(), 0);
		assertEquals(packageNode.getInterfaces().size(), 0);
		assertEquals(packageNode.getClasses().size(), 1);
*/
	}

}
