package com.nsc.base.hibernate;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.engine.NamedSQLQueryDefinition;
import org.hibernate.impl.SessionFactoryImpl;
import org.springframework.orm.hibernate3.HibernateCallback;

/**
 * ͨ�����ݿ��ѯ������
 * 
 * ����ͨ����BaseDAO����չ����һ���������ݿ�������ܡ���֤������ͳһ������ȷ��
 * 
 * @author bs-team
 * 
 * @date Oct 19, 2010 9:57:52 AM
 * @version
 */
@SuppressWarnings("all")
public class GeneralDAO extends BaseDAO {

	/**
	 * ����Entity�����ȡ��¼��
	 * 
	 * @param entity
	 * @return ��¼��
	 */
	@SuppressWarnings("unchecked")
	public List getResult(Object entity) {
		return super.getHibernateTemplate().findByExample(entity);
	}

	/**
	 * ����Entity�����ȡ��¼��
	 * 
	 * @param entity
	 *            --------------------
	 * @return ��¼��
	 */
	@SuppressWarnings("unchecked")
	public List getResultPage(Object entity, int firstResult, int maxResults) {

		List list = super.getHibernateTemplate().findByExample(entity,
				firstResult, maxResults);

		return list;
	}

	/**
	 * ����ID��ѯʵ��
	 * 
	 * @param entityClass
	 * @param id
	 * @return
	 */
	public Object findByID(Class<?> clazz, Serializable id) {
		return getHibernateTemplate().get(clazz, id);
	}

	/**
	 * ����ɾ��Entity
	 * 
	 * @param entity
	 */
	public void deleteAll(String queryString) {
		super.getHibernateTemplate().getSessionFactory().getCurrentSession()
				.createSQLQuery(queryString).executeUpdate();
	}

	/**
	 * ����HQL�����ƺ����������ѯ
	 * 
	 * @param hqlName
	 * @param params
	 * @return ��¼��
	 */
	@SuppressWarnings("unchecked")
	public List getResult(String hqlName, Object[] params) {
		return findResultBySQLName(hqlName, params);
	}

	/**
	 * ����HQL�����ƺ����������ѯ�������������CLASSת��
	 * 
	 * @param hqlName
	 * @param params
	 * @param class_
	 * @return ��¼��
	 */
	@SuppressWarnings("unchecked")
	public List getResult(String hqlName, Object[] params, Class<?> class_) {
		return findResultBySQLName(hqlName, params, class_);
	}

	/**
	 * ����HQL�����ƺ�HashMap������ѯ�������������CLASSת��
	 * 
	 * @param hqlName
	 * @param paraMap
	 * @return
	 */
	public List getResult(String hqlName, Map<String, Object> paraMap) {
		return findResultBySQLName(hqlName, paraMap);
	}

	/**
	 * ��ҳ��ѯ ͨ��HQL���Ƶ������ļ��л�ȡHQL��Ȼ����в�ѯ�� HQL�п����� ?���ò��� :list����ͨ���б�����
	 * :in����ͨ���������ã�����ͨ��Map<String, Parameter>���롣 ͨ�����ÿ�ʼ��¼�ͽ�����¼��С��ѯ��Χ
	 * 
	 * @param hqlName
	 * @param paraMap
	 * @param firstResult
	 * @param maxResults
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public List getResult(final String hqlName,
			final Map<String, Object> paraMap, int firstResult, int maxResults)
			throws Exception {
		return findResultBySQLName(hqlName, paraMap, firstResult, maxResults);
	}

	/**
	 * ��ҳ��ѯ ͨ��HQL���Ƶ������ļ��л�ȡHQL��Ȼ����в�ѯ�� HQL�п����� ?���ò��� :list����ͨ���б�����
	 * :in����ͨ���������ã�����ͨ��Map<String, Parameter>���롣 ͨ�����ÿ�ʼ��¼�ͽ�����¼��С��ѯ��Χ
	 * 
	 * @param hqlName
	 * @param paraMap
	 * @param firstResult
	 * @param maxResults
	 * @param clazz
	 * @return
	 * @throws Exception
	 */
	public List getResult(final String hqlName, final Object[] obj,
			int firstResult, int maxResults) throws Exception {
		return findResultBySQLName(hqlName, obj, firstResult, maxResults);
	}

	public long getResultCount(final String hqlName,
			final Map<String, Object> paraMap) {
		return super.findCountBySQLName(hqlName, paraMap);
	}

	public long getResultCount(final String hqlName, final Object[] obj) {
		return super.findCountBySQLName(hqlName, obj);
	}

	/**
	 * ����HQL�����ƽ��в�ѯ
	 * 
	 * @param hqlName
	 * @return ��¼��
	 */
	@SuppressWarnings("unchecked")
	public List getResult(String hqlName) {
		return findResultBySQLName(hqlName, new Object[] {});
	}

	/**
	 * ���ô����HQL���в�ѯ
	 * 
	 * @param hql
	 * @return ��¼��
	 */
	@SuppressWarnings("unchecked")
	public List queryByHQL(String hql) {
		return super.getHibernateTemplate().find(hql);
	}

	/**
	 * ��ѯ�����Entity����
	 * 
	 * @param entity
	 */
	public void refresh(Object entity) {
		super.getHibernateTemplate().refresh(entity);
	}

	/**
	 * �ں�Entity���������ݿ����Ƿ��м�¼���Զ�ѡ����»����
	 * 
	 * @param entity
	 */
	public void merge(Object entity) {
		super.getHibernateTemplate().merge(entity);
	}

	/**
	 * ����Entity�����ݿ�
	 * 
	 * @param entity
	 */
	public void add(Object entity) {
		super.getHibernateTemplate().save(entity);
	}

	/**
	 * ����Entity
	 * 
	 * @param entity
	 */
	public void modify(Object entity) {
		super.getHibernateTemplate().update(entity);
	}

	/**
	 * ɾ��Entity
	 * 
	 * @param entity
	 */
	public void delete(Object entity) {
		super.getHibernateTemplate().delete(entity);
	}

	public List queryByString(String queryString) {
		return super.getHibernateTemplate().find(queryString);
	}

	public String getQueryString(String queryName) {
		SessionFactoryImpl sessionFactoryImpl = (SessionFactoryImpl) super
				.getHibernateTemplate().getSessionFactory().getCurrentSession()
				.getSessionFactory();
		NamedSQLQueryDefinition nsqd = sessionFactoryImpl
				.getNamedSQLQuery(queryName);

		return nsqd.getQueryString();
	}

	public List<Object[]> queryByNativeSQL(String queryString) {
		return super.getHibernateTemplate().getSessionFactory()
				.getCurrentSession().createSQLQuery(queryString).list();
	}

	public List<?> queryByNativeSQL(String queryString,
			final Map<String, Object> paraMap) {
		return super.findResultBySQL(queryString, paraMap);
	}

	@SuppressWarnings("unchecked")
	public List<Map<String, Object>> queryByNativeSQL2(final String queryString) {
		return getHibernateTemplate().executeFind(new HibernateCallback() {
			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				// native sql��ѯ������list����map<key:column,value:columnValue>
				return session.createSQLQuery(queryString)
						.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)
						.list();
			}
		});
	}

	public Criteria createCriteria(Object entity) {
		return super.createCriteria(entity);
	}

	/**
	 * ʹ�ñ���SQL����ѯ������ʵ����
	 * 
	 * @param queryString
	 *            SQL���
	 * @param clazz
	 *            ʵ�����class
	 * @return
	 */
	public List queryByNativeSQLEntity(String queryString, Class clazz) {
		SQLQuery query = super.getHibernateTemplate().getSessionFactory()
				.getCurrentSession().createSQLQuery(queryString).addEntity(
						clazz);

		return query == null ? null : query.list();

	}

	/**
	 * ��ѯ�ۺϺ�����ֵ
	 * 
	 * @param queryString
	 * @param strScalar
	 *            �ۺϺ����ı���
	 * @return
	 */
	public Integer queryByNativeSQLMAX(String queryString, String strScalar) {
		return (Integer) super.getHibernateTemplate().getSessionFactory()
				.getCurrentSession().createSQLQuery(queryString).addScalar(
						strScalar, Hibernate.INTEGER).uniqueResult();
	}

}
