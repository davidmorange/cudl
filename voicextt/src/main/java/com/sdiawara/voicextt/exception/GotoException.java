package com.sdiawara.voicextt.exception;

import com.sdiawara.voicextt.node.Goto;

public class GotoException extends InterpreterException {
	private final Goto goto1;

	public GotoException(Goto goto1) {
		this.goto1 = goto1;
	}

	public Goto getGoto() {
		return goto1;
	}
}
