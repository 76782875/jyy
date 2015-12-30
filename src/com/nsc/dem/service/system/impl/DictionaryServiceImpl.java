package com.nsc.dem.service.system.impl;

import java.util.List;

import org.hibernate.FlushMode;

import com.nsc.dem.action.bean.TableBean;
import com.nsc.dem.bean.system.TDictionary;
import com.nsc.dem.service.base.BaseService;
import com.nsc.dem.service.system.IdictionaryService;

@SuppressWarnings("unchecked")
public class DictionaryServiceImpl extends BaseService implements IdictionaryService {

	/**
	 * ɾ�� �����ֵ� �Լ�����
	 */
	public void dictionaryDel(String[] dictionary) {
		if(dictionary==null){
			return;
		}
		for(int i=0;i<dictionary.length;i++){
			Object obj = super.EntityQuery(TDictionary.class, dictionary[i]);
			super.getSession().setFlushMode(FlushMode.AUTO);
			this.delEntity(obj);
			TDictionary dic = new TDictionary();
			dic.setParentCode(dictionary[i]);
			List list = super.EntityQuery(dic);
			if(list!=null&&list.size()>0){
				for(int j=0;j<list.size();j++){
					Object dict = list.get(j);
					this.delEntity(dict);
				}
			}
			
		}
	}

	/**
	 * ��ѯ  ���и������ֵ�
	 */
	public List dictionaryInfoList() {
		
		String hql = "from TDictionary as td where td.parentCode is null ";
		List<TDictionary> list=generalDAO.queryByHQL(hql);
		
		return list;
		
	}

	/**
	 * ���� ������������Դ  ��ѯ�����ֵ�
	 */
	public List<Object[]> dicSourceInfoList(String sql) {
		
		List<Object[]> list = super.generalDAO.queryByNativeSQL(sql);
		 
		return list;
	}
	/**
	 * 	�����ֵ����--�������ݱ�������ȡ������Ϣ
	 * @param  ����Ϊ�����ֵ���������
	 */

	public List<TDictionary> dictionaryList(String name) {
		String hqlParent = "from TDictionary as td where td.name = '"+name+"' ";
		List<TDictionary> listParent=generalDAO.queryByHQL(hqlParent);
		if(listParent.size()>0){
			TDictionary tDictionary=(TDictionary)(listParent.get(0));
			
			String hqlChild = "from TDictionary as td where td.parentCode = '"+tDictionary.getCode()+"' ";
			List<TDictionary> listChild=generalDAO.queryByHQL(hqlChild);
			
			return listChild;
		}
		return listParent;
	}

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

	public List querydictionaryList(Object[] obj,int firstResult,int maxResults,TableBean table,String flag) throws Exception{
		
		String sqlName = "";
	
		if(flag.equals("0")){
			sqlName = "dictionaryParentSearch";
		}else{
			sqlName = "dictionarySearch";
		}
		
//		Long count=super.generalDAO.getResultCount("dictionarySearch",obj);
//		
//		table.setRecords(count.intValue());
//
//		return count.intValue()==0?null:super.generalDAO.getResult("dictionarySearch",obj,firstResult,maxResults);

		Long count=super.generalDAO.getResultCount(sqlName,obj);
		
		table.setRecords(count.intValue());

		return count.intValue()==0?null:super.generalDAO.getResult(sqlName,obj,firstResult,maxResults);
		
	}
}
