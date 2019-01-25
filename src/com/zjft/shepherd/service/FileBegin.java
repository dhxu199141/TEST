package com.zjft.shepherd.service;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zjft.shepherd.common.SystemCons;

/**
 * file�����߳�
 * 
 * ���̼߳����ͻ����ӣ����пͻ����ӣ������ͻ����Ӵ����߳�
 * 
 * @author hpshen
 * @since 2007.04.10
 */

public class FileBegin implements Runnable {

	private static Log log = LogFactory.getLog(FileBegin.class);

	public void run() {
		
		int port;
		String portStr = SystemCons.getLocalFilePort();

		try {
			port = Integer.parseInt(portStr);
		} catch (NumberFormatException e) {
			log.error("��ȡ�ļ��˿��ļ�ʧ��" + portStr);
			return;
		}
		
		ServerSocket serverSocket = null;
		
		try{
			serverSocket=new ServerSocket(); 
			serverSocket.setReuseAddress(true);
			serverSocket.bind(new InetSocketAddress(port));  
			log.info("file service is runing on port:" + port);
		} catch (Exception e) {
			log.error("�����ļ�����ʧ�� " + e);
			return;
		}
		
		while (true) {
			try{
				Socket client = serverSocket.accept();
				Thread thread = new Thread(new FileClient(client));
				thread.start();
			} catch (Exception e) {
				if (serverSocket.isClosed()) {
					log.error("�������ѹر�");
					break;
				} else {
					log.error("create a socket connection occur exception: "+ e);
					continue;
				}
			}
		}
	}
}