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
 * @author ykliu ������������RMI������
 */
public class RmiShepherdServer  {

	public static String Log4jPath = "";
	public static String Shepherd_Path = "";
	
	/**
	 * ��ʼ��RMI����,����RMI����󶨵�����
	 */
	public void startRMI() {
		if (System.getSecurityManager() != null) {
			System.setSecurityManager(new RMISecurityManager());
		}
		try {
			RmiShepherdServerUtil rssu  = new RmiShepherdServerUtilImpl();
			// ���ö���ʵ��������FstRmiUtil��
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
	public static void main(String[] args) { //ZIJINMOD ��ȫ�� 2013-12-25 �޸������ļ���·��
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
			//System.out.println("file������������!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
