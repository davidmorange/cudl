package com.sdiawara.voicextt;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Before;
import org.junit.Test;
import org.mozilla.javascript.Undefined;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.sdiawara.voicextt.exception.InterpreterException;
import com.sdiawara.voicextt.node.Field;
import com.sdiawara.voicextt.node.Form;
import com.sdiawara.voicextt.node.Var;
import com.sdiawara.voicextt.node.VoiceXmlNode;
import com.sdiawara.voicextt.script.Scripting;

public class FormInterpretationAlgorithmTest {
	Scripting scripting;
	SystemOutput outPut;
	private UserInput userInput;

	@Before
	public void setUp() throws Exception {
		scripting = new Scripting();
		scripting.enterScope();
		scripting.enterScope();
		scripting.enterScope();
		outPut = new SystemOutput();
		userInput = new UserInput();
	}

	private FormInterpretationAlgorithm createFia(String fileName) throws ParserConfigurationException,
			IOException, SAXException {
		DocumentAcces documentAcces = new DocumentAcces("test/fia");
		Document document = documentAcces.get("file:" + new File(".").getCanonicalPath()
				+ "/src/test/java/vxml/" + fileName, null);
		VoiceXmlNode dialog = new Form(document.getElementsByTagName("form").item(0));

		return new FormInterpretationAlgorithm(dialog, scripting, outPut, userInput);
	}

	@Test
	public void initializationPhaseDeclareFormItemVariable() throws ParserConfigurationException, IOException,
			SAXException {
		Form form = mock(Form.class);
		List<VoiceXmlNode> childs = new ArrayList<VoiceXmlNode>();
		mockVar(childs, "name", "test", Var.class);
		mockFormItem(childs, "name", "couleur", Field.class);
		mockFormItem(childs, "name", "initial$4", Field.class);

		when(form.getChilds()).thenReturn(childs);

		new FormInterpretationAlgorithm(form, scripting, outPut, userInput).initialize();

		assertEquals(Undefined.instance, scripting.get("test"));
		assertEquals(Undefined.instance, scripting.get("couleur"));
		assertEquals(Undefined.instance, scripting.get("dialog.initial$4"));
	}

	private void mockFormItem(List<VoiceXmlNode> childs, String name, String value, Class<? extends FormItem> class1) {
		FormItem mock = mock(class1);
		when(mock.getName()).thenReturn(value);
		childs.add(mock);
	}

	private void mockVar(List<VoiceXmlNode> childs, String name, String value,
			Class<? extends VoiceXmlNode> class1) {
		VoiceXmlNode mock = mock(class1);
		when(mock.getAttribute(name)).thenReturn(value);
		childs.add(mock);
	}

	@Test
	public void selectPhase() throws ParserConfigurationException, IOException, SAXException {
		FormInterpretationAlgorithm fia = createFia("select1.vxml");
		fia.initialize();
		assertEquals("one", fia.select().getAttribute("name"));
		fia.setNextItem("two");
		assertEquals("two", fia.select().getAttribute("name"));
		fia.setNextItem(null);
		scripting.set("one", "'test'");
		assertEquals("couleur", fia.select().getAttribute("name"));
	}

	@Test
	public void blockVisitor() throws ParserConfigurationException, IOException, SAXException,
			InterpreterException {
		FormInterpretationAlgorithm fia = createFia("collectBlock.vxml");
		fia.initialize();
		fia.setSelectedFormItem(fia.select());
		fia.collect();
		fia.setSelectedFormItem(fia.select());
		fia.collect();

		assertEquals("xValue", scripting.get("x"));
		assertEquals("yValue", scripting.get("y"));
		assertEquals("zValue", scripting.get("z"));
		assertEquals(true, scripting.get("one"));
		assertEquals("this is prompt is block", outPut.getTTS().get(0));
		assertEquals("this is prompt is block", outPut.getTTS().get(0));
	}

	@Test
	public void fieldVisitor() throws ParserConfigurationException, IOException, SAXException {
		FormInterpretationAlgorithm fia = createFia("collectField.vxml");
		Speaker speecher = new Speaker(userInput);

		FormInterpretationAlgorithmRunner runner = new FormInterpretationAlgorithmRunner(fia, speecher);
		runner.start();

		runner.talk("blabla");

		System.err.println(outPut.getTTS());
		assertEquals(4, outPut.getTTS().size());
		assertEquals("this is prompt 1 in field", outPut.getTTS().get(0));
		assertEquals("this is audio 1 in field", outPut.getTTS().get(1));
		assertEquals("this is expr 1 in field", outPut.getTTS().get(2));
		assertEquals("you say blabla", outPut.getTTS().get(3));
	}
}
