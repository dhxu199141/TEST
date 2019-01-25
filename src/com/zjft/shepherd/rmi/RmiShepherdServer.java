/**
 * Created On 2009-03-18 By ykliu
 */
package com.zjft.shepherd.rmi;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.registry.LocateRegistry;

import com.zjft.shepherd.common.Log4JInit;
import com.zjft.shepherd.common.SystemCons;
import com.zjft.shepherd.service.FileTask;

/**
 * @author ykliu 此类用于启动RMI服务器
 */
public class RmiShepherdServer  {

	public static String Log4jPath = "";
	public static String Shepherd_Path = "";
	
	/**
	 * 初始化RMI对象,并将RMI对象绑定到名称
	 */
	public void startRMI() {
		if (System.getSecurityManager() != null) {
			System.setSecurityManager(new RMISecurityManager());
		}
		try {
			RmiShepherdServerUtil rssu  = new RmiShepherdServerUtilImpl();
			// 将该对象实例与名称FstRmiUtil绑定
			LocateRegistry.createRegistry(Integer.valueOf(SystemCons.getRmiport()));
			Naming.bind("rmi://"+SystemCons.getRmiip()+":"+SystemCons.getRmiport()+"/"+SystemCons.getRmiserver(),rssu);
		} catch (Exception e) {
			System.err.println("RMI services start Failure:");
			e.printStackTrace();
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) { //ZIJINMOD 徐全发 2013-12-25 修改配置文件的路径
		try {
			if(args.length != 2) {
				RmiShepherdServer.Log4jPath = "";
				RmiShepherdServer.Shepherd_Path = "./shepherd.properties";
			} else {
				RmiShepherdServer.Log4jPath = args[0];
				RmiShepherdServer.Shepherd_Path = args[1];
			}
			
			System.out.println("Log4jPath: ["+RmiShepherdServer.Log4jPath+"]");
			System.out.println("Shepherd_Path: ["+RmiShepherdServer.Shepherd_Path+"]");
			
			Log4JInit.init("");
			RmiShepherdServer server = new RmiShepherdServer();
			server.startRMI();
			FileTask aFileTask = new FileTask();
			aFileTask.run();
			System.out.println("RMI service is running!");
			//System.out.println("file服务启动结束!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
