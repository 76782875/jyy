package com.nsc.dem.action.system;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.struts2.ServletActionContext;

import com.nsc.base.conf.Configurater;
import com.nsc.base.conf.ConstConfig;
import com.nsc.base.excel.ExportUtil;
import com.nsc.base.task.TaskManager;
import com.nsc.base.util.DateUtils;
import com.nsc.base.util.GetCh2Spell;
import com.nsc.base.util.PropertyModify;
import com.nsc.dem.action.BaseAction;
import com.nsc.dem.action.bean.LogFileBean;
import com.nsc.dem.action.bean.RowBean;
import com.nsc.dem.action.bean.TableBean;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.bean.system.TDictionary;
import com.nsc.dem.bean.system.TLogFile;
import com.nsc.dem.bean.system.TOperateLog;
import com.nsc.dem.service.system.IlogService;
import com.nsc.dem.service.system.IsortSearchesService;
import com.nsc.dem.service.system.impl.LogServiceImpl;
import com.nsc.dem.util.log.Logger;
import com.opensymphony.xwork2.ActionContext;

public class LogOperateAction extends BaseAction {

	private static final long serialVersionUID = 1L;

	private IsortSearchesService sortSearchesService;
	private IlogService logService;
	private TaskManager taskManager;

	private List<Map<String, Object>> list;
	// Ĭ�ϵ�ҳ��
	private int page;
	private int rows;
	private TableBean tablebean = new TableBean();
	private String returnValue;// ajax�����ַ���
	private Object[] obj;// ajax����json����

	private String[] scope;// ���ݷ�Χ
	private String timeFrom;// ������ʼʱ��
	private String timeTo;// ���ݽ���ʱ��

	private String status;// ����״̬
	private String circle;// ��������
	private String time;// �������ڣ���/�£�
	private String amount;// ��������

	/**
	 * ��ʼ�� ������
	 */
	public String opertor() {
		TUser tUser = new TUser();
		list = new ArrayList<Map<String, Object>>();
		List<Object> tUserList = sortSearchesService.EntityQuery(tUser);
		for (int i = 0; i < tUserList.size(); i++) {
			tUser = (TUser) tUserList.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("code", tUser.getLoginId());
			map.put("id", tUser.getLoginId());
			map.put("name", tUser.getName());
			map.put("spell", GetCh2Spell.getBeginCharacter(tUser.getName()));
			map.put("other", tUser.getDuty());
			list.add(map);
		}
		return "list";
	}

	/**
	 * ��ʼ��ϵͳ��־����
	 */
	public String typeLog() {
		list = new ArrayList<Map<String, Object>>();
		List<TDictionary> tDictionaryList = logService.sysLogDicList();
		for (int i = 0; i < tDictionaryList.size(); i++) {
			TDictionary tDictionary = tDictionaryList.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("code", tDictionary.getCode());
			map.put("name", tDictionary.getName());
			list.add(map);
		}
		return "list";
	}

	/**
	 * ��ʼ��������־����
	 */
	public String typeLog2() {
		list = new ArrayList<Map<String, Object>>();
		List<TDictionary> tDictionaryList = logService.docLogDicList();
		for (int i = 0; i < tDictionaryList.size(); i++) {
			TDictionary tDictionary = tDictionaryList.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("code", tDictionary.getCode());
			map.put("name", tDictionary.getName());
			list.add(map);
		}
		return "list";
	}

	/**
	 * ��־�鿴,ϵͳ��־,��ҳ��ʾ
	 * 
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public String display() throws Exception {
		int firstResult = (page - 1) * rows;
		int maxResults = page * rows;
		// �������Ĳ�ѯ����
		String vals = getRequest().getParameter("vals");
		List<Object[]> list = null;
		// �������Ϊ�� ������Ϊ��һ�ν���û���ѯ֮ǰ ����ѯ���ݿ�
		if (vals == null) {
			list = new ArrayList<Object[]>();
		} else {
			String[] vStr = vals.split(",");
			String from = vStr[1]; // ��ʼʱ��
			if ("".equals(from) || from == null) {
				from = "1900-04-12";
			}
			String to = vStr[2]; // ����ʱ��
			if ("".equals(to) || to == null) {
				to = "3999-04-01";
			}
			String typeLog = vStr[3]; // ��־����
			String start = "";
			String end = "";
			if ("".equals(typeLog) || typeLog == null) {
				String flag = getRequest().getParameter("flag");
				if ("sys".equals(flag)) {
					start = "L20";
					end = "L99";
				} else if ("doc".equals(flag)) {
					start = "L00";
					end = "L19";
				}
			}
			String operatorCode = vStr[4]; // ������
			String content = vStr[5]; // ����
			if (!("".equals(content) || content == null)) {
				content = java.net.URLDecoder.decode(content, "UTF-8");
			}
			Object[] obj = null;
			if ("".equals(typeLog) || typeLog == null) {
				obj = new Object[] { start, end, "%" + operatorCode + "%", to,
						from, "%" + content + "%" };
			} else {
				obj = new Object[] { typeLog, typeLog,
						"%" + operatorCode + "%", to, from, "%" + content + "%" };
			}

			list = logService.queryOperateLogList(obj, firstResult, maxResults,
					tablebean);
		}

		List rowsList = new ArrayList();
		if (list != null) {
			for (Object[] objs : list) {
				TOperateLog tol = (TOperateLog) objs[0];
				TDictionary dic = (TDictionary) objs[2];
				// TDictionary dic = (TDictionary) logService.EntityQuery(
				// TDictionary.class, tol.getType());
				RowBean rowbean = new RowBean();
				rowbean.setCell(new Object[] { tol.getId(), dic.getName(),
						tol.getTUser().getName(), tol.getTarget(),
						DateUtils.DateToString(tol.getOperateTime()),
						tol.getContent() });
				rowbean.setId(tol.getId() + "");
				rowsList.add(rowbean);
			}
		}

		tablebean.setRows(rowsList);
		tablebean.setPage(this.page);
		int records = tablebean.getRecords();
		tablebean.setTotal(records % rows == 0 ? records / rows : records
				/ rows + 1);
		return "tab";
	}

	/****************************** ��־���� *******************************************/

	/**
	 * ��ȡ�����б��޲�ѯ������
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String getLogList() {
		int firstResult = (page - 1) * rows;
		int maxResults = page * rows;
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object[]> list;

		String backupPath = Configurater.getInstance().getConfigValue("backupPath");// ����·��
		try {
			list = logService.queryBackupList(map, firstResult, maxResults,
					tablebean);

			List rowsList = new ArrayList();
			if (list != null) {
				for (Object[] obj : list) {
					TLogFile log = (TLogFile) obj[0];
					TUser user = (TUser) obj[1];
					String lastTime = "";
					SimpleDateFormat timeFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					if (log.getBackupTime() != null) {
						lastTime = timeFormat.format(log.getBackupTime());
					}
					RowBean rowbean = new RowBean();
					rowbean.setCell(new Object[] { lastTime,
							"/" + backupPath + "/" + log.getFileName(),
							user.getName() });
					rowbean.setId(log.getId() + "");
					rowsList.add(rowbean);
				}
			}

			tablebean.setRows(rowsList);
			tablebean.setPage(this.page);
			int records = tablebean.getRecords();
			tablebean.setTotal(records % rows == 0 ? records / rows : records
					/ rows + 1);

		} catch (Exception e) {
			logger.getLogger(LogOperateAction.class).warn(e.getMessage());
		}

		return "tab";
	}

	/**
	 * ��ѯ��־����״̬�����û�ֹͣ
	 * 
	 * @return
	 */
	public String status() {
		//taskList.properties
		String taskBackup = Configurater.getInstance().getConfigValue("taskList.properties",
				"taskBackup");

		String backupPath = Configurater.getInstance().getConfigValue("backupPath");// ����·��
		if (taskBackup != null && !"".equals(taskBackup)) {// �Ƿ��б�������

			String[] backupInfo = taskBackup.split(",");
			String time = backupInfo[0];// ִ��ʱ��
			String period = backupInfo[1];// ִ������
			String status = backupInfo[4];// ִ��״̬
			if (status.equals("false")) {// ����δ����
				obj = new Object[] { "error", backupPath };
			} else {
				int per = Integer.parseInt(period) / (60 * 24);
				String circle = "d";// ��
				if (per >= 30) {
					per = per / 30;
					circle = "m";// ��
				}

				ServletContext application = ServletActionContext.getRequest()
						.getSession().getServletContext();// ���ݷ�Χ

				obj = new Object[] { "success", time, circle, per,
						(String[]) application.getAttribute("backupScope"),
						backupPath };
			}
		} else {

			obj = new Object[] { "error", backupPath };
		}

		return "status";
	}

	/**
	 * �ֶ���־����
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String hand() {
		Logger logger = super.logger.getLogger(LogServiceImpl.class);
		logger.info("��־���ݣ��ֶ�����");

		ActionContext ac = ActionContext.getContext();
		ServletContext sc = (ServletContext) ac
				.get(ServletActionContext.SERVLET_CONTEXT);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("timeFrom", timeFrom);
		map.put("timeTo", timeTo);

		if (scope != null && scope.length > 0) {
			for (String type : scope) {
				map.put("type", type);
				// ����ѯҪ���ݵ����ݷ���bean��
				List<Object[]> list = logService.logBackupHand(map);

				if (list != null && list.size() > 0) {

					String savePath = sc.getRealPath("/");
					String backupPath = Configurater.getInstance()
							.getConfigValue("backupPath");// ����·��
					savePath = savePath + backupPath + "\\";
					File f = new File(savePath);
					if (!f.exists()) {// ���Ŀ¼�������򴴽���Ŀ¼
						f.mkdirs();
					}

					Long i = 1L;
					List<LogFileBean> dataset = new ArrayList<LogFileBean>();

					for (Object[] obj : list) {
						TOperateLog log = (TOperateLog) obj[0];
						TUser user = (TUser) obj[1];
						TDictionary dic = (TDictionary) obj[2];

						LogFileBean logfile = new LogFileBean();
						logfile.setId(i);
						logfile.setTarget(log.getTarget());
						logfile.setContent(log.getContent());
						logfile.setType(dic.getName());
						logfile.setOperate(user.getName());
						logfile.setOperateTime(log.getOperateTime());

						dataset.add(logfile);
						i++;

					}

					// excle��ͷ
					ExportUtil<LogFileBean> ex = new ExportUtil<LogFileBean>();
					String[] headers = { "���", "����", "��־����", "����", "������",
							"��������" };

					OutputStream out;
					try {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
						String fileName = "log_bak_" + sdf.format(new Date())
								+ "_" + System.currentTimeMillis() + ".xls";
						savePath += fileName;
						logger.info("����·��·����------->" + savePath);

						out = new FileOutputStream(savePath);

						ex.exportExcel(headers, dataset, out);

						out.flush();
						out.close();
						logger.info("excel�����ɹ���");

						// ���뱸�ݱ�
						TLogFile logfile = new TLogFile();
						logfile.setBackupPath(backupPath);
						logfile.setFileName(fileName);
						logfile.setType(type);
						logfile.setBackupTime(new Timestamp(System
								.currentTimeMillis()));
						logfile.setTUserByBackupOperator(super.getLoginUser());
						logService.insertEntity(logfile);

						// �h�����ݱ��¼
						logService.deleteLog(map);

					} catch (FileNotFoundException e) {
						logger.warn(e.getMessage());
					} catch (IOException e) {
						logger.warn(e.getMessage());
					}

				}
			}

			// ������־��
			SimpleDateFormat format = new SimpleDateFormat(
					"yyyy��MM��dd��HHʱmm��ss��");

			TOperateLog tlog = new TOperateLog();
			tlog.setOperateTime(new Timestamp(System.currentTimeMillis()));
			tlog.setTarget(TUser.class.getSimpleName());
			tlog.setTUser(super.getLoginUser());
			tlog.setType("L45");
			tlog.setContent(super.getLoginUser().getName() + ","
					+ format.format(new Date()) + "������־����");
			logService.insertEntity(tlog);

			returnValue = "success";
		} else {
			returnValue = "error";
		}
		return "backup";
	}

	/**
	 * �Զ���־����
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String auto() throws UnsupportedEncodingException {
		taskManager = new TaskManager();
		if (status != null && !"".equals(status)) {
			if (scope == null || scope.length == 0) {
				returnValue = "error";
				return "backup";
			} else {// �����ݷ�Χ����application
				ServletContext application = ServletActionContext.getRequest()
						.getSession().getServletContext();// ���ݷ�Χ
				application.setAttribute("backupScope", scope);
			}
		}

		String task = Configurater.getInstance().getConfigValue("taskList.properties","taskBackup");

		String[] taskInfo = task.split(",");
		String taskTime = taskInfo[0];// ִ��ʱ��
		String taskPeriod = taskInfo[1];// ִ������
		String taskClazz = taskInfo[2];// ִ��ҵ���߼�
		String taskName = taskInfo[3];// ִ���������ƣ���ӦtimerMap�е�key
		String taskStatus = taskInfo[4];// ִ��״̬

		String path = Thread.currentThread().getContextClassLoader()
				.getResource("taskList.properties").getPath();
		path = java.net.URLDecoder.decode(path, "utf-8");

		taskTime = time;
		if (circle.equals("d")) {
			taskPeriod = String.valueOf(Integer.parseInt(amount) * 24 * 60);
		} else {
			taskPeriod = String
					.valueOf(Integer.parseInt(amount) * 24 * 60 * 30);
		}

		if (status == null || "".equals(status)) {// ͣ���Զ�����
			taskStatus = "false";

			task = taskTime + "," + taskPeriod + "," + taskClazz + ","
					+ taskName + "," + taskStatus;
			PropertyModify.writeProperties(path, "taskBackup", task);// �޸������ļ�
			try {// ���¼��������ļ�
				Configurater.getInstance().loadConfigure(
						ConstConfig.TASK_LIST, "taskList.properties");
			} catch (IOException e) {
				logger.getLogger(LogOperateAction.class).warn(e.getMessage());
			}

			taskManager.stop("taskBackup");// ��ֹ����
		} else {// �����Զ�����
			taskStatus = "true";

			task = taskTime + "," + taskPeriod + "," + taskClazz + ","
					+ taskName + "," + taskStatus;
			PropertyModify.writeProperties(path, "taskBackup", task);// �޸������ļ�
			try {// ���¼��������ļ�
				Configurater.getInstance().loadConfigure(
						ConstConfig.TASK_LIST, "taskList.properties");
			} catch (IOException e) {
				logger.getLogger(LogOperateAction.class).warn(e.getMessage());
			}

			taskManager.start("taskBackup", time, circle, status);// ������������
		}
		returnValue = taskStatus;
		return "backup";
	}

	/****************************** ��־ɾ�� *******************************************/

	/**
	 * ��ȥҪɾ������־����
	 */
	@SuppressWarnings("unchecked")
	public String getDeleteList() {
		int firstResult = (page - 1) * rows;
		int maxResults = page * rows;
		Map<String, Object> map = new HashMap<String, Object>();
		List<Object[]> list;

		String backupPath = Configurater.getInstance().getConfigValue("backupPath");// ����·��
		try {
			list = logService.queryLogDeleteList(map, firstResult, maxResults,
					tablebean);

			List rowsList = new ArrayList();
			if (list != null) {
				for (Object[] obj : list) {
					TLogFile log = (TLogFile) obj[0];
					TUser user = (TUser) obj[1];
					TDictionary dic = (TDictionary) obj[2];
					String lastTime = "";
					SimpleDateFormat timeFormat = new SimpleDateFormat(
							"yyyy-MM-dd HH:mm:ss");
					if (log.getBackupTime() != null) {
						lastTime = timeFormat.format(log.getBackupTime());
					}
					RowBean rowbean = new RowBean();
					rowbean.setCell(new Object[] { lastTime, dic.getName(),
							"/" + backupPath + "/" + log.getFileName(),
							user.getName() });
					rowbean.setId(log.getId() + "");
					rowsList.add(rowbean);
				}
			}

			tablebean.setRows(rowsList);
			tablebean.setPage(this.page);
			int records = tablebean.getRecords();
			tablebean.setTotal(records % rows == 0 ? records / rows : records
					/ rows + 1);

		} catch (Exception e) {
			logger.getLogger(LogOperateAction.class).warn(e.getMessage());
		}
		return "tab";
	}

	/**
	 * �Զ�ɾ������״̬(���ã�ͣ��)
	 * 
	 * @return
	 */
	public String deleteStatus() {
		String taskLogDelete = Configurater.getInstance().getConfigValue("taskList.properties",
				"taskLogDelete");

		String backupPath = Configurater.getInstance().getConfigValue("backupPath");// ����·��
		if (taskLogDelete != null && !"".equals(taskLogDelete)) {// �Ƿ��б�������

			String[] logDeleteInfo = taskLogDelete.split(",");
			String time = logDeleteInfo[0];// ִ��ʱ��
			String period = logDeleteInfo[1];// ִ������
			String status = logDeleteInfo[4];// ִ��״̬
			if (status.equals("false")) {// ����δ����
				obj = new Object[] { "error", backupPath };
			} else {
				int per = Integer.parseInt(period) / (24 * 60);
				String circle = "d";// ��
				if (per >= 30) {
					per = per / 30;
					circle = "m";// ��
				}

				ServletContext application = ServletActionContext.getRequest()
						.getSession().getServletContext();// ɾ����Χ

				obj = new Object[] { "success", time, circle, per,
						(String[]) application.getAttribute("logDeleteScope"),
						backupPath };
			}
		} else {
			obj = new Object[] { "error", backupPath };
		}

		return "status";
	}

	/**
	 * �ֶ�ɾ����־�����ļ�
	 * 
	 * @return
	 */
	public String handDelete() {

		try {
			Logger logger = super.logger.getLogger(LogServiceImpl.class);
			logger.info("��־ɾ�����ֶ�ɾ��");

			ActionContext ac = ActionContext.getContext();
			ServletContext sc = (ServletContext) ac
					.get(ServletActionContext.SERVLET_CONTEXT);
			String savePath = sc.getRealPath("/");// ����·��
			String backupPath = Configurater.getInstance().getConfigValue("backupPath");// ����·��

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("timeFrom", timeFrom);
			map.put("timeTo", timeTo);

			if (scope != null && scope.length > 0) {
				map.put("in", scope);

				// ����ѯҪ���ݵ����ݷ���bean��
				List<TLogFile> list = logService.logDelList(map);
				for (TLogFile log : list) {
					String filePath = savePath + backupPath + "\\"
							+ log.getFileName();
					logger.info("path=====" + filePath);
					File f = new File(filePath);
					if (f.exists()) {// ����ļ����ڣ���ɾ���ļ�
						f.delete();
					}

					log.setTUserByDeleteOperator(super.getLoginUser());
					log
							.setOperateTime(new Timestamp(System
									.currentTimeMillis()));

					logService.updateEntity(log);// ɾ����־���¼
				}

				// ������־��
				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy��MM��dd��HHʱmm��ss��");

				TOperateLog tlog = new TOperateLog();
				tlog.setOperateTime(new Timestamp(System.currentTimeMillis()));
				tlog.setTarget(TUser.class.getSimpleName());
				tlog.setTUser(super.getLoginUser());
				tlog.setType("L46");
				tlog.setContent(super.getLoginUser().getName() + ","
						+ format.format(new Date()) + "������־ɾ��");
				logService.insertEntity(tlog);

				returnValue = "success";
			} else {
				returnValue = "error";
			}

		} catch (Exception e) {
			logger.getLogger(LogOperateAction.class).warn(e.getMessage());
		}
		return "delete";
	}

	/**
	 * ��־ɾ��--�Զ�ɾ��
	 * 
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public String autoDelete() throws UnsupportedEncodingException {
		taskManager = new TaskManager();
		if (status != null && !"".equals(status)) {
			if (scope == null || scope.length == 0) {
				returnValue = "error";// δѡ��ɾ����Χ
				return "delete";
			} else {// ��ɾ����Χ����application
				ServletContext application = ServletActionContext.getRequest()
						.getSession().getServletContext();// ���ݷ�Χ
				application.setAttribute("logDeleteScope", scope);
			}
		}

		String task = Configurater.getInstance()
				.getConfigValue("taskList.properties","taskLogDelete");

		String[] taskInfo = task.split(",");
		String taskTime = taskInfo[0];// ִ��ʱ��
		String taskPeriod = taskInfo[1];// ִ������
		String taskClazz = taskInfo[2];// ִ��ҵ���߼�
		String taskName = taskInfo[3];// ִ���������ƣ���ӦtimerMap�е�key
		String taskStatus = taskInfo[4];// ִ��״̬

		String path = Thread.currentThread().getContextClassLoader()
				.getResource("taskList.properties").getPath();
		path = java.net.URLDecoder.decode(path, "utf-8");

		taskTime = time;
		if (circle.equals("d")) {
			taskPeriod = String.valueOf(Integer.parseInt(amount) * 24 * 60);
		} else {
			taskPeriod = String
					.valueOf(Integer.parseInt(amount) * 24 * 60 * 30);
		}

		if (status == null || "".equals(status)) {// ͣ���Զ�����
			taskStatus = "false";

			task = taskTime + "," + taskPeriod + "," + taskClazz + ","
					+ taskName + "," + taskStatus;
			PropertyModify.writeProperties(path, "taskLogDelete", task);// �޸������ļ�
			try {// ���¼��������ļ�
				Configurater.getInstance().loadConfigure(
						ConstConfig.TASK_LIST, "taskList.properties");
			} catch (IOException e) {
				logger.getLogger(LogOperateAction.class).warn(e.getMessage());
			}

			taskManager.stop("taskLogDelete");// ��ֹ����
		} else {// �����Զ�����
			taskStatus = "true";

			task = taskTime + "," + taskPeriod + "," + taskClazz + ","
					+ taskName + "," + taskStatus;
			PropertyModify.writeProperties(path, "taskLogDelete", task);// �޸������ļ�
			try {// ���¼��������ļ�
				Configurater.getInstance().loadConfigure(
						ConstConfig.TASK_LIST, "taskList.properties");
			} catch (IOException e) {
				logger.getLogger(LogOperateAction.class).warn(e.getMessage());
			}

			taskManager.start("taskLogDelete", time, circle, status);// ������������
		}

		returnValue = taskStatus;
		return "delete";
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void setRows(int rows) {
		this.rows = rows;
	}

	public TableBean getTablebean() {
		return tablebean;
	}

	public List<Map<String, Object>> getList() {
		return list;
	}

	public void setList(List<Map<String, Object>> list) {
		this.list = list;
	}

	public void setLogService(IlogService logService) {
		this.logService = logService;
	}

	public void setSortSearchesService(IsortSearchesService sortSearchesService) {
		this.sortSearchesService = sortSearchesService;
	}

	public String[] getScope() {
		return scope;
	}

	public void setScope(String[] scope) {
		this.scope = scope;
	}

	public String getTimeFrom() {
		return timeFrom;
	}

	public void setTimeFrom(String timeFrom) {
		this.timeFrom = timeFrom;
	}

	public String getTimeTo() {
		return timeTo;
	}

	public void setTimeTo(String timeTo) {
		this.timeTo = timeTo;
	}

	public String getReturnValue() {
		return returnValue;
	}

	public void setReturnValue(String returnValue) {
		this.returnValue = returnValue;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCircle() {
		return circle;
	}

	public void setCircle(String circle) {
		this.circle = circle;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public Object[] getObj() {
		return obj;
	}

	public void setObj(Object[] obj) {
		this.obj = obj;
	}

	public void setTaskManager(TaskManager taskManager) {
		this.taskManager = taskManager;
	}

}
