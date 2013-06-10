package cudl;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;

import cudl.node.Script;
import cudl.node.Var;
import cudl.node.VoiceXmlNode;
import cudl.node.Vxml;
import cudl.script.Scripting.Scope;
import cudl.utils.Session;

public class Interpreter {
	private static final int JOIN_TIME = 200;
	protected InterpreterContext interpreterContext;
	private UserInput userInput = new UserInput();
	private SystemOutput outPut = new SystemOutput();
	FormInterpretationAlgorithm fia;
	private Vxml vxml;
	private String currentFileName;
	private boolean isHangup;
	private DocumentAcces documentAcces;
	private boolean inSubdialog = false;

	private static final String APPLICATION_VARIABLES = "lastresult$[0].confidence = 1; " + "lastresult$[0].utterance = undefined;"
			+ "lastresult$[0].inputmode = undefined;" + "lastresult$[0].interpretation = undefined;";
	private Logger LOGGER = Logger.getRootLogger();
	
	public Interpreter(String url) throws IOException, ParserConfigurationException, SAXException {
		this(url, Session.getSessionScript());
	}

	public Interpreter(String url, String sessionVariables) throws IOException, ParserConfigurationException, SAXException {
		BasicConfigurator.configure();
		this.currentFileName = url;
		this.interpreterContext = new InterpreterContext(url);
		this.documentAcces = interpreterContext.getDocumentAcces();
		this.vxml = new Vxml(interpreterContext.getDocumentAcces().get(this.currentFileName, null).getDocumentElement());
		VoiceXmlNode dialog;
		if (!url.contains("#")) {
			dialog = vxml.getFirstDialog();
		} else {
			dialog = vxml.getDialogById(url.split("#")[1]);
		}
		this.fia = new FormInterpretationAlgorithm(dialog, interpreterContext.getScripting(), outPut, userInput, documentAcces);
		interpreterContext.getScripting().eval(sessionVariables);
		interpreterContext.getScripting().enterScope(Scope.APPLICATION);

		try {
			initializeApplicationVariables();
		} catch (InterpreterException e) {
			throw new RuntimeException(e);
		}
		interpreterContext.getScripting().enterScope(Scope.DOCUMENT);

		try {
			initializeDocumentVariables();
		} catch (InterpreterException e) {
			throw new RuntimeException(e);
		}
	}

	Interpreter(String url, String sessionVariables, SystemOutput output, UserInput userInput, DocumentAcces documentAcces) throws IOException,
			ParserConfigurationException, SAXException {
		this.currentFileName = url;
		this.outPut = output;
		this.userInput = userInput;
		this.interpreterContext = new InterpreterContext(url);
		this.documentAcces = documentAcces;
		this.vxml = new Vxml(this.documentAcces.get(this.currentFileName, null).getDocumentElement());
		VoiceXmlNode dialog;
		if (!url.contains("#")) {
			dialog = vxml.getFirstDialog();
		} else {
			dialog = vxml.getDialogById(url.split("#")[1]);
		}
		this.fia = new FormInterpretationAlgorithm(dialog, interpreterContext.getScripting(), outPut, userInput, null);
		interpreterContext.getScripting().eval(sessionVariables);
		interpreterContext.getScripting().enterScope(Scope.APPLICATION); 
		try {
			initializeApplicationVariables();
		} catch (InterpreterException e) {
			throw new RuntimeException(e);
		}
		interpreterContext.getScripting().enterScope(Scope.DOCUMENT); 
		try {
			initializeDocumentVariables();
		} catch (InterpreterException e) {
			throw new RuntimeException(e);
		}
	}

	public void start() throws IOException, SAXException {
		this.fia.start();
		try {
			LOGGER.debug("wait for interpretation ...");
			Thread.sleep(JOIN_TIME);
			LOGGER.debug("... en of interpretation");
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void noInput() {
		try {
			enterInput("noinput", "event$");
			this.fia.join(JOIN_TIME);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void noMatch()  {
		try {
			enterInput("nomatch", "event$");
			this.fia.join(JOIN_TIME);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void disconnect() {
		try {
			enterInput("connection.disconnect.hangup", "event$");
			this.fia.join(JOIN_TIME);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void blindTransferSuccess() {
		try {
			interpreterContext.getScripting().eval("connection.protocol.isdnvn6.transferresult= '0'");
			enterInput("connection.disconnect.transfer", "event$");
			this.fia.join(JOIN_TIME);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void noAnswer() {
		try {
			interpreterContext.getScripting().eval("connection.protocol.isdnvn6.transferresult= '2'");
			enterInput("'noanswer'", "transfer$");
			this.fia.join(JOIN_TIME);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void callerHangupDuringTransfer() {
		try {
			enterInput("'near_end_disconnect'", "transfer$");
			this.fia.join(JOIN_TIME);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void networkBusy() {
		try {
			interpreterContext.getScripting().eval("connection.protocol.isdnvn6.transferresult= '5'");
			enterInput("'network_busy'", "transfer$");
			this.fia.join(JOIN_TIME);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void destinationBusy() {
		try {
			enterInput("'busy'", "transfer$");
			this.fia.join(JOIN_TIME);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void transferTimeout() {
		try {
			enterInput("'maxtime_disconnect'", "transfer$");
			this.fia.join(JOIN_TIME);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public void talk(String sentence) {
		try {
			enterInput(sentence, "voice$");
			this.fia.join(JOIN_TIME);
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

	public void submitDtmf(String dtmf) {
		try {
			enterInput(dtmf, "dtmf$");
			this.fia.join(JOIN_TIME);
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
		return fia.isHangup;
	}

	public List<Prompt> getPrompts() {
		return outPut.getPrompts();
	}

	public String getTranferDestination() {
		return outPut.getTranferDestination();
	}

	public String getActiveGrammar() {
		return outPut.getActiveGrammar();// Utils.getNodeAttributeValue(context.getGrammarActive().get(0),
		// "src").trim();
	}

	public Properties getCurrentDialogProperties() {
		return null;// internalInterpreter.getCurrentDialogProperties();
	}

	public void destinationHangup() {
		throw new RuntimeException("destinationHangup not implemented");
		// internalInterpreter.interpret(DESTINATION_HANGUP, null);
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
					new Executor(interpreterContext.getScripting(), null, documentAcces).execute(voiceXmlNode);
				}
			}
		}
	}

	protected void initializeDocumentVariables() throws InterpreterException {
		for (VoiceXmlNode voiceXmlNode : vxml.getChilds()) {
			if (voiceXmlNode instanceof Var || voiceXmlNode instanceof Script) {
				new Executor(interpreterContext.getScripting(), null, documentAcces).execute(voiceXmlNode);
			}
		}
	}

	public boolean isHangup() {
		return isHangup;
	}

	public void setHangup(boolean isHangup) {
		this.isHangup = isHangup;
	}

	public boolean isInSubdialog() {
		return inSubdialog;
	}

	public void setInSubdialog(boolean inSubdialog) {
		this.inSubdialog = inSubdialog;
	}
}