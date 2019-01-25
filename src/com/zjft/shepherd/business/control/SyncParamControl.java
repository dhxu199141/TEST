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

//ͬ��Ӧ�ò���
public class SyncParamControl {
	private static Log log = LogFactory.getLog(SyncParamControl.class);
	private static final String cmdId = "200040";

	// ֪ͨc��ͬ��Ӧ�ã��Ƿ�ɹ���c�˾�����v��ֻ����֪ͨ�ӿڣ�����Ҫ���ؽ��,��¼���������Ϣ��Զ�̿�����ˮ��
	public Map<String, Object> remoteControl(Map<String, Object> pHashMap) {
		Map<String, Object> rHashMap=new HashMap<String, Object>();
		String[] devInfo= (String[]) pHashMap.get("devInfo");
		String remotePort = pHashMap.get("remotePort").toString();
		for(int i=0;i<devInfo.length;i++)
		{
			String[] tempRemote = devInfo[i].split("\\|");
			int packetLen = 8000;
			int vzipType=3;
			String devNo = tempRemote[0];
			String ip = tempRemote[1];
			try
			{
				StringBuffer retBody = new StringBuffer();
				//boolean commResult = SocketUtil.sendCmdToRvcMutil(genOutXml(devNo, ip, userName, oldPassword, newPassword), ip, "50006", retBody, 20, 60, packetLen, vzipType);
				//���Ӳ����豸�ţ�ͨ����Ϣת������ȡ�豸�ļ�����Կ��������ת��    2011-8-22  mod by cy
				boolean commResult = MessageEncoded.sendCmdToRvcMutil(genOutXml(devNo, ip), ip, remotePort, retBody, 20, 60, packetLen, vzipType, devNo);
				log.info("teset!!!!!!!!!!!1+commResult="+commResult);
				boolean controlResult = false;
				StringBuffer resultContent = new StringBuffer();
				if(commResult)
				{
					String retcode = XmlUtil.getValue(retBody.toString(), "//root/retcode", "value");
					resultContent.append("�豸").append(devNo).append("ͬ��Ӧ�ò���");
					if(retcode != null && (retcode.equals("RMT000")||retcode.equals("000000")))
					{
						resultContent.append("�ɹ�");
						controlResult = true;
					}
					else
					{
						resultContent.append("ʧ��");
					}	
				}
				else
				{
					resultContent.append("��Զ���豸ͨѶʧ��");
				}
				//���ƽ�������б�
				List<Object> resultList = new ArrayList<Object>();	
				/*�豸�š�IP�����������������͡��Ƿ��н���ļ����ļ����ơ�����*/
				//resultList.add(new Object[]{tempRemote[0],tempRemote[1], controlResult, "0", resultContent, "", "", ""});
				saveRemoteTrace(cmdId, pHashMap.get("user").toString(), devNo,(controlResult ? "�ɹ�" : "ʧ��"), "֪ͨC��ͬ��Ӧ�ò���");
				rHashMap.put(devNo, 1);
				//rHashMap.put(devNo+"resultList", resultList);
			}
			catch(Exception e)
			{
				log.info("Զ�̿���ʧ�ܣ�ʧ��ԭ��:"+e) ;
				//e.printStackTrace();
				rHashMap.put(devNo, "0");
				saveRemoteTrace(cmdId, pHashMap.get("user").toString(), devNo, "ʧ��", "֪ͨC��ͬ��Ӧ�ò���");
				//rHashMap.put(devNo+"retMsg", "��Զ���豸ͨѶ����");
			}
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
		xml.append("<actioninfo time=\"").append(CalendarUtil.getDate()).append(" ").append(CalendarUtil.getTime()).append("\"")
		   .append(" downloaddatatype=\"").append("1").append("\"/>");
		xml.append("<managerinfo ipaddress=\"").append(ip).append("\"/>") ;
		xml.append("<remote ipaddress=\"").append(ip).append("\"")
		   .append(" serialno=\"").append(devNo).append("\"/>");
		xml.append("</root>");
		System.out.println("send:" + xml.toString());
		log.debug("send:" + xml.toString());
		return xml.toString();
	}
	
	/**
	 * ��¼Զ�̿��Ʋ������
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
		   "','�豸��" + "|" +devNo+ "|" + "���Ʋ���Ϊ" + "|" + opContent+
		   "')";
		   DbProxoolUtil.insert(remote_trace);
	}
}
