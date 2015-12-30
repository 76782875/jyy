package com.nsc.dem.webservice.archive;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.xwork.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import com.nsc.base.conf.Configurater;
import com.nsc.base.hibernate.CurrentContext;
import com.nsc.base.hibernate.GeneratorFactory;
import com.nsc.base.util.ContinueFTP;
import com.nsc.base.util.DownloadStatus;
import com.nsc.base.util.FileUtil;
import com.nsc.dem.bean.archives.FileInfo;
import com.nsc.dem.bean.archives.TDoc;
import com.nsc.dem.bean.archives.TDocType;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.bean.project.ChildProject;
import com.nsc.dem.bean.project.MainProject;
import com.nsc.dem.bean.project.TDocProject;
import com.nsc.dem.bean.project.TDocProjectId;
import com.nsc.dem.bean.project.TPreDesgin;
import com.nsc.dem.bean.project.TProject;
import com.nsc.dem.bean.system.TDictionary;
import com.nsc.dem.service.archives.IarchivesService;
import com.nsc.dem.util.filestore.FileStoreLocation;
import com.nsc.dem.util.log.Logger;
import com.nsc.dem.webservice.util.ApplicationContext;

/**
 * FTP�ϴ�����
 * 
 * @author Administrator
 * 
 */
public class ArchiveUpdater {
	private IarchivesService service;

	private TUser user = null;

	private Logger logger = null;

	private List<String> errorFileList = null;

	private Map<String, List<String>> errorMap = new HashMap<String, List<String>>();

	public ArchiveUpdater() {
		errorFileList = new ArrayList<String>();
		service = (IarchivesService) ApplicationContext.getInstance()
				.getApplictionContext().getBean("archivesService");
		String loginId = Configurater.getInstance().getConfigValue("ws_user");
		user = (TUser) service.EntityQuery(TUser.class, loginId);
		logger = service.getLogManager(user).getLogger(ArchiveUpdater.class);
		CurrentContext.putInUser(user);
	}

	private FileInfo getFileInfo(FileInfo fi) {
		File file = new File(fi.getFilePath());

		// fi.setFilename(file.getName());
		// ���ý�Ҫ���浽���صġ����汾�ŵ��ļ�·����Ϣ
		fi.setLocalFolder(getLocalPath(file.getParent()));
		fi.setLocalFileName(fi.getFilename());
		fi.setLocalPath((fi.getLocalFolder() + File.separator + fi
				.getLocalFileName()).replaceAll("%20", " "));
		fi.setTargetPath(fi.getLocalPath().substring(
				fi.getLocalPath().indexOf("archieve")));
		return fi;
	}

	private FileInfo getFileInfoByHttp(FileInfo fi) {
		fi.setLocalFolder(getLocalPath(File.separator + fi.getFolderPath()));
		fi.setLocalFileName(fi.getFilename());
		fi.setLocalPath((fi.getLocalFolder() + File.separator + fi
				.getLocalFileName()).replaceAll("%20", " "));
		fi.setTargetPath(fi.getLocalPath().substring(
				fi.getLocalPath().indexOf("archieve")));
		return fi;
	}

	/**
	 * 
	 * @param mainProject
	 *            ������ MainProject
	 * @param project
	 *            ����ʵ�� TProject
	 * @param fi
	 *            �ĵ���Ϣ FileInfo
	 * @throws Exception
	 */
	private void saveFile(MainProject mainProject, TProject project, FileInfo fi)
			throws Exception {
		File file = null;
		fi = this.getFileInfo(fi);
		try {
			ContinueFTP ftpClient = ContinueFTP.getDownLoadInstance(mainProject
					.getFtpserver(), mainProject.getFtpPort() == null ? 21
					: Integer.parseInt(mainProject.getFtpPort()), mainProject
					.getFtpuser(), mainProject.getFtppwd());
			File target = new File(fi.getLocalFolder());
			if (!target.exists()) {
				target.mkdirs();
			}

			// ��FTP�����ĵ�
			DownloadStatus status = ftpClient.download(fi.getFilePath(), fi
					.getLocalPath());
			ftpClient.disconnect();

			this.decryptFile(fi);

			// HTTP���ط�ʽ
			// downloadFile(mainProject, fi);
			if (!status.name().equals(
					DownloadStatus.Download_From_Break_Success.name())
					&& !status.name().equals(
							DownloadStatus.Download_New_Success.name())
					&& !status.name().equals(
							DownloadStatus.Local_Bigger_Remote.name())) {
				logger.error("�ļ�ϵͳ", "����ͬ��", "�������ļ�: " + fi.getFilePath()
						+ " ʱ��������" + status.name());
				errorFileList.add(fi.getFileId());
				return;
			}
			// �����ļ����Ͳ�ͬ�ֱ���
			file = new File(fi.getLocalPath());
			String distFolder = fi.getTargetPath();
			distFolder = distFolder.replaceAll("\\\\", "/");
			logger.info("Distination Folder is:" + distFolder);

			Timestamp now = new Timestamp(System.currentTimeMillis());
			String suffix = FilenameUtils.getExtension(file.getName());
			if (suffix == null || suffix.trim().length() == 0) {
				suffix = "UNKNOWN";
			}

			TDoc doc = new TDoc();
			doc.setCreateDate(now);
			doc.setTUser(user);

			FileInputStream fis = new FileInputStream(file);

			doc.setFileSize(new BigDecimal(fis.available()));
			doc.setFormat(FileUtil.getFileFormat(file.getName()));
			doc.setSuffix(suffix);

			String fileName = file.getName();
			if (file.getName().lastIndexOf(".") != -1) {
				fileName = file.getName().substring(0,
						file.getName().lastIndexOf("."));
			}

			doc.setName(fileName.replaceAll("'", ""));
			doc.setPath(file.getPath());
			//�ļ��洢λ��
			doc.setStoreLocation(FileStoreLocation.getStoreLocation());
			
			TDocType docType = this.getDocType(fi, project);

			doc.setTDocType(docType);
			doc.setSecurity(docType.getDftSecurity() == null ? "S34" : docType
					.getDftSecurity());
			doc.setMetaFlag(new BigDecimal(0));
			doc.setStatus("01");

			// service.insertEntity(doc);
			// doc = (TDoc)service.EntityQuery(doc).get(0);

			TDocProject docPro = new TDocProject();

			TDocProjectId dp_id = new TDocProjectId();
			dp_id.setTDoc(doc);
			dp_id.setTProject(project);

			docPro.setId(dp_id);
			docPro.setRemark(project.getName() + doc.getTDocType().getName());

			TPreDesgin preDesgin = new TPreDesgin();
			preDesgin.setDocId(doc.getId());
			preDesgin.setCreateDate(new Date());
			preDesgin.setTDoc(doc);
			preDesgin.setTUnit(project.getTUnitByDesignUnitId());

			service.insertArchives(doc, docPro, preDesgin, project, null, null,
					docType, user.getLoginId(), fi.getLocalPath());
			// myFTPClient.uploadFile(file, distFolder);

			// file.delete();
		} catch (Exception ex) {
			logger.error("TDoc", "L06",
					"�ڴ����ļ� " + fi.getFilePath() + " ʱ��������", ex);
			errorFileList.add(fi.getFileId());
		}
	}

	/**
	 * 
	 * @param mainProject
	 *            ������ MainProject
	 * @param childProject
	 *            ����� ChildProject
	 * @param project
	 *            ����ʵ�� TProject
	 * @param fi
	 *            �ĵ���Ϣ FileInfo
	 * @throws Exception
	 */
	private void saveFile(MainProject mainProject, ChildProject childProject,
			TProject project, FileInfo fi) {
		File file = null;
		fi = this.getFileInfo(fi);

		try {
			ContinueFTP ftpClient = ContinueFTP.getDownLoadInstance(mainProject
					.getFtpserver(), mainProject.getFtpPort() == null ? 21
					: Integer.parseInt(mainProject.getFtpPort()), mainProject
					.getFtpuser(), mainProject.getFtppwd());
			File target = new File(fi.getLocalFolder());
			if (!target.exists()) {
				target.mkdirs();
			}
			// ��FTP�����ĵ�
			DownloadStatus status = ftpClient.download(fi.getFilePath(), fi
					.getLocalPath());
			ftpClient.disconnect();

			this.decryptFile(fi);

			// http��ʽ���ص��ĵ�����Ҫ����
			// downloadFile(mainProject, fi);

			if (!status.name().equals(
					DownloadStatus.Download_From_Break_Success.name())
					&& !status.name().equals(
							DownloadStatus.Download_New_Success.name())
					&& !status.name().equals(
							DownloadStatus.Local_Bigger_Remote.name())) {
				logger.error("�ļ�ϵͳ", "����ͬ��", "�������ļ�: " + fi.getFilePath()
						+ " ʱ��������" + status.name());
				errorFileList.add(fi.getFileId());
				return;
			}

			// �����ļ����Ͳ�ͬ�ֱ���
			file = new File(fi.getLocalPath().replaceAll("%20", " "));
			String distFolder = fi.getTargetPath();
			distFolder = distFolder.replaceAll("\\\\", "/");
			logger.info("Distination Folder is:" + distFolder);

			Timestamp now = new Timestamp(System.currentTimeMillis());
			String suffix = FilenameUtils.getExtension(file.getName());
			if (suffix == null || suffix.trim().length() == 0) {
				suffix = "UNKNOWN";
			}

			TDoc doc = new TDoc();
			doc.setCreateDate(now);
			doc.setTUser(user);

			FileInputStream fis = new FileInputStream(file);

			doc.setFileSize(new BigDecimal(fis.available()));
			doc.setFormat(FileUtil.getFileFormat(file.getName()));
			doc.setSuffix(suffix);

			String fileName = file.getName();
			if (file.getName().lastIndexOf(".") != -1) {
				fileName = file.getName().substring(0,
						file.getName().lastIndexOf("."));
			}
			doc.setName(fileName.replaceAll("'", ""));
			doc.setPath(file.getPath());

			// ����ĵ�����
			TDocType docType = this.getDocType(fi, project);

			doc.setTDocType(docType);
			doc.setSecurity(docType.getDftSecurity() == null ? "S34" : docType
					.getDftSecurity());
			doc.setMetaFlag(new BigDecimal(0));
			doc.setStatus("01");
			//�ļ��洢λ��
			doc.setStoreLocation(FileStoreLocation.getStoreLocation());
			// service.insertEntity(doc);
			// doc = (TDoc)service.EntityQuery(doc).get(0);

			TDocProject docPro = new TDocProject();
			TDocProjectId dp_id = new TDocProjectId();
			dp_id.setTDoc(doc);
			dp_id.setTProject(project);
			docPro.setId(dp_id);
			docPro.setRemark(project.getName() + doc.getTDocType().getName());

			TPreDesgin preDesgin = new TPreDesgin();
			preDesgin.setDocId(doc.getId());
			preDesgin.setCreateDate(new Date());
			preDesgin.setTDoc(doc);
			preDesgin.setTUnit(project.getTUnitByDesignUnitId());

			service.insertArchives(doc, docPro, preDesgin, project, null, null,
					docType, user.getLoginId(), fi.getLocalPath().replaceAll(
							"%20", " "));
			// myFTPClient.uploadFile(file, distFolder);

			// file.delete();
		} catch (Exception ex) {
			logger.error("TDoc", "L06",
					"�ڴ����ļ� " + fi.getFilePath() + " ʱ��������", ex);
			errorFileList.add(fi.getFileId());
		}
	}

	// �������ص��ĵ�
	private void decryptFile(FileInfo fi) {
		File file = new File(fi.getLocalPath());
		long time1 = System.currentTimeMillis();
		logger.info("�����ĵ���ʼ....");
		try {
			booway.jssys.encrypt.EdrpFileEncrypt.decrypt(file);
		} catch (Exception e) {
			logger.warn("����BOOTWAY�����ĵ������쳣!");
			errorFileList.add(fi.getFileId());
		}
		long time2 = System.currentTimeMillis();
		logger.info("�����ĵ����!����ʱ��" + ((time2 - time1) / 1000) + "��");
	}

	@SuppressWarnings("unchecked")
	private TDocType getDocType(FileInfo fi, TProject project)
			throws IllegalAccessException, InvocationTargetException,
			NoSuchMethodException {
		// ����ĵ�����
		TDocType docType = new TDocType();
		Criteria ct = service.getSession().getSessionFactory().openSession()
				.createCriteria(TDocType.class);
		ct.add(Restrictions.eq("name", fi.getFolderPath()));
		ct.add(Restrictions.like("code", project.getType(), MatchMode.START));
		ct.add(Restrictions.isNotNull("parentCode"));
		List<Object> docTypeList = ct.list();
		if (docTypeList.size() > 0) {
			docType = (TDocType) docTypeList.get(0);
		} else { // ����û�У����뵽����
			String type = "";
			TDictionary dictionary = new TDictionary();
			dictionary.setName(fi.getFileType());
			dictionary.setParentCode("WDLX");
			List<Object> dicList = service.EntityQuery(dictionary);

			if (dicList == null || dicList.size() == 0) {
				type = "SJ";
			} else {
				dictionary = (TDictionary) dicList.get(0);
				type = dictionary.getCode();
			}

			// �ĵ����ͱ�����Ҫ�������ͺ��ĵ�����
			docType.setName(fi.getFolderPath());
			docType.setCode(project.getType() + "_" + type);
			docType.setRemark(fi.getFolderPath());
			docType.setDftSecurity("S34");
			docType.setTUser(user);
			docType.setComFlag("0");
			docType.setCreateDate(new Date());
			String typeCode = GeneratorFactory.getGeneratorCode(service
					.getSession().getSessionFactory().openSession(), docType);

			TDocType parent = (TDocType) service.EntityQuery(TDocType.class,
					typeCode);
			if (parent != null) {
				docType.setParentCode(parent.getCode());
			} else {
				parent = new TDocType();
				org.apache.commons.beanutils.PropertyUtils.copyProperties(
						parent, docType);
				TDictionary dic = (TDictionary) service.EntityQuery(
						TDictionary.class, project.getType());
				parent.setName(dic.getName() + "���̳���");
				parent.setComFlag("0");
				parent.setCode(typeCode);
				parent.setParentCode(null);
				service.insertEntity(parent);
				docType.setParentCode(parent.getCode());
			}
			docType.setCode(GeneratorFactory.getGeneratorCode(service
					.getSession().getSessionFactory().openSession(), docType));
			service.insertEntity(docType);

		}
		return docType;
	}

	/**
	 * ���������ļ��б�
	 * 
	 * @param fileXml
	 * @throws Exception
	 */
	public Map<String, List<String>> getXmlInfo(File fileXml) throws Exception {
		ArchiveXmlPaser px = new ArchiveXmlPaser();
		List<MainProject> proList = px.parseXml(fileXml);
		int fileCount = 0;
		try {
			// ѭ��������
			for (MainProject mainProject : proList) {

				TProject project = new TProject();
				List<Object> list = null;
				if (mainProject.getProjectName() != null
						&& !"".equals(mainProject.getProjectName())) {
					project.setName(mainProject.getProjectName());
					list = service.EntityQuery(project);
				}

				// �ڹ��̹�������޴˹��̵ļ�¼��Խ��
				if (list == null || list.size() == 0) {
					logger.warn("�޷��ҵ����̣�" + mainProject.getProjectName());
					continue;
				} else {
					project = (TProject) list.get(0);
				}

				for (FileInfo fi : mainProject.getFileList()) {

					logger.info(mainProject.getProjectName() + " : "
							+ fi.getFilePath());

					saveFile(mainProject, project, fi);
				}

				fileCount = fileCount + mainProject.getFileList().size();

				List<ChildProject> childList = mainProject.getChildProjects();

				// ѭ���ӹ���
				for (ChildProject childProject : childList) {

					TProject child = new TProject();
					List<Object> cpList = null;
					if (childProject.getProjectName() != null
							&& !"".equals(childProject.getProjectName())) {
						child.setName(childProject.getProjectName());
						cpList = service.EntityQuery(child);
					}

					// �ڹ��̹�������޴˹��̵ļ�¼��Խ��
					if (cpList == null || cpList.size() == 0) {
						logger.warn("û���ӹ��̱���!�޷��ҵ����̣�"
								+ childProject.getProjectName());
						continue;
					} else {
						child = (TProject) cpList.get(0);
					}

					for (FileInfo fi : childProject.getFileList()) {

						logger.info(mainProject.getProjectName() + ": "
								+ childProject.getProjectName() + ": "
								+ fi.getFilePath());

						saveFile(mainProject, childProject, child, fi);
					}
					errorMap.put(mainProject.getProjectId(), errorFileList);
					fileCount = fileCount + childProject.getFileList().size();
				}
			}
		} finally {
			String path = FileUtil.getWebRootPath() + File.separator
					+ "archieve" + File.separator + "���ӻ�����";
			this.delFolder(new File(path));
		}
		return errorMap;
	}

	/**
	 * ���������ļ��б�
	 * 
	 * @param fileXml
	 * @throws Exception
	 */
	public Map<String, List<String>> getXmlInfoByHttp(File fileXml)
			throws Exception {
		ArchiveXmlPaser px = new ArchiveXmlPaser();
		List<MainProject> proList = px.parseXmlByHttp(fileXml);
		int fileCount = 0;
		try {
			// ѭ��������
			for (MainProject mainProject : proList) {

				TProject project = new TProject();
				List<Object> list = null;
				if (mainProject.getProjectName() != null
						&& !"".equals(mainProject.getProjectName())) {
					project.setName(mainProject.getProjectName());
					project.setCode(mainProject.getCode());
					list = service.EntityQuery(project);
				}

				// �ڹ��̹�������޴˹��̵ļ�¼��Խ��
				if (list == null || list.size() == 0) {
					logger.warn("�޷��ҵ����̣�" + mainProject.getProjectName());
					continue;
				} else {
					project = (TProject) list.get(0);
				}

				for (FileInfo fi : mainProject.getFileList()) {

					logger.info(mainProject.getProjectName() + " : "
							+ fi.getFilename());

					saveFileByHttp(mainProject, project, fi);
				}

				fileCount = fileCount + mainProject.getFileList().size();

				List<ChildProject> childList = mainProject.getChildProjects();

				// ѭ���ӹ���
				for (ChildProject childProject : childList) {

					TProject child = new TProject();
					List<Object> cpList = null;
					if (childProject.getProjectName() != null
							&& !"".equals(childProject.getProjectName())) {
						child.setName(childProject.getProjectName());
						child.setCode(childProject.getCode());
						cpList = service.EntityQuery(child);
					}

					// �ڹ��̹�������޴˹��̵ļ�¼��Խ��
					if (cpList == null || cpList.size() == 0) {
						logger.warn("û���ӹ��̱���!�޷��ҵ����̣�"
								+ childProject.getProjectName());
						continue;
					} else {
						child = (TProject) cpList.get(0);
					}

					for (FileInfo fi : childProject.getFileList()) {

						logger.info(mainProject.getProjectName() + ": "
								+ childProject.getProjectName() + ": "
								+ fi.getFilePath());

						saveFileByHttp(mainProject, childProject, child, fi);
					}
					errorMap.put(mainProject.getProjectId(), errorFileList);
					fileCount = fileCount + childProject.getFileList().size();
				}
			}
		} finally {
			String path = FileUtil.getWebRootPath() + File.separator
					+ "archieve" + File.separator + "���ӻ�����";
			this.delFolder(new File(path));
		}
		return errorMap;
	}

	/**
	 * ɾ���ô������ļ��м����ļ�
	 * 
	 * @param folderPath
	 */
	private void delFolder(File file) {

		// ������
		if (file.listFiles() != null && file.listFiles().length != 0) {
			for (File subFile : file.listFiles()) {
				delFolder(subFile);
			}
		}

		file.delete();
	}

	/**
	 * ���ļ�·��ת��Ϊ����·��
	 * 
	 * @param remotePath
	 * @return
	 */
	private String getLocalPath(String remotePath) {
		return (FileUtil.getWebRootPath() + File.separator + "archieve" + remotePath)
				.replaceAll("%20", " ");
	}

	public String downloadFilebyHttp(MainProject project, FileInfo fi) {
		InputStream bis = null;
		OutputStream bos = null;
		PostMethod postMethod = null;
		try {
			HttpClient client = new HttpClient();
			StringBuffer address = new StringBuffer(project.getAddress());
			address.append("from=client&");
			address.append("fileId=" + fi.getFileId() + "&");
			address.append("fileName="
					+ URIUtil.encodePath(fi.getFilename(), "UTF-8") + "&");
			address.append("area=" + URIUtil.encodePath(fi.getArea(), "UTF-8"));
			postMethod = new PostMethod(address.toString());

			// �����ص��ļ��Ƚϴ� , ���ڴ������������ӳ�ʱʱ��
			client.getHttpConnectionManager().getParams().setConnectionTimeout(
					8000);
			int httpStat = client.executeMethod(postMethod);

			if (!(httpStat == HttpStatus.SC_OK)) {
				return null;
			}
			// ���ص��ļ���
			bis = postMethod.getResponseBodyAsStream();
			File file = new File(fi.getLocalPath());
			File fileFolder = file.getParentFile();
			if (!fileFolder.exists())
				fileFolder.mkdirs();
			if (!file.exists())
				file.createNewFile();
			bos = new FileOutputStream(new File(fi.getLocalPath()));
			byte[] buffer = new byte[1024];
			int i = -1;
			while ((i = bis.read(buffer)) != -1) {
				bos.write(buffer, 0, i);
			}
			bos.flush();
			logger.info("�ڲ�΢����Զ���ļ���" + fi.getFilename() + "�ɹ���");
			return fi.getLocalFolder();
		} catch (Exception e) {
			logger.warn(e);
			return null;
		} finally {
			try {
				if (bis != null)
					bis.close();
				if (bos != null)
					bis.close();
				if (postMethod != null)
					postMethod.releaseConnection();
			} catch (IOException e) {
				logger.warn(e);
			}
		}
	}

	/**
	 * 
	 * @param mainProject
	 *            ������ MainProject
	 * @param project
	 *            ����ʵ�� TProject
	 * @param fi
	 *            �ĵ���Ϣ FileInfo
	 * @throws Exception
	 */
	private void saveFileByHttp(MainProject mainProject, TProject project,
			FileInfo fi) throws Exception {
		File file = null;
		fi = this.getFileInfoByHttp(fi);
		try {
			// HTTP���ط�ʽ
			String status = downloadFilebyHttp(mainProject, fi);
			if (StringUtils.isBlank(status)) {
				logger.error("�ļ�ϵͳ", "����ͬ��", "�������ļ�: " + fi.getFilename()
						+ " ʱ��������");
				errorFileList.add(fi.getFileId());
				return;
			}
			// �����ļ����Ͳ�ͬ�ֱ���
			file = new File(fi.getLocalPath());
			String distFolder = fi.getTargetPath();
			distFolder = distFolder.replaceAll("\\\\", "/");
			logger.info("Distination Folder is:" + distFolder);

			Timestamp now = new Timestamp(System.currentTimeMillis());
			String suffix = FilenameUtils.getExtension(file.getName());
			if (suffix == null || suffix.trim().length() == 0) {
				suffix = "UNKNOWN";
			}

			TDoc doc = new TDoc();
			doc.setCreateDate(now);
			doc.setTUser(user);

			FileInputStream fis = new FileInputStream(file);

			doc.setFileSize(new BigDecimal(fis.available()));
			doc.setFormat(FileUtil.getFileFormat(file.getName()));
			doc.setSuffix(suffix);

			String fileName = file.getName();
			if (file.getName().lastIndexOf(".") != -1) {
				fileName = file.getName().substring(0,
						file.getName().lastIndexOf("."));
			}

			doc.setName(fileName.replaceAll("'", ""));
			doc.setPath(file.getPath());

			TDocType docType = this.getDocType(fi, project);

			doc.setTDocType(docType);
			doc.setSecurity(docType.getDftSecurity() == null ? "S34" : docType
					.getDftSecurity());
			doc.setMetaFlag(new BigDecimal(0));
			doc.setStatus("01");
			// �ļ��洢λ��
			doc.setStoreLocation(FileStoreLocation.getStoreLocation());

			// service.insertEntity(doc);
			// doc = (TDoc)service.EntityQuery(doc).get(0);

			TDocProject docPro = new TDocProject();

			TDocProjectId dp_id = new TDocProjectId();
			dp_id.setTDoc(doc);
			dp_id.setTProject(project);

			docPro.setId(dp_id);
			docPro.setRemark(project.getName() + doc.getTDocType().getName());

			TPreDesgin preDesgin = new TPreDesgin();
			preDesgin.setDocId(doc.getId());
			preDesgin.setCreateDate(new Date());
			preDesgin.setTDoc(doc);
			preDesgin.setTUnit(project.getTUnitByDesignUnitId());

			service.insertArchives(doc, docPro, preDesgin, project, null, null,
					docType, user.getLoginId(), fi.getLocalPath());
			// myFTPClient.uploadFile(file, distFolder);

			// file.delete();
		} catch (Exception ex) {
			logger.error("TDoc", "L06",
					"�ڴ����ļ� " + fi.getFilePath() + " ʱ��������", ex);
			errorFileList.add(fi.getFileId());
		}
	}

	/**
	 * 
	 * @param mainProject
	 *            ������ MainProject
	 * @param childProject
	 *            ����� ChildProject
	 * @param project
	 *            ����ʵ�� TProject
	 * @param fi
	 *            �ĵ���Ϣ FileInfo
	 * @throws Exception
	 */
	private void saveFileByHttp(MainProject mainProject,
			ChildProject childProject, TProject project, FileInfo fi) {
		File file = null;
		fi = this.getFileInfoByHttp(fi);

		try {
			// http��ʽ���ص��ĵ�����Ҫ����
			String status = downloadFilebyHttp(mainProject, fi);

			if (StringUtils.isBlank(status)) {
				logger.error("�ļ�ϵͳ", "����ͬ��", "�������ļ�: " + fi.getFilename()
						+ " ʱ��������");
				errorFileList.add(fi.getFileId());
				return;
			}

			// �����ļ����Ͳ�ͬ�ֱ���
			file = new File(fi.getLocalPath().replaceAll("%20", " "));
			String distFolder = fi.getTargetPath();
			distFolder = distFolder.replaceAll("\\\\", "/");
			logger.info("Distination Folder is:" + distFolder);

			Timestamp now = new Timestamp(System.currentTimeMillis());
			String suffix = FilenameUtils.getExtension(file.getName());
			if (suffix == null || suffix.trim().length() == 0) {
				suffix = "UNKNOWN";
			}

			TDoc doc = new TDoc();
			doc.setCreateDate(now);
			doc.setTUser(user);

			FileInputStream fis = new FileInputStream(file);

			doc.setFileSize(new BigDecimal(fis.available()));
			doc.setFormat(FileUtil.getFileFormat(file.getName()));
			doc.setSuffix(suffix);

			String fileName = file.getName();
			if (file.getName().lastIndexOf(".") != -1) {
				fileName = file.getName().substring(0,
						file.getName().lastIndexOf("."));
			}
			doc.setName(fileName.replaceAll("'", ""));
			doc.setPath(file.getPath());

			// ����ĵ�����
			TDocType docType = this.getDocType(fi, project);

			doc.setTDocType(docType);
			doc.setSecurity(docType.getDftSecurity() == null ? "S34" : docType
					.getDftSecurity());
			doc.setMetaFlag(new BigDecimal(0));
			doc.setStatus("01");
			//�ļ��洢λ��
			doc.setStoreLocation(FileStoreLocation.getStoreLocation());
			// service.insertEntity(doc);
			// doc = (TDoc)service.EntityQuery(doc).get(0);

			TDocProject docPro = new TDocProject();
			TDocProjectId dp_id = new TDocProjectId();
			dp_id.setTDoc(doc);
			dp_id.setTProject(project);
			docPro.setId(dp_id);
			docPro.setRemark(project.getName() + doc.getTDocType().getName());

			TPreDesgin preDesgin = new TPreDesgin();
			preDesgin.setDocId(doc.getId());
			preDesgin.setCreateDate(new Date());
			preDesgin.setTDoc(doc);
			preDesgin.setTUnit(project.getTUnitByDesignUnitId());

			service.insertArchives(doc, docPro, preDesgin, project, null, null,
					docType, user.getLoginId(), fi.getLocalPath().replaceAll(
							"%20", " "));
			// myFTPClient.uploadFile(file, distFolder);

			// file.delete();
		} catch (Exception ex) {
			logger.error("TDoc", "L06",
					"�ڴ����ļ� " + fi.getFilePath() + " ʱ��������", ex);
			errorFileList.add(fi.getFileId());
		}
	}
}