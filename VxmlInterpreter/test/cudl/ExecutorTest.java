package cudl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mozilla.javascript.Undefined;

import cudl.node.Clear;
import cudl.node.Goto;
import cudl.node.Script;
import cudl.node.Submit;
import cudl.node.Var;
import cudl.script.Scripting;

public class ExecutorTest {

	@Test(expected = SemanticException.class)
	public void clearWithUndefinedVariable() throws SemanticException {
		Clear clear = mock(Clear.class);
		when(clear.getAttribute("namelist")).thenReturn("variable_non_definie");

		Executor executor = new Executor(new Scripting(), null);

		executor.execute(clear);
	}

	@Test
	public void clearWithDeclaredVariable() throws SemanticException {
		Clear clear = mock(Clear.class);
		when(clear.getAttribute("namelist")).thenReturn("variable_definie");
		Scripting scripting = new Scripting();
		scripting.put("variable_definie", "'valeur'");
		Executor executor = new Executor(scripting, null);

		executor.execute(clear);

		assertEquals(Undefined.instance, scripting.get("variable_definie"));
	}

	@Test(expected = SemanticException.class)
	public void varWithScopeName() throws InterpreterException {
		Var var = mock(Var.class);
		when(var.getAttribute("name")).thenReturn("dialog.name");
		Scripting scripting = new Scripting();

		Executor executor = new Executor(scripting, null);
		executor.execute(var);
	}

	@Test
	public void scriptInline() throws InterpreterException {
		Script script = mock(Script.class);
		when(script.getTextContent()).thenReturn("x++");
		Scripting scripting = new Scripting();
		scripting.put("x", "1");
		Executor executor = new Executor(scripting, null);

		executor.execute(script);

		assertEquals(2.0, scripting.get("x"));
	}

	@Test(expected = DocumentChangeException.class)
	public void gotoNext() throws InterpreterException {
		Goto goto1 = mock(Goto.class);
		when(goto1.getNext()).thenReturn("next.vxml");

		new Executor(new Scripting(), null).execute(goto1);
	}

	@Test(expected = FormItemChangeException.class)
	public void gotoNextInSameFile() throws InterpreterException {
		Goto goto1 = mock(Goto.class);
		when(goto1.getNextItem()).thenReturn("text");

		new Executor(new Scripting(), null).execute(goto1);
	}

	@Test(expected = DialogChangeException.class)
	public void gotoNextInSameFileShortForm() throws InterpreterException {
		Goto goto1 = mock(Goto.class);
		when(goto1.getNext()).thenReturn("#text");

		new Executor(new Scripting(), null).execute(goto1);
	}

	@Test(expected = DocumentChangeException.class)
	public void gotoExpr() throws InterpreterException {
		Goto goto1 = mock(Goto.class);
		when(goto1.getExpr()).thenReturn("'next.vxml'");

		new Executor(new Scripting(), null).execute(goto1);
	}

	@Test(expected = FormItemChangeException.class)
	public void gotoNextItem() throws InterpreterException {
		Goto goto1 = mock(Goto.class);
		when(goto1.getNextItem()).thenReturn("next_item");

		new Executor(new Scripting(), null).execute(goto1);
	}

	@Test(expected = FormItemChangeException.class)
	public void gotoExprItem() throws InterpreterException {
		Goto goto1 = mock(Goto.class);
		when(goto1.getExpritem()).thenReturn("'expr_item'");

		new Executor(new Scripting(), null).execute(goto1);
	}

	@Test(expected = DocumentChangeException.class)
	public void submit() throws InterpreterException {
		Submit submit = mock(Submit.class);
		when(submit.getAttribute("next")).thenReturn("next.vxml");

		new Executor(new Scripting(), null).execute(submit);
	}
}
