package com.nsc.dem.action.project;

import java.util.List;

@SuppressWarnings("unchecked")
public class UnitNode {
	
	//��λ����
	private String code;
	//��λ����
	private String name;
	//�Ƿ����ӽڵ�
	private boolean leaf;
	//��־λ
	private String flag="0";
	
	
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

    private List list;

	public List getList() {
		return list;
	}
	public void setList(List list) {
		this.list = list;
	}
	public boolean isLeaf() {
		return leaf;
	}
	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}
}
