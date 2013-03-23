package cudl.script;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

public class Scripting {
	private Context context;
	private final String[] scopesName = { "session", "application", "document", "dialog" };
	private Map<String, ScriptableObject> newScopes = new HashMap<String, ScriptableObject>();
	private ScriptableObject currentScope;
	
	public Scripting() {
		context = ContextFactory.getGlobal().enterContext();
		enterScope("session");
	}

	public void put(String name, String value) {
		currentScope.put(name, currentScope, eval(value));
	}

	public void set(String name, String value) {
		context = ContextFactory.getGlobal().enterContext();
		context.evaluateString(currentScope, name, name, 1, null);
		Scriptable declarationScope = searchDeclarationScope(name);
		declarationScope.put(name, declarationScope, eval(value));
	}

	public Object get(String name) {
		context = ContextFactory.getGlobal().enterContext();
		return context.evaluateString(currentScope, name, name, 1, null);
	}

	public Object eval(String script) {
		context = ContextFactory.getGlobal().enterContext();
		Object evaluateString = context.evaluateString(currentScope, script, script, 1, null);
		return evaluateString;
	}

	public void enterScope(String scopeName) {
		if (scopeName != null) {
			System.err.println(scopeName);
			currentScope =  createScope(getParentScope(scopeName));
			currentScope.put(scopeName, currentScope, currentScope);
			newScopes.put(scopeName, currentScope);
		} else {
			ScriptableObject lastScope = newScopes.get("dialog");
			currentScope = createScope(lastScope);
			System.err.println("anonyme");
		}
	}

	private ScriptableObject getParentScope(String scopeName) {
		int i = Arrays.asList(scopesName).indexOf(scopeName);
		if ((i - 1 ) >= 0) {
			 return newScopes.get(scopesName[i - 1]);
		}
		return null;
	}

	private ScriptableObject createScope(ScriptableObject parentScope) {
		ScriptableObject newScope = context.initStandardObjects();
		newScope.setParentScope(parentScope);
		return newScope;
	}

	private Scriptable searchDeclarationScope(String name) {
		Scriptable declarationScope = currentScope;
		while (declarationScope != null && !declarationScope.has(name, declarationScope)) {
			declarationScope = declarationScope.getParentScope();
		}
		return declarationScope;
	}

//	private void initializeScopes() {
//		ScriptableObject scope;
//		for (String name : scopesName) {
//			scope = context.initStandardObjects();
//			scope.put(name, scope, scope);
//			scopes.push(scope);
//			if (lastScopeCreate != null) {
//				scope.setParentScope(lastScopeCreate);
//			}
//			lastScopeCreate = scope;
//			newScopes.put(name, scope);
//		}
//		scope = context.initStandardObjects();
//		scopes.push(scope);
//		scope.setParentScope(lastScopeCreate);
//
//	}
}
