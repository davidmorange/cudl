package com.sdiawara.voicextt;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import com.sdiawara.voicextt.exception.GotoException;
import com.sdiawara.voicextt.exception.SubmitException;
import com.sdiawara.voicextt.exception.VoiceXTTException;
import com.sdiawara.voicextt.node.Assign;
import com.sdiawara.voicextt.node.Audio;
import com.sdiawara.voicextt.node.Clear;
import com.sdiawara.voicextt.node.Disconnect;
import com.sdiawara.voicextt.node.Else;
import com.sdiawara.voicextt.node.Elseif;
import com.sdiawara.voicextt.node.Exit;
import com.sdiawara.voicextt.node.Filled;
import com.sdiawara.voicextt.node.Goto;
import com.sdiawara.voicextt.node.If;
import com.sdiawara.voicextt.node.Log;
import com.sdiawara.voicextt.node.Prompt;
import com.sdiawara.voicextt.node.Return;
import com.sdiawara.voicextt.node.Submit;
import com.sdiawara.voicextt.node.Text;
import com.sdiawara.voicextt.node.Value;
import com.sdiawara.voicextt.node.Var;
import com.sdiawara.voicextt.node.VoiceXmlNode;
import com.sdiawara.voicextt.script.Scripting;

public class Executor {
	private final Scripting scripting;
	private final SystemOutput voiceXTTOutPut;

	public Executor(Scripting scripting, SystemOutput voiceXTTOutPut) {
		this.scripting = scripting;
		this.voiceXTTOutPut = voiceXTTOutPut;
	}

	public Object execute(VoiceXmlNode child) throws VoiceXTTException {
		try {
			return Executor.class.getMethod("execute", child.getClass()).invoke(this, child);
		} catch (IllegalArgumentException e) {
		} catch (SecurityException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof VoiceXTTException) {
				System.err.println("blah");
				throw (VoiceXTTException) e.getCause();
			}
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("No implementation for Executor.execute("
					+ child.getClass().getSimpleName() + " " + child.getNodeName() + ")");
		}
		return null;
	}

	public Object execute(Goto goto1) throws VoiceXTTException {
		throw new GotoException(goto1);
	}

	public Object execute(Exit Exit) throws VoiceXTTException {
		throw new RuntimeException("implement Exit executor");
	}

	public Object execute(Log log) throws VoiceXTTException {
		String debug = "";
		String label = log.getLabel();
		String expr = log.getExpr();

		if (label != null) {
			debug += "[" + label + "]";
		}

		if (expr != null) {
			debug += scripting.eval(expr);
		}

		debug += " " + log.getTextContent();
		voiceXTTOutPut.addLog(debug.trim());
		return null;
	}

	public Object execute(Return return1) throws VoiceXTTException {
		throw new RuntimeException("implement Return executor");
	}

	public Object execute(Disconnect disconnect) throws VoiceXTTException {
		throw new RuntimeException("implement Disconnect executor");
	}

	public Object execute(Submit submit) throws VoiceXTTException {
		throw new SubmitException(submit);
	}

	public Object execute(Audio audio) {
		return audio.getTextContent();
	}

	public Object execute(Var var) {
		String name = var.getAttribute("name");
		String expr = var.getAttribute("expr");
		scripting.put(name, expr == null ? "undefined" : expr);
		return null;
	}

	public Object execute(Assign assign) {
		String name = assign.getAttribute("name");
		String expr = assign.getAttribute("expr");
		scripting.set(name, expr);
		return null;
	}

	public Object execute(Clear clear) {
		String nameList = clear.getAttribute("namelist");
		StringTokenizer tokenizer = new StringTokenizer(nameList, " ");
		while (tokenizer.hasMoreElements()) {
			String name = (String) tokenizer.nextElement();
			scripting.set(name, "undefined");
		}
		return null;
	}

	public Object execute(Text text) {
		String value = text.getValue().trim();
		if (value.isEmpty()) {
			return "";
		}
		return value;
	}

	public Object execute(Value value) {
		return scripting.eval(value.getExpr());
	}

	public Object execute(Prompt prompt) throws VoiceXTTException {
		String tts = "";
		for (VoiceXmlNode voiceXmlNode : prompt.getChilds()) {
			tts += " " + execute(voiceXmlNode);
			tts = tts.trim();
		}

		voiceXTTOutPut.addTTS(tts.trim());
		return null;
	}

	public Object execute(Filled filled) throws VoiceXTTException {
		for (VoiceXmlNode voiceXmlNode : filled.getChilds()) {
			execute(voiceXmlNode);
		}
		return null;
	}

	public void execute(If if1) throws VoiceXTTException {
		String cond = if1.getCond();

		String string = scripting.eval(cond).toString();
		System.err.println("cond = " + string);

		if (Boolean.parseBoolean(string)) {
			execute(getInTrueChilds(if1));
		} else {
			execute(getInFalseChilds(if1));
		}

	}

	private List<VoiceXmlNode> getInFalseChilds(If if1) {
		List<VoiceXmlNode> childsFalse = new ArrayList<VoiceXmlNode>();
		int nbElseOrElseif = 0;

		for (VoiceXmlNode voiceXmlNode : if1.getChilds()) {
			boolean el_se = voiceXmlNode instanceof Else;
			boolean elseif = voiceXmlNode instanceof Elseif;
			if (el_se
					|| (elseif && Boolean.parseBoolean(scripting.eval(voiceXmlNode.getAttribute("cond"))
							.toString()))) {
				nbElseOrElseif++;
				if (nbElseOrElseif == 2)
					break;
				continue;
			}
			if (nbElseOrElseif == 1) {
				childsFalse.add(voiceXmlNode);
			}
		}
		return childsFalse;
	}

	private List<VoiceXmlNode> getInTrueChilds(If if1) {
		List<VoiceXmlNode> childsTrue = new ArrayList<VoiceXmlNode>();

		for (VoiceXmlNode voiceXmlNode : if1.getChilds()) {
			boolean el_se = voiceXmlNode instanceof Else;
			boolean elseif = voiceXmlNode instanceof Elseif;
			if (el_se || elseif)
				break;

			childsTrue.add(voiceXmlNode);
		}
		return childsTrue;
	}

	private void execute(List<VoiceXmlNode> voiceXmlNodes) throws VoiceXTTException {
		for (VoiceXmlNode voiceXmlNode : voiceXmlNodes) {
			try {
				execute(voiceXmlNode);
			} catch (Throwable e) {
				if (e instanceof VoiceXTTException) {
					throw (VoiceXTTException) e;
				}
			}
		}
	}
}
