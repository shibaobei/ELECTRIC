package com.elec.dao;


import com.elec.entity.ElecSystemDDL;

import java.util.List;

public interface IElecSystemDDLDao extends ICommonDao<ElecSystemDDL> {
	
	public static final String SERVICE_NAME = "com.itheima.elec.dao.impl.ElecSystemDDLDaoImpl";

	List<ElecSystemDDL> findSystemDDLListByDistinct();

	String findDdlNameByKeywordAndDdlCode(String keywrod, String ddlCode);

	String findDdlCodeByKeywordAndDdlName(String keyword, String ddlName);
	
}
