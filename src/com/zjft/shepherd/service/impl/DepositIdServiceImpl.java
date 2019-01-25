package com.zjft.shepherd.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zjft.shepherd.business.control.RemoteControlTool;
import com.zjft.shepherd.command.RMICommand;
import com.zjft.shepherd.command.RMICommandFactory;
import com.zjft.shepherd.command.RMICommandFactory200045;
import com.zjft.shepherd.command.RMICommandFactory200046;
import com.zjft.shepherd.common.SystemLanguage;
import com.zjft.shepherd.dao.RemoteControlDAO;
import com.zjft.shepherd.service.DepositIdService;

public class DepositIdServiceImpl implements DepositIdService {

	private static Log log = LogFactory.getLog(DepositIdServiceImpl.class);
	
	private static final long TIMEOUT = 5*1000;

	public Map<String, Object> qryDepositId(Map<String, Object> paraMap) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		String[] devInfo = ((String)paraMap.get("devInfo")).split("\\|", 0);
		paraMap.put("cmdId", "200045");
		boolean result = false;
		try {
			RMICommandFactory factory = new RMICommandFactory200045();
			RMICommand command = factory.createCommand(paraMap);
			retMap = command.invoke();
			result= "00".equals(retMap.get("retCode").toString());
			return retMap;
		} catch (Exception e) {
			log.error("��ѯ���IDʧ��", e);
			retMap.put("retCode", "99");
			return retMap;
		} finally {
			RemoteControlDAO.saveRemoteTrace(paraMap.get("cmdId").toString(),paraMap.get("userNo").toString(),devInfo[0],result?SystemLanguage.getControlRemoteControlResultSuccess():SystemLanguage.getMainFailed(),RemoteControlTool.convertCommandId(paraMap.get("cmdId").toString()));
		}
	}

	public Map<String, Object> setDepositId(Map<String, Object> paraMap) {
		Map<String, Object> retMap = new HashMap<String, Object>();
		String[] devInfo = ((String)paraMap.get("devInfo")).split("\\|", 0);
		paraMap.put("cmdId", "200046");
		boolean result = false;
		try {
			
			Map<String, Object> stopServerParam = new HashMap<String, Object>();
			stopServerParam.putAll(paraMap);
			stopServerParam.put("cmdId", "200027");
			
			log.info("�����豸["+devInfo[0]+"]���IDǰ��ʼ�ر��豸����......");
			RMICommandFactory factory = new RMICommandFactory();
			RMICommand command = factory.createCommand(stopServerParam);
			retMap = command.invoke();
			result= "00".equals(retMap.get("retCode").toString());
			
			log.info("�����豸["+devInfo[0]+"]���IDǰ�ر��豸�������,�رշ���"+(result?"�ɹ�":"ʧ��"));
			if(!result) {
				retMap.put("retCode", "91");
				retMap.put("retMsg", "���ô��IDǰ�ر��豸����ʧ��");
				return retMap;
			}
			
			log.info("�����豸["+devInfo[0]+"]���ID��ʼ......");
			factory = new RMICommandFactory200046();
			command = factory.createCommand(paraMap);
			retMap = command.invoke();
			result= "00".equals(retMap.get("retCode").toString());
			log.info("�����豸["+devInfo[0]+"]���ID����,����"+(result?"�ɹ�":"ʧ��"));
			
			if(!result)
				return retMap;
			
			Thread.sleep(TIMEOUT); //��ʱ��������
			
			Map<String, Object> startServerParam = new HashMap<String, Object>();
			startServerParam.putAll(paraMap);
			startServerParam.put("cmdId", "200026");
			
			log.info("�����豸["+devInfo[0]+"]���ID�ɹ������豸����ʼ......");
			factory = new RMICommandFactory();
			command = factory.createCommand(startServerParam);
			retMap = command.invoke();
			result= "00".equals(retMap.get("retCode").toString());
			log.info("�����豸["+devInfo[0]+"]���ID�ɹ������豸�������,��������"+(result?"�ɹ�":"ʧ��"));
			
			if(!result) {
				retMap.put("retCode", "92");
				retMap.put("retMsg", "���ô��ID�ɹ������豸����ʧ��");
				log.error("���ô��ID�ɹ������豸����ʧ��");
				return retMap;
			}
			
			return retMap;
		} catch (Exception e) {
			retMap.put("retCode", "99");
			log.error("���ô��IDʧ��", e);
			return retMap;
		} finally {
			RemoteControlDAO.saveRemoteTrace(paraMap.get("cmdId").toString(),paraMap.get("userNo").toString(),devInfo[0],result?SystemLanguage.getControlRemoteControlResultSuccess():SystemLanguage.getMainFailed(),RemoteControlTool.convertCommandId(paraMap.get("cmdId").toString()));
		}
	}
}
