package com.sdiawara.voicextt;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.sdiawara.voicextt.node.VoiceXmlNode;
import com.sdiawara.voicextt.node.Vxml;

public class Interpreter {
	private final Thread fiaThread;
	private final Thread speakerThread;
	private final Speaker speaker;
	private final InterpreterContext interpreterContext;
	private final UserInput userInput;
	private final SystemOutput outPut;
	private FormInterpretationAlgorithm fia;

	public Interpreter(String startFileName) throws ParserConfigurationException, IOException, SAXException {
		this.interpreterContext = new InterpreterContext(startFileName); // session variable
		this.userInput = new UserInput();
		this.outPut = new SystemOutput();
		this.speaker = new Speaker(userInput);
		Vxml vxml = new Vxml(interpreterContext.getDocumentAcces().get(startFileName, null).getDocumentElement());
		VoiceXmlNode dialog = vxml.getFirstDialog();
		fia = new FormInterpretationAlgorithm(dialog, interpreterContext.getScripting(), outPut, userInput);
		this.fiaThread = new Thread(fia);
		this.speakerThread = new Thread(speaker);
	}

	public void start() {
		this.fiaThread.start();
	}

	public void waitSpeaker() {
		try {
			fiaThread.join();
			speakerThread.join();
		} catch (InterruptedException e) {
		}
	}

	public List<String> getPrompts() {
		return outPut.getTTS();
	}
}
