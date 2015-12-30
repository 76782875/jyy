package com.nsc.dem.service.archives.impl;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.swing.text.BadLocationException;

import org.apache.commons.io.FilenameUtils;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;
import org.hibernate.Session;

import com.nsc.base.conf.Configurater;
import com.nsc.base.index.AnalyzerFactory;
import com.nsc.base.index.DOCFIELDEnum;
import com.nsc.base.index.FileField;
import com.nsc.base.index.IIndexWriter;
import com.nsc.base.index.IndexFactory;
import com.nsc.base.util.ContinueFTP;
import com.nsc.base.util.DeleteFileStatus;
import com.nsc.base.util.DesUtil;
import com.nsc.base.util.DownloadStatus;
import com.nsc.base.util.FileUtil;
import com.nsc.base.util.UploadStatus;
import com.nsc.dem.action.bean.TableBean;
import com.nsc.dem.bean.archives.TDoc;
import com.nsc.dem.bean.archives.TDocType;
import com.nsc.dem.bean.archives.TSynDoc;
import com.nsc.dem.bean.archives.TSynProject;
import com.nsc.dem.bean.profile.TRole;
import com.nsc.dem.bean.project.TComponent;
import com.nsc.dem.bean.project.TComponentDoc;
import com.nsc.dem.bean.project.TDocProject;
import com.nsc.dem.bean.project.TDocProjectId;
import com.nsc.dem.bean.project.TPreDesgin;
import com.nsc.dem.bean.project.TProject;
import com.nsc.dem.bean.project.TRecordDrawing;
import com.nsc.dem.bean.project.TShopDoc;
import com.nsc.dem.bean.project.TShopDrawing;
import com.nsc.dem.bean.project.TTender;
import com.nsc.dem.bean.system.TDictionary;
import com.nsc.dem.bean.system.TOperateLog;
import com.nsc.dem.bean.system.TUnit;
import com.nsc.dem.service.archives.IarchivesService;
import com.nsc.dem.service.base.BaseService;
import com.nsc.dem.util.filestore.FileStoreLocation;
import com.nsc.dem.util.filestore.Pinyin4jUtil;
import com.nsc.dem.util.index.EXFIELDEnum;
import com.nsc.dem.util.index.FileDirUtils;
import com.nsc.dem.util.index.IndexSearchManager;
import com.nsc.dem.util.log.Logger;
import com.nsc.dem.util.xml.FtpXmlUtils;
import com.nsc.dem.util.xml.StoreFileReceiveID;

@SuppressWarnings("unchecked")
public class ArchivesServiceImpl extends BaseService implements
		IarchivesService {

	/**
	 * ��ѯ����������Ϣ�б�
	 * 
	 * @param ����ʵ����
	 * @return
	 */
	public List archivesInfoList() {
		return null;
	}

	/**
	 * �鿴������ϸ��Ϣ
	 * 
	 * @param ��������
	 * @return ����ʵ����
	 */
	public List archivesInfo() {

		return null;
	}

	/**
	 * ¼�뵵�� ���汾��Ϣ��Ϊ1����ʼ��������Ϣ�����õ�����Ϣ�洢������ �Ե�����Ϣ���������洢����������Ԥ��ͼ��������Ԥ��ͼ
	 * 
	 * @param ����ʵ����
	 */

	public void insertArchives(TDoc tdoc, TDocProject tdocPro,
			TPreDesgin tpreDesgin, TProject tpro, TComponent tcom,
			TComponentDoc tcomp, TDocType docType, String login_id, String local)
			throws Exception {

		Logger logger = super.logger.getLogger(ArchivesServiceImpl.class);

		try {
			this.tranArchives(docType, tpro, tdoc, local);

			addArchives(tdoc, tdocPro, tpreDesgin, tpro, tcom, tcomp, login_id,
					local);

			logger.info("����¼����Ϣ�ɹ�");

		} catch (IOException e) {
			logger.warn("�ϴ��ļ�ʧ��:", e);
			logger.error("�ϴ��ļ�ʧ��:", "06", "�ϴ��ļ�:" + tdoc.getName() + " ʧ��");
			throw new Exception("�ϴ��ļ�:" + tdoc.getName() + " ʧ��"
					+ e.getMessage());
		} catch (Exception e) {
			logger.error("����¼����Ϣʧ��:", "06", "����¼����Ϣʧ��:" + tdoc.getName()
					+ " ʧ��", e);
			throw new Exception("����¼����Ϣʧ�ܣ�" + e.getMessage());
		}

	}

	/**
	 * ���ļ� ���м��� ��������ͼ �ϴ��������� ��ɾ�����ϴ��ļ�
	 * 
	 * @param docType
	 *            �ĵ�����
	 * @param tpro
	 *            ����
	 * @param tdoc
	 *            �ĵ�
	 * @param local
	 *            ����·��
	 * @throws Exception
	 */
	public void tranArchives(TDocType docType, TProject tpro, TDoc tdoc,
			String local) throws Exception {
		Logger logger = super.logger.getLogger(ArchivesServiceImpl.class);

		// �ϴ���FTP�ļ��������ĵ�ַ
		String remotePath = this.filePathProduce(docType.getName(), tpro);
		
		String fileNameTemp = tdoc.getName();
		
		//תΪƴ��
		String pathLanguage = Configurater.getInstance().getConfigValue("pathlanguage");
		if(pathLanguage.toUpperCase().equals("PY")){
			fileNameTemp = Pinyin4jUtil.getPinYin(fileNameTemp);
		}
		
		remotePath = remotePath + File.separator
				+ this.fileGuidName(fileNameTemp + "." + tdoc.getSuffix());

		tdoc.setPath(remotePath);
		tdoc.setMetaFlag(new BigDecimal(0));

		String fileName = DesUtil.getFileName(local);

		String ftpPath = File.separator + "archives" + remotePath;

		String mimeType = Configurater.getInstance().getConfigValue("mime",
				tdoc.getSuffix().toLowerCase());
		
		// ��ͼƬ�����ܴ���
		if (mimeType == null || mimeType.indexOf("image") == -1) {

			String encryptPath = DesUtil.getFilePath(local);
			encryptPath = encryptPath
					+ Configurater.getInstance().getConfigValue("encrypt");

			encryptPath = encryptPath + File.separator + fileName;

			// �����ļ�
			DesUtil.encrypt(local, encryptPath);

			UploadStatus status = ContinueFTP.getInstance().upload(
					encryptPath, ftpPath);
			logger.info("�ϴ�״̬:" + status);
			if (!encryptPath.equals("")) {
				FileUtil.deleteFile(encryptPath);
				FileUtil.deleteFile(local);
			}
		} else {
			ContinueFTP.getInstance().upload(local, ftpPath);
			FileUtil.deleteFile(local);
		}
		tdoc.setPath(ftpPath); 
	}

	
	
	
	/**
	 * ȫ�Ľ�������
	 */
	public Map<Enum<?>, FileField> setArchivesIndex(TDoc tdoc, TProject tpro,
			String local, TUnit tunit) throws IllegalAccessException,
			InstantiationException, InvocationTargetException,
			NoSuchMethodException {

		Map<Enum<?>, FileField> params = new HashMap<Enum<?>, FileField>();
		if (tdoc != null) {
			DOCFIELDEnum author = DOCFIELDEnum.author;
			author.setValue(tdoc.getTUser().getName());
			params.put(author, author.getF());

			DOCFIELDEnum title = DOCFIELDEnum.title;
			title.setValue(tdoc.getName());
			params.put(title, title.getF());

			DOCFIELDEnum cdate = DOCFIELDEnum.cdate;
			cdate.setValue(tdoc.getCreateDate().toString());
			params.put(cdate, cdate.getF());

			DOCFIELDEnum docid = DOCFIELDEnum.docid;
			docid.setValue(tdoc.getId());
			params.put(docid, docid.getF());

			DOCFIELDEnum doctype = DOCFIELDEnum.doctype;
			doctype.setValue(tdoc.getTDocType().getName());
			params.put(doctype, doctype.getF());

			DOCFIELDEnum version = DOCFIELDEnum.version;
			version.setValue(tdoc.getVersion());
			params.put(version, version.getF());

			// �ĵ�������״̬
			DOCFIELDEnum status = DOCFIELDEnum.status;
			status.setValue(tdoc.getStatus());
			params.put(status, status.getF());
			
			DOCFIELDEnum url = DOCFIELDEnum.url;
			url.setValue(tdoc.getPath());
			params.put(url, url.getF());
			
			DOCFIELDEnum suffix = DOCFIELDEnum.suffix;
			suffix.setValue(tdoc.getSuffix());
			params.put(suffix, suffix.getF());
		}

		if (tpro != null) {
			TDictionary dic = (TDictionary) super.EntityQuery(
					TDictionary.class, tpro.getType());

			EXFIELDEnum proType = EXFIELDEnum.proType;
			proType.setValue(dic.getName());
			params.put(proType, proType.getF());

			TDictionary tdic = (TDictionary) super.EntityQuery(
					TDictionary.class, tpro.getStatus());

			EXFIELDEnum pharase = EXFIELDEnum.pharase;
			pharase.setValue(tdic.getName());
			params.put(pharase, pharase.getF());

			EXFIELDEnum project = EXFIELDEnum.project;
			project.setValue(tpro.getName());
			params.put(project, project.getF());

			EXFIELDEnum projectid = EXFIELDEnum.projectid;
			projectid.setValue(tpro.getId().toString());
			params.put(projectid, projectid.getF());

			EXFIELDEnum predesign = EXFIELDEnum.designer;
			predesign.setValue(tpro.getTUnitByDesignUnitId().getCode());
			params.put(predesign, predesign.getF());

		}

		if (tunit != null) {
			EXFIELDEnum company = EXFIELDEnum.company;
			company.setValue(tunit.getName());
			params.put(company, company.getF());

			EXFIELDEnum companyid = EXFIELDEnum.companyid;
			companyid.setValue(tunit.getProxyCode());
			params.put(companyid, companyid.getF());

		}

		return params;
	}

	/**
	 * �洢������Ϣ �洢������Ϣ���ļ�������Ϣ����������������Ϣ
	 * 
	 * @param tdoc
	 *            �ļ���ʵ����
	 * @param tdocPro
	 *            �������̱�ʵ����
	 * @param tpreDesgin
	 *            ���赵����ʵ����
	 * @param tpro
	 *            ���̱�ʵ����
	 * @param tcom
	 *            ������ʵ����
	 * @param tcomp
	 *            ��������������ʵ����
	 * @throws Exception
	 */
	public void addArchives(TDoc tdoc, TDocProject tdocPro,
			TPreDesgin tpreDesgin, TProject tpro, TComponent tcom,
			TComponentDoc tcomp, String login_id, String local)
			throws Exception {
		if (tdoc == null || tdocPro == null || tpreDesgin == null
				|| tpro == null) {

			throw new Exception("�������ʵ���಻��Ϊ��");

		}

		// �õ��ļ��ĵ�ǰ��߰汾
		Integer vesion = this.getFileVersion(tdoc, tpro);
		vesion = vesion + 1;
		tdoc.setVersion(vesion.toString());
		tdoc.setMetaFlag(new BigDecimal(0));
		Timestamp now = new Timestamp(System.currentTimeMillis());
		tdoc.setCreateDate(now);

		// �����ļ�����Ϣ
		super.EntityAdd(tdoc);

		String id = tdoc.getId();
		// //���赵������Ϣ
		tpreDesgin.setDocId(id);
		tpreDesgin.setTDoc(tdoc);

		TUnit designUnit = (TUnit) super.EntityQuery(TUnit.class, tpro
				.getTUnitByDesignUnitId().getCode());

		tpreDesgin.setTUnit(designUnit);
		super.EntityAdd(tpreDesgin);

		// ���浵�����̱���Ϣ
		TDocProjectId tDocProjectId = new TDocProjectId();
		tDocProjectId.setTDoc(tdoc);
		tDocProjectId.setTProject(tpro);
		tdocPro.setId(tDocProjectId);

		super.EntityAdd(tdocPro);
	}

	/**
	 * �õ��ļ��ĵ�ǰ��߰汾
	 * 
	 * @param tdoc
	 * @return
	 */
	public Integer getFileVersion(TDoc tdoc, TProject tpro) {

		String sql = "select max(to_number(doc.version)) maxid from T_DOC doc,T_Doc_Project pro,T_Project proj "
				+ " where doc.id=pro.doc_id and pro.project_id=proj.id"
				+ " and doc.doc_type_code='"
				+ tdoc.getTDocType().getCode()
				+ "'"
				+ " and doc.name='"
				+ tdoc.getName()
				+ "'"
				+ " and proj.id='" + tpro.getId() + "'";

		Integer value = super.generalDAO.queryByNativeSQLMAX(sql, "maxid");

		if (value == null) {

			value = 0;
		}

		return value;
	}

	/**
	 * �������״̬ �����ļ�״̬
	 * 
	 * @param ����ʵ����
	 */

	public void updateArcState() {

	}

	/**
	 * ���µ��� ���õ�����Ϣ�洢�����洢�ļ���Ϣ�����ϴ��ļ�������ļ��汾�� �����ļ�ftp�洢����������Ԥ��ͼ��������Ԥ��ͼ
	 * 
	 * @param tdoc
	 *            �ļ���ʵ����
	 * @param code
	 *            �ĵ����������
	 * @param proCode
	 *            ���̱�����
	 * @param location
	 *            �ϴ��ļ���WEB·��
	 * @param preD
	 *            �����ʵ����
	 * @param shopDraw
	 *            ʩ��ͼ������
	 * @param shopDoc
	 *            ʩ��������
	 * @param recordDraw
	 *            ����ͼ������
	 * @param tdocPro
	 *            �������̱�
	 * @param tcom
	 *            ������
	 * @param tcomp
	 *            ��������������
	 * @param login_id
	 *            ��¼ID
	 * @throws Exception
	 */
	public void updateArchives(TDoc tdoc, String code, String proCode,
			String location, TPreDesgin preD, TShopDrawing shopDraw,
			TShopDoc shopDoc, TRecordDrawing recordDraw, TDocProject tdocPro,
			TComponent tcom, TComponentDoc tcomp, TTender tender,
			String login_id, String storeLaction, String isLocal) throws Exception {
		TProject tpro = new TProject();
		tpro = (TProject) super.EntityQuery(TProject.class, new Long(proCode));

		TDocType docType = new TDocType();
		docType = (TDocType) super.EntityQuery(TDocType.class, code);

		if (tdoc.getStatus().equals("03")) {
			if (location != null) {
				String[] doc = new String[] { tdoc.getId() };
				this.delFile(doc);

				this.tranArchives(docType, tpro, tdoc, location);
				this.tranArchivesUpdate(tdoc, preD, shopDraw, shopDoc,
						recordDraw, tdocPro, tcom, tcomp, tender);

				String sql = "select u.* from t_Project p,T_Unit u where p.owner_unit_id=u.code and p.owner_unit_id='"
						+ tpro.getTUnitByOwnerUnitId().getCode() + "'";

				List<TUnit> list = super.generalDAO.queryByNativeSQLEntity(sql,
						TUnit.class);

				TUnit tunit = list.get(0);

				Map<File, Map<Enum<?>, FileField>> files = new HashMap<File, Map<Enum<?>, FileField>>();
				files.put(new File(location), this.setArchivesIndex(tdoc, tpro,
						location, tunit));
			} else {
				this.tranArchivesUpdate(tdoc, preD, shopDraw, shopDoc,
						recordDraw, tdocPro, tcom, tcomp, tender);

			}
		}

		if (tdoc.getStatus().equals("01")) {
			if (location != null) {
				// �õ��ļ��ĵ�ǰ��߰汾
				Integer vesion = this.getFileVersion(tdoc, tpro);
				vesion = vesion + 1;
				tdoc.setVersion(vesion.toString());

				this.tranArchives(docType, tpro, tdoc, location);

				super.EntityAdd(tdoc);
				addArchives(tdoc, tdocPro, preD, tpro, tcom, tcomp, login_id,
						location);
			} else {
				this.tranArchivesUpdate(tdoc, preD, shopDraw, shopDoc,
						recordDraw, tdocPro, tcom, tcomp, tender);
			}
		}
	}

	/**
	 * �ļ����µ��ô˷��� �������ļ�·������
	 * 
	 * @param tdoc
	 * @param preD
	 * @param shopDraw
	 * @param shopDoc
	 * @param recordDraw
	 * @param tdocPro
	 * @param tcom
	 * @param tcomp
	 * @param tender
	 */
	private void tranArchivesUpdate(TDoc tdoc, TPreDesgin preD,
			TShopDrawing shopDraw, TShopDoc shopDoc, TRecordDrawing recordDraw,
			TDocProject tdocPro, TComponent tcom, TComponentDoc tcomp,
			TTender tender) {

		if (tdoc != null) {
			Session se = super.getSession();
			se.clear();
			super.EntityUpdate(tdoc);
		}
		if (preD != null) {
			super.EntityUpdate(preD);
		}

		if (shopDraw != null) {
			super.EntityUpdate(shopDraw);
		}

		if (shopDoc != null) {
			super.EntityUpdate(shopDoc);
		}

		if (recordDraw != null) {
			super.EntityUpdate(recordDraw);
		}
	}

	/**
	 * �����ļ��鿴�汾�б� �ṩ��ǰ���ļ��İ汾�ݱ���ʷ�������汾�ţ��ϴ��ˣ��ϴ�ʱ�䣬���
	 * 
	 * @param �ļ�����
	 * @return
	 */

	public List fileVersionList() {

		return null;
	}

	/**
	 * Ǩ���ļ� ת���ļ�����λ�ã��������ļ�����Ŀ¼Ϊ�·���λ��Ŀ¼
	 * 
	 * @param �ļ�����
	 * @return
	 */
	public void delMoveFile(String storeLocation, String isLocal, String fileCode[], String code) throws Exception {
		Logger logger = super.logger.getLogger(ArchivesServiceImpl.class);
		String fileContent = "";
		try {
			TDocType docType = new TDocType();

			docType = (TDocType) super.EntityQuery(TDocType.class, code);

			for (int i = 0; i < fileCode.length; i++) {

				TDoc doc = (TDoc) super.EntityQuery(TDoc.class, fileCode[i]);				

				String docTypeName = doc.getTDocType().getName();

				String path = doc.getPath();
				String previewPath = doc.getPreviewPath();

				if (previewPath != null) {
					String imageFileName = DesUtil.getFileName(previewPath);
					String fileName = DesUtil.getFileName(path);

					String newPath = path.substring(0, path
							.lastIndexOf(docTypeName))
							+ docType.getName() + File.separator + fileName;
					String newPreviewPath = previewPath.substring(0,
							previewPath.lastIndexOf(docTypeName))
							+ docType.getName()
							+ File.separator
							+ imageFileName;

					newPath = newPath.replaceAll("\\\\", "/");
					newPreviewPath = newPreviewPath.replaceAll("\\\\", "/");

					if (newPath.contains("/")) {
						// ����������Զ��Ŀ¼�ṹ
						ContinueFTP ftp = ContinueFTP.getInstance();
						ftp.CreateDirecroty(DesUtil.getFilePath(newPath),
								ftp.ftpClient);
						ftp.rename(path, newPath);
						ftp.disconnect();
					}

					if (newPreviewPath.contains("/")) {
						// ����������Զ��Ŀ¼�ṹ
						ContinueFTP ftpImage = ContinueFTP
								.getInstance();
						ftpImage.CreateDirecroty(DesUtil
								.getFilePath(newPreviewPath),
								ftpImage.ftpClient);
						ftpImage.rename(previewPath, newPreviewPath);

						ftpImage.disconnect();
					}
					doc.setTDocType(docType);
					doc.setPath(newPath);
					doc.setPreviewPath(newPreviewPath);

					super.EntityUpdate(doc);

					if (fileContent.equals("")) {
						fileContent = fileContent + doc.getName();
					} else {
						fileContent = fileContent + ";" + doc.getName();
					}
				} else {

					String fileName = DesUtil.getFileName(path);

					String newPath = path.substring(0, path
							.lastIndexOf(docTypeName))
							+ docType.getName() + File.separator + fileName;

					newPath = newPath.replaceAll("\\\\", "/");

					if (newPath.contains("/")) {
						// ����������Զ��Ŀ¼�ṹ
						ContinueFTP ftp = ContinueFTP.getInstance();
						ftp.CreateDirecroty(DesUtil.getFilePath(newPath),
								ftp.ftpClient);
						ftp.rename(path, newPath);
						ftp.disconnect();
					}
					doc.setTDocType(docType);
					doc.setPath(newPath);
					
					//����ļ��ɹ���ɾ�������ط��Ĵ洢λ�ã����ļ�ֻ�б��ر���
					doc.setStoreLocation(FileStoreLocation.getStoreLocation());
					super.EntityUpdate(doc);

					if (fileContent.equals("")) {
						fileContent = fileContent + doc.getName();
					} else {
						fileContent = fileContent + ";" + doc.getName();
					}
				}
				
				
				

				// �漰�����ж��ļ������������������
				Map<Enum<?>, FileField> map = this.setArchivesIndex(doc, null,
						null, null);
				this.updateArchiveIndex(storeLocation, isLocal, map);

			}
		} catch (Exception e) {
			logger.error("Ǩ���ļ�ʧ��:", "05", "�ļ�:" + fileContent + "Ǩ���ļ�ʧ��", e);
			throw new Exception("Ǩ���ļ�ʧ��:" + e.getMessage());
		}
	}

	/**
	 * ����ɾ���ļ� �ļ�������ɾ�������ļ���Ϣ�����ݿ���ɾ������ɾ�����ϴ�ʵ�塣
	 * 
	 * @param �ļ���������
	 * @return
	 */
	public void delFile(String fileCode[]) {
		Logger logger = super.logger.getLogger(ArchivesServiceImpl.class);
		for (int i = 0; i < fileCode.length; i++) {

			TDoc doc = (TDoc) super.EntityQuery(TDoc.class, fileCode[i]);

			String path = doc.getPath();
			String previewPath = doc.getPreviewPath();

			ContinueFTP ftp = null;
			try {
				ftp = ContinueFTP.getInstance();

				ftp.deleteFile(path);
				ftp.deleteFile(previewPath);

			} catch (NumberFormatException e) {
				logger.warn(e);
			} catch (IOException e) {
				logger.warn(e);
			} finally {
				try {
					if (ftp != null)
						ftp.disconnect();
				} catch (IOException e) {
					logger.warn(e.getMessage());
				}
			}

			super.EntityDel(doc);

		}
	}

	/**
	 * ɾ��������Ϣ ɾ����������Ϣ���ļ���Ϣ������������Ϣ����������������Ϣ��ftp�ļ�����
	 * 
	 * @param �ļ���������
	 * @return
	 */
	public void delArchives(String fileCode[]) throws Exception {

		for (int i = 0; i < fileCode.length; i++) {
			TDoc doc = (TDoc) EntityQuery(TDoc.class, fileCode[i]);
			// ���ɾ��ftp����ͼ
			// ��ȡ�ĵ�������ҵ����λ
			TProject project = getTProjectBydocId(doc.getId());
			doc.setProjectid(project.getId() + "");
			DeleteFileStatus dfs = ContinueFTP.getInstance()
					.deleteFile(doc.getPreviewPath());
			if (DeleteFileStatus.Delete_Remote_Success.name()
					.equals(dfs.name())) {
				super.logger.getLogger(ArchivesServiceImpl.class).warn(
						"����ͼɾ���ɹ�!");
			} else {
				super.logger.getLogger(ArchivesServiceImpl.class).warn(
						"����ͼɾ��ʧ��!");
			}

			Set<TDocProject> tdoc = doc.getTDocProjects();

			Iterator<TDocProject> it = tdoc.iterator();
			while (it.hasNext()) {// hasNext()�ж���û����һ��Ԫ��
				TDocProject st = it.next();
				super.EntityDel(st);
			}

			TPreDesgin preD = (TPreDesgin) EntityQuery(TPreDesgin.class,
					fileCode[i]);
			if (preD != null) {
				super.EntityDel(preD);
			}

			TShopDrawing shopDraw = (TShopDrawing) EntityQuery(
					TShopDrawing.class, fileCode[i]);
			if (shopDraw != null) {
				super.EntityDel(shopDraw);
			}

			TShopDoc shopDoc = (TShopDoc) EntityQuery(TShopDoc.class,
					fileCode[i]);
			if (shopDoc != null) {
				super.EntityDel(shopDoc);
			}

			TRecordDrawing recordDraw = (TRecordDrawing) EntityQuery(
					TRecordDrawing.class, fileCode[i]);
			if (recordDraw != null) {
				super.EntityDel(recordDraw);
			}
		}
		this.delFile(fileCode);
	}

	/**
	 * �ĵ�������״�б� T_Doc_Type �ܹ����ݹ������ͣ����̽׶ν��й���
	 * 
	 * @param projectType
	 *            ��������
	 * @param projectMoment
	 *            ���̽׶�
	 * @return
	 */

	public List tdocTypeList(String projectType, String projectMoment) {

		return null;
	}

	/**
	 * �����ļ����������ļ�·��
	 * 
	 * @param �ļ�����λ��
	 * @return
	 */
	public String filePathProduce(String docTypeName, TProject tpro) {
		Configurater config = Configurater.getInstance();
		String pathLanguage = config.getConfigValue("pathlanguage");
		
		Long parentId = tpro.getParentId();
		String childName = tpro.getName();

		String unitId = tpro.getTUnitByOwnerUnitId().getCode();

		TUnit unit = (TUnit) super.EntityQuery(TUnit.class, unitId);

		String unitName = unit.getName();

		TProject proj = new TProject();
		String parentName = "";
		if (parentId != null) {
			proj = (TProject) super.EntityQuery(TProject.class, parentId);
			parentName = proj.getName();
		}
		String path = "";
		
		//����·��
		if(pathLanguage.toUpperCase().equals("HZ")){
			if (parentName.equals("")) {
				path = File.separator + FileUtil.folderPathFilter(unitName)
						+ FileUtil.folderPathFilter(parentName) + File.separator
						+ FileUtil.folderPathFilter(childName) + File.separator
						+ FileUtil.folderPathFilter(docTypeName);
			} else {
				path = File.separator + FileUtil.folderPathFilter(unitName)
						+ File.separator + FileUtil.folderPathFilter(parentName)
						+ File.separator + FileUtil.folderPathFilter(childName)
						+ File.separator + FileUtil.folderPathFilter(docTypeName);
			}
		//ƴ��·��
		}else if (pathLanguage.toUpperCase().equals("PY")){
			if (parentName.equals("")) {
				path = File.separator + FileUtil.folderPathFilter(Pinyin4jUtil.getPinYin(unitName))
						+ FileUtil.folderPathFilter(Pinyin4jUtil.getPinYin(parentName)) + File.separator
						+ FileUtil.folderPathFilter(Pinyin4jUtil.getPinYin(childName)) + File.separator
						+ FileUtil.folderPathFilter(Pinyin4jUtil.getPinYin(docTypeName));
			} else {
				path = File.separator + FileUtil.folderPathFilter(Pinyin4jUtil.getPinYin(unitName))
						+ File.separator + FileUtil.folderPathFilter(Pinyin4jUtil.getPinYin(parentName))
						+ File.separator + FileUtil.folderPathFilter(Pinyin4jUtil.getPinYin(childName))
						+ File.separator + FileUtil.folderPathFilter(Pinyin4jUtil.getPinYin(docTypeName));
			}
		}
		return path;
	}

	/**
	 * ����guid·��
	 * 
	 * @param �ļ�����λ��
	 * @return
	 * @throws UnsupportedEncodingException
	 */

	private String fileGuidName(String fileName)
			throws UnsupportedEncodingException {

		UUID uuid = UUID.randomUUID();

		String strUuid = uuid.toString();

		fileName = strUuid
				+ Configurater.getInstance().getConfigValue("fileNameSplit")
				+ fileName;

		return fileName;
	}

	/**
	 * �����ļ�Ԥ��ͼ ���ݲ�ͬ���ļ���ʽ����Ԥ��ͼ
	 * 
	 * @param �ļ��ļ���ʽ
	 * @return
	 */

	public OutputStream filePreview(String fileFormat) {

		return null;
	}

	/**
	 * ��ȡԤ��ͼ �����ļ������ȡԤ��ͼ�ļ���ַ
	 * 
	 * @param �ļ�����
	 * @return
	 */

	public String filePreviewPath(String fileCode) {

		return null;
	}

	/**
	 * ��ȡ�ĵ����������б�
	 */
	public List<TDocType> docTypeList(String parentCode) {
		String sql = "";
		if ("".equals(parentCode) || parentCode == null) {
			sql = "SELECT * FROM T_DOC_TYPE WHERE PARENT_CODE IS NOT NULL";
		} else {
			sql = "SELECT * FROM T_DOC_TYPE WHERE PARENT_CODE='" + parentCode
					+ "'";
		}

		List<TDocType> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TDocType.class);

		return list;
	}

	/**
	 * ��ȡ�ĵ����������б�,�ڽ��ҳ
	 */
	public List<TDocType> docTypeResultList(String code, String parentCode) {
		String sql = "";
		if ("".equals(parentCode) || parentCode == null) {
			sql = "SELECT * FROM T_DOC_TYPE WHERE CODE LIKE  '" + code
					+ "%' and  PARENT_CODE IS NOT NULL ";
		} else {
			sql = "SELECT * FROM T_DOC_TYPE WHERE CODE LIKE  '" + code
					+ "%' and  PARENT_CODE = '" + parentCode + "'";
		}

		List<TDocType> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TDocType.class);

		return list;
	}

	/**
	 * �����ļ�
	 * @throws InterruptedException 
	 */
	public Set<File> addArchiveIndex(Map<File, Map<Enum<?>, FileField>> files,
			String storeLocation, String isLocal) throws IOException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, URISyntaxException, BadLocationException, InterruptedException {

		return this.indexingArchive(files, storeLocation, isLocal, false);
	}

	/**
	 * ���¼����ļ�
	 * @throws InterruptedException 
	 */
	private void updateArchiveIndex(Map<File, Map<Enum<?>, FileField>> files,
			String storeLocation, String isLocal) throws IOException,
			ClassNotFoundException, InstantiationException,
			IllegalAccessException, URISyntaxException, BadLocationException, InterruptedException {

		this.indexingArchive(files, storeLocation, isLocal, true);
	}

	/**
	 * ���¼����ļ�����
	 * 
	 * @param map
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws ClassNotFoundException
	 * @throws LockObtainFailedException
	 * @throws CorruptIndexException
	 */
	public void updateArchiveIndex(String storeLocation, String isLocal, Map<Enum<?>, FileField> params)
			throws URISyntaxException, CorruptIndexException,
			LockObtainFailedException, ClassNotFoundException,
			InstantiationException, IllegalAccessException, Exception {
		Logger logger = super.logger.getLogger(ArchivesServiceImpl.class);

		String indexDir = FileDirUtils.getRealPathByUnitId("doc_write_Dir", storeLocation, isLocal);
		// �����ļ����Ŀ¼

		boolean reload = false;
		IIndexWriter writer = null;
		File iFolder = new File(indexDir);

		try {
			writer = IndexFactory.getInstance().getIndexWriter(iFolder,
					AnalyzerFactory.getInstance().getAnalyzer());

			logger.info("���ĵ��������ļ������������� "
					+ params.get(DOCFIELDEnum.title).getContent());
			writer.updateDocument(params);
		} finally {
			// �ر������ļ�
			if (writer != null)
				IndexFactory.getInstance().close(iFolder);

			// �Ƿ�ʵʱ���ؼ����ļ�
			String realTime = Configurater.getInstance().getConfigValue(
					"realtime_Reaload");
			if (reload && "true".equals(realTime)) {
				File readDir = FileDirUtils.getReadFileByWriteFile(iFolder);
				IndexSearchManager.getInstance().releaseSearch(readDir);
				if(IndexSearchManager.getInstance().initReadFolder(iFolder, readDir)){
					IndexSearchManager.getInstance().reloadSingleFile(readDir);
				}
			}
		}
	}

	public void deleteArchiveIndex(String storeLocation, String isLocation, String docid)
			throws URISyntaxException, CorruptIndexException,
			LockObtainFailedException, ClassNotFoundException,
			InstantiationException, IllegalAccessException, IOException,
			InvocationTargetException, NoSuchMethodException {
		String indexDir = FileDirUtils.getRealPathByUnitId("doc_write_Dir", storeLocation, isLocation);
		boolean reload = false;
		IIndexWriter writer = null;
		File iFolder = new File(indexDir);
		
		try {
			writer = IndexFactory.getInstance().getIndexWriter(iFolder,
					AnalyzerFactory.getInstance().getAnalyzer());

			DOCFIELDEnum id = DOCFIELDEnum.docid;
			id.setValue(docid);
			writer.deleteDocument(id);
			reload = true;
		} finally {
			// �ر������ļ�
			if (writer != null)
				writer.closeWriter();

			// �Ƿ�ʵʱ���ؼ����ļ�
			String realTime = Configurater.getInstance().getConfigValue(
					"realtime_Reaload");
			if (reload && "true".equals(realTime)) {
				File readDir = FileDirUtils.getReadFileByWriteFile(iFolder);
				IndexSearchManager.getInstance().releaseSearch(readDir);
				if(IndexSearchManager.getInstance().initReadFolder(iFolder, readDir)){
					IndexSearchManager.getInstance().reloadSingleFile(readDir);
				}
			}
		}
	}

	/**
	 * �����ļ�
	 * 
	 * @param files
	 * @param unitCode
	 *            �洢λ��
	 * @param isLocal
	 * @param update
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws URISyntaxException
	 * @throws BadLocationException
	 * @throws InterruptedException 
	 */
	private Set<File> indexingArchive(Map<File, Map<Enum<?>, FileField>> files,
			String storeLocation, String isLocal, boolean update)
			throws IOException, ClassNotFoundException, InstantiationException,
			IllegalAccessException, URISyntaxException, BadLocationException, InterruptedException {

		Logger logger = super.logger.getLogger(ArchivesServiceImpl.class);

		String docDir = FileDirUtils.getRealPathByUnitId("doc_write_Dir",
				storeLocation, isLocal);
		String contentDir = FileDirUtils.getRealPathByUnitId("doc_content_Dir",
				storeLocation, isLocal);
		String readDir = FileDirUtils.getRealPathByUnitId("doc_read_Dir",
				storeLocation, isLocal);

		boolean reload = false;
		IIndexWriter writer = null;
		File iFolder = new File(docDir);
		Set<File> falses = new java.util.HashSet<File>();

		try {
			writer = IndexFactory.getInstance().getIndexWriter(iFolder,
					AnalyzerFactory.getInstance().getAnalyzer());

			File contents = new File(contentDir);
			File readFile = new File(docDir);
			File writeFile = new File(readDir);
			
			if (!contents.exists())
				contents.mkdirs();
			
			if (!readFile.exists())
				contents.mkdirs();
			
			if (!writeFile.exists())
				contents.mkdirs();

			Set<File> keys = files.keySet();

			for (File file : keys) {
				Map<Enum<?>, FileField> params = files.get(file);

				try {
					if (update) {
						writer.updateDocument(file, contents, params);
					} else {
						writer.addDocument(file, contents, params);
					}
 
					reload = true;
				} catch (Exception ex) {
					logger.warn("���ĵ����ӵ�������ʧ�ܻ��߸����ĵ�ID�����ĵ�ʧ��:", ex);
					falses.add(file);
				}
			}
		} finally {
			// �ر������ļ�
			if (writer != null)
				IndexFactory.getInstance().close(iFolder);

			// �Ƿ�ʵʱ���ؼ����ļ�
			String realTime = Configurater.getInstance().getConfigValue(
					"realtime_Reaload");
			if (reload && "true".equals(realTime)) {
				// �ͷ�read
				IndexSearchManager.getInstance().releaseSearch(
						new File(readDir));
				if (IndexSearchManager.getInstance().initReadFolder(iFolder,
						new File(readDir))) {
					IndexSearchManager.getInstance().reloadSingleFile(
							new File(readDir));
				}
			}
			for (File file : falses) {
				files.remove(file);
			}
		}

		return falses;
	}

	/**
	 * ��ȡ�ĵ���������б�
	 */
	public List<TDocType> docTypeList(String id, String flag) {
		String sql = "";

		if ("".equals(id) || id == null) {
			sql = "SELECT * FROM T_DOC_TYPE WHERE PARENT_CODE IS NULL";
		} else {
			sql = "SELECT * FROM T_DOC_TYPE WHERE CODE LIKE  '" + id
					+ "%'  and PARENT_CODE IS NULL";
		}

		List<TDocType> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TDocType.class);

		return list;
	}

	/**
	 * ����������ҳ��ѯ
	 * 
	 * @param map
	 * @param firstResult
	 *            ��ʼ��¼����
	 * @param maxResults
	 *            ������¼����
	 * @return
	 * @throws Exception
	 */
	public List<Object[]> queryTdocTypeList(Object[] obj, int firstResult,
			int maxResults, TableBean table) throws Exception {

		Long count = super.generalDAO.getResultCount("archieveTdocType", obj);

		table.setRecords(count.intValue());

		return count.intValue() == 0 ? null : super.generalDAO.getResult(
				"archieveTdocType", obj, firstResult, maxResults);

	}

	public List filePathProduce(String fileType) {
		// TODO Auto-generated method stub
		return null;
	}

	// ��ѯ��ɫ��Ϣ
	public List<TRole> TRoleList() {

		String sql = "SELECT * FROM T_ROLE ";

		List<TRole> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TRole.class);

		return list;
	}

	/**
	 * �õ��ļ�����޸�ʱ��
	 */
	public Date getDocLastUpdate(String loginId, String logType) {
		String sql = "select * from t_operate_log t where t.type='" + logType
				+ "' and operator='" + loginId
				+ "' and rownum=1 order by t.operate_time desc";
		List list = super.generalDAO.queryByNativeSQLEntity(sql,
				TOperateLog.class);
		if (list.size() == 0) {
			return null;
		} else {
			return ((TOperateLog) list.get(0)).getOperateTime();
		}
	}

	// �ؽ����� ��ѯ
	public List<Object[]> creatIndexingTDoc(Object[] obj) {
		List<Object[]> list = super.generalDAO.getResult("archieveIndexing",
				obj);

		return list;
	}

	// ������ѯTDoc
	public List<Object> batchQueryTDoc(String flag) {
		String sql = "select * from t_doc t where t.meta_flag=" + flag;
		List<Object> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TDoc.class);
		return list;
	}

	// ��������ͼ ��ѯ
	public List<Object[]> creatThumbnailsTDoc(Object[] obj, String flag) {
		List<Object[]> list = null;
		if ("1".equals(flag)) {
			list = super.generalDAO.getResult("archieveThumbnails1", obj);
		} else {
			list = super.generalDAO.getResult("archieveThumbnails2", obj);
		}
		return list;
	}

	/**
	 * ���ݷ�������ѯdoc
	 * 
	 * @param docTypeCode
	 *            �������
	 * @return
	 */
	public List<TDoc> getDocByDocTypeCode(String docTypeCode) {
		String sql = "select * from t_doc t where t.doc_type_code='"
				+ docTypeCode + "'";
		List<TDoc> list = super.generalDAO.queryByNativeSQLEntity(sql,
				TDoc.class);
		return list;
	}

	public String removeDocType(String[] codeList) {
		String returnValue = "";
		for (int i = 0; i < codeList.length; i++) {
			String eachCode = codeList[i];
			TDocType type = new TDocType();
			type.setParentCode(eachCode);
			List<Object> listdoc = super.EntityQuery(type);
			if (listdoc.size() != 0) {
				returnValue = "true";
			} else {
				TDocType doctype = (TDocType) super.EntityQuery(TDocType.class,
						eachCode);
				super.delEntity(doctype);
				returnValue = "success";

			}
		}
		return returnValue;
	}

	/**
	 * ��ѯ�ĵ�
	 */
	public List queryDoc(Map<String, Object> map) {
		return super.generalDAO.getResult("docSearch", map);
	}

	/**
	 * �����ĵ�ID���ҹ���
	 * 
	 * @param docId
	 * @return
	 */
	public TProject getTProjectBydocId(String docId) {
		List<TProject> list = super.generalDAO.getResult("updateArchivesIndex",
				new Object[] { docId });
		if(null != list && list.size() > 0){
			list.get(0).getTUnitByOwnerUnitId().getName();
			list.get(0).getTUnitByDesignUnitId().getName();
			return list.get(0);
		}
		return null;
	}

	/**
	 * ��������״̬
	 * 
	 * @param folder
	 * @param docids
	 * @throws Exception
	 */
	public void updateIndex(String isLocal, String docid, String storeLocation)
			throws Exception {
		TDoc tdoc = (TDoc) super.EntityQuery(TDoc.class, docid);
		TProject tpro = this.getTProjectBydocId(tdoc.getId());

		this.downloadFileToIndexUpdate(isLocal, tdoc, tpro, storeLocation,true);
	}

	/**
	 * ͬ�����ø�������״̬
	 * 
	 * @param isLocal
	 * @param tdoc
	 * @param tpro
	 * @param unit
	 * @throws Exception
	 */
	public void updateIndex(String isLocal, TDoc tdoc, TProject tpro,
			String storeLocation,boolean isIndex) throws Exception {
		this.downloadFileToIndexUpdate(isLocal, tdoc, tpro, storeLocation, isIndex);
	}

	/**
	 * 
	 * @param filePath
	 *            �ļ�·��
	 * @param tdoc
	 *            �ļ�ʵ��
	 * @param tpro
	 *            ����ʵ��
	 * @param unit
	 * @param unitCode
	 * @param isLocal
	 * @throws Exception
	 * @throws IOException
	 */
	private void downloadFileToIndexUpdate(String isLocal, TDoc tdoc,
			TProject tpro, String storeLocation, boolean isIndex) throws Exception, IOException {
		Map<File, Map<Enum<?>, FileField>> files = new HashMap<File, Map<Enum<?>, FileField>>();
		String tempDir = Configurater.getInstance().getConfigValue("temp");

		File tempFolder = new File(tempDir);

		if (!tempFolder.isDirectory()) {
			tempFolder.mkdirs();
		}

		ContinueFTP ftpUtil = ContinueFTP.getInstance();
		String remotePath = tdoc.getPath();

		String local = tempFolder.getAbsolutePath() + File.separator
				+ tdoc.getName() + "." + tdoc.getSuffix();
		File file = new File(local);
		ftpUtil.download(remotePath, local);

		if (file.exists() && file.canRead()) {

			String mimeType = Configurater.getInstance().getConfigValue("mime",
					FilenameUtils.getExtension(file.getName()));
			if (mimeType == null) {
				mimeType = Configurater.getInstance().getConfigValue("mime",
						"*");
			}

			String dest = local;

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
			if(isIndex){
				files.put(new File(dest), this.setArchivesIndex(tdoc, tpro, null,
						tpro.getTUnitByOwnerUnitId()));
				this.updateArchiveIndex(files, storeLocation, isLocal);
			}
		}
	}

	public List queryDocIsArchives(Map<String, Object> map) {
		// TODO Auto-generated method stub
		return super.generalDAO.getResult("docSearchIsArchives", map);
	}

	public void saveOrUpdateSynDoc(TSynDoc doc) throws Exception {
		TSynDoc docTemp = (TSynDoc) super.generalDAO.findByID(TSynDoc.class,
				doc.getId());
		if (null != docTemp) {
			docTemp.setName(doc.getName());
			docTemp.setPath(doc.getPath());
			docTemp.setPreviewPath(doc.getPreviewPath());
			docTemp.setSuffix(doc.getSuffix());
			docTemp.setTSynProject(doc.getTSynProject());
			//ȥ���ظ�ֵ
			String store = FileStoreLocation.delRepeated(doc.getStoreLocation());
			docTemp.setStoreLocation(store);
			super.EntityUpdate(docTemp);
		} else {
			super.EntityAdd(doc);
		}
	}

	public void saveOrUpdateSynProject(TSynProject project) {
		TSynProject projectTemp = (TSynProject) super.generalDAO.findByID(
				TSynProject.class, project.getCode());
		if (null != projectTemp) {
			projectTemp.setApproveUnitid(project.getApproveUnitid());
			projectTemp.setGiveoutUnitid(project.getGiveoutUnitid());
			projectTemp.setOwnerUnitId(project.getOwnerUnitId());
			projectTemp.setTSynDocs(project.getTSynDocs());
			projectTemp.setName(project.getName());
			super.EntityUpdate(projectTemp);
		} else {
			super.EntityAdd(project);
		}
	}

	public List<TDoc> queryDocByIds(String[] ids) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("in", ids);
		return (List<TDoc>) super.generalDAO.getResult("docSearcherByIds",
				param);
	}

	public void deleteSynDoc(String docId) {
		TSynDoc docTemp = (TSynDoc) super.generalDAO.findByID(TSynDoc.class,
				docId);
		if (docTemp != null)
			super.EntityDel(docTemp);
	}

	/**
	 * ��û�в��������Ĺ��̲�ѯ�ĵ�
	 * 
	 * @return
	 */
	public List<TDoc> getDocByNoIndexProject(Object[] projectIds) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("in", projectIds);
		return (List<TDoc>) super.generalDAO.getResult(
				"getDocByNoIndexProject", param);
	}

	/************************************* ����ͬ�� ***************************************************/

	/**
	 * Ǩ������, ����ͬ��ʱʹ��
	 * 
	 * @param oldPath
	 *            ��·��
	 * @param newPath
	 *            ��·��
	 * @param doc
	 *            doc����
	 * @param fid
	 *            �ļ�id
	 * @param pro
	 *            ���̶���
	 * @param storeLocation
	 *            �����洢λ��
	 * @param tempDir
	 *            ��ʱĿ¼
	 * @throws Exception 
	 */
	public void removeIndex(String isLocal, String oldPath, String newPath,
		TDoc doc,TProject pro, String storeLocation, boolean isIndex) throws Exception{
		Logger logger = super.logger.getLogger(ArchivesServiceImpl.class);
		try{
			doc.setStatus("01");			
			doc.setPath(oldPath);
			newPath = newPath.replaceAll("\\\\", "/");
			if (newPath.contains("/")) {
				// ����������Զ��Ŀ¼�ṹ
				ContinueFTP ftpm = ContinueFTP.getInstance();
				ftpm.CreateDirecroty(DesUtil.getFilePath(newPath),
						ftpm.ftpClient);
				ftpm.rename(oldPath, newPath);
				ftpm.disconnect();
				
			}
			StoreFileReceiveID.createInstance().addSuccessFileID(doc.getId());
			doc.setPath(newPath);   
			this.updateIndex(isLocal,doc, pro, storeLocation,isIndex);
		}catch(Exception e){
			StoreFileReceiveID.createInstance().addSuccessFileID(doc.getId());
			logger.error("Ǩ���ļ�ʧ��:", "05", "�ļ�:" + doc.getName() + "Ǩ���ļ�ʧ��", e);
			throw new Exception("Ǩ���ļ�ʧ��:" + e.getMessage());
		}
	}

	/**
	 * �������� ����ͬ��ʱʹ��
	 * 
	 * @param unit
	 *            Ҫ�������û���λ ��String ����
	 * @param ftpDoc
	 *            ftp��document����
	 * @param tempDir
	 *            ��ʱĿ¼
	 * @param doc
	 *            doc����
	 * @param pro
	 *            projec����
	 * @param doctype
	 *            doctype����
	 * @param file
	 *            �ļ�
	 * @param userUnit
	 *            �û����ڵĵ�λ
	 * @param tofileName
	 * @throws Exception
	 */
	public void archivesIndex(String unit, String tempDir, TDoc doc,
			TProject pro, String storeLocation,
			String isLocal, boolean isIndex) throws Exception {
		Logger logger = super.logger.getLogger(ArchivesServiceImpl.class);
		Configurater config = Configurater.getInstance();
		String[] ftpInfo = FtpXmlUtils.getFTPInfo(unit);
		ContinueFTP ftp = ContinueFTP.getDownLoadInstance(ftpInfo[0], Integer.parseInt(ftpInfo[1]), ftpInfo[2], ftpInfo[3]);
		if (ftp != null) {
			File tempFolder = new File(tempDir);
			if (!tempFolder.isDirectory()) {
				tempFolder.mkdirs();
			}
			Map<File, Map<Enum<?>, FileField>> files = new HashMap<File, Map<Enum<?>, FileField>>();
			Map<File, TDoc> docs = new HashMap<File, TDoc>();

			String remotePath = doc.getPath();
			String local = tempFolder.getAbsolutePath() + File.separator
					+ doc.getName() + "." + doc.getSuffix();
			File sourceFile = new File(local);
			File decryptFile = null;
			
			/**
			 * ����ļ��������������������ʧ�ܴ���
			 * Զ���ļ�������
			 * �ϵ�����������
			 * �������ļ�ʧ��
			 */
			DownloadStatus downStatus = ftp.download(remotePath, local);
			if(downStatus.equals(DownloadStatus.Remote_File_Noexist) ||
					downStatus.equals(DownloadStatus.Download_From_Break_Failed) ||
					downStatus.equals(DownloadStatus.Download_New_Failed)){
				StoreFileReceiveID.createInstance().addFailFileID(doc.getId());
				return;
			}
			
			if (sourceFile.exists() && sourceFile.canRead()) {
				String mimeType = Configurater.getInstance().getConfigValue(
						"mime", doc.getSuffix());
				if (mimeType == null) {
					mimeType = config.getConfigValue(
							"mime", "*");
				}
				String dest = local;
				// ��ͼƬ��Ҫ����
				if (mimeType.indexOf("image") == -1) {
					dest = Configurater.getInstance().getConfigValue("decrypt");
					File destPathFolder = new File(sourceFile.getParentFile(), dest);
					if (!destPathFolder.isDirectory())
						destPathFolder.mkdirs();
					dest = destPathFolder.getAbsolutePath() + File.separator
							+ sourceFile.getName();
					DesUtil.decrypt(local, dest);
				}
				
				decryptFile = new File(dest);
				doc.setStatus("01");
				docs.put(decryptFile, doc);
				files.put(decryptFile, this.setArchivesIndex(doc, pro, dest, pro.getTUnitByOwnerUnitId()));
				
				// �ϴ������ط�����
				try{
					ContinueFTP.getInstance().upload(local, doc.getPath());
					StoreFileReceiveID.createInstance().addSuccessFileID(doc.getId());
				}catch(Exception e){
					logger.warn(e.getMessage());
					StoreFileReceiveID.createInstance().addFailFileID(doc.getId());
				}
			}
			if(isIndex){
			    this.addArchiveIndex(files, storeLocation,isLocal);
			}
			// ����ͬһ�ļ��������ڽ����ļ������δ�����ļ�
			if (!sourceFile.equals(decryptFile)){
				sourceFile.delete();
			}
			if(decryptFile.exists()){
				decryptFile.delete();
			}
		}else{
			logger.warn("����FTP����ʧ�ܣ�");
		}
	}
	
	

}
