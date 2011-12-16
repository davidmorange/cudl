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

		interpreter.waitSpeaker();
		assertEquals("simple prompt in block", interpreter.getPrompts().get(0));
	}
}
