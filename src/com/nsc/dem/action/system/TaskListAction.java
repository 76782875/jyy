package com.nsc.dem.action.system;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.tools.ant.TaskAdapter;
import org.dom4j.Element;

import com.jnetdirect.jsql.m;
import com.nsc.base.conf.Configurater;
import com.nsc.base.task.TaskBase;
import com.nsc.dem.action.BaseAction;
import com.nsc.dem.bean.system.TServersInfo;
import com.nsc.dem.util.task.DataSynTask;
import com.nsc.dem.util.xml.IntenterXmlUtils;
import com.nsc.dem.webservice.client.WSClient;

@SuppressWarnings( { "serial", "unused", "unchecked" })
public class TaskListAction extends BaseAction {

	public String taskLists;// �����б�
	public String serverLists;// ��Ҫִ�������ʡ������
	public String whoTask;// ˭�ڵ��øú���
	public String returns;
	private List<Map<String, String>> aLists ;
	private List<Map<String, String>> pLists ;

	public List<Map<String, String>> getaLists() {
		return aLists;
	}

	public void setaLists(List<Map<String, String>> aLists) {
		this.aLists = aLists;
	}

	public List<Map<String, String>> getpLists() {
		return pLists;
	}

	public void setpLists(List<Map<String, String>> pLists) {
		this.pLists = pLists;
	}

	public String getWhoTask() {
		return whoTask;
	}

	public void setWhoTask(String whoTask) {
		this.whoTask = whoTask;
	}

	public String getTaskLists() {
		return taskLists;
	}

	public void setTaskLists(String taskLists) {
		this.taskLists = taskLists;
	}

	public String getServerLists() {
		return serverLists;
	}

	public void setServerLists(String serverLists) {
		this.serverLists = serverLists;
	}

	// ��ȡ������ʡ��˾��������Ϣ
	public String getProvinceInfo() {
		Configurater config = Configurater.getInstance();
		String unitCode = super.getLoginUser().getTUnit().getProxyCode();
		// ����
		aLists = IntenterXmlUtils.getAllAreas(unitCode);
		pLists = IntenterXmlUtils.getAllProvinces(unitCode);
		return SUCCESS;
	}

	

	public String executeTaskList() {
		String pwd = Configurater.getInstance().getConfigValue("wspwd");
		
		if ("country".equals(whoTask)) {
			if (serverLists != null && taskLists != null
					&& serverLists.length() > 0 && taskLists.length() > 0) {// ʡ����������,�����Լ�����
				for (String str : serverLists.split(",")) {
					WSClient client = WSClient.getClient(IntenterXmlUtils
							.getWSURL(str));
					for (String tstr : taskLists.split(",")) {
						try {
							client.getService().doExceuteTask(tstr,pwd);
						} catch (Exception e) {
							logger.getLogger(TaskListAction.class).warn(e.getMessage());
						}
					}
				}
				returns = "ִ�гɹ���";
			} else if ((serverLists.length() == 0 || serverLists == null)
					&& taskLists != null && taskLists.length() > 0) {// ��������
				returns = getLocalWSC();
			}
		} else if ("province".equals(whoTask)) {
			if (taskLists != null && taskLists.length() > 0) {// ʡ��˾webserver
				returns = getLocalWSC();
			}
		}
		return "success";
	}

	private String getLocalWSC() {
		StringBuffer buffer = new StringBuffer();
		for (String str : taskLists.split(",")) {
			String clazz = getClassName(str.charAt(0));
			String chineseName = getClassChineseName(str.charAt(0));
			try {
				this.executeLocalMethod(clazz, this.getSession()
						.getServletContext(), "");
				buffer.append(chineseName + "ִ�гɹ���");
			} catch (Exception e) {
				buffer.append(chineseName + "ִ��ʧ�ܣ�");
				logger.getLogger(TaskListAction.class).warn(e.getMessage());
			}
		}
		return buffer.toString();
	}

	private String getClassName(char c) {
		String methodName = "";
		switch (c) {
		case 'A':
			methodName = "com.nsc.dem.util.task.CreateTaskListTask";
			break;
		case 'B':
			methodName = "com.nsc.dem.util.task.DataSynTask";
			break;
		case 'C':
			methodName = "com.nsc.dem.util.task.UploadServerInfoTask";
			break;
		case 'D':
			methodName = "com.nsc.dem.util.task.SendServersInfoTask";
			break;
		case 'E':
			methodName = "com.nsc.dem.util.task.UploadIndexTask";
			break;
		case 'F':
			methodName = "com.nsc.dem.util.task.DocIndexingTask";
			break;
		case 'G':
			methodName = "com.nsc.dem.util.task.DownloadIndexTask";
			break;
		case 'H':
			methodName = "com.nsc.dem.util.task.DocImagingTask";
			break;
		case 'I':
			methodName = "com.nsc.dem.util.task.FileReceiveTask";
			break;
		case 'J':
			methodName = "com.nsc.dem.util.task.SuggestionTask";
			break;
		case 'K':
			methodName = "com.nsc.dem.util.task.BackupTask";
			break;
		case 'L':
			methodName = "com.nsc.dem.util.task.LogDeleteTask";
			break;
		case 'M':
			methodName = "com.nsc.dem.util.task.DataCountTask";
			break;
		}
		return methodName;
	}

	private String getClassChineseName(char c) {
		String methodName = "";
		switch (c) {
		case 'A':
			methodName = "��������ͬ���嵥";
			break;
		case 'B':
			methodName = "����ͬ��";
			break;
		case 'C':
			methodName = "�ϴ���������Ϣ";
			break;
		case 'D':
			methodName = "�·���������Ϣ";
			break;
		case 'E':
			methodName = "�ϴ�����";
			break;
		case 'F':
			methodName = "��������";
			break;
		case 'G':
			methodName = "��������";
			break;
		case 'H':
			methodName = "��������ͼ";
			break;
		case 'I':
			methodName = "�ļ�����";
			break;
		case 'J':
			methodName = "������ʾ";
			break;
		case 'K':
			methodName = "��־����";
			break;
		case 'L':
			methodName = "��־ɾ��";
			break;
		case 'M':
			methodName = "����ͳ��";
			break;
		}
		return methodName;
	}

	/**
	 * ����
	 */
	public void executeLocalMethod(String clazz, ServletContext context,
			String taskName) throws SecurityException, NoSuchMethodException,
			ClassNotFoundException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			InvocationTargetException {
		Class runClass = Class.forName(clazz);

		Constructor<TaskBase> cons = runClass.getConstructor(String.class,
				ServletContext.class, long.class);

		TaskBase taskObj = cons.newInstance(taskName, context, 0);

		Method method = runClass.getDeclaredMethod("doTask", new Class[] {});

		method.invoke(taskObj, new Object[] {});

	}
}