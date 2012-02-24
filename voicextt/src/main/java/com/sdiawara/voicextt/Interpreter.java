package com.sdiawara.voicextt;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
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
		this.vxml = new Vxml(interpreterContext.getDocumentAcces().get(startFileName, null)
				.getDocumentElement());
		resetFia(vxml.getFirstDialog());
	}

	private void resetFia(VoiceXmlNode dialog) throws IOException, SAXException {
		this.fia = new FormInterpretationAlgorithm(dialog, interpreterContext.getScripting(), outPut, userInput);
		UncaughtExceptionHandler eh = new UncaughtExceptionHandler() {

			public void uncaughtException(Thread t, Throwable e) {
				Throwable exception = e.getCause();
				System.err.println(exception);
				if (exception instanceof GotoException) {
					String next = ((GotoException) exception).getGoto().getNext();
					String expr = ((GotoException) exception).getGoto().getExpr();
					if (next == null && expr == null) {
						throw new RuntimeException("Semantic error");
					}
					System.err.println(next);
					next = ((next == null) ? ((String) interpreterContext.getScripting().eval(expr)) : next);
					if (next.startsWith("#")) {
						System.err.println("next " + next.replace("#", "") + " fia is a live= " + fia.isAlive()
								+ "test isinterrupt " + t.isInterrupted());
						fia.setCurrentDialog(vxml.getDialogById(next.replace("#", "")));
						System.err.println("next " + next.replace("#", "") + " fia is a live= " + fia.isAlive()
								+ "test isinterrupt " + t.isInterrupted());
						fia.run();
						System.err.println("after fia is a live= " + fia.isAlive()
								+ "test isinterrupt " + t.isInterrupted());
						fia.setUncaughtExceptionHandler(this);
					}
				}
			}

		};
		fia.setUncaughtExceptionHandler(eh);
	}

	public void start() throws IOException, SAXException {
		// if (!fia.isAlive()) {
		this.fia.start();
		waitSpeaker();
		System.err.println("fin");
		// }
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
