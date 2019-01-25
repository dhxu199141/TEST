package com.zjft.shepherd.business.control;

import java.io.OutputStream;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.zjft.shepherd.common.SocketUtil;
import com.zjft.shepherd.common.SystemCons;
import com.zjft.shepherd.dao.DevInfoDAO;

/**
 * Title:消息转发 获取设备的MAK
 * @version 1.0
 */
public class MessageEncoded {
	private static Log log = LogFactory.getLog(MessageEncoded.class);

	public static boolean sendCmdToRvcMutil(String sndBody, String ip,String strPort, StringBuffer retBody, int connTime, int soTime,int packetLen, int vzipType, String devNo) {
		String mak = null;
		if (SystemCons.isMessageEncrypt()) {// 加密
			try {
				HashMap<String, String> hmp = DevInfoDAO.getDevDekEncodedBydevNo(devNo);
				mak = hmp.get("encoded");
				if (mak == null || mak.equalsIgnoreCase("")) {
					log.info("从数据库中读取设备【" + devNo + "】MAK为空，此处不进行消息转发。");
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("从数据库中读取设备【" + devNo + "】MAK失败，此处不进行消息转发。");
				return false;
			}
		}
		return SocketUtil.sendCmdToRvcMutil(sndBody, ip, strPort, retBody,connTime, soTime, packetLen, vzipType, mak);
	}

	public static boolean sendMessage(OutputStream out, byte[] sendBody,int packetLen, int vzipType, StringBuffer retMsg, String devNo) {
		String mak = null;
		if (SystemCons.isMessageEncrypt()) {// 加密
			try {
				HashMap<String, String> hmp = DevInfoDAO.getDevDekEncodedBydevNo(devNo);
				mak = hmp.get("encoded");
				if (mak == null || mak.equalsIgnoreCase("")) {
					log.info("从数据库中读取设备【" + devNo + "】MAK为空，此处不进行消息转发。");
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("从数据库中读取设备【" + devNo + "】MAK失败，此处不进行消息转发。");
				return false;
			}
		}
		return SocketUtil.sendMessage(out, sendBody, packetLen, vzipType,retMsg, mak);
	}
}
