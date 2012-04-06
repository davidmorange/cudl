package cudl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.xml.parsers.ParserConfigurationException;

import org.mozilla.javascript.Undefined;
import org.xml.sax.SAXException;

import cudl.node.Audio;
import cudl.node.Block;
import cudl.node.Choice;
import cudl.node.Field;
import cudl.node.Filled;
import cudl.node.Form;
import cudl.node.Initial;
import cudl.node.Prompt;
import cudl.node.Record;
import cudl.node.Script;
import cudl.node.Subdialog;
import cudl.node.Transfer;
import cudl.node.Value;
import cudl.node.Var;
import cudl.node.VoiceXmlNode;
import cudl.script.Scripting;

public class FormInterpretationAlgorithm extends Thread implements FormItemVisitor {
	private VoiceXmlNode currentDialog;
	private final Scripting scripting;
	private String nextItem;
	private boolean lastIterationReprompt;
	private boolean dialogChanged;
	private Queue promptQueue;
	private VoiceXmlNode selectedFormItem;
	private final SystemOutput outPut;
	private Executor executor;
	private final UserInput userInput;
	private InterpreterException lastException = null;
	private Map<String, Integer> eventCounterMap = new HashMap<String, Integer>();

	public FormInterpretationAlgorithm(VoiceXmlNode dialog, Scripting scripting) {
		this(dialog, scripting, null, null);
	}

	public FormInterpretationAlgorithm(VoiceXmlNode dialog, Scripting scripting, SystemOutput outPut, UserInput userInput) {
		this.setCurrentDialog(dialog);
		this.outPut = outPut;
		this.scripting = scripting;
		this.userInput = userInput;
		this.executor = new Executor(scripting, this.outPut);
		this.promptQueue = new LinkedList<VoiceXmlNode>();
	}

	public void initialize() {
		// scripting.enterScope(); // enter scope dialog
		for (VoiceXmlNode formChild : getCurrentDialog().getChilds()) {
			if (formChild instanceof FormItem) {
				String name = ((FormItem) formChild).getName();
				String expr = formChild.getAttribute("expr");
				expr = ((expr == null) ? "undefined" : expr);
				scripting.put(name, expr);
				if (formChild instanceof InputFormItem) {
					((InputFormItem) formChild).setPromptCounter(new PromptCounter());
				}
			}
			if (formChild instanceof Var) {
				String name = formChild.getAttribute("name");
				String expr = formChild.getAttribute("expr");
				name = ((name == null) ? formChild.getNodeName() + "_generate_name" : name);
				expr = ((expr == null) ? "undefined" : expr);
				scripting.put(name, expr);

			}
			if (formChild instanceof Script) {
				scripting.eval(((Script) formChild).getTextContent());
			}
		}
	}

	@Override
	public void visit(Block block) throws InterpreterException {
		scripting.set(block.getName(), "true");
		List<VoiceXmlNode> childs = block.getChilds();
		for (VoiceXmlNode voiceXmlNode : childs) {
			executor.execute(voiceXmlNode);
		}
	}

	@Override
	public void visit(Field field) throws InterpreterException {
		this.playPrompt();
		String input = userInput.readData();
		while (input == null || !"voice dtmf".contains(input.split("\\$")[0])) {
			if (input != null) {
				String eventType = input.split("\\$")[1];
				InterpreterEventHandler.doEvent(field, executor, eventType, getEventCount(eventType));
				updateEventCount(eventType);
			}
			input = userInput.readData();
			Thread.yield();
		}
		if ("voice dtmf".contains(input.split("\\$")[0])) {
			setUterrance(field, input);
			executeFilled(field);
		}
	}

	private void updateEventCount(String eventType) {
		eventCounterMap.put(eventType, getEventCount(eventType) + 1);
	}

	private int getEventCount(String eventType) {
		Integer integer = eventCounterMap.get(eventType);
		if (integer == null) {
			return 1;
		}
		return integer;
	}

	private void executeFilled(Field field) throws InterpreterException {
		for (VoiceXmlNode voiceXmlNode : field.getChilds()) {
			if (voiceXmlNode instanceof Filled) {
				executor.execute(voiceXmlNode);
				return;
			}
		}
	}

	private void setUterrance(Field field, String input) {
		String[] split = input.split("\\$");
		String utterance = "'" + split[1] + "'";
		String inputmode = "'" + split[1] + "'";
		scripting.set(field.getName(), utterance);
		scripting.put(field.getName() + "$", "new Object()");
		scripting.eval("application.lastresult$.utterance =" + field.getName() + "$.utterance=" + utterance);
		scripting.eval("application.lastresult$.inputmode =" + field.getName() + "$.inputmode=" + inputmode);
		scripting.eval(field.getName() + "$.confidence=" + 1);
	}

	private void playPrompt() throws InterpreterException {
		Queue<VoiceXmlNode> pNodes = promptQueue;
		for (VoiceXmlNode prompt : pNodes) {
			executor.execute(prompt);
		}
		return;
	}

	@Override
	public void visit(Subdialog subdialog) {
		String srcexpr = subdialog.getAttribute("srcexpr");
		try {
			Interpreter interpreter = new Interpreter("file:test/docVxml/subdialogSrcExpr.vxml"+"#"+scripting.eval(srcexpr));
			interpreter.start();
			outPut.getPrompts().addAll(interpreter.getPrompts());
			List<String> logs = interpreter.getLogs();
			for (String log : logs) {
				outPut.addLog(log);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//throw new RuntimeException("implement subdialog visit");
	}

	@Override
	public void visit(Transfer transfer) {
		throw new RuntimeException("implement transfer visit");
	}

	@Override
	public void visit(Record Record) {
		throw new RuntimeException("implement Record visit");
	}

	@Override
	public void visit(Initial Initial) {
		throw new RuntimeException("implement Initial visit");
	}

	@Override
	public void visit(cudl.node.Object object) {
		throw new RuntimeException("implement object visit");
	}

	public VoiceXmlNode select() {
		if (getNextItem() != null) {
			return getNamedFormItem();
		}

		return getFirstUndefinedFormItem();
	}

	public void setNextItem(String nextItem) {
		this.nextItem = nextItem;
	}

	public void collect() throws InterpreterException {
		// Queue up prompts for the form item.
		if (!lastIterationReprompt || !dialogChanged) {
			if (selectedFormItem instanceof InputFormItem) {
				selectAppropriate();
			}
		}

		// Activate grammars for the form item.
		// if ( the form item is modal )
		// Set the active grammar set to the form item grammars,
		// if any. (Note that some form items, e.g. <block>,
		// cannot have any grammars).
		// else
		// Set the active grammar set to the form item
		// grammars and any grammars scoped to the form,
		// the current document, and the application root
		// document.

		// Execute the form item.
		try {
			setNextItem(null);
			scripting.enterScope();
			((FormItem) selectedFormItem).accept(this);
			scripting.exitScope();
		} catch (GotoException e) {
			String expritem = e.getGoto().getExpritem();
			String nextItem = e.getGoto().getNextItem();
			if (nextItem == null && expritem == null) {
				throw e;
			}
			setNextItem(nextItem);// Item == null ?
									// scripting.eval(expritem).toString() :
									// nextItem);
		} catch (SemanticException e) {
			InterpreterEventHandler.doEvent(e.node, executor, "error.semantic", getEventCount("error.semanic"));
		} catch (InterpreterException e) {
			throw e;
		}
	}

	private void selectAppropriate() {
		List<VoiceXmlNode> childs = selectedFormItem.getChilds();
		for (VoiceXmlNode child : childs) {
			if (child instanceof Prompt || child instanceof Audio || child instanceof Value) {
				promptQueue.add(child);
			}
		}
	}

	private VoiceXmlNode getNamedFormItem() {
		for (VoiceXmlNode formItem : getCurrentDialog().getChilds()) {
			if (formItem instanceof FormItem && getNextItem().equals(((FormItem) formItem).getName())) {
				return formItem;
			}
		}

		return null;
	}

	private VoiceXmlNode getFirstUndefinedFormItem() {
		for (VoiceXmlNode formItem : getCurrentDialog().getChilds()) {
			if (formItem instanceof FormItem) {
				if (Undefined.instance.equals(scripting.get(((FormItem) formItem).getName()))) {
					return formItem;
				}
			}
		}
		return null;
	}

	private String getNextItem() {
		return nextItem;
	}

	public void setSelectedFormItem(VoiceXmlNode select) {
		this.selectedFormItem = select;
	}

	@Override
	public void run() {
		scripting.enterScope(); // in scope dialog
		if (currentDialog instanceof Form) {
			executeForm();
		} else {
			executeMenu();
		}
	}

	private void executeMenu() {
		try {
			for (VoiceXmlNode node : currentDialog.getChilds()) {
				if (node instanceof Prompt) {
					executor.execute(node);
				}
			}

			String input = getInput();
			if (input != null) {
				if ("dtmf".equals(input.split("\\$")[0])) {
					executeChoiceByDtmf(input);
				} else {
					executeChoiceByVoice(input);
				}
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void executeChoiceByVoice(String input) throws InterpreterException {
		for (VoiceXmlNode node : currentDialog.getChilds()) {
			if (node instanceof Choice) {
				if (((Choice) node).getTextContent().contains(input.split("\\$")[1])) {
					executor.execute(node);
				}
			}
		}
	}

	private void executeChoiceByDtmf(String input) throws InterpreterException {
		int i = 1;
		for (VoiceXmlNode node : currentDialog.getChilds()) {
			if (node instanceof Choice) {
				if (i == Integer.parseInt(input.split("\\$")[1])) {
					executor.execute(node);
				}
				i++;
			}
		}
	}

	private String getInput() throws InterpreterException {
		String input = userInput.readData();
		while (input == null || !"voice dtmf".contains(input.split("\\$")[0])) {
			if (input != null) {
				String eventType = input.split("\\$")[1];
				InterpreterEventHandler.doEvent(currentDialog, executor, eventType, getEventCount(eventType));
				updateEventCount(eventType);
			}
			input = userInput.readData();

			Thread.yield();
		}
		return input;
	}

	private void executeForm() {
		initialize();
		while (true) {
			selectedFormItem = select();
			if (selectedFormItem == null) {
				return;
			}
			try {
				collect();
			} catch (ExitException e) {
				// setIshangup
				return;
			} catch (InterpreterException e) {
				throw new RuntimeException(e);
			}
		}
	}

	public InterpreterException getLastException() {
		return lastException;
	}

	public VoiceXmlNode getCurrentDialog() {
		return currentDialog;
	}

	public void setCurrentDialog(VoiceXmlNode currentDialog) {
		this.currentDialog = currentDialog;
	}

}
