package com.nsc.dem.util.download;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;

import com.nsc.base.conf.Configurater;
import com.nsc.dem.util.xml.FtpXmlUtils;
import com.nsc.dem.util.xml.XmlUtils;

public class DownloadAddessUtils {

	/**
	 * ������ص�ַ
	 * 
	 * @param loginLocation  ��¼���ڵ�ID
	 * @param toUintIds      �ĵ����ļ���ʡ��
	 * @param isLocalLogin   �Ƿ񱾵ص�¼
	 * @param userCode       �û�������λ
	 * @return
	 */
	public static List<String[]> getDownloadAddress(String loginLocation,
			String toUintIds, boolean isLocalLogin, String userCode) {
		/**
		 * ����û����ص�¼ 
		 *       �ļ����ش��� �������ص�ַ���� 
		 *       ����ļ������� �ɼ�������� ���
		 * �û�����ص�¼ 
		 *       �ļ��ڸ�������� ���������ص�ַ����
		 *       �����ڣ��ɼ��������
		 */
		// ���ص�¼
		if (isLocalLogin) {
			return localLogin(toUintIds, userCode);
		}else{
			return otherAreaLogin(loginLocation, toUintIds);
		}
	}
	
	
	/**
	 * �û�����ص�¼
	 * @param loginLocation
	 * @param toUintIds
	 * @return
	 */
	private static List<String[]> otherAreaLogin(String loginLocation,
			String toUintIds) {
		String firstAddress = findLocalIsHaveFile(loginLocation, toUintIds);
		// ����ļ��ڱ��ش���
		if (StringUtils.isNotBlank(firstAddress)) {
			toUintIds = reCreateToUnitIds(firstAddress, toUintIds);
			List<String[]> address = computerPlotDownLoadAddress(
					loginLocation, toUintIds);
			String[] str = new String[] { firstAddress,
					FtpXmlUtils.getUnitName(firstAddress) };
			address.add(0, str);
			return address;
		} else {
			return computerPlotDownLoadAddress(loginLocation,
					toUintIds);
		}
		
	}


	/**
	 * ���ص�¼
	 * @param loginLocation
	 * @param toUintIds
	 * @param userCode
	 * @return
	 */
	private static List<String[]> localLogin(String toUintIds, String userCode) {
		Configurater config = Configurater.getInstance();
		String systemType = config.getConfigValue("system_type");
		// ����ǵ�¼����
		if ("1".equals(systemType)) {
			// �����û�(ʡ��˾�û�����ڹ�����Χ��¼����Ϊ�����û�����)
			String country = config.getConfigValue("country");
			if (userCode.length() <= 4 || userCode.length() == 8) {
				String firstAddress = findLocalIsHaveFile(country,
						toUintIds);
				// ����ļ��ڱ��ش���
				if (StringUtils.isNotBlank(firstAddress)) {
					toUintIds = reCreateToUnitIds(firstAddress, toUintIds);
					List<String[]> address = computerPlotDownLoadAddress(
							country, toUintIds);
					String[] str = new String[] { firstAddress,
							FtpXmlUtils.getUnitName(firstAddress) };
					address.add(0, str);
					return address;
				} else {
					return computerPlotDownLoadAddress(country,
							toUintIds);
				}
				// �����û�
			} else {
				String firstAddress = findLocalIsHaveFile(userCode,
						toUintIds);
				// ����ļ��ڱ��ش���
				if (StringUtils.isNotBlank(firstAddress)) {
					toUintIds = reCreateToUnitIds(firstAddress, toUintIds);
					List<String[]> address = computerPlotDownLoadAddress(
							userCode, toUintIds);
					String[] str = new String[] { firstAddress,
							FtpXmlUtils.getUnitName(firstAddress) };
					address.add(0, str);
					return address;
				} else {
					return computerPlotDownLoadAddress(userCode,
							toUintIds);
				}
			}
		//ʡ��˾	
		}else if("3".equals(systemType)){
			String firstAddress = findLocalIsHaveFile(userCode,
					toUintIds);
			// ����ļ��ڱ��ش���
			if (StringUtils.isNotBlank(firstAddress)) {
				toUintIds = reCreateToUnitIds(firstAddress, toUintIds);
				List<String[]> address = computerPlotDownLoadAddress(
						userCode, toUintIds);
				String[] str = new String[] { firstAddress,
						FtpXmlUtils.getUnitName(firstAddress) };
				address.add(0, str);
				return address;
			} else {
				return computerPlotDownLoadAddress(userCode,
						toUintIds);
			}
		}
		return null;
	}

	/**
	 * ���ұ����Ƿ��и��ļ�
	 * 
	 * @param unitCode
	 *            ��λID
	 * @param toUnitIds
	 *            �ļ����䵽��Щ��λ
	 * @return
	 */

	private static String findLocalIsHaveFile(String unitCode, String toUnitIds) {
		String[] allDownAdds = toUnitIds.split("#");
		List<String> addDownAddList = new ArrayList<String>();
		for (String str : allDownAdds) {
			addDownAddList.add(str);
		}
		String firstDownLoadAdd = "";
		for (String str : addDownAddList) {
			if (str.equals(unitCode)) {
				firstDownLoadAdd = str;
				return firstDownLoadAdd;
			}
		}
		return null;
	}

	/**
	 * ��resourceStr��ɾ��deleteStr
	 * 
	 * @param args
	 */
	public static String reCreateToUnitIds(String deleteStr, String resourceStr) {
		String[] allDownAdds = resourceStr.split("#");
		List<String> addDownAddList = new ArrayList<String>();
		for (String str : allDownAdds) {
			addDownAddList.add(str);
		}
		addDownAddList.remove(deleteStr);
		String buffer = "";
		for (String str : addDownAddList) {
			buffer += str + "#";
		}
		return buffer;
	}

	/**
	 * �ɼ�����������ص�ַ
	 * 
	 * @param loginLocation
	 * @param toUintIds
	 * @return
	 */
	public static List<String[]> computerPlotDownLoadAddress(
			String loginLocation, String toUintIds) {
		XmlUtils util = XmlUtils.getInstance("ftp.xml");
		List<String[]> lists = new ArrayList<String[]>();
		// �����ַ���λ
		String[] ids = toUintIds.split("#");
		Document document = util.getDocument();
		for (String id : ids) {
			Element element = (Element) document
					.selectSingleNode("//ftp[@code='" + loginLocation
							+ "']/unit[@code='" + id + "']");
			if (null != element) {
				String value = element.attributeValue("value");
				// ���valueС��0��IP������
				if (Integer.parseInt(value) >= 0) {
					String[] unit = new String[3];
					unit[0] = element.attributeValue("code");
					unit[1] = element.attributeValue("name");
					unit[2] = value;
					lists.add(unit);
				}
			}
		}
		// ����
		java.util.Collections.sort(lists, new Comparator<String[]>() {
			public int compare(String[] o1, String[] o2) {
				return Integer.parseInt(o1[2]) - Integer.parseInt(o2[2]);
			}
		});

		return lists;
	}

}
