package com.sdiawara.voicextt;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Ignore;
import org.junit.Test;
import org.xml.sax.SAXException;

public class DocumentAccessorTest {
	// this test requier servlet
	private final String uri = "http://localhost:8181/chat/ServletVoiceXTT";

	@Test
	@Ignore
	public void documentAccessPostAndGetMode() throws IOException, ParserConfigurationException, SAXException {
		DocumentAcces documentAcces = new DocumentAcces("test/voicextt");
		Map<String, String> params = new HashMap<String, String>();
		params.put("key", "value");
		documentAcces.post(uri, params);

		assertEquals("get", documentAcces.get(uri, params).getElementsByTagName("body").item(0).getTextContent());
		assertEquals("post", documentAcces.post(uri, params).getElementsByTagName("body").item(0).getTextContent());
	}
}
