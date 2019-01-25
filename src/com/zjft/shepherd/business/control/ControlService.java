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
 * 类名称： ControlService
 * 类描述： 远程控制后台实现类  根据页面提供参数，进行远程浏览文件夹和文件控制，返回hashmap供页面调用
 * 创建人：  ykliu
 * 修改信息：zhangdd 2017-04-13 V1.4.3 
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
	
	private static final String SETCASHBOX_PAGE_CMDID = "200130";   //此CMDID主要用于钞箱设置页面结果查询时与功能定义进行区分，实际报文中CMDID并未改变  2011-8-8  add by cy
	//此CMDID主要用于昆仑银行复合卡开启关闭进行区分，实际报文中CMDID并未改变  2013-4-23  add by cy
	private static final String SETCOMPOSITECARDSTATUS_PAGE_CMDID = "200230";
	
	/**
	 * 浏览磁盘或文件夹
	 * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 *  key:设备表 devInfo;
	 *     	文件路径 viewpath;
	 *  	文件名 docName;
	 *  	上级目录请求 upDoc;
	 *  	文件目录 dirPath;
	 *  	Remote业务监听端口 remotePort;
	 *  	Remote文件监听端口 remoteFilePort;
	 *      当前用户 user;
	 *      每包传输大小 packetLen;
	 *      压缩方式 vzipType;
	 *      设备ip ipAddress;
	 *      设备号 devNo;
	 *      序列号 serialNo;
	 * @return rHashMap
	 *  key:返回码 retCode 0或1;
	 *  	返回信息 retMsg;
	 *  	文件list documentFileList;
	 **/
	public Map<String, Object> viewDocFile(Map<String, Object> pHashMap) throws Exception {
		Map<String, Object> rHashMap=new HashMap<String, Object>();
		rHashMap.put("retCode", 0);
		rHashMap.put("retMsg", SystemLanguage.getSrcCommRemFai());
//		rHashMap.put("retMsg", "与远程设备通讯出错！");
		
		String ipAddress = (String)pHashMap.get("ip");
		String devNo = (String)pHashMap.get("devNo");
		String terminalNo = devNo;//terminalNo=devInfo.getTerminalNo() alter by ssli 20080604();
		String serialNo = (String)pHashMap.get("serialNo") ;
		int packetLen = (Integer)pHashMap.get("packetLen");
		int vzipType = (Integer)pHashMap.get("vzipType");
		
		
		String remotePort=pHashMap.get("remotePort").toString();
		String remoteFilePort=pHashMap.get("remoteFilePort").toString();
		
		
		/*文件路径*/
		String viewpath=pHashMap.get("viewpath").toString();
		if(!viewpath.equals(""))
		{			
//			viewpath=new String(viewpath.getBytes("ISO-8859-1"),"UTF-8");
			viewpath=viewpath.trim();			
		}
		/*文件名*/
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
		/*上级目录请求*/
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
		
		/*如果文件目录不存在则生成*/
		String dirPath=pHashMap.get("dirPath").toString();
		log.debug("dirPath"+dirPath);
		File dir = new File(dirPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		String cmdDate = CalendarUtil.getSysTimeYMD();	//RVS服务器规格日期
	
		String cmdTime = CalendarUtil.getSysTimeHMS();	//RVS服务器规格时间
		
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

		/*调用与远程版本服务器通讯接口*/			
		//boolean cmdFlag = SocketUtil.sendCmdToRvcMutil(inputXML.toString(),ipAddress,remotePort,retBody,20,MAX_TIMEOUT,packetLen,vzipType);
		//增加参数设备号，通过消息转发，获取设备的加密密钥，并进行转发    2011-8-22  mod by cy
		NDC.push(ipAddress) ;
		boolean cmdFlag = MessageEncoded.sendCmdToRvcMutil(inputXML.toString(),ipAddress,remotePort,retBody,20,MAX_TIMEOUT,packetLen,vzipType,terminalNo);
		NDC.pop() ;
		if (!cmdFlag) {
			rHashMap.put("retMsg", SystemLanguage.getSrcCommRemFai());
			log.info("与远程设备通讯出错,200018报文发送失败");
			rHashMap.put("retMsg", "与远程设备通讯出错！");
			RemoteControlDAO.saveRemoteTrace("200018",pHashMap.get("user").toString(),devNo, SystemLanguage.getMainFailed(), SystemLanguage.getControlRemoteBrowse()+"|与远程设备通讯出错！");
			return rHashMap;
		}
		
		/*解析报文*/
		Document document=DocumentHelper.parseText(retBody.toString());
		org.dom4j.Node fileNameNode=document.selectSingleNode("//root/actioninfo");
		if (fileNameNode == null) {
			rHashMap.put("retMsg", SystemLanguage.getSrcNodeNotFound());
			RemoteControlDAO.saveRemoteTrace("200018",pHashMap.get("user").toString(),devNo, SystemLanguage.getMainFailed(), SystemLanguage.getControlRemoteBrowse()+"|"+SystemLanguage.getSrcNodeNotFound());
			return rHashMap;
		}
		String fileName=fileNameNode.valueOf("@filename");//远程生成的文件名称
		
		String locfilename=dirPath+"/"+msgId+".txt";//定义接受到的文件名
		
		/* 获取取文件 */
		if(!FileUtil.getRvcFile(ipAddress,remoteFilePort,locfilename,fileName,ipAddress,terminalNo,0,20,MAX_TIMEOUT,packetLen,vzipType).equals("0000")) {
			rHashMap.put("retMsg", SystemLanguage.getSrcErrorFileList());
			RemoteControlDAO.saveRemoteTrace("200018",pHashMap.get("user").toString(),devNo, SystemLanguage.getMainFailed(), SystemLanguage.getControlRemoteBrowse()+"|"+SystemLanguage.getSrcErrorFileList());
			return rHashMap;
		}
			
		/*解析返回的文件内容,将文件内容按行读取出来进行放到(DocumentFileList)列表中送到页面*/
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
			   
			   if(viewpath==null||viewpath.equals("DISK"))//如果是磁盘列表
			   {
				   while ((lineString = bufferedReader.readLine()) != null)
				   {
					   /*如果该行为空*/
					   if(lineString.equals(""))
					   {
						   continue;
					   }
					   s = lineString.split("\\|", -1);		
					   //多语言展现
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
					   doc.setDocmentFileType(s[0].trim()); //设定磁盘
					   if(!s[1].trim().equals(""))
					   {
						   doc.setDiskName(s[1].trim()+"|(|"+s[2].trim()+"|)");        //磁盘名称(DVD-RW 驱动器)
					   }
					   else
					   {
						   doc.setDiskName(s[3].trim()+"|(|"+s[2].trim()+"|)");        //磁盘名称(DVD-RW 驱动器)
					   }					   
					   doc.setDiskPath(Tool.escape(s[2].trim()));//磁盘路径信息(c:\、d:\、e:\)
					   doc.setDocmentFileName(s[3].trim()); //磁盘名称
					   
					   /*磁盘大小*/
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
					   	/*磁盘剩余空间*/
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
			   else//浏览目录
			   {
				   List fileList=new ArrayList();
				   List docList=new ArrayList();

				   while ((lineString = bufferedReader.readLine()) != null)
				   {
					   /*如果该行为空*/
					   if(lineString.equals(""))
					   {
						   continue;
					   }
					   
					   s = lineString.split("\\|", -1);
					   doc =new DocumentFileList();
					   doc.setDocmentFileType(s[0].trim());//设定文件夹或文件
					   doc.setDocmentFileName(Tool.escape(s[1].trim()));//文件或文件夹名称
					   if(s[0].trim().equals("0"))//文件
					   {						  
						   String fileProperty=s[1].trim();
						   try//获取文件类型
						   {							
							   fileProperty=fileProperty.substring(fileProperty.lastIndexOf("."),fileProperty.length());							   		
							   fileProperty=fileProperty.substring(1,fileProperty.length());
						   }
						   catch(Exception e)
						   {
							   /*如果捕捉到异常则认为文件不带后缀名*/
							   fileProperty="";
						   }
						  
						   String fileSize = "0 KB";
						   
						   if(!isNullOrBlank(s[5]))
						   {
							   String tmepSize = s[5].trim();
							   int postion = tmepSize.lastIndexOf(" ");
							   double low = Double.parseDouble(tmepSize.substring(postion + 1));//文件大小低位
							   double high = 0;//文件大小高位
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
							   String docmentFileUpdateTime=s[4].trim();//去掉空格
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
							   doc.setDocmentFileUpdateTime(sdf.format(gc.getTime()));//设定文件夹或文件的修改时间
						   }
						   //bocom_shep_003p-----------------------------
						   doc.setDocmentFileMode(fileProperty);
						   fileList.add(doc);//将对象加入到文件List中
					   }
					   else//文件夹
					   {
						   docList.add(doc);//将对象加入到目录List中
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
			docFile.delete();// 删除生成的临时文件

			if (viewpath.equals("DISK")) {
				viewpath="";
				RemoteControlDAO.saveRemoteTrace("200018",pHashMap.get("user").toString(),devNo, SystemLanguage.getControlRemoteControlResultSuccess(), SystemLanguage.getControlRemoteBrowse());
			}
			rHashMap.put("documentFileList", documentFileList);
			rHashMap.put("retCode", 1);
			rHashMap.put("viewpath", viewpath);
			return rHashMap;
		} catch (Exception e) {
			log.error("远程浏览出现异常,", e);
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
	 * 下载ATM上的文件到操作员端
	 * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 *	key:设备表 devInfo;
	 *		文件路径 viewpath;
	 *		文件名 fileName;
	 *		fileBean
	 *		文件目录 versionFilePath
	 *		Remote文件监听端口 remoteFilePort
	 *		当前用户 user;
	 *		每包传输大小 packetLen;
	 *		压缩方式 vzipType;
	 *		设备ip ipAddress;
	 *		设备号 devNo;
	 *		序列号 serialNo;
	 *      文件类型 fileType(默认是file)
	 * @return rHashMap
	 *	key:返回码 retCode 0或1;
	 *		返回信息 retMsg;
	 * */
	public Map<String, Object> downloadFile(Map<String, Object> pHashMap) throws Exception {
		
		Map<String, Object> rHashMap=new HashMap<String, Object>();
		String ipAddress = (String)pHashMap.get("ip");
		String devNo = (String)pHashMap.get("devNo");
		String terminalNo = devNo;
		int packetLen = (Integer)pHashMap.get("packetLen");
		int vzipType = (Integer)pHashMap.get("vzipType");
		String remoteFilePort=pHashMap.get("remoteFilePort").toString();
		/*文件路径*/
		String viewpath = pHashMap.get("viewpath").toString();
		String fileName = pHashMap.get("fileName").toString();
		//源文件名  用于解压缩
		String versionFilePath = pHashMap.get("versionFilePath").toString();
		String remotePort=pHashMap.get("remotePort").toString();
		String resumeLocalFile = pHashMap.get("localFile") == null ? null : pHashMap.get("localFile").toString();
		String remoteFile = getRemoteFile(viewpath,fileName);
		
		boolean flag = false; //文件是否压缩以及是否压缩成功标志
		String transferFile = remoteFile;//传输设备端的远程文件
		String compressfile = ""; //压缩后的文件
		if (pHashMap.containsKey("compressflag") && Integer.valueOf(pHashMap.get("compressflag").toString())!=0){
			log.info("文件下载前，需要进行压缩");
			compressfile = this.compressFile(ipAddress,devNo,remoteFile,remotePort,packetLen,vzipType);
			if(compressfile != null) {
				flag = true;
				transferFile = compressfile;
			}
		} else {
			log.info("文件不需要进行压缩传输");
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
			rHashMap.put("retMsg", "创建本地文件失败");
			return rHashMap;
		}
		RemoteTrace remoteTrace = null;
		RemoteFileDownInfo downInfo = null;
		//添加文件正在下载记录
		if(resumeLocalFile == null) {
			remoteTrace = RemoteControlDAO.saveRemoteTrace("200099",pHashMap.get("user").toString(), devNo, "正在下载",  SystemLanguage.getControlDownloadFile() + "|" + remoteFile);
			if(remoteTrace == null) {
				rHashMap.put("retCode", 0);
				rHashMap.put("retMsg", "创建文件下载记录失败");
				return rHashMap;
			}
			
			downInfo =  RemoteControlDAO.saveFileDownInfo(remoteTrace.getLogicId(),remoteFile,locfilename,compressfile,0.00);
		} else { //续传
			String logicId = pHashMap.get("logicId").toString();
			remoteTrace = new RemoteTrace();
			remoteTrace.setLogicId(logicId);
			downInfo = RemoteControlDAO.getFileDownInfoByRemoteLogicId(logicId);
		}
		
		if(downInfo != null) {
			downInfo.setUserNo(pHashMap.get("user").toString());
		}
		log.info("本地文件【"+locfilename+"】,传输远程文件【"+transferFile+"】,设备号【"+terminalNo+"】");
		//开始下载设备端文件
		NDC.push(ipAddress) ;
		String reStr = ResumeFileUtil.resumeFileTrans(ipAddress,remoteFilePort,locfilename,transferFile,ipAddress,terminalNo,0,20,20,packetLen,vzipType,downInfo);
		NDC.pop() ;
		
		/*获取取文件*/			
		if(!reStr.equals("0000")) {
			String status = SystemLanguage.getMainFailed();
			if("超时".equals(reStr)) {
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
	 * 下载多个客户端文件到操作员端
	 * @author limengrd
	 * @since 2016.09.07
	 * @param pHashMap 
	 *	key:
	 *		Remote文件监听端口 remoteFilePort
	 *		当前用户 user;
	 *		每包传输大小 packetLen;
	 *		压缩方式 vzipType;
	 *		设备ip ipAddress;
	 *		设备号 devNo;
	 *      路径文件 pathType logPath&file#logPath&file
	 * @return rHashMap
	 *	key:返回码 retCode 0或1;
	 *		返回信息 retMsg;
	 * */
	public Map<String, Object> downloadClientFile(Map<String, Object> pHashMap)throws Exception {
		Map<String, Object> rHashMap = new HashMap<String, Object>();
		String devs = StringUtil.parseString(pHashMap.get("devs")); // 设备号
		String projects = StringUtil.parseString(pHashMap.get("projects")); // 提取内容
		String username = StringUtil.parseString(pHashMap.get("username")); // 用户名
		
		String remoteFilePort = StringUtil.parseString(pHashMap.get("remoteFilePort"));
		String remotePort = StringUtil.parseString(pHashMap.get("remotePort"));
		String[] devNos = devs.split("\\|");
		String[] project = projects.split("\\|");
		for (int i = 0; i < devNos.length; i++) {
			String localFile = StringUtil.parseString(pHashMap.get("localFile")); // 本地文件
			Map<String, Object> retMap = new HashMap<String, Object>();
			for (int j = 0; j < project.length; j++) {
				String logType = project[j];
				// 根据提取内容查询设备端日志信息
				List<AtmvLogPathInfo> list = AtmvLogDAO.getAtmcLogPathInfo(logType);
				for (AtmvLogPathInfo info : list) {
					String fileType = info.getFileType().equals("1") ? "folder" : "file";
					String logPath = info.getLogPath();
					if (!(logPath.endsWith("\\") || logPath.endsWith("/")) && "folder".equals(fileType)) {
						//文件夹要以 /或\ 结尾
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

			String compressfile = ""; // 压缩文件
			boolean flag = false; // 压缩成功标识
			if (pHashMap.containsKey("compressflag") && Integer.valueOf(StringUtil.parseString(pHashMap.get("compressflag"))) != 0) {
				log.info("文件下载前，需要进行压缩");
				compressfile = this.compressFile(ip, devNo, remotePort, packetLen, vzipType, retMap);
				if (compressfile != null) {
					flag = true;
				}
			} else {
				log.info("文件不需要进行压缩传输");
			}
			// 确保本地文件存在
			FileUtil.createDirectory(localFile);
			// 下载到本地路径
			localFile = localFile + System.getProperty("file.separator")
					+ devNo + "_" + CalendarUtil.getSysTimeYMDHMS1() + ".zip";
			if (flag && FileUtil.getRvcFile(ip, remoteFilePort, localFile, compressfile, ip, devNo, 0, 20, MAX_TIMEOUT, packetLen, vzipType).equals("0000")) {
				rHashMap.put("retCode", 1);
				rHashMap.put("retMsg", SystemLanguage.getSrcOperateSuccess());
				RemoteControlDAO.saveRemoteTrace("200018", username, devNo, "下载成功", SystemLanguage.getSrcOperateSuccess());
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
	 * 创建本地文件路径
	 * @author cqluo
	 * @param versionFilePath 本地文件路径
	 * @param fileName 文件名
	 * @param flag 是否压缩远程文件
	 * @return
	 */
	private String createLocalFile(String versionFilePath, String fileName,Boolean flag) {
		
		try {
			/*如果文件目录不存在则生成*/
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
			//如果远程文件已经压缩，则把文件的后缀名改为zip文件
			fileProperty = flag ? ".zip" : fileProperty;
			String tempFile =UUID.randomUUID().toString()+fileProperty;
			String locfilename=dirPath + System.getProperty("file.separator")+tempFile;
			File locFile = new File(locfilename);
			locFile.createNewFile(); //创建本地文件
			log.info("本地文件["+locfilename+"]");
			return locfilename;
		} catch (Exception e) {
			log.info("下载文件时创建本地文件失败:",e);
		}
		return null;
	}

	/**
	 * 下载文件前，先压缩文件
	 * @author cqluo
	 * @param ipAddress 设备IP地址
	 * @param devNo 设备号
	 * @param remoteFile 要压缩的文件
	 * @param remotePort 设备端文件下载端口
	 * @param packetLen 每次发包长度
	 * @param vzipType 压缩方式
	 * @return 压缩失败时返回Null，成功时返回压缩后的文件
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
			log.info("发送压缩报文失败!");
		} else {
			log.info("发送压缩报文成功!") ;
			String retCode = XmlUtil.getValue(retBody.toString(),"//root/retcode","value");
			String destfile  = XmlUtil.getValue(retBody.toString(), "//root/destfile", "pathname");
			if(retCode == null || destfile == null){
				log.info("retCode或destfile为空!");
				return null;
			}else{
				if(retCode.equals("000000")||retCode.equals("RMT000")) {
					log.info("压缩文件成功!") ;
					return destfile;
				} else if(retCode.equals("RMT401")){
					log.info("action非法") ;
				} else if(retCode.equals("RMT402")){
					log.info("压缩类型非法") ;
				} else if(retCode.equals("RMT403")){
					log.info("至少有一个文件不存在") ;
				} else if(retCode.equals("RMT404")){
					log.info("磁盘空间不足");
				} else if(retCode.equals("RMT405")){
					log.info("不支持超大源文件");
				}else{
					log.info("未知原因") ;
				}
			}
		}
		return null;
	}
	
	/**
	 * 下载文件前，先压缩文件
	 * @author cqluo
	 * @param ipAddress 设备IP地址
	 * @param devNo 设备号
	 * @param remotePort 设备端文件下载端口
	 * @param packetLen 每次发包长度
	 * @param vzipType 压缩方式
	 * @param params 
	 *           key: 文件类型
	 *           value:文件路径
	 * @return 压缩失败时返回Null，成功时返回压缩后的文件
	 */
	private String compressFile(String ipAddress, String devNo, String remotePort, int packetLen, int vzipType,Map<String, Object> params) {
		
		String msgId = UUID.randomUUID().toString() ;
		String  sendBody = getFilePackXml("200041", msgId, ipAddress, devNo, params) ;
		//取得超时时间
		int soTime=SocketUtil.getsoTime("200041");
		StringBuffer retBody=new StringBuffer();
		NDC.push(ipAddress) ;
		boolean statusFlag = MessageEncoded.sendCmdToRvcMutil(sendBody, ipAddress,remotePort , retBody, 20,soTime,packetLen,vzipType, devNo) ;
		NDC.pop();
		if (!statusFlag) {
			log.info("发送压缩报文失败!");
		} else {
			log.info("发送压缩报文成功!") ;
			String retCode = XmlUtil.getValue(retBody.toString(),"//root/retcode","value");
			String destfile  = XmlUtil.getValue(retBody.toString(), "//root/destfile", "pathname");
			if(retCode == null || destfile == null){
				log.info("retCode或destfile为空!");
				return null;
			}else{
				if(retCode.equals("000000")||retCode.equals("RMT000")) {
					log.info("压缩文件成功!") ;
					return destfile;
				} else if(retCode.equals("RMT401")){
					log.info("action非法") ;
				} else if(retCode.equals("RMT402")){
					log.info("压缩类型非法") ;
				} else if(retCode.equals("RMT403")){
					log.info("至少有一个文件不存在") ;
				} else if(retCode.equals("RMT404")){
					log.info("磁盘空间不足");
				} else if(retCode.equals("RMT405")){
					log.info("不支持超大源文件");
				}else{
					log.info("未知原因") ;
				}
			}
		}
		return null;
	}
	
	
	
	/**
	 * 远程文件压缩报文信息 add by yyhe 2012-08-17
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
		
		log.info("=========V端发送的压缩指令报文======：" + inputXML.toString()) ;
		return inputXML.toString() ;
	} 
	
	/**
	 * 多个远程文件压缩报文信息 add by limengrd 2016-09-07
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
		
		log.info("=========V端发送的压缩指令报文======：" + inputXML.toString()) ;
		return inputXML.toString() ;
	}
	
	/**
	 * 上传文件到ATM设备
     * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 *  key:设备表 devInfo;
	 *  	文件路径 viewpath;
	 *  	上传文件名 uploadedFileName;
	 *  	上传方式 uploadType
	 *  	系统IP地址 localIp
	 *  	系统文件监听端口 localFilePort
	 *  	Remote业务监听端口 remotePort;
	 *  	当前用户 user;
	 *      每包传输大小 packetLen;
	 *      压缩方式 vzipType;
	 *      设备ip ipAddress;
	 *      设备号 devNo;
	 *      序列号 serialNo;
	 * @return rHashMap
	 *  key:返回码 retCode 0或1;
	 *  	返回信息 retMsg;
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
		
		/*文件路径*/
		String viewpath=pHashMap.get("viewpath").toString();
		
		String uploadType=pHashMap.get("uploadType").toString();
		String uploadedFileName=pHashMap.get("uploadedFileName").toString();
		
		
		String cmdDate = CalendarUtil.getSysTimeYMD();	//RVS服务器规格日期
		String cmdTime = CalendarUtil.getSysTimeHMS();	//RVS服务器规格时间
		
		String actionTime=cmdDate+cmdTime;		
		String msgId=UUID.randomUUID().toString();
		StringBuffer inputXML=new StringBuffer();
		uploadType=(uploadType==null||uploadType.equals(""))?"0":uploadType;
		
		String serverIp=pHashMap.get("localIp").toString();//运行管理系统IP地址
		String serverFilePort=pHashMap.get("localFilePort").toString();//运行管理系统文件监听端口
		
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
		/*调用与远程版本服务器通讯接口*/											
		//boolean cmdFlag=SocketUtil.sendCmdToRvcMutil(inputXML.toString(),ipAddress,remotePort,retBody,20,MAX_TIMEOUT,packetLen,vzipType);
		//增加参数设备号，通过消息转发，获取设备的加密密钥，并进行转发    2011-8-22  mod by cy
		NDC.push(ipAddress) ;
		boolean cmdFlag=MessageEncoded.sendCmdToRvcMutil(inputXML.toString(),ipAddress,remotePort,retBody,20,MAX_TIMEOUT,packetLen,vzipType, terminalNo);
		NDC.pop() ;
		
		log.info("cmdFlag="+cmdFlag);
		log.info("retBody="+retBody.toString());
		/*无论上传成功于否删除运行管理上的临时文件*/
		
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
//				logBean.saveOpLog(pHashMap.get("account").toString(), "0", "上传文件:"+localFile+"到设备:"+devNo+"路径:"+viewpath);
				//return new ModelAndView("control/dev_document_browser_result").addObject("message","上传文件成功！");
				rHashMap.put("retCode", 1);
				rHashMap.put("retMsg", SystemLanguage.getSrcUploadFilesuccess());   
//				rHashMap.put("retMsg", "上传文件:"+uploadedFileName+"到设备:"+devNo+"路径:"+viewpath);
				RemoteControlDAO.saveRemoteTrace("200005",pHashMap.get("user").toString(), devNo, SystemLanguage.getControlRemoteControlResultSuccess(), SystemLanguage.getControlUploadFile() + "|" + viewpath);
			}
			else
			{
				//return new ModelAndView("control/dev_document_browser_result").addObject("message","上传文件失败！");
				RemoteControlDAO.saveRemoteTrace("200005",pHashMap.get("user").toString(), devNo, SystemLanguage.getMainFailed(), SystemLanguage.getControlUploadFile() + "|" + viewpath);
				rHashMap.put("retCode", 0);
				rHashMap.put("retMsg", SystemLanguage.getSrcUploadFileFai());
//				rHashMap.put("retMsg", "上传文件失败！");
			}
		}
		else
		{
			//return new ModelAndView("control/dev_document_browser_result").addObject("message","上传文件失败！"); 
			RemoteControlDAO.saveRemoteTrace("200005",pHashMap.get("user").toString(), devNo, SystemLanguage.getMainFailed(), SystemLanguage.getControlUploadFile() + "|" + viewpath);
			rHashMap.put("retCode", 0);
			rHashMap.put("retMsg", SystemLanguage.getSrcUploadFileFai());   
//			rHashMap.put("retMsg", "上传文件失败！");
		}
		return rHashMap;
	}
	/**
	 * 删除客户端上操作员制定的文件
	 * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 *  key:设备表 devInfo;
	 *  	文件路径 remoteFile;
	 *  	Remote业务监听端口 remotePort;
	 *  	当前用户 user;
	 *      每包传输大小 packetLen;
	 *      压缩方式 vzipType;
	 *      设备ip ipAddress;
	 *      设备号 devNo;
	 *      序列号 serialNo;
	 * @return rHashMap
	 *  key:返回码 retCode 0或1;
	 *  	返回信息 retMsg;
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
		
		/*文件路径*/
		String remoteFile=pHashMap.get("remoteFile").toString();
		log.info("remoteFile="+remoteFile);
		String cmdDate = CalendarUtil.getSysTimeYMD();	//RVS服务器规格日期
		String cmdTime = CalendarUtil.getSysTimeHMS();	//RVS服务器规格时间
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
		
		/*调用与远程版本服务器通讯接口*/											
		//boolean cmdFlag=SocketUtil.sendCmdToRvcMutil(inputXML.toString(),ipAddress,remotePort,retBody,20,MAX_TIMEOUT,packetLen,vzipType);
		//增加参数设备号，通过消息转发，获取设备的加密密钥，并进行转发    2011-8-22  mod by cy
		NDC.push(ipAddress) ;
		boolean cmdFlag=MessageEncoded.sendCmdToRvcMutil(inputXML.toString(),ipAddress,remotePort,retBody,20,MAX_TIMEOUT,packetLen,vzipType, terminalNo);
		NDC.pop() ;
				
		if(!cmdFlag)
		{
//			return new ModelAndView("system/param_set_result").addObject("result","与远程设备通讯出错！");	 	
			rHashMap.put("retCode", "0");
			rHashMap.put("retMsg", SystemLanguage.getSrcCommRemFai());
//			rHashMap.put("retMsg", "与远程设备通讯出错！");
			return rHashMap;
		}	
		else
		{	
			String retCode = XmlUtil.getValue(retBody.toString(),"//root/retcode","value");
			
			if(retCode!=null&&retCode.equals("RMT000"))
			{
//				UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
//				logBean.saveOpLog(userSession.getAccount(), "0", "删除设备:"+devNo+"上的文件:"+remoteFile);
//				
//				return new ModelAndView("system/param_set_result").addObject("result","删除远程客户端文件成功！");	
				RemoteControlDAO.saveRemoteTrace("200006",pHashMap.get("user").toString(), devNo, SystemLanguage.getControlRemoteControlResultSuccess(), SystemLanguage.getControlDelFile() + "|" + remoteFile);
				rHashMap.put("retCode", "1");
				rHashMap.put("retMsg", SystemLanguage.getSrcDeleteRemoteSuc());
//				rHashMap.put("retMsg", "删除远程客户端文件成功！");
				return rHashMap;
			}
			else
			{
//				UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
//				logBean.saveOpLog(userSession.getAccount(), "1", "删除设备:"+devNo+"上的文件:"+remoteFile);
//				
//				return new ModelAndView("system/param_set_result").addObject("result","删除远程客户端文失败！");
				RemoteControlDAO.saveRemoteTrace("200006",pHashMap.get("user").toString(), devNo, SystemLanguage.getMainFailed(), SystemLanguage.getControlDelFile() + "|" + remoteFile);
				rHashMap.put("retCode", "0");
				rHashMap.put("retMsg", SystemLanguage.getSrcDeleteRemoteFai());
//				rHashMap.put("retMsg", "删除远程客户端文件失败！");
				return rHashMap;
			}
		}
	}
	
	/**
	 * 执行客户端上操作员制定的文件
	 * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 *  key:设备表 devInfo;
	 *  	文件路径 remoteFile;
	 *  	Remote业务监听端口 remotePort;
	 *  	当前用户 user;
	 *      每包传输大小 packetLen;
	 *      压缩方式 vzipType;
	 *      设备ip ipAddress;
	 *      设备号 devNo;
	 *      序列号 serialNo;
	 * @return rHashMap
	 *  key:返回码 retCode 0或1;
	 *  	返回信息 retMsg;
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
		
		/*文件路径*/
		String remoteFile=pHashMap.get("remoteFile").toString();
		
		String cmdDate = CalendarUtil.getSysTimeYMD();	//RVS服务器规格日期
		String cmdTime = CalendarUtil.getSysTimeHMS();	//RVS服务器规格时间
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
		
		/*调用与远程版本服务器通讯接口*/											
		//boolean cmdFlag=SocketUtil.sendCmdToRvcMutil(inputXML.toString(),ipAddress,remotePort,retBody,20,MAX_TIMEOUT,packetLen,vzipType);
		//增加参数设备号，通过消息转发，获取设备的加密密钥，并进行转发    2011-8-22  mod by cy
		NDC.push(ipAddress) ;
		boolean cmdFlag=MessageEncoded.sendCmdToRvcMutil(inputXML.toString(),ipAddress,remotePort,retBody,20,MAX_TIMEOUT,packetLen,vzipType, terminalNo);
		NDC.pop() ;

		if(!cmdFlag)
		{
//			return new ModelAndView("system/param_set_result").addObject("result","与远程设备通讯出错！");	
			RemoteControlDAO.saveRemoteTrace("200001",pHashMap.get("user").toString(), devNo, SystemLanguage.getMainFailed(), SystemLanguage.getControlExcuteFile() + "|" + remoteFile);
			rHashMap.put("retCode", "0");
			rHashMap.put("retMsg", SystemLanguage.getSrcCommRemFai());
//			rHashMap.put("retMsg", "与远程设备通讯出错！");
			return rHashMap;
		}	
		else
		{	
//			UserSession userSession = (UserSession)request.getSession().getAttribute("userSession");
//			logBean.saveOpLog(userSession.getAccount(), "0", "执行设备:"+devNo+"上的文件:"+remoteFile);
//			
//			return new ModelAndView("system/param_set_result").addObject("result","执行远程客户端文件成功！");
//			String reStr=FileUtil.getRvcFile(devNo, remoteFilePort, localFile, XmlUtil.getValue(retBody.toString(), "//root/actioninfo", "filename"), "", "",1,20,SocketUtil.getsoTime("200001"),packetLen,vzipType);
//			if(!reStr.equals("0000"))
//			{
//				RemoteControlDAO.saveRemoteTrace("200001",pHashMap.get("user").toString(), devNo, "失败", "执行文件:"+remoteFile);
//				rHashMap.put("retCode", "0");
////				rHashMap.put("retMsg", "remotecontrol.error-communication");
//				rHashMap.put("retMsg", "取执行返回文件出错！");
//				return rHashMap;
//			}
			/*解析报文*/
			String retCode = XmlUtil.getValue(retBody.toString(),"//root/retcode","value");
			
			if(retCode!=null&&retCode.equals("RMT000"))
			{
				RemoteControlDAO.saveRemoteTrace("200001",pHashMap.get("user").toString(), devNo, SystemLanguage.getControlRemoteControlResultSuccess(), SystemLanguage.getControlExcuteFile() + "|" + remoteFile);
				rHashMap.put("retCode", "1");
				rHashMap.put("retMsg", SystemLanguage.getSrcImpRemFileSuc());
	//			rHashMap.put("retMsg", "执行远程客户端文件成功！");}
			}else{
				RemoteControlDAO.saveRemoteTrace("200001",pHashMap.get("user").toString(), devNo, SystemLanguage.getMainFailed(), SystemLanguage.getControlExcuteFile() + "|" + remoteFile);
				rHashMap.put("retCode", "0");
				rHashMap.put("retMsg", SystemLanguage.getSrcImpRemFilefailed());
//				rHashMap.put("retMsg", "与远程设备通讯出错！");
				return rHashMap;
			}
			return rHashMap;
		}
	}

	
	/**
	 * 执行执行命令
	 * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 *  key:设备号数组 devInfo(devno,ip,packetLen,vzipType);
	 *  	命令 cmdId
	 *  	是否需要获取文件标志 fileFlag
	 *  	Remote业务监听端口 remotePort;
	 *  	Remote文件监听端口 remoteFilePort;
	 *  	v端文件存放路径 dirPath
	 *  	日志日期 logDate
	 *  	银行标志 bankVersion
	 *  	当前用户 user;
	 *      每包传输大小 packetLen;
	 *      压缩方式 vzipType;
	 * @return rHashMap
	 *  key:返回码 retCode 0或1;
	 *  	返回信息 retMsg;
	 *  	结果集 resultList
	 * */
	public Map<String, Object> remoteControl(Map<String, Object> pHashMap) throws Exception
	{
		
		String cmdId = pHashMap.get("cmdId").toString();
		String bankVersion = (String)pHashMap.get("bankVersion");
		//浙江中行新增部分接口，为了不影响其他行，原来的代码不考虑修改，新增报文处理对象
		if(cmdId.equals("200032"))//密码修改administrator密码
		{
			ChangePwdControl control = new ChangePwdControl();
			return control.remoteControl(pHashMap);
		}
		else if(cmdId.equals("200033"))//修改注册表权限
		{
			OsRegeditControl control = new OsRegeditControl();
			return control.remoteControl(pHashMap);
		}
		else if(cmdId.equals("200034"))//修改usb设备权限
		{
			OsUsbControl control = new OsUsbControl();
			return control.remoteControl(pHashMap);
		}
		//平安银行增加同步卡表接口
		if(cmdId.equals("200040")&& BANK_PA.equalsIgnoreCase(bankVersion)){
			SyncCardControl control = new SyncCardControl() ;
			return control.remoteControl(pHashMap) ;
		}
		//兴业银行设备参数配置后通知c端去p端更新参数
		if(cmdId.equals("200040")&& BANK_XY.equalsIgnoreCase(bankVersion)){
			SyncParamControl control = new SyncParamControl() ;
			return control.remoteControl(pHashMap) ;
		}
		
		if(cmdId.equals("200003") && BANK_XY.equalsIgnoreCase(bankVersion)) { //兴业银行提取多种日志
			return remoteControlCIB(pHashMap);
		}
		
		//ZIJINMOD 徐全发 2013-12-23 标准版增加开启/关闭现金缓存、开启/关闭冠字号缓存，进行远程控制处理
		if(cmdId.equals("2000431") || cmdId.equals("2000432") || cmdId.equals("2000433") || cmdId.equals("2000434")) {
			BufferMemoryControl control = new BufferMemoryControl();
			return control.remoteControl(pHashMap) ;
		}
		
		//ZIJINMOD 徐全发 2013-12-23 开启/关闭存款冠字号凭条打印、开启/关闭取款冠字号凭条打印
		if(cmdId.equals("20003060") || cmdId.equals("20003061") || cmdId.equals("20003070") || cmdId.equals("20003071")) {
			NtsReceiptPrintControl control = new NtsReceiptPrintControl();
			return control.remoteControl(pHashMap) ;
		}
		
		HashMap<String, Object> rHashMap=new HashMap<String, Object>();
		/*设备信息*/			
//		String[] devInfo= request.getParameter("devList").toString().split("\\-");		
//		String cmdId = request.getParameter("cmdId");		
//		String fileFlag = request.getParameter("fileFlag");//是否要获取文件标志		
		
		String[] devInfo= (String[]) pHashMap.get("devInfo");
		
		String fileFlag = pHashMap.get("fileFlag").toString();//是否要获取文件标志	
		
		int restarttype= -1;
		if(pHashMap.get("restarttype")!=null&&!pHashMap.get("restarttype").equals(""))
		{
			restarttype = Integer.valueOf((String)pHashMap.get("restarttype"));//0:立即重启，1：空闲重启， -1:默认
		}
		int shutdowntype= -1;
		if(pHashMap.get("shutdowntype")!=null&&!pHashMap.get("shutdowntype").equals(""))
		{
			shutdowntype = Integer.valueOf((String)pHashMap.get("shutdowntype"));//0:立即关机，1：空闲关机， -1:默认
		}
		
		String screen = "-1";
		int screenflag= -1;
		if(pHashMap.get("screenflag")!=null&&!pHashMap.get("screenflag").equals(""))
		{
			screenflag = Integer.valueOf((String)pHashMap.get("screenflag"));//0:主屏，1：副屏， -1:默认
			if(screenflag == 0){
				screen = "主屏";
			}
			if(screenflag == 1){
				screen = "副屏";
			}
		}
		
		String[] tempRemote=null;
		
		List resultList = new ArrayList();//设备信息列表			
		boolean result ;
		boolean result2 = false;//状态检测c，p端通讯情况 
		
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
		String retcode2=null;//状态检测c，p端通讯情况
		String logDate =null;
		//取得超时时间
		int soTime=SocketUtil.getsoTime(cmdId);
		
		/*如果操作需要提取文件*/
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
				
				if(cmdId.equals("200003")) //ZIJINMOD 徐全发 2014-01-24 杭州银行提取电子日志路径修改，考虑到tcr日志路径可能不同，在设备基本信息表中添加了dev_log_path保存tcr日志路径，如果不存在该字段或者该字段内容为空，则使用默认的路径
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
//			// || cmdId.equals("200014") 重起暂封掉
//			if(ncr_os2)//ncr os2机器单独发逻辑开,关,重起 命令
//			{
//				String ncrcmd = this.genString(cmdId,tempRemote[0],1);	
//				result = SocketUtil.sendCmdToOs2(ncrcmd, tempRemote[1], remoteOs2Port, retBody,20,soTime);
//			
//			}
//			else if(dbd_os2)//dbd os2机器单独发逻辑开,关,重起 命令
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
			    //增加参数设备号，通过消息转发，获取设备的加密密钥，并进行转发    2011-8-22  mod by cy
				NDC.push(tempRemote[1]) ;
				result = MessageEncoded.sendCmdToRvcMutil(this.genXML(cmdId,msgId,tempRemote[0],tempRemote[1],localFile,remoteFile, restarttype, screenflag,shutdowntype), tempRemote[1], remotePort, retBody,20,soTime,packetLen,vzipType, tempRemote[0]);
				NDC.pop() ;
			/*获取文件*/
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
			
			/*设备号、IP、操作结果、结果解释、是否有结果文件、文件名称、连接*/
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
					RemoteControlDAO.saveRemoteTrace(cmdId,pHashMap.get("user").toString(),tempRemote[0],(result?SystemLanguage.getControlRemoteControlResultSuccess():SystemLanguage.getMainFailed()),convertCommandId(cmdId) + "|" + "批量操作");
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
						RemoteControlDAO.saveRemoteTrace(cmdId,pHashMap.get("user").toString(),tempRemote[0],SystemLanguage.getMainFailed(),convertCommandId(cmdId) + "|" + "批量操作");
					}else{
						RemoteControlDAO.saveRemoteTrace(cmdId,pHashMap.get("user").toString(),tempRemote[0],SystemLanguage.getMainFailed(),convertCommandId(cmdId));
					}
				}
				rHashMap.put("retCode", 0);
				rHashMap.put("retMsg", SystemLanguage.getSrcCommRemFai());
//				rHashMap.put("retMsg", "与远程设备通讯出错！");
			}
	}
	return rHashMap;
}
	
	/**
	 * 执行执行命令
	 * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 *  key:设备号数组 devInfo(devno,ip,packetLen,vzipType);
	 *  	命令 cmdId
	 *  	是否需要获取文件标志 fileFlag
	 *  	Remote业务监听端口 remotePort;
	 *  	Remote文件监听端口 remoteFilePort;
	 *  	v端文件存放路径 dirPath
	 *  	日志日期 logDate
	 *  	银行标志 bankVersion
	 *  	当前用户 user;
	 *      每包传输大小 packetLen;
	 *      压缩方式 vzipType;
	 * @return rHashMap
	 *  key:返回码 retCode 0或1;
	 *  	返回信息 retMsg;
	 *  	结果集 resultList
	 * */
	public Map<String, Object> remoteControlCIB(Map<String, Object> pHashMap) throws Exception{
		
		Map<String, Object> rHashMap=new HashMap<String, Object>();
		
		String cmdId = pHashMap.get("cmdId").toString();
		String[] devInfo= (String[]) pHashMap.get("devInfo");
		String fileFlag = pHashMap.get("fileFlag").toString();//是否要获取文件标志	
		String[] tempRemote=null;
		List resultList = new ArrayList();//设备信息列表			
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
		//取得超时时间
		int soTime=SocketUtil.getsoTime(cmdId);
		
		List<String> remoteFiles = (List<String>) pHashMap.get("remoteFiles");
		
		/*如果操作需要提取文件*/
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
				log.info("文件路径："+remoteFile);
				log.info("文件名："+fileName);
				log.info("文件属性："+fileProp);
				
				logDate = pHashMap.get("logDate").toString();
				
				String curDate = CalendarUtil.getDate();
				
				if(!fileType.equals("2") || logDate.equals(curDate)) {
					//下载文件前发送远程压缩指令
					boolean flag = true ; //压缩文件成功与否标志
					boolean operateFlag = false ;//操作成功与否标志
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
					    log.info("发送压缩报文失败!") ;
					    System.out.println("发送压缩报文失败");
					    flag = false ;
					} else {
						log.info("发送压缩报文成功!") ;
						System.out.println("发送压缩报文成功!");
						retCode = XmlUtil.getValue(retBody.toString(),"//root/retcode","value");
						destfile  = XmlUtil.getValue(retBody.toString(), "//root/destfile", "pathname");
						if(retCode == null || destfile == null){
							log.info("retCode或destfile为空!") ;
							System.out.println("retCode或destfile为空!");
							flag = false ;
						}else{
							if(retCode.equals("000000")||retCode.equals("RMT000"))
							{
								log.info("压缩文件成功!") ;
								System.out.println("压缩文件成功!");
								rHashMap.put("retCode", 1);
								rHashMap.put("retMsg", "压缩文件成功!");
								flag = true ;
							}else if(retCode.equals("RMT401")){
								log.info("action非法") ;
								System.out.println("action非法");
							    rHashMap.put("retCode", 0);
								rHashMap.put("retMsg", "action非法!");
								flag = false ;
							}else if(retCode.equals("RMT402")){
								log.info("压缩类型非法") ;
								System.out.println("压缩类型非法");
								rHashMap.put("retCode", 0);
								rHashMap.put("retMsg", "压缩类型非法!");
								flag = false ;
							}else if(retCode.equals("RMT403")){
								log.info("至少有一个文件不存在") ;
								System.out.println("至少有一个文件不存在");
								rHashMap.put("retCode", 0);
								rHashMap.put("retMsg", "至少有一个文件不存在!");
								flag = false ;
							}else if(retCode.equals("RMT404")){
								log.info("磁盘空间不足") ;
								System.out.println("磁盘空间不足");
							    rHashMap.put("retCode", 0);
							    rHashMap.put("retMsg", "磁盘空间不足!");
							    flag = false ;
							}else if(retCode.equals("RMT405")){
								log.info("不支持超大源文件") ;
								System.out.println("不支持超大源文件");
							    rHashMap.put("retCode", 0);
							    rHashMap.put("retMsg", "不支持超大源文件!");
							    flag = false ;
							}else{
								log.info("未知原因") ;
								System.out.println("未知原因");
							    rHashMap.put("retCode", 0);
							    rHashMap.put("retMsg", "未知原因!");
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
				
				/*设备号、IP、操作结果、结果解释、是否有结果文件、文件名称、连接*/
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
	 * 函数名： createOrUpdateAtmFunction
	 * 函数功能：创建或者更新ATMC的功能信息
	 * 处理过程：(描述这个方法的处理逻辑)
	 * 输入参数描述： pHashMap
	 * 	key:设备号 devNo;
	 *  	设备IP ip
	 *  	参数类型 paramtype
	 *  	参数类型值 paramtypeValue;
	 *  	Remote业务监听端口 remotePort;
	 *  	当前用户 user;
	 *  	通讯每包传输大小 packetLen
	 *      通讯压缩方式 vzipType
	 *      功能类型 functionType
	 * 输出参数描述： rHashMap
	 * 	key:返回码 retCode 0或1;
	 *  	返回信息 retMsg;
	 *  	参数 result
	 * 异常处理描述： 
	 * 创建人：  lyk 2009.04.01
	 * 修改信息：zhangdd、xsxu 2017-05-03 V1.4.3
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

		//	zjmod by zhangdd 2017-04-13 V1.4.3 C端新架构功能定义接口变动 start
		String paramtype = pHashMap.get("paramtype").toString();
		//功能定义接口类型：1-数字（01|查询），2-字母（query|查询）。
		String functionType = pHashMap.get("functionType")==null||"".equals(pHashMap.get("functionType"))?"1":pHashMap.get("functionType").toString();
		//  zjmod by zhangdd 2017-04-13 V1.4.3 C端新架构功能定义接口变动 end
		try
		{
			//xsxu 2017-05-03 v1.4.3 C端新架构功能定义接口变动 start
			if("2".equals(functionType)){//xsxu 2017-05-03 v1.4.3 functionType为2是c端新架构，atmFunction为以逗号分隔的功能英文名字符串，若atmFunction为空，默认为空字符串
				if(atmFunction==null){
					atmFunction = "";
				}
			}else{//xsxu 2017-05-03 v1.4.3 c端旧架构的atmFunction为128位的01字符串
				if(atmFunction==null){
					atmFunction = "00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
				}else {
					atmFunction =atmFunction+"00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000";
					atmFunction = atmFunction.substring(0,128);
				}
			}
			//xsxu 2017-05-03 v1.4.3 C端新架构功能定义接口变动 end
//			if(atmFunction==null)
//			{
//				return mv.addObject("result", "设备"+devNo+"功能设置失败，创建功能列表不成功！");
//			}
			/*向设备发送定义包*/
			StringBuffer xml = new StringBuffer();
			
			xml.append("<?xml version=\"1.0\"?>");
			xml.append("<root>");
			xml.append("<cmdid value=\"").append("200030").append("\"/>");
			xml.append("<msgid value=\"").append(UUID.randomUUID().toString()).append("\"/>");
			xml.append("<cmddatetime date=\"").append(CalendarUtil.getDate()).append("\" time=\"").append(CalendarUtil.getTime()).append("\"/>");
			xml.append("<operatorinfo userid=\"zjft\"/>");
			//	zjmod by zhangdd 2017-04-13 C端新架构功能定义接口变动
			xml.append("<functioninfo ").append(paramtype).append(" =\"").append(atmFunction).append("\"/>");		
			xml.append("<actioninfo time=\"").append(CalendarUtil.getDate()).append(" ").append(CalendarUtil.getTime()).append("\"/>");
			xml.append("<managerinfo ipaddress=\"").append(ip).append("\"/>");
			xml.append("<remote ipaddress=\"").append(ip).append("\" termno=\"").append(devNo).append("\" serialno=\"").append(devNo).append("\"/>");
			xml.append("</root>");	
			
			StringBuffer retBody =new StringBuffer();
			
//			取得超时时间
			int soTime=SocketUtil.getsoTime("200030");
			//boolean result = SocketUtil.sendCmdToRvcMutil(xml.toString(), ip, remotePort, retBody,20,soTime,packetLen,vzipType);
			//增加参数设备号，通过消息转发，获取设备的加密密钥，并进行转发    2011-8-22  mod by cy
			NDC.push(ip) ;
			boolean result = MessageEncoded.sendCmdToRvcMutil(xml.toString(), ip, remotePort, retBody,20,soTime,packetLen,vzipType, devNo);
			NDC.pop() ;
			if(!result)
			{
//				logBean.saveOpLog(userSession.getAccount(), "1", "设备"+devNo+"功能设置失败，与设备通信不成功！");
				RemoteControlDAO.saveRemoteTrace("200030",user,devNo,SystemLanguage.getMainFailed(), SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcFunctionFail());
				rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcFunctionFail());
				rHashMap.put("retCode", "0");
				return rHashMap;
			}
			/*保存数据*/
			if(XmlUtil.getValue(retBody.toString(), "//root/retcode", "value").equals("000000"))
			{
//				logBean.saveOpLog(userSession.getAccount(), "0", "设备"+devNo+"功能设置成功！"+XmlUtil.getValue(retBody.toString(), "//root/retcode", "remark"));
//				mv.addObject("result", "设备"+devNo+"功能设置成功！"+XmlUtil.getValue(retBody.toString(), "//root/retcode", "remark"));
				RemoteControlDAO.saveRemoteTrace("200030",user,devNo,SystemLanguage.getControlRemoteControlResultSuccess(),SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcFunctionSuccess());
				rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcFunctionSuccess());
				
				List<Object> aList = DbProxoolUtil.query("select t.dev_no from atmc_function_table t where t.dev_no='"+devNo+"'",0);
				if (aList==null||aList.size()==0){
					//zhangdd 2017-04-13 V1.4.3 去掉NumberUtil.binaryToHexString(atmFunction)处理的原因:atmc_function_table的trans_list由VARCHAR(32)变成VARCHAR(500)，长度足够存储，不用再进制转换。而且如果是c端新架构的话，存储的不是01二进制而是以逗号分隔的英文。现在统一直接存储，在取这个字段信息时候也统一处理，不用再判断如果是老架构的话再进行十六进制转二进制的操作
					String sql="insert into atmc_function_table(dev_no,trans_list) values('"+devNo+"','"+atmFunction+"')";
					DbProxoolUtil.insert(sql);
				}else{
					//zhangdd 2017-04-13 V1.4.3 去掉NumberUtil.binaryToHexString(atmFunction)处理的原因:同上
					String sql="update atmc_function_table set trans_list='"+atmFunction+"' where dev_no='"+devNo+"'";
					DbProxoolUtil.update(sql);
				}
			}
			else
			{
//				logBean.saveOpLog(userSession.getAccount(), "1", "设备"+devNo+"功能设置失败！"+XmlUtil.getValue(retBody.toString(), "//root/retcode", "remark"));
//				return mv.addObject("result", "设备"+devNo+"功能设置失败！"+XmlUtil.getValue(retBody.toString(), "//root/retcode", "remark"));
				RemoteControlDAO.saveRemoteTrace("200030",user,devNo,SystemLanguage.getMainFailed(),SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcFunctionFail2());
				rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcFunctionFail2());
				rHashMap.put("retCode", "0");
				
			}
		}
		catch(Exception e)
		{
			log.info("创建或更新ATMC功能信息失败，失败原因:"+e) ;
			//e.printStackTrace();
			rHashMap.put("retCode", "0");
			RemoteControlDAO.saveRemoteTrace("200030",user,devNo,SystemLanguage.getMainFailed(),SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcFunctionFail2() + "|" +e.getMessage());
			rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcFunctionFail2() + "|" +e.getMessage());
			
		}
		return rHashMap;
	}
	
	/**
	 * 创建或者更新ATMC的日志补打池信息
	 * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 *  key:设备号 devNo;
	 *  	设备IP ip
	 *  	Remote业务监听端口 remotePort;
	 *  	当前用户 user;
	 *  	通讯每包传输大小 packetLen
	 *      通讯压缩方式 vzipType
	 * @return rHashMap
	 *  key:返回码 retCode 0或1;
	 *  	返回信息 retMsg;
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
			/*向设备发送定义包*/
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
//			取得超时时间
			int soTime=SocketUtil.getsoTime("200030");			
			//boolean result = SocketUtil.sendCmdToRvcMutil(xml.toString(), ip, remotePort, retBody,20,soTime,packetLen,vzipType);
			//增加参数设备号，通过消息转发，获取设备的加密密钥，并进行转发    2011-8-22  mod by cy
			NDC.push(ip) ;
			boolean result = MessageEncoded.sendCmdToRvcMutil(xml.toString(), ip, remotePort, retBody,20,soTime,packetLen,vzipType, devNo);
			NDC.pop() ;
			
			if(!result)
			{
//				logBean.saveOpLog(userSession.getAccount(), "1", "设备"+devNo+"日志补打池缓存设置失败，与设备通信不成功！");
//				return mv.addObject("result", "设备"+devNo+"日志补打池缓存设置失败，与设备通信不成功！");
				RemoteControlDAO.saveRemoteTrace("200031",user,devNo,SystemLanguage.getMainFailed(),SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcLogCommFailed());
				rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcLogCommFailed());
			}
			/*保存数据*/
			
			
			if(XmlUtil.getValue(retBody.toString(), "//root/retcode", "value").equals("000000"))
			{
//				logBean.saveOpLog(userSession.getAccount(), "0", "设备"+devNo+"日志补打池缓存设置成功！"+XmlUtil.getValue(retBody.toString(), "//root/retcode", "remark"));
//				mv.addObject("retMsg", "设备"+devNo+"功能设置成功！"+XmlUtil.getValue(retBody.toString(), "//root/retcode", "remark"));
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
//				logBean.saveOpLog(userSession.getAccount(), "1", "设备"+devNo+"日志补打池缓存设置失败！"+XmlUtil.getValue(retBody.toString(), "//root/retcode", "remark"));
//				return mv.addObject("retMsg", "设备"+devNo+"日志补打池缓存设置失败！"+XmlUtil.getValue(retBody.toString(), "//root/retcode", "remark"));
				RemoteControlDAO.saveRemoteTrace("200031",user,devNo,SystemLanguage.getMainFailed(),SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcLogCacheFailed());
				rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcLogCacheFailed());
				
			}
		}
		catch(Exception e)
		{
			log.info("创建或者更新ATMC的日志补打池信息失败，失败原因:"+e) ;
			//e.printStackTrace();
//			mv.addObject("retMsg", "设备"+devNo+"功能设置异常,失败:"+e.getMessage());
			rHashMap.put("retCode", "0");
			RemoteControlDAO.saveRemoteTrace("200031",user,devNo,SystemLanguage.getMainFailed(),SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcLogCacheFailed() + "|" + e.getMessage());
			rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcLogCacheFailed() + "|" + e.getMessage());
		}
		return rHashMap;
	}
	
	/**  
	 * 函数名： getAtmParam
	 * 函数功能：获取设备的功能列表信息参数
	 * 处理过程：(描述这个方法的处理逻辑)
	 * 输入参数描述： pHashMap 
	 *  key:设备号 devNo;
	 *  	设备IP ip
	 *  	参数类型 paramtype
	 *  	参数类型值 paramtypeValue;
	 *  	Remote业务监听端口 remotePort;
	 *  	通讯每包传输大小 packetLen
	 *      通讯压缩方式 vzipType
	 * 输出参数描述： rHashMap
	 *  key:返回码 retCode 0或1;
	 *  	返回信息 retMsg;
	 *  	参数 atmFunction
	 * 异常处理描述： 
	 * 创建人：  lyk 2009.04.01
	 * 修改信息：zhangdd 2017-04-13 V1.4.3
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
			/*向设备发送定义包*/
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
//			取得超时时间
			int soTime=SocketUtil.getsoTime("200031");	
			//增加参数设备号，通过消息转发，获取设备的加密密钥，并进行转发    2011-8-22  mod by cy
			NDC.push(ip) ;
			if(!MessageEncoded.sendCmdToRvcMutil(xml.toString(), ip, remotePort, retBody,20,soTime,packetLen,vzipType, devNo)) {				
				NDC.pop() ;
				rHashMap.put("retMsg", SystemLanguage.getSrcOperateFail());
				return rHashMap;
			}
			NDC.pop();
			log.info("用于解析的报文内容:" + retBody.toString());
			String retParamValue = XmlUtil.getValue(retBody.toString(), "//root/functioninfo", paramtype);
	    //  zjmod by zhangdd 2017-04-13 V1.4.3 C端新架构功能定义接口变动 
			if(paramtype.equals("paramtype1")||paramtype.equals("paramtype9")) {
				log.info("atmFunctionlist="+retParamValue);
				rHashMap.put("atmFunctionlist", retParamValue);
			} else if(paramtype.equals("paramtype5")) {
				log.info("compositeCardStatus="+retParamValue);
				rHashMap.put("compositeCardStatus", retParamValue);
			} else {
				log.info("参数类型错误["+paramtype+"]");
				return null;
			}

			rHashMap.put("retMsg", SystemLanguage.getSrcOperateSuccess());
			rHashMap.put("retCode", "1");
			return rHashMap;
		} catch (Exception e) {
			log.error("获取设备功能列表信息参数失败，失败原因:",e) ;
			rHashMap.put("retMsg", SystemLanguage.getSrcOperateFail());
			return rHashMap;
		}
	}
	
	/**
	 * 将远程命令代码解析为中文含义
	 * 
	 * @param command
	 *            远程命令代码
	 * @return 命令代码中文含义
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
	 * 组织与远程设备的通讯报文
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
		if (cmdId!=null && cmdId.equals("200014") && restarttype!=-1){//重启需要确定立即还是应用重启
			inputXML.append("\" restarapptype=\"").append(restarttype);
		}
		if (cmdId!=null && cmdId.equals("200015") && restarttype!=-1){//关机需要确定立即还是应用关机
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
	 * 获取设备的钞箱信息
	 * 2011-8-2  add by cy
	 */
	public Map<String, Object> getCashboxInfo (Map<String, Object> pHashMap) throws Exception
	{
		Map<String,Object> rHashMap = new HashMap<String,Object>();
		rHashMap.put("retCode", "0");       //0 失败  1 成功
		
		String devNo = pHashMap.get("devNo").toString();
		String ip = pHashMap.get("ip").toString();
		int packetLen = Integer.valueOf(pHashMap.get("packetLen").toString());
		int vzipType = Integer.valueOf(pHashMap.get("vzipType").toString());
		String remotePort = pHashMap.get("remotePort").toString();
		
		List cashInfo = new ArrayList();
		try
		{

			StringBuffer retBody =new StringBuffer();
//			取得超时时间
			int soTime=SocketUtil.getsoTime("200030");			
			//boolean result = SocketUtil.sendCmdToRvcMutil(getCashInfoXml("200031",ip,devNo,"CXBS","CXLX"), ip, remotePort, retBody,20,soTime,packetLen,vzipType);
			//增加参数设备号，通过消息转发，获取设备的加密密钥，并进行转发    2011-8-22  mod by cy
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
			rHashMap.put("retCode", "1");       //0 失败  1 成功
			rHashMap.put("cashInfo", cashInfo);
		}
		catch(Exception e)
		{
			log.info("获取设备钞箱信息失败！");
			rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+"|"+SystemLanguage.getSrcCommFai());
			return rHashMap;
		}
		return rHashMap;
	}
	
	
	/*
	 * 保存设备的钞箱信息
	 * 2011-8-2  add by cy
	 */
	public Map<String, Object> setCashboxInfo(Map<String, Object> pHashMap) throws Exception
	{
		HashMap<String,Object> rHashMap = new HashMap<String,Object>();
		rHashMap.put("retCode", "0");       //0 失败  1 成功
		
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
			//SETCASHBOX_PAGE_CMDID=200130  此CMDID主要用于钞箱设置页面结果查询时与功能定义进行区分，实际报文中CMDID并未改变  2011-8-8  add by cy
			StringBuffer retBody =new StringBuffer();
//			取得超时时间
			int soTime=SocketUtil.getsoTime("200030");			
			//boolean result = SocketUtil.sendCmdToRvcMutil(getCashInfoXml("200030",ip,devNo,cashId,cashType), ip, remotePort, retBody,20,soTime,packetLen,vzipType);
			//增加参数设备号，通过消息转发，获取设备的加密密钥，并进行转发    2011-8-22  mod by cy
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

			rHashMap.put("retCode", "1");       //0 失败  1 成功
		}
		catch(Exception e)
		{
			log.info("获取设备钞箱信息失败！");
			rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+"|"+SystemLanguage.getSrcCommFai());
			RemoteControlDAO.saveRemoteTrace(SETCASHBOX_PAGE_CMDID,user,devNo,SystemLanguage.getMainFailed(),SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcCommFai());
			return rHashMap;
		}
		RemoteControlDAO.saveRemoteTrace(SETCASHBOX_PAGE_CMDID,user,devNo,SystemLanguage.getControlRemoteControlResultSuccess(),SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + SystemLanguage.getSrcSetCashboxInfo()+"|"+SystemLanguage.getControlRemoteControlResultSuccess());
		return rHashMap;
	}
	
	
	/**
	 * 更新昆仑银行复合卡开启关闭功能
	 * @author cy
	 * @since 2013-4-23
	 * @param pHashMap 
	 *  key:设备号 devNo;
	 *  	设备IP ip
	 *  	Remote业务监听端口 remotePort;
	 *  	当前用户 user;
	 *  	通讯每包传输大小 packetLen
	 *      通讯压缩方式 vzipType
	 * @return rHashMap
	 *  key:返回码 retCode 0或1;
	 *  	返回信息 retMsg;
	 *  	参数 result
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
		
		String atmFunctionMsg = atmFunction.equals("0")?SystemLanguage.getCompositeCardClose():SystemLanguage.getCompositeCardOpen();//0-关闭，1-开启
		try
		{
			/*向设备发送定义包*/
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
			
//			取得超时时间
			int soTime=SocketUtil.getsoTime("200030");
			//boolean result = SocketUtil.sendCmdToRvcMutil(xml.toString(), ip, remotePort, retBody,20,soTime,packetLen,vzipType);
			//增加参数设备号，通过消息转发，获取设备的加密密钥，并进行转发    2011-8-22  mod by cy
			NDC.push(ip) ;
			boolean result = MessageEncoded.sendCmdToRvcMutil(xml.toString(), ip, remotePort, retBody,20,soTime,packetLen,vzipType, devNo);
			NDC.pop() ;
			if(!result)
			{
				rHashMap.put("retCode", "0");
//				logBean.saveOpLog(userSession.getAccount(), "1", "设备"+devNo+"复合卡磁条功能开启失败,通讯失败！");
				RemoteControlDAO.saveRemoteTrace(SETCOMPOSITECARDSTATUS_PAGE_CMDID,user,devNo,SystemLanguage.getMainFailed(), SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + atmFunctionMsg+ "|" + SystemLanguage.getMainFailed()+ "|,|" + SystemLanguage.getSrcCommFai());
				rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + atmFunctionMsg+ "|" + SystemLanguage.getMainFailed()+ "|,|" + SystemLanguage.getSrcCommFai());
				return rHashMap;
			}
			/*保存数据*/
			if(XmlUtil.getValue(retBody.toString(), "//root/retcode", "value").equals("000000"))
			{
//				logBean.saveOpLog(userSession.getAccount(), "0", "设备"+devNo+"功能设置成功！"+XmlUtil.getValue(retBody.toString(), "//root/retcode", "remark"));
//				mv.addObject("result", "设备"+devNo+"功能设置成功！"+XmlUtil.getValue(retBody.toString(), "//root/retcode", "remark"));
				RemoteControlDAO.saveRemoteTrace(SETCOMPOSITECARDSTATUS_PAGE_CMDID,user,devNo,SystemLanguage.getControlRemoteControlResultSuccess(),SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + atmFunctionMsg+ "|,|" + SystemLanguage.getSrcOperateSuccess());
				rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + atmFunctionMsg+ "|,|" + SystemLanguage.getSrcOperateSuccess());
			}
			else
			{
				rHashMap.put("retCode", "0");
//				logBean.saveOpLog(userSession.getAccount(), "1", "设备"+devNo+"功能设置失败！"+XmlUtil.getValue(retBody.toString(), "//root/retcode", "remark"));
//				return mv.addObject("result", "设备"+devNo+"功能设置失败！"+XmlUtil.getValue(retBody.toString(), "//root/retcode", "remark"));
				RemoteControlDAO.saveRemoteTrace(SETCOMPOSITECARDSTATUS_PAGE_CMDID,user,devNo,SystemLanguage.getMainFailed(),SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + atmFunctionMsg+ "|" + SystemLanguage.getMainFailed());
				rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + atmFunctionMsg+ "|" + SystemLanguage.getMainFailed());
				
			}
		}
		catch(Exception e)
		{
			log.info("创建或更新ATMC功能信息失败，失败原因:"+e) ;
			//e.printStackTrace();
			rHashMap.put("retCode", "0");
			RemoteControlDAO.saveRemoteTrace(SETCOMPOSITECARDSTATUS_PAGE_CMDID,user,devNo,SystemLanguage.getMainFailed(),SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + atmFunctionMsg+ "|" + SystemLanguage.getMainFailed() + "|" +e.getMessage());
			rHashMap.put("retMsg", SystemLanguage.getControlAtmFunctionDetailDev()+"|"+devNo+ "|" + atmFunctionMsg+ "|" + SystemLanguage.getMainFailed() + "|" +e.getMessage());
			
		}
		return rHashMap;
	}
	
	
	
	/*
	 * 编辑钞箱设置报文信息
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

	//自动精查 2016-01-20 ZJADD zhangdd 
	public Map<String, Object> qryCashBoxSift(Map<String, Object> paraMap) {
		// TODO Auto-generated method stub
		HashMap<String, Object> rHashMap=new HashMap<String, Object>();
		HashMap<String, Object> addRmtTraceMap=new HashMap<String, Object>();
		String[] tempRemote=null;

		String[] devInfo = ((String[])paraMap.get("devInfo"));
		
		List resultList = new ArrayList();//返回的设备信息列表			
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
				
//				String cmdDate = CalendarUtil.getSysTimeYMD();	//RVS服务器规格日期
//				String cmdTime = CalendarUtil.getSysTimeHMS();	//RVS服务器规格时间
//				String actionTime=cmdDate+cmdTime;
				String msgId=UUID.randomUUID().toString();		
				
				addRmtTraceMap.put("cmdName", "200047");
				addRmtTraceMap.put("msgId", msgId);
				addRmtTraceMap.put("userId", userId);
				addRmtTraceMap.put("devNo", devNo);
				addRmtTraceMap.put("opContent", "自动精查");
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
				
				log.info("自动精查inputXML="+inputXML);
				StringBuffer retBody=new StringBuffer();		
				try{
				NDC.push(ipAddress) ;
				boolean cmdFlag=MessageEncoded.sendCmdToRvcMutil(inputXML.toString(),ipAddress,remotePort,retBody,20,MAX_TIMEOUT,packetLen,vzipType, devNo);
				NDC.pop() ;


				if(!cmdFlag)
				{
					addRmtTraceMap.put("result", "发送失败");
					RemoteControlDAO.saveRemoteTrace(addRmtTraceMap);
					result = false;
					reason = "与远程设备通讯出错!";
				}	
				else
				{	
					/*解析返回报文*/
					String retCode = XmlUtil.getValue(retBody.toString(),"//root/retcode","value");
					
					if(retCode!=null&&retCode.equals("RMT000"))
					{
						addRmtTraceMap.put("result", "成功");
						RemoteControlDAO.saveRemoteTrace(addRmtTraceMap);
						result = true;
						reason = "自动精查成功！";
					}else{
						addRmtTraceMap.put("result", "失败");
						RemoteControlDAO.saveRemoteTrace(addRmtTraceMap);
						result = false;
						reason = "自动精查返回失败！";
					}
				}
				
				/*0：设备号 1：IP 2 :结果 3:原因*/
				resultList.add(new Object[]{tempRemote[0],tempRemote[1],result,"0",reason});
				rHashMap.put("retCode", 1);
				rHashMap.put("resultList", resultList);
				
				}catch(Exception e){
					addRmtTraceMap.put("result", "发生异常");
					RemoteControlDAO.saveRemoteTrace(addRmtTraceMap);
					rHashMap.put("retCode", 0);
					rHashMap.put("retMsg", "自动精查发生异常！");
				}
		}
		return rHashMap;
	
	}
}

