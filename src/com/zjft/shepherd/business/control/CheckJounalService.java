package com.zjft.shepherd.business.control;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.NDC;
import com.zjft.shepherd.common.CalendarUtil;
import com.zjft.shepherd.common.StringUtil;


/** 
* <p>Title:获取设备端流水文件信息 </p> 
* @author:zhangdd
* @version 1.0 
*/ 
public class CheckJounalService
{	
	private static Log log=LogFactory.getLog(CheckJounalService.class.getName());	

	public Map<String, Object> checkJounal(Map<String, Object> paraMap) {
		// TODO Auto-generated method stub		
		HashMap<String, Object> rHashMap=new HashMap<String, Object>();
	    String xml = generateCXML(paraMap);
	    String devNo = StringUtil.parseString(paraMap.get("devNo"));
	    String ip = StringUtil.parseString(paraMap.get("ip"));
	    String port = StringUtil.parseString(paraMap.get("port"));
	    int packetLen = Integer.parseInt(StringUtil.parseString(paraMap.get("packetLen")));
	    int vzipType = Integer.parseInt(StringUtil.parseString(paraMap.get("vzipType")));
	    int soTime = Integer.parseInt(StringUtil.parseString(paraMap.get("soTime")));

	    boolean result = false;
		String reason = "";

	    try{
		    StringBuffer retBody=new StringBuffer();

			NDC.push(ip) ;
			boolean cmdFlag=MessageEncoded.sendCmdToRvcMutil(xml,ip,port,retBody,20,soTime,packetLen,vzipType, devNo);
			NDC.pop() ;

			if(!cmdFlag)
			{
				result = false;
				reason = "与远程设备通讯出错!";
			}	
			else
			{	
				/*返回报文*/
				  result = true;
			      rHashMap.put("retBody", retBody.toString());
			}
			
			rHashMap.put("result", result);
			rHashMap.put("reason", reason);
			
			}catch(Exception e){
				rHashMap.put("result", false);
				rHashMap.put("reason", "获取设备端流水文件信息发生异常！");
			}

		return rHashMap;
	
	}
	
	
	  /**
	   * 函数名:generateCXML  
	   * 函数功能：组织请求C端文件属性的报文 
	   * 处理过程： 
	   * 输入参数描述： 
	   * 输出参数描述： 
	   * 异常处理描述： 
	   * 创建人：  zhangdd
	   * 修改人：  
	   * 修改时间：  2016-6-15
	   * 修改备注：
	   */
	  private String generateCXML(Map<String, Object> paramMap){
	    String devNo = StringUtil.parseString(paramMap.get("devNo"));
	    String ip = StringUtil.parseString(paramMap.get("ip"));
	    String properties = StringUtil.parseString(paramMap.get("properties"));
	    List<String> dateList = (List<String>)paramMap.get("dateList");
	    
	    String viewpath=StringUtil.parseString(paramMap.get("viewpath"));
	    
	    /*编辑报文信息*/
	    StringBuffer xml = new StringBuffer();
	    
	    /**组织版本控制报文*/
	    xml.append("<?xml version=\"1.0\"?>");
	    xml.append("<root>");
	    xml.append("<cmdid value=\"200052\"/>");
	    xml.append("<msgid value=\"").append(UUID.randomUUID().toString()).append("\"/>");
	    xml.append("<cmddatetime date=\"").append(CalendarUtil.getSysTimeYMD()).append("\" time=\"").append(CalendarUtil.getSysTimeHMS()).append("\"/>");
	    xml.append("<remote ipaddress=\"").append(ip).append("\" termno=\"").append(devNo).append("\"/>");
	    xml.append("<checkinfo>");
	    for (String date : dateList) {
	      xml.append("<file map=\"").append(properties).append("\" path=\"").append(viewpath + date).append(".J\"/>");
	    }
	    xml.append("</checkinfo>");
	    xml.append("</root>");

	    log.debug(xml.toString());
	    return xml.toString();
	  }
}

