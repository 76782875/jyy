package com.nsc.dem.action.searches;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.util.URIUtil;

import com.nsc.base.util.DateUtils;
import com.nsc.base.util.FileUtil;
import com.nsc.dem.action.BaseAction;
import com.nsc.dem.action.bean.RowBean;
import com.nsc.dem.action.bean.TableBean;
import com.nsc.dem.bean.archives.TDoc;
import com.nsc.dem.bean.archives.TDocType;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.bean.project.TProject;
import com.nsc.dem.service.searches.IsearchesService;
import com.nsc.dem.service.system.IprofileService;

public class BasicSearchAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	private String projectTypeCode;
	private String statussCode;
	private String projectNameCode;
	private String designUnitCode;
	private String proTypeCode;
	private String docTypeCode;
	private String recordTypeCode;
	private String fileName;
	private String from;
	private String to;
	private String format;

	IprofileService profileService;

	public void setProfileService(IprofileService profileService) {
		this.profileService = profileService;
	}

	private String menuId;

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	IsearchesService searchesService;

	private String projectType;
	private String recordType;
	private String projectName;

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getProjectType() {
		return projectType;
	}

	public void setProjectType(String projectType) {
		this.projectType = projectType;
	}

	public void setSearchesService(IsearchesService searchesService) {
		this.searchesService = searchesService;
	}

	public String getProjectTypeCode() {
		return projectTypeCode;
	}

	public void setProjectTypeCode(String projectTypeCode) {
		this.projectTypeCode = projectTypeCode;
	}

	public String getStatussCode() {
		return statussCode;
	}

	public void setStatussCode(String statussCode) {
		this.statussCode = statussCode;
	}

	public String getProjectNameCode() {
		return projectNameCode;
	}

	public void setProjectNameCode(String projectNameCode) {
		this.projectNameCode = projectNameCode;
	}

	public String getDesignUnitCode() {
		return designUnitCode;
	}

	public void setDesignUnitCode(String designUnitCode) {
		this.designUnitCode = designUnitCode;
	}

	public String getProTypeCode() {
		return proTypeCode;
	}

	public void setProTypeCode(String proTypeCode) {
		this.proTypeCode = proTypeCode;
	}

	public String getDocTypeCode() {
		return docTypeCode;
	}

	public void setDocTypeCode(String docTypeCode) {
		this.docTypeCode = docTypeCode;
	}

	public String getRecordTypeCode() {
		return recordTypeCode;
	}

	public void setRecordTypeCode(String recordTypeCode) {
		this.recordTypeCode = recordTypeCode;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	/**
	 * �����ѯ����
	 * 
	 * @return
	 * @throws Exception
	 */
	public String result() throws Exception {
		@SuppressWarnings("unchecked")
		Map<String, Object> map = super.getRequest().getParameterMap();
		Map<String, Object> mapCondition = new HashMap<String, Object>();
		mapCondition.put("uncode", super.getLoginUser().getTUnit()
				.getProxyCode());
		for (String t : map.keySet()) {
			String[] params = (String[]) map.get(t);
			if (!"".equals(params[0])) {
				mapCondition.put(t, params[0]);
				// ������Ҫ��Ҫ�� ��ѯ����е��ֶζ�Ӧ
				// ������ļ���ʽ
				if ("format".equals(t)) {
					mapCondition.put("format", params);
				}
				// ������ļ���С��λ
				if ("fileSize".equals(t)) {
					String[] fsj = (String[]) map.get("fileSizeJudge");
					String[] fsu = (String[]) map.get("fileSizeUnits");
					Double fileSize = Double.parseDouble(params[0]);
					if ("K".equals(fsu[0])) {
						fileSize = fileSize * 1024;
					} else if ("M".equals(fsu[0])) {
						fileSize = fileSize * 1024 * 1024;
					}
					if ("<".equals(fsj[0])) {
						mapCondition.put("max_file_size", fileSize);
					} else if (">".equals(fsj[0])) {
						mapCondition.put("min_file_size", fileSize);
					}
				}
				if ("fileName".equals(t)) { // �ļ���
					mapCondition.put("dname", '%' + params[0] + '%');
				}
				if ("statussCode".equals(t)) {// ���̽׶�
					mapCondition.put("status", params[0]);
				}
				if ("projectTypeCode".equals(t)) {// ���̷���
					mapCondition.put("type", params[0]);
				}
				if ("voltageLevelCode".equals(t)) {// ��ѹ�ȼ�
					mapCondition.put("voltage_level", params[0]);
				}
				if ("projectName".equals(t)) {// ��������
					mapCondition.put("pname", '%' + params[0] + '%');
				}
				if ("unname".equals(t)) { // ҵ����λ
					mapCondition.put("unname", '%' + params[0] + '%');
				}
				if ("creator".equals(t)) {// ������
					mapCondition.put("uname", '%' + params[0] + '%');
				}
				if ("proTypeCode".equals(t)) {// רҵ
					mapCondition.put("speciality", params[0]);
				}
				if ("from".equals(t)) {
					mapCondition.put("begin_create_date", params[0]);
				}
				if ("to".equals(t)) {
					mapCondition.put("end_create_date", params[0]);
				}
				if ("docTypeCode".equals(t)) {// �ĵ�����
					mapCondition.put("doc_type", params[0]);
				}
				if ("recordTypeCode".equals(t)) {// ��������
					mapCondition.put("child_doc_type", params[0]);
				}
			}
		}
		getRequest().getSession().setAttribute("condition", mapCondition);
		return "search";
	}

	/**
	 * ��ѯ��� ��ҳ��ʾ
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String inFormation() throws Exception {
		String flag = getRequest().getParameter("flag");

		if (!("jump".equals(flag))) {
			this.result();
		}
		Map<String, Object> map = (Map<String, Object>) getRequest()
				.getSession().getAttribute("condition");
		map.put("uncode", super.getLoginUser().getTUnit().getProxyCode());

		// ��ʼλ��Ϊ��(ҳ��-1)*rows
		int firstResult = (page - 1) * rows;
		// ��ֹλ��Ϊ��ҳ��*����-1
		int maxResults = page * rows;

		List<Object[]> list = new ArrayList<Object[]>();
		map.put("filestatus", "01");
		list = searchesService.queryBasicList(map, firstResult, maxResults,
				tablebean);
		TUser user = super.getLoginUser();
		menuId = getRequest().getParameter("menuId");

		boolean isPreView = profileService.getProfileByauthControl(user,
				menuId, "Ԥ��");
		boolean isOnLiSee = profileService.getProfileByauthControl(user,
				menuId, "���߲鿴Ԥ��");

		List rowsList = new ArrayList();

		for (Object[] objs : list) {
			TDoc tdoc = (TDoc) objs[0];
			TProject project = (TProject) objs[1];
			TDocType docType = (TDocType) objs[2];
			FileUtil fileUtil = new FileUtil();
			String docFileSize = fileUtil.getHumanSize(tdoc.getFileSize()
					.longValue());// ��С
			RowBean rowbean = new RowBean();
			String preView = "";
			if (isPreView == true) {
				if (tdoc.getPreviewPath() != null) {
					preView = "<a href='#'  onclick='previewImage("
							+ tdoc.getId() + ")'>[Ԥ��]</a>";
				}
			} else {
				preView = "";
			}
			String onLineSee = "";
			//��ַ���ô���
			if (isOnLiSee == true) {
				String str = "\""+tdoc.getId()+"\",\""+ project.getId()+"\",\""+project.getTUnitByOwnerUnitId().getCode()
			       +"\",\""+tdoc.getName()+"\",\""+URIUtil.encodePath(tdoc.getPath(),"UTF-8")+"\",\""+tdoc.getSuffix()+"\"";
				onLineSee = "<a href='#' onclick='onlineDown(" + str
						+ ")' style='cursor:hand'>" + tdoc.getName() + "</a>";
				if (tdoc.getPreviewPath() != null) {
					preView = "<a href='#'  onclick='previewImage("
							+ tdoc.getId() + ")'>[Ԥ��]</a>";
				}
			} else {
				onLineSee = tdoc.getName();
			}
			rowbean
					.setCell(new Object[] {
							onLineSee,
							docType == null ? "" : docType.getName(),
							project == null ? "" : project.getName(),
							tdoc.getFormat(),
							DateUtils.DateToString(tdoc.getCreateDate()),
							docFileSize,
							preView
									+ "<a href='#'  onclick='showDocDetails(\""
									+ tdoc.getId()
									+ "\")' >[��ϸ]</a><input type = 'hidden' name = 'checkDownloadVals' value = '"
									+ tdoc.getId() + "#" + tdoc.getProjectid()
									+ "#" + project.getTUnitByOwnerUnitId()
									+ "#" + tdoc.getName() + "#" + "'/>" });
			// ��ǰ��IDΪ1
			rowbean.setId(tdoc.getId() + "<>" + project.getId() + "<>"
					+ project.getTUnitByOwnerUnitId().getCode() + "<>" + tdoc.getName()
					+ "<>" + tdoc.getPath() + "<>" + tdoc.getSuffix());
			
			rowsList.add(rowbean);
		}
		// ����Ԫ����������
		tablebean.setRows(rowsList);

		// ��ǰҳ��1ҳ
		tablebean.setPage(this.page);

		int records = tablebean.getRecords();
		// ��ҳ��
		tablebean.setTotal(records % rows == 0 ? records / rows : records
				/ rows + 1);

		return SUCCESS;
	}

	public String getMenuId() {
		return menuId;
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
}
