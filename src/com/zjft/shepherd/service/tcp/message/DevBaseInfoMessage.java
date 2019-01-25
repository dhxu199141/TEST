package com.zjft.shepherd.service.tcp.message;

import org.w3c.dom.Document;

public class DevBaseInfoMessage extends CommonMessage
{

	public DevBaseInfoMessage(Document doc) 
	{
		super(doc);
	}
	
	public String getChkTermno()
	{
		return getValue("actioninfo", "termno");
	}
	
	public String getChkIp()
	{
		return getValue("actioninfo", "ip");
	}
	
	public String getChkOrgNo()
	{
		return getValue("actioninfo", "orgno");
	}
	
	public String getChkAtmvIp()
	{
		return getValue("actioninfo", "atmvip");
	}
	
	public String getChkIdBlk()
	{
		return getValue("actioninfo", "atmvip");
	}
	
	public String getChkDevCatalog()
	{
		return getValue("actioninfo", "devcatalog");
	}
	
	public String getChkDevVendor()
	{
		return getValue("actioninfo", "devvendor");
	}
	
	public String getChkDevType()
	{
		return getValue("actioninfo", "devtype");
	}
	
	public String getChkAtmcSoft()
	{
		return getValue("actioninfo", "atmcsoft");
	}
	
	public String getChkInstallType()
	{
		return getValue("actioninfo", "installtype");
	}
	
	public String getChkSerialNo()
	{
		return getValue("actioninfo", "serialno");
	}
}
