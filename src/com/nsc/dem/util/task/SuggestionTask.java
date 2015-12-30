package com.nsc.dem.util.task;

import static com.nsc.base.util.DateUtils.DateToString;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.RAMDirectory;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.nsc.base.conf.Configurater;
import com.nsc.base.index.AnalyzerFactory;
import com.nsc.base.index.DOCFIELDEnum;
import com.nsc.base.index.IIndexSearcher;
import com.nsc.base.index.IIndexWriter;
import com.nsc.base.index.IndexFactory;
import com.nsc.base.index.SearchFactory;
import com.nsc.base.task.TaskBase;
import com.nsc.base.util.Component;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.service.system.IuserService;
import com.nsc.dem.util.index.FileDirUtils;
import com.nsc.dem.util.index.IndexSearchManager;
import com.nsc.dem.util.index.RAMDirectoryStore;
import com.nsc.dem.util.log.Logger;

public class SuggestionTask extends TaskBase implements Job{
	private IuserService userService;
	private TUser user = null;
	private Logger logger = null;

	public SuggestionTask(String taskName, ServletContext context,long period) throws URISyntaxException{
		super(taskName, context, period);
		userService = (IuserService) Component.getInstance("userService",super.context);
		user = (TUser) userService.EntityQuery(TUser.class, Configurater.getInstance().getConfigValue("ws_user"));
		logger = userService.getLogManager(user).getLogger(
				SuggestionTask.class);
	}
	
	public SuggestionTask() throws URISyntaxException{
		super(null,Configurater.getInstance().getServletContext(),0);
		userService = (IuserService) Component.getInstance("userService",super.context);
		user = (TUser) userService.EntityQuery(TUser.class, Configurater.getInstance().getConfigValue("ws_user"));
		logger = userService.getLogManager(user).getLogger(
				SuggestionTask.class);
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
		File suggestDir = FileDirUtils.getWordFile();
		RAMDirectory ram;

		List<RAMDirectory> list = new ArrayList<RAMDirectory>();
		IIndexSearcher searcher = SearchFactory.getInstance().getIndexSearcher(
				AnalyzerFactory.getInstance().getAnalyzer(),null);
		
		Map<Enum<?>, Object> filters = new HashMap<Enum<?>, Object>();
		
		while ((ram = RAMDirectoryStore.getRAM()) != null && suggestDir != null) {
			
			IndexReader reader = IndexReader.open(ram,true);
			Document doc=reader.document(0);
			String queryStr=doc.getField("keyword").stringValue();
			
			Map<Enum<?>, Object> params = new HashMap<Enum<?>, Object>();
			params.put(DOCFIELDEnum.keyword, queryStr);
			List<String> docList = searcher==null?null:searcher.searchDocument(params, filters,false);
			
			//����ҵ����ַ���������ͬ����֤���Ѿ����ڣ����������
			if(docList!=null && !docList.isEmpty()){
				String matchStr=docList.get(0);
				if(matchStr.equals(queryStr)){
					continue;
				}
			}
			
			logger.info("��Ӽ�����ʾ�ʻ� "+queryStr);
			list.add(ram);
		}
		
		if(list.isEmpty())
			return;
		
		IIndexWriter writer = IndexFactory.getInstance().getIndexWriter(
				suggestDir, AnalyzerFactory.getInstance().getAnalyzer());
		try {			
			IndexSearchManager.getInstance().releaseSearch(suggestDir);
			writer.addIndex(list.toArray(new RAMDirectory[0]));
		} catch (Exception ex) {
			logger.warn(ex);
		} finally {
			IndexFactory.getInstance().close(suggestDir);
			IndexSearchManager.getInstance().reloadSingleFile(suggestDir);
		}
	}

}
