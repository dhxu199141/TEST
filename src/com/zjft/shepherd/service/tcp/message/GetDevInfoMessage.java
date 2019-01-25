package com.zjft.shepherd.service.tcp.message;

import org.w3c.dom.Document;

public class GetDevInfoMessage extends CommonMessage
{

	public GetDevInfoMessage(Document doc) 
	{
		super(doc);
	}
	
	public String getRetTermno()
	{
		return getValue("retcode", "termno");
	}
	
	public String getRetIp()
	{
		return getValue("retcode", "ip");
	}
	
	public String getRetOrgno()
	{
		return getValue("retcode", "orgno");
	}
	
	public String getRetAtmvip()
	{
		return getValue("retcode", "atmvip");
	}
	
	public String getRetIdblk()
	{
		return getValue("retcode", "atmvip");
	}
	
	public String getRetDevcategory()
	{
		return getValue("retcode", "devcategory");
	}
	
	public String getRetDevvendor()
	{
		return getValue("retcode", "devvendor");
	}
	
	public String getRetDevtype()
	{
		return getValue("retcode", "devtype");
	}
	
	public String getRetAtmcsoft()
	{
		return getValue("retcode", "atmcsoft");
	}
	
	public String getRetInstalltype()
	{
		return getValue("retcode", "installtype");
	}
	
	public String getRetSerialno()
	{
		return getValue("retcode", "serialno");
	}
}
