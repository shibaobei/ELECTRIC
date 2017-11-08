package com.elec.dao.impl;

import com.elec.dao.IElecRoleDao;
import com.elec.entity.ElecRole;
import org.springframework.stereotype.Repository;




@Repository(IElecRoleDao.SERVICE_NAME)
public class ElecRoleDaoImpl  extends CommonDaoImpl<ElecRole> implements IElecRoleDao {

}
