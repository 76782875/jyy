package com.nsc.dem.webservice.util;

import java.io.IOException;
import java.io.StringReader;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

import com.nsc.base.conf.Configurater;
import com.nsc.base.util.Component;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.service.base.IService;

public class WsUtils {
	private static IService service = (IService) Component.getInstance(
			"baseService", Configurater.getInstance().getServletContext());

	/**
	 * ��ȡ�����ļ������õ��û�
	 * 
	 * @return �û�
	 * @throws Exception
	 */
	public static TUser getWsUser() throws Exception {

		TUser user = (TUser) service.EntityQuery(TUser.class, Configurater
				.getInstance().getConfigValue("ws_user"));
		if (user != null) {

		} else {
			Logger.getLogger(WsUtils.class).error(
					"�޷���ȡ�������û�:"
							+ Configurater.getInstance().getConfigValue(
									"ws_user"));
			throw new Exception("�޷���ȡ�������û�");
		}
		return user;
	}

	/**
	 * ����Xml�ַ���
	 * 
	 * @throws IOException
	 * @throws JDOMException
	 * @throws Exception
	 */
	public static Document getDocument(String xmlStr) throws JDOMException,
			IOException {
		InputSource is = new InputSource(new StringReader(xmlStr));
		Document document = (new SAXBuilder()).build(is);
		return document;
	}


}
