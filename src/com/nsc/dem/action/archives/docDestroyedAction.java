package com.nsc.dem.action.archives;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import com.nsc.base.util.DateUtils;
import com.nsc.dem.action.BaseAction;
import com.nsc.dem.action.bean.RowBean;
import com.nsc.dem.action.bean.TableBean;
import com.nsc.dem.bean.archives.TDoc;
import com.nsc.dem.bean.archives.TDocType;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.bean.project.TProject;
import com.nsc.dem.bean.system.TDictionary;
import com.nsc.dem.service.archives.IarchivesService;
import com.nsc.dem.service.searches.IsearchesService;
import com.nsc.dem.util.index.IndexStoreUitls;

/**
 * 
 * �ĵ�����action
 * 
 * @author ibm
 * 
 */
public class docDestroyedAction extends BaseAction {
	
	private static final long serialVersionUID = -2250891955554921838L;
	// �����Ĳ���(���̱��롢���ܡ����̷��ࡢ���̽׶Ρ��������)
	private String projectNameCode;
	private String baomi;
	private String proClass;
	private String proStatus;
	private String fileStatus;

	public void setFileStatus(String fileStatus) {
		this.fileStatus = fileStatus;
	}
	

	// ҳ�������
	private int page;
	private int rows;

	public void setPage(int page) {
		this.page = page;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public void setProjectNameCode(String projectNameCode) {
		this.projectNameCode = projectNameCode;
	}

	public void setBaomi(String baomi) {
		this.baomi = baomi;
	}

	public void setProClass(String proClass) {
		this.proClass = proClass;
	}

	public void setProStatus(String proStatus) {
		this.proStatus = proStatus;
	}

	public void setKaigongyear(String kaigongyear) {
		this.kaigongyear = kaigongyear;
	}

	private String kaigongyear;

	// ��ѯ�ķ���
	IsearchesService searchesService;

	public void setSearchesService(IsearchesService searchesService) {
		this.searchesService = searchesService;
	}

	IarchivesService archivesService;

	public void setArchivesService(IarchivesService archivesService) {
		this.archivesService = archivesService;
	}

	TableBean tablebean = new TableBean();

	public TableBean getTablebean() {
		return tablebean;
	}

	public void setTablebean(TableBean tablebean) {
		this.tablebean = tablebean;
	}

	// ����ɾ��ʱ������codes
	private String codes;

	public String getCodes() {
		return codes;
	}

	public void setCodes(String codes) {
		this.codes = codes;
	}

	String returnValue;

	public String getReturnValue() {
		return returnValue;
	}

	/**
	 * 
	 * ��ѯ�����ļ�
	 * 
	 * @return
	 * @throws Exception
	 */
	public String selectDocDestoryedAction() throws Exception {

		// ��������λ��
		int firstResult = (page - 1) * rows;
		// ������ֹλ��
		int maxResult = page * rows;

		List<Object[]> list = new ArrayList<Object[]>();

		HashMap<String, Object> map = new HashMap<String, Object>();
		// δ�鵵
		map.put("filestatus", fileStatus);
		map.put("project", projectNameCode);// ����code
		// �ܼ�����
		if (baomi == null || baomi.equals("")) {
			map.put("security", null);
		} else {
			map.put("security", baomi);
		}
		// ���̷���
		if (proClass == null || proClass.equals("")) {
			map.put("type", null);
		} else {
			map.put("type", proClass);
		}
		// ���̽׶�
		if (proStatus == null || proStatus.equals("")) {
			map.put("status", null);
		} else {
			map.put("status", proStatus);
		}

		map.put("uncode", super.getLoginUser().getTUnit().getProxyCode());
		map.put("open_year", kaigongyear);
		list = searchesService.queryBasicList(map, firstResult, maxResult,
				tablebean);
		List<RowBean> rowsList = new ArrayList<RowBean>();
		if (list != null) {
			for (Object[] obj : list) {
				TDoc tdoc = (TDoc) obj[0];
				TProject project = (TProject) obj[1];
				TDocType docType = (TDocType) obj[2];
				TUser tuser = (TUser) obj[3];

				TDictionary tbaomi = (TDictionary) obj[6];
				TDictionary docStatus = (TDictionary) obj[7];
				RowBean rowbean = new RowBean();

				// ����״̬
				String status = docStatus.getName();
				// �ܼ�
				String baomi = tbaomi.getName();

				// ȡʱ��
				String datetime = DateUtils.DateToString(tdoc.getCreateDate());
				rowbean.setCell(new Object[] {

						project.getName(),
						tdoc.getName(),
						docType.getName(),
						status,
						baomi,
						tdoc.getFormat(),
						tuser.getName(),
						datetime,
						"<a href='#'  onclick='showDocDetails(\""
								+ tdoc.getId() + "\")' >[��ϸ]</a>"

				});
				// ��ǰ��IDΪ1
				rowbean.setId(tdoc.getId());
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
	 * ���鵵״̬������Ϊ����״̬
	 * 
	 * @return
	 * @throws Exception
	 */
	public String updateDocDestroyAction() throws Exception {
		String[] codeList = codes.split(",");
		
		for (int i = 0; i < codeList.length; i++) {
			String eachCode = codeList[i];
			TDoc doc = (TDoc) archivesService.EntityQuery(TDoc.class, eachCode);
			doc.setMetaFlag(BigDecimal.ZERO);
			doc.setStatus("02");
			archivesService.updateEntity(doc);
			//�����ļ�ID����
			String storeLocation = IndexStoreUitls.getStoreLocation(doc.getId(),this.getSession().getServletContext());
			archivesService.updateIndex("local", doc.getId(),storeLocation);
		}
		returnValue = "success";
		return SUCCESS;
	}

	/**
	 * ������״̬������Ϊ�鵵״̬
	 * 
	 * @return
	 * @throws Exception
	 */
	public String comeBackDocDestoryAction() throws Exception {
		String[] codeList = codes.split(",");
		for (int i = 0; i < codeList.length; i++) {
			String eachCode = codeList[i];
			TDoc doc = (TDoc) archivesService.EntityQuery(TDoc.class, eachCode);
			doc.setMetaFlag(BigDecimal.ONE);
			doc.setStatus("01");
			this.archivesService.updateEntity(doc);
			String storeLocation = IndexStoreUitls.getStoreLocation(doc.getId(), this.getSession().getServletContext());
			archivesService.updateIndex("local", doc.getId(), storeLocation);
		}
		returnValue = "success";
		return SUCCESS;
	}

	/**
	 * ���ĵ����
	 * 
	 * @return
	 * @throws Exception
	 */
	public String clearDocDestoryAction() throws Exception {
		String[] codeList = codes.split(",");
		for (int i = 0; i < codeList.length; i++) {
//			TDoc doc = (TDoc) archivesService.EntityQuery(TDoc.class,
//					codeList[i]);
			// ɾ���Ѿ�����������
			String storeLocation = IndexStoreUitls.getStoreLocation(codeList[i],this.getSession().getServletContext());
			this.archivesService.deleteArchiveIndex(storeLocation, "local", codeList[i]);
		}

		archivesService.delArchives(codeList);

		returnValue = "success";
		return SUCCESS;
	}
}