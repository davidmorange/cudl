package cudl.node;

import org.w3c.dom.Node;

public class Value extends VoiceXmlNode {
	private static final String ATT_EXPR = "expr";
	private boolean speakable;
	public Value(Node node) {
		super(node);
	}

	public String getExpr() {
		return getAttribute(ATT_EXPR);
	}

	public boolean isSpeakable() {
		return speakable;
	}

	public void setSpeakable(boolean speakable) {
		this.speakable = speakable;
	}
}
