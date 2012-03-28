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
	protected final InterpreterContext interpreterContext;
	private final UserInput userInput = new UserInput();;
	private final SystemOutput outPut = new SystemOutput();
	private FormInterpretationAlgorithm fia;
	private Vxml vxml;
	private String currentFileName;
	private Exception exceptionTothrow;
	private static final String APPLICATION_VARIABLES ="lastresult$[0].confidence = 1; "
			+ "lastresult$[0].utterance = undefined;" + "lastresult$[0].inputmode = undefined;"
			+ "lastresult$[0].interpretation = undefined;";
	
	public Interpreter(String startFileName) throws ParserConfigurationException, IOException, SAXException {
		this.currentFileName = startFileName;
		this.interpreterContext = new InterpreterContext(startFileName);

		this.vxml = new Vxml(interpreterContext.getDocumentAcces().get(this.currentFileName, null)
				.getDocumentElement());
		this.fia = new FormInterpretationAlgorithm(vxml.getFirstDialog(), interpreterContext.getScripting(),
				outPut, userInput);
		FormInterpretationAlgorithm.setDefaultUncaughtExceptionHandler(getDefaultUncaughtExceptionHandler());
		interpreterContext.getScripting().enterScope(); // in scope application
		initializeApplicationVariables();
		// interpreterContext.getScripting().enterScope(); // in scope document
		// initializeDocumentVariables();
	}

	protected void initializeApplicationVariables() {
		interpreterContext.getScripting().put("lastresult$","new Array()");
		interpreterContext.getScripting().eval("lastresult$[0] = new Object()");
		interpreterContext.getScripting().eval(APPLICATION_VARIABLES);
	}

	protected void initializeDocumentVariables() {

	}

	public void start() throws IOException, SAXException {
		this.fia.start();
		if (exceptionTothrow != null) {
			throw new RuntimeException(exceptionTothrow);
		}
		try {
			Thread.sleep(300);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void talk(String sentence) {
		try {
			Speaker speaker = new Speaker(userInput);
			speaker.setUtterance(sentence);
			speaker.start();
			speaker.join();
			this.fia.join(300);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public List<String> getPrompts() {
		return this.outPut.getTTS();
	}

	private UncaughtExceptionHandler getDefaultUncaughtExceptionHandler() {
		return new UncaughtExceptionHandler() {

			@Override
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
					fia.join();
				} catch (Exception e1) {
					exceptionTothrow = e1;
				}
			}

		};
	}

}
