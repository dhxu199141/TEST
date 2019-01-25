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

public class OsUsbControl {
	private static Log log = LogFactory.getLog(OsUsbControl.class);
	private static final String cmdId = "200034";

	// Զ�̿���
	public Map<String, Object> remoteControl(Map<String, Object> pHashMap) {
		Map<String, Object> rHashMap=new HashMap<String, Object>();
		String[] devInfo= (String[]) pHashMap.get("devInfo");
		String[] tempRemote = null;
		StringBuffer retBody = null;

		//��ȡusb����ѡ��
		String usbOption = (String) pHashMap.get("usbOption");//0���رգ�1������
		String content = null;
		if(usbOption != null && usbOption.equals("1"))
		{
			content = "����";
		}
		else
		{
			usbOption = "0";
			content = "�ر�";
		}
		//���ƽ�������б�
		List<Object> resultList = new ArrayList<Object>();	
		try
		{
			for(int i = 0; i < devInfo.length ; i++)
			{
				tempRemote = devInfo[i].split("\\|");//������0���豸�ţ�1��ip��2��������С��3��ѹ����ʽ
				retBody = new StringBuffer();
				//boolean commResult = SocketUtil.sendCmdToRvcMutil(genOutXml(tempRemote[0], tempRemote[1], usbOption), tempRemote[1], "50006", retBody, 20, 60, Integer.valueOf(tempRemote[2].toString()), Integer.valueOf(tempRemote[3].toString()));
				//���Ӳ����豸�ţ�ͨ����Ϣת������ȡ�豸�ļ�����Կ��������ת��    2011-8-22  mod by cy
				boolean commResult = MessageEncoded.sendCmdToRvcMutil(genOutXml(tempRemote[0], tempRemote[1], usbOption), tempRemote[1], "50006", retBody, 20, 60, Integer.valueOf(tempRemote[2].toString()), Integer.valueOf(tempRemote[3].toString()), tempRemote[0]);
				boolean controlResult = false;
				StringBuffer resultContent = new StringBuffer();
				if(commResult)
				{
					String retcode = XmlUtil.getValue(retBody.toString(), "//root/retcode", "value");
					resultContent.append(content).append("�豸").append(tempRemote[0]).append("usb�洢�豸Ȩ��");
					if(retcode != null && retcode.equals("RMT000"))
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
				/*�豸�š�IP�����������������͡��Ƿ��н���ļ����ļ����ơ�����*/
				resultList.add(new Object[]{tempRemote[0],tempRemote[1], controlResult, "0", resultContent, "", "", ""});
				saveRemoteTrace(cmdId, pHashMap.get("user").toString(), tempRemote[0], (controlResult ? "�ɹ�" : "ʧ��"), content+"usb�洢�豸Ȩ��");
			}

			rHashMap.put("retCode", 1);
			rHashMap.put("resultList", resultList);
		}
		catch(Exception e)
		{
			e.printStackTrace();
			rHashMap.put("retCode", "0");
			saveRemoteTrace(cmdId, pHashMap.get("user").toString(), tempRemote[0], "ʧ��", content+"usb�洢�豸Ȩ��");
			rHashMap.put("retMsg", "��Զ���豸ͨѶ����");
		}
		return rHashMap;
		
	}
	
	private String genOutXml(String devNo, String ip, String usbOption)
	{
		StringBuffer xml = new StringBuffer();
		xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		xml.append("<root>");
		xml.append("<cmdid value=\"200034\"/>");
		xml.append("<msgid value=\"").append(UUID.randomUUID().toString()).append("\"/>");
		xml.append("<cmddatetime date=\"").append(CalendarUtil.getDate()).append("\"")
		   .append(" time=\"").append(CalendarUtil.getTime()).append("\"/>");
		xml.append("<operatorinfo userid=\"zjft\"/>");
		xml.append("<actioninfo usboption=\"").append(usbOption).append("\"/>");
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
		   "','�豸��" +devNo+ "���Ʋ���Ϊ:" + opContent+
		   "')";
		   DbProxoolUtil.insert(remote_trace);
	}
	
}
