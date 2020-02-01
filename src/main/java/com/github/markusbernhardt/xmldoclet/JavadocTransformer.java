package com.github.markusbernhardt.xmldoclet;

import com.github.markusbernhardt.xmldoclet.xjc.Root;
import com.github.markusbernhardt.xmldoclet.xjc.TagInfo;
import com.github.markusbernhardt.xmldoclet.xjc.TypeInfo;
import com.github.markusbernhardt.xmldoclet.xjc.Wildcard;
import com.sun.source.doctree.BlockTagTree;
import com.sun.source.doctree.DocCommentTree;
import com.sun.source.doctree.DocTree;
import com.sun.source.util.DocTrees;
import java.io.Externalizable;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.QualifiedNameable;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import javax.tools.JavaFileObject;
import jdk.javadoc.doclet.DocletEnvironment;

public class JavadocTransformer {

	private final DocletEnvironment environment;
	private final DocTrees docTrees;
	private final Elements elementUtils;
	private final Types typeUtils;
	private final TypeMirror objectTypeMirror;
	private final TypeElement errorTypeElement;
	private final TypeElement exceptionTypeElement;
	private final TypeElement externalizableTypeElement;
	private final TypeElement serializableTypeElement;

	public JavadocTransformer(DocletEnvironment environment) {
		this.environment = environment;
		this.docTrees = environment.getDocTrees();
		this.elementUtils = environment.getElementUtils();
		this.typeUtils = environment.getTypeUtils();
		this.objectTypeMirror = elementUtils.getTypeElement(Object.class.getCanonicalName()).asType();
		this.errorTypeElement = elementUtils.getTypeElement(Error.class.getCanonicalName());
		this.exceptionTypeElement = elementUtils.getTypeElement(Exception.class.getCanonicalName());
		this.externalizableTypeElement = elementUtils.getTypeElement(Externalizable.class.getCanonicalName());
		this.serializableTypeElement = elementUtils.getTypeElement(Serializable.class.getCanonicalName());
	}

	public Root transform() {
		final Root xmlRoot = new Root();
		transformElements(xmlRoot, environment.getSpecifiedElements());
		return xmlRoot;
	}

	private void transformElements(Root xmlRoot, Collection<? extends Element> elements) {
		for (Element element : elements) {
			transformElement(xmlRoot, element);
		}
	}

	private void transformElement(Root xmlRoot, Element element) {
		if (element instanceof PackageElement) {
			transformPackageElement(xmlRoot, (PackageElement) element);
		}
		if (element instanceof TypeElement) {
			transformTypeElement(xmlRoot, (TypeElement) element);
		}
	}

	private void transformPackageElement(Root xmlRoot, PackageElement packageElement) {
		final com.github.markusbernhardt.xmldoclet.xjc.Package xmlPackage = new com.github.markusbernhardt.xmldoclet.xjc.Package();
		xmlPackage.setName(packageElement.getQualifiedName().toString());
		transformJavadoc(packageElement, xmlPackage::setComment, xmlPackage::getTag);
		xmlRoot.getPackage().add(xmlPackage);
		transformElements(xmlRoot, packageElement.getEnclosedElements());
	}

	private void transformTypeElement(Root xmlRoot, TypeElement typeElement) {
		if (environment.getFileKind(typeElement) != JavaFileObject.Kind.SOURCE) {
			return;
		}
		if (!environment.isIncluded(typeElement)) {
			return;
		}
		final PackageElement packageElement = getEnclosingPackage(typeElement);
		if (typeElement.getKind() == ElementKind.ANNOTATION_TYPE) {
			final com.github.markusbernhardt.xmldoclet.xjc.Annotation xmlAnnotation = new com.github.markusbernhardt.xmldoclet.xjc.Annotation();
			setNames(typeElement, packageElement, xmlAnnotation::setName, xmlAnnotation::setQualified);
			xmlAnnotation.setScope(getScope(typeElement.getModifiers()));
			xmlAnnotation.setIncluded(environment.isIncluded(typeElement));
			transformJavadoc(typeElement, xmlAnnotation::setComment, xmlAnnotation::getTag);
			xmlAnnotation.getAnnotation().addAll(transformAnnotationMirrors(typeElement.getAnnotationMirrors()));
			for (Element enclosedElement : typeElement.getEnclosedElements()) {
				if (!environment.isIncluded(enclosedElement)) {
					continue;
				}
				if (enclosedElement.getKind() == ElementKind.METHOD) {
					final ExecutableElement methodElement = (ExecutableElement) enclosedElement;
					xmlAnnotation.getElement().add(transformAnnotationElement(methodElement, xmlAnnotation.getQualified()));
				}
			}
			getXmlPackage(xmlRoot, packageElement).getAnnotation().add(xmlAnnotation);
		}
		if (typeElement.getKind() == ElementKind.ENUM) {
			final com.github.markusbernhardt.xmldoclet.xjc.Enum xmlEnum = new com.github.markusbernhardt.xmldoclet.xjc.Enum();
			setNames(typeElement, packageElement, xmlEnum::setName, xmlEnum::setQualified);
			xmlEnum.setScope(getScope(typeElement.getModifiers()));
			xmlEnum.setIncluded(environment.isIncluded(typeElement));
			transformJavadoc(typeElement, xmlEnum::setComment, xmlEnum::getTag);
			xmlEnum.setClazz(transformTypeMirror(typeElement.getSuperclass()));
			xmlEnum.getInterface().addAll(transformTypeMirrors(typeElement.getInterfaces()));
			xmlEnum.getAnnotation().addAll(transformAnnotationMirrors(typeElement.getAnnotationMirrors()));
			for (Element enclosedElement : typeElement.getEnclosedElements()) {
				if (!environment.isIncluded(enclosedElement)) {
					continue;
				}
				if (enclosedElement.getKind() == ElementKind.ENUM_CONSTANT) {
					final VariableElement constantElement = (VariableElement) enclosedElement;
					xmlEnum.getConstant().add(transformEnumConstant(constantElement));
				}
			}
			getXmlPackage(xmlRoot, packageElement).getEnum().add(xmlEnum);
		}
		if (typeElement.getKind() == ElementKind.INTERFACE) {
			final com.github.markusbernhardt.xmldoclet.xjc.Interface xmlInterface = new com.github.markusbernhardt.xmldoclet.xjc.Interface();
			setNames(typeElement, packageElement, xmlInterface::setName, xmlInterface::setQualified);
			xmlInterface.setScope(getScope(typeElement.getModifiers()));
			xmlInterface.setIncluded(environment.isIncluded(typeElement));
			transformJavadoc(typeElement, xmlInterface::setComment, xmlInterface::getTag);
			xmlInterface.getGeneric().addAll(transformTypeParameters(typeElement.getTypeParameters()));
			xmlInterface.getInterface().addAll(transformTypeMirrors(typeElement.getInterfaces()));
			xmlInterface.getAnnotation().addAll(transformAnnotationMirrors(typeElement.getAnnotationMirrors()));
			for (Element enclosedElement : typeElement.getEnclosedElements()) {
				if (!environment.isIncluded(enclosedElement)) {
					continue;
				}
				if (enclosedElement.getKind() == ElementKind.FIELD) {
					final VariableElement fieldElement = (VariableElement) enclosedElement;
					xmlInterface.getField().add(transformFieldElement(fieldElement, xmlInterface.getQualified()));
				}
				if (enclosedElement.getKind() == ElementKind.METHOD) {
					final ExecutableElement methodElement = (ExecutableElement) enclosedElement;
					xmlInterface.getMethod().add(transformMethodElement(methodElement, xmlInterface.getQualified()));
				}
			}
			getXmlPackage(xmlRoot, packageElement).getInterface().add(xmlInterface);
		}
		if (typeElement.getKind() == ElementKind.CLASS) {
			final com.github.markusbernhardt.xmldoclet.xjc.Class xmlClass = new com.github.markusbernhardt.xmldoclet.xjc.Class();
			setNames(typeElement, packageElement, xmlClass::setName, xmlClass::setQualified);
			xmlClass.setScope(getScope(typeElement.getModifiers()));
			setFlag(typeElement, Modifier.ABSTRACT, xmlClass::setAbstract);
			xmlClass.setError(environment.getTypeUtils().isAssignable(typeElement.asType(), errorTypeElement.asType()));
			xmlClass.setException(environment.getTypeUtils().isAssignable(typeElement.asType(), exceptionTypeElement.asType()));
			xmlClass.setExternalizable(environment.getTypeUtils().isAssignable(typeElement.asType(), externalizableTypeElement.asType()));
			xmlClass.setIncluded(environment.isIncluded(typeElement));
			xmlClass.setSerializable(environment.getTypeUtils().isAssignable(typeElement.asType(), serializableTypeElement.asType()));
			transformJavadoc(typeElement, xmlClass::setComment, xmlClass::getTag);
			xmlClass.getGeneric().addAll(transformTypeParameters(typeElement.getTypeParameters()));
			xmlClass.setClazz(transformTypeMirror(typeElement.getSuperclass()));
			xmlClass.getInterface().addAll(transformTypeMirrors(typeElement.getInterfaces()));
			xmlClass.getAnnotation().addAll(transformAnnotationMirrors(typeElement.getAnnotationMirrors()));
			for (Element enclosedElement : typeElement.getEnclosedElements()) {
				if (!environment.isIncluded(enclosedElement)) {
					continue;
				}
				if (enclosedElement.getKind() == ElementKind.FIELD) {
					final VariableElement fieldElement = (VariableElement) enclosedElement;
					xmlClass.getField().add(transformFieldElement(fieldElement, xmlClass.getQualified()));
				}
				if (enclosedElement.getKind() == ElementKind.CONSTRUCTOR) {
					final ExecutableElement constructorElement = (ExecutableElement) enclosedElement;
					xmlClass.getConstructor().add(transformConstructorElement(constructorElement, xmlClass.getQualified()));
				}
				if (enclosedElement.getKind() == ElementKind.METHOD) {
					final ExecutableElement methodElement = (ExecutableElement) enclosedElement;
					xmlClass.getMethod().add(transformMethodElement(methodElement, xmlClass.getQualified()));
				}
			}
			getXmlPackage(xmlRoot, packageElement).getClazz().add(xmlClass);
		}
		transformElements(xmlRoot, typeElement.getEnclosedElements());
	}

	private com.github.markusbernhardt.xmldoclet.xjc.AnnotationElement transformAnnotationElement(ExecutableElement annotationElement, String qualified) {
		final com.github.markusbernhardt.xmldoclet.xjc.AnnotationElement xmlElement = new com.github.markusbernhardt.xmldoclet.xjc.AnnotationElement();
		xmlElement.setName(annotationElement.getSimpleName().toString());
		xmlElement.setQualified(qualified + "." + xmlElement.getName());
		if (annotationElement.getDefaultValue() != null) {
			xmlElement.setDefault(String.valueOf(annotationElement.getDefaultValue().getValue()));
		}
		xmlElement.setType(transformTypeMirror(annotationElement.getReturnType()));
		return xmlElement;
	}

	private com.github.markusbernhardt.xmldoclet.xjc.EnumConstant transformEnumConstant(VariableElement constantElement) {
		final com.github.markusbernhardt.xmldoclet.xjc.EnumConstant xmlConstant = new com.github.markusbernhardt.xmldoclet.xjc.EnumConstant();
		xmlConstant.setName(constantElement.getSimpleName().toString());
		transformJavadoc(constantElement, xmlConstant::setComment, xmlConstant::getTag);
		xmlConstant.getAnnotation().addAll(transformAnnotationMirrors(constantElement.getAnnotationMirrors()));
		return xmlConstant;
	}

	private com.github.markusbernhardt.xmldoclet.xjc.Field transformFieldElement(VariableElement variableElement, String qualified) {
		final com.github.markusbernhardt.xmldoclet.xjc.Field xmlField = new com.github.markusbernhardt.xmldoclet.xjc.Field();
		xmlField.setName(variableElement.getSimpleName().toString());
		xmlField.setQualified(qualified + "." + xmlField.getName());
		xmlField.setScope(getScope(variableElement.getModifiers()));
		setFlag(variableElement, Modifier.VOLATILE, xmlField::setVolatile);
		setFlag(variableElement, Modifier.TRANSIENT, xmlField::setTransient);
		setFlag(variableElement, Modifier.STATIC, xmlField::setStatic);
		setFlag(variableElement, Modifier.FINAL, xmlField::setFinal);
		xmlField.setType(transformTypeMirror(variableElement.asType()));
		transformJavadoc(variableElement, xmlField::setComment, xmlField::getTag);
		final Object constantValue = variableElement.getConstantValue();
		if (constantValue != null) {
			xmlField.setConstant(elementUtils.getConstantExpression(constantValue));
		}
		xmlField.getAnnotation().addAll(transformAnnotationMirrors(variableElement.getAnnotationMirrors()));
		return xmlField;
	}

	private com.github.markusbernhardt.xmldoclet.xjc.Constructor transformConstructorElement(ExecutableElement constructorElement, String qualified) {
		final com.github.markusbernhardt.xmldoclet.xjc.Constructor xmlConstructor = new com.github.markusbernhardt.xmldoclet.xjc.Constructor();
		xmlConstructor.setName(constructorElement.getEnclosingElement().getSimpleName().toString());
		xmlConstructor.setSignature(getSignature(constructorElement));
		xmlConstructor.setQualified(qualified);
		xmlConstructor.setScope(getScope(constructorElement.getModifiers()));
		setFlag(constructorElement, Modifier.FINAL, xmlConstructor::setFinal);
		xmlConstructor.setIncluded(environment.isIncluded(constructorElement));
		setFlag(constructorElement, Modifier.NATIVE, xmlConstructor::setNative);
		setFlag(constructorElement, Modifier.SYNCHRONIZED, xmlConstructor::setSynchronized);
		setFlag(constructorElement, Modifier.STATIC, xmlConstructor::setStatic);
		xmlConstructor.setVarArgs(constructorElement.isVarArgs());
		transformJavadoc(constructorElement, xmlConstructor::setComment, xmlConstructor::getTag);
		xmlConstructor.getParameter().addAll(transformParameters(constructorElement));
		xmlConstructor.getException().addAll(transformExceptions(constructorElement));
		xmlConstructor.getAnnotation().addAll(transformAnnotationMirrors(constructorElement.getAnnotationMirrors()));
		return xmlConstructor;
	}

	private com.github.markusbernhardt.xmldoclet.xjc.Method transformMethodElement(ExecutableElement methodElement, String qualified) {
		final com.github.markusbernhardt.xmldoclet.xjc.Method xmlMethod = new com.github.markusbernhardt.xmldoclet.xjc.Method();
		xmlMethod.setName(methodElement.getSimpleName().toString());
		xmlMethod.setSignature(getSignature(methodElement));
		xmlMethod.setQualified(qualified + "." + xmlMethod.getName());
		xmlMethod.setScope(getScope(methodElement.getModifiers()));
		setFlag(methodElement, Modifier.ABSTRACT, xmlMethod::setAbstract);
		setFlag(methodElement, Modifier.FINAL, xmlMethod::setFinal);
		xmlMethod.setIncluded(environment.isIncluded(methodElement));
		setFlag(methodElement, Modifier.NATIVE, xmlMethod::setNative);
		setFlag(methodElement, Modifier.SYNCHRONIZED, xmlMethod::setSynchronized);
		setFlag(methodElement, Modifier.STATIC, xmlMethod::setStatic);
		xmlMethod.setVarArgs(methodElement.isVarArgs());
		transformJavadoc(methodElement, xmlMethod::setComment, xmlMethod::getTag);
		xmlMethod.getParameter().addAll(transformParameters(methodElement));
		xmlMethod.setReturn(transformTypeMirror(methodElement.getReturnType()));
		xmlMethod.getException().addAll(transformExceptions(methodElement));
		xmlMethod.getAnnotation().addAll(transformAnnotationMirrors(methodElement.getAnnotationMirrors()));
		return xmlMethod;
	}

	private List<TypeInfo> transformExceptions(ExecutableElement executableElement) {
		return transformTypeMirrors(executableElement.getThrownTypes());
	}

	private List<com.github.markusbernhardt.xmldoclet.xjc.MethodParameter> transformParameters(ExecutableElement executableElement) {
		return executableElement.getParameters().stream()
				.map(this::transformParameter)
				.collect(Collectors.toList());
	}

	private com.github.markusbernhardt.xmldoclet.xjc.MethodParameter transformParameter(VariableElement methodParameter) {
		final com.github.markusbernhardt.xmldoclet.xjc.MethodParameter xmlParameter = new com.github.markusbernhardt.xmldoclet.xjc.MethodParameter();
		xmlParameter.setName(methodParameter.getSimpleName().toString());
		xmlParameter.setType(transformTypeMirror(methodParameter.asType()));
		xmlParameter.getAnnotation().addAll(transformAnnotationMirrors(methodParameter.getAnnotationMirrors()));
		return xmlParameter;
	}

	private List<com.github.markusbernhardt.xmldoclet.xjc.AnnotationInstance> transformAnnotationMirrors(List<? extends AnnotationMirror> annotationMirrors) {
		return annotationMirrors.stream()
				.map(this::transformAnnotationMirror)
				.collect(Collectors.toList());
	}

	private com.github.markusbernhardt.xmldoclet.xjc.AnnotationInstance transformAnnotationMirror(AnnotationMirror annotationMirror) {
		final com.github.markusbernhardt.xmldoclet.xjc.AnnotationInstance xmlAnnotation = new com.github.markusbernhardt.xmldoclet.xjc.AnnotationInstance();
		setNames(annotationMirror.getAnnotationType().asElement(), xmlAnnotation::setName, xmlAnnotation::setQualified);
		xmlAnnotation.getArgument().addAll(transformAnnotationValues(annotationMirror.getElementValues()));
		return xmlAnnotation;
	}

	private List<com.github.markusbernhardt.xmldoclet.xjc.AnnotationArgument> transformAnnotationValues(Map<? extends ExecutableElement, ? extends AnnotationValue> annotationValues) {
		return annotationValues.entrySet().stream()
				.map(entry -> transformAnnotationValue(entry.getKey(), entry.getValue()))
				.collect(Collectors.toList());
	}

	private com.github.markusbernhardt.xmldoclet.xjc.AnnotationArgument transformAnnotationValue(ExecutableElement element, AnnotationValue annotationValue) {
		final com.github.markusbernhardt.xmldoclet.xjc.AnnotationArgument xmlArgument = new com.github.markusbernhardt.xmldoclet.xjc.AnnotationArgument();
		xmlArgument.setName(element.getSimpleName().toString());
		final TypeMirror type = element.getReturnType();
		xmlArgument.setType(transformTypeMirror(type));
		final Object value = annotationValue.getValue();
		if (type instanceof ArrayType) {
			final ArrayType arrayType = (ArrayType) type;
			final TypeMirror componentType = arrayType.getComponentType();
			xmlArgument.setPrimitive(componentType instanceof PrimitiveType);
			xmlArgument.setArray(true);
			if (value instanceof List<?>) {
				final List<?> list = (List<?>) value;
				for (Object item : list) {
					if (item instanceof AnnotationValue) {
						final AnnotationValue annotationValueItem = (AnnotationValue) item;
//						transformAnnotationSingleValueCompatibleMode(xmlArgument, annotationValueItem.getValue(), componentType);
						transformAnnotationSingleValue(xmlArgument, annotationValueItem.getValue(), componentType);
					}
				}
			}
		} else {
			xmlArgument.setPrimitive(type instanceof PrimitiveType);
			xmlArgument.setArray(false);
			transformAnnotationSingleValue(xmlArgument, value, type);
		}
		return xmlArgument;
	}

	// TODO remove this method
	@Deprecated
	private void transformAnnotationSingleValueCompatibleMode(com.github.markusbernhardt.xmldoclet.xjc.AnnotationArgument xmlArgument, Object value, TypeMirror type) {
		if (value instanceof VariableElement) {
			// compatible mode
			final VariableElement variableElement = (VariableElement) value;
			final String qualifiedName = variableElement.getEnclosingElement() + "." + variableElement;
			xmlArgument.getValue().add(qualifiedName);
		} else {
			transformAnnotationSingleValue(xmlArgument, value, type);
		}
	}

	private void transformAnnotationSingleValue(com.github.markusbernhardt.xmldoclet.xjc.AnnotationArgument xmlArgument, Object value, TypeMirror type) {
		if (type instanceof PrimitiveType) {
			if (value instanceof Character) {
				final Character character = (Character) value;
				xmlArgument.getValue().add(String.valueOf((long) character));
			} else {
				xmlArgument.getValue().add(String.valueOf(value));
			}
		} else if (value instanceof String) {
			xmlArgument.getValue().add(String.valueOf(value));
		} else if (value instanceof TypeMirror) {
			final TypeMirror typeMirror = (TypeMirror) value;
			xmlArgument.getValue().add(typeMirror.toString());
		} else if (value instanceof VariableElement) {
			final VariableElement variableElement = (VariableElement) value;
			xmlArgument.getValue().add(variableElement.getSimpleName().toString());
		} else if (value instanceof AnnotationMirror) {
			final AnnotationMirror annotationMirror = (AnnotationMirror) value;
			xmlArgument.getAnnotation().add(transformAnnotationMirror(annotationMirror));
		} else if (value instanceof List<?>) {
			// already handled by calling method
		}
	}

	private List<com.github.markusbernhardt.xmldoclet.xjc.TypeParameter> transformTypeParameters(List<? extends TypeParameterElement> typeParameterElements) {
		return typeParameterElements.stream()
				.map(this::transformTypeParameter)
				.collect(Collectors.toList());
	}

	private com.github.markusbernhardt.xmldoclet.xjc.TypeParameter transformTypeParameter(TypeParameterElement typeParameterElement) {
		final com.github.markusbernhardt.xmldoclet.xjc.TypeParameter xmlTypeParameter = new com.github.markusbernhardt.xmldoclet.xjc.TypeParameter();
		xmlTypeParameter.setName(typeParameterElement.getSimpleName().toString());
		final List<String> bounds = typeParameterElement.getBounds().stream()
				.filter(bound -> !typeUtils.isSameType(bound, objectTypeMirror))
				.map(TypeMirror::toString)
				.collect(Collectors.toList());
		xmlTypeParameter.getBound().addAll(bounds);
		return xmlTypeParameter;
	}

	private void transformJavadoc(Element element, Consumer<String> commentSetter, Supplier<List<TagInfo>> tagsGetter) {
		final DocCommentTree docCommentTree = docTrees.getDocCommentTree(element);
		if (docCommentTree != null) {
			final String fullBody = docCommentTree.getFullBody().stream()
					.map(Object::toString)
					.collect(Collectors.joining());
			if (!fullBody.isEmpty()) {
				commentSetter.accept(fullBody);
			}
			final List<TagInfo> tags = tagsGetter.get();
			for (DocTree blockTag : docCommentTree.getBlockTags()) {
				if (blockTag instanceof BlockTagTree) {
					final BlockTagTree blockTagTree = (BlockTagTree) blockTag;
					final String[] parts = blockTagTree.toString().split(" ", 2);
					if (parts.length == 2) {
						final TagInfo tag = new TagInfo();
						tag.setName(parts[0]);
						tag.setText(parts[1]);
						tags.add(tag);
					}
				}
			}
		}
	}

	private PackageElement getEnclosingPackage(Element element) {
		Objects.requireNonNull(element);
		return element instanceof PackageElement
				? (PackageElement) element
				: getEnclosingPackage(element.getEnclosingElement());
	}

	private static String getScope(Set<Modifier> modifiers) {
		if (modifiers.contains(Modifier.PUBLIC)) return "public";
		if (modifiers.contains(Modifier.PROTECTED)) return "protected";
		if (modifiers.contains(Modifier.PRIVATE)) return "private";
		return "";
	}

	private static String getSignature(ExecutableElement executableElement) {
		final StringBuilder sb = new StringBuilder();
		sb.append("(");
		final List<? extends VariableElement> parameters = executableElement.getParameters();
		final Iterator<? extends VariableElement> iterator = parameters.iterator();
		while (iterator.hasNext()) {
			final VariableElement variableElement = iterator.next();
			if (iterator.hasNext()) {
				sb.append(variableElement.asType().toString());
				sb.append(", ");
			} else {
				if (variableElement.asType() instanceof ArrayType && executableElement.isVarArgs()) {
					final ArrayType arrayType = (ArrayType) variableElement.asType();
					sb.append(arrayType.getComponentType().toString());
					sb.append("...");
				} else {
					sb.append(variableElement.asType().toString());
				}
			}
		}
		sb.append(")");
		return sb.toString();
	}

	private List<TypeInfo> transformTypeMirrorIfNonNull(TypeMirror typeMirror) {
		return typeMirror != null
				? Collections.singletonList(transformTypeMirror(typeMirror))
				: Collections.emptyList();
	}

	private List<TypeInfo> transformTypeMirrors(List<? extends TypeMirror> typeMirrors) {
		return typeMirrors.stream()
				.map(this::transformTypeMirror)
				.collect(Collectors.toList());
	}

	private TypeInfo transformTypeMirror(TypeMirror typeMirror) {
		final TypeInfo xmlTypeInfo = new TypeInfo();
		if (typeMirror instanceof DeclaredType) {
			xmlTypeInfo.setQualified(typeUtils.erasure(typeMirror).toString());
			final DeclaredType declaredType = (DeclaredType) typeMirror;
			final List<TypeInfo> generic = declaredType.getTypeArguments().stream()
					.map(this::transformTypeMirror)
					.collect(Collectors.toList());
			xmlTypeInfo.getGeneric().addAll(generic);
		} else if (typeMirror instanceof ArrayType) {
			final ArrayType arrayType = (ArrayType) typeMirror;
			final TypeInfo typeInfo = transformTypeMirror(arrayType.getComponentType());
			typeInfo.setDimension((typeInfo.getDimension() != null ? typeInfo.getDimension() : "") + "[]");
			return typeInfo;
		} else if (typeMirror instanceof WildcardType) {
			xmlTypeInfo.setQualified("?");
			final WildcardType wildcardType = (WildcardType) typeMirror;
			final Wildcard wildcard = new Wildcard();
			wildcard.getExtendsBound().addAll(transformTypeMirrorIfNonNull(wildcardType.getExtendsBound()));
			wildcard.getSuperBound().addAll(transformTypeMirrorIfNonNull(wildcardType.getSuperBound()));
			xmlTypeInfo.setWildcard(wildcard);
		} else {
			xmlTypeInfo.setQualified(typeMirror.toString());
		}
		return xmlTypeInfo;
	}

	private static void setFlag(Element element, Modifier modifier, Consumer<Boolean> flagSetter) {
		flagSetter.accept(element.getModifiers().contains(modifier));
	}

	private com.github.markusbernhardt.xmldoclet.xjc.Package getXmlPackage(Root xmlRoot, PackageElement packageElement) {
		final com.github.markusbernhardt.xmldoclet.xjc.Package xmlPackage = xmlRoot.getPackage().stream()
				.filter(p -> Objects.equals(p.getName(), packageElement.getQualifiedName().toString()))
				.findFirst()
				.orElse(null);
		if (xmlPackage == null) {
			final com.github.markusbernhardt.xmldoclet.xjc.Package newXmlPackage = new com.github.markusbernhardt.xmldoclet.xjc.Package();
			newXmlPackage.setName(packageElement.getQualifiedName().toString());
			xmlRoot.getPackage().add(newXmlPackage);
			return newXmlPackage;
		} else {
			return xmlPackage;
		}
	}

	private void setNames(QualifiedNameable qualifiedNameable, PackageElement packageElement, Consumer<String> nameSetter, Consumer<String> qualifiedSetter) {
		setNames(qualifiedNameable.getQualifiedName().toString(), packageElement, nameSetter, qualifiedSetter);
	}

	private void setNames(Element element, Consumer<String> nameSetter, Consumer<String> qualifiedSetter) {
		setNames(element.toString(), getEnclosingPackage(element), nameSetter, qualifiedSetter);
	}

	private void setNames(String qualifiedName, PackageElement packageElement, Consumer<String> nameSetter, Consumer<String> qualifiedSetter) {
		final String packageName = packageElement.getQualifiedName().toString();
		final String packagePrefix = packageName.isEmpty() ? "" : packageName + ".";
		final String name = qualifiedName.substring(packagePrefix.length());
		nameSetter.accept(name);
		qualifiedSetter.accept(qualifiedName);
	}

}
