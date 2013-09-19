package cudl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.mozilla.javascript.Undefined;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import cudl.node.Clear;
import cudl.node.Goto;
import cudl.node.If;
import cudl.node.Script;
import cudl.node.Submit;
import cudl.node.Var;
import cudl.node.Voice;
import cudl.script.Scripting;

public class ExecutorTest {

	@Test(expected = SemanticException.class)
	public void clearWithUndefinedVariable() throws SemanticException {
		Clear clear = mock(Clear.class);
		when(clear.getAttribute("namelist")).thenReturn("variable_non_definie");

		Executor executor = new Executor(new Scripting(), null, null);

		executor.execute(clear);
	}

	@Test
	public void clearWithDeclaredVariable() throws SemanticException {
		Clear clear = mock(Clear.class);
		when(clear.getAttribute("namelist")).thenReturn("variable_definie");
		Scripting scripting = new Scripting();
		scripting.put("variable_definie", "'valeur'");
		Executor executor = new Executor(scripting, null, null);

		executor.execute(clear);

		assertEquals(Undefined.instance, scripting.get("variable_definie"));
	}

	@Test(expected = SemanticException.class)
	public void varWithScopeName() throws InterpreterException {
		Var var = mock(Var.class);
		when(var.getAttribute("name")).thenReturn("dialog.name");
		Scripting scripting = new Scripting();

		Executor executor = new Executor(scripting, null, null);
		executor.execute(var);
	}

	@Test
	public void scriptInline() throws InterpreterException {
		Script script = mock(Script.class);
		when(script.getTextContent()).thenReturn("x++");
		Scripting scripting = new Scripting();
		scripting.put("x", "1");
		Executor executor = new Executor(scripting, null, null);

		executor.execute(script);

		assertEquals(2.0, scripting.get("x"));
	}

	@Test
	public void scriptFile() throws InterpreterException,
			ParserConfigurationException {
		Script script = mock(Script.class);
		when(script.getAttribute("src")).thenReturn("file:script.js");
		Scripting scripting = new Scripting();
		scripting.put("x", "1");
		Executor executor = new Executor(scripting, null, null) {
			@Override
			protected String getFileTextContent(String fileName)
					throws MalformedURLException, IOException {
				return "x++";
			}
		};

		executor.execute(script);

		assertEquals(2.0, scripting.get("x"));
	}

	@Test(expected = DocumentChangeException.class)
	public void gotoNext() throws InterpreterException {
		Goto goto1 = mock(Goto.class);
		when(goto1.getNext()).thenReturn("next.vxml");

		new Executor(new Scripting(), null, null).execute(goto1);
	}

	@Test(expected = FormItemChangeException.class)
	public void gotoNextInSameFile() throws InterpreterException {
		Goto goto1 = mock(Goto.class);
		when(goto1.getNextItem()).thenReturn("text");

		new Executor(new Scripting(), null, null).execute(goto1);
	}

	@Test(expected = DialogChangeException.class)
	public void gotoNextInSameFileShortForm() throws InterpreterException {
		Goto goto1 = mock(Goto.class);
		when(goto1.getNext()).thenReturn("#text");

		new Executor(new Scripting(), null, null).execute(goto1);
	}

	@Test(expected = DocumentChangeException.class)
	public void gotoExpr() throws InterpreterException {
		Goto goto1 = mock(Goto.class);
		when(goto1.getExpr()).thenReturn("'next.vxml'");

		new Executor(new Scripting(), null, null).execute(goto1);
	}

	@Test(expected = FormItemChangeException.class)
	public void gotoNextItem() throws InterpreterException {
		Goto goto1 = mock(Goto.class);
		when(goto1.getNextItem()).thenReturn("next_item");

		new Executor(new Scripting(), null, null).execute(goto1);
	}

	@Test(expected = FormItemChangeException.class)
	public void gotoExprItem() throws InterpreterException {
		Goto goto1 = mock(Goto.class);
		when(goto1.getExpritem()).thenReturn("'expr_item'");

		new Executor(new Scripting(), null, null).execute(goto1);
	}

	@Test(expected = DocumentChangeException.class)
	public void submit() throws InterpreterException {
		Submit submit = mock(Submit.class);
		when(submit.getAttribute("next")).thenReturn("next.vxml");

		new Executor(new Scripting(), null, null).execute(submit);
	}

	@Test
	public void voice() throws InterpreterException {
		Voice voice = mock(Voice.class);
		when(voice.getTextContent()).thenReturn("hello voice");

		SystemOutput voiceXTTOutPut = new SystemOutput();

		new Executor(new Scripting(), voiceXTTOutPut, null).execute(voice);

		assertEquals("hello voice", voiceXTTOutPut.getPrompts().get(0).tts);
	}
	
	@Test
	public void voiceWithProsodychild() throws InterpreterException, ParserConfigurationException, SAXException, IOException {
		String voiceFragment = "<voice name='Lise'><prosody  rate='1'  volume='50' >Bienvenue sur le service vocal</prosody></voice>";
		Voice voice = new Voice(buildDomNode(voiceFragment));
		
		SystemOutput voiceXTTOutPut = new SystemOutput();

		new Executor(new Scripting(), voiceXTTOutPut, null).execute(voice);

		assertEquals("Bienvenue sur le service vocal", voiceXTTOutPut.getPrompts().get(0).tts);
	}
	
	@Test
	public void ifWithPrompt() throws InterpreterException, ParserConfigurationException, SAXException, IOException {
		String ifFragment = "<if cond='!(false || c2IsV)'>" +
						  "			<prompt xml:lang='fr-FR'   >" +
						  "				<voice name='Lise'>Il n'y a pas d'information disponible dans ce menu.</voice>" +
						  "			</prompt>" +
						  "			<goto next='#menu_principal_EXIT_CHOICE'/>" +
						  "</if>";
		SystemOutput voiceXTTOutPut = new SystemOutput();
		Scripting scripting = new Scripting();
		scripting.put("c2IsV", "true");
		If if1 = new If(buildDomNode(ifFragment));
		
		try{
			new Executor(scripting, voiceXTTOutPut, null).execute(if1);
		} catch (DialogChangeException dialogChangeException){
			assertEquals("Il n'y a pas d'information disponible dans ce menu.  ", voiceXTTOutPut.getPrompts().get(0).tts);
		}
	
	}
	
	private Node buildDomNode(String xmlFragment) throws ParserConfigurationException, SAXException,
			IOException {
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = documentBuilder.parse(new InputSource(new StringReader(xmlFragment)));
		return document.getDocumentElement();
	}
}
