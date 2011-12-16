package com.sdiawara.voicextt;

import java.util.LinkedHashMap;
import java.util.Map;

import com.sdiawara.voicextt.node.Vxml;

public class Application {
	private Map<String, Vxml> documents;
	private Vxml currentDocument;

	public Application() {
		documents = new LinkedHashMap<String, Vxml>();
	}

	public Map<String, Vxml> getDocuments() {
		return documents;
	}

	public void setDocuments(Map<String, Vxml> documents) {
		this.documents = documents;
	}

	public void addDocument(Vxml document, String url) {
		documents.put(url, document);
	}

	public Vxml getCurrentDocument() {
		return currentDocument;
	}

	public void setCurrentDocument(Vxml currentDocument) {
		this.currentDocument = currentDocument;
	}
}
