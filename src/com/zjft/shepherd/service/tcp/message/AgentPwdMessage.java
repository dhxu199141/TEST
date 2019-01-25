package com.zjft.shepherd.service.tcp.message;

import org.w3c.dom.Document;

public class AgentPwdMessage extends CommonMessage
{
	public AgentPwdMessage(Document doc) 
	{
		super(doc);
	}
	
	public String getUninstallpassword()
	{
		return getValue("actioninfo","uninstallpassword");
	}
}
