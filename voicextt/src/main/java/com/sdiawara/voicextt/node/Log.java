package com.sdiawara.voicextt.node;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

public class Log extends VoiceXmlNode {
	private static final String ATT_LABEL = "label";
	private static final String ATT_EXPR = "expr";
	private static final List<String> CHILDS;

	static {
		CHILDS = new ArrayList<String>();
		CHILDS.add("value");
		CHILDS.add("#text");
 	}


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
	
	@Override
	public boolean canContainsChild(VoiceXmlNode child) {
		return CHILDS.contains(child.getNodeName());
	}
}
