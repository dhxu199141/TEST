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
	 * 根据上次文件传输情况，决定文件是重新开始传还是续传
	 * */
	public static String resumeFileTrans(String ip, String strPort,
			String locFile, String remoteFile, String remoteIp,
			String terminalNo, int type, int connTime, int soTime,
			int packetLen, int vzipType, RemoteFileDownInfo downInfo) {
		
		if (ip==null||strPort==null||locFile==null||remoteFile==null||remoteIp==null||terminalNo==null) {
			return "系统参数错误！";			
		}
		
		File outFile=new File(locFile);
		return fileResume(ip,strPort,locFile,remoteFile,remoteIp,terminalNo,type,connTime,soTime,packetLen,vzipType,outFile.length(),downInfo);
	}
	
	/**
	 * 从远程服务器或客户端取文件到本地（断点续传）
	 **/
	public static String fileResume(String ip, String strPort, String locFile,
			String remoteFile, String remoteIp, String terminalNo, int type,
			int connTime, int soTime, int packetLen, int vzipType,long startPos, RemoteFileDownInfo downInfo) {
		Socket s = null;
		DataInputStream r = null;
		DataOutputStream w = null;
		RandomAccessFile raFile = null;
		int port;
		Boolean IsPushMessage =Boolean.valueOf(SystemCons.getIsPushMessage()) ;  //增加 推送标志位
		try {
			port = Integer.parseInt(strPort);
		} catch (Exception e) {			
			e.printStackTrace();
			return "服务器端口错误,strPort:" + strPort;			
		}
		
		Double progress = 0.00;
		
		try {
			s = new Socket();
			InetSocketAddress isa = new InetSocketAddress(ip, port);
			s.connect(isa, connTime*1000);
			s.setSoTimeout(soTime*1000);
			w = new DataOutputStream(s.getOutputStream());
			r = new DataInputStream(s.getInputStream());
			
			/*文件请求*/
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
				return "接收文件失败";				
			}
			
			if (!ret0.toString().equals("00")) {
				return "接收文件失败，失败原因:" + tranCode(ret0.toString());				
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
			/*接受文件*/
			int i=0; //推送次数减少至原来的五分之一
			for (;;) {
				StringBuffer secondSend = new StringBuffer();
				secondSend.append("<?xml version=\"1.0\" encoding=\"GB2312\" ?>")
						.append("<root><cmdid>0102</cmdid>")
						.append("<startpos>").append(position).append("</startpos>")
						.append("</root>");				
				
				StringBuffer ret = null;
				byte[] readByte = null;
				long startTime = System.currentTimeMillis();//传输文件每包的开始时间
				try {
					w.write(StringUtil.preProcess(secondSend.toString().getBytes(), vzipType));
					w.flush();
					ret = new StringBuffer();
					readByte = SocketUtil.readSocket3(r, ret);
				} catch (IOException e) {
					return "超时";
				}
				
				if (readByte == null) {
					return "接收文件失败，原因:解压缩失败";
				}
				
				if (ret.toString().equals("21") && (position <= maxfileLength)) {
					
					if (fileWrite(raFile, readByte, position, readByte.length) == -1) {
						return "写本地文件错误";
					}
					position += readByte.length;
					progress = Math.round(position/(double)maxfileLength * 100) / 100.00; //文件下载进度
					if(downInfo != null) {
						downInfo.setProgress(progress);
					}
					
					if(IsPushMessage){ //如果推送标志位为false，则不推送
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
					limitTransRate(startTime,transRate,readByte.length);//速度限制
				} else if (ret.toString().equals("04") || ret.toString().equals("20")) {
					break;
				} else if (ret.toString().equals("01")) {					
					return "接收文件失败，，失败原因:" + tranCode(ret.toString());					
				} else {
					break;
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return "操作失败："+e.getMessage();
		} catch (Exception e) {			
			e.printStackTrace();
			return "操作失败："+e.getMessage();
			
		} finally {
			if(downInfo != null) {
				RemoteControlDAO.updateRemoteFileDownPropress(downInfo.getLogicId(), progress); //更新文件下载进度
			}
			SocketUtil.closeSocket(r);
			SocketUtil.closeSocket(w);
			SocketUtil.closeSocket(s);
			SocketUtil.closeSocket(raFile);
		}
		
		return "0000";
	}
	
	/**
	 * 解析文件传输返回代码
	 * */
	public static String tranCode(String retCode) {
		if(retCode.equals("01")) return "打开文件失败";
		if(retCode.equals("02")) return "定位服务器端文件到指定的文件位置失败";
		if(retCode.equals("04")) return "服务器端文件不能再继续读取数据，文件已经结束";
		if(retCode.equals("30")) return "报文解析错误，指定的XML字段在请求报文中不存在";
		if(retCode.equals("20")) return "服务器端文件发送结束";
		if(retCode.equals("21")) return "报文的返回数据为文件数据，数据的长度为(报文长度-8)";
		if(retCode.equals("00")) return "服务器端文件准备完毕";
		if(retCode.equals("05")) return "文件已被更名或文件不存在,获取文件信息失败";
		if(retCode.equals("06")) return "其他错误";
		return retCode;
	}

	/**
	 * 从断点开始写文件，写失败返回-1，否则返回写入的长度
	 * */
	public static synchronized int fileWrite(RandomAccessFile oSavedFile,byte[] b,long startPos,int nLen) {
		int n = -1;
		try {
			oSavedFile.seek(startPos);//定位断点
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
	 * 文件传输速度限制
	 * @param startTime 开始时间
	 * @param transRate 文件传输速度，为空时不限速
	 * @param length 文件包大小
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