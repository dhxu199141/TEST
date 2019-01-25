package com.zjft.shepherd.business.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.NDC;

import com.zjft.shepherd.common.CalendarUtil;
import com.zjft.shepherd.common.SocketUtil;
import com.zjft.shepherd.common.SystemLanguage;
import com.zjft.shepherd.common.XmlUtil;
import com.zjft.shepherd.dao.RemoteControlDAO;

/**
 * ����/�رմ����ֺ�ƾ����ӡ������/�ر�ȡ����ֺ�ƾ����ӡ
 * 
 * @author qfxu
 *
 * @date 2013-12-23
 *
 */
public class NtsReceiptPrintControl {
	
	private static Log log = LogFactory.getLog(ChangePwdControl.class);
	private static final String cmdId = "200030";
	
	public NtsReceiptPrintControl() {
	}
	
	/**
	 * Զ�̿��ƣ�����Զ�̿��Ʊ��ģ��Է��صı��Ľ��д���
	 * 
	 * @param pHashMap
	 * @return
	 */
	public Map<String, Object> remoteControl(Map<String, Object> pHashMap) {
		String cmdId = pHashMap.get("cmdId").toString();
		String[] devInfo= (String[]) pHashMap.get("devInfo");
		String fileFlag = pHashMap.get("fileFlag").toString();//�Ƿ�Ҫ��ȡ�ļ���־	
		
		Map<String, Object> rHashMap = new HashMap<String, Object>();
		
		String[] tempRemote=null;
		List resultList = new ArrayList();//�豸��Ϣ�б�			
		boolean result ;
		
		String remotePort = pHashMap.get("remotePort").toString();
		
		StringBuffer retBody =new StringBuffer();						
		String msgId = null; 		
		String remoteFile = null;
		String localFile = null;
		String fileName = null;
		String fileProp = null;
		String resultContent = SystemLanguage.getSrcCommFai();
		String retcode=null;
		//ȡ�ó�ʱʱ��
		int soTime=SocketUtil.getsoTime(cmdId);
		
		for(int i=0;i<devInfo.length;i++) {
			tempRemote = devInfo[i].split("\\|");				
			int packetLen=Integer.valueOf(tempRemote[2].toString());
			int vzipType=Integer.valueOf(tempRemote[3].toString());
			
			msgId = UUID.randomUUID().toString();
			
			try {
			    //���Ӳ����豸�ţ�ͨ����Ϣת������ȡ�豸�ļ�����Կ��������ת��
				NDC.push(tempRemote[1]) ;
				result = MessageEncoded.sendCmdToRvcMutil(this.genXML(cmdId, msgId, tempRemote[0], tempRemote[1], localFile,remoteFile), 
						tempRemote[1], remotePort, retBody,20,soTime,packetLen,vzipType, tempRemote[0]);
				NDC.pop() ;
			
				if (result) {
					retcode = XmlUtil.getValue(retBody.toString(), "//root/retcode", "value");				
					if(retcode.equals("RMT000") || retcode.equals("000000")) {
						resultContent= convertCommandId(cmdId)+ "|" + SystemLanguage.getSrcOperateSuccess();
						
						// Զ�̿��Ƴɹ������»���״̬
						if (cmdId.equals("20003060")) {
							RemoteControlDAO.updateRemoteCimsrpCdmsrpFlag(tempRemote[0], "cimsrp_Flag", 0);
						} else if (cmdId.equals("20003061")) {
							RemoteControlDAO.updateRemoteCimsrpCdmsrpFlag(tempRemote[0], "cimsrp_Flag", 1);
						} else if (cmdId.equals("20003070")) {
							RemoteControlDAO.updateRemoteCimsrpCdmsrpFlag(tempRemote[0], "cdmsrp_Flag", 0);
						} else if (cmdId.equals("20003071")) {
							RemoteControlDAO.updateRemoteCimsrpCdmsrpFlag(tempRemote[0], "cdmsrp_Flag", 1);
						}
					} else if(retcode.equals("RMT001") || retcode.equals("000001")) {
						resultContent = convertCommandId(cmdId)+ "|" + SystemLanguage.getSrcOperateFail();
						result = false;
					} else {
						resultContent = convertCommandId(cmdId)+ "|" + SystemLanguage.getSrcOperateFail();
						result = false;
					}
				}
				
				/*�豸�š�IP�����������������͡��Ƿ��н���ļ����ļ����ơ�����*/
				resultList.add(new Object[]{tempRemote[0],tempRemote[1],result,fileFlag,resultContent,localFile,fileName,fileProp});
				retBody.delete(0, retBody.length());
				resultContent = SystemLanguage.getSrcCommFai();
				if(pHashMap.get("batchFlag") != null && !pHashMap.get("batchFlag").equals("")){
					RemoteControlDAO.saveRemoteTrace(cmdId,pHashMap.get("user").toString(),tempRemote[0],(result?SystemLanguage.getControlRemoteControlResultSuccess():SystemLanguage.getMainFailed()),convertCommandId(cmdId) + "|" + "��������");
				}else{
					RemoteControlDAO.saveRemoteTrace(cmdId,pHashMap.get("user").toString(),tempRemote[0],(result?SystemLanguage.getControlRemoteControlResultSuccess():SystemLanguage.getMainFailed()),convertCommandId(cmdId));
				}
				
				rHashMap.put("retCode", 1);
				rHashMap.put("resultList", resultList);
			} catch (Exception e) {
				if(pHashMap.get("batchFlag")!=null&&!pHashMap.get("batchFlag").equals("")){
					RemoteControlDAO.saveRemoteTrace(cmdId,pHashMap.get("user").toString(),tempRemote[0],SystemLanguage.getMainFailed(),convertCommandId(cmdId) + "|" + "��������");
				}else{
					RemoteControlDAO.saveRemoteTrace(cmdId,pHashMap.get("user").toString(),tempRemote[0],SystemLanguage.getMainFailed(),convertCommandId(cmdId));
				}
				rHashMap.put("retCode", 0);
				rHashMap.put("retMsg", SystemLanguage.getSrcCommRemFai());
//				rHashMap.put("retMsg", "��Զ���豸ͨѶ����");
			}
		}
		return rHashMap;
		
	}
	
	/**
	 * ���ɿ����رմ�ȡ����ֺ�ƾ����ӡ�ı���
	 * 
	 * @param cmdId
	 * @param msgId
	 * @param devNo
	 * @param ip
	 * @param localFile
	 * @param remoteFile
	 * @return
	 */
	private String genXML(String cmdId,String msgId,String devNo,String ip,String localFile,String remoteFile) {
		String cimsrpFlag = "";
		String cdmsrpFlag = "";
		if (cmdId != null && cmdId.equals("20003060")){ //  �رմ����ֺ�ƾ����ӡ(cimsrpFlag)
			cimsrpFlag = "0";
		} else if (cmdId != null && cmdId.equals("20003061")){ // ���������ֺ�ƾ����ӡ(cimsrpFlag)
			cimsrpFlag = "1";
		} else if (cmdId != null && cmdId.equals("20003070")){ // �ر�ȡ����ֺ�ƾ����ӡ(cdmsrpFlag)
			cdmsrpFlag = "0";
		} else if (cmdId != null && cmdId.equals("20003071")){ // ����ȡ����ֺ�ƾ����ӡ(cdmsrpFlag)
			cdmsrpFlag = "1";
		}
		
		StringBuffer xml = new StringBuffer();
		
		xml.append("<?xml version=\"1.0\"?>");
		xml.append("<root>");
		xml.append("<cmdid value=\"").append("200030").append("\"/>");
		xml.append("<msgid value=\"").append(UUID.randomUUID().toString()).append("\"/>");
		xml.append("<cmddatetime date=\"").append(CalendarUtil.getDate()).append("\" time=\"").append(CalendarUtil.getTime()).append("\"/>");
		xml.append("<operatorinfo userid=\"zjft\"/>");
		xml.append("<functioninfo paramtype1=\"\" paramtype2=\"\" paramtype3=\"\" paramtype4=\"\" paramtype5=\"\" paramtype6=\"").append(cimsrpFlag).append("\" paramtype7=\"").append(cdmsrpFlag).append("\"/>");
		xml.append("<actioninfo time=\"").append(CalendarUtil.getDate()).append(" ").append(CalendarUtil.getTime()).append("\"/>");
		xml.append("<managerinfo ipaddress=\"").append(ip).append("\"/>");
		xml.append("<remote ipaddress=\"").append(ip).append("\" termno=\"").append(devNo).append("\" serialno=\"").append(devNo).append("\"/>");
		xml.append("</root>");
		
		log.info("rmi generate:" + xml.toString());
		
		return xml.toString();
	}
	
	/**
	 * ��Զ������������Ϊ���ĺ���
	 * 
	 * @param command
	 *            Զ���������
	 * @return ����������ĺ���
	 */
	private String convertCommandId(String cmdId) {
		if (cmdId.equals("20003060")) {
			return SystemLanguage.getControlCloseCimsrp();
		} else if (cmdId.equals("20003061")) {
			return SystemLanguage.getControlOpenCimsrp();
		} else if (cmdId.equals("20003070")) {
			return SystemLanguage.getControlCloseCdmsrp();
		} else if (cmdId.equals("20003071")) {
			return SystemLanguage.getControlOpenCdmsrp();
		} else {
			return SystemLanguage.getSrcInvalidControl();
		}
	}
	
}
