package com.nsc.dem.util.xml;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.util.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.nsc.base.conf.Configurater;
import com.nsc.base.util.DataSynStatus;
import com.nsc.dem.bean.archives.TDoc;
import com.nsc.dem.bean.project.TProject;

public class DataSynXmlUtils {
	
	//���̣��ӹ��̣��ļ��ڵ�
	public final static String PROJECT="project";
	public final static String SUBPROJECT="sub_project";
	public final static String FILE="file";
	
	/**
	 * ����ID��ѯԪ���Ƿ����
	 * @param node ��Ԫ�����ƣ�ʹ�ó���
	 * @param id   ��Ԫ��ID��Ψһ
	 * @return
	 */
	public static boolean findElementById(String node, String id){
		Document document = XmlUtils.getInstance().getDocument();
		return findElementById(node,id,document);
	}
	
	/**
	 * ����ID��ѯԪ���Ƿ����
	 * @param node ��Ԫ�����ƣ�ʹ�ó���
	 * @param id   ��Ԫ��ID��Ψһ
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean findElementById(String node, String id, Document document){
		List<Element> list = document.selectNodes("//"+node+"[@id='"+id+"']");
		if(list !=null && list.size() == 1)
			return true;
		return false;
	}
	
	
	/**
	 * ��������Ԫ��
	 * @param project
	 * @param document
	 */
	public static void createProjectNode(TProject project,Document document){
		Configurater config = Configurater.getInstance();
		String systemType = config.getConfigValue("system_type");
		String from = "";
		if("1".equals(systemType)){
			from = config.getConfigValue("country");
		}else if("3".equals(systemType)){
			from = config.getConfigValue("unitCode");
		}
		if(!findElementById(PROJECT, project.getId()+"",document)){  
			Element element = DocumentHelper.createElement(PROJECT);
			element.addAttribute("id", project.getId()+"");
			//ȥ������
			String to_unitIds = project.getGiveoutUnitId().replaceAll(from+"#", "");
			element.addAttribute("from_unit_id", from);
			element.addAttribute("location", project.getApproveUnit().getProxyCode());//����ص�
			element.addAttribute("to_unit_id",to_unitIds);
			element.addAttribute("project", project.getName());
			element.addAttribute("owerUnitUnitId", project.getTUnitByOwnerUnitId().getCode());
			element.addAttribute("owerUnitName", project.getTUnitByOwnerUnitId().getName());
			element.addAttribute("designerUnitId", project.getTUnitByDesignUnitId().getCode());
			element.addAttribute("designerName", project.getTUnitByDesignUnitId().getName());
			element.addAttribute("protype", project.getType());
			element.addAttribute("pharase", project.getStatus());
			element.addAttribute("voltage", project.getVoltageLevel());
			Element rootElement = document.getRootElement();
			rootElement.add(element);
			XmlUtils.getInstance().saveDocument(document);
		}
	} 
	 
	

	/**
	 * ��������Ԫ��
	 * @param project
	 * @param document
	 */
	public static void createProjectNode(TProject project){
		Document document =  XmlUtils.getInstance().getDocument();
		createProjectNode(project,document);
	}
	
	/**
	 * �����ӹ���
	 * @param parentProject�����ڵ�
	 * @param project���ӽڵ�
	 * @param document
	 */
	@SuppressWarnings("unchecked")
	public static void createSubProjectNode(TProject parentProject, TProject project, Document document){
		//��ѯ�ýڵ��Ƿ����
		if(!findElementById(SUBPROJECT, project.getId()+"",document)){
			//��ѯ���ڵ�
			if(!findElementById(PROJECT, parentProject.getId()+"",document)){
				createProjectNode(parentProject,document);
			}
			//��ȡ�����ڵ�
			List<Element> parentElementList = document.selectNodes("//"+PROJECT+"[@id="+parentProject.getId()+"]");
			if(parentElementList != null && parentElementList.size() == 1){
				Element parentElement = parentElementList.get(0);
				Element element = DocumentHelper.createElement(SUBPROJECT);
				element.addAttribute("id", project.getId()+"");
				element.addAttribute("project", project.getName());
				element.addAttribute("protype", project.getType());
				element.addAttribute("owerUnitUnitId", project.getTUnitByOwnerUnitId().getCode());
				element.addAttribute("owerUnitName", project.getTUnitByOwnerUnitId().getName());
				element.addAttribute("designerUnitId", project.getTUnitByDesignUnitId().getCode());
				element.addAttribute("designerName", project.getTUnitByDesignUnitId().getName());
				element.addAttribute("pharase", project.getStatus());
				parentElement.add(element);
				XmlUtils.getInstance().saveDocument(document);
			}
		}
	}
	
	
	/**
	 * �����ӹ���
	 * @param parentProject�����ڵ�
	 * @param project���ӽڵ�
	 * @param document
	 */
	public static void createSubProjectNode(TProject parentProject, TProject project){
		Document document = XmlUtils.getInstance().getDocument();
		createSubProjectNode(parentProject,project,document);
	}
	
	/**
	 * �����ļ��ڵ�
	 * @param doc �ĵ�����
	 * @param document 
	 * @param operate ��������
	 * @param parentNodeId ���ڵ�ID
	 * @param parentNodeType ���ڵ�����
	 */
	@SuppressWarnings("unchecked")
	public static void createFileNode(TDoc doc, String operate,String parentNodeId, String parentNodeType,String updateTime,Document document){
		XmlUtils util = XmlUtils.getInstance();
		List<Element> parentElementList = document.selectNodes("//"+parentNodeType+"[@id="+parentNodeId+"]");
		if(parentElementList != null && parentElementList.size() == 1 ){
			//�õ����ڵ�
			Element parentElement = parentElementList.get(0);
			Element element = DocumentHelper.createElement("file");
			element.addAttribute("id", doc.getId());
			element.addAttribute("operate", operate);
			element.addAttribute("update_time", updateTime+"");
			element.addAttribute("path", doc.getPath());
			if(!operate.trim().equals(DataSynStatus.DELETE.toString())){
				element.addAttribute("title", doc.getName());
				element.addAttribute("doctype", doc.getTDocType().getName());
				element.addAttribute("doctypeCode", doc.getTDocType().getCode());
				element.addAttribute("doctypeCodeName", doc.getTDocType().getName());
				element.addAttribute("format", doc.getFormat());
				element.addAttribute("previewpath", doc.getPreviewPath());
				element.addAttribute("author", doc.getTUser().getName());
				element.addAttribute("userCode", doc.getTUser().getTUnit().getCode());
				element.addAttribute("suffix", doc.getSuffix());
				element.addAttribute("version", doc.getVersion());
				element.addAttribute("storeLocation", doc.getStoreLocation());
				util.saveDocument(document);
			}
			parentElement.add(element);
			util.saveDocument(document);
			
		}
	}
	
	/**
	 * �����ļ��ڵ�
	 * @param doc �ĵ�����
	 * @param document 
	 * @param operate ��������
	 * @param parentNodeId ���ڵ�ID
	 * @param parentNodeType ���ڵ�����
	 */
	public static void createFileNode(TDoc doc, String operate,String parentNodeId, String parentNodeType, String updateTime){
		Document document = XmlUtils.getInstance().getDocument();
		createFileNode(doc,operate,parentNodeId,parentNodeType,updateTime,document);
	}
	
	/**
	 * �޸��ļ��ڵ�
	 * @param doc DOC�ĵ�
	 * @param operate ��������
	 * @param parentNodeId ���ڵ�
	 * @param parentNodeType 
	 * @param document
	 */
	public static void modifyFileNode(TDoc doc, String operate,String updateTime,Document document){
		try {
			//�ļ��ڵ����
			if(findElementById(FILE, doc.getId(),document)){
				//��ȡ���ļ��ڵ�
				Element element = (Element) document.selectNodes("//file[@id="+doc.getId()+"]").get(0);
				System.err.println("������һ��"+doc.getId());
				//�Ƚ����ڣ������ǰ�������������ڣ��޸�
				if(compareDate(element.attribute("update_time").getText(), updateTime)){
					element.addAttribute("id", doc.getId());
					element.addAttribute("operate", operate);
					element.addAttribute("update_time", updateTime+"");
					element.addAttribute("path", doc.getPath());
					if(!operate.trim().equals("L07")){
						element.addAttribute("title", doc.getName());
						element.addAttribute("doctype", doc.getTDocType().getName());
						element.addAttribute("format", doc.getFormat());
						element.addAttribute("previewpath", doc.getPreviewPath());
						element.addAttribute("author", doc.getTUser().getName());
						XmlUtils.getInstance().saveDocument(document);
					}
				}
			}
		} catch (Exception e) {
			Logger.getLogger(DataSynXmlUtils.class).warn(e.getMessage());
		}
	} 
	
	/**
	 * �޸��ļ��ڵ�
	 * @param doc
	 * @param operate
	 * @param parentNodeId
	 * @param parentNodeType
	 */
	public static void modifyFileNode(TDoc doc, String operate,String updateTime){
		Document document = XmlUtils.getInstance().getDocument();
		modifyFileNode(doc,operate,updateTime,document);
	}
	
	
	/**
	 * �Ƚ���������
	 * @param xmlDate��XML�ĵ����е�update_time
	 * @param Dbdate: ���ݿ��в�ѯ����־ִ��ʱ��
	 * ���DbDate�������������ڣ�����true,���򣬷���false
	 */
	public static boolean compareDate(String xmlDate, String Dbdate){
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date xml = format.parse(xmlDate);
			Date db = format.parse(Dbdate);
			Calendar xmlCalendar = Calendar.getInstance();
			xmlCalendar.setTime(xml);
			Calendar dbCalendar = Calendar.getInstance();
			dbCalendar.setTime(db);
			if(xmlCalendar.compareTo(dbCalendar) < 0){
				return true;
			}
		} catch (ParseException e) {
			Logger.getLogger(DataSynXmlUtils.class).warn(e.getMessage());
		}
		return false;
	}
	
	/**
	 * ����ID����ĳһ���
	 * @param node
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Element getElement(String node,String id){
		if(StringUtils.isNotBlank(node) && StringUtils.isNotBlank(id)){
			Document document = XmlUtils.getInstance().getDocument();
			List<Element> list = document.selectNodes("//"+node+"[@id="+id+"]");
			if(list != null && list.size() > 0){
				return list.get(0);
			}
		}
		return null;
	}	
	
	/**
	 * ��ȡ�����ļ��ڵ�
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static List<Element> getAllFileNode(){
		Document document = XmlUtils.getInstance().getDocument();
		List<Element> fileElementList = document.selectNodes("//"+FILE);
		if(fileElementList != null && fileElementList.size() > 0){
			return fileElementList;
		}
		return null;
	}
	
}
