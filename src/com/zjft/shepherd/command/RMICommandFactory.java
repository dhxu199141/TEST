package com.zjft.shepherd.command;

import java.util.Map;

import com.zjft.shepherd.request.RMIRequest;

public class RMICommandFactory {
	
	public RMICommand createCommand(Map<String, Object> paraMap) {
		RMIRequest request = createRequest();
		request.setParameter(paraMap);
		RMICommand command = createCommand(request);
		return command;
	}
	
	protected RMICommand createCommand(RMIRequest request) {
		return new RMICommand(request);
	}
	
	protected RMIRequest createRequest(){
		return new RMIRequest();
	}
}