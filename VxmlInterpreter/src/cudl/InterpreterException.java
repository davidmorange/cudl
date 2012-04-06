package cudl;

import org.w3c.dom.Node;

import cudl.node.Choice;
import cudl.node.Goto;
import cudl.node.Submit;
import cudl.node.Transfer;
import cudl.node.VoiceXmlNode;

public class InterpreterException extends Exception {

	public InterpreterException(String message) {
		super(message);
	}

	public InterpreterException() {
	}
}

class TransferException extends InterpreterException {

	private final Transfer transfer;

	public TransferException(Transfer transfer) {
		this.transfer = transfer;
	}

	public Transfer getTransfer() {
		return transfer;
	}
}

class FilledException extends InterpreterException {
	FilledException(Node formItem) {
	}
}

class ExitException extends InterpreterException {
}

class GotoException extends InterpreterException {
	final Goto goto1;

	public GotoException(Goto goto1) {
		this.goto1 = goto1;
	}

	public Goto getGoto() {
		return goto1;
	}
}

class EventException extends InterpreterException {
	String eventType;

	EventException(VoiceXmlNode node, String eventType) {
		this.eventType = eventType;
	}
}

class DialogChangeException extends InterpreterException {
	private final String nextDialogId;

	public DialogChangeException(String nextDialogId) {
		this.nextDialogId = nextDialogId;
	}

	public String getNextDialogId() {
		return nextDialogId;
	}

}

class FormItemChangeException extends InterpreterException {
	private final String nextFormItemName;

	public FormItemChangeException(String nextFormItemName) {
		this.nextFormItemName = nextFormItemName;
	}

	public String getNextFormItemName() {
		return nextFormItemName;
	}
}

class DocumentChangeException extends InterpreterException {
	private final String nextDocumentFileName;
	private final String method;

	public DocumentChangeException(String nextDocumentFileName, String method) {
		this.nextDocumentFileName = nextDocumentFileName;
		this.method = method;
	}

	public String getMethod() {
		return method;
	}

	public String getNextDocumentFileName() {
		return nextDocumentFileName;
	}
}

class SubmitException extends InterpreterException {
	private final Submit submit;

	SubmitException(Submit submit) {
		this.submit = submit;
	}
}

class ChoiceException extends InterpreterException {
	private final Choice choice;

	public ChoiceException(Choice choice) {
		this.choice = choice;
	}

	public Choice getChoice() {
		return choice;
	}

}

class ReturnException extends InterpreterException {
	final String namelist;
	final String eventexpr;
	final String event;

	ReturnException(String event, String eventexpr, String namelist) {
		this.event = event;
		this.eventexpr = eventexpr;
		this.namelist = namelist;
	}
}

class SemanticException extends InterpreterException {
	final VoiceXmlNode node;

	public SemanticException(VoiceXmlNode node, String message) {
		super(message);
		this.node = node;
	}

}
