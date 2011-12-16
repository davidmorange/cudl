package com.sdiawara.voicextt.script;

import java.util.ArrayList;
import java.util.List;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class Scripting {
	private Context context;
	private ScriptableObject sessionScope;
	private ScriptableObject currentScope;
	private int currentScopeIndex = 0;
	@SuppressWarnings("serial")
	private List<String> scopesNames = new ArrayList<String>() {
		{
			add("application");
			add("document");
			add("dialog");
		}
	};

	public Scripting() {
		context = ContextFactory.getGlobal().enterContext();
		sessionScope = context.initStandardObjects();
		sessionScope.put("session", sessionScope, sessionScope);
		currentScope = sessionScope;
	}

	public void put(String name, String value) {
		currentScope.put(name, currentScope, eval(value));
	}

	public void set(String name, String value) {
		context = ContextFactory.getGlobal().enterContext();
		try {
			context.evaluateString(currentScope, name, name, 1, null);
			Scriptable declarationScope = searchDeclarationScope(name);
			declarationScope.put(name, declarationScope, eval(value));
		} finally {
			Context.exit();
		}
	}

	public Object get(String name) {
		context = ContextFactory.getGlobal().enterContext();
		try {
			return context.evaluateString(currentScope, name, name, 1, null);
		} finally {
			Context.exit();
		}
	}

	public Object eval(String script) {
		context = ContextFactory.getGlobal().enterContext();
		try {
			return context.evaluateString(currentScope, script, script, 1, null);
		} finally {
			Context.exit();
		}
	}

	public void enterScope() {
		currentScope = createScope(getNextScopeName(), currentScope);
	}

	public void exitScope() {
		if (currentScopeIndex > 0) {
			currentScope = (ScriptableObject) currentScope.getParentScope();
			currentScopeIndex--;
		}
	}

	@SuppressWarnings("unused")
	private ScriptableObject createScope(ScriptableObject parentScope) {
		ScriptableObject newScope = context.initStandardObjects();
		newScope.setParentScope(parentScope);
		return newScope;
	}

	private ScriptableObject createScope(String scopeName, Scriptable parentScope) {
		ScriptableObject newScope = context.initStandardObjects();
		newScope.put(scopeName, newScope, newScope);
		newScope.setParentScope(parentScope);
		return newScope;
	}

	private String getNextScopeName() {
		String scopeName = null;
		if (currentScopeIndex < scopesNames.size()) {
			scopeName = scopesNames.get(currentScopeIndex);
			currentScopeIndex++;
		}
		return scopeName;
	}

	private Scriptable searchDeclarationScope(String name) {
		Scriptable declarationScope = currentScope;
		while (declarationScope != null && !declarationScope.has(name, declarationScope)) {
			declarationScope = declarationScope.getParentScope();
		}
		return declarationScope;
	}
}
