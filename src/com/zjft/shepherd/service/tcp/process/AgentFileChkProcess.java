package com.zjft.shepherd.service.tcp.process;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import com.zjft.shepherd.common.CalendarUtil;
import com.zjft.shepherd.common.DocumentUtil;
import com.zjft.shepherd.common.SocketUtil;
import com.zjft.shepherd.service.tcp.message.CommonMessage;
import com.zjft.shepherd.service.tcp.message.FirstBootMessage;
import com.zjft.shepherd.service.tcp.message.FirstBootMessage.ProjectInfo;

public class AgentFileChkProcess extends CommonProcess
{
	private static Log log=LogFactory.getLog(AgentFileChkProcess.class);
	
	public void process(FirstBootMessage message, Socket socket)
	{
		List<ProjectInfo> projectInfo =  message.getProjinfo();
		//此处缺少确定校验哪个版本的逻辑
		InetSocketAddress add = null;
		Socket client = null;
		try
		{
			add = new InetSocketAddress(message.getIpaddress(), 50006);
			client = new Socket();
			client.connect(add, 20 * 1000);
			client.setSoTimeout(600 * 1000);
			
			boolean result = this.sendDataToClient(client, this.genOutXml(message));
			if(result)
			{
				String xml = SocketUtil.readSocket2(new DataInputStream(client.getInputStream()));
				System.out.println(xml);
				Document doc = DocumentUtil.convertTextToDOM(xml);
				CommonMessage retMessage = new CommonMessage(doc);
				//此处缺少后续处理逻辑				
			}
		} 
		catch (ZipException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}

	}
	
	private String genOutXml(FirstBootMessage message)
	{
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append("<root>");
		xml.append("<cmdid value=\"100003\"/>");
		xml.append("<msgid value=\"").append(UUID.randomUUID().toString()).append("\"/>");
		xml.append("<cmddatetime date=\"").append(CalendarUtil.getDate()).append("\"")
		   .append(" time=\"").append(CalendarUtil.getTime()).append("\"/>");
		xml.append("<operatorinfo userid=\"zjft\"/>");
		xml.append("<actioninfo filename=\"").append("D:/shepherd/FileCheckTemplet.txt").append("\"/>");
		xml.append("<remote termno=\"").append(message.getTermno()).append("\"")
		   .append(" ipaddress=\"").append(message.getIpaddress()).append("\"/>");
		xml.append("</root>");
		System.out.println("send:" + xml.toString());
		return xml.toString();
	}
}
