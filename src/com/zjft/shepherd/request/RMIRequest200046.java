package com.zjft.shepherd.request;

import java.util.List;
import java.util.Map;

import com.zjft.shepherd.business.control.DepositIdInfo;

public class RMIRequest200046 extends RMIRequest {

	private List<DepositIdInfo> ids;
	private String version;
	
	@Override
	public void setParameter(Map<String, Object> paraMap) {
		super.setParameter(paraMap);
		@SuppressWarnings("unchecked")
		List<DepositIdInfo> ids = (List<DepositIdInfo>) paraMap.get("ids");
		this.setIds(ids);
		this.setVersion(paraMap.get("version").toString());
	}

	public List<DepositIdInfo> getIds() {
		return ids;
	}

	public void setIds(List<DepositIdInfo> ids) {
		this.ids = ids;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}
}
