package com.zjft.shepherd.common;

import java.io.*;

import org.apache.log4j.*;
public class Log4JInit {
	/**
	 * 
	 * @param path log4j�����ļ��ľ���·�������Ϊ�գ���ͬ��Ŀ¼�µ�log4jrmi.properties
	 * @throws Exception
	 */
	public static void init(String path) throws Exception {
//		String prefix = getServletContext().getRealPath("/");
//		String file = getServletConfig().getInitParameter("log4j-config-file");
//		
//		System.out.println("------file=="+file);
//		
//		// ��Servlet������ȡlog4j�������ļ�
//		if (file != null) {
//			System.out.println("---��Servlet������ȡlog4j�������ļ�----");
//			PropertyConfigurator.configureAndWatch(prefix + file,60000);
//		}
		if(path.equals(""))
			PropertyConfigurator.configureAndWatch(System.getProperty("user.dir")+"/log4jrmi.properties",60000);
		else
			PropertyConfigurator.configureAndWatch(path, 60000);
	}

}
