package com.elec.service;


import com.elec.entity.ElecSystemDDL;

import java.util.List;

public interface IElecSystemDDLService {
	public static final String SERVICE_NAME = "com.itheima.elec.service.impl.ElecSystemDDLServiceImpl";

	List<ElecSystemDDL> findSystemDDLListByDistinct();

	List<ElecSystemDDL> findSystemDDLListByKeyword(String keyword);

	void saveSystemDDL(ElecSystemDDL elecSystemDDL);
	String findDdlNameByKeywordAndDdlCode(String keyword, String ddlCode);
	String findDdlCodeByKeywordAndDdlName(String keywrod, String ddlName);

}
