package com.sdiawara.voicextt.node;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;


public class Form extends VoiceXmlNode {
	private static final String ATT_ID = "id";
	private static final String ATT_SCOPE = "scope";

	private static final List<String> CHILDS;

	static {
		CHILDS = new ArrayList<String>();
		CHILDS.add("block");
		CHILDS.add("catch");
		CHILDS.add("error");
		CHILDS.add("field");
		CHILDS.add("filled");
		CHILDS.add("grammar");
		CHILDS.add("help");
		CHILDS.add("initial");
		CHILDS.add("link");
		CHILDS.add("noinput");
		CHILDS.add("nomatch");
		CHILDS.add("object");
		CHILDS.add("property");
		CHILDS.add("record");
		CHILDS.add("script");
		CHILDS.add("subdialog");
		CHILDS.add("transfer");
		CHILDS.add("var");
	}

	public Form(Node node) {
		super(node);
	}

	public String getId() {
		return getAttribute(ATT_ID);
	}

	public String getScope() {
		return getAttribute(ATT_SCOPE);
	}

	@Override
	public boolean canContainsChild(VoiceXmlNode child) {
		return CHILDS.contains(child.getNodeName());
	}
}
