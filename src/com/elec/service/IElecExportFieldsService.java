package com.elec.service;


import com.elec.entity.ElecExportFields;

public interface IElecExportFieldsService {
	public static final String SERVICE_NAME = "com.itheima.elec.service.impl.ElecExportFieldsServiceImpl";

	ElecExportFields findExportFieldsByID(String belongTo);

	void saveSetExportExcel(ElecExportFields elecExportFields);


}
