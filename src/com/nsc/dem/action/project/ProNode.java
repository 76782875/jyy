package com.nsc.dem.action.project;

public class ProNode {
	
	//����id
	private Long id;
	//���̱���
	private String code;
	//��������
	private String name;
	//�Ƿ����ӽڵ�
	private boolean leaf;
	//��־λ
	private String flag="1";
	
	
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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
	public boolean isLeaf() {
		return leaf;
	}
	public void setLeaf(boolean leaf) {
		this.leaf = leaf;
	}

}
