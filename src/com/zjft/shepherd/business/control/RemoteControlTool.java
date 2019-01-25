package com.zjft.shepherd.business.control;

import com.zjft.shepherd.common.SystemLanguage;

public class RemoteControlTool {
	
	/**
	 * 解析远程控制结果
	 * @param retCode
	 * @param cmdId
	 * @return
	 */
	public static String parseRetCode(String retCode,String cmdId) {
		if (retCode.equals("RMT000")) {
			return SystemLanguage.getSrcOperateSuccess();
		} else if (retCode.equals("000001")) {
			return convertCommandId(cmdId) + "|" + SystemLanguage.getSrcOperateFail();
		} else if (retCode.equals("000000")) {
			return convertCommandId(cmdId) + "|" + SystemLanguage.getSrcOperateSuccess();
		} else {
			return SystemLanguage.getSrcOperateFail();
		}
	}
	
	/**
	 * 将远程命令代码解析为中文含义
	 * @param command 远程命令代码
	 * @return 命令代码中文含义
	 */
	public static String convertCommandId(String cmdId) {
		if (cmdId == null || cmdId.equals("")) {
			return "";
		} else if (cmdId.equals("200003")) {
			return SystemLanguage.getSrcTransLog();
		} else if (cmdId.equals("200016")) {
			return SystemLanguage.getControlObtainSoftwareList();
		} else if (cmdId.equals("200007")) {
			return SystemLanguage.getControlRemoteObtain();
		} else if (cmdId.equals("200008")) {
			return SystemLanguage.getControlCourseInfo();
		} else if (cmdId.equals("200011")) {
			return SystemLanguage.getControlNetworkConnection();
		} else if (cmdId.equals("200021")) {
			return SystemLanguage.getControlHardwareInfo();
		} else if (cmdId.equals("200022")) {
			return SystemLanguage.getControlObtainSysInfo();
		} else if (cmdId.equals("200014")) {
			return SystemLanguage.getSrcRestart();
		} else if (cmdId.equals("200015")) {
			return SystemLanguage.getSrcSysShut();
		} else if (cmdId.equals("200027")) {
			return SystemLanguage.getSrcLogicClose();
		} else if (cmdId.equals("200026")) {
			return SystemLanguage.getSrcLogicOpen();
		} else if (cmdId.equals("200028")) {
			return SystemLanguage.getSrcStatefulInspectionModule();
		} else if (cmdId.equals("200029")) {
			return SystemLanguage.getSrcMandatoryCard();
		} else if (cmdId.equals("200024")) {
			return SystemLanguage.getSrcModuleReset();
		} else if(cmdId.equals("200045")) {
			return SystemLanguage.getControlQryDepositId();
		} else if(cmdId.equals("200046")) {
			return SystemLanguage.getControlSetDepositId();
		} else {
			return SystemLanguage.getSrcInvalidControl();
		}
	}

}
