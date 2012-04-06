package cudl.node;

import org.w3c.dom.Node;

public class Choice extends VoiceXmlNode {
	public Choice(Node node) {
		super(node);
	}
	
	public String getTextContent() {
		return node.getTextContent();
	}
}
