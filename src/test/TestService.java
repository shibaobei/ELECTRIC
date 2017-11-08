package test;

import com.elec.entity.ElecTest;
import com.elec.service.IElecTestService;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Date;

/**
 * Created by Administrator on 2017-10-29.
 */
public class TestService {
    @Test
    public void save(){
        ApplicationContext ac = new ClassPathXmlApplicationContext("config/applicationContext.xml");
        IElecTestService iElecTestService = (IElecTestService) ac.getBean(IElecTestService.SERVICE_NAME);

        ElecTest elecTest = new ElecTest();
        elecTest.setTestName("测试Service名称");
        elecTest.setTestDate(new Date());
        elecTest.setTestRemark("测试Service备注");

        iElecTestService.saveElecTest(elecTest);
    }
}
