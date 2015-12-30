package com.nsc.dem.action.system;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nsc.base.util.DateUtils;
import com.nsc.base.util.GetCh2Spell;
import com.nsc.dem.action.BaseAction;
import com.nsc.dem.action.bean.RowBean;
import com.nsc.dem.action.bean.TableBean;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.bean.project.TProject;
import com.nsc.dem.bean.system.TDictionary;
import com.nsc.dem.bean.system.TUnit;
import com.nsc.dem.service.project.IprojectService;
import com.nsc.dem.service.system.IdictionaryService;
import com.nsc.dem.service.system.IuserService;

/**
 * ��λ������ϢAction
 * 
 * @author ibm
 * 
 */
public class UnitInfoManagerAction extends BaseAction {
	private static final long serialVersionUID = 5304532811867020924L;
	IprojectService projectService;

	public void setProjectService(IprojectService projectService) {
		this.projectService = projectService;
	}

	private String code;
	private String unitCode;

	public void setUnitCode(String unitCode) {
		this.unitCode = unitCode;
	}

	public String getUnitCode() {
		return unitCode;
	}

	private String unitisUsable;

	public void setUnitisUsable(String unitisUsable) {
		this.unitisUsable = unitisUsable;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	private TUnit unit;

	public TUnit getUnit() {
		return unit;
	}

	public void setUnit(TUnit unit) {
		this.unit = unit;
	}

	private List<Map<String, Object>> list;
	private String codes;

	public void setCodes(String codes) {
		this.codes = codes;
	}

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

	// ��ѯʱ�����Ĳ���(��λ���롢��λ���͡���λ״̬����λ��ַ����λ�绰)
	private String unitNameCode;
	private String unitType;
	private String isUsable;

	public void setUnitNameCode(String unitNameCode) {
		this.unitNameCode = unitNameCode;
	}

	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}

	public void setIsUsable(String isUsable) {
		this.isUsable = isUsable;
	}

	public void setUnitAddress(String unitAddress) {
		this.unitAddress = unitAddress;
	}

	public void setUnitTelePhone(String unitTelePhone) {
		this.unitTelePhone = unitTelePhone;
	}

	private String unitAddress;
	private String unitTelePhone;

	IuserService userService;

	public void setUserService(IuserService userService) {
		this.userService = userService;
	}

	TableBean tablebean = new TableBean();

	public TableBean getTablebean() {
		return tablebean;
	}

	public void setTablebean(TableBean tablebean) {
		this.tablebean = tablebean;
	}

	// ҳ�������
	private int page;
	private int rows;

	String returnValue;

	public String getReturnValue() {
		return returnValue;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	/**
	 * ��ѯ��λAction
	 * 
	 * @return
	 * @throws Exception
	 */
	public String selectUnitInfoAction() throws Exception {
		// ��������λ��
		int firstResult = (page - 1) * rows;
		// ������ֹλ��
		int maxResults = page * rows;

		List<Object[]> list = new ArrayList<Object[]>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		if (unitType == null || unitType.equals("")) {
			map.put("ntype", null);
		} else {
			map.put("ntype", unitType);
		}
		if (unitNameCode == null || unitNameCode.equals("")) {
			map.put("ncode", null);
		} else {
			map.put("ncode", unitNameCode);
		}
		map.put("isusable", isUsable);
		String address = unitAddress.length() == 0 ? null : unitAddress;
		String telePhone = unitTelePhone.length() == 0 ? null : unitTelePhone;
		map.put("address", address);
		map.put("telephone", telePhone);
		list = userService.queryUnitInfoList(map, firstResult, maxResults,
				tablebean);

		List<RowBean> rowsList = new ArrayList<RowBean>();
		if (list != null) {
			for (Object[] obj : list) {
				TUser user = (TUser) obj[0];
				TUnit unit = (TUnit) obj[1];
				TDictionary tdic = (TDictionary) obj[2];
				TUnit px = (TUnit) obj[3];
				RowBean rowbean = new RowBean();
				String unitStatus = "";
				if (unit.getIsUsable() == true) {
					unitStatus = "����";
				} else {
					unitStatus = "������";
				}
				String proxy = "";
				if (px != null) {
					proxy = px.getName();
				}
				String utype = tdic.getName();

				// ȡʱ��
				String datetime = DateUtils.DateToString(unit.getCreateDate());
				rowbean.setCell(new Object[] {

						unit.getCode(),
						unit.getName(),
						unit.getShortName(),
						unitStatus,
						proxy,
						utype,
						unit.getAddress(),
						unit.getTelephone(),
						user.getName(),
						datetime,
						"<a href='#'  onclick='insertUnitShowDailog(\""
								+ unit.getCode() + "\")' >�༭</a>"

				});
				// ��ǰ��IDΪ1
				rowbean.setId(unit.getCode());
				rowsList.add(rowbean);
			}
		}
		// ����Ԫ����������
		tablebean.setRows(rowsList);

		if (tablebean.getRecords() == 0) {
			tablebean.setPage(0);
		} else {
			// ��ǰҳ��1ҳ
			tablebean.setPage(this.page);
		}

		// ��ѯ���ļ�¼��Ϊ100
		// tablebean.setRecords(records.intValue());

		int records = tablebean.getRecords();
		// ��ҳ��
		tablebean.setTotal(records % rows == 0 ? records / rows : records
				/ rows + 1);

		return SUCCESS;
	}

	private String proxyNameCode;

	public void setProxyNameCode(String proxyNameCode) {
		this.proxyNameCode = proxyNameCode;
	}

	/**
	 * ��������ʹ��Action
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String insertUnitAction() throws UnsupportedEncodingException {

		if (unitisUsable.equals("1")) {
			unit.setIsUsable(true);

		} else {
			unit.setIsUsable(false);
		}
		unit.setProxyCode(proxyNameCode);
		unit.setCreateDate(new Date());
		unit.setTUser(super.getLoginUser());

		if (unitCode == null || (unitCode.equals(""))) {
			userService.insertEntity(unit);
		} else {
			unit.setCode(unitCode);
			userService.updateEntity(unit);

		}

		returnValue = "success";
		return SUCCESS;
	}

	private List<Map<String, Object>> updateList;

	/**
	 * ��λ ����
	 * 
	 * @return
	 */
	public String updateSelectUnitAction() {
		updateList = new ArrayList<Map<String, Object>>();
		unit = (TUnit) userService.EntityQuery(TUnit.class, code);
		Map<String, Object> map = new HashMap<String, Object>();
		getRequest().setAttribute("tcode", code);
		map.put("code", unit.getCode());
		map.put("name", unit.getName());
		map.put("type", unit.getType());
		map.put("shortName", unit.getShortName());
		map.put("address", unit.getAddress());
		map.put("telePhone", unit.getTelephone());
		map.put("isUsable", unit.getIsUsable());
		String proxyCode = unit.getProxyCode();
		if (unit.getCode().equals(proxyCode)) {
			map.put("proxyName", "");
			map.put("proxyCode", "");
		} else {
			TUnit unit = (TUnit) userService
					.EntityQuery(TUnit.class, proxyCode);
			map.put("proxyName", unit.getName());
			map.put("proxyCode", unit.getCode());
		}

		updateList.add(map);
		return SUCCESS;
	}

	public List<Map<String, Object>> getUpdateList() {
		return updateList;
	}

	public void setUpdateList(List<Map<String, Object>> updateList) {
		this.updateList = updateList;
	}

	/**
	 * ɾ����λ
	 * 
	 * @return
	 */
	public String deleteUnitAction() {

		String[] codeList = codes.split(",");

		for (int i = 0; i < codeList.length; i++) {
			String eachCode = codeList[i];
			TUnit unit = (TUnit) userService.EntityQuery(TUnit.class, eachCode);
			userService.delEntity(unit);
		}
		returnValue = "success";
		return SUCCESS;
	}

	/**
	 * ��ʼ����λ����
	 */
	public String unitNameAction() {

		list = new ArrayList<Map<String, Object>>();
		List<TUnit> tunitList = userService.unitList();
		if (tunitList.size() == 0) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", "-1");
			map.put("name", "û������");
			map.put("spell", GetCh2Spell.getBeginCharacter("û������"));
			list.add(map);
		} else {
			for (int i = 0; i < tunitList.size(); i++) {
				TUnit tunit = tunitList.get(i);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", tunit.getCode());
				map.put("name", tunit.getName());
				map
						.put("spell", GetCh2Spell.getBeginCharacter(tunit
								.getName()));
				list.add(map);
			}
		}

		return SUCCESS;
	}

	/**
	 * ��ʼ����������
	 */
	public String desginNameAction() {

		list = new ArrayList<Map<String, Object>>();
		List<TUnit> tunitList = userService.designList();
		if (tunitList.size() == 0) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", "-1");
			map.put("name", "û������");
			map.put("spell", GetCh2Spell.getBeginCharacter("û������"));
			list.add(map);
		} else {
			for (int i = 0; i < tunitList.size(); i++) {
				TUnit tunit = tunitList.get(i);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", tunit.getCode());
				map.put("name", tunit.getName());
				map
						.put("spell", GetCh2Spell.getBeginCharacter(tunit
								.getName()));
				list.add(map);
			}
		}

		return SUCCESS;
	}

	private String unitcode;

	public void setUnitcode(String unitcode) {
		this.unitcode = unitcode;
	}

	private List<Map<String, Object>> userList;

	/**
	 * �鿴�˵�λ�������û�
	 * 
	 * @return
	 */
	public String getUserByUnitCodeAction() {
		userList = new ArrayList<Map<String, Object>>();
		List<TUser> userLists = userService.getUserByUnitCode(unitcode);
		for (int i = 0; i < userLists.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			TUser user = userLists.get(i);
			map.put("username", user.getName());
			map.put("loginId", user.getLoginId());
			map.put("uname", user.getTUnit().getName());
			map.put("duty", user.getDuty());
			map.put("isValid", user.getIsValid());
			userList.add(map);
		}

		return SUCCESS;
	}

	public List<Map<String, Object>> getUserList() {
		return userList;
	}

	public void setUserList(List<Map<String, Object>> userList) {
		this.userList = userList;
	}

	public List<Map<String, Object>> gettProjectList() {
		return tProjectList;
	}

	public void settProjectList(List<Map<String, Object>> tProjectList) {
		this.tProjectList = tProjectList;
	}

	private List<Map<String, Object>> tProjectList;

	/**
	 * ��ѯ�˵�λ�����Ĺ���ҵ����λ
	 * 
	 * @return
	 */
	public String getProjectByDesCodeAction() {
		tProjectList = new ArrayList<Map<String, Object>>();
		List<TProject> userLists = projectService.getProjectByDesCode(unitcode);
		for (int i = 0; i < userLists.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			TProject project = userLists.get(i);
			map.put("code", project.getCode());
			map.put("name", project.getName());
			tProjectList.add(map);
		}
		return SUCCESS;
	}

	private List<Map<String, Object>> owenList;

	public List<Map<String, Object>> getOwenList() {
		return owenList;
	}

	public void setOwenList(List<Map<String, Object>> owenList) {
		this.owenList = owenList;
	}

	/**
	 * ��ѯ�˵�λ�����ĳ��赥λ
	 * 
	 * @return
	 */
	public String getProjectByOwenCodeAction() {
		owenList = new ArrayList<Map<String, Object>>();
		List<TProject> userLists = projectService
				.getProjectByOwenCode(unitcode);
		for (int i = 0; i < userLists.size(); i++) {
			Map<String, Object> map = new HashMap<String, Object>();
			TProject project = userLists.get(i);
			map.put("code", project.getCode());
			map.put("name", project.getName());
			owenList.add(map);
		}
		return SUCCESS;
	}

	private List<Map<String, Object>> unitTypeList;

	public List<Map<String, Object>> getUnitTypeList() {
		return unitTypeList;
	}

	public void setUnitTypeList(List<Map<String, Object>> unitTypeList) {
		this.unitTypeList = unitTypeList;
	}

	IdictionaryService dictionaryService;

	public void setDictionaryService(IdictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	/**
	 * ��ʼ����λ����
	 * 
	 * @return
	 */
	public String unitTypeAction() {
		unitTypeList = new ArrayList<Map<String, Object>>();
		List<TDictionary> tunitList = dictionaryService.dictionaryList("��λ����");
		if (tunitList.size() == 0) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", "-1");
			map.put("name", "û������");
			map.put("spell", GetCh2Spell.getBeginCharacter("û������"));
			unitTypeList.add(map);
		} else {
			for (int i = 0; i < tunitList.size(); i++) {
				TDictionary tunit = tunitList.get(i);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", tunit.getCode());
				map.put("name", tunit.getName());
				map
						.put("spell", GetCh2Spell.getBeginCharacter(tunit
								.getName()));
				unitTypeList.add(map);
			}
		}

		return SUCCESS;
	}

	private List<Map<String, Object>> allList;

	public List<Map<String, Object>> getAllList() {
		return allList;
	}

	public void setAllList(List<Map<String, Object>> allList) {
		this.allList = allList;
	}

	private String type;

	public void setType(String type) {
		this.type = type;
	}

	public String unitNameAllAction() {
		allList = new ArrayList<Map<String, Object>>();
		List<TUnit> tunitList = userService.unitAllList(type);
		if (tunitList.size() == 0) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", "-1");
			map.put("name", "û������");
			map.put("spell", GetCh2Spell.getBeginCharacter("û������"));
			allList.add(map);
		} else {
			for (int i = 0; i < tunitList.size(); i++) {
				TUnit tunit = (TUnit) tunitList.get(i);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", tunit.getCode());
				map.put("name", tunit.getName());
				map
						.put("spell", GetCh2Spell.getBeginCharacter(tunit
								.getName()));
				allList.add(map);
			}
		}
		return SUCCESS;
	}
}
