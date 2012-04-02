package com.sdiawara.voicextt.node;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import com.sdiawara.voicextt.FormItem;
import com.sdiawara.voicextt.FormItemVisitor;
import com.sdiawara.voicextt.exception.InterpreterException;

public class Subdialog extends FormItem {
	private static final List<String> CHILDS;
	
	static {
		CHILDS = new ArrayList<String>();
		CHILDS.add("audio");
		CHILDS.add("catch");
		CHILDS.add("error");
		CHILDS.add("filled");
		CHILDS.add("help");
		CHILDS.add("noinput");
		CHILDS.add("nomatch");
		CHILDS.add("param");
		CHILDS.add("property");
	}

	public Subdialog(Node node) {
		super(node);
	}

	@Override
	public boolean canContainsChild(VoiceXmlNode child) {
		return CHILDS.contains(child.getNodeName());
	}
	
	@Override
	public void accept(FormItemVisitor formInterpretationAlgorithm) throws InterpreterException {
		formInterpretationAlgorithm.visit(this);
	}
}
