package com.sdiawara.voicextt;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.sdiawara.voicextt.exception.GotoException;
import com.sdiawara.voicextt.node.Vxml;

public class Interpreter {
	// private final Speaker speaker;
	private final InterpreterContext interpreterContext;
	private final UserInput userInput = new UserInput();;
	private final SystemOutput outPut = new SystemOutput();
	private FormInterpretationAlgorithm fia;
	private Vxml vxml;
	private String currentFileName;
	private Exception exceptionTothrow;
	private boolean isStartedTalk;

	public Interpreter(String startFileName) throws ParserConfigurationException, IOException, SAXException {
		this.currentFileName = startFileName;
		this.interpreterContext = new InterpreterContext(startFileName); // session
		this.isStartedTalk = false;

		this.vxml = new Vxml(interpreterContext.getDocumentAcces().get(this.currentFileName, null)
				.getDocumentElement());
		this.fia = new FormInterpretationAlgorithm(vxml.getFirstDialog(), interpreterContext.getScripting(),
				outPut, userInput);
		FormInterpretationAlgorithm.setDefaultUncaughtExceptionHandler(getDefaultUncaughtExceptionHandler());
	}

	public void start() throws IOException, SAXException {
		this.fia.start();
		if (exceptionTothrow != null) {
			throw new RuntimeException(exceptionTothrow);
		}
	}

	public void talk(String sentence) {
		try {
			Speaker speaker = new Speaker(userInput);
			speaker.setUtterance(sentence);
			speaker.start();
			speaker.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void waits() {
		try {
			this.fia.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public List<String> getPrompts() {
		return this.outPut.getTTS();
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
							fia = new FormInterpretationAlgorithm(vxml.getDialogById(next),
									interpreterContext.getScripting(), outPut, userInput);
						} else {
							currentFileName = currentFileName.subSequence(0, currentFileName.lastIndexOf("/"))
									+ "/" + next;
							vxml = new Vxml(interpreterContext.getDocumentAcces().get(currentFileName, null)
									.getDocumentElement());
							System.err.println(currentFileName);
							if (next.contains("#")) {
								next = next.substring(next.lastIndexOf("#") + 1);
								fia = new FormInterpretationAlgorithm(vxml.getDialogById(next),
										interpreterContext.getScripting(), outPut, userInput);
							} else {
								fia = new FormInterpretationAlgorithm(vxml.getFirstDialog(),
										interpreterContext.getScripting(), outPut, userInput);
							}
						}

					}
					fia.start();
					Interpreter.this.waits();
				} catch (Exception e1) {
					exceptionTothrow = e1;
				}
			}

		};
	}

}
