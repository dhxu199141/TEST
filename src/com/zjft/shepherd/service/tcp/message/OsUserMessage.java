package com.zjft.shepherd.service.tcp.message;

import org.w3c.dom.Document;

public class OsUserMessage extends CommonMessage
{

	public OsUserMessage(Document doc) 
	{
		super(doc);
	}
	
	public String getUsername()
	{
		return getValue("actioninfo", "username");
	}
	
	public String getOldpassword()
	{
		return getValue("actioninfo", "oldpassword");
	}
	
	public String getNewpassword()
	{
		return getValue("actioninfo", "newpassword");
	}
}
