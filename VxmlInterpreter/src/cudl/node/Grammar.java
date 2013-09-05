package cudl.node;

import org.w3c.dom.Node;

public class Grammar extends VoiceXmlNode {
	public Grammar(Node node) {
		super(node);
	}
	
	@Override
	public String toString() {
		return getAttribute("src") == null ? getTextContent() : getAttribute("src");
	}
}
