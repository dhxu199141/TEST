package com.zjft.shepherd.service;

import java.util.TimerTask;

/**
 * file����ʱ����
 * 
 * ��������һ���ļ���������߳�
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