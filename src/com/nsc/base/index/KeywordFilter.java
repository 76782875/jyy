package com.nsc.base.index;

public class KeywordFilter {
	private static String firstError = "`~*)+,?����-"; // �ؼ����е�һ�������ַ�ʹ��������������ַ�
	private static String allError = "!^({}:[]������"; // �ؼ���������վλ�����ﶼ����������ַ�
	private static String linkError = "&&"; // �ؼ����������ַ��ϲ�ʹ���������ַ���
	private static String filterFirst = null;

	/**
	 * ����"&&"�����ַ�
	 * 
	 * @param keyword
	 * @return �����ꡰ&&���Ĺؼ���
	 */
	private static String filterLink(String keyword) {
		return keyword.replaceAll(linkError, "");
	}

	/**
	 * �����ַ�!^({}:[]������
	 * 
	 * @param str
	 * @return ���������������ַ��Ĺؼ���
	 */
	private static String filterAll(String str) {
		String filterFirst = filterFirst(str);
		for (int i = 0; i < filterFirst.length(); i++) {
			for (int j = 0; j < allError.length(); j++) {
				if (filterFirst != null && !"".equals(filterFirst)) {
					if (filterFirst.length() >= (i + 1)) { // ���������������ַ�Ϊ
						// *`~*)+,?����-*java!^({}:[]����
						// ��ֹ�����±�Խ��
						if (filterFirst.charAt(i) == allError.charAt(j)) {
							filterFirst = filterFirst
									.replaceAll("\\"
											+ String.valueOf(filterFirst
													.charAt(i)), "");
						}
					}
				} else
					return filterFirst;
			}
		}
		return filterFirst;
	}

	/**
	 * �ݹ鷽�������˵�һ���ַ�ʹ��������Ĺؼ���
	 * 
	 * @param filterLink
	 * @return �ݹ����
	 */
	private static String filterFirst(String filterLink) {
		String str = null;
		if (filterLink != null && !"".equals(filterLink)) {
			for (int i = 0; i < firstError.length(); i++) {
				if (filterLink.charAt(0) == firstError.charAt(i)) {
					str = filterLink.substring(1, filterLink.length());
					break;
				}
			}
			filterFirst = filterLink;
		} else {
			for (int j = 0; j < firstError.length(); j++) {
				if (filterFirst.charAt(0) == firstError.charAt(j)) {
					return "";
				}
			}
			return filterFirst;
		}
		return filterFirst(str);
	}

	/**
	 * �������ܷ������ṩ���ⲿ����
	 * 
	 * @param queryStr
	 * @return �Ѿ����˵Ĺؼ���
	 */
	public static String filterKeyword(String queryStr) {
		// �ȹ��� &&���ٹ��˵�һ���ַ�Ϊ `~*)+,?����- ����������!^({}:[]�������Ĺؼ���
		return filterAll(filterFirst(filterLink(queryStr)));
	}
}
