package com.elec.dao.impl;

import com.elec.dao.IElecUserDao;
import com.elec.entity.ElecUser;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.springframework.orm.hibernate3.HibernateCallback;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;


@Repository(IElecUserDao.SERVICE_NAME)
public class ElecUserDaoImpl  extends CommonDaoImpl<ElecUser> implements IElecUserDao {
    /**
     * @Name: chartUser
     * @Description: 统计用户分配情况
     * @Author: 刘洋（作者）
     * @Version: V1.00 （版本号）
     * @Create Date: 2014-12-10（创建日期）
     * @Parameters: String zName：传递的数据类型
     * 				 String eName：字段名称
     * @Return:List<Object[]>：数据集合
     */
    public List<Object[]> chartUser(String zName, String eName) {
        final String sql = "SELECT b.keyword,b.ddlName,COUNT(b.ddlCode) FROM elec_user a " +
                " INNER JOIN elec_systemddl b ON a."+eName+" = b.ddlCode AND b.keyword='"+zName+"' " +
                " WHERE a.isDuty='1' " +
                " GROUP BY b.keyword,b.ddlName " +
                " ORDER BY b.ddlCode ASC ";
        List<Object[]> list = (List<Object[]>) this.getHibernateTemplate().execute(new HibernateCallback() {

            public Object doInHibernate(Session session)
                    throws HibernateException, SQLException {
                SQLQuery query = session.createSQLQuery(sql);
                return query.list();
            }

        });
        return list;
    }
}
