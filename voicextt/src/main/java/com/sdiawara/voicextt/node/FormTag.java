package com.sdiawara.voicextt.node;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

class MenuTag extends VoiceXmlNode {
	private static final String ATT_ID = "id";
	private static final String ATT_SCOPE = "scope";
	private static final String ATT_DTMF = "dtmf";
	private static final String ATT_ACCEPT = "accept";

	private static final List<String> CHILDS;

	static {
		CHILDS = new ArrayList<String>();
		CHILDS.add("audio");
		CHILDS.add("catch");
		CHILDS.add("choice");
		CHILDS.add("enumerate");
		CHILDS.add("error");
		CHILDS.add("help");
		CHILDS.add("noinput");
		CHILDS.add("nomatch");
		CHILDS.add("prompt");
		CHILDS.add("property");
		CHILDS.add("script");
		CHILDS.add("value");
	}

	public MenuTag(Node node) {
		super(node);
	}

	public String getId() {
		return getAttribute(ATT_ID);
	}

	public String getScope() {
		return getAttribute(ATT_SCOPE);
	}

	public String getDtmf() {
		return getAttribute(ATT_DTMF);
	}

	public String getAttAccept() {
		return getAttribute(ATT_ACCEPT);
	}

	@Override
	public boolean canContainsChild(VoiceXmlNode child) {
		return CHILDS.contains(child.getNodeName());
	}
}

class VarTag extends VoiceXmlNode {
	private static final String ATT_NAME = "name";
	private static final String ATT_EXPR = "expr";

	public VarTag(Node node) {
		super(node);
	}

	public String getName() {
		return getAttribute(ATT_NAME);
	}

	public String getExpr() {
		return getAttribute(ATT_EXPR);
	}
}

class AssignTag extends VoiceXmlNode {
	private static final String ATT_NAME = "name";
	private static final String ATT_EXPR = "expr";

	public AssignTag(Node node) {
		super(node);
	}

	public String getName() {
		return getAttribute(ATT_NAME);
	}

	public String getExpr() {
		return getAttribute(ATT_EXPR);
	}
}

class LogTag extends VoiceXmlNode {
	private static final String ATT_LABEL = "label";
	private static final String ATT_EXPR = "expr";

	private static final List<String> CHILDS;

	static {
		CHILDS = new ArrayList<String>();
		CHILDS.add("value");
	}

	public LogTag(Node node) {
		super(node);
	}

	public String getLabel() {
		return getAttribute(ATT_LABEL);
	}

	public String getExpr() {
		return getAttribute(ATT_EXPR);
	}

	@Override
	public boolean canContainsChild(String childName) {
		return CHILDS.contains(childName);
	}
}

class PromptTag extends VoiceXmlNode {
	private static final String ATT_TIMEOUT = "timeout";
	private static final String ATT_BARGEIN = "bargein";
	private static final String ATT_BARGEINTYPE = "bargeintype";
	private static final String ATT_COND = "cond";
	private static final String ATT_XML_LANG = "xml:lang";
	private static final List<String> CHILDS;

	static {
		CHILDS = new ArrayList<String>();
		CHILDS.add("audio");
		CHILDS.add("value");
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

	public PromptTag(Node node) {
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
	public boolean canContainsChild(String childName) {
		return CHILDS.contains(childName);
	}
}

class ExitTag extends VoiceXmlNode {
	private static final String ATT_NAME_LIST = "namelist";
	private static final String ATT_EXPR = "expr";

	public ExitTag(Node node) {
		super(node);
	}

	public String getNameList() {
		return getAttribute(ATT_NAME_LIST);
	}

	public String getExpr() {
		return getAttribute(ATT_EXPR);
	}
}

class ClearTag extends VoiceXmlNode {
	private static final String ATT_NAMELIST = "namelist";

	public ClearTag(Node node) {
		super(node);
	}

	public String getNameList() {
		return getAttribute(ATT_NAMELIST);
	}
}

class IfTag extends VoiceXmlNode {
	private static final String ATT_COND = "cond";
	private static final List<String> CHILDS;

	static {
		CHILDS = new ArrayList<String>();
		CHILDS.add("assign");
		CHILDS.add("audio");
		CHILDS.add("clear");
		CHILDS.add("data");
		CHILDS.add("disconnect");
		CHILDS.add("else");
		CHILDS.add("elseif");
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

	public IfTag(Node node) {
		super(node);
	}

	public String getCond() {
		return getAttribute(ATT_COND);
	}
}

class TextTag extends VoiceXmlNode {
	public TextTag(Node node) {
		super(node);
	}

}

class CommentTag extends VoiceXmlNode {
	public CommentTag(Node node) {
		super(node);
	}

}

class GotoTag extends VoiceXmlNode {
	private static final String ATT_EXPR = "expr";
	private static final String ATT_EXPRITEM = "expritem";
	private static final String ATT_FETCH_AUDIO = "fetchaudio";
	private static final String ATT_FETCH_INT = "fetchint";
	private static final String ATT_FETCH_TIMEOUT = "fetchtimeout";
	private static final String ATT_MAX_AGE = "maxage";
	private static final String ATT_MAX_STALE = "maxstale";
	private static final String ATT_NEXT = "next";
	private static final String ATT_NEXT_ITEM = "nextitem";

	public GotoTag(Node node) {
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

class SubmitTag extends VoiceXmlNode {
	private static final String ATT_ENCTYPE = "enctype";
	private static final String ATT_EXPR = "expr";
	private static final String ATT_FETCH_AUDIO = "fetchaudio";
	private static final String ATT_FETCH_INT = "fetchint";
	private static final String ATT_FETCH_TIMEOUT = "fetchtimeout";
	private static final String ATT_METHOD = "method";
	private static final String ATT_NAME_LIST = "namelist";
	private static final String ATT_NEXT = "next";

	public SubmitTag(Node node) {
		super(node);
	}

	public String getExpr() {
		return getAttribute(ATT_EXPR);
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

	public String getMethod() {
		return getAttribute(ATT_METHOD);
	}

	public String getNext() {
		return getAttribute(ATT_NEXT);
	}

	public String getNameList() {
		return getAttribute(ATT_NAME_LIST);
	}

	public String getEnctype() {
		return getAttribute(ATT_ENCTYPE);
	}
}

class ReturnTag extends VoiceXmlNode {
	private static final String ATT_EVENT = "event";
	private static final String ATT_EVENT_EXPR = "eventexpr";
	private static final String ATT_MESSAGE_EXPR = "messageexpr";
	private static final String ATT_MESSAGE = "message";
	private static final String ATT_NAME_LIST = "namelist";

	public ReturnTag(Node node) {
		super(node);
	}

	public String getEvent() {
		return getAttribute(ATT_EVENT);
	}

	public String getEventExpr() {
		return getAttribute(ATT_EVENT_EXPR);
	}

	public String getMessageExpr() {
		return getAttribute(ATT_MESSAGE_EXPR);
	}

	public String getMessage() {
		return getAttribute(ATT_MESSAGE);
	}

	public String getNameList() {
		return getAttribute(ATT_NAME_LIST);
	}
}

class DisconnectTag extends VoiceXmlNode {
	public DisconnectTag(Node node) {
		super(node);
	}

}

class ValueTag extends VoiceXmlNode {
	public ValueTag(Node node) {
		super(node);
	}
}

class ThrowTag extends VoiceXmlNode {
	public ThrowTag(Node node) {
		super(node);
	}
}

class ScriptTag extends VoiceXmlNode {
	public ScriptTag(Node node) {
		super(node);
	}
}

class SubdialogTag extends VoiceXmlNode {
	public SubdialogTag(Node node) {
		super(node);
	}
}

class TransferTag extends VoiceXmlNode {
	public TransferTag(Node node) {
		super(node);
	}

}

class FieldTag extends VoiceXmlNode {
	private final String tags = "prompt var assign if goto submit filled";

	public FieldTag(Node node) {
		super(node);
	}

	@Override
	public boolean canContainsChild(String childName) {
		return tags.contains(childName);
	}
}

class AudioTag extends VoiceXmlNode {
	public AudioTag(Node node) {
		super(node);
	}

}

class EnumerateTag extends VoiceXmlNode {
	public EnumerateTag(Node node) {
		super(node);
	}

}

class RepromptTag extends VoiceXmlNode {

	public RepromptTag(Node node) {
		super(node);
	}
}

class ProceduralsTag extends VoiceXmlNode {

	ProceduralsTag(Node node) {
		super(node);
	}

	@Override
	public boolean canContainsChild(String childName) {
		return true;
	}
}

class BlockTag extends ProceduralsTag {
	public BlockTag(Node node) {
		super(node);
	}
}

class FilledTag extends ProceduralsTag {
	private boolean execute = false;

	public FilledTag(Node node) {
		super(node);
	}

	public void setExecute(boolean execute) {
		this.execute = execute;
	}
}

class NoinputTag extends ProceduralsTag {
	public NoinputTag(Node node) {
		super(node);
	}
}

class CatchTag extends ProceduralsTag {
	public CatchTag(Node node) {
		super(node);
	}
}

class NomatchTag extends ProceduralsTag {
	public NomatchTag(Node node) {
		super(node);
	}
}
