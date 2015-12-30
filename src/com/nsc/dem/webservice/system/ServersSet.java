package com.nsc.dem.webservice.system;

import java.util.List;

import com.nsc.dem.bean.system.TServersInfo;
import com.nsc.dem.service.system.IuserService;
import com.nsc.dem.webservice.util.ApplicationContext;

/**
 * 
 * ����������
 *
 */
public class ServersSet {
	private IuserService userService;

	public ServersSet() {
		userService = (IuserService) ApplicationContext.getInstance()
				.getApplictionContext().getBean("userService");
	}

	/**
	 * ���������õ�ftp
	 * 
	 * @param unitCode
	 *            ��λ code
	 * @param ftpIp
	 *            ftp IP��ַ
	 * @param ftpName
	 *            ftp �û���
	 * @param ftpPwd
	 *            ftp ����
	 * @param ftpPort
	 *            ftp �˿ں�
	 * @param webIp
	 *            Ӧ�÷������� IP
	 */
	public boolean saveServersInfo(String unitCode, String ftpIp, String ftpName,
			String ftpPwd, String ftpPort, String wsAdd, String unitname) {
		TServersInfo ftp = new TServersInfo();
		ftp.setUnitCode(unitCode);
		ftp.setFtpIp(ftpIp);
		ftp.setFtpName(ftpName);
		ftp.setUnitName(unitname);
		ftp.setFtpPort(ftpPort);
		ftp.setFtpPwd(ftpPwd);
		ftp.setWsAdd(wsAdd);
		
		//�Ȳ��ң�������ҳɹ����޸ģ���������
		List<TServersInfo> ftps = userService.getServersInfoByCode(unitCode);
		
		if(null !=ftps && ftps.size() == 1){
			ftp.setId(ftps.get(0).getId());
			userService.updateEntity(ftp);
		}else{
			userService.insertEntity(ftp);
		}
		return true;
	}

	/**
	 * ��ѯ��λ�ı��롢���ơ�FTP������IP��FTP�������˿ں�
	 * @return
	 */
	public String findAllServersInfo() {
		StringBuffer buffer = new StringBuffer();
		List serversInfo = userService.findAllServersInfo();
		for (int i = 0; i < serversInfo.size(); i++) {
			TServersInfo ftp = (TServersInfo) serversInfo.get(i);
			buffer.append(ftp.getUnitCode()+ "," + ftp.getUnitName() + ","+ftp.getFtpIp() + ","+ftp.getFtpPort());
			buffer.append("#");
		}
		buffer.deleteCharAt(buffer.length()-1);
		return buffer != null ? buffer.toString():null;
	}
	
	public List<TServersInfo> getServersInfoByCode(String unitCode) {
		return userService.getServersInfoByCode(unitCode);
	}
}
