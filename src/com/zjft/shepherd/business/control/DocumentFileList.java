package com.zjft.shepherd.business.control;

import java.io.Serializable;

/**
 * Զ���ļ������
 * 
 * @author hjtang
 * */

public class DocumentFileList implements Serializable {
	
	private static final long serialVersionUID = 1476793133716309327L;

	/** ��ʾ��¼��һ���ļ�(0)������һ��Ŀ¼(1)������ */
	private String docmentFileType;

	/** ��ʾ�ļ���Ŀ¼������ */
	private String docmentFileName;

	/** ����·����Ϣ(c:\��d:\��e:\) */
	private String diskPath;

	/** �������� */
	private String diskName;

	/** ����ʱ�� */
	private String docmentFileCreateTime;

	/** ������ʱ�� */
	private String docmentFileUpdateTime;

	/** (����)�ļ��Ĵ�С */
	private String docFileSize;

	/** ����ʣ��ռ� */
	private String diskFreeSize;

	/** (����)�ļ������� */
	private String docmentFilePro;

	/** �ļ������� :exe,gif,folder,txt,zip,unknown */
	private String docmentFileMode;

	public void setDiskName(String diskName) {
		this.diskName = diskName;
	}

	public String getDiskName() {
		return this.diskName;
	}

	public void setDiskPath(String diskPath) {
		this.diskPath = diskPath;
	}

	public String getDiskPath() {
		return this.diskPath;
	}

	public void setDocmentFileType(String docmentFileType) {
		this.docmentFileType = docmentFileType;
	}

	public String getDocmentFileType() {
		return this.docmentFileType;
	}

	public void setDocmentFileName(String docmentFileName) {
		this.docmentFileName = docmentFileName;
	}

	public String getDocmentFileName() {
		return this.docmentFileName;
	}

	public void setDocmentFileCreateTime(String docmentFileCreateTime) {
		this.docmentFileCreateTime = docmentFileCreateTime;
	}

	public String getDocmentFileCreateTime() {
		return this.docmentFileCreateTime;
	}

	public void setDocmentFileUpdateTime(String docmentFileUpdateTime) {
		this.docmentFileUpdateTime = docmentFileUpdateTime;
	}

	public String getDocmentFileUpdateTime() {
		return this.docmentFileUpdateTime;
	}

	public void setDocFileSize(String docFileSize) {
		this.docFileSize = docFileSize;
	}

	public String getDocFileSize() {
		return this.docFileSize;
	}

	public void setDocmentFilePro(String docmentFilePro) {
		this.docmentFilePro = docmentFilePro;
	}

	public String getDocmentFilePro() {
		return this.docmentFilePro;
	}

	public void setDocmentFileMode(String docmentFileMode) {
		this.docmentFileMode = docmentFileMode;
	}

	public String getDocmentFileMode() {
		return this.docmentFileMode;
	}

	public void setDiskFreeSize(String diskFreeSize) {
		this.diskFreeSize = diskFreeSize;
	}

	public String getDiskFreeSize() {
		return this.diskFreeSize;
	}
}
