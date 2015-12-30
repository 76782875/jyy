package com.nsc.dem.action.archives;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.struts2.ServletActionContext;

import com.nsc.dem.action.BaseAction;
import com.nsc.dem.action.bean.DocTypeBean;
import com.nsc.dem.action.bean.DocTypeDetailsBean;
import com.nsc.dem.action.bean.RowBean;
import com.nsc.dem.action.bean.TableBean;
import com.nsc.dem.bean.archives.TDocType;
import com.nsc.dem.bean.profile.TRole;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.bean.system.TDictionary;
import com.nsc.dem.service.archives.IarchivesService;
import com.nsc.dem.service.system.IroleService;

/**
 * 
 * �����������Action
 * 
 * @author ibm
 * 
 */
public class DocTypeAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7226078912003050238L;
	// ���ղ�ѯ�����Ĳ���
	String docCode;
	String docCreateFormDate;
	String docCreateToDate;
	String docName;
	String docCreator;
	String docClass;
	String docZhuanye;
	String docBaomi;
	String docComFlag;
	private String updateCode;
	private String seleceCode;

	public void setSeleceCode(String seleceCode) {
		this.seleceCode = seleceCode;
	}

	public void setUpdateCode(String updateCode) {
		this.updateCode = updateCode;
	}

	public void setDocCode(String docCode) {
		this.docCode = docCode;
	}

	public void setDocCreateFormDate(String docCreateFormDate) {
		this.docCreateFormDate = docCreateFormDate;
	}

	public void setDocCreateToDate(String docCreateToDate) {
		this.docCreateToDate = docCreateToDate;
	}

	public void setDocName(String docName) {
		this.docName = docName;
	}

	public void setDocCreator(String docCreator) {
		this.docCreator = docCreator;
	}

	public void setDocClass(String docClass) {
		this.docClass = docClass;
	}

	public void setDocZhuanye(String docZhuanye) {
		this.docZhuanye = docZhuanye;
	}

	public void setDocBaomi(String docBaomi) {
		this.docBaomi = docBaomi;
	}

	public void setDocComFlag(String docComFlag) {
		this.docComFlag = docComFlag;
	}

	String returnValue;

	public String getReturnValue() {
		return returnValue;
	}

	private List<Map<String, Object>> rolePriority;

	public List<Map<String, Object>> getRolePriority() {
		return rolePriority;
	}

	public void setRolePriority(List<Map<String, Object>> rolePriority) {
		this.rolePriority = rolePriority;
	}

	private List<TRole> tRoleList;

	public void settRoleList(List<TRole> tRoleList) {
		this.tRoleList = tRoleList;
	}

	DocTypeDetailsBean doctypeDetails;

	public DocTypeDetailsBean getDoctypeDetails() {
		return doctypeDetails;
	}

	public void setDoctypeDetails(DocTypeDetailsBean doctypeDetails) {
		this.doctypeDetails = doctypeDetails;
	}

	private String codes;

	public String getCodes() {
		return codes;
	}

	public void setCodes(String codes) {
		this.codes = codes;
	}

	// ȡ�ĵ���Ϣ
	TDocType tdocType = new TDocType();

	public TDocType getTdocType() {
		return tdocType;
	}

	public void setTdocType(TDocType tdocType) {
		this.tdocType = tdocType;
	}

	DocTypeBean docTypeBean;

	public void setTdocTypeBean(DocTypeBean docTypeBean) {
		this.docTypeBean = docTypeBean;
	}

	public DocTypeBean getTdocTypeBean() {
		return docTypeBean;
	}

	String id;

	public void setId(String id) {
		this.id = id;
	}

	IarchivesService archivesService;

	public void setArchivesService(IarchivesService archivesService) {
		this.archivesService = archivesService;
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

	TableBean tablebean = new TableBean();

	public TableBean getTablebean() {
		return tablebean;
	}

	IroleService roleService;

	public void setRoleService(IroleService roleService) {
		this.roleService = roleService;
	}

	private List<Map<String, Object>> updateList;

	public List<Map<String, Object>> getUpdateList() {
		return updateList;
	}

	public void setUpdateList(List<Map<String, Object>> updateList) {
		this.updateList = updateList;
	}

	/**
	 * 
	 * ��ѯ�ĵ�����
	 * 
	 * @return
	 * @throws Exception
	 */
	public String getFileGroupAction() throws Exception {
		ServletActionContext.getRequest().setCharacterEncoding("utf-8");
		// ��ʼλ��Ϊ��(ҳ��-1)*rows
		int firstResult = (page - 1) * rows;
		// ��ֹλ��Ϊ��ҳ��*����-1
		int maxResults = page * rows;

		List<Object[]> list = new ArrayList<Object[]>();

		String name = "%" + docCreator + "%";
		String docname = "%" + docName + "%";
		Object[] object = new Object[] { docClass, name, docCreateFormDate,
				docCreateToDate, docCode, docname, docComFlag, docZhuanye,
				docBaomi };

		list = archivesService.queryTdocTypeList(object, firstResult,
				maxResults, tablebean);

		List<RowBean> rowsList = new ArrayList<RowBean>();
		if (list != null) {

			for (Object[] obj : list) {

				TDocType docType = (TDocType) obj[0];
				TUser user = (TUser) obj[1];
				TDictionary tbaomi = (TDictionary) obj[3];
				TDictionary tzhuanye = (TDictionary) obj[4];
				String temptime = docType.getCreateDate().toString();
				String[] create = temptime.split(" ");
				RowBean rowbean = new RowBean();
				String baominame = "";
				if (docType.getDftSecurity() != null) {
					// ����

					baominame = tbaomi.getName();
				}
				String zhuanyename = "";
				// רҵ
				if (docType.getSpeciality() != null) {

					zhuanyename = tzhuanye.getName();
				}
				String authcontrol = roleService
						.getfieldSelectAuthControl(docType.getPrivilege());
				rowbean.setCell(new Object[] {

						docType.getCode(),
						docType.getName(),
						baominame,

						authcontrol,
						user.getName(),
						zhuanyename,
						create[0],
						"<a href='#'  onclick='updateFile(\""
								+ docType.getCode() + "\")' >����</a>"

				});

				// ��ǰ��IDΪ1
				rowbean.setId(docType.getCode());
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
	 * ɾ���ĵ���Ϣ
	 * 
	 * @return
	 */
	public String getFileGroupingDelete() {

		String[] codeList = codes.split(",");
		try {
			returnValue = archivesService.removeDocType(codeList);
		} catch (Exception e) {
			returnValue="false";
			logger.getLogger(DocTypeAction.class).warn(e.getMessage());
		}
		return SUCCESS;

	}

	/**
	 * 
	 * ����Ȩ��
	 * 
	 * @return
	 */
	public String getRoleProiorityAction() {
		rolePriority = new ArrayList<Map<String, Object>>();

		TDocType doc = (TDocType) archivesService.EntityQuery(TDocType.class,
				id);
		tRoleList = roleService.queryTRoleList(null);
		if (doc != null) {
			rolePriority = roleService.getRolePriority(doc, doc.getPrivilege(),
					tRoleList);
		} else {
			rolePriority = roleService.getRolePriority(doc, null, tRoleList);
		}

		return SUCCESS;
	}

	/**
	 * ����������ĵ�
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String getInsertFileGroupingAction()
			throws UnsupportedEncodingException {
		ServletActionContext.getRequest().setCharacterEncoding("utf-8");
		// TUser tuser = new TUser();
		tdocType.setTUser(super.getLoginUser());
		// ����ʱ��
		tdocType.setCreateDate(new Date());

		if (updateCode.equals("") || updateCode == null) {
			archivesService.insertEntity(tdocType);

		} else {
			archivesService.updateEntity(tdocType);
		}
		returnValue = "true";
		return SUCCESS;
	}

	/**
	 * ��ѯ��ǰcode�Ƿ����
	 * 
	 * @return
	 */
	public String selectDocTypeCodeAction() {
		TDocType doc = (TDocType) archivesService.EntityQuery(TDocType.class,
				seleceCode);
		if (doc != null) {
			returnValue = "true";
		} else {
			returnValue = "false";
		}
		return SUCCESS;
	}

	/**
	 * ���ݵ�ǰcode����ѯ�÷������ϸ��Ϣ
	 * @return
	 */
	public String getDocTypeByCodeAction() {
		updateList = new ArrayList<Map<String, Object>>();
		TDocType doc = (TDocType) archivesService.EntityQuery(TDocType.class,
				id);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pcode", doc.getParentCode());
		map.put("code", doc.getCode());
		map.put("name", doc.getName());
		map.put("zhuanye", doc.getSpeciality());
		map.put("baomi", doc.getDftSecurity());
		map.put("comFloat", doc.getComFlag());
		map.put("remark", doc.getRemark());
		updateList.add(map);
		return SUCCESS;
	}

	public DocTypeBean getDocTypeBean() {
		return docTypeBean;
	}

	public void setDocTypeBean(DocTypeBean docTypeBean) {
		this.docTypeBean = docTypeBean;
	}
}
