package cudl;

class GotoException extends InterpreterException {
	public String next;

	public GotoException(String string) {
		this.next = string;
	}
}
