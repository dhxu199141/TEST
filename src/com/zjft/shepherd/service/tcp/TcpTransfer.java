package com.zjft.shepherd.service.tcp;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.zip.ZipException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;

import com.zjft.shepherd.common.DocumentUtil;
import com.zjft.shepherd.common.SocketUtil;
import com.zjft.shepherd.service.tcp.message.CommonMessage;

public class TcpTransfer implements Runnable 
{
	private static Log log = LogFactory.getLog(TcpTransfer.class);
	
	private Socket socket = null;
	private TcpDispatcher tcpDispatcher = null;
	
	public TcpTransfer(Socket socket, TcpDispatcher tcpDispatcher)
	{
		this.socket = socket;
		this.tcpDispatcher = tcpDispatcher;
	}
	
	public void run() 
	{
		if(socket == null || socket.isClosed())
		{
			log.error("接收SOCKECT通信端口被关闭!");
			return;
		}
		try 
		{
			String xml = SocketUtil.readSocket2(new DataInputStream(socket.getInputStream()));
			if(xml == null || xml.equals(""))
			{
				return;
			}
			log.info("receive:"+xml) ;
			//System.out.println("receive:" + xml);
			Document doc = DocumentUtil.convertTextToDOM(xml);
			CommonMessage message = new CommonMessage(doc);
			tcpDispatcher.dispatch(message, socket);
		} 
		catch(ZipException e) 
		{
			e.printStackTrace();
		} 
		catch(IOException e) 
		{
			e.printStackTrace();
		}
		catch(Exception e) 
		{
			e.printStackTrace();
		}
		finally
		{
			if(socket != null)
			{
				try 
				{
					socket.close();
				} 
				catch (IOException e) 
				{
					e.printStackTrace();
				}
			}
		}
	}
}
