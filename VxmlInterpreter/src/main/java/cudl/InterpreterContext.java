package cudl;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import cudl.node.Vxml;
import cudl.script.Scripting;

public class InterpreterContext {
	private static final String USER_AGENT = "cudl/0.1";
	
	private final Scripting scripting = new Scripting();
	private final DocumentAcces documentAcces = new DocumentAcces(USER_AGENT);
	private final UserInput input = new UserInput();
	private final SystemOutput output = new SystemOutput();
	private boolean inSubdialog = false;
	private Vxml currentVxml;

	
	public InterpreterContext(String startFileName) throws ParserConfigurationException, IOException, SAXException {
		this.setCurrentVxml(new Vxml(getDocumentAcces().get(startFileName, null).getDocumentElement()));
	}

	public Scripting getScripting() {
		return scripting;
	}

	public DocumentAcces getDocumentAcces() {
		return documentAcces;
	}

	public UserInput getInput() {
		return input;
	}

	public SystemOutput getOutput() {
		return output;
	}

	public boolean isInSubdialog() {
		return inSubdialog;
	}

	public void setInSubdialog(boolean inSubdialog) {
		this.inSubdialog = inSubdialog;
	}

	public Vxml getCurrentVxml() {
		return currentVxml;
	}

	public void setCurrentVxml(Vxml currentVxml) {
		this.currentVxml = currentVxml;
	}
}
