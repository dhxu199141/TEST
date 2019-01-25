package com.zjft.shepherd.common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.zip.ZipException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zjft.shepherd.dao.DevInfoDAO;
import com.zjft.shepherd.common.EncryptUtil;
import com.zjft.shepherd.common.SystemCons;

/**
 * socket操作实用类
 * 
 * @author hpshen
 * @since 2007.04.10
 */

public class SocketUtil {

	private static Log log = LogFactory.getLog(SocketUtil.class);

	private static final boolean messageEncrypt = SystemCons.isMessageEncrypt();
	
    private static final int macLength = 8;
	
	private static final int devNoLength = 32;
	/**
	 * 从socket通道上读取数据，并将读到的数据解压缩
	 */
	
	public static byte[] readSocket1(DataInputStream in) throws IOException,ZipException,Exception {

		if (in == null) {
			throw new NullPointerException("readSocket方法输入参数为空");
		}

		// 读取报文信息段

		byte[] headSection = new byte[8];
		int charsRead;

		charsRead = in.read(headSection, 0, 8);
		log.info("C端返回报文头："+new String(headSection));
		if (charsRead == -1) {
			log.error("从socket中没读到内容");
			return null;
		}

		int realLength = 0;
		String zipFlag = "0";
		String encrytFlag = "0";
		try {
			realLength = Integer.parseInt(new String(headSection, 0, 4)) - 8;
			zipFlag = new String(headSection, 5, 1);
			//获取加密标志
			encrytFlag = new String(headSection, 6, 1);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			log.error("获取报文正文长度出错");
			return null;
		}

		// 读取报文数据段

		byte[] dataSection = new byte[realLength];
		int readLength = 0;
		while (readLength < realLength) {
			charsRead = in.read(dataSection, readLength, realLength - readLength);
			if (charsRead == -1 || charsRead == 0) {
				log.error("从socket中没读到内容");
				return null;
			}
			readLength += charsRead;
		}

		// 解压缩
		//return ZipUtil.unzip(dataSection);
		byte[] read = null;
		byte[] result = null;
		switch (Integer.valueOf(zipFlag)) {
		case 0://非压缩方式
			read = dataSection;
			break;
//		case 1://1： LZARI压缩方式
//			break;
		case 2://2： ZIP压缩方式
			read = ZipUtil.unzip(dataSection);
			break;
		case 3://3： GZIP压缩方式
			read = ZipUtil.ungzip(dataSection);
			break;
		default:
		    break;
		}
		
		result = read;
		if(encrytFlag.equals("1"))
		{
			result = new byte[read.length-macLength-devNoLength];
			if(!macCheck(result, read))
			{
				log.error("<<<Does not process the packet failed validation["+new String(read)+"]");
				return null;
			}
		}
		
//		log.info("C端返回报文："+new String(result));
		return result;
	}
   
	
	/**
	 * 从socket通道上读取数据，并将读到的数据解压缩
	 */
	
	public static String readSocket2(DataInputStream in) throws IOException,ZipException,Exception {
		
		if (in == null){
			throw new NullPointerException("readSocket方法输入参数为空");
		}

		byte[] dataByte = new byte[100 * 1024];
		int count = 0;
		String zipFlag=null;
		String encrytFlag = "0";
		while (true) {
			
     		//读取报文信息段

			byte[] headSection = new byte[8];
			int charsRead;

			charsRead = in.read(headSection, 0, 8);
			//sjxu 不打印空信息 v1.9.0
			log.debug("收到内容报文头："+new String(headSection));
			zipFlag = new String(headSection, 5, 1);
			//获取加密标志
			encrytFlag = new String(headSection, 6, 1);
			if (charsRead == -1) {
				log.debug("从socket中没读到内容");
				return null;
			}

			int realLength = 0;
			try {
				realLength = Integer.parseInt(new String(headSection, 0, 4)) - 8;
			} catch (NumberFormatException e) {
				log.error("获取报文正文长度出错");
				return null;
			}

			//读取报文数据段
			
			byte[] dataSection = new byte[realLength];
			int readLength = 0;
			while (readLength < realLength) {
				charsRead = in.read(dataSection, readLength, realLength - readLength);
				if (charsRead == -1 || charsRead == 0) {
					log.error("从socket中没读到内容");
					return null;
				}
				readLength += charsRead;
			}
			
			System.arraycopy(dataSection, 0, dataByte, count, realLength);
			count += realLength;
			
			//判断是否有后续包（T表示无后续包）
			
			String endFlag = new String(headSection, 4, 1);
			if (endFlag.equalsIgnoreCase("T")) {
				break;
			} else {
				continue;
			}
		}		
		
		byte[] beforeUnzip = new byte[count];
		System.arraycopy(dataByte, 0, beforeUnzip, 0, count);
		
		//解压缩

//		return new String(ZipUtil.unzip(beforeUnzip));
		byte[] read = null;
		byte[] result = null;
		switch (Integer.valueOf(zipFlag)) {
		case 0://非压缩方式
			read = beforeUnzip;
			break;
//		case 1://1： LZARI压缩方式
//			break;
		case 2://2： ZIP压缩方式
			read = ZipUtil.unzip(beforeUnzip);
			break;
		case 3://3： GZIP压缩方式
			read = ZipUtil.ungzip(beforeUnzip);
			break;
		default:
		    break;
		}
		
		result = read;
		if (encrytFlag.equals("1")) {
			result = new byte[read.length - macLength - devNoLength];
			if (!macCheck(result, read)) {
				log.error("<<<Does not process the packet failed validation[" + new String(read) + "]");
				return null;
			}
		}
		return new String(result);
	}

	/**
	 * 从socket通道上读取数据，并将读到的数据解压缩  拆包传输 2009年3月9日
	 */
	
	public static byte[] readSocket3(DataInputStream in) throws IOException,ZipException,Exception {
		
		if (in == null){
			throw new NullPointerException("readSocket方法输入参数为空");
		}
		log.info("开始接包in["+in+"]");
		byte[] dataByte = new byte[100 * 1024];
		int count = 0;
		String zipFlag=null;
		String encrytFlag = "0";  //默认为0  不加解密
		while (true) {
			
     		//读取报文信息段
			byte[] headSection = new byte[8];
			int charsRead;
			charsRead = in.read(headSection, 0, 8);
			log.info("C端返回报文头："+new String(headSection));
			if (charsRead == -1) {
				log.error("从socket中没读到内容");
				return null;
			}
			String inhead = new String(headSection, 0, 8);
			zipFlag=new String(headSection, 5, 1);
			//获取加密标志
			encrytFlag = new String(headSection, 6, 1);
			log.info("包头in["+in+"]["+inhead+"]");
			int realLength = 0;
			try {
				realLength = Integer.parseInt(new String(headSection, 0, 4)) - 8;
			} catch (NumberFormatException e) {
				log.error("获取报文正文长度出错");
				return null;
			}
			//读取报文数据段
			
			byte[] dataSection = new byte[realLength];
			int readLength = 0;
			while (readLength < realLength) {
				charsRead = in.read(dataSection, readLength, realLength - readLength);
				if (charsRead == -1 || charsRead == 0) {
					log.error("从socket中没读到内容");
					return null;
				}
				readLength += charsRead;
			}
			
			System.arraycopy(dataSection, 0, dataByte, count, realLength);
			count += realLength;
			log.info("小包in["+in+"]包长["+dataSection.length+"]");
			//判断是否有后续包（T表示无后续包）
			
			String endFlag = new String(headSection, 4, 1);
			if (endFlag.equalsIgnoreCase("T")) {
				break;
			} else {
				continue;
			}
		}		
		
		byte[] beforeUnzip = new byte[count];
		System.arraycopy(dataByte, 0, beforeUnzip, 0, count);
		log.info("解压前in["+in+"]包长["+beforeUnzip.length+"]");
		//解压缩
//		return ZipUtil.unzip(beforeUnzip);
		byte[] read = null;
		byte[] result = null;
		switch (Integer.valueOf(zipFlag)) {
		case 0://非压缩方式
			read = beforeUnzip;
			break;
//		case 1://1： LZARI压缩方式
//			break;
		case 2://2： ZIP压缩方式
			read = ZipUtil.unzip(beforeUnzip);
			break;
		case 3://3： GZIP压缩方式
			read = ZipUtil.ungzip(beforeUnzip);
			break;
		default:
		    break;
		}
		
		result = read;
		if(encrytFlag.equals("1"))
		{
			result = new byte[read.length-macLength-devNoLength];
			if(!macCheck(result, read))
			{
				log.error("<<<Does not process the packet failed validation["+new String(read)+"]");
				return null;
			}
		}
//		log.info("C端返回报文："+new String(result));
		return result;
	}
	
	/*
	 * 该方法涉及非现金设备，且该通讯方法目前没有使用，暂时不进行加解密的修改
	 * 日后若使用到该方法，再决定是否修改     2011-8-22
	 */
	public static byte[] readSocketNocash(DataInputStream in) throws IOException,ZipException {

		if (in == null) {
			throw new NullPointerException("readSocket方法输入参数为空");
		}

		// 读取报文信息段

		byte[] headSection = new byte[2];
		int charsRead;

		
		charsRead = in.read(headSection, 0, 2);
		if (charsRead == -1) {
			log.error("从socket中没读到内容");
			return null;
		}
		//log.info(String.format("%x" , headSection[1]));
		int realLength = 0;
		try {
			realLength = headSection[1]-2;
			if (realLength<0)
			{
				realLength = 256+headSection[1]-2;
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
			log.error("获取报文正文长度出错");
			return null;
		}
		log.info("报文长度："+realLength);
		// 读取报文数据段

		byte[] dataSection = new byte[realLength];
		int readLength = 0;
		while (readLength < realLength) {
			charsRead = in.read(dataSection, readLength, realLength - readLength);
			if (charsRead == -1 || charsRead == 0) {
				log.error("从socket中没读到内容");
				return null;
			}
			readLength += charsRead;
		}

		// 解压缩
//		log.info("C端返回报文："+new String(dataSection));
		return dataSection;

	}
	
	/**
	 * 从socket通道上读取数据，并将读到的数据解压缩，将返回码赋给retCode
	 */

	public static byte[] readSocket3(DataInputStream in, StringBuffer retCode)throws IOException, ZipException, Exception {

		if (in == null || retCode == null){
			throw new IllegalArgumentException("readSocket输入参数为空");
		}
		
		byte[] dataByte = new byte[SystemCons.getDowloadBytesBuffer() * 1024];
		int count = 0;

		String zipFlag = "0";
		String encrytFlag = "0";
		while (true) {
			
			//读取报文信息段

			byte[] headSection = new byte[8];
			int charsRead;

			charsRead = in.read(headSection, 0, 8);
			log.info("C端返回报文头："+new String(headSection));
			if (charsRead == -1) {
				log.error("从socket中没读到内容");
				return null;
			}

			int realLength = 0;
			try {
				realLength = Integer.parseInt(new String(headSection, 0, 4)) - 8;
			} catch (NumberFormatException e) {
				log.error("获取报文正文长度出错");
				return null;
			}

			//读取报文数据段
			
			byte[] dataSection = new byte[realLength];
			int readLength = 0;
			while (readLength < realLength) {
				charsRead = in.read(dataSection, readLength, realLength - readLength);
				if (charsRead == -1 || charsRead == 0) {
					log.error("从socket中没读到内容");
					return null;
				}
				readLength += charsRead;
			}

			System.arraycopy(dataSection, 0, dataByte, count, realLength);
			count += realLength;
			
            //判断是否有后续包（T表示无后续包）
			
			String endFlag = new String(headSection, 4, 1);
			zipFlag = new String(headSection, 5, 1);
			//获取加密标志
			encrytFlag = new String(headSection, 6, 1);
			if (endFlag.equalsIgnoreCase("T")) {
				break;
			} else {
				continue;
			}
		}
		
		byte[] beforeUnzip = new byte[count];
		System.arraycopy(dataByte, 0, beforeUnzip, 0, count);
		
		//解压缩
		
		//byte[] byteUnzip = (zipFlag.equals("2"))?ZipUtil.unzip(beforeUnzip):beforeUnzip;
		
		byte[] byteUnzip = null;
		byte[] result = null;
		switch (Integer.valueOf(zipFlag)) {
		case 0://非压缩方式
			byteUnzip = beforeUnzip;
			break;
//		case 1://1： LZARI压缩方式
//			break;
		case 2://2： ZIP压缩方式
			byteUnzip = ZipUtil.unzip(beforeUnzip);
			break;
		case 3://3： GZIP压缩方式
			byteUnzip = ZipUtil.ungzip(beforeUnzip);
			break;
		default:
		    break;
		}
		result = byteUnzip;
		if(encrytFlag.equals("1"))
		{
			result = new byte[byteUnzip.length-macLength-devNoLength];
			if(!macCheck(result, byteUnzip))
			{
				log.error("<<<Does not process the packet failed validation["+new String(byteUnzip)+"]");
				return null;
			}
		}
//		log.info("C端返回报文："+new String(result));
		//取返回码
		
		String ret = new String(result, 1, 2);
		retCode.append(ret);
		
		//取数据
		
		int contentLength = result.length - 4;
		byte[] contentByte = new byte[contentLength];
		System.arraycopy(result, 4, contentByte, 0, contentLength);
		return contentByte;
	}

	
	/**
	 * 发送命令信息到RVS服务器或Remote客户端并接收其返回内容(采用多包传输的方式)
	 * @param sndBody 发送内容(采用XML报文格式)
	 * @param ip ip地址
	 * @param strPort 端口号
	 * @param retBody 返回内容
	 * @param packetLen v端每包传输大小
	 * @param vzipType v端报文压缩方式
	 * @return true:发送成功 false:发送失败
	 */
	public static boolean sendCmdToRvcMutil(String sndBody, String ip, String strPort,StringBuffer retBody,int connTime,int soTime,int packetLen,int vzipType, String desKey) 
	{
		log.info("[SystemParam_messageEncrypt] "+SystemCons.isMessageEncrypt());
		log.info("[sendCmdToRvcMutilParam] "+"ip:"+ip+","+"strPort:"+strPort+","+"connTime:"+connTime+","+"soTime:"+soTime+","+"packetLen:"+packetLen+","+"vzipType:"+vzipType+","+"desKey:"+desKey);
		if (sndBody == null || ip == null || strPort == null || retBody == null) 
		{
			throw new IllegalArgumentException("参数非法");
		}

		//log.info("send to RVS:" + sndBody);

		int port = Integer.parseInt(strPort);
		if (port == -1) 
		{
			retBody.append("通讯端口出错:" + strPort);
			return false;
		}
		
		log.info("v端发送报文=["+sndBody+"]");
//		log.info("v端发送报文长度=["+sndBody.length()+"]");
		byte[] byteSnd = sndBody.getBytes();
		//是否加密   2011-8-22  mod by cy
		if(messageEncrypt)
		{
			try{
				byte[] mac = EncryptUtil.generateMacBytes919(byteSnd, desKey);
				byte[] byteSndbyEnc = new byte[byteSnd.length + 8];
				System.arraycopy(byteSnd, 0, byteSndbyEnc, 0, byteSnd.length);
				System.arraycopy(mac, 0, byteSndbyEnc, byteSnd.length, 8);
				byteSnd = byteSndbyEnc;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return false;
			}
		}
		
		byte[] byteZip;
		try 
		{
			switch (vzipType) {
			case 0://非压缩方式
				byteZip = byteSnd;
				break;
//			case 1://1： LZARI压缩方式
//				break;
			case 2://2： ZIP压缩方式
				byteZip = ZipUtil.zip(byteSnd);
				break;
			case 3://3： GZIP压缩方式
				byteZip = ZipUtil.gzip(byteSnd);
				break;
			default:
				retBody.append("压缩方式错误");
			    return false;
			} 
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			retBody.append("发送消息前压缩失败");
			return false;
		}
		
				
		Socket s = null;
		DataInputStream r = null;
		OutputStream out = null;
		
		
		
		// 组织发送报文
		StringBuffer head = new StringBuffer();
		
		int sendTimes=(byteZip.length/packetLen);// 发包次数
		
		if(byteZip.length%packetLen>0)
		{
			sendTimes++;
		}		
		
		try 
		{
			s = new Socket();
			InetSocketAddress isa=new InetSocketAddress(ip, port);
			s.connect(isa, connTime*1000);//设置连接超时
			s.setSoTimeout(soTime*1000);//设置超时时间1分钟,add by ssli 2008-06-23
			out = s.getOutputStream();

			r = new DataInputStream(s.getInputStream());
			
			int startLen=0;//截取压缩包开始字节数
			int endLen=0; //截取压缩包结束字节数
			int zipLen=0;//每次发送压缩包的字节数
			//多包传输
			for(int i=1;i<=sendTimes;i++)
			{								
				if(i==sendTimes)
				{
					endLen=byteZip.length;
					zipLen=endLen-startLen;
					head.append(String.format("%04d",  zipLen+ 8)); // 数据长度
					head.append("T"); // 报文结束
				}
				else
				{
					zipLen=packetLen;
					endLen+=zipLen;	
					
					head.append(String.format("%04d", zipLen + 8)); // 数据长度
					head.append("N"); // 报文未结束
				}
				
				//log.info("startLen:"+startLen);
				//log.info("endLen:"+endLen);
				
				head.append(vzipType); // 压缩方式
				//head.append("00"); // 保留位
				head.append(messageEncrypt?"1":"0"); // 加密标志  1  加密  0 未加密
				head.append("0"); // 保留位
				
				log.info("v端报文头:"+head.toString());
				
				byte[] byteSend = new byte[8 + zipLen];
				
				System.arraycopy(head.toString().getBytes(), 0, byteSend, 0, 8);		
				System.arraycopy(byteZip, startLen, byteSend, 8, zipLen);
				
				out.write(byteSend);
				out.flush();
				startLen=endLen;
				head.delete(0, head.capacity());//清除StringBuffer中的内容
			}


			byte[] dataByte = new byte[100 * 1024];
			int count = 0;
			String zipFlag = "0";
			String encrytFlag = "0";
			while (true) 
			{
				byte[] packSection = new byte[10];
				byte[] dataSection = new byte[10 * 1024];
				int byteRead = 0;
				// 读信息段数据
				byteRead = r.read(packSection, 0, 8);
				
				if (byteRead == -1) 
				{
					retBody.append("从SOCKECT中未读取任何字符");
					return false;
				}

				// 获得数据长度
				int realLength = 0;
				try 
				{
					log.info("C端返回报文头="+new String(packSection, 0, 8));
					realLength = Integer.parseInt(new String(packSection, 0, 4));	
				} 
				catch (NumberFormatException e) 
				{
					e.printStackTrace();
					retBody.append("长度转换错误");
					return false;
				}
				// 读数据段数据
				byteRead = r.read(dataSection, 0, realLength-8);
				log.info("C端返回原始报文内容="+new String(dataSection, 0, realLength-8));
				
				if (byteRead == -1) 
				{
					retBody.append("读取消息正文内容出错");
					return false;
				}
//				StringBuffer sb=null;
//				System.out.println("count="+count+"-----------------------------------------------------");
//				System.out.print("包内容");
//				for (int i=0;i<realLength-8;i++){
//					System.out.print(dataSection[count+i]);
//				}
//				System.out.println("");
//				System.out.println("dataSection="+new String(dataSection));
////				log.info("包内容dataSection="+sb.toString());
//				System.out.println("count="+count+"-----------------------------------------------------");
				System.arraycopy(dataSection, 0, dataByte, count,realLength - 8);
				
				count += (realLength - 8);
				
				// 判断是否有后续包.T标示无后续包
				String continueFlag = new String(packSection, 4, 1);
				
				zipFlag = new String(packSection, 5, 1);
				//获取加密标志
				encrytFlag = new String(packSection, 6, 1);
				
				if (continueFlag.equalsIgnoreCase("T")) 
				{
					break;
				} else {
					continue; 
				}
			}
			
			// 解压缩
			byte[] beforeUnzip = new byte[count];	
			
			System.arraycopy(dataByte, 0, beforeUnzip, 0, count);
			
		
			byte[] read = null;
			byte[] result = null;
			try 
			{
//				if(zipFlag.equals("0"))
//				{
//					read = beforeUnzip;
//				}
//				else if(zipFlag.equals("2"))
//				{
//					read = ZipUtil.unzip(beforeUnzip);
//				}
//				else if(zipFlag.equals("3")){
//					read = ZipUtil.ungzip(beforeUnzip);
//				}else{
//					
//				}
				switch (Integer.valueOf(zipFlag)) {
				case 0://非压缩方式
					read = beforeUnzip;
					break;
//				case 1://1： LZARI压缩方式
//					break;
				case 2://2： ZIP压缩方式
					read = ZipUtil.unzip(beforeUnzip);
					break;
				case 3://3： GZIP压缩方式
					read = ZipUtil.ungzip(beforeUnzip);
					break;
				default:
					retBody.append("压缩方式错误");
				    return false;
				} 
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				retBody.append("解压缩IO异常");
				return false;
			}
			result = read;

			if(encrytFlag.equals("1"))
			{
				result = new byte[read.length-macLength-devNoLength];
				if(!macCheck(result, read))
				{
					log.error("<<<Does not process the packet failed validation["+new String(read)+"]");
					return false;
				}
			}
//			log.info("C端返回报文："+new String(result));
			log.info("C端返回解压之后的报文：" + new String(result));
			String rcvStr = (new String(result)).trim();
			retBody.append(rcvStr);
			

		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			retBody.append("IO异常");
			return false;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			retBody.append("异常");
			return false;
		} 
		finally 
		{			
			try 
			{	
				if(r!=null)
		    	{  
			    	r.close();			    
		    	}
				
				if(out!=null)
	    		{		    	
	    			out.close();		    
	    		}	
				
				if (s != null) 
				{
					s.close();
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				retBody.append("异常");
				return false;
			}

		}
		return true;
	}
	
	/**
	 * 发送命令信息到os2客户端并接收其返回内容(采用多包传输的方式)
	 * @param sndBody 发送内容(采用定长报文格式)
	 * @param ip ip地址
	 * @param strPort 端口号
	 * @param retBody 返回内容
	 * @return true:发送成功 false:发送失败
	 * -------------------------2011-8-22 add by cy--------------------------
	 * 该方法目前没有使用，暂时不进行加解密方面的修改，待日后使用时根据需要进行修改
	 */
	public static boolean sendCmdToOs2(String sndBody, String ip, String strPort,StringBuffer retBody,int connTime,int soTime) 
	{
		
		if (sndBody == null || ip == null || strPort == null || retBody == null) 
		{
			throw new IllegalArgumentException("参数非法");
		}


		int port = Integer.parseInt(strPort);
		if (port == -1) 
		{
			retBody.append("通讯端口出错:" + strPort);
			return false;
		}
		
		byte[] byteSnd = sndBody.getBytes();
		
		
		
				
		Socket s = null;		
		DataInputStream r = null;
		OutputStream out = null;
		
		int sendTimes=(byteSnd.length/8000);// 发包次数
		
		if(byteSnd.length%8000>0)
		{
			sendTimes++;
		}		
		
		try 
		{
			s = new Socket();
			InetSocketAddress isa=new InetSocketAddress(ip, port);
			s.connect(isa,connTime*1000);//设置连接超时
			s.setSoTimeout(soTime*1000);//设置超时时间1分钟,add by ssli 2008-06-23
			log.info("sendTimes:"+sendTimes);
			out = s.getOutputStream();

			r = new DataInputStream(s.getInputStream());
			
			int startLen=0;//截取压缩包开始字节数
			int endLen=0; //截取压缩包结束字节数
			int zipLen=0;//每次发送压缩包的字节数
			//多包传输
			
			for(int i=1;i<=sendTimes;i++)
			{								
				if(i==sendTimes)
				{
					endLen=byteSnd.length;
					zipLen=endLen-startLen;
				}
				else
				{
					zipLen=8000;
					endLen+=zipLen;
				}
				
				//log.info("startLen:"+startLen);
				//log.info("endLen:"+endLen);
				

				byte[] byteSend = new byte[zipLen];
						
				System.arraycopy(byteSnd, startLen, byteSend, 0, zipLen);
				
				out.write(byteSend);
				out.flush();
				startLen=endLen;
			}


			byte[] dataSection = new byte[10 * 1024];
			int byteRead = 0;
			// 读信息段数据
			
			byteRead = r.read(dataSection);
			log.info("dataSection:"+byteRead+"|"+new String(dataSection).trim());
			if (byteRead == -1) 
			{
				retBody.append("从SOCKECT中未读取任何字符");	
			}

			String rcvStr = (new String(dataSection)).trim();
			retBody.append(rcvStr);
			log.info("retBody:" + retBody);

		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			retBody.append("IO异常");
			return false;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			retBody.append("异常");
			return false;
		} 
		finally 
		{			
			try 
			{	
				if(r!=null)
		    	{  
			    	r.close();			    
		    	}
				
				if(out!=null)
	    		{		    	
	    			out.close();		    
	    		}	
				
				if (s != null) 
				{
					s.close();
				}
			} 
			catch (Exception e) 
			{
				e.printStackTrace();
				retBody.append("异常");
				return false;
			}

		}
		return true;
	}
	
	public static final String bytesToHexString(byte[] bArray) {
	    StringBuffer sb = new StringBuffer(bArray.length);
	    String sTemp;
	    for (int i = 0; i < bArray.length; i++) {
	     sTemp = Integer.toHexString(0xFF & bArray[i]);
	     sb.append("0x");
	     if (sTemp.length() < 2)
	      sb.append(00);
//	     sb.append(sTemp.toUpperCase());
	     sb.append(sTemp);
	    }
	    return sb.toString();
	}
	/*运行管理远程操作remote端和V端的通讯socket超时-----
	序号	交易代码 	交易名称	超时时间(秒)
	1	200024	设备复位	300
	2	200027	逻辑关	300
	3	200026	逻辑开	300
	4	200028	状态检测	600
	5	200030	设备参数下载	300
	6	200031	获取功能列表	300*/
	public static int getsoTime(String cmdId) 
	{
		int soTime;//设置超时时间
		if(cmdId.equals("200024")||cmdId.equals("200027")||cmdId.equals("200026")||cmdId.equals("200030")||cmdId.equals("200031")){
			soTime=360;
		}else if(cmdId.equals("200028")){
			soTime=660;
		}else{
//			soTime=30;
			soTime=300; //根据实际情况，需要延长超时时间。 
		}
		return soTime;
	}
	
	/**
	 * 发送报文（报文头+报文体sendBody）到remote
	 * 可以进行加密
	 * @param OutputStream 输出流
	 * @param sendBody 发送内容(采用XML报文格式)
	 * @param packetLen v端每包传输大小
	 * @param vzipType v端报文压缩方式
	 * @return true:发送成功 false:发送失败
	 */
	public static Boolean sendMessage(OutputStream out,byte[] sendBody,int packetLen,int vzipType,StringBuffer retMsg, String desKey) 
	{
		if(messageEncrypt)     //是否计算MAC
		{
			try{
				byte[] mac = EncryptUtil.generateMacBytes919(sendBody, desKey);
				byte[] byteSndbyEnc = new byte[sendBody.length + 8];
				System.arraycopy(sendBody, 0, byteSndbyEnc, 0, sendBody.length);
				System.arraycopy(mac, 0, byteSndbyEnc, sendBody.length, 8);
				sendBody = byteSndbyEnc;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				return false;
			}
		}
		log.debug("v端发送报文=["+new String(sendBody)+"]");
		byte[] byteZip;
		try 
		{
			switch (vzipType) {
			case 0://非压缩方式
				byteZip = sendBody;
				break;
//			case 1://1： LZARI压缩方式
//				break;
			case 2://2： ZIP压缩方式
				byteZip = ZipUtil.zip(sendBody);
				log.debug("v端发送报文zip=["+new String(byteZip)+"]");
				log.debug("v端发送的报文unzip=["+new String(ZipUtil.unzip(byteZip))+"]");
				break;
			case 3://3： GZIP压缩方式
				byteZip = ZipUtil.gzip(sendBody);
				log.debug("v端发送的报文gzip=["+new String(byteZip)+"]");
				log.debug("v端发送的报文ungzip=["+new String(ZipUtil.ungzip(byteZip))+"]");
				break;
			default:
				retMsg.append("压缩方式错误");
			    return false;
			} 
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			retMsg.append("发送消息前压缩失败");
			return false;
		}
				
		// 组织发送报文
		StringBuffer head = new StringBuffer();
		
		int sendTimes=(byteZip.length/packetLen);// 发包次数
		
		if(byteZip.length%packetLen>0)
		{
			sendTimes++;
		}		
		
		try 
		{
			int startLen=0;//截取压缩包开始字节数
			int endLen=0; //截取压缩包结束字节数
			int zipLen=0;//每次发送压缩包的字节数
			//多包传输
			for(int i=1;i<=sendTimes;i++)
			{								
				if(i==sendTimes)
				{
					endLen=byteZip.length;
					zipLen=endLen-startLen;
					head.append(String.format("%04d",  zipLen+ 8)); // 数据长度
					head.append("T"); // 报文结束
				}
				else
				{
					zipLen=packetLen;
					endLen+=zipLen;	
					
					head.append(String.format("%04d", zipLen + 8)); // 数据长度
					head.append("N"); // 报文未结束
				}
				
				//log.info("startLen:"+startLen);
				//log.info("endLen:"+endLen);
				
				head.append(vzipType); // 压缩方式
				//head.append("00"); // 保留位
				head.append(messageEncrypt?"1":"0"); // 加密标志  1  加密  0 未加密  mod by cy   2011-8-22
				head.append("0"); // 保留位
				
				log.info("V端发送报文头head："+head);
				//log.info("head:"+head.toString());
				
				byte[] byteSend = new byte[8 + zipLen];
				
				System.arraycopy(head.toString().getBytes(), 0, byteSend, 0, 8);		
				System.arraycopy(byteZip, startLen, byteSend, 8, zipLen);
				
				out.write(byteSend);
				out.flush();
				startLen=endLen;
				head.delete(0, head.capacity());//清除StringBuffer中的内容
			}
			return true;
		}	
		catch (IOException e) 
		{
			e.printStackTrace();
			retMsg.append("IO异常");
			return false;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			retMsg.append("异常");
			return false;
		} 
	}
	
	
	/**
	 * 发送报文（报文头+报文体sendBody）到remote
	 * @param OutputStream 输出流
	 * @param sendBody 发送内容(采用XML报文格式)
	 * @param packetLen v端每包传输大小
	 * @param vzipType v端报文压缩方式
	 * @return true:发送成功 false:发送失败
	 */
	public static Boolean sendMessage(OutputStream out,byte[] sendBody,int packetLen,int vzipType,StringBuffer retMsg) {
		if(retMsg == null)
			retMsg = new StringBuffer();
		byte[] byteZip;
		try {
			switch (vzipType) {
			case 0://非压缩方式
				byteZip = sendBody;
				break;
//			case 1://1： LZARI压缩方式
//				break;
			case 2://2： ZIP压缩方式
				byteZip = ZipUtil.zip(sendBody);
				log.debug("发送报文zip=["+new String(byteZip)+"]");
				log.debug("发送的报文unzip=["+new String(ZipUtil.unzip(byteZip))+"]");
				break;
			case 3://3： GZIP压缩方式
				byteZip = ZipUtil.gzip(sendBody);
				log.debug("发送的报文gzip=["+new String(byteZip)+"]");
				log.debug("发送的报文ungzip=["+new String(ZipUtil.ungzip(byteZip))+"]");
				break;
			default:
				retMsg.append("压缩方式错误");
				return false;
			} 
		} catch (IOException e) {
			log.error("发送消息前压缩失败", e);
			retMsg.append("发送消息前压缩失败");
			return false;
		}
				
		// 组织发送报文
		StringBuffer head = new StringBuffer();
		
		int sendTimes=(byteZip.length/packetLen);// 发包次数
		
		if (byteZip.length % packetLen > 0) {
			sendTimes++;
		}		
		
		try {
			int startLen=0;//截取压缩包开始字节数
			int endLen=0; //截取压缩包结束字节数
			int zipLen=0;//每次发送压缩包的字节数
			//多包传输
			for (int i = 1; i <= sendTimes; i++) {
				if (i == sendTimes) {
					endLen=byteZip.length;
					zipLen=endLen-startLen;
					head.append(String.format("%04d",  zipLen+ 8)); // 数据长度
					head.append("T"); // 报文结束
				} else {
					zipLen=packetLen;
					endLen+=zipLen;	
					
					head.append(String.format("%04d", zipLen + 8)); // 数据长度
					head.append("N"); // 报文未结束
				}
				
				
				head.append(vzipType); // 压缩方式
				head.append("00"); // 保留位
				log.info("发送报文头head："+head);
				
				byte[] byteSend = new byte[8 + zipLen];
				
				System.arraycopy(head.toString().getBytes(), 0, byteSend, 0, 8);
				System.arraycopy(byteZip, startLen, byteSend, 8, zipLen);
				
				out.write(byteSend);
				out.flush();
				startLen=endLen;
				head.delete(0, head.capacity());//清除StringBuffer中的内容
			}
			return true;
		} catch (Exception e) {
			log.error("发送消息异常", e);
			retMsg.append("异常");
			return false;
		}
	}
	
	/**
	 * 向Client发送数据
	 * @param  sendByte 发送内容(byte数组)
	 * @param  ip ip地址
	 * @param  strPort 端口号
	 * @param  retBody 返回内容
	 * @return  true:发送成功 false:发送失败
	 * ------------------------------2011-8-22---------------------
	 * 该方法主要用于目录service/tcp/process下所有文件，该接口原定于浙江中行新增交易
	 * 目前该接口并未使用，暂时不予进行加解密的修改，如若如果使用，则根据具体情况决定
	 * 2011-8-22  add by cy
	 */
	public static boolean sendDataToClient(byte[] sendByte,DataOutputStream dos,int commPacket,int zipType) 
	{
		byte[] byteZip = null;
		try 
		{
			switch (zipType) 
			{
				case 0://非压缩方式
					byteZip = sendByte;
					break;
					//case 1://1： LZARI压缩方式
					//break;
				case 2://2： ZIP压缩方式
					byteZip = ZipUtil.zip(sendByte);
					break;
				case 3://3： GZIP压缩方式
					byteZip = ZipUtil.gzip(sendByte);
					break;
				default:
				    return false;
			} 
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			return false;
		}
		
		// 组织发送报文
		StringBuffer head = new StringBuffer();	
		int sendTimes=(byteZip.length/commPacket);// 发包次数
		if(byteZip.length%commPacket>0)
		{
			sendTimes++;
		}
		
		try 
		{		
			int startPos = 0;//截取压缩包开始字节数
			int zipLen = 0;//每次发送压缩包的字节数
			//多包传输
			for(int i=1;i<=sendTimes;i++)
			{								
				if(i==sendTimes)
				{
					zipLen = byteZip.length-startPos;
					head.append(String.format("%04d",  zipLen+8)); // 数据长度
					head.append("T"); // 报文结束
				}
				else
				{
					zipLen = commPacket;					
					head.append(String.format("%04d", zipLen + 8)); // 数据长度
					head.append("N"); // 报文未结束
				}
								
				head.append(zipType); // 压缩方式
				head.append("00"); // 保留位
				
				byte[] byteSend = new byte[8+zipLen];
				
				System.arraycopy(head.toString().getBytes(), 0, byteSend, 0, 8);		
				System.arraycopy(byteZip, startPos, byteSend, 8, zipLen);
				
				dos.write(byteSend);
				dos.flush();
				
				startPos = startPos+zipLen;
				head.delete(0, head.capacity());//清除StringBuffer中的内容
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return false;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return false;
		} 
		return true;
	}
	
	public static byte[] zipMessage(String message,int vzipType) {
		byte[] result = null;
		byte[] byteSnd = message.getBytes();
		try  {
			switch (vzipType) {
			case 0://非压缩方式
				result = byteSnd;
				break;
			case 2://2： ZIP压缩方式
				result = ZipUtil.zip(byteSnd);
				break;
			case 3://3： GZIP压缩方式
				result = ZipUtil.gzip(byteSnd);
				break;
			default:
			}
		} catch (IOException e) {
			log.info("压缩发送消息失败",e);
		}
		return result;
	}
	
	/**
	 * 发送消息到V端服务器
	 */
	public static Boolean sendMessageToServer(String sndBody,int connTime,int soTime,int packetLen,int vzipType) {
		try {
			Integer.parseInt(SystemCons.getVersionResultPort().trim());
		} catch (NumberFormatException e1) {
			log.error("端口配置错误["+SystemCons.getVersionResultPort()+"]", e1);
			return false;
		}
		log.info("RMI发送给V端服务器信息=["+new String(sndBody)+"]");
		byte[] byteZip = zipMessage(sndBody,vzipType);
		if(byteZip == null) {
			return false;
		}
		Socket s = null;
		OutputStream out = null;
		
		// 组织发送报文
		StringBuffer head = new StringBuffer();
		int sendTimes=(byteZip.length/packetLen);// 发包次数
		
		if(byteZip.length%packetLen>0) {
			sendTimes++;
		}		
		try  {
			
			s = new Socket();
			InetSocketAddress isa=new InetSocketAddress(SystemCons.getAtmvIp(), Integer.parseInt(SystemCons.getVersionResultPort().trim()));
			s.connect(isa, connTime*1000);//设置连接超时
			s.setSoTimeout(soTime*1000);//设置超时时间1分钟,add by ssli 2008-06-23
			out = s.getOutputStream();
			
			int startLen=0;//截取压缩包开始字节数
			int endLen=0; //截取压缩包结束字节数
			int zipLen=0;//每次发送压缩包的字节数
			//多包传输
			for(int i=1;i<=sendTimes;i++) {								
				if(i==sendTimes) {
					endLen=byteZip.length;
					zipLen=endLen-startLen;
					head.append(String.format("%04d",  zipLen+ 8)); // 数据长度
					head.append("T"); // 报文结束
				} else {
					zipLen=packetLen;
					endLen+=zipLen;	
					head.append(String.format("%04d", zipLen + 8)); // 数据长度
					head.append("N"); // 报文未结束
				}
				
				
				head.append(vzipType); // 压缩方式
				head.append("00"); // 保留位
				log.info("RMI发送给V端服务器消息头head："+head);

				byte[] byteSend = new byte[8 + zipLen];
				
				System.arraycopy(head.toString().getBytes(), 0, byteSend, 0, 8);		
				System.arraycopy(byteZip, startLen, byteSend, 8, zipLen);
				
				out.write(byteSend);
				out.flush();
				startLen=endLen;
				head.delete(0, head.capacity());//清除StringBuffer中的内容
			}
			return true;
		} catch (IOException e) {
			log.error("RMI发送给V端服务器信息失败",e);
			return false;
		} catch (Exception e) {
			log.error("RMI发送给V端服务器信息失败",e);
			return false;
		} finally {
			closeSocket(s);
			closeSocket(out);
		}
	}
	
	/**
	 * 发送消息到WEB服务器，用于APP和WEB分开部署
	 * add by guoyuanyuan
	 * 
	 * @param sndBody
	 * @param connTime
	 * @param soTime
	 * @param packetLen
	 * @param vzipType
	 * @return
	 */
	public static Boolean sendMessageToWebServer(String sndBody,int connTime,int soTime,int packetLen,int vzipType) {
		try {
			Integer.parseInt(SystemCons.getVersionResultPort().trim());
		} catch (NumberFormatException e1) {
			log.error("端口配置错误["+SystemCons.getVersionResultPort()+"]", e1);
			return false;
		}
		log.info("RMI发送给V端服务器信息=["+new String(sndBody)+"]");
		byte[] byteZip = zipMessage(sndBody,vzipType);
		if(byteZip == null) {
			return false;
		}
		Socket s = null;
		OutputStream out = null;
		
		// 组织发送报文
		StringBuffer head = new StringBuffer();
		int sendTimes=(byteZip.length/packetLen);// 发包次数
		
		if(byteZip.length%packetLen>0) {
			sendTimes++;
		}		
		try  {
			
			s = new Socket();
			//APP和WEB 分开部署，IP配置分离，使用web的IP地址 向web推送信息  mod by guoyuanyuan
			String serverIp = StringUtil.parseString(SystemCons.getAtmvWebIp());
			InetSocketAddress isa=new InetSocketAddress(serverIp, Integer.parseInt(SystemCons.getVersionResultPort().trim()));
			s.connect(isa, connTime*1000);//设置连接超时
			s.setSoTimeout(soTime*1000);//设置超时时间1分钟,add by ssli 2008-06-23
			out = s.getOutputStream();
			
			int startLen=0;//截取压缩包开始字节数
			int endLen=0; //截取压缩包结束字节数
			int zipLen=0;//每次发送压缩包的字节数
			//多包传输
			for(int i=1;i<=sendTimes;i++) {								
				if(i==sendTimes) {
					endLen=byteZip.length;
					zipLen=endLen-startLen;
					head.append(String.format("%04d",  zipLen+ 8)); // 数据长度
					head.append("T"); // 报文结束
				} else {
					zipLen=packetLen;
					endLen+=zipLen;	
					head.append(String.format("%04d", zipLen + 8)); // 数据长度
					head.append("N"); // 报文未结束
				}
				
				
				head.append(vzipType); // 压缩方式
				head.append("00"); // 保留位
				log.info("RMI发送给V端服务器消息头head："+head);

				byte[] byteSend = new byte[8 + zipLen];
				
				System.arraycopy(head.toString().getBytes(), 0, byteSend, 0, 8);		
				System.arraycopy(byteZip, startLen, byteSend, 8, zipLen);
				
				out.write(byteSend);
				out.flush();
				startLen=endLen;
				head.delete(0, head.capacity());//清除StringBuffer中的内容
			}
			return true;
		} catch (IOException e) {
			log.error("RMI发送给V端服务器信息失败",e);
			return false;
		} catch (Exception e) {
			log.error("RMI发送给V端服务器信息失败",e);
			return false;
		} finally {
			closeSocket(s);
			closeSocket(out);
		}
	}
		
	private static boolean macCheck(byte[] result,byte[] read)throws Exception {
		System.arraycopy(read, 0, result, 0, read.length-macLength-devNoLength);
		byte[] mac = new byte[8];
		System.arraycopy(read, read.length-macLength, mac, 0, macLength);
		byte[] devNo = new byte[32];
		System.arraycopy(read, read.length-macLength-devNoLength, devNo, 0, devNoLength);
		HashMap<String,String> hmp = DevInfoDAO.getDevDekEncodedBydevNo(StringUtil.parseString(new String(devNo)));
		String mak = hmp.get("encoded");
		byte[] macChk = EncryptUtil.generateMacBytes919(result,mak);

		return Arrays.equals(mac, macChk);
	}
	
	

	/**
	 * 关闭流及通讯连接，如果要关闭额外的流，请在方法中补充
	 */
	public static void closeSocket(Object r) {
		if(r != null) {
			try {
				if(r instanceof DataInputStream) {
					((DataInputStream) r).close();
				} else if(r instanceof DataOutputStream) {
					((DataOutputStream) r).close();
				} else if(r instanceof RandomAccessFile) {
					((RandomAccessFile) r).close();
				} else if(r instanceof Socket) {
					((Socket) r).close();
				} else {
					
				}
			} catch (IOException e) {
				log.info("关闭失败:",e);
			}
		}
	}
}