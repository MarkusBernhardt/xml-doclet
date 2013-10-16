package com.github.markusbernhardt.xmldoclet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.markusbernhardt.xmldoclet.simpledata.Annotation1;
import com.github.markusbernhardt.xmldoclet.simpledata.Annotation2;
import com.github.markusbernhardt.xmldoclet.simpledata.Annotation3;
import com.github.markusbernhardt.xmldoclet.xjc.Annotation;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationElement;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationInstance;
import com.github.markusbernhardt.xmldoclet.xjc.Package;
import com.github.markusbernhardt.xmldoclet.xjc.Root;

/**
 * Unit test group for Annotations
 */
@SuppressWarnings("deprecation")
public class AnnotationTest extends AbstractTestParent {
	/**
	 * Rigourous Parser :-)
	 */
	@Test
	public void testSampledoc() {
		executeJavadoc(null, new String[] { "./src/test/java" }, null, null, new String[] { "com" },
				new String[] { "-dryrun" });
	}

	/**
	 * testing an annotation with nothing defined
	 */
	@Test
	public void testAnnotation1() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Annotation1.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackages().get(0);
		Annotation annotationNode = packageNode.getAnnotations().get(0);

		assertEquals(rootNode.getPackages().size(), 1);
		assertEquals(packageNode.getComment(), null);
		assertEquals(packageNode.getName(), "com.github.markusbernhardt.xmldoclet.simpledata");
		assertEquals(packageNode.getAnnotations().size(), 1);
		assertEquals(packageNode.getEnums().size(), 0);
		assertEquals(packageNode.getInterfaces().size(), 0);
		assertEquals(packageNode.getClasses().size(), 0);

		assertEquals(annotationNode.getComment(), "Annotation1");
		assertEquals(annotationNode.getName(), Annotation1.class.getSimpleName());
		assertEquals(annotationNode.getQualifiedName(), Annotation1.class.getName());
		assertEquals(annotationNode.getScope(), "public");
		assertEquals(annotationNode.getAnnotations().size(), 0);
		assertEquals(annotationNode.getElements().size(), 0);
		assertTrue(annotationNode.isIncluded());
	}

	/**
	 * testing an annotation with an annotation decorating it
	 */
	@Test
	public void testAnnotation2() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Annotation2.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackages().get(0);
		Annotation annotationNode = packageNode.getAnnotations().get(0);
		AnnotationInstance annotationInstance = annotationNode.getAnnotations().get(0);

		assertEquals(rootNode.getPackages().size(), 1);
		assertEquals(packageNode.getComment(), "");
		assertEquals(packageNode.getName(), "com.github.markusbernhardt.xmldoclet.simpledata");
		assertEquals(packageNode.getAnnotations().size(), 1);
		assertEquals(packageNode.getEnums().size(), 0);
		assertEquals(packageNode.getInterfaces().size(), 0);
		assertEquals(packageNode.getClasses().size(), 0);

		assertEquals(annotationNode.getComment(), "Annotation2");
		assertEquals(annotationNode.getName(), Annotation2.class.getSimpleName());
		assertEquals(annotationNode.getQualifiedName(), Annotation2.class.getName());
		assertEquals(annotationNode.getScope(), "public");
		assertEquals(annotationNode.getAnnotations().size(), 1);
		assertEquals(annotationNode.getElements().size(), 0);
		assertTrue(annotationNode.isIncluded());

		// test annotation 'deprecated' on class
		assertEquals(annotationInstance.getQualifiedName(), "java.lang.Deprecated");
		assertEquals(annotationInstance.getName(), "Deprecated");
		assertEquals(annotationInstance.getArguments().size(), 0);
	}

	/**
	 * testing an annotation with one element field
	 */
	@Test
	public void testAnnotation3() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Annotation3.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackages().get(0);
		Annotation annotationNode = packageNode.getAnnotations().get(0);
		AnnotationElement element = annotationNode.getElements().get(0);

		assertEquals(rootNode.getPackages().size(), 1);
		assertEquals(packageNode.getComment(), "");
		assertEquals(packageNode.getName(), "com.github.markusbernhardt.xmldoclet.simpledata");
		assertEquals(packageNode.getAnnotations().size(), 1);
		assertEquals(packageNode.getEnums().size(), 0);
		assertEquals(packageNode.getInterfaces().size(), 0);
		assertEquals(packageNode.getClasses().size(), 0);

		assertEquals(annotationNode.getComment(), "Annotation3");
		assertEquals(annotationNode.getName(), Annotation3.class.getSimpleName());
		assertEquals(annotationNode.getQualifiedName(), Annotation3.class.getName());
		assertEquals(annotationNode.getScope(), "public");
		assertEquals(annotationNode.getAnnotations().size(), 0);
		assertEquals(annotationNode.getElements().size(), 1);
		assertTrue(annotationNode.isIncluded());

		// test annotation element
		assertEquals(element.getName(), "id");
		assertEquals(element.getQualifiedName(), "com.github.markusbernhardt.xmldoclet.simpledata.Annotation3.id");
		assertEquals(element.getType(), "int");
		assertEquals(element.getDefaultValue(), Integer.toString(3));
	}

	/**
	 * testing an annotation with non-public definition
	 */
	@Test
	public void testAnnotation4() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Annotation4.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackages().get(0);
		Annotation annotationNode = packageNode.getAnnotations().get(0);

		assertEquals(rootNode.getPackages().size(), 1);
		assertEquals(packageNode.getComment(), "");
		assertEquals(packageNode.getName(), "com.github.markusbernhardt.xmldoclet.simpledata");
		assertEquals(packageNode.getAnnotations().size(), 1);
		assertEquals(packageNode.getEnums().size(), 0);
		assertEquals(packageNode.getInterfaces().size(), 0);
		assertEquals(packageNode.getClasses().size(), 0);

		assertEquals(annotationNode.getComment(), "Annotation4");
		assertEquals(annotationNode.getName(), "Annotation4");
		assertEquals(annotationNode.getQualifiedName(), "com.github.markusbernhardt.xmldoclet.simpledata.Annotation4");
		assertEquals(annotationNode.getScope(), "");
		assertEquals(annotationNode.getAnnotations().size(), 0);
		assertEquals(annotationNode.getElements().size(), 0);
		assertTrue(annotationNode.isIncluded());
	}
}