package cudl;

import org.w3c.dom.Node;

import cudl.node.Goto;

public class InterpreterException extends Exception {
}

class TransferException extends InterpreterException {
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
	String type;

	EventException(String type) {
		this.type = type;
	}
}

class SubmitException extends InterpreterException {
	String next;

	SubmitException(String next) {
		this.next = next;
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
