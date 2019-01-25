package com.zjft.shepherd.service;

import java.util.Map;

public interface DepositIdService {
	
	/**
	 * 
	 * @param paraMap
	 * key userNo--用户编号
	 *     devInfo--设备信息    设备编号|ip地址|通讯每包传输大小|压缩标志
	 * @return 00成功，99失败
	 */
	Map<String,Object> qryDepositId(Map<String,Object> paraMap);
	
	
	/**
	 * 
	 * @param paraMap
	 * key userNo--用户编号
	 *     devInfo--设备信息    设备编号|ip地址|通讯每包传输大小|压缩标志
	 *     version--版本号
	 *     ids--id列表
	 * @return 00成功，99失败
	 */
	Map<String,Object> setDepositId(Map<String,Object> paraMap);
}
