package com.nsc.dem.action.searches;

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
import com.nsc.dem.bean.archives.TDoc;
import com.nsc.dem.bean.archives.TDocType;
import com.nsc.dem.bean.archives.TUserQuery;
import com.nsc.dem.bean.archives.TUserQueryId;
import com.nsc.dem.bean.project.TProject;
import com.nsc.dem.service.searches.IsearchesService;
import com.nsc.dem.service.system.IdictionaryService;
import com.nsc.dem.service.system.IprofileService;

public class AdvancedSearchAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	private String menuId;
	public String getMenuId() {
		return menuId;
	}
	IprofileService profileService; 
	public void setProfileService(IprofileService profileService) {
		this.profileService = profileService;
	}

	public void setMenuId(String menuId) {
		this.menuId = menuId;
	}

	IdictionaryService dictionaryService;
	IsearchesService searchesService;
	private List<Map<String,Object>> list;
	private String format;
	
	public void setDictionaryService(IdictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}
	
	public void setSearchesService(IsearchesService searchesService) {
		this.searchesService = searchesService;
	}


	/**
	 * ��ʼ����ѯ����
	 * @return
	 */
	public String queryName(){
		String loginId=super.getLoginUser().getLoginId();
		list=new ArrayList<Map<String,Object>>();
		@SuppressWarnings("unchecked")
		List tUserQueryList=searchesService.queryByLoginId(loginId);
	
		for(int i=0;i<tUserQueryList.size();i++){
			//�õ���ѯ��������,����Ҫȥ���ظ���,���Բ�ѯʱ ֻ��ѯ��queryName,loginId
			//�����ֶ�,��������ȡ����ʱ �е� ��һ��
			String name=((Object[])tUserQueryList.get(i))[0].toString();
			Map<String,Object> map= new HashMap<String,Object>();
			map.put("id", name);
			map.put("name", name);
			map.put("spell", GetCh2Spell.getBeginCharacter(name));
			list.add(map);
		}
		return "list";
	}
	
	/**
	 * ��ѯ��������
	 * @return
	 */
	public String query(){
		@SuppressWarnings("unchecked")
		Map<String,Object> map=getRequest().getParameterMap();
		
		Map<String,Object> mapCondition=new HashMap<String,Object>();
		
		for(String t:map.keySet()){  
			String[] params=(String[]) map.get(t);
			if(!"".equals(params[0])){
				getRequest().setAttribute(t, params[0]);
				mapCondition.put(t, params[0]);
				//������Ҫ��Ҫ��  ��ѯ����е��ֶζ�Ӧ
				//������ļ���ʽ
				if("format".equals(t)){
					mapCondition.put("format", params);
					String fm="";
					for(int i=0;i<params.length;i++){
						fm=fm+params[i]+",";
					}
					getRequest().setAttribute("format", fm);
				}
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
				}
				if("fileName".equals(t)){ //�ļ���
					mapCondition.put("dname", '%'+params[0]+'%');
				}
				if("statussCode".equals(t)){//���̽׶�
					mapCondition.put("status", params[0]);
				}
				if("projectTypeCode".equals(t)){//���̷���
					mapCondition.put("type", params[0]);
				}
				if("voltageLevelCode".equals(t)){//��ѹ�ȼ�
					mapCondition.put("voltage_level", params[0]);
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
				if("proTypeCode".equals(t)){//רҵ
					mapCondition.put("speciality", params[0]);
				}
				if("from".equals(t)){
					mapCondition.put("begin_create_date", params[0]);
				}
				if("to".equals(t)){
					mapCondition.put("end_create_date", params[0]);
				}
				if("docTypeCode".equals(t)){//�ĵ�����
					mapCondition.put("doc_type", params[0]);
				}
				if("recordTypeCode".equals(t)){//��������
					mapCondition.put("child_doc_type", params[0]);
				}
			}
	     }  
		getRequest().getSession().setAttribute("condition", mapCondition);
		return "result";
	}
	
	/**
	 * �����ѯ����
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public String saveQuery() throws UnsupportedEncodingException{
		//��ѯ��������
		String queryName=java.net.URLDecoder.decode(getRequest().getParameter("queryName"),"UTF-8");
		String loginId=super.getLoginUser().getLoginId();
		@SuppressWarnings("unchecked")
		Map<String,Object> map=getRequest().getParameterMap();
		List<TUserQuery> tuqList=searchesService.queryByLoginIdAndName(loginId, queryName);
		if(tuqList==null || tuqList.size()==0){
			for(String t:map.keySet()){  
				String value=((String[])map.get(t))[0];
				if(value.indexOf("%")>=0){
					value=java.net.URLDecoder.decode(value,"UTF-8");
				}
				//����ļ���ʽ  ���ж�ѡ�����ļ���
				if("format".equals(t)){
					String[] ff=(String[]) map.get("format");
					value="";
					if(ff.length>0){
						for(int i=0;i<ff.length ;i++){
							value=value+ff[i]+",";
						}
					}
				}
				
				if(!"".equals(value)){
					TUserQuery tuq=new TUserQuery();
					tuq.setCreateDate(new Date());
					tuq.setQueryParams(value);
					TUserQueryId tuqId=new TUserQueryId();
					tuqId.setLoginId(loginId);
					tuqId.setQueryKey(t.toString());
					tuqId.setQueryName(queryName);
					tuq.setId(tuqId);
					searchesService.insertEntity(tuq);
				}
		     }  
		}else{
			for(TUserQuery tuq:tuqList){
				searchesService.delEntity(tuq);
			}
			for(Object t:map.keySet()){  
				String value=((String[])map.get(t))[0];
				//����ļ���ʽ  ���ж�ѡ�����ļ���
				if("format".equals(t)){
					String[] ff=(String[]) map.get("format");
					value="";
					if(ff.length>0){
						for(int i=0;i<ff.length ;i++){
							value=value+ff[i]+",";
						}
					}
				}
				if(!"".equals(value)){
					TUserQuery tuq=new TUserQuery();
					tuq.setCreateDate(new Date());
					tuq.setQueryParams(value);
					TUserQueryId tuqId=new TUserQueryId();
					tuqId.setLoginId(loginId);
					tuqId.setQueryKey(t.toString());
					tuqId.setQueryName(queryName);
					tuq.setId(tuqId);
					searchesService.insertEntity(tuq);
				}
		     } 
		}
	     return "list";
	}
	
	/**
	 * ɾ����ѯ����
	 */
	public String deleteQuery(){
		String queryName=getRequest().getParameter("queryName");
		List<TUserQuery> tuqList=(List<TUserQuery>)searchesService.queryByLoginIdAndName(super.getLoginUser().getLoginId(), queryName);
		for(TUserQuery tuq:tuqList){
			searchesService.delEntity(tuq);
		}
		return "list";
	}
	
	/**
	 * ��ѯ��������
	 */
	public String loading(){
		String queryName=getRequest().getParameter("queryName");
		List<TUserQuery> tuqList=(List<TUserQuery>)searchesService.queryByLoginIdAndName(super.getLoginUser().getLoginId(), queryName);
		list=new ArrayList<Map<String,Object>>();
		for(int i=0;i<tuqList.size();i++){
			TUserQuery tUserQuery=(TUserQuery)tuqList.get(i);
			Map<String,Object> map= new HashMap<String,Object>();
			map.put("queryKey", tUserQuery.getId().getQueryKey());
			map.put("queryParams", tUserQuery.getQueryParams());
			list.add(map);
		}
		return "list";
	}
	
	/**
	 * ��ѯ���չʾ
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String show() throws Exception{

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
			RowBean rowbean = new RowBean();
			rowbean
					.setCell(new Object[] {
							tdoc.getName(),
							docType == null ? "" : docType.getName(),
							project == null ? "" : project.getName(),
							tdoc.getFormat(),
							DateUtils.DateToString(tdoc.getCreateDate()),
							tdoc.getFileSize(),
							"�鿴|<a href='#' id='dialog_link' onclick='previewImage("+tdoc.getId()+")'>Ԥ��</a>" }); 
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

		// ��ѯ���ļ�¼��Ϊ100
		//tablebean.setRecords(records.intValue());
		
		int records=tablebean.getRecords();
		// ��ҳ��
		tablebean.setTotal(records%rows==0?records/rows:records/rows+1);

		return "tab";
	}
	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
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

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

}
