package com.zjft.shepherd.request;

import java.util.Map;

import com.zjft.shepherd.business.control.RemoteControlTool;
import com.zjft.shepherd.common.SystemCons;

public class RMIRequest {

	private String cmdId;
	private String userNo;
	private String devNo;
	private String ip;
	private int packetLen;
	private int zipType;
	private String remotePort;
	
	public String getCmdIdName() {
		return RemoteControlTool.convertCommandId(cmdId);
	}
	
	public void setParameter(Map<String, Object> paraMap) {
		this.setCmdId(paraMap.get("cmdId").toString());
		this.setUserNo(paraMap.get("userNo").toString());
		String[] devInfo = ((String)paraMap.get("devInfo")).split("\\|", 0);
		this.setRemotePort(SystemCons.getRemotePort());
		this.setIp(devInfo[1]);
		this.setPacketLen(Integer.parseInt(devInfo[2]));
		this.setZipType(Integer.parseInt(devInfo[3]));
		this.setDevNo(devInfo[0]);
	}

	public String getCmdId() {
		return cmdId;
	}

	public void setCmdId(String cmdId) {
		this.cmdId = cmdId;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getDevNo() {
		return devNo;
	}

	public void setDevNo(String devNo) {
		this.devNo = devNo;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPacketLen() {
		return packetLen;
	}

	public void setPacketLen(int packetLen) {
		this.packetLen = packetLen;
	}

	public int getZipType() {
		return zipType;
	}

	public void setZipType(int zipType) {
		this.zipType = zipType;
	}

	public String getRemotePort() {
		return remotePort;
	}

	public void setRemotePort(String remotePort) {
		this.remotePort = remotePort;
	}

}
