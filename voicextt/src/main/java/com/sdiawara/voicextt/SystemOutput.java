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

	public void addLog(String log) {
		logs.add(log);
	}

	public List<String> getLogs(String... labels) {
		if (labels.length == 0)
			return logs;

		List<String> labeledLog = new ArrayList<String>();
		for (String log : logs) {
			for (String label : labels) {
				if (log.contains("[" + label + "]")) {
					labeledLog.add(log);
				}
			}
		}
		return labeledLog;
	}
}
