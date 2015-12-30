package com.nsc.dem.util.index;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;

import com.nsc.base.index.FileField;

public enum EXFIELDEnum {
	/** ��˾�� */
	companyid(new FileField("companyid", null, null, true, true, 0.9f, false,1)),
	/** ��˾���� */
	company(new FileField("company", null, null, true, true, 0.9f, false,1)),
	/** ��Ŀ�� */
	projectid(new FileField("projectid", null, null, true, true, 0.9f, false,1)),
	/** ��Ŀ���� */
	project(new FileField("project", null, null, true, true, 0.9f, false,1)),
	/** �׶� */
	pharase(new FileField("pharase", null, null, true, true, 0.9f, false,1)),
	/** �������� */
	proType(new FileField("proType", null, null, true, true, 0.9f, false,1)),
	/** ���赥λ*/
	designer(new FileField("designer", null, null, true, true, 0.9f, false,1));

	/**
	 * ���캯��
	 * 
	 * @param f
	 */
	EXFIELDEnum(FileField f) {
		this.f = f;
	}

	/**
	 * ��������
	 * 
	 * @param v
	 */
	public void setValue(String v) {
		this.f.setContent(v);
	}

	private final FileField f;

	public FileField getF() throws IllegalAccessException,
			InstantiationException, InvocationTargetException,
			NoSuchMethodException {
		return (FileField) BeanUtils.cloneBean(this.f);
	}

	/**
	 * �����ַ���ȡ��ö��ֵ
	 * 
	 * @param name
	 * @return
	 */
	public static EXFIELDEnum getValue(String name) {

		try {
			return valueOf(name);
		} catch (Exception ex) {
			return null;
		}
	}
}
