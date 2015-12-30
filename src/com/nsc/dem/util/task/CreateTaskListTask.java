package com.nsc.dem.util.task;

import static com.nsc.base.util.DateUtils.DateToString;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;
import javax.servlet.ServletContext;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.nsc.base.conf.Configurater;
import com.nsc.base.task.TaskBase;
import com.nsc.base.util.Component;
import com.nsc.base.util.DataSynStatus;
import com.nsc.dem.bean.archives.TDoc;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.bean.project.TProject;
import com.nsc.dem.bean.system.TOperateLogTemp;
import com.nsc.dem.service.archives.IarchivesService;
import com.nsc.dem.service.project.IprojectService;
import com.nsc.dem.service.system.IlogService;
import com.nsc.dem.util.log.Logger;
import com.nsc.dem.util.xml.DataSynXmlUtils;
import com.nsc.dem.util.xml.FailFileIDXMLUtils;
import com.nsc.dem.util.xml.XmlUtils;
import com.nsc.dem.webservice.client.WSClient;

/**
 * �����ļ�ͬ���嵥
 */
public class CreateTaskListTask extends TaskBase implements Job{

	private IlogService logService;
	private IarchivesService archivesService;
	private IprojectService projectService;
	private Logger logger = null;
	private TUser user = null;
	
	public CreateTaskListTask(String taskName, ServletContext context, long period) {
		super(taskName, context, period);
		logService = (IlogService) Component.getInstance("logService",super.context);
		archivesService = (IarchivesService) Component.getInstance("archivesService", super.context);
		projectService = (IprojectService) Component.getInstance("projectService", super.context);
		user = (TUser) logService.EntityQuery(TUser.class, Configurater
				.getInstance().getConfigValue("ws_user"));
		logger = logService.getLogManager(user).getLogger(
				CreateTaskListTask.class);
	}

	
	public CreateTaskListTask(){
		super(null,Configurater.getInstance().getServletContext(),0);
		logService = (IlogService) Component.getInstance("logService",super.context);
		archivesService = (IarchivesService) Component.getInstance("archivesService", super.context);
		projectService = (IprojectService) Component.getInstance("projectService", super.context);
		user = (TUser) logService.EntityQuery(TUser.class, Configurater
				.getInstance().getConfigValue("ws_user"));
		logger = logService.getLogManager(user).getLogger(
				CreateTaskListTask.class);
	}

	public void execute(JobExecutionContext context)
			throws JobExecutionException {
		String taskName = context.getTrigger().getKey().getName();
		logger.info("����[ " + taskName + " ]������ "
					+ DateToString(context.getFireTime(), "yyyy-MM-dd HH:mm:ss"));
		try{
			doTask();
		}catch(Exception e){
			throw new JobExecutionException(e);
		}finally{
			logger.info("����[ "+ taskName+ " ]�´ν���"
					+ DateToString(context.getNextFireTime(),"yyyy-MM-dd HH:mm:ss") + " ����");
		}		
	}	
	
	@Override
	public void doTask() throws Exception {
		try{
			//�����嵥===>>�����嵥===>>�����嵥
			createTaskListXml();
			plotTask();
			sendTask();
		}catch(Exception e){
			logger.warn(e);
		}
	}
	
	
	/**
	 * �����嵥�ļ�
	 * 
	 */
	private void createTaskListXml() {
		//ɾ���ļ��������嵥��������ǰ��
		deleteFile();
		//����
		insertFile();
		//��������
		updateAndOtherFile();
		//ɾ����־��ʱ������
		deleteAllOperateLog();
	}
	
	/**
	 * ��ѯ�鵵�����£���Ǩ�ƣ����ָ��������ļ�
	 */
	@SuppressWarnings("unchecked")
	private void updateAndOtherFile() {
		// ��ѯ�鵵�����£���Ǩ�ƣ����ָ��������ļ�ʱ��������־
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("in", new Object[] { DataSynStatus.ARCHIVE.toString(),DataSynStatus.UPDATE.toString(),
				DataSynStatus.DESTROY.toString(),DataSynStatus.RESTORE.toString()});
		List<TOperateLogTemp> list = logService.findOperateTempLog(map);
		for (TOperateLogTemp operateLog : list) {
			String content = operateLog.getContent();
			// ��ȡID
			String docId = content.substring(content.lastIndexOf("id=") + 3,content.length());
			// ��ѯ���ĵ��Ƿ����
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("in", new Object[] { docId });
			List<TDoc> docList = archivesService.queryDocIsArchives(param);
			// ����ļ������ڣ������Ѿ�ɾ��
			if (docList != null && docList.size() == 1) {
				TDoc doc = docList.get(0);
				//�����ĵ���־�����pathΪ�գ���Ϊ�ļ�Ǩ�ƣ�����Ϊ�ĵ�������Ϣ����
				//���������Ϣ���²��ô���
				if(operateLog.getType().equals(DataSynStatus.UPDATE.toString())){
					if(content.indexOf(" Path:") > 0){
						operateLog.setType(DataSynStatus.REMOVE.toString());
						try {
							String path = ""; 
							content = content.substring(content.indexOf(" Path:"));
							path = content.substring(content.indexOf(" Path:")+6, content.indexOf(","));
							doc.setPath(path);
						} catch (Exception e) {
							logger.warn(e);
						}
					}
					if(content.indexOf(" PreviewPath:") > 0){
						operateLog.setType(DataSynStatus.REMOVE.toString());
						try {
							String path = "";
							path = content.substring(content.indexOf(" PreviewPath:")+13, content.indexOf(" WHERE"));
							doc.setPreviewPath(path);
						} catch (Exception e) {
							logger.warn(e);
						}
					}
				}
				
				//���²��ô���
				if(!operateLog.getType().equals(DataSynStatus.UPDATE.toString())){
					TProject project = archivesService.getTProjectBydocId(doc.getId());
					createNode(doc, operateLog.getType(),project, operateLog.getOperateTime()+"");
				}
			}
		}
	}

	/**
	 * �����ڵ�
	 * 
	 * @param doc
	 * @param project
	 * @param operate
	 */
	private void createNode(TDoc doc, String operate, TProject project,
			String updateTime) {
		// �����ĵ�ID��ѯ����

		// ����ǹ���
		if (project.getParentId() == null
				|| "".equals(project.getParentId() + "".trim())) {
			DataSynXmlUtils.createProjectNode(project);
			DataSynXmlUtils.createFileNode(doc, operate, project.getId() + "",
					DataSynXmlUtils.PROJECT, updateTime);
		} else {// �ӹ���
			TProject parentProject = (TProject) projectService.EntityQuery(
					TProject.class, project.getParentId());
			DataSynXmlUtils.createSubProjectNode(parentProject, project);
			DataSynXmlUtils.createFileNode(doc, operate, project.getId() + "",
					DataSynXmlUtils.SUBPROJECT, updateTime);
		}
	}

	/**
	 * ɾ���ļ� ���⴦��ɾ�����ļ���t_doc���в����ڼ�¼ ���ԣ������ȡ���ļ���������
	 */
	private void deleteFile() {
		// ��ѯ�ĵ�������־
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("in", new Object[] { DataSynStatus.DELETE.toString() });
		List<TOperateLogTemp> list = logService.findOperateTempLog(map);

		for (TOperateLogTemp operateLog : list) {
			String content = operateLog.getContent();
			if (content.indexOf("Projectid:") > 0) {

				// projectId
				String proTemp = content.substring(content
						.indexOf("Projectid:"));
				String projectid = proTemp.substring(10, proTemp.indexOf(","));

				// ����IDΪ�գ�����Ҫͬ��
				if (StringUtils.isNotBlank(projectid)
						&& !projectid.equals("null")) {
					TProject project = (TProject) projectService.EntityQuery(
							TProject.class, Long.valueOf(projectid.trim()));

					TDoc doc = new TDoc();
					// docid
					String docTemp = content.substring(content.indexOf("Id:"));
					String docId = docTemp.substring(3, docTemp.indexOf(","));
					doc.setId(docId);

					// docPath
					String pathTemp = content.substring(content
							.indexOf("Path:"));
					String docPath = pathTemp.substring(5, pathTemp
							.indexOf(","));
					doc.setPath(docPath);

					// docStatus
					String statusTemp = content.substring(content
							.indexOf(" Status:"));
					String status = statusTemp.substring(8, statusTemp
							.indexOf(","));
					if(null != project){
						if (!"03".equals(status))// δ�鵵�ļ�������
							createNode(doc, operateLog.getType(), project,
									operateLog.getOperateTime() + "");
					}
				}
			}
		}
	}

	/**
	 * ��������
	 */
	private void sendTask() {
		try {
			Configurater config = Configurater.getInstance();
			String taskDir = config.getConfigValue("failTaskListDir");
			String pwd = config.getConfigValue("wspwd");
			
			String path = XmlUtils.getPath() + taskDir;
			//�õ�web-info/faitaskĿ¼�µ������ļ���
			File taskFile = new File(path);
			
			File[] list = taskFile.listFiles();  //�õ����е��ļ��б����ļ�����
			
			//���ļ�Ϊ��λ���з���
			for (File file : list) {
				if(file.getName().indexOf(".xml") > 0){
					//��λID
					String unit = file.getName().substring(0, file.getName().indexOf(".xml"));
					DataHandler dataHandler = new DataHandler(new URL("file:////"+file.getAbsolutePath()));
/**********************************************************************************************/
					//����webservice
					XmlUtils intenterUtil = XmlUtils.getInstance("intenterIp.xml");
					Document document = intenterUtil.getDocument();
					//��ȡ���յ�λ��webservice��ַ
					Element element = (Element) document.selectSingleNode("//intenter[@code="+unit+"]");
					if(element != null){
						String wsUrl = element.attributeValue("appIp");
						if(wsUrl != null){
							try{
								WSClient client = WSClient.getClient(wsUrl);
								//���ͳɹ���ɾ���ļ�
								if(client.getService().receiveFileList(dataHandler,pwd)){
									if(file.exists()){
										file.delete();
									}
								}
							}catch(Exception e){
								logger.info(wsUrl+"����ʧ��");
							}
						}
					}else{
						logger.info(unit+"��Ϣû�гɹ���ȡ");
					}
/**********************************************************************************************/
				}
			}
		} catch (Exception e) {
			logger.warn(e);
		}
	}	

	/**
	 * �������� �����������Ժ󣬱�����web-info/failtask/Ŀ¼�� ����õ�λ��û�з��ͳɹ�������׷��
	 */
	@SuppressWarnings("unchecked")
	private void plotTask() {
		try {
			Document document = XmlUtils.getInstance().getDocument();
			
			//�Ż�
			//1.ɾ���鵵�Ͳ�����ͬ���ļ�ID
			document = deleteAchieveAndInsertSame(document);
			// ��ȡ���й��̽ڵ�
			List<Element> list = document.selectNodes("//"
					+ DataSynXmlUtils.PROJECT);

			OutputFormat format = OutputFormat.createPrettyPrint();
			format.setEncoding("UTF-8");
			for (Element element : list) {
				// �õ�to_unit_id
				String totemp = element.attributeValue("to_unit_id");
				String path = XmlUtils.getPath()
						+ Configurater.getInstance().getConfigValue(
								"failTaskListDir");
				String[] to = totemp.split("#");
				
				for (String filename : to) {
					// ����ID����
					File file = new File(path, filename + ".xml");
					
					Document documentTemp = null;
					//����ļ������ڣ�����һ���µ�document
					if(!file.exists()){
						file.createNewFile();
						documentTemp = DocumentHelper.createDocument();
						documentTemp.add(DocumentHelper.createElement("tasks"));
					}else{
						SAXReader reader = new SAXReader();
						reader.setEncoding("utf-8");
						documentTemp = reader.read(file);
					}
					
					XMLWriter writer = new XMLWriter(new FileOutputStream(file), format);
					//���element���ӹ�ϵ
					element.detach();
					//�õ�tasks�ڵ�
					Element rootElement = (Element) documentTemp.selectSingleNode("tasks");
					rootElement.add(element);
					
					documentTemp.getRootElement().add(element.detach());
					writer.write(documentTemp);
					writer.close();
				}
			}
			//����ַ�����Ժ�ɾ�����г����ڵ�����������ڵ�
			XmlUtils.getInstance().deleteAllNode(document);
		} catch (Exception e) {
			logger.warn(e);
		}
	}
	
	/**
	 * �鵵�ļ������ϴ�
	 *  1.��ѯL06��־
	 *  2.��ȡID
	 *  3.��ѯ�ĵ�
	 *  4.��ѯ�ĵ���������
	 *  5.�����嵥
	 */
	@SuppressWarnings("unchecked")
	private void insertFile(){
		//��ѯL06��־
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("in", new Object[] {DataSynStatus.INSERT.toString()});
		List<TOperateLogTemp> list = logService.findOperateTempLog(map);
		
		for (TOperateLogTemp operateLog : list) {
				String content = operateLog.getContent();
				content = content.substring(content.indexOf("Id:"));
				// ��ȡID
				String docId = content.substring(content.lastIndexOf("Id:") + 3,content.indexOf(","));
				//��ѯ�ĵ�
				Map<String, Object> param = new HashMap<String, Object>();
				param.put("in", new Object[] { docId });
				List<TDoc> docList = archivesService.queryDocIsArchives(param);
				//����ļ�����
				if (docList != null && docList.size() == 1) {
					TProject project = archivesService.getTProjectBydocId(docList.get(0).getId());
					createNode(docList.get(0), operateLog.getType(),project, operateLog.getOperateTime()+"");
				}
		}
		
		Configurater config = Configurater.getInstance();
		String loationCode = "";
		if("1".equals(config.getConfigValue("system_type"))){
			loationCode = config.getConfigValue("country");
		}else if("3".equals(config.getConfigValue("system_type"))){
			loationCode = config.getConfigValue("unitCode");
		}
		
		//�û����ص�ʧ���ļ�
		String failIds = FailFileIDXMLUtils.getFailFileIDByCode(loationCode);		
		String[] ids = failIds.split(",");
		if(ids.length > 0){
			Map<String, Object> param = new HashMap<String, Object>();
			param.put("in", ids);
			List<TDoc> docList = archivesService.queryDocIsArchives(param);
			SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			//����ļ�����
			for(TDoc tdoc : docList) {
				TProject project = archivesService.getTProjectBydocId(tdoc.getId());
				if(null != project)
					createNode(tdoc, "L06",project, timeFormat.format(new Date())+"");
			}
		}
	}
	
	
	
	
	
	/**
	 * ɾ����־
	 */
	private void deleteAllOperateLog(){
		logService.deleteAllTempOperateLog();
	}
	
	
	/**
	 * ɾ���鵵�Ͳ����ļ�ID��ͬ�Ľڵ�
	 */
	@SuppressWarnings("unchecked")
	public Document deleteAchieveAndInsertSame(Document document){
		//����
		List<Element> insertLists = document.selectNodes("//file[@operate='"+DataSynStatus.INSERT.toString()+"']");
		//�鵵
		List<Element> arthieveLists = document.selectNodes("//file[@operate='"+DataSynStatus.ARCHIVE.toString()+"']");
		
		for(Element arthieve : arthieveLists){
			String artId = arthieve.attributeValue("id");
			for(Element insert : insertLists){
				String inId = insert.attributeValue("id");
				if(artId.trim().equals(inId.trim()))
				  insert.getParent().remove(insert.detach());
			}
		}
		return document;
	}
	
}
