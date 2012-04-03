package cudl;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.mozilla.javascript.Undefined;

import cudl.node.Audio;
import cudl.node.Block;
import cudl.node.Field;
import cudl.node.Filled;
import cudl.node.Goto;
import cudl.node.Initial;
import cudl.node.Prompt;
import cudl.node.Record;
import cudl.node.Script;
import cudl.node.Subdialog;
import cudl.node.Submit;
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

	public FormInterpretationAlgorithm(VoiceXmlNode dialog, Scripting scripting) {
		this(dialog, scripting, null, null);
	}

	public FormInterpretationAlgorithm(VoiceXmlNode dialog, Scripting scripting, SystemOutput outPut,
			UserInput userInput) {
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
		while (input == null) {
			input = userInput.readData();
			Thread.yield();
		}

		scripting.set(field.getName(), "'" + input + "'");
		for (VoiceXmlNode voiceXmlNode : field.getChilds()) {
			if (voiceXmlNode instanceof Filled) {
				executor.execute(voiceXmlNode);
				return;
			}
		}
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
		throw new RuntimeException("implement subdialog visit");
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
		initialize();
		while (true) {
			selectedFormItem = select();
			if (selectedFormItem == null) {
				return;
			}
			try {
				collect();
			} catch (InterpreterException e) {
				// System.err.println("* " + e + " *" + ((GotoException) e).next
				// + "=next expr="
				// + ((GotoException) e).getGoto().getExpr());
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
