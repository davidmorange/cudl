package com.sdiawara.voicextt;

import javax.xml.parsers.ParserConfigurationException;

import com.sdiawara.voicextt.script.Scripting;

public class InterpreterContext {
	private static final String USER_AGENT = "voicextt/0.1";
	private final Scripting scripting;
	private final DocumentAcces documentAcces;

	public InterpreterContext(String startFileName) throws ParserConfigurationException {
		this.scripting = new Scripting();
		this.documentAcces = new DocumentAcces(USER_AGENT);
	}

	public Scripting getScripting() {
		return scripting;
	}

	public DocumentAcces getDocumentAcces() {
		return documentAcces;
	}
}
