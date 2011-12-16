package com.sdiawara.voicextt;

public class Speaker implements Runnable {

	private final UserInput userInput;
	private String utterance;

	public Speaker(UserInput userInput) {
		this.userInput = userInput;
	}

	public void run() {
		userInput.setInput(utterance);
	}

	public void setUtterance(String utterance) {
		this.utterance = utterance;
	}
}
