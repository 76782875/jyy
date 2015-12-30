package com.nsc.dem.service.searches.impl;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.xwork.StringUtils;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.store.LockObtainFailedException;

import com.nsc.base.conf.Configurater;
import com.nsc.base.index.AnalyzerFactory;
import com.nsc.base.index.DOCFIELDEnum;
import com.nsc.base.index.FIELDEnum;
import com.nsc.base.index.IIndexSearcher;
import com.nsc.base.index.SearchFactory;
import com.nsc.dem.action.bean.DocNode;
import com.nsc.dem.action.bean.TableBean;
import com.nsc.dem.action.bean.WholeSearchDoc;
import com.nsc.dem.action.system.TMenu;
import com.nsc.dem.bean.archives.TDoc;
import com.nsc.dem.bean.archives.TProjectDocCount;
import com.nsc.dem.bean.archives.TSynDoc;
import com.nsc.dem.bean.archives.TUserQuery;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.bean.project.TDocProject;
import com.nsc.dem.bean.project.TProject;
import com.nsc.dem.bean.system.TDictionary;
import com.nsc.dem.bean.system.TNodeDef;
import com.nsc.dem.bean.system.TOperateLog;
import com.nsc.dem.bean.system.TTreeDef;
import com.nsc.dem.bean.system.TUnit;
import com.nsc.dem.service.base.BaseService;
import com.nsc.dem.service.searches.IsearchesService;


@SuppressWarnings("unchecked")
public class SearchesServiceImpl extends BaseService implements
		IsearchesService {

	/**
	 * �ļ����� ���ݲ����������ڵ���Ϣ���ѯ�����ȣ���ѯ�������ļ���Ҫ��Ϣ�б� ���ɰ����Ѷ���ĸ���������Ϣ��
	 * 
	 * @param T_Doc
	 *            �ļ�ʵ����
	 * @return
	 */
	public List<Object> fileSearches() {

		return null;
	}

	/**
	 * ȫ�ļ��� ͨ���ؼ��ֽ���ȫ�ļ��������ؼ�������б� �������ĵ��ṹ��Σ���������Ϣ���ϴ��ߣ��ļ�Ƭ�Σ��ؼ��ָ�����
	 * 
	 * @param keyword
	 *            �ؼ���
	 * @return
	 */
	public List<Object> wholeSearches(String keyword) {

		return null;
	}

	/**
	 * ָ����� ͨ��ָ�������ѯ�������ļ���Ҫ��Ϣ�б�
	 * 
	 * @param T_Doc
	 *            �ļ�ʵ����
	 * @return �ļ�ʵ����
	 */
	public List<Object> targetSearches() {

		return null;
	}

	/**
	 * ��ȡ�ļ����ص�ַ �����ļ�id�����ȡ�������ļ�ftp��ַ�б�
	 * 
	 * @param id
	 * @return
	 */
	public List<Object> fileDownloadList(String[] id) {

		return null;
	}

	/**
	 * ͨ����ID��ȡ�ӽڵ� sign=true ������Ŀ¼���ĸ��ڵ�
	 */
	public List treeDefList(Long id, boolean sign, Map<String, Object> map) {

		List<List<TNodeDef>> resultList = new ArrayList<List<TNodeDef>>();

		// ���û��ҵ����λ�����������Ӹ�����
		if (!map.containsKey("yz_unit")) {
			map.put("yz_unit", super.currentUser.getTUnit().getProxyCode());
		}

		if (sign) {

			TTreeDef treeDef = (TTreeDef) super.EntityQuery(TTreeDef.class, id);

			TTreeDef tTreeDef = new TTreeDef();
			tTreeDef.setParentId(id);
			List<?> list = super.EntityQuery(tTreeDef);

			resultList.addAll(quarryList(id, treeDef.getTNodeDef().getId(),
					list.isEmpty(), map));
		} else {
			TTreeDef tTreeDef = new TTreeDef();
			tTreeDef.setParentId(id);
			List<TTreeDef> list = super.EntityQuery(tTreeDef);

			for (TTreeDef child : list) {
				Long nodeId = child.getId();

				// ��ѯ�ýڵ����Ƿ����ӽڵ�
				TTreeDef def = new TTreeDef();
				def.setParentId(nodeId);
				List<TTreeDef> childList = super.EntityQuery(def);

				if (childList != null && childList.size() != 0) {
					resultList.addAll(this.quarryList(nodeId, child
							.getTNodeDef().getId(), false, map));
				} else {
					resultList.addAll(this.quarryList(nodeId, child
							.getTNodeDef().getId(), true, map));
				}
			}
		}

		return resultList;
	}

	/**
	 * ͨ���ӽڵ�ID��ȡ����Դ
	 */
	private List quarryList(Long treeId, Long id, boolean isLeaf,
			Map<String, Object> optionMap) {

		TNodeDef tNodeDef = (TNodeDef) super.EntityQuery(TNodeDef.class, id);

		// List<TNodeDef> list=super.EntityQuery(tNodeDef);
		String sqlName = "";
		String strParams = "";
		if (tNodeDef != null) {
			sqlName = tNodeDef.getSource();
			strParams = tNodeDef.getParams();
		} else {
			return null;
		}

		List<Object[]> sourceList;

		// �������ļ���
		if (sqlName.toLowerCase().indexOf("from ") == -1) {
			sourceList = super.generalDAO.getResult(sqlName, optionMap);
			// ֱ����SQL
		} else {
			sourceList = (List<Object[]>) super.generalDAO.queryByNativeSQL(
					sqlName, optionMap);
		}

		List<DocNode> resultList = new ArrayList<DocNode>();

		for (Object[] obj : sourceList) {

			DocNode node = new DocNode(treeId, obj[1].toString(), strParams,
					obj[0].toString(), isLeaf);

			resultList.add(node);
		}

		return resultList;
	}

	/**
	 * ���ݹ���״̬ ��ѯ�����б�
	 */
	public List tProjectList(String status) {
		String hql = "from TProject where status = '" + status + "'";
		List<TProject> list = generalDAO.queryByHQL(hql);
		return list;
	}

	/**
	 * �����û�ID�õ����ٶ�λ��
	 * 
	 * @param userId
	 * @return
	 */
	public List<TMenu> quarryUserTreeList(String userId, String roleId) {

		// �õ��û��Ľ�ɫID user.getTRole().getId()
		// TUser user = (TUser) super.EntityQuery(TUser.class, userId);

		String sql = "SELECT T.* FROM T_ROLE_TREE R,T_TREE_DEF T WHERE R.TREE_ID=T.ID AND R.IS_USABLE=1 AND R.ID='"
				+ roleId + "'";

		List<TTreeDef> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TTreeDef.class);

		List<TMenu> resultList = new ArrayList<TMenu>();
		for (TTreeDef treeDef : list) {
			Map<String, Object> map = new HashMap<String, Object>();
			TMenu menu = new TMenu();
			menu.setLable(treeDef.getName());
			map.put("id", treeDef.getId());
			menu.setImage(treeDef.getImageUrl());
			menu.setMap(map);

			resultList.add(menu);
		}

		return resultList;
	}

	/**
	 * ����������ҳ��ѯ
	 * 
	 * @param map
	 * @param firstResult
	 *            ��ʼ��¼����
	 * @param maxResults
	 *            ������¼����
	 * @return
	 * @throws Exception
	 */

	public List<Object[]> queryBasicList(Map<String, Object> map,
			int firstResult, int maxResults, TableBean table) throws Exception {

		Object obj = map.get("format");
		String[] format = null;

		if (obj instanceof String[]) {
			format = (String[]) obj;
		} else if (obj instanceof String) {
			format = new String[] { String.valueOf(obj) };
		}

		String[] in = null;

		if (format == null || format.length == 0) {
			TDictionary dic = new TDictionary();
			dic.setParentCode("FORMAT");

			List<TDictionary> list = super.EntityQuery(dic);
			in = new String[list.size()];

			for (int i = 0; i < list.size(); i++) {
				String code = ((TDictionary) list.get(i)).getCode();
				in[i] = code;
			}

			map.put("in", in);

		} else {

			map.put("in", format);
		}

		// map.remove("format");

		// ���û��ҵ����λ�����������Ӹ�����
		if (!map.containsKey("yz_unit")) {
			map.put("yz_unit", super.currentUser.getTUnit().getProxyCode());
		}

		Long count = super.generalDAO.getResultCount("archieveSearch", map);

		table.setRecords(count.intValue());

		return count.intValue() == 0 ? new ArrayList() : super.generalDAO
				.getResult("archieveSearch", map, firstResult, maxResults);
	}

	/**
	 * ���ĵ�����ȫ�ļ���
	 * 
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 */
	public List<WholeSearchDoc> searchArchive(String queryStr,
			Map<Enum<?>, Object> params, Map<?, Object> filters, TableBean tb,
			boolean highLighter, String unitId) throws URISyntaxException,
			CorruptIndexException, LockObtainFailedException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, IOException, ParseException,
			InvalidTokenOffsetsException, InvocationTargetException,
			NoSuchMethodException {


		IIndexSearcher searcher = SearchFactory.getInstance().getIndexSearcher(
				AnalyzerFactory.getInstance().getAnalyzer(),unitId);

		if (searcher == null)
			return null;

		if (params == null)
			params = new HashMap<Enum<?>, Object>();

		// params.put(IIndexSearcher.TYPE.whole, queryStr);
		params.put(FIELDEnum.contents, queryStr);
		params.put(DOCFIELDEnum.title, queryStr);

		List<Map<Enum<?>, Object>> docList = searcher.searchDocument(params,
				filters, tb.getRecords(), tb.getPage(), highLighter);

		tb.setTotal(searcher.getHitsSize());

		List<WholeSearchDoc> list = new ArrayList<WholeSearchDoc>();
		for (Map<Enum<?>, Object> docField : docList) {

			WholeSearchDoc doc = new WholeSearchDoc();

			Set<Enum<?>> keys = docField.keySet();

			for (Enum<?> e : keys) {
				if (e != null) {
					BeanUtils.copyProperty(doc, e.name(), docField.get(e));
				}
			}

			list.add(doc);
		}

		return list;
	}

	
	
	/**
	 * ��ѯ��������
	 */
	public List<String> searchSuggest(String queryStr,
			Map<Enum<?>, Object> params, Map<?, Object> filters,
			boolean highLighter,String untiId) throws URISyntaxException,
			CorruptIndexException, LockObtainFailedException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, IOException, ParseException,
			InvalidTokenOffsetsException, InvocationTargetException,
			NoSuchMethodException {

	
		IIndexSearcher searcher = SearchFactory.getInstance().getIndexSearcher(
				AnalyzerFactory.getInstance().getAnalyzer(),untiId);
		
		if (searcher == null)
			return null;
		if (params == null)
			params = new HashMap<Enum<?>, Object>();
		params.put(DOCFIELDEnum.keyword, queryStr + "%");
		List<String> docList = searcher.searchDocument(params, filters,
				highLighter);
		return docList;
	}

	/**
	 * �����ĵ�ID ��ѯ�ĵ� �͹��� ��Ϣ
	 */
	public List<Object[]> documentInfo(String id) {

		/*
		 * String sql =
		 * "select t.name,t.format,t.file_size,t.version,t.create_date,tp.name ,tu.name "
		 * +
		 * "from t_doc  t ,t_doc_type   tp  ,t_user tu	where  t.doc_type_code=tp.code and  "
		 * + "t.creator=tu.login_id and t.id=" + id; String hql =
		 * "select t.*,tp.* ,tu.* " +
		 * "from TDoc  t ,TDocType   tp  ,TUser tu where" +
		 * "  t.doc_type_code=tp.code and  t.creator=tu.login_id and t.id=15";
		 * 
		 * Query query = this.getSession().createSQLQuery(sql); // ��ѯ��list List
		 * list = query.list(); Object[] object = null; List returnList = new
		 * ArrayList(); for (int i = 0; i < list.size(); i++) { object =
		 * (Object[]) list.get(i);
		 * 
		 * }
		 */

		List<Object[]> sourceList = super.generalDAO.getResult(
				"projectDetailsSearch", new Object[] { id });

		return sourceList;
	}

	// ���ݵ�¼�û�,��ѯ�߼����� ��ѯ��������
	public List<TUserQuery> queryByLoginId(String loginId) {

		String hql = "select distinct tuq.id.queryName,tuq.id.loginId  from TUserQuery tuq where tuq.id.loginId= '"
				+ loginId + "' ";
		List<TUserQuery> tUserQueryList = this.generalDAO.queryByHQL(hql);
		return tUserQueryList;
	}

	// ���ݵ�¼�û�����������,��ѯ�߼����� ��ѯ��������
	public List<TUserQuery> queryByLoginIdAndName(String loginId, String name) {
		String hql = "from TUserQuery tuq where tuq.id.loginId= '" + loginId
				+ "' and tuq.id.queryName='" + name + "'";
		return this.generalDAO.queryByHQL(hql);
	}

	/**
	 * �����û�ID��ѯ���� �����ϴε�¼ʱ�������ĵ�����
	 * 
	 * @param beginTime
	 *            ��ѯ֮ǰ��¼������
	 * @param unitId
	 *            ��λID
	 * @return ������
	 */
	public int selectInsertDocCount(String beginTime, String unitId) {
		String sql = " select * from t_doc d,t_doc_project dp,t_project p "
				+ "where d.id=dp.doc_id and dp.project_id = p.id and p.owner_unit_id like '"
				+ unitId + "'||'%'";
		if (beginTime != null) {
			sql += " and to_char(d.create_date,'yyyy-mm-dd hh:mi:ss') > '"
					+ beginTime + "'";
		}
		List<TDoc> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TDoc.class);
		return list.size();
	}

	/**
	 * �����û�ID��ѯ���û����ϴε�¼ʱ�������Ĺ�����
	 * 
	 * @param loginId
	 *            ��¼ID
	 * @param unitId
	 *            ��λID
	 * @return ������
	 */
	public int selectInsertProjectCount(String beginTime, String unitId) {
		String sql = "select * from t_project p where "
				+ "p.owner_unit_id like '" + unitId + "'||'%' ";
		if (beginTime != null) {
			sql += " and to_char( p.create_date,'yyyy-mm-dd hh:mi:ss') > '"
					+ beginTime + "'";
		}
		List<TProject> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TProject.class);
		return list.size();
	}

	/**
	 * �����û�ID��ѯ���������ļ�
	 * 
	 * @param loginId
	 *            ��¼ID
	 * @return ���������ļ�
	 */
	public List<TOperateLog> selectBrowseOperateLog(String loginId) {
		String sql = "select * from (select  *  from t_operate_log t where t.type='L01' "
				+ "and t.operator =  '"
				+ loginId
				+ "' order by t.operate_time desc) tl where  rownum<9";
		List<TOperateLog> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TOperateLog.class);
		return list;
	}

	/**
	 * �����û�ID��ѯ��¼ϵͳ��ǰʮ���û�
	 * 
	 * @param loginid
	 *            ��¼ID
	 * @return
	 */
	public List<TUser> getTUserByLoginIdTop10(String loginId) {
		String sql = "select * from (select * from t_user t where t.login_id!='"
				+ loginId
				+ "' order by t.login_time desc) u where  rownum<9 and u.is_local!='R' ";
		List<TUser> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TUser.class);
		return list;
	}

	/**
	 * ��ѯ�����ĵ���������ͳ��ͼ����ʽ��ʾ����
	 * 
	 * @param unitId
	 *            ��λID
	 * @return ������������
	 */
	public List<Object[]> selectInsertDocPic(HashMap<String, Object> map,String searchType) {
		List<Object[]> returnList = new ArrayList<Object[]>();
		List<Object[]> sourceList = null;
		List<Object[]> list = null;
		if("searchMonth".equals(searchType)){//���·�ͳ��
			sourceList = super.generalDAO.getResult(
				"queryMonthDocsCount", map);
			list = super.generalDAO.getResult(
				"queryTDocsCount", map);
			for (int i = 0; i < sourceList.size(); i++) {
				Object[] obj = sourceList.get(i);
				BigDecimal bd = (BigDecimal) obj[1];
				int count = bd.intValue() ;
				String ymonth =  (String) obj[0];
				for (int j = 0; j < list.size(); j++) {
					Object[] object = list.get(j);
					String month = object[0].toString();
					int d = ((BigDecimal)object[1]).intValue();
					if (ymonth.equals(month)) {
						count += d;
					}
				}
				returnList.add(new Object[]{ymonth,count});
			}
			return returnList;
		} else if("searchTesion".equals(searchType)){//����ѹͳ��
			sourceList = super.generalDAO.getResult(
					"queryTesionDocsCount", map);
				list = super.generalDAO.getResult(
					"queryTesionTDocsCount", map);
			return this.sumList(sourceList, list);
		} else if("searchProvince".equals(searchType)){//��ʡ��˾ͳ��
			sourceList = super.generalDAO.getResult(
					"queryProvinceDocsCount", map);
				list = super.generalDAO.getResult(
					"queryProvinceTDocsCount", map);
				return this.sumListToCountry(sourceList, list);
		} else if("searchArea".equals(searchType)){//������ͳ��
			return this.countByArea(String.valueOf(map.get("unit")));
		} else if ("searchCountry".equals(searchType)){//������ͳ��
			sourceList = super.generalDAO.getResult(
					"queryProjectDocCount", map);
			
				list = super.generalDAO.getResult(
						"queryTProjectDocCount", map);
		} else {//
			sourceList = super.generalDAO.getResult("queryDocsCount",map);
			list = super.generalDAO.getResult("queryTDocsCount",map);
		}
		for (int i = 0; i < sourceList.size(); i++) {
			Object[] obj = sourceList.get(i);
			int count = Integer.parseInt(obj[1].toString());
			String ymonth = obj[0].toString();
			for (int j = 0; j < list.size(); j++) {
				Object[] object = list.get(j);
				String month = object[0].toString();
				int d = Integer.parseInt(object[1].toString());
				if (ymonth.equals(month)) {
					count += d;
				}
				obj[1]=(Object)count;
			}
			returnList.add(obj);
		}
		return returnList;
	}

	/**
	 * ��ѯ�����µĵ����������Ա�ͼ����ʽ��ʾ����
	 * 
	 * @param unitId
	 *            ��λID
	 * @return
	 */
	public List<Object[]> selectProjectandDocCountPic(String unitId,String searchType) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		
		map.put("unit", unitId + "%");
		
		List<Object[]> sourceList = null;
		List<Object[]> returnList = new ArrayList<Object[]>();
		List<Object[]> list = null;
		if ("searchCountry".equals(searchType)){//������ͳ��
			sourceList = super.generalDAO.getResult(
					"queryProjectDocCount", map);
			
				list = super.generalDAO.getResult(
						"queryTProjectDocCount", map);
		}
		if("searchProvince".equals(searchType)){
			sourceList = super.generalDAO.getResult(
					"queryProvinceDocsCount", map);
				list = super.generalDAO.getResult(
					"queryProvinceTDocsCount", map);
				return this.sumList(sourceList, list);
		}
		for (int i = 0; i < sourceList.size(); i++) {
			Object[] obj = sourceList.get(i);
			int count = Integer.parseInt(obj[1].toString());
			String name = obj[0].toString();
			for (int j = 0; j < list.size(); j++) {
				Object[] object = list.get(j);
				String tname = object[0].toString();
				int d = Integer.parseInt(object[1].toString());
				if (name.equals(tname)) {
					count += d;
				}
				obj[1]=(Object)count;
			}
			returnList.add(obj);
		}
		return returnList;
	}

	/**
	 * �Ƿ�������ͳ�Ʊ�
	 * 
	 * @param pid
	 *            ����id
	 * @param ymonth
	 *            �·�
	 * @return
	 */
	public int isInsertTProjectDocCount(String pid, String ymonth) {
		int id = 0;
		String sql = "select t.* from t_project_doc_count t "
				+ "where t.project_id='" + pid + "' " + "and t.year_month='"
				+ ymonth + "'";
		List<TProjectDocCount> list = super.generalDAO.queryByNativeSQLEntity(
				sql, TProjectDocCount.class);
		// ������id
		for (int i = 0; i < list.size(); i++) {
			TProjectDocCount c = list.get(i);
			id = Integer.parseInt(c.getId().toString());
		}
		return id;
	}
	
	/**
	 * �ݹ�õ�һ��Ŀ¼�µ������ļ�
	 * @param dir
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unused")
	private static  List<String> showAllFiles(File dir) {
		  File[] fs = dir.listFiles();
		  List<String> fileNames = new ArrayList<String>();
		  for(int i=0; i<fs.length; i++){
			  fileNames.add(fs[i].getAbsolutePath());
			  if(fs[i].isDirectory()){
		          List<String> list = showAllFiles(fs[i]);
		          for(String str : list){
		        	  fileNames.add(str);
		          }
		      }
		}
		  return fileNames;
	}

	public List<Object[]> countByArea(String unitId) {
		List<String> list = new ArrayList<String>();
		//
		if(unitId.length() == 4 || unitId.length() == 2){
			Configurater config = Configurater.getInstance();
			String unitCode = config.getConfigValue("unitCode");
			for (String str : unitCode.split(",")) {
				if(6 == str.length())
					list.add(str);
			}
		}else if(unitId.length() == 6){
			list.add(unitId);
		}
		
		List<Object[]> returnList = new ArrayList<Object[]>();
		for(String code : list){
			HashMap<String, Object> map = new HashMap<String,Object>();
			map.put("unit", code+"%");
			List<Object[]> local = super.generalDAO.getResult("queryProjectDocCount",map);
			List<Object[]> syn = super.generalDAO.getResult("queryTProjectDocCount",map);
			returnList.add(sum(local,syn,code));
		}
		
		return returnList;
	}
	
	private Object[] sum(List<Object[]> local, List<Object[]> syn, String code){
		Object[] obj = new Object[2];
		String hql = "FROM TUnit tu where tu.code = '"+code+"'";
		List<TUnit> lists = super.generalDAO.queryByHQL(hql);
		for (TUnit tUnit : lists) {
			if(tUnit.getCode().equals(code)){
				obj[0] = tUnit.getName();
			}
		}
		int count = 0;
		for(Object[] objLocal : local){
			count += Integer.parseInt(objLocal[1].toString());
		}
		for(Object[] synLocal : syn){
			count += Integer.parseInt(synLocal[1].toString());
		}
		obj[1] = count;
		return obj;
	}
	
	private List<Object[]> sumList(List<Object[]> local,List<Object[]> syn){
		List<Object[]> list = new ArrayList<Object[]>();
		Object[] obj = null ;
		for(int i = 0 ; i < local.size() ; i++){
			for(int j = 0 ; j < syn.size() ; j++){
				obj = new Object[2];
				//�������list�ж�Ӧ��ֵ����ӣ���ɾ������һ��list�ж�Ӧ�ļ�¼
				if(local.get(i)[1].equals(syn.get(j)[1])){
					obj[1] = Integer.parseInt(local.get(i)[0].toString()) + Integer.parseInt(syn.get(j)[0].toString());
					obj[0] = local.get(i)[2];
					local.set(i, null);
					syn.remove(j);
					list.add(obj);
					break;
				} 
			}
			//�����syn��û����local��Ӧ��ֵ����ִ��
			if(null != local.get(i)){
				obj = new Object[2];
				obj[0] = local.get(i)[2];
				obj[1] = Integer.parseInt(local.get(i)[0].toString());
				list.add(obj);
			}
		}
		//�����local��û����syn��Ӧ��ֵ����ִ��
		if(0 != syn.size()){
			obj = new Object[2];
			for (int d = 0; d < syn.size(); d++) {
				obj[0] = syn.get(d)[2];
				obj[1] = Integer.parseInt(syn.get(d)[0].toString());
				list.add(obj);
			}
		}
		
		//����ð������
		Object[] objs = new Object[2];
		for (int k = 0; k < list.size(); k++) {
			for (int d = k+1; d < list.size() ; d++) {
				if(Integer.parseInt(list.get(k)[1].toString()) < Integer.parseInt(list.get(d)[1].toString())){
					objs = list.get(k);
					list.set(k, list.get(d));
					list.set(d, objs);
				}
			}
		}
		return list;
	}
	
	private List<Object[]> sumListToCountry(List<Object[]> local,List<Object[]> syn){
		List<Object[]> list = new ArrayList<Object[]>();
		Object[] obj = null ;
		for(int i = 0 ; i < local.size() ; i++){
			for(int j = 0 ; j < syn.size() ; j++){
				obj = new Object[2];
				//�������list�ж�Ӧ��ֵ����ӣ���ɾ������һ��list�ж�Ӧ�ļ�¼
				if(local.get(i)[1].equals(syn.get(j)[1])){
					obj[1] = Integer.parseInt(local.get(i)[0].toString()) + Integer.parseInt(syn.get(j)[0].toString());
					String name = (String) local.get(i)[2];
					name = name.indexOf("ʡ")!=-1?name.substring(0, name.indexOf("ʡ")):name.substring(0,2);
					obj[0] = name;
					local.set(i, null);
					syn.remove(j);
					list.add(obj);
					break;
				} 
			}
			//�����syn��û����local��Ӧ��ֵ����ִ��
			if(null != local.get(i)){
				obj = new Object[2];
				String name = (String) local.get(i)[2];
				if(name.indexOf("ʡ") != -1)
					name = name.substring(0, name.indexOf("ʡ"));
				else
					name = name.substring(0, 2);
				obj[0] = name;
				obj[1] = Integer.parseInt(local.get(i)[0].toString());
				list.add(obj);
			}
		}
		//�����local��û����syn��Ӧ��ֵ����ִ��
		if(0 != syn.size()){
			for (int d = 0; d < syn.size(); d++) {
				obj = new Object[2];
				String name = (String) syn.get(d)[2];
				name = name.indexOf("ʡ")!=-1?name.substring(0, name.indexOf("ʡ")):name.substring(0,2);
				obj[0] = name;
				obj[1] = Integer.parseInt(syn.get(d)[0].toString());
				list.add(obj);
			}
		}
		
		//����ð������
		Object[] objs = new Object[2];
		for (int k = 0; k < list.size(); k++) {
			for (int d = k+1; d < list.size() ; d++) {
				if(Integer.parseInt(list.get(k)[1].toString()) < Integer.parseInt(list.get(d)[1].toString())){
					objs = list.get(k);
					list.set(k, list.get(d));
					list.set(d, objs);
				}
			}
		}
		return list;
	}
	


	/**
	 * ��ѯ�����ĵ�
	 * @return  List<TDocProject> 
	 * WholeSearchDoc   
	 * @throws Exception  
	 */
	public TDocProject searchLocalDoc(String docid,String projectid){
		String hql = "";
		if(StringUtils.isNotBlank(projectid)){
			hql = "FROM TDocProject tdp where tdp.id.TDoc.id='"+docid+"' AND tdp.id.TProject.id='"+projectid+"'";
		}else{
			hql = "FROM TDocProject tdp where tdp.id.TDoc.id='"+docid + "'";
		}
		List list = super.generalDAO.queryByHQL(hql);
		TDocProject tdp = null;
		if(list.size()>0){
			tdp = (TDocProject)list.get(0);
			tdp.getId().getTProject().getGiveoutUnitId();
			tdp.getId().getTDoc().getName();
		}
		return tdp;
	}

	
	
	/**
	 * ��ѯͬ���ĵ�
	 * @return List<TSynDoc>
	 * @throws Exception
	 */
	public TSynDoc searchSynDoc(String docid,String projectid){
		String hql = "FROM TSynDoc tsd where tsd.id='"+docid+"' AND tsd.TSynProject.code='"+projectid+"'";
		List list = super.generalDAO.queryByHQL(hql);
		TSynDoc tsd = null;
		if(list.size()>0){
			tsd = (TSynDoc)list.get(0);
			tsd.getTSynProject().getGiveoutUnitid();
		}
		return tsd;
	}
	
	/**
	 * ��ѯͬ���ĵ�����
	 * @param beginTime
	 * @param unitId
	 * @return
	 */

	public int selectInsertDocCountBySyn(String beginTime, String unitId) {
		String sql = "select sum(t.doc_count) from t_project_doc_count t where t.owner_unit_id like '"+ unitId +"'||'%' group by t.year_month";
		List list = super.generalDAO.queryByNativeSQL(sql);
		if(list == null || list.isEmpty())
			return 0;
		return ((BigDecimal)list.get(0)).intValue();
	}
	
	/**
	 * ��������IDȡ����������ʡ��˾
	 * @param areaId
	 * @return code,name
	 */
	public List<Object[]> getProvincesByAreaId(String areaId){
		String sql = "select t.code code , t.name name from t_unit t where t.code like '"+areaId+"__' and t.type = 'C01'" ;
		return super.generalDAO.queryByNativeSQL(sql);
	}
}