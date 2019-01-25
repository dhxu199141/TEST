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
 * @author ykliu �������ڵ���RMI�������Ϸ���
 */
public class RmiShepherdClient {
	private static Log log = LogFactory.getLog(RmiShepherdClient.class);

	public void invokeRMI(String IpAddress, String PortNumber,
			String ServiceName) {
		try {
			// System.out.println("��ʼ����RMI����...");
			log.info("��ʼ����RMI����...") ;
			RmiShepherdServerUtil rssu = (RmiShepherdServerUtil) Naming.lookup("rmi://" + IpAddress + ":" + PortNumber + "/"+ ServiceName);
			log.info("����RMI�������!") ;
			// System.out.println("����RMI�������!");
		} catch (NotBoundException ne) {
			// System.err.println("����RMI������ NotBoundException �쳣,��ϸ��Ϣ����:");
			log.error("����RMI������ NotBoundException �쳣,��ϸ��Ϣ����:"+ne) ;
			//ne.printStackTrace();
		} catch (MalformedURLException mue) {
			// System.err.println("����RMI������ MalformedURLException �쳣,��ϸ��Ϣ����:");
			log.error("����RMI������ MalformedURLException �쳣,��ϸ��Ϣ����:"+mue) ;
			//mue.printStackTrace();
		} catch (RemoteException re) {
			// System.err.println("����RMI������ RemoteException �쳣,��ϸ��Ϣ����:");
			log.error("����RMI������ RemoteException �쳣,��ϸ��Ϣ����:"+re) ;
//			re.printStackTrace();
		}
	}

	/**
	 * @param args
	 *            ����RMI����Ĳ���,��ϸ���� 
	 *            args[0]:RMI������IP��ַ 
	 *            args[1]:RMI����˿�
	 *            args[2]:RMI��������
	 */
	public static void main(String[] args) {
		RmiShepherdClient client = new RmiShepherdClient();
		if (args.length != 3) {
			log.error("---------------------------");
			client.invokeRMI("127.0.0.1", "1777", "rssu");
			log.error("-----------------------------");
		} else {
			log.info("����RMI������Ĳ���Ϊ��RMI������IP��ַ��"+args[0]+",RMI�������˿ڣ�"+args[1]+",RMI�������ƣ�"+args[2]) ;
			client.invokeRMI(args[0], args[1], args[2]);
		}
	}
}
