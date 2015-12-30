package com.nsc.dem.service.system.impl;

import java.util.List;
import java.util.Map;

import com.nsc.dem.action.bean.TableBean;
import com.nsc.dem.bean.profile.TProfileTemp;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.service.base.BaseService;
import com.nsc.dem.service.system.IprofileTempService;

@SuppressWarnings("unchecked")
public class ProfileTempServiceImpl extends BaseService implements
		IprofileTempService {

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
			int firstResult, int maxResults, TableBean table) throws Exception {
		if (map.get("guoqishouquan") == null) {
			Long count = super.generalDAO.getResultCount("yishouquanSearch",
					map);
			table.setRecords(count.intValue());

			return count.intValue() == 0 ? null : super.generalDAO.getResult(
					"yishouquanSearch", map, firstResult, maxResults);
		} else {
			Long count = super.generalDAO.getResultCount(
					"yiguoqishouquanSearch", map);
			table.setRecords(count.intValue());

			return count.intValue() == 0 ? null : super.generalDAO.getResult(
					"yiguoqishouquanSearch", map, firstResult, maxResults);
		}

	}

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
			int firstResult, int maxResults, TableBean table) throws Exception {

		if (map.get("guoqishouquan") == null) {
			Long count = super.generalDAO
					.getResultCount("dataProfileInfo", map);
			table.setRecords(count.intValue());

			return count.intValue() == 0 ? null : super.generalDAO.getResult(
					"dataProfileInfo", map, firstResult, maxResults);
		} else {
			Long count = super.generalDAO.getResultCount(
					"dataguoqiProfileInfo", map);
			table.setRecords(count.intValue());

			return count.intValue() == 0 ? null : super.generalDAO.getResult(
					"dataguoqiProfileInfo", map, firstResult, maxResults);
		}
	}

	/**
	 * ���ݽ�ɫID����ȨID��ѯ��ɫ����Ϣ
	 * 
	 * @param roleId
	 * @param profileId
	 * @return
	 */
	public List<TProfileTemp> getRoleProfileTempByRoleIdandProfileId(
			String roleId, String profileId) {
		String sql = "select * from t_profile_temp tp where tp.role='" + roleId
				+ "' and tp.profile_id='" + profileId + "'";
		List<TProfileTemp> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TProfileTemp.class);

		return list;
	}

	/**
	 * ���ݽ�ɫ��ѯ�û���Ϣ
	 * 
	 * @param roleId
	 * @return
	 */
	public List<TUser> getTUserByRoleId(String roleId) {
		String sql = "select * from t_user  u where u.role_id='" + roleId + "'";
		List<TUser> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TUser.class);
		return list;
	}

	/**
	 * ����ID��ѯ��Ϣ
	 * 
	 * @param map
	 * @return
	 */
	public List<Object[]> updateYiShouQuan(String id) {
		List<Object[]> list = super.generalDAO.getResult("updateYiShouQuan",
				new Object[] { id });
		return list;
	}

	/**
	 * ����ID��ѯ��Ȩ��Ϣ
	 * 
	 * @param map
	 * @return
	 */
	public List<Object[]> updateDataProfile(String id) {
		List<Object[]> list = super.generalDAO.getResult("updateDataProfile",
				new Object[] { id });
		return list;

	}

	/**
	 * ���ݽ�ɫID��Ȩ�޷����ѯ��Ȩ��Ϣ
	 * 
	 * @param roleId
	 * @param type
	 * @return
	 */
	public List<Object[]> getTProfileTempByroleIdandProfileId(
			Map<String, Object> map) {
		List<Object[]> list = super.generalDAO.getResult("tempProfile", map);
		return list;
	}

}
