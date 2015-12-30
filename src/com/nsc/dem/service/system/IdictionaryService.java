package com.nsc.dem.service.system;

import java.util.List;

import com.nsc.dem.action.bean.TableBean;
import com.nsc.dem.bean.system.TDictionary;
import com.nsc.dem.service.base.IService;

public interface IdictionaryService extends IService {
	
	/**
	 * 	�����ֵ����--��ѯ�б�    ��Ҫ���ݲ�ѯ�����������ֵ���л�ȡ�����ֵ��б���Ϣ
	 * @param  ����Ϊ�����ֵ�ʵ����
	 * @return
	 */
	public List<Object> dictionaryInfoList();
	
	public List<Object[]> dicSourceInfoList(String sql);
	
	/**
	 * 	�����ֵ����--ɾ��
	 * @param  ����Ϊ�����ֵ���������
	 */
	public void dictionaryDel(String dictionary[]);
	
	/**
	 * 	�����ֵ����--�������ݱ�������ȡ������Ϣ
	 * @param  ����Ϊ�����ֵ���������
	 */
	public List<TDictionary> dictionaryList(String name);
	
	
	/**
	 * �����ֵ�����ҳ��ѯ
	 * 
	 * @param map
	 * @param firstResult
	 *            ��ʼ��¼����
	 * @param maxResults
	 *            ������¼����
	 * @return
	 * @throws Exception
	 */
	public List<Object[]> querydictionaryList(Object[] obj,int firstResult,int maxResults,TableBean table,String flag) throws Exception;
	
}
