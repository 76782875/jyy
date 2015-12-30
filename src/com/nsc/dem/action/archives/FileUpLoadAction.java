package com.nsc.dem.action.archives;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;

import com.nsc.base.util.FileUtil;
import com.nsc.base.util.GetCh2Spell;
import com.nsc.dem.action.BaseAction;
import com.nsc.dem.bean.archives.TDoc;
import com.nsc.dem.bean.archives.TDocType;
import com.nsc.dem.bean.project.TDocProject;
import com.nsc.dem.bean.project.TPreDesgin;
import com.nsc.dem.bean.project.TProject;
import com.nsc.dem.service.archives.IarchivesService;
import com.nsc.dem.service.project.IprojectService;
import com.nsc.dem.service.system.IdictionaryService;
import com.nsc.dem.util.filestore.FileStoreLocation;
import com.nsc.dem.util.index.IndexStoreUitls;
import com.nsc.dem.util.log.Logger;
import com.opensymphony.xwork2.ActionContext;

public class FileUpLoadAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	private List<TDocType> tDocTypeList;
	private List<TDoc> listResult;
	private String projectName;
	private String dftSecurity;
	private String remark;
	private String projectNameCode;
	private String docTypeCode;
	private String docTypeName;
	private String docTypeParentCode;

	private String tdocId; // �ļ� ID
	private String docTypeId; // �ĵ����� ID
	private String projectId; // ���̶���ID
	private String fileStatus; // �ļ�״̬

	IarchivesService archivesService;
	IdictionaryService dictionaryService;
	IprojectService projectService;

	private List<File> Filedata;
	private List<String> FiledataFileName;
	private List<String> FiledataContentType;

	private List<Map<String, Object>> list;

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

	public List<TDocType> gettDocTypeList() {
		return tDocTypeList;
	}

	public void settDocTypeList(List<TDocType> tDocTypeList) {
		this.tDocTypeList = tDocTypeList;
	}

	public void setArchivesService(IarchivesService archivesService) {
		this.archivesService = archivesService;
	}

	public void setDictionaryService(IdictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setProjectService(IprojectService projectService) {
		this.projectService = projectService;
	}

	/**
	 * ��ʼ���ĵ������б�
	 * 
	 * @return
	 */
	public String initTree() {
		String id = "";
		String options = getRequest().getParameter("options");
		if (options != null) {
			String[] condition = options.split(";");
			for (int i = 0; i < condition.length; i++) {
				if (condition[i].indexOf("sub_doc_type") > -1) {
					docTypeCode = condition[i].substring(condition[i]
							.indexOf("=") + 1);
					TDocType tDocType = (TDocType) archivesService.EntityQuery(
							TDocType.class, docTypeCode);
					docTypeParentCode = tDocType.getParentCode();
				} else if (condition[i].indexOf("project") > -1) {
					id = condition[i].substring(condition[i].indexOf("=") + 1);

				}
			}
			TProject project = (TProject) projectService.EntityQuery(
					TProject.class, Long.parseLong(id));
			projectName = project.getName();
			projectNameCode = project.getCode();
		}

		tDocTypeList = archivesService.docTypeList("", "");
		for (int i = 0; i < tDocTypeList.size(); i++) {
			TDocType tDocType = tDocTypeList.get(i);
			List<TDocType> listChild = archivesService.docTypeList(tDocType
					.getCode());
			tDocType.setList(listChild);
		}

		return "initTree";
	}

	/**
	 * ɾ���Ѿ��ϴ��ļ�
	 * 
	 * @throws UnsupportedEncodingException
	 */
	public void deleteFile() throws UnsupportedEncodingException {
		String dfileName = getRequest().getParameter("dfileName");
		String[] dfNames = dfileName.split(",");
		HttpSession session = getSession();

		if (session.getAttribute("fileData") != null) {
			@SuppressWarnings("unchecked")
			List<File> fileList = (List<File>) session.getAttribute("fileData");
			@SuppressWarnings("unchecked")
			List<String> fileNameList = (List<String>) session
					.getAttribute("fileName");
			for (int i = 0; i < dfNames.length; i++) {
				File f = (File) fileList.get(Integer.parseInt(dfNames[i]));
				FileUtil.deleteFile(f.getAbsolutePath());
			}
			if (fileList.size() == 0) {
				// ���session�е��ļ����ļ���
				session.removeAttribute("fileData");
				session.removeAttribute("fileName");
			} else {
				int j = 0;
				for (int i = 0; i < dfNames.length; i++) {
					fileList.remove(Integer.parseInt(dfNames[i]) - j);
					fileNameList.remove(Integer.parseInt(dfNames[i]) - j);
					j++;
				}
				session.setAttribute("fileData", fileList);
				session.setAttribute("fileName", fileNameList);
			}
			if (fileList.size() == 0) {
				// ���session�е��ļ����ļ���
				session.removeAttribute("fileData");
				session.removeAttribute("fileName");
			}
		}
	}

	/**
	 * ��ʼ����������
	 * 
	 * @return
	 */
	public String projectName() {
		String type = getRequest().getParameter("tid");
		type = type.substring(0, 2); // �õ��ĵ������ǰ��λ
		String code = super.getLoginUser().getTUnit().getProxyCode(); // ҵ����λ����
		// String code = "CN001";
		list = new ArrayList<Map<String, Object>>();
		List<TProject> tProjectList = projectService.unitProList(code, type);
		if (tProjectList.size() == 0) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("id", "-1");
			map.put("name", "û������");
			map.put("spell", GetCh2Spell.getBeginCharacter("û������"));
			list.add(map);
		} else {
			for (int i = 0; i < tProjectList.size(); i++) {
				TProject tProject = tProjectList.get(i);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("id", tProject.getId());
				map.put("code", tProject.getCode());
				map.put("name", tProject.getName());
				map.put("spell", GetCh2Spell.getBeginCharacter(tProject
						.getName()));
				map.put("other", tProject.getType());
				list.add(map);
			}
		}

		return SUCCESS;
	}

	/**
	 * �ļ�¼��
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String entryFile() {
		Logger logger = super.logger.getLogger(FileUpLoadAction.class);
		logger.info("��������:" + projectName);
		logger.info("����ȼ�:" + dftSecurity);
		logger.info("��ע:" + remark);
		// ��ȡsession�б������ʱ�ļ�
		HttpSession session = getSession();
		listResult = new ArrayList<TDoc>();
		try {
			logger.info("sessionid: " + session.getId());
			if (session.getAttribute("fileData") != null) {
				List<File> fileList = (List<File>) session
						.getAttribute("fileData");
				List<String> fileNameList = (List<String>) session
						.getAttribute("fileName");
				for (int i = 0; i < fileList.size(); i++) {
					File file = fileList.get(i);
					String fileName = fileNameList.get(i);
					String fileFormat = FileUtil.getFileFormat(fileName);
					String fileSuffix = fileName.substring(fileName
							.lastIndexOf("."));
					long fileSize = file.length();
					String[] code = docTypeCode.split("/");
					TDocType tDocType = (TDocType) archivesService.EntityQuery(
							TDocType.class, code[0]);
					docTypeName = tDocType.getName();

					TDoc tDoc = new TDoc();
					fileName = fileName.substring(0, fileName.lastIndexOf("."));
					tDoc.setName(fileName.replaceAll("'", ""));
					tDoc.setFormat(fileFormat);
					fileSuffix = fileSuffix.substring(1);
					tDoc.setSuffix(fileSuffix);
					tDoc.setRemark(remark);
					// Ԫ�����ļ�,�ļ�·��,Ԥ��ͼ·�� ,״̬����,�����汾service�� ȷ��
					tDoc.setFileSize(BigDecimal.valueOf(fileSize));
					tDoc.setTDocType(tDocType);
					tDoc.setSecurity(dftSecurity);
					tDoc.setStatus("03");
					Date date = new Date();
					Timestamp nousedate = new Timestamp(date.getTime());
					tDoc.setCreateDate(nousedate);
					tDoc.setTUser(super.getLoginUser());
					tDoc.setStoreLocation(FileStoreLocation.getStoreLocation());
					
					TProject tProject = new TProject();
					tProject.setCode(projectNameCode);
					tProject.setName(projectName);
					List<Object> tProjectList = archivesService
							.EntityQuery(tProject);
					if (tProjectList.size() != 0) {
						tProject = (TProject) tProjectList.get(0);
					}
					TDocProject tDocProject = new TDocProject(); // ����

					TPreDesgin tPreDesgin = new TPreDesgin();
					tPreDesgin.setTDoc(tDoc);
					tPreDesgin.setTUnit(super.getLoginUser().getTUnit());
					tPreDesgin.setCreateDate(tPreDesginT.getCreateDate());

					tPreDesgin.setSjrm(tPreDesginT.getSjrm());
					tPreDesgin.setXhrm(tPreDesginT.getXhrm());
					tPreDesgin.setShrm(tPreDesginT.getShrm());
					tPreDesgin.setPzrm(tPreDesginT.getPzrm());
					tPreDesgin.setLjrm(tPreDesginT.getLjrm());
					tPreDesgin.setJcrm(tPreDesginT.getJcrm());
					tPreDesgin.setSjjd(tPreDesginT.getSjjd());
					tPreDesgin.setAjtm(tPreDesginT.getAjtm());
					tPreDesgin.setTzmc(tPreDesginT.getTzmc());
					tPreDesgin.setTzzs(tPreDesginT.getTzzs());
					tPreDesgin.setFlbh(tPreDesginT.getFlbh());
					tPreDesgin.setAndh(tPreDesginT.getAndh());
					tPreDesgin.setJnyh(tPreDesginT.getJnyh());

					String loginId = super.getLoginUser().getLoginId();

					archivesService.insertArchives(tDoc, tDocProject,
							tPreDesgin, tProject, null, null, tDocType,
							loginId, file.getAbsolutePath());

					FileUtil fileUtil = new FileUtil();
					String docFileSize = fileUtil.getHumanSize(tDoc
							.getFileSize().longValue());// ��С
					tDoc.setDocFileSize(docFileSize);

					logger.info("��ʱ�ļ�:" + file);
					logger.info("�ļ���:" + fileName);
					logger.info("�ļ���ʽ:" + fileFormat);
					logger.info("�ļ���׺:" + fileSuffix);
					logger.info("�ļ���С:" + fileSize);

					listResult.add(tDoc);
				}
			}
			getRequest().setAttribute("message", "�ϴ��ɹ�!");
		} catch (Exception e) {
			getRequest().setAttribute("message", "�ϴ�ʧ��!");
			logger.warn("�ϴ�ʧ��:", e);
		} finally {
			getRequest().setAttribute("pageTitle", "����¼��");
			// ɾ�����ϴ��ļ�
			if (session.getAttribute("fileData") != null) {
				List<File> fileList = (List<File>) session
						.getAttribute("fileData");
				for (int i = 0; i < fileList.size(); i++) {
					File file = fileList.get(i);
					String fPath = file.getAbsolutePath();
					logger.info(fPath);
					FileUtil.deleteFile(fPath);
				}
				// ���session�е��ļ����ļ���
				session.removeAttribute("fileData");
				session.removeAttribute("fileName");
			}
		}
		return "display";
	}

	/**
	 * �ļ�����
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String fileUpdate() {
		Logger logger = super.logger.getLogger(FileUpLoadAction.class);
		// ��ȡsession�б������ʱ�ļ�
		HttpSession session = getSession();
		listResult = new ArrayList<TDoc>();
		try {
			String loginId = super.getLoginUser().getLoginId();
			TPreDesgin tPreDesgin = (TPreDesgin) archivesService.EntityQuery(
					TPreDesgin.class, tdocId); // ���赵����
			if (tPreDesgin != null) {
				tPreDesgin.setCreateDate(tPreDesginT.getCreateDate());
				tPreDesgin.setTUnit(super.getLoginUser().getTUnit());
				tPreDesgin.setSjrm(tPreDesginT.getSjrm());
				tPreDesgin.setXhrm(tPreDesginT.getXhrm());
				tPreDesgin.setShrm(tPreDesginT.getShrm());
				tPreDesgin.setPzrm(tPreDesginT.getPzrm());
				tPreDesgin.setLjrm(tPreDesginT.getLjrm());
				tPreDesgin.setJcrm(tPreDesginT.getJcrm());
				tPreDesgin.setSjjd(tPreDesginT.getSjjd());
				tPreDesgin.setAjtm(tPreDesginT.getAjtm());
				tPreDesgin.setTzmc(tPreDesginT.getTzmc());
				tPreDesgin.setTzzs(tPreDesginT.getTzzs());
				tPreDesgin.setFlbh(tPreDesginT.getFlbh());
				tPreDesgin.setAndh(tPreDesginT.getAndh());
				tPreDesgin.setJnyh(tPreDesginT.getJnyh());
			}

			if (session.getAttribute("fileData") != null) {
				List<File> fileList = (List<File>) session
						.getAttribute("fileData");
				List<String> fileNameList = (List<String>) session
						.getAttribute("fileName");
				for (int i = 0; i < fileList.size(); i++) {
					File file = fileList.get(i);
					String fileName = fileNameList.get(i);
					String fileFormat = FileUtil.getFileFormat(fileName);
					String fileSuffix = fileName.substring(fileName
							.lastIndexOf("."));
					long fileSize = file.length();

					TDocType tDocType = (TDocType) archivesService.EntityQuery(
							TDocType.class, docTypeId); // �ĵ������
					docTypeName = tDocType.getName();

					TDoc tDoc = (TDoc) archivesService.EntityQuery(TDoc.class,
							tdocId); // �ļ���
					fileName = fileName.substring(0, fileName.lastIndexOf("."));
					tDoc.setName(fileName);
					tDoc.setFormat(fileFormat);
					fileSuffix = fileSuffix.substring(1);
					tDoc.setSuffix(fileSuffix);
					tDoc.setRemark(remark);
					// Ԫ�����ļ�,�ļ�·��,Ԥ��ͼ·�� ,״̬����,�����汾service�� ȷ��
					tDoc.setFileSize(BigDecimal.valueOf(fileSize));
					tDoc.setTDocType(tDocType);
					tDoc.setSecurity(dftSecurity);
					tDoc.setStatus(fileStatus);
					Date date = new Date();
					Timestamp nousedate = new Timestamp(date.getTime());
					tDoc.setCreateDate(nousedate);
					tDoc.setTUser(super.getLoginUser());

					TDocProject tDocProject = new TDocProject(); // ����
					if (tPreDesgin != null) {
						tPreDesgin.setTDoc(tDoc);
					}
					String storeLocation = IndexStoreUitls.getStoreLocation(tdocId, this.getSession().getServletContext());
					// ����
					archivesService.updateArchives
					(tDoc, docTypeId, projectId,
							file.getAbsolutePath(), tPreDesgin, null, null,
							null, tDocProject, null, null, null, loginId,storeLocation, "local");

					FileUtil fileUtil = new FileUtil();
					String docFileSize = fileUtil.getHumanSize(tDoc
							.getFileSize().longValue());// ��С
					tDoc.setDocFileSize(docFileSize);

					logger.info("��ʱ�ļ�:" + file);
					logger.info("�ļ���:" + fileName);
					logger.info("�ļ���ʽ:" + fileFormat);
					logger.info("�ļ���׺:" + fileSuffix);
					logger.info("�ļ���С:" + fileSize);

					listResult.add(tDoc);
				}
			} else {
				// û���ϴ��ļ�
				TDocType tDocType = (TDocType) archivesService.EntityQuery(
						TDocType.class, docTypeId);
				docTypeName = tDocType.getName();
				TDocProject tDocProject = new TDocProject(); // ����
				TDoc tDoc = (TDoc) archivesService.EntityQuery(TDoc.class,
						tdocId); // �ļ���
				tDoc.setRemark(remark);
				tDoc.setStatus(fileStatus);
				tDoc.setSecurity(dftSecurity);
				if (tPreDesgin != null) {
					tPreDesgin.setTDoc(tDoc);
				}
				String storeLocation = IndexStoreUitls.getStoreLocation(tdocId,this.getSession().getServletContext());
				archivesService.updateArchives(tDoc, docTypeId, projectId,
						null, tPreDesgin, null, null, null, tDocProject, null,
						null, null, loginId,storeLocation,"local");
				FileUtil fileUtil = new FileUtil();
				String docFileSize = fileUtil.getHumanSize(tDoc.getFileSize()
						.longValue());// ��С
				tDoc.setDocFileSize(docFileSize);
				listResult.add(tDoc);
			}
			getRequest().setAttribute("message", "���³ɹ�!");
		} catch (Exception e) {
			getRequest().setAttribute("message", "����ʧ��!");
			logger.warn("����ʧ��:", e);
		} finally {
			getRequest().setAttribute("pageTitle", "��������");
			// ɾ�����ϴ��ļ�
			if (session.getAttribute("fileData") != null) {
				List<File> fileList = (List<File>) session
						.getAttribute("fileData");
				for (int i = 0; i < fileList.size(); i++) {
					File file = fileList.get(i);
					String fPath = file.getAbsolutePath();
					logger.info(fPath);
					FileUtil.deleteFile(fPath);
				}
				// ���session�е��ļ����ļ���
				session.removeAttribute("fileData");
				session.removeAttribute("fileName");
			}
		}
		return "display";
	}

	/**
	 * �ϴ��ļ�ʱ���ô˷���
	 * 
	 * @return
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public String doUpload() throws IOException {
		Logger logger = super.logger.getLogger(FileUpLoadAction.class);
		// ����ļ�����������
		// ���ļ����浽Ӳ����
		File file = this.saveFile();
		String fName = (String) FiledataFileName.get(0);
		// ��ת���õ��ļ������·�������
		List<String> fNameList = new ArrayList<String>();
		fNameList.add(fName);
		// ���ϴ��õ���ʱ�ļ����·�������
		List<File> fDataList = new ArrayList<File>();
		fDataList.add(file);
		HttpSession session = getSession();
		session.setMaxInactiveInterval(1000 * 60 * 30);// ��λ����
		logger.info("sessionid: " + session.getId());

		if (session.getAttribute("fileData") == null) {
			// �ϴ���һ���ļ�
			session.setAttribute("fileData", fDataList);
			session.setAttribute("fileName", fNameList);
		} else {
			// �������ļ����浽session��
			List<File> filedata = (List<File>) session.getAttribute("fileData");
			filedata.addAll(fDataList);

			List<String> fileName = (List<String>) session
					.getAttribute("fileName");
			fileName.addAll(fNameList);
		}

		// ҳ�淵������
		HttpServletResponse response = getResponse();
		try {
			response.getWriter().print("�ϴ��ɹ�!");
		} catch (IOException e) {
			logger.warn("�������Ӧ�쳣:", e);
		}

		return null;
	}

	/**
	 * �ļ��ϴ�
	 */
	public File saveFile() throws IOException {
		Logger logger = super.logger.getLogger(FileUpLoadAction.class);
		ActionContext ac = ActionContext.getContext();
		ServletContext sc = (ServletContext) ac
				.get(ServletActionContext.SERVLET_CONTEXT);
		String savePath = sc.getRealPath("/");
		savePath = savePath + "uploads/";
		logger.info("�ϴ�·����------->" + savePath);
		File f1 = new File(savePath);
		if (!f1.exists()) {
			f1.mkdirs();
		}
		String extName = FiledataFileName.get(0).substring(
				FiledataFileName.get(0).lastIndexOf("."));
		String name = UUID.randomUUID().toString();
		File file = new File(savePath + name + extName);
		FileUtils.copyFile(Filedata.get(0), file);
		return file;
	}

	public List<File> getFiledata() {
		return Filedata;
	}

	public void setFiledata(List<File> filedata) {
		Filedata = filedata;
	}

	public List<String> getFiledataFileName() {
		return FiledataFileName;
	}

	public void setFiledataFileName(List<String> filedataFileName) {
		FiledataFileName = filedataFileName;
	}

	public List<String> getFiledataContentType() {
		return FiledataContentType;
	}

	public void setFiledataContentType(List<String> filedataContentType) {
		FiledataContentType = filedataContentType;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getDftSecurity() {
		return dftSecurity;
	}

	public void setDftSecurity(String dftSecurity) {
		this.dftSecurity = dftSecurity;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getProjectNameCode() {
		return projectNameCode;
	}

	public void setProjectNameCode(String projectNameCode) {
		this.projectNameCode = projectNameCode;
	}

	public String getDocTypeCode() {
		return docTypeCode;
	}

	public void setDocTypeCode(String docTypeCode) {
		this.docTypeCode = docTypeCode;
	}

	public List<TDoc> getListResult() {
		return listResult;
	}

	public void setListResult(List<TDoc> listResult) {
		this.listResult = listResult;
	}

	public void setTdocId(String tdocId) {
		this.tdocId = tdocId;
	}

	public void setDocTypeId(String docTypeId) {
		this.docTypeId = docTypeId;
	}

	public void setProjectId(String projectId) {
		this.projectId = projectId;
	}

	public void setFileStatus(String fileStatus) {
		this.fileStatus = fileStatus;
	}

	private TPreDesgin tPreDesginT;

	public TPreDesgin gettPreDesginT() {
		return tPreDesginT;
	}

	public void settPreDesginT(TPreDesgin tPreDesginT) {
		this.tPreDesginT = tPreDesginT;
	}

	public String getDocTypeName() {
		return docTypeName;
	}

	public void setDocTypeName(String docTypeName) {
		this.docTypeName = docTypeName;
	}

	public String getDocTypeParentCode() {
		return docTypeParentCode;
	}

	public void setDocTypeParentCode(String docTypeParentCode) {
		this.docTypeParentCode = docTypeParentCode;
	}
}
