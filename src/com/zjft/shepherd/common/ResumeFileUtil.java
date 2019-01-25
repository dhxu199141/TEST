package com.zjft.shepherd.common;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zjft.shepherd.dao.DevInfoDAO;
import com.zjft.shepherd.dao.RemoteControlDAO;
import com.zjft.shepherd.dao.RemoteFileDownInfo;

public final class ResumeFileUtil {

	private static Log log=LogFactory.getLog(ResumeFileUtil .class);	

	/**
	 * �����ϴ��ļ���������������ļ������¿�ʼ����������
	 * */
	public static String resumeFileTrans(String ip, String strPort,
			String locFile, String remoteFile, String remoteIp,
			String terminalNo, int type, int connTime, int soTime,
			int packetLen, int vzipType, RemoteFileDownInfo downInfo) {
		
		if (ip==null||strPort==null||locFile==null||remoteFile==null||remoteIp==null||terminalNo==null) {
			return "ϵͳ��������";			
		}
		
		File outFile=new File(locFile);
		return fileResume(ip,strPort,locFile,remoteFile,remoteIp,terminalNo,type,connTime,soTime,packetLen,vzipType,outFile.length(),downInfo);
	}
	
	/**
	 * ��Զ�̷�������ͻ���ȡ�ļ������أ��ϵ�������
	 **/
	public static String fileResume(String ip, String strPort, String locFile,
			String remoteFile, String remoteIp, String terminalNo, int type,
			int connTime, int soTime, int packetLen, int vzipType,long startPos, RemoteFileDownInfo downInfo) {
		Socket s = null;
		DataInputStream r = null;
		DataOutputStream w = null;
		RandomAccessFile raFile = null;
		int port;
		Boolean IsPushMessage =Boolean.valueOf(SystemCons.getIsPushMessage()) ;  //���� ���ͱ�־λ
		try {
			port = Integer.parseInt(strPort);
		} catch (Exception e) {			
			e.printStackTrace();
			return "�������˿ڴ���,strPort:" + strPort;			
		}
		
		Double progress = 0.00;
		
		try {
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
								.append("</root>").toString();

			raFile=new RandomAccessFile(locFile,"rw");;			
			w.write(StringUtil.preProcess(firstSend.getBytes(),vzipType));
			w.flush();
			StringBuffer ret0 = new StringBuffer();
			
			byte[] b = SocketUtil.readSocket3(r, ret0);
			
			if (b == null) {			
				return "�����ļ�ʧ��";				
			}
			
			if (!ret0.toString().equals("00")) {
				return "�����ļ�ʧ�ܣ�ʧ��ԭ��:" + tranCode(ret0.toString());				
			}			
			
			int maxfileLength=0;
			
			try {
				
				String [] messageArray=(new String(b,0,b.length)).split("\\|");		
				maxfileLength = Integer.parseInt(messageArray[4].replace(" ", ""));
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			long position = startPos;

			String transRate = DevInfoDAO.getTransRate(terminalNo);
			/*�����ļ�*/
			int i=0; //���ʹ���������ԭ�������֮һ
			for (;;) {
				StringBuffer secondSend = new StringBuffer();
				secondSend.append("<?xml version=\"1.0\" encoding=\"GB2312\" ?>")
						.append("<root><cmdid>0102</cmdid>")
						.append("<startpos>").append(position).append("</startpos>")
						.append("</root>");				
				
				StringBuffer ret = null;
				byte[] readByte = null;
				long startTime = System.currentTimeMillis();//�����ļ�ÿ���Ŀ�ʼʱ��
				try {
					w.write(StringUtil.preProcess(secondSend.toString().getBytes(), vzipType));
					w.flush();
					ret = new StringBuffer();
					readByte = SocketUtil.readSocket3(r, ret);
				} catch (IOException e) {
					return "��ʱ";
				}
				
				if (readByte == null) {
					return "�����ļ�ʧ�ܣ�ԭ��:��ѹ��ʧ��";
				}
				
				if (ret.toString().equals("21") && (position <= maxfileLength)) {
					
					if (fileWrite(raFile, readByte, position, readByte.length) == -1) {
						return "д�����ļ�����";
					}
					position += readByte.length;
					progress = Math.round(position/(double)maxfileLength * 100) / 100.00; //�ļ����ؽ���
					if(downInfo != null) {
						downInfo.setProgress(progress);
					}
					
					if(IsPushMessage){ //������ͱ�־λΪfalse��������
						i++;
							if(i%5==0){
								SocketUtil.sendMessageToWebServer(downInfo.organizeXML(),20,60,8000,0);	
							}
							if(i%5!=0 && position == maxfileLength){
								SocketUtil.sendMessageToWebServer(downInfo.organizeXML(),20,60,8000,0);	
							}							
					}else{
						if(position == maxfileLength){
							SocketUtil.sendMessageToWebServer(downInfo.organizeXML(),20,60,8000,0);	
						}
					}
					limitTransRate(startTime,transRate,readByte.length);//�ٶ�����
				} else if (ret.toString().equals("04") || ret.toString().equals("20")) {
					break;
				} else if (ret.toString().equals("01")) {					
					return "�����ļ�ʧ�ܣ���ʧ��ԭ��:" + tranCode(ret.toString());					
				} else {
					break;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return "����ʧ�ܣ�"+e.getMessage();
		} catch (Exception e) {			
			e.printStackTrace();
			return "����ʧ�ܣ�"+e.getMessage();
			
		} finally {
			if(downInfo != null) {
				RemoteControlDAO.updateRemoteFileDownPropress(downInfo.getLogicId(), progress); //�����ļ����ؽ���
			}
			SocketUtil.closeSocket(r);
			SocketUtil.closeSocket(w);
			SocketUtil.closeSocket(s);
			SocketUtil.closeSocket(raFile);
		}
		
		return "0000";
	}
	
	/**
	 * �����ļ����䷵�ش���
	 * */
	public static String tranCode(String retCode) {
		if(retCode.equals("01")) return "���ļ�ʧ��";
		if(retCode.equals("02")) return "��λ���������ļ���ָ�����ļ�λ��ʧ��";
		if(retCode.equals("04")) return "���������ļ������ټ�����ȡ���ݣ��ļ��Ѿ�����";
		if(retCode.equals("30")) return "���Ľ�������ָ����XML�ֶ����������в�����";
		if(retCode.equals("20")) return "���������ļ����ͽ���";
		if(retCode.equals("21")) return "���ĵķ�������Ϊ�ļ����ݣ����ݵĳ���Ϊ(���ĳ���-8)";
		if(retCode.equals("00")) return "���������ļ�׼�����";
		if(retCode.equals("05")) return "�ļ��ѱ��������ļ�������,��ȡ�ļ���Ϣʧ��";
		if(retCode.equals("06")) return "��������";
		return retCode;
	}

	/**
	 * �Ӷϵ㿪ʼд�ļ���дʧ�ܷ���-1�����򷵻�д��ĳ���
	 * */
	public static synchronized int fileWrite(RandomAccessFile oSavedFile,byte[] b,long startPos,int nLen) {
		int n = -1;
		try {
			oSavedFile.seek(startPos);//��λ�ϵ�
			log.info("startPos="+startPos);
			log.info("nLen="+nLen);
			log.info("bSize="+b.length);			
			oSavedFile.write(b,0,nLen);
			n = nLen;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return n;
	}
	
	
	/**
	 * �ļ������ٶ�����
	 * @param startTime ��ʼʱ��
	 * @param transRate �ļ������ٶȣ�Ϊ��ʱ������
	 * @param length �ļ�����С
	 * @since 2014-09-10
	 * @author cqluo
	 */
	private static void limitTransRate(long startTime, String transRate, int length) {
		if(transRate == null)
			return;
		double needTime = length * 1000/ (double)(Integer.parseInt(transRate) * 1024);
		double costTime = (System.currentTimeMillis() - startTime);
		if(costTime >= needTime)
			return;
		try {
			Thread.sleep((long) (needTime - costTime));
		} catch (InterruptedException e) {
		}
	}
}