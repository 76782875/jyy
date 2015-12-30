package com.nsc.dem.util.index;


import javax.servlet.ServletContext;

import com.nsc.base.conf.Configurater;
import com.nsc.base.util.Component;
import com.nsc.dem.bean.archives.TDoc;
import com.nsc.dem.bean.project.TProject;
import com.nsc.dem.service.project.IprojectService;

/**
 * �����ļ��洢��������
 *
 */
public class IndexStoreUitls {
	
	/**
	 * ��ȡ�洢λ��
	 */
	public static String getStoreLocation(String docId, ServletContext context){
		IprojectService projectService = (IprojectService) Component.getInstance(
				"projectService", context);
		TDoc doc = new TDoc();
		doc.setId(docId);
		TProject tPro = projectService.getProjectByDoc(doc);
		if(tPro == null)
			return null;
		String location = tPro.getApproveUnit().getProxyCode();
		String companyId = tPro.getTUnitByOwnerUnitId().getCode();
		return getStoreLocation(companyId, location);
	}
	
	
	/**
	 * ��ȡ�洢λ��
	 * @param companyId ҵ����λ
	 * @param location  ����ص�
	 */
	public static String getStoreLocation(String companyId, String location){
		Configurater config = Configurater.getInstance();
		/*
		 * �����û����������λ�ǹ��������浽����local
		 *           �������λ�����򣬱��浽ҵ����λ��������
		 */
		if("1".equals(config.getConfigValue("system_type").trim())){
			if(location.length() == 4){
				return config.getConfigValue("country");
			}else if(location.trim().length() == 6){
				return companyId.trim().substring(0,6);
			}else{
				return null;
			}
		//ʡ��˾�����в���ֱ�ӱ����ڱ��ص�local
		}else if("3".equals(config.getConfigValue("system_type").trim())){//ʡ��˾
			return config.getConfigValue("unitCode");
		}else{
			return null;
		}
	}
	
}
