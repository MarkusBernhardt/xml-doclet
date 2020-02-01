package com.github.markusbernhardt.xmldoclet;

import com.github.markusbernhardt.xmldoclet.xjc.Annotation;
import com.github.markusbernhardt.xmldoclet.xjc.Class;
import com.github.markusbernhardt.xmldoclet.xjc.Enum;
import com.github.markusbernhardt.xmldoclet.xjc.Interface;
import com.github.markusbernhardt.xmldoclet.xjc.Package;
import com.github.markusbernhardt.xmldoclet.xjc.Root;
import java.io.BufferedOutputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;
import javax.lang.model.SourceVersion;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;


public class NewXmlDoclet implements Doclet {

    /**
     * For tests.
     */
    public static Root root;

    private String directory;
    private String encoding;
    private boolean dryrun;
    private String filename;

    @Override
    public void init(Locale locale, Reporter reporter) {
    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public Set<? extends Option> getSupportedOptions() {
        return new LinkedHashSet<>(Arrays.asList(
                new SingleArgumentOption(
                        Arrays.asList("-d"),
                        "directory",
                        "Destination directory for output file.\nDefault: .",
                        argument -> this.directory = argument
                ),
                new SingleArgumentOption(
                        Arrays.asList("-docencoding"),
                        "encoding",
                        "Encoding of the output file.\nDefault: UTF8",
                        argument -> this.encoding = argument
                ),
                new FlagOption(
                        Arrays.asList("-dryrun"),
                        "Parse javadoc, but don't write output file.\nDefault: false",
                        () -> this.dryrun = true
                ),
                new SingleArgumentOption(
                        Arrays.asList("-filename"),
                        "filename",
                        "Name of the output file.\nDefault: javadoc.xml",
                        argument -> this.filename = argument
                )
        ));
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }

    @Override
    public boolean run(DocletEnvironment environment) {
        final Root xmlRoot = new JavadocTransformer(environment).transform();
        root = xmlRoot;
        save(xmlRoot);
        return true;
    }

    private void save(Root xmlRoot) {
        ClassLoader originalClassLoader = null;
        try {
            originalClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(this.getClass().getClassLoader());
            if (dryrun) {
                return;
            }
            sort(xmlRoot);
            final JAXBContext context = JAXBContext.newInstance(Root.class);
            final CharArrayWriter writer = new CharArrayWriter();
            final Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            if (encoding != null) {
                marshaller.setProperty(Marshaller.JAXB_ENCODING, encoding);
            }
            final String name = (directory != null ? directory + File.separator : "") + (filename != null ? filename : "javadoc.xml");
            try (OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(name))) {
                marshaller.marshal(xmlRoot, outputStream);
            }
            marshaller.marshal(xmlRoot,  writer);
        } catch (JAXBException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (originalClassLoader != null) {
                Thread.currentThread().setContextClassLoader(originalClassLoader);
            }
        }
    }

    public static void sort(Root xmlRoot) {
        xmlRoot.getPackage().sort(Comparator.comparing(Package::getName));
        for (Package pkg : xmlRoot.getPackage()) {
            pkg.getAnnotation().sort(Comparator.comparing(Annotation::getQualified));
            pkg.getEnum().sort(Comparator.comparing(Enum::getQualified));
            pkg.getInterface().sort(Comparator.comparing(Interface::getQualified));
            pkg.getClazz().sort(Comparator.comparing(Class::getQualified));
        }
    }

    private static class FlagOption extends StandardOption {
        public FlagOption(List<String> names, String description, Runnable processor) {
            super(names, null, description, 0, arguments -> processor.run());
        }
    }

    private static class SingleArgumentOption extends StandardOption {
        public SingleArgumentOption(List<String> names, String parameter, String description, Consumer<String> processor) {
            super(names, parameter, description, 1, arguments -> processor.accept(arguments.get(0)));
        }
    }

    private static class StandardOption implements Option {

        private final List<String> names;
        private final String parameters;
        private final String description;
        private final int argumentCount;
        private final Consumer<List<String>> processor;

        public StandardOption(List<String> names, String parameters, String description, int argumentCount, Consumer<List<String>> processor) {
            this.names = names;
            this.parameters = parameters;
            this.description = description;
            this.argumentCount = argumentCount;
            this.processor = processor;
        }

        @Override
        public int getArgumentCount() {
            return argumentCount;
        }

        @Override
        public String getDescription() {
            return description;
        }

        @Override
        public Kind getKind() {
            return Kind.STANDARD;
        }

        @Override
        public List<String> getNames() {
            return names;
        }

        @Override
        public String getParameters() {
            return parameters;
        }

        @Override
        public boolean process(String option, List<String> arguments) {
            processor.accept(arguments);
            return true;
        }
    }

}
