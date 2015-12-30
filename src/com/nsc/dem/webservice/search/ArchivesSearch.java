package com.nsc.dem.webservice.search;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;

import com.nsc.base.hibernate.CurrentContext;
import com.nsc.dem.bean.archives.TProjectDocCount;
import com.nsc.dem.bean.profile.TUser;
import com.nsc.dem.bean.system.TUnit;
import com.nsc.dem.service.searches.IsearchesService;

public class ArchivesSearch {
	private IsearchesService searchesService;

	public ArchivesSearch(IsearchesService service, TUser user) {
		searchesService = service;
		searchesService.getLogManager(user);
		CurrentContext.putInUser(user);
	}

	/**
	 * ����ͳ��
	 * 
	 * @param document
	 */
	public void siteCountAarchives(Document document) {
		Element root = document.getRootElement();
		List<Element> archieves = root.getChildren("count");
		for (Element ele : archieves) {
			// ����count
			// ȡcode����
			String code = ele.getAttributeValue("code");
			// ȡ������ʱ��
			String updatetime = ele.getAttributeValue("create_time");
			// ����yearmonth
			List<Element> yearmonths = ele.getChildren("year_month");
			for (Element year : yearmonths) {
				// ȡ��ʱ��
				String month = year.getAttributeValue("value");
				// ȡ��������
				String docsCount = year.getAttributeValue("docsCount");

				// ��������id���������ơ���ѹ�ȼ�
				Element pidElement = year.getChild("project_id");
				Element pnameElement = year.getChild("project_name");
				Element plevelElement = year.getChild("voltage_level");
				TProjectDocCount prdoc = new TProjectDocCount();
				// ����Id
				prdoc.setProjectId(Long.valueOf(pidElement.getTextTrim()));
				// ��������
				prdoc.setProjectName(pnameElement.getTextTrim());
				// ��ѹ�ȼ�
				prdoc.setVoltageLevel(plevelElement.getTextTrim());

				TUnit unit = (TUnit) searchesService.EntityQuery(TUnit.class,
						code);
				// ��λ
				prdoc.setTUnit(unit);
				// �ĵ�����
				prdoc.setDocCount(BigDecimal.valueOf(Long.valueOf(docsCount)));

				SimpleDateFormat simpleDateFormat = new SimpleDateFormat(
						"yyyy-MM-dd");

				Date udate;
				try {
					udate = simpleDateFormat.parse(updatetime);
					prdoc.setUpdateTime(udate);

				} catch (ParseException e) {
					Logger.getLogger(ArchivesSearch.class).error(e);
				}
				prdoc.setYearMonth(month);
				int isInsert = searchesService.isInsertTProjectDocCount(
						pidElement.getTextTrim(), month);
				if (isInsert == 0) {
					searchesService.insertEntity(prdoc);
				} else {
					prdoc.setId(new Long((long) isInsert));
					searchesService.updateEntity(prdoc);
				}

			}

		}

	}
}
