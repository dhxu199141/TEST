package com.zjft.shepherd.service.tcp.process;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zjft.shepherd.common.CalendarUtil;
import com.zjft.shepherd.service.tcp.message.FirstBootMessage;

public class FirstBootProcess extends CommonProcess
{
	private static Log log=LogFactory.getLog(FirstBootProcess.class);
	
	public void process(FirstBootMessage message, Socket socket)
	{
		try
		{
			//需要验证密码正确性，过程忽略
			sendDataToClient(socket, this.genOutXml(message, "000000", "已收到初次开机报告"));
			if(true)//当某个条件时发送文件版本校验交易
			{
				AgentFileChkProcess agentFileChkProcess = new AgentFileChkProcess();
				agentFileChkProcess.process(message, socket);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
		finally
		{
			if(socket != null)
			{
				try
				{
					socket.close();
				}
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 生成110001返回报文
	 * @since 2010.06.08
	 * @param code 返回码
	 * @param remark 处理结果
	 * @return 400001返回报文cancelJobManagerMessage
	 **/
	public String genOutXml(FirstBootMessage message, String code, String remark)
	{
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append("<root>");
		xml.append("<cmdid value=\"900007\"/>");
		xml.append("<msgid value=\"").append(UUID.randomUUID().toString()).append("\"/>");
		xml.append("<cmddatetime date=\"").append(CalendarUtil.getDate()).append("\"")
		   .append(" time=\"").append(CalendarUtil.getTime()).append("\"/>");
		xml.append("<operatorinfo userid=\"zjft\"/>");
		xml.append("<remote ipaddress=\"").append(message.getTermno()).append("\"")
		   .append(" termno=\"").append(message.getIpaddress()).append("\"/>");
		xml.append("<retcode value=\"").append(code).append("\"")
		   .append(" remark=\"").append(remark).append("\"/>");
		xml.append("</root>");
		System.out.println("send:" + xml.toString());
		log.debug("send:" + xml.toString());
		return xml.toString();
	}
}
