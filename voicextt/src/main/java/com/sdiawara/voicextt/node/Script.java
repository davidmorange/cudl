package com.sdiawara.voicextt.node;

import org.w3c.dom.Node;

public class Script extends VoiceXmlNode {

	public Script(Node node) {
		super(node);
	}

	public String getTextContent() {
		return node.getTextContent();
	}
}
