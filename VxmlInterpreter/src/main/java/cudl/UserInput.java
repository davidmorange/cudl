package cudl;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserInput {
	private String input;

	ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	public UserInput() {
	}

	public void setInput(String str) {
		lock.writeLock().lock();
		try {
			input = str;
		} finally {
			lock.writeLock().unlock();
		}
	}

	public String readData() {
		lock.readLock().lock();
		String inputTmp = null;
		try {
			if (null != input) {
				inputTmp = "" + input;
			}
			input = null;
		} finally {
			lock.readLock().unlock();
		}
		return inputTmp;
	}
}