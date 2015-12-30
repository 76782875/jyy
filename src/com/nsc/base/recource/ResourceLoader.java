package com.nsc.base.recource;

import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

/**
 * ��Դ������
 * 
 * ����������Դ�ļ��أ����ڹ������л�������ָ������Դ�������س��ֽ�����
 * 
 * @author bs-team
 *
 * @date Oct 19, 2010 10:27:51 AM
 * @version
 */
public class ResourceLoader {
	
	/**
	 * ȡ����Դ�ֽ���
	 * @param resource
	 * @param servletContext
	 * @return ��Դ�ֽ���
	 */
	public static InputStream getResourceAsStream(String resource,
												  ServletContext servletContext) {
		String stripped = resource.startsWith("/") ? resource.substring(1)
				: resource;
		InputStream stream = null;

		if (servletContext != null) {
			stream = servletContext.getResourceAsStream(resource);
		}

		if (stream == null) {
			stream = getResourceAsStream(stripped);
		}

		return stream;
	}
	
	/**
	 * ȡ����Դ�ֽ���
	 * @param path
	 * @return �ֽ���
	 */
	public static InputStream getResourceAsStream(String path){
		ClassLoader classLoader=getDefaultClassLoader();
		
		return classLoader.getResourceAsStream(path);
	}
	
	/**
	 * ȡ��Ĭ������ع���
	 * @return ����ع���
	 */
	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		}
		catch (Throwable ex) {
			// Cannot access thread context ClassLoader - falling back to system class loader...
		}
		if (cl == null) {
			// No thread context class loader -> use class loader of this class.
			cl = ResourceLoader.class.getClassLoader();
		}
		return cl;
	}
	
	/**
	  * ��ȡ��Ŀ��Ŀ¼�ľ���·��
	  * 
	  * @return ��Ŀ��Ŀ.����<br/> F:\tomcat\webapps\J2EEUtil\
	  * */
	 public static String getWebRootPath(
	   HttpServletRequest request) {
	  return request.getSession().getServletContext().getRealPath("/");
	 }
}
