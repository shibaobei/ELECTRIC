package com.elec.action;

import javax.annotation.Resource;

import com.elec.entity.ElecTest;
import com.elec.service.IElecTestService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;


/**
 * @Controller("elecTextAction")
 * 相当于spring容器中定义
 * <bean id="elecTestAction" class="com.elec.web.action.ElecTestAction" scope="prototype">
 */
@Controller("elecTestAction")
@Scope(value="prototype")
public class ElecTestAction extends BaseAction<ElecTest> {
	
	ElecTest elecText = this.getModel();
	
	/**注入Service*/
	@Resource(name= IElecTestService.SERVICE_NAME)
	IElecTestService elecTextService;
	
	/**  
	* @Name: save
	* @Description: 保存
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-11-28 （创建日期）
	* @Parameters: 无
	* @Return: String：跳转到textAdd.jsp
	*/
	public String save(){
		elecTextService.saveElecTest(elecText);
		String textDate = request.getParameter("textDate");
		return "save";
	}

	
}
