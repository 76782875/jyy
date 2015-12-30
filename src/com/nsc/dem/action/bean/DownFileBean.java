package com.nsc.dem.action.bean;

import java.util.List;

public class DownFileBean {
	private String name;//�ļ���
	private String path;//����·��
	private String code; //ҵ����λ��Ҳ���������û�ѡ������ص�ַ
	private String docid;  //�ĵ�ID
	private String suffix;//�ĵ���׺
	private String projectId;//����ID
	private boolean isonline;//true���߲鿴��false����
	private List<String[]> downAddress;//���ص�ַ
	
	public String getSuffix() {
		return suffix;
	}
	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}
	public String getDocid() {
		return docid;
	}
	public void setDocid(String docid) {
		this.docid = docid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name.replaceAll("<[.[^<]]*>","");
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}
	public List<String[]> getDownAddress() {
		return downAddress;
	}
	public void setDownAddress(List<String[]> downAddress) {
		this.downAddress = downAddress;
	}
	public boolean isIsonline() {
		return isonline;
	}
	public void setIsonline(boolean isonline) {
		this.isonline = isonline;
	}
	
	
	
	
}
