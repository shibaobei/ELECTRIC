package com.elec.dao;

import com.elec.entity.ElecRolePopedom;

import java.util.List;



public interface IElecRolePopedomDao extends ICommonDao<ElecRolePopedom> {
	
	public static final String SERVICE_NAME = "com.itheima.elec.dao.impl.ElecRolePopedomDaoImpl";

	List<Object> findPopedomByRoleIDs(String condition);

	
	
}
