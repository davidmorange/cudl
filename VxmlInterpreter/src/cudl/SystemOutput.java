package cudl;

import java.util.ArrayList;
import java.util.List;

import cudl.node.VoiceXmlNode;

public class SystemOutput {
	private List<String> logs = new ArrayList<String>();
	private List<cudl.Prompt> prompts = new ArrayList<cudl.Prompt>();
	private List<VoiceXmlNode> activedGrammars = new ArrayList<VoiceXmlNode>();
	private String transfertDestination;

	public void addLog(String log) {
		logs.add(log);
	}

	public List<String> getLogs(String... labels) {
		if (labels.length == 0) {
			return getUnLabledLogs();
		}

		return getLabledLogs(labels);
	}

	private List<String> getUnLabledLogs() {
		List<String> ulogs = new ArrayList<String>();
		for (String log : logs) {
			ulogs.add(log.replaceFirst("\\[.*\\] ", ""));
		}
		return ulogs;
	}

	private List<String> getLabledLogs(String... labels) {
		List<String> labeledLog = new ArrayList<String>();
		for (String log : logs) {
			for (String label : labels) {
				String string = "[" + label + "]";
				if (log.contains(string)) {
					labeledLog.add(log.replace(string, "").trim());
				}
			}
		}
		return labeledLog;
	}

	public void addPrompt(cudl.Prompt p) {
		prompts.add(p);
	}

	public List<Prompt> getPrompts() {
		return prompts;
	}

	public void setTransfertDestination(String dest) {
		this.transfertDestination = dest;
	}

	public String getTranferDestination() {
		return transfertDestination;
	}
	
	public String getActiveGrammar() {
		return getActivedGrammars().get(0).toString();
	}

	protected List<VoiceXmlNode> getActivedGrammars() {
		return activedGrammars;
	}

	protected void setActivedGrammars(List<VoiceXmlNode> activedGrammars) {
		this.activedGrammars = activedGrammars;
	}
}
