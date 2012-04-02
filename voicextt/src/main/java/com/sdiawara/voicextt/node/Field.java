package com.sdiawara.voicextt.node;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Node;

import com.sdiawara.voicextt.FormItemVisitor;
import com.sdiawara.voicextt.InputFormItem;
import com.sdiawara.voicextt.exception.InterpreterException;

/**
 * 
 * @author sdiawara
 * 
 */
public class Field extends InputFormItem {
	private static final List<String> CHILDS;
	/**
	 * The form item variable in the dialog scope that will hold the result. The
	 * name must be unique among form items in the form. If the name is not
	 * unique, then a badfetch error is thrown when the document is fetched. The
	 * name must conform to the variable naming conventions
	 */
	private static final String ATT_NAME = "name";
	/**
	 * The initial value of the form item variable; default is ECMAScript
	 * undefined. If initialized to a value, then the form item will not be
	 * visited unless the form item variable is cleared.
	 */
	private static final String ATT_EXPR = "expr";
	/**
	 * An expression that must evaluate to true after conversion to boolean in
	 * order for the form item to be visited. The form item can also be visited
	 * if the attribute is not specified.
	 */
	private static final String ATT_COND = "cond";
	/**
	 * The type of field, i.e., the name of a builtin grammar type . Platform
	 * support for builtin grammar types is optional. If the specified builtin
	 * type is not supported by the platform, an error.unsupported.builtin event
	 * is thrown.
	 */
	private static final String ATT_TYPE = "type";
	/**
	 * If this is false (the default) all active grammars are turned on while
	 * collecting this field. If this is true, then only the field's grammars
	 * are enabled: all others are temporarily disabled.
	 */
	private static final String ATT_MODAL = "modal";
	/**
	 * The name of the grammar slot used to populate the variable (if it is
	 * absent, it defaults to the variable name). This attribute is useful in
	 * the case where the grammar format being used has a mechanism for
	 * returning sets of slot/value pairs and the slot names differ from the
	 * form item variable names.
	 */
	private static final String ATT_SLOT = "slot";

	static {
		CHILDS = new ArrayList<String>();
		CHILDS.add("audio");
		CHILDS.add("catch");
		CHILDS.add("enumerate");
		CHILDS.add("error");
		CHILDS.add("filled");
		CHILDS.add("grammar");
		CHILDS.add("help");
		CHILDS.add("link");
		CHILDS.add("noinput");
		CHILDS.add("nomatch");
		CHILDS.add("option");
		CHILDS.add("prompt");
		CHILDS.add("property");
		CHILDS.add("value");
	}

	public Field(Node node) {
		super(node);
	}

	@Override
	public boolean canContainsChild(VoiceXmlNode child) {
		return CHILDS.contains(child.getNodeName());
	}

	@Override
	public void accept(FormItemVisitor visitor) throws InterpreterException {
		visitor.visit(this);
	}

	/**
	 * 
	 * @return the attExpr
	 */
	public String getExpr() {
		return getAttribute(ATT_EXPR);
	}

	/**
	 * 
	 * @return the attCond
	 */
	public String getCond() {
		return getAttribute(ATT_COND);
	}

	/**
	 * 
	 * @return the attType
	 */
	public String getType() {
		return getAttribute(ATT_TYPE);
	}

	/**
	 * 
	 * @return the attModal
	 */
	public String getModal() {
		return getAttribute(ATT_MODAL);
	}

	/**
	 * @return the attSlot
	 */
	public String getSlot() {
		return getAttribute(ATT_SLOT);
	}
}
