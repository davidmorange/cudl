package cudl.node;

import org.w3c.dom.Node;

import cudl.FormItemVisitor;
import cudl.InputFormItem;
import cudl.InterpreterException;

public class Record  extends InputFormItem{
	public Record(Node node) {
		super(node);
	}

	@Override
	public void accept(FormItemVisitor formInterpretationAlgorithm) throws InterpreterException {
		formInterpretationAlgorithm.visit(this);
	}
}
