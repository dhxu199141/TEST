package com.zjft.shepherd.dao;

import com.zjft.shepherd.common.CalendarUtil;
import com.zjft.shepherd.common.NumberUtil;

public class RemoteCountResultInfo  implements java.io.Serializable {

	/**
	 * 自动精查结果
	 */
	private static final long serialVersionUID = 3304578759323344074L;
	private String logicId;
	private String traceLogicId;
	private String commResult;
	private String datetime;
	private String commStartTime;
	private String commEndTime;
	private String hostAmount;
	private String loacalAmount;
	private String puidBefore;
	private String puPosNameBefore;
	private String puRejectCountBefore;
	private String puCountBefore;
	private String typeBefore;
	private String noteValueBefore;
	private String statusBefore;
	private String puidAfter;
	private String puPosNameAfter;
	private String puRejectCountAfter;
	private String puCountAfter;
	private String typeAfter;
	private String noteValueAfter;
	private String statusAfter;

	public RemoteCountResultInfo() {
		
	}

	public RemoteCountResultInfo(String logicId, String traceLogicId, String commResult, String datetime, String commStartTime, String commEndTime,
			String hostAmount, String loacalAmount, String puidBefore, String puPosNameBefore, String puRejectCountBefore,String puCountBefore,
			String typeBefore, String noteValueBefore, String statusBefore, String puidAfter, String puPosNameAfter,String puRejectCountAfter,
			String puCountAfter, String typeAfter, String noteValueAfter, String statusAfter) {
		this.logicId = logicId;
		this.traceLogicId = traceLogicId;
		this.commResult = commResult;
		this.datetime = datetime;
		this.commStartTime = commStartTime;
		this.commEndTime = commEndTime;
		this.hostAmount = hostAmount;
		this.loacalAmount = loacalAmount;
		this.puidBefore = puidBefore;
		this.puPosNameBefore = puPosNameBefore;
		this.puRejectCountBefore = puRejectCountBefore;
		this.puCountBefore = puCountBefore;
		this.typeBefore = typeBefore;
		this.noteValueBefore = noteValueBefore;
		this.statusBefore = statusBefore;
		this.puidAfter = puidAfter;
		this.puPosNameAfter = puPosNameAfter;
		this.puRejectCountAfter = puRejectCountAfter;
		this.puCountAfter = puCountAfter;
		this.typeAfter = typeAfter;
		this.noteValueAfter = noteValueAfter;
		this.statusAfter = statusAfter;
	}
	
	public String getLogicId() {
		return logicId;
	}

	public void setLogicId(String logicId) {
		this.logicId = logicId;
	}

	public String getTraceLogicId() {
		return traceLogicId;
	}

	public void setTraceLogicId(String traceLogicId) {
		this.traceLogicId = traceLogicId;
	}

	public String getCommResult() {
		return commResult;
	}

	public void setCommResult(String commResult) {
		this.commResult = commResult;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getCommStartTime() {
		return commStartTime;
	}

	public void setCommStartTime(String commStartTime) {
		this.commStartTime = commStartTime;
	}

	public String getCommEndTime() {
		return commEndTime;
	}

	public void setCommEndTime(String commEndTime) {
		this.commEndTime = commEndTime;
	}

	public String getHostAmount() {
		return hostAmount;
	}

	public void setHostAmount(String hostAmount) {
		this.hostAmount = hostAmount;
	}

	public String getLoacalAmount() {
		return loacalAmount;
	}

	public void setLoacalAmount(String loacalAmount) {
		this.loacalAmount = loacalAmount;
	}

	public String getPuidBefore() {
		return puidBefore;
	}

	public void setPuidBefore(String puidBefore) {
		this.puidBefore = puidBefore;
	}

	public String getPuPosNameBefore() {
		return puPosNameBefore;
	}

	public void setPuPosNameBefore(String puPosNameBefore) {
		this.puPosNameBefore = puPosNameBefore;
	}

	public String getPuRejectCountBefore() {
		return puRejectCountBefore;
	}

	public void setPuRejectCountBefore(String puRejectCountBefore) {
		this.puRejectCountBefore = puRejectCountBefore;
	}

	public String getPuCountBefore() {
		return puCountBefore;
	}

	public void setPuCountBefore(String puCountBefore) {
		this.puCountBefore = puCountBefore;
	}

	public String getTypeBefore() {
		return typeBefore;
	}

	public void setTypeBefore(String typeBefore) {
		this.typeBefore = typeBefore;
	}

	public String getNoteValueBefore() {
		return noteValueBefore;
	}

	public void setNoteValueBefore(String noteValueBefore) {
		this.noteValueBefore = noteValueBefore;
	}

	public String getStatusBefore() {
		return statusBefore;
	}

	public void setStatusBefore(String statusBefore) {
		this.statusBefore = statusBefore;
	}

	public String getPuidAfter() {
		return puidAfter;
	}

	public void setPuidAfter(String puidAfter) {
		this.puidAfter = puidAfter;
	}

	public String getPuPosNameAfter() {
		return puPosNameAfter;
	}

	public void setPuPosNameAfter(String puPosNameAfter) {
		this.puPosNameAfter = puPosNameAfter;
	}

	public String getPuRejectCountAfter() {
		return puRejectCountAfter;
	}

	public void setPuRejectCountAfter(String puRejectCountAfter) {
		this.puRejectCountAfter = puRejectCountAfter;
	}

	public String getPuCountAfter() {
		return puCountAfter;
	}

	public void setPuCountAfter(String puCountAfter) {
		this.puCountAfter = puCountAfter;
	}

	public String getTypeAfter() {
		return typeAfter;
	}

	public void setTypeAfter(String typeAfter) {
		this.typeAfter = typeAfter;
	}

	public String getNoteValueAfter() {
		return noteValueAfter;
	}

	public void setNoteValueAfter(String noteValueAfter) {
		this.noteValueAfter = noteValueAfter;
	}

	public String getStatusAfter() {
		return statusAfter;
	}

	public void setStatusAfter(String statusAfter) {
		this.statusAfter = statusAfter;
	}
}