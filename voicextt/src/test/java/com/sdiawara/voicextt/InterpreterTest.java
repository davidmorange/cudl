package com.sdiawara.voicextt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.xml.sax.SAXException;

import com.sdiawara.voicextt.script.Scripting;

public class InterpreterTest {
	String url;

	@Before
	public void setUp() throws IOException {
		url = "file:" + new File(".").getCanonicalPath() + "/src/test/java/vxml/";
	}

	@Test
	public void initializeApplicationVariables() throws ParserConfigurationException, IOException, SAXException {
		Interpreter interpreter = new Interpreter(url + "simple.vxml");
		Scripting scripting = interpreter.interpreterContext.getScripting();
		
		assertTrue(scripting.eval("application") instanceof NativeObject);
		assertTrue(scripting.eval("application.lastresult$") instanceof NativeArray);
		scripting.enterScope();
		assertTrue(scripting.eval("application.lastresult$[0]") instanceof NativeObject);
		assertEquals(1.0, scripting.eval("application.lastresult$[0].confidence"));
		assertEquals(1.0, scripting.eval("application.lastresult$[0].confidence"));
	}

	@Test
	public void testRegularScenario() throws ParserConfigurationException, IOException, SAXException {
		Interpreter interpreter = new Interpreter(url + "simple.vxml");
		interpreter.start();

		assertEquals("simple prompt in block 0", interpreter.getPrompts().get(0));
		assertEquals("simple prompt in block 1", interpreter.getPrompts().get(1));
		assertEquals("simple prompt in block 2", interpreter.getPrompts().get(2));
		assertEquals("simple prompt in block 3", interpreter.getPrompts().get(3));
		assertEquals("simple prompt in block 4", interpreter.getPrompts().get(4));
		assertEquals("simple prompt in block 5", interpreter.getPrompts().get(5));
		assertEquals("simple prompt in block 6", interpreter.getPrompts().get(6));
		assertEquals("simple prompt in another form 0", interpreter.getPrompts().get(7));
		assertEquals("simple prompt in another form 1", interpreter.getPrompts().get(8));
		assertEquals("simple prompt in another form 2", interpreter.getPrompts().get(9));
		assertEquals("simple prompt in another form 3", interpreter.getPrompts().get(10));
		assertEquals("simple prompt in another form 4", interpreter.getPrompts().get(11));
		assertEquals("simple prompt in another form 5", interpreter.getPrompts().get(12));
		assertEquals("simple prompt in another form 6", interpreter.getPrompts().get(13));
		assertEquals("simple prompt in block final", interpreter.getPrompts().get(14));

	}

	@Test
	public void testRegularScenarioWithInteraction() throws ParserConfigurationException, IOException,
			SAXException {
		Interpreter interpreter = new Interpreter(url + "simpleTalk.vxml");
		interpreter.start();

		interpreter.talk("blabla");
		interpreter.talk("toto");

		assertEquals(3, interpreter.getPrompts().size());
		assertEquals("you say blabla", interpreter.getPrompts().get(0));
		assertEquals("hello toto", interpreter.getPrompts().get(1));
		assertEquals("Welcome to menu three", interpreter.getPrompts().get(2));
	}
}
