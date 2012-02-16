package com.sdiawara.voicextt;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.sdiawara.voicextt.node.Vxml;

public class Interpreter {
	private final Speaker speaker;
	private final InterpreterContext interpreterContext;
	private final UserInput userInput = new UserInput();;
	private final SystemOutput outPut = new SystemOutput() ;
	private FormInterpretationAlgorithm fia;

	public Interpreter(String startFileName) throws ParserConfigurationException, IOException, SAXException {
		this.interpreterContext = new InterpreterContext(startFileName); // session variable
		this.speaker = new Speaker(userInput);
		Vxml vxml = new Vxml(interpreterContext.getDocumentAcces().get(startFileName, null).getDocumentElement());
		fia = new FormInterpretationAlgorithm(vxml.getFirstDialog(), interpreterContext.getScripting(), outPut, userInput);
	}

	public void start() {
		this.fia.start();
	}

	public void waitSpeaker() {
		try {
			fia.join();
			speaker.join();
		} catch (InterruptedException e) {
		}
	}

	public List<String> getPrompts() {
		return outPut.getTTS();
	}
}
