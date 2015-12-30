package com.nsc.base.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

import org.directwebremoting.util.Logger;

public class PropertyModify {

	
	//���ļ�   
	public static String readFile(String filePath, String parameterName) throws IOException, FileNotFoundException {   
	    String result = null;   
	    File file = new File(filePath);   
	    if (file.exists()) {   
	        FileInputStream fis = new FileInputStream(file);   
	        byte[] b = new byte[fis.available()];   
	        fis.read(b);   
	        result = new String(b, "UTF-8");   
	        fis.close();   
	    }   
	    return result;   
	}   
	//�޸ĺ�洢   
	public static void saveFile(String content, String path, String fileName) throws IOException {
	    File f = new File(path);   
	    if (!f.exists()) {   
	        f.mkdirs();   
	    }   
	    File fn = new File(f, fileName);   
	    FileOutputStream fos = new FileOutputStream(fn);   
	    fos.write(content.getBytes());   
	    fos.close();   
	}   
	//ɾ�����ļ�   
	public static void deleteFile(String path) throws IOException {   
	    File f = new File(path);   
	    if (f.exists()) {   
	        f.delete();   
	} else {   
	        throw new IOException("δ�ҵ�����ļ�");   
	    }   
	}   
	//ִ�з���   
	public static boolean writeProperties(String filePath, String parameterName, String parameterValue) {   
	    boolean flag = false;   
	    try {   
	        //ȡ���ļ���������   
	        String all = PropertyModify.readFile(filePath, parameterName);   
	        String result = null;   
	        //��������ļ������parameterName   
	        if (all.indexOf(parameterName) != -1) {   
	            //�õ�parameterNameǰ���ֽ���   
	            int a=all.indexOf(parameterName);   
	            //ȡ����ǰparameterName��ֵ   
	            String old = readProperties(filePath, parameterName);   
	            //�õ�parameterNameֵǰ���ֽ���   
	            int b=a+(parameterName.length()+"=".length());   
	            //�µ�properties�ļ���������Ϊ���ɵ�properties�ļ�������parameterName+"="+�µ�parameterNameֵ(parameterValue)+�ɵ�properties�ļ���parameterNameֵ֮�������   
	            result=all.substring(0,a)+parameterName+"="+parameterValue+all.substring(b+ToUnicode.convert(old).length(),all.length());   
	        }   
	        //ɾ����ǰ��properties�ļ�   
	        PropertyModify.deleteFile(filePath);   
	        //�洢���ļ�����ǰλ��   
//	        String[] arrPath = filePath.split("WEB-INF");   
//	        String path = arrPath[0]+"WEB-INF/configs";
	        String path=filePath.substring(0,filePath.lastIndexOf("/"));
	        String name=filePath.substring(filePath.lastIndexOf("/")+1);
	        Logger.getLogger(PropertyModify.class).info("filePath-====================="+filePath);
	        PropertyModify.saveFile(result, path,name);   
	        flag=true;   
	} catch (IOException e) {   
    	Logger.getLogger(PropertyModify.class).warn(e.getMessage());  
	        flag=false;   
	    }   
	    return flag;   
	}   
	//��properties�ļ���Properties����   
	public static String readProperties(String filePath, String parameterName) {   
	    String value = "";   
	    Properties prop = new Properties();   
	    try {   
	        InputStream fis = new FileInputStream(filePath);   
	        prop.load(fis);   
	        value = prop.getProperty(parameterName);   
	    } catch (IOException e) {  
	    	Logger.getLogger(PropertyModify.class).warn(e.getMessage());
	    }   
	    return value;   
	}   
}
