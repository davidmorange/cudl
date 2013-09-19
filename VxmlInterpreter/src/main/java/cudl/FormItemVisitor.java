package cudl;

import cudl.node.Block;
import cudl.node.Field;
import cudl.node.Initial;
import cudl.node.Record;
import cudl.node.Subdialog;
import cudl.node.Transfer;

public interface FormItemVisitor {
	public void visit(Block block) throws InterpreterException;

	public void visit(Field field) throws InterpreterException;

	public void visit(Subdialog subdialog) throws InterpreterException;

	public void visit(Transfer transfer) throws InterpreterException;

	public void visit(Record Record) throws InterpreterException;

	public void visit(Initial Initial) throws InterpreterException;

	public void visit(cudl.node.Object object) throws InterpreterException;

}
