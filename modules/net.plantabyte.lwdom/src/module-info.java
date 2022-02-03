/**
 * The net.plantabyte.lwdom module is a light-weight XML DOM library for java. It is simple to use and only supports
 * writing to XML (no parsing).
 *
 * <p>
 * Example usage:<p>
 <code>import net.plantabyte.lwdom.LWDNode;<br>
 String xmlString = LWDNode.newElement("root")<br>
 &nbsp;&nbsp;&nbsp;&nbsp;.appendChild(<br>
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;LWDNode.newElement("group")<br>
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.setAttribute("id", "g1")<br>
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.appendText("Say hello to group 1!")<br>
 &nbsp;&nbsp;&nbsp;&nbsp;)<br>
 &nbsp;&nbsp;&nbsp;&nbsp;.appendChild(<br>
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;LWDNode.newElement("group")<br>
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.setAttribute("id", "g2")<br>
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.setAttribute("color", "green")<br>
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.appendText("Hi! I'm in group 2")<br>
 &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;.appendChild(LWDNode.newElement("something"))<br>
 &nbsp;&nbsp;&nbsp;&nbsp;).writeToString();<br>
 System.out.print(xmlString);</code>
 */
module net.plantabyte.lwdom {
	exports net.plantabyte.lwdom;
	requires java.base;
}
