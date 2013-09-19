package cudl.node;

import org.w3c.dom.Node;

public class Cdatasection extends VoiceXmlNode {

	public Cdatasection(Node node) {
		super(node);
	}

	public String getTextContent() {
		return node.getTextContent();
	}
}
