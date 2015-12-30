package com.nsc.dem.service.system.impl;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.nsc.dem.action.bean.TableBean;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.bean.system.TServersInfo;
import com.nsc.dem.bean.system.TUnit;
import com.nsc.dem.service.base.BaseService;
import com.nsc.dem.service.system.IuserService;

@SuppressWarnings("unchecked")
public class UserServiceImpl extends BaseService implements IuserService {

	public void userDel(String[] user) {
		// TODO Auto-generated method stub

	}

	public List<Object> userInfoList() {
		// TODO Auto-generated method stub
		return null;
	}

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
			int firstResult, int maxResults, TableBean table) throws Exception {

		Long count = super.generalDAO.getResultCount("unitInfoSearch", map);

		table.setRecords(count.intValue());

		return count.intValue() == 0 ? null : super.generalDAO.getResult(
				"unitInfoSearch", map, firstResult, maxResults);

	}

	/**
	 * ��ѯ��λ��Ϣ
	 * 
	 * @param code
	 * @return
	 */
	public List<TUnit> unitList() {
		String ucode = super.currentUser.getTUnit().getProxyCode();
		String sql = "select t.* from t_unit t where t.code like '" + ucode
				+ "%'";

		List<TUnit> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TUnit.class);

		return list;
	}

	public List<TUnit> unitAllList(String type) {
		String sql = "select t.* from t_unit t where t.type='" + type + "'";
		List<TUnit> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TUnit.class);

		return list;
	}

	/**
	 * ��ѯ���赥λ
	 * 
	 * @param code
	 * @return
	 */
	public List designList() {

		String sql = "SELECT * FROM T_UNIT WHERE IS_USABLE=1 and type='C02'";

		List<TUnit> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TUnit.class);

		return list;
	}

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
			int firstResult, int maxResults, TableBean table) throws Exception {

		Long count = super.generalDAO.getResultCount("userInfoSearch", map);

		table.setRecords(count.intValue());

		return count.intValue() == 0 ? null : super.generalDAO.getResult(
				"userInfoSearch", map, firstResult, maxResults);

	}

	/**
	 * ���ݵ�λcode��ѯ����ʹ�õ�λ���û�
	 * 
	 * @param unitCode
	 * @return
	 */
	public List<TUser> getUserByUnitCode(String unitCode) {
		String sql = "select * from t_user u where u.unit_id='" + unitCode
				+ "'";

		List<TUser> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TUser.class);

		return list;
	}

	/**
	 * ��ѯ���еķ�������Ϣ
	 */
	public List<TServersInfo> findAllServersInfo() {
		String sql = "select * from t_serversinfo";
		List<TServersInfo> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TServersInfo.class);
		return list;
	}

	/**
	 */
	public List<TServersInfo> getServersInfoByCode(String unitCode) {
		String sql = "select * from t_serversinfo u where u.unit_code='" + unitCode+ "'";
		List<TServersInfo> list = super.generalDAO.queryByNativeSQLEntity(sql, TServersInfo.class);
		return list;
	}

}
