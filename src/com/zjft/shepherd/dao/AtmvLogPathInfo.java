package com.zjft.shepherd.dao;

public class AtmvLogPathInfo {
	private String no;
	private String logName;
	private String logType;
	private String logPath;
	private String fileAddress;
	private String fileType;
	private String showFlag;
	private Integer devType;
	private String note;

	public AtmvLogPathInfo() {
	}

	public AtmvLogPathInfo(String no, String logName, String logType,
			String logPath, String fileAddress, String fileType,
			String showFlag, Integer devType, String note) {
		this.no = no;
		this.logName = logName;
		this.logType = logType;
		this.logPath = logPath;
		this.fileAddress = fileAddress;
		this.fileType = fileType;
		this.showFlag = showFlag;
		this.devType = devType;
		this.note = note;
	}
	

	public String getLogName() {
		return this.logName;
	}


	public void setLogName(String logName) {
		this.logName = logName;
	}

	public String getLogType() {
		return this.logType;
	}

	public void setLogType(String logType) {
		this.logType = logType;
	}

	public String getLogPath() {
		return this.logPath;
	}

	public void setLogPath(String logPath) {
		this.logPath = logPath;
	}

	public String getNo() {
		return no;
	}

	public void setNo(String no) {
		this.no = no;
	}

	public String getFileAddress() {
		return fileAddress;
	}

	public void setFileAddress(String fileAddress) {
		this.fileAddress = fileAddress;
	}

	public String getShowFlag() {
		return showFlag;
	}

	public void setShowFlag(String showFlag) {
		this.showFlag = showFlag;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public Integer getDevType() {
		return devType;
	}

	public void setDevType(Integer devType) {
		this.devType = devType;
	}

}
