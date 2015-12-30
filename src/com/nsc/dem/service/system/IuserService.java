package com.nsc.dem.service.system;

import java.util.List;
import java.util.Map;

import com.nsc.dem.action.bean.TableBean;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.bean.system.TServersInfo;
import com.nsc.dem.bean.system.TUnit;
import com.nsc.dem.service.base.IService;


public interface IuserService extends IService {

	/**
	 * �û������ѯ�б� ��Ҫ���û����л�ȡ�û��б���Ϣ��
	 * 
	 * @param ����Ϊ�û�ʵ����
	 * @return
	 */
	public List<Object> userInfoList();

	/**
	 * �û�����--ɾ��
	 * 
	 * @param ����Ϊ�û�����������
	 */
	public void userDel(String user[]);

	/**
	 * ��λ��Ϣ��ҳ��ѯ
	 * 
	 * @param map
	 * @param firstResult
	 *            ��ʼ��¼����
	 * @param maxResults
	 *            ������¼����
	 * @return
	 * @throws Exception
	 */
	public List<Object[]> queryUnitInfoList(Map<String, Object> map,
			int firstResult, int maxResults, TableBean table) throws Exception;

	public List<TUnit> unitList();
	public List<TUnit> unitAllList(String type);

	public List<TUnit> designList();

	/**
	 * �û�������Ϣ ��ҳ��ѯ
	 * 
	 * @param map
	 * @param firstResult
	 *            ��ʼ��¼����
	 * @param maxResults
	 *            ������¼����
	 * @param table
	 * @return
	 * @throws Exception
	 */
	public List<Object[]> queryUserInfoList(Map<String, Object> map,
			int firstResult, int maxResults, TableBean table) throws Exception;

	
	public List<TUser> getUserByUnitCode(String unitCode);
	
	public List<TServersInfo> getServersInfoByCode(String unitCode);
	
	public List<TServersInfo> findAllServersInfo();
}
