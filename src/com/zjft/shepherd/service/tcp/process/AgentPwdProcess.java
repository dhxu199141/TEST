package com.zjft.shepherd.service.tcp.process;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zjft.shepherd.common.CalendarUtil;
import com.zjft.shepherd.common.SocketUtil;
import com.zjft.shepherd.dao.AgentPwdDAO;
import com.zjft.shepherd.service.tcp.message.AgentPwdMessage;
import com.zjft.shepherd.vo.AgentPwdInfo;

public class AgentPwdProcess 
{
	private static Log log=LogFactory.getLog(AgentPwdProcess.class);
	
	public boolean process(AgentPwdMessage message, Socket socket)
	{
		try
		{
			String pwd = message.getUninstallpassword();
			if(pwd == null || pwd.equals(""))
			{
				sendDataToClient(socket, this.genOutXml(message, "000001", "�ͻ�����������Ϊ��"));
				return false;
			}
			ArrayList<AgentPwdInfo> list = AgentPwdDAO.getAgentPwd(message.getTermno());
			String code = null;
			String remark = null;
			if(list == null || list.isEmpty())
			{
				code = "000001";
				remark = "δ��ȡ���豸��������agentж������";
			}
			else
			{
				for(AgentPwdInfo aPwdInfo : list)
				{
					if(aPwdInfo.getAgentPwd() == null || aPwdInfo.getAgentPwd().equals(""))
					{
						code = "000001";
						remark = "��������agentж������Ϊ��";
					}
					else if(!aPwdInfo.getAgentPwd().equalsIgnoreCase(pwd))
					{
						code = "000001";
						remark = "�ͻ����������벻��ȷ";
					}
					else if(aPwdInfo.getAgentValidDate() ==null || aPwdInfo.getAgentValidDate().compareTo(CalendarUtil.getSysTimeYMD()) < 0)
					{
						code = "000001";
						remark = "agentж���������";
					}
					else
					{
						code = "000000";
						remark = "������ȷ";
						break;
					}
				}
			}
			//System.out.println("��ѹ������룺"+ EcbDESUtil.dncryptData(pwd));
			sendDataToClient(socket, this.genOutXml(message, code, remark));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
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
		return true;
	}
	
	/**
	 * ��remote����110001���ر���
	 * @param request 110001���ر���
	 **/
	private boolean sendDataToClient(Socket socket, String xml)
	{
		if(socket==null||socket.isClosed())
		{
			log.error("sendRequest socket has closed!!!!");
			return false;
		}
		try 
		{
			return SocketUtil.sendDataToClient(xml.getBytes(), new DataOutputStream(socket.getOutputStream()), 8000, 2);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * ����110001���ر���
	 * @since 2010.06.08
	 * @param code ������
	 * @param remark ������
	 * @return 400001���ر���cancelJobManagerMessage
	 **/
	public String genOutXml(AgentPwdMessage message, String code, String remark)
	{
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append("<root>");
		xml.append("<cmdid value=\"110001\"/>");
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
		return xml.toString();
	}
}
