package com.nsc.dem.service.system.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nsc.base.conf.Configurater;
import com.nsc.dem.action.bean.RoleBean;
import com.nsc.dem.action.bean.TableBean;
import com.nsc.dem.bean.archives.TDocType;
import com.nsc.dem.bean.profile.TProfileTemp;
import com.nsc.dem.bean.profile.TRole;
import com.nsc.dem.bean.profile.TRoleProfile;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.bean.system.TDictionary;
import com.nsc.dem.bean.system.TRoleTree;
import com.nsc.dem.service.base.BaseService;
import com.nsc.dem.service.system.IroleService;

@SuppressWarnings("unchecked")
public class RoleServiceImpl extends BaseService implements IroleService {

	/**
	 * ��ɫ����--��ѯ�б� ��Ҫ���ݲ�ѯ�����ӽ�ɫ���л�ȡ��ɫ��Ϣ�б�
	 * 
	 * @param ����Ϊ��ɫ��ʵ����
	 * @return
	 */
	public List<Object> roleInfoList() {
		return null;
	}

	/**
	 * ��ɫ����--ɾ��
	 * 
	 * @param ����Ϊ��ɫ����������
	 */
	public void roleDel(String role[]) {

	}

	/**
	 * ��������Ȩ�޻��Ĭ��Ȩ���ַ���
	 * 
	 * @param field
	 *            �ֶ�����
	 * @return
	 */
	public List<Map<String, Object>> getRolePriority(Object entity,
			String field, List<TRole> tRoleList) {
		List<Map<String, Object>> rolePriority = new ArrayList<Map<String, Object>>();
		// ��ȡ�����ļ�
		Configurater config = Configurater.getInstance();
		// ȡ�������ļ��е�������Ϣ
		String proiority = config.getConfigValue("PRIORITY_CONFIG_KEY");
		// �÷ֺŽ�Ԫ���۷�
		String[] tempproiority = proiority.split(";");
		// �����ִ����Object������
		Object[] names = new Object[tempproiority.length];

		String cfgStr = "";
		for (int i = 0; i < tempproiority.length; i++) {
			String[] nameArray = tempproiority[i].split(":");
			// ȡ�������ļ��е�ֵ������һ��Object��
			names[i] = nameArray[1];
			// ȡ�������ļ��е�key������һ���ַ�����
			cfgStr += nameArray[0];
		}

		if (entity != null && field != null) {
			// ȡ��ǰ��ɫ
			String privilege = field;
			// ��ɫ1:01;��ɫ2:11
			String[] tempprivilege = privilege.split(";");

			for (int j = 0; j < tRoleList.size(); j++) {
				TRole trole = (TRole) tRoleList.get(j);
				// ȡ����ɫ����
				String[] roles = new String[tRoleList.size()];
				// ȡ����Ȩ��Ϣ����
				String[] priStrs = new String[tRoleList.size()];
				RoleBean rolebean = new RoleBean();
				String priStr = "";
				String tempId = "";
				String tempPri = "";
				// ��ɫ1:01
				for (int i = 0; i < tempprivilege.length; i++) {
					String[] rolpriStrArray = tempprivilege[i].split(":");
					// �ҵ��ý�ɫ�����ݿ����Ѿ������ˡ�
					if (rolpriStrArray[0].equals(trole.getId())) {
						tempId = rolpriStrArray[0];
						tempPri = rolpriStrArray[1];
						break;
					}
				}
				// ��ɫ1
				roles[j] = tempId == null ? "" : trole.getId();
				// ���ݽǽ�ID��ѯ����ɫ����
				// 01
				priStrs[j] = tempPri == null ? "" : tempPri;

				rolebean.setRoleid(trole.getId());
				rolebean.setRolename(trole.getName());
				priStr = priStrs[j];
				// 01+XXX
				priStr += cfgStr.substring(priStr.length());
				char[] readPri = priStr.toCharArray();

				Map<String, Object> map = new HashMap<String, Object>();
				map.put("rolebean", rolebean);
				map.put("readPri", readPri);
				map.put("names", names);
				// 0,1,X,X,X
				// Object[] readPri = new Object []{priStr};
				// �����list�� �û���ɫ �Ƿ��ȡ �Ƿ�����Ȩ ��ȡ����Ȩ������
				rolePriority.add(map);
			}
		} else {
			// ��ɫ
			char[] readPri = cfgStr.toCharArray();
			String roleid = "";
			String rolename = "";
			for (int j = 0; j < tRoleList.size(); j++) {
				TRole trole = (TRole) tRoleList.get(j);
				roleid = trole.getId();
				rolename = trole.getName();
				RoleBean rolebean = new RoleBean();
				rolebean.setRoleid(roleid);
				rolebean.setRolename(rolename);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("rolebean", rolebean);
				map.put("readPri", readPri);
				map.put("names", names);
				// 0,1,X,X,X
				// Object[] readPri = new Object []{priStr};
				// �����list�� �û���ɫ �Ƿ��ȡ �Ƿ�����Ȩ ��ȡ����Ȩ������
				rolePriority.add(map);
			}

		}
		return rolePriority;
	}

	/**
	 * ��ѯ���еĽ�ɫ
	 * 
	 * @return
	 */

	public List<TRole> roleList() {

		String sql = "select * from t_role r";

		List<TRole> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TRole.class);

		return list;
	}

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
			int firstResult, int maxResults, TableBean table) throws Exception {

		Long count = super.generalDAO.getResultCount("roleInfoSearch", map);

		table.setRecords(count.intValue());

		return count.intValue() == 0 ? null : super.generalDAO.getResult(
				"roleInfoSearch", map, firstResult, maxResults);
	}

	/**
	 * ���ݽ�ɫID��ѯ����ʹ�ý�ɫ���û�
	 * 
	 * @param roleid
	 * @return
	 */
	public List<TUser> getUserByRoleId(String roleid) {
		String sql = "select * from t_user u where u.role_id='" + roleid + "'";

		List<TUser> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TUser.class);

		return list;
	}

	/**
	 * ���ݽ�ɫID��ѯ���ν�ɫ
	 * 
	 * @param id
	 * @return
	 */
	public List<TRoleTree> getRoleIdByTRoleTreeId(String id) {
		String sql = "select *  from t_role_tree t where t.id='" + id + "'";

		List<TRoleTree> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TRoleTree.class);

		return list;

	}

	/**
	 * ���ݽ�ɫID��ѯ��ݲ˵�
	 * 
	 * @param id
	 * @return
	 */
	public List<TRoleProfile> getRoleIdByTRoleProFileId(String id) {
		String sql = "select * from t_role_profile t where t.role_id = '" + id
				+ "'";

		List<TRoleProfile> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TRoleProfile.class);

		return list;
	}

	/**
	 * 
	 *���ݽ�ɫID��ѯ �����ֵ��
	 * 
	 * @param id
	 * @return
	 */
	public List<TDictionary> getRoleIdByTDictionaryAuthControl(String roleid) {
		String sql = "select t.* from t_dictionary t where t.auth_control  like '%"
				+ roleid + "%'";

		List<TDictionary> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TDictionary.class);

		return list;
	}

	/**
	 * ���ݽ�ɫID��ѯ�ĵ������
	 * 
	 * @param roleid
	 * @return
	 */
	public List<TDocType> getRoleIdByTDocTypePrivilege(String roleid) {
		String sql = "select * from t_doc_type  t where t.privilege like '%"
				+ roleid + "%'";

		List<TDocType> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TDocType.class);

		return list;
	}

	/**
	 * ���ݽ�ɫID��ѯ��ʱ��Ȩ��
	 * 
	 * @param roleid
	 * @return
	 */
	public List<TProfileTemp> getRoleIdByTProfileTempRole(String roleid) {
		String sql = "select * from t_profile_temp  t where t.role like '%"
				+ roleid + "%'";

		List<TProfileTemp> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TProfileTemp.class);

		return list;
	}

	/**
	 * ɾ����ɫ��Ϣ
	 * 
	 * @param roleId
	 */
	public void deleteRoleInfo(IroleService roleService, String roleId) {

		TRole role = (TRole) roleService.EntityQuery(TRole.class, roleId);

		List<TRoleTree> roleTreeList = roleService
				.getRoleIdByTRoleTreeId(roleId);
		if (roleTreeList.size() != 0) {
			for (int j = 0; j < roleTreeList.size(); j++) {
				TRoleTree roletree = (TRoleTree) roleTreeList.get(j);
				roleService.delEntity(roletree);
			}

		}

		List<TRoleProfile> roleProfileList = roleService
				.getRoleIdByTRoleProFileId(roleId);
		if (roleProfileList.size() != 0) {
			for (int k = 0; k < roleProfileList.size(); k++) {
				TRoleProfile roleProfile = (TRoleProfile) roleProfileList
						.get(k);
				roleService.delEntity(roleProfile);
			}
		}

		List<TDictionary> dicList = roleService
				.getRoleIdByTDictionaryAuthControl(roleId);
		for (int j = 0; j < dicList.size(); j++) {
			TDictionary dic = dicList.get(j);

			String[] authArray = dic.getAuthControl().split(";");

			for (int k = 0; k < authArray.length; k++) {
				if (authArray[k].startsWith(roleId)) {
					authArray[k] = null;
				}
			}
			String auth = "";
			for (int k = 0; k < authArray.length; k++) {

				if (authArray[k] != null) {
					auth += authArray[k] + ";";
				}

			}
			if (auth.trim().length() > 0) {
				auth = auth.substring(0, auth.length() - 1);
			}
			dic.setAuthControl(auth);
			roleService.updateEntity(dic);

		}

		List<TDocType> docTypeList = roleService
				.getRoleIdByTDocTypePrivilege(roleId);
		for (int j = 0; j < docTypeList.size(); j++) {
			TDocType docType = docTypeList.get(j);

			String[] authArray = docType.getPrivilege().split(";");

			for (int k = 0; k < authArray.length; k++) {
				if (authArray[k].startsWith(roleId)) {
					authArray[k] = null;
				}
			}
			String auth = "";
			for (int k = 0; k < authArray.length; k++) {

				if (authArray[k] != null) {
					auth += authArray[k] + ";";
				}

			}
			if (auth.trim().length() > 0) {
				auth = auth.substring(0, auth.length() - 1);
			}
			docType.setPrivilege(auth);
			roleService.updateEntity(docType);

		}

		List<TProfileTemp> profileTempList = roleService
				.getRoleIdByTProfileTempRole(roleId);

		for (int j = 0; j < profileTempList.size(); j++) {
			TProfileTemp profiletemp = profileTempList.get(j);

			String[] authArray = profiletemp.getRole().split(";");

			for (int k = 0; k < authArray.length; k++) {
				if (authArray[k].startsWith(roleId)) {
					authArray[k] = null;
				}
			}
			String auth = "";
			for (int k = 0; k < authArray.length; k++) {

				if (authArray[k] != null) {
					auth += authArray[k] + ";";
				}

			}
			if (auth.trim().length() > 0) {
				auth = auth.substring(0, auth.length() - 1);
			}
			profiletemp.setRole(auth);
			roleService.updateEntity(profiletemp);

		}
		roleService.delEntity(role);
	}

	/**
	 * ��ѯ�û���ɫ
	 * 
	 * @param roleId
	 * @return
	 */
	public List<TRole> queryTRoleList(String roleId) {
		String sql;
		if (roleId != null) {
			sql = "select * from t_role r where r.id='" + roleId + "'";
		} else {
			sql = "select * from t_role r";
		}

		List<TRole> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TRole.class);

		return list;

	}

	/**
	 * �����ֶ�����ѯ�����ӦȨ��
	 * 
	 * @param field
	 */
	public String getfieldSelectAuthControl(String field) {
		String authControle = "";
		// ��ȡ�����ļ�
		Configurater config = Configurater.getInstance();
		// ȡ�������ļ��е�������Ϣ
		String proiority = config.getConfigValue("PRIORITY_CONFIG_KEY");
		// �÷ֺŽ�Ԫ���۷�
		String[] tempproiority = proiority.split(";");
		String configpro = "";
		for (int i = 0; i < tempproiority.length; i++) {
			String[] eachpro = tempproiority[i].split(":");
			configpro += eachpro[1] + ",";
		}

		if (field != null) {
			String[] authControlArray = field.split(";");
			for (int i = 0; i < authControlArray.length; i++) {
				String[] eachAuth = authControlArray[i].split(":");
				String tempconfigPro = configpro.substring(0, configpro
						.length() - 1);
				String[] eachConfigPro = tempconfigPro.split(",");
				TRole role = (TRole) super
						.EntityQuery(TRole.class, eachAuth[0]);
				authControle += role.getName() + ":";
				char[] tempeach = eachAuth[1].toCharArray();
				for (int j = 0; j < tempeach.length; j++) {
					if (tempeach[j] == '1') {
						authControle += eachConfigPro[j];
						authControle += ",";
					}
				}
				authControle = authControle.substring(0,
						authControle.length() - 1);

				authControle += ";";

			}
			authControle = authControle.substring(0, authControle.length() - 1);
		}

		return authControle;
	}
}
