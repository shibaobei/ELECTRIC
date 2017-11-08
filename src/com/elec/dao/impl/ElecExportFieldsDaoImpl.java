package com.elec.dao.impl;

import com.elec.dao.IElecExportFieldsDao;
import com.elec.entity.ElecExportFields;
import org.springframework.stereotype.Repository;



@Repository(IElecExportFieldsDao.SERVICE_NAME)
public class ElecExportFieldsDaoImpl  extends CommonDaoImpl<ElecExportFields> implements IElecExportFieldsDao {

}
