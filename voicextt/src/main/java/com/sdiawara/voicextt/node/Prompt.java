package com.sdiawara.voicextt.node;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

public class Prompt extends VoiceXmlNode {
	private static final String ATT_TIMEOUT = "timeout";
	private static final String ATT_BARGEIN = "bargein";
	private static final String ATT_BARGEINTYPE = "bargeintype";
	private static final String ATT_COND = "cond";
	private static final String ATT_COUNT = "count";
	private static final String ATT_XML_LANG = "xml:lang";
	private static final List<String> CHILDS;

	static {
		CHILDS = new ArrayList<String>();
		CHILDS.add("audio");
		CHILDS.add("value");
		CHILDS.add("#text");
		// CHILDS.add("break");
		// CHILDS.add("emphasis");
		// CHILDS.add("enumerate");
		// CHILDS.add("mark");
		// CHILDS.add("paragraph");
		// CHILDS.add("phoneme");
		// CHILDS.add("prosody");
		// CHILDS.add("say-as");
		// CHILDS.add("sentence");
	}

	public Prompt(Node node) {
		super(node);
	}

	public String getBargein() {
		return getAttribute(ATT_BARGEIN);
	}

	public String getBargeinType() {
		return getAttribute(ATT_BARGEINTYPE);
	}

	public String getCond() {
		return getAttribute(ATT_COND);
	}

	public String getTimeout() {
		return getAttribute(ATT_TIMEOUT);
	}

	public String getXmlLang() {
		return getAttribute(ATT_XML_LANG);
	}

	@Override
	public boolean canContainsChild(VoiceXmlNode child) {
		return CHILDS.contains(child.getNodeName());
	}

	public String getCount() {
		return getAttribute(ATT_COUNT);
	}
}
