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
import com.nsc.dem.bean.system.TDictionary;
import com.nsc.dem.bean.system.TNodeDef;
import com.nsc.dem.service.searches.IsearchesService;
import com.nsc.dem.service.system.IdictionaryService;
import com.nsc.dem.service.system.IsortSearchesService;

public class ClassConfigAction extends BaseAction {

	private static final long serialVersionUID = 1L;
	private List<Map<String,Object>> list;
	
	private String typeName;
	private String source;
	private String remark;
	private String params;
	IsearchesService searchesService;
	IsortSearchesService sortSearchesService;
	IdictionaryService dictionaryService;
	
	public void setDictionaryService(IdictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setSearchesService(IsearchesService searchesService) {
		this.searchesService = searchesService;
	}
	
	public void setSortSearchesService(IsortSearchesService sortSearchesService) {
		this.sortSearchesService = sortSearchesService;
	}
    
	/**
	 * ��ӻ��߸��·��ඨ��ڵ�
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String add() throws UnsupportedEncodingException{
		TNodeDef tnd=new TNodeDef();
		tnd.setName(typeName);
		tnd.setSource(source);
		tnd.setRemark(remark);
		tnd.setParams(params);
		tnd.setTUser(getLoginUser());tnd.setCreateDate(new Date());
		tnd.setSqlPlace("02");
		String nodeId=getRequest().getParameter("nodeId");
		if("".equals(nodeId)|| nodeId==null){
			searchesService.insertEntity(tnd);
		}else{
			tnd.setId(Long.parseLong(nodeId));
			searchesService.updateEntity(tnd);
		}
	
		return "list";
	}
	
	/**
	 * �Է��ඨ����� ���²���
	 */
	public String edit(){
		String id=getRequest().getParameter("id");
		TNodeDef tNodeDef=(TNodeDef)sortSearchesService.EntityQuery(TNodeDef.class, Long.parseLong(id));
		typeName=tNodeDef.getName();
		source=tNodeDef.getSource();
		remark=tNodeDef.getRemark();
		params=tNodeDef.getParams();
		getRequest().setAttribute("nodeId", id);
		return "edit";
	}
	
	/**
	 * �Է��ඨ�����ɾ������
	 */
	public String del(){
		String ids=getRequest().getParameter("id");
		String[] sIds=ids.split(",");
		for(int i=0;i<sIds.length;i++){
			TNodeDef tnd=(TNodeDef)sortSearchesService.EntityQuery(TNodeDef.class, Long.parseLong(sIds[i]));
			sortSearchesService.delEntity(tnd);
		}
		return "del";
	}
	
	/**
	 * ��ѯ
	 */
	public String show(){
		
	      return "del"	;
	}
	
	/**
	 * �ж�����������Ƿ����!
	 * @throws UnsupportedEncodingException 
	 * 
	 */
	public String existence() throws UnsupportedEncodingException{
	    String name=getRequest().getParameter("id");
	    list=new ArrayList<Map<String,Object>>();
	    if(!("".equals(name) || name==null)){
	    	TNodeDef tNodeDef=new TNodeDef();
	    	List<Object> tNodeDefList=sortSearchesService.EntityQuery(tNodeDef);
	    	for(Object tnd:tNodeDefList){	
	    		TNodeDef td=(TNodeDef)tnd;
	    		String nodeName=td.getName();
	    		if(nodeName.equals(name)){
	    			Map<String,Object> map= new HashMap<String,Object>();
	    			map.put("message", "�������Ѵ���,����������");
	    			list.add(map);
	    		}
	    	}
	    }
		return "list";
	}
	
	/**
	 * ��ѯ�����ֵ�������parentCodeΪ�յ�����
	 * ��Ϊ�����ֵ�Ĳ�ѯ������ҳ����ʾ
	 * @return
	 */
	public String queryDictionary(){
		List<Object> tList=dictionaryService.dictionaryInfoList();
		list=new ArrayList<Map<String,Object>>();
		for(int i=0;i<tList.size();i++){
			TDictionary tDictionary=(TDictionary)tList.get(i);
			Map<String,Object> map= new HashMap<String,Object>();
			map.put("code", tDictionary.getCode());
			map.put("name", tDictionary.getName());
			map.put("spell", GetCh2Spell.getBeginCharacter(tDictionary.getName()));
			list.add(map);
		}
		return "list";
	}
	
	/**
	 *  ��ѯ����parentCode��XX�������ֵ�����
	 *  �����б���ʾ
	 * @return
	 */
	public String resultDictionary(){
		String code=getRequest().getParameter("id");
		list=new ArrayList<Map<String,Object>>();
		TDictionary td=(TDictionary) dictionaryService.EntityQuery(TDictionary.class, code);
		List<TDictionary> tDictionaryList=dictionaryService.dictionaryList(td.getName());
		for(int i=0;i<tDictionaryList.size();i++){
			TDictionary tDictionary=tDictionaryList.get(i);
			Map<String,Object> map= new HashMap<String,Object>();
			map.put("code", tDictionary.getCode());
			map.put("name", tDictionary.getName());
			map.put("auth", tDictionary.getAuthControl());
			map.put("createDate", DateUtils.DateToString(tDictionary.getCreateDate()));
			map.put("creator", tDictionary.getTUser().getName());
			map.put("parentCode", tDictionary.getParentCode());
			map.put("remark", tDictionary.getRemark());
			list.add(map);
		}
		return "list";
	}
	
	/**
	 * ����Ԥ������
	 * @return
	 * @throws Exception
	 */
	public String preview(){
		String source=getRequest().getParameter("id");
		list=new ArrayList<Map<String,Object>>();
		List<Object[]> ll = null;
		try {
			ll=dictionaryService.dicSourceInfoList(source);
			for(int i=0;i<ll.size();i++){
				Map<String,Object> map= new HashMap<String,Object>();
				String code=ll.get(i)[0]+"";
				String name=ll.get(i)[1]+"";
				map.put("code", code);
				map.put("name", name);
				list.add(map);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "list";
	}
	
	/**
	 * ��ѯ�����ҳ��ʾ
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String display() throws Exception {
        
		// ��ʼλ��Ϊ��(ҳ��-1)*rows
		int firstResult = (page - 1) * rows;
		// ��ֹλ��Ϊ��ҳ��*����-1
		int maxResults = page * rows ;
		typeName=java.net.URLDecoder.decode(getRequest().getParameter("name"),"UTF-8");
		 List<Object[]> list=sortSearchesService.querySortList(new Object[]{"%"+typeName+"%"}, firstResult, maxResults, tablebean);
	//	 List<Object[]> list=sortSearchesService.querySortList(new Object[]{"%"}, firstResult, maxResults, tablebean);
		List rowsList = new ArrayList();
		for (Object[] objs : list) {
			TNodeDef tn=(TNodeDef)objs[0];
			RowBean rowbean = new RowBean();
			rowbean
					.setCell(new Object[] {
							tn.getName(),
							tn.getType(),
							tn.getSource(),
							tn.getTUser().getName(),
							DateUtils.DateToString(tn.getCreateDate()),
							"<a href='#' id='dialog_link' onclick='edit("+tn.getId()+")'>����</a>" }); 
			// ��ǰ��IDΪ1
			rowbean.setId(tn.getId()+"");
			rowsList.add(rowbean);
		}
		// ����Ԫ����������
		tablebean.setRows(rowsList);

		// ��ǰҳ��1ҳ
		tablebean.setPage(this.page);
		int records=tablebean.getRecords();
		// ��ҳ��
		tablebean.setTotal(records%rows==0?records/rows:records/rows+1);
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

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}
}
