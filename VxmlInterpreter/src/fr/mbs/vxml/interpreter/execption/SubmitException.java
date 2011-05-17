package fr.mbs.vxml.interpreter.execption;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import fr.mbs.vxml.utils.VariableDeclaration;

public class SubmitException extends InterpreterException {
	public String next;

	public SubmitException(Node node, VariableDeclaration variableVxml) {
		NamedNodeMap attributes = node.getAttributes();
		this.next = attributes.getNamedItem("next").getNodeValue();

		Node namedItem = attributes.getNamedItem("namelist");
		String[] namelist = namedItem != null ? namedItem.getNodeValue().split(
				" ") : new String[0];
		String urlSuite = "?";
		for (int i = 0; i < namelist.length; i++) {
			String declareVariable = namelist[i];

			urlSuite += declareVariable + "="
					+ variableVxml.getValue(declareVariable, 0) + "&";

		}

		this.next += urlSuite;
	}
}
