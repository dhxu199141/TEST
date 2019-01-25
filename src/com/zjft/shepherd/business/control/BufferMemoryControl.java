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
 * ����/�ر��ֽ𻺴桢����/�رչ��ֺŻ���Զ�̿��ƴ�����
 * 
 * @author qfxu
 * 
 * @date 2013-12-23
 * 
 */
public class BufferMemoryControl {

	private static Log log = LogFactory.getLog(BufferMemoryControl.class);
	private static final String cmdId = "200043";

	/**
	 * Զ�̿��ƣ�����Զ�̿��Ʊ��ģ��Է��صı��Ľ��д���
	 * 
	 * @param pHashMap
	 * @return
	 */
	public Map<String, Object> remoteControl(Map<String, Object> pHashMap) {
		String cmdId = pHashMap.get("cmdId").toString();
		String bankVersion = (String)pHashMap.get("bankVersion");
		String[] devInfo= (String[]) pHashMap.get("devInfo");
		String fileFlag = pHashMap.get("fileFlag").toString();//�Ƿ�Ҫ��ȡ�ļ���־	
		
		Map<String, Object> rHashMap = new HashMap<String, Object>();
		
		String[] tempRemote=null;
		List resultList = new ArrayList();//�豸��Ϣ�б�			
		boolean result ;
		boolean result2 = false;//״̬���c��p��ͨѶ��� 
		
		String remotePort = pHashMap.get("remotePort").toString();
		String remoteFilePort = pHashMap.get("remoteFilePort").toString();
		String remoteOs2Port = pHashMap.get("remoteOs2Port").toString();
		
		StringBuffer retBody =new StringBuffer();						
		String msgId = null; 		
		String remoteFile = null;
		String localFile = null;
		String dirPath = null;
		String fileName = null;
		String fileProp = null;
		String resultContent = SystemLanguage.getSrcCommFai();
		String retcode=null;
		String retcode2=null;//״̬���c��p��ͨѶ���
		String logDate =null;
		//ȡ�ó�ʱʱ��
		int soTime=SocketUtil.getsoTime(cmdId);
		
		for(int i=0;i<devInfo.length;i++) {
			tempRemote = devInfo[i].split("\\|");				
			int packetLen=Integer.valueOf(tempRemote[2].toString());
			int vzipType=Integer.valueOf(tempRemote[3].toString());
			
			msgId = UUID.randomUUID().toString();
			
			try {
			    //���Ӳ����豸�ţ�ͨ����Ϣת������ȡ�豸�ļ�����Կ��������ת��    2011-8-22  mod by cy
				NDC.push(tempRemote[1]) ;
				result = MessageEncoded.sendCmdToRvcMutil(this.genXML(cmdId, msgId, tempRemote[0], tempRemote[1], localFile,remoteFile), 
						tempRemote[1], remotePort, retBody,20,soTime,packetLen,vzipType, tempRemote[0]);
				NDC.pop() ;
			
				if (result) {
					retcode = XmlUtil.getValue(retBody.toString(), "//root/retcode", "value");				
					if(retcode.equals("RMT000") || retcode.equals("000000")) {
						resultContent= SystemLanguage.getSrcOperateSuccess();
						
						// Զ�̿��Ƴɹ������»���״̬
						if (cmdId.equals("2000431")) {
							RemoteControlDAO.updateRemoteSerialNoInfo(tempRemote[0], 1);
						} else if (cmdId.equals("2000432")) {
							RemoteControlDAO.updateRemoteSerialNoInfo(tempRemote[0], 0);
						} else if (cmdId.equals("2000433")) {
							RemoteControlDAO.updateRemoteTxNoInfo(tempRemote[0], 1);
						} else if (cmdId.equals("2000434")) {
							RemoteControlDAO.updateRemoteTxNoInfo(tempRemote[0], 0);
						}
					} else if(retcode.equals("RMT001") || retcode.equals("000001")) {
						resultContent = SystemLanguage.getSrcOperateFail();
						result=false;
					} else if(retcode.equals("RMT400") || retcode.equals("000400")) {
						resultContent = SystemLanguage.getSrcXmlParseFail();
						result=false;
					}else if(retcode.equals("RMT401") || retcode.equals("000401")) {
						resultContent = SystemLanguage.getSrcAtcionIllegality();
						result=false;
					}else if(retcode.equals("RMT402") || retcode.equals("000402")) {
						resultContent = SystemLanguage.getSrcNonSupportBufferMemory();
						result=false;
					} else {
						resultContent = SystemLanguage.getSrcOperateFail();
						result=false;
					}
				}
				
				/*�豸�š�IP�����������������͡��Ƿ��н���ļ����ļ����ơ�����*/
				resultList.add(new Object[]{tempRemote[0],tempRemote[1],result,fileFlag,resultContent,localFile,fileName,fileProp});
				retBody.delete(0, retBody.length());
				resultContent = SystemLanguage.getSrcCommFai();
				if(pHashMap.get("batchFlag")!=null&&!pHashMap.get("batchFlag").equals("")){
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
	 * ���ɿ����رչ��ֺŻ��桢�����ر��ֽ𻺴�ı���
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
		String bufferType = "SerialNoInfo";
		Integer bufferValue = -1;
		if (cmdId!=null && cmdId.equals("2000431")){ // �������ֺŻ���
			cmdId = "200043";
			bufferType = "SerialNoInfo";
			bufferValue = 1;
		} else if (cmdId!=null && cmdId.equals("2000432")){ // �رչ��ֺŻ���
			cmdId = "200043";
			bufferType = "SerialNoInfo";
			bufferValue = 0;
		} else if (cmdId!=null && cmdId.equals("2000433")){ // �����ֽ𻺴�
			cmdId = "200043";
			bufferType = "TxtInfo";
			bufferValue = 1;
		} else if (cmdId!=null && cmdId.equals("2000434")){ // �ر��ֽ𻺴�
			cmdId = "200043";
			bufferType = "TxtInfo";
			bufferValue = 0;
		}
		
		StringBuffer inputXML = new StringBuffer();	  				    			
		inputXML.append("<?xml version=\"1.0\"?>");
		inputXML.append("<root>");
		inputXML.append("<cmdid value=\"").append(cmdId).append("\"/>");
		inputXML.append("<msgid value=\"").append(msgId).append("\"/>");
		inputXML.append("<cmddatetime date=\"").append(CalendarUtil.getSysTimeYMD()).append("\" time=\"").append(CalendarUtil.getSysTimeHMS()).append("\"/>");
		inputXML.append("<operatorinfo userid=\"zjft\"/>");
		inputXML.append("<actioninfo type=\"").append(bufferType).append("\" value=\"").append(bufferValue).append("\"/>");
		inputXML.append("<remote termno=\"").append(devNo).append("\" ipaddress=\"").append(ip).append("\"/>");
		inputXML.append("</root>");
		
		log.info("rmi generate:" + inputXML.toString());
		
		return inputXML.toString();
	}
	
	/**
	 * ��Զ������������Ϊ���ĺ���
	 * 
	 * @param command
	 *            Զ���������
	 * @return ����������ĺ���
	 */
	private String convertCommandId(String cmdId) {
		if (cmdId == null || cmdId.equals("")) {
			return "";
		} else if (cmdId.equals("200003")) {
			return SystemLanguage.getSrcTransLog();
		} else if (cmdId.equals("200016")) {
			return SystemLanguage.getControlObtainSoftwareList();
		} else if (cmdId.equals("200007")) {
			return SystemLanguage.getControlRemoteObtain();
		} else if (cmdId.equals("200008")) {
			return SystemLanguage.getControlCourseInfo();
		} else if (cmdId.equals("200011")) {
			return SystemLanguage.getControlNetworkConnection();
		} else if (cmdId.equals("200021")) {
			return SystemLanguage.getControlHardwareInfo();
		} else if (cmdId.equals("200022")) {
			return SystemLanguage.getControlObtainSysInfo();
		} else if (cmdId.equals("200014")) {
			return SystemLanguage.getSrcRestart();
		} else if (cmdId.equals("200015")) {
			return SystemLanguage.getSrcSysShut();
		} else if (cmdId.equals("200027")) {
			return SystemLanguage.getSrcLogicClose();
		} else if (cmdId.equals("200026")) {
			return SystemLanguage.getSrcLogicOpen();
		} else if (cmdId.equals("200028")) {
			return SystemLanguage.getSrcStatefulInspectionModule();
		} else if (cmdId.equals("200029")) {
			return SystemLanguage.getSrcMandatoryCard();
		} else if (cmdId.equals("200024")) {
			return SystemLanguage.getSrcModuleReset();
		} else if (cmdId.equals("2000431")) {
			return SystemLanguage.getControlOpenNtsBuffer();
		} else if (cmdId.equals("2000432")) {
			return SystemLanguage.getControlCloseNtsBuffer();
		} else if (cmdId.equals("2000433")) {
			return SystemLanguage.getControlOpenCashBuffer();
		} else if (cmdId.equals("2000434")) {
			return SystemLanguage.getControlCloseCashBuffer();
		} else {
			return SystemLanguage.getSrcInvalidControl();
		}
	}
	
}
