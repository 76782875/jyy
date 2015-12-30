package com.nsc.base.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import com.nsc.base.recource.ResourceLoader;
/**
 * �����ļ���ȡ��
 * 
 * <p>
 * ͨ��������ظ���·���µ������ļ��� ������õ�ʵ��ģʽ��ͨ��<b>getInstance()</b>������á� Ŀǰ��֧��properties�ļ��ļ��ء�
 * <p/>
 * 
 * @author bs-team
 * 
 * @date Oct 19, 2010 9:44:15 AM
 * @version
 */
public class Configurater {

	private static Configurater config;
	private Map<String, Properties> configList;
	private ServletContext servletContext;

	private Configurater(ServletContext servletContext) {
		this.servletContext = servletContext;
		configList = new HashMap<String, Properties>();
	}

	/**
	 * ȡ��ServletContext
	 * 
	 * @return
	 */
	public ServletContext getServletContext() {
		return servletContext;
	}

	/**
	 * ���캯��
	 * 
	 * @return
	 */
	public static Configurater getInstance() {
		return getInstance(null);
	}

	/**
	 * ���캯��
	 * 
	 * @param servletContext
	 * @return
	 */
	public static synchronized Configurater getInstance(
			ServletContext servletContext) {

		if (config == null) {
			config = new Configurater(servletContext);

			try {
				config.loadConfigure(ConstConfig.GLOBAL_PROPERTIES,
						"global.properties");

				String configuers = config.getConfigValue(
						ConstConfig.GLOBAL_PROPERTIES,
						ConstConfig.CONFIGUERS_KEY);
				// �������������ļ�
				if (configuers != null) {
					String[] configs = configuers.split(",");

					for (String configPath : configs) {
						String[] names = configPath.split(":");
						if (names != null && names.length == 2) {
							String suffix = names[1].substring(names[1]
									.indexOf(".") + 1);
							if (suffix.equals("properties")) {
								config.loadConfigure(names[0], names[1]);
							} else if (suffix.equals("xml")) {
								// ����xml�����ļ�
								// config.loadXml(names[0], names[1]);
							}
						}
					}
				}
			} catch (IOException e) {
				Logger.getLogger(Configurater.class).warn(e.getMessage());
			}
		}

		return config;
	}

	/**
	 * ���������ļ�(Properties�ļ�)
	 * 
	 * @param srcPath
	 *            ��Դȫ·��
	 * @return �����ļ�����
	 * @throws IOException
	 */
	public Properties loadConfigure(String nameSpace, String srcPath)
			throws IOException {

		InputStream is = ResourceLoader.getResourceAsStream(srcPath.trim());

		Properties properties = new Properties();

		properties.load(is);

		this.configList.remove(nameSpace.trim().toUpperCase());

		this.configList.put(nameSpace.trim().toUpperCase(), properties);

		Logger.getLogger(Configurater.class).info(
				"�����ļ�: " + nameSpace + "���سɹ���");

		return properties;
	}

	/**
	 * ���������ռ�ͼ�ȡֵ
	 * 
	 * @param nameSpace
	 * @param key
	 * @return
	 */
	public String getConfigValue(String nameSpace, String key) {

		if (nameSpace != null && nameSpace.length() > 0) {
			if (configList.get(nameSpace.toUpperCase()) instanceof Properties) {
				Properties properties = (Properties) configList.get(nameSpace
						.toUpperCase());
				return properties == null ? null : properties.getProperty(key);
			}
		}
		for (String name : configList.keySet()) {
			if (configList.get(name) instanceof Properties) {
				Properties properties = (Properties)configList.get(name);
				if (properties.containsKey(key))
					return properties.getProperty(key);
			}
		}

		return null;
	}

	/**
	 * ���������ռ�ȡ��properties
	 * @param nameSpace
	 * @return
	 */
	public Properties getConfig(String nameSpace){
		if (nameSpace != null && nameSpace.length() > 0) {
			if (configList.get(nameSpace.toUpperCase()) instanceof Properties) {
				Properties properties = (Properties) configList.get(nameSpace
						.toUpperCase());
				return properties;
			}
		}
		return null;
	}
	/**
	 * ���ݼ�ȡֵ
	 * 
	 * @param key
	 * @return
	 */
	public String getConfigValue(String key) {
		return getConfigValue(null, key);
	}

	/**
	 * ���ݼ�ȡֵ
	 * 
	 * @param key
	 * @return
	 */
	public Object getConfigValueXml(String key) {
		return configList.get(key);
	}

	// public void loadXml(String name, String path) {
	// String xmlPath = ResourceLoader.getDefaultClassLoader().getResource(
	// path).getPath();
	// Document doc = XmlParse.parseXml(xmlPath);
	// configList.put(name, doc);
	// Logger.getLogger(Configurater.class).info("�����ļ�: " + name + "���سɹ���");
	// }

	/**
	 * ���ݼ�����ƥ��ȡֵ
	 * 
	 * @param regExp
	 *            key��������ʾ
	 * @return
	 */
	public List<String> like(String regExp) {
		List<String> valueList = new ArrayList<String>();

		// ѭ����key����������ƥ��key
		for (String name : configList.keySet()) {
			Properties properties =  configList.get(name);
			for (Iterator<Object> iter = properties.keySet().iterator(); iter
					.hasNext();) {
				String key = (String) iter.next();
				if (key.startsWith(regExp)) {
					valueList.add(getConfigValue(key));
				}
			}
		}
		return valueList;
	}
}
