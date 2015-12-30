package com.nsc.base.hibernate;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Time;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.NamedQueryDefinition;
import org.hibernate.engine.NamedSQLQueryDefinition;
import org.hibernate.impl.AbstractSessionImpl;
import org.hibernate.impl.SessionFactoryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;

import com.nsc.base.util.AppException;
import com.nsc.base.util.Reflections;

/**
 * ���ݿ���ʻ�����
 * 
 * ����ͨ����HibernateDaoSupport�����չ�����书�ܽ����������ϣ�ʹ֮�������ϲ����ϰ�ߡ�
 * 
 * @author bs-team
 * 
 * @date Oct 19, 2010 9:46:39 AM
 * @version
 */
@SuppressWarnings("all")  
public abstract class BaseDAO extends HibernateDaoSupport {
	private Session tepSession;
	private Session newSession;

	/**
	 * ͨ��HQL���Ƶ������ļ��л�ȡHQL��Ȼ����в�ѯ�� HQL�п����� ?���ò��� :list����ͨ���б�����
	 * :in����ͨ���������ã�����ͨ��Object[]���롣 ����������Object[]����ֵ�����������
	 * 
	 * @param hqlName
	 * @param params
	 * @return ��ѯ����б�
	 */
	protected List<?> findResultBySQLName(final String hqlName,
			final Object[] params) {

		Query query = getQueryBySQLName(hqlName, params);

		return query.list();
	}

	/**
	 * ͨ��HQL���Ƶ������ļ��л�ȡHQL��Ȼ����в�ѯ�� HQL�п����� ?���ò��� :list����ͨ���б�����
	 * :in����ͨ���������ã�����ͨ��Object[]���롣 ���ô����class����󣬶�list�б��е�Ԫ�ؽ���ת��
	 * 
	 * @param hqlName
	 * @param params
	 * @param class_
	 * @return ��ѯ����б�
	 */
	protected List<?> findResultBySQLName(final String hqlName,
			final Object[] params, Class class_) {

		Query query = getQueryBySQLName(hqlName, params);

		return query.setResultTransformer(
				Transformers.aliasToBean(class_.getClass())).list();
	}

	/**
	 * ����HQL���ƺͲ�����������Query����
	 * 
	 * @param hqlName
	 * @param params
	 * @return Query����
	 */
	protected Query getQueryBySQLName(final String hqlName,
			final Object[] params) {

		AbstractSessionImpl session = (AbstractSessionImpl) super
				.getSessionFactory().getCurrentSession();
		Query query = session.getNamedQuery(hqlName);

		if (null != params && params.length > 0) {
			for (int i = 0; i < params.length; i++) {

				Object obj = params[i];
				setParameterByInt(i, obj, query);
			}
		}
		return query;
	}

	/**
	 * ͨ��HQL���Ƶ������ļ��л�ȡHQL��Ȼ����в�ѯ�� HQL�п����� ?���ò��� :list����ͨ���б�����
	 * :in����ͨ���������ã�����ͨ��Map<String, Parameter>���롣
	 * 
	 * @param hqlName
	 * @param paraMap
	 * @return ��ѯ����б�
	 */
	protected List<?> findResultBySQLName(final String hqlName,
			final Map<String, Object> paraMap) {
		Query query = getQueryBySQLName(hqlName, paraMap);

		return query.list();
	}

	/**
	 * ͨ��HQL���Ƶ������ļ��л�ȡHQL��Ȼ����в�ѯ�� HQL�п����� ?���ò��� :list����ͨ���б�����
	 * :in����ͨ���������ã�����ͨ��Map<String, Parameter>���롣 ͨ�����ÿ�ʼ��¼�ͽ�����¼��С��ѯ��Χ
	 * 
	 * @param hqlName
	 * @param paraMap
	 * @param firstResult
	 * @param maxResults
	 * @return ��ѯ����б�
	 * @throws Exception
	 */
	protected List<?> findResultBySQLName(final String hqlName,
			final Map<String, Object> paraMap, int firstResult, int maxResults)
			throws Exception {

		Query query = getQueryBySQLName(hqlName, paraMap);

		query.setFirstResult(firstResult);
		query.setMaxResults(maxResults);

		return query.list();
	}

	/**
	 * ͨ��HQL���Ƶ������ļ��л�ȡHQL��Ȼ����в�ѯ�� HQL�п����� ?���ò��� :list����ͨ���б�����
	 * :in����ͨ���������ã�����ͨ��Map<String, Parameter>���롣 ͨ�����ÿ�ʼ��¼�ͽ�����¼��С��ѯ��Χ
	 * 
	 * @param hqlName
	 * @param paraMap
	 * @param firstResult
	 * @param maxResults
	 * @return ��ѯ����б�
	 * @throws Exception
	 */
	protected List<?> findResultBySQLName(final String hqlName,
			final Object[] obj, int firstResult, int maxResults)
			throws Exception {

		Query query = getQueryBySQLName(hqlName, obj);

		query.setFirstResult(firstResult);
		query.setMaxResults(maxResults);

		return query.list();
	}

	/**
	 * ����HQL���ƺͲ����ۼ�����Query����
	 * 
	 * @param hqlName
	 * @param paraMap
	 * @return Query����
	 */
	protected Query getQueryBySQLName(final String hqlName,
			final Map<String, Object> paraMap) {

		AbstractSessionImpl session = (AbstractSessionImpl) super
				.getSessionFactory().getCurrentSession();
		Query query = session.getNamedQuery(hqlName);
		String[] parameters = query.getNamedParameters();

		for (String s : parameters) {
			Object obj = paraMap.get(s);

			setParameterByStr(s, obj, query);
		}
		return query;
	}

	/**
	 * ����HQL���ƺͲ����ۼ�ͳ�ƽ��������
	 * 
	 * @param hqlName
	 * @param paraMap
	 * @return ���������
	 */
	protected long findCountBySQLName(final String hqlName,
			final Map<String, Object> paraMap) {

		Session session = super.getSessionFactory().getCurrentSession();

		SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) session
				.getSessionFactory();
		NamedQueryDefinition nqd = sessionFactoryImpl.getNamedQuery(hqlName);

		String queryStr;
		// HQL
		if (nqd != null) {
			queryStr = nqd.getQueryString().trim();
			// SQL
		} else {
			NamedSQLQueryDefinition nsqd = sessionFactoryImpl
					.getNamedSQLQuery(hqlName);

			queryStr = nsqd.getQueryString().trim();
		}

		if (queryStr == null) {
			throw new AppException(
					"Get null sql from hibernate sql definition",
					"app.hibernate.getsql", null, new String[] {});
		}

		if (queryStr.trim().toLowerCase().startsWith("select")) {
			int sql_index = queryStr.trim().indexOf(" from");
			queryStr = "select count(*)" + queryStr.substring(sql_index);
		} else {
			queryStr = "select count(*) " + queryStr;
		}

		Query query;
		if (nqd != null)
			query = session.createQuery(queryStr);
		else
			query = session.createSQLQuery(queryStr);

		String[] parameters = query.getNamedParameters();

		for (String s : parameters) {
			Object obj = paraMap.get(s);

			setParameterByStr(s, obj, query);
		}
		
		BigDecimal count=(BigDecimal)query.uniqueResult();

		return count.longValue();
	}
	
	protected List<?> findResultBySQL(final String sql,
			final Map<String, Object> paraMap){
		
		Session session = super.getSessionFactory().getCurrentSession();
		Query query = session.createSQLQuery(sql);
		
		String[] parameters = query.getNamedParameters();

		for (String s : parameters) {
			Object obj = paraMap.get(s);

			setParameterByStr(s, obj, query);
		}
		
		return query.list();
	}

	/**
	 * ����HQL���ƺͲ����ۼ�ͳ�ƽ��������
	 * 
	 * @param hqlName
	 * @param paraMap
	 * @return ���������
	 */
	protected long findCountBySQLName(final String hqlName, final Object[] obj) {

		Session session = super.getSessionFactory().getCurrentSession();

		SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) session
				.getSessionFactory();
		NamedQueryDefinition nqd = sessionFactoryImpl.getNamedQuery(hqlName);

		String queryStr;
		// HQL
		if (nqd != null) {
			queryStr = nqd.getQueryString().trim();
			// SQL
		} else {
			NamedSQLQueryDefinition nsqd = sessionFactoryImpl
					.getNamedSQLQuery(hqlName);

			queryStr = nsqd.getQueryString().trim();
		}

		if (queryStr == null) {
			throw new AppException(
					"Get null sql from hibernate sql definition",
					"app.hibernate.getsql", null, new String[] {});
		}

		if (queryStr.trim().toLowerCase().startsWith("select")) {
			int sql_index = queryStr.trim().indexOf(" from");
			queryStr = "select count(*)" + queryStr.substring(sql_index);
		} else {
			queryStr = "select count(*) " + queryStr;
		}

		Query query;
		if (nqd != null)
			query = session.createQuery(queryStr);
		else
			query = session.createSQLQuery(queryStr);

		String[] parameters = query.getNamedParameters();

		if (null != obj && obj.length > 0) {
			for (int i = 0; i < obj.length; i++) {

				Object o = obj[i];
				setParameterByInt(i, o, query);
			}
		}

		return ((BigDecimal) query.uniqueResult()).longValue();
	}

	/**
	 * ���ò����������HQL����
	 * 
	 * @param i
	 * @param obj
	 * @param query
	 *            Query����
	 */
	private void setParameterByInt(int i, Object obj, Query query) {
		if (obj instanceof Long) {
			query.setLong(i, ((Long) obj).longValue());
		} else if (obj instanceof String) {
			query.setString(i, obj.toString());
		} else if (obj instanceof Double) {
			query.setDouble(i, ((Double) obj).doubleValue());
		} else if (obj instanceof Float) {
			query.setFloat(i, ((Float) obj).floatValue());
		} else if (obj instanceof Integer) {
			query.setInteger(i, ((Integer) obj).intValue());
		} else if (obj instanceof Date || obj instanceof Time) {
			query.setDate(i, (Date) obj);
		} else if (obj instanceof Short) {
			query.setShort(i, ((Short) obj).shortValue());
		} else if (obj instanceof Boolean) {
			query.setBoolean(i, ((Boolean) obj).booleanValue());
		} else if (obj instanceof List) {
			query.setParameterList("list", (List<?>) obj);
		} else if (obj instanceof Object[]) {
			query.setParameterList("in", (Object[]) obj);
		} else if(obj!=null){
			query.setEntity(i, obj);
		}else{
			query.setParameter(i, "");
		}
	}

	/**
	 * ���ò����ַ����������ò���
	 * 
	 * @param s
	 * @param obj
	 * @param query
	 */
	private void setParameterByStr(String s, Object obj, Query query) {
		if (obj instanceof Long) {
			query.setLong(s, ((Long) obj).longValue());
		} else if (obj instanceof String) {
			query.setString(s, obj.toString());
		} else if (obj instanceof Double) {
			query.setDouble(s, ((Double) obj).doubleValue());
		} else if (obj instanceof Float) {
			query.setFloat(s, ((Float) obj).floatValue());
		} else if (obj instanceof Integer) {
			query.setInteger(s, ((Integer) obj).intValue());
		} else if (obj instanceof Date || obj instanceof Time) {
			query.setDate(s, (Date) obj);
		} else if (obj instanceof Short) {
			query.setShort(s, ((Short) obj).shortValue());
		} else if (obj instanceof Boolean) {
			query.setBoolean(s, ((Boolean) obj).booleanValue());
		} else if (obj instanceof List) {
			query.setParameterList("list", (List<?>) obj);
		} else if (obj instanceof Object[]) {
			query.setParameterList("in", (Object[]) obj);
		} else if (obj !=null){
			query.setEntity(s, obj);
		}else{
			query.setParameter(s, "");
		}
	}

	/**
	 * ���ô���Ķ���������ѯ�������
	 * 
	 * @param entity
	 * @return ��ѯ�������
	 */
	protected Criteria createCriteria(Object entity) {
		Criteria criteria = createTemporarySession().createCriteria(
				entity.getClass());

		List<Method> methods = Reflections.getGetterMethods(entity.getClass());
		try {
			for (Method m : methods) {
				String methodName = m.getName();
				String property = methodName.substring(3);
				property = property.substring(0, 1).toLowerCase()
						+ property.substring(1);
				Object value = Reflections.invoke(m, entity, new Object[] {});
				if (!property.equals("class") && value != null)
					criteria.add(Restrictions.eq(property, value));
			}
		} catch (Exception ex) {
			throw new AppException(ex, "app.basedao.criteria.create", null,
					new String[] {});
		}

		return criteria;
	}

	/**
	 * ������ʱSession����
	 * 
	 * @return ��ʱSession����
	 */
	private Session createTemporarySession() {
		SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) super
				.getSessionFactory();
		return sessionFactoryImpl.openTemporarySession();
	}

	/**
	 * ������ʱSession����
	 * 
	 * @param tepSession
	 */
	public void releaseTemporarySession(Session tepSession) {
		if (tepSession != null)
			super.releaseSession(tepSession);
		else if (this.tepSession != null)
			super.releaseSession(tepSession);
	}

	/**
	 * ͨ��HQL���Ƶõ�HQL
	 * 
	 * @param queryName
	 * @return HQL�ַ���
	 */
	public String getQueryString(String queryName) {
		SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) super
				.getHibernateTemplate().getSessionFactory().getCurrentSession()
				.getSessionFactory();
		NamedSQLQueryDefinition nsqd = sessionFactoryImpl
				.getNamedSQLQuery(queryName);

		return nsqd.getQueryString();
	}

	/**
	 * ͨ����ȻSQL���в�ѯ
	 * 
	 * @param queryString
	 * @return �������
	 */
	@SuppressWarnings("unchecked")
	public List queryByNativeSQL(String queryString) {
		return super.getHibernateTemplate().getSessionFactory()
				.getCurrentSession().createSQLQuery(queryString).list();
	}

	public Session getNewSession() {
		return newSession;
	}

	public void setNewSession(Session newSession) {
		this.newSession = newSession;
	}

	public void createNewSession() {
		SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) super
				.getSessionFactory();
		newSession = sessionFactoryImpl.openTemporarySession();
	}

	public void releaseNewSession() {
		if (newSession != null)
			super.releaseSession(newSession);
	}
}
