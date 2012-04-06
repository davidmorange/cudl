package cudl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import cudl.node.Field;
import cudl.node.VoiceXmlNode;
import cudl.utils.Utils;

//http://www.yoyodesign.org/doc/w3c/voicexml20/#dml5.2.2 french
// or http://www.w3.org/TR/voicexml20/#dml5.2.2 English
//http://www.yoyodesign.org/doc/w3c/voicexml20/#dml5.2.4
//TODO inline this code inside InternalInterpreter ???

//FIXME:
/*The occurrence of the event (default is 1). The count allows you to handle different occurrences of the same event differently.

 Each <form>, <menu>, and form item maintains a counter for each event that occurs while it is being visited. Item-level event counters are used for events thrown while visiting individual form items and while executing <filled> elements contained within those items. Form-level and menu-level counters are used for events thrown during dialog initialization and while executing form-level <filled> elements.

 Form-level and menu-level event counters are reset each time the <menu> or <form> is re-entered. Form-level and menu-level event counters are not reset by the <clear> element.

 Item-level event counters are reset each time the <form> containing the item is re-entered. Item-level event counters are also reset when the item is reset with the <clear> element. An item's event counters are not reset when the item is re-entered without leaving the <form>.

 Counters are incremented against the full event name and every prefix matching event name; for example, occurrence of the event "event.foo.1" increments the counters for "event.foo.1" plus "event.foo" and "event".*/
class InterpreterEventHandler {
	private Map<String, Integer> eventCounter;
	private final InterpreterContext context;

	InterpreterEventHandler(InterpreterContext context) {
		this.context = context;
		this.eventCounter = new Hashtable<String, Integer>();
		// FIXME this is a
		// global counter...
		// check in vxml
		// spec when a
		// counter should be
		// reset to 0 ?
	}

	public void doEvent(String eventType) throws IOException, SAXException, InterpreterException,
			ParserConfigurationException {
		// int counter = (eventCounter.get(eventType) == null) ? 1 :
		// eventCounter.get(eventType) + 1;
		// // System.err.println(context.getSelectedFormItem() +
		// // "********************");
		// eventCounter.put(eventType, counter);
		// //Node node = searchEventHandlers(eventType, counter, null/*
		// * context.
		// * getSelectedFormItem
		// * ()
		// */);
		// // Document rootDoc = context.getRootDocument();
		// // node = (node == null && rootDoc != null) ?
		// // searchEventHandlers(eventType, counter,
		// // rootDoc.getElementsByTagName("vxml").item(0)) : node;
		//
		// // if (node == null) {
		// // // FIXME what are we supposed to do here ? Check the spec...
		// throw new RuntimeException("No event handler found for event " +
		// eventType);
		// }
		// TagInterpreterFactory.getTagInterpreter(node).interpret(context);
	}

	private static VoiceXmlNode searchEventHandlers(String eventType, int eventCounter, VoiceXmlNode parent) {
		List<VoiceXmlNode> availableHandler = new ArrayList<VoiceXmlNode>();
		while (parent != null) {
			List<VoiceXmlNode> childs = parent.getChilds();
			for (VoiceXmlNode node : childs) {
				if (eventType.endsWith(node.getNodeName()) || isHandlerForEventType(eventType, node)) {
					String count = node.getAttribute("count");
					int nodeCount = (count == null) ? 1 : Integer.parseInt(count);
					if (nodeCount == eventCounter) {
						return node;
					}
					availableHandler.add(node);
				}
			}
			parent = parent.getParent();
		}

		return availableHandler.size() == 0 ? null : availableHandler.get(0);
	}

	private static boolean isHandlerForEventType(String eventType, VoiceXmlNode node) {
		return node.getNodeName().equals(eventType) || isCatchItemAndContainsEvent(node, eventType);
	}

	private static boolean isCatchItemAndContainsEvent(VoiceXmlNode node, String eventName) {
		String eventAttribute = node.getAttribute("event");
		return node.getNodeName().equals("catch") && eventAttribute != null
				&& eventAttribute.contains(eventName);
	}

	public void resetEventCounter() {
		this.eventCounter = new Hashtable<String, Integer>();
	}

	public static void doEvent(VoiceXmlNode field, Executor executor, String eventType, int eventCount)
			throws InterpreterException {
		VoiceXmlNode eventHandlers = searchEventHandlers(eventType, eventCount, field);
		if (eventHandlers != null) {
			List<VoiceXmlNode> childs = eventHandlers.getChilds();
			for (VoiceXmlNode voiceXmlNode : childs) {
				System.err.println(voiceXmlNode);
				executor.execute(voiceXmlNode);
			}
		}
	}
}
