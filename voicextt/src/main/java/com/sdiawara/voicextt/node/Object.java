package com.sdiawara.voicextt.node;

import org.w3c.dom.Node;

import com.sdiawara.voicextt.FormItem;
import com.sdiawara.voicextt.FormItemVisitor;
import com.sdiawara.voicextt.exception.VoiceXTTException;

public class Object extends FormItem {
	public Object(Node node) {
		super(node);
	}

	@Override
	public void accept(FormItemVisitor formInterpretationAlgorithm) throws VoiceXTTException {
		formInterpretationAlgorithm.visit(this);
	}
}
