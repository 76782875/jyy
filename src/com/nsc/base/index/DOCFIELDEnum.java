package com.nsc.base.index;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;

public enum DOCFIELDEnum {
	/** �ĵ��� */
	docid(new FileField("docid", null, null, true, true, 1f, false,1)),
	/** ���� */
	title(new FileField("title", null, null, true, true, 0.9f, true,3)),
	/** ���� */
	subject(new FileField("subject", null, null, true, true, 0.8f, false,3)),
	/** �ĵ���� */
	doctype(new FileField("doctype", null, null, true, true, 0.7f, false,1)),
	/** �ؼ��� */
	keyword(new FileField("keyword", null, null, true, true, 0.6f, false,3)),
	/** ���� */
	author(new FileField("author", null, null, true, true, 0.9f, false,1)),
	/** ��Դ */
	resource(new FileField("resource", null, null, true, true, 0.9f, false,1)),
	/** �汾 */
	version(new FileField("version", null, null, true, true, 0.9f, false,1)),
	/** ����ʱ�� */
	cdate(new FileField("cdate", null, null, true, true, 0.5f, false,3)),
	/** ����״̬ */
	status(new FileField("status", null, null,true, true,0.9f,false,1)),
	/**·��*/
	url(new FileField("url", null, null, true, true, 0.1f, false, 1)),
	/**��׺*/
	suffix(new FileField("suffix", null, null, true, true, 0.1f, false, 1));
	
	
	
	/**
	 * ���캯��
	 * 
	 * @param f
	 */
	DOCFIELDEnum(FileField f) {
		Logger.getLogger(DOCFIELDEnum.class).info(f.getName() + " " + f.hashCode());
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
	public static DOCFIELDEnum getValue(String name) {

		try {
			return valueOf(name);
		} catch (Exception ex) {
			return null;
		}
	}
}
