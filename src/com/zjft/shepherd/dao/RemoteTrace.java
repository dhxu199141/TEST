package com.zjft.shepherd.dao;

import java.io.Serializable;

public class RemoteTrace implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 671691969661889503L;
	
	private String logicId;
	private String commandId;
	private String devNo;
	private String opNo;
	private String opTime;
	private String status;
	private String content;
	//自动精查 2015-01-20 zhangdd
	private String commandCache;
	private String cimCountAll;
	private String cimCashUnitCount;
	private String cashUnitList;
	private String checkType;


	/** full constructor */
	public RemoteTrace(String logicId, String commandId, String devNo,
			String opNo, String opTime, String status, String content,String commandCache,
			String cimCountAll,String cimCashUnitCount, String cashUnitList,String checkType) {
		this.logicId = logicId;
		this.commandId = commandId;
		this.devNo = devNo;
		this.opNo = opNo;
		this.opTime = opTime;
		this.status = status;
		this.content = content;		
		this.commandCache = commandCache;
		this.cimCountAll = cimCountAll;
		this.cimCashUnitCount = cimCashUnitCount;
		this.cashUnitList = cashUnitList;
		this.checkType = checkType;
	}

	/** default constructor */
	public RemoteTrace() {
	}

	public String getLogicId() {
		return this.logicId;
	}

	public void setLogicId(String logicId) {
		this.logicId = logicId;
	}

	public String getCommandId() {
		return this.commandId;
	}

	public void setCommandId(String commandId) {
		this.commandId = commandId;
	}

	public String getDevNo() {
		return this.devNo;
	}

	public void setDevNo(String devNo) {
		this.devNo = devNo;
	}

	public String getOpNo() {
		return this.opNo;
	}

	public void setOpNo(String opNo) {
		this.opNo = opNo;
	}

	public String getOpTime() {
		return this.opTime;
	}

	public void setOpTime(String opTime) {
		this.opTime = opTime;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getContent() {
		return this.content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getCommandCache() {
		return commandCache;
	}

	public void setCommandCache(String commandCache) {
		this.commandCache = commandCache;
	}

	public String getCimCountAll() {
		return cimCountAll;
	}

	public void setCimCountAll(String cimCountAll) {
		this.cimCountAll = cimCountAll;
	}

	public String getCimCashUnitCount() {
		return cimCashUnitCount;
	}

	public void setCimCashUnitCount(String cimCashUnitCount) {
		this.cimCashUnitCount = cimCashUnitCount;
	}

	public String getCashUnitList() {
		return cashUnitList;
	}

	public void setCashUnitList(String cashUnitList) {
		this.cashUnitList = cashUnitList;
	}

	public String getCheckType() {
		return checkType;
	}

	public void setCheckType(String checkType) {
		this.checkType = checkType;
	}
}
