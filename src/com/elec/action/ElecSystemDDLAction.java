package com.elec.action;

import java.util.List;

import javax.annotation.Resource;

import com.elec.entity.ElecSystemDDL;
import com.elec.service.IElecSystemDDLService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;



@SuppressWarnings("serial")
@Controller("elecSystemDDLAction")
@Scope(value="prototype")
public class ElecSystemDDLAction extends BaseAction<ElecSystemDDL> {
	
	ElecSystemDDL elecSystemDDL = this.getModel();
	
	/**注入运行监控Service*/
	@Resource(name= IElecSystemDDLService.SERVICE_NAME)
	IElecSystemDDLService elecSystemDDLService;
	
	
	/**  
	* @Name: home
	* @Description: 数据字典的首页显示
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-12-1（创建日期）
	* @Parameters: 无
	* @Return: String：跳转到system/dictionaryIndex.jsp
	*/
	public String home(){
		// * 1：查询数据库中已有的数据类型，返回List<ElecSystemDDL>集合，遍历到页面的下拉菜单中
		List<ElecSystemDDL> list = elecSystemDDLService.findSystemDDLListByDistinct();
		request.setAttribute("list", list);
		return "home";
	}
	
	/**  
	* @Name: edit
	* @Description: 跳转到编辑数据字典的页面的页面
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-12-1（创建日期）
	* @Parameters: 无
	* @Return: String：跳转到system/dictionaryEdit.jsp
	*/
	public String edit(){
		//1：获取数据类型
		String keyword = elecSystemDDL.getKeyword();
		//2：使用数据类型作为条件，查询数据字典，返回List<ElecSystemDDL>
		List<ElecSystemDDL> list = elecSystemDDLService.findSystemDDLListByKeyword(keyword);
		request.setAttribute("list", list);
		return "edit";
	}
	
	/**  
	* @Name: save
	* @Description: 保存数据字典
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-12-1（创建日期）
	* @Parameters: 无
	* @Return: String：重定向system/dictionaryEdit.jsp
	*/
	public String save(){
		elecSystemDDLService.saveSystemDDL(elecSystemDDL);
		return "save";
	}
	
	
}
