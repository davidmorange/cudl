package cudl;

public class PromptCounter {
	protected int count;

	public PromptCounter() {
		count = 1;
	}

	public void incr() {
		count++;
	}

	public int getCount() {
		return count;
	}
}
