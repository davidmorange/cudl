package cudl;

import java.util.ArrayList;
import java.util.List;

import cudl.node.VoiceXmlNode;

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
	public static void doEvent(VoiceXmlNode field, Executor executor, String eventType, int eventCount) throws InterpreterException {
		VoiceXmlNode eventHandlers = searchEventHandlers(eventType, eventCount, field);
		if (eventHandlers != null) {
			List<VoiceXmlNode> childs = eventHandlers.getChilds();
			for (VoiceXmlNode voiceXmlNode : childs) {
				executor.execute(voiceXmlNode);
			}
		}
	}

	private static VoiceXmlNode searchEventHandlers(String eventType, int eventCounter, VoiceXmlNode parent) {
		List<VoiceXmlNode> availableHandler = new ArrayList<VoiceXmlNode>();
		while (parent != null) {
			List<VoiceXmlNode> childs = parent.getChilds();
			for (VoiceXmlNode node : childs) {
				if (eventType.equals(node.getNodeName()) || isHandlerForEventType(eventType, node)) {
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
		return node.getNodeName().equals("catch") && eventAttribute != null && eventAttribute.contains(eventName);
	}
}
