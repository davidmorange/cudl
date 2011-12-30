package com.sdiawara.voicextt.node;

import org.w3c.dom.Node;

public class Log extends VoiceXmlNode {
	private static final String ATT_LABEL = "label";
	private static final String ATT_EXPR = "expr";
	
	public Log(Node node) {
		super(node);
	}

	public String getTextContent() {
		return this.node.getTextContent();
	}

	public String getLabel() {
		return getAttribute(ATT_LABEL);
	}

	public String getExpr() {
		return getAttribute(ATT_EXPR);
	}
}
