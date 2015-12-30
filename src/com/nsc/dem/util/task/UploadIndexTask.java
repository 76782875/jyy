package com.nsc.dem.util.task;

import static com.nsc.base.util.DateUtils.DateToString;

import java.io.File;
import java.util.Date;

import javax.servlet.ServletContext;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.nsc.base.conf.Configurater;
import com.nsc.base.task.TaskBase;
import com.nsc.base.util.Component;
import com.nsc.base.util.ContinueFTP;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.service.system.IuserService;
import com.nsc.dem.util.index.FileDirUtils;
import com.nsc.dem.util.index.StoreLastModifyTime;
import com.nsc.dem.util.log.Logger;
import com.nsc.dem.util.xml.FtpXmlUtils;

/**
 * �ϴ����غ�ͬ�������⵽����FTPվ��
 *    
 *
 */
public class UploadIndexTask extends TaskBase implements Job{
	private IuserService userService;
	private TUser user = null;
	private Logger logger = null;
	
	public UploadIndexTask(String taskName, ServletContext context, long period) {
		super(taskName, context, period);
		userService = (IuserService) Component.getInstance("userService",super.context);
		user = (TUser) userService.EntityQuery(TUser.class, Configurater.getInstance().getConfigValue("ws_user"));
		logger = userService.getLogManager(user).getLogger(
				UploadIndexTask.class);
	}
	
	public UploadIndexTask(){
		super(null,Configurater.getInstance().getServletContext(),0);
		userService = (IuserService) Component.getInstance("userService",super.context);
		user = (TUser) userService.EntityQuery(TUser.class, Configurater.getInstance().getConfigValue("ws_user"));
		logger = userService.getLogManager(user).getLogger(
				UploadIndexTask.class);
	}
	
	public void execute(JobExecutionContext context)
		throws JobExecutionException {
		String taskName = context.getTrigger().getKey().getName();
		logger.info("����[ " + taskName + " ]������ "
					+ DateToString(context.getFireTime(), "yyyy-MM-dd HH:mm:ss"));
		try {
			doTask();
		} catch(Exception e){
			throw new JobExecutionException(e);
		}finally{
			logger.info("����[ "+ taskName+ " ]�´ν���"
					+ DateToString(context.getNextFireTime(),"yyyy-MM-dd HH:mm:ss") + " ����");
		}		
	}

	@Override
	public void doTask() throws Exception {
		Configurater config = Configurater.getInstance();
		String unitCode = config.getConfigValue("unitCode");
		String countryCode = config.getConfigValue("country");
		//��ȡ�������λ��
		String localDir = FileDirUtils.getRealPathByUnitId("doc_read_Dir", unitCode, "local");
		String synDir = FileDirUtils.getRealPathByUnitId("doc_read_Dir", unitCode, "syn");
		String localCon = FileDirUtils.getRealPathByUnitId("doc_content_Dir", unitCode, "local");
		String synCon = FileDirUtils.getRealPathByUnitId("doc_content_Dir", unitCode, "syn");
		
		//��¼�ļ��ϴ�ʱ������޸�ʱ��
		StoreLastModifyTime store = StoreLastModifyTime.createInstance();
		
		String[] ftpInfo = FtpXmlUtils.getFTPInfo(countryCode);
		if(ftpInfo  == null || ftpInfo.length < 4){
			logger.warn("�������FTP��Ϣ");
			return;
		}
		ContinueFTP ftp = ContinueFTP.getDownLoadInstance(ftpInfo[0], Integer.parseInt(ftpInfo[1]), ftpInfo[2], ftpInfo[3]);
		if(ftp == null){
			logger.warn("����FTP����ʧ��");
			return;
		}
		
		//�ϴ������ļ�������
		if(isUpload(store.getLocal(), new File(localDir), true)){
			for(File file : new File(localDir).listFiles()){
				ftp.upload(file.getAbsolutePath(), "write/"+unitCode+"/local"+File.separator+file.getName());
			}
			StoreLastModifyTime.createInstance().setLocal(new File(localDir).lastModified());
		}
		
		//�ϴ�ͬ��������
		if(isUpload(store.getSyn(), new File(synDir),true)){
			for(File file : new File(synDir).listFiles()){
				ftp.upload(file.getAbsolutePath(), "write/"+unitCode+"/syn"+File.separator+file.getName());
			}
			StoreLastModifyTime.createInstance().setSyn(new File(synDir).lastModified());
		}
		
		//�ϴ������ļ����ݿ�
		if(isUpload(store.getLocalContent(),new File(localCon), false)){
			for(File file : new File(localCon).listFiles()){
				ftp.upload(file.getAbsolutePath(), "content/"+unitCode+"/local"+File.separator+file.getName());
			}
			StoreLastModifyTime.createInstance().setLocalContent(new File(localCon).lastModified());
		}
		
		//�ϴ�ͬ�������ݿ�
		if(isUpload(store.getSynContent(), new File(synCon), false)){
			for(File file : new File(synCon).listFiles()){
				ftp.upload(file.getAbsolutePath(), "content/"+unitCode+"/syn"+File.separator+file.getName());
			}
			StoreLastModifyTime.createInstance().setSynContent(new File(synCon).lastModified());
		}
	}
	
	/**
	 * 
	 * @param lastModify :�ļ���һ���ϴ�ʱ������޸�ʱ��
	 * @param file       ����Ҫ��ת��Ŀ¼��
	 * @param ischeck    ���Ƿ���������м��
	 * @return
	 */
	private boolean isUpload(long lastModify ,File file, boolean ischeck){
		if(!file.exists()){
			logger.info("�����ⲻ����:"+file.getAbsolutePath());
			return false;
		}
		
		if(lastModify == 0 && file.listFiles().length > 0){
			if(ischeck){
				for(String fileName : file.list()){
					if(fileName.lastIndexOf("segments") != -1){
						logger.info("��һ���ϴ�������:"+file.getAbsolutePath());
						return true;
					}
				}
				logger.info("��������һ���������������� ,����:"+file.getAbsolutePath());
				return false;
			}
			logger.info("��һ���ϴ����ݿ�:"+file.getAbsolutePath());
			return true;
		}
		
		if(lastModify < file.lastModified() && file.listFiles().length > 0){
			if(ischeck){
				for(String fileName : file.list()){
					if(fileName.lastIndexOf("segments") != -1){
						logger.info("��ʼ�ϴ�������:"+file.getAbsolutePath());
						return true;
					}
				}
				logger.info("��������һ���������������� ,����:"+file.getAbsolutePath());
				return false;
			}else{
				logger.info("��ʼ�ϴ����ݿ�:"+file.getAbsolutePath());
			}
			return true;
		}else{
			if(ischeck){
				logger.info("������"+file.getAbsolutePath()+"�����ϴ�,��һ���޸�ʱ��:"+new Date(lastModify));
			}else{
				logger.info("���ݿ�"+file.getAbsolutePath()+"�����ϴ�,��һ���޸�ʱ��:"+new Date(lastModify));
			}
			return false;
		}
	}
}