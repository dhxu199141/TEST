package com.zjft.shepherd.service;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zjft.shepherd.common.SystemCons;

/**
 * file服务线程
 * 
 * 该线程监听客户连接，若有客户连接，则开启客户连接处理线程
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
			log.error("获取文件端口文件失败" + portStr);
			return;
		}
		
		ServerSocket serverSocket = null;
		
		try{
			serverSocket=new ServerSocket(); 
			serverSocket.setReuseAddress(true);
			serverSocket.bind(new InetSocketAddress(port));  
			log.info("file service is runing on port:" + port);
		} catch (Exception e) {
			log.error("启动文件服务失败 " + e);
			return;
		}
		
		while (true) {
			try{
				Socket client = serverSocket.accept();
				Thread thread = new Thread(new FileClient(client));
				thread.start();
			} catch (Exception e) {
				if (serverSocket.isClosed()) {
					log.error("服务器已关闭");
					break;
				} else {
					log.error("create a socket connection occur exception: "+ e);
					continue;
				}
			}
		}
	}
}