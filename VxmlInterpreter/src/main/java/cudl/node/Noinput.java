package cudl.node;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

public class Noinput extends VoiceXmlNode {

	public Noinput(Node node) {
		super(node);
	}

	private static final List<String> CHILDS;
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

	@Override
	public boolean canContainsChild(VoiceXmlNode child) {
		return CHILDS.contains(child.getNodeName());
	}
}
