package com.github.markusbernhardt.xmldoclet;

import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.markusbernhardt.xmldoclet.xjc.Annotation;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationArgument;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationElement;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationInstance;
import com.github.markusbernhardt.xmldoclet.xjc.Class;
import com.github.markusbernhardt.xmldoclet.xjc.Constructor;
import com.github.markusbernhardt.xmldoclet.xjc.Enum;
import com.github.markusbernhardt.xmldoclet.xjc.EnumConstant;
import com.github.markusbernhardt.xmldoclet.xjc.Field;
import com.github.markusbernhardt.xmldoclet.xjc.Interface;
import com.github.markusbernhardt.xmldoclet.xjc.Method;
import com.github.markusbernhardt.xmldoclet.xjc.MethodParameter;
import com.github.markusbernhardt.xmldoclet.xjc.Package;
import com.github.markusbernhardt.xmldoclet.xjc.Root;
import com.github.markusbernhardt.xmldoclet.xjc.TypeInfo;
import com.github.markusbernhardt.xmldoclet.xjc.TypeParameter;
import com.github.markusbernhardt.xmldoclet.xjc.Wildcard;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.AnnotationTypeElementDoc;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.ConstructorDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ParameterizedType;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;
import com.sun.javadoc.WildcardType;

public class Parser {

	private final static Logger log = LoggerFactory.getLogger(Parser.class);

	protected Map<PackageDoc, Package> packages = new TreeMap<PackageDoc, Package>();

	/**
	 * The entry point into parsing the javadoc.
	 * 
	 * @param rootDoc
	 *            The RootDoc intstance obtained via the doclet API
	 * @return The root node, containing everything parsed from javadoc doclet
	 */
	public Root parseRootDoc(RootDoc rootDoc) {
		Root rootNode = new Root();

		for (ClassDoc classDoc : rootDoc.classes()) {
			PackageDoc packageDoc = classDoc.containingPackage();

			Package packageNode = packages.get(packageDoc);
			if (packageNode == null) {
				packageNode = new Package();
				packages.put(packageDoc, packageNode);
			}

			if (classDoc instanceof AnnotationTypeDoc) {
				packageNode.getAnnotations().add(parseAnnotationTypeDoc((AnnotationTypeDoc) classDoc));
			} else if (classDoc.isEnum()) {
				packageNode.getEnums().add(parseEnum(classDoc));
			} else if (classDoc.isInterface()) {
				packageNode.getInterfaces().add(parseInterface(classDoc));
			} else {
				packageNode.getClasses().add(parseClass(classDoc));
			}
		}

		return rootNode;
	}

	/**
	 * Parse an annotation.
	 * 
	 * @param annotationTypeDoc
	 *            A AnnotationTypeDoc instance
	 * @return the annotation node
	 */
	protected Annotation parseAnnotationTypeDoc(AnnotationTypeDoc annotationTypeDoc) {
		log.debug("Parsing annotation " + annotationTypeDoc.qualifiedName());

		Annotation annotationNode = new Annotation();
		annotationNode.setName(annotationTypeDoc.name());
		annotationNode.setQualifiedName(annotationTypeDoc.qualifiedName());
		annotationNode.setComment(annotationTypeDoc.commentText());
		annotationNode.setIsIncluded(annotationTypeDoc.isIncluded());
		annotationNode.setScope(parseScope(annotationTypeDoc));

		for (AnnotationTypeElementDoc annotationTypeElementDoc : annotationTypeDoc.elements()) {
			annotationNode.getElements().add(parseAnnotationTypeElementDoc(annotationTypeElementDoc));
		}

		for (AnnotationDesc annotationDesc : annotationTypeDoc.annotations()) {
			annotationNode.getAnnotations().add(parseAnnotationDesc(annotationDesc, annotationTypeDoc.qualifiedName()));
		}

		return annotationNode;
	}

	/**
	 * Parse the elements of an annotation
	 * 
	 * @param element
	 *            A AnnotationTypeElementDoc instance
	 * @return the annotation element node
	 */
	protected AnnotationElement parseAnnotationTypeElementDoc(AnnotationTypeElementDoc annotationTypeElementDoc) {
		AnnotationElement annotationElementNode = new AnnotationElement();
		annotationElementNode.setName(annotationTypeElementDoc.name());
		annotationElementNode.setQualifiedName(annotationTypeElementDoc.qualifiedName());
		annotationElementNode.setType(annotationTypeElementDoc.returnType().qualifiedTypeName());

		AnnotationValue value = annotationTypeElementDoc.defaultValue();
		if (value != null) {
			annotationElementNode.setDefaultValue(value.toString());
		}
		return annotationElementNode;
	}

	/**
	 * Parses annotation instances of an annotable program element
	 * 
	 * @param annotationDocs
	 *            Annotations decorated on some program element
	 * @return representation of annotations
	 */
	protected AnnotationInstance parseAnnotationDesc(AnnotationDesc annotationDesc, String programElement) {
		AnnotationInstance annotationInstanceNode = new AnnotationInstance();

		try {
			AnnotationTypeDoc annotTypeInfo = annotationDesc.annotationType();
			annotationInstanceNode.setName(annotTypeInfo.name());
			annotationInstanceNode.setQualifiedName(annotTypeInfo.qualifiedTypeName());
		} catch (ClassCastException castException) {
			log.error("Unable to obtain type data about an annotation found on: " + programElement);
			log.error("Add to the classpath the class/jar that defines this annotation.");
		}

		for (AnnotationDesc.ElementValuePair elementValuesPair : annotationDesc.elementValues()) {
			AnnotationArgument annotationArgumentNode = new AnnotationArgument();
			annotationArgumentNode.setName(elementValuesPair.element().name());

			Type annotationArgumentType = elementValuesPair.element().returnType();
			annotationArgumentNode.setType(annotationArgumentType.qualifiedTypeName());
			annotationArgumentNode.setIsPrimitive(annotationArgumentType.isPrimitive());
			annotationArgumentNode.setIsArray(annotationArgumentType.dimension().length() > 0);

			Object objValue = elementValuesPair.value().value();
			if (objValue instanceof AnnotationValue[]) {
				for (AnnotationValue annotationValue : (AnnotationValue[]) objValue) {
					annotationArgumentNode.getValue().add(annotationValue.value().toString());
				}
			} else if (objValue instanceof FieldDoc) {
				annotationArgumentNode.getValue().add(((FieldDoc) objValue).name());
			} else if (objValue instanceof ClassDoc) {
				annotationArgumentNode.getValue().add(((ClassDoc) objValue).qualifiedTypeName());
			} else {
				annotationArgumentNode.getValue().add(objValue.toString());
			}
			annotationInstanceNode.getArguments().add(annotationArgumentNode);
		}

		return annotationInstanceNode;
	}

	protected Enum parseEnum(ClassDoc classDoc) {
		Enum enumNode = new Enum();
		enumNode.setName(classDoc.name());
		enumNode.setQualifiedName(classDoc.qualifiedName());
		enumNode.setComment(classDoc.commentText());
		enumNode.setIsIncluded(classDoc.isIncluded());
		enumNode.setScope(parseScope(classDoc));

		Type superClassType = classDoc.superclassType();
		if (superClassType != null) {
			enumNode.setExtends(parseTypeInfo(superClassType));
		}

		for (Type interfaceType : classDoc.interfaceTypes()) {
			enumNode.getImplements().add(parseTypeInfo(interfaceType));
		}

		for (FieldDoc field : classDoc.enumConstants()) {
			enumNode.getConstants().add(parseEnumConstant(field));
		}

		for (AnnotationDesc annotationDesc : classDoc.annotations()) {
			enumNode.getAnnotations().add(parseAnnotationDesc(annotationDesc, classDoc.qualifiedName()));
		}

		return enumNode;
	}

	/**
	 * Parses an enum type definition
	 * 
	 * @param fieldDoc
	 * @return
	 */
	protected EnumConstant parseEnumConstant(FieldDoc fieldDoc) {
		EnumConstant enumConstant = new EnumConstant();
		enumConstant.setName(fieldDoc.name());
		enumConstant.setComment(fieldDoc.commentText());

		for (AnnotationDesc annotationDesc : fieldDoc.annotations()) {
			enumConstant.getAnnotations().add(parseAnnotationDesc(annotationDesc, fieldDoc.qualifiedName()));
		}

		return enumConstant;
	}

	protected Interface parseInterface(ClassDoc classDoc) {

		Interface interfaceNode = new Interface();
		interfaceNode.setName(classDoc.name());
		interfaceNode.setQualifiedName(classDoc.qualifiedName());
		interfaceNode.setComment(classDoc.commentText());
		interfaceNode.setIsIncluded(classDoc.isIncluded());
		interfaceNode.setScope(parseScope(classDoc));

		for (TypeVariable typeVariable : classDoc.typeParameters()) {
			interfaceNode.getGenerics().add(parseTypeParameter(typeVariable));
		}

		for (Type interfaceType : classDoc.interfaceTypes()) {
			interfaceNode.getExtends().add(parseTypeInfo(interfaceType));
		}

		for (MethodDoc method : classDoc.methods()) {
			interfaceNode.getMethods().add(parseMethod(method));
		}

		for (AnnotationDesc annotationDesc : classDoc.annotations()) {
			interfaceNode.getAnnotations().add(parseAnnotationDesc(annotationDesc, classDoc.qualifiedName()));
		}

		return interfaceNode;
	}

	protected Class parseClass(ClassDoc classDoc) {

		Class classNode = new Class();
		classNode.setName(classDoc.name());
		classNode.setQualifiedName(classDoc.qualifiedName());
		classNode.setComment(classDoc.commentText());
		classNode.setIsAbstract(classDoc.isAbstract());
		classNode.setIsError(classDoc.isError());
		classNode.setIsException(classDoc.isException());
		classNode.setIsExternalizable(classDoc.isExternalizable());
		classNode.setIsIncluded(classDoc.isIncluded());
		classNode.setIsSerializable(classDoc.isSerializable());
		classNode.setScope(parseScope(classDoc));

		for (TypeVariable typeVariable : classDoc.typeParameters()) {
			classNode.getGenerics().add(parseTypeParameter(typeVariable));
		}

		Type superClassType = classDoc.superclassType();
		if (superClassType != null) {
			classNode.setExtends(parseTypeInfo(superClassType));
		}

		for (Type interfaceType : classDoc.interfaceTypes()) {
			classNode.getImplements().add(parseTypeInfo(interfaceType));
		}

		for (MethodDoc method : classDoc.methods()) {
			classNode.getMethods().add(parseMethod(method));
		}

		for (AnnotationDesc annotationDesc : classDoc.annotations()) {
			classNode.getAnnotations().add(parseAnnotationDesc(annotationDesc, classDoc.qualifiedName()));
		}

		for (ConstructorDoc constructor : classDoc.constructors()) {
			classNode.getConstructors().add(parseConstructor(constructor));
		}

		for (FieldDoc field : classDoc.fields()) {
			classNode.getFields().add(parseField(field));
		}

		return classNode;
	}

	protected Constructor parseConstructor(ConstructorDoc constructorDoc) {
		Constructor constructorNode = new Constructor();

		constructorNode.setName(constructorDoc.name());
		constructorNode.setQualifiedName(constructorDoc.qualifiedName());
		constructorNode.setComment(constructorDoc.commentText());
		constructorNode.setScope(parseScope(constructorDoc));
		constructorNode.setIsIncluded(constructorDoc.isIncluded());
		constructorNode.setIsFinal(constructorDoc.isFinal());
		constructorNode.setIsNative(constructorDoc.isNative());
		constructorNode.setIsStatic(constructorDoc.isStatic());
		constructorNode.setIsSynchronized(constructorDoc.isSynchronized());
		constructorNode.setIsVarArgs(constructorDoc.isVarArgs());
		constructorNode.setSignature(constructorDoc.signature());

		for (Parameter parameter : constructorDoc.parameters()) {
			constructorNode.getParameters().add(parseMethodParameter(parameter));
		}

		for (Type exceptionType : constructorDoc.thrownExceptionTypes()) {
			constructorNode.getThrows().add(parseTypeInfo(exceptionType));
		}

		for (AnnotationDesc annotationDesc : constructorDoc.annotations()) {
			constructorNode.getAnnotations().add(parseAnnotationDesc(annotationDesc, constructorDoc.qualifiedName()));
		}

		return constructorNode;
	}

	protected Method parseMethod(MethodDoc methodDoc) {
		Method methodNode = new Method();

		methodNode.setName(methodDoc.name());
		methodNode.setQualifiedName(methodDoc.qualifiedName());
		methodNode.setComment(methodDoc.commentText());
		methodNode.setScope(parseScope(methodDoc));
		methodNode.setIsAbstract(methodDoc.isAbstract());
		methodNode.setIsIncluded(methodDoc.isIncluded());
		methodNode.setIsFinal(methodDoc.isFinal());
		methodNode.setIsNative(methodDoc.isNative());
		methodNode.setIsStatic(methodDoc.isStatic());
		methodNode.setIsSynchronized(methodDoc.isSynchronized());
		methodNode.setIsVarArgs(methodDoc.isVarArgs());
		methodNode.setSignature(methodDoc.signature());
		methodNode.setReturn(parseTypeInfo(methodDoc.returnType()));

		for (Parameter parameter : methodDoc.parameters()) {
			methodNode.getParameters().add(parseMethodParameter(parameter));
		}

		for (Type exceptionType : methodDoc.thrownExceptionTypes()) {
			methodNode.getThrows().add(parseTypeInfo(exceptionType));
		}

		for (AnnotationDesc annotationDesc : methodDoc.annotations()) {
			methodNode.getAnnotations().add(parseAnnotationDesc(annotationDesc, methodDoc.qualifiedName()));
		}

		return methodNode;
	}

	protected MethodParameter parseMethodParameter(Parameter parameter) {
		MethodParameter parameterMethodNode = new MethodParameter();
		parameterMethodNode.setName(parameter.name());
		parameterMethodNode.setType(parseTypeInfo(parameter.type()));

		for (AnnotationDesc annotationDesc : parameter.annotations()) {
			parameterMethodNode.getAnnotations().add(parseAnnotationDesc(annotationDesc, parameter.typeName()));
		}

		return parameterMethodNode;
	}

	protected Field parseField(FieldDoc fieldDoc) {
		Field fieldNode = new Field();
		fieldNode.setType(parseTypeInfo(fieldDoc.type()));
		fieldNode.setName(fieldDoc.name());
		fieldNode.setQualifiedName(fieldDoc.qualifiedName());
		fieldNode.setComment(fieldDoc.commentText());
		fieldNode.setScope(parseScope(fieldDoc));
		fieldNode.setIsFinal(fieldDoc.isFinal());
		fieldNode.setIsStatic(fieldDoc.isStatic());
		fieldNode.setIsVolatile(fieldDoc.isVolatile());
		fieldNode.setIsTransient(fieldDoc.isTransient());
		fieldNode.setConstant(fieldDoc.constantValueExpression());

		for (AnnotationDesc annotationDesc : fieldDoc.annotations()) {
			fieldNode.getAnnotations().add(parseAnnotationDesc(annotationDesc, fieldDoc.qualifiedName()));
		}

		return fieldNode;
	}

	protected TypeInfo parseTypeInfo(Type type) {
		TypeInfo typeInfoNode = new TypeInfo();
		typeInfoNode.setQualifiedName(type.qualifiedTypeName());
		typeInfoNode.setDimension(type.dimension());

		WildcardType wildcard = type.asWildcardType();
		if (wildcard != null) {
			typeInfoNode.setWildcard(parseWildcard(wildcard));
		}

		ParameterizedType parameterized = type.asParameterizedType();
		if (parameterized != null) {
			for (Type typeArgument : parameterized.typeArguments()) {
				typeInfoNode.getGenerics().add(parseTypeInfo(typeArgument));
			}
		}

		return typeInfoNode;
	}

	protected Wildcard parseWildcard(WildcardType wildcard) {
		Wildcard wildcardNode = new Wildcard();

		for (Type extendType : wildcard.extendsBounds()) {
			wildcardNode.getExtendsBounds().add(parseTypeInfo(extendType));
		}

		for (Type superType : wildcard.superBounds()) {
			wildcardNode.getSuperBounds().add(parseTypeInfo(superType));
		}

		return wildcardNode;
	}

	/**
	 * Parse type variables for generics
	 * 
	 * @param typeVariable
	 * @return
	 */
	protected TypeParameter parseTypeParameter(TypeVariable typeVariable) {
		TypeParameter typeParameter = new TypeParameter();
		typeParameter.setName(typeVariable.typeName());

		for (Type bound : typeVariable.bounds()) {
			typeParameter.getBounds().add(bound.qualifiedTypeName());
		}

		return typeParameter;
	}

	/**
	 * Returns string representation of scope
	 * 
	 * @param doc
	 * @return
	 */
	protected String parseScope(ProgramElementDoc doc) {
		if (doc.isPrivate()) {
			return "private";
		} else if (doc.isProtected()) {
			return "protected";
		} else if (doc.isPublic()) {
			return "public";
		}
		return "";
	}
}
