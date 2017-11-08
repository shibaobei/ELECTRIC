package com.elec.dao.impl;

import com.elec.dao.IElecCommonMsgContentDao;
import com.elec.entity.ElecCommonMsgContent;
import org.springframework.stereotype.Repository;



@Repository(IElecCommonMsgContentDao.SERVICE_NAME)
public class ElecCommonMsgContentDaoImpl  extends CommonDaoImpl<ElecCommonMsgContent> implements IElecCommonMsgContentDao {

}
