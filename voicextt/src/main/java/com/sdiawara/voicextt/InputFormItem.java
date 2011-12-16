package com.sdiawara.voicextt;

import org.w3c.dom.Node;

public abstract class InputFormItem extends FormItem {
	private PromptCounter promptCounter;

	public InputFormItem(Node node) {
		super(node);
	}

	public PromptCounter getPromptCounter() {
		return promptCounter;
	}

	public void setPromptCounter(PromptCounter promptCounter) {
		this.promptCounter = promptCounter;
	}
}
