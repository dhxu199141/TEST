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
			log.error("查询存款ID失败", e);
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
			
			log.info("设置设备["+devInfo[0]+"]存款ID前开始关闭设备服务......");
			RMICommandFactory factory = new RMICommandFactory();
			RMICommand command = factory.createCommand(stopServerParam);
			retMap = command.invoke();
			result= "00".equals(retMap.get("retCode").toString());
			
			log.info("设置设备["+devInfo[0]+"]存款ID前关闭设备服务结束,关闭服务"+(result?"成功":"失败"));
			if(!result) {
				retMap.put("retCode", "91");
				retMap.put("retMsg", "设置存款ID前关闭设备服务失败");
				return retMap;
			}
			
			log.info("设置设备["+devInfo[0]+"]存款ID开始......");
			factory = new RMICommandFactory200046();
			command = factory.createCommand(paraMap);
			retMap = command.invoke();
			result= "00".equals(retMap.get("retCode").toString());
			log.info("设置设备["+devInfo[0]+"]存款ID结束,操作"+(result?"成功":"失败"));
			
			if(!result)
				return retMap;
			
			Thread.sleep(TIMEOUT); //延时开启服务
			
			Map<String, Object> startServerParam = new HashMap<String, Object>();
			startServerParam.putAll(paraMap);
			startServerParam.put("cmdId", "200026");
			
			log.info("设置设备["+devInfo[0]+"]存款ID成功后开启设备服务开始......");
			factory = new RMICommandFactory();
			command = factory.createCommand(startServerParam);
			retMap = command.invoke();
			result= "00".equals(retMap.get("retCode").toString());
			log.info("设置设备["+devInfo[0]+"]存款ID成功后开启设备服务结束,开启服务"+(result?"成功":"失败"));
			
			if(!result) {
				retMap.put("retCode", "92");
				retMap.put("retMsg", "设置存款ID成功后开启设备服务失败");
				log.error("设置存款ID成功后开启设备服务失败");
				return retMap;
			}
			
			return retMap;
		} catch (Exception e) {
			retMap.put("retCode", "99");
			log.error("设置存款ID失败", e);
			return retMap;
		} finally {
			RemoteControlDAO.saveRemoteTrace(paraMap.get("cmdId").toString(),paraMap.get("userNo").toString(),devInfo[0],result?SystemLanguage.getControlRemoteControlResultSuccess():SystemLanguage.getMainFailed(),RemoteControlTool.convertCommandId(paraMap.get("cmdId").toString()));
		}
	}
}
