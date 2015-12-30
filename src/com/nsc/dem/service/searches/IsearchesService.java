package com.nsc.dem.service.searches;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.store.LockObtainFailedException;

import com.nsc.dem.action.bean.TableBean;
import com.nsc.dem.action.bean.WholeSearchDoc;
import com.nsc.dem.action.system.TMenu;
import com.nsc.dem.bean.archives.TSynDoc;
import com.nsc.dem.bean.archives.TUserQuery;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.bean.project.TDocProject;
import com.nsc.dem.bean.system.TOperateLog;
import com.nsc.dem.service.base.IService;

public interface IsearchesService extends IService {

	/**
	 * �ļ����� ���ݲ����������ڵ���Ϣ���ѯ�����ȣ���ѯ�������ļ���Ҫ��Ϣ�б� ���ɰ����Ѷ���ĸ���������Ϣ��
	 * 
	 * @param T_Doc
	 *            �ļ�ʵ����
	 * @return
	 */
	public List<Object> fileSearches();

	/**
	 * ȫ�ļ��� ͨ���ؼ��ֽ���ȫ�ļ��������ؼ�������б� �������ĵ��ṹ��Σ���������Ϣ���ϴ��ߣ��ļ�Ƭ�Σ��ؼ��ָ�����
	 * 
	 * @param keyword
	 *            �ؼ���
	 * @return
	 */
	public List<Object> wholeSearches(String keyword);

	/**
	 * ָ����� ͨ��ָ�������ѯ�������ļ���Ҫ��Ϣ�б�
	 * 
	 * @param T_Doc
	 *            �ļ�ʵ����
	 * @return �ļ�ʵ����
	 */
	public List<Object> targetSearches();

	/**
	 * ��ȡ�ļ����ص�ַ �����ļ�id�����ȡ�������ļ�ftp��ַ�б�
	 * 
	 * @param id
	 * @return
	 */
	public List<Object> fileDownloadList(String[] id);

	/**
	 * ͨ����ID��ȡ�ӽڵ� sign=true ������Ŀ¼���ĸ��ڵ�
	 * 
	 * @param id
	 * @param sign
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List treeDefList(Long id, boolean sign, Map<String, Object> map);

	public List<Object[]> queryBasicList(Map<String, Object> map, int firstResult,
			int maxResults, TableBean count) throws Exception;

	/**
	 * ȫ�ļ����ļ�
	 * 
	 * @param file
	 * @param params
	 * @param roleId
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public List<WholeSearchDoc> searchArchive(String queryStr,
			Map<Enum<?>, Object> params, Map<?, Object> filters, TableBean tb,
			boolean highLight, String untiId) throws URISyntaxException,
			CorruptIndexException, LockObtainFailedException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, IOException, ParseException,
			InvalidTokenOffsetsException, InvocationTargetException,
			NoSuchMethodException;
	
	public List<String> searchSuggest(String queryStr,
			Map<Enum<?>, Object> params, Map<?, Object> filters,
			boolean highLight, String untiId) throws URISyntaxException,
			CorruptIndexException, LockObtainFailedException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, IOException, ParseException,
			InvalidTokenOffsetsException, InvocationTargetException,
			NoSuchMethodException;

	public List<Object[]> documentInfo(String id);

	public List<TMenu> quarryUserTreeList(String userId,String roleId);

	public List<TUserQuery> queryByLoginId(String loginId);

	public List<TUserQuery> queryByLoginIdAndName(String loginId, String name);



	/**
	 * �����û�ID��ѯ���� �����ϴε�¼ʱ�������ĵ�����
	 * @param loginId ��¼ID
	 * @param unitId ��λID
	 * @return ������
	 */
	public int selectInsertDocCount(String beginTime, String unitId);

	/**
	 * �����û�ID��ѯ���û����ϴε�¼ʱ�������Ĺ�����
	 * @param loginId ��¼ID
	 * @param unitId ��λID
	 * @return ������
	 */
	public int selectInsertProjectCount(String beginTime, String unitId);

	/**
	 * �����û�ID��ѯ���������ļ�
	 * @param loginId ��¼ID
	 * @return ���������ļ�
	 */
	public List<TOperateLog> selectBrowseOperateLog(String loginId);
	
	/**
	 * �����û�ID��ѯ��¼ϵͳ��ǰʮ���û�
	 * @param loginid ��¼ID
	 * @return
	 */
	public List<TUser> getTUserByLoginIdTop10(String loginId);
	
	/**
	 * ��ѯ�����ĵ���������ͳ��ͼ����ʽ��ʾ���� 
	 * @param searchType 
	 * @param unitId ��λID
	 * @return 
	 */
	public List<Object[]> selectInsertDocPic(HashMap<String,Object> map, String searchType) ;
	
	
	/**
	 * ��ѯ�����µĵ����������Ա�ͼ����ʽ��ʾ����
	 * @param unitId ��λID
	 * @param searchType 
	 * @return
	 */
	public List<Object[]> selectProjectandDocCountPic(String unitId, String searchType);
	
	/**
	 * �Ƿ�������ͳ�Ʊ�
	 * @param pid ����id
	 * @param ymonth �·�
	 * @return
	 */
	public int isInsertTProjectDocCount(String pid,String ymonth);
	
	/**
	 * 
	 * @param map
	 * @return
	 */
	public List<Object[]> countByArea(String unitId);
	

	/**
	 * ��ѯ�����ĵ�
	 * @return List<TDocProject>
	 * @throws Exception
	 */
	public TDocProject searchLocalDoc(String docid,String projectid);
	
	/**
	 * ��ѯͬ���ĵ�
	 * @return List<TSynDoc>
	 * @throws Exception
	 */
	public TSynDoc searchSynDoc(String docid,String projectid);

	public int selectInsertDocCountBySyn(String object, String unitId);
	
	/**
	 * ��������ID��ѯ��������ʡ��˾
	 * @param areaId
	 * @return code,name
	 */
	public List<Object[]> getProvincesByAreaId(String areaId);
}
