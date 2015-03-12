# Introduction #
SilverTrout is a IRC bot written in Java published under GNU General Public
License.

This page will show how to install and run the bot and its plugin using any operating system able to compile and run Java.


# Build from source #

SilverTrout and its plugins are written in Java 6.0 and you will need the Java Development Kit (JDK) to compile the source code. The latest version of JDK may be found at Sun Microsystem’s web page:
http://java.sun.com/javase/downloads/index.jsp

To use the build system you need to have ANT intalled. The latest version of ANT may be found at the Apache Ant's  web page:
http://ant.apache.org/bindownload.cgi

When you have installed JDK and ensured that your PATH variable is correctly configured run the build script using ant:
```
  ant build
```

# Start Silvertrout #
Starting SilverTrout works the same way in every system with java capabilities. Simply run the following command in the base directory:
```
  ant run
```
The first time SilverTrout is executed it will create a template configuration
file called config.xml and by changing this file you may customize SilverTrout
to work as you want it to.

The xml configuration file should only contain xml elements and attributes. By
looking in the template file you’ll get the grip on how the file should be
formatted.