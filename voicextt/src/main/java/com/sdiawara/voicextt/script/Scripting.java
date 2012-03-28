package com.sdiawara.voicextt.script;

import java.util.Stack;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class Scripting {
	private Context context;
	private final String[] scopesName = { "session", "application", "document", "dialog" };
	private Stack<ScriptableObject> scopes = new Stack<ScriptableObject>();
	private ScriptableObject lastScopeCreate;
	private Stack<ScriptableObject> exitedScopes = new Stack<ScriptableObject>();

	public Scripting() {
		context = ContextFactory.getGlobal().enterContext();
		initializeScopes();
		backToSessionScope();
	}

	public void put(String name, String value) {
		scopes.peek().put(name, scopes.peek(), eval(value));
	}

	public void set(String name, String value) {
		context = ContextFactory.getGlobal().enterContext();
		context.evaluateString(scopes.peek(), name, name, 1, null);
		Scriptable declarationScope = searchDeclarationScope(name);
		declarationScope.put(name, declarationScope, eval(value));
	}

	public Object get(String name) {
		context = ContextFactory.getGlobal().enterContext();
		return context.evaluateString(scopes.peek(), name, name, 1, null);
	}

	public Object eval(String script) {
		context = ContextFactory.getGlobal().enterContext();
		ScriptableObject peek = scopes.peek();
		return context.evaluateString(peek, script, script, 1, null);
	}

	public void enterScope() {
		if (!exitedScopes.isEmpty()) {
			scopes.push(exitedScopes.pop());
		}
	}

	public void exitScope() {
		if (scopes.size() > 1) {
			exitedScopes.push(scopes.pop());
		}
	}

	@SuppressWarnings("unused")
	private ScriptableObject createScope(ScriptableObject parentScope) {
		ScriptableObject newScope = context.initStandardObjects();
		newScope.setParentScope(parentScope);
		return newScope;
	}

	private Scriptable searchDeclarationScope(String name) {
		Scriptable declarationScope = scopes.peek();
		while (declarationScope != null && !declarationScope.has(name, declarationScope)) {
			declarationScope = declarationScope.getParentScope();
		}
		return declarationScope;
	}

	private void backToSessionScope() {
		exitScope();
		exitScope();
		exitScope();
		exitScope();
	}

	private void initializeScopes() {
		ScriptableObject scope;
		for (String name : scopesName) {
			scope = context.initStandardObjects();
			scope.put(name, scope, scope);
			scopes.push(scope);
			if (lastScopeCreate != null) {
				scope.setParentScope(lastScopeCreate);
			}
			lastScopeCreate = scope;
		}
		scope = context.initStandardObjects();
		scopes.push(scope);
		scope.setParentScope(lastScopeCreate);

	}
}
