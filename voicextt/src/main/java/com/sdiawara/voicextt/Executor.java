package com.sdiawara.voicextt;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.log4j.Appender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

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
import com.sdiawara.voicextt.node.Prompt;
import com.sdiawara.voicextt.node.Return;
import com.sdiawara.voicextt.node.Submit;
import com.sdiawara.voicextt.node.Text;
import com.sdiawara.voicextt.node.Value;
import com.sdiawara.voicextt.node.Var;
import com.sdiawara.voicextt.node.VoiceXmlNode;
import com.sdiawara.voicextt.script.Scripting;

public class Executor {
	private Logger logger = Logger.getLogger(Executor.class);
	private final Scripting scripting;
	private final SystemOutput voiceXTTOutPut;

	public Executor(Scripting scripting, SystemOutput voiceXTTOutPut) {
		this.scripting = scripting;
		this.voiceXTTOutPut = voiceXTTOutPut;
		Appender newAppender = new ConsoleAppender(new SimpleLayout());
		logger.addAppender(newAppender);
	}

	public void execute(VoiceXmlNode child) throws VoiceXTTException {
		try {
			Executor.class.getMethod("execute", child.getClass()).invoke(this,
					child);
		} catch (IllegalArgumentException e) {
		} catch (SecurityException e) {
		} catch (IllegalAccessException e) {
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof VoiceXTTException)
				throw (VoiceXTTException) e.getCause();
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(
					"No implementation for Executor.execute("
							+ child.getClass().getSimpleName() + " "
							+ child.getNodeName() + ")");
		}
	}

	public void execute(Goto goto1) throws VoiceXTTException {
		throw new GotoException(goto1);
	}

	public void execute(Exit Exit) throws VoiceXTTException {
		throw new RuntimeException("implement Exit executor");
	}

	public void execute(Return return1) throws VoiceXTTException {
		throw new RuntimeException("implement Return executor");
	}

	public void execute(Disconnect disconnect) throws VoiceXTTException {
		throw new RuntimeException("implement Disconnect executor");
	}

	public void execute(Submit submit) throws VoiceXTTException {
		throw new SubmitException();
	}

	public void execute(Audio audio) {
		voiceXTTOutPut.addTTS(audio.getTextContent());
	}

	public void execute(Var var) {
		String name = var.getAttribute("name");
		String expr = var.getAttribute("expr");
		logger.info("[var] " + name + "=" + expr);
		scripting.put(name, expr == null ? "undefined" : expr);
	}

	public void execute(Assign assign) {
		String name = assign.getAttribute("name");
		String expr = assign.getAttribute("expr");
		logger.info("[assign] " + name + "=" + expr);
		scripting.set(name, expr);
	}

	public void execute(Clear clear) {
		String nameList = clear.getAttribute("namelist");
		StringTokenizer tokenizer = new StringTokenizer(nameList, " ");
		logger.info("[Clear] " + nameList);
		while (tokenizer.hasMoreElements()) {
			String name = (String) tokenizer.nextElement();
			scripting.set(name, "undefined");
		}
	}

	public void execute(Text text) {
		String value = text.getValue().trim();
		if (value.isEmpty())
			return;
		if (text.isSpeakable()) {
			voiceXTTOutPut.addTTS(value);
		} else {
			voiceXTTOutPut.addLog(value);
		}
	}

	public void execute(Value value) {
		String expr = scripting.eval(value.getExpr()).toString();
		if (value.isSpeakable()) {
			voiceXTTOutPut.addTTS(expr);
		} else {
			voiceXTTOutPut.addLog(expr);
		}
	}

	public void execute(Prompt prompt) {
		proceduralExecution(prompt);
	}

	public void execute(Filled filled) {
		proceduralExecution(filled);
	}

	private void proceduralExecution(VoiceXmlNode node) {
		List<VoiceXmlNode> childs = node.getChilds();
		for (VoiceXmlNode voiceXmlNode : childs) {
			boolean text = voiceXmlNode instanceof Text;
			boolean value = voiceXmlNode instanceof Value;
			if (text) {
				((Text) voiceXmlNode).setSpeakable(true);
			}
			if (value) {
				((Value) voiceXmlNode).setSpeakable(true);
			}
			try {
				execute(voiceXmlNode);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public void execute(If if1) {
		String cond = if1.getCond();
		if (Boolean.parseBoolean(scripting.eval(cond).toString())) {
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
					|| (elseif && Boolean.parseBoolean(scripting.eval(
							voiceXmlNode.getAttribute("cond")).toString()))) {
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

	private void execute(List<VoiceXmlNode> voiceXmlNodes) {
		for (VoiceXmlNode voiceXmlNode : voiceXmlNodes) {
			try {
				execute(voiceXmlNode);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}
}
