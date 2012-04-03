package cudl.node;

import org.w3c.dom.Node;

public class Noinput extends VoiceXmlNode {

	public Noinput(Node node) {
		super(node);
	}

	public String getTextContent() {
		return node.getTextContent();
	}
}
