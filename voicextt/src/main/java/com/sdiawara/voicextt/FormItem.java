package com.sdiawara.voicextt;

import org.w3c.dom.Node;

import com.sdiawara.voicextt.exception.InterpreterException;
import com.sdiawara.voicextt.node.VoiceXmlNode;

public abstract class FormItem extends VoiceXmlNode {
	protected int idFormItem;
	protected static int size = 1;

	public FormItem(Node node) {
		super(node);
		this.idFormItem = size++;
	}

	public abstract void accept(FormItemVisitor formInterpretationAlgorithm) throws InterpreterException;

	public String getName() {
		String name = getAttribute("name");
		return name == null ? getNodeName() + "$" + idFormItem : name;
	}
}
