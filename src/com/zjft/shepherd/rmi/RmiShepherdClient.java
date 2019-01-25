/**
 * Created On 2009-03-18 By ykliu
 */
package com.zjft.shepherd.rmi;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author ykliu 此类用于调用RMI服务器上服务
 */
public class RmiShepherdClient {
	private static Log log = LogFactory.getLog(RmiShepherdClient.class);

	public void invokeRMI(String IpAddress, String PortNumber,
			String ServiceName) {
		try {
			// System.out.println("开始调用RMI服务...");
			log.info("开始调用RMI服务...") ;
			RmiShepherdServerUtil rssu = (RmiShepherdServerUtil) Naming.lookup("rmi://" + IpAddress + ":" + PortNumber + "/"+ ServiceName);
			log.info("调用RMI服务完毕!") ;
			// System.out.println("调用RMI服务完毕!");
		} catch (NotBoundException ne) {
			// System.err.println("调用RMI服务发生 NotBoundException 异常,详细信息如下:");
			log.error("调用RMI服务发生 NotBoundException 异常,详细信息如下:"+ne) ;
			//ne.printStackTrace();
		} catch (MalformedURLException mue) {
			// System.err.println("调用RMI服务发生 MalformedURLException 异常,详细信息如下:");
			log.error("调用RMI服务发生 MalformedURLException 异常,详细信息如下:"+mue) ;
			//mue.printStackTrace();
		} catch (RemoteException re) {
			// System.err.println("调用RMI服务发生 RemoteException 异常,详细信息如下:");
			log.error("调用RMI服务发生 RemoteException 异常,详细信息如下:"+re) ;
//			re.printStackTrace();
		}
	}

	/**
	 * @param args
	 *            调用RMI服务的参数,详细入下 
	 *            args[0]:RMI服务器IP地址 
	 *            args[1]:RMI服务端口
	 *            args[2]:RMI服务名称
	 */
	public static void main(String[] args) {
		RmiShepherdClient client = new RmiShepherdClient();
		if (args.length != 3) {
			log.error("---------------------------");
			client.invokeRMI("127.0.0.1", "1777", "rssu");
			log.error("-----------------------------");
		} else {
			log.info("调用RMI服务传入的参数为：RMI服务器IP地址："+args[0]+",RMI服务器端口："+args[1]+",RMI服务名称："+args[2]) ;
			client.invokeRMI(args[0], args[1], args[2]);
		}
	}
}
