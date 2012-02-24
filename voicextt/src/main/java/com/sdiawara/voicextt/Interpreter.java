package com.sdiawara.voicextt;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.sdiawara.voicextt.exception.GotoException;
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
		this.vxml = new Vxml(interpreterContext.getDocumentAcces().get(startFileName, null).getDocumentElement());
		this.fia = new FormInterpretationAlgorithm(vxml.getFirstDialog(), interpreterContext.getScripting(), outPut, userInput);
		FormInterpretationAlgorithm.setDefaultUncaughtExceptionHandler(getDefaultUncaughtExceptionHandler());
	}

	public void start() throws IOException, SAXException {
		this.fia.start();
		waitSpeaker();
	}

	private void waitSpeaker() {
		try {
			fia.join();
			speaker.join();
		} catch (InterruptedException e) {
			System.err.println(e);
		}
	}

	public List<String> getPrompts() {
		return outPut.getTTS();
	}

	private UncaughtExceptionHandler getDefaultUncaughtExceptionHandler() {
		return new UncaughtExceptionHandler() {
			public void uncaughtException(Thread t, Throwable e) {
				Throwable exception = e.getCause();
				if (exception instanceof GotoException) {
					String next = ((GotoException) exception).getGoto().getNext();
					String expr = ((GotoException) exception).getGoto().getExpr();
					if (next == null && expr == null) {
						throw new RuntimeException("Semantic error");
					}
					next = ((next == null) ? ((String) interpreterContext.getScripting().eval(expr)) : next);
					if (next.contains("#")) {
						fia = new FormInterpretationAlgorithm(vxml.getDialogById(next.replace("#", "")), interpreterContext.getScripting(), outPut,
								userInput);
						try {
							fia.start();
							Interpreter.this.waitSpeaker();
						} catch (Exception e1) {
							System.err.println(e1);
						}
					}
				}
			}

		};
	}
}
