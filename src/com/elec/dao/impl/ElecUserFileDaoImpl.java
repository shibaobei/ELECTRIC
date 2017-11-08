package com.elec.dao.impl;

import com.elec.dao.IElecUserFileDao;
import com.elec.entity.ElecUserFile;
import org.springframework.stereotype.Repository;



@Repository(IElecUserFileDao.SERVICE_NAME)
public class ElecUserFileDaoImpl  extends CommonDaoImpl<ElecUserFile> implements IElecUserFileDao {

}
