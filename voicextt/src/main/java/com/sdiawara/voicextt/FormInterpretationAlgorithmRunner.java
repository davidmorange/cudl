package com.sdiawara.voicextt;

public class FormInterpretationAlgorithmRunner {
	private final Thread fiaThread;
	private final Thread speecherThread;
	private final Speaker speecher;

	public FormInterpretationAlgorithmRunner(FormInterpretationAlgorithm fia, Speaker speecher) {
		this.speecher = speecher;
		this.fiaThread = new Thread(fia);
		this.speecherThread = new Thread(this.speecher);
	}

	public void start() {
		getFiaThread().start();
	}

	public void speech(String utterance) {
		speecher.setUtterance(utterance);
		getSpeecherThread().start();
		try {
			getSpeecherThread().join();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	public Thread getFiaThread() {
		return fiaThread;
	}

	public Thread getSpeecherThread() {
		return speecherThread;
	}
}
