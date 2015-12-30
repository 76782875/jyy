package com.nsc.dem.service.system;

import java.util.List;
import java.util.Map;

import com.nsc.dem.action.bean.TableBean;
import com.nsc.dem.bean.archives.TDocType;
import com.nsc.dem.bean.profile.TProfileTemp;
import com.nsc.dem.bean.profile.TRole;
import com.nsc.dem.bean.profile.TRoleProfile;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.bean.system.TDictionary;
import com.nsc.dem.bean.system.TRoleTree;
import com.nsc.dem.service.base.IService;

public interface IroleService extends IService {

	/**
	 * ��ɫ����--��ѯ�б� ��Ҫ���ݲ�ѯ�����ӽ�ɫ���л�ȡ��ɫ��Ϣ�б�
	 * 
	 * @param ����Ϊ��ɫ��ʵ����
	 * @return
	 */
	public List<Object> roleInfoList();

	/**
	 * ��ɫ����--ɾ��
	 * 
	 * @param ����Ϊ��ɫ����������
	 */
	public void roleDel(String role[]);

	/**
	 * ��������Ȩ�޻��Ĭ��Ȩ���ַ���
	 * 
	 * @param field
	 *            �ֶ�����
	 * @return
	 */
	public List<Map<String, Object>> getRolePriority(Object entity,
			String field, List<TRole> tRoleList);

	/**
	 * �����ֶ�����ѯ�����ӦȨ��
	 * 
	 * @param field
	 */
	public String getfieldSelectAuthControl(String field);

	/**
	 * ��ѯ���еĽ�ɫ
	 * 
	 * @return
	 */
	public List<TRole> roleList();

	/**
	 * ��ɫ��Ϣ���� ��ҳ��ѯ
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
	public List<Object[]> queryRoleInfoList(Map<String, Object> map,
			int firstResult, int maxResults, TableBean table) throws Exception;

	/**
	 * ���ݽ�ɫID��ѯ����ʹ�ý�ɫ���û�
	 * 
	 * @param roleid
	 * @return
	 */
	public List<TUser> getUserByRoleId(String roleid);

	/**
	 * ���ݽ�ɫID��ѯ���ν�ɫ
	 * 
	 * @param id
	 * @return
	 */
	public List<TRoleTree> getRoleIdByTRoleTreeId(String id);

	/**
	 * ���ݽ�ɫID��ѯ��ݲ˵�
	 * 
	 * @param id
	 * @return
	 */
	public List<TRoleProfile> getRoleIdByTRoleProFileId(String id);

	/**
	 * 
	 *���ݽ�ɫID��ѯ �����ֵ��
	 * 
	 * @param id
	 * @return
	 */
	public List<TDictionary> getRoleIdByTDictionaryAuthControl(String roleid);

	/**
	 * ���ݽ�ɫID��ѯ�ĵ������
	 * 
	 * @param roleid
	 * @return
	 */
	public List<TDocType> getRoleIdByTDocTypePrivilege(String roleid);

	/**
	 * ���ݽ�ɫID��ѯ��ʱ��Ȩ��
	 * 
	 * @param roleid
	 * @return
	 */
	public List<TProfileTemp> getRoleIdByTProfileTempRole(String roleid);

	/**
	 * ɾ����ɫ��Ϣ
	 * 
	 * @param roleId
	 */
	public void deleteRoleInfo(IroleService roleService, String roleId);

	/**
	 * ��ѯ�û���ɫ
	 * 
	 * @param roleId
	 * @return
	 */
	public List<TRole> queryTRoleList(String roleId);
}
