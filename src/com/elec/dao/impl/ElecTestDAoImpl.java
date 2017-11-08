package com.elec.dao.impl;

import com.elec.dao.IElecTestDao;
import com.elec.entity.ElecTest;
import org.springframework.stereotype.Repository;

/**
 * Created by Administrator on 2017-10-29.
 *  @Repository(IElecTextDao.SERVICE_NAME)
 * 相当于在spring容器中定义：
 * <bean id="com.elec.dao.impl.ElecTestDaoImpl" class="com.elec.dao.impl.ElecTestDaoImpl">
 */
@Repository(IElecTestDao.SERVICE_NAME)
public class ElecTestDAoImpl extends CommonDaoImpl<ElecTest> implements IElecTestDao {

}
