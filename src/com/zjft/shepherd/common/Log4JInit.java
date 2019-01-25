package com.zjft.shepherd.common;

import java.io.*;

import org.apache.log4j.*;
public class Log4JInit {
	/**
	 * 
	 * @param path log4j配置文件的绝对路径，如果为空，则同级目录下的log4jrmi.properties
	 * @throws Exception
	 */
	public static void init(String path) throws Exception {
//		String prefix = getServletContext().getRealPath("/");
//		String file = getServletConfig().getInitParameter("log4j-config-file");
//		
//		System.out.println("------file=="+file);
//		
//		// 从Servlet参数读取log4j的配置文件
//		if (file != null) {
//			System.out.println("---从Servlet参数读取log4j的配置文件----");
//			PropertyConfigurator.configureAndWatch(prefix + file,60000);
//		}
		if(path.equals(""))
			PropertyConfigurator.configureAndWatch(System.getProperty("user.dir")+"/log4jrmi.properties",60000);
		else
			PropertyConfigurator.configureAndWatch(path, 60000);
	}

}
