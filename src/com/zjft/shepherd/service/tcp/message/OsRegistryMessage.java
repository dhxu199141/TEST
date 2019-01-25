package com.zjft.shepherd.service.tcp.message;

import org.w3c.dom.Document;

public class OsRegistryMessage extends CommonMessage
{

	public OsRegistryMessage(Document doc) 
	{
		super(doc);
	}
	
	public String getRegoption()
	{
		return getValue("actioninfo", "regoption");
	}
	
	public String getRegstatus()
	{
		return getValue("retcode", "regstatus");
	}
}
