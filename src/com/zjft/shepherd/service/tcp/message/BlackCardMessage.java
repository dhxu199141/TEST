package com.zjft.shepherd.service.tcp.message;

import org.w3c.dom.Document;

public class BlackCardMessage extends CommonMessage
{

	public BlackCardMessage(Document doc) 
	{
		super(doc);
	}
	
	public String getBadcardoption()
	{
		return getValue("actioninfo", "badcardoption");
	}
}
