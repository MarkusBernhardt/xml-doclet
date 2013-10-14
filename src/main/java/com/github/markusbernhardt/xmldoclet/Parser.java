package com.github.markusbernhardt.xmldoclet;

import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.markusbernhardt.xmldoclet.xjc.Annotation;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationArgument;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationElement;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationInstance;
import com.github.markusbernhardt.xmldoclet.xjc.Package;
import com.github.markusbernhardt.xmldoclet.xjc.Root;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.AnnotationTypeElementDoc;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Type;

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
				// TODO
				// mediary.addEnum(ParseEnum(classDoc));
			} else if (classDoc.isInterface()) {
				// TODO
				// mediary.addInterface(ParseInterface(classDoc));
			} else if (classDoc.isOrdinaryClass() || classDoc.isException() || classDoc.isError()) {
				// TODO
				// mediary.addClass(ParseClass(classDoc));
			} else {
				// TODO
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

		Annotation annotation = new Annotation();
		annotation.setName(annotationTypeDoc.name());
		annotation.setQualifiedName(annotationTypeDoc.qualifiedName());
		annotation.setComment(annotationTypeDoc.commentText());
		annotation.setIsIncluded(annotationTypeDoc.isIncluded());
		annotation.setScope(parseScope(annotationTypeDoc));

		AnnotationTypeElementDoc[] annotationTypeElementDocs = annotationTypeDoc.elements();
		if (annotationTypeElementDocs != null) {
			for (AnnotationTypeElementDoc annotationTypeElementDoc : annotationTypeElementDocs) {
				annotation.getElements().add(parseAnnotationTypeElementDoc(annotationTypeElementDoc));
			}
		}

		AnnotationDesc[] annotationDescs = annotationTypeDoc.annotations();
		if (annotationDescs != null) {
			for (AnnotationDesc annotationDesc : annotationDescs) {
				annotation.getAnnotations().add(parseAnnotationDesc(annotationDesc, annotationTypeDoc.qualifiedName()));
			}
		}

		return annotation;
	}

	/**
	 * Parse the elements of an annotation
	 * 
	 * @param element
	 *            A AnnotationTypeElementDoc instance
	 * @return the annotation element node
	 */
	protected AnnotationElement parseAnnotationTypeElementDoc(AnnotationTypeElementDoc annotationTypeElementDoc) {
		AnnotationElement annotationElement = new AnnotationElement();
		annotationElement.setName(annotationTypeElementDoc.name());
		annotationElement.setQualifiedName(annotationTypeElementDoc.qualifiedName());
		annotationElement.setType(annotationTypeElementDoc.returnType().qualifiedTypeName());
		AnnotationValue value = annotationTypeElementDoc.defaultValue();
		if (value != null) {
			annotationElement.setDefaultValue(value.toString());
		}
		return annotationElement;
	}

	/**
	 * Parses annotation instances of an annotable program element
	 * 
	 * @param annotationDocs
	 *            Annotations decorated on some program element
	 * @return representation of annotations
	 */
	protected static AnnotationInstance parseAnnotationDesc(AnnotationDesc annotationDesc, String programElement) {
		AnnotationInstance annotation = new AnnotationInstance();

		try {
			AnnotationTypeDoc annotTypeInfo = annotationDesc.annotationType();
			annotation.setName(annotTypeInfo.name());
			annotation.setQualifiedName(annotTypeInfo.qualifiedTypeName());
		} catch (ClassCastException castException) {
			log.error("Unable to obtain type data about an annotation found on: " + programElement);
			log.error("Add to the classpath the class/jar that defines this annotation.");
		}

		AnnotationDesc.ElementValuePair[] elementValuesPairs = annotationDesc.elementValues();
		if (elementValuesPairs != null) {
			for (AnnotationDesc.ElementValuePair elementValuesPair : elementValuesPairs) {
				AnnotationArgument annotationArgument = new AnnotationArgument();
				annotationArgument.setName(elementValuesPair.element().name());

				Type annotationArgumentType = elementValuesPair.element().returnType();
				annotationArgument.setType(annotationArgumentType.qualifiedTypeName());
				annotationArgument.setIsPrimitive(annotationArgumentType.isPrimitive());
				annotationArgument.setIsArray(annotationArgumentType.dimension().length() > 0);

				Object objValue = elementValuesPair.value().value();
				if (objValue instanceof AnnotationValue[]) {
					for (AnnotationValue annotationValue : (AnnotationValue[]) objValue) {
						annotationArgument.getValue().add(annotationValue.value().toString());
					}
				} else if (objValue instanceof FieldDoc) {
					annotationArgument.getValue().add(((FieldDoc) objValue).name());
				} else if (objValue instanceof ClassDoc) {
					annotationArgument.getValue().add(((ClassDoc) objValue).qualifiedTypeName());
				} else {
					annotationArgument.getValue().add(objValue.toString());
				}
				annotation.getArguments().add(annotationArgument);
			}

		}

		return annotation;
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
