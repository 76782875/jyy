package com.nsc.dem.action.searches;

import java.io.File;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.xwork.StringUtils;
import org.apache.struts2.json.annotations.JSON;

import com.nsc.base.conf.Configurater;
import com.nsc.base.util.FileUtil;
import com.nsc.dem.action.bean.DownFileBean;
import com.nsc.dem.bean.archives.TDoc;
import com.nsc.dem.bean.archives.TSynDoc;
import com.nsc.dem.bean.project.TDocProject;
import com.nsc.dem.bean.system.TOperateLog;
import com.nsc.dem.service.base.IService;
import com.nsc.dem.service.searches.IsearchesService;
import com.nsc.dem.util.download.DownloadAddessUtils;
import com.nsc.dem.util.download.LoginLocationUtils;
import com.nsc.dem.util.download.MD5;
import com.nsc.dem.util.xml.DownLoadXmlUtils;
import com.nsc.dem.util.xml.FtpXmlUtils;
import com.nsc.dem.util.xml.IntenterXmlUtils;

/**
 * �������Action
 * 
 * 
 */
public class PackAgeDownLoadAction extends DownloadAction {
	private static final long serialVersionUID = 5304532811867020924L;
	String[] eachCheckbox;
	String eachCheckboxVals; // �ĵ�id#����id#��˾id#����,�ĵ�id#����id#��˾id#����
	private String[] name;
	private Collection<DownFileBean> downFileList;
	private String downType; //�������ͣ�isLocal�����ء� ��λ���룺�Ǳ���
	private String[] loginLocationInfo; //0����¼���ڵ�ID��1����¼��������
	private String filename; //����
	
	/**
	 * ��ȡ���ص�ַ
	 */
	public String getDownLoadAddress() {
		loginLocationInfo = new String[4];
		//���ص�ַ
		allDocsObj = new ArrayList<DownFileBean>();
		List<DownFileBean> downloadFiles = getDownloadFileParameter();
		if(downloadFiles.size() <= 0)
			return null;
		String userCode = super.getLoginUser().getTUnit().getCode();
		
		//�ж��û���¼���ڵ�
		String loginIp = super.getRequest().getRemoteAddr();
		String loginLocation = LoginLocationUtils.getIpLocal(loginIp);
		//��������ж��û���������Ϊ�û���������λ��Χ��¼
		if(null != loginLocation){
			this.loginLocationInfo[0] = loginLocation;
			boolean isLocalLogin = LoginLocationUtils.isLocationLogin(loginLocation);
			String unitName = FtpXmlUtils.getUnitName(loginLocation);
			String message = "";
			if(!isLocalLogin){
				message = "�Ƽ���ʹ��"+unitName+"����������!";
				this.loginLocationInfo[2] = "N"; //ת��
			}else{
				message="��ʹ�ñ������أ�";
				this.loginLocationInfo[2] = "Y"; //��������
			}
			this.loginLocationInfo[1] = super.getLoginUser().getName() + 
			                "���ã�����ǰλ���ǣ�" + unitName +  "��Χ��" + message;
			this.loginLocationInfo[3] = unitName;
		}else{
			Configurater config = Configurater.getInstance();
			if("1".equals(config.getConfigValue("system_type"))){
				loginLocation = config.getConfigValue("country");
			}else{
				loginLocation = config.getConfigValue("unitCode");
			}
			this.loginLocationInfo[0] = loginLocation;
			this.loginLocationInfo[1] = super.getLoginUser().getName() + 
	                           "���ã�����ǰλ���ǣ�δ֪������ʹ�ñ������أ�";
			this.loginLocationInfo[2] = "Y"; //��������
			this.loginLocationInfo[3] = "";
		}
		
		//�û��Ƿ��ڱ��ص�¼
		boolean isLocalLogin = LoginLocationUtils.isLocationLogin(loginLocation);
		
		String systemType = Configurater.getInstance().getConfigValue("system_type");
		
		for(DownFileBean doc : downloadFiles){
			
			//���ұ������ݿ��Ƿ����
			TDocProject tDocProject = searchesService.searchLocalDoc(doc.getDocid(),doc.getProjectId());
			if(tDocProject != null){
				TDoc tdoc = (TDoc) searchesService.EntityQuery(TDoc.class, doc.getDocid());
				String to_unit_ids = tdoc.getStoreLocation();
				//����ַ���λΪ�գ�����ĵ������ص�ַֻ��ҵ����λ����
				if (to_unit_ids == null) {
					to_unit_ids = doc.getCode();
				}
				    DownFileBean downDocBean = new DownFileBean();
					downDocBean.setDocid(doc.getDocid());
					downDocBean.setName(doc.getName());
					downDocBean.setPath(tDocProject.getId().getTDoc().getPath());
					downDocBean.setSuffix(tDocProject.getId().getTDoc().getSuffix());
					List<String[]> downAddress = DownloadAddessUtils.getDownloadAddress(
							loginLocation, to_unit_ids, isLocalLogin,userCode);
					downDocBean.setDownAddress(downAddress);
					allDocsObj.add(downDocBean);
					continue;
				}
			//ʡ��˾
			if("3".equals(systemType)){
				TSynDoc synDoc = searchesService.searchSynDoc(doc.getDocid(),doc.getProjectId());
				if(synDoc != null){
					String to_unit_ids = synDoc.getStoreLocation();
					if(to_unit_ids == null){
						to_unit_ids = doc.getCode();
					}
					DownFileBean downDocBean = new DownFileBean();
					downDocBean.setDocid(doc.getDocid());
					downDocBean.setName(doc.getName());
					downDocBean.setPath(synDoc.getPath());
					downDocBean.setSuffix(synDoc.getSuffix());
					List<String[]> downAddress = DownloadAddessUtils.getDownloadAddress(
							loginLocation, to_unit_ids, isLocalLogin,userCode);
					downDocBean.setDownAddress(downAddress);
					allDocsObj.add(downDocBean);
					continue;
				}
			}
			//����������ز����ڸ��ļ�������ҵ����λΪ���ص�ַ
			if("1".equals(systemType)){
				DownFileBean downDocBean = new DownFileBean();
				downDocBean.setDocid(doc.getDocid());
				downDocBean.setName(doc.getName());
				downDocBean.setPath(doc.getPath());
				downDocBean.setSuffix(doc.getSuffix());
				downDocBean.setProjectId(doc.getProjectId());
				String name = FtpXmlUtils.getUnitName(doc.getCode());
				String[] address = new String[]{doc.getCode(),name,""};
				List<String[]> downAddress = new ArrayList<String[]>();
				downAddress.add(address);
				downDocBean.setDownAddress(downAddress);
				allDocsObj.add(downDocBean);
				continue;
			}
		}
		return SUCCESS;	
	}

	/**
	 * �������	
	 * @throws Exception
	 */
	public void downloadFile() throws Exception{
		List<File> packageDownFileList = null; //��������б�
		SimpleDateFormat format = new SimpleDateFormat(
		                          "yyyy��MM��dd��HHʱmm��ss��");
		//�Ǳ���������Ҫת��
		if(!"isLocal".equals(downType)){
			super.getResponse().setCharacterEncoding("UTF-8");
			String downFileName = DownLoadXmlUtils.createDownloadList(downFileList);
			String appServerAdd = IntenterXmlUtils.getAppServerAdd(downType.trim());
			if(StringUtils.isBlank(downFileName))
				return;
			if(StringUtils.isBlank(appServerAdd))
				return;
			String userName = super.getLoginUser().getLoginId();
			//����MD5����
			String pwd = String.valueOf(super.getLoginUser().getPassword());
			pwd = MD5.MD5Encode(pwd);
			
			String unitCode = "";
			Configurater config = Configurater.getInstance();
			String action = config.getConfigValue("sendRedirect");
			String systemType = config.getConfigValue("system_type");
			if("1".equals(systemType.trim())){
				unitCode = config.getConfigValue("country");
			}else if("3".equals(systemType.trim())){
				unitCode = config.getConfigValue("unitCode");
			}
			StringBuffer buffer = new StringBuffer();
			buffer.append(appServerAdd + "/")
			.append(action+"?")
			.append("username=")
			.append(userName+"&")
			.append("password=")
			.append(pwd+"&")
			.append("unitCode=")
			.append(unitCode+"&")
			.append("filename=")
			.append(downFileName+"&")
			.append("packageName=")   
			.append(filename);
			
			for(DownFileBean downFB : downFileList){
				TOperateLog tlog = new TOperateLog();
				tlog.setOperateTime(new Timestamp(System
						.currentTimeMillis()));
				tlog.setTarget(TDoc.class.getSimpleName());
				tlog.setTUser(super.getLoginUser());
				tlog.setType("L02");
				tlog.setContent(" �û�:" + super.getLoginUser().getName()+ "," + format.format(new Date()) + "ת����:"+
						downType +" �����ĵ���"
						+ downFB.getName()+"���ص�ַ��"+downFB.getCode());
				baseService.insertEntity(tlog);
			}
			
			super.getResponse().sendRedirect(buffer.toString());
			return;
		}else{//��������
			String filename = URLDecoder.decode(this.filename, "UTF-8");
			Configurater config = Configurater.getInstance();
			packageDownFileList = new ArrayList<File>();
			// ��zip�ĸ�ʽ����
			super.getResponse().setContentType("application/zip");
			super.getResponse().setHeader("Content-Disposition",
														"attachment;filename=\"" + new String(filename.getBytes(), "ISO-8859-1") + ".rar\""); //������
			
			String unitCode = config.getConfigValue("unitCode");
			
			for(DownFileBean downFB : downFileList){
				String protocol = FtpXmlUtils.getProtocol(downFB.getCode());
				
				//������ص�ַ���ڱ��ص�λ���룬��ô����һ�����ڸ��ļ�
				if(downFB.getCode().equals(unitCode)){
					protocol = "FTP";
				}
				
				TOperateLog tlog = new TOperateLog();
				tlog.setOperateTime(new Timestamp(System
						.currentTimeMillis()));
				tlog.setTarget(TDoc.class.getSimpleName());
				tlog.setTUser(super.getLoginUser());
				tlog.setType("L02");
				tlog.setContent("��˾��"+super.getLoginUser().getTUnit().getName()+" �û�:" + super.getLoginUser().getName()
						+ "," + format.format(new Date()) + "�����ĵ���"
						+ downFB.getName()+"���ص�ַ��"+downFB.getCode());
				
				baseService.insertEntity(tlog);
				
				//�����ļ�����ʹ��Э������
				if("FTP".equals(protocol)){
					try{
						ftpDownload(packageDownFileList, downFB);
					}catch(Exception e){
						logger.getLogger(PackAgeDownLoadAction.class).warn(e.getMessage());
					}	
					continue;
				}else{
					try{
						httpDownload(packageDownFileList, downFB);
					}catch(Exception e){
						logger.getLogger(PackAgeDownLoadAction.class).warn(e.getMessage());
					}	
				}
			}
			
			//���ļ�������ظ��ͻ���
			FileUtil.getPackAgeDownLoad(packageDownFileList.toArray(new File[0]),
					super.getResponse());
		}
	}
		

	/**
	 * ��װ�����ļ�����
	 * �ļ�ID<>����ID<>ҵ����λ<>�ļ�<>����·��<>��׺��:.....
	 *  �ָ���ֻ��ʹ��WINDOWS���õ��ļ����ر����
	 * @return
	 */
	private List<DownFileBean> getDownloadFileParameter(){
		// ���ز�������
		List<DownFileBean> downParamList = new ArrayList<DownFileBean>();
		if (eachCheckboxVals != null && eachCheckboxVals.trim().length() > 0) {
			String[] arry = eachCheckboxVals.split(":");
			if (arry.length > 0) {
				for (String str : arry) {
					if (str.indexOf("<>") != -1) {
						String[] param = str.split("<>");
						DownFileBean doc = new DownFileBean();
						doc.setDocid(param[0]);
						doc.setProjectId(param[1]);
						doc.setCode(param[2]);
						doc.setName(param[3].replaceAll("<[.[^<]]*>",""));
 						doc.setPath(param[4]!= null?param[4]:null);
						doc.setSuffix(param[5]!= null?param[5]:null);
						downParamList.add(doc);
					}
				} 
			}
		}
		return downParamList;
	}

	
	
	
	public String getDownType() {
		return downType;
	}

	public void setDownType(String downType) {
		this.downType = downType;
	}

	public Collection<DownFileBean> getDownFileList() {
		return downFileList;
	}

	public void setDownFileList(Collection<DownFileBean> downFileList) {
		this.downFileList = downFileList;
	}

	public String[] getName() {
		return name;
	}

	public void setName(String[] name) {
		this.name = name;
	}

	public void setEachCheckboxVals(String eachCheckboxVals) {
		this.eachCheckboxVals = eachCheckboxVals;
	}

	
	private List<DownFileBean> allDocsObj;

	public void setAllDocsObj(List<DownFileBean> allDocsObj) {
		this.allDocsObj = allDocsObj;
	}

	@JSON
	public List<DownFileBean> getAllDocsObj() {
		return allDocsObj;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public void setEachCheckbox(String[] eachCheckbox) {
		this.eachCheckbox = eachCheckbox;
	}

	IService baseService;

	private IsearchesService searchesService;

	public void setSearchesService(IsearchesService searchesService) {
		this.searchesService = searchesService;
	}

	public void setBaseService(IService baseService) {
		this.baseService = baseService;
	}

	private String returnValue;

	public String getReturnValue() {
		return returnValue;
	}
	
	@JSON
	public String[] getLoginLocationInfo() {
		return loginLocationInfo;
	}
}
