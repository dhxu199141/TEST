package com.zjft.shepherd.process;


import java.util.HashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.InvalidXPathException;
import com.ximpleware.*;
/**
 * 该类对从队列中获取的消息进行解析
 * 
 * @author hpshen
 */

public class MessageParse {
	
	private static Log log=LogFactory.getLog(MessageParse.class);
			

	/**
	 * 根据消息体XML文档解析消息类型
	 * @param document 消息体XML文档
	 * @return 消息类型
	 */
	public static String getMessageType(Document document){
		
		if(document==null){
			log.error("document is null");
			return "";
		}		
		
		String rootValue=document.getRootElement().getName();
				
		if(rootValue.equalsIgnoreCase("StatusInfo")){
			return "StatusInfo";
		}
		else if(rootValue.equalsIgnoreCase("FaultInfo")){
			return "FaultInfo";
		}
		else if(rootValue.equalsIgnoreCase("PropInfo")){
			return "PropInfo";
		}
		else if(rootValue.equalsIgnoreCase("OpencloseInfo")){
			return "OpencloseInfo";
		}		
		else if(rootValue.equalsIgnoreCase("HardwareInfo")){
			return "HardwareInfo";
		}
		else if(rootValue.equalsIgnoreCase("SoftwareInfo")){
			return "SoftwareInfo";
		}
		else if(rootValue.equalsIgnoreCase("RunInfo")){
			return "RunInfo";
		}
		else if(rootValue.equalsIgnoreCase("ClearInfo")){
			return "ClearInfo";
		}
		else if(rootValue.equalsIgnoreCase("CardRetainInfo")){
			return "CardRetainInfo";
		}
		else if(rootValue.equalsIgnoreCase("root")){
			return "root";
		}
		else if(rootValue.equalsIgnoreCase("TxInfo")){
			return "TxInfo";
		}
		else if(rootValue.equalsIgnoreCase("SmsInfo")){
			return "SmsInfo";
		}				
		else if(rootValue.equalsIgnoreCase("DownVersionInfo"))//部署于分行版本
		{
			return "DownVersionInfo";
		}		
		else 
		{
			return "";
		}
		
	}
	
	public static String getMessageType(VTDNav vn){
		String rootValue = "";
		try
		{
			rootValue = vn.toNormalizedString(vn.getRootIndex());
		}
		catch(Exception e)
		{
			
		}
		if(rootValue.equalsIgnoreCase("StatusInfo")){
			return "StatusInfo";
		}
		else if(rootValue.equalsIgnoreCase("FaultInfo")){
			return "FaultInfo";
		}
		else if(rootValue.equalsIgnoreCase("PropInfo")){
			return "PropInfo";
		}
		else if(rootValue.equalsIgnoreCase("OpencloseInfo")){
			return "OpencloseInfo";
		}		
		else if(rootValue.equalsIgnoreCase("HardwareInfo")){
			return "HardwareInfo";
		}
		else if(rootValue.equalsIgnoreCase("SoftwareInfo")){
			return "SoftwareInfo";
		}
		else if(rootValue.equalsIgnoreCase("RunInfo")){
			return "RunInfo";
		}
		else if(rootValue.equalsIgnoreCase("ClearInfo")){
			return "ClearInfo";
		}
		else if(rootValue.equalsIgnoreCase("CardRetainInfo")){
			return "CardRetainInfo";
		}
		else if(rootValue.equalsIgnoreCase("root")){
			return "root";
		}
		else if(rootValue.equalsIgnoreCase("TxInfo")){
			return "TxInfo";
		}
		else if(rootValue.equalsIgnoreCase("SmsInfo")){
			return "SmsInfo";
		}				
		else if(rootValue.equalsIgnoreCase("DownVersionInfo"))//部署于分行版本
		{
			return "DownVersionInfo";
		}		
		else 
		{
			return "";
		}
	}
	
	/**
	 * 从消息体XML文档中取得短信信息
	 * @param document - 信息的xml
	 * @return HashMap - 信息的map
	 */
	
	public static HashMap<String,String> getSmsInfoMap(Document document){
		

		if(document==null){
			log.error("document is null");
			return null;
		}
		
		HashMap<String,String> map=new HashMap<String,String>();
		
		String mobileCode=getSingleNodeValue(document,"//SmsInfo/MobileCode");
		String smsBody=getSingleNodeValue(document,"//SmsInfo/SmsBody");

		map.put("MobileCode",mobileCode);
		map.put("SmsBody",smsBody);
		
		return map;		
	}
	
	public static HashMap<String,String> getSmsInfoMap(VTDNav document){
		

		if(document==null){
			log.error("document is null");
			return null;
		}
		
		HashMap<String,String> map=new HashMap<String,String>();
		
		String mobileCode=getSingleNodeValue(document,"//SmsInfo/MobileCode");
		String smsBody=getSingleNodeValue(document,"//SmsInfo/SmsBody");

		map.put("MobileCode",mobileCode);
		map.put("SmsBody",smsBody);
		
		return map;		
	}
	
	/**
	 * 从消息体XML文档中取得RVS服务器信息
	 * @param document - 信息的xml
	 * @return HashMap - 信息的map
	 */
	
	public static HashMap<String,String> getRootInfoMap(Document document){
		
		if(document==null){
			log.error("document is null");
			return null;
		}
		
		HashMap<String,String> map=new HashMap<String,String>();
				
		String cmdid=getSingleNodeValue(document,"//root/cmdid/@value");
		String cmddate=getSingleNodeValue(document,"//root/cmddatetime/@date");
		String cmdtime=getSingleNodeValue(document,"//root/cmddatetime/@time");
		String messageid=getSingleNodeValue(document,"//root/msgid/@value");
		String userid=getSingleNodeValue(document,"//root/operatorinfo/@userid");
		String actiontime=getSingleNodeValue(document,"//root/actioninfo/@time");
		String actionfile=getSingleNodeValue(document,"//root/actioninfo/@filename");
		String retcode=getSingleNodeValue(document,"//root/retcode/@value");        
		
		map.put("cmdid",cmdid);
		map.put("cmddate",cmddate);
		map.put("cmdtime",cmdtime);
		map.put("messageid",messageid);
		map.put("userid",userid);
		map.put("actiontime",actiontime);
		map.put("actionfile",actionfile);
		map.put("retcode",retcode);

		return map;
		
	}

	public static HashMap<String,String> getRootInfoMap(VTDNav document){
		
		if(document==null){
			log.error("document is null");
			return null;
		}
		
		HashMap<String,String> map=new HashMap<String,String>();
				
		String cmdid=getSingleNodeValue(document,"//root/cmdid/@value");
		String cmddate=getSingleNodeValue(document,"//root/cmddatetime/@date");
		String cmdtime=getSingleNodeValue(document,"//root/cmddatetime/@time");
		String messageid=getSingleNodeValue(document,"//root/msgid/@value");
		String userid=getSingleNodeValue(document,"//root/operatorinfo/@userid");
		String actiontime=getSingleNodeValue(document,"//root/actioninfo/@time");
		String actionfile=getSingleNodeValue(document,"//root/actioninfo/@filename");
		String retcode=getSingleNodeValue(document,"//root/retcode/@value");        
		
		map.put("cmdid",cmdid);
		map.put("cmddate",cmddate);
		map.put("cmdtime",cmdtime);
		map.put("messageid",messageid);
		map.put("userid",userid);
		map.put("actiontime",actiontime);
		map.put("actionfile",actionfile);
		map.put("retcode",retcode);

		return map;
		
	}
	
	/**
	 * 从消息体XML文档中取得设备运行信息
	 * @param document - 信息的xml
	 * @return HashMap - 信息的map
	 */
	
	public static HashMap<String,String> getRunInfoMap(Document document){
		
		if(document==null){
			log.error("getRunInfoMap() document is null");
			return null;
		}
		
		HashMap<String,String> map=new HashMap<String,String>();
		
		String TerminalId=getSingleNodeValue(document,"//RunInfo/TerminalId");
		String TerminalIp=getSingleNodeValue(document,"//RunInfo/TerminalIp");
		String ActionType=getSingleNodeValue(document,"//RunInfo/ActionType");
		String DateTime=getSingleNodeValue(document,"//RunInfo/DateTime");
    
		map.put("TerminalId",TerminalId);	
		map.put("TerminalIp",TerminalIp);			
		map.put("ActionType",ActionType);	
		map.put("DateTime",DateTime);	
		return map;		
	}
	
	
	/**
	 * 从消息体XML文档中取得文件下载信息
	 * @param document - 信息的xml
	 * @return HashMap - 信息的map
	 */
	
	public static HashMap<String,String> getDownInfoMap(Document document){
		
		if(document==null){
			log.error("document is null");
			return null;
		}
		
		HashMap<String,String> rootMap=new HashMap<String,String>();
		
		String cmdid=getSingleNodeValue(document,"//root/cmdid");
		String filename=getSingleNodeValue(document,"//root/filename");
		String startpos=getSingleNodeValue(document,"//root/startpos");

		rootMap.put("cmdid",cmdid);
		rootMap.put("filename",filename);
		rootMap.put("startpos",startpos);

		return rootMap;
		
	}
	
	/**
	 * 从消息体XML文档中取得设备故障信息
	 * @param document - 信息的xml
	 * @return HashMap - 信息的map
	 */
	
	public static HashMap<String,String> getFaultInfoMap(Document document){
		
		if(document==null){
			log.error("document is null");
			return null;
		}
		
		HashMap<String,String> faultMap=new HashMap<String,String>();
		
		String terminalId=getSingleNodeValue(document,"//FaultInfo/TerminalId");
		String terminalIp=getSingleNodeValue(document,"//FaultInfo/TerminalIp");
		String devType=getSingleNodeValue(document,"//FaultInfo/DevType");
		String faultCode=getSingleNodeValue(document,"//FaultInfo/FaultCode");
		String faultDescription=getSingleNodeValue(document,"//FaultInfo/FaultDescription");
		String DateTime=getSingleNodeValue(document,"//FaultInfo/DateTime");
		
		faultMap.put("TerminalId",terminalId);
		faultMap.put("TerminalIp",terminalIp);
		faultMap.put("DevType",devType);
		faultMap.put("FaultCode",faultCode);
		faultMap.put("FaultDescription",faultDescription);
		faultMap.put("DateTime",DateTime);
		
		if(devType.equalsIgnoreCase("IDC")){
			
			String devStatusCode=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailIdc/DevStatusCode");
			String devIdcMedia=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailIdc/DevIdcMedia");
			String devIdcRetainBin=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailIdc/DevIdcRetainBin");
			String devIdcCards=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailIdc/DevIdcCards");
			
			faultMap.put("DevDetailIdc/DevStatusCode",devStatusCode);		
			faultMap.put("DevDetailIdc/DevIdcMedia",devIdcMedia);	
			faultMap.put("DevDetailIdc/DevIdcRetainBin",devIdcRetainBin);			
			faultMap.put("DevDetailIdc/DevIdcCards",devIdcCards);
		}
		
		else if(devType.equalsIgnoreCase("SPR")){
			
			String devStatusCode=getSingleNodeValue(document,"//v/DevDetail/DevDetailSpr/DevStatusCode");
			String devPtrMedia=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSpr/DevPtrMedia");
			String devPtrRetractBin=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSpr/DevPtrRetractBin");
			String devPtrInk=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSpr/DevPtrInk");
			String devPtrToner=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSpr/DevPtrToner");
			String devPtrSupplyLevel=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSpr/DevPtrSupplyLevel");

			faultMap.put("DevDetailSpr/DevStatusCode",devStatusCode);			
			faultMap.put("DevDetailSpr/DevPtrMedia",devPtrMedia);			
			faultMap.put("DevDetailSpr/DevPtrRetractBin",devPtrRetractBin);			
			faultMap.put("DevDetailSpr/DevPtrInk",devPtrInk);			
			faultMap.put("DevDetailSpr/DevPtrToner",devPtrToner);			
			faultMap.put("DevDetailSpr/DevPtrSupplyLevel",devPtrSupplyLevel);
		}
		
		else if(devType.equalsIgnoreCase("RPR")){
			
			String devStatusCode=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailRpr/DevStatusCode");
			String devPtrMedia=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailRpr/DevPtrMedia");
			String devPtrRetractBin=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailRpr/DevPtrRetractBin");
			String devPtrInk=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailRpr/DevPtrInk");
			String devPtrToner=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailRpr/DevPtrToner");
			String devPtrSupplyLevel=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailRpr/DevPtrSupplyLevel");
										
			faultMap.put("DevDetailRpr/DevStatusCode",devStatusCode);			
			faultMap.put("DevDetailRpr/DevPtrMedia",devPtrMedia);			
			faultMap.put("DevDetailRpr/DevPtrRetractBin",devPtrRetractBin);			
			faultMap.put("DevDetailRpr/DevPtrInk",devPtrInk);			
			faultMap.put("DevDetailRpr/DevPtrToner",devPtrToner);			
			faultMap.put("DevDetailRpr/DevPtrSupplyLevel",devPtrSupplyLevel);
		}

		else if(devType.equalsIgnoreCase("JPR")){

			String devStatusCode=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailJpr/DevStatusCode");
			String devPtrMedia=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailJpr/DevPtrMedia");
			String devPtrRetractBin=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailJpr/DevPtrRetractBin");
			String devPtrInk=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailJpr/DevPtrInk");
			String devPtrToner=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailJpr/DevPtrToner");
			String devPtrSupplyLevel=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailJpr/DevPtrSupplyLevel");
						
			faultMap.put("DevDetailJpr/DevStatusCode",devStatusCode);			
			faultMap.put("DevDetailJpr/DevPtrMedia",devPtrMedia);			
			faultMap.put("DevDetailJpr/DevPtrRetractBin",devPtrRetractBin);			
			faultMap.put("DevDetailJpr/DevPtrInk",devPtrInk);			
			faultMap.put("DevDetailJpr/DevPtrToner",devPtrToner);			
			faultMap.put("DevDetailJpr/DevPtrSupplyLevel",devPtrSupplyLevel);
		}
		
		else if(devType.equalsIgnoreCase("PBK")){
			
			String devStatusCode=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailPbk/DevStatusCode");
			String devPtrMedia=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailPbk/DevPtrMedia");
			String devPtrRetractBin=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailPbk/DevPtrRetractBin");
			String devPtrInk=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailPbk/DevPtrInk");
			String devPtrToner=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailPbk/DevPtrToner");
			
			faultMap.put("DevDetailPbk/DevStatusCode",devStatusCode);			
			faultMap.put("DevDetailPbk/DevPtrMedia",devPtrMedia);			
			faultMap.put("DevDetailPbk/DevPtrRetractBin",devPtrRetractBin);			
			faultMap.put("DevDetailPbk/DevPtrInk",devPtrInk);			
			faultMap.put("DevDetailPbk/DevPtrToner",devPtrToner);
		}
		
		else if(devType.equalsIgnoreCase("CDM")){

			String devStatusCode=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCdm/DevStatusCode");
			String devCdmSafeDoor=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCdm/DevCdmSafeDoor");
			String devCdmCashUnits=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCdm/DevCdmCashUnits");
			String devCdmIntermediateStacker=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCdm/DevCdmIntermediateStacker");
			
			faultMap.put("DevDetailCdm/DevStatusCode",devStatusCode);			
			faultMap.put("DevDetailCdm/DevCdmSafeDoor",devCdmSafeDoor);			
			faultMap.put("DevDetailCdm/DevCdmCashUnits",devCdmCashUnits);			
			faultMap.put("DevDetailCdm/DevCdmIntermediateStacker",devCdmIntermediateStacker);
			
				
			if(getHasContent(document,"//FaultInfo/DevDetail/DevDetailCdm/DevCdmPosition")){
				
				String devCdmShutter=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCdm/DevCdmPosition/DevCdmShutter");
				String devCdmTransport=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCdm/DevCdmPosition/DevCdmTransport");
				String devCdmTransportItems=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCdm/DevCdmPosition/DevCdmTransportItems");
				
				faultMap.put("DevDetailCdm/DevCdmPosition/DevCdmShutter",devCdmShutter);				
				faultMap.put("DevDetailCdm/DevCdmPosition/DevCdmTransport",devCdmTransport);				
				faultMap.put("DevDetailCdm/DevCdmPosition/DevCdmTransportItems",devCdmTransportItems);
			}
			
			if(getHasContent(document,"//FaultInfo/DevDetail/DevDetailCdm/DevCdmBin")){
				
				String DevCdmPUID=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCdm/DevCdmBin/DevCdmPUID");
				String DevCdmPUBinStatus=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCdm/DevCdmBin/DevCdmPUBinStatus");
				String DevCdmPUCurrentCount=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCdm/DevCdmBin/DevCdmPUCurrentCount");
				String DevCdmPURejectCount=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCdm/DevCdmBin/DevCdmPURejectCount");
				String DevCdmPCUID=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCdm/DevCdmBin/DevCdmPCUID");
				String DevCdmCUID=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCdm/DevCdmBin/DevCdmCUID");
				String DevCdmCUCurrency=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCdm/DevCdmBin/DevCdmCUCurrency");
				String DevCdmCUNoteValue=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCdm/DevCdmBin/DevCdmCUNoteValue");
				String DevCdmCUCurrentCount=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCdm/DevCdmBin/DevCdmCUCurrentCount");
				String DevCdmCURejectCount=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCdm/DevCdmBin/DevCdmCURejectCount");
				String DevCdmCUType=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCdm/DevCdmBin/DevCdmCUType");
				String DevCdmCUBinStatus=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCdm/DevCdmBin/DevCdmCUBinStatus");
				
				faultMap.put("DevDetailCdm/DevCdmBin/DevCdmPUID",DevCdmPUID);				
				faultMap.put("DevDetailCdm/DevCdmBin/DevCdmPUBinStatus",DevCdmPUBinStatus);				
				faultMap.put("DevDetailCdm/DevCdmBin/DevCdmPUCurrentCount",DevCdmPUCurrentCount);				
				faultMap.put("DevDetailCdm/DevCdmBin/DevCdmPURejectCount",DevCdmPURejectCount);				
				faultMap.put("DevDetailCdm/DevCdmBin/DevCdmPCUID",DevCdmPCUID);				
				faultMap.put("DevDetailCdm/DevCdmBin/DevCdmCUID",DevCdmCUID);				
				faultMap.put("DevDetailCdm/DevCdmBin/DevCdmCUCurrency",DevCdmCUCurrency);				
				faultMap.put("DevDetailCdm/DevCdmBin/DevCdmCUNoteValue",DevCdmCUNoteValue);				
				faultMap.put("DevDetailCdm/DevCdmBin/DevCdmCUCurrentCount",DevCdmCUCurrentCount);				
				faultMap.put("DevDetailCdm/DevCdmBin/DevCdmCURejectCount",DevCdmCURejectCount);				
				faultMap.put("DevDetailCdm/DevCdmBin/DevCdmCUType",DevCdmCUType);				
				faultMap.put("DevDetailCdm/DevCdmBin/DevCdmCUBinStatus",DevCdmCUBinStatus);				
			}
		}
		else if(devType.equalsIgnoreCase("PIN")){
		
			String devStatusCode=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailPin/DevStatusCode");
		    faultMap.put("DevDetailPin/DevStatusCode",devStatusCode);
		}
		
		else if(devType.equalsIgnoreCase("CHK")){
			
			String devStatusCode=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailChk/DevStatusCode");
			String devChkMedia=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailChk/DevChkMedia");
			String devChkInk=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailChk/DevChkInk");
			
			faultMap.put("DevDetailChk/DevStatusCode",devStatusCode);			
			faultMap.put("DevDetailChk/DevChkMedia",devChkMedia);			
			faultMap.put("DevDetailChk/DevChkInk",devChkInk);	
			
		}
		else if(devType.equalsIgnoreCase("DEP")){
	
			String devStatusCode=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailDep/DevStatusCode");
			String devDepTransport=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailDep/DevDepTransport");
			String devDepContainer=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailDep/DevDepContainer");
			String devDepEnvelopeSupply=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailDep/DevDepEnvelopeSupply");
			String devDepEnvelopeDispenser=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailDep/DevDepEnvelopeDispenser");
			String devDepPrinter=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailDep/DevDepPrinter");
			String devDepToner=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailDep/DevDepToner");
			String devDepShutter=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailDep/DevDepShutter");
			String devDepNumOfDeposits=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailDep/DevDepNumOfDeposits");
								
			faultMap.put("DevDetailDep/DevStatusCode",devStatusCode);			
			faultMap.put("DevDetailDep/DevDepTransport",devDepTransport);		
			faultMap.put("DevDetailDep/DevDepContainer",devDepContainer);			
			faultMap.put("DevDetailDep/DevDepEnvelopeSupply",devDepEnvelopeSupply);			
			faultMap.put("DevDetailDep/DevDepEnvelopeDispenser",devDepEnvelopeDispenser);			
			faultMap.put("DevDetailDep/DevDepPrinter",devDepPrinter);			
			faultMap.put("DevDetailDep/DevDepToner",devDepToner);		
			faultMap.put("DevDetailDep/DevDepShutter",devDepShutter);			
			faultMap.put("DevDetailDep/DevDepNumOfDeposits",devDepNumOfDeposits);
		}
		
		else if(devType.equalsIgnoreCase("TTU")){
			
			String devStatusCode=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailTtu/DevStatusCode");
			faultMap.put("DevDetailTtu/DevStatusCode",devStatusCode);			
		}
		
		else if(devType.equalsIgnoreCase("SIU")){
			
            String devStatusCode=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevStatusCode");			
			faultMap.put("DevDetailSiu/DevStatusCode",devStatusCode);
				
			
			if(getHasContent(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuSensorStatus")){
				
				String devSiuOperatorSwitch=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuSensorStatus/DevSiuOperatorSwitch");
				String devSiuTamper=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuSensorStatus/DevSiuTamper");
				String devSiuSeismic=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuSensorStatus/DevSiuSeismic");
				String devSiuHeat=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuSensorStatus/DevSiuHeat");
				String devSiuProximity=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuSensorStatus/DevSiuProximity");
				String devSiuAmbientLight=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuSensorStatus/DevSiuAmbientLight");
																	
				faultMap.put("DevDetailSiu/DevSiuSensorStatus/DevSiuOperatorSwitch",devSiuOperatorSwitch);				
				faultMap.put("DevDetailSiu/DevSiuSensorStatus/DevSiuTamper",devSiuTamper);				
				faultMap.put("DevDetailSiu/DevSiuSensorStatus/DevSiuSeismic",devSiuSeismic);				
				faultMap.put("DevDetailSiu/DevSiuSensorStatus/DevSiuHeat",devSiuHeat);				
				faultMap.put("DevDetailSiu/DevSiuSensorStatus/DevSiuProximity",devSiuProximity);				
				faultMap.put("DevDetailSiu/DevSiuSensorStatus/DevSiuAmbientLight",devSiuAmbientLight);
			}
			
			if(getHasContent(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuDoorStatus")){
				
				String devSiuCabinet=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuDoorStatus/DevSiuCabinet");
				String devSiuSafe=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuDoorStatus/DevSiuSafe");
				String devSiuVandalShield=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuDoorStatus/DevSiuVandalShield");

				faultMap.put("DevDetailSiu/DevSiuDoorStatus/DevSiuCabinet",devSiuCabinet);				
				faultMap.put("DevDetailSiu/DevSiuDoorStatus/DevSiuSafe",devSiuSafe);				
				faultMap.put("DevDetailSiu/DevSiuDoorStatus/DevSiuVandalShield",devSiuVandalShield);
			}
			
			if(getHasContent(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuIndicatorStatus")){
				
				String devSiuOpenClose=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuIndicatorStatus/DevSiuOpenClose");
				String devSiuFasciaLight=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuIndicatorStatus/DevSiuFasciaLight");
				String devSiuAudio=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuIndicatorStatus/DevSiuAudio");
				String devSiuHeating=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuIndicatorStatus/DevSiuHeating");
				
				faultMap.put("DevDetailSiu/DevSiuIndicatorStatus/DevSiuOpenClose",devSiuOpenClose);				
				faultMap.put("DevDetailSiu/DevSiuIndicatorStatus/DevSiuFasciaLight",devSiuFasciaLight);				
				faultMap.put("DevDetailSiu/DevSiuIndicatorStatus/DevSiuAudio",devSiuAudio);				
				faultMap.put("DevDetailSiu/DevSiuIndicatorStatus/DevSiuHeating",devSiuHeating);
			}
			
			if(getHasContent(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuAuxiliaryStatus")){
				
				String devSiuVolume=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuAuxiliaryStatus/DevSiuVolume");
				String devSiuUps=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuAuxiliaryStatus/DevSiuUps");
				String devSiuAudibleAlarm=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuAuxiliaryStatus/DevSiuAudibleAlarm");
				
				faultMap.put("DevDetailSiu/DevSiuAuxiliaryStatus/DevSiuVolume",devSiuVolume);				
				faultMap.put("DevDetailSiu/DevSiuAuxiliaryStatus/DevSiuUps",devSiuUps);				
				faultMap.put("DevDetailSiu/DevSiuAuxiliaryStatus/DevSiuAudibleAlarm",devSiuAudibleAlarm);				
			}
			
			
			if(getHasContent(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuGuidelightStatus")){
				
				String devSiuIdcGuidelight=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuGuidelightStatus/DevSiuIdcGuidelight");
				String devSiuCdmGuidelight=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuGuidelightStatus/DevSiuCdmGuidelight");
				String devSiuCoinOutGuidelight=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuGuidelightStatus/DevSiuCoinOutGuidelight");
				String devSiuReceiptGuidelight=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuGuidelightStatus/DevSiuReceiptGuidelight");
				String devSiuPassbookGuidelight=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuGuidelightStatus/DevSiuPassbookGuidelight");
				String devSiuDepGuidelight=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuGuidelightStatus/DevSiuDepGuidelight");
				String devSiuChkGuidelight=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuGuidelightStatus/DevSiuChkGuidelight");
				String devSiuCimGuidelight=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuGuidelightStatus/DevSiuCimGuidelight");
				String devSiuDocumentGuidelight=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuGuidelightStatus/DevSiuDocumentGuidelight");
				String devSiuCoinInGuidelight=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuGuidelightStatus/DevSiuCoinInGuidelight");
				String devSiuScannerGuidelight=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailSiu/DevSiuGuidelightStatus/DevSiuScannerGuidelight");
																			
				faultMap.put("DevDetailSiu/DevSiuGuidelightStatus/DevSiuIdcGuidelight",devSiuIdcGuidelight);				
				faultMap.put("DevDetailSiu/DevSiuGuidelightStatus/DevSiuCdmGuidelight",devSiuCdmGuidelight);				
				faultMap.put("DevDetailSiu/DevSiuGuidelightStatus/DevSiuCoinOutGuidelight",devSiuCoinOutGuidelight);				
				faultMap.put("DevDetailSiu/DevSiuGuidelightStatus/DevSiuReceiptGuidelight",devSiuReceiptGuidelight);				
				faultMap.put("DevDetailSiu/DevSiuGuidelightStatus/DevSiuPassbookGuidelight",devSiuPassbookGuidelight);				
				faultMap.put("DevDetailSiu/DevSiuGuidelightStatus/DevSiuDepGuidelight",devSiuDepGuidelight);				
				faultMap.put("DevDetailSiu/DevSiuGuidelightStatus/DevSiuChkGuidelight",devSiuChkGuidelight);				
				faultMap.put("DevDetailSiu/DevSiuGuidelightStatus/DevSiuCimGuidelight",devSiuCimGuidelight);				
				faultMap.put("DevDetailSiu/DevSiuGuidelightStatus/DevSiuDocumentGuidelight",devSiuDocumentGuidelight);				
				faultMap.put("DevDetailSiu/DevSiuGuidelightStatus/DevSiuCoinInGuidelight",devSiuCoinInGuidelight);				
				faultMap.put("DevDetailSiu/DevSiuGuidelightStatus/DevSiuScannerGuidelight",devSiuScannerGuidelight);
			}
		}
		
		else if(devType.equalsIgnoreCase("CAM")){
			
			String devStatusCode=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCam/DevStatusCode");
			String devCamStatusArea=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCam/DevCamStatusArea");
			String devCamStatusMedia=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCam/DevCamStatusMedia");
			String devCamStatusState=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCam/DevCamStatusState");
			String devCamStatusPictures=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCam/DevCamStatusPictures");

			faultMap.put("DevDetailCam/DevStatusCode",devStatusCode);
			faultMap.put("DevDetailCam/DevCamStatusArea",devCamStatusArea);
			faultMap.put("DevDetailCam/DevCamStatusMedia",devCamStatusMedia);
			faultMap.put("DevDetailCam/DevCamStatusState",devCamStatusState);
			faultMap.put("DevDetailCam/DevCamStatusPictures",devCamStatusPictures);
		}
		
		else if(devType.equalsIgnoreCase("CIM")){

			String devStatusCode=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCim/DevStatusCode");
			String devCimSafeDoor=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCim/DevCimSafeDoor");
			String devCimCashUnits=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCim/DevCimCashUnits");
			String devCimIntermediateStacker=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCim/DevCimIntermediateStacker");
			
			faultMap.put("DevDetailCim/DevStatusCode",devStatusCode);			
			faultMap.put("DevDetailCim/DevCimSafeDoor",devCimSafeDoor);			
			faultMap.put("DevDetailCim/DevCimCashUnits",devCimCashUnits);			
			faultMap.put("DevDetailCim/DevCimIntermediateStacker",devCimIntermediateStacker);
				
			
			if(getHasContent(document,"//FaultInfo/DevDetail/DevDetailCim/DevCimPosition")){
				
				String devCimShutter=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCim/DevCimPosition/DevCimShutter");
				String devCimTransport=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCim/DevCimPosition/DevCimTransport");

				faultMap.put("DevDetailCim/DevCimPosition/DevCimShutter",devCimShutter);				
				faultMap.put("DevDetailCim/DevCimPosition/DevCimTransport",devCimTransport);
			}
			
			
			if(getHasContent(document,"//FaultInfo/DevDetail/DevDetailCim/DevCimBin")){
				
				String DevCimPUID=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCim/DevCimBin/DevCimPUID");
				String DevCimPUBinStatus=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCim/DevCimBin/DevCimPUBinStatus");
				String DevCimPUCount=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCim/DevCimBin/DevCimPUCount");
				String DevCimPUCashInCount=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCim/DevCimBin/DevCimPUCashInCount");
				String DevCimPCUID=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCim/DevCimBin/DevCimPCUID");
				String DevCimCUID=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCim/DevCimBin/DevCimCUID");
				String DevCimCUCurrency=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCim/DevCimBin/DevCimCUCurrency");
				String DevCimCUNoteValue=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCim/DevCimBin/DevCimCUNoteValue");
				String DevCimCUCount=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCim/DevCimBin/DevCimCUCount");
				String DevCimCUCashInCount=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCim/DevCimBin/DevCimCUCashInCount");
				String DevCimCUType=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCim/DevCimBin/DevCimCUType");
				String DevCimCUBinStatus=getSingleNodeValue(document,"//FaultInfo/DevDetail/DevDetailCim/DevCimBin/DevCimCUBinStatus");
				
				faultMap.put("DevDetailCim/DevCimBin/DevCimPUID",DevCimPUID);				
				faultMap.put("DevDetailCim/DevCimBin/DevCimPUBinStatus",DevCimPUBinStatus);				
				faultMap.put("DevDetailCim/DevCimBin/DevCimPUCount",DevCimPUCount);				
				faultMap.put("DevDetailCim/DevCimBin/DevCimPUCashInCount",DevCimPUCashInCount);				
				faultMap.put("DevDetailCdm/DevCimBin/DevCimPCUID",DevCimPCUID);				
				faultMap.put("DevDetailCim/DevCimBin/DevCimCUID",DevCimCUID);				
				faultMap.put("DevDetailCim/DevCimBin/DevCimCUCurrency",DevCimCUCurrency);				
				faultMap.put("DevDetailCim/DevCimBin/DevCimCUNoteValue",DevCimCUNoteValue);				
				faultMap.put("DevDetailCim/DevCimBin/DevCimCUCount",DevCimCUCount);				
				faultMap.put("DevDetailCim/DevCimBin/DevCimCUCashInCount",DevCimCUCashInCount);				
				faultMap.put("DevDetailCim/DevCimBin/DevCimCUType",DevCimCUType);				
				faultMap.put("DevDetailCim/DevCimBin/DevCimCUBinStatus",DevCimCUBinStatus);				
			}			
		}
		return faultMap;
		
	}
	
	/**
	 * 从消息体XML文档中取得状态信息
	 * @param document - 信息的xml
	 * @return HashMap - 信息的map
	 */
	
	public static HashMap<String,String> getStatusInfoMap(Document document){
		
		if(document==null){
			log.error("getStatusInfoMap() document is null");
			return null;
		}
		
		HashMap<String,String> StatusMap=new HashMap<String,String>();
		
		
		String terminalId=getSingleNodeValue(document,"//StatusInfo/TerminalId");
		String terminalIp=getSingleNodeValue(document,"//StatusInfo/TerminalIp");
		String ActionType=getSingleNodeValue(document,"//StatusInfo/ActionType");
		String DateTime=getSingleNodeValue(document,"//StatusInfo/DateTime");
		
		StatusMap.put("TerminalId",terminalId);		
		StatusMap.put("TerminalIp",terminalIp);
		StatusMap.put("ActionType",ActionType);	
		StatusMap.put("DateTime",DateTime);	
				
		
		if(getHasContent(document,"//StatusInfo/DevDetail/DevDetailIdc")){
			
			String devStatusCode=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailIdc/DevStatusCode");
			String devIdcMedia=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailIdc/DevIdcMedia");
			String devIdcRetainBin=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailIdc/DevIdcRetainBin");
			String devIdcCards=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailIdc/DevIdcCards");
			
			StatusMap.put("DevDetailIdc/DevStatusCode",devStatusCode);		
			StatusMap.put("DevDetailIdc/DevIdcMedia",devIdcMedia);	
			StatusMap.put("DevDetailIdc/DevIdcRetainBin",devIdcRetainBin);			
			StatusMap.put("DevDetailIdc/DevIdcCards",devIdcCards);
		}
		
		if(getHasContent(document,"//StatusInfo/DevDetail/DevDetailSpr")){
			
			String devStatusCode=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSpr/DevStatusCode");
			String devPtrMedia=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSpr/DevPtrMedia");
			String devPtrRetractBin=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSpr/DevPtrRetractBin");
			String devPtrInk=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSpr/DevPtrInk");
			String devPtrToner=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSpr/DevPtrToner");
			String devPtrSupplyLevel=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSpr/DevPtrSupplyLevel");

			StatusMap.put("DevDetailSpr/DevStatusCode",devStatusCode);			
			StatusMap.put("DevDetailSpr/DevPtrMedia",devPtrMedia);			
			StatusMap.put("DevDetailSpr/DevPtrRetractBin",devPtrRetractBin);			
			StatusMap.put("DevDetailSpr/DevPtrInk",devPtrInk);			
			StatusMap.put("DevDetailSpr/DevPtrToner",devPtrToner);			
			StatusMap.put("DevDetailSpr/DevPtrSupplyLevel",devPtrSupplyLevel);
		}
		
		if(getHasContent(document,"//StatusInfo/DevDetail/DevDetailRpr")){
			
			String devStatusCode=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailRpr/DevStatusCode");
			String devPtrMedia=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailRpr/DevPtrMedia");
			String devPtrRetractBin=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailRpr/DevPtrRetractBin");
			String devPtrInk=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailRpr/DevPtrInk");
			String devPtrToner=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailRpr/DevPtrToner");
			String devPtrSupplyLevel=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailRpr/DevPtrSupplyLevel");
										
			StatusMap.put("DevDetailRpr/DevStatusCode",devStatusCode);			
			StatusMap.put("DevDetailRpr/DevPtrMedia",devPtrMedia);			
			StatusMap.put("DevDetailRpr/DevPtrRetractBin",devPtrRetractBin);			
			StatusMap.put("DevDetailRpr/DevPtrInk",devPtrInk);			
			StatusMap.put("DevDetailRpr/DevPtrToner",devPtrToner);			
			StatusMap.put("DevDetailRpr/DevPtrSupplyLevel",devPtrSupplyLevel);
		}
		
		if(getHasContent(document,"//StatusInfo/DevDetail/DevDetailJpr")){
			
			String devStatusCode=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailJpr/DevStatusCode");
			String devPtrMedia=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailJpr/DevPtrMedia");
			String devPtrRetractBin=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailJpr/DevPtrRetractBin");
			String devPtrInk=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailJpr/DevPtrInk");
			String devPtrToner=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailJpr/DevPtrToner");
			String devPtrSupplyLevel=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailJpr/DevPtrSupplyLevel");
						
			StatusMap.put("DevDetailJpr/DevStatusCode",devStatusCode);			
			StatusMap.put("DevDetailJpr/DevPtrMedia",devPtrMedia);			
			StatusMap.put("DevDetailJpr/DevPtrRetractBin",devPtrRetractBin);			
			StatusMap.put("DevDetailJpr/DevPtrInk",devPtrInk);			
			StatusMap.put("DevDetailJpr/DevPtrToner",devPtrToner);			
			StatusMap.put("DevDetailJpr/DevPtrSupplyLevel",devPtrSupplyLevel);
		}
		
		
		if(getHasContent(document,"//StatusInfo/DevDetail/DevDetailPbk")){
			
			String devStatusCode=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailPbk/DevStatusCode");
			String devPtrMedia=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailPbk/DevPtrMedia");
			String devPtrRetractBin=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailPbk/DevPtrRetractBin");
			String devPtrInk=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailPbk/DevPtrInk");
			String devPtrToner=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailPbk/DevPtrToner");
			
			StatusMap.put("DevDetailPbk/DevStatusCode",devStatusCode);			
			StatusMap.put("DevDetailPbk/DevPtrMedia",devPtrMedia);			
			StatusMap.put("DevDetailPbk/DevPtrRetractBin",devPtrRetractBin);			
			StatusMap.put("DevDetailPbk/DevPtrInk",devPtrInk);			
			StatusMap.put("DevDetailPbk/DevPtrToner",devPtrToner);
		}
		
		if(getHasContent(document,"//StatusInfo/DevDetail/DevDetailCdm")){
			
			String devStatusCode=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCdm/DevStatusCode");
			String devCdmSafeDoor=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCdm/DevCdmSafeDoor");
			String devCdmCashUnits=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCdm/DevCdmCashUnits");
			String devCdmIntermediateStacker=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCdm/DevCdmIntermediateStacker");
			
			StatusMap.put("DevDetailCdm/DevStatusCode",devStatusCode);			
			StatusMap.put("DevDetailCdm/DevCdmSafeDoor",devCdmSafeDoor);			
			StatusMap.put("DevDetailCdm/DevCdmCashUnits",devCdmCashUnits);			
			StatusMap.put("DevDetailCdm/DevCdmIntermediateStacker",devCdmIntermediateStacker);
			
				
			if(getHasContent(document,"//StatusInfo/DevDetail/DevDetailCdm/DevCdmPosition")){
				
				String devCdmShutter=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCdm/DevCdmPosition/DevCdmShutter");
				String devCdmTransport=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCdm/DevCdmPosition/DevCdmTransport");
				String devCdmTransportItems=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCdm/DevCdmPosition/DevCdmTransportItems");
				
				StatusMap.put("DevDetailCdm/DevCdmPosition/DevCdmShutter",devCdmShutter);				
				StatusMap.put("DevDetailCdm/DevCdmPosition/DevCdmTransport",devCdmTransport);				
				StatusMap.put("DevDetailCdm/DevCdmPosition/DevCdmTransportItems",devCdmTransportItems);
			}
			
			if(getHasContent(document,"//StatusInfo/DevDetail/DevDetailCdm/DevCdmBin")){
				
				String DevCdmPUID=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCdm/DevCdmBin/DevCdmPUID");
				String DevCdmPUBinStatus=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCdm/DevCdmBin/DevCdmPUBinStatus");
				String DevCdmPUCurrentCount=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCdm/DevCdmBin/DevCdmPUCurrentCount");
				String DevCdmPURejectCount=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCdm/DevCdmBin/DevCdmPURejectCount");
				String DevCdmPCUID=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCdm/DevCdmBin/DevCdmPCUID");
				String DevCdmCUID=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCdm/DevCdmBin/DevCdmCUID");
				String DevCdmCUCurrency=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCdm/DevCdmBin/DevCdmCUCurrency");
				String DevCdmCUNoteValue=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCdm/DevCdmBin/DevCdmCUNoteValue");
				String DevCdmCUCurrentCount=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCdm/DevCdmBin/DevCdmCUCurrentCount");
				String DevCdmCURejectCount=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCdm/DevCdmBin/DevCdmCURejectCount");
				String DevCdmCUType=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCdm/DevCdmBin/DevCdmCUType");
				String DevCdmCUBinStatus=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCdm/DevCdmBin/DevCdmCUBinStatus");
				
				StatusMap.put("DevDetailCdm/DevCdmBin/DevCdmPUID",DevCdmPUID);				
				StatusMap.put("DevDetailCdm/DevCdmBin/DevCdmPUBinStatus",DevCdmPUBinStatus);				
				StatusMap.put("DevDetailCdm/DevCdmBin/DevCdmPUCurrentCount",DevCdmPUCurrentCount);				
				StatusMap.put("DevDetailCdm/DevCdmBin/DevCdmPURejectCount",DevCdmPURejectCount);				
				StatusMap.put("DevDetailCdm/DevCdmBin/DevCdmPCUID",DevCdmPCUID);				
				StatusMap.put("DevDetailCdm/DevCdmBin/DevCdmCUID",DevCdmCUID);				
				StatusMap.put("DevDetailCdm/DevCdmBin/DevCdmCUCurrency",DevCdmCUCurrency);				
				StatusMap.put("DevDetailCdm/DevCdmBin/DevCdmCUNoteValue",DevCdmCUNoteValue);				
				StatusMap.put("DevDetailCdm/DevCdmBin/DevCdmCUCurrentCount",DevCdmCUCurrentCount);				
				StatusMap.put("DevDetailCdm/DevCdmBin/DevCdmCURejectCount",DevCdmCURejectCount);				
				StatusMap.put("DevDetailCdm/DevCdmBin/DevCdmCUType",DevCdmCUType);				
				StatusMap.put("DevDetailCdm/DevCdmBin/DevCdmCUBinStatus",DevCdmCUBinStatus);				
			}
		}
		
		if(getHasContent(document,"//StatusInfo/DevDetail/DevDetailPin")){
			
			String devStatusCode=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailPin/DevStatusCode");			
			StatusMap.put("DevDetailPin/DevStatusCode",devStatusCode);			
		}
		
		
		if(getHasContent(document,"//StatusInfo/DevDetail/DevDetailChk")){
			
			String devStatusCode=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailChk/DevStatusCode");
			String devChkMedia=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailChk/DevChkMedia");
			String devChkInk=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailChk/DevChkInk");
			
			StatusMap.put("DevDetailChk/DevStatusCode",devStatusCode);			
			StatusMap.put("DevDetailChk/DevChkMedia",devChkMedia);			
			StatusMap.put("DevDetailChk/DevChkInk",devChkInk);			
		}
		
		if(getHasContent(document,"//StatusInfo/DevDetail/DevDetailDep")){
			
			String devStatusCode=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailDep/DevStatusCode");
			String devDepTransport=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailDep/DevDepTransport");
			String devDepContainer=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailDep/DevDepContainer");
			String devDepEnvelopeSupply=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailDep/DevDepEnvelopeSupply");
			String devDepEnvelopeDispenser=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailDep/DevDepEnvelopeDispenser");
			String devDepPrinter=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailDep/DevDepPrinter");
			String devDepToner=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailDep/DevDepToner");
			String devDepShutter=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailDep/DevDepShutter");
			String devDepNumOfDeposits=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailDep/DevDepNumOfDeposits");
								
			StatusMap.put("DevDetailDep/DevStatusCode",devStatusCode);			
			StatusMap.put("DevDetailDep/DevDepTransport",devDepTransport);		
			StatusMap.put("DevDetailDep/DevDepContainer",devDepContainer);			
			StatusMap.put("DevDetailDep/DevDepEnvelopeSupply",devDepEnvelopeSupply);			
			StatusMap.put("DevDetailDep/DevDepEnvelopeDispenser",devDepEnvelopeDispenser);			
			StatusMap.put("DevDetailDep/DevDepPrinter",devDepPrinter);			
			StatusMap.put("DevDetailDep/DevDepToner",devDepToner);		
			StatusMap.put("DevDetailDep/DevDepShutter",devDepShutter);			
			StatusMap.put("DevDetailDep/DevDepNumOfDeposits",devDepNumOfDeposits);
		}
		
		
		if(getHasContent(document,"//StatusInfo/DevDetail/DevDetailTtu")){
			
			String devStatusCode=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailTtu/DevStatusCode");
			StatusMap.put("DevDetailTtu/DevStatusCode",devStatusCode);
			
		}
		
		if(getHasContent(document,"//StatusInfo/DevDetail/DevDetailSiu")){
			
			String devStatusCode=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevStatusCode");
			
			StatusMap.put("DevDetailSiu/DevStatusCode",devStatusCode);
				
			
			if(getHasContent(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuSensorStatus")){
				
				String devSiuOperatorSwitch=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuSensorStatus/DevSiuOperatorSwitch");
				String devSiuTamper=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuSensorStatus/DevSiuTamper");
				String devSiuSeismic=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuSensorStatus/DevSiuSeismic");
				String devSiuHeat=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuSensorStatus/DevSiuHeat");
				String devSiuProximity=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuSensorStatus/DevSiuProximity");
				String devSiuAmbientLight=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuSensorStatus/DevSiuAmbientLight");
																	
				StatusMap.put("DevDetailSiu/DevSiuSensorStatus/DevSiuOperatorSwitch",devSiuOperatorSwitch);				
				StatusMap.put("DevDetailSiu/DevSiuSensorStatus/DevSiuTamper",devSiuTamper);				
				StatusMap.put("DevDetailSiu/DevSiuSensorStatus/DevSiuSeismic",devSiuSeismic);				
				StatusMap.put("DevDetailSiu/DevSiuSensorStatus/DevSiuHeat",devSiuHeat);				
				StatusMap.put("DevDetailSiu/DevSiuSensorStatus/DevSiuProximity",devSiuProximity);				
				StatusMap.put("DevDetailSiu/DevSiuSensorStatus/DevSiuAmbientLight",devSiuAmbientLight);
			}
			
			if(getHasContent(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuDoorStatus")){
				
				String devSiuCabinet=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuDoorStatus/DevSiuCabinet");
				String devSiuSafe=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuDoorStatus/DevSiuSafe");
				String devSiuVandalShield=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuDoorStatus/DevSiuVandalShield");

				StatusMap.put("DevDetailSiu/DevSiuDoorStatus/DevSiuCabinet",devSiuCabinet);				
				StatusMap.put("DevDetailSiu/DevSiuDoorStatus/DevSiuSafe",devSiuSafe);				
				StatusMap.put("DevDetailSiu/DevSiuDoorStatus/DevSiuVandalShield",devSiuVandalShield);
			}
			
			if(getHasContent(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuIndicatorStatus")){
				
				String devSiuOpenClose=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuIndicatorStatus/DevSiuOpenClose");
				String devSiuFasciaLight=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuIndicatorStatus/DevSiuFasciaLight");
				String devSiuAudio=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuIndicatorStatus/DevSiuAudio");
				String devSiuHeating=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuIndicatorStatus/DevSiuHeating");
				
				StatusMap.put("DevDetailSiu/DevSiuIndicatorStatus/DevSiuOpenClose",devSiuOpenClose);				
				StatusMap.put("DevDetailSiu/DevSiuIndicatorStatus/DevSiuFasciaLight",devSiuFasciaLight);				
				StatusMap.put("DevDetailSiu/DevSiuIndicatorStatus/DevSiuAudio",devSiuAudio);				
				StatusMap.put("DevDetailSiu/DevSiuIndicatorStatus/DevSiuHeating",devSiuHeating);
			}
			
			if(getHasContent(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuAuxiliaryStatus")){
				
				String devSiuVolume=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuAuxiliaryStatus/DevSiuVolume");
				String devSiuUps=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuAuxiliaryStatus/DevSiuUps");
				String devSiuAudibleAlarm=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuAuxiliaryStatus/DevSiuAudibleAlarm");
				
				StatusMap.put("DevDetailSiu/DevSiuAuxiliaryStatus/DevSiuVolume",devSiuVolume);				
				StatusMap.put("DevDetailSiu/DevSiuAuxiliaryStatus/DevSiuUps",devSiuUps);				
				StatusMap.put("DevDetailSiu/DevSiuAuxiliaryStatus/DevSiuAudibleAlarm",devSiuAudibleAlarm);				
			}
			
			
			if(getHasContent(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuGuidelightStatus")){
				
				String devSiuIdcGuidelight=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuGuidelightStatus/DevSiuIdcGuidelight");
				String devSiuCdmGuidelight=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuGuidelightStatus/DevSiuCdmGuidelight");
				String devSiuCoinOutGuidelight=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuGuidelightStatus/DevSiuCoinOutGuidelight");
				String devSiuReceiptGuidelight=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuGuidelightStatus/DevSiuReceiptGuidelight");
				String devSiuPassbookGuidelight=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuGuidelightStatus/DevSiuPassbookGuidelight");
				String devSiuDepGuidelight=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuGuidelightStatus/DevSiuDepGuidelight");
				String devSiuChkGuidelight=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuGuidelightStatus/DevSiuChkGuidelight");
				String devSiuCimGuidelight=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuGuidelightStatus/DevSiuCimGuidelight");
				String devSiuDocumentGuidelight=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuGuidelightStatus/DevSiuDocumentGuidelight");
				String devSiuCoinInGuidelight=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuGuidelightStatus/DevSiuCoinInGuidelight");
				String devSiuScannerGuidelight=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailSiu/DevSiuGuidelightStatus/DevSiuScannerGuidelight");
																			
				StatusMap.put("DevDetailSiu/DevSiuGuidelightStatus/DevSiuIdcGuidelight",devSiuIdcGuidelight);				
				StatusMap.put("DevDetailSiu/DevSiuGuidelightStatus/DevSiuCdmGuidelight",devSiuCdmGuidelight);				
				StatusMap.put("DevDetailSiu/DevSiuGuidelightStatus/DevSiuCoinOutGuidelight",devSiuCoinOutGuidelight);				
				StatusMap.put("DevDetailSiu/DevSiuGuidelightStatus/DevSiuReceiptGuidelight",devSiuReceiptGuidelight);				
				StatusMap.put("DevDetailSiu/DevSiuGuidelightStatus/DevSiuPassbookGuidelight",devSiuPassbookGuidelight);				
				StatusMap.put("DevDetailSiu/DevSiuGuidelightStatus/DevSiuDepGuidelight",devSiuDepGuidelight);				
				StatusMap.put("DevDetailSiu/DevSiuGuidelightStatus/DevSiuChkGuidelight",devSiuChkGuidelight);				
				StatusMap.put("DevDetailSiu/DevSiuGuidelightStatus/DevSiuCimGuidelight",devSiuCimGuidelight);				
				StatusMap.put("DevDetailSiu/DevSiuGuidelightStatus/DevSiuDocumentGuidelight",devSiuDocumentGuidelight);				
				StatusMap.put("DevDetailSiu/DevSiuGuidelightStatus/DevSiuCoinInGuidelight",devSiuCoinInGuidelight);				
				StatusMap.put("DevDetailSiu/DevSiuGuidelightStatus/DevSiuScannerGuidelight",devSiuScannerGuidelight);
			}			
		}
		
		
		
		if(getHasContent(document,"//StatusInfo/DevDetail/DevDetailCam")){
			
			String devStatusCode=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCam/DevStatusCode");
			String devCamStatusArea=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCam/DevCamStatusArea");
			String devCamStatusMedia=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCam/DevCamStatusMedia");
			String devCamStatusState=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCam/DevCamStatusState");
			String devCamStatusPictures=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCam/DevCamStatusPictures");
								
			StatusMap.put("DevDetailCam/DevStatusCode",devStatusCode);			
			StatusMap.put("DevDetailCam/DevCamStatusArea",devCamStatusArea);		
			StatusMap.put("DevDetailCam/DevCamStatusMedia",devCamStatusMedia);			
			StatusMap.put("DevDetailCam/DevCamStatusState",devCamStatusState);			
			StatusMap.put("DevDetailCam/DevCamStatusPictures",devCamStatusPictures);			
		}
		
		
		if(getHasContent(document,"//StatusInfo/DevDetail/DevDetailCim")){
			
			String devStatusCode=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCim/DevStatusCode");
			String devCimSafeDoor=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCim/DevCimSafeDoor");
			String devCimCashUnits=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCim/DevCimCashUnits");
			String devCimIntermediateStacker=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCim/DevCimIntermediateStacker");
			
			StatusMap.put("DevDetailCim/DevStatusCode",devStatusCode);			
			StatusMap.put("DevDetailCim/DevCimSafeDoor",devCimSafeDoor);			
			StatusMap.put("DevDetailCim/DevCimCashUnits",devCimCashUnits);			
			StatusMap.put("DevDetailCim/DevCimIntermediateStacker",devCimIntermediateStacker);
				
			
			if(getHasContent(document,"//StatusInfo/DevDetail/DevDetailCim/DevCimPosition")){
				
				String devCimShutter=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCim/DevCimPosition/DevCimShutter");
				String devCimTransport=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCim/DevCimPosition/DevCimTransport");

				StatusMap.put("DevDetailCim/DevCimPosition/DevCimShutter",devCimShutter);				
				StatusMap.put("DevDetailCim/DevCimPosition/DevCimTransport",devCimTransport);
			}
			
			
			if(getHasContent(document,"//StatusInfo/DevDetail/DevDetailCim/DevCimBin")){
				
				String DevCimPUID=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCim/DevCimBin/DevCimPUID");
				String DevCimPUBinStatus=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCim/DevCimBin/DevCimPUBinStatus");
				String DevCimPUCount=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCim/DevCimBin/DevCimPUCount");
				String DevCimPUCashInCount=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCim/DevCimBin/DevCimPUCashInCount");
				String DevCimPCUID=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCim/DevCimBin/DevCimPCUID");
				String DevCimCUID=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCim/DevCimBin/DevCimCUID");
				String DevCimCUCurrency=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCim/DevCimBin/DevCimCUCurrency");
				String DevCimCUNoteValue=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCim/DevCimBin/DevCimCUNoteValue");
				String DevCimCUCount=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCim/DevCimBin/DevCimCUCount");
				String DevCimCUCashInCount=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCim/DevCimBin/DevCimCUCashInCount");
				String DevCimCUType=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCim/DevCimBin/DevCimCUType");
				String DevCimCUBinStatus=getSingleNodeValue(document,"//StatusInfo/DevDetail/DevDetailCim/DevCimBin/DevCimCUBinStatus");
				
				StatusMap.put("DevDetailCim/DevCimBin/DevCimPUID",DevCimPUID);				
				StatusMap.put("DevDetailCim/DevCimBin/DevCimPUBinStatus",DevCimPUBinStatus);				
				StatusMap.put("DevDetailCim/DevCimBin/DevCimPUCount",DevCimPUCount);				
				StatusMap.put("DevDetailCim/DevCimBin/DevCimPUCashInCount",DevCimPUCashInCount);				
				StatusMap.put("DevDetailCdm/DevCimBin/DevCimPCUID",DevCimPCUID);				
				StatusMap.put("DevDetailCim/DevCimBin/DevCimCUID",DevCimCUID);				
				StatusMap.put("DevDetailCim/DevCimBin/DevCimCUCurrency",DevCimCUCurrency);				
				StatusMap.put("DevDetailCim/DevCimBin/DevCimCUNoteValue",DevCimCUNoteValue);				
				StatusMap.put("DevDetailCim/DevCimBin/DevCimCUCount",DevCimCUCount);				
				StatusMap.put("DevDetailCim/DevCimBin/DevCimCUCashInCount",DevCimCUCashInCount);				
				StatusMap.put("DevDetailCim/DevCimBin/DevCimCUType",DevCimCUType);				
				StatusMap.put("DevDetailCim/DevCimBin/DevCimCUBinStatus",DevCimCUBinStatus);				
			}			
		}
		
		return StatusMap;		
	}
	
	

	
	/**
	 * 从消息体XML文档中取得属性信息
	 * @param document - 信息的xml
	 * @return HashMap - 信息的map
	 */
	
	public static HashMap<String,String> getPropInfoMap(Document document){
		
		if(document==null){
			log.error("document is null");
			return null;
		}
		
		HashMap<String,String> propMap=new HashMap<String,String>();
		
		String terminalId=getSingleNodeValue(document,"//PropInfo/TerminalId");
		String terminalIp=getSingleNodeValue(document,"//PropInfo/TerminalIp");
		String DateTime=getSingleNodeValue(document,"//PropInfo/DateTime");
		
		propMap.put("TerminalId",terminalId);
		propMap.put("TerminalIp",terminalIp);
		propMap.put("DateTime",DateTime);	

		
		if(getHasContent(document,"//PropInfo/PropDetail/DevPropIdc")){
			
			String variant=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropIdc/Variant");
			String canEject=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropIdc/CanEject");
			String canCapture=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropIdc/CanCapture");
			String binCapacity=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropIdc/BinCapacity");
			String canDispense=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropIdc/CanDispense");
			String security=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropIdc/Security");
			String track1Read=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropIdc/Track1Read");
			String track1Write=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropIdc/Track1Write");
			String track2Read=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropIdc/Track2Read");
			String track2Write=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropIdc/Track2Write");
			String track3Read=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropIdc/Track3Read");
			String track3Write=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropIdc/Track3Write");
			String trackJisiiRead=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropIdc/TrackJisiiRead");
			String trackJisiiWrite=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropIdc/TrackJisiiWrite");
			

			propMap.put("DevPropIdc/Variant",variant);
			propMap.put("DevPropIdc/CanEject",canEject);
			propMap.put("DevPropIdc/CanCapture",canCapture);
			propMap.put("DevPropIdc/BinCapacity",binCapacity);
			propMap.put("DevPropIdc/CanDispense",canDispense);
			propMap.put("DevPropIdc/Security",security);
			propMap.put("DevPropIdc/Track1Read",track1Read);
			propMap.put("DevPropIdc/Track1Write",track1Write);
			propMap.put("DevPropIdc/Track2Read",track2Read);
			propMap.put("DevPropIdc/Track2Write",track2Write);
			propMap.put("DevPropIdc/Track3Read",track3Read);
			propMap.put("DevPropIdc/Track3Write",track3Write);
			propMap.put("DevPropIdc/TrackJisiiRead",trackJisiiRead);
			propMap.put("DevPropIdc/TrackJisiiWrite",trackJisiiWrite);
		}
		
		if(getHasContent(document,"//PropInfo/PropDetail/DevPropSpr")){
			String variant=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropSpr/Variant");
			String canEject=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropSpr/CanEject");
			String canCapture=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropSpr/CanCapture");
			String canStack=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropSpr/CanStack");
			String maxRetract=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropSpr/MaxRetract");
			String maxStack=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropSpr/MaxStack");
			
			propMap.put("DevPropSpr/Variant",variant);
		    propMap.put("DevPropSpr/CanEject",canEject);
			propMap.put("DevPropSpr/CanCapture",canCapture);
			propMap.put("DevPropSpr/CanStack",canStack);
			propMap.put("DevPropSpr/MaxRetract",maxRetract);
			propMap.put("DevPropSpr/MaxStack",maxStack);
		}
		
		if(getHasContent(document,"//PropInfo/PropDetail/DevPropRpr")){
			
			String variant=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropRpr/Variant");
			String canEject=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropRpr/CanEject");
			String canCapture=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropRpr/CanCapture");
			String canStack=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropRpr/CanStack");
			String maxRetract=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropRpr/MaxRetract");
			
			propMap.put("DevPropRpr/Variant",variant);
			propMap.put("DevPropRpr/CanEject",canEject);
			propMap.put("DevPropRpr/CanCapture",canCapture);
			propMap.put("DevPropRpr/CanStack",canStack);
			propMap.put("DevPropRpr/MaxRetract",maxRetract);
		}
		
		if(getHasContent(document,"//PropInfo/PropDetail/DevPropJpr")){
			
			String variant=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropJpr/Variant");
			String canEject=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropJpr/CanEject");
			String canCapture=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropJpr/CanCapture");
			String canStack=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropJpr/CanStack");

			propMap.put("DevPropJpr/Variant",variant);
			propMap.put("DevPropJpr/CanEject",canEject);
			propMap.put("DevPropJpr/CanCapture",canCapture);
			propMap.put("DevPropJpr/CanStack",canStack);
		}
		
		
		if(getHasContent(document,"//PropInfo/PropDetail/DevPropPbk")){
			
			String variant=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropPbk/Variant");
			String canEject=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropPbk/CanEject");
			String canCapture=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropPbk/CanCapture");
			String canStack=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropPbk/CanStack");
			String maxRetract=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropPbk/MaxRetract");
			String canTurnPage=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropPbk/CanTurnPage");
			String canDetectMediaTaken=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropPbk/CanDetectMediaTaken");
			
			propMap.put("DevPropPbk/Variant",variant);
			propMap.put("DevPropPbk/CanEject",canEject);
			propMap.put("DevPropPbk/CanCapture",canCapture);
			propMap.put("DevPropPbk/CanStack",canStack);
			propMap.put("DevPropPbk/MaxRetract",maxRetract);
			propMap.put("DevPropPbk/CanTurnPage",canTurnPage);
			propMap.put("DevPropPbk/CanDetectMediaTaken",canDetectMediaTaken);
		}
		
		
		if(getHasContent(document,"//PropInfo/PropDetail/DevPropCdm")){
			
			String cpHasStacker=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropCdm/CpHasStacker");
			String cpHasShutter=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropCdm/CpHasShutter");
			String cpCanRetract=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropCdm/CpCanRetract");
			String cpCanDetectCashTaken=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropCdm/CpCanDetectCashTaken");
			String cpCanTestPhysicalUnits=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropCdm/CpCanTestPhysicalUnits");
			String cpMaxDispensableBills=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropCdm/CpMaxDispensableBills");
			String numberofLogicalCashUnits=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropCdm/NumberofLogicalCashUnits");
			String numberofPhysicalCashUnits=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropCdm/NumberofPhysicalCashUnits");
			String numberOfCurrencies=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropCdm/NumberOfCurrencies");
			String currencies=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropCdm/Currencies");
			String exponents=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropCdm/Exponents");
			
			propMap.put("DevPropCdm/CpHasStacker",cpHasStacker);
			propMap.put("DevPropCdm/CpHasShutter",cpHasShutter);
			propMap.put("DevPropCdm/CpCanRetract",cpCanRetract);
			propMap.put("DevPropCdm/CpCanDetectCashTaken",cpCanDetectCashTaken);
			propMap.put("DevPropCdm/CpCanTestPhysicalUnits",cpCanTestPhysicalUnits);
			propMap.put("DevPropCdm/CpMaxDispensableBills",cpMaxDispensableBills);
			propMap.put("DevPropCdm/NumberofLogicalCashUnits",numberofLogicalCashUnits);
			propMap.put("DevPropCdm/NumberofPhysicalCashUnits",numberofPhysicalCashUnits);
			propMap.put("DevPropCdm/NumberOfCurrencies",numberOfCurrencies);
			propMap.put("DevPropCdm/Currencies",currencies);
			propMap.put("DevPropCdm/Exponents",exponents);
		}
		
		
		if(getHasContent(document,"//PropInfo/PropDetail/DevPropPin")){
			
			String canEBC=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropPin/CanEBC");
			String canCBC=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropPin/CanCBC");
			String canMAC=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropPin/CanMAC");
			String canRSA=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropPin/CanRSA");
			String canVerifyDES=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropPin/CanVerifyDES");
			String canVerifyEC=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropPin/CanVerifyEC");			
			String canVerifyVISA=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropPin/CanVerifyVISA");
			String canDESOffset=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropPin/CanDESOffset");
			String canTripleEBC=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropPin/CanTripleEBC");
			String canTripleCBC=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropPin/CanTripleCBC");
			String canTripleMAC=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropPin/CanTripleMAC");
			String canTripleCFB=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropPin/CanTripleCFB");
			String functionKeysSupported=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropPin/FunctionKeysSupported");
			
			propMap.put("DevPropPin/CanEBC",canEBC);
			propMap.put("DevPropPin/CanCBC",canCBC);
			propMap.put("DevPropPin/CanMAC",canMAC);
			propMap.put("DevPropPin/CanRSA",canRSA);
			propMap.put("DevPropPin/CanVerifyDES",canVerifyDES);
			propMap.put("DevPropPin/CanVerifyEC",canVerifyEC);
			propMap.put("DevPropPin/CanVerifyVISA",canVerifyVISA);
			propMap.put("DevPropPin/CanDESOffset",canDESOffset);
			propMap.put("DevPropPin/CanTripleEBC",canTripleEBC);
			propMap.put("DevPropPin/CanTripleCBC",canTripleCBC);
			propMap.put("DevPropPin/CanTripleMAC",canTripleMAC);
			propMap.put("DevPropPin/CanTripleCFB",canTripleCFB);
			propMap.put("DevPropPin/FunctionKeysSupported",functionKeysSupported);
		}
		
		
		if(getHasContent(document,"//PropInfo/PropDetail/DevPropChk")){
			
			String canRawPrint=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropChk/CanRawPrint");
			String canEject=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropChk/CanEject");
			String canStack=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropChk/CanStack");
			String canCapture=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropChk/CanCapture");
			String canStamp=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropChk/CanStamp");
			String canDetectMediaTaken=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropChk/CanDetectMediaTaken");
			String canReadFrontImage=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropChk/CanReadFrontImage");
			String canReadBackImage=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropChk/CanReadBackImage");
			String canReadCodeline=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropChk/CanReadCodeline");
			String codelineFormats=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropChk/CodelineFormats");
			String maxretract=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropChk/MaxRetract");
			String supportedImageFormats=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropChk/SupportedImageFormats");
			String maxAcceptItems=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropChk/MaxAcceptItems");
			
			propMap.put("DevPropChk/CanRawPrint",canRawPrint);
			propMap.put("DevPropChk/CanEject",canEject);
			propMap.put("DevPropChk/CanStack",canStack);
			propMap.put("DevPropChk/CanCapture",canCapture);
			propMap.put("DevPropChk/CanStamp",canStamp);
			propMap.put("DevPropChk/CanDetectMediaTaken",canDetectMediaTaken);
			propMap.put("DevPropChk/CanReadFrontImage",canReadFrontImage);
			propMap.put("DevPropChk/CanReadBackImage",canReadBackImage);
			propMap.put("DevPropChk/CanReadCodeline",canReadCodeline);
			propMap.put("DevPropChk/CodelineFormats",codelineFormats);
			propMap.put("DevPropChk/MaxRetract",maxretract);
			propMap.put("DevPropChk/SupportedImageFormats",supportedImageFormats);
			propMap.put("DevPropChk/MaxAcceptItems",maxAcceptItems);
		}
		
		
		if(getHasContent(document,"//PropInfo/PropDetail/DevPropDep")){
			
			String depositType=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropDep/DepositType");
			String canDispense=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropDep/CanDispense");
			String canPrint=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropDep/CanPrint");
			String canPrintOnretracts=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropDep/CanPrintOnRetracts");
			String canRetract=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropDep/CanRetract");
			String maxPrintLength=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropDep/MaxPrintLength");
			
			propMap.put("DevPropDep/DepositType",depositType);
			propMap.put("DevPropDep/CanDispense",canDispense);
			propMap.put("DevPropDep/CanPrint",canPrint);
			propMap.put("DevPropDep/CanPrintOnRetracts",canPrintOnretracts);
			propMap.put("DevPropDep/CanRetract",canRetract);
			propMap.put("DevPropDep/MaxPrintLength",maxPrintLength);
		}
		
		
		if(getHasContent(document,"//PropInfo/PropDetail/DevPropTtu")){
			
			String alphanumericKeysPresent=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropTtu/AlphanumericKeysPresent");
			String hexadecimalKeysPresent=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropTtu/HexadecimalKeysPresent");
			String numericKeysPresent=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropTtu/NumericKeysPresent");
			String keyboardLockPresent=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropTtu/KeyboardLockPresent");
			String displayLightPresent=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropTtu/DisplayLightPresent");
			String cursorSupported=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropTtu/CursorSupported");
			String formsSupported=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropTtu/FormsSupported");
			String resolutionX=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropTtu/ResolutionX");			
			String resolutionY=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropTtu/ResolutionY");
		
			propMap.put("DevPropTtu/AlphanumericKeysPresent",alphanumericKeysPresent);
			propMap.put("DevPropTtu/HexadecimalKeysPresent",hexadecimalKeysPresent);
			propMap.put("DevPropTtu/NumericKeysPresent",numericKeysPresent);
			propMap.put("DevPropTtu/KeyboardLockPresent",keyboardLockPresent);
			propMap.put("DevPropTtu/DisplayLightPresent",displayLightPresent);
			propMap.put("DevPropTtu/CursorSupported",cursorSupported);
			propMap.put("DevPropTtu/FormsSupported",formsSupported);
			propMap.put("DevPropTtu/ResolutionX",resolutionX);
			propMap.put("DevPropTtu/ResolutionY",resolutionY);
		}
		
		
		if(getHasContent(document,"//PropInfo/PropDetail/DevPropCam")){
			
			String cpMaxPictures=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropCam/CpMaxPictures");
			String cpPictureInfo=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropCam/CpPictureInfo");
			String cpMaxPictureInfoSize=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropCam/CpMaxPictureInfoSize");

		    propMap.put("DevPropCam/CpMaxPictures",cpMaxPictures);
			propMap.put("DevPropCam/CpPictureInfo",cpPictureInfo);
			propMap.put("DevPropCam/CpMaxPictureInfoSize",cpMaxPictureInfoSize);
		}
		
		if(getHasContent(document,"//PropInfo/PropDetail/DevPropCim")){
			
			String canEscrow=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropCim/CanEscrow");
			String shutterControlSupported=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropCim/ShutterControlSupported");
			String maxAcceptItems=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropCim/MaxAcceptItems");
			String canDetectCashInserted=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropCim/CanDetectCashInserted");
			String canDetectCashTaken=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropCim/CanDetectCashTaken");
			String retractAreas=getSingleNodeValue(document,"//PropInfo/PropDetail/DevPropCim/RetractAreas");
			
			propMap.put("DevPropCim/CanEscrow",canEscrow);
			propMap.put("DevPropCim/ShutterControlSupported",shutterControlSupported);
			propMap.put("DevPropCim/MaxAcceptItems",maxAcceptItems);
			propMap.put("DevPropCim/CanDetectCashInserted",canDetectCashInserted);
			propMap.put("DevPropCim/CanDetectCashTaken",canDetectCashTaken);
			propMap.put("DevPropCim/RetractAreas",retractAreas);
		}
		
		return propMap;
	}
	
	
	/**
	 * 从消息体XML文档中取得硬件信息
	 * @param document - 信息的xml
	 * @return HashMap - 信息的map
	 */
	
	public static HashMap<String,String> getHardwareInfoMap(Document document){
		
		if(document==null){
			log.error("document is null");
			return null;
		}
		
		HashMap<String,String> map=new HashMap<String,String>();
		
		String terminalId=getSingleNodeValue(document,"//HardwareInfo/TerminalId");
		String terminalIp=getSingleNodeValue(document,"//HardwareInfo/TerminalIp");
		
		map.put("TerminalId",terminalId);
		map.put("TerminalIp",terminalIp);

		
		if(getHasContent(document,"//HardwareInfo/DevHardwareDetail")){
			
			String biosFromVersion=getSingleNodeValue(document,"//HardwareInfo/DevHardwareDetail/BiosVersion");
			String hardwareFromCpu=getSingleNodeValue(document,"//HardwareInfo/DevHardwareDetail/HardwareCpu");
			String hardwareFromMemory=getSingleNodeValue(document,"//HardwareInfo/DevHardwareDetail/HardwareMemory");
			String hardwareFromHardisk=getSingleNodeValue(document,"//HardwareInfo/DevHardwareDetail/HardwareHardisk/TotalDiskSize");
			
			map.put("DevHardwareDetail/BiosVersion",biosFromVersion);
			map.put("DevHardwareDetail/HardwareCpu",hardwareFromCpu);
			map.put("DevHardwareDetail/HardwareMemory",hardwareFromMemory);
			map.put("DevHardwareDetail/HardwareHardisk/TotalDiskSize",hardwareFromHardisk);
		}
		
		return map;
	}
	
	/**
	 * 从消息体XML文档中取得软件信息
	 * @param document - 信息的xml
	 * @return HashMap - 信息的map
	 */
	
	public static HashMap<String,String> getSoftwareInfoMap(Document document){
		
		if(document==null){
			log.error("document is null");
			return null;
		}
		
		HashMap<String,String> map=new HashMap<String,String>();
		
		String terminalId=getSingleNodeValue(document,"//SoftwareInfo/TerminalId");
		String terminalIp=getSingleNodeValue(document,"//SoftwareInfo/TerminalIp");

		map.put("TerminalId",terminalId);
		map.put("TerminalIp",terminalIp);

			
		if(getHasContent(document,"//SoftwareInfo/DevSoftwareDetail")){
			
			String operatingSys=getSingleNodeValue(document,"//SoftwareInfo/DevSoftwareDetail/OperatingSys");
			String operatingVersion=getSingleNodeValue(document,"//SoftwareInfo/DevSoftwareDetail/OperatingVersion");
			String operatingPatchVersion=getSingleNodeValue(document,"//SoftwareInfo/DevSoftwareDetail/OperatingPatchVersion");
			String antivirusSystem=getSingleNodeValue(document,"//SoftwareInfo/DevSoftwareDetail/AntivirusSystem");
			//String antivirusVersion=getSingleNodeValue(document,"//SoftwareInfo/DevSoftwareDetail/AntivirusVersion");
			//String antivirusDefinition=getSingleNodeValue(document,"//SoftwareInfo/DevSoftwareDetail/AntivirusDefinition");
			//String spVersion=getSingleNodeValue(document,"//SoftwareInfo/DevSoftwareDetail/SpVersion");
			//String appVersion=getSingleNodeValue(document,"//SoftwareInfo/DevSoftwareDetail/AppVersion");
			//String chkCashData=getSingleNodeValue(document,"//SoftwareInfo/DevSoftwareDetail/ChkCashData");
			
			map.put("DevSoftwareDetail/OperatingSys",operatingSys);
			map.put("DevSoftwareDetail/OperatingVersion",operatingVersion);
			map.put("DevSoftwareDetail/OperatingPatchVersion",operatingPatchVersion);
			map.put("DevSoftwareDetail/AntivirusSystem",antivirusSystem);
			//map.put("DevSoftwareDetail/AntivirusVersion",antivirusVersion);
			//map.put("DevSoftwareDetail/AntivirusDefinition",antivirusDefinition);
		    //map.put("DevSoftwareDetail/SpVersion",spVersion);
			//map.put("DevSoftwareDetail/AppVersion",appVersion);
			//map.put("DevSoftwareDetail/ChkCashData",chkCashData);
			
		}
		
		return map;
	}
	
	
	/**
	 * 从消息体XML文档中取得吞卡信息
	 * @param document - 信息的xml
	 * @return HashMap - 信息的map
	 */
	
	public static HashMap<String,String> getCardRetainInfoMap(Document document){
		
		if(document==null){
			log.error("document is null");
			return null;
		}
		
		HashMap<String,String> map = new HashMap<String,String>();
		
		
		String terminalId=getSingleNodeValue(document,"//CardRetainInfo/TerminalId");
		String terminalIp=getSingleNodeValue(document,"//CardRetainInfo/TerminalIp");
		String dateTime=getSingleNodeValue(document,"//CardRetainInfo//DateTime");	
		String account=getSingleNodeValue(document,"//CardRetainInfo//Account");					
		String reason=getSingleNodeValue(document,"//CardRetainInfo//Reason");

		map.put("TerminalId",terminalId);
		map.put("TerminalIp",terminalIp);
		map.put("Account",account);
		map.put("Reason",reason);
		map.put("DateTime",dateTime);	
		
		return map;
	}
	
	/**
	 * 从消息体XML文档中取得清机报文信息
	 * @param document - 信息的xml
	 * @return HashMap - 信息的map
	 */
	
	public static HashMap<String,String> getClearInfoMap(Document document){
		
		if(document==null){
			log.error("document is null");
			return null;
		}
		
		HashMap<String,String> map = new HashMap<String,String>();
		
		
		String terminalId=getSingleNodeValue(document,"//ClearInfo/TerminalId");
		String terminalIp=getSingleNodeValue(document,"//ClearInfo/TerminalIp");
		String dateTime=getSingleNodeValue(document,"//ClearInfo/DateTime");
		String depositCount=getSingleNodeValue(document,"//ClearInfo/DepositCount");
		String depositAmount=getSingleNodeValue(document,"//ClearInfo/DepositAmount");
		String withdrawCount=getSingleNodeValue(document,"//ClearInfo/WithdrawCount");
		String withdrawAmount=getSingleNodeValue(document,"//ClearInfo/WithdrawAmount");
		String transferCount=getSingleNodeValue(document,"//ClearInfo/TransferCount");
		String transferAmount=getSingleNodeValue(document,"//ClearInfo/TransferAmount");

		map.put("TerminalId",terminalId);
		map.put("TerminalIp",terminalIp);
		map.put("DateTime",dateTime);
		map.put("DepositCount",depositCount);
		map.put("DepositAmount",depositAmount);
		map.put("WithdrawCount",withdrawCount);
		map.put("WithdrawAmount",withdrawAmount);
		map.put("TransferCount",transferCount);
		map.put("TransferAmount",transferAmount);		
		
		return map;
	}
	
	/**
	 * 从消息体XML文档中取得交易信息
	 * @param document - 信息的xml
	 * @return HashMap - 信息的map
	 */
	
	public static HashMap<String,String> getTxInfoMap(Document document){
		
		if(document==null){
			log.error(" document is null");
			return null;
		}
	
		HashMap<String,String> TxMap=new HashMap<String,String>();
		
		
		String terminalId=getSingleNodeValue(document,"//TxInfo/TerminalId");
		String terminalIp=getSingleNodeValue(document,"//TxInfo/TerminalIp");
		String txSerial=getSingleNodeValue(document,"//TxInfo/TxSerial");
		String txCode=getSingleNodeValue(document,"//TxInfo/TxCode");
		String txType=getSingleNodeValue(document,"//TxInfo/TxType");
		String branchNo=getSingleNodeValue(document,"//TxInfo/BranchNo");
		String txDate=getSingleNodeValue(document,"//TxInfo/TxDate");
		String txTime=getSingleNodeValue(document,"//TxInfo/TxTime");
		String startDate=getSingleNodeValue(document,"//TxInfo/StartDate");
		String startTime=getSingleNodeValue(document,"//TxInfo/StartTime");
		String endTime=getSingleNodeValue(document,"//TxInfo/EndTime");
		String hostSerial=getSingleNodeValue(document,"//TxInfo/HostSerial");
		String atmpSerial=getSingleNodeValue(document,"//TxInfo/AtmpSerial");
		String agentItem=getSingleNodeValue(document,"//TxInfo/AgentItem");
		String cdFlag=getSingleNodeValue(document,"//TxInfo/CdFlag");
		
		
		TxMap.put("TerminalId",terminalId);
		TxMap.put("TerminalIp",terminalIp);
		TxMap.put("TxSerial",txSerial);
		TxMap.put("TxCode",txCode);
		TxMap.put("TxType",txType);
		TxMap.put("BranchNo",branchNo);
		TxMap.put("TxDate",txDate);
		TxMap.put("TxTime",txTime);
		TxMap.put("StartDate",startDate);
		TxMap.put("StartTime",startTime);
		TxMap.put("EndTime",endTime);
		TxMap.put("HostSerial",hostSerial);
		TxMap.put("AtmpSerial",atmpSerial);
		TxMap.put("AgentItem",agentItem);
		TxMap.put("CdFlag",cdFlag);
			
		
		if(getHasContent(document,"//TxInfo/DepAcctIdFrom")){
			
			String acctFromId=getSingleNodeValue(document,"//TxInfo/DepAcctIdFrom/AcctId");
			String acctFromType=getSingleNodeValue(document,"//TxInfo/DepAcctIdFrom/AcctType");
			String acctFromKey=getSingleNodeValue(document,"//TxInfo/DepAcctIdFrom/AcctKey");
			String acctFromCur=getSingleNodeValue(document,"//TxInfo/DepAcctIdFrom/AcctCur");
			String bankFromId=getSingleNodeValue(document,"//TxInfo/DepAcctIdFrom/BankId");
			
			TxMap.put("DepAcctIdFrom/AcctId",acctFromId);
			TxMap.put("DepAcctIdFrom/AcctType",acctFromType);
			TxMap.put("DepAcctIdFrom/AcctKey",acctFromKey);
			TxMap.put("DepAcctIdFrom/AcctCur",acctFromCur);
			TxMap.put("DepAcctIdFrom/BankId",bankFromId);
		}
		
		if(getHasContent(document,"//TxInfo/DepAcctIdTo")){
			
			String acctToId=getSingleNodeValue(document,"//TxInfo/DepAcctIdTo/AcctId");
			String acctToType=getSingleNodeValue(document,"//TxInfo/DepAcctIdTo/AcctType");
			String acctToKey=getSingleNodeValue(document,"//TxInfo/DepAcctIdTo/AcctKey");
			String acctToCur=getSingleNodeValue(document,"//TxInfo/DepAcctIdTo/AcctCur");
			String bankToId=getSingleNodeValue(document,"//TxInfo/DepAcctIdTo/BankId");

			TxMap.put("DepAcctIdTo/AcctId",acctToId);
			TxMap.put("DepAcctIdTo/AcctType",acctToType);
			TxMap.put("DepAcctIdTo/AcctKey",acctToKey);
			TxMap.put("DepAcctIdTo/AcctCur",acctToCur);
			TxMap.put("DepAcctIdTo/BankId",bankToId);
		}
		
		
		String startDt=getSingleNodeValue(document,"//TxInfo/StartDt");
		String curType=getSingleNodeValue(document,"//TxInfo/CurType");
		String exchangeRate=getSingleNodeValue(document,"//TxInfo/ExchangeRate");
		String curAmt=getSingleNodeValue(document,"//TxInfo/CurAmt");
		String charge=getSingleNodeValue(document,"//TxInfo/Charge");
		String mechantNo=getSingleNodeValue(document,"//TxInfo/MechantNo");
		String tellNo=getSingleNodeValue(document,"//TxInfo/TellNo");
		String answer=getSingleNodeValue(document,"//TxInfo/answer");
		String hostReturn=getSingleNodeValue(document,"//TxInfo/HostReturn");
		String hostInfo=getSingleNodeValue(document,"//TxInfo/HostInfo");
		
		TxMap.put("StartDt",startDt);
		TxMap.put("CurType",curType);
		TxMap.put("ExchangeRate",exchangeRate);
		TxMap.put("CurAmt",curAmt);
		TxMap.put("Charge",charge);
		TxMap.put("MechantNo",mechantNo);
		TxMap.put("TellNo",tellNo);
		TxMap.put("answer",answer);
		TxMap.put("HostReturn",hostReturn);
		TxMap.put("HostInfo",hostInfo);
		
		return TxMap;
	}
	
	/**
	 * 从消息体XML文档中取得交易信息
	 * @param document - 信息的xml
	 * @return HashMap - 信息的map
	 */
	
	public static HashMap<String,String> getTxInfoMap(VTDNav document){
		
		if(document==null){
			log.error(" document is null");
			return null;
		}
	
		HashMap<String,String> TxMap=new HashMap<String,String>();
		
		
		String terminalId=getSingleNodeValue(document,"//TxInfo/TerminalId");
		String terminalIp=getSingleNodeValue(document,"//TxInfo/TerminalIp");
		String txSerial=getSingleNodeValue(document,"//TxInfo/TxSerial");
		String txCode=getSingleNodeValue(document,"//TxInfo/TxCode");
		String txType=getSingleNodeValue(document,"//TxInfo/TxType");
		String branchNo=getSingleNodeValue(document,"//TxInfo/BranchNo");
		String txDate=getSingleNodeValue(document,"//TxInfo/TxDate");
		String txTime=getSingleNodeValue(document,"//TxInfo/TxTime");
		String startDate=getSingleNodeValue(document,"//TxInfo/StartDate");
		String startTime=getSingleNodeValue(document,"//TxInfo/StartTime");
		String endTime=getSingleNodeValue(document,"//TxInfo/EndTime");
		String hostSerial=getSingleNodeValue(document,"//TxInfo/HostSerial");
		String atmpSerial=getSingleNodeValue(document,"//TxInfo/AtmpSerial");
		String agentItem=getSingleNodeValue(document,"//TxInfo/AgentItem");
		String cdFlag=getSingleNodeValue(document,"//TxInfo/CdFlag");
		
		
		TxMap.put("TerminalId",terminalId);
		TxMap.put("TerminalIp",terminalIp);
		TxMap.put("TxSerial",txSerial);
		TxMap.put("TxCode",txCode);
		TxMap.put("TxType",txType);
		TxMap.put("BranchNo",branchNo);
		TxMap.put("TxDate",txDate);
		TxMap.put("TxTime",txTime);
		TxMap.put("StartDate",startDate);
		TxMap.put("StartTime",startTime);
		TxMap.put("EndTime",endTime);
		TxMap.put("HostSerial",hostSerial);
		TxMap.put("AtmpSerial",atmpSerial);
		TxMap.put("AgentItem",agentItem);
		TxMap.put("CdFlag",cdFlag);
			
		
		if(getHasContent(document,"//TxInfo/DepAcctIdFrom")){
			
			String acctFromId=getSingleNodeValue(document,"//TxInfo/DepAcctIdFrom/AcctId");
			String acctFromType=getSingleNodeValue(document,"//TxInfo/DepAcctIdFrom/AcctType");
			String acctFromKey=getSingleNodeValue(document,"//TxInfo/DepAcctIdFrom/AcctKey");
			String acctFromCur=getSingleNodeValue(document,"//TxInfo/DepAcctIdFrom/AcctCur");
			String bankFromId=getSingleNodeValue(document,"//TxInfo/DepAcctIdFrom/BankId");
			
			TxMap.put("DepAcctIdFrom/AcctId",acctFromId);
			TxMap.put("DepAcctIdFrom/AcctType",acctFromType);
			TxMap.put("DepAcctIdFrom/AcctKey",acctFromKey);
			TxMap.put("DepAcctIdFrom/AcctCur",acctFromCur);
			TxMap.put("DepAcctIdFrom/BankId",bankFromId);
		}
		
		if(getHasContent(document,"//TxInfo/DepAcctIdTo")){
			
			String acctToId=getSingleNodeValue(document,"//TxInfo/DepAcctIdTo/AcctId");
			String acctToType=getSingleNodeValue(document,"//TxInfo/DepAcctIdTo/AcctType");
			String acctToKey=getSingleNodeValue(document,"//TxInfo/DepAcctIdTo/AcctKey");
			String acctToCur=getSingleNodeValue(document,"//TxInfo/DepAcctIdTo/AcctCur");
			String bankToId=getSingleNodeValue(document,"//TxInfo/DepAcctIdTo/BankId");

			TxMap.put("DepAcctIdTo/AcctId",acctToId);
			TxMap.put("DepAcctIdTo/AcctType",acctToType);
			TxMap.put("DepAcctIdTo/AcctKey",acctToKey);
			TxMap.put("DepAcctIdTo/AcctCur",acctToCur);
			TxMap.put("DepAcctIdTo/BankId",bankToId);
		}
		
		
		String startDt=getSingleNodeValue(document,"//TxInfo/StartDt");
		String curType=getSingleNodeValue(document,"//TxInfo/CurType");
		String exchangeRate=getSingleNodeValue(document,"//TxInfo/ExchangeRate");
		String curAmt=getSingleNodeValue(document,"//TxInfo/CurAmt");
		String charge=getSingleNodeValue(document,"//TxInfo/Charge");
		String mechantNo=getSingleNodeValue(document,"//TxInfo/MechantNo");
		String tellNo=getSingleNodeValue(document,"//TxInfo/TellNo");
		String answer=getSingleNodeValue(document,"//TxInfo/answer");
		String hostReturn=getSingleNodeValue(document,"//TxInfo/HostReturn");
		String hostInfo=getSingleNodeValue(document,"//TxInfo/HostInfo");
		
		TxMap.put("StartDt",startDt);
		TxMap.put("CurType",curType);
		TxMap.put("ExchangeRate",exchangeRate);
		TxMap.put("CurAmt",curAmt);
		TxMap.put("Charge",charge);
		TxMap.put("MechantNo",mechantNo);
		TxMap.put("TellNo",tellNo);
		TxMap.put("answer",answer);
		TxMap.put("HostReturn",hostReturn);
		TxMap.put("HostInfo",hostInfo);
		
		return TxMap;
	}	
	
	/**
	 * 取得XPath上的节点值
	 */
	
	public static String getSingleNodeValue(Document document,String xPath){
		try {
			Node node = document.selectSingleNode(xPath);
			return node == null ? "" : node.getText();
		} catch (InvalidXPathException e) {
			log.error(xPath + " is invalid:" + e);
			return "";
		}
	}
	
	public static String getSingleNodeValue(VTDNav vn ,String xPath)
	{
		try
		{
			AutoPilot ap = new AutoPilot(vn);
			ap.selectXPath(xPath);
			String xmlStr = "";
			
			if(ap.evalXPath() != -1){
				int t = vn.getText();
				if (t!=-1){
					xmlStr = vn.toNormalizedString(t);
				}
			}
			return xmlStr;
		}
		catch(Exception e)
		{
			log.error("error");
			return null;
		}
	}
	
	/**
	 * 查看某个XPath是否有子节点
	 */
	
	public static boolean getHasContent(Document document,String xPath){

		try{
			Node node=document.selectSingleNode(xPath);
			return node==null ? false : node.hasContent();
		}
		catch(InvalidXPathException e){
			log.error(xPath + " is invalid:" + e);
			return false;
		}
	}
	
	public static boolean getHasContent(VTDNav vn,String xPath)
	{

		try
		{
			AutoPilot ap = new AutoPilot(vn);
			ap.selectXPath(xPath);

			if(ap.evalXPath() != -1){
					return true;
			}
			return false;
		}catch(Exception e){
			return false;
		}
	}
	
	public static void main( String[] args ) throws Exception {
	
	}
}