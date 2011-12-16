package com.sdiawara.voicextt;

import com.sdiawara.voicextt.exception.VoiceXTTException;
import com.sdiawara.voicextt.node.Block;
import com.sdiawara.voicextt.node.Field;
import com.sdiawara.voicextt.node.Initial;
import com.sdiawara.voicextt.node.Record;
import com.sdiawara.voicextt.node.Subdialog;
import com.sdiawara.voicextt.node.Transfer;

public interface FormItemVisitor {
	public void visit(Block block) throws VoiceXTTException;

	public void visit(Field field) throws VoiceXTTException;

	public void visit(Subdialog subdialog) throws VoiceXTTException;

	public void visit(Transfer transfer) throws VoiceXTTException;

	public void visit(Record Record) throws VoiceXTTException;

	public void visit(Initial Initial) throws VoiceXTTException;

	public void visit(com.sdiawara.voicextt.node.Object object) throws VoiceXTTException;

}
