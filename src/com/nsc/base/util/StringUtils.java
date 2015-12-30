package com.nsc.base.util;

import java.util.ArrayList;
import java.util.List;

/**
 * �ַ���������
 * 
 * ���������ַ����ĳ��ò�����������롢�����жϵȡ�
 * 
 * @author bs-team
 *
 * @date Oct 19, 2010 10:36:18 AM
 * @version
 */
public class StringUtils {
	/**
	 * ���ַ�����newCoding��ʽ���б���
	 * @param url
	 * @param oldCoding ԭ���뷽ʽ
	 * @param newCoding 
	 * @return �������ַ���
	 */
	public static String codingUrl(String url,String oldCoding,String newCoding){
		try{
			return new String(url.getBytes(oldCoding),newCoding);
		}catch(Exception ex){
			throw new AppException(ex,"app.string.decoding",null,new String[]{});
		}
	}
	/**
	 * �ж��ַ������Ƿ�������
	 * @param digital
	 * @return �Ƿ�����
	 */
	public static boolean isNumberic(String digital){		
		for (int i = 0; i < digital.length(); i++) {
            //If we find a non-digit character we return false.
            if (!Character.isDigit(digital.charAt(i)))
                return false;
        }
		
        return true;
	}
	/**
	 * ��str�ַ�����slipt���ݽ�ȡ��һ���ַ�������
	 * @param str
	 * @param slipt
	 * @return �ַ�������
	 */
	public static String[] getHierarchy(String str,String slipt){
		String[] hierarchy=new String[0];
		StringBuffer originalStr=new StringBuffer(str);
		List<String> list=new ArrayList<String>();
		while(originalStr.indexOf(slipt)!=-1){
			list.add(originalStr.substring(0, str.indexOf(slipt)));
			originalStr.delete(0, originalStr.indexOf(slipt)+1);
		}
		list.add(originalStr.toString());
		
		return list.toArray(hierarchy);
	}
	
	/**
	 * ����ҳ���������
	 * ���Զ��嵱�������Ϊ��ʱ��ֵ.
	 * */
	public static Object isNull(Object obj,String nullRetStr){
		Object retStr;
		if(obj!=null&&!"".equals(obj)){
			retStr = obj;
		}
		else{
			retStr = nullRetStr;
		}
		return retStr;
	}
	
	/**
	 * �Ѷ���ת�����ַ���
	 * @param obj ��ת������
	 * @return
	 */
	public static String toString(Object obj) {
		String str = "";
		if (obj != null) {
			str = obj.toString();
		}
		return str;
	}

	/**
	 * �Ѷ���ת�����ַ�
	 * @param obj  ��ת������
	 * @param outStr ����Ϊ��ʱ��Ĭ�����ֵ
	 * @return
	 */
	public static String toString(Object obj, String outStr) {
		String str = "";
		str = outStr != null ? outStr : "";
		if (obj != null) {
			str = obj.toString();
		} else {

		}
		return str;
	}
}
