package cudl;

import static cudl.utils.Utils.getNodeAttributeValue;
import static cudl.utils.Utils.serachItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.script.ScriptException;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cudl.event.InterpreterEvent;
import cudl.script.DefaultInterpreterScriptContext;
import cudl.script.InterpreterScriptContext;
import cudl.script.InterpreterVariableDeclaration;

public class InternalInterpreter {
	private InterpreterVariableDeclaration declaration;
	private Properties dialogProperties = new Properties();
	private boolean hangup;

	private FormInterpreationAlgorithm fia;
	private WIPContext context;
	private InterpreterEventHandler interpreterListener;

	InternalInterpreter(String location) throws IOException, ScriptException,
			ParserConfigurationException, SAXException {
		declaration = new InterpreterVariableDeclaration(location);
		context = new WIPContext(location, declaration);
		interpreterListener = new InterpreterEventHandler(context);
	}

	void interpretDialog() throws ScriptException, IOException, SAXException {
		fia = new FormInterpreationAlgorithm(context, declaration);
		fia.initializeDialog(context.getCurrentDialog());
		mainLoop();
	}

	private void mainLoop() throws ScriptException, IOException, SAXException {
		try {
			fia.mainLoop();
		} catch (GotoException e) {
			context.buildDocument(e.next);
			fia.initializeDialog(context.getCurrentDialog());
			mainLoop();
		} catch (SubmitException e) {
			System.err.println("submmit "+e.next);
			context.buildDocument(e.next);
			fia.initializeDialog(context.getCurrentDialog());
			mainLoop();
		} catch (EventException e) {
			try {
				interpreterListener.doEvent(new InterpreterEvent(this, e.type),
						fia.executor);
			} catch (InterpreterException e1) {
				executionHandler(e1);
			}
		} catch (InterpreterException e) {
			System.err.println(e.getClass() + "++=======");
		}
	}

	void blindTransferSuccess() throws ScriptException, IOException,
			SAXException {
		try {
			interpreterListener.doEvent(new InterpreterEvent(this,
					"connection.disconnect.transfer"), fia.executor);
		} catch (InterpreterException e) {
			executionHandler(e);
		}
	}

	void destinationHangup() throws ScriptException, IOException, SAXException {
		declaration.setValue(
				fia.getFormItemName(context.getSelectedFormItem()),
				"'far_end_disconnect'",
				DefaultInterpreterScriptContext.DIALOG_SCOPE);
		mainLoop();
	}

	void callerHangDestination() throws ScriptException, IOException,
			SAXException {
		try {
			setTransferResultAndExecute("'near_end_disconnect'");
		} catch (InterpreterException e) {
			executionHandler(e);
		}
	}

	private void setTransferResultAndExecute(String transferResult)
			throws ScriptException, InterpreterException, IOException {
		declaration.setValue(
				fia.getFormItemName(context.getSelectedFormItem()),
				transferResult, DefaultInterpreterScriptContext.DIALOG_SCOPE);
		fia.executor
				.execute(serachItem(context.getSelectedFormItem(), "filled"));
	}

	void event(String eventType) throws ScriptException, IOException,
			SAXException {
		try {
			interpreterListener.doEvent(new InterpreterEvent(this, eventType),
					fia.executor);
		} catch (InterpreterException e) {
			executionHandler(e);
		}
	}

	private void executionHandler(InterpreterException e)
			throws ScriptException, IOException, SAXException {

		if (e instanceof GotoException) {
			declaration
					.resetScopeBinding(InterpreterScriptContext.ANONYME_SCOPE);
			context.buildDocument(((GotoException) e).next);
			fia.initializeDialog(context.getCurrentDialog());
			mainLoop();
		} else if (e instanceof SubmitException) {
			context.buildDocument(((SubmitException) e).next);
			System.err.println("current submit dialog"
					+ context.getCurrentDialog());
			fia.initializeDialog(context.getCurrentDialog());
			mainLoop();
		}
	}

	void callerHangup(int i) throws ScriptException, IOException, SAXException {
		declaration.evaluateScript(
				"connection.protocol.isdnvn6.transferresult= '" + i + "'",
				DefaultInterpreterScriptContext.SESSION_SCOPE);
		try {
			interpreterListener.doEvent(new InterpreterEvent(this,
					"connection.disconnect.hangup"), fia.executor);
		} catch (InterpreterException e) {
			executionHandler(e);
		}
	}

	void noAnswer() throws ScriptException, IOException, SAXException {
		declaration.evaluateScript(
				"connection.protocol.isdnvn6.transferresult= '2'",
				DefaultInterpreterScriptContext.SESSION_SCOPE);
		try {
			setTransferResultAndExecute("'noanswer'");
		} catch (InterpreterException e) {
			executionHandler(e);
		}
	}

	// private boolean isBlindTransfer(Node node) {
	// return !Boolean.parseBoolean(getNodeAttributeValue(node, "bridge"));
	// }

	List<String> getTraceLog() {
		List<String> labeledLog = new ArrayList<String>();
		for (Iterator<Log> iterator = fia.getLogs().iterator(); iterator
				.hasNext();) {
			labeledLog.add(((Log) iterator.next()).value);
		}

		return labeledLog;
	}

	List<String> getTracetWithLabel(String... label) {
		List<String> labeledLog = new ArrayList<String>();
		for (Iterator<Log> iterator = fia.getLogs().iterator(); iterator
				.hasNext();) {
			Log log = (Log) iterator.next();
			for (int i = 0; i < label.length; i++) {
				if (log.label.equals(label[i])) {
					labeledLog.add(log.value);
				}
			}
		}
		return labeledLog;
	}

	List<Prompt> getPrompts() {
		return fia.getPrompts();
	}

	void resetDocumentScope() {
		declaration
				.resetScopeBinding(DefaultInterpreterScriptContext.ANONYME_SCOPE);
		declaration
				.resetScopeBinding(DefaultInterpreterScriptContext.DIALOG_SCOPE);
		declaration
				.resetScopeBinding(DefaultInterpreterScriptContext.DOCUMENT_SCOPE);
	}

	void resetDialogScope() {
		declaration
				.resetScopeBinding(DefaultInterpreterScriptContext.ANONYME_SCOPE);
		declaration
				.resetScopeBinding(DefaultInterpreterScriptContext.DIALOG_SCOPE);

	}

	private void collectDialogProperty(NodeList nodeList) {
		dialogProperties.clear();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeName().equals("property")) {
				collectProperty(node);
			}
		}
	}

	private void collectProperty(Node node) {
		dialogProperties.put(getNodeAttributeValue(node, "name"),
				getNodeAttributeValue(node, "value"));
	}

	void resetApplicationScope() {
		declaration
				.resetScopeBinding(DefaultInterpreterScriptContext.APPLICATION_SCOPE);
		declaration
				.resetScopeBinding(DefaultInterpreterScriptContext.DOCUMENT_SCOPE);
		declaration
				.resetScopeBinding(DefaultInterpreterScriptContext.DIALOG_SCOPE);
		declaration
				.resetScopeBinding(DefaultInterpreterScriptContext.ANONYME_SCOPE);

	}

	void utterance(String string, String string2) throws ScriptException,
			IOException, SAXException {
		declaration.evaluateScript("lastresult$[0].utterance =" + string,
				InterpreterScriptContext.APPLICATION_SCOPE);
		declaration.evaluateScript("lastresult$[0].inputmode =" + string2,
				InterpreterScriptContext.APPLICATION_SCOPE);
		try {
			fia.executor.execute(serachItem(context.getSelectedFormItem(),
					"filled"));
		} catch (InterpreterException e) {
			executionHandler(e);
		}
	}

	void setCurrentDialogProperties(Properties currentDialogProperties) {
		this.dialogProperties = currentDialogProperties;
	}

	// FIXME: add well management
	Properties getCurrentDialogProperties() {
		collectDialogProperty(context.getSelectedFormItem().getParentNode()
				.getChildNodes());
		System.err.println(dialogProperties);
		return dialogProperties;
	}

	void maxTimeDisconnect() throws ScriptException, IOException, SAXException {
		try {
			setTransferResultAndExecute("'maxtime_disconnect'");
		} catch (InterpreterException e) {
			executionHandler(e);
		}
	}

	void destinationBusy() throws ScriptException, IOException, SAXException {
		try {
			setTransferResultAndExecute("'busy'");
		} catch (InterpreterException e) {
			executionHandler(e);
		}
	}

	void networkBusy() throws ScriptException, SAXException, IOException {
		try {
			setTransferResultAndExecute("'network_busy'");
		} catch (InterpreterException e) {
			executionHandler(e);
		}
	}

	boolean raccrochage() {
		return hangup;
	}

	public WIPContext getContext() {
		return context;
	}
}