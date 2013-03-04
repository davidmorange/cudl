package cudl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.io.StringReader;

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
import cudl.script.Scripting;

public class FormInterpretationAlgorithmTest {

	@Test
	public void visitBlock() throws ParserConfigurationException, SAXException, IOException {
		Scripting scripting = new Scripting();
		
		VoiceXmlNode dialog = new Form(buildDomNode("<form><block>bonjour</block><block>bibi</block><block><value expr=\"test\"/></block> <script>var test = 'blablah';</script></form>"));
		
		SystemOutput outPut = new SystemOutput();
		UserInput userInput = new UserInput();
		DocumentAcces documentAcces = mock(DocumentAcces.class);
		FormInterpretationAlgorithm formInterpretationAlgorithm = new FormInterpretationAlgorithm(dialog, scripting, outPut, userInput, documentAcces);
		formInterpretationAlgorithm.run();
		
		assertEquals("bonjour", outPut.getPrompts().get(0).tts);
		assertEquals("bibi", outPut.getPrompts().get(1).tts);
		assertEquals("blablah", outPut.getPrompts().get(2).tts);
	}

	private Node buildDomNode(String xmlFragment) throws ParserConfigurationException, SAXException,
			IOException {
		DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document document = documentBuilder.parse(new InputSource(new StringReader(xmlFragment)));
		return document.getDocumentElement();
	}
}
