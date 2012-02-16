package com.sdiawara.voicextt.node;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

public class Vxml extends VoiceXmlNode {
	private final String ATT_APPLICATION = "application";
	private static final String ATT_VERSION = "version";
	private static final String ATT_XML_BASE = "xml:base";
	private static final String ATT_XML_LANG = "xml:lang";
	private static final String ATT_XMLNS = "xmlns";
	private static final List<String> CHILDS;

	static {
		CHILDS = new ArrayList<String>();
		CHILDS.add("catch");
		CHILDS.add("error");
		CHILDS.add("form");
		CHILDS.add("help");
		CHILDS.add("link");
		CHILDS.add("menu");
		CHILDS.add("meta");
		CHILDS.add("noinput");
		CHILDS.add("nomatch");
		CHILDS.add("property");
		CHILDS.add("script");
		CHILDS.add("var");
	}

	public Vxml(Node node) {
		super(node);
	}

	public String getApplication() {
		return getAttribute(ATT_APPLICATION);
	}

	public String getVersion() {
		return getAttribute(ATT_VERSION);
	}

	public String getXmlBase() {
		return getAttribute(ATT_XML_BASE);
	}

	public String getXmlLang() {
		return getAttribute(ATT_XML_LANG);
	}

	public String getXmlns() {
		return getAttribute(ATT_XMLNS);
	}

	public VoiceXmlNode getFirstDialog() {
		for (VoiceXmlNode child : childs) {
			if (child.getNodeName().equals("form") || child.getNodeName().equals("menu")) {
				return child;
			}
		}
		throw new RuntimeException("vxml document must have one dialog (menu or form)");
	}

	@Override
	public boolean canContainsChild(VoiceXmlNode child) {
		return CHILDS.contains(child.getNodeName());
	}

	public VoiceXmlNode getDialogById(String id) {
		for (VoiceXmlNode child : getChilds()) {
			if (child instanceof Form || child instanceof Menu) {
				if (id.equals(((Form) child).getId())) {
					return child;
				}
			}
		}
		return null;
	}
}
