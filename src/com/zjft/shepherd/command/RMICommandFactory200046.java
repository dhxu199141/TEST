package com.zjft.shepherd.command;

import com.zjft.shepherd.request.RMIRequest;
import com.zjft.shepherd.request.RMIRequest200046;

public class RMICommandFactory200046 extends RMICommandFactory {

	@Override
	protected RMICommand createCommand(RMIRequest request) {
		return new RMICommand200046(request);
	}

	@Override
	protected RMIRequest createRequest() {
		return new RMIRequest200046();
	}
	
}
