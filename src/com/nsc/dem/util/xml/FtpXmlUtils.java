package com.nsc.dem.util.xml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.directwebremoting.util.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.nsc.base.conf.Configurater;
import com.nsc.dem.action.bean.ServiceSetBean;
import com.nsc.dem.bean.system.TServersInfo;
import com.nsc.dem.webservice.client.WSClient;

public class FtpXmlUtils {
	
	private static XmlUtils util = XmlUtils.getInstance("ftp.xml");
	
	/**
	 * ������unit��FTP�ڵ�
	 * @param name ��λ����
	 * @param hostName  FTP IP
	 * @param port   FTP �˿ں�
	 * @param username  FTP �û���
	 * @param password  FTP ����
	 * @throws Exception 
	 * @throws IOException
	 */
	public static void createFtpNodes(String unitCode, String name, String hostName, String port, String username, String password,boolean isDafault) {
		Document document = util.getDocument();
		String ftpUnit = "//ftp[@code=" + unitCode + "]";
		Element ftpNode = (Element) document.selectSingleNode(ftpUnit);
		
		//����ýڵ���ڣ�ɾ���ýڵ�
		if (ftpNode != null) {
			document.getRootElement().remove(ftpNode.detach());
		}
		
		Element ftpElement = DocumentHelper.createElement("ftp");
		ftpElement.addAttribute("code", unitCode);
		ftpElement.addAttribute("name", name);
		ftpElement.addAttribute("hostname", hostName);
		ftpElement.addAttribute("port", port);
		ftpElement.addAttribute("username", username);
		ftpElement.addAttribute("password", password);
		if(isDafault)
			ftpElement.addAttribute("default", "Y");
		//����unit
		ftpElement = createUnitNodeByWs(ftpElement);
		document.getRootElement().add(ftpElement);
		util.saveDocument(document);
	}
	/**
	 * ������unit��FTP�ڵ�
	 * @param name ��λ����
	 * @param hostName  FTP IP
	 * @param port   FTP �˿ں�
	 * @param username  FTP �û���
	 * @param password  FTP ����
	 * @throws Exception 
	 * @throws IOException
	 */
	public static void createFtpNodes(String unitCode, String name, String hostName, String port, String username, String password,String agreement,String context ,boolean isDafault) {
		Document document = util.getDocument();
		String ftpUnit = "//ftp[@code=" + unitCode + "]";
		Element ftpNode = (Element) document.selectSingleNode(ftpUnit);
		//unitCode, unitname, ftpIp, ftpPort, ftpName, ftpPwd,proAgreement,proContext,true
		//����ýڵ���ڣ�ɾ���ýڵ�
		if (ftpNode != null) {
			document.getRootElement().remove(ftpNode.detach());
		}
		
		Element ftpElement = DocumentHelper.createElement("ftp");
		ftpElement.addAttribute("code", unitCode);
		ftpElement.addAttribute("name", name);
		ftpElement.addAttribute("hostname", hostName);
		ftpElement.addAttribute("port", port);
		if(username!=null)
		ftpElement.addAttribute("username", username);
		if(password!=null)
		ftpElement.addAttribute("password", password);
		ftpElement.addAttribute("agreement", agreement);
		ftpElement.addAttribute("context", context);
		if(isDafault)
			ftpElement.addAttribute("default", "Y");
		//����unit
		ftpElement = createUnitNodeByWs(ftpElement);
		document.getRootElement().add(ftpElement);
		util.saveDocument(document);
	}
	/**
	 * ��������unit�ڵ��ftp�ڵ�
	 * @param unitCode
	 * @param name
	 * @param hostName
	 * @param port
	 * @param username
	 * @param password
	 * @param isDafault
	 */
	public static void createFtpNodesNoUnit(String unitCode, String name, String hostName, String port, String username, String password,boolean isDafault) {
		Document document = util.getDocument();
		String ftpUnit = "//ftp[@code=" + unitCode + "]";
		Element ftpNode = (Element) document.selectSingleNode(ftpUnit);
		
		//����ýڵ���ڣ�ɾ���ýڵ�
		if (ftpNode != null) {
			document.getRootElement().remove(ftpNode.detach());
		}
		
		Element ftpElement = DocumentHelper.createElement("ftp");
		ftpElement.addAttribute("code", unitCode);
		ftpElement.addAttribute("name", name);
		ftpElement.addAttribute("hostname", hostName);
		ftpElement.addAttribute("port", port);
		ftpElement.addAttribute("username", username);
		ftpElement.addAttribute("password", password);
		if(isDafault)
			ftpElement.addAttribute("default", "Y");
		document.getRootElement().add(ftpElement);
		util.saveDocument(document);
	}

	/**
	 * ��������unit�ڵ��ftp�ڵ�
	 * @param unitCode
	 * @param name
	 * @param hostName
	 * @param port
	 * @param username
	 * @param password
	 * @param isDafault
	 */
	public static void createFtpNodesNoUnit(String unitCode, String name, String hostName, String port, String username, String password,String argeement,String context , boolean isDafault) {
		Document document = util.getDocument();
		String ftpUnit = "//ftp[@code=" + unitCode + "]";
		Element ftpNode = (Element) document.selectSingleNode(ftpUnit);
		
		//����ýڵ���ڣ�ɾ���ýڵ�
		if (ftpNode != null) {
			document.getRootElement().remove(ftpNode.detach());
		}
		
		Element ftpElement = DocumentHelper.createElement("ftp");
		ftpElement.addAttribute("code", unitCode);
		ftpElement.addAttribute("name", name);
		ftpElement.addAttribute("hostname", hostName);
		ftpElement.addAttribute("port", port);
		ftpElement.addAttribute("username", username);
		ftpElement.addAttribute("password", password);
		ftpElement.addAttribute("agreement", argeement);
		ftpElement.addAttribute("context", context);
		if(isDafault)
			ftpElement.addAttribute("default", "Y");
		document.getRootElement().add(ftpElement);
		util.saveDocument(document);
	}
	
	/**
	 * ����ftp�ڵ��ȡ����
	 * @param unitCode
	 * @throws Exception
	 */
	public static String getFtpNodeByUnitCode(String unitCode) throws Exception {
		if(StringUtils.isBlank(unitCode))
			return null;
		Document document = util.getDocument();
		Element element = (Element) document.selectSingleNode("//ftp[@code='"+unitCode+"']");
		if(null != element){
			//ȥ��Ĭ��ֵ
			element.addAttribute("default", null);
			//����һ����ʱdocument
			Document documentTemp = DocumentHelper.createDocument();
			documentTemp.add(DocumentHelper.createElement("ftps"));
			documentTemp.getRootElement().add(element.detach());
			return XmlUtils.document2String(documentTemp);
		}
		return null;
	}
	
	/**
	 * ����unit
	 *   ����webService��ȡ������������Ϣ
	 * @param ftpNode
	 */
	@SuppressWarnings("unchecked")
	public static Element createUnitNodeByWs(Element ftpNode){
		if(null == ftpNode){
			return ftpNode;
		}
		//��ɾ��ftpNode�µ�����unit�ڵ�
		List<Element> elementList = ftpNode.elements();
		for(Element element : elementList){
			ftpNode.remove(element);
		}
		Configurater config = Configurater.getInstance();
		//�ӹ��ҵ�����ȡ���й�˾��FTP IP��ַ����������
		String url = config.getConfigValue("wsUrl");
		String pwd = config.getConfigValue("wspwd");
		//������ҵ�����WS�������ӣ�����ִ�����´���
		if(StringUtils.isNotBlank(url)){
			String serversInfo = null;
			try{
				WSClient client = WSClient.getClient(url);
				serversInfo = client.getService().findServersInfo(pwd);
			}catch(Exception e){
				Logger.getLogger(FtpXmlUtils.class).warn("���ҵ���webservice����ʧ��");
			}
			if(StringUtils.isNotBlank(serversInfo)){
				String[] servers = serversInfo.split("#");
				for (String server : servers) {
					//�ָ��յ�����Ϣ
					String[] ftpArray = server.split(",");
					String unitId = ftpArray[0];
					String unitName = ftpArray[1];
					String  ftpIp= ftpArray[2];
					String ftpPort = ftpArray[3];
					//����unit�ڵ�
					ftpNode = createUnitNode(ftpNode, unitId, unitName, ftpIp, ftpPort);
				}
			}
		}
		return ftpNode;
	}
	

	/**
	 * ����unit�ڵ�
	 *    ͨ�����ñ������ݿ���������Ϣ����
	 * @param ftpNode
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Element createUnitNodeByLocal(Element ftpNode,List<TServersInfo> serversInfoList){
		if(null != ftpNode){
			//��ɾ��ftpNode�µ�����unit�ڵ�
			List<Element> elementList = ftpNode.elements();
			for(Element element : elementList){
				ftpNode.remove(element);
			}
			for(TServersInfo server : serversInfoList){
				String unitId = server.getUnitCode();
				String unitName = server.getUnitName();
				String ftpIp = server.getFtpIp();
				String ftpPort = server.getFtpPort();
				createUnitNode(ftpNode, unitId, unitName, ftpIp, ftpPort);
			}
		}
		return ftpNode;
	}
	
	
	/**
	 * ����unit�ڵ�
	 *    ͨ�����ñ������ݿ���������Ϣ����
	 * @param ftpNode
	 * @return
	 */
	public static Element createUnitNodeByLocal(String unitCode, List<TServersInfo> serversInfoList){
		Document document = util.getDocument();
		Element ftpNode = (Element) document.selectSingleNode("//ftp[@code='"+unitCode+"']");
		ftpNode = createUnitNodeByLocal(ftpNode,serversInfoList);
		return ftpNode;
	}
	
	
	/**
	 * ����unit�ڵ�
	 * @param ftpNode
	 * @param unitId
	 * @param unitName
	 * @param ftpIp
	 * @param ftpPort
	 */
	private static Element createUnitNode(Element ftpNode, String unitId,
			String unitName, String ftpIp, String ftpPort) {
		Element unitElement = DocumentHelper.createElement("unit");
		unitElement.addAttribute("code", unitId);
		unitElement.addAttribute("name", unitName);
		unitElement.addAttribute("value", Ping.getTime(ftpIp,Integer.parseInt(ftpPort)));
		ftpNode.add(unitElement);
		return ftpNode;
	}

	/**
	 * ����
	 * @param element
	 */
	public static void saveFtpFile(Element element) {
		if(null != element){
			Document document = element.getDocument();
			util.saveDocument(document);
		}
	}
	
	/**
	 * ��ȡftp��Ϣ
	 * 0 : FTP��ַ
	 * 1 ��FTP �˿ں�
	 * 2 ��FTP ��¼��
	 * 3 ��FTP ��¼����
	 * @param unitCode
	 * @return
	 */
	public static  String[] getFTPInfo(String unitCode){
		if(StringUtils.isNotBlank(unitCode)){
			Document document = util.getDocument();
			Element element = (Element) document.selectSingleNode("//ftp[@code='"+unitCode+"']");
			if(null != element){
				String[] str = new String[4];
				str[0] = element.attributeValue("hostname");
				str[1] = element.attributeValue("port");
				str[2] = element.attributeValue("username");
				str[3] = element.attributeValue("password");
				return str;
			}
		}
		return null;
	}
	
	
	
	/**
	 * ����ID��ȡ��λ����
	 * @param unitCode
	 * @return
	 */
	public static String getUnitName(String unitCode){
		Document document = util.getDocument();
		if(unitCode.length() == 2)
			unitCode = "0801" ;
		Element element = (Element) document.selectSingleNode("//ftp[@code='"+unitCode+"']");
		if(null != element){
			return element.attributeValue("name");
		}
		return null;
	}
	
	/**
	 * ��ȡЭ��
	 * @param unitCode
	 * @return
	 */
	public static String getProtocol(String unitCode){
		Document document = util.getDocument();
		Element element = (Element) document.selectSingleNode("//ftp[@code='"+unitCode+"']");
		if(element != null){
			return element.attributeValue("agreement");
		}
		return null;
	}
	
	/**
	 *  ��ȡ�ļ���������ַ
	 *  @param unitCode 
	 */
	public static String getFileServerAdd(String unitCode){
		Document document = util.getDocument();
		Element element = (Element) document.selectSingleNode("//ftp[@code='"+unitCode+"']");
		if(element != null){
			String protocol = element.attributeValue("agreement");
			String hostName = element.attributeValue("hostname");
			String port = element.attributeValue("port");
			String context = element.attributeValue("context");
			return protocol + "://" + hostName + ":" + port + "/" + context + "/";
		}
		return null;
	}
	
	
	/**
	 * ��ȡ����ʡ����Ϣ
	 */
	@SuppressWarnings("unchecked")
	public static List<ServiceSetBean> getAllProvinceInfo(){
		Document document = util.getDocument();
		List<Element> elements = document.selectNodes("//ftp");
		List<ServiceSetBean> allProvinces = new ArrayList<ServiceSetBean>();
		for(Element ele : elements){
			String code = ele.attributeValue("code");
			if(code.length() == 8 ){
				ServiceSetBean bean = new ServiceSetBean();
				bean.setName(ele.attributeValue("name"));//ʡ��˾����
				bean.setProtocol(ele.attributeValue("agreement"));//Э��
				bean.setFtpPort(ele.attributeValue("port"));//�˿ں�
				bean.setFtpIP(ele.attributeValue("hostname"));//IP��ַ
				bean.setContext(ele.attributeValue("context"));//������
				bean.setFtpLoginName(ele.attributeValue("username"));//FTP�û���
				bean.setFtpPwd(ele.attributeValue("password"));//FTP����
				bean.setEndNetWay(IntenterXmlUtils.getInfoByCode(code, "end"));//������ַ 
				bean.setStartNetWay(IntenterXmlUtils.getInfoByCode(code, "start"));//��ʼ��ַ
				bean.setWsUrl(IntenterXmlUtils.getInfoByCode(code, "appIp"));//webservice��ַ
			allProvinces.add(bean);
			}
		}
		return allProvinces;
	}
	
	
	/**
	 * ����ʡ��˾���������͵�������ftp��Ϣ
	 * @param context ftp�ڵ���Ϣ
	 * @return
	 */
	public static boolean saveSynFtpNode(String context){
		if(StringUtils.isBlank(context))
			return false;
		try{
			Document document = DocumentHelper.parseText(context);
			Element ftpNode = (Element) document.selectSingleNode("//ftp");
			String code = ftpNode.attributeValue("code");
			// ���ص�document
			Document localDocument = util.getDocument();
			org.dom4j.Element ftpNodeTemp = (Element) localDocument.selectSingleNode("//ftp[@code='" +code+"']");
			if (ftpNodeTemp != null) {
				localDocument.getRootElement().remove(ftpNodeTemp.detach());
			}
			localDocument.getRootElement().add(ftpNode.detach());
			util.saveDocument(localDocument);
			return true;
		}catch (Exception e){
			Logger.getLogger(FtpXmlUtils.class).error(e.getMessage());
			return false;
		}
	}
	

	/**
	 * ��ʡ��˾���������͵�ftp��Ϣ�л�ȡ
	 *  ��˾����
	 */
	public static String getUnitCode(String context){
		try{
			Document document = DocumentHelper.parseText(context);
			Element ftpNode = (Element) document.selectSingleNode("//ftp");
			String code = ftpNode.attributeValue("code");
			return code;
		}catch(Exception e){
			Logger.getLogger(FtpXmlUtils.class).error(e.getMessage());
		}
		return null;
	}
	
	/**
	 * ��ʡ��˾���������͵�ftp��Ϣ�л�ȡ
	 *  �ڵ���Ϣ
	 */
	public static Element getSynFtpNode(String context){
		try{
			Document document = DocumentHelper.parseText(context);
			Element ftpNode = (Element) document.selectSingleNode("//ftp");
			return ftpNode;
		}catch(Exception e){
			Logger.getLogger(FtpXmlUtils.class).error(e.getMessage());
		}
		return null;
	}
	
	/**
	 * ����code
	 * @param hostName
	 * @return
	 */
	public static String getUnitCodeByHostName(String hostName){
		Document document = util.getDocument();
		Element element = (Element) document.selectSingleNode("//ftp[@hostname='"+hostName+"']");
		if(element != null){
			return element.attributeValue("code");
		}
		return null;
	}
	
	
	public static ServiceSetBean getInfoByCode(ServiceSetBean bean, String code) {
		
		Document doc = util.getDocument();
		Element elm = (Element) doc.selectSingleNode("//ftp[@code='"+code+"']");
		if(null != elm){
			bean.setProtocol(elm.attributeValue("agreement"));
			bean.setContext(elm.attributeValue("context"));
			bean.setFtpPort(elm.attributeValue("port"));
			bean.setFtpIP(elm.attributeValue("hostname"));
			bean.setFtpLoginName(elm.attributeValue("username"));
			bean.setFtpPwd(elm.attributeValue("password"));
		}
		return bean;
	}
	
}
