package com.nsc.dem.util.filestore;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.xwork.StringUtils;

import com.nsc.base.conf.Configurater;

public class FileStoreLocation {
	
	public static String getStoreLocation(){
		Configurater config = Configurater.getInstance();
		String systemType = config.getConfigValue("system_type");
		if("1".equals(systemType)){
			return config.getConfigValue("country");
		}else if("3".equals(systemType)){
			return config.getConfigValue("unitCode");
		}
		return null;
	}
	
	/**
	 * �ı�洢λ��
	 * @param store
	 * @return
	 */
	public static String changStoreLocation(String store){
		String current = getStoreLocation();
		//����洢λ��Ϊ�գ����ص�ǰϵͳID
		if(StringUtils.isBlank(store))
			return current;
		//���ҵ�ǰϵͳID�Ƿ��ڴ洢��
		if(isExist(store,current)){
			return store;
		}else{
			return store + "#" + current;
		}
	}
	
	/**
	 * ���ַ���context�м���Ƿ���unitCode����
	 * @param context �ļ��洢λ�ã���ID��#Ϊ�ָ�
	 * @param unitCode ��λID
	 * @return ���ڷ���true�������ڷ���false
	 */
	public static boolean isExist(String context, String unitCode){
		if(StringUtils.isBlank(context) || StringUtils.isBlank(context))
			return false;
		
		String[] codes = context.split("#");
		for(String code : codes){
			if(code.trim().equals(unitCode.trim()))
				return true;
		}
		return false;
	}
	
	
	public static String delRepeated(String store){
		if(StringUtils.isBlank(store))
			return "";
		
		String[] strs = store.split("#");
		List<String> lists = new ArrayList<String>();
		for (int i = 0; i < strs.length; i++) {
			if(lists.contains(strs[i])){
				continue;
			}
			lists.add(strs[i]);
		}
		
		StringBuffer buffer = new StringBuffer();
		for(String str : lists){
			buffer.append(str+"#");
		}
		
		if(buffer.length() <= 0)
			return "";
		
		//ɾ�����һ��#
		buffer.deleteCharAt(buffer.length()-1);
		return buffer.toString();
	}
}
