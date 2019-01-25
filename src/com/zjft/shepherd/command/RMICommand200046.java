package com.zjft.shepherd.command;

import java.util.UUID;

import com.zjft.shepherd.business.control.DepositIdInfo;
import com.zjft.shepherd.common.CalendarUtil;
import com.zjft.shepherd.request.RMIRequest;
import com.zjft.shepherd.request.RMIRequest200046;

public class RMICommand200046 extends RMICommand {
	
	public RMICommand200046(RMIRequest request) {
		super(request);
	}

	@Override
	protected String genXML() {
		StringBuffer inputXML = new StringBuffer();
		inputXML.append("<?xml version=\"1.0\"?>");
		inputXML.append("<root>");
		inputXML.append("<cmdid value=\"200046\"/>");
		inputXML.append("<msgid value=\"").append(UUID.randomUUID().toString()).append("\"/>");
		inputXML.append("<cmddatetime date=\"").append(CalendarUtil.getDate()).append("\" time=\"").append(CalendarUtil.getTime()).append("\"/>");
		inputXML.append("<banknoteid version=\"").append(this.getRequest().getVersion()).append("\">");
		for(DepositIdInfo id : this.getRequest().getIds()) {
			inputXML.append("<item ID=\"").append(id.getId()).append("\"/>");
		}
		inputXML.append("</banknoteid>");
		inputXML.append("<remote ipaddress=\"").append(this.getRequest().getIp()).append("\" termno=\"").append(this.getRequest().getDevNo()).append("\" serialno=\"").append(this.getRequest().getDevNo()).append("\"/>");
		inputXML.append("</root>");
		return inputXML.toString();
	}

	public RMIRequest200046 getRequest() {
		return (RMIRequest200046)super.getRequest();
	}
}
