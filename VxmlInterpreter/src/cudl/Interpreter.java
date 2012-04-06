package cudl;

import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import cudl.node.Script;
import cudl.node.Var;
import cudl.node.VoiceXmlNode;
import cudl.node.Vxml;
import cudl.script.Scripting;

public class Interpreter {
	protected InterpreterContext interpreterContext;
	private UserInput userInput = new UserInput();;
	private SystemOutput outPut = new SystemOutput();
	private FormInterpretationAlgorithm fia;
	private Vxml vxml;
	private String currentFileName;
	private Exception exceptionTothrow;
	private static final String APPLICATION_VARIABLES = "lastresult$[0].confidence = 1; "
			+ "lastresult$[0].utterance = undefined;" + "lastresult$[0].inputmode = undefined;"
			+ "lastresult$[0].interpretation = undefined;";

	public Interpreter(String url) throws IOException, ParserConfigurationException, SAXException {
		this(url, "");
	}

	public Interpreter(String url, String sessionVariables) throws IOException, ParserConfigurationException,
			SAXException {
		this.currentFileName = url;
		this.interpreterContext = new InterpreterContext(url);

		this.vxml = new Vxml(interpreterContext.getDocumentAcces().get(this.currentFileName, null)
				.getDocumentElement());
		VoiceXmlNode dialog;
		if(!url.contains("#")){
			dialog= vxml.getFirstDialog();
		}else {
			dialog = vxml.getDialogById(url.split("#")[1]);
		}
		this.fia = new FormInterpretationAlgorithm(dialog, interpreterContext.getScripting(),
				outPut, userInput);
		FormInterpretationAlgorithm.setDefaultUncaughtExceptionHandler(getDefaultUncaughtExceptionHandler());
		interpreterContext.getScripting().eval(sessionVariables);
		interpreterContext.getScripting().enterScope(); // in scope application
		try {
			initializeApplicationVariables();
		} catch (InterpreterException e) {
			throw new RuntimeException(e);
		}
		interpreterContext.getScripting().enterScope(); // in scope document
		try {
			initializeDocumentVariables();
		} catch (InterpreterException e) {
			throw new RuntimeException(e);
		}
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

	public void noInput() throws IOException, SAXException, ParserConfigurationException {
		try {
			enterInput("noinput", "event$");
			this.fia.join(300);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void noMatch() throws IOException, SAXException, ParserConfigurationException {
		try {
			enterInput("nomatch", "event$");
			this.fia.join(300);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void disconnect() throws IOException, SAXException, ParserConfigurationException {
		throw new RuntimeException("noMatch not implemented");
		// internalInterpreter.interpret(EVENT, "connection.disconnect.hangup");
	}

	public void blindTransferSuccess() throws IOException, SAXException, ParserConfigurationException {
		throw new RuntimeException("noMatch not implemented");
		// internalInterpreter.interpret(BLIND_TRANSFER_SUCCESSSS, null);
	}

	public void noAnswer() throws IOException, SAXException, ParserConfigurationException {
		throw new RuntimeException("noMatch not implemented");
		// internalInterpreter.interpret(NOANSWER, null);
	}

	public void callerHangupDuringTransfer() throws IOException, SAXException, ParserConfigurationException {
		throw new RuntimeException("noMatch not implemented");
		// internalInterpreter.interpret(CALLER_HUNGUP_DURING_TRANSFER, null);
	}

	public void networkBusy() throws IOException, SAXException, ParserConfigurationException {
		throw new RuntimeException("noMatch not implemented");
		// internalInterpreter.interpret(NETWORK_BUSY, null);
	}

	public void destinationBusy() throws IOException, SAXException, ParserConfigurationException {
		throw new RuntimeException("noMatch not implemented");
		// internalInterpreter.interpret(DESTINATION_BUSY, null);
	}

	public void transferTimeout() throws IOException, SAXException, ParserConfigurationException {
		throw new RuntimeException("noMatch not implemented");
		// internalInterpreter.interpret(MAX_TIME_DISCONNECT, null);
	}

	public void talk(String sentence) {
		try {
			enterInput(sentence, "voice$");
			this.fia.join(300);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	private void enterInput(String sentence, String inputType) throws InterruptedException {
		Speaker speaker = new Speaker(userInput);
		speaker.setUtterance(inputType + sentence);
		speaker.start();
		speaker.join();
	}

	public void submitDtmf(String dtmf) throws IOException, SAXException, ParserConfigurationException {
		try {
			enterInput(dtmf, "dtmf$");
			this.fia.join(300);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public List<String> getLogsWithLabel(String... label) {
		return outPut.getLogs(label);
	}

	public List<String> getLogs() {
		return outPut.getLogs();
	}

	public boolean hungup() {
		return !fia.isAlive();
	}

	public List<Prompt> getPrompts() {
		return outPut.getPrompts();
	}

	public String getTranferDestination() {
		return null;// context.getTransferDestination();
	}

	public String getActiveGrammar() {
		return null;// Utils.getNodeAttributeValue(context.getGrammarActive().get(0),
					// "src").trim();
	}

	public Properties getCurrentDialogProperties() {
		return null;// internalInterpreter.getCurrentDialogProperties();
	}

	public void destinationHangup() throws IOException, SAXException, ParserConfigurationException {
		throw new RuntimeException("noMatch not implemented");
		// internalInterpreter.interpret(DESTINATION_HANGUP, null);
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
							if (next.contains("#")) {
								next = next.substring(next.lastIndexOf("#") + 1);
								fia = new FormInterpretationAlgorithm(vxml.getDialogById(next),
										interpreterContext.getScripting(), outPut, userInput);
							} else {
								fia = new FormInterpretationAlgorithm(vxml.getFirstDialog(),
										interpreterContext.getScripting(), outPut, userInput);
							}
						}
						fia.start();
						fia.join();
					} else if (exception instanceof ChoiceException) {
						String next = ((ChoiceException) exception).getChoice().getAttribute("next");
						if (next != null) {
							currentFileName = currentFileName.subSequence(0, currentFileName.lastIndexOf("/"))
									+ "/" + next;
							vxml = new Vxml(interpreterContext.getDocumentAcces().get(currentFileName, null)
									.getDocumentElement());
							if (next.contains("#")) {
								next = next.substring(next.lastIndexOf("#") + 1);
								fia = new FormInterpretationAlgorithm(vxml.getDialogById(next),
										interpreterContext.getScripting(), outPut, userInput);
							} else {
								fia = new FormInterpretationAlgorithm(vxml.getFirstDialog(),
										interpreterContext.getScripting(), outPut, userInput);
							}
							fia.start();
							fia.join();
						}
					} else {
						throw new RuntimeException(e);
					}
				} catch (Exception e1) {
					exceptionTothrow = e1;
				}
			}

		};
	}

	protected void initializeApplicationVariables() throws IOException, SAXException, InterpreterException {
		interpreterContext.getScripting().put("lastresult$", "new Array()");
		interpreterContext.getScripting().eval("lastresult$[0] = new Object()");
		interpreterContext.getScripting().eval(APPLICATION_VARIABLES);
		String rootName = vxml.getApplication();

		if (rootName != null) {
			Vxml root = new Vxml(interpreterContext.getDocumentAcces().get(rootName, null).getDocumentElement());
			for (VoiceXmlNode voiceXmlNode : root.getChilds()) {
				if (voiceXmlNode instanceof Var || voiceXmlNode instanceof Script) {
					new Executor(interpreterContext.getScripting(), null).execute(voiceXmlNode);
				}
			}
		}
	}

	protected void initializeDocumentVariables() throws InterpreterException {
		for (VoiceXmlNode voiceXmlNode : vxml.getChilds()) {
			if (voiceXmlNode instanceof Var || voiceXmlNode instanceof Script) {
				new Executor(interpreterContext.getScripting(), null).execute(voiceXmlNode);
			}
		}
	}
}
