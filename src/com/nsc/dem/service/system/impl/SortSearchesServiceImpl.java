package com.nsc.dem.service.system.impl;

import java.util.List;

import com.nsc.dem.bean.system.TRoleTree;
import com.nsc.dem.action.bean.TableBean;
import com.nsc.dem.service.base.BaseService;
import com.nsc.dem.service.system.IsortSearchesService;
@SuppressWarnings("unchecked")
public class SortSearchesServiceImpl extends BaseService implements IsortSearchesService{
	
	
	/**
	 * �ĵ��ȼ�������ҳ��ѯ
	 * 
	 * @param map
	 * @param firstResult
	 *            ��ʼ��¼����
	 * @param maxResults
	 *            ������¼����
	 * @return
	 * @throws Exception
	 */

	public List querySortList(Object[] obj,int firstResult,int maxResults,TableBean table) throws Exception{
		
		Long count=super.generalDAO.getResultCount("sortSearch",obj);
		
		table.setRecords(count.intValue());

		return count.intValue()==0?null:super.generalDAO.getResult("sortSearch",obj,firstResult,maxResults);
	}
	
	
	/**
	 * �ĵ��ȼ����ñ��ҳ��ѯ
	 * 
	 * @param map
	 * @param firstResult
	 *            ��ʼ��¼����
	 * @param maxResults
	 *            ������¼����
	 * @return
	 * @throws Exception
	 */

	public List queryTreeDefList(Object[] obj,int firstResult,int maxResults,TableBean table, String m) throws Exception{
		
		Long count=super.generalDAO.getResultCount(m,obj);
		
		table.setRecords(count.intValue());

		return count.intValue()==0?null:super.generalDAO.getResult(m,obj,firstResult,maxResults);
	}

    /**
     * ����ȼ�����-��ɫӳ��  ����ɾ����������
     */
	public List<TRoleTree> delRoleTree(String treeId) {
		// TODO Auto-generated method stub
		String hql="from TRoleTree tr where tr.id.TTreeDef.id= '" +treeId+"' " ;
		
		return this.generalDAO.queryByHQL(hql);
	}
}
