package com.elec.service;


import com.elec.entity.ElecCommonMsg;

public interface IElecCommonMsgService {
	public static final String SERVICE_NAME = "com.itheima.elec.service.impl.ElecCommonMsgServiceImpl";

	ElecCommonMsg findCommonMsg();

	void saveCommonMsg(ElecCommonMsg elecCommonMsg);

}
