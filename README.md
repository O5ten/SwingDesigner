SwingDesigner
===================

A simple tool to test out new groovy SwingBuilder layouts with MigLayout and see the updates happen right away when code is modified.
Useful for testing out experimental components or new layouts since Java or Groovy classes may be entered and built directly in the editor. 

###Resources
- [SwingBuilder Documentation](http://groovy.codehaus.org/Swing+Builder)
- [MigLayout Documentation](http://www.miglayout.com/)
- [GroovyFX Documentation](http://groovyfx.org/docs/index.html)
- MigPane

###0.8
- Added support for GroovyFX, and added some nice examples to show off its features.

###Requirements
This app requires Java8, because reasons.
This app also requires Maven3 to build.

###Build
mvn clean install

###Run
java -jar target/GroovyInterfaceDesigner.jar

or use the shellscripts

####*NIX:
gid.sh

####Windows
gis.bat