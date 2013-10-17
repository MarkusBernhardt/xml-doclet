A doclet to output javadoc as XML
=================================

This library provides a doclet to output the javadoc comments from Java source code to a XML document.

The name, some ideas and most unit tests were shamelessly stolen from the
[xml-doclet](http://code.google.com/p/xml-doclet) library by Seth Call.

Usage
-----

If you are using maven you can use this library by adding the following report to your pom.xml:

    <dependency>
        <groupId>com.github.markusbernhardt</groupId>
        <artifactId>robotframework-selenium2library-java</artifactId>
        <version>1.2.0.13</version>
        <scope>test</scope>
    </dependency>

If you cannot use the maven you can use the [jar-with-dependencies](http://search.maven.org/remotecontent?filepath=com/github/markusbernhardt/xml-doclet/1.0.0/xml-doclet-1.0.0-jar-with-dependencies.jar), which contains all required libraries.

If you want more control and feel adventurous you could you use this [jar](http://search.maven.org/remotecontent?filepath=com/github/markusbernhardt/xml-doclet/1.0.0/xml-doclet-1.0.0.jar) and provide all required libraries from this [list](DEPENDENCIES.md) on your own.

Parameter
---------

    -d <directory>            Destination directory for output file.
                              Default: .
    -docencoding <encoding>   Encoding of the output file.
                              Default: UTF8
    -dryrun                   Parse javadoc, but don't write output file.
                              Default: false
    -filename <filename>      Name of the output file.
                              Default: javadoc.xml

