package com.elec.dao.impl;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.elec.dao.ICommonDao;
import com.elec.utils.PageInfo;
import com.elec.utils.TUtil;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;



public class CommonDaoImpl<T> extends HibernateDaoSupport implements ICommonDao<T> {
	/**T型转换*/
	Class entityClass = TUtil.getActualType(this.getClass());

	@Resource(name="sessionFactory")
	public void setDi(SessionFactory sessionFactory){
		this.setSessionFactory(sessionFactory);
	}

	/**保存*/
	public void save(T entity) {
		this.getHibernateTemplate().save(entity);
	}

	/**更新*/
	public void update(T entity) {
		this.getHibernateTemplate().update(entity);
	}

	/**使用主键ID，查询对象*/
	public T findObjectByID(Serializable id) {
		return (T) this.getHibernateTemplate().get(entityClass, id);
	}

	/**删除（使用主键ID删除）*/
	public void deleteObjectByIds(Serializable... ids) {
		if(ids!=null && ids.length>0){
			for(Serializable id:ids){
				//先查询
				Object entity = this.findObjectByID(id);
				//再删除
				this.getHibernateTemplate().delete(entity);
			}
		}
	}

	/**删除（使用集合List进行删除）*/
	public void deleteObjectByCollection(List<T> list) {
		this.getHibernateTemplate().deleteAll(list);
	}

	/**指定查询条件，查询列表*/
	/**
	 * SELECT * FROM elec_text o WHERE 1=1     #Dao层
	 AND o.textName LIKE '%张%'   #Service层
	 AND o.textRemark LIKE '%张%'   #Service层
	 ORDER BY o.textDate ASC,o.textName DESC  #Service层
	 */
	public List<T> findCollectionByConditionNoPage(String condition,
												   final Object[] params, Map<String, String> orderby) {
		//hql语句
		String hql = "from "+entityClass.getSimpleName()+" o where 1=1 ";
		//将Map集合中存放的字段排序，组织成ORDER BY o.textDate ASC,o.textName DESC
		String orderbyCondition = this.orderbyHql(orderby);
		//添加查询条件
		final String finalHql = hql + condition + orderbyCondition;
		//查询，执行hql语句
		/**方案一*/
		//List<T> list = this.getHibernateTemplate().find(finalHql, params);
		/**方案二*/
//		SessionFactory sf = this.getHibernateTemplate().getSessionFactory();
//		Session s = sf.getCurrentSession();//Session与线程绑定
//		Query query = s.createQuery(finalHql);
//		if(params!=null && params.length>0){
//			for(int i=0;i<params.length;i++){
//				query.setParameter(i, params[i]);
//			}
//		}
//		List<T> list = query.list();
		/**方案三*/
		List<T> list = (List<T>) this.getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery(finalHql);
				if(params!=null && params.length>0){
					for(int i=0;i<params.length;i++){
						query.setParameter(i, params[i]);
					}
				}
				return query.list();
			}

		});
		return list;
	}

	/**指定查询条件，查询列表 （不分页），使用投影查询*/
	/**
	 * SELECT * FROM elec_text o WHERE 1=1     #Dao层
	 AND o.textName LIKE '%张%'   #Service层
	 AND o.textRemark LIKE '%张%'   #Service层
	 ORDER BY o.textDate ASC,o.textName DESC  #Service层
	 */
	public List findCollectionByConditionNoPageWithSelectCondition(String condition,
																   final Object[] params, Map<String, String> orderby,String selectCondition) {
		//hql语句
		String hql = "select "+selectCondition+" from "+entityClass.getSimpleName()+" o where 1=1 ";
		//将Map集合中存放的字段排序，组织成ORDER BY o.textDate ASC,o.textName DESC
		String orderbyCondition = this.orderbyHql(orderby);
		//添加查询条件
		final String finalHql = hql + condition + orderbyCondition;
		//查询，执行hql语句
		List list = (List) this.getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery(finalHql);
				if(params!=null && params.length>0){
					for(int i=0;i<params.length;i++){
						query.setParameter(i, params[i]);
					}
				}
				return query.list();
			}

		});
		return list;
	}

	/**指定查询条件，查询列表（使用分页）*/
	/**
	 * SELECT * FROM elec_text o WHERE 1=1     #Dao层
	 AND o.textName LIKE '%张%'   #Service层
	 AND o.textRemark LIKE '%张%'   #Service层
	 ORDER BY o.textDate ASC,o.textName DESC  #Service层
	 */
	public List<T> findCollectionByConditionWithPage(String condition,
													 final Object[] params, Map<String, String> orderby,final PageInfo pageInfo) {
		//hql语句
		String hql = "from "+entityClass.getSimpleName()+" o where 1=1 ";
		//将Map集合中存放的字段排序，组织成ORDER BY o.textDate ASC,o.textName DESC
		String orderbyCondition = this.orderbyHql(orderby);
		//添加查询条件
		final String finalHql = hql + condition + orderbyCondition;

		List<T> list = (List<T>) this.getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery(finalHql);
				if(params!=null && params.length>0){
					for(int i=0;i<params.length;i++){
						query.setParameter(i, params[i]);
					}
				}
				/**2017-12-9,添加分页 begin*/
				pageInfo.setTotalResult(query.list().size());//初始化总的记录数
				query.setFirstResult(pageInfo.getBeginResult());//当前页从第几条开始检索，默认是0,0是第一条
				query.setMaxResults(pageInfo.getPageSize());//当前页最多显示多少条记录
				/**2017-12-9,添加分页 end*/
				return query.list();
			}

		});
		return list;
	}


	/**指定查询条件，查询列表，指定查询缓存（二级缓存）*/
	/**
	 * SELECT * FROM elec_text o WHERE 1=1     #Dao层
	 AND o.textName LIKE '%张%'   #Service层
	 AND o.textRemark LIKE '%张%'   #Service层
	 ORDER BY o.textDate ASC,o.textName DESC  #Service层
	 */
	public List<T> findCollectionByConditionNoPageWithCache(String condition,
															final Object[] params, Map<String, String> orderby) {
		//hql语句
		String hql = "from "+entityClass.getSimpleName()+" o where 1=1 ";
		//将Map集合中存放的字段排序，组织成ORDER BY o.textDate ASC,o.textName DESC
		String orderbyCondition = this.orderbyHql(orderby);
		//添加查询条件
		final String finalHql = hql + condition + orderbyCondition;

		/**方案三*/
		List<T> list = (List<T>) this.getHibernateTemplate().execute(new HibernateCallback() {

			public Object doInHibernate(Session session)
					throws HibernateException, SQLException {
				Query query = session.createQuery(finalHql);
				if(params!=null && params.length>0){
					for(int i=0;i<params.length;i++){
						query.setParameter(i, params[i]);
					}
				}
				query.setCacheable(true);
				return query.list();
			}

		});
		return list;
	}

	/**将Map集合中存放的字段排序，组织成
	 * ORDER BY o.textDate ASC,o.textName DESC*/
	private String orderbyHql(Map<String, String> orderby) {
		StringBuffer buffer = new StringBuffer("");
		if(orderby!=null && orderby.size()>0){
			buffer.append(" ORDER BY ");
			for(Map.Entry<String, String> map:orderby.entrySet()){
				buffer.append(map.getKey()+" "+map.getValue()+",");
			}
			//在循环后，删除最后一个逗号
			buffer.deleteCharAt(buffer.length()-1);
		}
		return buffer.toString();
	}

	/**批量保存*/
	public void saveList(List<T> list) {
		this.getHibernateTemplate().saveOrUpdateAll(list);
	}

}
