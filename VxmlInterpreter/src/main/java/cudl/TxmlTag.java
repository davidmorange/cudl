package cudl;

import static cudl.utils.Utils.getNodeAttributeValue;

import java.io.IOException;

import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import cudl.node.VoiceXmlNode;

abstract class TxmlTag extends VoiceXmlNode {
	TxmlTag(Node node) {
		super(node);
	}
}

class ConfpassTag extends TxmlTag {

	public ConfpassTag(Node node) {
		super(node);
	}

	public Object interpret(InterpreterContext context) throws InterpreterException, IOException,
			SAXException {
		Prompt prompt = new Prompt();
		prompt.tts = "pass";
		//context.getPrompts().add(prompt);
		throw new ExitException();
	}

}

class ConffailTag extends TxmlTag {

	public ConffailTag(Node node) {
		super(node);
	}

	public Object interpret(InterpreterContext context) throws InterpreterException, IOException,
			SAXException {
		String reason = getNodeAttributeValue(node, "reason");
		if (reason == null) {
			String expr = getNodeAttributeValue(node, "expr");
			//if (expr != null)
			//	reason = "" + context.getDeclaration().evaluateScript(expr, 50);
			
		}
		throw new RuntimeException(reason == null ? "Fail" : "Fail reason -> " + reason);
	}
}

class ConfcommentTag extends TxmlTag {

	public ConfcommentTag(Node node) {
		super(node);
	}

	public Object interpret(InterpreterContext context) {
		return null;
	}
}