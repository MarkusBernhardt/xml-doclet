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

		Package packageNode = rootNode.getPackages().get(0);
		Enum enumNode = packageNode.getEnums().get(0);

		assertEquals(rootNode.getPackages().size(), 1);
		assertNull(packageNode.getComment());
		assertEquals(packageNode.getName(), "com.github.markusbernhardt.xmldoclet.simpledata");
		assertEquals(packageNode.getAnnotations().size(), 0);
		assertEquals(packageNode.getEnums().size(), 1);
		assertEquals(packageNode.getInterfaces().size(), 0);
		assertEquals(packageNode.getClasses().size(), 0);

		assertEquals(enumNode.getName(), "Enum1");
		assertNull(enumNode.getComment());
		assertEquals(enumNode.getQualifiedName(), "com.github.markusbernhardt.xmldoclet.simpledata.Enum1");
		assertEquals(enumNode.getConstants().size(), 3);
		assertEquals(enumNode.getConstants().get(0).getName(), "a");
		assertEquals(enumNode.getConstants().get(1).getName(), "b");
		assertEquals(enumNode.getConstants().get(2).getName(), "c");
	}

	/**
	 * testing an empty enum
	 */
	@Test
	public void testEnum2() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Enum2.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackages().get(0);
		Enum enumNode = packageNode.getEnums().get(0);

		assertEquals(enumNode.getName(), "Enum2");
		assertEquals(enumNode.getComment(), "Enum2");
		assertEquals(enumNode.getQualifiedName(), "com.github.markusbernhardt.xmldoclet.simpledata.Enum2");
		assertEquals(enumNode.getConstants().size(), 0);
	}

	/**
	 * testing enum comment
	 */
	@Test
	public void testEnum3() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Enum3.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackages().get(0);
		Enum enumNode = packageNode.getEnums().get(0);
		assertEquals(enumNode.getComment(), "Enum3");
	}

	/**
	 * testing enum field comment
	 */
	@Test
	public void testEnum4() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Enum4.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackages().get(0);
		Enum enumNode = packageNode.getEnums().get(0);

		EnumConstant enumConstantNode = enumNode.getConstants().get(0);
		assertEquals(enumConstantNode.getComment(), "field1");
	}

	/**
	 * testing single annotation
	 */
	@Test
	public void testEnum5() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Enum5.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackages().get(0);
		Enum enumNode = packageNode.getEnums().get(0);
		assertEquals(enumNode.getAnnotations().size(), 1);
		AnnotationInstance annotationInstanceNode = enumNode.getAnnotations().get(0);
		assertEquals(annotationInstanceNode.getQualifiedName(), "java.lang.Deprecated");
	}

	/**
	 * testing multiple annotation
	 */
	@Test
	public void testEnum6() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Enum6.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackages().get(0);
		Enum enumNode = packageNode.getEnums().get(0);
		assertEquals(enumNode.getAnnotations().size(), 2);

		AnnotationInstance annotationInstanceNode = enumNode.getAnnotations().get(0);
		assertEquals(annotationInstanceNode.getQualifiedName(), "java.lang.Deprecated");
		assertEquals(annotationInstanceNode.getName(), "Deprecated");
		assertEquals(annotationInstanceNode.getArguments().size(), 0);

		annotationInstanceNode = enumNode.getAnnotations().get(1);
		assertEquals(annotationInstanceNode.getQualifiedName(), Annotation12.class.getName());
		assertEquals(annotationInstanceNode.getName(), Annotation12.class.getSimpleName());
		assertEquals(annotationInstanceNode.getArguments().size(), 1);

		AnnotationArgument annotationArgumentNode = annotationInstanceNode.getArguments().get(0);
		assertEquals(annotationArgumentNode.getName(), "value");
		assertEquals(annotationArgumentNode.getValue().get(0), "mister");

	}

}
