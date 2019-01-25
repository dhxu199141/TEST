package com.zjft.shepherd.service;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;

import com.zjft.shepherd.common.CalendarUtil;
import com.zjft.shepherd.common.DbProxoolUtil;
import com.zjft.shepherd.common.FileUtil;
import com.zjft.shepherd.common.SocketUtil;
import com.zjft.shepherd.common.StringUtil;
import com.zjft.shepherd.common.SystemCons;
import com.zjft.shepherd.common.XmlUtil;
import com.zjft.shepherd.dao.DevInfoDAO;
import com.zjft.shepherd.process.MessageParse;
/**
 * file客户连接处理线程
 * 
 * 处理文件传输
 * @author hpshen
 * @since 2007.04.10
 */

public class FileClient implements Runnable {
	
	private static Log log = LogFactory.getLog(FileClient.class);
	private Socket socket = null;
	
	public FileClient(Socket socket) {
		this.socket = socket;
	}

	public void run() {
		
		DataInputStream inStream = null;		
		OutputStream outStream = null;
		RandomAccessFile file = null;
		
		try {
			inStream = new DataInputStream(socket.getInputStream());
			outStream = socket.getOutputStream();

			String line = SocketUtil.readSocket2(inStream);
			//sjxu 不打印空信息 v1.9.0
			log.debug("收到报文:["+line+"]");
			if (line == null) {
				log.debug("没有接收到任何内容");
				return;
			}
			
			/*接受文件传输请求*/
			Document document = XmlUtil.createDocument(line);
			if (document == null) {
				log.error("接收到的内容不能被解析为xml格式");
				String send = "|30|";
				SocketUtil.sendMessage(outStream, send.getBytes(), 8000, 0,null);
				return;
			}
			String devNo = MessageParse.getSingleNodeValue(document,"//root/localinfo/termno");
			String hostTermNo = MessageParse.getSingleNodeValue(document,"//root/hostinfo/termno");
			int packetLen = 2000;
			int vzipType = 0;
			//如果设备向服务端进行文件请求，则采用数据库中该台设备设置的传输大小值
			if(!StringUtil.isNullorEmpty(devNo)) {
				try {
					List<Object> aList = DbProxoolUtil.query("select t.comm_packet,t.zip_type from dev_base_info t where t.NO='" + devNo + "'", 0);
					if (aList==null||aList.size()==0) {
						return;
					}
					List temp = (List) aList.get(0);
					packetLen = Integer.parseInt(temp.get(0).toString());
					vzipType = Integer.parseInt(temp.get(1).toString());
				} catch (Exception e) {
					log.debug("取得通讯每包传输大小和压缩方式失败，采用默认值");
				}
			}
			String filename = MessageParse.getSingleNodeValue(document,"//root/filename");
			if (filename == null) {
				log.error("要下载的文件名不存在,请检查发送报文");
				String send = "|30|";
				SocketUtil.sendMessage(outStream, send.getBytes(), packetLen, vzipType, null);
				return;
			}

			File locFile = new File(filename);
			if (!locFile.exists()) {
				log.error("文件不存在");
				String send = "|05|";
				StringBuffer retMsg=null;
				SocketUtil.sendMessage(outStream, send.getBytes(), packetLen, vzipType, retMsg);
				return;
			}
			
			String fileAttribute = FileUtil.getFileAttr(locFile);
			long length = locFile.length();
			String time = CalendarUtil.getYMDHMS(locFile.lastModified());
			
			
			StringBuffer fileProp = new StringBuffer();
			fileProp.append(fileAttribute).append("|").append(time).append("|").append(time).append("|").append(time).append("|").append(length);
			SocketUtil.sendMessage(outStream, ("|00|" + fileProp).getBytes(), packetLen, vzipType, null);
			
			file = new RandomAccessFile(filename, "r");
			
			String transRate = DevInfoDAO.getTransRate(devNo);
			
			/*开始传输文件*/
			while (true) {
				line = SocketUtil.readSocket2(inStream);
				//sjxu 不打印空信息 v1.9.0
				log.debug("收到报文:["+line+"]");
				if (line == null) {
					log.error("没有接收到任何内容");
					return;
				}
				
				document = XmlUtil.createDocument(line);
				if (document == null) {
					log.error("接收到的内容不能被解析为xml格式");
					SocketUtil.sendMessage(outStream, "|30|".getBytes(), packetLen, vzipType, null);
				}
				
				String startpos = MessageParse.getSingleNodeValue(document,"//root/startpos");
				int pos = 0;
				if (startpos != null) {
					try {
						pos = Integer.parseInt(startpos);
					} catch (NumberFormatException e) {
						log.error("数据格式转换失败");
						return;
					}
				} else {
					log.error("startpos为null");
					return;
				}

				try {
					file.seek(pos);
				} catch (IOException e) {
					log.error("文件位置指定不对");
					SocketUtil.sendMessage(outStream, "|02|".getBytes(), packetLen, vzipType, null);
					return;
				}
				
				byte[] bytesBuffer = new byte[SystemCons.getUploadBytesBuffer()*1024];//创建Socket传输块(socket缓存为8192)
				if("notdevice".equals(hostTermNo)) {
					bytesBuffer = new byte[1024*1024];
					packetLen = 9*1024;
				}
				int bytes_read = 0;
				long startTime = 0;//传输文件每包时的开始时间
				if ((bytes_read = file.read(bytesBuffer)) != -1) {
					byte[] sendBytes = new byte[bytes_read + 4];
					System.arraycopy("|21|".getBytes(), 0, sendBytes, 0, 4);
					System.arraycopy(bytesBuffer, 0, sendBytes, 4, bytes_read);
					startTime = System.currentTimeMillis();
					SocketUtil.sendMessage(outStream, sendBytes, packetLen, vzipType, null);
					limitTransRate(startTime,transRate,sendBytes.length);//速度限制
				} else {
					SocketUtil.sendMessage(outStream, "|20|".getBytes(), packetLen, vzipType, null);
					break;
				}
			}
		} catch (Exception e) {
			log.error("下载文件失败",e);
		} finally {
			try {
				if (inStream != null) {
					inStream.close();
				}
				if (outStream != null) {
					outStream.close();
				}
				if (file != null) {
					file.close();
				}
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				inStream = null;
				outStream = null;
				file = null;
				socket = null;
			}
		}
	}
	
	/**
	 * 文件传输速度限制
	 * @param startTime 开始时间
	 * @param transRate 文件传输速度，为空时不限速
	 * @param length 文件包大小
	 * @author cqluo
	 */
	private void limitTransRate(long startTime, String transRate, int length) {
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