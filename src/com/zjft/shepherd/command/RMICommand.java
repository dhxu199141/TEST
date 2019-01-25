package com.zjft.shepherd.command;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zjft.shepherd.business.control.MessageEncoded;
import com.zjft.shepherd.common.CalendarUtil;
import com.zjft.shepherd.common.SocketUtil;
import com.zjft.shepherd.common.SystemLanguage;
import com.zjft.shepherd.common.XmlUtil;
import com.zjft.shepherd.request.RMIRequest;

public class RMICommand {
	
	public final String SUCCESS = "00";
	public final String FAIL = "99";
	
	private static Log log = LogFactory.getLog(RMICommand.class);
	
	private RMIRequest request;
	
	private String retBody;
	
	public RMICommand(RMIRequest request) {
		setRequest(request);
	}
	
	public Map<String, Object> invoke(){
		Map<String, Object> retMap = initRet();
		StringBuffer retBody = new StringBuffer();
		boolean result = MessageEncoded.sendCmdToRvcMutil(genXML(), request.getIp(), request.getRemotePort(), retBody,20,SocketUtil.getsoTime("200045"),request.getPacketLen(),request.getZipType(),request.getDevNo());
		if(!result)
			return retMap;
		setRetBody(retBody.toString());
		String retCode = XmlUtil.getValue(this.getRetBody(),"//root/retcode","value");
		retMap.put("devRetCode", retCode);
		if(!"RMT000".equals(retCode) && !"000000".equals(retCode)) {
			String remark = XmlUtil.getValue(this.getRetBody(),"//root/retcode","remark");
			log.error(this.getRequest().getCmdIdName() + "失败,设备端返回信息："+remark);
			retMap.put("retMsg", remark);
			result = false;
			return retMap;
		}
		retMap.put("retCode", SUCCESS);
		retMap.put("retMsg", "操作成功");
		return retMap;
	}
	
	private Map<String, Object> initRet() {
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("retCode",  FAIL);
		retMap.put("retMsg", SystemLanguage.getSrcCommFai());
		return retMap;
		
	}

	protected String genXML() {
		
		StringBuffer inputXML = new StringBuffer();
		inputXML.append("<?xml version=\"1.0\"?>");
		inputXML.append("<root>");
		inputXML.append("<cmdid value=\"").append(request.getCmdId()).append("\"/>");
		inputXML.append("<msgid value=\"").append(UUID.randomUUID().toString()).append("\"/>");
		inputXML.append("<cmddatetime date=\"").append(CalendarUtil.getDate()).append("\" time=\"").append(CalendarUtil.getTime()).append("\"/>");
		inputXML.append("<operatorinfo userid=\"zjft\"/>");
		inputXML.append("<managerinfo ipaddress=\"").append(request.getIp()).append("\"/>");
		inputXML.append("<remote ipaddress=\"").append(request.getDevNo()).append("\" termno=\"").append(request.getDevNo()).append("\" serialno=\"").append(request.getDevNo()).append("\"/>");
		inputXML.append("</root>");
		return inputXML.toString();
	}
	
	
	public RMIRequest getRequest() {
		return request;
	}

	public void setRequest(RMIRequest reqest) {
		this.request = reqest;
	}

	protected String getRetBody() {
		return retBody;
	}

	protected void setRetBody(String retBody) {
		this.retBody = retBody;
	}

}
