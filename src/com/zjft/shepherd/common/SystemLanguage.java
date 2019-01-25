package com.zjft.shepherd.common;

import java.io.FileNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zjft.shepherd.property.ProperFile;
/**
 * 系统运行参数
 * */
public class SystemLanguage {
	private static Log log = LogFactory.getLog(SystemLanguage.class);
	
	private static String  controlAtmFunctionDetailDev = null;
	private static String  controlCDdriveCD = null;
	private static String  controlCourseInfo = null;
	private static String  controlDelFile = null;
	private static String  controlDevDocumentBrowserBodyLocalDisk = null;
	private static String  controlDownloadFile = null;
	private static String  controlExcuteFile = null;
	private static String  controlFloppyDisk = null;
	private static String  controlHardwareInfo = null;
	private static String  controlInchFloppyDisk = null;
	private static String  controlInvalidpath = null;
	private static String  controlNetworkConnection = null;
	private static String  controlNetworkDisk = null;
	private static String  controlObtainSoftwareList = null;
	private static String  controlObtainSysInfo = null;
	private static String  controlPsideAndEquipmentCommunications = null;
	private static String  controlRemoteBrowse = null;
	private static String  controlRemoteControlResultSuccess = null;
	private static String  controlRemoteObtain = null;
	private static String  controlRemovableDisk = null;
	private static String  controlUnrecognized = null;
	private static String  controlUploadFile = null;
	private static String  controlVsideAndEquipmentCommunications = null;
	private static String  controlremoteControlLogDate  = null;
	private static String  mainDev_no = null;
	private static String  mainFailed = null;
	private static String  srcCommFai = null;
	private static String  srcCommRemFai = null;
	private static String  srcDeleteRemoteFai = null;
	private static String  srcDeleteRemoteSuc = null;
	private static String  srcErrorFileList = null;
	private static String  srcFunctionFail = null;
	private static String  srcFunctionFail2 = null;
	private static String  srcFunctionSuccess = null;
	private static String  srcImpRemFileSuc = null;
	private static String  srcImpRemFilefailed = null;
	private static String  srcInvalidControl = null;
	private static String  srcLocalListNotExist = null;
	private static String  srcLogCacheFailed = null;
	private static String  srcLogCacheSuccess = null;
	private static String  srcLogCommFailed = null;
	private static String  srcLogicClose = null;
	private static String  srcLogicOpen = null;
	private static String  srcMandatoryCard = null;
	private static String  srcModuleReset = null;
	private static String  srcNodeNotFound = null;
	private static String  srcOperateAction = null;
	private static String  srcOperateFail = null;
	private static String  srcOperateSuccess = null;
	private static String  srcRestart = null;
	private static String  srcStatefulInspectionModule = null;
	private static String  srcSysShut = null;
	private static String  srcTransLog = null;
	private static String  srcUploadFileFai = null;
	private static String  srcUploadFilesuccess = null;
	private static String  srcGetCashboxInfo = null;
	private static String  srcSetCashboxInfo = null;
	private static String  compositeCardOpen = null;
	private static String  compositeCardClose = null;
	private static String  srcXmlParseFail = null;
	private static String  srcAtcionIllegality = null;
	private static String  srcNonSupportBufferMemory = null;
	//ZIJINMOD 徐全发 2013-12-23 加载冠字号缓存、现金缓存相关的国际化语言
	private static String  controlOpenNtsBuffer = null;
	private static String  controlCloseNtsBuffer = null;
	private static String  controlOpenCashBuffer = null;
	private static String  controlCloseCashBuffer = null;
	//ZIJINMOD 徐全发 2014-01-20 加载存取款冠字号凭条打印的国际化语言
	private static String  controlOpenCimsrp = null;
	private static String  controlCloseCimsrp = null;
	private static String  controlOpenCdmsrp = null;
	private static String  controlCloseCdmsrp = null;
	
	private static String controlQryDepositId = null;
	private static String controlSetDepositId = null;
	
	static {
		SystemLanguage.loadSysParam();
	}

	public static void loadSysParam() {
		ProperFile proper = null;
		try {
			proper = new ProperFile(System.getProperty("user.dir") + "/language/rmi/" + SystemCons.getSystemLanguage() , SystemCons.getSystemLanguage());
		} catch (FileNotFoundException e) {
			log.error("获取语言文件失败", e);
		}
		
		controlAtmFunctionDetailDev = StringUtil.parseString(proper.getProper("control.atmFunctionDetail.dev"));
		controlCDdriveCD = StringUtil.parseString(proper.getProper("control.cDdriveCD"));
		controlCourseInfo = StringUtil.parseString(proper.getProper("control.courseInfo"));
		controlDelFile = StringUtil.parseString(proper.getProper("control.delFile"));
		controlDevDocumentBrowserBodyLocalDisk = StringUtil.parseString(proper.getProper("control.devDocumentBrowserBody.localDisk"));
		controlDownloadFile = StringUtil.parseString(proper.getProper("control.downloadFile"));
		controlExcuteFile = StringUtil.parseString(proper.getProper("control.excuteFile"));
		controlFloppyDisk = StringUtil.parseString(proper.getProper("control.3.5floppyDisk"));
		controlHardwareInfo = StringUtil.parseString(proper.getProper("control.hardwareInfo"));
		controlInchFloppyDisk = StringUtil.parseString(proper.getProper("control.3.5-inchFloppyDisk"));
		controlInvalidpath = StringUtil.parseString(proper.getProper("control.invalidPath"));
		controlNetworkConnection = StringUtil.parseString(proper.getProper("control.networkConnection"));
		controlNetworkDisk = StringUtil.parseString(proper.getProper("control.networkDisk"));
		controlObtainSoftwareList = StringUtil.parseString(proper.getProper("control.obtainSoftwareList"));
		controlObtainSysInfo = StringUtil.parseString(proper.getProper("control.obtainSysInfo"));
		controlPsideAndEquipmentCommunications = StringUtil.parseString(proper.getProper("control.p-side-and-Equipment-Communications"));
		controlRemoteBrowse = StringUtil.parseString(proper.getProper("control.remoteBrowse"));
		controlRemoteControlResultSuccess = StringUtil.parseString(proper.getProper("control.remoteControlResult.success"));
		controlRemoteObtain = StringUtil.parseString(proper.getProper("control.remoteObtain"));
		controlRemovableDisk = StringUtil.parseString(proper.getProper("control.removableDisk"));
		controlUnrecognized = StringUtil.parseString(proper.getProper("control.unrecognized"));
		controlUploadFile = StringUtil.parseString(proper.getProper("control.uploadFile"));
		controlVsideAndEquipmentCommunications = StringUtil.parseString(proper.getProper("control.v-side-and-Equipment-Communications"));
		controlremoteControlLogDate = StringUtil.parseString(proper.getProper("control.remoteControl.logDate"));
		mainDev_no = StringUtil.parseString(proper.getProper("main.dev_no"));
		mainFailed = StringUtil.parseString(proper.getProper("main.failed"));
		srcCommFai = StringUtil.parseString(proper.getProper("src.commFai"));
		srcCommRemFai = StringUtil.parseString(proper.getProper("src.commRemFai"));
		srcDeleteRemoteFai = StringUtil.parseString(proper.getProper("src.deleteRemoteFai"));
		srcDeleteRemoteSuc = StringUtil.parseString(proper.getProper("src.deleteRemoteSuc"));
		srcErrorFileList = StringUtil.parseString(proper.getProper("src.errorFileList"));
		srcFunctionFail = StringUtil.parseString(proper.getProper("src.functionFail"));
		srcFunctionFail2 = StringUtil.parseString(proper.getProper("src.functionFail2"));
		srcFunctionSuccess = StringUtil.parseString(proper.getProper("src.functionSuccess"));
		srcImpRemFileSuc = StringUtil.parseString(proper.getProper("src.impRemFileSuc"));
		srcImpRemFilefailed = StringUtil.parseString(proper.getProper("src.impRemFilefailed"));
		srcInvalidControl = StringUtil.parseString(proper.getProper("src.invalidControl"));
		srcLocalListNotExist = StringUtil.parseString(proper.getProper("src.localListNotExist"));
		srcLogCacheFailed = StringUtil.parseString(proper.getProper("src.logCacheFailed"));
		srcLogCacheSuccess = StringUtil.parseString(proper.getProper("src.logCacheSuccess"));
		srcLogCommFailed = StringUtil.parseString(proper.getProper("src.logCommFailed"));
		srcLogicClose = StringUtil.parseString(proper.getProper("src.logicClose"));
		srcLogicOpen = StringUtil.parseString(proper.getProper("src.logicOpen"));
		srcMandatoryCard = StringUtil.parseString(proper.getProper("src.mandatoryCard"));
		srcModuleReset = StringUtil.parseString(proper.getProper("src.moduleReset"));
		srcNodeNotFound = StringUtil.parseString(proper.getProper("src.nodeNotFound"));
		srcOperateAction = StringUtil.parseString(proper.getProper("src.operateAction"));
		srcOperateFail = StringUtil.parseString(proper.getProper("src.operateFail"));
		srcOperateSuccess = StringUtil.parseString(proper.getProper("src.operateSuccess"));
		srcRestart = StringUtil.parseString(proper.getProper("src.restart"));
		srcStatefulInspectionModule = StringUtil.parseString(proper.getProper("src.statefulInspectionModule"));
		srcSysShut = StringUtil.parseString(proper.getProper("src.sysShut"));
		srcTransLog = StringUtil.parseString(proper.getProper("src.transLog"));
		srcUploadFileFai = StringUtil.parseString(proper.getProper("src.uploadFileFai"));
		srcUploadFilesuccess = StringUtil.parseString(proper.getProper("src.uploadFilesuccess"));
		srcGetCashboxInfo = StringUtil.parseString(proper.getProper("src.getCashboxInfo"));
		srcSetCashboxInfo = StringUtil.parseString(proper.getProper("src.setCashboxInfo"));
		compositeCardOpen = StringUtil.parseString(proper.getProper("control.compositeCardOpen"));
		compositeCardClose = StringUtil.parseString(proper.getProper("control.compositeCardClose"));
		srcXmlParseFail = StringUtil.parseString(proper.getProper("src.xmlParseFail"));
		srcAtcionIllegality = StringUtil.parseString(proper.getProper("src.atcionIllegality"));
		srcNonSupportBufferMemory = StringUtil.parseString(proper.getProper("src.nonSupportBufferMemory"));
		controlOpenNtsBuffer = StringUtil.parseString(proper.getProper("control.openNtsBuffer"));
		controlCloseNtsBuffer = StringUtil.parseString(proper.getProper("control.closeNtsBuffer"));
		controlOpenCashBuffer = StringUtil.parseString(proper.getProper("control.openCashBuffer"));
		controlCloseCashBuffer = StringUtil.parseString(proper.getProper("control.closeCashBuffer"));
		controlOpenCimsrp = StringUtil.parseString(proper.getProper("control.openCimsrp"));
		controlCloseCimsrp = StringUtil.parseString(proper.getProper("control.closeCimsrp"));
		controlOpenCdmsrp = StringUtil.parseString(proper.getProper("control.openCdmsrp"));
		controlCloseCdmsrp = StringUtil.parseString(proper.getProper("control.closeCdmsrp"));
		
		controlQryDepositId = StringUtil.parseString(proper.getProper("control.qryDepositId"));
		controlSetDepositId = StringUtil.parseString(proper.getProper("control.setDepositId"));
		
		log.debug("control.3.5-inchFloppyDisk=" + controlInchFloppyDisk);
		log.debug("control.3.5floppyDisk=" + controlFloppyDisk);
		log.debug("control.invalidPath=" + controlInvalidpath);
		log.debug("control.atmFunctionDetail.dev=" + controlAtmFunctionDetailDev);
		log.debug("control.cDdriveCD=" + controlCDdriveCD);
		log.debug("control.courseInfo=" + controlCourseInfo);
		log.debug("control.delFile=" + controlDelFile);
		log.debug("control.devDocumentBrowserBody.localDisk=" + controlDevDocumentBrowserBodyLocalDisk);
		log.debug("control.downloadFile=" + controlDownloadFile);
		log.debug("control.excuteFile=" + controlExcuteFile);
		log.debug("control.hardwareInfo=" + controlHardwareInfo);
		log.debug("control.networkConnection=" + controlNetworkConnection);
		log.debug("control.networkDisk=" + controlNetworkDisk);
		log.debug("control.obtainSoftwareList=" + controlObtainSoftwareList);
		log.debug("control.obtainSysInfo=" + controlObtainSysInfo);
		log.debug("control.p-side-and-Equipment-Communications=" + controlPsideAndEquipmentCommunications);
		log.debug("control.remoteBrowse=" + controlRemoteBrowse);
		log.debug("control.remoteControl.logDate=" + controlremoteControlLogDate);
		log.debug("control.remoteControlResult.success=" + controlRemoteControlResultSuccess);
		log.debug("control.remoteObtain=" + controlRemoteObtain);
		log.debug("control.removableDisk=" + controlRemovableDisk);
		log.debug("control.unrecognized=" + controlUnrecognized);
		log.debug("control.uploadFile=" + controlUploadFile);
		log.debug("control.v-side-and-Equipment-Communications=" + controlVsideAndEquipmentCommunications);
		log.debug("main.dev_no=" + mainDev_no);
		log.debug("main.failed=" + mainFailed);
		log.debug("src.commFai=" + srcCommFai);
		log.debug("src.commRemFai=" + srcCommRemFai);
		log.debug("src.deleteRemoteFai=" + srcDeleteRemoteFai);
		log.debug("src.deleteRemoteSuc=" + srcDeleteRemoteSuc);
		log.debug("src.errorFileList=" + srcErrorFileList);
		log.debug("src.functionFail2=" + srcFunctionFail2);
		log.debug("src.functionFail=" + srcFunctionFail);
		log.debug("src.functionSuccess=" + srcFunctionSuccess);
		log.debug("src.impRemFileSuc=" + srcImpRemFileSuc);
		log.debug("src.impRemFilefailed=" + srcImpRemFilefailed);
		log.debug("src.invalidControl=" + srcInvalidControl);
		log.debug("src.localListNotExist=" + srcLocalListNotExist);
		log.debug("src.logCacheFailed=" + srcLogCacheFailed);
		log.debug("src.logCacheSuccess=" + srcLogCacheSuccess);
		log.debug("src.logCommFailed=" + srcLogCommFailed);
		log.debug("src.logicClose=" + srcLogicClose);
		log.debug("src.logicOpen=" + srcLogicOpen);
		log.debug("src.mandatoryCard=" + srcMandatoryCard);
		log.debug("src.moduleReset=" + srcModuleReset);
		log.debug("src.nodeNotFound=" + srcNodeNotFound);
		log.debug("src.operateAction=" + srcOperateAction);
		log.debug("src.operateFail=" + srcOperateFail);
		log.debug("src.operateSuccess=" + srcOperateSuccess);
		log.debug("src.restart=" + srcRestart);
		log.debug("src.statefulInspectionModule=" + srcStatefulInspectionModule);
		log.debug("src.sysShut=" + srcSysShut);
		log.debug("src.transLog=" + srcTransLog);
		log.debug("src.uploadFileFai=" + srcUploadFileFai);
		log.debug("src.uploadFilesuccess=" + srcUploadFilesuccess);
		log.debug("src.getCashboxInfo=" + srcGetCashboxInfo);
		log.debug("src.setCashboxInfo=" + srcSetCashboxInfo);
		log.debug("control.compositeCardOpen=" + compositeCardOpen);
		log.debug("control.compositeCardClose=" + compositeCardClose);
		log.debug("src.xmlParseFail=" + srcXmlParseFail);
		log.debug("src.atcionIllegality=" + srcAtcionIllegality);
		log.debug("src.nonSupportBufferMemory=" + srcNonSupportBufferMemory);
		log.debug("control.openNtsBuffer=" + controlOpenNtsBuffer);
		log.debug("control.closeNtsBuffer=" + controlCloseNtsBuffer);
		log.debug("control.openCashBuffer=" + controlOpenCashBuffer);
		log.debug("control.closeCashBuffer=" + controlCloseCashBuffer);
		log.debug("control.controlOpenCimsrp=" + controlOpenCimsrp);
		log.debug("control.controlCloseCimsrp=" + controlCloseCimsrp);
		log.debug("control.controlOpenCdmsrp=" + controlOpenCdmsrp);
		log.debug("control.controlCloseCdmsrp=" + controlCloseCdmsrp);
		
		log.debug("control.qryDepositId = " + controlQryDepositId);
		log.debug("control.setDepositId = " + controlSetDepositId);
		proper.close();
	}


	public static String getControlAtmFunctionDetailDev() {
		return controlAtmFunctionDetailDev;
	}


	public static void setControlAtmFunctionDetailDev(
			String controlAtmFunctionDetailDev) {
		SystemLanguage.controlAtmFunctionDetailDev = controlAtmFunctionDetailDev;
	}


	public static String getControlCDdriveCD() {
		return controlCDdriveCD;
	}


	public static void setControlCDdriveCD(String controlCDdriveCD) {
		SystemLanguage.controlCDdriveCD = controlCDdriveCD;
	}


	public static String getControlCourseInfo() {
		return controlCourseInfo;
	}


	public static void setControlCourseInfo(String controlCourseInfo) {
		SystemLanguage.controlCourseInfo = controlCourseInfo;
	}


	public static String getControlDelFile() {
		return controlDelFile;
	}


	public static void setControlDelFile(String controlDelFile) {
		SystemLanguage.controlDelFile = controlDelFile;
	}


	public static String getControlDevDocumentBrowserBodyLocalDisk() {
		return controlDevDocumentBrowserBodyLocalDisk;
	}


	public static void setControlDevDocumentBrowserBodyLocalDisk(
			String controlDevDocumentBrowserBodyLocalDisk) {
		SystemLanguage.controlDevDocumentBrowserBodyLocalDisk = controlDevDocumentBrowserBodyLocalDisk;
	}


	public static String getControlDownloadFile() {
		return controlDownloadFile;
	}


	public static void setControlDownloadFile(String controlDownloadFile) {
		SystemLanguage.controlDownloadFile = controlDownloadFile;
	}


	public static String getControlExcuteFile() {
		return controlExcuteFile;
	}


	public static void setControlExcuteFile(String controlExcuteFile) {
		SystemLanguage.controlExcuteFile = controlExcuteFile;
	}


	public static String getControlFloppyDisk() {
		return controlFloppyDisk;
	}


	public static void setControlFloppyDisk(String controlFloppyDisk) {
		SystemLanguage.controlFloppyDisk = controlFloppyDisk;
	}


	public static String getControlHardwareInfo() {
		return controlHardwareInfo;
	}


	public static void setControlHardwareInfo(String controlHardwareInfo) {
		SystemLanguage.controlHardwareInfo = controlHardwareInfo;
	}


	public static String getControlInchFloppyDisk() {
		return controlInchFloppyDisk;
	}


	public static void setControlInchFloppyDisk(String controlInchFloppyDisk) {
		SystemLanguage.controlInchFloppyDisk = controlInchFloppyDisk;
	}


	public static String getControlInvalidpath() {
		return controlInvalidpath;
	}


	public static void setControlInvalidpath(String controlInvalidpath) {
		SystemLanguage.controlInvalidpath = controlInvalidpath;
	}


	public static String getControlNetworkConnection() {
		return controlNetworkConnection;
	}


	public static void setControlNetworkConnection(String controlNetworkConnection) {
		SystemLanguage.controlNetworkConnection = controlNetworkConnection;
	}


	public static String getControlNetworkDisk() {
		return controlNetworkDisk;
	}


	public static void setControlNetworkDisk(String controlNetworkDisk) {
		SystemLanguage.controlNetworkDisk = controlNetworkDisk;
	}


	public static String getControlObtainSoftwareList() {
		return controlObtainSoftwareList;
	}


	public static void setControlObtainSoftwareList(String controlObtainSoftwareList) {
		SystemLanguage.controlObtainSoftwareList = controlObtainSoftwareList;
	}


	public static String getControlObtainSysInfo() {
		return controlObtainSysInfo;
	}


	public static void setControlObtainSysInfo(String controlObtainSysInfo) {
		SystemLanguage.controlObtainSysInfo = controlObtainSysInfo;
	}


	public static String getControlPsideAndEquipmentCommunications() {
		return controlPsideAndEquipmentCommunications;
	}


	public static void setControlPsideAndEquipmentCommunications(
			String controlPsideAndEquipmentCommunications) {
		SystemLanguage.controlPsideAndEquipmentCommunications = controlPsideAndEquipmentCommunications;
	}


	public static String getControlRemoteBrowse() {
		return controlRemoteBrowse;
	}


	public static void setControlRemoteBrowse(String controlRemoteBrowse) {
		SystemLanguage.controlRemoteBrowse = controlRemoteBrowse;
	}


	public static String getControlRemoteControlResultSuccess() {
		return controlRemoteControlResultSuccess;
	}


	public static void setControlRemoteControlResultSuccess(
			String controlRemoteControlResultSuccess) {
		SystemLanguage.controlRemoteControlResultSuccess = controlRemoteControlResultSuccess;
	}


	public static String getControlRemoteObtain() {
		return controlRemoteObtain;
	}


	public static void setControlRemoteObtain(String controlRemoteObtain) {
		SystemLanguage.controlRemoteObtain = controlRemoteObtain;
	}


	public static String getControlRemovableDisk() {
		return controlRemovableDisk;
	}


	public static void setControlRemovableDisk(String controlRemovableDisk) {
		SystemLanguage.controlRemovableDisk = controlRemovableDisk;
	}


	public static String getControlUnrecognized() {
		return controlUnrecognized;
	}


	public static void setControlUnrecognized(String controlUnrecognized) {
		SystemLanguage.controlUnrecognized = controlUnrecognized;
	}


	public static String getControlUploadFile() {
		return controlUploadFile;
	}


	public static void setControlUploadFile(String controlUploadFile) {
		SystemLanguage.controlUploadFile = controlUploadFile;
	}


	public static String getControlVsideAndEquipmentCommunications() {
		return controlVsideAndEquipmentCommunications;
	}


	public static void setControlVsideAndEquipmentCommunications(
			String controlVsideAndEquipmentCommunications) {
		SystemLanguage.controlVsideAndEquipmentCommunications = controlVsideAndEquipmentCommunications;
	}


	public static String getControlremoteControlLogDate() {
		return controlremoteControlLogDate;
	}


	public static void setControlremoteControlLogDate(
			String controlremoteControlLogDate) {
		SystemLanguage.controlremoteControlLogDate = controlremoteControlLogDate;
	}


	public static String getMainDev_no() {
		return mainDev_no;
	}


	public static void setMainDev_no(String mainDevNo) {
		mainDev_no = mainDevNo;
	}


	public static String getMainFailed() {
		return mainFailed;
	}


	public static void setMainFailed(String mainFailed) {
		SystemLanguage.mainFailed = mainFailed;
	}


	public static String getSrcCommFai() {
		return srcCommFai;
	}


	public static void setSrcCommFai(String srcCommFai) {
		SystemLanguage.srcCommFai = srcCommFai;
	}


	public static String getSrcCommRemFai() {
		return srcCommRemFai;
	}


	public static void setSrcCommRemFai(String srcCommRemFai) {
		SystemLanguage.srcCommRemFai = srcCommRemFai;
	}


	public static String getSrcDeleteRemoteFai() {
		return srcDeleteRemoteFai;
	}


	public static void setSrcDeleteRemoteFai(String srcDeleteRemoteFai) {
		SystemLanguage.srcDeleteRemoteFai = srcDeleteRemoteFai;
	}


	public static String getSrcDeleteRemoteSuc() {
		return srcDeleteRemoteSuc;
	}


	public static void setSrcDeleteRemoteSuc(String srcDeleteRemoteSuc) {
		SystemLanguage.srcDeleteRemoteSuc = srcDeleteRemoteSuc;
	}


	public static String getSrcErrorFileList() {
		return srcErrorFileList;
	}


	public static void setSrcErrorFileList(String srcErrorFileList) {
		SystemLanguage.srcErrorFileList = srcErrorFileList;
	}


	public static String getSrcFunctionFail() {
		return srcFunctionFail;
	}


	public static void setSrcFunctionFail(String srcFunctionFail) {
		SystemLanguage.srcFunctionFail = srcFunctionFail;
	}


	public static String getSrcFunctionFail2() {
		return srcFunctionFail2;
	}


	public static void setSrcFunctionFail2(String srcFunctionFail2) {
		SystemLanguage.srcFunctionFail2 = srcFunctionFail2;
	}


	public static String getSrcFunctionSuccess() {
		return srcFunctionSuccess;
	}


	public static void setSrcFunctionSuccess(String srcFunctionSuccess) {
		SystemLanguage.srcFunctionSuccess = srcFunctionSuccess;
	}


	public static String getSrcImpRemFileSuc() {
		return srcImpRemFileSuc;
	}


	public static void setSrcImpRemFileSuc(String srcImpRemFileSuc) {
		SystemLanguage.srcImpRemFileSuc = srcImpRemFileSuc;
	}


	public static String getSrcImpRemFilefailed() {
		return srcImpRemFilefailed;
	}


	public static void setSrcImpRemFilefailed(String srcImpRemFilefailed) {
		SystemLanguage.srcImpRemFilefailed = srcImpRemFilefailed;
	}


	public static String getSrcInvalidControl() {
		return srcInvalidControl;
	}


	public static void setSrcInvalidControl(String srcInvalidControl) {
		SystemLanguage.srcInvalidControl = srcInvalidControl;
	}


	public static String getSrcLocalListNotExist() {
		return srcLocalListNotExist;
	}


	public static void setSrcLocalListNotExist(String srcLocalListNotExist) {
		SystemLanguage.srcLocalListNotExist = srcLocalListNotExist;
	}


	public static String getSrcLogCacheFailed() {
		return srcLogCacheFailed;
	}


	public static void setSrcLogCacheFailed(String srcLogCacheFailed) {
		SystemLanguage.srcLogCacheFailed = srcLogCacheFailed;
	}


	public static String getSrcLogCacheSuccess() {
		return srcLogCacheSuccess;
	}


	public static void setSrcLogCacheSuccess(String srcLogCacheSuccess) {
		SystemLanguage.srcLogCacheSuccess = srcLogCacheSuccess;
	}


	public static String getSrcLogCommFailed() {
		return srcLogCommFailed;
	}


	public static void setSrcLogCommFailed(String srcLogCommFailed) {
		SystemLanguage.srcLogCommFailed = srcLogCommFailed;
	}


	public static String getSrcLogicClose() {
		return srcLogicClose;
	}


	public static void setSrcLogicClose(String srcLogicClose) {
		SystemLanguage.srcLogicClose = srcLogicClose;
	}


	public static String getSrcLogicOpen() {
		return srcLogicOpen;
	}


	public static void setSrcLogicOpen(String srcLogicOpen) {
		SystemLanguage.srcLogicOpen = srcLogicOpen;
	}


	public static String getSrcMandatoryCard() {
		return srcMandatoryCard;
	}


	public static void setSrcMandatoryCard(String srcMandatoryCard) {
		SystemLanguage.srcMandatoryCard = srcMandatoryCard;
	}


	public static String getSrcModuleReset() {
		return srcModuleReset;
	}


	public static void setSrcModuleReset(String srcModuleReset) {
		SystemLanguage.srcModuleReset = srcModuleReset;
	}


	public static String getSrcNodeNotFound() {
		return srcNodeNotFound;
	}


	public static void setSrcNodeNotFound(String srcNodeNotFound) {
		SystemLanguage.srcNodeNotFound = srcNodeNotFound;
	}


	public static String getSrcOperateAction() {
		return srcOperateAction;
	}


	public static void setSrcOperateAction(String srcOperateAction) {
		SystemLanguage.srcOperateAction = srcOperateAction;
	}


	public static String getSrcOperateFail() {
		return srcOperateFail;
	}


	public static void setSrcOperateFail(String srcOperateFail) {
		SystemLanguage.srcOperateFail = srcOperateFail;
	}


	public static String getSrcOperateSuccess() {
		return srcOperateSuccess;
	}


	public static void setSrcOperateSuccess(String srcOperateSuccess) {
		SystemLanguage.srcOperateSuccess = srcOperateSuccess;
	}


	public static String getSrcRestart() {
		return srcRestart;
	}


	public static void setSrcRestart(String srcRestart) {
		SystemLanguage.srcRestart = srcRestart;
	}


	public static String getSrcStatefulInspectionModule() {
		return srcStatefulInspectionModule;
	}


	public static void setSrcStatefulInspectionModule(
			String srcStatefulInspectionModule) {
		SystemLanguage.srcStatefulInspectionModule = srcStatefulInspectionModule;
	}


	public static String getSrcSysShut() {
		return srcSysShut;
	}


	public static void setSrcSysShut(String srcSysShut) {
		SystemLanguage.srcSysShut = srcSysShut;
	}


	public static String getSrcTransLog() {
		return srcTransLog;
	}


	public static void setSrcTransLog(String srcTransLog) {
		SystemLanguage.srcTransLog = srcTransLog;
	}


	public static String getSrcUploadFileFai() {
		return srcUploadFileFai;
	}


	public static void setSrcUploadFileFai(String srcUploadFileFai) {
		SystemLanguage.srcUploadFileFai = srcUploadFileFai;
	}


	public static String getSrcUploadFilesuccess() {
		return srcUploadFilesuccess;
	}


	public static void setSrcUploadFilesuccess(String srcUploadFilesuccess) {
		SystemLanguage.srcUploadFilesuccess = srcUploadFilesuccess;
	}


	public static String getSrcGetCashboxInfo() {
		return srcGetCashboxInfo;
	}


	public static void setSrcGetCashboxInfo(String srcGetCashboxInfo) {
		SystemLanguage.srcGetCashboxInfo = srcGetCashboxInfo;
	}


	public static String getSrcSetCashboxInfo() {
		return srcSetCashboxInfo;
	}


	public static void setSrcSetCashboxInfo(String srcSetCashboxInfo) {
		SystemLanguage.srcSetCashboxInfo = srcSetCashboxInfo;
	}

	public static String getCompositeCardOpen() {
		return compositeCardOpen;
	}


	public static void setCompositeCardOpen(String compositeCardOpen) {
		SystemLanguage.compositeCardOpen = compositeCardOpen;
	}


	public static String getCompositeCardClose() {
		return compositeCardClose;
	}


	public static void setCompositeCardClose(String compositeCardClose) {
		SystemLanguage.compositeCardClose = compositeCardClose;
	}


	public static String getControlOpenNtsBuffer() {
		return controlOpenNtsBuffer;
	}


	public static void setControlOpenNtsBuffer(String controlOpenNtsBuffer) {
		SystemLanguage.controlOpenNtsBuffer = controlOpenNtsBuffer;
	}


	public static String getControlCloseNtsBuffer() {
		return controlCloseNtsBuffer;
	}


	public static void setControlCloseNtsBuffer(String controlCloseNtsBuffer) {
		SystemLanguage.controlCloseNtsBuffer = controlCloseNtsBuffer;
	}


	public static String getControlOpenCashBuffer() {
		return controlOpenCashBuffer;
	}


	public static void setControlOpenCashBuffer(String controlOpenCashBuffer) {
		SystemLanguage.controlOpenCashBuffer = controlOpenCashBuffer;
	}


	public static String getControlCloseCashBuffer() {
		return controlCloseCashBuffer;
	}


	public static void setControlCloseCashBuffer(String controlCloseCashBuffer) {
		SystemLanguage.controlCloseCashBuffer = controlCloseCashBuffer;
	}


	public static String getSrcXmlParseFail() {
		return srcXmlParseFail;
	}


	public static void setSrcXmlParseFail(String srcXmlParseFail) {
		SystemLanguage.srcXmlParseFail = srcXmlParseFail;
	}


	public static String getSrcAtcionIllegality() {
		return srcAtcionIllegality;
	}


	public static void setSrcAtcionIllegality(String srcAtcionIllegality) {
		SystemLanguage.srcAtcionIllegality = srcAtcionIllegality;
	}


	public static String getSrcNonSupportBufferMemory() {
		return srcNonSupportBufferMemory;
	}


	public static void setSrcNonSupportBufferMemory(String srcNonSupportBufferMemory) {
		SystemLanguage.srcNonSupportBufferMemory = srcNonSupportBufferMemory;
	}


	public static String getControlOpenCimsrp() {
		return controlOpenCimsrp;
	}


	public static void setControlOpenCimsrp(String controlOpenCimsrp) {
		SystemLanguage.controlOpenCimsrp = controlOpenCimsrp;
	}


	public static String getControlCloseCimsrp() {
		return controlCloseCimsrp;
	}


	public static void setControlCloseCimsrp(String controlCloseCimsrp) {
		SystemLanguage.controlCloseCimsrp = controlCloseCimsrp;
	}


	public static String getControlOpenCdmsrp() {
		return controlOpenCdmsrp;
	}


	public static void setControlOpenCdmsrp(String controlOpenCdmsrp) {
		SystemLanguage.controlOpenCdmsrp = controlOpenCdmsrp;
	}


	public static String getControlCloseCdmsrp() {
		return controlCloseCdmsrp;
	}


	public static void setControlCloseCdmsrp(String controlCloseCdmsrp) {
		SystemLanguage.controlCloseCdmsrp = controlCloseCdmsrp;
	}
	
	public static String getControlQryDepositId() {
		return controlQryDepositId;
	}

	public static void setControlQryDepositId(String controlQryDepositId) {
		SystemLanguage.controlQryDepositId = controlQryDepositId;
	}

	public static String getControlSetDepositId() {
		return controlSetDepositId;
	}

	public static void setControlSetDepositId(String controlSetDepositId) {
		SystemLanguage.controlSetDepositId = controlSetDepositId;
	}

}
