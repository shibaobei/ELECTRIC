package com.elec.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Administrator on 2017-10-29.
 */
public class ElecTest implements Serializable{
    private String testID;
    private String testName;
    private Date testDate;
    private String testRemark;

    public String getTestID() {
        return testID;
    }

    public void setTestID(String testID) {
        this.testID = testID;
    }

    public String getTestName() {
        return testName;
    }

    public void setTestName(String testName) {
        this.testName = testName;
    }

    public Date getTestDate() {
        return testDate;
    }

    public void setTestDate(Date testDate) {
        this.testDate = testDate;
    }

    public String getTestRemark() {
        return testRemark;
    }

    public void setTestRemark(String testRemark) {
        this.testRemark = testRemark;
    }
}
