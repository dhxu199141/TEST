package com.zjft.shepherd.service.tcp.message;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class CommonMessage 
{
	private Document doc = null;
	
	public CommonMessage(Document doc)
	{
		this.doc = doc;
	}
	
	/**
	 * 获取DOM某个节点的内容
	 * @since 2009.05.01
	 * @param nodeName 节点名称
	 * @return 节点内容 value
	 **/		
	protected String getValue(String nodeName)
	{
		NodeList nodes = doc.getElementsByTagName(nodeName);
		Node node = nodes.item(0);
		
		return node.getTextContent();
	}
	
	/**
	 * 获取DOM节点的属性值
	 * @since 2009.05.01
	 * @param nodeName 节点名称
	 * @param attrName 属性名称
	 * @return 节点的属性值 value
	 **/		
	protected String getValue(String nodeName,String attrName)
	{
		try
		{
			NodeList nodes = doc.getElementsByTagName(nodeName);
			Node node = nodes.item(0);
			if(node==null)
			{
				return "";
			}		
			NamedNodeMap attrs = node.getAttributes();
			return attrs.getNamedItem(attrName).getNodeValue();
		}
		catch(Exception e)
		{
			return "";
		}
	}

	public String getCmdid()
	{
		return getValue("cmdid", "value");
	}
	
	public String getMsgid()
	{
		return getValue("msgid", "value");
	}
	
	public String getDate()
	{
		return getValue("cmddatetime", "date");
	}
	
	public String getTime()
	{
		return getValue("cmddatetime", "time");
	}
	
	public String getIpaddress()
	{
		return getValue("remote", "ipaddress");
	} 
	
	public String getTermno()
	{
		return getValue("remote", "termno");
	}
	
	public String getRetcode()
	{
		return getValue("retcode", "value");
	}
	
	public String getRemark()
	{
		return getValue("retcode", "remark");
	}
	
	public Document getDoc() 
	{
		return doc;
	}

	public void setDoc(Document doc) 
	{
		this.doc = doc;
	}
}
