package com.sdiawara.voicextt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.Undefined;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.sdiawara.voicextt.exception.GotoException;
import com.sdiawara.voicextt.exception.SubmitException;
import com.sdiawara.voicextt.exception.VoiceXTTException;
import com.sdiawara.voicextt.node.Assign;
import com.sdiawara.voicextt.node.Clear;
import com.sdiawara.voicextt.node.Filled;
import com.sdiawara.voicextt.node.Goto;
import com.sdiawara.voicextt.node.If;
import com.sdiawara.voicextt.node.Log;
import com.sdiawara.voicextt.node.Prompt;
import com.sdiawara.voicextt.node.Submit;
import com.sdiawara.voicextt.node.Value;
import com.sdiawara.voicextt.node.Var;
import com.sdiawara.voicextt.script.Scripting;

public class ExecutorTest {
	private DocumentAcces documentAcces;
	private Scripting scripting;
	private Executor executor;
	private SystemOutput voiceXTTOutPut;
	private String url;

	@Before
	public void setUp() throws ParserConfigurationException, IOException {
		documentAcces = new DocumentAcces("test/fia");
		scripting = new Scripting();
		voiceXTTOutPut = new SystemOutput();
		executor = new Executor(scripting, voiceXTTOutPut);
		url = "file:" + new File(".").getCanonicalPath() + "/src/test/java/vxml/executable/";
	}

	@Test
	public void testVarExecutor() throws IOException, SAXException, ParserConfigurationException,
			XPathExpressionException {

		Document document = documentAcces.get(url + "var.vxml", null);
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xpath.compile("vxml/form/var");
		Object evaluate = expr.evaluate(document, XPathConstants.NODESET);

		NodeList childs = (NodeList) evaluate;
		for (int i = 0; i < childs.getLength(); i++) {
			executor.execute(new Var(childs.item(i)));
		}

		assertEquals(Undefined.instance, scripting.get("variable1"));
		assertEquals("toto", scripting.get("variable2"));
	}

	@Test
	public void testAssignExecutor() throws IOException, SAXException, ParserConfigurationException,
			XPathExpressionException {

		Document document = documentAcces.get(url + "assign.vxml", null);

		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xpath.compile("vxml/form/var");
		Object evaluate = expr.evaluate(document, XPathConstants.NODESET);

		NodeList childs = (NodeList) evaluate;
		for (int i = 0; i < childs.getLength(); i++) {
			executor.execute(new Var(childs.item(i)));
		}

		expr = xpath.compile("vxml/form/block/assign");
		Object evaluate2 = expr.evaluate(document, XPathConstants.NODE);
		executor.execute(new Assign((Node) evaluate2));

		assertEquals("tata", scripting.get("variable1"));
		assertEquals("toto", scripting.get("variable2"));
	}

	@Test
	public void testClearExecutor() throws IOException, SAXException, ParserConfigurationException,
			XPathExpressionException {

		Document document = documentAcces.get(url + "clear.vxml", null);
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xpath.compile("vxml/form/var");
		Object evaluate = expr.evaluate(document, XPathConstants.NODESET);

		NodeList childs = (NodeList) evaluate;

		for (int i = 0; i < childs.getLength(); i++) {
			executor.execute(new Var(childs.item(i)));
		}

		expr = xpath.compile("vxml/form/block/clear");
		Object evaluate2 = expr.evaluate(document, XPathConstants.NODE);
		executor.execute(new Clear((Node) evaluate2));

		assertEquals(Undefined.instance, scripting.get("variable1"));
		assertEquals(Undefined.instance, scripting.get("variable2"));
	}

	@Test
	public void testIfExecutor() throws IOException, SAXException, ParserConfigurationException,
			XPathExpressionException {

		Document document = documentAcces.get(url + "if.vxml", null);
		XPath xpath = XPathFactory.newInstance().newXPath();
		XPathExpression expr = xpath.compile("vxml/form/block/if");
		Object evaluate = expr.evaluate(document, XPathConstants.NODESET);

		NodeList childs = (NodeList) evaluate;
		for (int i = 0; i < childs.getLength(); i++) {
			executor.execute(new If(childs.item(i)));
		}

		assertEquals(true, scripting.get("vrai"));
		assertEquals(false, scripting.get("faux"));
		try {
			scripting.eval("fauxx");
			fail();
		} catch (Exception e) {
		}
		assertEquals(false, scripting.get("fausse"));
		assertEquals("vraie", scripting.get("chaine"));
		try {
			scripting.eval("blabla");
			fail();
		} catch (Exception e) {
		}
		try {
			scripting.get("fausse1");
			fail("Value of fausse1 is " + scripting.get("fausse1"));
		} catch (Exception e) {
		}
	}

	@Test
	public void testPromptExecutor() throws IOException, SAXException, ParserConfigurationException,
			XPathExpressionException, VoiceXTTException {
		Document document = documentAcces.get(url + "prompt.vxml", null);

		NodeList childs = document.getElementsByTagName("prompt");

		for (int i = 0; i < childs.getLength(); i++) {
			executor.execute(new Prompt(childs.item(i)));
		}
		
		assertEquals(4, voiceXTTOutPut.getTTS().size());
		assertEquals("Welcome To this application vocal", voiceXTTOutPut.getTTS().get(0));
		assertEquals("This is value text", voiceXTTOutPut.getTTS().get(1));
		assertEquals("This is audio text", voiceXTTOutPut.getTTS().get(2));
		assertEquals("Welcome To this application vocal This is value text This is audio text", voiceXTTOutPut.getTTS().get(3));
	}

	@Test
	public void testFilledExecutor() throws IOException, SAXException, ParserConfigurationException,
			XPathExpressionException {

		Document document = documentAcces.get(url + "filled.vxml", null);

		Executor executor = new Executor(scripting, voiceXTTOutPut);
		NodeList childs = document.getElementsByTagName("filled");

		executor.execute(new Filled(childs.item(0)));

		assertEquals(1, voiceXTTOutPut.getTTS().size());
		assertEquals("Welcome To this application vocal", voiceXTTOutPut.getTTS().get(0));
	}

	@Test(expected = GotoException.class)
	public void testGotoExecutor() throws IOException, SAXException, ParserConfigurationException,
			XPathExpressionException, VoiceXTTException {

		Document document = documentAcces.get(url + "goto.vxml", null);

		executor.execute(new Goto(document.getElementsByTagName("goto").item(0)));
	}

	@Test(expected = SubmitException.class)
	public void testSubmitExecutor() throws IOException, SAXException, ParserConfigurationException,
			XPathExpressionException, VoiceXTTException {

		Document document = documentAcces.get(url + "submit.vxml", null);

		executor.execute(new Submit(document.getElementsByTagName("submit").item(0)));
	}

	@Test
	public void testLogExecutor() throws IOException, SAXException, ParserConfigurationException,
			XPathExpressionException, VoiceXTTException {

		Document document = documentAcces.get(url + "log.vxml", null);

		NodeList childs = document.getElementsByTagName("log");
		for (int i = 0; i < childs.getLength(); i++) {
			executor.execute(new Log(childs.item(i)));
		}
		
		List<String> logs = voiceXTTOutPut.getLogs();
		List<String> labeledLogs = voiceXTTOutPut.getLogs("mylabel");
		assertEquals("simple log", logs.get(0));
		assertEquals("[mylabel] simple log with label", logs.get(1));
		assertEquals("my_expr simple log with expr", logs.get(2));
		assertEquals("[mylabel] simple log with label", labeledLogs.get(0));
	}
	
	@Test
	public void testValueExecutor() throws IOException, SAXException {
		Value value = mock(Value.class);
		when(value.getExpr()).thenReturn("'toto '");
		when(value.isSpeakable()).thenReturn(true);
		
		assertEquals("toto ", executor.execute(value));
	}
}


