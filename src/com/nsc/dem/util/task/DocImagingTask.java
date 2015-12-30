package com.nsc.dem.util.task;

import static com.nsc.base.util.DateUtils.DateToString;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;

import org.apache.commons.io.FilenameUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.nsc.base.conf.Configurater;
import com.nsc.base.task.TaskBase;
import com.nsc.base.thumbnail.ThumbnailFactory;
import com.nsc.base.thumbnail.ThumbnailInterface;
import com.nsc.base.util.Component;
import com.nsc.base.util.ContinueFTP;
import com.nsc.base.util.DesUtil;
import com.nsc.base.util.ExtractImages;
import com.nsc.base.util.FileUtil;
import com.nsc.dem.bean.archives.TDoc;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.bean.project.TProject;
import com.nsc.dem.service.archives.IarchivesService;
import com.nsc.dem.service.project.IprojectService;
import com.nsc.dem.util.log.Logger;

public class DocImagingTask extends TaskBase implements Job{

	private IarchivesService archivesService;
	private IprojectService projectService;
	private TUser user = null;
	private Logger logger = null;
	
	public DocImagingTask(String taskName, ServletContext context,long period) {
		super(taskName, context, period);
		archivesService = (IarchivesService) Component.getInstance(
				"archivesService", super.context);
		projectService = (IprojectService) Component.getInstance(
				"projectService", super.context);
		user = (TUser) archivesService.EntityQuery(TUser.class, Configurater
				.getInstance().getConfigValue("ws_user"));
		logger = projectService.getLogManager(user).getLogger(
				DocImagingTask.class);
	}
	
	public DocImagingTask(){
		super(null,Configurater.getInstance().getServletContext(),0);
		archivesService = (IarchivesService) Component.getInstance(
				"archivesService", super.context);
		projectService = (IprojectService) Component.getInstance(
				"projectService", super.context);
		user = (TUser) archivesService.EntityQuery(TUser.class, Configurater
				.getInstance().getConfigValue("ws_user"));
		logger = projectService.getLogManager(user).getLogger(
				DocImagingTask.class);
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
		String tempDir=Configurater.getInstance().getConfigValue("temp");
		tempDir = super.context.getRealPath(tempDir);

		File tempFolder = new File(tempDir);

		if (!tempFolder.isDirectory()) {
			tempFolder.mkdirs();
		}

		List<Object> dList=new ArrayList<Object>();
		
		//����
			Object[] o=new Object[]{"%%","%%"};
			//"1"��������ͼ  ������
			List<Object[]> list1=this.archivesService.creatThumbnailsTDoc(o,"1");
			for (Object[] objs : list1) {
				TDoc tn=(TDoc)objs[0];
				ThumbnailInterface thum=ThumbnailFactory.getInstance().getAbstractor(tn.getName()+"."+tn.getSuffix());
				if(thum!=null){
					dList.add(tn);
				}
			}
		
		if(dList.isEmpty()){
			String message2="ϵͳ�в�����Ҫ��������ͼ���ĵ�";
			logger.info(message2);
			return ;
		}
		
		ContinueFTP ftpUtil=null;
		int i=0;
		// �����ĵ�
		for (Object obj : dList) {
			TDoc doc = (TDoc) obj;

			// �鹤��
			TProject tPro = projectService.getProjectByDoc(doc);
			
			if(tPro==null) continue;
            //ftp�ļ����·��
			String remotePath = doc.getPath();
            //���ص����ش��·��
			String local = tempFolder.getAbsolutePath() + File.separator
					+ doc.getName() + "." + doc.getSuffix();
			File file=new File(local);
			
			ftpUtil=ContinueFTP.getInstance();
		    //����
			ftpUtil.download(remotePath, local);
			
			if(file.exists() && file.canRead()){
				
				String mimeType = Configurater.getInstance().getConfigValue("mime", FilenameUtils.getExtension(file.getName()));
				if (mimeType == null) {
					mimeType = Configurater.getInstance().getConfigValue("mime", "*");
				}
				
				String dest=local;
				
				//��ͼƬ��Ҫ����
				if(mimeType.indexOf("image")==-1){
					dest = Configurater.getInstance().getConfigValue("decrypt");
	
					File destPathFolder = new File(file.getParentFile(),dest);
					if (!destPathFolder.isDirectory())
						destPathFolder.mkdirs();
					
					dest=destPathFolder.getAbsolutePath()+File.separator+file.getName();
	
					DesUtil.decrypt(local, dest);
				}
				
				//����ͼ����·��
				String imagePath="";
				//���ù���ģʽ ��������ͼ
				ThumbnailInterface thum=ThumbnailFactory.getInstance().getAbstractor(doc.getName()+"."+doc.getSuffix());
				if(thum!=null){
					imagePath=thum.makeThumbnil(dest);
					// FTP����������ͼ·��
					
					remotePath=remotePath.replaceFirst("archives", "images");
					String ftpImagePath = remotePath.substring(0, remotePath.lastIndexOf("."))+ ".jpg";
					
					// �ϴ�����ͼ   �˷����Ѿ�������ͼFTP·���ŵ�doc��
					ExtractImages.uploadImage(doc, imagePath, ftpImagePath);
					
				    //���µ���״̬
					this.archivesService.updateEntity(doc);
					i++;
				}
				
				if (!dest.equals("")) {
					FileUtil.deleteFile(dest);
				}

				if (!local.equals("")) {
					FileUtil.deleteFile(local);
				}

				if (!imagePath.equals("")) {
					FileUtil.deleteFile(imagePath);
				}
				//1.��������ͼ
				//2.������ͼ�ϴ�
				//3.ɾ���Ѿ������ļ�
				//4.����������ͼ�� Ҫ������ͼ·�� ����
			}
		}
		String message2="���ι���������ͼ <b>"+i+"</b> ��!";
		logger.info(message2);
	}
}
