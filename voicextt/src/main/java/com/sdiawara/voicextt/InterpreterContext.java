package com.sdiawara.voicextt;

import javax.xml.parsers.ParserConfigurationException;

import com.sdiawara.voicextt.script.Scripting;

public class InterpreterContext {
	private final Scripting scripting;
	private final DocumentAcces documentAcces;

	public InterpreterContext(String startFileName) throws ParserConfigurationException {
		this.scripting = new Scripting();
		this.scripting.enterScope(); // in scope application
		this.documentAcces = new DocumentAcces("voicextt/0.1");
	}

	public Scripting getScripting() {
		return scripting;
	}

	public DocumentAcces getDocumentAcces() {
		return documentAcces;
	}
}
