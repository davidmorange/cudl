package cudl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mozilla.javascript.Undefined;

import cudl.node.Clear;
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
}
