package com.zjft.shepherd.service;

import com.zjft.shepherd.service.impl.DepositIdServiceImpl;

public class DepositIdFactory {
	
	private static DepositIdService depositIdService;

	public static DepositIdService builtDepositIdService() {
		if (depositIdService == null)
			depositIdService = new DepositIdServiceImpl();
		return depositIdService;
	}

}
