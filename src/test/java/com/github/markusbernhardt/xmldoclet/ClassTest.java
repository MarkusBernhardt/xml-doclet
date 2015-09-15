package com.github.markusbernhardt.xmldoclet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.github.markusbernhardt.xmldoclet.simpledata.Class1;
import com.github.markusbernhardt.xmldoclet.simpledata.Class2;
import com.github.markusbernhardt.xmldoclet.simpledata.Class3;
import com.github.markusbernhardt.xmldoclet.simpledata.Class4;
import com.github.markusbernhardt.xmldoclet.simpledata.Class5;
import com.github.markusbernhardt.xmldoclet.simpledata.Class6;
import com.github.markusbernhardt.xmldoclet.simpledata.Class7;
import com.github.markusbernhardt.xmldoclet.simpledata.Class8;
import com.github.markusbernhardt.xmldoclet.simpledata.Class9;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationArgument;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationInstance;
import com.github.markusbernhardt.xmldoclet.xjc.Class;
import com.github.markusbernhardt.xmldoclet.xjc.Constructor;
import com.github.markusbernhardt.xmldoclet.xjc.Field;
import com.github.markusbernhardt.xmldoclet.xjc.Method;
import com.github.markusbernhardt.xmldoclet.xjc.Package;
import com.github.markusbernhardt.xmldoclet.xjc.Root;
import com.github.markusbernhardt.xmldoclet.xjc.TypeInfo;
import com.github.markusbernhardt.xmldoclet.xjc.TypeParameter;

/**
 * Unit test group for Classes
 */
@SuppressWarnings("deprecation")
public class ClassTest extends AbstractTestParent {

	/**
	 * Rigourous Parser :-)
	 */
	@Test
	public void testSampledoc() {
		executeJavadoc(".", new String[] { "./src/test/java" }, null, null, new String[] { "com" },
				new String[] { "-dryrun" });
	}

	/**
	 * testing a class with nothing defined EMPIRICAL OBSERVATION: The default
	 * constructor created by the java compiler is not marked synthetic. um
	 * what?
	 */
	@Test
	public void testClass1() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Class1.java" };
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

		assertEquals(classNode.getComment(), "Class1");
		assertEquals(classNode.getName(), Class1.class.getSimpleName());
		assertEquals(classNode.getQualified(), Class1.class.getName());
		assertEquals(classNode.getScope(), "public");
		assertEquals(classNode.getConstructor().size(), 1);
		assertEquals(classNode.getField().size(), 0);
		assertEquals(classNode.getMethod().size(), 0);
		assertEquals(classNode.getAnnotation().size(), 0);
		assertEquals(classNode.getInterface().size(), 0);
		assertEquals(classNode.getClazz().getQualified(), Object.class.getName());
		assertFalse(classNode.isAbstract());
		assertFalse(classNode.isExternalizable());
		assertTrue(classNode.isIncluded());
		assertFalse(classNode.isSerializable());
		assertFalse(classNode.isException());
		assertFalse(classNode.isError());
		assertEquals(classNode.getGeneric().size(), 0);
	}

	/**
	 * testing a class with 1 constructor
	 */
	@Test
	public void testClass2() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Class2.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Class classNode = packageNode.getClazz().get(0);
		Constructor constructor = classNode.getConstructor().get(0);

		assertEquals(rootNode.getPackage().size(), 1);
		assertNull(packageNode.getComment());
		assertEquals(packageNode.getName(), "com.github.markusbernhardt.xmldoclet.simpledata");
		assertEquals(packageNode.getAnnotation().size(), 0);
		assertEquals(packageNode.getEnum().size(), 0);
		assertEquals(packageNode.getInterface().size(), 0);
		assertEquals(packageNode.getClazz().size(), 1);

		assertEquals(classNode.getComment(), "Class2");
		assertEquals(classNode.getConstructor().size(), 1);
		assertEquals(classNode.getName(), Class2.class.getSimpleName());
		assertEquals(classNode.getQualified(), Class2.class.getName());
		assertEquals(classNode.getScope(), "public");
		assertEquals(classNode.getField().size(), 0);
		assertEquals(classNode.getMethod().size(), 0);
		assertEquals(classNode.getAnnotation().size(), 0);
		assertEquals(classNode.getInterface().size(), 0);
		assertEquals(classNode.getClazz().getQualified(), Object.class.getName());
		assertFalse(classNode.isAbstract());
		assertFalse(classNode.isExternalizable());
		assertTrue(classNode.isIncluded());
		assertFalse(classNode.isSerializable());
		assertFalse(classNode.isException());
		assertFalse(classNode.isError());
		assertEquals(classNode.getGeneric().size(), 0);

		assertEquals(constructor.getComment(), "Constructor1");
		assertEquals(constructor.getName(), "Class2");
		assertEquals(constructor.getParameter().size(), 0);
		assertEquals(constructor.getAnnotation().size(), 0);
	}

	/**
	 * testing a class with 1 method
	 */
	@Test
	public void testClass3() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Class3.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Class classNode = packageNode.getClazz().get(0);
		Method method = classNode.getMethod().get(0);

		assertEquals(rootNode.getPackage().size(), 1);
		assertNull(packageNode.getComment());
		assertEquals(packageNode.getName(), "com.github.markusbernhardt.xmldoclet.simpledata");
		assertEquals(packageNode.getAnnotation().size(), 0);
		assertEquals(packageNode.getEnum().size(), 0);
		assertEquals(packageNode.getInterface().size(), 0);
		assertEquals(packageNode.getClazz().size(), 1);

		assertEquals(classNode.getComment(), "Class3");
		assertEquals(classNode.getConstructor().size(), 1);
		assertEquals(classNode.getName(), Class3.class.getSimpleName());
		assertEquals(classNode.getQualified(), Class3.class.getName());
		assertEquals(classNode.getScope(), "public");
		assertEquals(classNode.getMethod().size(), 1);
		assertEquals(classNode.getField().size(), 0);
		assertEquals(classNode.getAnnotation().size(), 0);
		assertEquals(classNode.getInterface().size(), 0);
		assertEquals(classNode.getClazz().getQualified(), Object.class.getName());
		assertFalse(classNode.isAbstract());
		assertFalse(classNode.isExternalizable());
		assertTrue(classNode.isIncluded());
		assertFalse(classNode.isSerializable());
		assertFalse(classNode.isException());
		assertFalse(classNode.isError());
		assertEquals(classNode.getGeneric().size(), 0);

		assertEquals(method.getComment(), "method1");
		assertEquals(method.getName(), "method1");
		assertEquals(method.getSignature(), "()");
		assertFalse(method.isFinal());
		assertFalse(method.isNative());
		assertFalse(method.isStatic());
		assertFalse(method.isSynchronized());
		assertFalse(method.isVarArgs());
		assertEquals(method.getQualified(), "com.github.markusbernhardt.xmldoclet.simpledata.Class3.method1");
		assertEquals(method.getScope(), "public");
		assertEquals(method.getAnnotation().size(), 0);
		assertEquals(method.getParameter().size(), 0);
		assertEquals(method.getException().size(), 0);

		TypeInfo returnNode = method.getReturn();
		assertEquals(returnNode.getQualified(), "int");
		assertNull(returnNode.getDimension());
		assertEquals(returnNode.getGeneric().size(), 0);
		assertNull(returnNode.getWildcard());
	}

	/**
	 * testing a class with 1 field
	 */
	@Test
	public void testClass4() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Class4.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Class classNode = packageNode.getClazz().get(0);
		Field field = classNode.getField().get(0);

		assertEquals(rootNode.getPackage().size(), 1);
		assertNull(packageNode.getComment());
		assertEquals(packageNode.getName(), "com.github.markusbernhardt.xmldoclet.simpledata");
		assertEquals(packageNode.getAnnotation().size(), 0);
		assertEquals(packageNode.getEnum().size(), 0);
		assertEquals(packageNode.getInterface().size(), 0);
		assertEquals(packageNode.getClazz().size(), 1);

		assertEquals(classNode.getComment(), "Class4");
		assertEquals(classNode.getConstructor().size(), 1);
		assertEquals(classNode.getName(), Class4.class.getSimpleName());
		assertEquals(classNode.getQualified(), Class4.class.getName());
		assertEquals(classNode.getScope(), "public");
		assertEquals(classNode.getField().size(), 1);
		assertEquals(classNode.getMethod().size(), 0);
		assertEquals(classNode.getAnnotation().size(), 0);
		assertEquals(classNode.getInterface().size(), 0);
		assertEquals(classNode.getClazz().getQualified(), Object.class.getName());
		assertFalse(classNode.isAbstract());
		assertFalse(classNode.isExternalizable());
		assertTrue(classNode.isIncluded());
		assertFalse(classNode.isSerializable());
		assertFalse(classNode.isException());
		assertFalse(classNode.isError());
		assertEquals(classNode.getGeneric().size(), 0);

		// test field
		assertEquals(field.getComment(), "field1");
		assertEquals(field.getName(), "field1");
		assertEquals(field.getScope(), "public");
		assertEquals(field.getType().getQualified(), "int");
		assertNull(field.getType().getDimension());
		assertEquals(field.getType().getGeneric().size(), 0);
		assertNull(field.getType().getWildcard());
		assertFalse(field.isStatic());
		assertFalse(field.isTransient());
		assertFalse(field.isVolatile());
		assertFalse(field.isFinal());
		assertNull(field.getConstant());
		assertEquals(field.getAnnotation().size(), 0);
	}

	/**
	 * testing a class that extends another class with 1 method
	 */
	@Test
	public void testClass5() {
		String[] sourceFiles = new String[] {
				"./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Class5.java",
				"./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Class3.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Class classNode = packageNode.getClazz().get(0);

		assertEquals(rootNode.getPackage().size(), 1);
		assertNull(packageNode.getComment());
		assertEquals(packageNode.getName(), "com.github.markusbernhardt.xmldoclet.simpledata");
		assertEquals(packageNode.getAnnotation().size(), 0);
		assertEquals(packageNode.getEnum().size(), 0);
		assertEquals(packageNode.getInterface().size(), 0);
		assertEquals(packageNode.getClazz().size(), 2);

		assertEquals(classNode.getComment(), "Class5");
		assertEquals(classNode.getConstructor().size(), 1);
		assertEquals(classNode.getName(), Class5.class.getSimpleName());
		assertEquals(classNode.getQualified(), Class5.class.getName());
		assertEquals(classNode.getScope(), "public");
		assertEquals(classNode.getMethod().size(), 0);
		assertEquals(classNode.getField().size(), 0);
		assertEquals(classNode.getAnnotation().size(), 0);
		assertEquals(classNode.getInterface().size(), 0);
		assertEquals(classNode.getClazz().getQualified(), "com.github.markusbernhardt.xmldoclet.simpledata.Class3");
		assertFalse(classNode.isAbstract());
		assertFalse(classNode.isExternalizable());
		assertTrue(classNode.isIncluded());
		assertFalse(classNode.isSerializable());
		assertFalse(classNode.isException());
		assertFalse(classNode.isError());
		assertEquals(classNode.getGeneric().size(), 0);
	}

	/**
	 * testing a class that implements one interface
	 */
	@Test
	public void testClass6() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Class6.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Class classNode = packageNode.getClazz().get(0);
		TypeInfo interfaceNode = classNode.getInterface().get(0);

		assertEquals(rootNode.getPackage().size(), 1);
		assertNull(packageNode.getComment());
		assertEquals(packageNode.getName(), "com.github.markusbernhardt.xmldoclet.simpledata");
		assertEquals(packageNode.getAnnotation().size(), 0);
		assertEquals(packageNode.getEnum().size(), 0);
		assertEquals(packageNode.getInterface().size(), 0);
		assertEquals(packageNode.getClazz().size(), 1);

		assertEquals(classNode.getComment(), "Class6");
		assertEquals(classNode.getConstructor().size(), 1);
		assertEquals(classNode.getName(), Class6.class.getSimpleName());
		assertEquals(classNode.getQualified(), Class6.class.getName());
		assertEquals(classNode.getScope(), "public");
		assertEquals(classNode.getMethod().size(), 0);
		assertEquals(classNode.getField().size(), 0);
		assertEquals(classNode.getAnnotation().size(), 1);
		assertEquals(classNode.getInterface().size(), 1);
		assertEquals(classNode.getClazz().getQualified(), Object.class.getName());
		assertFalse(classNode.isAbstract());
		assertFalse(classNode.isExternalizable());
		assertTrue(classNode.isIncluded());
		assertFalse(classNode.isException());
		assertFalse(classNode.isError());
		assertEquals(classNode.getGeneric().size(), 0);

		// the particular interface chosen for this test also will change this
		// flag to true!
		assertTrue(classNode.isSerializable());

		// verify interface
		assertEquals(interfaceNode.getQualified(), java.io.Serializable.class.getName());
	}

	/**
	 * testing one annotation instance on the class
	 */
	@Test
	public void testClass7() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Class7.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Class classNode = packageNode.getClazz().get(0);
		AnnotationInstance annotationNode = classNode.getAnnotation().get(0);

		assertEquals(rootNode.getPackage().size(), 1);
		assertNull(packageNode.getComment());
		assertEquals(packageNode.getName(), "com.github.markusbernhardt.xmldoclet.simpledata");
		assertEquals(packageNode.getAnnotation().size(), 0);
		assertEquals(packageNode.getEnum().size(), 0);
		assertEquals(packageNode.getInterface().size(), 0);
		assertEquals(packageNode.getClazz().size(), 1);

		assertEquals(classNode.getComment(), "Class7");
		assertEquals(classNode.getConstructor().size(), 1);
		assertEquals(classNode.getName(), Class7.class.getSimpleName());
		assertEquals(classNode.getQualified(), Class7.class.getName());
		assertEquals(classNode.getScope(), "public");
		assertEquals(classNode.getMethod().size(), 0);
		assertEquals(classNode.getField().size(), 0);
		assertEquals(classNode.getAnnotation().size(), 1);
		assertEquals(classNode.getInterface().size(), 0);
		assertEquals(classNode.getClazz().getQualified(), Object.class.getName());
		assertFalse(classNode.isAbstract());
		assertFalse(classNode.isExternalizable());
		assertTrue(classNode.isIncluded());
		assertFalse(classNode.isSerializable());
		assertFalse(classNode.isException());
		assertFalse(classNode.isError());
		assertEquals(classNode.getGeneric().size(), 0);

		// test annotation 'deprecated' on class
		assertEquals(annotationNode.getQualified(), "java.lang.Deprecated");
		assertEquals(annotationNode.getName(), "Deprecated");
		assertEquals(annotationNode.getArgument().size(), 0);
	}

	/**
	 * testing abstract keyword on class
	 */
	@Test
	public void testClass8() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Class8.java" };
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

		assertEquals(classNode.getComment(), "Class8");
		assertEquals(classNode.getConstructor().size(), 1);
		assertEquals(classNode.getName(), Class8.class.getSimpleName());
		assertEquals(classNode.getQualified(), Class8.class.getName());
		assertEquals(classNode.getScope(), "public");
		assertEquals(classNode.getMethod().size(), 0);
		assertEquals(classNode.getField().size(), 0);
		assertEquals(classNode.getAnnotation().size(), 0);
		assertEquals(classNode.getInterface().size(), 0);
		assertEquals(classNode.getClazz().getQualified(), Object.class.getName());
		assertTrue(classNode.isAbstract());
		assertFalse(classNode.isExternalizable());
		assertTrue(classNode.isIncluded());
		assertFalse(classNode.isSerializable());
		assertFalse(classNode.isException());
		assertFalse(classNode.isError());
		assertEquals(classNode.getGeneric().size(), 0);
	}

	/**
	 * testing java.io.Externalizable interface on class
	 */
	@Test
	public void testClass9() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Class9.java" };
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

		assertEquals(classNode.getComment(), "Class9");
		assertEquals(classNode.getConstructor().size(), 1);
		assertEquals(classNode.getName(), Class9.class.getSimpleName());
		assertEquals(classNode.getQualified(), Class9.class.getName());
		assertEquals(classNode.getScope(), "public");
		assertEquals(classNode.getMethod().size(), 2);
		assertEquals(classNode.getField().size(), 0);
		assertEquals(classNode.getAnnotation().size(), 0);
		assertEquals(classNode.getInterface().size(), 1);
		assertEquals(classNode.getClazz().getQualified(), Object.class.getName());
		assertFalse(classNode.isAbstract());
		assertTrue(classNode.isExternalizable());
		assertTrue(classNode.isIncluded());
		assertTrue(classNode.isSerializable());
		assertFalse(classNode.isException());
		assertFalse(classNode.isError());
		assertEquals(classNode.getGeneric().size(), 0);
	}

	/**
	 * testing difference of scope modifier on class
	 */
	@Test
	public void testClass10() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Class10.java" };
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

		assertEquals(classNode.getComment(), "Class10");
		assertEquals(classNode.getConstructor().size(), 1);
		assertEquals(classNode.getName(), "Class10");
		assertEquals(classNode.getQualified(), "com.github.markusbernhardt.xmldoclet.simpledata.Class10");
		assertEquals(classNode.getScope(), "");
		assertEquals(classNode.getMethod().size(), 0);
		assertEquals(classNode.getField().size(), 0);
		assertEquals(classNode.getAnnotation().size(), 0);
		assertEquals(classNode.getInterface().size(), 0);
		assertEquals(classNode.getClazz().getQualified(), Object.class.getName());
		assertFalse(classNode.isAbstract());
		assertFalse(classNode.isExternalizable());
		assertTrue(classNode.isIncluded());
		assertFalse(classNode.isSerializable());
		assertFalse(classNode.isException());
		assertFalse(classNode.isError());
		assertEquals(classNode.getGeneric().size(), 0);
	}

	/**
	 * testing if isException is populated correctly
	 */
	@Test
	public void testClass11() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Class11.java" };
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

		assertEquals(classNode.getComment(), "Class11");
		assertEquals(classNode.getConstructor().size(), 1);
		assertEquals(classNode.getName(), "Class11");
		assertEquals(classNode.getQualified(), "com.github.markusbernhardt.xmldoclet.simpledata.Class11");
		assertEquals(classNode.getScope(), "public");
		assertEquals(classNode.getMethod().size(), 0);
		assertEquals(classNode.getField().size(), 0);
		assertEquals(classNode.getAnnotation().size(), 1);
		assertEquals(classNode.getInterface().size(), 0);
		assertEquals(classNode.getClazz().getQualified(), java.lang.Exception.class.getName());
		assertFalse(classNode.isAbstract());
		assertFalse(classNode.isExternalizable());
		assertTrue(classNode.isIncluded());
		assertTrue(classNode.isSerializable());
		assertTrue(classNode.isException());
		assertFalse(classNode.isError());
		assertEquals(classNode.getGeneric().size(), 0);
	}

	/**
	 * testing if isError is populated correctly
	 */
	@Test
	public void testClass12() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Class12.java" };
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

		assertEquals(classNode.getComment(), "Class12");
		assertEquals(classNode.getConstructor().size(), 1);
		assertEquals(classNode.getName(), "Class12");
		assertEquals(classNode.getQualified(), "com.github.markusbernhardt.xmldoclet.simpledata.Class12");
		assertEquals(classNode.getScope(), "public");
		assertEquals(classNode.getMethod().size(), 0);
		assertEquals(classNode.getField().size(), 0);
		assertEquals(classNode.getAnnotation().size(), 1);
		assertEquals(classNode.getInterface().size(), 0);
		assertEquals(classNode.getClazz().getQualified(), java.lang.Error.class.getName());
		assertFalse(classNode.isAbstract());
		assertFalse(classNode.isExternalizable());
		assertTrue(classNode.isIncluded());
		assertTrue(classNode.isSerializable());
		assertFalse(classNode.isException());
		assertTrue(classNode.isError());
		assertEquals(classNode.getGeneric().size(), 0);
	}

	/**
	 * testing if type variables can be determined
	 */
	@Test
	public void testClass13() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Class13.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Class classNode = packageNode.getClazz().get(0);
		TypeParameter typeParameter = classNode.getGeneric().get(0);

		assertEquals(rootNode.getPackage().size(), 1);
		assertNull(packageNode.getComment());
		assertEquals(packageNode.getName(), "com.github.markusbernhardt.xmldoclet.simpledata");
		assertEquals(packageNode.getAnnotation().size(), 0);
		assertEquals(packageNode.getEnum().size(), 0);
		assertEquals(packageNode.getInterface().size(), 0);
		assertEquals(packageNode.getClazz().size(), 1);

		assertEquals(classNode.getComment(), "Class13");
		assertEquals(classNode.getConstructor().size(), 1);
		assertEquals(classNode.getName(), "Class13");
		assertEquals(classNode.getQualified(), "com.github.markusbernhardt.xmldoclet.simpledata.Class13");
		assertEquals(classNode.getScope(), "public");
		assertEquals(classNode.getGeneric().size(), 1);
		assertEquals(classNode.getMethod().size(), 0);
		assertEquals(classNode.getField().size(), 0);
		assertEquals(classNode.getAnnotation().size(), 0);
		assertEquals(classNode.getInterface().size(), 0);
		assertEquals(classNode.getClazz().getQualified(), Object.class.getName());
		assertFalse(classNode.isAbstract());
		assertFalse(classNode.isExternalizable());
		assertTrue(classNode.isIncluded());
		assertFalse(classNode.isSerializable());
		assertFalse(classNode.isException());
		assertFalse(classNode.isError());

		// check the 'fun' type var
		assertEquals(typeParameter.getName(), "Fun");
		assertEquals(typeParameter.getBound().size(), 0);
	}

	/**
	 * testing if a single bounds can be determined
	 */
	@Test
	public void testClass14() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Class14.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Class classNode = packageNode.getClazz().get(0);
		TypeParameter typeParameter = classNode.getGeneric().get(0);

		assertEquals(rootNode.getPackage().size(), 1);
		assertNull(packageNode.getComment());
		assertEquals(packageNode.getName(), "com.github.markusbernhardt.xmldoclet.simpledata");
		assertEquals(packageNode.getAnnotation().size(), 0);
		assertEquals(packageNode.getEnum().size(), 0);
		assertEquals(packageNode.getInterface().size(), 0);
		assertEquals(packageNode.getClazz().size(), 1);

		assertEquals(classNode.getComment(), "Class14");
		assertEquals(classNode.getConstructor().size(), 1);
		assertEquals(classNode.getName(), "Class14");
		assertEquals(classNode.getQualified(), "com.github.markusbernhardt.xmldoclet.simpledata.Class14");
		assertEquals(classNode.getScope(), "public");
		assertEquals(classNode.getGeneric().size(), 1);
		assertEquals(classNode.getMethod().size(), 0);
		assertEquals(classNode.getField().size(), 0);
		assertEquals(classNode.getAnnotation().size(), 0);
		assertEquals(classNode.getInterface().size(), 0);
		assertEquals(classNode.getClazz().getQualified(), Object.class.getName());
		assertFalse(classNode.isAbstract());
		assertFalse(classNode.isExternalizable());
		assertTrue(classNode.isIncluded());
		assertFalse(classNode.isSerializable());
		assertFalse(classNode.isException());
		assertFalse(classNode.isError());

		// check the 'fun' type var

		assertEquals(typeParameter.getName(), "Fun");
		assertEquals(typeParameter.getBound().size(), 1);
		assertEquals(typeParameter.getBound().get(0), Number.class.getName());
	}

	/**
	 * testing if a multiple bounds can be determined
	 */
	@Test
	public void testClass15() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Class15.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Class classNode = packageNode.getClazz().get(0);
		TypeParameter typeParameter = classNode.getGeneric().get(0);

		assertEquals(rootNode.getPackage().size(), 1);
		assertNull(packageNode.getComment());
		assertEquals(packageNode.getName(), "com.github.markusbernhardt.xmldoclet.simpledata");
		assertEquals(packageNode.getAnnotation().size(), 0);
		assertEquals(packageNode.getEnum().size(), 0);
		assertEquals(packageNode.getInterface().size(), 0);
		assertEquals(packageNode.getClazz().size(), 1);

		assertEquals(classNode.getComment(), "Class15");
		assertEquals(classNode.getConstructor().size(), 1);
		assertEquals(classNode.getName(), "Class15");
		assertEquals(classNode.getQualified(), "com.github.markusbernhardt.xmldoclet.simpledata.Class15");
		assertEquals(classNode.getScope(), "public");
		assertEquals(classNode.getGeneric().size(), 1);
		assertEquals(classNode.getMethod().size(), 0);
		assertEquals(classNode.getField().size(), 0);
		assertEquals(classNode.getAnnotation().size(), 0);
		assertEquals(classNode.getInterface().size(), 0);
		assertEquals(classNode.getClazz().getQualified(), Object.class.getName());
		assertFalse(classNode.isAbstract());
		assertFalse(classNode.isExternalizable());
		assertTrue(classNode.isIncluded());
		assertFalse(classNode.isSerializable());
		assertFalse(classNode.isException());
		assertFalse(classNode.isError());

		// check the 'fun' type var
		assertEquals(typeParameter.getName(), "Fun");
		assertEquals(typeParameter.getBound().size(), 2);
		assertEquals(typeParameter.getBound().get(0), Number.class.getName());
		assertEquals(typeParameter.getBound().get(1), Runnable.class.getName());
	}

	/**
	 * testing integer annotation argument
	 */
	@Test
	public void testClass16() {
		String[] sourceFiles = new String[] {
				"./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Class16.java",
				"./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Annotation3.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Class classNode = packageNode.getClazz().get(0);
		AnnotationInstance instance = classNode.getAnnotation().get(0);
		AnnotationArgument argument = instance.getArgument().get(0);
		assertEquals(argument.getName(), "id");
		assertEquals(argument.getType().getQualified(), "int");
		assertEquals(argument.getValue().size(), 1);
		assertEquals(argument.getValue().get(0), "3");
		assertTrue(argument.isPrimitive());
		assertFalse(argument.isArray());
	}

	/**
	 * testing integer array annotation argument
	 */
	@Test
	public void testClass17() {
		String[] sourceFiles = new String[] {
				"./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Class17.java",
				"./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Annotation5.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Class classNode = packageNode.getClazz().get(0);
		AnnotationInstance instance = classNode.getAnnotation().get(0);
		AnnotationArgument argument = instance.getArgument().get(0);
		assertEquals(argument.getType().getQualified(), "int");
		assertEquals(argument.getValue().size(), 2);
		assertEquals(argument.getValue().get(0), "1");
		assertEquals(argument.getValue().get(1), "2");
		assertTrue(argument.isPrimitive());
		assertTrue(argument.isArray());
	}

	/**
	 * testing integer array annotation argument
	 */
	@Test
	public void testClass18() {
		String[] sourceFiles = new String[] {
				"./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Class18.java",
				"./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Annotation6.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Class classNode = packageNode.getClazz().get(0);
		AnnotationInstance instance = classNode.getAnnotation().get(0);
		AnnotationArgument argument = instance.getArgument().get(0);
		assertEquals(argument.getType().getQualified(), "java.lang.String");
		assertEquals(argument.getValue().size(), 1);
		assertEquals(argument.getValue().get(0), "hey");
		assertFalse(argument.isPrimitive());
		assertFalse(argument.isArray());
	}

	/**
	 * testing enum annotation argument
	 */
	@Test
	public void testClass19() {
		String[] sourceFiles = new String[] {
				"./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Class19.java",
				"./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Annotation7.java",
				"./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Enum1.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Class classNode = packageNode.getClazz().get(0);
		AnnotationInstance instance = classNode.getAnnotation().get(0);
		AnnotationArgument argument = instance.getArgument().get(0);
		assertEquals(argument.getType().getQualified(), "com.github.markusbernhardt.xmldoclet.simpledata.Enum1");
		assertEquals(argument.getValue().size(), 1);
		assertEquals(argument.getValue().get(0), "a");
		assertFalse(argument.isPrimitive());
		assertFalse(argument.isArray());
	}

	/**
	 * testing class annotation argument
	 */
	@Test
	public void testClass20() {
		String[] sourceFiles = new String[] {
				"./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Class20.java",
				"./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Annotation8.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Class classNode = packageNode.getClazz().get(0);
		AnnotationInstance instance = classNode.getAnnotation().get(0);
		AnnotationArgument argument = instance.getArgument().get(0);
		assertEquals(argument.getType().getQualified(), "java.lang.Class");
		assertEquals(argument.getValue().size(), 1);
		assertEquals(argument.getValue().get(0), "java.lang.String");
		assertFalse(argument.isPrimitive());
		assertFalse(argument.isArray());
	}

	/**
	 * testing character annotation argument
	 */
	@Test
	public void testClass21() {
		String[] sourceFiles = new String[] {
				"./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Class21.java",
				"./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Annotation10.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Class classNode = packageNode.getClazz().get(0);
		AnnotationInstance instance = classNode.getAnnotation().get(0);
		AnnotationArgument argument = instance.getArgument().get(0);
		assertEquals(argument.getType().getQualified(), "char");
		assertEquals(argument.getValue().size(), 1);
		assertEquals(argument.getValue().get(0), Integer.toString((int) 'a'));
		assertTrue(argument.isPrimitive());
		assertFalse(argument.isArray());
	}

	/**
	 * testing 0 character annotation argument
	 */
	@Test
	public void testClass22() {
		String[] sourceFiles = new String[] {
				"./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Class22.java",
				"./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Annotation10.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Class classNode = packageNode.getClazz().get(0);
		AnnotationInstance instance = classNode.getAnnotation().get(0);
		AnnotationArgument argument = instance.getArgument().get(0);
		assertEquals(argument.getType().getQualified(), "char");
		assertEquals(argument.getValue().size(), 1);
		assertEquals(argument.getValue().get(0), Integer.toString(0));
		assertTrue(argument.isPrimitive());
		assertFalse(argument.isArray());
	}

	/**
	 * testing boolean annotation argument
	 */
	@Test
	public void testClass23() {
		String[] sourceFiles = new String[] {
				"./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Class23.java",
				"./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Annotation11.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Class classNode = packageNode.getClazz().get(0);
		AnnotationInstance instance = classNode.getAnnotation().get(0);
		AnnotationArgument argument = instance.getArgument().get(0);
		assertEquals(argument.getType().getQualified(), "boolean");
		assertEquals(argument.getValue().size(), 1);
		assertEquals(argument.getValue().get(0), Boolean.TRUE.toString());
		assertTrue(argument.isPrimitive());
		assertFalse(argument.isArray());
	}

	/**
	 * testing empty int array annotation argument
	 */
	@Test
	public void testClass24() {
		String[] sourceFiles = new String[] {
				"./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Class24.java",
				"./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Annotation5.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackage().get(0);
		Class classNode = packageNode.getClazz().get(0);
		AnnotationInstance instance = classNode.getAnnotation().get(0);
		AnnotationArgument argument = instance.getArgument().get(0);
		assertEquals(argument.getType().getQualified(), "int");
		assertEquals(argument.getValue().size(), 0);
		assertTrue(argument.isPrimitive());
		assertTrue(argument.isArray());
	}
}
