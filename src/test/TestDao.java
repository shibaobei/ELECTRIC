package test;

import com.elec.dao.IElecTestDao;
import com.elec.entity.ElecTest;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;

/**
 * Created by Administrator on 2017-10-29.
 */
public class TestDao {
    @Test
    public void save(){
        ApplicationContext ac = new ClassPathXmlApplicationContext("config/applicationContext.xml");
        IElecTestDao iElecTestDao = (IElecTestDao) ac.getBean(IElecTestDao.SERVICE_NAME);

        ElecTest elecTest = new ElecTest();
        elecTest.setTestName("测试Dao名称");
        elecTest.setTestDate(new Date());
        elecTest.setTestRemark("测试Dao备注");

        iElecTestDao.save(elecTest);

    }
}
