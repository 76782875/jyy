package com.nsc.dem.util.task;

import static com.nsc.base.util.DateUtils.DateToString;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletContext;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.nsc.base.conf.Configurater;
import com.nsc.base.index.FileField;
import com.nsc.base.task.TaskBase;
import com.nsc.base.util.Component;
import com.nsc.base.util.ContinueFTP;
import com.nsc.base.util.DateUtils;
import com.nsc.base.util.DesUtil;
import com.nsc.base.util.DownloadStatus;
import com.nsc.dem.bean.archives.TDoc;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.bean.project.TProject;
import com.nsc.dem.bean.system.TUnit;
import com.nsc.dem.service.archives.IarchivesService;
import com.nsc.dem.service.project.IprojectService;
import com.nsc.dem.util.index.IndexStoreUitls;
import com.nsc.dem.util.log.Logger;


/**
 * ������������
 *     ֻ��ʡ��˾�͹�������
 */
public class DocIndexingTask extends TaskBase implements Job{
	private TUser user = null;
	IarchivesService archivesService;
	IprojectService projectService;
	private Logger logger = null;
	
	public DocIndexingTask(String taskName, ServletContext context,long period) {
		super(taskName, context, period);
		archivesService = (IarchivesService) Component.getInstance("archivesService", super.context);
		projectService = (IprojectService) Component.getInstance("projectService", super.context);
		user = (TUser) archivesService.EntityQuery(TUser.class, Configurater.getInstance().getConfigValue("ws_user"));
		logger = archivesService.getLogManager(user).getLogger(
				DocIndexingTask.class);
	}
	
	public DocIndexingTask(){
		super(null,Configurater.getInstance().getServletContext(),0);
		archivesService = (IarchivesService) Component.getInstance("archivesService", super.context);
		projectService = (IprojectService) Component.getInstance("projectService", super.context);
		user = (TUser) archivesService.EntityQuery(TUser.class, Configurater.getInstance().getConfigValue("ws_user"));
		logger = archivesService.getLogManager(user).getLogger(
				DocIndexingTask.class);
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
		String tempDir = Configurater.getInstance().getConfigValue("temp");
		tempDir = super.context.getRealPath(tempDir);
		File tempFolder = new File(tempDir);
		if (!tempFolder.isDirectory()) {
			tempFolder.mkdirs();
		}

		List<Object[]> projects = projectService.getProjectByNoCreateIndex();
		if(projects == null || projects.size() <= 0)
			return;
		
		//������
		Map<Long,TProject> mainProject = new HashMap<Long,TProject>(); 
		for(Object[] obj : projects){
			TProject project = (TProject) obj[1];
			if(project  != null){
				mainProject.put(project.getId(),project);
			} else {
				project = (TProject) obj[0];
				mainProject.put(project.getId(), project);
			}
		}
		
		ContinueFTP ftpUtil = null;
		
		for(TProject project : mainProject.values()){
			List<Object> ids = new ArrayList<Object>();
			ids.add(project.getId().toString());
			
			//�õ����������µ��ӹ���ID
			for(Object[] obj : projects){
				TProject subProject = (TProject) obj[0];
				Long parentId = subProject.getParentId();
				if(parentId != null && parentId.toString().equals(project.getId().toString())){
					ids.add(subProject.getId().toString());
				}
			}
			
			List<TDoc> dList = archivesService.getDocByNoIndexProject(ids.toArray());
			
			Map<File, Map<Enum<?>, FileField>> files = new HashMap<File, Map<Enum<?>, FileField>>();
			Map<File, TDoc> docs = new HashMap<File, TDoc>();
			
			// �����ĵ�
			for (TDoc tdoc : dList) {
				int nowHour=Integer.valueOf(DateUtils.DateToString(new Date(),"HH"));
				
				//����8��ֹͣ����
				if(nowHour>8){
					//break;
				}
				
				
				String format = Configurater.getInstance().getConfigValue("format",
						tdoc.getSuffix().toLowerCase());
				
				if(format==null){
					logger.warn("δ�õ����ļ����ļ����� "+tdoc.getSuffix().toLowerCase());
					continue;
				}
				String abstractor = Configurater.getInstance().getConfigValue(
						format.toLowerCase() + "_Abstractor");

				// �޴��ĵ���������ȡ���ߣ�����
				if (abstractor == null)
					continue;

				// �鹤��
				TProject tPro = projectService.getProjectByDoc(tdoc);
				if (tPro == null)
					continue;


				String remotePath = tdoc.getPath();

				String local = tempFolder.getAbsolutePath() + File.separator
						+ tdoc.getName() + "." + tdoc.getSuffix();
				
				ftpUtil=ContinueFTP.getInstance();
				DownloadStatus status = ftpUtil.download(remotePath, local);

				if (status == DownloadStatus.Download_New_Success
						|| status == DownloadStatus.Download_From_Break_Success) {
					
					String dest = local;
					File file=new File(local);
					
					String mimeType = Configurater.getInstance().getConfigValue(
							"mime", FilenameUtils.getExtension(file.getName()));
					if (mimeType == null) {
						mimeType = Configurater.getInstance().getConfigValue(
								"mime", "*");
					}

					// ��ͼƬ��Ҫ����
					if (mimeType.indexOf("image") == -1) {
						dest = Configurater.getInstance().getConfigValue("decrypt");

						File destPathFolder = new File(file.getParentFile(), dest);
						if (!destPathFolder.isDirectory())
							destPathFolder.mkdirs();

						dest = destPathFolder.getAbsolutePath() + File.separator
								+ file.getName();

						DesUtil.decrypt(local, dest);
					}
					
					//��ȡ�ĵ�������ҵ����λ
					String unitCode = project.getTUnitByOwnerUnitId().getCode();
					TUnit unit = (TUnit) archivesService.EntityQuery(TUnit.class, unitCode);
					File f = new File(dest);
					files.put(f, archivesService.setArchivesIndex(tdoc, tPro, dest, unit));
					docs.put(f, tdoc);
					// ����ͬһ�ļ��������ڽ����ļ������δ�����ļ�
					if (!file.equals(f))
						file.delete();
				}
			}
			try {
				logger.info("�账���ļ�����: "+files.size());
				
			
				String storeLocation = IndexStoreUitls.getStoreLocation(project.getTUnitByOwnerUnitId().getCode(),
						project.getApproveUnit().getProxyCode());
				if(StringUtils.isBlank(storeLocation))
					continue;
				
				Set<File> set =archivesService.addArchiveIndex(files,storeLocation,"local");
				//������Щʧ�ܵ��ļ�
				if(set!=null){
					logger.info("ʧ���ļ�����: "+set.size());
					logger.info("ʧ���ļ�: "+set.toArray());
					Iterator<File> fileSet=set.iterator();
					
					while(fileSet.hasNext()){
						fileSet.next().delete();
					}
				}
				
			}finally {
				for (File file : files.keySet()) {
					if (file.exists() && file.canRead()){
						TDoc doc = docs.get(file);
						doc.setMetaFlag(BigDecimal.ONE);
						this.archivesService.updateEntity(doc);
						file.delete();
					}
				}
			}
			
		}
	}
	
	
}
