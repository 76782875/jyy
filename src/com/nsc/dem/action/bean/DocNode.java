package com.nsc.dem.action.bean;

/*
 * �ڵ���
 * */
public class DocNode {
	//����
	private String title;
	//�Ƿ����ӽڵ�
	private boolean leaf;
	//������ֵ
	private Key key;
	
	public String getTitle() {
		return title;
	}

	public boolean isIsLazy() {
		return !this.leaf;
	}

	public boolean isIsFolder() {
		return !this.leaf;
	}
	
	public Key getKey() {
		return key;
	}
	
	public DocNode(Long id,String lable, String parm, String value, boolean leaf) {
		this.title = lable;
		this.leaf = leaf;
		
		this.key=new Key();
		this.key.id=id;
		this.key.parm = parm;
		this.key.value = value;
	}
	
	public class Key{
		private Long id;
		public Long getId() {
			return id;
		}
		public String getParm() {
			return parm;
		}
		public String getValue() {
			return value;
		}
		private String parm;
		private String value;
	}
}
