package com.zjft.shepherd.service;

import java.util.TimerTask;

/**
 * file服务定时任务
 * 
 * 该任务开启一个文件传输服务线程
 * 
 * @author hpshen
 * @since 2007.04.10
 */

public class FileTask extends TimerTask {

	public void run() {
		Thread thread = new Thread(new FileBegin());
		thread.start();
	}
}