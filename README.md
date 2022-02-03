# LWDOM

A single-file, light-weight DOM library for Java.

# How to use

Either include the JPMS modular .jar file as a dependency or add the file **LWDNode.java** in your project.

Then you can create a DOM and write it to an XML file with a builder-pattern syntax:

```java
import java.io.IOException;
import java.nio.file.Paths;
import net.plantabyte.lwdom.LWDNode;
try {
	LWDNode.newElement("root")
			.appendChild(
					LWDNode.newElement("group")
							.setAttribute("id", "g1")
							.appendText("Say hello to group 1!")
			)
			.appendChild(
					LWDNode.newElement("group")
							.setAttribute("id", "g2")
							.setAttribute("color", "green")
							.appendText("Hi! I'm in group 2")
							.appendChild(LWDNode.newElement("something"))
			)
			.writeTo(Paths.get("output.xml"));
} catch (IOException ioe) {
	System.err.println("Error: Unable to write to file!");
}
```

The above code will produce the following XML:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<root>
 <group id="g1">
  Say hello to group 1!
 </group>
 <group color="green" id="g2">
  Hi! I&apos;m in group 2
  <something />
 </group>
</root>
```

# How to compile

The javac and jar args for compiling this project are included. Just run the following in the terminal to build this library as a JPMS modular .jar file:
```bash
cd <project directory>
rm build/* -rf
javac @javac-args.txt
jar @jar-args.txt
javadoc @javadoc-args.txt
```
 
