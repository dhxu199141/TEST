package com.zjft.shepherd.common;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.InvalidXPathException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
//import com.ximpleware.*;
//import com.ximpleware.xpath.*;

/**
 * xml操作实用类

 * @author hpshen
 * @since 2007.04.10
 */

public class XmlUtil {

	private static Log log = LogFactory.getLog(XmlUtil.class);

	
	/**
	 * 将基于xml的消息字符串构建为xml内存树
	 * 
	 * @param xmlStr xml的消息字符串
	 * @return xml内存树
	 */

	public static Document createDocument(String xmlStr) {
		if (xmlStr == null) {
			throw new NullPointerException("createDocument参数为null");
		}	
		try {
			return DocumentHelper.parseText(xmlStr);
		} 
		catch (Exception e) {
			log.error("createDocument occur exception:" + e.getMessage());
			return null;
		}		
	}

//	public static VTDNav createDocument(byte[] xml) {
//		if (xml == null) {
//			throw new NullPointerException("createDocument参数为null");
//		}	
//		try {
//			
//			VTDGen vg = new VTDGen();
//			vg.setDoc(xml);
//			vg.parse(false);  // set namespace awareness to true
//			VTDNav vn = vg.getNav();
//			
//			return vn;
//		} catch(com.ximpleware.ParseException pe){
//			try{
//				VTDGen vg = new VTDGen();
//				vg.setDoc((new String(xml)).getBytes("utf-8"));
//				vg.parse(false);  // set namespace awareness to true
//				VTDNav vn = vg.getNav();
//				
//				return vn;
//			}catch(Exception e){
//				log.error("createDocument occur exception:" + e.getMessage());
//				return null;
//			}
//        }catch (Exception e) {
//			log.error("createDocument occur exception:" + e.getMessage());
//			return null;
//		}		
//	}
    /**
	 * 获取DOM节点的属性值
	 * @param xmlText DOM文档
	 * @param nodeName 节点名称 (例如//root/retcode)
	 * @param attrName 属性名称 (例如code)
	 * @return 节点的属性值
	 */
	public static String getValue(String xmlText,String nodeName,String attrName)
	{		
		try
		{
			Document doc = (Document)DocumentHelper.parseText(xmlText);
			
			org.dom4j.Node node = doc.selectSingleNode(nodeName);				

			return node.valueOf("@"+attrName);
		}
		catch(Exception e)
		{
			return null;
		}
		
	}
	
	/**
	 * 获取DOM节点的属性值
	 * @param document DOM文档
	 * @param nodeName 节点名称 (例如//root/retcode)
	 * @param attrName 属性名称 (例如code)
	 * @return 节点的属性值
	 */
	public static String getValue(Document document,String nodeName,String attrName)
	{		
		
		try
		{			
			org.dom4j.Node node = document.selectSingleNode(nodeName);				

			return node.valueOf("@"+attrName);
		}
		catch(Exception e)
		{
			return null;
		}
		
	}
	
	/**
	 * 获取DOM节点的属性值
	 * @param document DOM文档
	 * @param nodeName 节点名称 (例如//root/retcode)
	 * @param attrName 属性名称 (例如code)
	 * @return 节点的属性值
	 */
//	public static String getValue(VTDNav vn,String nodeName,String attrName)
//	{		
//		
//		try
//		{	
////			处理代码未实现
//			log.error("处理代码未实现");
//			return null;
//		}
//		catch(Exception e)
//		{
//			return null;
//		}
//		
//	}
	/**
	 * 取得XPath上的节点值
	 * @param document DOM文档
	 * @param xPath 节点路径名称 (例如//CardRetainInfo/TerminalId)
	 * @return 节点的属性值
	 */
	
	public static String getSingleNodeValue(Document document,String xPath)
	{
		try
		{
			return (document.selectSingleNode(xPath)).getText();
		}
		catch(Exception e)
		{
			return null;
		}
	}
	/**
	 * 取得XPath上的节点值
	 * @param document XML字符串
	 * @param xPath 节点路径名称 (例如//CardRetainInfo/TerminalId)
	 * @return 节点的属性值
	 */
	public static String getSingleNodeValue(String document,String xPath)
	{
		String[] str = xPath.split("/");
		String xPathh,xPatht;
		String xmlStr = document;
		for(int i=0;i<str.length;i++)
		{
			if((str[i]==null)||(str[i].length() < 1))
			{
				continue;
			}
			xPathh = "<"+str[i]+">";
			xPatht = "</"+str[i]+">";
			try
			{
				xmlStr = xmlStr.substring(xmlStr.indexOf(xPathh)+xPathh.length(),xmlStr.indexOf(xPatht));
			}
			catch(Exception e)
			{
				return null;
			}
		}
		str = null;
		xPathh = null;
		xPatht = null;
		return xmlStr;
	}
	
//	public static String getSingleNodeValue(VTDNav vn ,String xPath)
//	{
//		try
//		{
//			AutoPilot ap = new AutoPilot(vn);
//		
//			ap.selectXPath(xPath);
//			String xmlStr = "";
//			
//			if(ap.evalXPath() != -1){
//				int t = vn.getText();
//				if (t!=-1){
//					xmlStr = vn.toNormalizedString(t);
//				}
//			}
//			return xmlStr;
//		}
//		catch(Exception e)
//		{
//			log.error("error");
//			return null;
//		}
//	}
	/**
	 * 查看某个XPath是否有子节点
	 * @param document DOM文档
	 * @param xPath 节点路径名称 (例如//CardRetainInfo/TerminalId)
	 * @return 节点的属性值
	 */
	
	public static boolean getHasContent(Document document,String xPath)
	{

		try
		{
			Node node=document.selectSingleNode(xPath);
			return node==null ? false : node.hasContent();
		}
		catch(InvalidXPathException e)
		{
			return false;
		}
	}
	
	/**
	 * 查看某个XPath是否有子节点
	 * @param document XML字符串
	 * @param xPath 节点路径名称 (例如//CardRetainInfo/TerminalId)
	 * @return 节点的属性值
	 */
	
	public static boolean getHasContent(String document,String xPath)
	{

		try
		{
			String node = getSingleNodeValue(document,xPath);
			return (node==null||node.length() <1) ? false : true;
		}
		catch(InvalidXPathException e)
		{
			return false;
		}
	}
	
	
//	public static boolean getHasContent(VTDNav vn,String xPath)
//	{
//
//		try
//		{
//			AutoPilot ap = new AutoPilot(vn);
//			ap.selectXPath(xPath);
//
//			if(ap.evalXPath() != -1){
//					return true;
//			}
//			return false;
//		}catch(Exception e){
//			return false;
//		}
//	}
//	
//	public static void main(String[] args)
//	{	
//		//String retBody = "<StatusInfo><TerminalId>370110000013</TerminalId><TerminalIp>182.119.172.163</TerminalIp><ActionType>2</ActionType><DateTime>20080201170456</DateTime><DevDetail><DevDetailIdc><DevStatusCode>Healthy</DevStatusCode><DevIdcMedia>NotPresent</DevIdcMedia><DevIdcRetainBin>OK</DevIdcRetainBin><DevIdcCards>0</DevIdcCards></DevDetailIdc><DevDetailPin><DevStatusCode>Healthy</DevStatusCode></DevDetailPin><DevDetailRpr><DevStatusCode>Healthy</DevStatusCode><DevPtrMedia>NotPresent</DevPtrMedia><DevPtrSupplyLevel>Full</DevPtrSupplyLevel><DevPtrRetractBin>OK</DevPtrRetractBin><DevPtrToner>NotSupported</DevPtrToner><DevPtrInk>NotSupported</DevPtrInk></DevDetailRpr><DevDetailJpr><DevStatusCode>Healthy</DevStatusCode><DevPtrMedia>NotPresent</DevPtrMedia><DevPtrSupplyLevel>Out</DevPtrSupplyLevel><DevPtrRetractBin>Unknown</DevPtrRetractBin><DevPtrToner>NotSupported</DevPtrToner><DevPtrInk>NotSupported</DevPtrInk></DevDetailJpr><DevDetailCdm><DevStatusCode>Healthy</DevStatusCode><DevCdmSafeDoor>Closed</DevCdmSafeDoor><DevCdmCashUnits>OK</DevCdmCashUnits><DevCdmIntermediateStacker>Empty</DevCdmIntermediateStacker><DevCdmPosition><DevCdmShutter>Closed</DevCdmShutter><DevCdmTransport>NotSupported</DevCdmTransport><DevCdmTransportItems>NotSupported</DevCdmTransportItems></DevCdmPosition><DevCdmBin><DevCdmPUID>AB1,RB1,RB2,RB3</DevCdmPUID><DevCdmPUBinStatus>OK,OK,OK,OK</DevCdmPUBinStatus><DevCdmPUCurrentCount>0,1000,1000,1000</DevCdmPUCurrentCount><DevCdmPURejectCount>0,0,0,0</DevCdmPURejectCount><DevCdmPCUID>LOGI1,LOGI2,LOGI3,LOGI4</DevCdmPCUID><DevCdmCUID>LOGI1,LOGI2,LOGI3,LOGI4</DevCdmCUID><DevCdmCUType>RetractCassette,Recycling,Recycling,Recycling</DevCdmCUType><DevCdmCUBinStatus>OK,OK,OK,OK</DevCdmCUBinStatus><DevCdmCUCurrency>,CNY,CNY,CNY</DevCdmCUCurrency><DevCdmCUCurrentCount>0,1000,1000,1000</DevCdmCUCurrentCount><DevCdmCUInitialCount>0,1000,1000,1000</DevCdmCUInitialCount><DevCdmCURejectCount>0,0,0,0</DevCdmCURejectCount><DevCdmCUNoteValue>0,100,100,50</DevCdmCUNoteValue></DevCdmBin></DevDetailCdm><DevDetailCim><DevStatusCode>Healthy</DevStatusCode><DevCimCashUnits>OK</DevCimCashUnits><DevCimSafeDoor>Closed</DevCimSafeDoor><DevCimIntermediateStacker>Empty</DevCimIntermediateStacker><DevCimPosition><DevCimShutter>Closed</DevCimShutter><DevCimTransport>NotSupported</DevCimTransport></DevCimPosition><DevCimPosition><DevCimShutter>Closed</DevCimShutter><DevCimTransport>NotSupported</DevCimTransport></DevCimPosition><DevCimBin><DevCimPUID>AB1,RB1,RB2,RB3</DevCimPUID><DevCimPUBinStatus>OK,OK,OK,OK</DevCimPUBinStatus><DevCimPUCount>0,1000,1000,1000</DevCimPUCount><DevCimPUCashInCount>0,0,0,0</DevCimPUCashInCount><DevCimPCUID>LOGI1,LOGI2,LOGI3,LOGI4</DevCimPCUID><DevCimCUID>LOGI1,LOGI2,LOGI3,LOGI4</DevCimCUID><DevCimCUType>RetractCassette,Recycling,Recycling,Recycling</DevCimCUType><DevCimCUBinStatus>OK,OK,OK,OK</DevCimCUBinStatus><DevCimCUCurrency>[][CNY][CNY][CNY]</DevCimCUCurrency><DevCimCUCount>[][1000][1000][1000]</DevCimCUCount><DevCimCUCashInCount>0,0,0,0</DevCimCUCashInCount><DevCimCUNoteValue>[][100][100][50]</DevCimCUNoteValue></DevCimBin></DevDetailCim></DevDetail><bbb><ccc><ddd><eee>123123rwqew</eee></ddd></ccc></bbb></StatusInfo>";
//		String retBody="<StatusInfo><TerminalId>地址1000001</TerminalId><TerminalIp>182.119.172.163</TerminalIp><ActionType>1</ActionType><DateTime>20080201170456</DateTime><DevDetail><DevDetailIdc><DevStatusCode>FATAL</DevStatusCode><DevIdcMedia>NotPresent</DevIdcMedia><DevIdcRetainBin>Fatal</DevIdcRetainBin><DevIdcCards>0</DevIdcCards></DevDetailIdc><DevDetailPin><DevStatusCode>Fatal</DevStatusCode></DevDetailPin><DevDetailRpr><DevStatusCode>Fatal</DevStatusCode><DevPtrMedia>NotPresent</DevPtrMedia><DevPtrSupplyLevel>Full</DevPtrSupplyLevel><DevPtrRetractBin>OK</DevPtrRetractBin><DevPtrToner>NotSupported</DevPtrToner><DevPtrInk>NotSupported</DevPtrInk></DevDetailRpr><DevDetailJpr><DevStatusCode>Fatal</DevStatusCode><DevPtrMedia>NotPresent</DevPtrMedia><DevPtrSupplyLevel>Out</DevPtrSupplyLevel><DevPtrRetractBin>Unknown</DevPtrRetractBin><DevPtrToner>NotSupported</DevPtrToner><DevPtrInk>NotSupported</DevPtrInk></DevDetailJpr><DevDetailCdm><DevStatusCode>Fatal</DevStatusCode><DevCdmSafeDoor>Closed</DevCdmSafeDoor><DevCdmCashUnits>OK</DevCdmCashUnits><DevCdmIntermediateStacker>Empty</DevCdmIntermediateStacker><DevCdmPosition><DevCdmShutter>Closed</DevCdmShutter><DevCdmTransport>NotSupported</DevCdmTransport><DevCdmTransportItems>NotSupported</DevCdmTransportItems></DevCdmPosition><DevCdmBin><DevCdmPUID>AB1,RB1,RB2,RB3</DevCdmPUID><DevCdmPUBinStatus>OK,OK,OK,OK</DevCdmPUBinStatus><DevCdmPUCurrentCount>0,1000,1000,1000</DevCdmPUCurrentCount><DevCdmPURejectCount>0,0,0,0</DevCdmPURejectCount><DevCdmPCUID>LOGI1,LOGI2,LOGI3,LOGI4</DevCdmPCUID><DevCdmCUID>LOGI1,LOGI2,LOGI3,LOGI4</DevCdmCUID><DevCdmCUType>RetractCassette,Recycling,Recycling,Recycling</DevCdmCUType><DevCdmCUBinStatus>OK,OK,OK,OK</DevCdmCUBinStatus><DevCdmCUCurrency>,CNY,CNY,CNY</DevCdmCUCurrency><DevCdmCUCurrentCount>0,1000,1000,1000</DevCdmCUCurrentCount><DevCdmCUInitialCount>0,1000,1000,1000</DevCdmCUInitialCount><DevCdmCURejectCount>0,0,0,0</DevCdmCURejectCount><DevCdmCUNoteValue>0,100,100,50</DevCdmCUNoteValue></DevCdmBin></DevDetailCdm><DevDetailCim><DevStatusCode>Fatal</DevStatusCode><DevCimCashUnits>OK</DevCimCashUnits><DevCimSafeDoor>Closed</DevCimSafeDoor><DevCimIntermediateStacker>Empty</DevCimIntermediateStacker><DevCimPosition><DevCimShutter>Closed</DevCimShutter><DevCimTransport>NotSupported</DevCimTransport></DevCimPosition><DevCimPosition><DevCimShutter>Closed</DevCimShutter><DevCimTransport>NotSupported</DevCimTransport></DevCimPosition><DevCimBin><DevCimPUID>AB1,RB1,RB2,RB3</DevCimPUID><DevCimPUBinStatus>OK,OK,OK,OK</DevCimPUBinStatus><DevCimPUCount>0,1000,1000,1000</DevCimPUCount><DevCimPUCashInCount>0,0,0,0</DevCimPUCashInCount><DevCimPCUID>LOGI1,LOGI2,LOGI3,LOGI4</DevCimPCUID><DevCimCUID>LOGI1,LOGI2,LOGI3,LOGI4</DevCimCUID><DevCimCUType>RetractCassette,Recycling,Recycling,Recycling</DevCimCUType><DevCimCUBinStatus>OK,OK,OK,OK</DevCimCUBinStatus><DevCimCUCurrency>[][CNY][CNY][CNY]</DevCimCUCurrency><DevCimCUCount>[][1000][1000][1000]</DevCimCUCount><DevCimCUCashInCount>0,0,0,0</DevCimCUCashInCount><DevCimCUNoteValue>[][100][100][50]</DevCimCUNoteValue></DevCimBin></DevDetailCim></DevDetail></StatusInfo>";
//		//String retBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><retcode code=\"ERR001\" remark=\"下发任务已经添加\"/></root>";
//		//String code = XmlUtil.getValue(retBody.toString(),"retcode","code");
//		String code=null;
//		long base;
//		long now;
//		
//		VTDNav vn =null;
//		base = System.currentTimeMillis();
//		try
//		{
//
//			base = System.currentTimeMillis();
//			vn = XmlUtil.createDocument(retBody.getBytes());
//			code = vn.toNormalizedString(vn.getRootIndex());
//			now = System.currentTimeMillis() - base;
//			log.error("vtd报文解析 消耗=["+now+"]毫秒");
//			
//			
//			retBody="<StatusInfo><TerminalId>1000001</TerminalId><TerminalIp>182.119.172.163</TerminalIp><ActionType>1</ActionType><DateTime>20080201170456</DateTime><DevDetail><DevDetailIdc><DevStatusCode>FATAL</DevStatusCode><DevIdcMedia>NotPresent</DevIdcMedia><DevIdcRetainBin>Fatal</DevIdcRetainBin><DevIdcCards>0</DevIdcCards></DevDetailIdc><DevDetailPin><DevStatusCode>Fatal</DevStatusCode></DevDetailPin><DevDetailRpr><DevStatusCode>Fatal</DevStatusCode><DevPtrMedia>NotPresent</DevPtrMedia><DevPtrSupplyLevel>Full</DevPtrSupplyLevel><DevPtrRetractBin>OK</DevPtrRetractBin><DevPtrToner>NotSupported</DevPtrToner><DevPtrInk>NotSupported</DevPtrInk></DevDetailRpr><DevDetailJpr><DevStatusCode>Fatal</DevStatusCode><DevPtrMedia>NotPresent</DevPtrMedia><DevPtrSupplyLevel>Out</DevPtrSupplyLevel><DevPtrRetractBin>Unknown</DevPtrRetractBin><DevPtrToner>NotSupported</DevPtrToner><DevPtrInk>NotSupported</DevPtrInk></DevDetailJpr><DevDetailCdm><DevStatusCode>Fatal</DevStatusCode><DevCdmSafeDoor>Closed</DevCdmSafeDoor><DevCdmCashUnits>OK</DevCdmCashUnits><DevCdmIntermediateStacker>Empty</DevCdmIntermediateStacker><DevCdmPosition><DevCdmShutter>Closed</DevCdmShutter><DevCdmTransport>NotSupported</DevCdmTransport><DevCdmTransportItems>NotSupported</DevCdmTransportItems></DevCdmPosition><DevCdmBin><DevCdmPUID>AB1,RB1,RB2,RB3</DevCdmPUID><DevCdmPUBinStatus>OK,OK,OK,OK</DevCdmPUBinStatus><DevCdmPUCurrentCount>0,1000,1000,1000</DevCdmPUCurrentCount><DevCdmPURejectCount>0,0,0,0</DevCdmPURejectCount><DevCdmPCUID>LOGI1,LOGI2,LOGI3,LOGI4</DevCdmPCUID><DevCdmCUID>LOGI1,LOGI2,LOGI3,LOGI4</DevCdmCUID><DevCdmCUType>RetractCassette,Recycling,Recycling,Recycling</DevCdmCUType><DevCdmCUBinStatus>OK,OK,OK,OK</DevCdmCUBinStatus><DevCdmCUCurrency>,CNY,CNY,CNY</DevCdmCUCurrency><DevCdmCUCurrentCount>0,1000,1000,1000</DevCdmCUCurrentCount><DevCdmCUInitialCount>0,1000,1000,1000</DevCdmCUInitialCount><DevCdmCURejectCount>0,0,0,0</DevCdmCURejectCount><DevCdmCUNoteValue>0,100,100,50</DevCdmCUNoteValue></DevCdmBin></DevDetailCdm><DevDetailCim><DevStatusCode>Fatal</DevStatusCode><DevCimCashUnits>OK</DevCimCashUnits><DevCimSafeDoor>Closed</DevCimSafeDoor><DevCimIntermediateStacker>Empty</DevCimIntermediateStacker><DevCimPosition><DevCimShutter>Closed</DevCimShutter><DevCimTransport>NotSupported</DevCimTransport></DevCimPosition><DevCimPosition><DevCimShutter>Closed</DevCimShutter><DevCimTransport>NotSupported</DevCimTransport></DevCimPosition><DevCimBin><DevCimPUID>AB1,RB1,RB2,RB3</DevCimPUID><DevCimPUBinStatus>OK,OK,OK,OK</DevCimPUBinStatus><DevCimPUCount>0,1000,1000,1000</DevCimPUCount><DevCimPUCashInCount>0,0,0,0</DevCimPUCashInCount><DevCimPCUID>LOGI1,LOGI2,LOGI3,LOGI4</DevCimPCUID><DevCimCUID>LOGI1,LOGI2,LOGI3,LOGI4</DevCimCUID><DevCimCUType>RetractCassette,Recycling,Recycling,Recycling</DevCimCUType><DevCimCUBinStatus>OK,OK,OK,OK</DevCimCUBinStatus><DevCimCUCurrency>[][CNY][CNY][CNY]</DevCimCUCurrency><DevCimCUCount>[][1000][1000][1000]</DevCimCUCount><DevCimCUCashInCount>0,0,0,0</DevCimCUCashInCount><DevCimCUNoteValue>[][100][100][50]</DevCimCUNoteValue></DevCimBin></DevDetailCim></DevDetail></StatusInfo>";
//			
//			base = System.currentTimeMillis();
//			vn = XmlUtil.createDocument(retBody.getBytes());
//			code = vn.toNormalizedString(vn.getRootIndex());
//			now = System.currentTimeMillis() - base;
//			log.error("vtd报文解析 消耗=["+now+"]毫秒");
//		}
//		catch(Exception e)
//		{
//			
//		}
//		log.error(code);
//		for(int i=0;i<10;i++)
//		{
//			code = XmlUtil.getSingleNodeValue(vn,"//StatusInfo/TerminalId");
//		}
//		log.error("code=" + code);
//		now = System.currentTimeMillis() - base;
//		log.error("VTDGen报文解析 消耗=["+now+"]毫秒");
//		
//		if(XmlUtil.getHasContent(vn,"//StatusInfo/TerminalId"))
//		{
//			log.error("true");
//		}
//		else
//		{
//			log.error("false");
//		}
//		
//		base = System.currentTimeMillis();
//		Document document = XmlUtil.createDocument(retBody);		
//		for(int i=0;i<10;i++)
//		{
//			code = XmlUtil.getSingleNodeValue(document,"//StatusInfo/TerminalId");
//		}
//		log.debug(code);
//		now = System.currentTimeMillis() - base;
//		log.debug("document报文解析 消耗=["+now+"]毫秒");
//	   
//		if(XmlUtil.getHasContent(vn,"//StatusInfo/TerminalId"))
//		{
//			log.error("//StatusInfo/TerminalId true");
//		}
//		else
//		{
//			log.error("//StatusInfo/TerminalId false");
//		}
//		
//		if(XmlUtil.getHasContent(vn,"//StatusInfo/DevDetail/DevDetailIdc"))
//		{
//			log.error("//StatusInfo/DevDetail/DevDetailIdc true");
//		}
//		else
//		{
//			log.error("//StatusInfo/DevDetail/DevDetailIdc false");
//		}
//	}	
}