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
import com.github.markusbernhardt.xmldoclet.xjc.ObjectFactory;
import com.github.markusbernhardt.xmldoclet.xjc.Package;
import com.github.markusbernhardt.xmldoclet.xjc.Root;
import com.github.markusbernhardt.xmldoclet.xjc.TagInfo;
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
import com.sun.javadoc.Tag;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;
import com.sun.javadoc.WildcardType;

/**
 * The main parser class. It scans the given Doclet document root and creates
 * the XML tree.
 * 
 * @author markus
 */
public class Parser {

	private final static Logger log = LoggerFactory.getLogger(Parser.class);

	protected Map<String, Package> packages = new TreeMap<String, Package>();

	protected ObjectFactory objectFactory = new ObjectFactory();

	/**
	 * The entry point into parsing the javadoc.
	 * 
	 * @param rootDoc
	 *            The RootDoc intstance obtained via the doclet API
	 * @return The root node, containing everything parsed from javadoc doclet
	 */
	public Root parseRootDoc(RootDoc rootDoc) {
		Root rootNode = objectFactory.createRoot();

		for (ClassDoc classDoc : rootDoc.classes()) {
			PackageDoc packageDoc = classDoc.containingPackage();

			Package packageNode = packages.get(packageDoc.name());
			if (packageNode == null) {
				packageNode = parsePackage(packageDoc);
				packages.put(packageDoc.name(), packageNode);
				rootNode.getPackage().add(packageNode);
			}

			if (classDoc instanceof AnnotationTypeDoc) {
				packageNode.getAnnotation().add(parseAnnotationTypeDoc((AnnotationTypeDoc) classDoc));
			} else if (classDoc.isEnum()) {
				packageNode.getEnum().add(parseEnum(classDoc));
			} else if (classDoc.isInterface()) {
				packageNode.getInterface().add(parseInterface(classDoc));
			} else {
				packageNode.getClazz().add(parseClass(classDoc));
			}
		}

		return rootNode;
	}

	protected Package parsePackage(PackageDoc packageDoc) {
		Package packageNode = objectFactory.createPackage();
		packageNode.setName(packageDoc.name());
		String comment = packageDoc.commentText();
		if (comment.length() > 0) {
			packageNode.setComment(comment);
		}

		for (Tag tag : packageDoc.tags()) {
			packageNode.getTag().add(parseTag(tag));
		}

		return packageNode;
	}

	/**
	 * Parse an annotation.
	 * 
	 * @param annotationTypeDoc
	 *            A AnnotationTypeDoc instance
	 * @return the annotation node
	 */
	protected Annotation parseAnnotationTypeDoc(AnnotationTypeDoc annotationTypeDoc) {
		Annotation annotationNode = objectFactory.createAnnotation();
		annotationNode.setName(annotationTypeDoc.name());
		annotationNode.setQualified(annotationTypeDoc.qualifiedName());
		String comment = annotationTypeDoc.commentText();
		if (comment.length() > 0) {
			annotationNode.setComment(comment);
		}
		annotationNode.setIncluded(annotationTypeDoc.isIncluded());
		annotationNode.setScope(parseScope(annotationTypeDoc));

		for (AnnotationTypeElementDoc annotationTypeElementDoc : annotationTypeDoc.elements()) {
			annotationNode.getElement().add(parseAnnotationTypeElementDoc(annotationTypeElementDoc));
		}

		for (AnnotationDesc annotationDesc : annotationTypeDoc.annotations()) {
			annotationNode.getAnnotation().add(parseAnnotationDesc(annotationDesc, annotationTypeDoc.qualifiedName()));
		}

		for (Tag tag : annotationTypeDoc.tags()) {
			annotationNode.getTag().add(parseTag(tag));
		}

		return annotationNode;
	}

	/**
	 * Parse the elements of an annotation
	 * 
	 * @param annotationTypeElementDoc
	 *            A AnnotationTypeElementDoc instance
	 * @return the annotation element node
	 */
	protected AnnotationElement parseAnnotationTypeElementDoc(AnnotationTypeElementDoc annotationTypeElementDoc) {
		AnnotationElement annotationElementNode = objectFactory.createAnnotationElement();
		annotationElementNode.setName(annotationTypeElementDoc.name());
		annotationElementNode.setQualified(annotationTypeElementDoc.qualifiedName());
		annotationElementNode.setType(parseTypeInfo(annotationTypeElementDoc.returnType()));

		AnnotationValue value = annotationTypeElementDoc.defaultValue();
		if (value != null) {
			annotationElementNode.setDefault(value.toString());
		}

		return annotationElementNode;
	}

	/**
	 * Parses annotation instances of an annotable program element
	 * 
	 * @param annotationDesc
	 *            annotationDesc
	 * @param programElement
	 *            programElement
	 * @return representation of annotations
	 */
	protected AnnotationInstance parseAnnotationDesc(AnnotationDesc annotationDesc, String programElement) {
		AnnotationInstance annotationInstanceNode = objectFactory.createAnnotationInstance();

		try {
			AnnotationTypeDoc annotTypeInfo = annotationDesc.annotationType();
			annotationInstanceNode.setName(annotTypeInfo.name());
			annotationInstanceNode.setQualified(annotTypeInfo.qualifiedTypeName());
		} catch (ClassCastException castException) {
			log.error("Unable to obtain type data about an annotation found on: " + programElement);
			log.error("Add to the classpath the class/jar that defines this annotation.");
		}

		for (AnnotationDesc.ElementValuePair elementValuesPair : annotationDesc.elementValues()) {
			AnnotationArgument annotationArgumentNode = objectFactory.createAnnotationArgument();
			annotationArgumentNode.setName(elementValuesPair.element().name());

			Type annotationArgumentType = elementValuesPair.element().returnType();
			annotationArgumentNode.setType(parseTypeInfo(annotationArgumentType));
			annotationArgumentNode.setPrimitive(annotationArgumentType.isPrimitive());
			annotationArgumentNode.setArray(annotationArgumentType.dimension().length() > 0);

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
			annotationInstanceNode.getArgument().add(annotationArgumentNode);
		}

		return annotationInstanceNode;
	}

	protected Enum parseEnum(ClassDoc classDoc) {
		Enum enumNode = objectFactory.createEnum();
		enumNode.setName(classDoc.name());
		enumNode.setQualified(classDoc.qualifiedName());
		String comment = classDoc.commentText();
		if (comment.length() > 0) {
			enumNode.setComment(comment);
		}
		enumNode.setIncluded(classDoc.isIncluded());
		enumNode.setScope(parseScope(classDoc));

		Type superClassType = classDoc.superclassType();
		if (superClassType != null) {
			enumNode.setClazz(parseTypeInfo(superClassType));
		}

		for (Type interfaceType : classDoc.interfaceTypes()) {
			enumNode.getInterface().add(parseTypeInfo(interfaceType));
		}

		for (FieldDoc field : classDoc.enumConstants()) {
			enumNode.getConstant().add(parseEnumConstant(field));
		}

		for (AnnotationDesc annotationDesc : classDoc.annotations()) {
			enumNode.getAnnotation().add(parseAnnotationDesc(annotationDesc, classDoc.qualifiedName()));
		}

		for (Tag tag : classDoc.tags()) {
			enumNode.getTag().add(parseTag(tag));
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
		EnumConstant enumConstant = objectFactory.createEnumConstant();
		enumConstant.setName(fieldDoc.name());
		String comment = fieldDoc.commentText();
		if (comment.length() > 0) {
			enumConstant.setComment(comment);
		}

		for (AnnotationDesc annotationDesc : fieldDoc.annotations()) {
			enumConstant.getAnnotation().add(parseAnnotationDesc(annotationDesc, fieldDoc.qualifiedName()));
		}

		for (Tag tag : fieldDoc.tags()) {
			enumConstant.getTag().add(parseTag(tag));
		}

		return enumConstant;
	}

	protected Interface parseInterface(ClassDoc classDoc) {

		Interface interfaceNode = objectFactory.createInterface();
		interfaceNode.setName(classDoc.name());
		interfaceNode.setQualified(classDoc.qualifiedName());
		String comment = classDoc.commentText();
		if (comment.length() > 0) {
			interfaceNode.setComment(comment);
		}
		interfaceNode.setIncluded(classDoc.isIncluded());
		interfaceNode.setScope(parseScope(classDoc));

		for (TypeVariable typeVariable : classDoc.typeParameters()) {
			interfaceNode.getGeneric().add(parseTypeParameter(typeVariable));
		}

		for (Type interfaceType : classDoc.interfaceTypes()) {
			interfaceNode.getInterface().add(parseTypeInfo(interfaceType));
		}

		for (MethodDoc method : classDoc.methods()) {
			interfaceNode.getMethod().add(parseMethod(method));
		}

		for (AnnotationDesc annotationDesc : classDoc.annotations()) {
			interfaceNode.getAnnotation().add(parseAnnotationDesc(annotationDesc, classDoc.qualifiedName()));
		}

		for (Tag tag : classDoc.tags()) {
			interfaceNode.getTag().add(parseTag(tag));
		}

		for (FieldDoc field : classDoc.fields()) {
			interfaceNode.getField().add(parseField(field));
		}

		return interfaceNode;
	}

	protected Class parseClass(ClassDoc classDoc) {

		Class classNode = objectFactory.createClass();
		classNode.setName(classDoc.name());
		classNode.setQualified(classDoc.qualifiedName());
		String comment = classDoc.commentText();
		if (comment.length() > 0) {
			classNode.setComment(comment);
		}
		classNode.setAbstract(classDoc.isAbstract());
		classNode.setError(classDoc.isError());
		classNode.setException(classDoc.isException());
		classNode.setExternalizable(classDoc.isExternalizable());
		classNode.setIncluded(classDoc.isIncluded());
		classNode.setSerializable(classDoc.isSerializable());
		classNode.setScope(parseScope(classDoc));

		for (TypeVariable typeVariable : classDoc.typeParameters()) {
			classNode.getGeneric().add(parseTypeParameter(typeVariable));
		}

		Type superClassType = classDoc.superclassType();
		if (superClassType != null) {
			classNode.setClazz(parseTypeInfo(superClassType));
		}

		for (Type interfaceType : classDoc.interfaceTypes()) {
			classNode.getInterface().add(parseTypeInfo(interfaceType));
		}

		for (MethodDoc method : classDoc.methods()) {
			classNode.getMethod().add(parseMethod(method));
		}

		for (AnnotationDesc annotationDesc : classDoc.annotations()) {
			classNode.getAnnotation().add(parseAnnotationDesc(annotationDesc, classDoc.qualifiedName()));
		}

		for (ConstructorDoc constructor : classDoc.constructors()) {
			classNode.getConstructor().add(parseConstructor(constructor));
		}

		for (FieldDoc field : classDoc.fields()) {
			classNode.getField().add(parseField(field));
		}

		for (Tag tag : classDoc.tags()) {
			classNode.getTag().add(parseTag(tag));
		}

		return classNode;
	}

	protected Constructor parseConstructor(ConstructorDoc constructorDoc) {
		Constructor constructorNode = objectFactory.createConstructor();

		constructorNode.setName(constructorDoc.name());
		constructorNode.setQualified(constructorDoc.qualifiedName());
		String comment = constructorDoc.commentText();
		if (comment.length() > 0) {
			constructorNode.setComment(comment);
		}
		constructorNode.setScope(parseScope(constructorDoc));
		constructorNode.setIncluded(constructorDoc.isIncluded());
		constructorNode.setFinal(constructorDoc.isFinal());
		constructorNode.setNative(constructorDoc.isNative());
		constructorNode.setStatic(constructorDoc.isStatic());
		constructorNode.setSynchronized(constructorDoc.isSynchronized());
		constructorNode.setVarArgs(constructorDoc.isVarArgs());
		constructorNode.setSignature(constructorDoc.signature());

		for (Parameter parameter : constructorDoc.parameters()) {
			constructorNode.getParameter().add(parseMethodParameter(parameter));
		}

		for (Type exceptionType : constructorDoc.thrownExceptionTypes()) {
			constructorNode.getException().add(parseTypeInfo(exceptionType));
		}

		for (AnnotationDesc annotationDesc : constructorDoc.annotations()) {
			constructorNode.getAnnotation().add(parseAnnotationDesc(annotationDesc, constructorDoc.qualifiedName()));
		}

		for (Tag tag : constructorDoc.tags()) {
			constructorNode.getTag().add(parseTag(tag));
		}

		return constructorNode;
	}

	protected Method parseMethod(MethodDoc methodDoc) {
		Method methodNode = objectFactory.createMethod();

		methodNode.setName(methodDoc.name());
		methodNode.setQualified(methodDoc.qualifiedName());
		String comment = methodDoc.commentText();
		if (comment.length() > 0) {
			methodNode.setComment(comment);
		}
		methodNode.setScope(parseScope(methodDoc));
		methodNode.setAbstract(methodDoc.isAbstract());
		methodNode.setIncluded(methodDoc.isIncluded());
		methodNode.setFinal(methodDoc.isFinal());
		methodNode.setNative(methodDoc.isNative());
		methodNode.setStatic(methodDoc.isStatic());
		methodNode.setSynchronized(methodDoc.isSynchronized());
		methodNode.setVarArgs(methodDoc.isVarArgs());
		methodNode.setSignature(methodDoc.signature());
		methodNode.setReturn(parseTypeInfo(methodDoc.returnType()));

		for (Parameter parameter : methodDoc.parameters()) {
			methodNode.getParameter().add(parseMethodParameter(parameter));
		}

		for (Type exceptionType : methodDoc.thrownExceptionTypes()) {
			methodNode.getException().add(parseTypeInfo(exceptionType));
		}

		for (AnnotationDesc annotationDesc : methodDoc.annotations()) {
			methodNode.getAnnotation().add(parseAnnotationDesc(annotationDesc, methodDoc.qualifiedName()));
		}

		for (Tag tag : methodDoc.tags()) {
			methodNode.getTag().add(parseTag(tag));
		}

		return methodNode;
	}

	protected MethodParameter parseMethodParameter(Parameter parameter) {
		MethodParameter parameterMethodNode = objectFactory.createMethodParameter();
		parameterMethodNode.setName(parameter.name());
		parameterMethodNode.setType(parseTypeInfo(parameter.type()));

		for (AnnotationDesc annotationDesc : parameter.annotations()) {
			parameterMethodNode.getAnnotation().add(parseAnnotationDesc(annotationDesc, parameter.typeName()));
		}

		return parameterMethodNode;
	}

	protected Field parseField(FieldDoc fieldDoc) {
		Field fieldNode = objectFactory.createField();
		fieldNode.setType(parseTypeInfo(fieldDoc.type()));
		fieldNode.setName(fieldDoc.name());
		fieldNode.setQualified(fieldDoc.qualifiedName());
		String comment = fieldDoc.commentText();
		if (comment.length() > 0) {
			fieldNode.setComment(comment);
		}
		fieldNode.setScope(parseScope(fieldDoc));
		fieldNode.setFinal(fieldDoc.isFinal());
		fieldNode.setStatic(fieldDoc.isStatic());
		fieldNode.setVolatile(fieldDoc.isVolatile());
		fieldNode.setTransient(fieldDoc.isTransient());
		fieldNode.setConstant(fieldDoc.constantValueExpression());

		for (AnnotationDesc annotationDesc : fieldDoc.annotations()) {
			fieldNode.getAnnotation().add(parseAnnotationDesc(annotationDesc, fieldDoc.qualifiedName()));
		}

		for (Tag tag : fieldDoc.tags()) {
			fieldNode.getTag().add(parseTag(tag));
		}

		return fieldNode;
	}

	protected TypeInfo parseTypeInfo(Type type) {
		TypeInfo typeInfoNode = objectFactory.createTypeInfo();
		typeInfoNode.setQualified(type.qualifiedTypeName());
		String dimension = type.dimension();
		if (dimension.length() > 0) {
			typeInfoNode.setDimension(dimension);
		}

		WildcardType wildcard = type.asWildcardType();
		if (wildcard != null) {
			typeInfoNode.setWildcard(parseWildcard(wildcard));
		}

		ParameterizedType parameterized = type.asParameterizedType();
		if (parameterized != null) {
			for (Type typeArgument : parameterized.typeArguments()) {
				typeInfoNode.getGeneric().add(parseTypeInfo(typeArgument));
			}
		}

		return typeInfoNode;
	}

	protected Wildcard parseWildcard(WildcardType wildcard) {
		Wildcard wildcardNode = objectFactory.createWildcard();

		for (Type extendType : wildcard.extendsBounds()) {
			wildcardNode.getExtendsBound().add(parseTypeInfo(extendType));
		}

		for (Type superType : wildcard.superBounds()) {
			wildcardNode.getSuperBound().add(parseTypeInfo(superType));
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
		TypeParameter typeParameter = objectFactory.createTypeParameter();
		typeParameter.setName(typeVariable.typeName());

		for (Type bound : typeVariable.bounds()) {
			typeParameter.getBound().add(bound.qualifiedTypeName());
		}

		return typeParameter;
	}

	protected TagInfo parseTag(Tag tagDoc) {
		TagInfo tagNode = objectFactory.createTagInfo();
		tagNode.setName(tagDoc.kind());
		tagNode.setText(tagDoc.text());
		return tagNode;
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
