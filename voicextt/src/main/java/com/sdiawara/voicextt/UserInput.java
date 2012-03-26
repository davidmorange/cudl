package com.sdiawara.voicextt;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class UserInput {
	private String input;

	ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

	public UserInput() {
	}

	public void setInput(String str) {
		lock.writeLock().lock();
		try {
			System.out.println("input incomming  "+str);
			input = str;
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} finally {
			lock.readLock().unlock();
		}
		return inputTmp;
	}
}