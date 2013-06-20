package cudl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import cudl.node.Form;
import cudl.node.VoiceXmlNode;
import cudl.node.Vxml;
import cudl.script.Scripting;

public class FormInterpretationAlgorithmTest {

	@Test
	public void visitBlock() throws ParserConfigurationException, SAXException, IOException {
		DocumentAcces documentAcces = mock(DocumentAcces.class);
		InterpreterContext interpreterContext = mockInterpreterContext(new Scripting(), new Form(buildDomNode("<form><block>bonjour</block><block>bibi</block><block><value expr=\"test\"/></block> <script>var test = 'blablah';</script></form>")), new SystemOutput(), new UserInput(), documentAcces);
		
		FormInterpretationAlgorithm formInterpretationAlgorithm = new FormInterpretationAlgorithm(interpreterContext);
		formInterpretationAlgorithm.run();
		
		List<Prompt> prompts = interpreterContext.getOutput().getPrompts();
		
		assertEquals("bonjour", prompts.get(0).tts);
		assertEquals("bibi", prompts.get(1).tts);
		assertEquals("blablah", prompts.get(2).tts);
	}

	private InterpreterContext mockInterpreterContext(Scripting scripting,
			VoiceXmlNode dialog, SystemOutput outPut, UserInput userInput,
			DocumentAcces documentAcces) {
		InterpreterContext interpreterContext = mock(InterpreterContext.class);
		Vxml vxml = mock(Vxml.class);
		when(vxml.getFirstDialog()).thenReturn(dialog);
		when(interpreterContext.getCurrentVxml()).thenReturn(vxml);
		when(interpreterContext.getDocumentAcces()).thenReturn(documentAcces);
		when(interpreterContext.getInput()).thenReturn(userInput);
		when(interpreterContext.getOutput()).thenReturn(outPut);
		when(interpreterContext.getScripting()).thenReturn(scripting);
		return interpreterContext;
	}

	private Node buildDomNode(String xmlFragment) throws ParserConfigurationException, SAXException,
			IOException {
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = documentBuilder.parse(new InputSource(new StringReader(xmlFragment)));
		return document.getDocumentElement();
	}
}
