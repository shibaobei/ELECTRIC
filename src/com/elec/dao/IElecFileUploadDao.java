package com.elec.dao;

import com.elec.entity.ElecFileUpload;

import java.util.List;
import java.util.Map;


public interface IElecFileUploadDao extends ICommonDao<ElecFileUpload> {
	
	public static final String SERVICE_NAME = "com.itheima.elec.dao.impl.ElecFileUploadDaoImpl";

	List<ElecFileUpload> findFileUploadListByCondition(String condition,
													   Object[] params, Map<String, String> orderby);

	
	
}
