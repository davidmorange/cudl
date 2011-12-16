package com.sdiawara.voicextt.script;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.EcmaError;

public class ScriptingTest {
	private Scripting scripting;

	@Before
	public void setUp() {
		scripting = new Scripting();
	}

	@Test(expected = EcmaError.class)
	public void getUndeclaredVariableMakeError() {
		scripting.get("undeclaredVaraiable");
	}

	@Test
	public void declarableVariableAssumeCantUseGetMethodeToAccessItValue() {
		scripting.put("variableName", "'variableValue'");
		scripting.put("variableName2", "2");

		assertEquals("variableValue", scripting.get("variableName"));
		assertEquals(2, scripting.get("variableName2"));
	}

	@Test(expected = EcmaError.class)
	public void evalWithUndeclaredVariableMakeEcmaError() {
		scripting.eval("undeclaredVaraiable");
	}

	@Test
	public void evalWithDeclaredVariableReturnValueOfVariable() {
		scripting.put("variableName", "'variableValue'");
		assertEquals("variableValue", scripting.eval("variableName"));
	}

	@Test(expected = EcmaError.class)
	public void setUndeclaredVariableMakeEcmaError() {
		scripting.set("variableName", "value");
	}

	@Test
	public void setDeclaredVariableWillChangeValue() {
		scripting.put("variableName", "'variableValue'");
		scripting.set("variableName", "'newValue'");
		assertEquals("newValue", scripting.get("variableName"));
	}

	@Test
	public void weCanUseDeclaredVaribleInScript() {
		scripting.put("variableName", "[]");
		scripting.put("variableName1", "[1, 2]");
		assertEquals(0.0, scripting.eval("variableName.length"));
		assertEquals(1.0, scripting.eval("variableName1[0]"));
		assertEquals(2, scripting.eval("var le= variableName1.length-1;variableName1[le]"));
	}

	@Test
	public void sessionScope() {
		scripting.put("variableName", "[]");
		scripting.put("variableName1", "[1, 2]");
		assertEquals(0.0, scripting.eval("session.variableName.length"));
		assertEquals(1.0, scripting.eval("session.variableName1[0]"));
		assertEquals(2, scripting.eval("var le= variableName1.length-1;variableName1[session.le]"));
	}

	@Test
	public void applicationScope() {
		scripting.enterScope();
		scripting.put("variableName", "[]");
		scripting.put("variableName1", "[1, 2]");
		assertEquals(0.0, scripting.eval("application.variableName.length"));
		assertEquals(1.0, scripting.eval("application.variableName1[0]"));
		assertEquals(2, scripting.eval("var le= variableName1.length-1;variableName1[application.le]"));
	}

	@Test
	public void documentScope() {
		scripting.enterScope();
		scripting.enterScope();
		scripting.put("variableName", "[]");
		scripting.put("variableName1", "[1, 2]");
		assertEquals(0.0, scripting.eval("document.variableName.length"));
		assertEquals(1.0, scripting.eval("document.variableName1[0]"));
		assertEquals(2, scripting.eval("var le= variableName1.length-1;variableName1[document.le]"));
	}

	@Test
	public void dialogScope() {
		scripting.enterScope();
		scripting.enterScope();
		scripting.enterScope();
		scripting.put("variableName", "[]");
		scripting.put("variableName1", "[1, 2]");
		assertEquals(0.0, scripting.eval("dialog.variableName.length"));
		assertEquals(1.0, scripting.get("dialog.variableName1[0]"));
		assertEquals(2, scripting.eval("var le= variableName1.length-1;variableName1[dialog.le]"));
	}

	@Test
	public void variableIsAlwaysVisibleInEnclosingScope() {
		scripting.put("variableName", "[]");
		scripting.enterScope();
		assertEquals(0.0, scripting.eval("variableName.length"));
		assertEquals(0.0, scripting.eval("session.variableName.length"));
		scripting.put("variableName", "[1]");
		scripting.enterScope();
		assertEquals(0.0, scripting.eval("session.variableName.length"));
		assertEquals(1.0, scripting.eval("variableName.length"));
		assertEquals(1.0, scripting.eval("application.variableName.length"));
		scripting.put("variableName", "[1,2]");
		scripting.enterScope();
		assertEquals(0.0, scripting.eval("session.variableName.length"));
		assertEquals(1.0, scripting.eval("application.variableName.length"));
		assertEquals(2.0, scripting.eval("document.variableName.length"));
		assertEquals(2.0, scripting.eval("variableName.length"));
		scripting.put("variableName", "[1,2,3]");
		scripting.enterScope();
		assertEquals(0.0, scripting.eval("session.variableName.length"));
		assertEquals(1.0, scripting.eval("application.variableName.length"));
		assertEquals(2.0, scripting.eval("document.variableName.length"));
		assertEquals(3.0, scripting.eval("dialog.variableName.length"));
		scripting.put("variableName", "[1,2,3,4]");
		assertEquals(3.0, scripting.eval("dialog.variableName.length"));
		assertEquals(4.0, scripting.eval("variableName.length"));
		scripting.enterScope();
		scripting.put("variableName", "'anonyme'");
		assertEquals("anonyme", scripting.eval("variableName"));
	}

	@Test
	public void enclosingScopeCanChangeVariableInItOwnerScope() {
		scripting.put("variableName", "[]");
		scripting.enterScope();
		scripting.set("variableName", "[1,2]");
		assertEquals(2.0, scripting.eval("variableName.length"));
		assertEquals(2.0, scripting.eval("session.variableName.length"));
	}

	
	@Test
	public void exitScopeAssumeItVariableItNotVisible() {
		scripting.enterScope();
		scripting.put("applicationVariable", "'appli'");
		scripting.enterScope();
		scripting.put("documentVariable", "'doc'");
		scripting.enterScope();
		scripting.put("dialogVariable", "'dial'");

		assertEquals("dial", scripting.eval("dialogVariable"));
		scripting.exitScope();
		try {
			assertEquals("dial", scripting.eval("dialogVariable"));
			fail("Not clean scope Exited dialog");
		} catch (EcmaError e) {
		}
		assertEquals("doc", scripting.eval("documentVariable"));
		scripting.exitScope();
		try {
			assertEquals("dial", scripting.eval("documentVariable"));
			fail("Not clean scope Exited document");
		} catch (EcmaError e) {
		}
	}
}
