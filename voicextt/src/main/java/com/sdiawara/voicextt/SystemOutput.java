package com.sdiawara.voicextt;

import java.util.ArrayList;
import java.util.List;

public class SystemOutput {
	private List<String> textsToSpeech = new ArrayList<String>();
	private List<String> logs = new ArrayList<String>();

	public List<String> getTTS() {
		return textsToSpeech;
	}

	public void addTTS(String value) {
		textsToSpeech.add(value);
	}

	public void addLog(String expr) {
	}
}

