package com.nsc.dem.action.archives;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import com.nsc.base.conf.Configurater;
import com.nsc.base.thumbnail.ThumbnailFactory;
import com.nsc.base.thumbnail.ThumbnailInterface;
import com.nsc.base.util.ContinueFTP;
import com.nsc.base.util.DeleteFileStatus;
import com.nsc.base.util.DesUtil;
import com.nsc.base.util.ExtractImages;
import com.nsc.base.util.FileUtil;
import com.nsc.dem.action.BaseAction;
import com.nsc.dem.bean.archives.TDoc;
import com.nsc.dem.bean.project.TProject;
import com.nsc.dem.service.archives.IarchivesService;
import com.nsc.dem.service.project.IprojectService;

public class ThumbnailsAction extends BaseAction {

	private static final long serialVersionUID = -936659800363579380L;
	private IarchivesService archivesService;
	private IprojectService projectService;
	private String unitName2;
	private String unitNameCode2;
	private String projectName2;
	private String projectNameCode2;
	private String message2;
	private String thumbnailsCheckBox;

	public String getMessage2() {
		return message2;
	}

	public void setArchivesService(IarchivesService archivesService) {
		this.archivesService = archivesService;
	}
	
	public void setProjectService(IprojectService projectService){
		this.projectService=projectService;
	}

	/**
	 * ��������ͼ
	 * 
	 * @return
	 * @throws Exception 
	 */
	public String generate() throws Exception {
		String tempDir=Configurater.getInstance().getConfigValue("temp");
		
		tempDir=super.getRealPath(tempDir);

		File tempFolder = new File(tempDir);

		if (!tempFolder.isDirectory()) {
			tempFolder.mkdirs();
		}

		List<Object> dList=new ArrayList<Object>();
		
		String uCode=unitNameCode2;
		String pCode=projectNameCode2;
		if("".equals(unitNameCode2) || unitNameCode2==null){
			uCode="%%";
		}
		if("".equals(projectNameCode2) || projectNameCode2==null){
			pCode="%%";
		}
		
		//����
		if(thumbnailsCheckBox==null){
			Object[] o=new Object[]{uCode,pCode};
			//"1"��������ͼ  ������
			List<Object[]> list1=this.archivesService.creatThumbnailsTDoc(o,"1");
			for (Object[] objs : list1) {
				TDoc tn=(TDoc)objs[0];
				ThumbnailInterface thum=ThumbnailFactory.getInstance().getAbstractor(tn.getName()+"."+tn.getSuffix());
				if(thum!=null){
					dList.add(tn);
				}
			}
		  }else if("on".equals(thumbnailsCheckBox)){ //��������
			  //������ѯTDoc
			  Object[] o=new Object[]{uCode,pCode};
			  //"2"��������ͼ  ����
			  List<Object[]> list=this.archivesService.creatThumbnailsTDoc(o,"2");
			  //ɾ����������ͼ
				for(int i=0;i<list.size();i++){
					Object[] obj=(Object[])list.get(i);
					TDoc doc=(TDoc) obj[0];
					
					DeleteFileStatus dfs=ContinueFTP.getInstance().deleteFile(doc.getPreviewPath());
					if(DeleteFileStatus.Delete_Remote_Success.name().equals(dfs.name())){
						
						Logger.getLogger(ThumbnailsAction.class).warn("����ͼɾ���ɹ�!");
					}else{
						Logger.getLogger(ThumbnailsAction.class).warn("����ͼɾ��ʧ��!");
					}
				}
			  //�����ļ�״̬
			  for (Object[] objs : list) {
					TDoc tn=(TDoc)objs[0];
					tn.setPreviewPath(null);
					archivesService.updateEntity(tn);
			  }
			

			
			//�õ���Ҫ��������ͼ���ļ�
			Object[] o2=new Object[]{uCode,pCode};
			//"1"��������ͼ  ������
			List<Object[]> list2=this.archivesService.creatThumbnailsTDoc(o2,"1");
			for (Object[] objs : list2) {
				TDoc tn=(TDoc)objs[0];
				ThumbnailInterface thum=ThumbnailFactory.getInstance().getAbstractor(tn.getName()+"."+tn.getSuffix());
				if(thum!=null){
					dList.add(tn);
				}
			}
		}
		
		if(dList.isEmpty()){
			message2="ϵͳ�в�����Ҫ��������ͼ���ĵ�";
			return SUCCESS;
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
		
		
		
		this.message2="���ι���������ͼ <b>"+i+"</b> ��!";
		return SUCCESS;
	}

	public String getUnitName2() {
		return unitName2;
	}

	public void setUnitName2(String unitName2) {
		this.unitName2 = unitName2;
	}

	public String getUnitNameCode2() {
		return unitNameCode2;
	}

	public void setUnitNameCode2(String unitNameCode2) {
		this.unitNameCode2 = unitNameCode2;
	}

	public String getProjectName2() {
		return projectName2;
	}

	public void setProjectName2(String projectName2) {
		this.projectName2 = projectName2;
	}

	public String getProjectNameCode2() {
		return projectNameCode2;
	}

	public void setProjectNameCode2(String projectNameCode2) {
		this.projectNameCode2 = projectNameCode2;
	}

	public String getThumbnailsCheckBox() {
		return thumbnailsCheckBox;
	}

	public void setThumbnailsCheckBox(String thumbnailsCheckBox) {
		this.thumbnailsCheckBox = thumbnailsCheckBox;
	}
}
