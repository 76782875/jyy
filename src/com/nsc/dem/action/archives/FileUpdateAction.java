package com.nsc.dem.action.archives;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nsc.base.util.DateUtils;
import com.nsc.dem.action.BaseAction;
import com.nsc.dem.action.bean.RowBean;
import com.nsc.dem.action.bean.TableBean;
import com.nsc.dem.bean.archives.TDoc;
import com.nsc.dem.bean.archives.TDocType;
import com.nsc.dem.bean.project.TPreDesgin;
import com.nsc.dem.bean.project.TProject;
import com.nsc.dem.bean.system.TDictionary;
import com.nsc.dem.service.archives.IarchivesService;
import com.nsc.dem.service.searches.IsearchesService;

public class FileUpdateAction  extends BaseAction {

	private static final long serialVersionUID = 1L;
	
	IsearchesService searchesService;
	IarchivesService archivesService;
	
	private List<TDocType> tDocTypeList;
	private TDoc tDoc;
	private TDocType tDocType;
	private TProject tProject;
	private TPreDesgin tPreDesgin;
	/**
	 * ��ʼ������ҳ��
	 * @return
	 */
	public String initUpdate() {
		
		String tdocId=getRequest().getParameter("tdocID");
		String projectId=getRequest().getParameter("projectId");
		String docTypeId=getRequest().getParameter("docTypeId");
		tDoc=(TDoc) archivesService.EntityQuery(TDoc.class, tdocId);
		tDocType=(TDocType) archivesService.EntityQuery(TDocType.class, docTypeId);
		tProject=(TProject) archivesService.EntityQuery(TProject.class, Long.parseLong(projectId));
		tPreDesgin=(TPreDesgin) archivesService.EntityQuery(TPreDesgin.class, tdocId);
		getRequest().setAttribute("tdocId", tdocId);
		tDocTypeList = archivesService.docTypeList("", "");
		for (int i = 0; i < tDocTypeList.size(); i++) {
			TDocType tDocType = tDocTypeList.get(i);
			List<TDocType> listChild = archivesService.docTypeList(tDocType
					.getCode());
			tDocType.setList(listChild);
		}

		return "initUpdate";
	}
	
	/**
	 * �����ѯ����
	 * @return
	 * @throws Exception
	 */
	public String result() throws Exception{
		@SuppressWarnings("unchecked")
		Map<String,Object> map=super.getRequest().getParameterMap();
		Map<String,Object> mapCondition=new HashMap<String,Object>();
		
		for(String t:map.keySet()){  
			String[] params=(String[]) map.get(t);
			if(!"".equals(params[0])){
				mapCondition.put(t, params[0]);
				//������Ҫ��Ҫ��  ��ѯ����е��ֶζ�Ӧ
				//������ļ���ʽ
				if("format".equals(t)){
					mapCondition.put("format", params);
				}
				if("fileName".equals(t)){ //�ļ���
					mapCondition.put("dname", '%'+params[0]+'%');
				}
				if("projectName".equals(t)){//��������
					mapCondition.put("pname", '%'+params[0]+'%');
				}
				if("unname".equals(t)){ //ҵ����λ
					mapCondition.put("unname", '%'+params[0]+'%');
				}
				if("creator".equals(t)){//������
					mapCondition.put("uname", '%'+params[0]+'%');
				}
				if("from".equals(t)){
					mapCondition.put("begin_create_date", params[0]);
				}
				if("to".equals(t)){
					mapCondition.put("end_create_date", params[0]);
				}
				if("docTypeCode".equals(t)){//�ĵ�����
					mapCondition.put("sub_doc_type", params[0]);
				}
				if("dftSecurity".equals(t)){
					if(!"0".equals(params[0])){
						mapCondition.put("security", params[0]);
					}
				}
				if("fileStatus".equals(t)){
					if(!"0".equals(params[0])){
						mapCondition.put("filestatus", params[0]);
					}
				}
			}
	     }  

		getRequest().getSession().setAttribute("conditionFileUpdate", mapCondition);
		return "search";
	}
	
	/**
	 * ��ѯ�����ҳ��ʾ
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String displayResult() throws Exception {
		
		 this.result();
         Map<String,Object> map=(Map<String,Object>)getRequest().getSession().getAttribute("conditionFileUpdate");
         map.put("uncode", super.getLoginUser().getTUnit().getProxyCode());
         Object obj=map.get("filestatus");
         if(obj==null){
        	 map.put("filestatus", "03");
         }
		// ��ʼλ��Ϊ��(ҳ��-1)*rows
		int firstResult = (page - 1) * rows;
		// ��ֹλ��Ϊ��ҳ��*����-1
		int maxResults = page * rows ;

		List<Object[]> list = new ArrayList<Object[]>();
		
		list = searchesService.queryBasicList(map, firstResult, maxResults,tablebean);

		List rowsList = new ArrayList();

		for (Object[] objs : list) {
			TDoc tdoc = (TDoc) objs[0];
			TProject project = (TProject) objs[1];
			TDocType docType = (TDocType) objs[2];
			String code="";
			if(docType!=null){
				code=docType.getCode();
				code="'"+code+"'";
			}
			TDictionary tDictionary=(TDictionary)objs[6];
			TDictionary tDictionary2=(TDictionary)objs[7];
			RowBean rowbean = new RowBean();
			rowbean
			.setCell(new Object[] {
					project == null ? "" : project.getName(),
					tdoc.getName(),
					docType == null ? "" : docType.getName(),
					tDictionary2 == null ? "" : tDictionary2.getName(),
					tDictionary == null ? "" : tDictionary.getName(),
					tdoc.getFormat(),
					tdoc.getTUser().getName(),
					DateUtils.DateToString(tdoc.getCreateDate()),
					"<a href='#' id='dialog_link' onclick=update("+tdoc.getId()+","+project.getId()+","+code+")>����</a>" }); 
		// ��ǰ��IDΪ1
		rowbean.setId(tdoc.getId());
		rowsList.add(rowbean);
		}
		// ����Ԫ����������
		tablebean.setRows(rowsList);

		// ��ǰҳ��1ҳ
		tablebean.setPage(this.page);
		int records=tablebean.getRecords();
		// ��ҳ��
		tablebean.setTotal(records%rows==0?records/rows:records/rows+1);
		getRequest().getSession().removeAttribute("conditionFileUpdate");
		return "tab";
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

	public void setSearchesService(IsearchesService searchesService) {
		this.searchesService = searchesService;
	}

	public void setArchivesService(IarchivesService archivesService) {
		this.archivesService = archivesService;
	}

	public List<TDocType> gettDocTypeList() {
		return tDocTypeList;
	}

	public void settDocTypeList(List<TDocType> tDocTypeList) {
		this.tDocTypeList = tDocTypeList;
	}

	public TDoc gettDoc() {
		return tDoc;
	}

	public void settDoc(TDoc tDoc) {
		this.tDoc = tDoc;
	}

	public TDocType gettDocType() {
		return tDocType;
	}

	public void settDocType(TDocType tDocType) {
		this.tDocType = tDocType;
	}

	public TProject gettProject() {
		return tProject;
	}

	public void settProject(TProject tProject) {
		this.tProject = tProject;
	}

	public TPreDesgin gettPreDesgin() {
		return tPreDesgin;
	}

	public void settPreDesgin(TPreDesgin tPreDesgin) {
		this.tPreDesgin = tPreDesgin;
	}
	
}
