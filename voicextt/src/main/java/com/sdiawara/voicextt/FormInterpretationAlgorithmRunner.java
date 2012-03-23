package com.sdiawara.voicextt;

public class FormInterpretationAlgorithmRunner {
	private final FormInterpretationAlgorithm fia;
	private final Speaker speecher;

	public FormInterpretationAlgorithmRunner(FormInterpretationAlgorithm fia, Speaker speecher) {
		this.fia = fia;
		this.speecher = speecher;
	}

	public void start() {
		this.fia.start();
	}

	public void speech(String utterance) {
		try {
			this.speecher.setUtterance(utterance);
			this.speecher.start();
			this.fia.join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

}
