/**
 * Created On 2009-03-18 By ykliu
 */

package com.zjft.shepherd.rmi;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.zjft.shepherd.business.control.CheckJounalService;
import com.zjft.shepherd.business.control.ControlService;
import com.zjft.shepherd.service.DepositIdFactory;
import com.zjft.shepherd.service.DepositIdService;

/**
* @since 2009-03-19
* @author ykliu ������RmiShepherdServerUtil�ӿڵ�ʵ��
*/
public class RmiShepherdServerUtilImpl  extends UnicastRemoteObject implements RmiShepherdServerUtil {

	private static final long serialVersionUID = 6158084897536450551L;
	
	private static Log log = LogFactory.getLog(RmiShepherdServerUtilImpl.class);
	
	ControlService controlService = new ControlService();
	
	CheckJounalService checkJounalService = new CheckJounalService();
	
	private DepositIdService depositIdService;
	
	protected RmiShepherdServerUtilImpl() throws RemoteException {
		super();
		setDepositIdService(DepositIdFactory.builtDepositIdService());
	}
	
	public DepositIdService getDepositIdService() {
		return depositIdService;
	}

	public void setDepositIdService(DepositIdService depositIdService) {
		this.depositIdService = depositIdService;
	}

	/**
	* �ڷ������Ͻ��ղ���,�����Ľ��������
	* 
	* @param pHashMap
	* @return rHashMap
	* @throws RemoteException
	*/
	public Map<String, Object> viewDocFile(Map<String, Object> pHashMap) throws RemoteException {
		try {
			return controlService.viewDocFile(pHashMap);
		} catch (Exception e) {
			log.info("Զ��������̻��ļ���ʧ�ܣ�ʧ��ԭ��:",e) ;
			return null;
		}
	}
	
	public Map<String, Object> downloadFile(Map<String, Object> pHashMap) throws RemoteException {
		try {
			return controlService.downloadFile(pHashMap);
		} catch (Exception e) {
			log.info("�����Զ�������ļ�ʧ�ܣ�ʧ��ԭ��:",e) ;
			return null;
		}
	}
	
	public Map<String, Object> downloadClientFile(Map<String, Object> pHashMap) throws RemoteException {
		try {
			return controlService.downloadClientFile(pHashMap);
		} catch (Exception e) {
			log.info("�����Զ�������ļ�ʧ�ܣ�ʧ��ԭ��:",e) ;
			return null;
		}
	}
	
	public Map<String, Object> uploadFile(Map<String, Object> pHashMap) throws RemoteException {
		try {
			return controlService.uploadFile(pHashMap);
		} catch (Exception e) {
			log.info("������ϴ��ļ�ʧ�ܣ�ʧ��ԭ��:",e) ;
			return null;
		}
	}

	public Map<String, Object> delFile(Map<String, Object> pHashMap) throws RemoteException {
		try {
			return controlService.delFile(pHashMap);
		} catch (Exception e) {
			log.info("������ɾ��Զ���ļ�ʧ�ܣ�ʧ��ԭ��:", e);
			return null;
		}
	}

	public Map<String, Object> excuteFile(Map<String, Object> pHashMap) throws RemoteException {
		try {
			return controlService.excuteFile(pHashMap);
		} catch (Exception e) {
			log.info("������ִ��Զ���ļ�ʧ�ܣ�ʧ��ԭ��:", e);
			return null;
		}
	}

	public Map<String, Object> remoteControl(Map<String, Object> pHashMap) throws RemoteException {
		try {
			return controlService.remoteControl(pHashMap);
		} catch (Exception e) {
			log.info("������ִ�п�������ʧ�ܣ�ʧ��ԭ��:", e);
			return null;
		}
	}

	public Map<String, Object> createOrUpdateAtmFunction(Map<String, Object> pHashMap)
			throws RemoteException {
		try {
			return controlService.createOrUpdateAtmFunction(pHashMap);
		} catch (Exception e) {
			return null;
		}
	}

	public Map<String, Object> createOrUpdateAtmLog(Map<String, Object> pHashMap)
			throws RemoteException {
		try {
			return controlService.createOrUpdateAtmLog(pHashMap);
		} catch (Exception e) {
			return null;
		}
	}


	public Map<String, Object> getAtmParam(Map<String, Object> pHashMap) throws RemoteException {
		try {
			return controlService.getAtmParam(pHashMap);
		} catch (Exception e) {
			return null;
		}
	}
	
	public Map<String, Object> getCompositeCardStatus(Map<String, Object> pHashMap) throws RemoteException {
		try {
			return controlService.getAtmParam(pHashMap);
		} catch (Exception e) {
			return null;
		}
	}

	public Map<String, Object> getCashboxInfo(Map<String, Object> pHashMap) throws RemoteException {
		try {
			return controlService.getCashboxInfo(pHashMap);
		} catch (Exception e) {
			return null;
		}
	}

	public Map<String, Object> setCashboxInfo(Map<String, Object> pHashMap) throws RemoteException {
		try {
			return controlService.setCashboxInfo(pHashMap);
		} catch (Exception e) {
			return null;
		}
	}

	public Map<String, Object> createOrUpdateCompositeCardStatus(Map<String, Object> pHashMap)
			throws RemoteException {
		try {
			return controlService.createOrUpdateCompositeCardStatus(pHashMap);
		} catch (Exception e) {
			return null;
		}
	}

	public Map<String, Object> qryDepositId(Map<String, Object> paraMap) throws RemoteException {
		return this.getDepositIdService().qryDepositId(paraMap);
	}

	public Map<String, Object> setDepositId(Map<String, Object> paraMap) throws RemoteException {
		return this.getDepositIdService().setDepositId(paraMap);
	}


	public Map<String, Object> qryCashBoxSift(Map<String, Object> paraMap)
			throws RemoteException {
		// TODO Auto-generated method stub
		try {
			return controlService.qryCashBoxSift(paraMap);
		} catch (Exception e) {
			log.info("�Զ�����ʧ�ܣ�ʧ��ԭ��:", e);
			return null;
		}
	}
	
	/**
	 * ��ȡ�豸����ֽ����ˮ��Ϣ
	 */
	public Map<String, Object> getCJournalProperties(Map<String, Object> pHashMap) throws RemoteException{
		try {
			return checkJounalService.checkJounal(pHashMap);
		} catch (Exception e) {
			log.info("��ȡ�豸����ˮ�ļ���Ϣʧ�ܣ�ʧ��ԭ��:", e);
			return null;
		}

	}

	
} 
