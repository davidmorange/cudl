package cudl.node;

import org.w3c.dom.Node;

public class Nomatch extends VoiceXmlNode {

	public Nomatch(Node node) {
		super(node);
	}

	public String getTextContent() {
		return node.getTextContent();
	}
}
