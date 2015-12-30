package com.nsc.dem.webservice.util;

import java.util.List;

import org.apache.log4j.Logger;

import com.nsc.base.conf.Configurater;
import com.nsc.base.util.Component;
import com.nsc.dem.bean.system.TDictionary;
import com.nsc.dem.service.base.IService;


/**
 * ����δʹ�ô���
 * @author ycl
 *
 */
public class ProjectUtils {
	private static IService service = (IService) Component.getInstance(
			"baseService", Configurater.getInstance().getServletContext());

	/**
	 * ������ƻ��еĹ��������жϹ������
	 * 
	 * @param gcName
	 *            ��������
	 * @return �ֵ����
	 * @throws Exception
	 */
	public static TDictionary getProjectType(String gcName) throws Exception {
		TDictionary dic = new TDictionary();
		dic.setParentCode("GCFL");
		if (gcName.indexOf("���") != -1 || gcName.indexOf("���") != -1) {

			dic.setName("���繤��");

		} else if (gcName.indexOf("���վ") != -1) {

			dic.setName("���վ");

		} else if (gcName.indexOf("ͨ��վ") != -1) {

			dic.setName("ͨ��վ");

		} else if (gcName.indexOf("��������վ") != -1) {

			dic.setName("��������վ");

		} else if (gcName.indexOf("����վ") != -1) {

			dic.setName("����վ");

		} else if (gcName.indexOf("��·") != -1 && gcName.indexOf("�ܿ�") != -1) {

			dic.setName("�ܿ���·");
		} else if (gcName.indexOf("ͨ����·") != -1) {

			dic.setName("ͨ����·");
		} else if (gcName.indexOf("��·") != -1) {
			dic.setName("������·");
		} else {
			dic.setName("���繤��");
		}

		List<Object> typeList = service.EntityQuery(dic);

		if (typeList != null && typeList.size() > 0) {
			dic = (TDictionary) typeList.get(0);
		} else {
			Logger.getLogger(WsUtils.class).error("�޷���ȡ�����̷���:");
			throw new Exception("�޷���ȡ�����̷��࣡");
		}

		return dic;
	}

	/**
	 * ������ƻ��л�ȡ���̵�ѹ�ȼ�
	 * 
	 * @param projectInfo
	 *            ��������
	 * @return �ֵ����
	 * 
	 */
	public static String getVoltageLevelByProjectName(String projectInfo) {
		TDictionary dictionary = new TDictionary();
		dictionary.setParentCode("DYDJ");

		if (projectInfo.indexOf("��1000kV") != -1) {
			dictionary.setName("��1000kV");
		} else if (projectInfo.indexOf("1000kV") != -1) {
			dictionary.setName("1000kV");
		} else if (projectInfo.indexOf("��800kV") != -1) {
			dictionary.setName("��800kV");
		} else if (projectInfo.indexOf("750kV") != -1) {
			dictionary.setName("750kV");
		} else if (projectInfo.indexOf("��660kV") != -1) {
			dictionary.setName("��660kV");
		} else if (projectInfo.indexOf("500kV") != -1) {
			dictionary.setName("500kV");
		} else if (projectInfo.indexOf("��400kV") != -1) {
			dictionary.setName("��400kV");
		} else if (projectInfo.indexOf("330kV") != -1) {
			dictionary.setName("330kV");
		} else if (projectInfo.indexOf("220kV") != -1) {
			dictionary.setName("220kV");
		} else if (projectInfo.indexOf("110kV") != -1) {
			dictionary.setName("110kV");
		} else if (projectInfo.indexOf("66kV") != -1) {
			dictionary.setName("66kV");
		} else if (projectInfo.indexOf("35kV") != -1) {
			dictionary.setName("35kV");
		} else {
			return null;
		}
		List<Object> typeList = service.EntityQuery(dictionary);

		if (typeList != null && typeList.size() > 0) {
			dictionary = (TDictionary) typeList.get(0);
		} else {
			Logger.getLogger(WsUtils.class).error("�޷���ȡ�����̵�ѹ�ȼ�");
		}

		return dictionary.getCode();
	}

	/**
	 * ������ƻ��л�ȡ���̵�ѹ�ȼ�
	 * 
	 * @param projectInfo
	 *            ��������
	 * @return �ֵ����
	 * 
	 */
	public static String getVoltageLevelByProjectType(String projectInfo) {
		TDictionary dictionary = new TDictionary();
		dictionary.setParentCode("DYDJ");
			if (projectInfo.indexOf("��1000kV") != -1) {
				dictionary.setName("��1000kV");
			} else if (projectInfo.indexOf("1000kV") != -1) {
				dictionary.setName("1000kV");
			} else if (projectInfo.indexOf("��800kV") != -1) {
				dictionary.setName("��800kV");
			} else if (projectInfo.indexOf("750kV") != -1) {
				dictionary.setName("750kV");
			} else if (projectInfo.indexOf("��660kV") != -1) {
				dictionary.setName("��660kV");
			} else if (projectInfo.indexOf("500kV") != -1) {
				dictionary.setName("500kV");
			} else if (projectInfo.indexOf("��400kV") != -1) {
				dictionary.setName("��400kV");
			} else if (projectInfo.indexOf("330kV") != -1) {
				dictionary.setName("330kV");
			} else if (projectInfo.indexOf("220kV") != -1) {
				dictionary.setName("220kV");
			} else if (projectInfo.indexOf("110kV") != -1) {
				dictionary.setName("110kV");
			} else if (projectInfo.indexOf("66kV") != -1) {
				dictionary.setName("66kV");
			} else if (projectInfo.indexOf("35kV") != -1) {
				dictionary.setName("35kV");
			} else {
				return null;
			}
		List<Object> typeList = service.EntityQuery(dictionary);

		if (typeList != null && typeList.size() > 0) {
			dictionary = (TDictionary) typeList.get(0);
		} else {
			Logger.getLogger(WsUtils.class).error("�޷���ȡ�����̵�ѹ�ȼ�");

		}

		return dictionary.getCode();
	}
}
