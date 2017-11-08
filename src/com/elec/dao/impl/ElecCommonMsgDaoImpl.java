package com.elec.dao.impl;

import com.elec.dao.IElecCommonMsgDao;
import com.elec.entity.ElecCommonMsg;
import org.springframework.stereotype.Repository;


@Repository(IElecCommonMsgDao.SERVICE_NAME)
public class ElecCommonMsgDaoImpl  extends CommonDaoImpl<ElecCommonMsg> implements IElecCommonMsgDao {

}
