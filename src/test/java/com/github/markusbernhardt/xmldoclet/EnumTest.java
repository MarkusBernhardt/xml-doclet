package com.github.markusbernhardt.xmldoclet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.github.markusbernhardt.xmldoclet.simpledata.Annotation12;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationArgument;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationInstance;
import com.github.markusbernhardt.xmldoclet.xjc.Enum;
import com.github.markusbernhardt.xmldoclet.xjc.EnumConstant;
import com.github.markusbernhardt.xmldoclet.xjc.Package;
import com.github.markusbernhardt.xmldoclet.xjc.Root;

/**
 * Unit test group for Enumerations
 */
public class EnumTest extends AbstractTestParent {

	/**
	 * Rigourous Parser :-)
	 */
	@Test
	public void testSampledoc() {
		executeJavadoc(".", new String[] { "./src/test/java" }, null, null, new String[] { "com" },
				new String[] { "-dryrun" });
	}

	/**
	 * testing a simple enum
	 */
	@Test
	public void testEnum1() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Enum1.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Enum enumNode = packageNode.getEnum().get(0);

		assertEquals(rootNode.getPackage().size(), 1);
		assertNull(packageNode.getComment());
		assertEquals(packageNode.getName(), "com.github.markusbernhardt.xmldoclet.simpledata");
		assertEquals(packageNode.getAnnotation().size(), 0);
		assertEquals(packageNode.getEnum().size(), 1);
		assertEquals(packageNode.getInterface().size(), 0);
		assertEquals(packageNode.getClazz().size(), 0);

		assertEquals(enumNode.getName(), "Enum1");
		assertNull(enumNode.getComment());
		assertEquals(enumNode.getQualified(), "com.github.markusbernhardt.xmldoclet.simpledata.Enum1");
		assertEquals(enumNode.getConstant().size(), 3);
		assertEquals(enumNode.getConstant().get(0).getName(), "a");
		assertEquals(enumNode.getConstant().get(1).getName(), "b");
		assertEquals(enumNode.getConstant().get(2).getName(), "c");
	}

	/**
	 * testing an empty enum
	 */
	@Test
	public void testEnum2() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Enum2.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Enum enumNode = packageNode.getEnum().get(0);

		assertEquals(enumNode.getName(), "Enum2");
		assertEquals(enumNode.getComment(), "Enum2");
		assertEquals(enumNode.getQualified(), "com.github.markusbernhardt.xmldoclet.simpledata.Enum2");
		assertEquals(enumNode.getConstant().size(), 0);
	}

	/**
	 * testing enum comment
	 */
	@Test
	public void testEnum3() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Enum3.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Enum enumNode = packageNode.getEnum().get(0);
		assertEquals(enumNode.getComment(), "Enum3");
	}

	/**
	 * testing enum field comment
	 */
	@Test
	public void testEnum4() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Enum4.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Enum enumNode = packageNode.getEnum().get(0);

		EnumConstant enumConstantNode = enumNode.getConstant().get(0);
		assertEquals(enumConstantNode.getComment(), "field1");
	}

	/**
	 * testing single annotation
	 */
	@Test
	public void testEnum5() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Enum5.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Enum enumNode = packageNode.getEnum().get(0);
		assertEquals(enumNode.getAnnotation().size(), 1);
		AnnotationInstance annotationInstanceNode = enumNode.getAnnotation().get(0);
		assertEquals(annotationInstanceNode.getQualified(), "java.lang.Deprecated");
	}

	/**
	 * testing multiple annotation
	 */
	@Test
	public void testEnum6() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Enum6.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Enum enumNode = packageNode.getEnum().get(0);
		assertEquals(enumNode.getAnnotation().size(), 2);

		AnnotationInstance annotationInstanceNode = enumNode.getAnnotation().get(0);
		assertEquals(annotationInstanceNode.getQualified(), "java.lang.Deprecated");
		assertEquals(annotationInstanceNode.getName(), "Deprecated");
		assertEquals(annotationInstanceNode.getArgument().size(), 0);

		annotationInstanceNode = enumNode.getAnnotation().get(1);
		assertEquals(annotationInstanceNode.getQualified(), Annotation12.class.getName());
		assertEquals(annotationInstanceNode.getName(), Annotation12.class.getSimpleName());
		assertEquals(annotationInstanceNode.getArgument().size(), 1);

		AnnotationArgument annotationArgumentNode = annotationInstanceNode.getArgument().get(0);
		assertEquals(annotationArgumentNode.getName(), "value");
		assertEquals(annotationArgumentNode.getValue().get(0), "mister");

	}

}
