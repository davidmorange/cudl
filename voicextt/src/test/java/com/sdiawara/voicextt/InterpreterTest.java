package com.sdiawara.voicextt;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

public class InterpreterTest {
	String url;

	@Before
	public void setUp() throws IOException {
		url = "file:" + new File(".").getCanonicalPath() + "/src/test/java/vxml/";
	}

	@Test
	public void testSimple() throws ParserConfigurationException, IOException, SAXException {
		Interpreter interpreter = new Interpreter(url + "simple.vxml");
		interpreter.start();
		
		assertEquals("simple prompt in block 0", interpreter.getPrompts().get(0));
		assertEquals("simple prompt in block 1", interpreter.getPrompts().get(1));
		assertEquals("simple prompt in block 2", interpreter.getPrompts().get(2));
		assertEquals("simple prompt in block 3", interpreter.getPrompts().get(3));	
		assertEquals("simple prompt in block 4", interpreter.getPrompts().get(4));
		assertEquals("simple prompt in block 5", interpreter.getPrompts().get(5));
		//assertEquals("simple prompt in another form", interpreter.getPrompts().get(6));
	}
}
