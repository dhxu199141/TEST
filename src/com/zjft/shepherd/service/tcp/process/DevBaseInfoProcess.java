package com.zjft.shepherd.service.tcp.process;

import java.io.IOException;
import java.net.Socket;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zjft.shepherd.common.CalendarUtil;
import com.zjft.shepherd.dao.DevInfoDAO;
import com.zjft.shepherd.service.tcp.message.DevBaseInfoMessage;
import com.zjft.shepherd.vo.ChkDevInfo;

public class DevBaseInfoProcess extends CommonProcess
{
	private static Log log = LogFactory.getLog(DevBaseInfoProcess.class);
	
	public boolean process(DevBaseInfoMessage message, Socket socket)
	{
		try
		{
			sendDataToClient(socket, this.genOutXml(message, "000000", "�յ��豸������Ϣ����"));
			if(message.getChkTermno() == null || message.getChkTermno().equals(""))
			{
				return true;
			}
			
			boolean sameDevice = false;
			ChkDevInfo chkDevInfo = DevInfoDAO.getChkDevInfo(message.getChkTermno());
			if(chkDevInfo != null)
			{
				sameDevice = this.sameAs(chkDevInfo.getIp(), message.getChkIp())
						&& this.sameAs(chkDevInfo.getOrgNo(), message.getChkOrgNo())
						&& this.sameAs(chkDevInfo.getDevCatalog(), message.getChkDevCatalog())
						&& this.sameAs(chkDevInfo.getDevVendor(), message.getChkDevVendor()) 
						&& this.sameAs(chkDevInfo.getDevType(), message.getChkDevType())
						&& this.sameAs(chkDevInfo.getAtmcSoft(), message.getChkAtmcSoft()) 
						&& this.sameAs(chkDevInfo.getInstallType(), message.getChkInstallType())
						&& this.sameAs(chkDevInfo.getSerialNo(), message.getChkSerialNo());
			}
			
			//�жϲ�����Ϣ�Ƿ��Ѿ����ڣ����������ɾ��
			if(DevInfoDAO.isDiffInfoExists(message.getChkTermno()))
			{
				DevInfoDAO.delDiffInfo(message.getChkTermno());
			}
			//����豸��Ϣ��һ������Ҫ�ǿ�
			if(!sameDevice)
			{
				DevInfoDAO.saveDiffInfo(chkDevInfo, message);
			}
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
	 * ����110001���ر���
	 * @since 2010.06.08
	 * @param code ������
	 * @param remark ������
	 * @return 400001���ر���cancelJobManagerMessage
	 **/
	public String genOutXml(DevBaseInfoMessage message, String code, String remark)
	{
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append("<root>");
		xml.append("<cmdid value=\"120001\"/>");
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
	/**
	 * �Ƚ�2���ַ�������һ��Ϊ�ջ���2���ַ����ݲ�ͬ������false��
	 * ���2���ַ�������ͬ����true
	 * */
	private boolean sameAs(String arg1, String arg2)
	{
		if(arg1 == null || arg1.equals("") || arg2 == null || arg2.equals(""))
		{
			return false;
		}
		else if(arg1.equalsIgnoreCase(arg2))
		{
			return true;
		}
		return false;
	}
}
