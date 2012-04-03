package cudl;

import org.w3c.dom.Node;

import cudl.node.VoiceXmlNode;

public abstract class FormItem extends VoiceXmlNode {
	private static int formItemIdSize = 0;
	protected int idFormItem;

	public FormItem(Node node) {
		super(node);
		idFormItem = formItemIdSize++;
	}

	public abstract void accept(FormItemVisitor formInterpretationAlgorithm) throws InterpreterException;

	public String getName() {
		String name = getAttribute("name");
		return name == null ? getNodeName() + "$" + idFormItem : name;
	}
}
