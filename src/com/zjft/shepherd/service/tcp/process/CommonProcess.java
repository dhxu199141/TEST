package com.zjft.shepherd.service.tcp.process;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import com.zjft.shepherd.common.SocketUtil;

public abstract class CommonProcess 
{
	public boolean sendDataToClient(Socket socket, String xml)
	{
		if(socket==null||socket.isClosed())
		{
			return false;
		}
		try 
		{
			return SocketUtil.sendDataToClient(xml.getBytes(), new DataOutputStream(socket.getOutputStream()), 8000, 0);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			return false;
		}
	}
}
