package com.nsc.base.util;

/**
 * Class������
 * 
 * �������ڻ�ȡClass��һ����Ϣ��
 * 
 * @author bs-team
 *
 * @date Oct 19, 2010 10:30:26 AM
 * @version
 */
public class ClassUtil {

	/**
	 * ���ض�������ƣ��������������֣�
	 * 
	 * @param c
	 * @return ��������ƣ��������������֣�
	 */
	public static String getClassName(Class<?> c) {
		String FQClassName = c.getName();
		int firstChar;
		firstChar = FQClassName.lastIndexOf('.') + 1;
		if (firstChar > 0) {
			FQClassName = FQClassName.substring(firstChar);
		}
		return FQClassName;
	}

	/**
	 * ���ظ���İ���������
	 * 
	 * @param c
	 * @return ����İ���������
	 */
	public static String getFullClassName(Class<?> c) {
		return c.getName();
	}

	/**
	 * ֻ���ظ������ڰ������ƣ���û�а��򷵻ؿ��ַ���
	 * 
	 * @param c
	 * @return �������ڰ������ƣ���û�а��򷵻ؿ��ַ���
	 */
	public static String getPackageName(Class<?> c) {
		String fullyQualifiedName = c.getName();
		int lastDot = fullyQualifiedName.lastIndexOf('.');
		if (lastDot == -1) {
			return "";
		}
		return fullyQualifiedName.substring(0, lastDot);
	}
}
