package com.nsc.base.task;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Timer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.log4j.Logger;

import com.nsc.base.conf.Configurater;
import com.nsc.base.util.DateUtils;

@SuppressWarnings("all")
public class TaskManager extends HttpServlet {

	private static final long serialVersionUID = 930742281559699951L;

	private static Map<String, Timer> timerMap = new HashMap<String, Timer>();

	private static ServletContext context;

	// init����������ʱ��
	public void init() throws ServletException {

		context = getServletContext();

		// (trueΪ�ö�ʱ��ˢ�»���)
		String startTask = getInitParameter("startTask");

		// ���� ��λ��Сʱ
		// Long period = Long.parseLong(getInitParameter("period"));

		// ������ʱ��
		if (startTask.equals("true")) {
			Configurater con = Configurater.getInstance();
			Properties pros = Configurater.getInstance().getConfig("taskList");
			Set set = pros.stringPropertyNames();
			for (Object obj : set) {
				String task = (String)pros.get(obj);
				String[] taskInfo = task.split(",");
				String time = taskInfo[0];// ִ��ʱ��
				String period = taskInfo[1];// ִ������
				String clazz = taskInfo[2];// ִ��ҵ���߼�
				String taskName = taskInfo[3];// ִ���������ƣ���ӦtimerMap�е�key
				String status = taskInfo[4];// ִ��״̬
				if ("true".equals(status)) {
					doSchedule(time, period, clazz, taskName, status);
				}
			}
		}
	}

	/**
	 * ���ö�ʱ��
	 */
	public void start(String taskKey, String time, String period, String status) {
		String task = Configurater.getInstance().getConfigValue("taskList.properties",taskKey);

		String[] taskInfo = task.split(",");

		String taskTime = taskInfo[0];// ִ��ʱ��
		String taskPeriod = taskInfo[1];// ִ������
		String taskClazz = taskInfo[2];// ִ��ҵ���߼�
		String taskName = taskInfo[3];// ִ���������ƣ���ӦtimerMap�е�key
		String taskStatus = taskInfo[4];// ִ��״̬

		Timer timer = timerMap.get(taskKey);
		if (timer != null) {// �����Ѵ��ڣ�����������
			timer.cancel();
			Logger.getLogger(TaskManager.class).info("ȡ����������");
			timerMap.remove(taskKey);
			doSchedule(taskTime, taskPeriod, taskClazz, taskName, taskStatus);
		} else {// �������
			doSchedule(taskTime, taskPeriod, taskClazz, taskName, taskStatus);
		}
	}

	/**
	 * ֹͣ��ʱ��
	 */
	public void stop(String taskKey) {
		Timer timer = timerMap.get(taskKey);
		if (timer != null) {// ��������Ѵ��ھ����
			timer.cancel();
			Logger.getLogger(TaskManager.class).info("ȡ����������");
			timerMap.remove(taskKey);
		}
	}

	/**
	 * ����ƻ���
	 * @param time ����ʱ��
	 * @param period ��������
	 * @param clazz ����ִ����
	 * @param taskName ��������
	 * @param status ���ñ�ʶ
	 */
	public void doSchedule(String time, String period, String clazz,
			String taskName, String status) {

		Date now = new Date();
		String today = DateUtils.getDate(now);

		Timer timer = new Timer(true);

		String runTime = today + " " + time;

		Date runDate = DateUtils.StringToDate(runTime, "yyyy-MM-dd HH:mm");

		// �ڴ�ʱ֮ǰ���Ƴٵ�����
		runDate = runDate.after(now) ? runDate : DateUtils.getDayAfterDays(
				runDate, 1);

		// ʱ���-�ӳ�ʱ��-��
		long delay = runDate.getTime() - now.getTime();

		try {
			Logger.getLogger(TaskManager.class).info(
					"������������["
							+ clazz
							+ "] "
							+ DateUtils.DateToString(runDate,
									"yyyy-MM-dd HH:mm:ss"));
			Class runClass = Class.forName(clazz);

			Constructor<TaskBase> cons = runClass.getConstructor(String.class,
					ServletContext.class, long.class);

			TaskBase taskObj = cons.newInstance(taskName,context, 60 * 1000 * Integer
					.parseInt(period));

			timer
					.schedule(taskObj, delay, 60 * 1000 * Integer
							.parseInt(period));// period ΪСʱ

			// ��timer����timerMap��
			timerMap.put(taskName, timer);

			Logger.getLogger(TaskManager.class).info("���óɹ�");
		} catch (Exception ex) {
			Logger.getLogger(TaskManager.class).warn("�����ڼ䷢������", ex);
			timer.cancel();// ȡ������
			Logger.getLogger(TaskManager.class).warn("ȡ���������ã�", ex);
		}

	}

}
