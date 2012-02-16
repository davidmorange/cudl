package com.sdiawara.voicextt;

import org.w3c.dom.Node;

import com.sdiawara.voicextt.exception.VoiceXTTException;
import com.sdiawara.voicextt.node.VoiceXmlNode;

public abstract class FormItem extends VoiceXmlNode {

	public FormItem(Node node) {
		super(node);
	}

	public abstract void accept(FormItemVisitor formInterpretationAlgorithm) throws VoiceXTTException;

	public String getId() {
		return getAttribute("id");
	}
}
