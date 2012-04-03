package cudl;

public class Speaker extends Thread {

	private final UserInput userInput;
	private String utterance;

	public Speaker(UserInput userInput) {
		this.userInput = userInput;
	}

	@Override
    public synchronized  void run() {
		super.run();
		userInput.setInput(utterance);
	}

	public synchronized void setUtterance(String utterance) {
		this.utterance = utterance;
	}
}
