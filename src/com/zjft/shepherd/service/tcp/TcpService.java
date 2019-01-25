package com.zjft.shepherd.service.tcp;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class TcpService implements Runnable
{	
	private static Log log = LogFactory.getLog(TcpService.class);
	
	private ServerSocket serverSocket = null;
	private ThreadPoolExecutor executor = null;
    private TcpDispatcher tcpDispatcher = null;
	
	public TcpService(TcpDispatcher tcpDispatcher) 
	{
		this.tcpDispatcher = tcpDispatcher;
	}

	public void run()
	{	
		
		try
		{				
			serverSocket = new ServerSocket(50009);
    		Socket s = null;
    		try
    		{
    			executor = new ThreadPoolExecutor(10, 40, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(500), new ThreadPoolExecutor.AbortPolicy());
    		}
    		catch (Exception e)
    		{
    			executor = new ThreadPoolExecutor(10, 40, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(500), new ThreadPoolExecutor.AbortPolicy());
    		}
    		while(true)
    		{						
    			try
    			{
    				if(serverSocket == null || serverSocket.isClosed())
    				{
    					break;
    				}
    			    s = serverSocket.accept();   			    
    			    if(s != null)
    			    {		    
    			    	s.setSoTimeout(60 * 1000);
    			    	TcpTransfer tcpTransfer = new TcpTransfer(s, tcpDispatcher);			    	
    			    	executor.execute(tcpTransfer);			    	
    			    }
    			}
    			catch(Exception e)
    			{
    				log.info("processing socket exception:" + e.getStackTrace());
    				if(s != null)
    				{
    					try 
    					{
							s.close();
						} 
    					catch (Exception e1) 
    					{
							
						}
    				}
    			}
    		}
		}
		catch(Exception e)
		{
			log.info("rvs command service port error!"+e.getMessage());
		}	
	}	
	
}

