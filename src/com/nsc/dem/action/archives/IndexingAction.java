package com.nsc.dem.action.archives;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.nsc.base.conf.Configurater;
import com.nsc.base.index.FileField;
import com.nsc.base.util.ContinueFTP;
import com.nsc.base.util.DesUtil;
import com.nsc.base.util.DownloadStatus;
import com.nsc.dem.action.BaseAction;
import com.nsc.dem.bean.archives.TDoc;
import com.nsc.dem.bean.project.TProject;
import com.nsc.dem.bean.system.TUnit;
import com.nsc.dem.service.archives.IarchivesService;
import com.nsc.dem.service.project.IprojectService;
import com.nsc.dem.util.index.IndexStoreUitls;
import com.nsc.dem.util.task.DocIndexingTask;
import com.nsc.dem.util.task.FileReceiveTask;

public class IndexingAction extends BaseAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = -936659800363579380L;
	private IarchivesService archivesService;
	private IprojectService projectService;
	private String unitName1;
	private String unitNameCode1;
	private String projectName1;
	private String projectNameCode1;
	private String message1;
	private String indexCheckBox;

	public void setArchivesService(IarchivesService archivesService) {
		this.archivesService = archivesService;
	}

	public void setProjectService(IprojectService projectService) {
		this.projectService = projectService;
	}

	/**
	 * ���ĵ�������������
	 * 
	 * @return
	 * @throws Exception
	 */
	public String indexingArchive() throws Exception {
		
		String uCode = unitNameCode1;
		String pCode = projectNameCode1;
		if (StringUtils.isBlank(unitName1)) {
			uCode = "%%";
		}
		if (StringUtils.isBlank(projectNameCode1)) {
			pCode = "%%";
		}
		
		//������´�������  ��ɾ���ɵ������⼰�������ݿ�
		if("on".equals(indexCheckBox)){
			Object[] o1 = new Object[] { "%%", "%%", uCode, pCode };
			List<Object[]> docList = this.archivesService.creatIndexingTDoc(o1);
			
			for (Object[] obj : docList) {
				TDoc doc = (TDoc) obj[0];
				doc.setMetaFlag(BigDecimal.ZERO);
				archivesService.updateEntity(doc);
				//������洢Ŀ¼
				String storeLocation = IndexStoreUitls.getStoreLocation(doc.getId(), this.getSession().getServletContext());
				this.archivesService.deleteArchiveIndex(storeLocation,"local", doc.getId());
			}
		}
		
		//��ѯ�ѹ鵵��û�в����������ĵ�
		Object[] o = new Object[] { "%" + BigDecimal.ZERO + "%", "%01%",uCode, pCode };
		List<Object[]> lists = this.archivesService.creatIndexingTDoc(o);
		
		//�ҳ�������
		Map<Long,TProject> mainProject = new HashMap<Long,TProject>(); 
		for(Object[] obj : lists){
			TProject project = (TProject) obj[1];
			if(project.getParentId() != null){
				project = (TProject) projectService.EntityQuery(TProject.class, project.getParentId());
			}
			if(project.getParentId() == null){
				mainProject.put(project.getId(), project);
			}
		}
		
		if (mainProject.isEmpty()) {
			message1 = "ϵͳ�в�����δ���������ĵ�";
			return SUCCESS;
		}
		
		ContinueFTP ftpUtil = null;
		String tempDir = Configurater.getInstance().getConfigValue("temp");
		tempDir = super.getRealPath(tempDir);
		File tempFolder = new File(tempDir);
		if (!tempFolder.isDirectory()) {
			tempFolder.mkdirs();
		}
		
		int count = 0;
		
		for(TProject project : mainProject.values()){
			//�õ��ӹ���
			List<Object> ids = new ArrayList<Object>();
			for(Object[] obj : lists){
				TProject subProject = (TProject) obj[1];
				if(subProject.getId().toString().equals(project.getId().toString())){
					ids.add(subProject.getId().toString());
					continue;
				}
				Long parentId = subProject.getParentId();
				if( parentId != null && parentId.toString().equals(project.getId().toString())){
					ids.add(subProject.getId().toString());
				}
			}
			
			List<TDoc> dList = archivesService.getDocByNoIndexProject(ids.toArray());
			
			Map<File, Map<Enum<?>, FileField>> files = new HashMap<File, Map<Enum<?>, FileField>>();
			Map<File, TDoc> docs = new HashMap<File, TDoc>();
			
			// �����ĵ�
			for (TDoc tdoc : dList) {
				String format = Configurater.getInstance().getConfigValue("format",
						tdoc.getSuffix().toLowerCase());
				
				if(format==null){
					Logger.getLogger(DocIndexingTask.class).warn("δ�õ����ļ����ļ����� "+tdoc.getSuffix().toLowerCase());
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

				//��ȡ�ĵ�������ҵ����λ
				String unitCode = project.getTUnitByOwnerUnitId().getCode();
				
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
				Logger.getLogger(DocIndexingTask.class).info("�账���ļ�����: "+files.size());
				count = files.size();
				String storeLocation = IndexStoreUitls.getStoreLocation(project.getTUnitByOwnerUnitId().getCode(),
						project.getApproveUnit().getProxyCode());
				if(StringUtils.isBlank(storeLocation))
					continue;
				
				Set<File> set =archivesService.addArchiveIndex(files,storeLocation,"local");
				//������Щʧ�ܵ��ļ�
				if(set!=null){
					Logger.getLogger(DocIndexingTask.class).warn("ʧ���ļ�����: "+set.size());
					count -= set.size();
					Logger.getLogger(DocIndexingTask.class).warn("ʧ���ļ�: "+set.toArray());
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
		
		this.message1 = "���ι������� <b>" + count + "</b> ��";
		return SUCCESS;
	}

		
		
	public String importArchivesAction() {
		// ��ȡ�����ļ�
		Configurater config = Configurater.getInstance();

		FileReceiveTask fileTack = new FileReceiveTask("FileReceiveTask",config
				.getServletContext(), 0);
		try {
			message1 = fileTack.importArchieves();

		} catch (Exception e) {
			message1 = e.getMessage();
			Logger.getLogger(IndexingAction.class).warn("����ͬ�����̳����쳣", e);
		}

		return SUCCESS;
	}

	public String getUnitName1() {
		return unitName1;
	}

	public void setUnitName1(String unitName1) {
		this.unitName1 = unitName1;
	}

	public String getUnitNameCode1() {
		return unitNameCode1;
	}

	public void setUnitNameCode1(String unitNameCode1) {
		this.unitNameCode1 = unitNameCode1;
	}

	public String getProjectName1() {
		return projectName1;
	}

	public void setProjectName1(String projectName1) {
		this.projectName1 = projectName1;
	}

	public String getProjectNameCode1() {
		return projectNameCode1;
	}

	public void setProjectNameCode1(String projectNameCode1) {
		this.projectNameCode1 = projectNameCode1;
	}

	public String getMessage1() {
		return message1;
	}

	public void setMessage1(String message1) {
		this.message1 = message1;
	}

	public String getIndexCheckBox() {
		return indexCheckBox;
	}

	public void setIndexCheckBox(String indexCheckBox) {
		this.indexCheckBox = indexCheckBox;
	}

}
