package com.elec.service.impl;

import javax.annotation.Resource;

import com.elec.dao.IElecExportFieldsDao;
import com.elec.entity.ElecExportFields;
import com.elec.service.IElecExportFieldsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;



@Service(IElecExportFieldsService.SERVICE_NAME)
@Transactional(readOnly=true)
public class ElecExportFieldsServiceImpl implements IElecExportFieldsService {

	/**导出设置表Dao*/
	@Resource(name= IElecExportFieldsDao.SERVICE_NAME)
	IElecExportFieldsDao elecExportFieldsDao;
	

	/**  
	* @Name: findExportFieldsByID
	* @Description: 使用主键ID，查询对应的导出设置对象
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-12-06（创建日期）
	* @Parameters: String：主键ID
	* @Return: ElecExportFields：PO对象
	*/
	public ElecExportFields findExportFieldsByID(String belongTo) {
		return elecExportFieldsDao.findObjectByID(belongTo);
	}
	
	/**  
	* @Name: saveSetExportExcel
	* @Description: 更新保存导出设置的操作
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-12-06（创建日期）
	* @Parameters: ElecExportFields：VO对象
	* @Return: 无
	*/
	@Transactional(isolation=Isolation.DEFAULT,propagation=Propagation.REQUIRED,readOnly=false)
	public void saveSetExportExcel(ElecExportFields elecExportFields) {
		elecExportFieldsDao.update(elecExportFields);
	}
}
