package com.zjft.shepherd.business.control;

import java.io.Serializable;

public class DepositIdInfo implements Serializable {

	private static final long serialVersionUID = -8820124498088745167L;

	private String id; //�泮ID
	private String currency;//����
	private String value;//��ֵ
	private boolean configured;//�Ƿ�֧�ָ���ֵ��true OR false��
	private String release; //���

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isConfigured() {
		return configured;
	}

	public void setConfigured(boolean configured) {
		this.configured = configured;
	}

	public String getRelease() {
		return release;
	}

	public void setRelease(String release) {
		this.release = release;
	}

}
