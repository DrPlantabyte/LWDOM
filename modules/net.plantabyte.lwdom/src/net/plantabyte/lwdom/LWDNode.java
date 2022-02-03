/*
Copyright © 2022 Dr. Christopher C. Hall, aka DrPlantabyte

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the “Software”), to deal in the Software without restriction, including without limitation the
rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit
persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/
package net.plantabyte.lwdom;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The light-weight DOM node class LWDNode is the super class to represent a DOM that can be written to XML format. It
 * is an abstract class. DOM elements use the LWDNode.LWDElement class while text content uses the LWDNode.LWDText class.
 *<p>
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
public abstract class LWDNode {

	/**
	 * Returns an unmodifiable list of all child nodes (both elements and text) of this node. Text nodes have no
	 * children and thus return an empty list.
	 * @return an unmodifiable list of all child nodes
	 */
	public abstract List<LWDNode> getAllChildNodes();

	/**
	 * Returns an unmodifiable list of all element nodes (excludes text nodes) of this node. Text nodes have no
	 * children and thus return an empty list.
	 * @return an unmodifiable list of all child elements
	 */
	public abstract List<LWDElement> getElements();
	/**
	 * Returns an unmodifiable list of all text nodes (excludes element nodes) of this node. Text nodes have no
	 * children and thus return an empty list.
	 * @return an unmodifiable list of all child text nodes
	 */
	public abstract List<LWDText> getTexts();

	/**
	 * Converts the node and all child nodes (ie the DOM with this node as the root node) into XML text
	 * @param indent How far indented the root node (this node) is
	 * @param indentStr The indent prefix to use (can be null to disable indentation)
	 * @return An XML string representation of the DOM
	 */
	public abstract String toString(int indent, String indentStr);

	/**
	 * XML prefix
	 */
	private static final String xmlHeader = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";

	/**
	 * Creates a new LWDElement with the given element name, useful for structuring your code in a builder pattern.
	 * @param name The tagname of the element
	 * @return a new LWDElement instance
	 * @throws IllegalArgumentException thrown if <code>name</code> is not a valid XML name
	 */
	public static LWDElement newElement(String name) throws IllegalArgumentException {
		return new LWDElement(name);
	}

	/**
	 * Light-weight DOM element class for representing a DOM element that may have child nodes (which may be other
	 * elements or may be text)
	 */
	public static class LWDElement extends LWDNode {
		// tags
		private final String name;
		private final LinkedList<LWDNode> children;
		private final Map<String,String> attributes;

		/**
		 * Creates a new LWDElement with the given element name
		 * @param name The tagname of the element
		 * @throws IllegalArgumentException thrown if <code>name</code> is not a valid XML name
		 */
		public LWDElement(String name) throws IllegalArgumentException {
			if(!isValidIdentifier(name)) throw new IllegalArgumentException(String.format("%s is not a valid element name", name));
			this.name = name;
			this.children = new LinkedList<>();
			this.attributes = new HashMap<>();
		}

		/**
		 * Gets the name of this element
		 * @return a String
		 */
		public String getName(){ return name;}

		/**
		 * Gets all attributes of the element as an unmodifiable map
		 * @return an attribute key-value mapping
		 */
		public Map<String, String> getAttributes(){
			return Collections.unmodifiableMap(this.attributes);
		}

		/**
		 * Appends a node to this element's list of child nodes. Care must be taken to not create circular references.
		 * @param n A node
		 * @return Returns this object to support the builder pattern of usage
		 */
		public LWDElement appendChild(LWDNode n){
			this.children.add(n);
			return this;
		}

		/**
		 * Removes a node from the list of child nodes.
		 * @param n the node to remove
		 * @return Returns this object to support the builder pattern of usage
		 */
		public LWDElement removeChild(LWDNode n){
			this.children.remove(n);
			return this;
		}

		/**
		 * Removes the first child node
		 * @return Returns this object to support the builder pattern of usage
		 */
		public LWDElement removeFirstChild(){
			this.children.removeFirst();
			return this;
		}

		/**
		 * Removes the last child node
		 * @return Returns this object to support the builder pattern of usage
		 */
		public LWDElement removeLastChild(){
			this.children.removeLast();
			return this;
		}

		/**
		 * Creates and appends a new text node to the list of child nodes for this element
		 * @param text The text to add
		 * @return Returns this object to support the builder pattern of usage
		 */
		public LWDElement appendText(String text){
			this.children.add(new LWDText(text));
			return this;
		}

		/**
		 * Gets the first child node from the list of children (if one exists)
		 * @return An optional that is empty if there are no child nodes
		 */
		public Optional<LWDNode> getFirstChild(){
			if(children.isEmpty()) return Optional.empty();
			return Optional.of(children.getFirst());
		}

		/**
		 * Gets the last child node from the list of children (if one exists)
		 * @return An optional that is empty if there are no child nodes
		 */
		public Optional<LWDNode> getLastChild(){
			if(children.isEmpty()) return Optional.empty();
			return Optional.of(children.getLast());
		}

		/**
		 * Replaces one node with another
		 * @param target The node to remove
		 * @param newNode The node to put in the place of the old node
		 * @return Returns this object to support the builder pattern of usage
		 */
		public LWDElement replaceChild(LWDNode target, LWDNode newNode){
			int i = this.children.indexOf(target);
			if(i >= 0) this.children.set(i, newNode);
			return this;
		}

		/**
		 * Inserts a child node at the given index in the list of child nodes
		 * @param index the index to insert at
		 * @param n the node to insert
		 * @return Returns this object to support the builder pattern of usage
		 */
		public LWDElement insertChild(int index, LWDNode n){
			this.children.add(index, n);
			return this;
		}

		/**
		 * Finds the index of a child node, if it exists in the list of childe nodes (returning an empty optional if the
		 * node is not found)
		 * @param n the node to find
		 * @return An optional holding the index of the node
		 */
		public Optional<Integer> indexOfChild(LWDNode n){
			int i = children.indexOf(n);
			if(i < 0) return Optional.empty();
			return Optional.of(i);
		}

		/**
		 * Gets a child node by index
		 * @param index Index of node to fetch
		 * @return An optional that is empty if the index is out of bounds
		 */
		public Optional<LWDNode> getChild(int index){
			if(children.isEmpty() || index >= children.size() || index < 0) return Optional.empty();
			return Optional.of(children.get(index));
		}

		/**
		 * Gets the number of child nodes
		 * @return size of child node list
		 */
		public int childCount(){
			return this.children.size();
		}

		/**
		 * Recursively searches the children of this element and returns a list of all descendant elements with the
		 * given name.
		 * @param elementName element name to search for
		 * @return A list of all descendant elements with given name
		 */
		public List<LWDElement> searchElementsByName(String elementName){
			var out = new LinkedList<LWDElement>();
			for(var e : this.getElements()){
				if(e.getName().equals(elementName)) {
					out.add(e);
				}
				out.addAll(e.searchElementsByName(elementName));
			}
			return Collections.unmodifiableList(new ArrayList<LWDElement>(out));
		}

		/**
		 * Sets an attribute to a value
		 * @param key The attribute name to set
		 * @param value The value to set the attribute to
		 * @return Returns this object to support the builder pattern of usage
		 * @throws IllegalArgumentException thrown if <code>key</code> is not a valid attribute name
		 */
		public LWDElement setAttribute(String key, String value) throws IllegalArgumentException {
			if(!isValidIdentifier(key)) throw new IllegalArgumentException(String.format("%s is not a valid attribute name", key));
			attributes.put(key, value);
			return this;
		}

		/**
		 * Removes an attribute from this element
		 * @param key attribute to remove
		 * @return Returns this object to support the builder pattern of usage
		 */
		public LWDElement removeAttribute(String key){
			attributes.remove(key);
			return this;
		}

		/**
		 * Returns true if the given attribute has been set for this element, false otherwise
		 * @param key attribute name to check for
		 * @return true if the attribute has been set for this element, false otherwise
		 */
		public boolean hasAttribute(String key){
			return attributes.containsKey(key);
		}

		/**
		 * Writes this element's DOM as XML to the given destination stream. The destination stream is not closed.
		 * @param out The destination stream
		 * @throws IOException Thrown if unable to write to the stream
		 */
		public void writeTo(PrintStream out) throws IOException {
			out.print(xmlHeader);
			out.print(this.toString());
			// does not close the stream (intended behavior)
		}
		/**
		 * Writes this element's DOM as XML to the given destination stream. The destination stream is not closed.
		 * @param out The destination stream
		 * @throws IOException Thrown if unable to write to the stream
		 */
		public void writeTo(OutputStream out) throws IOException {
			var w = new OutputStreamWriter(out, StandardCharsets.UTF_8);
			w.write(xmlHeader);
			w.write(this.toString());
			// does not close the stream (intended behavior)
		}
		/**
		 * Writes this element's DOM as XML to the given destination file, overwriting the previous contents of that file.
		 * @param filepath The destination file
		 * @throws IOException Thrown if unable to write to the stream
		 */
		public void writeTo(Path filepath) throws IOException{
			try(var fout = Files.newOutputStream(filepath)){
				this.writeTo(fout);
			}
		}

		/**
		 * Writes this element's DOM as XML to a string, and then returns the string.
		 * @return a full XML document as a string
		 */
		public String writeToString(){
			return xmlHeader.concat(this.toString());
		}

		/**
		 * Converts the DOM to XML, but does not include the XML header that XML files should start with
		 * @return an XML string
		 */
		@Override public String toString(){
			return this.toString(0, " ");
		}

		/**
		 * Converts the node and all child nodes (ie the DOM with this node as the root node) into XML text
		 * @param indent How far indented the root node (this node) is
		 * @param indentStr The indent prefix to use (can be null to disable indentation)
		 * @return An XML string representation of the DOM
		 */
		@Override public String toString(int indent, String indentStr){
			var sb = new StringBuilder();
			var attributeStr = new StringBuilder();
			var prefix = new StringBuilder();
			var terminator = "\n";
			if(indentStr != null) {
				for (int i = 0; i < indent; i++) prefix.append(indentStr);
			} else {
				terminator = "";
			}
			sb.append(prefix);
			for(var e : this.attributes.entrySet()){
				attributeStr.append(' ')
						.append(String.valueOf(e.getKey()))
						.append("=\"")
						.append(LWDNode.escapeText(e.getValue()))
						.append("\"");
			}
			if(children.size() == 0) {
				// no children
				sb.append(String.format("<%s%s />", this.name, attributeStr));
			} else {
				sb.append(String.format("<%s%s>%s", this.name, attributeStr, terminator));
				for(var c : children) sb.append(c.toString(indent+1, indentStr));
				sb.append(prefix);
				sb.append(String.format("</%s>", this.name));
			}
			sb.append(terminator);
			return sb.toString();
		}

		/**
		 * Returns an unmodifiable list of all child nodes (both elements and text) of this node.
		 * @return an unmodifiable list of all child nodes
		 */
		@Override
		public List<LWDNode> getAllChildNodes() {
			return Collections.unmodifiableList(children);
		}

		/**
		 * Returns an unmodifiable list of all element nodes (excludes text nodes) of this node.
		 * @return an unmodifiable list of all child elements
		 */
		@Override
		public List<LWDElement> getElements() {
			return Collections.unmodifiableList(
					children.stream()
							.filter((var n) -> n instanceof LWDElement)
							.map((var n) -> (LWDElement)n)
							.collect(Collectors.toList())
			);
		}

		/**
		 * Returns an unmodifiable list of all text nodes (excludes element nodes) of this node.
		 * @return an unmodifiable list of all child text nodes
		 */
		@Override
		public List<LWDText> getTexts() {
			return Collections.unmodifiableList(
					children.stream()
							.filter((var n) -> n instanceof LWDText)
							.map((var n) -> (LWDText)n)
							.collect(Collectors.toList())
			);
		}
	}

	/**
	 * This class is used to represent text in the XML DOM. For example, &lt;first-name&gt;Pat&lt;/first-name&gt; would be
	 * represented as a LWDElement node with name <code>first-name</code> with one child node of class LWDText whose
	 * content is <code>Pat</code>.
	 */
	public static class LWDText extends LWDNode {
		// text content
		private final String content;

		/**
		 * Constructor for text nodes
		 * @param text the text content of this text node
		 */
		public LWDText(String text) {
			this.content = text;
		}

		/**
		 * Converts the content of this text node to escaped XML text
		 * @return A string
		 */
		@Override public String toString(){
			return this.toString(0, " ");
		}

		/**
		 * Converts the content of this text node to escaped XML text, with the given indentation
		 * @param indent How far indented this node is
		 * @param indentStr The indent prefix to use (can be null to disable indentation)
		 * @return A string
		 */
		@Override public String toString(int indent, String indentStr) {
			var sb = new StringBuilder();
			var attributeStr = new StringBuilder();
			var prefix = new StringBuilder();
			var terminator = "\n";
			if (indentStr != null) {
				for (int i = 0; i < indent; i++) prefix.append(indentStr);
			} else {
				terminator = "";
			}
			sb.append(prefix)
					.append(LWDNode.escapeText(content))
					.append(terminator);
			return sb.toString();
		}

		/**
		 * Text nodes have no children
		 * @return an empty list
		 */
		@Override
		public List<LWDNode> getAllChildNodes() {
			return Collections.emptyList();
		}
		/**
		 * Text nodes have no children
		 * @return an empty list
		 */
		@Override
		public List<LWDElement> getElements() {
			return Collections.emptyList();
		}
		/**
		 * Text nodes have no children
		 * @return an empty list
		 */
		@Override
		public List<LWDText> getTexts() {
			return Collections.emptyList();
		}
	}

	/**
	 * Performs standard XML escapes on the following charcters: &amp;, &quot;, &apos;, &lt;, and &gt;
	 * @param input A string
	 * @return An escaped string that an XML parser would convert back to the original string
	 */
	public static String escapeText(String input){
		return String.valueOf(input)
				.replace("&", "&amp;")
				.replace("\"", "&quot;")
				.replace("'", "&apos;")
				.replace("<", "&lt;")
				.replace(">", "&gt;");
	}

	/**
	 * Checks a given ID name if it is valid to use in XML as an element name or an attribute
	 * @param name an identifier name
	 * @return true if the name is valid, false otherwise
	 */
	public static boolean isValidIdentifier(String name){
		if(name == null) return false;
		char first = name.charAt(0);
		if(!(Character.isLetter(first) || first == '_' || first == ':')) return false;
		int numCodepoints = name.codePointCount(0, name.length());
		int i = 0;
		while(i < name.length()){
			char c = name.charAt(i);
			int codepoint = name.codePointAt(i);
			if(!(c == '.' || c == '-' || c == '_' || c == ':'
					|| Character.isLetterOrDigit(codepoint)
					|| Character.isAlphabetic(codepoint)
					|| Character.isIdeographic(codepoint)
			)) return false;
			i += Character.charCount(codepoint);
		}
		return true;
	}
	
}

