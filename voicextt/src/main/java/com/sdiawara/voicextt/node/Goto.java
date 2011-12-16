package com.sdiawara.voicextt.node;

import org.w3c.dom.Node;

public class Goto extends VoiceXmlNode {
	private static final String ATT_EXPR = "expr";
	private static final String ATT_EXPRITEM = "expritem";
	private static final String ATT_FETCH_AUDIO = "fetchaudio";
	private static final String ATT_FETCH_INT = "fetchint";
	private static final String ATT_FETCH_TIMEOUT = "fetchtimeout";
	private static final String ATT_MAX_AGE = "maxage";
	private static final String ATT_MAX_STALE = "maxstale";
	private static final String ATT_NEXT = "next";
	private static final String ATT_NEXT_ITEM = "nextitem";

	public Goto(Node node) {
		super(node);
	}

	public String getExpr() {
		return getAttribute(ATT_EXPR);
	}

	public String getExpritem() {
		return getAttribute(ATT_EXPRITEM);
	}

	public String getFetchAudio() {
		return getAttribute(ATT_FETCH_AUDIO);
	}

	public String getFetchInt() {
		return getAttribute(ATT_FETCH_INT);
	}

	public String getFetchTimeout() {
		return getAttribute(ATT_FETCH_TIMEOUT);
	}

	public String getMaxAge() {
		return getAttribute(ATT_MAX_AGE);
	}

	public String getMaxStale() {
		return getAttribute(ATT_MAX_STALE);
	}

	public String getNext() {
		return getAttribute(ATT_NEXT);
	}

	public String getNextItem() {
		return getAttribute(ATT_NEXT_ITEM);
	}
}
