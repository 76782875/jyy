package com.nsc.dem.action.bean;

/**
 * ���շ��������ò���
 * ��Ϊ���ҵ�����ʡ��˾
 *    ����ǹ��ҵ�����˾����Ҫ�����ĸ����򼰹��ҵ�����˾��FTP��Ϣ������
 *    �����ʡ��˾����Ҫ����ʡ��˾FTP��Ϣ������
 */
public class ServiceSetBean {
	private String id;                 //��λID 
	private String name;               //��λ����
	private String wsUrl;              //webService��ַ
	private String ftpIP;
	private String ftpPort;
	private String ftpLoginName;
	private String protocol;
	private String ftpPwd;
	private String startNetWay;
	private String endNetWay;
	private String context;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getWsUrl() {
		return wsUrl;
	}
	public void setWsUrl(String wsUrl) {
		this.wsUrl = wsUrl;
	}
	public String getFtpIP() {
		return ftpIP;
	}
	public void setFtpIP(String ftpIP) {
		this.ftpIP = ftpIP;
	}
	public String getFtpPort() {
		return ftpPort;
	}
	public void setFtpPort(String ftpPort) {
		this.ftpPort = ftpPort;
	}
	public String getFtpLoginName() {
		return ftpLoginName;
	}
	public void setFtpLoginName(String ftpLoginName) {
		this.ftpLoginName = ftpLoginName;
	}
	public String getProtocol() {
		return protocol;
	}
	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}
	public String getFtpPwd() {
		return ftpPwd;
	}
	public void setFtpPwd(String ftpPwd) {
		this.ftpPwd = ftpPwd;
	}
	public String getStartNetWay() {
		return startNetWay;
	}
	public void setStartNetWay(String startNetWay) {
		this.startNetWay = startNetWay;
	}
	public String getEndNetWay() {
		return endNetWay;
	}
	public void setEndNetWay(String endNetWay) {
		this.endNetWay = endNetWay;
	}
	public String getContext() {
		return context;
	}
	public void setContext(String context) {
		this.context = context;
	}
	
}
