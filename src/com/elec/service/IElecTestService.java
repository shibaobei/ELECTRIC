package com.elec.service;

import com.elec.entity.ElecTest;

import java.util.List;

/**
 * Created by Administrator on 2017-10-29.
 */
public interface IElecTestService {
    public static final String SERVICE_NAME = "com.elec.service.IElecTestService";
    public void saveElecTest(ElecTest elecTest);
    List<ElecTest> findCollectionByConditionNoPage(ElecTest elecText);
}
