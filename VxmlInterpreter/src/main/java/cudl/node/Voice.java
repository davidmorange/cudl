package cudl.node;

import org.w3c.dom.Node;

public class Voice extends VoiceXmlNode {
	public Voice(Node node) {
		super(node);
	}

	public String getTextContent() {
		return node.getTextContent();
	}
}
