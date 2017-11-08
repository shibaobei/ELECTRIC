package com.elec.dao;


import com.elec.entity.ElecUser;

import java.util.List;

public interface IElecUserDao extends ICommonDao<ElecUser> {
	
	public static final String SERVICE_NAME = "com.itheima.elec.dao.impl.ElecUserDaoImpl";
	List<Object[]> chartUser(String zName, String eName);

	
	
}
