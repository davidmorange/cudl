package com.sdiawara.voicextt;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.sdiawara.voicextt.exception.GotoException;
import com.sdiawara.voicextt.exception.VoiceXTTException;
import com.sdiawara.voicextt.node.VoiceXmlNode;
import com.sdiawara.voicextt.node.Vxml;

public class Interpreter {
	private final Speaker speaker;
	private final InterpreterContext interpreterContext;
	private final UserInput userInput = new UserInput();;
	private final SystemOutput outPut = new SystemOutput();
	private FormInterpretationAlgorithm fia;
	private Vxml vxml;

	public Interpreter(String startFileName) throws ParserConfigurationException, IOException, SAXException {
		this.interpreterContext = new InterpreterContext(startFileName); // session
		this.speaker = new Speaker(userInput);
		vxml = new Vxml(interpreterContext.getDocumentAcces().get(startFileName, null).getDocumentElement());
		resetFia(vxml.getFirstDialog());
	}

	private void resetFia(VoiceXmlNode dialog) throws IOException, SAXException {
		fia = new FormInterpretationAlgorithm(dialog, interpreterContext.getScripting(), outPut, userInput);
	}

	public void start() throws IOException, SAXException {
		VoiceXTTException lastException = fia.getLastException();
		do {
			System.err.println("start");
			if (!fia.isAlive()) {
				this.fia.start();
				waitSpeaker();
				lastException = fia.getLastException();
				if (lastException instanceof GotoException) {
					String next = ((GotoException) lastException).getGoto().getNext();
					String expr = ((GotoException) lastException).getGoto().getExpr();
					if (next == null && expr == null) {
						throw new RuntimeException("Semantic error");
					}
					next = ((next == null) ? ((String) interpreterContext.getScripting().eval(expr)) : next);
					if (next.startsWith("#")) {
						System.err.println("next " + vxml.getDialogById(next.replace("#", "")));
						resetFia(vxml.getDialogById(next.replace("#", "")));
					}
				}
			}
		} while (lastException != null);
	}

	private void waitSpeaker() {
		try {
			fia.join();
			speaker.join();
		} catch (InterruptedException e) {
			// System.err.println(e);
		}
	}

	public List<String> getPrompts() {
		return outPut.getTTS();
	}
}
