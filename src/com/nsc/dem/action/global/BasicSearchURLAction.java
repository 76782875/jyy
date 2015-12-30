package com.nsc.dem.action.global;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nsc.base.util.DateUtils;
import com.nsc.base.util.FileUtil;
import com.nsc.dem.action.BaseAction;
import com.nsc.dem.action.bean.RowBean;
import com.nsc.dem.action.bean.TableBean;
import com.nsc.dem.bean.archives.TDoc;
import com.nsc.dem.bean.archives.TDocType;
import com.nsc.dem.bean.project.TProject;
import com.nsc.dem.service.searches.IsearchesService;

public class BasicSearchURLAction extends BaseAction{

	private static final long serialVersionUID = 1L;
	IsearchesService searchesService;

    public void setSearchesService(IsearchesService searchesService) {
		this.searchesService = searchesService;
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
		mapCondition.put("uncode", super.getLoginUser().getTUnit().getProxyCode());
		for(String t:map.keySet()){  
			String[] params=(String[]) map.get(t);
			if(!"".equals(params[0])){
				mapCondition.put(t, params[0]);
				//������Ҫ��Ҫ��  ��ѯ����е��ֶζ�Ӧ
				//������ļ���ʽ
				if("format".equals(t)){
					mapCondition.put("format", params);
				}else
				//������ļ���С��λ
				if("fileSize".equals(t)){
					String[] fsj=(String[]) map.get("fileSizeJudge");
					String[] fsu=(String[]) map.get("fileSizeUnits");
					Double fileSize=Double.parseDouble(params[0]);
					if("K".equals(fsu[0])){
						fileSize=fileSize*1024;
					}else if("M".equals(fsu[0])){
						fileSize=fileSize*1024*1024;
					}
					if("<".equals(fsj[0])){
						mapCondition.put("max_file_size", fileSize);
					}else if(">".equals(fsj[0])){
						mapCondition.put("min_file_size", fileSize);
					}
				}else
				if("fileName".equals(t)){ //�ļ���
					mapCondition.put("dname", '%'+params[0]+'%');
				}else
				if("statussCode".equals(t)){//���̽׶�
					mapCondition.put("status", params[0]);
				}else
				if("projectTypeCode".equals(t)){//���̷���
					mapCondition.put("type", params[0]);
				}else
				if("voltageLevelCode".equals(t)){//��ѹ�ȼ�
					mapCondition.put("voltage_level", params[0]);
				}else
				if("projectName".equals(t)){//��������
					mapCondition.put("pname", '%'+params[0]+'%');
				}else
				if("unname".equals(t)){ //ҵ����λ
					mapCondition.put("unname", '%'+params[0]+'%');
				}else
				if("creator".equals(t)){//������
					mapCondition.put("uname", '%'+params[0]+'%');
				}else
				if("proTypeCode".equals(t)){//רҵ
					mapCondition.put("speciality", params[0]);
				}else
				if("from".equals(t)){
					mapCondition.put("begin_create_date", params[0]);
				}else
				if("to".equals(t)){
					mapCondition.put("end_create_date", params[0]);
				}else
				if("docTypeCode".equals(t)){//�ĵ�����
					mapCondition.put("doc_type", params[0]);
				}else
				if("recordTypeCode".equals(t)){//��������
					mapCondition.put("child_doc_type", params[0]);
				}else{
					mapCondition.put(t, params[0]);
				}
			}
	     }  

		getRequest().getSession().setAttribute("condition", mapCondition);
		return "search";
	}
	
	/**
	 * ��ѯ��� ��ҳ��ʾ
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String inFormation() throws Exception {
         Map<String,Object> map=(Map<String,Object>)getRequest().getSession().getAttribute("condition");
         map.put("uncode", super.getLoginUser().getTUnit().getProxyCode());

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
			
			FileUtil fileUtil = new FileUtil();
			String docFileSize = fileUtil.getHumanSize(tdoc.getFileSize()
					.longValue());// ��С
			RowBean rowbean = new RowBean();
			String preView = tdoc.getPreviewPath() == null ? ""
					: ("<a href='#' id='dialog_link' onclick='previewImage("
							+ tdoc.getId() + ")'>[Ԥ��]</a>");
			rowbean
			.setCell(new Object[] {
					"<a href='#' onclick='onlineDown(" + tdoc.getId()
					+ ")'>"+tdoc.getName()+"</a>",
					docType == null ? "" : docType.getName(),
					project == null ? "" : project.getName(),
					tdoc.getFormat(),
					DateUtils.DateToString(tdoc.getCreateDate()),
					docFileSize,
					preView
					+ "<a href='#'  onclick='showDocDetails(\""
					+ tdoc.getId()
					+ "\")' id ='dialog_details'>[��ϸ]</a>"
			});
		// ��ǰ��IDΪ1
		rowbean.setId(tdoc.getId());
		rowsList.add(rowbean);
		}
		// ����Ԫ����������
		tablebean.setRows(rowsList);

		// ��ǰҳ��1ҳ
		tablebean.setPage(this.page);

		// ��ѯ���ļ�¼��Ϊ100
		//tablebean.setRecords(records.intValue());
		
		int records=tablebean.getRecords();
		// ��ҳ��
		tablebean.setTotal(records%rows==0?records/rows:records/rows+1);

		return SUCCESS;
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
