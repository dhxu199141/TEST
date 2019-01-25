package com.zjft.shepherd.service.tcp.message;

import org.w3c.dom.Document;

public class OsUsbMessage extends CommonMessage
{

	public OsUsbMessage(Document doc) 
	{
		super(doc);
	}
	
	public String getUsboption()
	{
		return getValue("actioninfo", "usboption");
	}
	
	public String getUsbstatus()
	{
		return getValue("retcode", "usbstatus");
	}
}
