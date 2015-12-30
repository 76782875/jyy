package com.nsc.dem.util.download;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.Element;

import com.nsc.base.conf.Configurater;
import com.nsc.dem.util.xml.XmlUtils;

/**
 * IP������
 *
 */
public class LoginLocationUtils {
	
	/**
	 * �����û���¼��IP���ڵ�
	 * @param ip
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String getIpLocal(String ip){
		//��IP�ĵ�ȥ��
		String ipStr = ip.replace(".", "");
		int ipInt = Integer.parseInt(ipStr);
		
		XmlUtils util = XmlUtils.getInstance("intenterIp.xml");
		Document document = util.getDocument();
		List<Element> intenterList = document.selectNodes("//intenter");
		for(Element intenter: intenterList){
			String startStr = intenter.attributeValue("start");
			String endStr = intenter.attributeValue("end");
			startStr = startStr.replace(".","");
			endStr = endStr.replace(".", "");
			long start = Integer.parseInt(startStr);
			long end = Integer.parseInt(endStr);
			if(ipInt >= start && ipInt <= end){
				return intenter.attributeValue("code");
			}
		}
		return null;
	}
	
	/**
	 * �ж��Ƿ��ڱ��ص�¼
	 * @param unitCode �û���¼���ڵصı���
	 */
	public static boolean isLocationLogin(String unitCode){
		Configurater config = Configurater.getInstance();
		String systemType = config.getConfigValue("system_type");
		//����
		if("1".equals(systemType)){
			String country = config.getConfigValue("country");
			//�����û�
			if(unitCode.length()== 6){
				return false;
			}else if (unitCode.equals(country.trim())){
				return true;
			}else{
				return false;
			}
		//ʡ��˾
		}else if("3".equals(systemType)){
			if(unitCode.equals(config.getConfigValue("unitCode"))){
				return true;
			}else{
				return false;
			}
		}
		return false;
	}
}
