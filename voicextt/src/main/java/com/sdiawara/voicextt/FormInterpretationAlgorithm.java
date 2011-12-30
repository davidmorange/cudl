package com.sdiawara.voicextt;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import org.mozilla.javascript.Undefined;

import com.sdiawara.voicextt.exception.GotoException;
import com.sdiawara.voicextt.exception.VoiceXTTException;
import com.sdiawara.voicextt.node.Audio;
import com.sdiawara.voicextt.node.Block;
import com.sdiawara.voicextt.node.Field;
import com.sdiawara.voicextt.node.Initial;
import com.sdiawara.voicextt.node.Prompt;
import com.sdiawara.voicextt.node.Record;
import com.sdiawara.voicextt.node.Script;
import com.sdiawara.voicextt.node.Subdialog;
import com.sdiawara.voicextt.node.Transfer;
import com.sdiawara.voicextt.node.Value;
import com.sdiawara.voicextt.node.Var;
import com.sdiawara.voicextt.node.VoiceXmlNode;
import com.sdiawara.voicextt.script.Scripting;

public class FormInterpretationAlgorithm implements FormItemVisitor, Runnable {
	private final VoiceXmlNode currentDialog;
	private final Scripting scripting;
	private String nextItem;
	private boolean lastIterationReprompt;
	private boolean dialogChanged;
	private Queue<VoiceXmlNode> promptQueue;
	private VoiceXmlNode selectedFormItem;
	private final SystemOutput outPut;
	private Executor executor;
	private final UserInput userInput;

	public FormInterpretationAlgorithm(VoiceXmlNode dialog, Scripting scripting) {
		this(dialog, scripting, null, null);
	}

	public FormInterpretationAlgorithm(VoiceXmlNode dialog, Scripting scripting, SystemOutput outPut,
			UserInput userInput) {
		this.currentDialog = dialog;
		this.outPut = outPut;
		this.scripting = scripting;
		this.userInput = userInput;
		System.err.println(outPut);
		this.executor = new Executor(scripting, this.outPut);
		this.promptQueue = new LinkedList<VoiceXmlNode>();
	}

	public void initialize() {
		for (VoiceXmlNode formChild : currentDialog.getChilds()) {
			if (formChild instanceof FormItem) {
				String name = formChild.getAttribute("name");
				String expr = formChild.getAttribute("expr");
				name = ((name == null) ? formChild.getNodeName() + "_generate_name" : name);
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

	public void visit(Block block) throws VoiceXTTException {
		scripting.set(block.getAttribute("name"), "true");
		List<VoiceXmlNode> childs = block.getChilds();
		for (VoiceXmlNode voiceXmlNode : childs) {
			executor.execute(voiceXmlNode);
		}
	}

	public void visit(Field field) throws VoiceXTTException {
		this.playPrompt();
		while (userInput.getInput() == null) {
			userInput.readData();
		}
	}

	@SuppressWarnings("unused")
	private VoiceXmlNode serachFilled(Field field) {
		List<VoiceXmlNode> childs = field.getChilds();
		for (VoiceXmlNode voiceXmlNode : childs) {
			if (voiceXmlNode.getNodeName().equals("filled")) {
				return voiceXmlNode;
			}
		}
		return null;
	}

	private void playPrompt() throws VoiceXTTException {
		while (!promptQueue.isEmpty()) {
			VoiceXmlNode remove = promptQueue.remove();
			executor.execute(remove);
		}
	}

	public void visit(Subdialog subdialog) {
		throw new RuntimeException("implement subdialog visit");
	}

	public void visit(Transfer transfer) {
		throw new RuntimeException("implement transfer visit");
	}

	public void visit(Record Record) {
		throw new RuntimeException("implement Record visit");
	}

	public void visit(Initial Initial) {
		throw new RuntimeException("implement Initial visit");
	}

	public void visit(com.sdiawara.voicextt.node.Object object) {
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

	public void collect() {
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
			((FormItem) selectedFormItem).accept(this);
		} catch (GotoException e) {
				setNextItem(e.getGoto().getNextItem());
		} catch (VoiceXTTException e) {
			e.printStackTrace();
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
		for (VoiceXmlNode formItem : currentDialog.getChilds()) {
			if (getNextItem().equals(formItem.getAttribute("name"))) {
				return formItem;
			}
		}

		return null;
	}

	private VoiceXmlNode getFirstUndefinedFormItem() {
		for (VoiceXmlNode formItem : currentDialog.getChilds()) {
			if (Undefined.instance.equals(scripting.get(formItem.getAttribute("name")))) {
				if (formItem instanceof FormItem) {
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

	public void run() {
		initialize();
		selectedFormItem = select();
		collect();
	}
}
