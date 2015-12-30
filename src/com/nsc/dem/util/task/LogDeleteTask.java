package com.nsc.dem.util.task;

import static com.nsc.base.util.DateUtils.DateToString;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.nsc.base.conf.Configurater;
import com.nsc.base.task.TaskBase;
import com.nsc.base.util.Component;
import com.nsc.base.util.DateUtils;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.bean.system.TDictionary;
import com.nsc.dem.bean.system.TLogFile;
import com.nsc.dem.bean.system.TOperateLog;
import com.nsc.dem.service.system.IlogService;
import com.nsc.dem.util.log.Logger;

public class LogDeleteTask extends TaskBase implements Job{

	private IlogService logService;
	private TUser user = null;
	private Logger logger = null;
	
	public LogDeleteTask(String taskName, ServletContext context,long period) {
		super(taskName, context, period);
		logService = (IlogService) Component.getInstance("logService",
				super.context);
		user = (TUser) logService.EntityQuery(TUser.class, Configurater.getInstance().getConfigValue("ws_user"));
		logger = logService.getLogManager(user).getLogger(
				LogDeleteTask.class);
	}
	
	public LogDeleteTask(){
		super(null,Configurater.getInstance().getServletContext(),0);
		user = (TUser) logService.EntityQuery(TUser.class, Configurater.getInstance().getConfigValue("ws_user"));
		logger = logService.getLogManager(user).getLogger(
				LogDeleteTask.class);
	}
	
	public void execute(JobExecutionContext context)
		throws JobExecutionException {
		String taskName = context.getTrigger().getKey().getName();
		logger.info("����[ " + taskName + " ]������ "
					+ DateToString(context.getFireTime(), "yyyy-MM-dd HH:mm:ss"));
		try {
			doTask();
		} catch(Exception e){
			throw new JobExecutionException(e);
		}finally{
			logger.info("����[ "+ taskName+ " ]�´ν���"
					+ DateToString(context.getNextFireTime(),"yyyy-MM-dd HH:mm:ss") + " ����");
		}		
	}

	@Override
	public void doTask() throws Exception {
		TUser user=(TUser)logService.EntityQuery(TUser.class, "admin");

		// ���������ļ��Զ�ɾ����־
		String taskLogDelete = Configurater.getInstance().getConfigValue(
				"taskLogDelete");
		Integer period = Integer.parseInt(taskLogDelete.split(",")[1]);// ִ������

		// ȡ������ʱ�䣬���ݷ�ΧΪ��ǰʱ����ǰ��һ����������
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DATE, -period / (60 * 24));

		String timeTo = DateUtils.DateToString(new Date(), "yyyy-MM-dd");// ����ʱ��
		String timeFrom = DateUtils.DateToString(cal.getTime(), "yyyy-MM-dd");// ��ʼʱ��
		String[] logDeleteScope = (String[]) super.context.getAttribute("logDeleteScope");//���ݷ�Χ
		
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("timeFrom",timeFrom);
		map.put("timeTo", timeTo);
		
		if(logDeleteScope==null || logDeleteScope.length==0){
			//application����ɾ����Χ,ɾ��������־�����ļ�
			TDictionary dic=new TDictionary();
			dic.setParentCode("RZFL");
			List<Object> dicList=logService.EntityQuery(dic);
			
			logDeleteScope=new String[dicList.size()];
			for(int i=0;i<dicList.size();i++){
				TDictionary di=(TDictionary)dicList.get(i);
				logDeleteScope[i]=di.getCode();
			}
		}

		String savePath = super.context.getRealPath("/");//����·��
		String backupPath=Configurater.getInstance().getConfigValue("backupPath");//����·��
		
		map.put("in", logDeleteScope);
		//����ѯҪ���ݵ����ݷ���bean��
		List<TLogFile> list=logService.logDelList(map);
		for(TLogFile log:list){
			String filePath=savePath+backupPath+"\\"+log.getFileName();
			logger.info("path====="+filePath);
			File f=new File(filePath);
			if(f.exists()){//����ļ����ڣ���ɾ���ļ�
				f.delete();
			}
			log.setTUserByDeleteOperator(user);
			log.setOperateTime(new Timestamp(System.currentTimeMillis()));
			logService.updateEntity(log);//ɾ����־���¼
		}
		
		//������־��
		SimpleDateFormat format = new SimpleDateFormat("yyyy��MM��dd��HHʱmm��ss��");

		TOperateLog tlog = new TOperateLog();
		tlog.setOperateTime(new Timestamp(System.currentTimeMillis()));
		tlog.setTarget(TUser.class.getSimpleName());
		tlog.setTUser(user);
		tlog.setType("L46");
		tlog.setContent(user.getName() + ","+ format.format(new Date()) + "������־ɾ��");
		logService.insertEntity(tlog);
	}

}
