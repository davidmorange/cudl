package com.sdiawara.voicextt;

import java.lang.reflect.Constructor;

import org.w3c.dom.Node;

import com.sdiawara.voicextt.node.VoiceXmlNode;

public class VoiceXmlNodeFactory {

	public static VoiceXmlNode newInstance(Node node) {
		String nodeName = node.getNodeName().replaceAll("#", "");
		String className = getClassName(nodeName);
		try {
			Constructor<?> constructor = Class.forName(className).getConstructor(new Class[] { Node.class });
			return (VoiceXmlNode) constructor.newInstance(node);
		} catch (ClassNotFoundException e) {
			throw new UnsupportedOperationException(capitalize(nodeName) + ".java not found");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static String getClassName(String tagName) {
		return VoiceXmlNode.class.getPackage().getName() + "." + capitalize(tagName);
	}

	private static String capitalize(String tagName) {
		return ((tagName.charAt(0) + "").toUpperCase()) + tagName.substring(1);
	}
}
