package com.sdiawara.voicextt;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;

import javax.management.RuntimeErrorException;
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
	private String currentFileName;
	private Exception exceptionTothrow;

	public Interpreter(String startFileName) throws ParserConfigurationException, IOException, SAXException {
		this.currentFileName = startFileName;
		this.interpreterContext = new InterpreterContext(startFileName); // session
		this.speaker = new Speaker(userInput);

		this.vxml = new Vxml(interpreterContext.getDocumentAcces().get(this.currentFileName, null).getDocumentElement());
		this.fia = new FormInterpretationAlgorithm(vxml.getFirstDialog(), interpreterContext.getScripting(), outPut, userInput);
		FormInterpretationAlgorithm.setDefaultUncaughtExceptionHandler(getDefaultUncaughtExceptionHandler());
	}

	public void start() throws IOException, SAXException {
		this.fia.start();
		waitSpeaker();
		if(exceptionTothrow != null){
			throw new RuntimeException(exceptionTothrow);
		}

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
				try {
					if (exception instanceof GotoException) {
						String next = ((GotoException) exception).getGoto().getNext();
						String expr = ((GotoException) exception).getGoto().getExpr();
						if (next == null && expr == null) {
							throw new RuntimeException("Semantic error");
						}
						next = ((next == null) ? ((String) interpreterContext.getScripting().eval(expr)) : next);
						if (next.startsWith("#")) {
							next = next.replace("#", "");
							fia = new FormInterpretationAlgorithm(vxml.getDialogById(next), interpreterContext.getScripting(), outPut, userInput);
						} else {
							currentFileName = currentFileName.subSequence(0, currentFileName.lastIndexOf("/")) + "/" + next;
							vxml = new Vxml(interpreterContext.getDocumentAcces().get(currentFileName, null).getDocumentElement());
							System.err.println(currentFileName);
							if (next.contains("#")) {
								next = next.substring(next.lastIndexOf("#") + 1);
								fia = new FormInterpretationAlgorithm(vxml.getDialogById(next), interpreterContext.getScripting(), outPut, userInput);
							} else {
								fia = new FormInterpretationAlgorithm(vxml.getFirstDialog(), interpreterContext.getScripting(), outPut, userInput);
							}
						}

					}
					fia.start(); 
					Interpreter.this.waitSpeaker();
				} catch (Exception e1) {
					exceptionTothrow = e1;
				}
			}

		};
	}
}
