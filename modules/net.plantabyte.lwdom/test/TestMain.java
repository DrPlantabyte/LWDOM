import net.plantabyte.lwdom.LWDNode;

import java.io.IOException;
import java.nio.file.Paths;

public class TestMain {
	public static void main(String[] args){
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
			.writeTo(System.out);
} catch (IOException ioe) {
	System.err.println("Error: Unable to write to output stream!");
}
	}
}
