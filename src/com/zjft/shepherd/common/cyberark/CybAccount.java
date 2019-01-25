package com.zjft.shepherd.common.cyberark;

public class CybAccount {
	private String userName;
	private String passWord;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassWord() {
		return passWord;
	}

	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}

	public String toString() {
		return "userName=[" + userName + "]    passWord=[" + passWord + "]";
	}
}
