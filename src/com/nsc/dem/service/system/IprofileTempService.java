package com.nsc.dem.service.system;

import java.util.List;
import java.util.Map;

import com.nsc.dem.action.bean.TableBean;
import com.nsc.dem.bean.profile.TProfileTemp;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.service.base.IService;

public interface IprofileTempService  extends IService{
	

	// /**
	// * ��ʱȨ�޹���--��ѯ�б�
	// * ��Ҫ���ݲ�ѯ��������ʱȨ���л�ȡ��ʱȨ����Ϣ�б�
	// * @param ����Ϊ��ʱȨ�ޱ�ʵ����
	// * @return
	// */
	// public List<Object> profileTempInfoList();
	
	/**
	 * ��ѯ����Ȩ��Ϣ��ҳ
	 * 
	 * @param map
	 *            ����
	 * @param firstResult
	 *            ��ʼҳ��
	 * @param maxResults
	 *            ��βҳ��
	 * @param table
	 * @return Object[]
	 * @throws Exception
	 */
	public List<Object[]> queryYiProfileInfoList(Map<String, Object> map,
			int firstResult, int maxResults, TableBean table) throws Exception;
	
	
	/**
	 * ��ѯ������Ȩ��Ϣ��ҳ
	 * 
	 * @param map
	 *            ����
	 * @param firstResult
	 *            ��ʼҳ��
	 * @param maxResults
	 *            ��βҳ��
	 * @param table
	 * @return Object[]
	 * @throws Exception
	 */
	public List<Object[]> queryDataProfileList(Map<String, Object> map,
			int firstResult, int maxResults, TableBean table) throws Exception;

	/**
	 * ���ݽ�ɫID��Ȩ�޷����ѯ��Ȩ��Ϣ
	 * 
	 * @param roleId
	 * @param type
	 * @return
	 */
	public List<Object[]> getTProfileTempByroleIdandProfileId(Map<String,Object> map);

	/**
	 * ���ݽ�ɫID����ȨID��ѯ��ɫ����Ϣ
	 * 
	 * @param roleId
	 * @param profileId
	 * @return
	 */
	public List<TProfileTemp> getRoleProfileTempByRoleIdandProfileId(
			String roleId, String profileId);

	/**
	 * ���ݽ�ɫ��ѯ�û���Ϣ
	 * 
	 * @param roleId
	 * @return
	 */
	public List<TUser> getTUserByRoleId(String roleId);

	/**
	 * ����ID��ѯ��Ϣ
	 * @param map
	 * @return
	 */
	public List<Object[]> updateYiShouQuan(String id);
	
	/**
	 * ����ID��ѯ��Ȩ��Ϣ
	 * @param map
	 * @return
	 */
	public List<Object[]> updateDataProfile(String id);
}
