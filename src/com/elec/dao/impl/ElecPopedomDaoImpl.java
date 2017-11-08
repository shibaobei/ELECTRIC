package com.elec.dao.impl;

import com.elec.dao.IElecPopedomDao;
import com.elec.entity.ElecPopedom;
import org.springframework.stereotype.Repository;




@Repository(IElecPopedomDao.SERVICE_NAME)
public class ElecPopedomDaoImpl  extends CommonDaoImpl<ElecPopedom> implements IElecPopedomDao {

}
