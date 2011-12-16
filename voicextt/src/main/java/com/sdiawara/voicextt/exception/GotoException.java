package com.sdiawara.voicextt.exception;

public class GotoException extends VoiceXTTException {
	private static final long serialVersionUID = 564001111649829928L;
	private final String nextItem;

	public GotoException(String nextItem) {
		this.nextItem = nextItem;
	}

	public String getNextItem() {
		return nextItem;
	}
}
