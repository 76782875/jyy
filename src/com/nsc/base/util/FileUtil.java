package com.nsc.base.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipOutputStream;
import org.springframework.util.StringUtils;

import com.nsc.base.jsf.util.FacesUtils;
import com.nsc.base.recource.ResourceLoader;

/**
 * �ļ�������
 * 
 * ���������ļ��ĳ��ò��������翽�������еȡ�
 * 
 * @author bs-team
 * 
 * @date Oct 19, 2010 10:34:53 AM
 * @version
 */
public class FileUtil {

	private static String temp = "";

	static {
		try {
			URL classUrl = FileUtil.class.getResource("");
			temp = classUrl.getPath();
			temp = temp.substring(0, temp.indexOf("classes") + 7);
		} catch (Exception e) {
			temp = "";
		}

	}

	/**
	 * �����ļ�
	 * 
	 * @param oldFilePath
	 *            ԭ�ļ�·��
	 * @param newFilePath
	 *            ���ļ�·��
	 */
	public static void copyFile(String oldFilePath, String newFilePath) {
		File oldFile = new File(oldFilePath);
		File newFile = new File(newFilePath);
		File folderPath = new File(newFilePath.substring(0, newFilePath.lastIndexOf("\\")));
		try {
			if (oldFile.exists()) {
				
				//����Ŀ¼
				if (!folderPath.isDirectory()) {
					folderPath.mkdirs();
				}
				
				if(newFile.exists()) {
					newFile.delete();
				}
				newFile.createNewFile();
				
				FileInputStream fis = new FileInputStream(oldFile);
				FileOutputStream out = new FileOutputStream(newFile);
				int length = 0;
				byte[] buffer = new byte[1444];
				while ((length = fis.read(buffer)) != -1) {
					out.write(buffer, 0, length);
				}
				fis.close();
				out.close();
			}
		} catch (IOException e) {
			Logger.getLogger(FileUtil.class).warn("�ļ������쳣:",e);
		}

	}

	/**
	 * sourceFile���Ƶ�newFile
	 * 
	 * @param sourceFile
	 *            ԭ�ļ�����
	 * @param newFile
	 *            ���ļ�����
	 */
	public static void newFile(File sourceFile, File newFile) {

		try {
			FileInputStream fis = new FileInputStream(sourceFile);
			FileOutputStream out = new FileOutputStream(newFile);
			int bytes = 0;
			int bytesum = 0;
			int byteread = 0;
			byte[] buffer = new byte[1444];
			while ((bytes = fis.read(buffer)) != -1) {
				bytesum += byteread;
				out.write(buffer, 0, bytes);
			}
			fis.close();
			out.close();
		} catch (Exception e) {
			Logger.getLogger(FileUtil.class).warn("�ļ������쳣:",e);
		}

	}

	/**
	 * ɾ���ļ�
	 * 
	 * @param filePathAndName
	 *            �ļ�����·��
	 */
	public static void deleteFile(String filePathAndName) {
		try {
			File delFile = new File(filePathAndName);
			if (delFile.exists())
				delFile.delete();
		} catch (Exception e) {
			Logger.getLogger(FileUtil.class).warn("ɾ���ļ��쳣:",e);
		}
	}

	/**
	 * �ƶ��ļ�����ԭ�ļ�ɾ��
	 * 
	 * @param oldFilePath
	 *            �ļ�ԭ·��
	 * @param newFilePath
	 *            �ļ���·��
	 */
	public static void moveFile(String oldFilePath, String newFilePath) {
		copyFile(oldFilePath, newFilePath);
		deleteFile(oldFilePath);
	}

	/**
	 * �ƶ�����ļ�
	 * 
	 * @param filePaths
	 */
	public static void moveFiles(String filePaths) {
		String basePath;
		String[] filePath;
		String tempFilePath;
		String newFilePath;
		basePath = FacesUtils.getBasePath();
		filePath = filePaths.split(";");
		for (int i = 0; i < filePath.length; i++) {
			tempFilePath = StringUtils.replace(basePath + "temp\\file\\"
					+ filePath[i], "/", "\\");
			newFilePath = StringUtils.replace(basePath + "source\\file\\"
					+ filePath[i], "/", "\\");
			FileUtil.moveFile(tempFilePath, newFilePath);
		}
	}

	/**
	 * �ƶ��ļ�
	 * 
	 * @param lineCode
	 * @param filePath
	 */
	public static void moveFileTo(String lineCode, String filePath) {
		String basePath;
		String tempFilePath;
		String newFilePath;
		basePath = FacesUtils.getBasePath();
		tempFilePath = StringUtils.replace(
				basePath + "temp\\file\\" + filePath, "/", "\\");
		newFilePath = StringUtils.replace(basePath + "source\\file\\"
				+ lineCode + "\\" + filePath, "/", "\\");

		FileUtil.moveFile(tempFilePath, newFilePath);
	}

	/**
	 * ���ļ����ص��ͻ��������
	 * 
	 * @param fileName
	 * @param filePath
	 * @param request
	 * @param response
	 * @throws IOException
	 */
	public static void download(String fileName, String filePath,
			String contentType, boolean inline, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		OutputStream outp = null;
		FileInputStream br = null;

		int len = 0;
		try {
			File source = new File(filePath);
			br = new FileInputStream(source);
			response.reset();
			outp = response.getOutputStream();
			response.setContentType(contentType);
			response.setContentLength((int) source.length());

			String header = (inline ? "inline" : "attachment") + ";filename="
					+ new String(fileName.getBytes(), "ISO8859-1");

			response.addHeader("Content-Disposition", header);

			byte[] buf = new byte[1024];
			while ((len = br.read(buf)) != -1) {
				outp.write(buf, 0, len);
			}

			outp.flush();

		} finally {
			if (br != null)
				br.close();
		}
	}

	/**
	 * ����ļ���ʽ
	 * 
	 * @param fileName
	 * @return
	 */
	public static String getFileFormat(String fileName) {
		String suffix = FilenameUtils.getExtension(fileName);
		
		Properties p=PropertyReader.getProperties("fileformat.properties");
		return p.getProperty(suffix.toLowerCase(), "UNKNOWN");
	}

	/**
	 * ���ļ������ָ��������
	 * 
	 * @param files
	 *            ��������ļ�
	 * @param out
	 *            ��ָ�������
	 * @throws IOException
	 * @throws Exception
	 */
	public static void getPackAgeDownLoad(File[] files, HttpServletResponse response)
			throws IOException {
		
		ServletOutputStream out=response.getOutputStream();
		ZipOutputStream zipout = new ZipOutputStream(out);

		zipout.setLevel(1);

		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.canRead()) {
				try {
					String filename = new String(file.getName().getBytes(),
							"GBK");
					zipout.putNextEntry(new ZipEntry(filename));
				} catch (IOException e) {
					Logger.getLogger(FileUtil.class).warn("�ļ����ʱ�쳣:",e);
				}
				BufferedInputStream fr = new BufferedInputStream(
						new FileInputStream(file));
				int b;
				while ((b = fr.read()) != -1)
					zipout.write(b);
				fr.close();
				zipout.closeEntry();
			}
			if (file.exists()) {
				file.delete();
			}
		}

		zipout.finish();
		zipout.flush();
//		out.flush();

	}

	// ��ȡ����classesĿ¼
	public static String getClassesPath() {
		return temp;
	}

	// ��ȡ����web-infĿ¼
	public static String getWebInfPath() {
		return temp.substring(0, temp.lastIndexOf("/"));
	}

	// ��ȡ���̸�Ŀ¼
	public static String getWebRootPath() {
		return getWebInfPath().substring(0, getWebInfPath().lastIndexOf("/"));
	}

	/**
	 * ���ļ���Сת����ͨ����ʽ
	 * 
	 * @param fileSize
	 *            �ļ���С
	 * @return
	 */
	public String getHumanSize(long fileSize) {
		// ����λ��
		DecimalFormat df = new DecimalFormat("#.##");
		String[] units = new String[] { "�ֽ�", "KB", "MB", "GB" };
		int i = 0;
		double size = fileSize;
		while (size > 102) {
			size = size / 1024;
			i++;
		}
		return (df.format(size)) + units[i];
	}

	/**
	 * �õ�ϵͳ�ļ����·�� path �ļ���
	 * 
	 * @throws URISyntaxException
	 */
	public static String relPath(String path) throws URISyntaxException {
		URL docUrl = ResourceLoader.getDefaultClassLoader().getResource(path);
		String docDir = docUrl.toURI().getPath();
		return docDir;
	}
	/**
	 * 
	 * @param str
	 * @return ��������������ַ�
	 * @throws PatternSyntaxException
	 */
	public static String folderPathFilter(String str) throws PatternSyntaxException {
		// ֻ������ĸ������
		// String regEx = "[^a-zA-Z0-9]";
		// ��������������ַ�
		String regEx = "[:*?\"<>|/\\\\]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(str);
		
		return m.replaceAll("_").trim();
	}
}
