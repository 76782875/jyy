package com.nsc.dem.action.bean;

import java.util.List;

/*
 * page��
 * */
public class TableBean {
	//���� ʵ�����ݵ�����
	private List<RowBean> rows;

	public List<RowBean> getRows() {
		return rows;
	}

	public void setRows(List<RowBean> rows) {
		this.rows = rows;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getRecords() {
		return records;
	}

	public void setRecords(int records) {
		this.records = records;
	}
	//��ǰ��
	private int page;
	//��ҳ��
	private int total;
	//��ѯ���ļ�¼��
	private int records;

}
