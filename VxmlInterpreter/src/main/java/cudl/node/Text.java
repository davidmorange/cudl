package cudl.node;

import org.w3c.dom.Node;

public class Text extends VoiceXmlNode {
	private boolean speakable;

	public Text(Node node) {
		super(node);
	}

	public boolean isSpeakable() {
		return speakable;
	}

	public void setSpeakable(boolean speakable) {
		this.speakable = speakable;
	}

	public String getValue() {
		return node.getNodeValue();
	}
}
