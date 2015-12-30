package com.nsc.dem.action.archives;

import java.util.ArrayList;
import java.util.List;

import com.nsc.base.util.DateUtils;
import com.nsc.base.util.FileUtil;
import com.nsc.dem.action.BaseAction;
import com.nsc.dem.bean.archives.TDoc;
import com.nsc.dem.bean.archives.TDocType;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.bean.project.TPreDesgin;
import com.nsc.dem.bean.project.TProject;
import com.nsc.dem.bean.system.TDictionary;
import com.nsc.dem.bean.system.TUnit;
import com.nsc.dem.service.base.IService;
import com.nsc.dem.service.searches.IsearchesService;
/**
 * �ĵ���ϸ��Ϣ��������Ϣ��������ϢAction
 * 
 * @author ibm
 *
 */
public class docDetailsInfoAction extends BaseAction {

	private static final long serialVersionUID = -1110917411158741377L;
	private String id;

	public void setId(String id) {
		this.id = id;
	}

	IsearchesService searchesService;

	public void setSearchesService(IsearchesService searchesService) {
		this.searchesService = searchesService;
	}

	public void setDocPreDetailsList(List<Object[]> docPreDetailsList) {
		this.docPreDetailsList = docPreDetailsList;
	}

	List<Object[]> docPreDetailsList;

	public List<Object[]> getDocPreDetailsList() {
		return docPreDetailsList;
	}

	Object[] docPreDetials;

	public Object[] getDocPreDetials() {
		return docPreDetials;
	}

	public void setDocPreDetials(Object[] docPreDetials) {
		this.docPreDetials = docPreDetials;
	}

	Object[] docDetials;

	public Object[] getDocDetials() {
		return docDetials;
	}

	Object[] proDetials;

	public Object[] getProDetials() {
		return proDetials;
	}

	Object[] preDesignDetials;

	IService baseService;

	public void setBaseService(IService baseService) {
		this.baseService = baseService;
	}

	/**
	 * ��ȡ ���̵���ϸ��Ϣ
	 * @return
	 */
	public String getProjectDetailsAction() {

		List<Object[]> listobject = searchesService.documentInfo(id);

		for (int i = 0; i < listobject.size(); i++) {
			// ������ȡ��ֵ
			TDoc tdoc = (TDoc) listobject.get(i)[0];// �ĵ���
			TDocType tdoctype = (TDocType) listobject.get(i)[1];// �ĵ������
			TProject tproject = (TProject) listobject.get(i)[2];// ���̱�
			TUnit tunit = (TUnit) listobject.get(i)[3];// ��λ��
			TUser tuser = (TUser) listobject.get(i)[4];// �û���
			TDictionary tbaomi = (TDictionary) listobject.get(i)[5];
			TDictionary tdocStatus = (TDictionary) listobject.get(i)[6];
			TDictionary tzhuanYe = (TDictionary) listobject.get(i)[7];
			TDictionary tdianYa = (TDictionary) listobject.get(i)[8];
			TDictionary tprojectClass = (TDictionary) listobject.get(i)[9];
			TDictionary tprojectStatus = (TDictionary) listobject.get(i)[10];
			TDictionary ttypeClass = (TDictionary) listobject.get(i)[11];
			// �ļ���Ϣ(�ļ����ơ��ļ���ʽ���ļ���׺���ĵ��ܼ����ļ�״̬���ϴ�ʱ�䡢�ϴ��ߡ��ļ���С)
			// ������Ϣ(�ĵ����ͷ��ࡢ�������ࡢרҵ�������汾)
			// �ļ���Ϣ ������Ϣ
			String docName = tdoc.getName();// ����
			String docFormat = tdoc.getFormat();// ��ʽ
			FileUtil fileUtil = new FileUtil();

			String docFileSize = fileUtil.getHumanSize(tdoc.getFileSize()
					.longValue())
					+ "(" + tdoc.getFileSize() + "[byte])";// ��С
			String docVersion = tdoc.getVersion();// �汾��
			String docCreateDate = DateUtils.DateToString(tdoc.getCreateDate());// ����ʱ��
			String docClass = tdoctype.getName();// ��������
			String docCreate = tuser.getName();// ������
			String suffix = tdoc.getSuffix();// �ļ���׺��
			String sheJiName = tunit.getName();
			// �ܼ������������û���ܼ�����ȡ�ĵ�������е��ܼ�
			String baomi = "";
			if (tdoc.getSecurity() != null) {

				baomi = tbaomi.getName();
			} else if (tdoctype.getSpeciality() != null) {
				baomi = tbaomi.getName();
			}
			// �ļ�״̬
			String docStatus = tdocStatus.getName();

			// �ĵ����ͷ���
			String zongHeFile = "";
			if (tdoc.getTDocType() != null) {
				// String tempCode = tdoc.getTDocType().getCode();
				// String zhcode = tempCode.substring(4, 6);

				zongHeFile = ttypeClass.getName();
			}
			// רҵ
			String zhuanye = "";
			if (tzhuanYe != null) {
				zhuanye = tzhuanYe.getName();
			}

			// ��ѯ�������Ϣ
			TPreDesgin preDesign = (TPreDesgin) searchesService.EntityQuery(
					TPreDesgin.class, tdoc.getId());
			if (preDesign != null && preDesign.getTUnit().getCode() != null) {

				TUnit unit = (TUnit) searchesService.EntityQuery(TUnit.class,
						preDesign.getTUnit().getCode());

				preDesignDetials = new Object[] {
						unit.getName(),
						DateUtils.DateToString(preDesign.getCreateDate(),
								"yyyy-MM-dd"), preDesign.getAjtm(),
						preDesign.getAndh(), preDesign.getFlbh(),
						preDesign.getJcrm(), preDesign.getJnyh(),
						preDesign.getLjrm(), preDesign.getPzrm(),
						preDesign.getShrm(), preDesign.getSjjd(),
						preDesign.getSjrm(), preDesign.getTzmc(),
						preDesign.getTzzs(), preDesign.getXhrm() };

			}

			// ����ĵ���Ϣ��Object����
			docDetials = new Object[] { docName, docFormat, docFileSize,
					docVersion, docCreateDate, docClass, docCreate, suffix,
					baomi, docStatus, zongHeFile, zhuanye, sheJiName };

			// ������Ϣ(�������ơ�ҵ����λ����ʡ��˾�����̷��ࡢ���̽׶Ρ���ѹ�ȼ�������λ��ʩ����λ�����赥λ)
			String proCode = tproject == null ? "" : tproject.getCode();// ���̱���
			String proName = tproject == null ? "" : tproject.getName();// ��������
			// ��ѹ�ȼ�
			String proVoltageLevel = tdianYa.getName();

			String proOpenYear = tproject == null ? "" : DateUtils
					.DateToString(tproject.getOpenYear());// �������

			// �жϵĸ�ID�Ƿ�Ϊ�������Ϊ�� ��������
			String proParent = "";
			if (tproject != null && tproject.getParentId() != null) {
				TProject newTproject = (TProject) baseService.EntityQuery(
						TProject.class, tproject.getParentId());
				proParent = newTproject.getName();
			}

			// ���̷���
			String projectType = tprojectClass.getName();

			// ���̽׶�
			String projectStatus = tprojectStatus.getName();

			// ��ʡ��˾
			String wangSheng = "";
			// ҵ����λ
			String ownerCode = "";
			if (tproject.getTUnitByOwnerUnitId() != null) {
				TUnit td = (TUnit) searchesService.EntityQuery(TUnit.class,
						tproject.getTUnitByOwnerUnitId().getCode());
				ownerCode = td.getName();
				wangSheng = td.getName();
			}
			// ���赥λ
			String designCode = "";

			if (tproject.getTUnitByDesignUnitId() != null) {
				TUnit tu = (TUnit) searchesService.EntityQuery(TUnit.class,
						tproject.getTUnitByDesignUnitId().getCode());

				designCode = tu.getName();
			}

			// ��Ź�����Ϣ��Object����
			proDetials = new Object[] { proCode, proName, proVoltageLevel,
					proOpenYear, ownerCode, proParent, projectType,
					projectStatus, wangSheng, designCode };
			if (preDesignDetials != null) {
			docPreDetials = new Object[] { docDetials, proDetials,
					preDesignDetials };
			// ���ĵ���Ϣ�͹�����Ϣ����һ��list������
			docPreDetailsList = new ArrayList<Object[]>();
			docPreDetailsList.add(docPreDetials);
			} else {
				docPreDetials = new Object[] { docDetials, proDetials };
				// ���ĵ���Ϣ�͹�����Ϣ����һ��list������
				docPreDetailsList = new ArrayList<Object[]>();
				docPreDetailsList.add(docPreDetials);
			}

		}
		return SUCCESS;
	}

	public Object[] getPreDesignDetials() {
		return preDesignDetials;
	}

	public void setPreDesignDetials(Object[] preDesignDetials) {
		this.preDesignDetials = preDesignDetials;
	}
}