package cudl.w3c;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

import cudl.Interpreter;
import cudl.Prompt;

public class InterpreterTestW3C {
	private static Prompt prompt;
	private static String url;
	private Interpreter interpreter;
	private final static ArrayList<String> filenames = new ArrayList<String>() {
		{
			add("a11.txml");
			add("a12a.txml");// need file to add("a12b.txml");
			add("a18.txml");
			add("a19.txml");
		}
	};

	@BeforeClass
	public static void setUp() throws IOException, TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		url =  new File(".").getCanonicalPath() + "/test/docVxml/w3c/";
		prompt = new Prompt();
		prompt.tts = "pass";
		File file = new File(new File(".").getCanonicalPath() + "/test/docVxml/w3c/");
//		for (String name : file.list()) {
//			if (name.endsWith(".txml")) {
//				txmlToVxml(name, name.replace(".txml", ".vxml"));
//				System.err.println(name);
//			}
//		}
	}

	@Test
	public void testSimpleTxmlToVxml() throws IOException, ParserConfigurationException, SAXException,
			TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException {
		for (String fileName : filenames) {
			System.err.println();
			interpreter = new Interpreter("file://" + url + "xslt_output/" + fileName.replace(".txml", ".vxml"));
			interpreter.start();
			if (fileName.equals("a18.txml")) {
				interpreter.talk("Chicago");
			}
			System.err.println(interpreter.getLogs());
			assertEquals(prompt, interpreter.getPrompts().get(interpreter.getPrompts().size() - 1));
			System.err.println(fileName+"\ttest OK");
		}

	}

	private static void txmlToVxml(String fromFile, String toFile) throws TransformerFactoryConfigurationError,
			TransformerConfigurationException, TransformerException, FileNotFoundException {
		TransformerFactory tFactory = TransformerFactory.newInstance();
		Transformer transformer = tFactory.newTransformer(new StreamSource(url + "/irtest.xslt"));
		
		String outputFileName = (url + "/xslt_output/").replaceAll("file:/*", "/");

		transformer.transform(new StreamSource(url + fromFile), new StreamResult(new FileOutputStream(outputFileName + toFile)));

	}
	
	@AfterClass
	public static void afterAllTest() {
		File f = new File(url + "/xslt_output/");
		f.deleteOnExit();
		
	}

}
