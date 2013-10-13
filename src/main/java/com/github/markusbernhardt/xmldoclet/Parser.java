package com.github.markusbernhardt.xmldoclet;

import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.markusbernhardt.xmldoclet.xjc.Annotation;
import com.github.markusbernhardt.xmldoclet.xjc.Package;
import com.github.markusbernhardt.xmldoclet.xjc.Root;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.RootDoc;

public class Parser {

	private final static Logger log = LoggerFactory.getLogger(Parser.class);

	protected Map<PackageDoc, Package> packages = new TreeMap<PackageDoc, Package>();

	/**
	 * The entry point into parsing the javadoc.
	 * 
	 * @param rootDoc
	 *            A RootDoc intstance obtained via the doclet API
	 * @return The root element, containing everything parsed from javadoc
	 *         doclet
	 */
	public Root parseRoot(RootDoc rootDoc) {
		Root rootNode = new Root();

		for (ClassDoc classDoc : rootDoc.classes()) {
			PackageDoc packageDoc = classDoc.containingPackage();

			// get or create Package node
			Package packageNode = packages.get(packageDoc);
			if (packageNode == null) {
				packageNode = new Package();
				packages.put(packageDoc, packageNode);
			}

			if (classDoc.isIncluded()) {

				if (classDoc instanceof AnnotationTypeDoc) {
					packageNode.getAnnotations().add(parseAnnotationType((AnnotationTypeDoc) classDoc));
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
			} else {
				log.debug("Skipping not-included class " + classDoc.qualifiedName());
			}
		}

		return rootNode;
	}

	protected Annotation parseAnnotationType(AnnotationTypeDoc classDoc) {
		return null;
	}

}
