package cudl.node;

import org.w3c.dom.Node;

import cudl.FormItem;
import cudl.FormItemVisitor;
import cudl.InterpreterException;

public class Object extends FormItem {
	public Object(Node node) {
		super(node);
	}

	@Override
	public void accept(FormItemVisitor formInterpretationAlgorithm) throws InterpreterException {
		formInterpretationAlgorithm.visit(this);
	}
}
