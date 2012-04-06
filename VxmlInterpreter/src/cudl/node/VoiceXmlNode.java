package cudl.node;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import cudl.VoiceXmlNodeFactory;

public abstract class VoiceXmlNode {
	protected final Node node;
	protected List<VoiceXmlNode> childs = new ArrayList<VoiceXmlNode>();
	private VoiceXmlNode parent;

	public VoiceXmlNode(Node node) {
		this.node = node;
		createChilds();
	}

	private void createChilds() {
		NodeList childNodes = node.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			VoiceXmlNode child = VoiceXmlNodeFactory.newInstance(childNodes.item(i));
			if (child != null) {
				child.setParent(this);
				addChild(child);
			}
		}
	}

	public String getAttribute(String att) {
		NamedNodeMap attributes = node.getAttributes();
		if (attributes == null){
			return null;
		}

		Node attValue = attributes.getNamedItem(att);
		if (attValue == null) {
			return null;
		}

		return attValue.getNodeValue();
	}

	private void addChild(VoiceXmlNode childNode) {
		if (canContainsChild(childNode)) {
			childs.add(childNode);
		}
	}

	public boolean canContainsChild(VoiceXmlNode child) {
		return false;
	}


	public String getNodeName() {
		return node.getNodeName();
	}

	public List<VoiceXmlNode> getChilds() {
		return childs;
	}

	public VoiceXmlNode getParent() {
		return parent;
	}

	protected void setParent(VoiceXmlNode parent) {
		this.parent = parent;
	}
}
