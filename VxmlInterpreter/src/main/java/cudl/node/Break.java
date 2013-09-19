package cudl.node;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

public class Break extends VoiceXmlNode {
	private static final String ATT_EXPR = "expr";
	private static final String ATT_FETCH_HINT = "fetchhint";
	private static final String ATT_FETCH_TIMEOUT = "fetchtimeout";
	private static final String ATT_MAXAGE = "maxage";
	private static final String ATT_MAXSTALE = "maxstale";
	private static final String ATT_SRC = "src";
	private boolean speakable;
	private static final List<String> CHILDS;

	static {
		CHILDS = new ArrayList<String>();
		CHILDS.add("audio");
		CHILDS.add("value");
		CHILDS.add("break");
		CHILDS.add("emphasis");
		CHILDS.add("enumerate");
		CHILDS.add("foreach");
		CHILDS.add("mark");
		CHILDS.add("phoneme");
		CHILDS.add("prosody");
		CHILDS.add("say-as");
	}

	public Break(Node node) {
		super(node);
	}

	public String getExpr() {
		return getAttribute(ATT_EXPR);
	}

	public String getFetchHint() {
		return getAttribute(ATT_FETCH_HINT);
	}

	public String getFetchTimeout() {
		return getAttribute(ATT_FETCH_TIMEOUT);
	}

	public String getMaxage() {
		return getAttribute(ATT_MAXAGE);
	}

	public String getMaxstale() {
		return getAttribute(ATT_MAXSTALE);
	}

	public String getSrc() {
		return getAttribute(ATT_SRC);
	}

	public boolean isSpeakable() {
		return speakable;
	}

	public void setSpeakable(boolean speakable) {
		this.speakable = speakable;
	}

	public String getTextContent() {
		return node.getTextContent();
	}
}
