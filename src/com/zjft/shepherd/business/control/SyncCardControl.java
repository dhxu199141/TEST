package com.zjft.shepherd.business.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zjft.shepherd.common.CalendarUtil;
import com.zjft.shepherd.common.DbProxoolUtil;
import com.zjft.shepherd.common.XmlUtil;

//同步卡表
public class SyncCardControl {
	private static Log log = LogFactory.getLog(SyncCardControl.class);
	private static final String cmdId = "200040";

	// 远程控制
	public Map<String, Object> remoteControl(Map<String, Object> pHashMap) {
		Map<String, Object> rHashMap=new HashMap<String, Object>();
		String[] devInfo= (String[]) pHashMap.get("devInfo");
		String[] tempRemote = devInfo[0].split("\\|");
		int packetLen = Integer.valueOf(tempRemote[2].toString());
		int vzipType=Integer.valueOf(tempRemote[3].toString());
		String devNo = tempRemote[0];
		String ip = tempRemote[1];
		try
		{
			StringBuffer retBody = new StringBuffer();
			//boolean commResult = SocketUtil.sendCmdToRvcMutil(genOutXml(devNo, ip, userName, oldPassword, newPassword), ip, "50006", retBody, 20, 60, packetLen, vzipType);
			//增加参数设备号，通过消息转发，获取设备的加密密钥，并进行转发    2011-8-22  mod by cy
			boolean commResult = MessageEncoded.sendCmdToRvcMutil(genOutXml(devNo, ip), ip, "50006", retBody, 20, 60, packetLen, vzipType, devNo);
			boolean controlResult = false;
			StringBuffer resultContent = new StringBuffer();
			if(commResult)
			{
				String retcode = XmlUtil.getValue(retBody.toString(), "//root/retcode", "value");
				resultContent.append("同步设备").append(devNo).append("上卡表信息");
				if(retcode != null && (retcode.equals("RMT000")||retcode.equals("000000")))
				{
					resultContent.append("成功");
					controlResult = true;
				}
				else
				{
					resultContent.append("失败");
				}	
			}
			else
			{
				resultContent.append("与远程设备通讯失败");
			}
			//控制结果返回列表
			List<Object> resultList = new ArrayList<Object>();	
			/*设备号、IP、操作结果、结果解释、是否有结果文件、文件名称、连接*/
			resultList.add(new Object[]{tempRemote[0],tempRemote[1], controlResult, "0", resultContent, "", "", ""});
			saveRemoteTrace(cmdId, pHashMap.get("user").toString(), devNo,(controlResult ? "成功" : "失败"), "同步卡表");
			rHashMap.put("retCode", 1);
			rHashMap.put("resultList", resultList);
		}
		catch(Exception e)
		{
			log.info("远程控制失败，失败原因:"+e) ;
			//e.printStackTrace();
			rHashMap.put("retCode", "0");
			saveRemoteTrace(cmdId, pHashMap.get("user").toString(), devNo, "失败", "同步卡表");
			rHashMap.put("retMsg", "与远程设备通讯出错");
		}
		return rHashMap;
		
	}
	
	private String genOutXml(String devNo, String ip)
	{
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append("<root>");
		xml.append("<cmdid value=\"200040\"/>");
		xml.append("<cmddatetime date=\"").append(CalendarUtil.getDate()).append("\"")
		   .append(" time=\"").append(CalendarUtil.getTime()).append("\"/>");
		xml.append("<operatorinfo userid=\"zjft\"/>");
		xml.append("<actioninfo time=\"").append(CalendarUtil.getDate()).append(" ").append(CalendarUtil.getTime()).append("\"/>");
		xml.append("<managerinfo ipaddress=\"").append(ip).append("\"/>") ;
		xml.append("<remote ipaddress=\"").append(ip).append("\"")
		   .append(" serialno=\"").append(devNo).append("\"/>");
		xml.append("</root>");
		System.out.println("send:" + xml.toString());
		log.debug("send:" + xml.toString());
		return xml.toString();
	}
	
	/**
	 * 记录远程控制操作结果
	 * */
	private void saveRemoteTrace(String cmdName,String opNo,String devNo,String result,String opContent)
	{
		String remote_trace="insert into remote_trace (logic_id,command_id,dev_no,op_no,op_time,status,content) values ('"+
		   UUID.randomUUID().toString()+
		   "','"+cmdName+
		   "','"+devNo+
		   "','"+opNo+
		   "','"+CalendarUtil.getSysTimeYMDHMS()+
		   "','"+result+
		   "','设备号" + "|" +devNo+ "|" + "控制操作为" + "|" + opContent+
		   "')";
		   DbProxoolUtil.insert(remote_trace);
	}
}
