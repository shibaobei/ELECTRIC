package test;

import com.elec.entity.ElecTest;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.Test;

import java.util.Date;

/**
 * Created by Administrator on 2017-10-29.
 */
public class TestHibernate {
    @Test
    public void save(){
        Configuration configuration = new Configuration();
        configuration.configure();//加载classPath下的hiber.cfg.xml
        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();

        ElecTest elecTest = new ElecTest();
        elecTest.setTestName("测试hibernate名称");
        elecTest.setTestRemark("测试hib备注");
        elecTest.setTestDate(new Date());
        session.save(elecTest);

        transaction.commit();
        session.close();
    }
}
