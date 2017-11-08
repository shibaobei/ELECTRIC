package com.elec.action;

import java.util.List;

import javax.annotation.Resource;

import com.elec.entity.ElecPopedom;
import com.elec.entity.ElecRole;
import com.elec.entity.ElecUser;
import com.elec.service.IElecRoleService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;



@SuppressWarnings("serial")
@Controller("elecRoleAction")
@Scope(value="prototype")
public class ElecRoleAction extends BaseAction<ElecPopedom> {
	
	ElecPopedom elecPopedom = this.getModel();
	
	/**注入角色的Service*/
	@Resource(name= IElecRoleService.SERVICE_NAME)
	IElecRoleService elecRoleService;
	
	/**  
	* @Name: home
	* @Description: 角色管理的首页显示
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-12-03（创建日期）
	* @Parameters: 无
	* @Return: String：跳转到system/roleIndex.jsp
	*/
	public String home(){
       //一：查询系统中所有的角色
		List<ElecRole> roleList =  elecRoleService.findAllRoleList();
		request.setAttribute("roleList",roleList);
		// 二：查询系统中所有的权限
		//返回List<ElecPopedom>，存放所有的tr，也就是pid=0，父集合
		List<ElecPopedom> popedomList = elecRoleService.findAllPopedomList();
		request.setAttribute("popedomList",popedomList);
		return "home";
	}
	
	/**  
	* @Name: edit
	* @Description: 跳转到编辑页面
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-12-03（创建日期）
	* @Parameters: 无
	* @Return: String：跳转到system/roleEdit.jsp
	*/
	public String edit(){
		//角色ID
		String roleID = elecPopedom.getRoleID();
		//一：使用当前角色ID，查询系统中所有的权限，并显示（匹配）
		List<ElecPopedom> popedomList = elecRoleService.findAllPopedomListByRoleID(roleID);
		request.setAttribute("popedomList", popedomList);
		//二：使用当前角色ID，查询系统中所有的用户，并显示（匹配）
		List<ElecUser> userList = elecRoleService.findAllUserListByRoleID(roleID);
		request.setAttribute("userList", userList);
		return "edit";
	}
	
	/**  
	* @Name: save
	* @Description: 保存角色和权限，角色和用户的关联关系
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-12-03（创建日期）
	* @Parameters: 无
	* @Return: String：重定向到system/roleIndex.jsp
	*/
	public String save(){
		elecRoleService.saveRole(elecPopedom);
		return "save";
	}
	
}
