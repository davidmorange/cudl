package cudl.node;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;


public class Menu extends VoiceXmlNode {
	private static final String ATT_ID = "id";
	private static final String ATT_SCOPE = "scope";

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

	public Menu(Node node) {
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
