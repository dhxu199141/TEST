package com.zjft.shepherd.service;

import java.util.Map;

public interface DepositIdService {
	
	/**
	 * 
	 * @param paraMap
	 * key userNo--�û����
	 *     devInfo--�豸��Ϣ    �豸���|ip��ַ|ͨѶÿ�������С|ѹ����־
	 * @return 00�ɹ���99ʧ��
	 */
	Map<String,Object> qryDepositId(Map<String,Object> paraMap);
	
	
	/**
	 * 
	 * @param paraMap
	 * key userNo--�û����
	 *     devInfo--�豸��Ϣ    �豸���|ip��ַ|ͨѶÿ�������С|ѹ����־
	 *     version--�汾��
	 *     ids--id�б�
	 * @return 00�ɹ���99ʧ��
	 */
	Map<String,Object> setDepositId(Map<String,Object> paraMap);
}
