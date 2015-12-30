package com.nsc.dem.action.system;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.struts2.ServletActionContext;
import com.nsc.dem.action.BaseAction;
import com.nsc.dem.action.bean.DictionaryBean;
import com.nsc.dem.action.bean.RowBean;
import com.nsc.dem.action.bean.TableBean;
import com.nsc.dem.bean.profile.TRole;
import com.nsc.dem.bean.system.TDictionary;
import com.nsc.dem.service.system.IdictionaryService;
import com.nsc.dem.service.system.IroleService;

/**
 * 
 * ϵͳ�����������ֵ�
 * 
 * @author ibm
 * 
 */
public class DictionaryAction extends BaseAction {
	private static final long serialVersionUID = 5304532811867020924L;
	// ɾ��������codes
	String codes;
	String returnValue;
	// �����������б���ֵ
	String checkCode;

	public void setCheckCode(String checkCode) {
		this.checkCode = checkCode;
	}

	String dicname;

	public void setDicname(String dicname) {
		this.dicname = dicname;
	}

	String diccode;

	public String getDiccode() {
		return diccode;
	}

	public void setDiccode(String diccode) {
		this.diccode = diccode;
	}

	public String getReturnValue() {
		return returnValue;
	}

	public void setCodes(String codes) {
		this.codes = codes;
	}

	private String updateCode;

	public void setUpdateCode(String updateCode) {
		this.updateCode = updateCode;
	}

	// Ĭ�ϵ�ҳ��
	private int page;

	public void setPage(int page) {
		this.page = page;
	}

	private int rows;

	public void setRows(int rows) {
		this.rows = rows;
	}

	// ���������ֵ����
	private TDictionary tdictionary = new TDictionary();

	public TDictionary getTdictionary() {
		return tdictionary;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public void setTdictionary(TDictionary tdictionary) {
		this.tdictionary = tdictionary;
	}

	// ������code
	String code;

	// ��ɫ��
	private List<TRole> tRoleList;

	public void settRoleList(List<TRole> tRoleList) {
		this.tRoleList = tRoleList;
	}

	IdictionaryService dictionaryService;

	IroleService roleService;

	public void setRoleService(IroleService roleService) {
		this.roleService = roleService;
	}

	public void setDictionaryService(IdictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	// ���ص�json

	private List<Map<String, Object>> rolePriority;

	public List<Map<String, Object>> getRolePriority() {
		return rolePriority;
	}

	public void setRolePriority(List<Map<String, Object>> rolePriority) {
		this.rolePriority = rolePriority;
	}

	TableBean tablebean = new TableBean();

	public TableBean getTablebean() {
		return tablebean;
	}

	// ��������������е�ֵ
	private List<DictionaryBean> selectList;

	public List<DictionaryBean> getSelectList() {
		return selectList;
	}

	public void setSelectList(List<DictionaryBean> selectList) {
		this.selectList = selectList;
	}

	private List<Map<String, Object>> updateList;

	public List<Map<String, Object>> getUpdateList() {
		return updateList;
	}

	public void setUpdateList(List<Map<String, Object>> updateList) {
		this.updateList = updateList;
	}

	/**
	 * ��ѯȨ�ޣ�����ʱ������Ϣ��ֵ
	 * 
	 * @return
	 */
	public String getRoleProioityAction() {

		rolePriority = new ArrayList<Map<String, Object>>();

		TDictionary dic = (TDictionary) dictionaryService.EntityQuery(
				TDictionary.class, code);
		tRoleList = roleService.queryTRoleList(null);
		if (dic != null) {
			rolePriority = roleService.getRolePriority(dic, dic
					.getAuthControl(), tRoleList);
		} else {
			rolePriority = roleService.getRolePriority(dic, null, tRoleList);
		}

		return SUCCESS;
	}

	/**
	 * 
	 * ��ѯ�����ֵ��action
	 * 
	 * @return
	 * @throws Exception
	 * 
	 */
	public String getDictionaryDataAction() throws Exception {

		ServletActionContext.getResponse().setCharacterEncoding("utf-8");
		ServletActionContext.getRequest().setCharacterEncoding("utf-8");
		// ��ʼλ��Ϊ��(ҳ��-1)*rows
		int firstResult = (page - 1) * rows;
		// ��ֹλ��Ϊ��ҳ��*����-1
		int maxResults = page * rows;

		List<Object[]> list = new ArrayList<Object[]>();

		String tempName = dicname == null ? "%" : "%" + dicname + "%";

		Object[] object;
		String flog = "";
		if (diccode == null || diccode.equals("0")) {
			diccode = null;
			flog = "0";
			object = new Object[] { tempName };
		} else {
			object = new Object[] { diccode, tempName };
		}
		list = dictionaryService.querydictionaryList(object, firstResult,
				maxResults, tablebean, flog);

		List<RowBean> rowsList = new ArrayList<RowBean>();
		if (list != null) {

			for (Object[] obj : list) {
				RowBean rowbean = new RowBean();

				TDictionary tdic = (TDictionary) obj[0];
				String temptime = tdic.getCreateDate().toString();
				String[] create = temptime.split(" ");

				String authControle = roleService
						.getfieldSelectAuthControl(tdic.getAuthControl());
				rowbean.setCell(new Object[] {

						tdic.getCode(),
						tdic.getName(),
						authControle,
						tdic.getTUser().getName(),

						create[0],
						tdic.getRemark(),
						"<a href='#' onclick='updateDictionary(\""
								+ tdic.getCode()
								+ "\")' >����</a>" });

				// ��ǰ��IDΪ1
				rowbean.setId(tdic.getCode());
				rowsList.add(rowbean);
			}

			// ����Ԫ����������
			tablebean.setRows(rowsList);

			if (tablebean.getRecords() == 0) {
				tablebean.setPage(0);
			} else {
				// ��ǰҳ��1ҳ
				tablebean.setPage(this.page);
			}

			int records = tablebean.getRecords();
			// ��ҳ��
			tablebean.setTotal(records % rows == 0 ? records / rows : records
					/ rows + 1);
		}

		return SUCCESS;
	}

	/**
	 * 
	 * ��ѯ�����и���ı��룬���������б�� ��
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getSelectDictionaryAction()
			throws UnsupportedEncodingException {
		selectList = new ArrayList<DictionaryBean>();
		List<Object> list = dictionaryService.dictionaryInfoList();
		for (int i = 0; i < list.size(); i++) {
			DictionaryBean dicbean = new DictionaryBean();
			TDictionary tdic = (TDictionary) list.get(i);
			dicbean.setCode(tdic.getCode());
			dicbean.setName(tdic.getName());
			selectList.add(dicbean);
		}

		return SUCCESS;
	}

	/**
	 * 
	 * ���²���action
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getUpdateDictionaryAction()
			throws UnsupportedEncodingException {
		ServletActionContext.getResponse().setCharacterEncoding("utf-8");
		ServletActionContext.getRequest().setCharacterEncoding("utf-8");

		// ������
		tdictionary.setTUser(super.getLoginUser());

		tdictionary.setCreateDate(new Date());
		if (updateCode == null || updateCode.equals("")) {
			dictionaryService.insertEntity(tdictionary);
		} else {
			dictionaryService.updateEntity(tdictionary);
		}
		returnValue = "success";
		return SUCCESS;
	}

	/**
	 * ɾ�������ֵ�
	 * 
	 * @return
	 */
	public String getDeleteDictionaryAction() {

		String[] codeList = codes.split(",");

		dictionaryService.dictionaryDel(codeList);

		returnValue = "success";
		return SUCCESS;

	}

	/**
	 * 
	 *��ѯ��ǰ�ֵ�����Ƿ����
	 * 
	 * @return
	 */
	public String selectDictionaryCodeAction() {
		TDictionary tdic = (TDictionary) dictionaryService.EntityQuery(
				TDictionary.class, checkCode);
		if (tdic != null) {
			returnValue = "true";

		} else {
			returnValue = "false";
		}
		return SUCCESS;
	}

	/**
	 * ����ʱ��ѯ
	 * 
	 * @return
	 */
	public String getDictionaryByCodeAction() {
		updateList = new ArrayList<Map<String, Object>>();
		TDictionary dic = (TDictionary) dictionaryService.EntityQuery(
				TDictionary.class, code);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pCode", dic.getParentCode());
		map.put("code", dic.getCode());
		map.put("dicName", dic.getName());
		map.put("remark", dic.getRemark());
		updateList.add(map);
		return SUCCESS;
	}
}
