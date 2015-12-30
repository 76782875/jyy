package com.nsc.dem.service.project;

import java.util.List;
import java.util.Map;

import com.nsc.dem.action.bean.TableBean;
import com.nsc.dem.action.project.UnitNode;
import com.nsc.dem.bean.archives.TDoc;
import com.nsc.dem.bean.project.TProject;
import com.nsc.dem.bean.system.TDictionary;
import com.nsc.dem.service.archives.IarchivesService;
import com.nsc.dem.service.base.IService;

public interface IprojectService extends IService {

	/**
	 * ���ɹ��̱���
	 * 
	 * @param codeType
	 *            ��������
	 * @return
	 */
	public String projectCode(String codeType);

	/**
	 * ���ɵ�λ����
	 * 
	 * @param codeType
	 *            ��������
	 * @return
	 */
	public String unitCode(String codeType);

	/**
	 * ��λ��״�б� ҵ����λ�ɰ���ʡ��˾����
	 * 
	 * @param company
	 *            ��ʡ��˾
	 * @return
	 */
	public List<Object> unitTreeList(String company);

	/**
	 * ������״�б� ���������г������̣�������ʡ��˾���ˣ���ҵ��
	 * 
	 * @return
	 */
	public List<UnitNode> projectTreeList(String code, String type);

	/**
	 * ������״�б� �����̡�ҵ���Ȳ�νṹ
	 * 
	 * @return
	 */
	public List<Object> partsTreeList();

	public List<TProject> tProjectList(String code, String tcode);

	public List<TProject> tProjectListDoc(String code);

	/**
	 * ��ѯ��λ��Ϣ�������Ŀ�����ȡ������Ϣ
	 * 
	 * @param code
	 *            ҵ����λ����
	 * @param type
	 *            �ĵ���������ǰ��λ -- ��Ŀ����
	 * @return
	 */
	public List<TProject> unitProList(String code, String type);

	/**
	 * �����ĵ���ѯ��������
	 * 
	 * @param doc
	 * @return
	 */
	public TProject getProjectByDoc(TDoc doc);

	/**
	 * ���ݹ���IDɾ�����̣���ɾ�������µ������ĵ�
	 * 
	 * @param id
	 *            ����ID
	 * @param archives
	 *            �ĵ�����ӿ�
	 * @throws Exception
	 */
	public void deleteProjectWithDoc(Long id, IarchivesService archives)
			throws Exception;

	/**
	 * ������Ϣ��ҳ��ѯ
	 * 
	 * @param map
	 * @param firstResult
	 *            ��ʼ��¼����
	 * @param maxResults
	 *            ������¼����
	 * @return
	 * @throws Exception
	 */
	public List<Object[]> queryProjectInfoList(Map<String, Object> map,
			int firstResult, int maxResults, TableBean table) throws Exception;

	/**
	 * ��ʼ����������
	 * 
	 * @return
	 */
	public List<TProject> parentNameList();

	/**
	 * ���ݵ�λcode��ѯ���й��̴��ڵĳ��赥λ
	 * 
	 * @param unitCode
	 * @return
	 */
	public List<TProject> getProjectByDesCode(String unitCode);

	/**
	 * ���ݵ�λcode��ѯ���й��̴��ڵ�ҵ����λ
	 * 
	 * @param unitCode
	 * @return
	 */
	public List<TProject> getProjectByOwenCode(String unitCode);

	/**
	 * ���ݹ���id���ĵ��������ڣ� ��ѯ�ĵ���
	 * 
	 * @param pid
	 *            ����id
	 * @param cdate
	 *            �ĵ���������
	 * @return �ĵ���
	 */
	public int getDocCountByProjectId(String pid, String cdate);
	
	/**
	 * ��ѯû�в��������Ĺ���
	 * @return
	 */
	public List<Object[]> getProjectByNoCreateIndex();
	
	
	/**
	 * ���ݵ�ѹ���Ʋ�ѯ
	 * @param levelName
	 * 					��ѹ����
	 * @return
	 * 			TDictionary����
	 */
	public List<TDictionary> getVoltageLevelByName(String levelName);
}
