package com.zjft.shepherd.business.control;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.NDC;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;

import com.zjft.shepherd.common.CalendarUtil;
import com.zjft.shepherd.common.DbProxoolUtil;
import com.zjft.shepherd.common.FileUtil;
import com.zjft.shepherd.common.NumberUtil;
import com.zjft.shepherd.common.ResumeFileUtil;
import com.zjft.shepherd.common.SocketUtil;
import com.zjft.shepherd.common.StringUtil;
import com.zjft.shepherd.common.SystemCons;
import com.zjft.shepherd.common.SystemLanguage;
import com.zjft.shepherd.common.Tool;
import com.zjft.shepherd.common.XmlUtil;
import com.zjft.shepherd.dao.AtmvLogDAO;
import com.zjft.shepherd.dao.AtmvLogPathInfo;
import com.zjft.shepherd.dao.DevInfoDAO;
import com.zjft.shepherd.dao.RemoteControlDAO;
import com.zjft.shepherd.dao.RemoteFileDownInfo;
import com.zjft.shepherd.dao.RemoteTrace;
import com.zjft.shepherd.vo.DevAtmpInfo;
import com.zjft.shepherd.vo.DevBaseInfo;

/**  
 *   
 * �����ƣ� ControlService
 * �������� Զ�̿��ƺ�̨ʵ����  ����ҳ���ṩ����������Զ������ļ��к��ļ����ƣ�����hashmap��ҳ�����
 * �����ˣ�  ykliu
 * �޸���Ϣ��zhangdd 2017-04-13 V1.4.3 
*   
*/ 
public class ControlService
{	
	private static Log log=LogFactory.getLog(ControlService.class.getName());	
	private static final int MAX_TIMEOUT=600 ;
	private static final String BANK_JH = "JH";
	private static final String BANK_BZ = "BZ";
	private static final String BANK_CZ = "BANKCZ";
	private static final String BANK_XY = "CIB";
	private static final String BANK_PA = "BOPA";
	private static final double SIZE_RATE = 4294967296D; 
	
	private static final String SETCASHBOX_PAGE_CMDID = "200130";   //��CMDID��Ҫ���ڳ�������ҳ������ѯʱ�빦�ܶ���������֣�ʵ�ʱ�����CMDID��δ�ı�  2011-8-8  add by cy
	//��CMDID��Ҫ�����������и��Ͽ������رս������֣�ʵ�ʱ�����CMDID��δ�ı�  2013-4-23  add by cy
	private static final String SETCOMPOSITECARDSTATUS_PAGE_CMDID = "200230";
	
	/**
	 * ������̻��ļ���
	 * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 *  key:�豸�� devInfo;
	 *     	�ļ�·�� viewpath;
	 *  	�ļ��� docName;
	 *  	�ϼ�Ŀ¼���� upDoc;
	 *  	�ļ�Ŀ¼ dirPath;
	 *  	Remoteҵ������˿� remotePort;
	 *  	Remote�ļ������˿� remoteFilePort;
	 *      ��ǰ�û� user;
	 *      ÿ�������С packetLen;
	 *      ѹ����ʽ vzipType;
	 *      �豸ip ipAddress;
	 *      �豸�� devNo;
	 *      ���к� serialNo;
	 * @return rHashMap
	 *  key:������ retCode 0��1;
	 *  	������Ϣ retMsg;
	 *  	�ļ�list documentFileList;
	 **/
	public Map<String, Object> viewDocFile(Map<String, Object> pHashMap) throws Exception {
		Map<String, Object> rHashMap=new HashMap<String, Object>();
		rHashMap.put("retCode", 0);
		rHashMap.put("retMsg", SystemLanguage.getSrcCommRemFai());
//		rHashMap.put("retMsg", "��Զ���豸ͨѶ����");
		
		String ipAddress = (String)pHashMap.get("ip");
		String devNo = (String)pHashMap.get("devNo");
		String terminalNo = devNo;//terminalNo=devInfo.getTerminalNo() alter by ssli 20080604();
		String serialNo = (String)pHashMap.get("serialNo") ;
		int packetLen = (Integer)pHashMap.get("packetLen");
		int vzipType = (Integer)pHashMap.get("vzipType");
		
		
		String remotePort=pHashMap.get("remotePort").toString();
		String remoteFilePort=pHashMap.get("remoteFilePort").toString();
		
		
		/*�ļ�·��*/
		String viewpath=pHashMap.get("viewpath").toString();
		if(!viewpath.equals(""))
		{			
//			viewpath=new String(viewpath.getBytes("ISO-8859-1"),"UTF-8");
			viewpath=viewpath.trim();			
		}
		/*�ļ���*/
		String docName = pHashMap.get("docName").toString();
		if(!docName.equals(""))
		{			
//			docName=new String(docName.getBytes("ISO-8859-1"),"UTF-8");
			docName=docName.trim();			
		}		
		if(viewpath==null||viewpath.equals(""))
		{
			if(docName!=null&&!docName.equals(""))
			{
				viewpath="";
			}
			else
			{
				viewpath="DISK";
			}			
		}
		
		if(docName!=null&&!docName.equals(""))
		{
				
			if(viewpath.equals(""))
			{
				viewpath+=docName;
			}
			else if(viewpath.length()>3)
			{
				viewpath=viewpath+"\\"+docName;
			}
			else
			{
				viewpath=viewpath+docName;
			}			
		}
		/*�ϼ�Ŀ¼����*/
		String upDoc=pHashMap.get("upDoc").toString();
		if(upDoc!=null&&upDoc.equals("1"))
		{
			if(viewpath.length()==3)
			{
				viewpath="DISK";
			}
			else
			{
				viewpath=viewpath.substring(0,viewpath.lastIndexOf("\\"));
			}
			if(viewpath.length()==2)
			{
				viewpath+="\\";
			}
			
			if(viewpath.equals(""))
			{
				viewpath="DISK";
			}
		}	
		
		/*����ļ�Ŀ¼������������*/
		String dirPath=pHashMap.get("dirPath").toString();
		log.debug("dirPath"+dirPath);
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		String cmdDate = CalendarUtil.getSysTimeYMD();	//RVS�������������
	
		String cmdTime = CalendarUtil.getSysTimeHMS();	//RVS���������ʱ��
		
		String actionTime = cmdDate+cmdTime;
		
		String msgId = UUID.randomUUID().toString();				
		
		StringBuffer inputXML=new StringBuffer();	  				    			
		inputXML.append("<?xml version=\"1.0\"?>").append("<root>")
		        .append("<cmdid value=\"200018\" />")		    
				.append("<msgid value=\"").append(msgId).append("\" />")
				.append("<cmddatetime date=\"").append(cmdDate).append("\" time=\"").append(cmdTime).append("\" />")
				.append("<operatorinfo userid=\"zjft\"/>")
				.append("<actioninfo time=\"").append(actionTime).append("\" filename=\"").append(msgId+".flst").append("\" viewpath=\"").append(viewpath).append("\" />")			   
				.append("<remote ipaddress=\"").append(ipAddress)
				.append("\" termno=\"").append(terminalNo)   
				.append("\" serialno=\"").append(serialNo).append("\" />")
			    .append("</root>");
		
		StringBuffer retBody=new StringBuffer();

		/*������Զ�̰汾������ͨѶ�ӿ�*/			
		//boolean cmdFlag = SocketUtil.sendCmdToRvcMutil(inputXML.toString(),ipAddress,remotePort,retBody,20,MAX_TIMEOUT,packetLen,vzipType);
		//���Ӳ����豸�ţ�ͨ����Ϣת������ȡ�豸�ļ�����Կ��������ת��    2011-8-22  mod by cy
		NDC.push(ipAddress) ;
		boolean cmdFlag = MessageEncoded.sendCmdToRvcMutil(inputXML.toString(),ipAddress,remotePort,retBody,20,MAX_TIMEOUT,packetLen,vzipType,terminalNo);
		NDC.pop() ;
		if (!cmdFlag) {
			rHashMap.put("retMsg", SystemLanguage.getSrcCommRemFai());
			log.info("��Զ���豸ͨѶ����,200018���ķ���ʧ��");
			rHashMap.put("retMsg", "��Զ���豸ͨѶ����");
			RemoteControlDAO.saveRemoteTrace("200018",pHashMap.get("user").toString(),devNo, SystemLanguage.getMainFailed(), SystemLanguage.getControlRemoteBrowse()+"|��Զ���豸ͨѶ����");
			return rHashMap;
		}
		
		/*��������*/
		Document document=DocumentHelper.parseText(retBody.toString());
		org.dom4j.Node fileNameNode=document.selectSingleNode("//root/actioninfo");
		if (fileNameNode == null) {
			rHashMap.put("retMsg", SystemLanguage.getSrcNodeNotFound());
			RemoteControlDAO.saveRemoteTrace("200018",pHashMap.get("user").toString(),devNo, SystemLanguage.getMainFailed(), SystemLanguage.getControlRemoteBrowse()+"|"+SystemLanguage.getSrcNodeNotFound());
			return rHashMap;
		}
		String fileName=fileNameNode.valueOf("@filename");//Զ�����ɵ��ļ�����
		
		String locfilename=dirPath+"/"+msgId+".txt";//������ܵ����ļ���
		
		/* ��ȡȡ�ļ� */
		if(!FileUtil.getRvcFile(ipAddress,remoteFilePort,locfilename,fileName,ipAddress,terminalNo,0,20,MAX_TIMEOUT,packetLen,vzipType).equals("0000")) {
			rHashMap.put("retMsg", SystemLanguage.getSrcErrorFileList());
			RemoteControlDAO.saveRemoteTrace("200018",pHashMap.get("user").toString(),devNo, SystemLanguage.getMainFailed(), SystemLanguage.getControlRemoteBrowse()+"|"+SystemLanguage.getSrcErrorFileList());
			return rHashMap;
		}
			
		/*�������ص��ļ�����,���ļ����ݰ��ж�ȡ�������зŵ�(DocumentFileList)�б����͵�ҳ��*/
	   File docFile=new File(locfilename);
	   List documentFileList=new ArrayList();
	   FileReader myFileReader = null;
//	   FileInputStream inStream = null;
	   BufferedReader bufferedReader =null;
			try{
		   if(docFile.isFile())
		   {
//			   inStream = new FileInputStream(new File(docFile.toString()));
//				bufferedReader = new BufferedReader(
//						new InputStreamReader(inStream, "unicode"));
			   myFileReader = new FileReader(docFile);
			   bufferedReader = new BufferedReader(myFileReader);
			   String lineString = bufferedReader.readLine();
			   String[] s ;
			   DocumentFileList doc;
			   
			   if(viewpath==null||viewpath.equals("DISK"))//����Ǵ����б�
			   {
				   while ((lineString = bufferedReader.readLine()) != null)
				   {
					   /*�������Ϊ��*/
					   if(lineString.equals(""))
					   {
						   continue;
					   }
					   s = lineString.split("\\|", -1);		
					   //������չ��
				  		switch (Integer.valueOf(s[7].trim())) {
						case 0:
							s[1] = s[1].equals("") ? "" : SystemLanguage.getControlUnrecognized();
							s[3] = s[3].equals("") ? "" : SystemLanguage.getControlUnrecognized();
							break;
						case 1:
							s[1] = s[1].equals("") ? "" : SystemLanguage.getControlInvalidpath();
							s[3] = s[3].equals("") ? "" : SystemLanguage.getControlInvalidpath();
							break;	
						case 2:
							s[1] = s[1].equals("") ? "" : SystemLanguage.getControlRemovableDisk();
							s[3] = s[3].equals("") ? "" : SystemLanguage.getControlRemovableDisk();
							break;
						case 3:
							s[1] = s[1].equals("") ? "" : SystemLanguage.getControlDevDocumentBrowserBodyLocalDisk();
							s[3] = s[3].equals("") ? "" : SystemLanguage.getControlDevDocumentBrowserBodyLocalDisk();
							break;	
						case 4:
							s[1] = s[1].equals("") ? "" : SystemLanguage.getControlNetworkDisk();
							s[3] = s[3].equals("") ? "" : SystemLanguage.getControlNetworkDisk();
							break;
						case 5:
							s[1] = s[1].equals("") ? "" : SystemLanguage.getControlCDdriveCD();
							s[3] = s[3].equals("") ? "" : SystemLanguage.getControlCDdriveCD();
							break;
						case 10:
							s[1] = s[1].equals("") ? "" : SystemLanguage.getControlFloppyDisk();
							s[3] = s[3].equals("") ? "" : SystemLanguage.getControlInchFloppyDisk();
							break;
						default:
							break;
						}
						
					   doc =new DocumentFileList();
					   doc.setDocmentFileType(s[0].trim()); //�趨����
					   if(!s[1].trim().equals(""))
					   {
						   doc.setDiskName(s[1].trim()+"|(|"+s[2].trim()+"|)");        //��������(DVD-RW ������)
					   }
					   else
					   {
						   doc.setDiskName(s[3].trim()+"|(|"+s[2].trim()+"|)");        //��������(DVD-RW ������)
					   }					   
					   doc.setDiskPath(Tool.escape(s[2].trim()));//����·����Ϣ(c:\��d:\��e:\)
					   doc.setDocmentFileName(s[3].trim()); //��������
					   
					   /*���̴�С*/
					   if(s[4]!=null)
					   {
						   String diskSpaceSize=s[4].trim();						
						   diskSpaceSize=Tool.getSize(diskSpaceSize);
						   if(diskSpaceSize.equals("0 B"))
						   {
							   diskSpaceSize="";
						   }
						   doc.setDocFileSize(diskSpaceSize); 
					   }					   
					   	/*����ʣ��ռ�*/
					   if(s[5]!=null)
					   {
						   String diskFreeSpaceSize=Tool.getSize(s[5].trim());						
						   
						   if(diskFreeSpaceSize.equals("0 B"))
						   {
							   diskFreeSpaceSize="";
						   }
						   doc.setDiskFreeSize(diskFreeSpaceSize); 
					   }					  
					   doc.setDocmentFilePro(s[6].trim());		
					   documentFileList.add(doc);
				   }
			   }
			   else//���Ŀ¼
			   {
				   List fileList=new ArrayList();
				   List docList=new ArrayList();

				   while ((lineString = bufferedReader.readLine()) != null)
				   {
					   /*�������Ϊ��*/
					   if(lineString.equals(""))
					   {
						   continue;
					   }
					   
					   s = lineString.split("\\|", -1);
					   doc =new DocumentFileList();
					   doc.setDocmentFileType(s[0].trim());//�趨�ļ��л��ļ�
					   doc.setDocmentFileName(Tool.escape(s[1].trim()));//�ļ����ļ�������
					   if(s[0].trim().equals("0"))//�ļ�
					   {						  
						   String fileProperty=s[1].trim();
						   try//��ȡ�ļ�����
						   {							
							   fileProperty=fileProperty.substring(fileProperty.lastIndexOf("."),fileProperty.length());							   		
							   fileProperty=fileProperty.substring(1,fileProperty.length());
						   }
						   catch(Exception e)
						   {
							   /*�����׽���쳣����Ϊ�ļ�������׺��*/
							   fileProperty="";
						   }
						  
						   String fileSize = "0 KB";
						   
						   if(!isNullOrBlank(s[5]))
						   {
							   String tmepSize = s[5].trim();
							   int postion = tmepSize.lastIndexOf(" ");
							   double low = Double.parseDouble(tmepSize.substring(postion + 1));//�ļ���С��λ
							   double high = 0;//�ļ���С��λ
							   if(postion > 0)
							   {
								   high = Double.parseDouble(tmepSize.substring(0, postion));
							   }
							   double ulSize = high * SIZE_RATE + low;
							   fileSize = Tool.getSize(String.valueOf(ulSize));						   
						   }
						   
						   doc.setDocFileSize(fileSize);
						   //bocom_shep_003p-----------------------------
						   if(s[4]!=null)
						   {
							   String docmentFileUpdateTime=s[4].trim();//ȥ���ո�
							   String updateTime[]=docmentFileUpdateTime.split("\\s+");
							   String str1="00000000"+updateTime[0],str2="00000000"+updateTime[1];
//							   log.info("str1="+str1.substring(str1.length()-8,str1.length()));
//							   log.info("str2="+str2.substring(str2.length()-8,str2.length()));
							   str1=str1.substring(str1.length()-8,str1.length());
							   str2=str2.substring(str2.length()-8,str2.length());
							   long longdocmentFileUpdateTime=(Long.parseLong(str1+str2,16)-116444736000000000l)/10000000*1000;
							   GregorianCalendar gc = new GregorianCalendar();   
							   gc.setTimeInMillis(longdocmentFileUpdateTime);
//							   log.info("gc.getTime()="+gc.getTime());
							   SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
							   doc.setDocmentFileUpdateTime(sdf.format(gc.getTime()));//�趨�ļ��л��ļ����޸�ʱ��
						   }
						   //bocom_shep_003p-----------------------------
						   doc.setDocmentFileMode(fileProperty);
						   fileList.add(doc);//��������뵽�ļ�List��
					   }
					   else//�ļ���
					   {
						   docList.add(doc);//��������뵽Ŀ¼List��
					   }					 				  
				   }
				   
				   if(!docList.isEmpty())
				   {
					   documentFileList.addAll(docList);
				   }
				   if(!fileList.isEmpty())
				   {
					   documentFileList.addAll(fileList);
				   }
				}
				myFileReader.close();
			} else {
				rHashMap.put("retMsg", SystemLanguage.getSrcLocalListNotExist());
				RemoteControlDAO.saveRemoteTrace("200018", pHashMap.get("user").toString(), devNo, SystemLanguage.getMainFailed(),SystemLanguage.getControlRemoteBrowse() + "|" + SystemLanguage.getSrcLocalListNotExist());
				return rHashMap;
			}
			docFile.delete();// ɾ�����ɵ���ʱ�ļ�

			if (viewpath.equals("DISK")) {
				viewpath="";
				RemoteControlDAO.saveRemoteTrace("200018",pHashMap.get("user").toString(),devNo, SystemLanguage.getControlRemoteControlResultSuccess(), SystemLanguage.getControlRemoteBrowse());
			}
			rHashMap.put("documentFileList", documentFileList);
			rHashMap.put("retCode", 1);
			rHashMap.put("viewpath", viewpath);
			return rHashMap;
		} catch (Exception e) {
			log.error("Զ����������쳣,", e);
			if (viewpath.equals("DISK")) {
				RemoteControlDAO.saveRemoteTrace("200018",pHashMap.get("user").toString(),devNo, SystemLanguage.getMainFailed(), SystemLanguage.getControlRemoteBrowse());
			}
			rHashMap.put("retMsg", SystemLanguage.getSrcLocalListNotExist());
			return rHashMap;
		} finally {
			if (myFileReader != null) {
				myFileReader.close();
			}
			if (bufferedReader != null) {
				bufferedReader.close();
			}
		}
	}
	
	/**
	 * ����ATM�ϵ��ļ�������Ա��
	 * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 *	key:�豸�� devInfo;
	 *		�ļ�·�� viewpath;
	 *		�ļ��� fileName;
	 *		fileBean
	 *		�ļ�Ŀ¼ versionFilePath
	 *		Remote�ļ������˿� remoteFilePort
	 *		��ǰ�û� user;
	 *		ÿ�������С packetLen;
	 *		ѹ����ʽ vzipType;
	 *		�豸ip ipAddress;
	 *		�豸�� devNo;
	 *		���к� serialNo;
	 *      �ļ����� fileType(Ĭ����file)
	 * @return rHashMap
	 *	key:������ retCode 0��1;
	 *		������Ϣ retMsg;
	 * */
	public Map<String, Object> downloadFile(Map<String, Object> pHashMap) throws Exception {
		
		Map<String, Object> rHashMap=new HashMap<String, Object>();
		String ipAddress = (String)pHashMap.get("ip");
		String devNo = (String)pHashMap.get("devNo");
		String terminalNo = devNo;
		int packetLen = (Integer)pHashMap.get("packetLen");
		int vzipType = (Integer)pHashMap.get("vzipType");
		String remoteFilePort=pHashMap.get("remoteFilePort").toString();
		/*�ļ�·��*/
		String viewpath = pHashMap.get("viewpath").toString();
		String fileName = pHashMap.get("fileName").toString();
		//Դ�ļ���  ���ڽ�ѹ��
		String versionFilePath = pHashMap.get("versionFilePath").toString();
		String remotePort=pHashMap.get("remotePort").toString();
		String resumeLocalFile = pHashMap.get("localFile") == null ? null : pHashMap.get("localFile").toString();
		String remoteFile = getRemoteFile(viewpath,fileName);
		
		boolean flag = false; //�ļ��Ƿ�ѹ���Լ��Ƿ�ѹ���ɹ���־
		String transferFile = remoteFile;//�����豸�˵�Զ���ļ�
		String compressfile = ""; //ѹ������ļ�
		if (pHashMap.containsKey("compressflag") && Integer.valueOf(pHashMap.get("compressflag").toString())!=0){
			log.info("�ļ�����ǰ����Ҫ����ѹ��");
			compressfile = this.compressFile(ipAddress,devNo,remoteFile,remotePort,packetLen,vzipType);
			if(compressfile != null) {
				flag = true;
				transferFile = compressfile;
			}
		} else {
			log.info("�ļ�����Ҫ����ѹ������");
		}
		
		String locfilename = null;
		if(resumeLocalFile != null) {
			if(new File(resumeLocalFile).exists()) {
				locfilename = resumeLocalFile;
			} else {
				locfilename = this.createLocalFile(versionFilePath,fileName,flag);
			}
		} else {
			locfilename = this.createLocalFile(versionFilePath,fileName,flag);
		}
		
		if(locfilename == null) {
			RemoteControlDAO.saveRemoteTrace("200099",pHashMap.get("user").toString(), devNo, SystemLanguage.getMainFailed(),  SystemLanguage.getControlDownloadFile() + "|" + remoteFile);
			rHashMap.put("retCode", 0);
			rHashMap.put("retMsg", "���������ļ�ʧ��");
			return rHashMap;
		}
		RemoteTrace remoteTrace = null;
		RemoteFileDownInfo downInfo = null;
		//����ļ��������ؼ�¼
		if(resumeLocalFile == null) {
			remoteTrace = RemoteControlDAO.saveRemoteTrace("200099",pHashMap.get("user").toString(), devNo, "��������",  SystemLanguage.getControlDownloadFile() + "|" + remoteFile);
			if(remoteTrace == null) {
				rHashMap.put("retCode", 0);
				rHashMap.put("retMsg", "�����ļ����ؼ�¼ʧ��");
				return rHashMap;
			}
			
			downInfo =  RemoteControlDAO.saveFileDownInfo(remoteTrace.getLogicId(),remoteFile,locfilename,compressfile,0.00);
		} else { //����
			String logicId = pHashMap.get("logicId").toString();
			remoteTrace = new RemoteTrace();
			remoteTrace.setLogicId(logicId);
			downInfo = RemoteControlDAO.getFileDownInfoByRemoteLogicId(logicId);
		}
		
		if(downInfo != null) {
			downInfo.setUserNo(pHashMap.get("user").toString());
		}
		log.info("�����ļ���"+locfilename+"��,����Զ���ļ���"+transferFile+"��,�豸�š�"+terminalNo+"��");
		//��ʼ�����豸���ļ�
		NDC.push(ipAddress) ;
		String reStr = ResumeFileUtil.resumeFileTrans(ipAddress,remoteFilePort,locfilename,transferFile,ipAddress,terminalNo,0,20,20,packetLen,vzipType,downInfo);
		NDC.pop() ;
		
		/*��ȡȡ�ļ�*/			
		if(!reStr.equals("0000")) {
			String status = SystemLanguage.getMainFailed();
			if("��ʱ".equals(reStr)) {
				status = reStr;
			}
			if(downInfo != null) {
				downInfo.setStatus(status);
				SocketUtil.sendMessageToServer(downInfo.organizeXML(),20,60,8000,0);
			}
			
			RemoteControlDAO.updateRemoteTraceStatus(remoteTrace.getLogicId(), status);
			rHashMap.put("retCode", 0);
			rHashMap.put("retMsg", SystemLanguage.getSrcOperateFail());
		} else {
			rHashMap.put("locfilename", locfilename);
			if(flag){
				rHashMap.put("remoteFile", (remoteFile.lastIndexOf(".")!=-1?remoteFile.substring(0, remoteFile.lastIndexOf(".")):remoteFile)+ ".zip");
			}else{
				rHashMap.put("remoteFile", remoteFile);
			}
			RemoteControlDAO.updateRemoteTraceStatus(remoteTrace.getLogicId(), SystemLanguage.getControlRemoteControlResultSuccess());
			rHashMap.put("retCode", 1);
			rHashMap.put("retMsg", SystemLanguage.getSrcOperateSuccess());
		}
		return rHashMap;
	}
	

	
	/**
	 * ���ض���ͻ����ļ�������Ա��
	 * @author limengrd
	 * @since 2016.09.07
	 * @param pHashMap 
	 *	key:
	 *		Remote�ļ������˿� remoteFilePort
	 *		��ǰ�û� user;
	 *		ÿ�������С packetLen;
	 *		ѹ����ʽ vzipType;
	 *		�豸ip ipAddress;
	 *		�豸�� devNo;
	 *      ·���ļ� pathType logPath&file#logPath&file
	 * @return rHashMap
	 *	key:������ retCode 0��1;
	 *		������Ϣ retMsg;
	 * */
	public Map<String, Object> downloadClientFile(Map<String, Object> pHashMap)throws Exception {
		Map<String, Object> rHashMap = new HashMap<String, Object>();
		String devs = StringUtil.parseString(pHashMap.get("devs")); // �豸��
		String projects = StringUtil.parseString(pHashMap.get("projects")); // ��ȡ����
		String username = StringUtil.parseString(pHashMap.get("username")); // �û���
		
		String remoteFilePort = StringUtil.parseString(pHashMap.get("remoteFilePort"));
		String remotePort = StringUtil.parseString(pHashMap.get("remotePort"));
		String[] devNos = devs.split("\\|");
		String[] project = projects.split("\\|");
		for (int i = 0; i < devNos.length; i++) {
			String localFile = StringUtil.parseString(pHashMap.get("localFile")); // �����ļ�
			Map<String, Object> retMap = new HashMap<String, Object>();
			for (int j = 0; j < project.length; j++) {
				String logType = project[j];
				// ������ȡ���ݲ�ѯ�豸����־��Ϣ
				List<AtmvLogPathInfo> list = AtmvLogDAO.getAtmcLogPathInfo(logType);
				for (AtmvLogPathInfo info : list) {
					String fileType = info.getFileType().equals("1") ? "folder" : "file";
					String logPath = info.getLogPath();
					if (!(logPath.endsWith("\\") || logPath.endsWith("/")) && "folder".equals(fileType)) {
						//�ļ���Ҫ�� /��\ ��β
						logPath = logPath + System.getProperty("file.separator");
					}
					retMap.put(logPath, fileType);
				}
			}
			String devNo = devNos[i];
			DevBaseInfo devBaseInfo = DevInfoDAO.getDevBaseInfo(devNo);
			String ip = devBaseInfo.getIp();
			int packetLen = devBaseInfo.getPacketLen();
			int vzipType = devBaseInfo.getVizpType();

			String compressfile = ""; // ѹ���ļ�
			boolean flag = false; // ѹ���ɹ���ʶ
			if (pHashMap.containsKey("compressflag") && Integer.valueOf(StringUtil.parseString(pHashMap.get("compressflag"))) != 0) {
				log.info("�ļ�����ǰ����Ҫ����ѹ��");
				compressfile = this.compressFile(ip, devNo, remotePort, packetLen, vzipType, retMap);
				if (compressfile != null) {
					flag = true;
				}
			} else {
				log.info("�ļ�����Ҫ����ѹ������");
			}
			// ȷ�������ļ�����
			FileUtil.createDirectory(localFile);
			// ���ص�����·��
			localFile = localFile + System.getProperty("file.separator")
					+ devNo + "_" + CalendarUtil.getSysTimeYMDHMS1() + ".zip";
			if (flag && FileUtil.getRvcFile(ip, remoteFilePort, localFile, compressfile, ip, devNo, 0, 20, MAX_TIMEOUT, packetLen, vzipType).equals("0000")) {
				rHashMap.put("retCode", 1);
				rHashMap.put("retMsg", SystemLanguage.getSrcOperateSuccess());
				RemoteControlDAO.saveRemoteTrace("200018", username, devNo, "���سɹ�", SystemLanguage.getSrcOperateSuccess());
			} else {
				rHashMap.put("retCode", 0);
				rHashMap.put("retMsg", SystemLanguage.getSrcErrorFileList());
				RemoteControlDAO.saveRemoteTrace("200018", username, devNo, SystemLanguage.getMainFailed(), SystemLanguage.getControlRemoteBrowse() + "|" + SystemLanguage.getSrcErrorFileList());
			}
		}
		return rHashMap;
	}
	
	private String getRemoteFile(String viewpath, String fileName) {
		String remoteFile = viewpath;
		if(viewpath.length() != 3 && !fileName.equals("")) {
			remoteFile += "\\";
		}
		remoteFile += fileName;
		return remoteFile;
	}

	
	/**
	 * ���������ļ�·��
	 * @author cqluo
	 * @param versionFilePath �����ļ�·��
	 * @param fileName �ļ���
	 * @param flag �Ƿ�ѹ��Զ���ļ�
	 * @return
	 */
	private String createLocalFile(String versionFilePath, String fileName,Boolean flag) {
		
		try {
			/*����ļ�Ŀ¼������������*/
			String dirPath=versionFilePath+System.getProperty("file.separator")+"file"+System.getProperty("file.separator")+"download"+System.getProperty("file.separator")+"temp";
			File dir = new File(dirPath);
			if (!dir.isDirectory()) {
				dir.mkdirs();
			}
			String fileProperty=null;
			try {
				fileProperty = fileName.substring(fileName.lastIndexOf("."),fileName.length());
			} catch (Exception e) {
				fileProperty = "";
			}
			//���Զ���ļ��Ѿ�ѹ��������ļ��ĺ�׺����Ϊzip�ļ�
			fileProperty = flag ? ".zip" : fileProperty;
			String tempFile =UUID.randomUUID().toString()+fileProperty;
			String locfilename=dirPath + System.getProperty("file.separator")+tempFile;
			File locFile = new File(locfilename);
			locFile.createNewFile(); //���������ļ�
			log.info("�����ļ�["+locfilename+"]");
			return locfilename;
		} catch (Exception e) {
			log.info("�����ļ�ʱ���������ļ�ʧ��:",e);
		}
		return null;
	}

	/**
	 * �����ļ�ǰ����ѹ���ļ�
	 * @author cqluo
	 * @param ipAddress �豸IP��ַ
	 * @param devNo �豸��
	 * @param remoteFile Ҫѹ�����ļ�
	 * @param remotePort �豸���ļ����ض˿�
	 * @param packetLen ÿ�η�������
	 * @param vzipType ѹ����ʽ
	 * @return ѹ��ʧ��ʱ����Null���ɹ�ʱ����ѹ������ļ�
	 */
	private String compressFile(String ipAddress, String devNo,
			String remoteFile, String remotePort, int packetLen, int vzipType) {
		
		String msgId = UUID.randomUUID().toString() ;
		String  sendBody = getFilePackXml("200041", msgId, ipAddress, devNo, remoteFile, "file") ;
		StringBuffer retBody=new StringBuffer();
		NDC.push(ipAddress) ;
		boolean statusFlag = MessageEncoded.sendCmdToRvcMutil(sendBody, ipAddress,remotePort , retBody, 20,20,packetLen,vzipType, devNo) ;
		NDC.pop();
		if (!statusFlag) {
			log.info("����ѹ������ʧ��!");
		} else {
			log.info("����ѹ�����ĳɹ�!") ;
			String retCode = XmlUtil.getValue(retBody.toString(),"//root/retcode","value");
			String destfile  = XmlUtil.getValue(retBody.toString(), "//root/destfile", "pathname");
			if(retCode == null || destfile == null){
				log.info("retCode��destfileΪ��!");
				return null;
			}else{
				if(retCode.equals("000000")||retCode.equals("RMT000")) {
					log.info("ѹ���ļ��ɹ�!") ;
					return destfile;
				} else if(retCode.equals("RMT401")){
					log.info("action�Ƿ�") ;
				} else if(retCode.equals("RMT402")){
					log.info("ѹ�����ͷǷ�") ;
				} else if(retCode.equals("RMT403")){
					log.info("������һ���ļ�������") ;
				} else if(retCode.equals("RMT404")){
					log.info("���̿ռ䲻��");
				} else if(retCode.equals("RMT405")){
					log.info("��֧�ֳ���Դ�ļ�");
				}else{
					log.info("δ֪ԭ��") ;
				}
			}
		}
		return null;
	}
	
	/**
	 * �����ļ�ǰ����ѹ���ļ�
	 * @author cqluo
	 * @param ipAddress �豸IP��ַ
	 * @param devNo �豸��
	 * @param remotePort �豸���ļ����ض˿�
	 * @param packetLen ÿ�η�������
	 * @param vzipType ѹ����ʽ
	 * @param params 
	 *           key: �ļ�����
	 *           value:�ļ�·��
	 * @return ѹ��ʧ��ʱ����Null���ɹ�ʱ����ѹ������ļ�
	 */
	private String compressFile(String ipAddress, String devNo, String remotePort, int packetLen, int vzipType,Map<String, Object> params) {
		
		String msgId = UUID.randomUUID().toString() ;
		String  sendBody = getFilePackXml("200041", msgId, ipAddress, devNo, params) ;
		//ȡ�ó�ʱʱ��
		int soTime=SocketUtil.getsoTime("200041");
		StringBuffer retBody=new StringBuffer();
		NDC.push(ipAddress) ;
		boolean statusFlag = MessageEncoded.sendCmdToRvcMutil(sendBody, ipAddress,remotePort , retBody, 20,soTime,packetLen,vzipType, devNo) ;
		NDC.pop();
		if (!statusFlag) {
			log.info("����ѹ������ʧ��!");
		} else {
			log.info("����ѹ�����ĳɹ�!") ;
			String retCode = XmlUtil.getValue(retBody.toString(),"//root/retcode","value");
			String destfile  = XmlUtil.getValue(retBody.toString(), "//root/destfile", "pathname");
			if(retCode == null || destfile == null){
				log.info("retCode��destfileΪ��!");
				return null;
			}else{
				if(retCode.equals("000000")||retCode.equals("RMT000")) {
					log.info("ѹ���ļ��ɹ�!") ;
					return destfile;
				} else if(retCode.equals("RMT401")){
					log.info("action�Ƿ�") ;
				} else if(retCode.equals("RMT402")){
					log.info("ѹ�����ͷǷ�") ;
				} else if(retCode.equals("RMT403")){
					log.info("������һ���ļ�������") ;
				} else if(retCode.equals("RMT404")){
					log.info("���̿ռ䲻��");
				} else if(retCode.equals("RMT405")){
					log.info("��֧�ֳ���Դ�ļ�");
				}else{
					log.info("δ֪ԭ��") ;
				}
			}
		}
		return null;
	}
	
	
	
	/**
	 * Զ���ļ�ѹ��������Ϣ add by yyhe 2012-08-17
	 */
	private String getFilePackXml(String cmdId, String msgId, String ip,String devNo, String pathName, String type) {
		
	StringBuffer inputXML = new StringBuffer();
		inputXML.append("<?xml version=\"1.0\"?>");
		inputXML.append("<root>");
		inputXML.append("<cmdid value=\"").append(cmdId).append("\"/>");
		inputXML.append("<msgid value=\"").append(msgId).append("\"/>");
		inputXML.append("<cmddatetime date=\"").append(CalendarUtil.getDate()).append("\" time=\"").append(CalendarUtil.getTime()).append("\"/>");
		inputXML.append("<operatorinfo userid=\"zjft\"/>"); 
		inputXML.append("<actioninfo compresstype=\"1\">");
		inputXML.append("<sourfile type=\"").append(type).append("\" pathname=\"").append(pathName).append("\"/>");
		inputXML.append("</actioninfo>");
		inputXML.append("<remote ipaddress=\"").append(ip).append("\" serialno=\"").append(devNo).append("\"/>");
		inputXML.append("</root>");
		
		log.info("=========V�˷��͵�ѹ��ָ���======��" + inputXML.toString()) ;
		return inputXML.toString() ;
	} 
	
	/**
	 * ���Զ���ļ�ѹ��������Ϣ add by limengrd 2016-09-07
	 */
	private String getFilePackXml(String cmdId, String msgId, String ip,String devNo, Map<String, Object> params) {
		
	StringBuffer inputXML = new StringBuffer();
		inputXML.append("<?xml version=\"1.0\"?>");
		inputXML.append("<root>");
		inputXML.append("<cmdid value=\"").append(cmdId).append("\"/>");
		inputXML.append("<msgid value=\"").append(msgId).append("\"/>");
		inputXML.append("<cmddatetime date=\"").append(CalendarUtil.getDate()).append("\" time=\"").append(CalendarUtil.getTime()).append("\"/>");
		inputXML.append("<operatorinfo userid=\"zjft\"/>"); 
		inputXML.append("<actioninfo compresstype=\"1\">");
		for(String key:params.keySet()){
			inputXML.append("<sourfile type=\"").append(params.get(key)).append("\" pathname=\"").append(key).append("\"/>");		
		}
		inputXML.append("</actioninfo>");
		inputXML.append("<remote ipaddress=\"").append(ip).append("\" serialno=\"").append(devNo).append("\"/>");
		inputXML.append("</root>");
		
		log.info("=========V�˷��͵�ѹ��ָ���======��" + inputXML.toString()) ;
		return inputXML.toString() ;
	}
	
	/**
	 * �ϴ��ļ���ATM�豸
     * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 *  key:�豸�� devInfo;
	 *  	�ļ�·�� viewpath;
	 *  	�ϴ��ļ��� uploadedFileName;
	 *  	�ϴ���ʽ uploadType
	 *  	ϵͳIP��ַ localIp
	 *  	ϵͳ�ļ������˿� localFilePort
	 *  	Remoteҵ������˿� remotePort;
	 *  	��ǰ�û� user;
	 *      ÿ�������С packetLen;
	 *      ѹ����ʽ vzipType;
	 *      �豸ip ipAddress;
	 *      �豸�� devNo;
	 *      ���к� serialNo;
	 * @return rHashMap
	 *  key:������ retCode 0��1;
	 *  	������Ϣ retMsg;
	 * */
	public Map<String, Object> uploadFile(Map<String, Object> pHashMap) throws Exception
	{	     
		Map<String, Object> rHashMap=new HashMap<String, Object>();
		
		String ipAddress = (String)pHashMap.get("ip");
		String devNo = (String)pHashMap.get("devNo");
		String terminalNo = devNo;//terminalNo=devInfo.getTerminalNo() alter by ssli 20080604();
		String serialNo = (String)pHashMap.get("serialNo") ;		
		int packetLen = (Integer)pHashMap.get("packetLen");
		int vzipType = (Integer)pHashMap.get("vzipType");
		
		String remotePort=pHashMap.get("remotePort").toString();
		
		/*�ļ�·��*/
		String viewpath=pHashMap.get("viewpath").toString();
		
		String uploadType=pHashMap.get("uploadType").toString();
		String uploadedFileName=pHashMap.get("uploadedFileName").toString();
		
		
		String cmdDate = CalendarUtil.getSysTimeYMD();	//RVS�������������
		String cmdTime = CalendarUtil.getSysTimeHMS();	//RVS���������ʱ��
		
		String actionTime=cmdDate+cmdTime;		
		String msgId=UUID.randomUUID().toString();
		StringBuffer inputXML=new StringBuffer();
		uploadType=(uploadType==null||uploadType.equals(""))?"0":uploadType;
		
		String serverIp=pHashMap.get("localIp").toString();//���й���ϵͳIP��ַ
		String serverFilePort=pHashMap.get("localFilePort").toString();//���й���ϵͳ�ļ������˿�
		
		inputXML.append("<?xml version=\"1.0\"?>").append("<root>")
		        .append("<cmdid value=\"200005\" />")
		       // .append("<mgrtype value=\"SH\"/>")
				.append("<msgid value=\"").append(msgId).append("\" />")
				.append("<cmddatetime date=\"").append(cmdDate).append("\" time=\"").append(cmdTime).append("\" />")
				.append("<operatorinfo userid=\"zjft\"/>")
				.append("<actioninfo time=\"").append(actionTime).append("\" filename=\"").append(uploadedFileName)
				.append("\" remotefilename=\"").append(viewpath).append("\" />")			   
				.append("<remote ipaddress=\"").append(ipAddress)
				.append("\" termno=\"").append(terminalNo)			//delete by ssli 2008-06-04
				.append("\" serialno=\"").append(serialNo).append("\" />")
				.append("<serverinfo ipaddress=\"").append(serverIp).append("\" fileport=\"").append(serverFilePort).append("\" />")
				.append("<uploadType value=\"").append(uploadType).append("\" />")
			    .append("</root>");	
		
		StringBuffer retBody=new StringBuffer();		
		log.info("inputXML="+inputXML.toString());
		/*������Զ�̰汾������ͨѶ�ӿ�*/											
		//boolean cmdFlag=SocketUtil.sendCmdToRvcMutil(inputXML.toString(),ipAddress,remotePort,retBody,20,MAX_TIMEOUT,packetLen,vzipType);
		//���Ӳ����豸�ţ�ͨ����Ϣת������ȡ�豸�ļ�����Կ��������ת��    2011-8-22  mod by cy
		NDC.push(ipAddress) ;
		boolean cmdFlag=MessageEncoded.sendCmdToRvcMutil(inputXML.toString(),ipAddress,remotePort,retBody,20,MAX_TIMEOUT,packetLen,vzipType, terminalNo);
		NDC.pop() ;
		
		log.info("cmdFlag="+cmdFlag);
		log.info("retBody="+retBody.toString());
		/*�����ϴ��ɹ��ڷ�ɾ�����й����ϵ���ʱ�ļ�*/
		
		/*
		if(uploadedFile.isFile())
		{
			uploadedFile.delete();
		}		
		*/
		
		if(cmdFlag)
		{			
			Document document=DocumentHelper.parseText(retBody.toString());
			org.dom4j.Node Node=document.selectSingleNode("//root/retcode");
			String retCode = Node.valueOf("@value");
			log.info("retCode="+retCode.toString());
			if(retCode.equals("000000")||retCode.equals("RMT000"))
			{
				//UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
//				logBean.saveOpLog(pHashMap.get("account").toString(), "0", "�ϴ��ļ�:"+localFile+"���豸:"+devNo+"·��:"+viewpath);
				//return new ModelAndView("control/dev_document_browser_result").addObject("message","�ϴ��ļ��ɹ���");
				rHashMap.put("retCode", 1);
				rHashMap.put("retMsg", SystemLanguage.getSrcUploadFilesuccess());   
//				rHashMap.put("retMsg", "�ϴ��ļ�:"+uploadedFileName+"���豸:"+devNo+"·��:"+viewpath);
				RemoteControlDAO.saveRemoteTrace("200005",pHashMap.get("user").toString(), devNo, SystemLanguage.getControlRemoteControlResultSuccess(), SystemLanguage.getControlUploadFile() + "|" + viewpath);
			}
			else
			{
				//return new ModelAndView("control/dev_document_browser_result").addObject("message","�ϴ��ļ�ʧ�ܣ�");
				RemoteControlDAO.saveRemoteTrace("200005",pHashMap.get("user").toString(), devNo, SystemLanguage.getMainFailed(), SystemLanguage.getControlUploadFile() + "|" + viewpath);
				rHashMap.put("retCode", 0);
				rHashMap.put("retMsg", SystemLanguage.getSrcUploadFileFai());
//				rHashMap.put("retMsg", "�ϴ��ļ�ʧ�ܣ�");
			}
		}
		else
		{
			//return new ModelAndView("control/dev_document_browser_result").addObject("message","�ϴ��ļ�ʧ�ܣ�"); 
			RemoteControlDAO.saveRemoteTrace("200005",pHashMap.get("user").toString(), devNo, SystemLanguage.getMainFailed(), SystemLanguage.getControlUploadFile() + "|" + viewpath);
			rHashMap.put("retCode", 0);
			rHashMap.put("retMsg", SystemLanguage.getSrcUploadFileFai());   
//			rHashMap.put("retMsg", "�ϴ��ļ�ʧ�ܣ�");
		}
		return rHashMap;
	}
	/**
	 * ɾ���ͻ����ϲ���Ա�ƶ����ļ�
	 * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 *  key:�豸�� devInfo;
	 *  	�ļ�·�� remoteFile;
	 *  	Remoteҵ������˿� remotePort;
	 *  	��ǰ�û� user;
	 *      ÿ�������С packetLen;
	 *      ѹ����ʽ vzipType;
	 *      �豸ip ipAddress;
	 *      �豸�� devNo;
	 *      ���к� serialNo;
	 * @return rHashMap
	 *  key:������ retCode 0��1;
	 *  	������Ϣ retMsg;
	 * */
	public Map<String, Object> delFile(Map<String, Object> pHashMap) throws Exception
	{
		Map<String, Object> rHashMap=new HashMap<String, Object>();
		
		String ipAddress = (String)pHashMap.get("ip");
		String devNo = (String)pHashMap.get("devNo");
		String terminalNo = devNo;//terminalNo=devInfo.getTerminalNo() alter by ssli 20080604();
		String serialNo = (String)pHashMap.get("serialNo") ;		
		int packetLen = (Integer)pHashMap.get("packetLen");
		int vzipType = (Integer)pHashMap.get("vzipType");
		
		String remotePort=pHashMap.get("remotePort").toString();
		
		/*�ļ�·��*/
		String remoteFile=pHashMap.get("remoteFile").toString();
		log.info("remoteFile="+remoteFile);
		String cmdDate = CalendarUtil.getSysTimeYMD();	//RVS�������������
		String cmdTime = CalendarUtil.getSysTimeHMS();	//RVS���������ʱ��
		String actionTime=cmdDate+cmdTime;
		String msgId=UUID.randomUUID().toString();		
		
		StringBuffer inputXML=new StringBuffer();	 
		inputXML.append("<?xml version=\"1.0\"?>").append("<root>")
		        .append("<cmdid value=\"200006\" />")
		        //.append("<mgrtype value=\"SH\"/>")
				.append("<msgid value=\"").append(msgId).append("\" />")
				.append("<cmddatetime date=\"").append(cmdDate).append("\" time=\"").append(cmdTime).append("\" />")
				.append("<operatorinfo userid=\"zjft\"/>")
				.append("<actioninfo time=\"").append(actionTime).append("\" filename=\"").append(remoteFile).append("\" />")			   
				.append("<remote ipaddress=\"").append(ipAddress)
				.append("\" termno=\"").append(terminalNo)	
				.append("\" serialno=\"").append(serialNo).append("\" />")
			    .append("</root>");
		
		StringBuffer retBody=new StringBuffer();		
		
		/*������Զ�̰汾������ͨѶ�ӿ�*/											
		//boolean cmdFlag=SocketUtil.sendCmdToRvcMutil(inputXML.toString(),ipAddress,remotePort,retBody,20,MAX_TIMEOUT,packetLen,vzipType);
		//���Ӳ����豸�ţ�ͨ����Ϣת������ȡ�豸�ļ�����Կ��������ת��    2011-8-22  mod by cy
		NDC.push(ipAddress) ;
		boolean cmdFlag=MessageEncoded.sendCmdToRvcMutil(inputXML.toString(),ipAddress,remotePort,retBody,20,MAX_TIMEOUT,packetLen,vzipType, terminalNo);
		NDC.pop() ;
				
		if(!cmdFlag)
		{
//			return new ModelAndView("system/param_set_result").addObject("result","��Զ���豸ͨѶ����");	 	
			rHashMap.put("retCode", "0");
			rHashMap.put("retMsg", SystemLanguage.getSrcCommRemFai());
//			rHashMap.put("retMsg", "��Զ���豸ͨѶ����");
			return rHashMap;
		}	
		else
		{	
			String retCode = XmlUtil.getValue(retBody.toString(),"//root/retcode","value");
			
			if(retCode!=null&&retCode.equals("RMT000"))
			{
//				UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
//				logBean.saveOpLog(userSession.getAccount(), "0", "ɾ���豸:"+devNo+"�ϵ��ļ�:"+remoteFile);
//				
//				return new ModelAndView("system/param_set_result").addObject("result","ɾ��Զ�̿ͻ����ļ��ɹ���");	
				RemoteControlDAO.saveRemoteTrace("200006",pHashMap.get("user").toString(), devNo, SystemLanguage.getControlRemoteControlResultSuccess(), SystemLanguage.getControlDelFile() + "|" + remoteFile);
				rHashMap.put("retCode", "1");
				rHashMap.put("retMsg", SystemLanguage.getSrcDeleteRemoteSuc());
//				rHashMap.put("retMsg", "ɾ��Զ�̿ͻ����ļ��ɹ���");
				return rHashMap;
			}
			else
			{
//				UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
//				logBean.saveOpLog(userSession.getAccount(), "1", "ɾ���豸:"+devNo+"�ϵ��ļ�:"+remoteFile);
//				
//				return new ModelAndView("system/param_set_result").addObject("result","ɾ��Զ�̿ͻ�����ʧ�ܣ�");
				RemoteControlDAO.saveRemoteTrace("200006",pHashMap.get("user").toString(), devNo, SystemLanguage.getMainFailed(), SystemLanguage.getControlDelFile() + "|" + remoteFile);
				rHashMap.put("retCode", "0");
				rHashMap.put("retMsg", SystemLanguage.getSrcDeleteRemoteFai());
//				rHashMap.put("retMsg", "ɾ��Զ�̿ͻ����ļ�ʧ�ܣ�");
				return rHashMap;
			}
		}
	}
	
	/**
	 * ִ�пͻ����ϲ���Ա�ƶ����ļ�
	 * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 *  key:�豸�� devInfo;
	 *  	�ļ�·�� remoteFile;
	 *  	Remoteҵ������˿� remotePort;
	 *  	��ǰ�û� user;
	 *      ÿ�������С packetLen;
	 *      ѹ����ʽ vzipType;
	 *      �豸ip ipAddress;
	 *      �豸�� devNo;
	 *      ���к� serialNo;
	 * @return rHashMap
	 *  key:������ retCode 0��1;
	 *  	������Ϣ retMsg;
	 * */
	public Map<String, Object> excuteFile(Map<String, Object> pHashMap) throws Exception
	{
		HashMap<String, Object> rHashMap=new HashMap<String, Object>();
		
		String ipAddress = (String)pHashMap.get("ip");
		String devNo = (String)pHashMap.get("devNo");
		String terminalNo = devNo;//terminalNo=devInfo.getTerminalNo() alter by ssli 20080604();
		String serialNo = (String)pHashMap.get("serialNo") ;		
		int packetLen = (Integer)pHashMap.get("packetLen");
		int vzipType = (Integer)pHashMap.get("vzipType");
//		String remoteFilePort = pHashMap.get("remoteFilePort").toString();
		
		String remotePort=pHashMap.get("remotePort").toString();
//		String localFile=pHashMap.get("localFile").toString();
		
		/*�ļ�·��*/
		String remoteFile=pHashMap.get("remoteFile").toString();
		
		String cmdDate = CalendarUtil.getSysTimeYMD();	//RVS�������������
		String cmdTime = CalendarUtil.getSysTimeHMS();	//RVS���������ʱ��
		String actionTime=cmdDate+cmdTime;
		String msgId=UUID.randomUUID().toString();		
		
		StringBuffer inputXML=new StringBuffer();	 
		inputXML.append("<?xml version=\"1.0\"?>").append("<root>")
		        .append("<cmdid value=\"200001\" />")
		        //.append("<mgrtype value=\"SH\"/>")
				.append("<msgid value=\"").append(msgId).append("\" />")
				.append("<cmddatetime date=\"").append(cmdDate).append("\" time=\"").append(cmdTime).append("\" />")
				.append("<operatorinfo userid=\"zjft\"/>")
				.append("<actioninfo time=\"").append(actionTime).append("\" filename=\"").append(remoteFile).append("\" />")			   
				.append("<remote ipaddress=\"").append(ipAddress)
				.append("\" termno=\"").append(terminalNo) 
				.append("\" serialno=\"").append(serialNo).append("\" />")
			    .append("</root>");
		
		log.info("inputXML="+inputXML);
		StringBuffer retBody=new StringBuffer();		
		
		/*������Զ�̰汾������ͨѶ�ӿ�*/											
		//boolean cmdFlag=SocketUtil.sendCmdToRvcMutil(inputXML.toString(),ipAddress,remotePort,retBody,20,MAX_TIMEOUT,packetLen,vzipType);
		//���Ӳ����豸�ţ�ͨ����Ϣת������ȡ�豸�ļ�����Կ��������ת��    2011-8-22  mod by cy
		NDC.push(ipAddress) ;
		boolean cmdFlag=MessageEncoded.sendCmdToRvcMutil(inputXML.toString(),ipAddress,remotePort,retBody,20,MAX_TIMEOUT,packetLen,vzipType, terminalNo);
		NDC.pop() ;

		if(!cmdFlag)
		{
//			return new ModelAndView("system/param_set_result").addObject("result","��Զ���豸ͨѶ����");	
			RemoteControlDAO.saveRemoteTrace("200001",pHashMap.get("user").toString(), devNo, SystemLanguage.getMainFailed(), SystemLanguage.getControlExcuteFile() + "|" + remoteFile);
			rHashMap.put("retCode", "0");
			rHashMap.put("retMsg", SystemLanguage.getSrcCommRemFai());
//			rHashMap.put("retMsg", "��Զ���豸ͨѶ����");
			return rHashMap;
		}	
		else
		{	
//			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
//			logBean.saveOpLog(userSession.getAccount(), "0", "ִ���豸:"+devNo+"�ϵ��ļ�:"+remoteFile);
//			
//			return new ModelAndView("system/param_set_result").addObject("result","ִ��Զ�̿ͻ����ļ��ɹ���");
//			String reStr=FileUtil.getRvcFile(devNo, remoteFilePort, localFile, XmlUtil.getValue(retBody.toString(), "//root/actioninfo", "filename"), "", "",1,20,SocketUtil.getsoTime("200001"),packetLen,vzipType);
//			if(!reStr.equals("0000"))
//			{
//				RemoteControlDAO.saveRemoteTrace("200001",pHashMap.get("user").toString(), devNo, "ʧ��", "ִ���ļ�:"+remoteFile);
//				rHashMap.put("retCode", "0");
////				rHashMap.put("retMsg", "remotecontrol.error-communication");
//				rHashMap.put("retMsg", "ȡִ�з����ļ�����");
//				return rHashMap;
//			}
			/*��������*/
			String retCode = XmlUtil.getValue(retBody.toString(),"//root/retcode","value");
			
			if(retCode!=null&&retCode.equals("RMT000"))
			{
				RemoteControlDAO.saveRemoteTrace("200001",pHashMap.get("user").toString(), devNo, SystemLanguage.getControlRemoteControlResultSuccess(), SystemLanguage.getControlExcuteFile() + "|" + remoteFile);
				rHashMap.put("retCode", "1");
				rHashMap.put("retMsg", SystemLanguage.getSrcImpRemFileSuc());
	//			rHashMap.put("retMsg", "ִ��Զ�̿ͻ����ļ��ɹ���");}
			}else{
				RemoteControlDAO.saveRemoteTrace("200001",pHashMap.get("user").toString(), devNo, SystemLanguage.getMainFailed(), SystemLanguage.getControlExcuteFile() + "|" + remoteFile);
				rHashMap.put("retCode", "0");
				rHashMap.put("retMsg", SystemLanguage.getSrcImpRemFilefailed());
//				rHashMap.put("retMsg", "��Զ���豸ͨѶ����");
				return rHashMap;
			}
			return rHashMap;
		}
	}

	
	/**
	 * ִ��ִ������
	 * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 *  key:�豸������ devInfo(devno,ip,packetLen,vzipType);
	 *  	���� cmdId
	 *  	�Ƿ���Ҫ��ȡ�ļ���־ fileFlag
	 *  	Remoteҵ������˿� remotePort;
	 *  	Remote�ļ������˿� remoteFilePort;
	 *  	v���ļ����·�� dirPath
	 *  	��־���� logDate
	 *  	���б�־ bankVersion
	 *  	��ǰ�û� user;
	 *      ÿ�������С packetLen;
	 *      ѹ����ʽ vzipType;
	 * @return rHashMap
	 *  key:������ retCode 0��1;
	 *  	������Ϣ retMsg;
	 *  	����� resultList
	 * */
	public Map<String, Object> remoteControl(Map<String, Object> pHashMap) throws Exception
	{
		
		String cmdId = pHashMap.get("cmdId").toString();
		String bankVersion = (String)pHashMap.get("bankVersion");
		//�㽭�����������ֽӿڣ�Ϊ�˲�Ӱ�������У�ԭ���Ĵ��벻�����޸ģ��������Ĵ������
		if(cmdId.equals("200032"))//�����޸�administrator����
		{
			ChangePwdControl control = new ChangePwdControl();
			return control.remoteControl(pHashMap);
		}
		else if(cmdId.equals("200033"))//�޸�ע���Ȩ��
		{
			OsRegeditControl control = new OsRegeditControl();
			return control.remoteControl(pHashMap);
		}
		else if(cmdId.equals("200034"))//�޸�usb�豸Ȩ��
		{
			OsUsbControl control = new OsUsbControl();
			return control.remoteControl(pHashMap);
		}
		//ƽ����������ͬ������ӿ�
		if(cmdId.equals("200040")&& BANK_PA.equalsIgnoreCase(bankVersion)){
			SyncCardControl control = new SyncCardControl() ;
			return control.remoteControl(pHashMap) ;
		}
		//��ҵ�����豸�������ú�֪ͨc��ȥp�˸��²���
		if(cmdId.equals("200040")&& BANK_XY.equalsIgnoreCase(bankVersion)){
			SyncParamControl control = new SyncParamControl() ;
			return control.remoteControl(pHashMap) ;
		}
		
		if(cmdId.equals("200003") && BANK_XY.equalsIgnoreCase(bankVersion)) { //��ҵ������ȡ������־
			return remoteControlCIB(pHashMap);
		}
		
		//ZIJINMOD ��ȫ�� 2013-12-23 ��׼�����ӿ���/�ر��ֽ𻺴桢����/�رչ��ֺŻ��棬����Զ�̿��ƴ���
		if(cmdId.equals("2000431") || cmdId.equals("2000432") || cmdId.equals("2000433") || cmdId.equals("2000434")) {
			BufferMemoryControl control = new BufferMemoryControl();
			return control.remoteControl(pHashMap) ;
		}
		
		//ZIJINMOD ��ȫ�� 2013-12-23 ����/�رմ����ֺ�ƾ����ӡ������/�ر�ȡ����ֺ�ƾ����ӡ
		if(cmdId.equals("20003060") || cmdId.equals("20003061") || cmdId.equals("20003070") || cmdId.equals("20003071")) {
			NtsReceiptPrintControl control = new NtsReceiptPrintControl();
			return control.remoteControl(pHashMap) ;
		}
		
		HashMap<String, Object> rHashMap=new HashMap<String, Object>();
		/*�豸��Ϣ*/			
//		String[] devInfo= request.getParameter("devList").toString().split("\\-");		
//		String cmdId = request.getParameter("cmdId");		
//		String fileFlag = request.getParameter("fileFlag");//�Ƿ�Ҫ��ȡ�ļ���־		
		
		String[] devInfo= (String[]) pHashMap.get("devInfo");
		
		String fileFlag = pHashMap.get("fileFlag").toString();//�Ƿ�Ҫ��ȡ�ļ���־	
		
		int restarttype= -1;
		if(pHashMap.get("restarttype")!=null&&!pHashMap.get("restarttype").equals(""))
		{
			restarttype = Integer.valueOf((String)pHashMap.get("restarttype"));//0:����������1������������ -1:Ĭ��
		}
		int shutdowntype= -1;
		if(pHashMap.get("shutdowntype")!=null&&!pHashMap.get("shutdowntype").equals(""))
		{
			shutdowntype = Integer.valueOf((String)pHashMap.get("shutdowntype"));//0:�����ػ���1�����йػ��� -1:Ĭ��
		}
		
		String screen = "-1";
		int screenflag= -1;
		if(pHashMap.get("screenflag")!=null&&!pHashMap.get("screenflag").equals(""))
		{
			screenflag = Integer.valueOf((String)pHashMap.get("screenflag"));//0:������1�������� -1:Ĭ��
			if(screenflag == 0){
				screen = "����";
			}
			if(screenflag == 1){
				screen = "����";
			}
		}
		
		String[] tempRemote=null;
		
		List resultList = new ArrayList();//�豸��Ϣ�б�			
		boolean result ;
		boolean result2 = false;//״̬���c��p��ͨѶ��� 
		
//		String remotePort = shepherd.getSystemParam("1", "remotePort");
//		String remoteFilePort = shepherd.getSystemParam("1", "remoteFilePort");	
//		String remoteOs2Port = shepherd.getSystemParam("1", "remoteOs2Port");
		String remotePort = pHashMap.get("remotePort").toString();
		String remoteFilePort = pHashMap.get("remoteFilePort").toString();
		String remoteOs2Port = pHashMap.get("remoteOs2Port").toString();
		
		StringBuffer retBody =new StringBuffer();						
		String msgId = null; 		
		String remoteFile = null;
		String localFile = null;
		String dirPath = null;
		String fileName = null;
		String fileProp = pHashMap.get("fileProp")==null||"".equals(pHashMap.get("fileProp").toString())?".PDF":pHashMap.get("fileProp").toString();
		String resultContent = SystemLanguage.getSrcCommFai();
		String resultContent2 = SystemLanguage.getSrcCommFai();
		String retcode=null;
		String retcode2=null;//״̬���c��p��ͨѶ���
		String logDate =null;
		//ȡ�ó�ʱʱ��
		int soTime=SocketUtil.getsoTime(cmdId);
		
		/*���������Ҫ��ȡ�ļ�*/
		if(fileFlag!=null&&fileFlag.equals("1"))
		{				
			//dirPath = shepherd.getSystemParam("1", "versionFilePath")+System.getProperty("file.separator")+"file"+System.getProperty("file.separator")+"download"+System.getProperty("file.separator")+"temp";
			dirPath = pHashMap.get("dirPath").toString();
			File dir = new File(dirPath);
			
			if (!dir.isDirectory()) 
			{
	             dir.mkdirs();	                   
	        }
		}
		
//		UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
		
		for(int i=0;i<devInfo.length;i++)
		{			
			tempRemote = devInfo[i].split("\\|");				
			int packetLen=Integer.valueOf(tempRemote[2].toString());
			int vzipType=Integer.valueOf(tempRemote[3].toString());
			
			msgId = UUID.randomUUID().toString();
			
			if(fileFlag!=null&&fileFlag.equals("1"))
			{
				remoteFile = msgId+".txt";
				
				if(cmdId.equals("200003")) //ZIJINMOD ��ȫ�� 2014-01-24 ����������ȡ������־·���޸ģ����ǵ�tcr��־·�����ܲ�ͬ�����豸������Ϣ���������dev_log_path����tcr��־·������������ڸ��ֶλ��߸��ֶ�����Ϊ�գ���ʹ��Ĭ�ϵ�·��
				{
					String devLogPath = DevInfoDAO.getDevLogPathBydevNo(tempRemote[0]);
					if (devLogPath != null && !"".equals(devLogPath)) {
						logDate = StringUtil.parseString(pHashMap.get("logDate").toString());
						logDate = (isNullOrBlank(logDate)) ? CalendarUtil.getDate() : logDate.substring(0, 8);
						
						devLogPath = devLogPath.replaceAll("/", "\\");
						fileName = devLogPath.substring(devLogPath.lastIndexOf("\\") + 1);
						fileName = fileName.replaceAll("yyyyMMdd", logDate);
						fileName = fileName.replaceAll("YYYYMMDD", logDate);
						fileName = fileName.replaceAll("yyyymmdd", logDate);
						String fileSuffix = fileName.substring(fileName.lastIndexOf(".") < 0 ? 0:fileName.lastIndexOf("."));
						devLogPath = devLogPath.substring(0, devLogPath.lastIndexOf("\\")) + "\\";
						if ("".equals(fileName)) {
							fileName = logDate + ".PDF";
							fileSuffix = ".PDF";
						}
						
						remoteFile = devLogPath + fileName; 
						fileProp = fileSuffix;
						
					} 
//					else if(!isNullOrBlank(bankVersion) && (BANK_JH.equalsIgnoreCase(bankVersion) 
//							|| BANK_BZ.equalsIgnoreCase(bankVersion) || BANK_CZ.equalsIgnoreCase(bankVersion)))
//					{
//						if(BANK_JH.equalsIgnoreCase(bankVersion) || BANK_BZ.equalsIgnoreCase(bankVersion))
//						{
//							logDate = pHashMap.get("logDate").toString();
//							logDate=(logDate==null||logDate.equals(""))?CalendarUtil.getDate()+"C":logDate;							
//							remoteFile = "C:\\WSAP\\DATA\\"+tempRemote[0]+"_"+StringUtil.parseString(logDate)+".J"; 
//							fileName = tempRemote[0]+"_"+StringUtil.parseString(logDate);
//							fileProp = ".J";
//						}
//						
//					}
					else
					{
						HashMap<String, Object> param=new HashMap<String, Object>();
						logDate = StringUtil.parseString(pHashMap.get("logDate").toString());
						logDate = (isNullOrBlank(logDate))?CalendarUtil.getDate():logDate;
						param.put("logDate", logDate);
						param.put("devNo", tempRemote[0]);
						remoteFile = AtmLogRule.getAtmLogRule(param);
						fileName = remoteFile.substring(remoteFile.replace("\\", "/").lastIndexOf("/")+1,remoteFile.lastIndexOf("."));
						fileProp = AtmLogRule.getAtmcLogPorp();
					}
				}
				else if(cmdId.equals("200007"))
				{
					fileName = tempRemote[0];
					fileProp = ".jpg";
				}
				else
				{
					fileName = tempRemote[0];
					fileProp = ".txt";
				}
				localFile = dirPath+System.getProperty("file.separator")+msgId+fileProp;
			}
			
//			DevBaseInfo dev = (DevBaseInfo) shepherd.retrieveObject(DevBaseInfo.class, tempRemote[0]);
//			boolean ncr_os2 = (cmdId.equals("200026") || cmdId.equals("200027")) && dev.getOs().equals("10003") && (dev.getDevVendor()==10001);
//			boolean dbd_os2 = (cmdId.equals("200026") || cmdId.equals("200027")) && dev.getOs().equals("10003") && (dev.getDevVendor()==10002);
//			// || cmdId.equals("200014") �����ݷ��
//			if(ncr_os2)//ncr os2�����������߼���,��,���� ����
//			{
//				String ncrcmd = this.genString(cmdId,tempRemote[0],1);	
//				result = SocketUtil.sendCmdToOs2(ncrcmd, tempRemote[1], remoteOs2Port, retBody,20,soTime);
//			
//			}
//			else if(dbd_os2)//dbd os2�����������߼���,��,���� ����
//			{
//				String dbdcmd = this.genString(cmdId,tempRemote[0],2);
//				result = SocketUtil.sendCmdToOs2(dbdcmd, tempRemote[1], remoteOs2Port, retBody,20,soTime);	
//			}
//			else
//			{
//				result = SocketUtil.sendCmdToRvcMutil(this.genXML(cmdId,msgId,tempRemote[0],tempRemote[1],localFile,remoteFile), tempRemote[1], remotePort, retBody,20,soTime);
//			}
			try {
			//result = SocketUtil.sendCmdToRvcMutil(this.genXML(cmdId,msgId,tempRemote[0],tempRemote[1],localFile,remoteFile, restarttype, screenflag,shutdowntype), tempRemote[1], remotePort, retBody,20,soTime,packetLen,vzipType);
			    //���Ӳ����豸�ţ�ͨ����Ϣת������ȡ�豸�ļ�����Կ��������ת��    2011-8-22  mod by cy
				NDC.push(tempRemote[1]) ;
				result = MessageEncoded.sendCmdToRvcMutil(this.genXML(cmdId,msgId,tempRemote[0],tempRemote[1],localFile,remoteFile, restarttype, screenflag,shutdowntype), tempRemote[1], remotePort, retBody,20,soTime,packetLen,vzipType, tempRemote[0]);
				NDC.pop() ;
			/*��ȡ�ļ�*/
			if(fileFlag!=null&&fileFlag.equals("1")&&result)
			{				
//				result = FileUtil.getRvcFile(tempRemote[1], remoteFilePort, localFile, XmlUtil.getValue(retBody.toString(), "//root/actioninfo", "filename"), "", "", 1);
//				if(!result)
				NDC.push(tempRemote[1]) ;
				String reStr=FileUtil.getRvcFile(tempRemote[1], remoteFilePort, localFile, XmlUtil.getValue(retBody.toString(), "//root/actioninfo", "filename"), "", "",1,20,SocketUtil.getsoTime(cmdId),packetLen,vzipType);
				NDC.pop() ;
				if(!reStr.equals("0000"))
				{
					resultContent= SystemLanguage.getSrcOperateFail();
					result=false;
				}
				else
				{
					resultContent = SystemLanguage.getSrcOperateSuccess();
				}
			}
			
			if(fileFlag!=null&&fileFlag.equals("0")&&result)
			{
//				if(ncr_os2)	
//				{
//					if(retBody.toString().substring(0, 3).equals("MSU"))
//					{
//						resultContent = SystemLanguage.getSrcOperateSuccess();
//					}
//					else
//					{
//						resultContent= SystemLanguage.getSrcOperateFail();
//						result=false;
//					}
//				}
//				else
//				{						
					retcode = XmlUtil.getValue(retBody.toString(), "//root/retcode", "value");				
					if(retcode.equals("RMT000"))
					{
						resultContent= SystemLanguage.getSrcOperateSuccess();
					}
					else if(retcode.equals("000001"))
					{
						result=false;
						resultContent=convertCommandId(cmdId)+ "|" +SystemLanguage.getSrcOperateFail();
					}
					else if(retcode.equals("000000"))
					{
						resultContent=convertCommandId(cmdId)+ "|" + SystemLanguage.getSrcOperateSuccess();
//						result=false;
					}
					else
					{
						resultContent = SystemLanguage.getSrcOperateFail();
						result=false;
					}
					if(cmdId.equals("200011")){
						retcode2 = XmlUtil.getValue(retBody.toString(), "//root/retcode2", "value");
						if(retcode2==null){
							resultContent2= SystemLanguage.getSrcOperateFail();
							result2=false;
						}
						else if(retcode2.equals("RMT000"))
						{
							result2=true;
							resultContent2= SystemLanguage.getSrcOperateSuccess();
						}
						else if(retcode2.equals("000001"))
						{
							result=false;
							resultContent2=convertCommandId(cmdId)+ "|" + SystemLanguage.getSrcOperateFail();
						}
						else if(retcode2.equals("000000"))
						{
							resultContent2=convertCommandId(cmdId)+ "|" + SystemLanguage.getSrcOperateSuccess();
							result2=false;
						}
						else
						{
							resultContent2 = SystemLanguage.getSrcOperateFail();
							result2=false;
						}	
						log.info("retcode2="+retcode2);
						result=result&&result2;
						resultContent= SystemLanguage.getControlVsideAndEquipmentCommunications() + "|" + resultContent + "|" + SystemLanguage.getControlPsideAndEquipmentCommunications() + "|" + resultContent2;
					}
//				}
			}
			
			/*�豸�š�IP�����������������͡��Ƿ��н���ļ����ļ����ơ�����*/
			resultList.add(new Object[]{tempRemote[0],tempRemote[1],result,fileFlag,resultContent,localFile,fileName,fileProp});
			retBody.delete(0, retBody.length());
			resultContent = SystemLanguage.getSrcCommFai();
			if (cmdId.equals("200003")){
				RemoteControlDAO.saveRemoteTrace(cmdId,pHashMap.get("user").toString(),tempRemote[0],(result?SystemLanguage.getControlRemoteControlResultSuccess():SystemLanguage.getMainFailed()),convertCommandId(cmdId) + "|" + SystemLanguage.getControlremoteControlLogDate() + "|" + logDate);
			}
			else if(cmdId.equals("200007")){
				RemoteControlDAO.saveRemoteTrace(cmdId,pHashMap.get("user").toString(),tempRemote[0],(result?SystemLanguage.getControlRemoteControlResultSuccess():SystemLanguage.getMainFailed()),convertCommandId(cmdId) + "|" + screen);
			}
			else{
				if(pHashMap.get("batchFlag")!=null&&!pHashMap.get("batchFlag").equals("")){
					RemoteControlDAO.saveRemoteTrace(cmdId,pHashMap.get("user").toString(),tempRemote[0],(result?SystemLanguage.getControlRemoteControlResultSuccess():SystemLanguage.getMainFailed()),convertCommandId(cmdId) + "|" + "��������");
				}else{
					RemoteControlDAO.saveRemoteTrace(cmdId,pHashMap.get("user").toString(),tempRemote[0],(result?SystemLanguage.getControlRemoteControlResultSuccess():SystemLanguage.getMainFailed()),convertCommandId(cmdId));
				}
			}
			
			rHashMap.put("retCode", 1);
			rHashMap.put("resultList", resultList);
			} catch (Exception e) {
				// TODO: handle exception
				if (cmdId.equals("200003")){
					RemoteControlDAO.saveRemoteTrace(cmdId,pHashMap.get("user").toString(),tempRemote[0],SystemLanguage.getMainFailed(),convertCommandId(cmdId) +  "|" + SystemLanguage.getControlremoteControlLogDate() + "|" + logDate);
				}
			if(cmdId.equals("200007")){
					RemoteControlDAO.saveRemoteTrace(cmdId,pHashMap.get("user").toString(),tempRemote[0],SystemLanguage.getMainFailed(),convertCommandId(cmdId)+ "|" + screen);
				}
				else{
					if(pHashMap.get("batchFlag")!=null&&!pHashMap.get("batchFlag").equals("")){
						RemoteControlDAO.saveRemoteTrace(cmdId,pHashMap.get("user").toString(),tempRemote[0],SystemLanguage.getMainFailed(),convertCommandId(cmdId) + "|" + "��������");
					}else{
						RemoteControlDAO.saveRemoteTrace(cmdId,pHashMap.get("user").toString(),tempRemote[0],SystemLanguage.getMainFailed(),convertCommandId(cmdId));
					}
				}
				rHashMap.put("retCode", 0);
				rHashMap.put("retMsg", SystemLanguage.getSrcCommRemFai());
//				rHashMap.put("retMsg", "��Զ���豸ͨѶ����");
			}
	}
	return rHashMap;
}
	
	/**
	 * ִ��ִ������
	 * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 *  key:�豸������ devInfo(devno,ip,packetLen,vzipType);
	 *  	���� cmdId
	 *  	�Ƿ���Ҫ��ȡ�ļ���־ fileFlag
	 *  	Remoteҵ������˿� remotePort;
	 *  	Remote�ļ������˿� remoteFilePort;
	 *  	v���ļ����·�� dirPath
	 *  	��־���� logDate
	 *  	���б�־ bankVersion
	 *  	��ǰ�û� user;
	 *      ÿ�������С packetLen;
	 *      ѹ����ʽ vzipType;
	 * @return rHashMap
	 *  key:������ retCode 0��1;
	 *  	������Ϣ retMsg;
	 *  	����� resultList
	 * */
	public Map<String, Object> remoteControlCIB(Map<String, Object> pHashMap) throws Exception{
		
		Map<String, Object> rHashMap=new HashMap<String, Object>();
		
		String cmdId = pHashMap.get("cmdId").toString();
		String[] devInfo= (String[]) pHashMap.get("devInfo");
		String fileFlag = pHashMap.get("fileFlag").toString();//�Ƿ�Ҫ��ȡ�ļ���־	
		String[] tempRemote=null;
		List resultList = new ArrayList();//�豸��Ϣ�б�			
		boolean result ;
		String remotePort = pHashMap.get("remotePort").toString();
		String remoteFilePort = pHashMap.get("remoteFilePort").toString();
		
		StringBuffer retBody =new StringBuffer();						
		String msgId = null; 		
		String remoteFile = null;
		String localFile = null;
		String dirPath = null;
		String fileName = null;
		String fileProp = null;
		String resultContent = SystemLanguage.getSrcCommFai();
		String retcode=null;
		String logDate =null;
		//ȡ�ó�ʱʱ��
		int soTime=SocketUtil.getsoTime(cmdId);
		
		List<String> remoteFiles = (List<String>) pHashMap.get("remoteFiles");
		
		/*���������Ҫ��ȡ�ļ�*/
		if(fileFlag!=null&&fileFlag.equals("1")) {				
			dirPath = pHashMap.get("dirPath").toString();
			File dir = new File(dirPath);
			
			if (!dir.isDirectory()) {
	             dir.mkdirs();	    	                   
	        }
		}
		for(int i=0;i<devInfo.length;i++){
			for(int j=0;j<remoteFiles.size();j++) {
				tempRemote = devInfo[i].split("\\|");				
				int packetLen=Integer.valueOf(tempRemote[2].toString());
				int vzipType=Integer.valueOf(tempRemote[3].toString());
				
				msgId = UUID.randomUUID().toString();
				
				logDate = StringUtil.parseString(pHashMap.get("logDate").toString());
				logDate=logDate.substring(0, 8);
				String[] file = remoteFiles.get(j).split("\\|",0);
				remoteFile = file[0];
				fileName = file[1];
				fileProp = remoteFile.substring(remoteFile.indexOf('.'));
				
				String fileType = file[2];
				localFile = dirPath+System.getProperty("file.separator")+msgId+fileProp;
				log.info("�ļ�·����"+remoteFile);
				log.info("�ļ�����"+fileName);
				log.info("�ļ����ԣ�"+fileProp);
				
				logDate = pHashMap.get("logDate").toString();
				
				String curDate = CalendarUtil.getDate();
				
				if(!fileType.equals("2") || logDate.equals(curDate)) {
					//�����ļ�ǰ����Զ��ѹ��ָ��
					boolean flag = true ; //ѹ���ļ��ɹ�����־
					boolean operateFlag = false ;//�����ɹ�����־
					String retCode = "" ;
			        String sendBody = "" ;
			        String destfile = "" ;
			        sendBody = getFilePackXml("200041", UUID.randomUUID().toString(), tempRemote[1], tempRemote[0], remoteFile, "file") ;
			        NDC.push(tempRemote[1]) ;
			        boolean statusFlag = MessageEncoded.sendCmdToRvcMutil(sendBody, tempRemote[1],remotePort , retBody, 20,20,packetLen,vzipType, tempRemote[0]) ;
			        NDC.pop() ;
			        System.out.println("=====retBody===="+retBody);
			        System.out.println("====statusFlag====" + statusFlag);
			        if(!statusFlag) {
					    log.info("����ѹ������ʧ��!") ;
					    System.out.println("����ѹ������ʧ��");
					    flag = false ;
					} else {
						log.info("����ѹ�����ĳɹ�!") ;
						System.out.println("����ѹ�����ĳɹ�!");
						retCode = XmlUtil.getValue(retBody.toString(),"//root/retcode","value");
						destfile  = XmlUtil.getValue(retBody.toString(), "//root/destfile", "pathname");
						if(retCode == null || destfile == null){
							log.info("retCode��destfileΪ��!") ;
							System.out.println("retCode��destfileΪ��!");
							flag = false ;
						}else{
							if(retCode.equals("000000")||retCode.equals("RMT000"))
							{
								log.info("ѹ���ļ��ɹ�!") ;
								System.out.println("ѹ���ļ��ɹ�!");
								rHashMap.put("retCode", 1);
								rHashMap.put("retMsg", "ѹ���ļ��ɹ�!");
								flag = true ;
							}else if(retCode.equals("RMT401")){
								log.info("action�Ƿ�") ;
								System.out.println("action�Ƿ�");
							    rHashMap.put("retCode", 0);
								rHashMap.put("retMsg", "action�Ƿ�!");
								flag = false ;
							}else if(retCode.equals("RMT402")){
								log.info("ѹ�����ͷǷ�") ;
								System.out.println("ѹ�����ͷǷ�");
								rHashMap.put("retCode", 0);
								rHashMap.put("retMsg", "ѹ�����ͷǷ�!");
								flag = false ;
							}else if(retCode.equals("RMT403")){
								log.info("������һ���ļ�������") ;
								System.out.println("������һ���ļ�������");
								rHashMap.put("retCode", 0);
								rHashMap.put("retMsg", "������һ���ļ�������!");
								flag = false ;
							}else if(retCode.equals("RMT404")){
								log.info("���̿ռ䲻��") ;
								System.out.println("���̿ռ䲻��");
							    rHashMap.put("retCode", 0);
							    rHashMap.put("retMsg", "���̿ռ䲻��!");
							    flag = false ;
							}else if(retCode.equals("RMT405")){
								log.info("��֧�ֳ���Դ�ļ�") ;
								System.out.println("��֧�ֳ���Դ�ļ�");
							    rHashMap.put("retCode", 0);
							    rHashMap.put("retMsg", "��֧�ֳ���Դ�ļ�!");
							    flag = false ;
							}else{
								log.info("δ֪ԭ��") ;
								System.out.println("δ֪ԭ��");
							    rHashMap.put("retCode", 0);
							    rHashMap.put("retMsg", "δ֪ԭ��!");
							    flag = false ;
							}
						}
					}
			        if(!flag) {
			        	return rHashMap;
			        }
			        
			        remoteFile = destfile;
			        fileProp = remoteFile.substring(remoteFile.indexOf('.'));
			        localFile = dirPath+System.getProperty("file.separator")+msgId+fileProp;
				}
				
				log.info("locfilename="+localFile);
				
				try {
					NDC.push(tempRemote[1]) ;
					result = MessageEncoded.sendCmdToRvcMutil(this.genXML(cmdId,msgId,tempRemote[0],tempRemote[1],localFile,remoteFile, -1, -1,-1), tempRemote[1], remotePort, retBody,20,soTime,packetLen,vzipType, tempRemote[0]);
					NDC.pop() ;
				if(fileFlag!=null&&fileFlag.equals("1")&&result){				
					NDC.push(tempRemote[1]) ;
					String reStr=FileUtil.getRvcFile(tempRemote[1], remoteFilePort, localFile,remoteFile, "", "",1,20,SocketUtil.getsoTime(cmdId),packetLen,vzipType);
					NDC.pop() ;
					if(!reStr.equals("0000"))
					{
						resultContent= SystemLanguage.getSrcOperateFail();
						result=false;
					}
					else
					{
						resultContent = SystemLanguage.getSrcOperateSuccess();
					}
				}
				if(fileFlag!=null&&fileFlag.equals("0")&&result){		
						retcode = XmlUtil.getValue(retBody.toString(), "//root/retcode", "value");					
						if(retcode.equals("RMT000"))
						{
							resultContent= SystemLanguage.getSrcOperateSuccess();
						}
						else if(retcode.equals("000001"))
						{
							result=false;
							resultContent=convertCommandId(cmdId)+ "|" +SystemLanguage.getSrcOperateFail();
						}
						else if(retcode.equals("000000"))
						{
							resultContent=convertCommandId(cmdId)+ "|" + SystemLanguage.getSrcOperateSuccess();
						}
						else
						{
							resultContent = SystemLanguage.getSrcOperateFail();
							result=false;
						}	
				}
				
				/*�豸�š�IP�����������������͡��Ƿ��н���ļ����ļ����ơ�����*/
				resultList.add(new Object[]{tempRemote[0],tempRemote[1],result,fileFlag,resultContent,localFile,fileName,fileProp});
				retBody.delete(0, retBody.length());
				resultContent = SystemLanguage.getSrcCommFai();		
				
				RemoteControlDAO.saveRemoteTrace(cmdId,pHashMap.get("user").toString(),tempRemote[0],(result?SystemLanguage.getControlRemoteControlResultSuccess():SystemLanguage.getMainFailed()),convertCommandId(cmdId) + "|" + SystemLanguage.getControlremoteControlLogDate() + "|" + logDate);
				rHashMap.put("retCode", 1);
				rHashMap.put("resultList", resultList);
				} catch (Exception e) {
					RemoteControlDAO.saveRemoteTrace(cmdId,pHashMap.get("user").toString(),tempRemote[0],SystemLanguage.getMainFailed(),convertCommandId(cmdId) +  "|" + SystemLanguage.getControlremoteControlLogDate() + "|" + logDate);
					rHashMap.put("retCode", 0);
					rHashMap.put("retMsg", SystemLanguage.getSrcCommRemFai());
				}
			}
		}
		return rHashMap;
	}
	
	/**  
	 * �������� createOrUpdateAtmFunction
	 * �������ܣ��������߸���ATMC�Ĺ�����Ϣ
	 * ������̣�(������������Ĵ����߼�)
	 * ������������� pHashMap
	 * 	key:�豸�� devNo;
	 *  	�豸IP ip
	 *  	�������� paramtype
	 *  	��������ֵ paramtypeValue;
	 *  	Remoteҵ������˿� remotePort;
	 *  	��ǰ�û� user;
	 *  	ͨѶÿ�������С packetLen
	 *      ͨѶѹ����ʽ vzipType
	 *      �������� functionType
	 * ������������� rHashMap
	 * 	key:������ retCode 0��1;
	 *  	������Ϣ retMsg;
	 *  	���� result
	 * �쳣���������� 
	 * �����ˣ�  lyk 2009.04.01
	 * �޸���Ϣ��zhangdd��xsxu 2017-05-03 V1.4.3
	*/ 
	public Map<String, Object> createOrUpdateAtmFunction(Map<String, Object> pHashMap)throws Exception
	{		
		Map<String, Object> rHashMap=new HashMap<String, Object>();
		
		rHashMap.put("retCode", "1");
		
		String devNo = pHashMap.get("devNo").toString();
		String ip = pHashMap.get("ip").toString();
		String remotePort = pHashMap.get("remotePort").toString();
		String atmFunction = pHashMap.get("atmFunction").toString();
		String user = pHashMap.get("user").toString();
		int packetLen=Integer.valueOf(pHashMap.get("packetLen").toString());
		int vzipType=Integer.valueOf(pHashMap.get("vzipType").toString());

		//	zjmod by zhangdd 2017-04-13 V1.4.3 C���¼ܹ����ܶ���ӿڱ䶯 start
		String paramtype = pHashMap.get("paramtype").toString();
		//���ܶ���ӿ����ͣ�1-���֣�01|��ѯ����2-��ĸ��query|��ѯ����
		String functionType = pHashMap.get("functionType")==null||"".equals(pHashMap.get("functionType"))?"1":pHashMap.get("functionType").toString();
		//  zjmod by zhangdd 2017-04-13 V1.4.3 C���¼ܹ����ܶ���ӿڱ䶯 end
		try
		{
			//xsxu 2017-05-03 v1.4.3 C���¼ܹ����ܶ���ӿڱ䶯 start
			if("2".equals(functionType)){//xsxu 2017-05-03 v1.4.3 functionTypeΪ2��c���¼ܹ���atmFunctionΪ�Զ��ŷָ��Ĺ���Ӣ�����ַ�������atmFunctionΪ�գ�Ĭ��Ϊ���ַ���
				if(atmFunction==null){
					atmFunction = "";
				}
			}else{//xsxu 2017-05-03 v1.4.3 c�˾ɼܹ���atmFunctionΪ128λ��01�ַ���
				if(atmFunction==null){
					atmFunction = "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
				}else {
					atmFunction =atmFunction+"00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
					atmFunction = atmFunction.substring(0,128);
				}
			}
			//xsxu 2017-05-03 v1.4.3 C���¼ܹ����ܶ���ӿڱ䶯 end
//			if(atmFunction==null)
//			{
//				return mv.addObject("result", "�豸"+devNo+"��������ʧ�ܣ����������б��ɹ���");
//			}
			/*���豸���Ͷ����*/
			StringBuffer xml = new StringBuffer();
			
			xml.append("<?xml version=\"1.0\"?>");
			xml.append("<root>");
			xml.append("<cmdid value=\"").append("200030").append("\"/>");
			xml.append("<msgid value=\"").append(UUID.randomUUID().toString()).append("\"/>");
			xml.append("<cmddatetime date=\"").append(CalendarUtil.getDate()).append("\" time=\"").append(CalendarUtil.getTime()).append("\"/>");
			xml.append("<operatorinfo userid=\"zjft\"/>");
			//	zjmod by zhangdd 2017-04-13 C���¼ܹ����ܶ���ӿڱ䶯
			xml.append("<functioninfo ").append(paramtype).append(" =\"").append(atmFunction).append("\"/>");		
			xml.append("<actioninfo time=\"").append(CalendarUtil.getDate()).append(" ").append(CalendarUtil.getTime()).append("\"/>");
			xml.append("<managerinfo ipaddress=\"").append(ip).append("\"/>");
			xml.append("<remote ipaddress=\"").append(ip).append("\" termno=\"").append(devNo).append("\" serialno=\"").append(devNo).append("\"/>");
			xml.append("</root>");	
			
			StringBuffer retBody =new StringBuffer();
			
//			ȡ�ó�ʱʱ��
			int soTime=SocketUtil.getsoTime("200030");
			//boolean result = SocketUtil.sendCmdToRvcMutil(xml.toString(), ip, remotePort, retBody,20,soTime,packetLen,vzipType);
			//���Ӳ����豸�ţ�ͨ����Ϣת������ȡ�豸�ļ�����Կ��������ת��    2011-8-22  mod by cy
			NDC.push(ip) ;
			boolean result = MessageEncoded.sendCmdToRvcMutil(xml.toString(), ip, remotePort, retBody,20,soTime,packetLen,vzipType, devNo);
			NDC.pop() ;
			if(!result)
			{
//				logBean.saveOpLog(userSession.getAccount(), "1", "�豸"+devNo+"��������ʧ�ܣ����豸ͨ�Ų��ɹ���");
				RemoteControlDAO.saveRemoteTrace("200030",user,devNo,SystemLanguage.getMainFailed(), SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcFunctionFail());
				rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcFunctionFail());
				rHashMap.put("retCode", "0");
				return rHashMap;
			}
			/*��������*/
			if(XmlUtil.getValue(retBody.toString(), "//root/retcode", "value").equals("000000"))
			{
//				logBean.saveOpLog(userSession.getAccount(), "0", "�豸"+devNo+"�������óɹ���"+XmlUtil.getValue(retBody.toString(), "//root/retcode", "remark"));
//				mv.addObject("result", "�豸"+devNo+"�������óɹ���"+XmlUtil.getValue(retBody.toString(), "//root/retcode", "remark"));
				RemoteControlDAO.saveRemoteTrace("200030",user,devNo,SystemLanguage.getControlRemoteControlResultSuccess(),SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcFunctionSuccess());
				rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcFunctionSuccess());
				
				List<Object> aList = DbProxoolUtil.query("select t.dev_no from atmc_function_table t where t.dev_no='"+devNo+"'",0);
				if (aList==null||aList.size()==0){
					//zhangdd 2017-04-13 V1.4.3 ȥ��NumberUtil.binaryToHexString(atmFunction)�����ԭ��:atmc_function_table��trans_list��VARCHAR(32)���VARCHAR(500)�������㹻�洢�������ٽ���ת�������������c���¼ܹ��Ļ����洢�Ĳ���01�����ƶ����Զ��ŷָ���Ӣ�ġ�����ͳһֱ�Ӵ洢����ȡ����ֶ���Ϣʱ��Ҳͳһ�����������ж�������ϼܹ��Ļ��ٽ���ʮ������ת�����ƵĲ���
					String sql="insert into atmc_function_table(dev_no,trans_list) values('"+devNo+"','"+atmFunction+"')";
					DbProxoolUtil.insert(sql);
				}else{
					//zhangdd 2017-04-13 V1.4.3 ȥ��NumberUtil.binaryToHexString(atmFunction)�����ԭ��:ͬ��
					String sql="update atmc_function_table set trans_list='"+atmFunction+"' where dev_no='"+devNo+"'";
					DbProxoolUtil.update(sql);
				}
			}
			else
			{
//				logBean.saveOpLog(userSession.getAccount(), "1", "�豸"+devNo+"��������ʧ�ܣ�"+XmlUtil.getValue(retBody.toString(), "//root/retcode", "remark"));
//				return mv.addObject("result", "�豸"+devNo+"��������ʧ�ܣ�"+XmlUtil.getValue(retBody.toString(), "//root/retcode", "remark"));
				RemoteControlDAO.saveRemoteTrace("200030",user,devNo,SystemLanguage.getMainFailed(),SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcFunctionFail2());
				rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcFunctionFail2());
				rHashMap.put("retCode", "0");
				
			}
		}
		catch(Exception e)
		{
			log.info("���������ATMC������Ϣʧ�ܣ�ʧ��ԭ��:"+e) ;
			//e.printStackTrace();
			rHashMap.put("retCode", "0");
			RemoteControlDAO.saveRemoteTrace("200030",user,devNo,SystemLanguage.getMainFailed(),SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcFunctionFail2() + "|" +e.getMessage());
			rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcFunctionFail2() + "|" +e.getMessage());
			
		}
		return rHashMap;
	}
	
	/**
	 * �������߸���ATMC����־�������Ϣ
	 * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 *  key:�豸�� devNo;
	 *  	�豸IP ip
	 *  	Remoteҵ������˿� remotePort;
	 *  	��ǰ�û� user;
	 *  	ͨѶÿ�������С packetLen
	 *      ͨѶѹ����ʽ vzipType
	 * @return rHashMap
	 *  key:������ retCode 0��1;
	 *  	������Ϣ retMsg;
	 * */
	public Map<String, Object> createOrUpdateAtmLog(Map<String, Object> pHashMap)throws Exception
	{		
		Map<String, Object> rHashMap=new HashMap<String, Object>();
		rHashMap.put("retCode", "1");
		String devNo = pHashMap.get("devNo").toString();
		String ip = pHashMap.get("ip").toString();
		String remotePort = pHashMap.get("remotePort").toString();
		String atmLog = pHashMap.get("atmLog").toString();
		String user = pHashMap.get("user").toString();
		int packetLen=Integer.valueOf(pHashMap.get("packetLen").toString());
		int vzipType=Integer.valueOf(pHashMap.get("vzipType").toString());
		
		try
		{			
			/*���豸���Ͷ����*/
			StringBuffer xml = new StringBuffer();
			
			xml.append("<?xml version=\"1.0\"?>");
			xml.append("<root>");
			xml.append("<cmdid value=\"").append("200030").append("\"/>");
			xml.append("<msgid value=\"").append(UUID.randomUUID().toString()).append("\"/>");
			xml.append("<cmddatetime date=\"").append(CalendarUtil.getDate()).append("\" time=\"").append(CalendarUtil.getTime()).append("\"/>");
			xml.append("<operatorinfo userid=\"zjft\"/>");
			xml.append("<functioninfo paramtype2 =\"").append(atmLog).append("\"/>");		
			xml.append("<actioninfo time=\"").append(CalendarUtil.getDate()).append(" ").append(CalendarUtil.getTime()).append("\"/>");
			xml.append("<managerinfo ipaddress=\"").append(ip).append("\"/>");
			xml.append("<remote ipaddress=\"").append(ip).append("\" termno=\"").append(devNo).append("\" serialno=\"").append(devNo).append("\"/>");
			xml.append("</root>");	
			
			StringBuffer retBody =new StringBuffer();
//			ȡ�ó�ʱʱ��
			int soTime=SocketUtil.getsoTime("200030");			
			//boolean result = SocketUtil.sendCmdToRvcMutil(xml.toString(), ip, remotePort, retBody,20,soTime,packetLen,vzipType);
			//���Ӳ����豸�ţ�ͨ����Ϣת������ȡ�豸�ļ�����Կ��������ת��    2011-8-22  mod by cy
			NDC.push(ip) ;
			boolean result = MessageEncoded.sendCmdToRvcMutil(xml.toString(), ip, remotePort, retBody,20,soTime,packetLen,vzipType, devNo);
			NDC.pop() ;
			
			if(!result)
			{
//				logBean.saveOpLog(userSession.getAccount(), "1", "�豸"+devNo+"��־����ػ�������ʧ�ܣ����豸ͨ�Ų��ɹ���");
//				return mv.addObject("result", "�豸"+devNo+"��־����ػ�������ʧ�ܣ����豸ͨ�Ų��ɹ���");
				RemoteControlDAO.saveRemoteTrace("200031",user,devNo,SystemLanguage.getMainFailed(),SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcLogCommFailed());
				rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcLogCommFailed());
			}
			/*��������*/
			
			
			if(XmlUtil.getValue(retBody.toString(), "//root/retcode", "value").equals("000000"))
			{
//				logBean.saveOpLog(userSession.getAccount(), "0", "�豸"+devNo+"��־����ػ������óɹ���"+XmlUtil.getValue(retBody.toString(), "//root/retcode", "remark"));
//				mv.addObject("retMsg", "�豸"+devNo+"�������óɹ���"+XmlUtil.getValue(retBody.toString(), "//root/retcode", "remark"));
				RemoteControlDAO.saveRemoteTrace("200031",user,devNo,SystemLanguage.getControlRemoteControlResultSuccess(),SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcLogCacheSuccess());
				rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcLogCacheSuccess());
				
				List<Object> aList = DbProxoolUtil.query("select t.dev_no from atmc_function_table t where t.dev_no='"+devNo+"'",0);
				if (aList==null||aList.size()==0){
					String sql="insert into atmc_function_table(dev_no,trans_list) values('"+devNo+"','')";
					DbProxoolUtil.insert(sql);
				}else{
					String sql="update atmc_function_table set trans_list='' where dev_no='"+devNo+"'";
					DbProxoolUtil.update(sql);
				}
			}
			else
			{
//				logBean.saveOpLog(userSession.getAccount(), "1", "�豸"+devNo+"��־����ػ�������ʧ�ܣ�"+XmlUtil.getValue(retBody.toString(), "//root/retcode", "remark"));
//				return mv.addObject("retMsg", "�豸"+devNo+"��־����ػ�������ʧ�ܣ�"+XmlUtil.getValue(retBody.toString(), "//root/retcode", "remark"));
				RemoteControlDAO.saveRemoteTrace("200031",user,devNo,SystemLanguage.getMainFailed(),SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcLogCacheFailed());
				rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcLogCacheFailed());
				
			}
		}
		catch(Exception e)
		{
			log.info("�������߸���ATMC����־�������Ϣʧ�ܣ�ʧ��ԭ��:"+e) ;
			//e.printStackTrace();
//			mv.addObject("retMsg", "�豸"+devNo+"���������쳣,ʧ��:"+e.getMessage());
			rHashMap.put("retCode", "0");
			RemoteControlDAO.saveRemoteTrace("200031",user,devNo,SystemLanguage.getMainFailed(),SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcLogCacheFailed() + "|" + e.getMessage());
			rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcLogCacheFailed() + "|" + e.getMessage());
		}
		return rHashMap;
	}
	
	/**  
	 * �������� getAtmParam
	 * �������ܣ���ȡ�豸�Ĺ����б���Ϣ����
	 * ������̣�(������������Ĵ����߼�)
	 * ������������� pHashMap 
	 *  key:�豸�� devNo;
	 *  	�豸IP ip
	 *  	�������� paramtype
	 *  	��������ֵ paramtypeValue;
	 *  	Remoteҵ������˿� remotePort;
	 *  	ͨѶÿ�������С packetLen
	 *      ͨѶѹ����ʽ vzipType
	 * ������������� rHashMap
	 *  key:������ retCode 0��1;
	 *  	������Ϣ retMsg;
	 *  	���� atmFunction
	 * �쳣���������� 
	 * �����ˣ�  lyk 2009.04.01
	 * �޸���Ϣ��zhangdd 2017-04-13 V1.4.3
	*/ 
	public Map<String, Object> getAtmParam(Map<String, Object> pHashMap) throws Exception {
		
		Map<String, Object> rHashMap=new HashMap<String, Object>();
		rHashMap.put("retCode", "0");
		
		String devNo = pHashMap.get("devNo").toString();
		String ip = pHashMap.get("ip").toString();
		String paramtype = pHashMap.get("paramtype").toString();
		String paramtypeValue = pHashMap.get("paramtypeValue").toString();
		String remotePort = pHashMap.get("remotePort").toString();
		int packetLen=Integer.valueOf(pHashMap.get("packetLen").toString());
		int vzipType=Integer.valueOf(pHashMap.get("vzipType").toString());
		log.info("[VrequestParam] "+"devNo:"+devNo+","+"devNo:"+devNo+","+"ip:"+ip+","+"paramtype:"+paramtype+","+"paramtypeValue:"+paramtypeValue+","+"remotePort:"+remotePort+","+"packetLen:"+packetLen+","+"vzipType:"+vzipType);
		try {	
			/*���豸���Ͷ����*/
			StringBuffer xml = new StringBuffer();
			
			xml.append("<?xml version=\"1.0\"?>");
			xml.append("<root>");
			xml.append("<cmdid value=\"").append("200031").append("\"/>");
			xml.append("<msgid value=\"").append(UUID.randomUUID().toString()).append("\"/>");
			xml.append("<cmddatetime date=\"").append(CalendarUtil.getDate()).append("\" time=\"").append(CalendarUtil.getTime()).append("\"/>");
			xml.append("<operatorinfo userid=\"zjft\"/>");	
			xml.append("<functioninfo ").append(paramtype).append(" =\"").append(paramtypeValue).append("\"/>");	
			xml.append("<actioninfo time=\"").append(CalendarUtil.getDate()).append(" ").append(CalendarUtil.getTime()).append("\"/>");
			xml.append("<managerinfo ipaddress=\"").append(ip).append("\"/>");
			xml.append("<remote ipaddress=\"").append(ip).append("\" termno=\"").append(devNo).append("\" serialno=\"").append(devNo).append("\"/>");
			xml.append("</root>");	
			
			StringBuffer retBody =new StringBuffer();			
//			ȡ�ó�ʱʱ��
			int soTime=SocketUtil.getsoTime("200031");	
			//���Ӳ����豸�ţ�ͨ����Ϣת������ȡ�豸�ļ�����Կ��������ת��    2011-8-22  mod by cy
			NDC.push(ip) ;
			if(!MessageEncoded.sendCmdToRvcMutil(xml.toString(), ip, remotePort, retBody,20,soTime,packetLen,vzipType, devNo)) {				
				NDC.pop() ;
				rHashMap.put("retMsg", SystemLanguage.getSrcOperateFail());
				return rHashMap;
			}
			NDC.pop();
			log.info("���ڽ����ı�������:" + retBody.toString());
			String retParamValue = XmlUtil.getValue(retBody.toString(), "//root/functioninfo", paramtype);
	    //  zjmod by zhangdd 2017-04-13 V1.4.3 C���¼ܹ����ܶ���ӿڱ䶯 
			if(paramtype.equals("paramtype1")||paramtype.equals("paramtype9")) {
				log.info("atmFunctionlist="+retParamValue);
				rHashMap.put("atmFunctionlist", retParamValue);
			} else if(paramtype.equals("paramtype5")) {
				log.info("compositeCardStatus="+retParamValue);
				rHashMap.put("compositeCardStatus", retParamValue);
			} else {
				log.info("�������ʹ���["+paramtype+"]");
				return null;
			}

			rHashMap.put("retMsg", SystemLanguage.getSrcOperateSuccess());
			rHashMap.put("retCode", "1");
			return rHashMap;
		} catch (Exception e) {
			log.error("��ȡ�豸�����б���Ϣ����ʧ�ܣ�ʧ��ԭ��:",e) ;
			rHashMap.put("retMsg", SystemLanguage.getSrcOperateFail());
			return rHashMap;
		}
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
		} else {
			return SystemLanguage.getSrcInvalidControl();
		}
	}
	
	/**
	 * ��֯��Զ���豸��ͨѶ����
	 */
	public String genXML(String cmdId,String msgId,String devNo,String ip,String localFile,String remoteFile,Integer restarttype,Integer screenflag,Integer shutdowntype)
	{
		
		StringBuffer inputXML = new StringBuffer();	  				    			
		inputXML.append("<?xml version=\"1.0\"?>");
		inputXML.append("<root>");
		inputXML.append("<cmdid value=\"").append(cmdId).append("\"/>");
		inputXML.append("<msgid value=\"").append(msgId).append("\"/>");
		inputXML.append("<cmddatetime date=\"").append(CalendarUtil.getDate()).append("\" time=\"").append(CalendarUtil.getTime()).append("\"/>");
		inputXML.append("<operatorinfo userid=\"zjft\"/>");
		inputXML.append("<actioninfo time=\"").append(CalendarUtil.getDate()).append(" ").append(CalendarUtil.getTime());
		if (cmdId!=null && cmdId.equals("200014") && restarttype!=-1){//������Ҫȷ����������Ӧ������
			inputXML.append("\" restarapptype=\"").append(restarttype);
		}
		if (cmdId!=null && cmdId.equals("200015") && restarttype!=-1){//�ػ���Ҫȷ����������Ӧ�ùػ�
			inputXML.append("\" restarapptype=\"").append(shutdowntype);
		}
		inputXML.append("\" locfilename=\"").append(localFile).append("\" filename=\"").append(remoteFile).append("\"/>");
		inputXML.append("<managerinfo ipaddress=\"").append(ip).append("\"/>");
		inputXML.append("<remote ipaddress=\"").append(ip).append("\" termno=\"").append(devNo).append("\" serialno=\"").append(devNo).append("\"/>");
		if (cmdId!=null && cmdId.equals("200007") && screenflag!=-1){
			inputXML.append("<screenflag value=\"").append(screenflag).append("\"/>");
		}
		if(cmdId != null && cmdId.equals("200011") && SystemCons.getBankVersion().equalsIgnoreCase("bobj"))
		{
			DevAtmpInfo devAtmpInfo = DevInfoDAO.getDevAtmpInfo(devNo);
			if(devAtmpInfo != null)
			{
				inputXML.append("<atmp ip=\"").append(devAtmpInfo.getAtmpIp()).append("\" port=\"").append(devAtmpInfo.getAtmpPort()).append("\"/>");
			}
		}
		inputXML.append("</root>");
		log.info("rmi generate:" + inputXML.toString());
		return inputXML.toString();
	}
	
	/*
	 * ��ȡ�豸�ĳ�����Ϣ
	 * 2011-8-2  add by cy
	 */
	public Map<String, Object> getCashboxInfo (Map<String, Object> pHashMap) throws Exception
	{
		Map<String,Object> rHashMap = new HashMap<String,Object>();
		rHashMap.put("retCode", "0");       //0 ʧ��  1 �ɹ�
		
		String devNo = pHashMap.get("devNo").toString();
		String ip = pHashMap.get("ip").toString();
		int packetLen = Integer.valueOf(pHashMap.get("packetLen").toString());
		int vzipType = Integer.valueOf(pHashMap.get("vzipType").toString());
		String remotePort = pHashMap.get("remotePort").toString();
		
		List cashInfo = new ArrayList();
		try
		{

			StringBuffer retBody =new StringBuffer();
//			ȡ�ó�ʱʱ��
			int soTime=SocketUtil.getsoTime("200030");			
			//boolean result = SocketUtil.sendCmdToRvcMutil(getCashInfoXml("200031",ip,devNo,"CXBS","CXLX"), ip, remotePort, retBody,20,soTime,packetLen,vzipType);
			//���Ӳ����豸�ţ�ͨ����Ϣת������ȡ�豸�ļ�����Կ��������ת��    2011-8-22  mod by cy
			NDC.push(ip) ;
			boolean result = MessageEncoded.sendCmdToRvcMutil(getCashInfoXml("200031",ip,devNo,"CXBS","CXLX"), ip, remotePort, retBody,20,soTime,packetLen,vzipType, devNo);
			if(!result)
			{
				NDC.pop() ;
				rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+"|"+SystemLanguage.getSrcGetCashboxInfo()+"|"+SystemLanguage.getMainFailed());
				return rHashMap;
			}
			NDC.pop() ;

			String cxbs = XmlUtil.getValue(retBody.toString(), "//root/functioninfo", "paramtype3");
			String cxlx = XmlUtil.getValue(retBody.toString(), "//root/functioninfo", "paramtype4");

			if(cxbs == null || cxbs.equals("") || cxlx == null || cxlx.equals(""))
			{
				rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+"|"+SystemLanguage.getSrcGetCashboxInfo()+"|"+SystemLanguage.getMainFailed());
				return rHashMap;
			}
			
			String[] cxbsArray = cxbs.split(",",-1);
			String[] cxlxArray = cxlx.split(",",-1);
			String[] cashArray = null;
			for(int i = 0;i<cxbsArray.length;i++)
			{
				cashArray = new String[2];
				cashArray[0]=cxbsArray[i];
				cashArray[1]=cxlxArray[i];
				cashInfo.add(cashArray);
			}
			rHashMap.put("retCode", "1");       //0 ʧ��  1 �ɹ�
			rHashMap.put("cashInfo", cashInfo);
		}
		catch(Exception e)
		{
			log.info("��ȡ�豸������Ϣʧ�ܣ�");
			rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+"|"+SystemLanguage.getSrcCommFai());
			return rHashMap;
		}
		return rHashMap;
	}
	
	
	/*
	 * �����豸�ĳ�����Ϣ
	 * 2011-8-2  add by cy
	 */
	public Map<String, Object> setCashboxInfo(Map<String, Object> pHashMap) throws Exception
	{
		HashMap<String,Object> rHashMap = new HashMap<String,Object>();
		rHashMap.put("retCode", "0");       //0 ʧ��  1 �ɹ�
		
		String devNo = pHashMap.get("devNo").toString();
		String ip = pHashMap.get("ip").toString();
		String cashType = pHashMap.get("cashType").toString();
		String cashId = pHashMap.get("cashId").toString();
		int packetLen = Integer.valueOf(pHashMap.get("packetLen").toString());
		int vzipType = Integer.valueOf(pHashMap.get("vzipType").toString());
		String remotePort = pHashMap.get("remotePort").toString();
		String user = pHashMap.get("user").toString();
		
		List cashInfo = new ArrayList();
		try
		{
			//SETCASHBOX_PAGE_CMDID=200130  ��CMDID��Ҫ���ڳ�������ҳ������ѯʱ�빦�ܶ���������֣�ʵ�ʱ�����CMDID��δ�ı�  2011-8-8  add by cy
			StringBuffer retBody =new StringBuffer();
//			ȡ�ó�ʱʱ��
			int soTime=SocketUtil.getsoTime("200030");			
			//boolean result = SocketUtil.sendCmdToRvcMutil(getCashInfoXml("200030",ip,devNo,cashId,cashType), ip, remotePort, retBody,20,soTime,packetLen,vzipType);
			//���Ӳ����豸�ţ�ͨ����Ϣת������ȡ�豸�ļ�����Կ��������ת��    2011-8-22  mod by cy
			NDC.push(ip) ;
			boolean result = MessageEncoded.sendCmdToRvcMutil(getCashInfoXml("200030",ip,devNo,cashId,cashType), ip, remotePort, retBody,20,soTime,packetLen,vzipType, devNo);
			NDC.pop() ;
			if(!result)
			{
				rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+"|"+SystemLanguage.getSrcSetCashboxInfo()+"|"+SystemLanguage.getMainFailed());
				RemoteControlDAO.saveRemoteTrace(SETCASHBOX_PAGE_CMDID,user,devNo,SystemLanguage.getMainFailed(),SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+"|"+SystemLanguage.getSrcSetCashboxInfo()+"|"+SystemLanguage.getMainFailed());
				return rHashMap;
			}
			
			String retcode = XmlUtil.getValue(retBody.toString(), "//root/retcode", "value");

			if(retcode == null || !retcode.equals("000000"))
			{
				rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+"|"+SystemLanguage.getSrcSetCashboxInfo()+"|"+SystemLanguage.getMainFailed());
				RemoteControlDAO.saveRemoteTrace(SETCASHBOX_PAGE_CMDID,user,devNo,SystemLanguage.getMainFailed(),SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcSetCashboxInfo()+"|"+SystemLanguage.getMainFailed());
				return rHashMap;
			}

			rHashMap.put("retCode", "1");       //0 ʧ��  1 �ɹ�
		}
		catch(Exception e)
		{
			log.info("��ȡ�豸������Ϣʧ�ܣ�");
			rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+"|"+SystemLanguage.getSrcCommFai());
			RemoteControlDAO.saveRemoteTrace(SETCASHBOX_PAGE_CMDID,user,devNo,SystemLanguage.getMainFailed(),SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcCommFai());
			return rHashMap;
		}
		RemoteControlDAO.saveRemoteTrace(SETCASHBOX_PAGE_CMDID,user,devNo,SystemLanguage.getControlRemoteControlResultSuccess(),SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcSetCashboxInfo()+"|"+SystemLanguage.getControlRemoteControlResultSuccess());
		return rHashMap;
	}
	
	
	/**
	 * �����������и��Ͽ������رչ���
	 * @author cy
	 * @since 2013-4-23
	 * @param pHashMap 
	 *  key:�豸�� devNo;
	 *  	�豸IP ip
	 *  	Remoteҵ������˿� remotePort;
	 *  	��ǰ�û� user;
	 *  	ͨѶÿ�������С packetLen
	 *      ͨѶѹ����ʽ vzipType
	 * @return rHashMap
	 *  key:������ retCode 0��1;
	 *  	������Ϣ retMsg;
	 *  	���� result
	 * */
	public Map<String, Object> createOrUpdateCompositeCardStatus(Map<String, Object> pHashMap)throws Exception
	{		
		Map<String, Object> rHashMap=new HashMap<String, Object>();
		rHashMap.put("retCode", "1");
		
		String devNo = pHashMap.get("devNo").toString();
		String ip = pHashMap.get("ip").toString();
		String remotePort = pHashMap.get("remotePort").toString();
		String atmFunction = pHashMap.get("atmFunction").toString();
		String user = pHashMap.get("user").toString();
		int packetLen=Integer.valueOf(pHashMap.get("packetLen").toString());
		int vzipType=Integer.valueOf(pHashMap.get("vzipType").toString());
		
		String atmFunctionMsg = atmFunction.equals("0")?SystemLanguage.getCompositeCardClose():SystemLanguage.getCompositeCardOpen();//0-�رգ�1-����
		try
		{
			/*���豸���Ͷ����*/
			StringBuffer xml = new StringBuffer();
			
			xml.append("<?xml version=\"1.0\"?>");
			xml.append("<root>");
			xml.append("<cmdid value=\"").append("200030").append("\"/>");
			xml.append("<msgid value=\"").append(UUID.randomUUID().toString()).append("\"/>");
			xml.append("<cmddatetime date=\"").append(CalendarUtil.getDate()).append("\" time=\"").append(CalendarUtil.getTime()).append("\"/>");
			xml.append("<operatorinfo userid=\"zjft\"/>");
			xml.append("<functioninfo paramtype1=\"\" paramtype2=\"\" paramtype3=\"\" paramtype4=\"\" paramtype5=\"").append(atmFunction).append("\"/>");
			xml.append("<actioninfo time=\"").append(CalendarUtil.getDate()).append(" ").append(CalendarUtil.getTime()).append("\"/>");
			xml.append("<managerinfo ipaddress=\"").append(ip).append("\"/>");
			xml.append("<remote ipaddress=\"").append(ip).append("\" termno=\"").append(devNo).append("\" serialno=\"").append(devNo).append("\"/>");
			xml.append("</root>");	
			
			StringBuffer retBody =new StringBuffer();
			
//			ȡ�ó�ʱʱ��
			int soTime=SocketUtil.getsoTime("200030");
			//boolean result = SocketUtil.sendCmdToRvcMutil(xml.toString(), ip, remotePort, retBody,20,soTime,packetLen,vzipType);
			//���Ӳ����豸�ţ�ͨ����Ϣת������ȡ�豸�ļ�����Կ��������ת��    2011-8-22  mod by cy
			NDC.push(ip) ;
			boolean result = MessageEncoded.sendCmdToRvcMutil(xml.toString(), ip, remotePort, retBody,20,soTime,packetLen,vzipType, devNo);
			NDC.pop() ;
			if(!result)
			{
				rHashMap.put("retCode", "0");
//				logBean.saveOpLog(userSession.getAccount(), "1", "�豸"+devNo+"���Ͽ��������ܿ���ʧ��,ͨѶʧ�ܣ�");
				RemoteControlDAO.saveRemoteTrace(SETCOMPOSITECARDSTATUS_PAGE_CMDID,user,devNo,SystemLanguage.getMainFailed(), SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + atmFunctionMsg+ "|" + SystemLanguage.getMainFailed()+ "|,|" + SystemLanguage.getSrcCommFai());
				rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + atmFunctionMsg+ "|" + SystemLanguage.getMainFailed()+ "|,|" + SystemLanguage.getSrcCommFai());
				return rHashMap;
			}
			/*��������*/
			if(XmlUtil.getValue(retBody.toString(), "//root/retcode", "value").equals("000000"))
			{
//				logBean.saveOpLog(userSession.getAccount(), "0", "�豸"+devNo+"�������óɹ���"+XmlUtil.getValue(retBody.toString(), "//root/retcode", "remark"));
//				mv.addObject("result", "�豸"+devNo+"�������óɹ���"+XmlUtil.getValue(retBody.toString(), "//root/retcode", "remark"));
				RemoteControlDAO.saveRemoteTrace(SETCOMPOSITECARDSTATUS_PAGE_CMDID,user,devNo,SystemLanguage.getControlRemoteControlResultSuccess(),SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + atmFunctionMsg+ "|,|" + SystemLanguage.getSrcOperateSuccess());
				rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + atmFunctionMsg+ "|,|" + SystemLanguage.getSrcOperateSuccess());
			}
			else
			{
				rHashMap.put("retCode", "0");
//				logBean.saveOpLog(userSession.getAccount(), "1", "�豸"+devNo+"��������ʧ�ܣ�"+XmlUtil.getValue(retBody.toString(), "//root/retcode", "remark"));
//				return mv.addObject("result", "�豸"+devNo+"��������ʧ�ܣ�"+XmlUtil.getValue(retBody.toString(), "//root/retcode", "remark"));
				RemoteControlDAO.saveRemoteTrace(SETCOMPOSITECARDSTATUS_PAGE_CMDID,user,devNo,SystemLanguage.getMainFailed(),SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + atmFunctionMsg+ "|" + SystemLanguage.getMainFailed());
				rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + atmFunctionMsg+ "|" + SystemLanguage.getMainFailed());
				
			}
		}
		catch(Exception e)
		{
			log.info("���������ATMC������Ϣʧ�ܣ�ʧ��ԭ��:"+e) ;
			//e.printStackTrace();
			rHashMap.put("retCode", "0");
			RemoteControlDAO.saveRemoteTrace(SETCOMPOSITECARDSTATUS_PAGE_CMDID,user,devNo,SystemLanguage.getMainFailed(),SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + atmFunctionMsg+ "|" + SystemLanguage.getMainFailed() + "|" +e.getMessage());
			rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + atmFunctionMsg+ "|" + SystemLanguage.getMainFailed() + "|" +e.getMessage());
			
		}
		return rHashMap;
	}
	
	
	
	/*
	 * �༭�������ñ�����Ϣ
	 * 2011-8-2  add by cy
	 */
	private String getCashInfoXml(String cmdId,String ip,String devNo,String cxbs,String cxlx) {
		StringBuffer inputXML = new StringBuffer();	  				    			
		inputXML.append("<?xml version=\"1.0\"?>");
		inputXML.append("<root>");
		inputXML.append("<cmdid value=\"").append(cmdId).append("\"/>");
		inputXML.append("<cmddatetime date=\"").append(CalendarUtil.getDate()).append("\" time=\"").append(CalendarUtil.getTime()).append("\"/>");
		inputXML.append("<operatorinfo userid=\"zjft\"/>");
		inputXML.append("<actioninfo time=\"").append(CalendarUtil.getDate()).append(" ").append(CalendarUtil.getTime()).append("\"/>");
		inputXML.append("<managerinfo ipaddress=\"\"/>");
		inputXML.append("<remote ipaddress=\"").append(ip).append("\" serialno=\"").append(devNo).append("\"/>");
		inputXML.append("<functioninfo paramtype1=\"\" paramtype2=\"\" paramtype3=\"").append(cxbs).append("\" paramtype4=\"").append(cxlx).append("\"/>");
		inputXML.append("<retcode value=\"\"/>");
		inputXML.append("</root>");
		
		return inputXML.toString();
	}

	private boolean isNullOrBlank(String obj)
	{
		if(obj == null || obj.trim().equals(""))
		{
			return true;
		}
		return false;
	}

	//�Զ����� 2016-01-20 ZJADD zhangdd 
	public Map<String, Object> qryCashBoxSift(Map<String, Object> paraMap) {
		// TODO Auto-generated method stub
		HashMap<String, Object> rHashMap=new HashMap<String, Object>();
		HashMap<String, Object> addRmtTraceMap=new HashMap<String, Object>();
		String[] tempRemote=null;

		String[] devInfo = ((String[])paraMap.get("devInfo"));
		
		List resultList = new ArrayList();//���ص��豸��Ϣ�б�			
		boolean result = false;
		String reason = "";

		for(int i=0;i<devInfo.length;i++)
			{			
				tempRemote = devInfo[i].split("\\|");		
				String devNo = tempRemote[0].toString();
				String ipAddress =tempRemote[1].toString();
				int packetLen=Integer.valueOf(tempRemote[2].toString());
				int vzipType=Integer.valueOf(tempRemote[3].toString());
				String remotePort=paraMap.get("remotePort").toString();
				String date=paraMap.get("date").toString();
				String time=paraMap.get("time").toString();
				String userId=paraMap.get("userid").toString();
				String commandCache=paraMap.get("commandCache").toString();
				String cimCountAll=paraMap.get("cimCountAll").toString();
				String cimCashUnitCount=paraMap.get("cimCashUnitCount").toString();
				String cashUnitList=paraMap.get("cashUnitList").toString();
				String checkType=paraMap.get("checkType").toString();		
				
//				String cmdDate = CalendarUtil.getSysTimeYMD();	//RVS�������������
//				String cmdTime = CalendarUtil.getSysTimeHMS();	//RVS���������ʱ��
//				String actionTime=cmdDate+cmdTime;
				String msgId=UUID.randomUUID().toString();		
				
				addRmtTraceMap.put("cmdName", "200047");
				addRmtTraceMap.put("msgId", msgId);
				addRmtTraceMap.put("userId", userId);
				addRmtTraceMap.put("devNo", devNo);
				addRmtTraceMap.put("opContent", "�Զ�����");
				addRmtTraceMap.put("commandCache", commandCache);
				addRmtTraceMap.put("cimCountAll", cimCountAll);
				addRmtTraceMap.put("cimCashUnitCount", cimCashUnitCount);
				addRmtTraceMap.put("cashUnitList", cashUnitList);
				addRmtTraceMap.put("checkType", checkType);

				
				StringBuffer inputXML=new StringBuffer();	 
				inputXML.append("<?xml version=\"1.0\"?>").append("<root>")
				        .append("<cmdid value=\"200047\" />")
						.append("<msgid value=\"").append(msgId).append("\" />")
						.append("<cmddatetime date=\"").append(date).append("\" time=\"").append(time).append("\" />")
						.append("<operatorinfo userid=\"zjft\"/>")
						.append("<checktype value=\"").append(checkType).append("\" />")
						.append("<commandcache value=\"").append(commandCache).append("\" />")
						.append("<cimcount countall =\"").append(cimCountAll).append("\" cashunitcount=\"").append(cimCashUnitCount).append("\" cashunitlist=\"").append(cashUnitList).append("\" />")			   
						.append("<remote termno =\"").append(devNo).append("\" ipaddress=\"").append(ipAddress).append("\" />")
					    .append("</root>");
				
				log.info("�Զ�����inputXML="+inputXML);
				StringBuffer retBody=new StringBuffer();		
				try{
				NDC.push(ipAddress) ;
				boolean cmdFlag=MessageEncoded.sendCmdToRvcMutil(inputXML.toString(),ipAddress,remotePort,retBody,20,MAX_TIMEOUT,packetLen,vzipType, devNo);
				NDC.pop() ;


				if(!cmdFlag)
				{
					addRmtTraceMap.put("result", "����ʧ��");
					RemoteControlDAO.saveRemoteTrace(addRmtTraceMap);
					result = false;
					reason = "��Զ���豸ͨѶ����!";
				}	
				else
				{	
					/*�������ر���*/
					String retCode = XmlUtil.getValue(retBody.toString(),"//root/retcode","value");
					
					if(retCode!=null&&retCode.equals("RMT000"))
					{
						addRmtTraceMap.put("result", "�ɹ�");
						RemoteControlDAO.saveRemoteTrace(addRmtTraceMap);
						result = true;
						reason = "�Զ�����ɹ���";
					}else{
						addRmtTraceMap.put("result", "ʧ��");
						RemoteControlDAO.saveRemoteTrace(addRmtTraceMap);
						result = false;
						reason = "�Զ����鷵��ʧ�ܣ�";
					}
				}
				
				/*0���豸�� 1��IP 2 :��� 3:ԭ��*/
				resultList.add(new Object[]{tempRemote[0],tempRemote[1],result,"0",reason});
				rHashMap.put("retCode", 1);
				rHashMap.put("resultList", resultList);
				
				}catch(Exception e){
					addRmtTraceMap.put("result", "�����쳣");
					RemoteControlDAO.saveRemoteTrace(addRmtTraceMap);
					rHashMap.put("retCode", 0);
					rHashMap.put("retMsg", "�Զ����鷢���쳣��");
				}
		}
		return rHashMap;
	
	}
}

