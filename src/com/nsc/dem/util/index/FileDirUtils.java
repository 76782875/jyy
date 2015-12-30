package com.nsc.dem.util.index;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.nsc.base.conf.Configurater;
import com.nsc.base.recource.ResourceLoader;

public class FileDirUtils {
	
	
	/**
	 * ��ȡcontent\read\write�µ�Ŀ¼������Ҫ���ص�Ŀ¼
	 * 
	 * 
	 * @param dir: global�ļ��еģ�doc_read_Dir|doc_write_Dir|doc_content_Dir
	 * @param unitId:�û�������λID
	 */
	public static List<File> getAllLoadDirs(String dir, String code) throws URISyntaxException{
		List<String> readFiles = new ArrayList<String>(); 
		String readDir = Configurater.getInstance().getConfigValue(dir);
		// �����ļ���Ÿ�Ŀ¼
		URL readUrl = ResourceLoader.getDefaultClassLoader().getResource(readDir);
		readDir = readUrl.toURI().getPath();
		/*
		 * ����ǹ����û�����Ҫ���ر����������ͬ���������24��ʡ��˾���������⣬������26���ļ�
		 *  ������z��ͷ��ʡ��q��ͷ
		 */
		//08�ǿ�����Ա����ʱʹ��
		if(code != null && code.trim().length()==4 || code.equals("08")){
			//��ȡreadĿ¼�������ļ�
			List<String> fileList = showAllFiles(new File(readDir));
			String pattern = "z+.*local$";
			Pattern pp = Pattern.compile(pattern);
			Matcher m = null;
			//��ȡ����ʡ��˾���شʿ�Ŀ¼
			for(String str : fileList){
				m = pp.matcher(str);
				if(m.find()){
					readFiles.add(str);
				}	
			}
			
			//��ȡ�����������ͬ��������
			readFiles.add(0,readDir+"/local");
			readFiles.add(1,readDir+"/syn");
			
		 } else if( code != null && code.length() == 6){ 
			File file = new File(readDir+"/z"+code);
			if(!file.exists()){
				file.mkdirs();
			}
			List<String> fileList = showAllFiles(file);
			String pattern = "z+.*q+.*local$";
			Pattern p = Pattern.compile(pattern);
			Matcher m = null;
			//��ȡ��������ʡ��˾���شʿ�Ŀ¼
			for(String str : fileList){
				m = p.matcher(str);
				if(m.find()){
					readFiles.add(str);
				}	
			}
			//��ȡ�����������ͬ��������
			readFiles.add(0,readDir+"/z"+code+"/local");
			readFiles.add(1,readDir+"/z"+code+"/syn"); 
			
		}else if(code != null && code.length() == 8){
			File file = new File(readDir+"/z"+code.substring(0,6)+"/q"+code);
			if(!file.exists()){
				file.mkdirs();
			}
			//ʡ��˾
			List<String> fileList = showAllFiles(file);
			String pattern = "z+.*q+"+code+".*(local|syn)$";
			Pattern p = Pattern.compile(pattern);
			Matcher m = null;
			for(String str : fileList){
				m = p.matcher(str);
				if(m.find()){
					readFiles.add(str);
				}	
			}
			
		}
		//to File
		List<File> dirs = new ArrayList<File>();
		for(String path : readFiles){
			File file = new File(path);
			if(!file.exists())
				file.mkdirs();
			dirs.add(file);
		}
		return dirs;
	}
	
	
	//��ȡwordĿ¼
	public static File getWordFile() throws URISyntaxException{
		String wordDir = Configurater.getInstance().getConfigValue("doc_word_Dir");
		URL wordUrl = ResourceLoader.getDefaultClassLoader().getResource(wordDir);
		wordDir = wordUrl.toURI().getPath();
		File file = new File(wordDir);
		if(! file.exists()){
			file.mkdirs();
		}
		return file;
	}
	
	
	
	/**
	 * ����code��ȡ��code��Ӧ�ļ���Ŀ¼���λ��
	 * 
	 * @param dir  gobal.properties�ļ��е�doc_read_Dir ��
	 * @param code ��λID
	 * @param folder �ļ��� local����syn
	 * @return ���磺z080118/q08011801/local
	 * @throws URISyntaxException
	 */
	public static String getDirByUnitId(String dir, String code, String folder) throws URISyntaxException{
		if(code != null && code.trim().length()==4 || code.equals("08")){
			return "/"+folder;
		 } else if( code != null && code.length() == 6){ 		
			return "/z"+code+"/"+folder;
		}else if(code != null && code.length() == 8){
			//ʡ��˾
			String zone = code.substring(0,6);
			return "/z"+zone+"/q"+code+"/"+folder;
		}
		
		return null;
	}
	
	
	
	/**
	 * ����code��ȡ��code��Ӧ�ļ���Ŀ¼����λ��
	 * 
	 * @param dir  gobal.properties�ļ��е�doc_read_Dir ��
	 * @param code ��λID
	 * @param folder �ļ��� local����syn
	 * @return ���磺c:/seroot/wirte/z080118/q08011801/local
	 * @throws URISyntaxException 
	 * @throws URISyntaxException
	 */
	public static String getRealPathByUnitId(String dir, String code, String folder) throws URISyntaxException{
		String fileDir = Configurater.getInstance().getConfigValue(dir);
		URL docUrl = ResourceLoader.getDefaultClassLoader().getResource(fileDir);
		fileDir = docUrl.toURI().getPath();
		//��ȡ��Բ���·��
		String relative = getDirByUnitId(dir, code, folder);	
		return fileDir +  relative;
	}
	
	
	public static String getClassPath() throws URISyntaxException{
		String readDir = Configurater.getInstance().getConfigValue("doc_content_Dir");
		// �����ļ���Ÿ�Ŀ¼
		URL readUrl = ResourceLoader.getDefaultClassLoader().getResource(readDir);
		
		String path = readUrl.toURI().getPath();
		
		return path.substring(0,path.lastIndexOf(readDir));
	}
	
	
	/**
	 * �ݹ�õ�һ��Ŀ¼�µ������ļ����ļ���
	 * @param dir
	 * @return
	 * @throws Exception
	 */
	private  static List<String> showAllFiles(File dir) {
		  File[] fs = dir.listFiles();
		  List<String> fileNames = new ArrayList<String>();
		  for(int i=0; i<fs.length; i++){
			  fileNames.add(fs[i].getAbsolutePath());
			  if(fs[i].isDirectory()){
		          List<String> list = showAllFiles(fs[i]);
		          for(String str : list){
		        	  fileNames.add(str);
		          }
		      }
		}
		  return fileNames;
	}
	
	/**
	 * ����write�ҵ�read
	 * @param writeDir
	 * @return
	 */
	public static File getReadFileByWriteFile(File writeDir){
		String path = writeDir.getAbsolutePath();
		String regex = File.separator + "seroot" + File.separator+ "dic" + File.separator + "write";
		String replacement = File.separator + "seroot"+ File.separator + "dic" + File.separator + "read";
		path = path.replace(regex, replacement);
		File file = new File(path);
		if(! file.exists()){
			file.mkdirs();
		}
		return file;
	}
}
