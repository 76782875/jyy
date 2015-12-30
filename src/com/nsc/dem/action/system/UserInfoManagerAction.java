package com.nsc.dem.action.system;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nsc.base.util.DateUtils;
import com.nsc.dem.action.BaseAction;
import com.nsc.dem.action.bean.RowBean;
import com.nsc.dem.action.bean.TableBean;
import com.nsc.dem.bean.profile.TRole;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.bean.system.TUnit;
import com.nsc.dem.service.system.IroleService;
import com.nsc.dem.service.system.IuserService;

/**
 * �û���Ϣ����
 * 
 * @author Administrator
 * 
 */
public class UserInfoManagerAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	private String insertisValid;
	private String isLoginId;
	private Long password;
	

	public void setPassword(Long password) {
		this.password = password;
	}

	public void setIsLoginId(String isLoginId) {
		this.isLoginId = isLoginId;
	}

	public void setInsertisValid(String insertisValid) {
		this.insertisValid = insertisValid;
	}

	private String updateId;

	public void setUpdateId(String updateId) {
		this.updateId = updateId;
	}

	private int page;
	private int rows;

	public void setPage(int page) {
		this.page = page;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	// ǰ̨�����Ĳ�����¼�� �û����� ������λ ��ɫ �Ƿ���Ч
	private String loginId;
	private String userName;
	private String uNameCode;

	public void setuNameCode(String uNameCode) {
		this.uNameCode = uNameCode;
	}

	private String roleId;
	private String isValid;

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public void setIsValid(String isValid) {
		this.isValid = isValid;
	}

	IuserService userService;

	public void setUserService(IuserService userService) {
		this.userService = userService;
	}

	IroleService roleService;

	public void setRoleService(IroleService roleService) {
		this.roleService = roleService;
	}

	TableBean tablebean = new TableBean();

	public TableBean getTablebean() {
		return tablebean;
	}

	public void setTablebean(TableBean tablebean) {
		this.tablebean = tablebean;
	}

	private List<Map<String, Object>> updateList;

	public void setUpdateList(List<Map<String, Object>> updateList) {
		this.updateList = updateList;
	}

	public List<Map<String, Object>> getUpdateList() {
		return updateList;
	}

	private List<Map<String, Object>> list;

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

	private String codes;

	public void setCodes(String codes) {
		this.codes = codes;
	}

	private String returnValue;

	public String getReturnValue() {
		return returnValue;
	}

	private TUser tuser;

	public TUser getTuser() {
		return tuser;
	}

	public void setTuser(TUser tuser) {
		this.tuser = tuser;
	}

	private String insertRoleId;

	public void setInsertRoleId(String insertRoleId) {
		this.insertRoleId = insertRoleId;
	}

	private String insertUNameCode;

	public void setInsertUNameCode(String insertUNameCode) {
		this.insertUNameCode = insertUNameCode;
	}

	/**
	 * 
	 * ��ѯ�����ļ�
	 * 
	 * @return
	 * @throws Exception
	 */
	public String selectUserInfoManagerAction() throws Exception {

		int firstResult = (page - 1) * rows;
		// ������ֹλ��
		int maxResults = page * rows;

		List<Object[]> searchlist = new ArrayList<Object[]>();
		HashMap<String, Object> map = new HashMap<String, Object>();
		String username = "%" + userName + "%";
		String loginid = "%" + loginId + "%";

		map.put("loginId", loginid);

		map.put("isvalid", isValid);
		map.put("rid", roleId);

		map.put("uname", username);

		map.put("ucode", uNameCode);
		searchlist = userService.queryUserInfoList(map, firstResult,
				maxResults, tablebean);

		List<RowBean> rowsList = new ArrayList<RowBean>();

		if (searchlist != null) {
			for (Object[] obj : searchlist) {
				TUser user = (TUser) obj[0];

				TRole role = (TRole) obj[1];
				TUnit unit = (TUnit) obj[2];
				RowBean rowbean = new RowBean();
				String lastTime = "";
				SimpleDateFormat timeFormat = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");

				if (user.getLastLoginTime() != null) {
					lastTime = timeFormat.format(user.getLastLoginTime());
				}
				String loginTime = "";
				if (user.getLoginTime() != null) {
					loginTime = (timeFormat.format(user.getLoginTime()));
				}

				String createDate = DateUtils
						.DateToString(user.getCreateDate());
				String showValid = "";
				if (user.getIsValid() == true) {
					showValid = "��Ч";
				} else {
					showValid = "��Ч";
				}

				rowbean.setCell(new Object[] {
						user.getLoginId(),
						user.getName(),
						user.getDuty(),
						unit.getName(),
						user.getTelephone(),
						role.getName(),
						loginTime,
						lastTime,
						user.getLoginCount(),
						showValid,
						user.getName(),
						createDate,
						"<a href='#'  onclick='insertUserShowDialog(\""
								+ user.getLoginId() + "\")' >�༭</a>"

				});
				// ��ǰ��IDΪ1
				rowbean.setId(user.getLoginId());
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

		int records = tablebean.getRecords();
		// ��ҳ��
		tablebean.setTotal(records % rows == 0 ? records / rows : records
				/ rows + 1);
		return SUCCESS;

	}

	/**
	 * ���н�ɫ��List
	 * 
	 * @return
	 */
	public String roleListAction() {

		list = new ArrayList<Map<String, Object>>();
		List<TRole> listRole = roleService.roleList();

		for (int i = 0; i < listRole.size(); i++) {
			TRole role = listRole.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", role.getId());
			map.put("name", role.getName());
			list.add(map);
		}

		return SUCCESS;
	}

	/**
	 * 
	 * ɾ���û���Ϣ
	 * 
	 * @return
	 */
	public String deleteUserinfoAction() {

		String[] codeList = codes.split(",");

		for (int i = 0; i < codeList.length; i++) {
			String eachCode = codeList[i];
			TUser user = (TUser) userService.EntityQuery(TUser.class, eachCode);
			user.setIsValid(false);
			userService.updateEntity(user);
		}
		returnValue = "success";
		return SUCCESS;
	}

	private String loginCount;

	public void setLoginCount(String loginCount) {
		this.loginCount = loginCount;
	}

	/**
	 * �������Action
	 * 
	 * @return
	 */

	public String insertUserinfoAction() {
		TRole role = (TRole) userService.EntityQuery(TRole.class, insertRoleId);
		TUnit unit = (TUnit) userService.EntityQuery(TUnit.class,
				insertUNameCode);
		tuser.setTRole(role);
		tuser.setTUnit(unit);
		tuser.setCreator(super.getLoginUser().getLoginId());
		tuser.setCreateDate(new Timestamp(System.currentTimeMillis()));
		if (insertisValid.equals("1")) {
			tuser.setIsValid(true);
		} else {
			tuser.setIsValid(false);
		}

		if (updateId == null || (updateId.equals(""))) {
			tuser.setLoginCount((Long.valueOf(0)));
			tuser.setIsLocal("L");
			userService.insertEntity(tuser);
		} else {
			TUser user = (TUser) userService.EntityQuery(TUser.class, updateId);
			tuser.setIsLocal("L");
			tuser.setLastLoginTime(user.getLastLoginTime());
			tuser.setLoginTime(user.getLoginTime());
			tuser.setLoginCount(Long.valueOf(loginCount));
			userService.updateEntity(tuser);
		}

		returnValue = "true";
		return SUCCESS;
	}

	/**
	 * ����ʱ��ѯ��ɫ��Ϣ
	 * 
	 * @return
	 */
	public String updateSelectUserAction() {
		updateList = new ArrayList<Map<String, Object>>();
		TUser user = (TUser) userService.EntityQuery(TUser.class, loginId);
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("username", user.getName());
		map.put("loginId", user.getLoginId());
		map.put("telephone", user.getTelephone());
		map.put("password", user.getPassword());
		map.put("roleId", user.getTRole().getId());
		map.put("ucode", user.getTUnit().getCode());
		map.put("uname", user.getTUnit().getName());
		map.put("duty", user.getDuty());
		map.put("isValid", user.getIsValid());
		map.put("loginCount", user.getLoginCount());
		updateList.add(map);
		return SUCCESS;
	}

	/**
	 * ��ѯ��ǰ�û��Ƿ����
	 * 
	 * @return
	 */
	public String selectUserBooleanAction() {
		TUser user = (TUser) userService.EntityQuery(TUser.class, isLoginId);
		if (user != null) {
			returnValue = "true";
		} else {
			returnValue = "false";
		}
		return SUCCESS;
	}

	private String repassword;

	public void setRepassword(String repassword) {
		this.repassword = repassword;
	}

	
	private String name;
	private String telephone;
	public void setName(String name) {
		this.name = name;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	/**
	 * �޸�����
	 */
	public String updatePasswordUserInfoManagerAction() {

		TUser updateUser = (TUser) userService.EntityQuery(TUser.class, super
				.getLoginUser().getLoginId());
		
		updateUser.setName(name);
		updateUser.setTelephone(telephone);
		updateUser.setPassword(Long.valueOf(repassword));
		userService.updateEntity(updateUser);
		returnValue = "true";
		return SUCCESS;
	}
	
	/**
	 * �ж�����ԭʼ������ȷ���
	 * @return
	 */
	public String checkPassword(){
		returnValue="false";
		if(super.getLoginUser().getPassword().intValue()==this.password.intValue()){
			returnValue="true";
		}
		return SUCCESS;
	}
}
