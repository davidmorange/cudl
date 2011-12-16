package com.sdiawara.voicextt.node;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

public class Filled extends VoiceXmlNode {
	private static final List<String> CHILDS;
	private static final String ATT_NAMELIST = "namelist";
	private static final String Att_MODE = "mode";
	static {
		CHILDS = new ArrayList<String>();
		CHILDS.add("assign");
		CHILDS.add("audio");
		CHILDS.add("clear");
		CHILDS.add("disconnect");
		CHILDS.add("enumerate");
		CHILDS.add("exit");
		CHILDS.add("goto");
		CHILDS.add("if");
		CHILDS.add("log");
		CHILDS.add("prompt");
		CHILDS.add("reprompt");
		CHILDS.add("return");
		CHILDS.add("script");
		CHILDS.add("submit");
		CHILDS.add("throw");
		CHILDS.add("value");
		CHILDS.add("var");
	}

	public Filled(Node node) {
		super(node);
	}

	@Override
	public boolean canContainsChild(VoiceXmlNode child) {
		return CHILDS.contains(child.getNodeName());
	}
}
