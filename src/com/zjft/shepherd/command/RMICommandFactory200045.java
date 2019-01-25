package com.zjft.shepherd.command;

import com.zjft.shepherd.request.RMIRequest;

public class RMICommandFactory200045 extends RMICommandFactory {

	@Override
	protected RMICommand createCommand(RMIRequest request) {
		return new RMICommand200045(request);
	}
	
}
