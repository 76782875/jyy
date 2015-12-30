package com.nsc.base.util;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.Key;
import java.security.SecureRandom;

public class DesUtil{
	
	//�õ���Ŀweb-infoĿ¼
	public static String getAppPath(){
    	String path =""; 
		try {
			path = DesUtil.class.getResource("").toURI().getPath();
		} catch (Exception e) {
			Logger.getLogger(DesUtil.class).warn("�õ���Ŀweb-infoĿ¼�����쳣:",e);
		}
    	path=path.substring(0,path.indexOf("classes"));
    	
		return path;
	}
	
	/**
	 * �õ��ļ���ǰ��·��
	 * @param filePath
	 * @return
	 */
	public static String getFilePath(String filePath){
		String temp[] = filePath.replaceAll("\\\\","/").split("/");
		String fileName = "";
		if(temp.length > 1){
		    fileName = temp[temp.length - 1];
		}
		int i = filePath.indexOf(fileName);
		filePath = filePath.substring(0, i);

		return filePath;
	}
	
	/**
	 * �õ��ļ���ǰ��·��
	 * @param filePath
	 * @return
	 */
	public static String getFileName(String filePath){
		String temp[] = filePath.replaceAll("\\\\","/").split("/");
		String fileName = "";
		if(temp.length > 1){
		    fileName = temp[temp.length - 1];
		}
		return fileName;
	}	

	/**
	 * �ѳ�����һ����Կ���浽DesKey.xml�ļ���
	 */
	public static void saveDesKey() {
		try {
			SecureRandom sr = new SecureRandom();
			// Ϊ����ѡ���DES�㷨����һ��KeyGenerator����
			KeyGenerator kg = KeyGenerator.getInstance("DES");
			kg.init(sr);
	    	String path = getAppPath();
			FileOutputStream fos = new FileOutputStream(path+"DesKey.xml");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			// ������Կ
			Key key = kg.generateKey();
			oos.writeObject(key);
			oos.close();
		} catch (Exception e) {
			Logger.getLogger(DesUtil.class).warn("�ѳ�����һ����Կ���浽DesKey.xml�ļ��з����쳣:",e);
		}
	}

	/**
	 * ���DES���ܵ���Կ���ڽ��״���Ĺ�����Ӧ�ö�ʱ�� ����Կ����ҪJCE��֧�֣����jdk�汾����1.4������Ҫ
	 * ��װjce-1_2_2��������ʹ�á�
	 * 
	 * @return Key ���ضԳ���Կ
	 */
	public static Key getKey() {
		Key kp = null;
		try {
			String path = getAppPath();
 			String fileName = path+"DesKey.xml";
			InputStream is = new FileInputStream(fileName);
			ObjectInputStream oos = new ObjectInputStream(is);
			kp = (Key) oos.readObject();
			oos.close();
		} catch (Exception e) {
			Logger.getLogger(DesUtil.class).warn("���DES���ܵ���Կ�쳣:",e);
		}
		return kp;
	}

	// �ļ�����DES�㷨�����ļ�
	/**
	 * �ļ�file���м��ܲ�����Ŀ���ļ�destFile��
	 * 
	 * @param file
	 *            Ҫ���ܵ��ļ� ��c:/test/srcFile.txt
	 * @param destFile
	 *            ���ܺ��ŵ��ļ��� ��c:/���ܺ��ļ�.txt
	 */
	public static void encrypt(String file, String destFile) throws Exception {
		File targetFile=new File(destFile);
		File encryptFolder=targetFile.getParentFile();
		
		if(!encryptFolder.isDirectory())
			encryptFolder.mkdirs();
		
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.ENCRYPT_MODE, getKey());
		InputStream is = new FileInputStream(file);
		OutputStream out = new FileOutputStream(destFile);
		CipherInputStream cis = new CipherInputStream(is, cipher);
		byte[] buffer = new byte[1024];
		int r;
		while ((r = cis.read(buffer)) > 0) {
			out.write(buffer, 0, r);
		}
		cis.close();
		is.close();
		out.flush();
		out.close();
	}

	// ���ļ�����DES�㷨�����ļ�
	/**
	 * �ļ�file���м��ܲ�����Ŀ���ļ�destFile��
	 * 
	 * @param file
	 *            �Ѽ��ܵ��ļ� ��c:/���ܺ��ļ�.txt
	 * @param destFile
	 *            ���ܺ��ŵ��ļ��� ��c:/ test/���ܺ��ļ�.txt
	 */
	public static void decrypt(String file, String dest) throws Exception {
		Cipher cipher = Cipher.getInstance("DES");
		cipher.init(Cipher.DECRYPT_MODE, getKey());
		InputStream is = new FileInputStream(file);
		OutputStream out = new FileOutputStream(dest);
		CipherOutputStream cos = new CipherOutputStream(out, cipher);
		byte[] buffer = new byte[1024];
		int r;
		while ((r = is.read(buffer)) >= 0) {
			cos.write(buffer, 0, r);
		}
		cos.flush();
		cos.close();
		out.flush();
		out.close();
		is.close();
	}

}
