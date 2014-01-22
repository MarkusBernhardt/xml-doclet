A doclet to output javadoc as XML
=================================

This library provides a doclet to output the javadoc comments from Java source code to a XML document.

The name, some ideas and most unit tests were shamelessly stolen from the
[xml-doclet](http://code.google.com/p/xml-doclet) library by Seth Call.

Usage
-----

If you are using maven you can use this library by adding the following report to your pom.xml:

    <project>
    	...
    			<plugin>
    				<groupId>org.apache.maven.plugins</groupId>
    				<artifactId>maven-javadoc-plugin</artifactId>
    				<executions>
    					<execution>
    						<id>xml-doclet</id>
    						<goals>
    							<goal>javadoc</goal>
    						</goals>
    						<configuration>
    							<doclet>com.github.markusbernhardt.xmldoclet.XmlDoclet</doclet>
    							<additionalparam>-d ${project.build.directory} -filename ${project.artifactId}-${project.version}-javadoc.xml</additionalparam>
    							<useStandardDocletOptions>false</useStandardDocletOptions>
    							<docletArtifact>
    								<groupId>com.github.markusbernhardt</groupId>
    								<artifactId>xml-doclet</artifactId>
    								<version>1.0.4</version>
    							</docletArtifact>
    						</configuration>
						</execution>
    				</executions>
    			</plugin>
    	...
    </project>
    
If you are not using maven, you can use the [jar-with-dependencies](http://search.maven.org/remotecontent?filepath=com/github/markusbernhardt/xml-doclet/1.0.4/xml-doclet-1.0.4-jar-with-dependencies.jar), which contains all required libraries.

    javadoc -doclet com.github.markusbernhardt.xmldoclet.XmlDoclet \
    -docletpath xml-doclet-1.0.4-jar-with-dependencies.jar \
    [Javadoc- and XmlDoclet-Options]

If you want more control and feel adventurous you could you use this [jar](http://search.maven.org/remotecontent?filepath=com/github/markusbernhardt/xml-doclet/1.0.4/xml-doclet-1.0.4.jar) and provide all required libraries from this [list](DEPENDENCIES.md) on your own.

Options
-------

    -d <directory>            Destination directory for output file.
                              Default: .
                              
    -docencoding <encoding>   Encoding of the output file.
                              Default: UTF8
                              
    -dryrun                   Parse javadoc, but don't write output file.
                              Default: false
                              
    -filename <filename>      Name of the output file.
                              Default: javadoc.xml

