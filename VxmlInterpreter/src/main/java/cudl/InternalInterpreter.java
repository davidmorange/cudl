//package cudl;
//
//import static cudl.utils.Utils.getNodeAttributeValue;
//import static cudl.utils.Utils.serachItem;
//
//import java.io.IOException;
//import java.util.Properties;
//
//import javax.xml.parsers.ParserConfigurationException;
//
//import org.mozilla.javascript.EcmaError;
//import org.w3c.dom.Node;
//import org.w3c.dom.NodeList;
//import org.xml.sax.SAXException;
//
//import cudl.node.VoiceXmlNode;
//import cudl.script.InterpreterVariableDeclaration;
//
//class InternalInterpreter {
//	public static final int START = 1;
//	public static final int EVENT = 2;
//	public static final int BLIND_TRANSFER_SUCCESSSS = 3;
//	public static final int CONNECTION_DISCONNECT_HANGUP = 4;
//	public static final int NOANSWER = 5;
//	public static final int CALLER_HUNGUP_DURING_TRANSFER = 7;
//	public static final int NETWORK_BUSY = 8;
//	public static final int DESTINATION_BUSY = 9;
//	public static final int MAX_TIME_DISCONNECT = 10;
//	public static final int DESTINATION_HANGUP = 11;
//	public static final int TALK = 12;
//	public static final int DTMF = 13;
//	private final InterpreterContext context;
//	private InterpreterEventHandler ieh;
//	private Properties properties = new Properties();
//	private boolean test;
//
//	InternalInterpreter(InterpreterContext context) throws IOException, SAXException {
//		this.context = context;
//		ieh = new InterpreterEventHandler(context);
//	}
//
//	void interpret(int action, String arg) throws IOException, SAXException, ParserConfigurationException {
//		Node node = context.getCurrentDialog();
//
//		try {
//			VoiceXmlNode dialog;
//			switch (action) {
//			case START:
//				// System.err.println(node);
//				dialog = TagInterpreterFactory.getTagInterpreter(node);
//				if (test) {
//					((FormTag) dialog).setInitVar(false);
//				} else
//					context.getFormItemNames().clear();
//				try {
//					dialog.interpret(context);
//				} catch (EcmaError e) {
//					throw new EventException("error.semantic");
//				}
//				break;
//			case EVENT:
//				ieh.doEvent(arg);
//				break;
//			case BLIND_TRANSFER_SUCCESSSS:
//				context.getDeclaration().evaluateScript("connection.protocol.isdnvn6.transferresult= '0'", InterpreterVariableDeclaration.SESSION_SCOPE);
//				ieh.doEvent("connection.disconnect.transfer");
//				break;
//			case NOANSWER:
//				context.getDeclaration().evaluateScript("connection.protocol.isdnvn6.transferresult= '2'", InterpreterVariableDeclaration.SESSION_SCOPE);
//				setTransferResultAndExecute("'noanswer'");
//				break;
//			case CALLER_HUNGUP_DURING_TRANSFER:
//				setTransferResultAndExecute("'near_end_disconnect'");
//				break;
//			case NETWORK_BUSY:
//				context.getDeclaration().evaluateScript("connection.protocol.isdnvn6.transferresult= '5'", InterpreterVariableDeclaration.SESSION_SCOPE);
//				setTransferResultAndExecute("'network_busy'");
//				break;
//			case DESTINATION_BUSY:
//				setTransferResultAndExecute("'busy'");
//				break;
//			case MAX_TIME_DISCONNECT:
//				setTransferResultAndExecute("'maxtime_disconnect'");
//				break;
//			case DESTINATION_HANGUP:
//				context.getDeclaration().setValue(context.getFormItemNames().get(context.getSelectedFormItem()), "'far_end_disconnect'");
//				test = true;
//				interpret(1, null);
//				break;
//			case TALK:
//				utterance(arg, "'voice'");
//				break;
//			case DTMF:
//				utterance(arg, "'dtmf'");
//				break;
//			}
//		} catch (GotoException e) {
//			// FIXME: this is use to check transition between leaf document and
//			// root document
//			context.lastChangeEvent = "goto";
//			ieh.resetEventCounter();
//			if (e.next != null) {
//				context.buildDocument(e.next);
//				node = context.getCurrentDialog();
//			} else {
//				node = context.getCurrentDialog();
//				context.setNextItemToVisit(e.nextItem);
//			}
//			interpret(1, null);
//		} catch (SubmitException e) {
//			context.lastChangeEvent = "submit";
//			ieh.resetEventCounter();
//			context.buildDocument(e.next);
//			node = context.getCurrentDialog();
//			interpret(1, null);
//		} catch (FilledException e) {
//		} catch (EventException e) {
//			interpret(EVENT, e.type);
//		} catch (TransferException e) {
//			ieh.resetEventCounter();
//		} catch (ReturnException e) {
//			context.setReturnValue(e.event, e.eventexpr, e.namelist);
//		} catch (InterpreterException e) {
//			System.err.println(e);
//		}
//	}
//
//	private void setTransferResultAndExecute(String transferResult) throws InterpreterException, IOException, SAXException,
//			ParserConfigurationException {
//		context.getDeclaration().setValue(context.getFormItemNames().get(context.getSelectedFormItem()), transferResult);
//		FilledTag filled = (FilledTag) TagInterpreterFactory.getTagInterpreter(serachItem(context.getSelectedFormItem(), "filled"));
//		filled.setExecute(true);
//		filled.interpret(context); // FIXME raise disconnect event instead to be
//		// closer to OMS buggy interpretation.
//	}
//
//	private void collectDialogProperty(NodeList nodeList) {
//		properties.clear();
//		for (int i = 0; i < nodeList.getLength(); i++) {
//			Node node = nodeList.item(i);
//			if (node.getNodeName().equals("property")) {
//				collectProperty(node);
//			}
//		}
//	}
//
//	private void collectProperty(Node node) {
//		properties.put(getNodeAttributeValue(node, "name"), getNodeAttributeValue(node, "value"));
//	}
//
//	void utterance(String utterance, String utteranceType) throws IOException, SAXException, ParserConfigurationException, InterpreterException {
//		Node currentDialog = context.getCurrentDialog();
//		if (currentDialog.getNodeName().equals("form")) {
//			context.getDeclaration().evaluateScript("lastresult$[0].utterance =" + utterance,
//					InterpreterVariableDeclaration.APPLICATION_SCOPE);
//			context.getDeclaration().evaluateScript("lastresult$[0].inputmode =" + utteranceType,
//					InterpreterVariableDeclaration.APPLICATION_SCOPE);
//			context.getDeclaration().setValue(context.getFormItemNames().get(context.getSelectedFormItem()), utterance);
//			FilledTag filled = (FilledTag) TagInterpreterFactory.getTagInterpreter(serachItem(context.getSelectedFormItem(), "filled"));
//			filled.setExecute(true);
//			filled.interpret(context);
//		} else {
//			// TODO: Refactor to add choice bailise
//			NodeList childNodes = currentDialog.getChildNodes();
//			int choiceCount = 0;
//			for (int i = 0; i < childNodes.getLength(); i++) {
//				Node child = childNodes.item(i);
//				if (child.getNodeName().equals("choice")) {
//					choiceCount++;
//					try {
//						if (choiceCount == Integer.parseInt(utterance.replaceAll("'", ""))) {
//							String next = getNodeAttributeValue(child, "next");
//							if (null != next)
//								throw new GotoException(next, null);
//							else
//								throw new EventException(getNodeAttributeValue(child, "event"));
//						}
//					} catch (NumberFormatException e) {
//						if (child.getTextContent().contains(utterance.replaceAll("'", ""))) {
//							String next = getNodeAttributeValue(child, "next");
//							if (null != next)
//								throw new GotoException(next, null);
//							else
//								throw new EventException(getNodeAttributeValue(child, "event"));
//						}
//					}
//				}
//			}
//		}
//	}
//
//	Properties getCurrentDialogProperties() {
//		collectDialogProperty(context.getSelectedFormItem().getParentNode().getChildNodes());
//		return properties;
//	}
//
//	public InterpreterContext getContext() {
//		return context;
//	}
//}
