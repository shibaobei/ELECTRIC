package com.elec.service.impl;

import com.elec.dao.IElecTestDao;
import com.elec.entity.ElecTest;
import com.elec.service.IElecTestService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2017-10-29.
 * * @Service
 * 相当于在spring容器中定义：
 * <bean id="com.elec.service.impl.ElecTestServiceImpl" class="com.elec.service.impl.ElecTestServiceImpl">
 */
@Service(IElecTestService.SERVICE_NAME)
@Transactional(readOnly=true)
public class ElecServiceImpl implements IElecTestService{
    @Resource(name=IElecTestDao.SERVICE_NAME)
    private IElecTestDao iElecTestDao;
    @Override
    @Transactional(readOnly=false)
    public void saveElecTest(ElecTest elecTest) {
        this.iElecTestDao.save(elecTest);
    }
    /**指定查询条件，查询列表*/
    /**
     * SELECT * FROM elec_text o WHERE 1=1     #Dao层
     AND o.textName LIKE '%张%'   #Service层
     AND o.textRemark LIKE '%张%'   #Service层
     ORDER BY o.textDate ASC,o.textName DESC  #Service层
     */
    @Override
    public List<ElecTest> findCollectionByConditionNoPage(ElecTest elecText) {
        //查询条件
        String condition = "";
        //查询条件对应的参数
        List<Object> paramsList = new ArrayList<Object>();
        if(StringUtils.isNotBlank(elecText.getTestName())){
            condition += " and o.textName like ?";
            paramsList.add("%"+elecText.getTestName()+"%");
        }
        if(StringUtils.isNotBlank(elecText.getTestRemark())){
            condition += " and o.textRemark like ?";
            paramsList.add("%"+elecText.getTestRemark()+"%");
        }
        //传递可变参数
        Object [] params = paramsList.toArray();
        //排序
        Map<String, String> orderby = new LinkedHashMap<String, String>();//有序
        orderby.put("o.textDate", "asc");
        orderby.put("o.textName", "desc");
        //查询
        List<ElecTest> list = iElecTestDao.findCollectionByConditionNoPage(condition,params,orderby);
        return list;
    }
}
