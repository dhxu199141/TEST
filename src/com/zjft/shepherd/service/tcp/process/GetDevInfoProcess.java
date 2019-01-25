package com.zjft.shepherd.service.tcp.process;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.UUID;
import java.util.zip.ZipException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import com.zjft.shepherd.common.CalendarUtil;
import com.zjft.shepherd.common.DocumentUtil;
import com.zjft.shepherd.common.SocketUtil;
import com.zjft.shepherd.service.tcp.message.GetDevInfoMessage;

public class GetDevInfoProcess extends CommonProcess
{
	private static Log log = LogFactory.getLog(GetDevInfoProcess.class);
	
	public HashMap<String, Object>  ctrlHandler(HashMap<String, Object> paramMap)
	{
		HashMap<String, Object> resultMap = new HashMap<String, Object>();
		
		String devNo = (String)paramMap.get("devNo");
		String ip = (String)paramMap.get("ip");
		
		InetSocketAddress add = null;
		Socket client = null;
		try
		{
			add = new InetSocketAddress(ip, 50006);
			client = new Socket();
			client.connect(add, 20 * 1000);
			client.setSoTimeout(60 * 1000);
		}
		catch(Exception e)
		{
			resultMap.put("retCode", "0");
			resultMap.put("message", "与设备通讯失败");
			return resultMap;
		}
		
		try 
		{
			boolean result = this.sendDataToClient(client, this.genOutXml(devNo, ip));
			if(!result)
			{
				resultMap.put("retCode", "0");
				resultMap.put("message", "发送命令失败");
			}
			else
			{
				String xml = SocketUtil.readSocket2(new DataInputStream(client.getInputStream()));
				if(xml == null || xml.equals(""))
				{
					resultMap.put("retCode", "0");
					resultMap.put("message", "获取设备信息失败");
					return resultMap;
				}
				System.out.println("receive:");
				System.out.println(xml);
				Document doc = DocumentUtil.convertTextToDOM(xml);
				GetDevInfoMessage retMessage = new GetDevInfoMessage(doc);
				if(retMessage.getRetcode() != null && retMessage.getRetcode().equals("RMT000"))
				{
					resultMap.put("retCode", "1");
					resultMap.put("termno", retMessage.getRetTermno());
					resultMap.put("ip", retMessage.getRetIp());
					resultMap.put("orgno", retMessage.getRetOrgno());
					resultMap.put("atmvip", retMessage.getRetAtmvip());
					resultMap.put("idblk", retMessage.getRetIdblk());
					resultMap.put("devcategory", retMessage.getRetDevcategory());
					resultMap.put("devvendor", retMessage.getRetDevvendor());
					resultMap.put("devtype", retMessage.getRetDevtype());
					resultMap.put("atmcsoft", retMessage.getRetAtmcsoft());
					resultMap.put("installtype", retMessage.getRetInstalltype());
					resultMap.put("serialno", retMessage.getRetSerialno());
					resultMap.put("message", retMessage.getRemark());
				}
				else
				{
					resultMap.put("retCode", "0");
					resultMap.put("message", retMessage.getRemark());
				}
			}
		} 
		catch(ZipException e) 
		{
			e.printStackTrace();
			resultMap.put("retCode", "0");
			resultMap.put("message", "解压报文错误");
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			resultMap.put("retCode", "0");
			resultMap.put("message", "通讯异常");
		}
		catch(Exception e)
		{
			e.printStackTrace();
			resultMap.put("retCode", "0");
			resultMap.put("message", "获取设备信息失败");
		}
		return resultMap;
	}
	
	private String genOutXml(String devNo, String ip)
	{
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append("<root>");
		xml.append("<cmdid value=\"200035\"/>");
		xml.append("<msgid value=\"").append(UUID.randomUUID().toString()).append("\"/>");
		xml.append("<cmddatetime date=\"").append(CalendarUtil.getDate()).append("\"")
		   .append(" time=\"").append(CalendarUtil.getTime()).append("\"/>");
		xml.append("<operatorinfo userid=\"zjft\"/>");
		xml.append("<remote ipaddress=\"").append(ip).append("\"")
		   .append(" termno=\"").append(devNo).append("\"/>");
		xml.append("</root>");
		System.out.println("send:" + xml.toString());
		log.debug("send:" + xml.toString());
		return xml.toString();
	}

}
