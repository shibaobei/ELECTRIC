package com.elec.service;


import com.elec.entity.ElecUser;
import com.elec.entity.ElecUserFile;

import java.util.ArrayList;
import java.util.List;

public interface IElecUserService {
	public static final String SERVICE_NAME = "com.itheima.elec.service.impl.ElecUserServiceImpl";
	String checkUser(String logonName);
	List<ElecUser> findUserListByCondition(ElecUser elecUser);
	void saveUser(ElecUser elecUser);

	ElecUser findUserByID(String userID);

	ElecUserFile findUserFileByID(String fileID);

	void deleteUserByID(ElecUser elecUser);
	ElecUser findUserByLoninName(String loginName);
	ArrayList<String> findFieldNameWithExcel();

	ArrayList<ArrayList<String>> findFieldDataWithExcel(ElecUser elecUser);

	void saveUserList(List<ElecUser> userList);
	List<Object[]> chartUser(String zName, String eName);

}
