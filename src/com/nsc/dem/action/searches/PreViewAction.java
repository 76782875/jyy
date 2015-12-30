package com.nsc.dem.action.searches;

import java.io.IOException;
import javax.servlet.ServletOutputStream;
import com.nsc.base.conf.Configurater;
import com.nsc.base.util.ContinueFTP;
import com.nsc.dem.action.BaseAction;
import com.nsc.dem.bean.archives.TDoc;
import com.nsc.dem.service.base.IService;
import com.nsc.dem.util.log.Logger;

/**
 * Ԥ��action
 * @author ibm
 *
 */
public class PreViewAction extends BaseAction {
	private static final long serialVersionUID = 5304532811867020924L;
	// ID
	private String id;

	public void setId(String id) {
		this.id = id;
	}

	IService baseService;

	public void setBaseService(IService baseService) {
		this.baseService = baseService;

	}

	//Ԥ��
	public void getPreView() throws IOException {
		Logger logger=super.logger.getLogger(PreViewAction.class);
		TDoc tdoc = (TDoc) baseService.EntityQuery(TDoc.class, id);
		if (tdoc != null) {

			String filePath = tdoc.getPreviewPath();
			if (filePath != null) {
				ServletOutputStream outs = super.getResponse()
						.getOutputStream();
				// �������ص�FileUtil.download(fileName, filePath, request,
				// response);
				// String fileName = tdoc.getName();
				// �������ļ��ж�ȡ���������û��������롢�˿ں�
				Configurater config = Configurater.getInstance();
				String hostname = config.getConfigValue("HOSTNAME");
				String username = config.getConfigValue("USERNAME");
				int port = Integer.parseInt(config.getConfigValue("PORT"));
				String password = config.getConfigValue("PASSWORD");
				ContinueFTP ftp = ContinueFTP.getDownLoadInstance(hostname, port, username, password);
				// �ж�ftp�Ƿ����ӳɹ���������ӳɹ��������������ķ���
				if (ftp != null) {
					ftp.loadFile(filePath, outs);
				}
				outs.flush();
			}
			logger.info("�ļ�·����" + filePath);
		}
	}
}
