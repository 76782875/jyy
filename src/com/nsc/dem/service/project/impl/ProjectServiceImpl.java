package com.nsc.dem.service.project.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nsc.dem.action.bean.TableBean;
import com.nsc.dem.action.project.ProNode;
import com.nsc.dem.action.project.UnitNode;
import com.nsc.dem.bean.archives.TDoc;
import com.nsc.dem.bean.project.TDocProject;
import com.nsc.dem.bean.project.TDocProjectId;
import com.nsc.dem.bean.project.TProject;
import com.nsc.dem.bean.system.TDictionary;
import com.nsc.dem.bean.system.TUnit;
import com.nsc.dem.service.archives.IarchivesService;
import com.nsc.dem.service.base.BaseService;
import com.nsc.dem.service.project.IprojectService;

@SuppressWarnings("unchecked")
public class ProjectServiceImpl extends BaseService implements IprojectService {

	/**
	 * ������״�б� �����̡�ҵ���Ȳ�νṹ
	 * 
	 * @return
	 */
	public List<Object> partsTreeList() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * ���ɹ��̱���
	 * 
	 * @param codeType
	 *            ��������
	 * @return
	 */
	public String projectCode(String codeType) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * ������״�б� ���������г������̣�������ʡ��˾���ˣ���ҵ��
	 * 
	 * @param code
	 *            ��λ������
	 * @param type
	 *            ��������
	 * @return
	 */
	public List<UnitNode> projectTreeList(String code, String type) {

		int codeLength = code.length() + 2;
		String sql = "";
		if (code == null) {
			sql = "SELECT CODE,NAME FROM T_UNIT WHERE LENGTH(CODE)='4' AND TYPE='C01'";
		} else {
			sql = "SELECT CODE,NAME FROM T_UNIT WHERE LENGTH(CODE)='"
					+ codeLength + "'" + " AND SUBSTR(CODE,1," + code.length()
					+ ")='" + code + "'" + " AND TYPE='C01'";
		}

		List<TUnit> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TUnit.class);

		List<UnitNode> unitList = new ArrayList<UnitNode>();
		for (TUnit unit : list) {
			UnitNode node = new UnitNode();
			node.setCode(unit.getCode());
			node.setName(unit.getName());

			sql = "SELECT ID,CODE,NAME FROM T_PROJECT WHERE PARENT_ID IS NULL "
					+ "AND OWNER_UNIT_ID='" + unit.getCode() + "'"
					+ " AND TYPE='" + type + "'";
			List<TProject> list2 = super.generalDAO.queryByNativeSQLEntity(sql,
					TProject.class);

			List<ProNode> proList = new ArrayList<ProNode>();
			for (TProject tpro : list2) {

				ProNode proNode = new ProNode();
				proNode.setId(tpro.getId());
				proNode.setCode(tpro.getCode());
				proNode.setName(tpro.getName());

				TProject pro = new TProject();
				pro.setParentId(tpro.getId());
				List list3 = super.EntityQuery(pro);

				if (list3 != null && list3.size() != 0) {
					proNode.setLeaf(true);
				} else {
					proNode.setLeaf(false);
				}

				proList.add(proNode);
			}

			node.setList(proList);

			List list4 = this.unitChildTreeList(node.getCode());
			if (list4 != null && list4.size() != 0) {
				node.setLeaf(true);
			} else {
				node.setLeaf(false);
			}
			unitList.add(node);
		}
		return unitList;
	}

	/**
	 * ��ѯ��λ��Ϣ�Ƿ����ӽڵ�
	 * 
	 * @param code
	 * @return
	 */
	public List unitChildTreeList(String code) {

		int codeLength = code.length() + 2;
		String sql = "";

		sql = "SELECT CODE,NAME FROM T_UNIT WHERE LENGTH(CODE)='" + codeLength
				+ "'" + " AND SUBSTR(CODE,1," + code.length() + ")='" + code
				+ "'" + " AND TYPE='C01'";

		List<TUnit> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TUnit.class);

		return list;
	}

	/**
	 * ��ѯ������Ϣ�ӽڵ�
	 * 
	 * @param code
	 * @return
	 */
	public List proChildTreeList(Long id) {

		TProject pro = new TProject();
		pro.setParentId(id);
		List<TProject> list = super.EntityQuery(pro);
		List<ProNode> proList = new ArrayList<ProNode>();
		for (TProject tpro : list) {
			ProNode proNode = new ProNode();
			proNode.setId(tpro.getId());
			proNode.setCode(tpro.getCode());
			proNode.setName(tpro.getName());

			TProject proj = new TProject();
			proj.setParentId(tpro.getId());
			List<?> list3 = super.EntityQuery(proj);

			if (list3 != null && list3.size() != 0) {
				proNode.setLeaf(true);
			} else {
				proNode.setLeaf(false);
			}

			proList.add(proNode);
		}

		return list;
	}

	/**
	 * ��ѯ��λ��Ϣ�������Ŀ�����ȡ������Ϣ
	 * 
	 * @param code
	 *            ҵ����λ����
	 * @param type
	 *            �ĵ���������ǰ��λ -- ��Ŀ����
	 * @return
	 */
	public List<TProject> unitProList(String code, String type) {

		String sql = "";

		if (type.equals("") || type == null) {

			return null;
		}

		sql = "SELECT P.* FROM T_UNIT U,T_PROJECT P WHERE U.CODE=P.OWNER_UNIT_ID"
				+ " AND U.CODE like '"
				+ code
				+ "'"
				+ " AND P.TYPE='"
				+ type
				+ "'";

		List<TProject> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TProject.class);

		return list;
	}

	/**
	 * ���ɵ�λ����
	 * 
	 * @param codeType
	 *            ��������
	 * @return
	 */
	public String unitCode(String codeType) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * ��λ��״�б� ҵ����λ�ɰ���ʡ��˾����
	 * 
	 * @param company
	 *            ��ʡ��˾
	 * @return
	 */
	public List<Object> unitTreeList(String company) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * ��ȡ�����б�
	 */
	public List tProjectList(String code, String tcode) {
		String ucode = super.currentUser.getTUnit().getProxyCode();
		String hql = "";
		if (("".equals(code) || code == null)
				&& ("".equals(tcode) || tcode == null)) {
			hql = "from TProject p where 1=1";// �����̽׶κ͹��̷��඼Ϊ��ʱʹ��
		} else if ((!("".equals(code) || code == null))
				&& ("".equals(tcode) || tcode == null)) {
			hql = "from TProject p where p.status = '" + code + "'";// �����̽׶�Ϊ�պ͹��̷��಻Ϊ��ʱʹ��
		} else if ((!("".equals(tcode) || tcode == null))
				&& ("".equals(code) || code == null)) {
			hql = "from TProject p  where p.type = '" + tcode + "'";// �����̽׶β�Ϊ�պ͹��̷���Ϊ��ʱʹ��
		} else {
			hql = "from TProject p where p.type = '" + tcode
					+ "'  and p.status = '" + code + "' ";// ����Ϊ��ʱʹ��
		}
		hql = hql + " and p.TUnitByOwnerUnitId.code like '" + ucode + "%'";
		List<TProject> list = generalDAO.queryByHQL(hql);
		return list;
	}

	/**
	 * �����ĵ�ȡ�ù�������
	 * 
	 * @param doc
	 * @return
	 */
	public TProject getProjectByDoc(TDoc doc) {
		String hql = "select p from TProject p,TDocProject dp where p.id=dp.id.TProject.id and dp.id.TDoc.id="
				+ doc.getId();

		List<TProject> list = generalDAO.queryByHQL(hql);

		return list.isEmpty() ? null : list.get(0);
	}

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
			throws Exception {
		TProject project = (TProject) super.EntityQuery(TProject.class, id);

		TDocProject tdocProject = new TDocProject();
		TDocProjectId docProjectId = new TDocProjectId();
		docProjectId.setTProject(project);
		tdocProject.setId(docProjectId);

		String hql = "from TDocProject t where t.id.TProject.id="
				+ tdocProject.getId().getTProject().getId();
		List<TDocProject> docProject = generalDAO.queryByHQL(hql);

		String[] fileCode = new String[docProject.size()];
		for (int i = 0; i < docProject.size(); i++) {

			TDocProject tpro = (TDocProject) docProject.get(i);
			fileCode[i] = tpro.getId().getTDoc().getId();

		}
		archives.delArchives(fileCode);
		super.EntityDel(project);

	}

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
	public List queryProjectInfoList(Map<String, Object> map, int firstResult,
			int maxResults, TableBean table) throws Exception {
		Long count = super.generalDAO.getResultCount("proInfoSearch", map);

		table.setRecords(count.intValue());

		return count.intValue() == 0 ? null : super.generalDAO.getResult(
				"proInfoSearch", map, firstResult, maxResults);

	}

	/**
	 * ��ѯ���赥λ
	 * 
	 * @param code
	 * @return
	 */
	public List<TProject> parentNameList() {

		String ucode = super.currentUser.getTUnit().getProxyCode();
		// String sql =
		// "select t.*, u.* from t_unit t  ,t_user u where t.code like '"
		// + ucode + "%'";
		String sql = "SELECT * FROM t_project p WHERE parent_id is null and p.owner_unit_id like '"
				+ ucode + "%'";

		List<TProject> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TProject.class);

		return list;
	}

	/**
	 * ��ȡ�����б� ����ά��ר��
	 */
	public List<TProject> tProjectListDoc(String code) {
		String hql = "from TProject p ";
		if (!"".equals(code) && code != null) {
			hql = hql + " where p.TUnitByOwnerUnitId.code like '" + code + "%'";
		}
		List<TProject> list = generalDAO.queryByHQL(hql);
		return list;
	}

	/**
	 * ���ݵ�λcode��ѯ���й��̴��ڵĳ��赥λ
	 * 
	 * @param unitCode
	 * @return
	 */
	public List<TProject> getProjectByDesCode(String unitCode) {
		String sql = "select t.* from t_project t where t.design_unit_id= '"
				+ unitCode + "'";
		List<TProject> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TProject.class);
		return list;
	}

	/**
	 * ���ݵ�λcode��ѯ���й��̴��ڵ�ҵ����λ
	 * 
	 * @param unitCode
	 * @return
	 */
	public List<TProject> getProjectByOwenCode(String unitCode) {
		String sql = "select t.* from t_project t where t.owner_unit_id= '"
				+ unitCode + "'";
		List<TProject> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TProject.class);
		return list;
	}

	/**
	 * ���ݹ���id���ĵ��������ڣ� ��ѯ�ĵ���
	 * 
	 * @param pid
	 *            ����id
	 * @param cdate
	 *            �ĵ���������
	 * @return �ĵ���
	 */
	public int getDocCountByProjectId(String pid, String cdate) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("pid", pid);
		map.put("cdate", cdate);
		List<Object[]> list = super.generalDAO.getResult("docCountByProjectId",
				map);
		return list.size();
	}

	/**
	 * ��ѯû�в��������Ĺ���
	 */
	public List<Object[]> getProjectByNoCreateIndex() {
		return super.generalDAO.getResult("findProjectByNoIndex");
	}

	/**
	 * ���ݵ�ѹ���Ʋ�ѯ                                                                                                                                                                                                                                                                                                                           
	 * 
	 * @param levelName
	 *            ��ѹ����
	 * @return TDictionary����
	 */
	public List<TDictionary> getVoltageLevelByName(String levelName) {
		String sql = "select * from t_dictionary t where upper(t.name) like '%'||upper('"+levelName+"')||'%'";
		List<TDictionary> list = super.generalDAO.queryByNativeSQLEntity(sql,TDictionary.class);
		return list;
	}

}
