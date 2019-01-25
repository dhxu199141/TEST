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
* <p>Title:��ȡ�豸����ˮ�ļ���Ϣ </p> 
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
				reason = "��Զ���豸ͨѶ����!";
			}	
			else
			{	
				/*���ر���*/
				  result = true;
			      rHashMap.put("retBody", retBody.toString());
			}
			
			rHashMap.put("result", result);
			rHashMap.put("reason", reason);
			
			}catch(Exception e){
				rHashMap.put("result", false);
				rHashMap.put("reason", "��ȡ�豸����ˮ�ļ���Ϣ�����쳣��");
			}

		return rHashMap;
	
	}
	
	
	  /**
	   * ������:generateCXML  
	   * �������ܣ���֯����C���ļ����Եı��� 
	   * ������̣� 
	   * ������������� 
	   * ������������� 
	   * �쳣���������� 
	   * �����ˣ�  zhangdd
	   * �޸��ˣ�  
	   * �޸�ʱ�䣺  2016-6-15
	   * �޸ı�ע��
	   */
	  private String generateCXML(Map<String, Object> paramMap){
	    String devNo = StringUtil.parseString(paramMap.get("devNo"));
	    String ip = StringUtil.parseString(paramMap.get("ip"));
	    String properties = StringUtil.parseString(paramMap.get("properties"));
	    List<String> dateList = (List<String>)paramMap.get("dateList");
	    
	    String viewpath=StringUtil.parseString(paramMap.get("viewpath"));
	    
	    /*�༭������Ϣ*/
	    StringBuffer xml = new StringBuffer();
	    
	    /**��֯�汾���Ʊ���*/
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

