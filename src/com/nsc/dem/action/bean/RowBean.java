package com.nsc.dem.action.bean;

/*
 * ���ID��һ��Object[]
 * */
public class RowBean {
	// ��ID
	private String id;
	// ��ǰ�е����е�Ԫ��
	private Object[] cell;

	private Object bean;

	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}

	public Object getCell() {
		return cell == null ? bean : cell;
	}

	public void setCell(Object[] cell) {
		this.cell = cell;
	}

	public void setCell(Object bean) {
		this.bean = bean;
	}
}
