package vxml.interpreter.w3c;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.Test;
import org.xml.sax.SAXException;

import vxml.interpreter.InterpreterContext;
import vxml.interpreter.execption.InterpreterException;

public class W3cInterpreterTest {
	private InterpreterContext interpreterContext;
	private List<String> fileNames = new ArrayList<String>() {
		{
			/**
			 * These test file is from
			 * http://www.w3.org/Voice/2004/vxml-ir/#test_api
			 */

			// The interpreter must be able to freely sequence TTS and audio
			// output.
			add("w3c/a002.txml");

			// The interpreter executes content contained in the block.
			add("w3c/259.txml");

			// The interpreter visits the block when the cond attribute
			// evaluates to true and the form item variable associated with
			// the block is undefined.
			add("w3c/260.txml");

			// The interpreter ignores the block when the form item variable
			// associated with the block is defined via expr.
			add("w3c/261.txml");

			// The interpreter ignores the block when the form item variable
			// associated with the block is set via an assign.
			add("w3c/262.txml");

			// FIA ends when it encounters an <goto>.
			add("w3c/assert111.txml");

			// Using the goto nextitem will force the FIA to immediately
			// transition to the chosen form item.
			// FIXME: restore the original file
			add("w3c/assert154.txml");

			// FIA ends when it encounters an <submit>.
			add("w3c/assert112.txml");

			// FIA ends when it encounters an <exit>.
			add("w3c/assert113.txml");

			// FIA ends when it encounters an <return>.
			// FIXME: Implemeent dialog
			add("w3c/assert114.txml");
		}
	};

	@Test
	public void w3cIRTest() throws SAXException, IOException,
			InterpreterException {
		int count = 0;
		for (Iterator<String> iterator = fileNames.iterator(); iterator
				.hasNext();) {
			String fileName = (String) iterator.next();

			interpreterContext = new InterpreterContext(fileName);
			interpreterContext.launchInterpreter();

			if (!(interpreterContext.interpreter.w3cNodeConfSuite.size() == 1)) {
				System.out.println(count + " tests of " + fileNames.size());
			}
			assertTrue(interpreterContext.interpreter.w3cNodeConfSuite.size() == 1);
			count++;
			System.out.println(fileName + " test pass");
		}
		System.out.println(count + " tests of " + fileNames.size());
	}

	@Test
	public void w3cManualTest() throws SAXException, IOException,
			InterpreterException {
		// If the last main FIA loop did not result in a goto nextitem
		// and there is no form item which is eligible to be visited
		// then an implicit exit is generated.
		
		// Read file
		interpreterContext = new InterpreterContext("w3c/assert165.txml");
		interpreterContext.launchInterpreter();
		
		assertTrue(interpreterContext.interpreter.w3cNodeConfSuite.isEmpty());
	}
}