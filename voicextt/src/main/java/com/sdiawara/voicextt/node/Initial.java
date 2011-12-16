package com.sdiawara.voicextt.node;

import org.w3c.dom.Node;

import com.sdiawara.voicextt.FormItemVisitor;
import com.sdiawara.voicextt.InputFormItem;
import com.sdiawara.voicextt.exception.VoiceXTTException;

public class Initial extends InputFormItem {
	public Initial(Node node) {
		super(node);
	}

	@Override
	public void accept(FormItemVisitor formInterpretationAlgorithm) throws VoiceXTTException {
		formInterpretationAlgorithm.visit(this);
	}
}
