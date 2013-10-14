package com.github.markusbernhardt.xmldoclet;

import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.markusbernhardt.xmldoclet.xjc.Annotation;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationArgument;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationElement;
import com.github.markusbernhardt.xmldoclet.xjc.AnnotationInstance;
import com.github.markusbernhardt.xmldoclet.xjc.Enum;
import com.github.markusbernhardt.xmldoclet.xjc.EnumConstant;
import com.github.markusbernhardt.xmldoclet.xjc.GenericTypeVariable;
import com.github.markusbernhardt.xmldoclet.xjc.Interface;
import com.github.markusbernhardt.xmldoclet.xjc.Method;
import com.github.markusbernhardt.xmldoclet.xjc.MethodParameter;
import com.github.markusbernhardt.xmldoclet.xjc.MethodParameterType;
import com.github.markusbernhardt.xmldoclet.xjc.Package;
import com.github.markusbernhardt.xmldoclet.xjc.Root;
import com.sun.javadoc.AnnotationDesc;
import com.sun.javadoc.AnnotationTypeDoc;
import com.sun.javadoc.AnnotationTypeElementDoc;
import com.sun.javadoc.AnnotationValue;
import com.sun.javadoc.ClassDoc;
import com.sun.javadoc.FieldDoc;
import com.sun.javadoc.MethodDoc;
import com.sun.javadoc.PackageDoc;
import com.sun.javadoc.Parameter;
import com.sun.javadoc.ProgramElementDoc;
import com.sun.javadoc.RootDoc;
import com.sun.javadoc.Type;
import com.sun.javadoc.TypeVariable;

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
                packageNode.getAnnotations().add(
                        parseAnnotationTypeDoc((AnnotationTypeDoc) classDoc));
            } else if (classDoc.isEnum()) {
                packageNode.getEnums().add(parseEnum(classDoc));
            } else if (classDoc.isInterface()) {
                packageNode.getInterfaces().add(parseInterface(classDoc));
            } else if (classDoc.isOrdinaryClass() || classDoc.isException()
                    || classDoc.isError()) {
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
    protected Annotation parseAnnotationTypeDoc(
            AnnotationTypeDoc annotationTypeDoc) {
        log.debug("Parsing annotation " + annotationTypeDoc.qualifiedName());

        Annotation annotationNode = new Annotation();
        annotationNode.setName(annotationTypeDoc.name());
        annotationNode.setQualifiedName(annotationTypeDoc.qualifiedName());
        annotationNode.setComment(annotationTypeDoc.commentText());
        annotationNode.setIsIncluded(annotationTypeDoc.isIncluded());
        annotationNode.setScope(parseScope(annotationTypeDoc));

        AnnotationTypeElementDoc[] annotationTypeElementDocs = annotationTypeDoc
                .elements();
        if (annotationTypeElementDocs != null) {
            for (AnnotationTypeElementDoc annotationTypeElementDoc : annotationTypeElementDocs) {
                annotationNode
                        .getElements()
                        .add(parseAnnotationTypeElementDoc(annotationTypeElementDoc));
            }
        }

        AnnotationDesc[] annotationDescs = annotationTypeDoc.annotations();
        if (annotationDescs != null) {
            for (AnnotationDesc annotationDesc : annotationDescs) {
                annotationNode.getAnnotations().add(
                        parseAnnotationDesc(annotationDesc,
                                annotationTypeDoc.qualifiedName()));
            }
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
    protected AnnotationElement parseAnnotationTypeElementDoc(
            AnnotationTypeElementDoc annotationTypeElementDoc) {
        AnnotationElement annotationElementNode = new AnnotationElement();
        annotationElementNode.setName(annotationTypeElementDoc.name());
        annotationElementNode.setQualifiedName(annotationTypeElementDoc
                .qualifiedName());
        annotationElementNode.setType(annotationTypeElementDoc.returnType()
                .qualifiedTypeName());
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
    protected AnnotationInstance parseAnnotationDesc(
            AnnotationDesc annotationDesc, String programElement) {
        AnnotationInstance annotationInstanceNode = new AnnotationInstance();

        try {
            AnnotationTypeDoc annotTypeInfo = annotationDesc.annotationType();
            annotationInstanceNode.setName(annotTypeInfo.name());
            annotationInstanceNode.setQualifiedName(annotTypeInfo
                    .qualifiedTypeName());
        } catch (ClassCastException castException) {
            log.error("Unable to obtain type data about an annotation found on: "
                    + programElement);
            log.error("Add to the classpath the class/jar that defines this annotation.");
        }

        AnnotationDesc.ElementValuePair[] elementValuesPairs = annotationDesc
                .elementValues();
        if (elementValuesPairs != null) {
            for (AnnotationDesc.ElementValuePair elementValuesPair : elementValuesPairs) {
                AnnotationArgument annotationArgumentNode = new AnnotationArgument();
                annotationArgumentNode.setName(elementValuesPair.element()
                        .name());

                Type annotationArgumentType = elementValuesPair.element()
                        .returnType();
                annotationArgumentNode.setType(annotationArgumentType
                        .qualifiedTypeName());
                annotationArgumentNode.setIsPrimitive(annotationArgumentType
                        .isPrimitive());
                annotationArgumentNode.setIsArray(annotationArgumentType
                        .dimension().length() > 0);

                Object objValue = elementValuesPair.value().value();
                if (objValue instanceof AnnotationValue[]) {
                    for (AnnotationValue annotationValue : (AnnotationValue[]) objValue) {
                        annotationArgumentNode.getValue().add(
                                annotationValue.value().toString());
                    }
                } else if (objValue instanceof FieldDoc) {
                    annotationArgumentNode.getValue().add(
                            ((FieldDoc) objValue).name());
                } else if (objValue instanceof ClassDoc) {
                    annotationArgumentNode.getValue().add(
                            ((ClassDoc) objValue).qualifiedTypeName());
                } else {
                    annotationArgumentNode.getValue().add(objValue.toString());
                }
                annotationInstanceNode.getArguments().add(
                        annotationArgumentNode);
            }

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
            enumNode.setExtends(superClassType.qualifiedTypeName());
        }

        Type[] interfaces = classDoc.interfaceTypes();
        if (interfaces != null) {
            for (Type interfaceType : interfaces) {
                enumNode.getImplements().add(interfaceType.qualifiedTypeName());
            }
        }

        FieldDoc[] fields = classDoc.enumConstants();
        if (fields != null) {
            for (FieldDoc field : fields) {
                enumNode.getConstants().add(parseEnumConstant(field));
            }
        }

        AnnotationDesc[] annotationDescs = classDoc.annotations();
        if (annotationDescs != null) {
            for (AnnotationDesc annotationDesc : annotationDescs) {
                enumNode.getAnnotations().add(
                        parseAnnotationDesc(annotationDesc,
                                classDoc.qualifiedName()));
            }
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

        AnnotationDesc[] annotationDescs = fieldDoc.annotations();
        if (annotationDescs != null) {
            for (AnnotationDesc annotationDesc : annotationDescs) {
                enumConstant.getAnnotations().add(
                        parseAnnotationDesc(annotationDesc,
                                fieldDoc.qualifiedName()));
            }
        }
        return enumConstant;
    }

    protected Interface parseInterface(ClassDoc classDoc) {

        Interface interfaceMode = new Interface();
        interfaceMode.setName(classDoc.name());
        interfaceMode.setQualifiedName(classDoc.qualifiedName());
        interfaceMode.setComment(classDoc.commentText());
        interfaceMode.setIsIncluded(classDoc.isIncluded());
        interfaceMode.setScope(parseScope(classDoc));

        TypeVariable[] typeVariables = classDoc.typeParameters();
        if (typeVariables != null) {
            for (TypeVariable typeVariable : typeVariables) {
                interfaceMode.getGenerics().add(
                        parseGenericTypeVariables(typeVariable));
            }
        }

        Type[] interfaces = classDoc.interfaceTypes();
        if (interfaces != null) {
            for (Type interfaceType : interfaces) {
                interfaceMode.getExtends().add(interfaceType.qualifiedTypeName());
            }
        }

        MethodDoc[] methods = classDoc.methods();
        if (methods != null) {
            for (MethodDoc method : methods) {
                interfaceMode.getMethods().add(parseMethod(method));
            }
        }

        AnnotationDesc[] annotationDescs = classDoc.annotations();
        if (annotationDescs != null) {
            for (AnnotationDesc annotationDesc : annotationDescs) {
                interfaceMode.getAnnotations().add(
                        parseAnnotationDesc(annotationDesc,
                                classDoc.qualifiedName()));
            }
        }

        return interfaceMode;
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
       
        Parameter[] parameters = methodDoc.parameters();
        if(parameters != null)        {
            for(Parameter parameter : parameters)          {
                methodNode.getParameters().add(ParseParameter(parameter));
            }

            methodNode.parameters = paramList.toArray(new Param[] {});
        }
        else
        {
            log.debug("No parameters for method: " + methodDoc.name());
        }

        // Parse result data

        Result returnInfo = new Result();

        Tag[] returnTags = methodDoc.tags("@return");
        if(returnTags != null && returnTags.length > 0)
        {
            // there should be only one return tag.  but heck,
            // if they specify two, so what...
            StringBuilder builder = new StringBuilder();
            for(Tag returnTag : returnTags)
            {
                String returnTagText = returnTag.text();
                if(returnTagText != null)
                {
                    builder.append(returnTagText);
                    builder.append("\n");
                }
            }

            returnInfo.comment = builder.substring(0, builder.length() - 1);
        }

        returnInfo.type = ParseType(methodDoc.returnType());
        methodNode.result = returnInfo;

        // Parse exceptions of the method

        Type[] types = methodDoc.thrownExceptionTypes();
        ThrowsTag[] exceptionComments = methodDoc.throwsTags();

        if(types != null && types.length > 0)
        {
            ArrayList<ExceptionInstance> exceptionList = new ArrayList<ExceptionInstance>();

            for(Type exceptionType : types)
            {
                ExceptionInstance exception = new ExceptionInstance();

                exception.type = ParseType(exceptionType);

                for(ThrowsTag exceptionComment : exceptionComments)
                {
                    if(exceptionType == exceptionComment.exceptionType())
                    {
                        exception.comment = exceptionComment.exceptionComment();

                        ClassDoc exceptionDetails = exceptionComment.exception();

                        // not yet parsing Exceptions defined within the supplied code set
                        exception.type = ParseType(exceptionComment.exceptionType());
                        break;
                    }
   
                    }

                exceptionList.add(exception);
            }

            methodNode.exceptions = exceptionList.toArray(new ExceptionInstance[] { });
        }

        // parse annotations from the method
        methodNode.annotationInstances = ParseAnnotationInstances(methodDoc.annotations(), methodDoc.qualifiedName());
       
        return methodNode;
    }

    protected MethodParameter parseMethodParameter(Parameter parameter)    {
        MethodParameter parameterMethodNode = new MethodParameter();
        parameterMethodNode.setName(parameter.name());
        parameterMethodNode.setType(parseMethodParameterType(parameter.type()));
               
        AnnotationDesc[] annotationDescs = parameter.annotations();
        if (annotationDescs != null) {
            for (AnnotationDesc annotationDesc : annotationDescs) {
                parameterMethodNode.getAnnotations().add(
                        parseAnnotationDesc(annotationDesc,
                                parameter.typeName()));
            }
        }

        return parameterMethodNode;
    }

    protected MethodParameterType parseMethodParameterType(Type type) 
    {
        MethodParameterType typeInfo = new MethodParameterType();
        typeInfo.setQualifiedName(type.qualifiedTypeName());
        typeInfo.wildcard = ParseWildCard(type.asWildcardType());
        typeInfo.generics = ParseGenerics(type.asParameterizedType());
        typeInfo.setDimension(type.dimension());
        return typeInfo;
    }

    /**
     * Parse type variables for generics
     * 
     * @param typeVariable
     * @return
     */
    protected GenericTypeVariable parseGenericTypeVariables(
            TypeVariable typeVariable) {
        GenericTypeVariable genericTypeVariable = new GenericTypeVariable();
        genericTypeVariable.setName(typeVariable.typeName());

        Type[] bounds = typeVariable.bounds();
        if (bounds != null) {
            for (Type bound : bounds) {
                genericTypeVariable.getBounds().add(bound.qualifiedTypeName());
            }
        }
        return genericTypeVariable;
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
