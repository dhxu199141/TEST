package com.zjft.shepherd.vo;

public class AgentPwdInfo 
{
	private String orgNo = null;
	private int orgGradeNo = -1;
	private String agentPwd = null;
	private String agentValidDate = null;
	public String getOrgNo() {
		return orgNo;
	}
	public void setOrgNo(String orgNo) {
		this.orgNo = orgNo;
	}
	public int getOrgGradeNo() {
		return orgGradeNo;
	}
	public void setOrgGradeNo(int orgGradeNo) {
		this.orgGradeNo = orgGradeNo;
	}
	public String getAgentPwd() {
		return agentPwd;
	}
	public void setAgentPwd(String agentPwd) {
		this.agentPwd = agentPwd;
	}
	public String getAgentValidDate() {
		return agentValidDate;
	}
	public void setAgentValidDate(String agentValidDate) {
		this.agentValidDate = agentValidDate;
	}
}
