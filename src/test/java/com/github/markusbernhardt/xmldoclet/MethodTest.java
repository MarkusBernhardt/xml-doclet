package com.github.markusbernhardt.xmldoclet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Test;

import com.github.markusbernhardt.xmldoclet.xjc.AnnotationArgument;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationInstance;
import com.github.markusbernhardt.xmldoclet.xjc.Class;
import com.github.markusbernhardt.xmldoclet.xjc.Method;
import com.github.markusbernhardt.xmldoclet.xjc.MethodParameter;
import com.github.markusbernhardt.xmldoclet.xjc.Package;
import com.github.markusbernhardt.xmldoclet.xjc.Root;
import com.github.markusbernhardt.xmldoclet.xjc.TypeInfo;
import com.github.markusbernhardt.xmldoclet.xjc.Wildcard;

public class MethodTest extends AbstractTestParent {

	/**
	 * Rigourous Parser :-)
	 */
	@Test
	public void testSampledoc() {
		executeJavadoc(".", new String[] { "./src/test/java" }, null, null, new String[] { "com" },
				new String[] { "-dryrun" });
	}

	/**
	 * testing a returns of methodNodes
	 */
	@Test
	public void testMethod1() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Method1.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackages().get(0);
		Class classNode = packageNode.getClasses().get(0);
		List<Method> testMethods = classNode.getMethods();

		// with methodNode1 we are checking that a simple methodNode can exist
		// with no arguments and no return
		Method methodNode = findByMethodName("method1", testMethods);
		assertEquals(methodNode.getReturn().getQualifiedName(), "void");
		assertEquals(methodNode.getReturn().getGenerics().size(), 0);
		assertNull(methodNode.getReturn().getWildcard());
		assertNull(methodNode.getReturn().getDimension());

		// methodNode2 - checking Object based returns
		methodNode = findByMethodName("method2", testMethods);
		assertEquals(methodNode.getReturn().getQualifiedName(), "java.lang.Integer");
		assertEquals(methodNode.getReturn().getGenerics().size(), 0);
		assertNull(methodNode.getReturn().getWildcard());
		assertNull(methodNode.getReturn().getDimension());

		// methodNode 3 - checking primitive based returns
		methodNode = findByMethodName("method3", testMethods);
		assertEquals(methodNode.getReturn().getQualifiedName(), "int");
		assertEquals(methodNode.getReturn().getGenerics().size(), 0);
		assertNull(methodNode.getReturn().getWildcard());
		assertNull(methodNode.getReturn().getDimension());
	}

	/**
	 * testing arguments of methodNodes
	 */
	@Test
	public void testMethod2() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Method2.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackages().get(0);
		Class classNode = packageNode.getClasses().get(0);
		List<Method> testMethods = classNode.getMethods();

		// methodNode - methodNode with no arguments
		Method methodNode = findByMethodName("method1", testMethods);
		assertEquals(methodNode.getParameters().size(), 0);
		assertEquals(methodNode.getSignature(), "()");

		// methodNode2 - methodNode with one Object-derived argument
		methodNode = findByMethodName("method2", testMethods);
		assertEquals(methodNode.getParameters().size(), 1);
		assertEquals(methodNode.getSignature(), "(" + Integer.class.getName() + ")");

		// one should be able to reliably access getParameters() in this fashion
		// since XML order is important, and order of getParameters() to
		// methodNodes is
		// likewise important. ORDER MATTERS AND SHOULD BE TRUSTY!
		MethodParameter methodParameterNode = methodNode.getParameters().get(0);
		assertEquals(methodParameterNode.getType().getQualifiedName(), "java.lang.Integer");

		// methodNode3 - check primitive argument
		methodNode = findByMethodName("method3", testMethods);
		assertEquals(methodNode.getParameters().size(), 1);
		assertEquals(methodNode.getSignature(), "(int)");

		methodParameterNode = methodNode.getParameters().get(0);
		assertEquals(methodParameterNode.getType().getQualifiedName(), "int");
		assertNull(methodParameterNode.getType().getDimension());
		assertEquals(methodParameterNode.getType().getGenerics().size(), 0);
		assertNull(methodParameterNode.getType().getWildcard());

		// methodNode4 - check that two args are OK
		methodNode = findByMethodName("method4", testMethods);
		assertEquals(methodNode.getParameters().size(), 2);
		assertEquals(methodNode.getSignature(), "(" + Integer.class.getName() + ", " + Integer.class.getName() + ")");

		methodParameterNode = methodNode.getParameters().get(0);
		assertEquals(methodParameterNode.getType().getQualifiedName(), "java.lang.Integer");

		methodParameterNode = methodNode.getParameters().get(1);
		assertEquals(methodParameterNode.getType().getQualifiedName(), "java.lang.Integer");

		// methodNode5 - check that a generic argument is valid
		methodNode = findByMethodName("method5", testMethods);
		assertEquals(methodNode.getParameters().size(), 1);
		assertEquals(methodNode.getSignature(), "(java.util.ArrayList<java.lang.String>)");

		methodParameterNode = methodNode.getParameters().get(0);
		assertEquals(methodParameterNode.getName(), "arg1");
		assertEquals(methodParameterNode.getType().getQualifiedName(), "java.util.ArrayList");
		assertNull(methodParameterNode.getType().getDimension());
		assertNull(methodParameterNode.getType().getWildcard());
		assertEquals(methodParameterNode.getType().getGenerics().size(), 1);

		TypeInfo type = methodParameterNode.getType().getGenerics().get(0);
		assertEquals(type.getQualifiedName(), "java.lang.String");
		assertNull(type.getDimension());
		assertNull(type.getWildcard());
		assertEquals(type.getGenerics().size(), 0);

		// methodNode6 - check that a wildcard argument is valid
		methodNode = findByMethodName("method6", testMethods);
		assertEquals(methodNode.getParameters().size(), 1);
		assertEquals(methodNode.getSignature(), "(java.util.ArrayList<?>)");

		methodParameterNode = methodNode.getParameters().get(0);
		assertEquals(methodParameterNode.getName(), "arg1");
		assertEquals(methodParameterNode.getType().getQualifiedName(), "java.util.ArrayList");
		assertNull(methodParameterNode.getType().getDimension());
		assertNull(methodParameterNode.getType().getWildcard());
		assertEquals(methodParameterNode.getType().getGenerics().size(), 1);

		type = methodParameterNode.getType().getGenerics().get(0);
		assertEquals(type.getQualifiedName(), "?");
		assertNull(type.getDimension());
		assertNotNull(type.getWildcard());
		assertEquals(type.getGenerics().size(), 0);

		Wildcard wildcard = type.getWildcard();
		assertEquals(wildcard.getExtendsBounds().size(), 0);
		assertEquals(wildcard.getSuperBounds().size(), 0);

		// methodNode7 - check that a wildcard argument is valid with extends
		// clause
		methodNode = findByMethodName("method7", testMethods);
		assertEquals(methodNode.getParameters().size(), 1);
		assertEquals(methodNode.getSignature(), "(java.util.ArrayList<? extends java.lang.String>)");

		methodParameterNode = methodNode.getParameters().get(0);
		assertEquals(methodParameterNode.getName(), "arg1");
		assertEquals(methodParameterNode.getType().getQualifiedName(), "java.util.ArrayList");
		assertEquals(methodParameterNode.getType().getDimension(), null);
		assertEquals(methodParameterNode.getType().getGenerics().size(), 1);
		assertNull(methodParameterNode.getType().getWildcard());

		type = methodParameterNode.getType().getGenerics().get(0);
		assertEquals(type.getQualifiedName(), "?");
		assertEquals(type.getDimension(), null);
		assertEquals(type.getGenerics().size(), 0);
		assertNotNull(type.getWildcard());

		wildcard = type.getWildcard();
		assertEquals(wildcard.getExtendsBounds().size(), 1);
		assertEquals(wildcard.getSuperBounds().size(), 0);

		TypeInfo extendsBound = wildcard.getExtendsBounds().get(0);
		assertEquals(extendsBound.getQualifiedName(), "java.lang.String");
		assertEquals(extendsBound.getDimension(), null);
		assertEquals(extendsBound.getGenerics().size(), 0);
		assertNull(extendsBound.getWildcard());

		// methodNode8 - check that a wildcard argument is valid with super
		// clause
		methodNode = findByMethodName("method8", testMethods);
		assertEquals(methodNode.getParameters().size(), 1);
		assertEquals(methodNode.getSignature(), "(java.util.ArrayList<? super java.lang.String>)");

		methodParameterNode = methodNode.getParameters().get(0);
		assertEquals(methodParameterNode.getName(), "arg1");
		assertEquals(methodParameterNode.getType().getQualifiedName(), "java.util.ArrayList");
		assertEquals(methodParameterNode.getType().getDimension(), null);
		assertEquals(methodParameterNode.getType().getGenerics().size(), 1);
		assertNull(methodParameterNode.getType().getWildcard());

		type = methodParameterNode.getType().getGenerics().get(0);
		assertEquals(type.getQualifiedName(), "?");
		assertEquals(type.getDimension(), null);
		assertEquals(type.getGenerics().size(), 0);
		assertNotNull(type.getWildcard());

		wildcard = type.getWildcard();
		assertEquals(wildcard.getSuperBounds().size(), 1);
		assertEquals(wildcard.getExtendsBounds().size(), 0);

		TypeInfo superBounds = wildcard.getSuperBounds().get(0);
		assertEquals(superBounds.getQualifiedName(), "java.lang.String");
		assertEquals(superBounds.getDimension(), null);
		assertEquals(superBounds.getGenerics().size(), 0);
		assertNull(superBounds.getWildcard());

		// methodNode9 - check that a two-level deep nested generic
		methodNode = findByMethodName("method9", testMethods);
		assertEquals(methodNode.getParameters().size(), 1);
		assertEquals(methodNode.getSignature(), "(java.util.ArrayList<java.util.ArrayList<java.lang.String>>)");

		methodParameterNode = methodNode.getParameters().get(0);
		assertEquals(methodParameterNode.getName(), "arg1");
		assertEquals(methodParameterNode.getType().getQualifiedName(), "java.util.ArrayList");
		assertEquals(methodParameterNode.getType().getDimension(), null);
		assertEquals(methodParameterNode.getType().getGenerics().size(), 1);
		assertNull(methodParameterNode.getType().getWildcard());

		type = methodParameterNode.getType().getGenerics().get(0);
		assertEquals(type.getQualifiedName(), "java.util.ArrayList");
		assertEquals(type.getDimension(), null);
		assertEquals(type.getGenerics().size(), 1);
		assertNull(type.getWildcard());

		type = type.getGenerics().get(0);
		assertEquals(type.getQualifiedName(), "java.lang.String");
		assertEquals(type.getDimension(), null);
		assertEquals(type.getGenerics().size(), 0);
		assertNull(type.getWildcard());

		// methodNode10 - check var args
		methodNode = findByMethodName("method10", testMethods);
		assertEquals(methodNode.getParameters().size(), 1);
		assertEquals(methodNode.getSignature(), "(java.lang.Object...)");
		assertTrue(methodNode.isVarArgs());

		methodParameterNode = methodNode.getParameters().get(0);
		assertEquals(methodParameterNode.getName(), "object");
		assertEquals(methodParameterNode.getType().getQualifiedName(), "java.lang.Object");
		assertEquals(methodParameterNode.getType().getDimension(), "[]"); 
		
		// methodNode9--check var args negative test
		assertFalse(findByMethodName("method9", testMethods).isVarArgs());
	}

	/**
	 * testing methodNode properties
	 */
	@Test
	public void testMethod3() {
		String[] sourceFiles = new String[] { "./src/test/java/com/github/markusbernhardt/xmldoclet/simpledata/Method3.java" };
		Root rootNode = executeJavadoc(null, null, null, sourceFiles, null, new String[] { "-dryrun" });

		Package packageNode = rootNode.getPackages().get(0);
		Class classNode = packageNode.getClasses().get(0);
		List<Method> testMethods = classNode.getMethods();

		// methodNode1 -- we check public scope
		Method methodNode = findByMethodName("method1", testMethods);
		assertEquals(methodNode.getScope(), "public");

		// methodNode2 -- we check package scope
		methodNode = findByMethodName("method2", testMethods);
		assertEquals(methodNode.getScope(), "");

		// methodNode3 -- we check private scope
		methodNode = findByMethodName("method3", testMethods);
		assertEquals(methodNode.getScope(), "private");

		// methodNode4 -- we check private scope
		methodNode = findByMethodName("method4", testMethods);
		assertEquals(methodNode.getScope(), "protected");

		// methodNode5 -- we check native
		methodNode = findByMethodName("method5", testMethods);
		assertTrue(methodNode.isNative());
		// and negative
		assertFalse(findByMethodName("method4", testMethods).isNative());

		// methodNode6 -- we check static
		methodNode = findByMethodName("method6", testMethods);
		assertTrue(methodNode.isStatic());
		// and negative
		assertFalse(findByMethodName("method4", testMethods).isStatic());

		// methodNode7 -- we check final
		methodNode = findByMethodName("method7", testMethods);
		assertTrue(methodNode.isFinal());
		// and negative
		assertFalse(findByMethodName("method4", testMethods).isFinal());

		// methodNode8 -- we check synchronized
		methodNode = findByMethodName("method8", testMethods);
		assertTrue(methodNode.isSynchronized());
		// and negative
		assertFalse(findByMethodName("method4", testMethods).isSynchronized());

		// methodNode9 -- we check one thrown exception
		methodNode = findByMethodName("method9", testMethods);
		assertEquals(methodNode.getThrows().size(), 1);

		TypeInfo exception = methodNode.getThrows().get(0);
		assertEquals(exception.getQualifiedName(), "java.lang.Exception");
		assertEquals(exception.getDimension(), null);
		assertEquals(exception.getGenerics().size(), 0);
		assertNull(exception.getWildcard());

		// methodNode10 -- we check two thrown exceptions
		methodNode = findByMethodName("method10", testMethods);
		assertEquals(methodNode.getThrows().size(), 2);

		exception = methodNode.getThrows().get(0);
		assertEquals(exception.getQualifiedName(), "java.lang.OutOfMemoryError");
		assertEquals(exception.getDimension(), null);
		assertEquals(exception.getGenerics().size(), 0);

		exception = methodNode.getThrows().get(1);
		assertEquals(exception.getQualifiedName(), "java.lang.IllegalArgumentException");
		assertEquals(exception.getDimension(), null);
		assertEquals(exception.getGenerics().size(), 0);

		// negative--no exceptions
		assertEquals(findByMethodName("method4", testMethods).getThrows().size(), 0);

		// methodNode11 -- 1 annotation instance

		methodNode = findByMethodName("method11", testMethods);
		assertEquals(methodNode.getAnnotations().size(), 1);

		AnnotationInstance annotation = methodNode.getAnnotations().get(0);
		assertEquals(annotation.getQualifiedName(), "java.lang.Deprecated");
		assertEquals(annotation.getArguments().size(), 0);

		// methodNode12 -- 2 annotation instances
		methodNode = findByMethodName("method12", testMethods);
		assertEquals(methodNode.getAnnotations().size(), 2);

		annotation = methodNode.getAnnotations().get(0);
		assertEquals(annotation.getQualifiedName(), "java.lang.Deprecated");

		annotation = methodNode.getAnnotations().get(1);
		assertEquals(annotation.getQualifiedName(), "java.lang.SuppressWarnings");
		assertEquals(annotation.getArguments().size(), 1);
		AnnotationArgument annotArgument = annotation.getArguments().get(0);
		assertEquals(annotArgument.getName(), "value");
		assertEquals(annotArgument.getValue().get(0), "java.lang.Warning");

		// negative -- no annotations
		assertEquals(findByMethodName("method4", testMethods).getAnnotations().size(), 0);

	}

	/**
	 * Short way of finding methodNodes. It's meant to only be used for
	 * methodNodes that do not share the same name in the same class. In fact,
	 * this class will junit assert that there is only 1 methodNode matching
	 * this name in the supplied <code>list</code> methodParameterNodeeter.
	 * 
	 * @param methodNodeName
	 *            the shortname of the methodNode
	 * @param methodNodes
	 *            the list of methodNodes to look through.
	 * @return The matching methodNode
	 */
	private Method findByMethodName(String methodNodeName, List<Method> methodNodes) {
		for (Method methodNode : methodNodes) {
			if (methodNode.getName().equals(methodNodeName)) {
				return methodNode;
			}
		}

		fail();
		return null;
	}
}
