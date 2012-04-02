package com.sdiawara.voicextt;

import com.sdiawara.voicextt.exception.InterpreterException;
import com.sdiawara.voicextt.node.Block;
import com.sdiawara.voicextt.node.Field;
import com.sdiawara.voicextt.node.Initial;
import com.sdiawara.voicextt.node.Record;
import com.sdiawara.voicextt.node.Subdialog;
import com.sdiawara.voicextt.node.Transfer;

public interface FormItemVisitor {
	public void visit(Block block) throws InterpreterException;

	public void visit(Field field) throws InterpreterException;

	public void visit(Subdialog subdialog) throws InterpreterException;

	public void visit(Transfer transfer) throws InterpreterException;

	public void visit(Record Record) throws InterpreterException;

	public void visit(Initial Initial) throws InterpreterException;

	public void visit(com.sdiawara.voicextt.node.Object object) throws InterpreterException;

}
