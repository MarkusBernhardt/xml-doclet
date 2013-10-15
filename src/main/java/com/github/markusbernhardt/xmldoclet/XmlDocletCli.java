package com.github.markusbernhardt.xmldoclet;

import com.lexicalscope.jewel.cli.Option;

public interface XmlDocletCli {
    @Option(shortName = "d", defaultValue = ".", description = "Specifies the destination directory where the doclet saves the generated XML file. Omitting this option causes the files to be saved to the current directory. The value directory can be absolute, or relative to the current working directory. The destination directory is automatically created when the doclet runs. ")
    String getDestination();
}
