package cudl.node;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

public class If extends VoiceXmlNode {
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

	public If(Node node) {
		super(node);
	}

	public String getCond() {
		return getAttribute(ATT_COND);
	}
	@Override
	public boolean canContainsChild(VoiceXmlNode child) {
		return CHILDS.contains(child.getNodeName());
	}
}
