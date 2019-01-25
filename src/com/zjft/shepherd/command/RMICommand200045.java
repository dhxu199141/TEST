package com.zjft.shepherd.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.zjft.shepherd.business.control.DepositIdInfo;
import com.zjft.shepherd.common.DocumentUtil;
import com.zjft.shepherd.request.RMIRequest;

public class RMICommand200045 extends RMICommand {

	public RMICommand200045(RMIRequest request) {
		super(request);
	}

	@Override
	public Map<String, Object> invoke() {
		Map<String, Object> retMap = super.invoke();
		String retCode = (String) retMap.get("retCode");
		if(SUCCESS.equals(retCode)) {
			retMap.put("ids", getDepositIdInfos());
			return retMap;
		}
		String devRetCode = (String) retMap.get("devRetCode");
		if("RMT002".equals(devRetCode)) {
			retMap.put("retCode", "02");
			return retMap;
		}
		return retMap;
	}
	
	private List<DepositIdInfo> getDepositIdInfos() {
		Document doc = DocumentUtil.convertTextToDOM(this.getRetBody().toString());
		NodeList nodes = doc.getElementsByTagName("item");
		List<DepositIdInfo> depositIdInfos = new ArrayList<DepositIdInfo>();
		for (int i = 0; i < nodes.getLength(); i++) {
			DepositIdInfo depositIdInfo = new DepositIdInfo();
			Node node = nodes.item(i);
			node.getChildNodes();
			NamedNodeMap attrs = node.getAttributes();
			depositIdInfo.setCurrency(attrs.getNamedItem("currency").getNodeValue());
			depositIdInfo.setValue(attrs.getNamedItem("value").getNodeValue());
			depositIdInfo.setId(attrs.getNamedItem("ID").getNodeValue());
			depositIdInfo.setConfigured("TRUE".equalsIgnoreCase(attrs.getNamedItem("configured").getNodeValue()));
			depositIdInfo.setRelease(attrs.getNamedItem("release").getNodeValue());
			depositIdInfos.add(depositIdInfo);
		}
		return depositIdInfos;
	}

	
}
