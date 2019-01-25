/**
 * Created On 2009-03-18 By ykliu
 */
package com.zjft.shepherd.rmi;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;


/**
 * @since 2009-03-19
 * @author ykliu �˽ӿ����ڶ��屾�غ�Զ�̷������ϳ���Ĺ����ӿ�
 */
public interface RmiShepherdServerUtil extends Remote {
	/**
	 * ������̻��ļ���
	 * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 *  key:�豸�� devInfo
	 *      �ļ�·�� viewpath
	 *      �ļ��� docName
	 *      �ϼ�Ŀ¼���� upDoc
	 *      �ļ�Ŀ¼ dirPath
	 *      Remoteҵ������˿� remotePort
	 *      Remote�ļ������˿� remoteFilePort
	 *      ��ǰ�û� user;
	 * @return HashMap
	 * key:documentFileList
		   rHashMap.put("retCode", 1);
		   rHashMap.put("viewpath", viewpath);
	 **/
	Map<String, Object> viewDocFile(Map<String, Object> pHashMap) throws RemoteException;
	
	/**
	 * ����ATM�ϵ��ļ�������Ա��
	 * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 * key���豸�� devInfo;
	 *  �ļ�·�� viewpath;
	 *  �ļ��� fileName;
	 *  fileBean
	 *  �ļ�Ŀ¼ versionFilePath
	 *  Remote�ļ������˿� remoteFilePort
	 *  ��ǰ�û� user;
	 * @return rHashMap
	 *  key�������� retCode 0��1;
	 *  ������Ϣ retMsg;
	 * */
	Map<String, Object> downloadFile(Map<String, Object> pHashMap) throws RemoteException;
	
	/**
	 * ���ض���ͻ����ļ�������Ա��
	 * @author limengrd
	 * @since 2016.09.07
	 * @param pHashMap 
	 *	key:
	 *		Remote�ļ������˿� remoteFilePort
	 *		��ǰ�û� user;
	 *		ÿ�������С packetLen;
	 *		ѹ����ʽ vzipType;
	 *		�豸ip ipAddress;
	 *		�豸�� devNo;
	 *      ·���ļ� pathType logPath&file#logPath&file
	 * @return rHashMap
	 *	key:������ retCode 0��1;
	 *		������Ϣ retMsg;
	 * */
	Map<String, Object> downloadClientFile(Map<String, Object> pHashMap) throws RemoteException;
	
	/**
	 * �ϴ��ļ���ATM�豸
     * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 * key���豸�� devInfo;
	 *  �ļ�·�� viewpath;
	 *  �����ļ��� uploadedFileName;
	 *  �ϴ���ʽ uploadType
	 *  Remoteҵ������˿� remotePort;
	 *  ��ǰ�û� user;
	 * @return rHashMap
	 *  key�������� retCode 0��1;
	 *  ������Ϣ retMsg;
	 * */
	Map<String, Object> uploadFile(Map<String, Object> pHashMap) throws RemoteException;
	
	
	/**
	 * �ϴ��ļ���ATM�豸
     * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 * key���豸�� devInfo;
	 *  �ļ�·�� remoteFile;
	 *  Remoteҵ������˿� remotePort;
	 *  ��ǰ�û� user;
	 * @return rHashMap
	 *  key�������� retCode 0��1;
	 *  ������Ϣ retMsg;
	 * */
	Map<String, Object> delFile(Map<String, Object> pHashMap) throws RemoteException;
	
	/**
	 * �ϴ��ļ���ATM�豸
     * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 * key���豸�� devInfo;
	 *  �ļ�·�� remoteFile;
	 *  Remoteҵ������˿� remotePort;
	 *  ��ǰ�û� user;
	 * @return rHashMap
	 *  key�������� retCode 0��1;
	 *  ������Ϣ retMsg;
	 * */
	Map<String, Object> excuteFile(Map<String, Object> pHashMap) throws RemoteException;
	
	/**
	 * ִ��ִ������
	 * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 *  key:�豸������ devInfo;
	 *  	���� cmdId
	 *  	�Ƿ���Ҫ��ȡ�ļ���־ fileFlag
	 *  	Remoteҵ������˿� remotePort;
	 *  	Remote�ļ������˿� remoteFilePort;
	 *  	v���ļ����·�� dirPath
	 *  	��־���� logDate
	 *      ��ǰ�û� user;
	 * @return rHashMap
	 *  key:������ retCode 0��1;
	 *  	������Ϣ retMsg;
	 *  	����� resultList
	 * */
	Map<String, Object> remoteControl(Map<String, Object> pHashMap) throws RemoteException;
	
	/**
	 * �������߸���ATMC�Ĺ�����Ϣ
	 * 
	 * @param pHashMap
	 * @return rHashMap
	 * @throws RemoteException
	 */
	Map<String, Object> createOrUpdateAtmFunction(Map<String, Object> pHashMap) throws RemoteException;
	
	/**
	 * �������߸���ATMC����־�������Ϣ
	 * 
	 * @param pHashMap
	 * @return rHashMap
	 * @throws RemoteException
	 */
	Map<String, Object> createOrUpdateAtmLog(Map<String, Object> pHashMap) throws RemoteException;
	
	/**
	 * ��ȡ�豸�Ĺ����б���Ϣ
	 * @author lyk
	 * @since 2009.04.01
	 * @param pHashMap 
	 *  key:�豸�� devNo;
	 *  	�豸IP ip
	 *  	�������� paramtype
	 *  	��������ֵ paramtypeValue;
	 *  	Remoteҵ������˿� remotePort;
	 *  	��ǰ�û� user;
	 * @return rHashMap
	 *  key:������ retCode 0��1;
	 *  	������Ϣ retMsg;
	 *  	���� param
	 * */
	Map<String, Object> getAtmParam(Map<String, Object> pHashMap) throws RemoteException;
	
	/**
	 * 
	 * @param ��ȡ���Ͽ�״̬
	 * @return
	 * @throws RemoteException
	 */
	Map<String, Object> getCompositeCardStatus(Map<String, Object> pHashMap) throws RemoteException;
	
	/**
	 * ��ȡ��ȡ�豸������Ϣ
	 * @author cy
	 * @since 2011-8-3
	 * @param pHashMap 
	 *  key:�豸�� devNo;
	 *  	�豸IP ip
	 *  	Remoteҵ������˿� remotePort;
	 *      �������С
	 *      ѹ����ʽ
	 * @return rHashMap
	 *  key:������ retCode 0��1;
	 *  	������Ϣ retMsg;
	 *  	������Ϣ
	 * */
	Map<String, Object> getCashboxInfo(Map<String, Object> pHashMap) throws RemoteException;
	
	/**
	 * �����ȡ�豸������Ϣ
	 * @author cy
	 * @since 2011-8-3
	 * @param pHashMap 
	 *  key:�豸�� devNo;
	 *  	�豸IP ip
	 *  	Remoteҵ������˿� remotePort;
	 *      �������С
	 *      ѹ����ʽ
	 *      ������Ϣ
	 * @return rHashMap
	 *  key:������ retCode 0��1;
	 *  	������Ϣ retMsg,retcode;
	 * */
	Map<String, Object> setCashboxInfo(Map<String, Object> pHashMap) throws RemoteException;
	
	/**
	 * �����������и��Ͽ������رչ���
	 * @author cy
	 * @since 2013-4-23
	 * @param pHashMap 
	 *  key:�豸�� devNo;
	 *  	�豸IP ip
	 *  	Remoteҵ������˿� remotePort;
	 *  	��ǰ�û� user;
	 *  	ͨѶÿ�������С packetLen
	 *      ͨѶѹ����ʽ vzipType
	 * @return rHashMap
	 *  key:������ retCode 0��1;
	 *  	������Ϣ retMsg;
	 *  	���� result
	 * */
	Map<String, Object> createOrUpdateCompositeCardStatus(Map<String, Object> pHashMap)throws RemoteException;
	
	/**
	 * 
	 * @param paraMap
	 * key userNo--�û����
	 *     devInfo--�豸��Ϣ    �豸���|ip��ַ|ͨѶÿ�������С|ѹ����־
	 * @return 00�ɹ���99ʧ��
	 */
	Map<String, Object> qryDepositId(Map<String, Object> paraMap) throws RemoteException;
	
	
	/**
	 * 
	 * @param paraMap
	 * key userNo--�û����
	 *     devInfo--�豸��Ϣ    �豸���|ip��ַ|ͨѶÿ�������С|ѹ����־
	 *     version--�汾��
	 *     ids--id�б�
	 * @return 00�ɹ���99ʧ��
	 */
	Map<String, Object> setDepositId(Map<String, Object> paraMap) throws RemoteException;
	
	/**
	 * 
	 * @param paraMap
	 * key devInfo--�豸���|ip��ַ|ͨѶÿ�������С|ѹ����־
	 *     devInfo--�豸��Ϣ    �豸���|ip��ַ|ͨѶÿ�������С|ѹ����־
	 *     date--����
	 *     time--ʱ��
	 *     userid--�û�
	 *     commandCache--�Ƿ���ҪC�˽��л��洦��
	 *     cimCountAll--�Ƿ񾫲����г���
	 *     cimCashUnitCount--��Ҫ����ĳ�����Ŀ
	 *     CashUnitList--�߼������������б�
	 *     checktype--���鷽ʽ��0-����ִ�У�Ĭ�ϣ���1-��ʱִ��
	 * @return 00�ɹ���99ʧ��
	 */	
	Map<String, Object> qryCashBoxSift(Map<String, Object> paraMap) throws RemoteException;
	
	/**
	 * ��ȡ�豸����ֽ����ˮ��Ϣ
	 */
	Map<String, Object> getCJournalProperties(Map<String, Object> pHashMap) throws RemoteException;

}
