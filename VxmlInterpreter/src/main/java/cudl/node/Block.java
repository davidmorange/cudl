package cudl.node;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import cudl.FormItem;
import cudl.FormItemVisitor;
import cudl.InterpreterException;

public class Block extends FormItem {
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
		CHILDS.add("#text");
 	}

	public Block(Node node) {
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
