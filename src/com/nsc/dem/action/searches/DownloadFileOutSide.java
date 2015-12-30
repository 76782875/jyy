package com.nsc.dem.action.searches;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.ServletActionContext;

import com.nsc.base.conf.Configurater;
import com.nsc.base.util.ContinueFTP;
import com.nsc.base.util.FileUtil;
import com.nsc.dem.action.bean.DownFileBean;
import com.nsc.dem.util.xml.DownLoadXmlUtils;
import com.nsc.dem.util.xml.FtpXmlUtils;
import com.nsc.dem.util.xml.IntenterXmlUtils;
import com.nsc.dem.webservice.client.WSClient;


/**
 *  ������������û�
 *      �������
 *      ���ص����ļ�
 */
@SuppressWarnings("serial")
public class DownloadFileOutSide extends DownloadAction {
	private String username;// �û���
	private String password;// ����
	private String fileName;// �ļ����������û���ȡ�ļ�
	private String userCode;// �����������Ǹ���λ
	private String packageName;// ����

	
	/**
	 * ת������
	 * @return
	 * @throws Exception
	 */
	public void packAgeDownByOtherArea() throws Exception{
		HttpServletRequest request = super.getRequest();
		request.setCharacterEncoding("UTF-8");
		username = request.getParameter("username");
		password = request.getParameter("password");
		userCode = request.getParameter("unitCode");
		fileName = request.getParameter("filename");
		packageName =  new String(request.getParameter("packageName").getBytes("UTF-8"),"utf-8");
		
		if(StringUtils.isBlank(username) || StringUtils.isBlank(password) 
				    || StringUtils.isBlank(userCode) || StringUtils.isBlank(fileName)){
			return;
		}
		
		if (StringUtils.isBlank(packageName)) {
			packageName = new Date().toString();
		}
		
		// ��zip�ĸ�ʽ����
		ServletActionContext.getResponse().setContentType("application/zip");
		ServletActionContext.getResponse().setHeader("Content-Disposition",
													"attachment;filename=\"" + new String(packageName.getBytes(), "ISO-8859-1") + ".rar\""); 
		
		List<File> packageDownFileList = new ArrayList<File>(); //��������б�
		
		Configurater config = Configurater.getInstance();
		String unitCode = config.getConfigValue("unitCode");
		String pwd = config.getConfigValue("wspwd");
		
		// ��ȡ������webservice��ַ
		String url = IntenterXmlUtils.getWSURL(userCode);
		
		if (StringUtils.isNotBlank(url)) {
			// ͨ��webserive���û�������֤�������֤�ɹ��������û���Ҫ�����ļ����б�
			WSClient client = WSClient.getClient(url);
			String downloadFileList = client.getService().getDownLoadFile(
					username, password, fileName,pwd);
			
			//��ʧ�ܺ󷵻�null
			if(StringUtils.isBlank(downloadFileList)){
				return ;
			}
			
			List<DownFileBean> downFileList = DownLoadXmlUtils.parserDownloadList(downloadFileList);					
			for(DownFileBean downFB : downFileList){
				
				//�����ļ�����ʹ��Э������
				String protocol = FtpXmlUtils.getProtocol(downFB.getCode());
				if(downFB.getCode().equals(unitCode)){
					protocol = "FTP";
				}
				if("FTP".equals(protocol)){
					try{
						ftpDownload(packageDownFileList,downFB);
					}catch(Exception e){
						logger.getLogger(DownloadFileOutSide.class).warn(e.getMessage());
					}	
					continue;
				}else{
					try{
						httpDownload(packageDownFileList, downFB);
					}catch(Exception e){
						logger.getLogger(DownloadFileOutSide.class).warn(e.getMessage());
					}	
				}
			}
		}
			
		FileUtil.getPackAgeDownLoad(packageDownFileList.toArray(new File[0]),
				ServletActionContext.getResponse());
	}




	/**
	 * ������������ã�HTTP��ʽ���أ����ص��ļ�һ���Ǳ����ļ�
	 *    ��������
	 * @throws Exception
	 */
	public void getDownloadFileStream()throws Exception {
		HttpServletRequest  request  = super.getRequest();
		request.setCharacterEncoding("UTF-8");
		String path  = URIUtil.decode(request.getParameter("path"),"UTF-8");
		String name = URIUtil.decode(request.getParameter("name"),"UTF-8");
		String suffix = request.getParameter("suffix");
		this.fileName = name + "." + suffix;
		Configurater config = Configurater.getInstance();
		String temp = config.getConfigValue("temp");
		String folderPath = super.getRealPath(temp);
		
		File fileDir = new File(folderPath);
		if(!fileDir.exists()){
			fileDir.mkdirs();
		}
		String local = folderPath + File.separator + fileName;
		
		File file = new File(local);
		if(file.exists()){
			 file.delete();
		}
		String hostname = config.getConfigValue("HOSTNAME");
		int port = Integer.parseInt(config.getConfigValue("PORT"));
		String username = config.getConfigValue("USERNAME");
		String password = config.getConfigValue("PASSWORD");
				
		ContinueFTP ftp = ContinueFTP.getDownLoadInstance(hostname, port, username, password);
		// �ж�ftp�Ƿ����ӳɹ���������ӳɹ�����������ط���
		if (ftp != null) {
			ftp.download(path, local);
			if(file.exists()){
				try{
					String mimeType = config.getConfigValue("mime", suffix.toLowerCase());
					if (mimeType == null) {
						mimeType = config.getConfigValue("mime", "*");
					}
					super.getResponse().setContentType(mimeType);
					FileUtil.download(fileName, file.getAbsolutePath(), mimeType,false,super.getRequest(),super.getResponse());
				}finally{
					file.delete();
				}
			}
		}
	}

	
	
}
