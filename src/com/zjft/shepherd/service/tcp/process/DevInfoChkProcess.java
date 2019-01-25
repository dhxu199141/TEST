package com.zjft.shepherd.service.tcp.process;

import java.io.IOException;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zjft.shepherd.common.CalendarUtil;
import com.zjft.shepherd.common.SystemCons;
import com.zjft.shepherd.dao.DevInfoDAO;
import com.zjft.shepherd.service.tcp.message.DevInfoChkMessage;
import com.zjft.shepherd.vo.ChkDevInfo;

public class DevInfoChkProcess extends CommonProcess
{
	private static Log log = LogFactory.getLog(DevInfoChkProcess.class);
	private String termnoChecked = "0";
	private String ipChecked = "0";
	private String orgNoChecked = "0";
	private String atmvIpChecked = "0";
	private String idBlkChecked = "1";//默认不校验
	private String devCatalogChecked = "0";
	private String devVendorChecked = "0";
	private String devTypeChecked = "0";
	private String atmcSoftChecked = "0";
	private String installTypeChecked = "0";
	private String serialNoChecked = "0";
	
	public boolean process(DevInfoChkMessage message, Socket socket)
	{
		try
		{
			ChkDevInfo chkDevInfo = DevInfoDAO.getChkDevInfo(message.getChkTermno());
			if(chkDevInfo != null)
			{
				termnoChecked = "1";
				if(this.sameAs(chkDevInfo.getIp(), message.getChkIp()))
				{
					ipChecked = "1";
				}
				if(this.sameAs(chkDevInfo.getOrgNo(), message.getChkOrgNo()))
				{
					orgNoChecked = "1";
				}
				if(this.sameAs(SystemCons.getAtmvIp(), message.getChkAtmvIp()))
				{
					atmvIpChecked = "1";
				}
				if(this.sameAs(chkDevInfo.getDevCatalog(), message.getChkDevCatalog()))
				{
					devCatalogChecked = "1";
				}
				if(this.sameAs(chkDevInfo.getDevVendor(), message.getChkDevVendor()))
				{
					devVendorChecked = "1";
				}
				if(this.sameAs(chkDevInfo.getDevType(), message.getChkDevType()))
				{
					devTypeChecked = "1";
				}
				if(this.sameAs(chkDevInfo.getAtmcSoft(), message.getChkAtmcSoft()))
				{
					atmcSoftChecked = "1";
				}
				if(this.sameAs(chkDevInfo.getInstallType(), message.getChkInstallType()))
				{
					installTypeChecked = "1";
				}
				if(this.sameAs(chkDevInfo.getSerialNo(), message.getChkSerialNo()))
				{
					serialNoChecked = "1";
				}
			}
			sendDataToClient(socket, this.genOutXml(message, "000000", "已校验设备基本信息"));
			//需要验证密码正确性，过程忽略

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
	 * 生成110002返回报文
	 * @return 110002返回报文
	 **/
	private String genOutXml(DevInfoChkMessage message, String code, String remark)
	{
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append("<root>");
		xml.append("<cmdid value=\"110002\"/>");
		xml.append("<msgid value=\"").append(message.getMsgid()).append("\"/>");
		xml.append("<cmddatetime date=\"").append(CalendarUtil.getDate()).append("\"")
		   .append(" time=\"").append(CalendarUtil.getTime()).append("\"/>");
		xml.append("<operatorinfo userid=\"zjft\"/>");
		xml.append("<remote termno=\"").append(message.getTermno()).append("\"")
		   .append(" ipaddress =\"").append(message.getIpaddress()).append("\"/>");
		xml.append("<retcode value=\"").append(code).append("\"")
		   .append(" remark=\"").append(remark).append("\"")
		   .append(" termnochecked=\"").append(termnoChecked).append("\"")
		   .append(" ipchecked=\"").append(ipChecked).append("\"")
		   .append(" orgnochecked=\"").append(orgNoChecked).append("\"")
		   .append(" atmvipchecked=\"").append(atmvIpChecked).append("\"")
		   .append(" idblkchecked=\"").append(idBlkChecked).append("\"")
		   .append(" devcatalogchecked=\"").append(devCatalogChecked).append("\"")
		   .append(" devvendorchecked=\"").append(devVendorChecked).append("\"")
		   .append(" devtypechecked=\"").append(devTypeChecked).append("\"")
		   .append(" atmcsoftchecked=\"").append(atmcSoftChecked).append("\"")
		   .append(" installtypechecked=\"").append(installTypeChecked).append("\"")
		   .append(" serialnochecked=\"").append(serialNoChecked).append("\"/>");
		xml.append("</root>");
		System.out.println("send:" + xml.toString());
		log.debug("send:" + xml.toString());
		return xml.toString();
	}
	/**
	 * 比较2个字符，任意一个为空或者2个字符内容不同，返回false。
	 * 如果2个字符内容相同返回true
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
