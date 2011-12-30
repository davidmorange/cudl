package com.sdiawara.voicextt.exception;

import com.sdiawara.voicextt.node.Submit;

public class SubmitException extends VoiceXTTException {
	private static final long serialVersionUID = 1882706949174306527L;
	private final Submit submit;

	public SubmitException(Submit submit) {
		this.submit = submit;
	}
}
