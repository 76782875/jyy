package com.nsc.dem.action.searches;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.log4j.Logger;

import com.nsc.base.conf.Configurater;
import com.nsc.base.util.ContinueFTP;
import com.nsc.base.util.DesUtil;
import com.nsc.dem.action.BaseAction;
import com.nsc.dem.action.bean.DownFileBean;
import com.nsc.dem.bean.archives.TDoc;
import com.nsc.dem.bean.system.TOperateLog;
import com.nsc.dem.service.base.IService;
import com.nsc.dem.util.xml.FtpXmlUtils;

@SuppressWarnings("serial")
public class DownloadAction extends BaseAction{
	
	protected IService baseService;
	

	public void setBaseService(IService baseService) {
		this.baseService = baseService;
	}
	
	
	
	/**
	 * �������أ�ʹ��TCPЭ������
	 * @param file
	 * @param packageDownFileList
	 * @param downFB
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	protected void ftpDownload(List<File> packageDownFileList,DownFileBean downFB) throws IOException,
			FileNotFoundException {
		Configurater config = Configurater.getInstance();
		String temp = config.getConfigValue("temp");
		String folderPath = super.getRealPath(temp);
		File file = new File(folderPath);
		if (!file.exists()) {
			if (!file.mkdir()) {
			}
		}
		
		String[] ftpInfo = FtpXmlUtils.getFTPInfo(downFB.getCode());
		if(null == ftpInfo){
			Logger.getLogger(DownloadAction.class).warn("������"+downFB.getCode()+"FTP��Ϣ");
			return;
		}
		
		//ʡ��˾��������ʹ�õ���httpЭ�飬���Զ˿ںſ��ܴ������⡣���Ի�ȡglobal.properties
		String systemType = config.getConfigValue("system_type");
		//����
		if("1".equals(systemType.trim())){
			if(downFB.getCode().equals(config.getConfigValue("country"))){
				ftpInfo[0] = config.getConfigValue("HOSTNAME");
				ftpInfo[1] = config.getConfigValue("PORT");
				ftpInfo[2] = config.getConfigValue("USERNAME");
				ftpInfo[3] = config.getConfigValue("PASSWORD");
			}
		}else{
		    if(downFB.getCode().equals(config.getConfigValue("unitCode"))){
		    	ftpInfo[0] = config.getConfigValue("HOSTNAME");
				ftpInfo[1] = config.getConfigValue("PORT");
				ftpInfo[2] = config.getConfigValue("USERNAME");
				ftpInfo[3] = config.getConfigValue("PASSWORD");
		    }
		}
		
		
		// ȡ�ñ��صĸ�·��,����
		String local = super.getRealPath("uploads");
		String dest = Configurater.getInstance().getConfigValue("decrypt");
		File destPathFolder = new File(local, dest);
		if (!destPathFolder.isDirectory()){
			destPathFolder.mkdirs();
		}
		
		ContinueFTP ftp = ContinueFTP.getDownLoadInstance(ftpInfo[0], Integer.parseInt(ftpInfo[1]), ftpInfo[2], ftpInfo[3]);
		if (ftp != null) {
			String remotePath = downFB.getPath();
			String name = downFB.getName();
			String suffix = downFB.getSuffix();
			File newfile = new File(file, name + "." + suffix);
			
			//���ͬ���ļ�
			if(newfile.exists()){
				newfile = new File(file, downFB.getDocid()+name + "." + suffix);
			}
			TOperateLog tlog = new TOperateLog();
			tlog.setOperateTime(new Timestamp(System.currentTimeMillis()));
			tlog.setTarget(TDoc.class.getSimpleName());
			tlog.setTUser(super.getLoginUser());
			tlog.setType("L02");
			if (!newfile.exists()){
				newfile.createNewFile();
			}
			FileOutputStream fos = new FileOutputStream(newfile);
			try{
				if (ftp.loadFile(remotePath, fos)) {
					fos.close();//����ر������������Ĳ���ɾ��
					
					dest = destPathFolder.getAbsolutePath() + File.separator + newfile.getName();
					File destFile = new File(dest);
					
					if(destFile.exists()){
						dest = destPathFolder.getAbsolutePath() + File.separator +downFB.getDocid() + newfile.getName();
					}
					
					String mimeType = config.getConfigValue("mime", downFB.getSuffix().toLowerCase());
					
					if (mimeType == null) {
						mimeType = config.getConfigValue("mime", "*");
					}
					super.getResponse().setContentType(mimeType);
					
					//��ͼƬ��Ҫ����
					if(mimeType.indexOf("image")==-1){
						DesUtil.decrypt(newfile.getAbsolutePath(),dest);
						newfile.delete();
						packageDownFileList.add(new File(dest));
					}else{
						packageDownFileList.add(newfile);
					}
				}
			
		   }catch(Exception ex){
			   File tempFile = new File(destPathFolder.getAbsoluteFile(),downFB.getName()+" �ļ�������  ��ѡ�����������ص�ַ.txt"); 
			   if(!tempFile.exists())
				   tempFile.createNewFile();
			    packageDownFileList.add(tempFile);
			    logger.getLogger(DownloadAction.class).warn(ex.getMessage());
			}finally {
				if(fos!=null){
					fos.close();
				}
			}
		}
	}





	/**
	  *���� HHTP���ط�ʽ
	  * @param fileName
	  * @param packageDownFileList
	  * @param downFB
	  */
	protected void httpDownload(List<File> packageDownFileList, DownFileBean downFB) throws IOException{
				String temp = Configurater.getInstance().getConfigValue("temp");
				String folderPath = super.getRealPath(temp);
				
				InputStream bis = null;
				OutputStream out = null;
				PostMethod postMethod = null;
				
				if ( downFB.getDocid() == null || "".equals(downFB.getDocid()) ){
					Logger.getLogger(DownloadAction.class).info("��ȡ�ļ�id ʧ��!");
				}
				
				HttpClient client = new HttpClient();
				String fileService = FtpXmlUtils.getFileServerAdd(downFB.getCode());
				String action = Configurater.getInstance().getConfigValue("downAction");
				fileService += action;
				postMethod = new PostMethod(fileService);
				
				postMethod.addParameter("path", URIUtil.encodePath(downFB.getPath(),"UTF-8"));
				postMethod.addParameter("name", URIUtil.encodePath(downFB.getName(),"UTF-8"));
				postMethod.addParameter("suffix", downFB.getSuffix());
				
				// ȡ�ñ��صĸ�·��,����
				String local = super.getRealPath("uploads");
				String dest = Configurater.getInstance().getConfigValue("decrypt");

				File destPathFolder = new File(local, dest);
				if (!destPathFolder.isDirectory()){
					destPathFolder.mkdirs();
				}
				
				// �����ص��ļ��Ƚϴ� , ���ڴ������������ӳ�ʱʱ��
				client.getHttpConnectionManager().getParams().setConnectionTimeout(6000);
					
				int httpStat = 0;
				try {
					httpStat = client.executeMethod(postMethod);
					//���سɹ�
					if ((httpStat == HttpStatus.SC_OK)) {
						
						// ���ص��ļ���
						bis = postMethod.getResponseBodyAsStream();
						if(bis != null){
							String filePath = folderPath + File.separator + downFB.getName() + "." + downFB.getSuffix(); 
							File file = new File(filePath);
							//����ļ�����������
							if(file.exists()){
								filePath = folderPath + File.separator + downFB.getDocid() + downFB.getName() + "." + downFB.getSuffix();
							}
							
							file = new File(filePath);
							if(!file.exists())
								file.createNewFile();
							
							out = new FileOutputStream(file);	
						
							
							byte[] buffer = new byte[1024];
							int len;
							while ((len = bis.read(buffer)) != -1){
								out.write(buffer, 0, len);
							}
							out.flush();
							out.close();//����ر������������Ĳ���ɾ��
							
							dest = destPathFolder.getAbsolutePath() + File.separator + downFB.getDocid() + downFB.getName() + "." + downFB.getSuffix();
							
                             Configurater config = Configurater.getInstance();
							String mimeType = config.getConfigValue("mime", downFB.getSuffix().toLowerCase());
							
							if (mimeType == null) {
								mimeType = config.getConfigValue("mime", "*");
							}
							
							//��ͼƬ��Ҫ����
							if(mimeType.indexOf("image")==-1){
								DesUtil.decrypt(filePath,dest);
								file.delete();
								packageDownFileList.add(new File(dest));
							}else{
								packageDownFileList.add(file);
							}
						}
					}
				} catch (Exception e) {
					 File tempFile = new File(destPathFolder.getAbsoluteFile(),downFB.getName()+" �ļ�������  ��ѡ�����������ص�ַ.txt"); 
					   if(!tempFile.exists())
						    tempFile.createNewFile();
					    packageDownFileList.add(tempFile);
					    logger.getLogger(DownloadAction.class).warn(e.getMessage());
				}finally {
					try {
						if(bis != null)
							bis.close();
						if(out != null)
							out.close();
						if(postMethod != null) 
							postMethod.releaseConnection();
					 }catch(Exception ex){
						 logger.getLogger(DownloadAction.class).warn(ex.getMessage());
					}
					
			    }
			}
	
	
}
