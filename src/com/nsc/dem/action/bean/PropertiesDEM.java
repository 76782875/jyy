package com.nsc.dem.action.bean;

public class PropertiesDEM implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	private String key;   // �����ļ� �ļ�
	private String value; // ֵ
	private String notes; // ע��
	
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
}
