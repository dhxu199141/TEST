package com.zjft.shepherd.dao;

import com.zjft.shepherd.common.CalendarUtil;
import com.zjft.shepherd.common.NumberUtil;

public class RemoteFileDownInfo  implements java.io.Serializable {

	private static final long serialVersionUID = -4727207733167984692L;
	
	private String logicId;
	private String traceLogicId;
	private String remoteFile;
	private String localFile;
	private String remoteHandleFile;
	private Double progress;
	private String status;
	private String userNo;

	public RemoteFileDownInfo() {
		
	}

	public RemoteFileDownInfo(String logicId, String traceLogicId, String remoteFile, String localFile, String remoteHandleFile, Double progress,String status) {
		this.logicId = logicId;
		this.traceLogicId = traceLogicId;
		this.remoteFile = remoteFile;
		this.localFile = localFile;
		this.remoteHandleFile = remoteHandleFile;
		this.progress = progress;
		this.status = status;
	}
	
	public String organizeXML() {
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\"?>");
		xml.append("<FileDownInfo>");
		xml.append("<LogicId>"+this.getTraceLogicId()+"</LogicId>");
		xml.append("<DateTime>"+CalendarUtil.getSysTimeYMDHMS1()+"</DateTime>");
		xml.append("<RemoteFile>"+this.getRemoteFile()+"</RemoteFile>");
		xml.append("<LocalFile>"+this.getLocalFile()+"</LocalFile>");
		if(this.getProgress() != null) {
			xml.append("<Progress>"+NumberUtil.percent(this.getProgress())+"</Progress>");
		} else {
			xml.append("<Progress></Progress>");
		}
		xml.append("<Status>"+this.getStatus()+"</Status>");
		xml.append("<UserNo>"+this.getUserNo()+"</UserNo>");
		xml.append("</FileDownInfo>");
		return xml.toString();
	}


	public String getLogicId() {
		return this.logicId;
	}

	public void setLogicId(String logicId) {
		this.logicId = logicId;
	}

	public String getRemoteFile() {
		return this.remoteFile;
	}

	public void setRemoteFile(String remoteFile) {
		this.remoteFile = remoteFile;
	}

	public String getLocalFile() {
		return this.localFile;
	}

	public void setLocalFile(String localFile) {
		this.localFile = localFile;
	}

	public String getRemoteHandleFile() {
		return this.remoteHandleFile;
	}

	public void setRemoteHandleFile(String remoteHandleFile) {
		this.remoteHandleFile = remoteHandleFile;
	}

	public Double getProgress() {
		return this.progress;
	}

	public void setProgress(Double progress) {
		this.progress = progress;
	}

	public String getTraceLogicId() {
		return traceLogicId;
	}

	public void setTraceLogicId(String traceLogicId) {
		this.traceLogicId = traceLogicId;
	}

	public String getStatus() {
		if(progress == 1.00) {
			return "成功";
		} else {
			if(status == null || "".equals(status)) {
				return "正在下载";
			} else {
				return status;
			}
		}
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}
}