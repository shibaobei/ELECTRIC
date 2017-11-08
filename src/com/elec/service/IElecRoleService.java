package com.elec.service;

import com.elec.entity.ElecPopedom;
import com.elec.entity.ElecRole;
import com.elec.entity.ElecUser;

import java.util.Hashtable;
import java.util.List;



public interface IElecRoleService {
	public static final String SERVICE_NAME = "com.itheima.elec.service.impl.ElecRoleServiceImpl";

	List<ElecRole> findAllRoleList();

	List<ElecPopedom> findAllPopedomList();

	List<ElecPopedom> findAllPopedomListByRoleID(String roleID);

	List<ElecUser> findAllUserListByRoleID(String roleID);

	void saveRole(ElecPopedom elecPopedom);

	String findPopedomByRoleIDs(Hashtable<String, String> ht);
	List<ElecPopedom> findPopedomListByString(String popedom);

	boolean findRolePopedomByID(String roleID, String mid, String pid);

}
