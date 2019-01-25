package com.zjft.shepherd.business.control;

import java.io.Serializable;

/**
 * 远程文件浏览器
 * 
 * @author hjtang
 * */

public class DocumentFileList implements Serializable {
	
	private static final long serialVersionUID = 1476793133716309327L;

	/** 表示记录是一个文件(0)或者是一个目录(1)或者是 */
	private String docmentFileType;

	/** 表示文件或目录的名称 */
	private String docmentFileName;

	/** 磁盘路径信息(c:\、d:\、e:\) */
	private String diskPath;

	/** 磁盘名称 */
	private String diskName;

	/** 创建时间 */
	private String docmentFileCreateTime;

	/** 最后访问时间 */
	private String docmentFileUpdateTime;

	/** (磁盘)文件的大小 */
	private String docFileSize;

	/** 磁盘剩余空间 */
	private String diskFreeSize;

	/** (磁盘)文件的属性 */
	private String docmentFilePro;

	/** 文件的类型 :exe,gif,folder,txt,zip,unknown */
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
