package cudl.old;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptException;
import javax.xml.parsers.ParserConfigurationException;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

import cudl.Interpreter;
import cudl.Prompt;

public class InterpreterTest extends TestCase {

	private String url;
	private Interpreter interpreter;

	@Before
	public void setUp() throws IOException {
		url = "file:" + new File(".").getCanonicalPath() + "/test/docVxml/oldtestfile/";
	}

	@Test
	public void testCallServiceNavigateUntilNextInteractionAndCollectsNavigationTraces() throws IOException, ScriptException,
			ParserConfigurationException, SAXException {
		List<String> expectedLogs = new ArrayList<String>();
		expectedLogs.add("LOG PHASE init");
		expectedLogs.add("new call");
		expectedLogs.add("LOG PHASE relai");
		expectedLogs.add("info:+@_relai.enter");
		expectedLogs.add("LOG PHASE interaction");

		List<String> expectedStats = new ArrayList<String>();
		expectedStats.add("new call");

		List<Prompt> expectedPrompts = new ArrayList<Prompt>();
		Prompt expectedPrompt;

		expectedPrompt = new Prompt();
		expectedPrompt.timeout = "300ms";
		expectedPrompt.bargein = "false";
		expectedPrompt.tts = "Pour avoir des informations détaillées sur ce tarif, dites : « tarif ».";
		expectedPrompts.add(expectedPrompt);

		expectedPrompt = new Prompt();
		expectedPrompt.timeout = "100ms";
		expectedPrompt.bargein = "false";
		expectedPrompt.audio = "file:///usr2/sons/3900sonsappli/AT_REROUTAGE_SAUI.wav";
		expectedPrompts.add(expectedPrompt);

		expectedPrompt = new Prompt();
		expectedPrompt.timeout = "200ms";
		expectedPrompt.bargein = "true";
		expectedPrompt.audio = "/ROOT/prompts/WAV/ACCUEIL_CHOIX_TARIFS.wav";
		expectedPrompt.tts = "Pour avoir des informations détaillées sur ce tarif, dites : « tarif ».";
		expectedPrompts.add(expectedPrompt);

		interpreter = new Interpreter(url + "VxmlGlobalServletService");
		interpreter.start();

		System.err.println(expectedLogs);
		System.err.println(interpreter.getLogs());
		assertEquals(expectedLogs, interpreter.getLogs());
		assertEquals(expectedStats, interpreter.getLogsWithLabel("stats"));

		System.err.println(expectedPrompts);
		System.err.println(interpreter.getPrompts());
		assertEquals(expectedPrompts, interpreter.getPrompts());
		assertFalse(interpreter.hungup());
	}

	@Test
	public void testNoInputAndNoMatchNavigatesUntilNextInteraction() throws IOException, ScriptException,
			ParserConfigurationException, SAXException {
		List<String> expectedLogs = new ArrayList<String>();
		expectedLogs.add("LOG PHASE init");
		expectedLogs.add("new call");
		expectedLogs.add("LOG PHASE relai");
		expectedLogs.add("info:+@_relai.enter");
		expectedLogs.add("LOG PHASE interaction");
		expectedLogs.add("LOG PHASE silence");
		expectedLogs.add("LOG PHASE interaction");
		expectedLogs.add("LOG PHASE incompris");
		expectedLogs.add("LOG PHASE interaction");

		interpreter = new Interpreter(url + "VxmlGlobalServletService");
		interpreter.start();
		interpreter.noInput();
		interpreter.noMatch();

		System.err.println(expectedLogs);
		System.err.println(interpreter.getLogs());
		assertEquals(expectedLogs, interpreter.getLogs());
	}

	@Test
	public void testNavigationCollectsTransfertInformationButDoNotTransfer() throws IOException, ScriptException,
			ParserConfigurationException, SAXException {

		List<String> expectedLogs = new ArrayList<String>();
		expectedLogs.add("LOG PHASE init");
		expectedLogs.add("LOG PHASE transfert");

		interpreter = new Interpreter(url + "transfer/VxmlGlobalServletService");
		interpreter.start();

		assertEquals("sup:4700810C810106830783105506911808", interpreter.getTranferDestination());

		assertFalse(interpreter.getLogs().isEmpty());
		assertEquals(expectedLogs, interpreter.getLogs());
		assertFalse(interpreter.hungup());
	}

	@Test
	public void testTransferOKSimulatesTransferOnTransferPage() throws IOException, ScriptException, ParserConfigurationException,
			SAXException {

		List<String> expectedLogs = new ArrayList<String>();
		expectedLogs.add("LOG PHASE init");
		expectedLogs.add("LOG PHASE transfert");
		expectedLogs.add("LOG PHASE fin transfert");

		interpreter = new Interpreter(url + "transfer/VxmlGlobalServletService");
		interpreter.start();
		interpreter.blindTransferSuccess();

		assertEquals("sup:4700810C810106830783105506911808", interpreter.getTranferDestination());
		// si
		// transfert
		// alors il
		// n'y
		// a pas hungup
		assertFalse(interpreter.getLogs().isEmpty());
		assertEquals(expectedLogs, interpreter.getLogs());
		assertTrue(interpreter.hungup());
	}

	@Test
	public void testTransferKOSimulatesBadTransferOnTransferPage() throws IOException, ScriptException, ParserConfigurationException,
			SAXException {
		List<String> expectedLogs = new ArrayList<String>();
		expectedLogs.add("LOG PHASE init");
		expectedLogs.add("LOG PHASE transfert");
		expectedLogs.add("LOG PHASE fin transfert KO");

		interpreter = new Interpreter(url + "transfer/VxmlGlobalServletService");
		interpreter.start();
		interpreter.noAnswer();

		assertEquals("sup:4700810C810106830783105506911808", interpreter.getTranferDestination());
		assertFalse(interpreter.getLogs().isEmpty());
		assertEquals(expectedLogs, interpreter.getLogs());
	}
}
