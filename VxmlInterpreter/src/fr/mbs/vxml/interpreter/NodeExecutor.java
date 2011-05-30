package fr.mbs.vxml.interpreter;

import java.io.IOException;

import javax.script.ScriptException;

import org.w3c.dom.Node;

import fr.mbs.vxml.interpreter.execption.InterpreterException;

public interface NodeExecutor {
	public void execute(Node node) throws InterpreterException,
			ScriptException, IOException;
}
