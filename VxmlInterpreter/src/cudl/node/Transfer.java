package cudl.node;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import cudl.FormItemVisitor;
import cudl.InputFormItem;
import cudl.InterpreterException;

public class Transfer extends InputFormItem {
	private static final List<String> CHILDS;

	static {
		CHILDS = new ArrayList<String>();
		CHILDS.add("audio");
		CHILDS.add("catch");
		CHILDS.add("error");
		CHILDS.add("filled");
		CHILDS.add("grammar");
		CHILDS.add("help");
		CHILDS.add("noinput");
		CHILDS.add("nomatch");
		CHILDS.add("prompt");
		CHILDS.add("property");
		CHILDS.add("value");
	}

	public Transfer(Node node) {
		super(node);
	}

	@Override
	public void accept(FormItemVisitor formInterpretationAlgorithm) throws InterpreterException {
		formInterpretationAlgorithm.visit(this);
	}
}
