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
 * socket����ʵ����
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
	 * ��socketͨ���϶�ȡ���ݣ��������������ݽ�ѹ��
	 */
	
	public static byte[] readSocket1(DataInputStream in) throws IOException,ZipException,Exception {

		if (in == null) {
			throw new NullPointerException("readSocket�����������Ϊ��");
		}

		// ��ȡ������Ϣ��

		byte[] headSection = new byte[8];
		int charsRead;

		charsRead = in.read(headSection, 0, 8);
		log.info("C�˷��ر���ͷ��"+new String(headSection));
		if (charsRead == -1) {
			log.error("��socket��û��������");
			return null;
		}

		int realLength = 0;
		String zipFlag = "0";
		String encrytFlag = "0";
		try {
			realLength = Integer.parseInt(new String(headSection, 0, 4)) - 8;
			zipFlag = new String(headSection, 5, 1);
			//��ȡ���ܱ�־
			encrytFlag = new String(headSection, 6, 1);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			log.error("��ȡ�������ĳ��ȳ���");
			return null;
		}

		// ��ȡ�������ݶ�

		byte[] dataSection = new byte[realLength];
		int readLength = 0;
		while (readLength < realLength) {
			charsRead = in.read(dataSection, readLength, realLength - readLength);
			if (charsRead == -1 || charsRead == 0) {
				log.error("��socket��û��������");
				return null;
			}
			readLength += charsRead;
		}

		// ��ѹ��
		//return ZipUtil.unzip(dataSection);
		byte[] read = null;
		byte[] result = null;
		switch (Integer.valueOf(zipFlag)) {
		case 0://��ѹ����ʽ
			read = dataSection;
			break;
//		case 1://1�� LZARIѹ����ʽ
//			break;
		case 2://2�� ZIPѹ����ʽ
			read = ZipUtil.unzip(dataSection);
			break;
		case 3://3�� GZIPѹ����ʽ
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
		
//		log.info("C�˷��ر��ģ�"+new String(result));
		return result;
	}
   
	
	/**
	 * ��socketͨ���϶�ȡ���ݣ��������������ݽ�ѹ��
	 */
	
	public static String readSocket2(DataInputStream in) throws IOException,ZipException,Exception {
		
		if (in == null){
			throw new NullPointerException("readSocket�����������Ϊ��");
		}

		byte[] dataByte = new byte[100 * 1024];
		int count = 0;
		String zipFlag=null;
		String encrytFlag = "0";
		while (true) {
			
     		//��ȡ������Ϣ��

			byte[] headSection = new byte[8];
			int charsRead;

			charsRead = in.read(headSection, 0, 8);
			//sjxu ����ӡ����Ϣ v1.9.0
			log.debug("�յ����ݱ���ͷ��"+new String(headSection));
			zipFlag = new String(headSection, 5, 1);
			//��ȡ���ܱ�־
			encrytFlag = new String(headSection, 6, 1);
			if (charsRead == -1) {
				log.debug("��socket��û��������");
				return null;
			}

			int realLength = 0;
			try {
				realLength = Integer.parseInt(new String(headSection, 0, 4)) - 8;
			} catch (NumberFormatException e) {
				log.error("��ȡ�������ĳ��ȳ���");
				return null;
			}

			//��ȡ�������ݶ�
			
			byte[] dataSection = new byte[realLength];
			int readLength = 0;
			while (readLength < realLength) {
				charsRead = in.read(dataSection, readLength, realLength - readLength);
				if (charsRead == -1 || charsRead == 0) {
					log.error("��socket��û��������");
					return null;
				}
				readLength += charsRead;
			}
			
			System.arraycopy(dataSection, 0, dataByte, count, realLength);
			count += realLength;
			
			//�ж��Ƿ��к�������T��ʾ�޺�������
			
			String endFlag = new String(headSection, 4, 1);
			if (endFlag.equalsIgnoreCase("T")) {
				break;
			} else {
				continue;
			}
		}		
		
		byte[] beforeUnzip = new byte[count];
		System.arraycopy(dataByte, 0, beforeUnzip, 0, count);
		
		//��ѹ��

//		return new String(ZipUtil.unzip(beforeUnzip));
		byte[] read = null;
		byte[] result = null;
		switch (Integer.valueOf(zipFlag)) {
		case 0://��ѹ����ʽ
			read = beforeUnzip;
			break;
//		case 1://1�� LZARIѹ����ʽ
//			break;
		case 2://2�� ZIPѹ����ʽ
			read = ZipUtil.unzip(beforeUnzip);
			break;
		case 3://3�� GZIPѹ����ʽ
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
	 * ��socketͨ���϶�ȡ���ݣ��������������ݽ�ѹ��  ������� 2009��3��9��
	 */
	
	public static byte[] readSocket3(DataInputStream in) throws IOException,ZipException,Exception {
		
		if (in == null){
			throw new NullPointerException("readSocket�����������Ϊ��");
		}
		log.info("��ʼ�Ӱ�in["+in+"]");
		byte[] dataByte = new byte[100 * 1024];
		int count = 0;
		String zipFlag=null;
		String encrytFlag = "0";  //Ĭ��Ϊ0  ���ӽ���
		while (true) {
			
     		//��ȡ������Ϣ��
			byte[] headSection = new byte[8];
			int charsRead;
			charsRead = in.read(headSection, 0, 8);
			log.info("C�˷��ر���ͷ��"+new String(headSection));
			if (charsRead == -1) {
				log.error("��socket��û��������");
				return null;
			}
			String inhead = new String(headSection, 0, 8);
			zipFlag=new String(headSection, 5, 1);
			//��ȡ���ܱ�־
			encrytFlag = new String(headSection, 6, 1);
			log.info("��ͷin["+in+"]["+inhead+"]");
			int realLength = 0;
			try {
				realLength = Integer.parseInt(new String(headSection, 0, 4)) - 8;
			} catch (NumberFormatException e) {
				log.error("��ȡ�������ĳ��ȳ���");
				return null;
			}
			//��ȡ�������ݶ�
			
			byte[] dataSection = new byte[realLength];
			int readLength = 0;
			while (readLength < realLength) {
				charsRead = in.read(dataSection, readLength, realLength - readLength);
				if (charsRead == -1 || charsRead == 0) {
					log.error("��socket��û��������");
					return null;
				}
				readLength += charsRead;
			}
			
			System.arraycopy(dataSection, 0, dataByte, count, realLength);
			count += realLength;
			log.info("С��in["+in+"]����["+dataSection.length+"]");
			//�ж��Ƿ��к�������T��ʾ�޺�������
			
			String endFlag = new String(headSection, 4, 1);
			if (endFlag.equalsIgnoreCase("T")) {
				break;
			} else {
				continue;
			}
		}		
		
		byte[] beforeUnzip = new byte[count];
		System.arraycopy(dataByte, 0, beforeUnzip, 0, count);
		log.info("��ѹǰin["+in+"]����["+beforeUnzip.length+"]");
		//��ѹ��
//		return ZipUtil.unzip(beforeUnzip);
		byte[] read = null;
		byte[] result = null;
		switch (Integer.valueOf(zipFlag)) {
		case 0://��ѹ����ʽ
			read = beforeUnzip;
			break;
//		case 1://1�� LZARIѹ����ʽ
//			break;
		case 2://2�� ZIPѹ����ʽ
			read = ZipUtil.unzip(beforeUnzip);
			break;
		case 3://3�� GZIPѹ����ʽ
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
//		log.info("C�˷��ر��ģ�"+new String(result));
		return result;
	}
	
	/*
	 * �÷����漰���ֽ��豸���Ҹ�ͨѶ����Ŀǰû��ʹ�ã���ʱ�����мӽ��ܵ��޸�
	 * �պ���ʹ�õ��÷������پ����Ƿ��޸�     2011-8-22
	 */
	public static byte[] readSocketNocash(DataInputStream in) throws IOException,ZipException {

		if (in == null) {
			throw new NullPointerException("readSocket�����������Ϊ��");
		}

		// ��ȡ������Ϣ��

		byte[] headSection = new byte[2];
		int charsRead;

		
		charsRead = in.read(headSection, 0, 2);
		if (charsRead == -1) {
			log.error("��socket��û��������");
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
			log.error("��ȡ�������ĳ��ȳ���");
			return null;
		}
		log.info("���ĳ��ȣ�"+realLength);
		// ��ȡ�������ݶ�

		byte[] dataSection = new byte[realLength];
		int readLength = 0;
		while (readLength < realLength) {
			charsRead = in.read(dataSection, readLength, realLength - readLength);
			if (charsRead == -1 || charsRead == 0) {
				log.error("��socket��û��������");
				return null;
			}
			readLength += charsRead;
		}

		// ��ѹ��
//		log.info("C�˷��ر��ģ�"+new String(dataSection));
		return dataSection;

	}
	
	/**
	 * ��socketͨ���϶�ȡ���ݣ��������������ݽ�ѹ�����������븳��retCode
	 */

	public static byte[] readSocket3(DataInputStream in, StringBuffer retCode)throws IOException, ZipException, Exception {

		if (in == null || retCode == null){
			throw new IllegalArgumentException("readSocket�������Ϊ��");
		}
		
		byte[] dataByte = new byte[SystemCons.getDowloadBytesBuffer() * 1024];
		int count = 0;

		String zipFlag = "0";
		String encrytFlag = "0";
		while (true) {
			
			//��ȡ������Ϣ��

			byte[] headSection = new byte[8];
			int charsRead;

			charsRead = in.read(headSection, 0, 8);
			log.info("C�˷��ر���ͷ��"+new String(headSection));
			if (charsRead == -1) {
				log.error("��socket��û��������");
				return null;
			}

			int realLength = 0;
			try {
				realLength = Integer.parseInt(new String(headSection, 0, 4)) - 8;
			} catch (NumberFormatException e) {
				log.error("��ȡ�������ĳ��ȳ���");
				return null;
			}

			//��ȡ�������ݶ�
			
			byte[] dataSection = new byte[realLength];
			int readLength = 0;
			while (readLength < realLength) {
				charsRead = in.read(dataSection, readLength, realLength - readLength);
				if (charsRead == -1 || charsRead == 0) {
					log.error("��socket��û��������");
					return null;
				}
				readLength += charsRead;
			}

			System.arraycopy(dataSection, 0, dataByte, count, realLength);
			count += realLength;
			
            //�ж��Ƿ��к�������T��ʾ�޺�������
			
			String endFlag = new String(headSection, 4, 1);
			zipFlag = new String(headSection, 5, 1);
			//��ȡ���ܱ�־
			encrytFlag = new String(headSection, 6, 1);
			if (endFlag.equalsIgnoreCase("T")) {
				break;
			} else {
				continue;
			}
		}
		
		byte[] beforeUnzip = new byte[count];
		System.arraycopy(dataByte, 0, beforeUnzip, 0, count);
		
		//��ѹ��
		
		//byte[] byteUnzip = (zipFlag.equals("2"))?ZipUtil.unzip(beforeUnzip):beforeUnzip;
		
		byte[] byteUnzip = null;
		byte[] result = null;
		switch (Integer.valueOf(zipFlag)) {
		case 0://��ѹ����ʽ
			byteUnzip = beforeUnzip;
			break;
//		case 1://1�� LZARIѹ����ʽ
//			break;
		case 2://2�� ZIPѹ����ʽ
			byteUnzip = ZipUtil.unzip(beforeUnzip);
			break;
		case 3://3�� GZIPѹ����ʽ
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
//		log.info("C�˷��ر��ģ�"+new String(result));
		//ȡ������
		
		String ret = new String(result, 1, 2);
		retCode.append(ret);
		
		//ȡ����
		
		int contentLength = result.length - 4;
		byte[] contentByte = new byte[contentLength];
		System.arraycopy(result, 4, contentByte, 0, contentLength);
		return contentByte;
	}

	
	/**
	 * ����������Ϣ��RVS��������Remote�ͻ��˲������䷵������(���ö������ķ�ʽ)
	 * @param sndBody ��������(����XML���ĸ�ʽ)
	 * @param ip ip��ַ
	 * @param strPort �˿ں�
	 * @param retBody ��������
	 * @param packetLen v��ÿ�������С
	 * @param vzipType v�˱���ѹ����ʽ
	 * @return true:���ͳɹ� false:����ʧ��
	 */
	public static boolean sendCmdToRvcMutil(String sndBody, String ip, String strPort,StringBuffer retBody,int connTime,int soTime,int packetLen,int vzipType, String desKey) 
	{
		log.info("[SystemParam_messageEncrypt] "+SystemCons.isMessageEncrypt());
		log.info("[sendCmdToRvcMutilParam] "+"ip:"+ip+","+"strPort:"+strPort+","+"connTime:"+connTime+","+"soTime:"+soTime+","+"packetLen:"+packetLen+","+"vzipType:"+vzipType+","+"desKey:"+desKey);
		if (sndBody == null || ip == null || strPort == null || retBody == null) 
		{
			throw new IllegalArgumentException("�����Ƿ�");
		}

		//log.info("send to RVS:" + sndBody);

		int port = Integer.parseInt(strPort);
		if (port == -1) 
		{
			retBody.append("ͨѶ�˿ڳ���:" + strPort);
			return false;
		}
		
		log.info("v�˷��ͱ���=["+sndBody+"]");
//		log.info("v�˷��ͱ��ĳ���=["+sndBody.length()+"]");
		byte[] byteSnd = sndBody.getBytes();
		//�Ƿ����   2011-8-22  mod by cy
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
			case 0://��ѹ����ʽ
				byteZip = byteSnd;
				break;
//			case 1://1�� LZARIѹ����ʽ
//				break;
			case 2://2�� ZIPѹ����ʽ
				byteZip = ZipUtil.zip(byteSnd);
				break;
			case 3://3�� GZIPѹ����ʽ
				byteZip = ZipUtil.gzip(byteSnd);
				break;
			default:
				retBody.append("ѹ����ʽ����");
			    return false;
			} 
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			retBody.append("������Ϣǰѹ��ʧ��");
			return false;
		}
		
				
		Socket s = null;
		DataInputStream r = null;
		OutputStream out = null;
		
		
		
		// ��֯���ͱ���
		StringBuffer head = new StringBuffer();
		
		int sendTimes=(byteZip.length/packetLen);// ��������
		
		if(byteZip.length%packetLen>0)
		{
			sendTimes++;
		}		
		
		try 
		{
			s = new Socket();
			InetSocketAddress isa=new InetSocketAddress(ip, port);
			s.connect(isa, connTime*1000);//�������ӳ�ʱ
			s.setSoTimeout(soTime*1000);//���ó�ʱʱ��1����,add by ssli 2008-06-23
			out = s.getOutputStream();

			r = new DataInputStream(s.getInputStream());
			
			int startLen=0;//��ȡѹ������ʼ�ֽ���
			int endLen=0; //��ȡѹ���������ֽ���
			int zipLen=0;//ÿ�η���ѹ�������ֽ���
			//�������
			for(int i=1;i<=sendTimes;i++)
			{								
				if(i==sendTimes)
				{
					endLen=byteZip.length;
					zipLen=endLen-startLen;
					head.append(String.format("%04d",  zipLen+ 8)); // ���ݳ���
					head.append("T"); // ���Ľ���
				}
				else
				{
					zipLen=packetLen;
					endLen+=zipLen;	
					
					head.append(String.format("%04d", zipLen + 8)); // ���ݳ���
					head.append("N"); // ����δ����
				}
				
				//log.info("startLen:"+startLen);
				//log.info("endLen:"+endLen);
				
				head.append(vzipType); // ѹ����ʽ
				//head.append("00"); // ����λ
				head.append(messageEncrypt?"1":"0"); // ���ܱ�־  1  ����  0 δ����
				head.append("0"); // ����λ
				
				log.info("v�˱���ͷ:"+head.toString());
				
				byte[] byteSend = new byte[8 + zipLen];
				
				System.arraycopy(head.toString().getBytes(), 0, byteSend, 0, 8);		
				System.arraycopy(byteZip, startLen, byteSend, 8, zipLen);
				
				out.write(byteSend);
				out.flush();
				startLen=endLen;
				head.delete(0, head.capacity());//���StringBuffer�е�����
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
				// ����Ϣ������
				byteRead = r.read(packSection, 0, 8);
				
				if (byteRead == -1) 
				{
					retBody.append("��SOCKECT��δ��ȡ�κ��ַ�");
					return false;
				}

				// ������ݳ���
				int realLength = 0;
				try 
				{
					log.info("C�˷��ر���ͷ="+new String(packSection, 0, 8));
					realLength = Integer.parseInt(new String(packSection, 0, 4));	
				} 
				catch (NumberFormatException e) 
				{
					e.printStackTrace();
					retBody.append("����ת������");
					return false;
				}
				// �����ݶ�����
				byteRead = r.read(dataSection, 0, realLength-8);
				log.info("C�˷���ԭʼ��������="+new String(dataSection, 0, realLength-8));
				
				if (byteRead == -1) 
				{
					retBody.append("��ȡ��Ϣ�������ݳ���");
					return false;
				}
//				StringBuffer sb=null;
//				System.out.println("count="+count+"-----------------------------------------------------");
//				System.out.print("������");
//				for (int i=0;i<realLength-8;i++){
//					System.out.print(dataSection[count+i]);
//				}
//				System.out.println("");
//				System.out.println("dataSection="+new String(dataSection));
////				log.info("������dataSection="+sb.toString());
//				System.out.println("count="+count+"-----------------------------------------------------");
				System.arraycopy(dataSection, 0, dataByte, count,realLength - 8);
				
				count += (realLength - 8);
				
				// �ж��Ƿ��к�����.T��ʾ�޺�����
				String continueFlag = new String(packSection, 4, 1);
				
				zipFlag = new String(packSection, 5, 1);
				//��ȡ���ܱ�־
				encrytFlag = new String(packSection, 6, 1);
				
				if (continueFlag.equalsIgnoreCase("T")) 
				{
					break;
				} else {
					continue; 
				}
			}
			
			// ��ѹ��
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
				case 0://��ѹ����ʽ
					read = beforeUnzip;
					break;
//				case 1://1�� LZARIѹ����ʽ
//					break;
				case 2://2�� ZIPѹ����ʽ
					read = ZipUtil.unzip(beforeUnzip);
					break;
				case 3://3�� GZIPѹ����ʽ
					read = ZipUtil.ungzip(beforeUnzip);
					break;
				default:
					retBody.append("ѹ����ʽ����");
				    return false;
				} 
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				retBody.append("��ѹ��IO�쳣");
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
//			log.info("C�˷��ر��ģ�"+new String(result));
			log.info("C�˷��ؽ�ѹ֮��ı��ģ�" + new String(result));
			String rcvStr = (new String(result)).trim();
			retBody.append(rcvStr);
			

		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			retBody.append("IO�쳣");
			return false;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			retBody.append("�쳣");
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
				retBody.append("�쳣");
				return false;
			}

		}
		return true;
	}
	
	/**
	 * ����������Ϣ��os2�ͻ��˲������䷵������(���ö������ķ�ʽ)
	 * @param sndBody ��������(���ö������ĸ�ʽ)
	 * @param ip ip��ַ
	 * @param strPort �˿ں�
	 * @param retBody ��������
	 * @return true:���ͳɹ� false:����ʧ��
	 * -------------------------2011-8-22 add by cy--------------------------
	 * �÷���Ŀǰû��ʹ�ã���ʱ�����мӽ��ܷ�����޸ģ����պ�ʹ��ʱ������Ҫ�����޸�
	 */
	public static boolean sendCmdToOs2(String sndBody, String ip, String strPort,StringBuffer retBody,int connTime,int soTime) 
	{
		
		if (sndBody == null || ip == null || strPort == null || retBody == null) 
		{
			throw new IllegalArgumentException("�����Ƿ�");
		}


		int port = Integer.parseInt(strPort);
		if (port == -1) 
		{
			retBody.append("ͨѶ�˿ڳ���:" + strPort);
			return false;
		}
		
		byte[] byteSnd = sndBody.getBytes();
		
		
		
				
		Socket s = null;		
		DataInputStream r = null;
		OutputStream out = null;
		
		int sendTimes=(byteSnd.length/8000);// ��������
		
		if(byteSnd.length%8000>0)
		{
			sendTimes++;
		}		
		
		try 
		{
			s = new Socket();
			InetSocketAddress isa=new InetSocketAddress(ip, port);
			s.connect(isa,connTime*1000);//�������ӳ�ʱ
			s.setSoTimeout(soTime*1000);//���ó�ʱʱ��1����,add by ssli 2008-06-23
			log.info("sendTimes:"+sendTimes);
			out = s.getOutputStream();

			r = new DataInputStream(s.getInputStream());
			
			int startLen=0;//��ȡѹ������ʼ�ֽ���
			int endLen=0; //��ȡѹ���������ֽ���
			int zipLen=0;//ÿ�η���ѹ�������ֽ���
			//�������
			
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
			// ����Ϣ������
			
			byteRead = r.read(dataSection);
			log.info("dataSection:"+byteRead+"|"+new String(dataSection).trim());
			if (byteRead == -1) 
			{
				retBody.append("��SOCKECT��δ��ȡ�κ��ַ�");	
			}

			String rcvStr = (new String(dataSection)).trim();
			retBody.append(rcvStr);
			log.info("retBody:" + retBody);

		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			retBody.append("IO�쳣");
			return false;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			retBody.append("�쳣");
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
				retBody.append("�쳣");
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
	/*���й���Զ�̲���remote�˺�V�˵�ͨѶsocket��ʱ-----
	���	���״��� 	��������	��ʱʱ��(��)
	1	200024	�豸��λ	300
	2	200027	�߼���	300
	3	200026	�߼���	300
	4	200028	״̬���	600
	5	200030	�豸��������	300
	6	200031	��ȡ�����б�	300*/
	public static int getsoTime(String cmdId) 
	{
		int soTime;//���ó�ʱʱ��
		if(cmdId.equals("200024")||cmdId.equals("200027")||cmdId.equals("200026")||cmdId.equals("200030")||cmdId.equals("200031")){
			soTime=360;
		}else if(cmdId.equals("200028")){
			soTime=660;
		}else{
//			soTime=30;
			soTime=300; //����ʵ���������Ҫ�ӳ���ʱʱ�䡣 
		}
		return soTime;
	}
	
	/**
	 * ���ͱ��ģ�����ͷ+������sendBody����remote
	 * ���Խ��м���
	 * @param OutputStream �����
	 * @param sendBody ��������(����XML���ĸ�ʽ)
	 * @param packetLen v��ÿ�������С
	 * @param vzipType v�˱���ѹ����ʽ
	 * @return true:���ͳɹ� false:����ʧ��
	 */
	public static Boolean sendMessage(OutputStream out,byte[] sendBody,int packetLen,int vzipType,StringBuffer retMsg, String desKey) 
	{
		if(messageEncrypt)     //�Ƿ����MAC
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
		log.debug("v�˷��ͱ���=["+new String(sendBody)+"]");
		byte[] byteZip;
		try 
		{
			switch (vzipType) {
			case 0://��ѹ����ʽ
				byteZip = sendBody;
				break;
//			case 1://1�� LZARIѹ����ʽ
//				break;
			case 2://2�� ZIPѹ����ʽ
				byteZip = ZipUtil.zip(sendBody);
				log.debug("v�˷��ͱ���zip=["+new String(byteZip)+"]");
				log.debug("v�˷��͵ı���unzip=["+new String(ZipUtil.unzip(byteZip))+"]");
				break;
			case 3://3�� GZIPѹ����ʽ
				byteZip = ZipUtil.gzip(sendBody);
				log.debug("v�˷��͵ı���gzip=["+new String(byteZip)+"]");
				log.debug("v�˷��͵ı���ungzip=["+new String(ZipUtil.ungzip(byteZip))+"]");
				break;
			default:
				retMsg.append("ѹ����ʽ����");
			    return false;
			} 
		}
		catch (IOException e) 
		{
			e.printStackTrace();
			retMsg.append("������Ϣǰѹ��ʧ��");
			return false;
		}
				
		// ��֯���ͱ���
		StringBuffer head = new StringBuffer();
		
		int sendTimes=(byteZip.length/packetLen);// ��������
		
		if(byteZip.length%packetLen>0)
		{
			sendTimes++;
		}		
		
		try 
		{
			int startLen=0;//��ȡѹ������ʼ�ֽ���
			int endLen=0; //��ȡѹ���������ֽ���
			int zipLen=0;//ÿ�η���ѹ�������ֽ���
			//�������
			for(int i=1;i<=sendTimes;i++)
			{								
				if(i==sendTimes)
				{
					endLen=byteZip.length;
					zipLen=endLen-startLen;
					head.append(String.format("%04d",  zipLen+ 8)); // ���ݳ���
					head.append("T"); // ���Ľ���
				}
				else
				{
					zipLen=packetLen;
					endLen+=zipLen;	
					
					head.append(String.format("%04d", zipLen + 8)); // ���ݳ���
					head.append("N"); // ����δ����
				}
				
				//log.info("startLen:"+startLen);
				//log.info("endLen:"+endLen);
				
				head.append(vzipType); // ѹ����ʽ
				//head.append("00"); // ����λ
				head.append(messageEncrypt?"1":"0"); // ���ܱ�־  1  ����  0 δ����  mod by cy   2011-8-22
				head.append("0"); // ����λ
				
				log.info("V�˷��ͱ���ͷhead��"+head);
				//log.info("head:"+head.toString());
				
				byte[] byteSend = new byte[8 + zipLen];
				
				System.arraycopy(head.toString().getBytes(), 0, byteSend, 0, 8);		
				System.arraycopy(byteZip, startLen, byteSend, 8, zipLen);
				
				out.write(byteSend);
				out.flush();
				startLen=endLen;
				head.delete(0, head.capacity());//���StringBuffer�е�����
			}
			return true;
		}	
		catch (IOException e) 
		{
			e.printStackTrace();
			retMsg.append("IO�쳣");
			return false;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			retMsg.append("�쳣");
			return false;
		} 
	}
	
	
	/**
	 * ���ͱ��ģ�����ͷ+������sendBody����remote
	 * @param OutputStream �����
	 * @param sendBody ��������(����XML���ĸ�ʽ)
	 * @param packetLen v��ÿ�������С
	 * @param vzipType v�˱���ѹ����ʽ
	 * @return true:���ͳɹ� false:����ʧ��
	 */
	public static Boolean sendMessage(OutputStream out,byte[] sendBody,int packetLen,int vzipType,StringBuffer retMsg) {
		if(retMsg == null)
			retMsg = new StringBuffer();
		byte[] byteZip;
		try {
			switch (vzipType) {
			case 0://��ѹ����ʽ
				byteZip = sendBody;
				break;
//			case 1://1�� LZARIѹ����ʽ
//				break;
			case 2://2�� ZIPѹ����ʽ
				byteZip = ZipUtil.zip(sendBody);
				log.debug("���ͱ���zip=["+new String(byteZip)+"]");
				log.debug("���͵ı���unzip=["+new String(ZipUtil.unzip(byteZip))+"]");
				break;
			case 3://3�� GZIPѹ����ʽ
				byteZip = ZipUtil.gzip(sendBody);
				log.debug("���͵ı���gzip=["+new String(byteZip)+"]");
				log.debug("���͵ı���ungzip=["+new String(ZipUtil.ungzip(byteZip))+"]");
				break;
			default:
				retMsg.append("ѹ����ʽ����");
				return false;
			} 
		} catch (IOException e) {
			log.error("������Ϣǰѹ��ʧ��", e);
			retMsg.append("������Ϣǰѹ��ʧ��");
			return false;
		}
				
		// ��֯���ͱ���
		StringBuffer head = new StringBuffer();
		
		int sendTimes=(byteZip.length/packetLen);// ��������
		
		if (byteZip.length % packetLen > 0) {
			sendTimes++;
		}		
		
		try {
			int startLen=0;//��ȡѹ������ʼ�ֽ���
			int endLen=0; //��ȡѹ���������ֽ���
			int zipLen=0;//ÿ�η���ѹ�������ֽ���
			//�������
			for (int i = 1; i <= sendTimes; i++) {
				if (i == sendTimes) {
					endLen=byteZip.length;
					zipLen=endLen-startLen;
					head.append(String.format("%04d",  zipLen+ 8)); // ���ݳ���
					head.append("T"); // ���Ľ���
				} else {
					zipLen=packetLen;
					endLen+=zipLen;	
					
					head.append(String.format("%04d", zipLen + 8)); // ���ݳ���
					head.append("N"); // ����δ����
				}
				
				
				head.append(vzipType); // ѹ����ʽ
				head.append("00"); // ����λ
				log.info("���ͱ���ͷhead��"+head);
				
				byte[] byteSend = new byte[8 + zipLen];
				
				System.arraycopy(head.toString().getBytes(), 0, byteSend, 0, 8);
				System.arraycopy(byteZip, startLen, byteSend, 8, zipLen);
				
				out.write(byteSend);
				out.flush();
				startLen=endLen;
				head.delete(0, head.capacity());//���StringBuffer�е�����
			}
			return true;
		} catch (Exception e) {
			log.error("������Ϣ�쳣", e);
			retMsg.append("�쳣");
			return false;
		}
	}
	
	/**
	 * ��Client��������
	 * @param  sendByte ��������(byte����)
	 * @param  ip ip��ַ
	 * @param  strPort �˿ں�
	 * @param  retBody ��������
	 * @return  true:���ͳɹ� false:����ʧ��
	 * ------------------------------2011-8-22---------------------
	 * �÷�����Ҫ����Ŀ¼service/tcp/process�������ļ����ýӿ�ԭ�����㽭������������
	 * Ŀǰ�ýӿڲ�δʹ�ã���ʱ������мӽ��ܵ��޸ģ��������ʹ�ã�����ݾ����������
	 * 2011-8-22  add by cy
	 */
	public static boolean sendDataToClient(byte[] sendByte,DataOutputStream dos,int commPacket,int zipType) 
	{
		byte[] byteZip = null;
		try 
		{
			switch (zipType) 
			{
				case 0://��ѹ����ʽ
					byteZip = sendByte;
					break;
					//case 1://1�� LZARIѹ����ʽ
					//break;
				case 2://2�� ZIPѹ����ʽ
					byteZip = ZipUtil.zip(sendByte);
					break;
				case 3://3�� GZIPѹ����ʽ
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
		
		// ��֯���ͱ���
		StringBuffer head = new StringBuffer();	
		int sendTimes=(byteZip.length/commPacket);// ��������
		if(byteZip.length%commPacket>0)
		{
			sendTimes++;
		}
		
		try 
		{		
			int startPos = 0;//��ȡѹ������ʼ�ֽ���
			int zipLen = 0;//ÿ�η���ѹ�������ֽ���
			//�������
			for(int i=1;i<=sendTimes;i++)
			{								
				if(i==sendTimes)
				{
					zipLen = byteZip.length-startPos;
					head.append(String.format("%04d",  zipLen+8)); // ���ݳ���
					head.append("T"); // ���Ľ���
				}
				else
				{
					zipLen = commPacket;					
					head.append(String.format("%04d", zipLen + 8)); // ���ݳ���
					head.append("N"); // ����δ����
				}
								
				head.append(zipType); // ѹ����ʽ
				head.append("00"); // ����λ
				
				byte[] byteSend = new byte[8+zipLen];
				
				System.arraycopy(head.toString().getBytes(), 0, byteSend, 0, 8);		
				System.arraycopy(byteZip, startPos, byteSend, 8, zipLen);
				
				dos.write(byteSend);
				dos.flush();
				
				startPos = startPos+zipLen;
				head.delete(0, head.capacity());//���StringBuffer�е�����
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
			case 0://��ѹ����ʽ
				result = byteSnd;
				break;
			case 2://2�� ZIPѹ����ʽ
				result = ZipUtil.zip(byteSnd);
				break;
			case 3://3�� GZIPѹ����ʽ
				result = ZipUtil.gzip(byteSnd);
				break;
			default:
			}
		} catch (IOException e) {
			log.info("ѹ��������Ϣʧ��",e);
		}
		return result;
	}
	
	/**
	 * ������Ϣ��V�˷�����
	 */
	public static Boolean sendMessageToServer(String sndBody,int connTime,int soTime,int packetLen,int vzipType) {
		try {
			Integer.parseInt(SystemCons.getVersionResultPort().trim());
		} catch (NumberFormatException e1) {
			log.error("�˿����ô���["+SystemCons.getVersionResultPort()+"]", e1);
			return false;
		}
		log.info("RMI���͸�V�˷�������Ϣ=["+new String(sndBody)+"]");
		byte[] byteZip = zipMessage(sndBody,vzipType);
		if(byteZip == null) {
			return false;
		}
		Socket s = null;
		OutputStream out = null;
		
		// ��֯���ͱ���
		StringBuffer head = new StringBuffer();
		int sendTimes=(byteZip.length/packetLen);// ��������
		
		if(byteZip.length%packetLen>0) {
			sendTimes++;
		}		
		try  {
			
			s = new Socket();
			InetSocketAddress isa=new InetSocketAddress(SystemCons.getAtmvIp(), Integer.parseInt(SystemCons.getVersionResultPort().trim()));
			s.connect(isa, connTime*1000);//�������ӳ�ʱ
			s.setSoTimeout(soTime*1000);//���ó�ʱʱ��1����,add by ssli 2008-06-23
			out = s.getOutputStream();
			
			int startLen=0;//��ȡѹ������ʼ�ֽ���
			int endLen=0; //��ȡѹ���������ֽ���
			int zipLen=0;//ÿ�η���ѹ�������ֽ���
			//�������
			for(int i=1;i<=sendTimes;i++) {								
				if(i==sendTimes) {
					endLen=byteZip.length;
					zipLen=endLen-startLen;
					head.append(String.format("%04d",  zipLen+ 8)); // ���ݳ���
					head.append("T"); // ���Ľ���
				} else {
					zipLen=packetLen;
					endLen+=zipLen;	
					head.append(String.format("%04d", zipLen + 8)); // ���ݳ���
					head.append("N"); // ����δ����
				}
				
				
				head.append(vzipType); // ѹ����ʽ
				head.append("00"); // ����λ
				log.info("RMI���͸�V�˷�������Ϣͷhead��"+head);

				byte[] byteSend = new byte[8 + zipLen];
				
				System.arraycopy(head.toString().getBytes(), 0, byteSend, 0, 8);		
				System.arraycopy(byteZip, startLen, byteSend, 8, zipLen);
				
				out.write(byteSend);
				out.flush();
				startLen=endLen;
				head.delete(0, head.capacity());//���StringBuffer�е�����
			}
			return true;
		} catch (IOException e) {
			log.error("RMI���͸�V�˷�������Ϣʧ��",e);
			return false;
		} catch (Exception e) {
			log.error("RMI���͸�V�˷�������Ϣʧ��",e);
			return false;
		} finally {
			closeSocket(s);
			closeSocket(out);
		}
	}
	
	/**
	 * ������Ϣ��WEB������������APP��WEB�ֿ�����
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
			log.error("�˿����ô���["+SystemCons.getVersionResultPort()+"]", e1);
			return false;
		}
		log.info("RMI���͸�V�˷�������Ϣ=["+new String(sndBody)+"]");
		byte[] byteZip = zipMessage(sndBody,vzipType);
		if(byteZip == null) {
			return false;
		}
		Socket s = null;
		OutputStream out = null;
		
		// ��֯���ͱ���
		StringBuffer head = new StringBuffer();
		int sendTimes=(byteZip.length/packetLen);// ��������
		
		if(byteZip.length%packetLen>0) {
			sendTimes++;
		}		
		try  {
			
			s = new Socket();
			//APP��WEB �ֿ�����IP���÷��룬ʹ��web��IP��ַ ��web������Ϣ  mod by guoyuanyuan
			String serverIp = StringUtil.parseString(SystemCons.getAtmvWebIp());
			InetSocketAddress isa=new InetSocketAddress(serverIp, Integer.parseInt(SystemCons.getVersionResultPort().trim()));
			s.connect(isa, connTime*1000);//�������ӳ�ʱ
			s.setSoTimeout(soTime*1000);//���ó�ʱʱ��1����,add by ssli 2008-06-23
			out = s.getOutputStream();
			
			int startLen=0;//��ȡѹ������ʼ�ֽ���
			int endLen=0; //��ȡѹ���������ֽ���
			int zipLen=0;//ÿ�η���ѹ�������ֽ���
			//�������
			for(int i=1;i<=sendTimes;i++) {								
				if(i==sendTimes) {
					endLen=byteZip.length;
					zipLen=endLen-startLen;
					head.append(String.format("%04d",  zipLen+ 8)); // ���ݳ���
					head.append("T"); // ���Ľ���
				} else {
					zipLen=packetLen;
					endLen+=zipLen;	
					head.append(String.format("%04d", zipLen + 8)); // ���ݳ���
					head.append("N"); // ����δ����
				}
				
				
				head.append(vzipType); // ѹ����ʽ
				head.append("00"); // ����λ
				log.info("RMI���͸�V�˷�������Ϣͷhead��"+head);

				byte[] byteSend = new byte[8 + zipLen];
				
				System.arraycopy(head.toString().getBytes(), 0, byteSend, 0, 8);		
				System.arraycopy(byteZip, startLen, byteSend, 8, zipLen);
				
				out.write(byteSend);
				out.flush();
				startLen=endLen;
				head.delete(0, head.capacity());//���StringBuffer�е�����
			}
			return true;
		} catch (IOException e) {
			log.error("RMI���͸�V�˷�������Ϣʧ��",e);
			return false;
		} catch (Exception e) {
			log.error("RMI���͸�V�˷�������Ϣʧ��",e);
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
	 * �ر�����ͨѶ���ӣ����Ҫ�رն�����������ڷ����в���
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
				log.info("�ر�ʧ��:",e);
			}
		}
	}
}