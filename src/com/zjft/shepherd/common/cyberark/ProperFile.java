package com.zjft.shepherd.common.cyberark;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProperFile {
	private Properties info;
	private File nf;
	private InputStream in;
	private static Log log = LogFactory.getLog(ProperFile.class);

	public ProperFile(String fStr) {
		try {
			info = new Properties();
			in = null;
			in = new FileInputStream(fStr);
			info.load(in);
		} catch (Exception e) {
			log.error("初始化配置文件异常", e);
		}
	}

	public void setProper(String PName, String PValue, String PStr) {
		try {
			FileOutputStream fos = new FileOutputStream(nf);
			info.setProperty(PName, PValue);
			info.store(fos, PStr);
			fos.close();
		} catch (Exception e) {
			log.error("异常", e);
		}
	}

	public String getProper(String pName) {
		String reStr = "";
		reStr = info.getProperty(pName) == null ? "" : info.getProperty(pName);
		return reStr;
	}

	public void close() {
		try {
			if (in != null)
				in.close();
		} catch (Exception e) {
			log.error("异常", e);
		}
	}
}