package com.zjft.shepherd.common;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;

//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zjft.shepherd.business.control.MessageEncoded;
import com.zjft.shepherd.common.SocketUtil;


/**
 * �������ļ���ز���
 * @author hjtang
 * @since 2007.07.20
 */


public final class FileUtil 
{

	private static Log log=LogFactory.getLog(FileUtil.class);	
	/**
	 * �ϴ��ļ�Http�����ϴ����ļ�
	 * @param  srcName �����ҳ���DOM����
	 * @param  desDoc �ϴ�����ļ���(����·��)
	 * @param  descFileName �ϴ�����ļ��������ļ���(���Ϊ������ԭ���Ŀͻ���������ļ���)
	 * @return false���ϴ��ļ�ʧ�ܣ�true���ϴ��ļ��ɹ�
	 */
	
//	public static String upLoadHttpRequestFile(HttpServletRequest request,String srcName,String desDoc,StringBuffer descFileName) throws Exception
//	{
//		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
//		
//
//		CommonsMultipartFile file = (CommonsMultipartFile) multipartRequest.getFile(srcName);
//		
//		byte[] bytes=file.getBytes();   
//		
//		//����ļ������ڻ����ļ�Ϊ��
//		if(bytes==null||bytes.length==0)
//		{
//			return "�ļ������ڻ��ļ�Ϊ�գ�";
//		}
////		if(bytes.length>20971520)
////		{
////			return "�ļ���С����20M����";
////		}     
//
//        //String uploadDir=request.getSession().getServletContext().getRealPath(desDoc);	        	
//       	            
//        String sep = System.getProperty("file.separator");//����ϵͳ�ж����ļ���Ŀ¼б������         
//       
//        File dirPath = new File(desDoc);
//        
//        if(!dirPath.isDirectory())
//        {
//        	dirPath.mkdirs();
//        }
//        String originalFile=file.getOriginalFilename();//ԭ�ļ��� 
//        
//        
//        /*���δ�����µ��ļ�����ʹ��ԭ�����ļ���������ʹ�ô�����ļ������ļ���׺����ԭ�������ļ���׺*/
//        if(descFileName.toString().equals(""))
//        {
//        	descFileName.append(originalFile);
//        }
//        else
//        {
//        	descFileName.append((originalFile.lastIndexOf(".")>0?originalFile.substring(originalFile.lastIndexOf("."),originalFile.length()):""));
//        }        
//                   
//        File uploadedFile = new File(desDoc + sep+ descFileName);
//        
//        try
//        {
//        	FileCopyUtils.copy(bytes, uploadedFile);
//        }
//        catch(IOException e)
//        {
//        	e.printStackTrace();
//        	return "�ļ��ϴ�ʧ�ܣ�"+e.getMessage();
//        	//return false;
//        }
//        catch(Exception ex)
//        {
//        	ex.printStackTrace();
//        	return "�ļ��ϴ�ʧ�ܣ�"+ex.getMessage();
//        	//return false;
//        }		    
//	  
//		return "0000";
//	}
//	
	/**
	 * ȡ���ļ���һЩ���ԣ���ֻ�������أ�Ŀ¼��
	 * @param locFile File����
	 * @return String--�����ļ����Ե�һ���ִ�
	 */
	public static String getFileAttr(File locFile)
	{
		if(locFile==null)
		{
			throw new IllegalArgumentException("getFileAttr() ��������");
		}
		
		int fileAttribute=0;
		
		if(locFile.canRead() && !locFile.canWrite()) //ֻ��
		{
			fileAttribute+=1;
		}
		if(locFile.isHidden()) //����
		{
			fileAttribute+=2;
		}
		if(locFile.isDirectory()) //Ŀ¼
		{
			fileAttribute+=16;
		}
		String ss=new Integer(fileAttribute).toString();
		
		return ss;
	}
	
	
	/**
	 * ��Զ�̷�������ͻ���ȡ�ļ�������
	 * 
	 * @param ip Զ�̷�������ͻ���ip��ַ
	 * @param strPort Զ�̷�������ͻ��˶˿�
	 * @param locFile ���ص��ļ���
	 * @param remoteFile Զ�̷�������ͻ����ϵ��ļ���
	 * @param type �ļ����䷽ʽ 0:ת������ , 1:ȡ�ļ�
	 * @param remoteIp �ͻ��˵�IP��ַ(��汾����������ʱ����Ϊ���ַ���)
	 * @param terminalNo �ͻ��˵��ն˺� (��汾����������ʱ����Ϊ���ַ���)
	 * @param packetLen	ͨѶÿ�������С 
	 * @param vzipType  ͨѶѹ����ʽ 
	 * @return  true���ɹ���false��ʧ��
	 */
	public static String getRvcFile(String ip, String strPort, String locFile,
			String remoteFile, String remoteIp, String terminalNo, int type,int connTime,int soTime,int packetLen,int vzipType) 
	{
		log.info("vziptype="+vzipType);System.out.println("vziptype="+vzipType);
		log.info("ip=["+String.valueOf(ip)+"]") ;
		log.info("strPort=["+String.valueOf(strPort)+"]") ;
		log.info("locFile=["+String.valueOf(locFile)+"]") ;
		log.info("remoteFile=["+String.valueOf(remoteFile)+"]") ;
		log.info("remoteIp=["+String.valueOf(remoteIp)+"]") ;
		log.info("terminalNo=["+String.valueOf(terminalNo)+"]") ;
		
		if (ip == null || strPort == null || locFile == null
				|| remoteFile == null || remoteIp == null || terminalNo == null) 
		{
			return "ϵͳ��������";			
		}
		Socket s = null;
		DataInputStream r = null;
		DataOutputStream w = null;
		FileOutputStream to = null;

		int port;
		
		try
		{
			port = Integer.parseInt(strPort);
		}
		catch(Exception e)
		{	
			log.info("�������˿ڴ���,",e) ;		
			e.printStackTrace();
			return "�������˿ڴ���,strPort:" + strPort;			
		}
		
		try 
		{
			s = new Socket();
			InetSocketAddress isa = new InetSocketAddress(ip, port);
			s.connect(isa, connTime*1000);
			//s.setSoTimeout(soTime*1000);
			s.setSoTimeout(soTime*1000);
			w = new DataOutputStream(s.getOutputStream());
			r = new DataInputStream(s.getInputStream());
			
			/*�ļ�����*/
			String firstSend = new StringBuffer("<?xml version=\"1.0\" encoding=\"GB2312\" ?>")
								   .append("<root>")
								   .append("<downfiletype>").append(type).append("</downfiletype>")
								   .append("<hostinfo>")
								   .append("<termno>").append(terminalNo).append("</termno>")
								   .append("<hostip>").append(remoteIp).append("</hostip>")
								   .append("</hostinfo>")
								   .append("<cmdid>0101</cmdid>")
								   .append("<filename>").append(remoteFile).append("</filename>")
								   .append("</root>").toString();

			File to_file = new File(locFile);
			to = new FileOutputStream(to_file);
			
//			w.write(StringUtil.preProcess(firstSend.getBytes(),2));
//			w.write(StringUtil.preProcess(firstSend.getBytes(),vzipType));
//			w.flush();
			StringBuffer retMsg = new StringBuffer();
			log.info("v�˷��͵ı���=["+firstSend+"]");
//			log.info("v�˷��͵ı��ĳ���=["+firstSend.length()+"]");
			if(!SocketUtil.sendMessage(w, firstSend.getBytes(), packetLen, vzipType, retMsg)){
			//���Ӳ����豸�ţ�ͨ����Ϣת������ȡ�豸�ļ�����Կ��������ת��    2011-8-22  mod by cy
			//�ļ����䲻����MAC����ԭ֮ǰ������    2011-9-1  mod by cy
			//if(!MessageEncoded.sendMessage(w, firstSend.getBytes(), packetLen, vzipType, retMsg, terminalNo)){
				return "���ͱ���ʧ��"+retMsg;	
			}
			StringBuffer ret0 = new StringBuffer();
			
			byte[] b = SocketUtil.readSocket3(r, ret0);
			if (b == null) 
			{				
				return "�����ļ�ʧ��";
			}
			if (!ret0.toString().equals("00")) 
			{
				return "�����ļ�ʧ�ܣ�ʧ��ԭ��:" + tranCode(ret0.toString());				
			}			
			
			int maxfileLength=0;
			
			try
			{
				
				String [] messageArray=(new String(b,0,b.length)).split("\\|");		
				maxfileLength = Integer.parseInt(messageArray[4].replace(" ", ""));
			}
			catch(Exception e)
			{
				log.info("1----------,",e) ;		
				e.printStackTrace();
			}			
			int position = 0;

			/*�����ļ�*/
			for (;;) 
			{
				StringBuffer secondSend = new StringBuffer();
				secondSend.append("<?xml version=\"1.0\" encoding=\"GB2312\" ?>")
						  .append("<root><cmdid>0102</cmdid>")
						  .append("<startpos>").append(position).append("</startpos>")
						  .append("</root>");				
				
//				w.write(StringUtil.preProcess(secondSend.toString().getBytes(),vzipType));
//				w.flush();
				log.info("v�˷��͵ı���=["+secondSend.toString()+"]");
				retMsg.setLength(0);
				if(!SocketUtil.sendMessage(w, secondSend.toString().getBytes(), packetLen, vzipType, retMsg)){
				//���Ӳ����豸�ţ�ͨ����Ϣת������ȡ�豸�ļ�����Կ��������ת��    2011-8-22  mod by cy
				//�ļ����䲻����MAC����ԭ֮ǰ������    2011-9-1  mod by cy
				//if(!MessageEncoded.sendMessage(w, secondSend.toString().getBytes(), packetLen, vzipType, retMsg, terminalNo)){
					return "���ͱ���ʧ��"+retMsg;
				}
				StringBuffer ret = new StringBuffer();
				byte[] readByte =  SocketUtil.readSocket3(r, ret);						
				
				if (readByte == null) 
				{					
					return "�����ļ�ʧ�ܣ�ԭ��:��ѹ��ʧ��";
					
				}			
				
				if (ret.toString().equals("21")&&(position<=maxfileLength)) 
				{
					to.write(readByte);
					to.flush();
					position += readByte.length;
					
				} 
				else if (ret.toString().equals("04")|| ret.toString().equals("20")) 
				{
					break;
				}
				else if (ret.toString().equals("01")) 
				{
					log.info("�������˴�Ҫ������ļ�ʧ�ܣ�ʧ�ܴ���Ϊ:" + ret);
					return "�������˴�Ҫ������ļ�ʧ�ܣ�ʧ�ܴ���Ϊ:" + ret;
				} 	
				else if (ret.toString().equals("02")) 
				{
					log.info("�������˶�λ�ļ���ָ�����ļ�λ��ʧ�ܣ�ʧ�ܴ���Ϊ:" + ret);
					return "�������˶�λ�ļ���ָ�����ļ�λ��ʧ�ܣ�ʧ�ܴ���Ϊ:" + ret;
				} 
				else if (ret.toString().equals("07")) 
				{
					log.info("�����ֹͣ�ļ����ͷ���ʧ�ܴ���Ϊ:" + ret);
					return "�����ֹͣ�ļ����ͷ���ʧ�ܴ���Ϊ:" + ret;
				} 	
				else if (ret.toString().equals("30")) 
				{
					log.info("����˽������Ĵ���ָ����XML�ֶ����������в����ڣ�ʧ�ܴ���Ϊ:" + ret);
					return "����˽������Ĵ���ָ����XML�ֶ����������в����ڣ�ʧ�ܴ���Ϊ:" + ret;
				} 
				else if (ret.toString().equals("21")&&(position>maxfileLength)) 
				{
					break;				
				}
				else
				{
					log.info("δ֪���ش��룬����Ϊ:" + ret);
					return "δ֪���ش��룬����Ϊ:" + ret;					
				}
			}
		} 
		catch (IOException e) 
		{		
			log.info("2----------,",e) ;	
			e.printStackTrace();
			return "����ʧ�ܣ�"+e.getMessage();			
		} 
		catch (Exception e) 
		{	
			log.info("3----------,",e) ;		
			e.printStackTrace();
			return "����ʧ�ܣ�"+e.getMessage();
			
		} 
		finally 
		{
			try 
			{
				if (r != null)
				{
					r.close();
				}
			}
			catch (IOException e) 
			{				
				e.printStackTrace();
				return "����ʧ�ܣ�"+e.getMessage();				
			} 
			catch (Exception e) 
			{				
				e.printStackTrace();
				return "����ʧ�ܣ�"+e.getMessage();				
			}			
			
			try 
			{
				if (w != null) 
				{
					w.close();
				}
			}
			catch (IOException e) 
			{				
				e.printStackTrace();
				return "����ʧ�ܣ�"+e.getMessage();				
			} 
			catch (Exception e) 
			{				
				e.printStackTrace();
				return "����ʧ�ܣ�"+e.getMessage();				
			}
			
			try 
			{
				if (s != null) 
				{
					s.close();
				}
			}
			catch (IOException e) 
			{				
				e.printStackTrace();
				return "����ʧ�ܣ�"+e.getMessage();				
			} 
			catch (Exception e) 
			{				
				e.printStackTrace();
				return "����ʧ�ܣ�"+e.getMessage();				
			}
			try 
			{
				if (to != null) 
				{
					to.close();
				}
			}
			catch (IOException e) 
			{				
				e.printStackTrace();
				return "����ʧ�ܣ�"+e.getMessage();				
			} 
			catch (Exception e) 
			{				
				e.printStackTrace();
				return "����ʧ�ܣ�"+e.getMessage();				
			}
		}
		return "0000";
	}
	
	
	/**  
	 * ɾ��ָ��Ŀ¼�е��������ݡ�(�ݹ�)   
	 * 
	 * @param dir Ҫɾ����Ŀ¼  
	 * @return ɾ���ɹ�ʱ����true�����򷵻�false�� 
	 */  
	
	public static boolean deleteDirectory(File dir) 
	{   
		if ( (dir == null) || !dir.isDirectory()) 
		{     
			return false;  
		}  
		
		boolean delResult=true;
		
		try
		{
			File[] entries = dir.listFiles(); 
			
			int sz = entries.length; 
			
			for (int i = 0; i < sz; i++) 
			{  
				if (entries[i].isDirectory())
				{      
					if (!deleteDirectory(entries[i]))
					{         
						delResult=false;       
					}      
				}      
				else 
				{      
					if (!entries[i].delete()) 
					{       
						delResult=false;     
					}     
				}  
			} 
			
			if (!dir.delete()) 
			{     
				delResult=false;  
			} 	
		}
		catch(SecurityException  e)
		{
			e.printStackTrace();				
		}
		catch(Exception e)
		{
			e.printStackTrace();			
		}
		return delResult; 
	}
	
	/**
	 * �����ļ��������ϵ��ļ�
	 * 
	 * @param fileName ���غ󱣴���ļ���
	 * @param delFlag ɾ��ԭ�ļ���־ true:ɾ�� false:��ɾ��
	 * @param fileSrc  �ļ�����·��(����/shepherd/file/upload/version/10001_10001.zip)
	 * @return ���سɹ���ʧ�ܵı�־
	 * */
//	public static boolean downLoadResponseFile(HttpServletRequest request,HttpServletResponse response,String fileName,String fileSrc,boolean delFlag)
//	{
//				
//		//File downFile=new File( request.getSession().getServletContext().
//		//		 getRealPath(fileSrc));
//		File downFile=new File( fileSrc);
//		BufferedInputStream bis = null;
//		BufferedOutputStream  bos = null;
//		try
//		{
//			if(downFile.exists())
//			{
//				response.setContentType("application/octet-stream");
//				response.setHeader("Content-disposition","attachment;filename="+new String(fileName.getBytes("gbk"),"iso8859-1"));
//						
//				bis =new BufferedInputStream(new FileInputStream(downFile));
//				
//				bos=new BufferedOutputStream(response.getOutputStream());
//				
//				byte[] buff = new byte[2048];
//				int bytesRead;
//				while(-1 != (bytesRead = bis.read(buff, 0, buff.length))) 
//				{
//					bos.write(buff,0,bytesRead);
//				}
//				bis.close();
//				bos.flush();
//				bos.close();
//				
//				return true;
//			}
//			else
//			{				
//				return false;
//			}
//		}
//		catch(Exception e)
//		{				
//			return false;
//		}
//		finally 
//		{			
//			try
//			{
//				if (bis != null)
//				{
//					bis.close();					
//				}
//			}
//			catch(Exception ebis)
//			{
//				ebis.printStackTrace();
//			}
//			try
//			{
//				if (bos != null)
//				{			
//					bos.flush();			
//					bos.close();			
//				}
//			}
//			catch(Exception ebos)
//			{ 
//				ebos.printStackTrace();
//			}
//			
//			try
//			{
//				if(delFlag)
//				{
//					downFile.delete();
//				}
//			}
//			catch(Exception edel)
//			{ 
//				edel.printStackTrace();
//			}
//		}
//		
//	}
//	
	/**
	 * �����ļ����䷵�ش���
	 * */
	public static String tranCode(String retCode)
	{
		if(retCode.equals("01")) return "���ļ�ʧ��";
		if(retCode.equals("02")) return "��λ���������ļ���ָ�����ļ�λ��ʧ��";
		if(retCode.equals("04")) return "���������ļ������ټ�����ȡ���ݣ��ļ��Ѿ�����";
		if(retCode.equals("30")) return "���Ľ�������ָ����XML�ֶ����������в�����";
		if(retCode.equals("20")) return "���������ļ����ͽ���";
		if(retCode.equals("21")) return "���ĵķ�������Ϊ�ļ����ݣ����ݵĳ���Ϊ(���ĳ���-8)";
		if(retCode.equals("00")) return "���������ļ�׼�����";
		if(retCode.equals("05")) return "�ļ��ѱ��������ļ�������,��ȡ�ļ���Ϣʧ��";
		if(retCode.equals("06")) return "��������";
		if(retCode.equals("07")) return "�����ֹͣ�ļ����ͷ���";
		return retCode;
	}

	/**
	 * ��ȡ���޸��ļ�����
	 * */
	public static String decreaseTrace() {
		String path = "./recodeTraceNo.txt";
		File fp = null;
		FileReader fr = null;
		FileWriter fw = null;
		FileInputStream fip = null;
		String trace = null;
		char data[]=new char[6]; 
		try
		{
			fp = new File(path);
			log.info("["+System.getProperty("user.dir")+System.getProperty("file.separator")+"test0.txt]");
			if(fp.exists())
			{
			fr = new FileReader(path);
			fr.read(data);
			String str=new String(data); 
			str = str.trim();
			if(str!=null && !str.equals(""))
			{
			int num = Integer.parseInt(str);
			if (num >=999999)
			{
				num = 1;
			}
			else 
			{
				num ++;
			}
			trace = String.valueOf(num);
			}
			else
			{
			trace = "1";	
			}
				fw = new FileWriter(fp);
				fw.write(trace);
				fw.close();
				fr.close();
			while(trace.length()!=6)
			{
				trace = "0"+trace;
			}
			return trace;
			}
			else
			{
				fw = new FileWriter(fp);
				fw.write("1");
				fw.close();
				return "000001";
			}
		}
		catch(Exception e)
		{
			return trace;
		}
		finally 
		{			
			try
			{
				if (fr != null)
				{
					fr.close();					
				}
			}
			catch(Exception ebis)
			{
				ebis.printStackTrace();
			}
			try
			{
				if (fw != null)
				{			
					//fw.flush();			
					fw.close();			
				}
			}
			catch(Exception ebos)
			{ 
				ebos.printStackTrace();
			}
		}
	}	
	
   /**
    * ѹ���ļ�����
    */
	public static String getPackRvcFile(String ip, String strPort, String locFile,
			String remoteFile, String remoteIp, String terminalNo, int type,int connTime,int soTime,int packetLen,int vzipType) 
	{	
		if (ip == null || strPort == null || locFile == null
				|| remoteFile == null || remoteIp == null || terminalNo == null) 
		{
			return "ϵͳ��������";			
		}
		Socket s = null;
		DataInputStream r = null;
		DataOutputStream w = null;
		FileOutputStream to = null;

		int port;
		
		try
		{
			port = Integer.parseInt(strPort);
		}
		catch(Exception e)
		{	
			log.info("�������˿ڴ���,",e) ;		
			e.printStackTrace();
			return "�������˿ڴ���,strPort:" + strPort;			
		}
		
		try 
		{
			s = new Socket();
			InetSocketAddress isa = new InetSocketAddress(ip, port);
			s.connect(isa, connTime*1000);
			s.setSoTimeout(soTime*1000);
			w = new DataOutputStream(s.getOutputStream());
			r = new DataInputStream(s.getInputStream());
			
			/*�ļ�����*/
			String firstSend = new StringBuffer("<?xml version=\"1.0\" encoding=\"GB2312\" ?>")
								   .append("<root>")
								   .append("<downfiletype>").append(type).append("</downfiletype>")
								   .append("<hostinfo>")
								   .append("<termno>").append(terminalNo).append("</termno>")
								   .append("<hostip>").append(remoteIp).append("</hostip>")
								   .append("</hostinfo>")
								   .append("<cmdid>0101</cmdid>")
								   .append("<filename>").append(remoteFile).append("</filename>")
								   .append("<localinfo>")
								   .append("<termno>").append(terminalNo).append("</termno>")
								   .append("<localip>").append(ip).append("</localip>")
								   .append("</localinfo>")
								   .append("</root>").toString();

			File to_file = new File(locFile);
			to = new FileOutputStream(to_file);
			
//			w.write(StringUtil.preProcess(firstSend.getBytes(),2));
//			w.write(StringUtil.preProcess(firstSend.getBytes(),vzipType));
//			w.flush();
			StringBuffer retMsg = new StringBuffer();
			log.info("v�˷��͵ı���=["+firstSend+"]");
			System.out.println("====0101����=====" + firstSend.toString());
//			log.info("v�˷��͵ı��ĳ���=["+firstSend.length()+"]");
			if(!SocketUtil.sendMessage(w, firstSend.getBytes(), packetLen, vzipType, retMsg)){
			//���Ӳ����豸�ţ�ͨ����Ϣת������ȡ�豸�ļ�����Կ��������ת��    2011-8-22  mod by cy
			//�ļ����䲻����MAC����ԭ֮ǰ������    2011-9-1  mod by cy
			//if(!MessageEncoded.sendMessage(w, firstSend.getBytes(), packetLen, vzipType, retMsg, terminalNo)){
				return "���ͱ���ʧ��"+retMsg;	
			}
			StringBuffer ret0 = new StringBuffer();
			
			byte[] b = SocketUtil.readSocket3(r, ret0);
			if (b == null) 
			{				
				return "�����ļ�ʧ��";				
			}
			if (!ret0.toString().equals("00")) 
			{
				return "�����ļ�ʧ�ܣ�ʧ��ԭ��:" + tranCode(ret0.toString());				
			}			
			
			int maxfileLength=0;
			
			try
			{
				
				String [] messageArray=(new String(b,0,b.length)).split("\\|");		
				maxfileLength = Integer.parseInt(messageArray[4].replace(" ", ""));
			}
			catch(Exception e)
			{
				log.info("1----------,",e) ;		
				e.printStackTrace();
			}			
			int position = 0;

			/*�����ļ�*/
			for (;;) 
			{   
				/**
				 * 0102���ļ���compresstype�ڵ�,��socket��ȡ����ʱ0Ϊ��ѹ����־
				 */
				StringBuffer secondSend = new StringBuffer();
				secondSend.append("<?xml version=\"1.0\" encoding=\"GB2312\" ?>")
						  .append("<root><cmdid>0102</cmdid>")
						  .append("<startpos>").append(position).append("</startpos>")
						  .append("<compresstype>").append(0).append("</compresstype>")
						  .append("</root>");				
				
//				w.write(StringUtil.preProcess(secondSend.toString().getBytes(),vzipType));
//				w.flush();
				log.info("v�˷��͵ı���=["+secondSend.toString()+"]");
				System.out.println("======0102����======"+secondSend.toString());
				retMsg.setLength(0);
         
				if(!SocketUtil.sendMessage(w, secondSend.toString().getBytes(), packetLen, vzipType, retMsg)){
				//���Ӳ����豸�ţ�ͨ����Ϣת������ȡ�豸�ļ�����Կ��������ת��    2011-8-22  mod by cy
				//�ļ����䲻����MAC����ԭ֮ǰ������    2011-9-1  mod by cy
				//if(!MessageEncoded.sendMessage(w, secondSend.toString().getBytes(), packetLen, vzipType, retMsg, terminalNo)){
					System.out.println("======0102���ķ���ʧ��======");
					return "���ͱ���ʧ��"+retMsg;
				}
				StringBuffer ret = new StringBuffer();
				byte[] readByte =  SocketUtil.readSocket3(r, ret);						
				System.out.println("<<<======ret=====>>>" + ret);
				System.out.println("<<<====maxfileLength====>>>" + maxfileLength);
				if (readByte == null) 
				{					
					return "�����ļ�ʧ�ܣ�ԭ��:��ѹ��ʧ��";
					
				}
				
				if (ret.toString().equals("21")&&(position<=maxfileLength)) 
				{
					to.write(readByte);
					to.flush();
					position += readByte.length;
					
				} 
				else if (ret.toString().equals("04")|| ret.toString().equals("20")) 
				{
					break;
				}
				else if (ret.toString().equals("01")) 
				{
					log.info("�������˴�Ҫ������ļ�ʧ�ܣ�ʧ�ܴ���Ϊ:" + ret);
					return "�������˴�Ҫ������ļ�ʧ�ܣ�ʧ�ܴ���Ϊ:" + ret;
				} 	
				else if (ret.toString().equals("02")) 
				{
					log.info("�������˶�λ�ļ���ָ�����ļ�λ��ʧ�ܣ�ʧ�ܴ���Ϊ:" + ret);
					return "�������˶�λ�ļ���ָ�����ļ�λ��ʧ�ܣ�ʧ�ܴ���Ϊ:" + ret;
				} 
				else if (ret.toString().equals("07")) 
				{
					log.info("�����ֹͣ�ļ����ͷ���ʧ�ܴ���Ϊ:" + ret);
					return "�����ֹͣ�ļ����ͷ���ʧ�ܴ���Ϊ:" + ret;
				} 	
				else if (ret.toString().equals("30")) 
				{
					log.info("����˽������Ĵ���ָ����XML�ֶ����������в����ڣ�ʧ�ܴ���Ϊ:" + ret);
					return "����˽������Ĵ���ָ����XML�ֶ����������в����ڣ�ʧ�ܴ���Ϊ:" + ret;
				} 
				else if (ret.toString().equals("21")&&(position>maxfileLength)) 
				{
					break;				
				}
				else
				{
					log.info("δ֪���ش��룬����Ϊ:" + ret);
					return "δ֪���ش��룬����Ϊ:" + ret;					
				}
			}
		} 
		catch (IOException e) 
		{		
			log.info("2----------,",e) ;
			try {
				System.out.println("---TimeOut----" + s.getSoTimeout());
			} catch (SocketException e1) {
				// TODO Auto-generated catch block
				System.out.println("��ȡ��ʱʱ���쳣");
				e1.printStackTrace();
			}
			e.printStackTrace();
			return "����ʧ�ܣ�"+e.getMessage();			
		} 
		catch (Exception e) 
		{	
			log.info("3----------,",e) ;		
			e.printStackTrace();
			return "����ʧ�ܣ�"+e.getMessage();
			
		} 
		finally 
		{
			try 
			{
				if (r != null)
				{
					r.close();
				}
			}
			catch (IOException e) 
			{				
				e.printStackTrace();
				return "����ʧ�ܣ�"+e.getMessage();				
			} 
			catch (Exception e) 
			{				
				e.printStackTrace();
				return "����ʧ�ܣ�"+e.getMessage();				
			}			
			
			try 
			{
				if (w != null) 
				{
					w.close();
				}
			}
			catch (IOException e) 
			{				
				e.printStackTrace();
				return "����ʧ�ܣ�"+e.getMessage();				
			} 
			catch (Exception e) 
			{				
				e.printStackTrace();
				return "����ʧ�ܣ�"+e.getMessage();				
			}
			
			try 
			{
				if (s != null) 
				{
					s.close();
				}
			}
			catch (IOException e) 
			{				
				e.printStackTrace();
				return "����ʧ�ܣ�"+e.getMessage();				
			} 
			catch (Exception e) 
			{				
				e.printStackTrace();
				return "����ʧ�ܣ�"+e.getMessage();				
			}
			try 
			{
				if (to != null) 
				{
					to.close();
				}
			}
			catch (IOException e) 
			{				
				e.printStackTrace();
				return "����ʧ�ܣ�"+e.getMessage();				
			} 
			catch (Exception e) 
			{				
				e.printStackTrace();
				return "����ʧ�ܣ�"+e.getMessage();				
			}
		}
		return "0000";
	}
	
	/**
	 * ���������������ѹ���ļ�Ŀ¼
	 * @return
	 */
	public static  String createDirectory(String path) {
		String[] list = path.split("\\\\");
		String dirPath = "" ;			
		try {
			/*����ļ�Ŀ¼������������*/
			for(int i=0;i<list.length;i++){
				dirPath +=list[i]+System.getProperty("file.separator");
			}
			dirPath = dirPath.substring(0,dirPath.lastIndexOf(System.getProperty("file.separator")));
			File dir = new File(dirPath);
			if (!dir.isDirectory() && !dir.exists()) {
				dir.mkdirs();
			}
			return dirPath;
		} catch (Exception e) {
			System.out.println("�����ļ�ʧ��");
		}
		return null;
	}
	
	public static void main(String[] args){	

	   
	}	

}