package com.elec.action;

import com.elec.entity.ElecCommonMsg;
import com.elec.entity.ElecPopedom;
import com.elec.entity.ElecRole;
import com.elec.entity.ElecUser;
import com.elec.form.MenuForm;
import com.elec.service.IElecCommonMsgService;
import com.elec.service.IElecRoleService;
import com.elec.service.IElecUserService;
import com.elec.utils.LogonUtils;
import com.elec.utils.MD5keyBean;
import com.elec.utils.ValueUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;


@Controller("elecMenuAction")
@Scope(value="prototype")
public class ElecMenuAction extends BaseAction<MenuForm> {

	MenuForm menuForm = this.getModel();
	@Resource(name = IElecCommonMsgService.SERVICE_NAME)
	private  IElecCommonMsgService iElecCommonMsgService;
	@Resource(name=IElecUserService.SERVICE_NAME)
	private IElecUserService iElecUserService;
	@Resource(name= IElecRoleService.SERVICE_NAME)
	IElecRoleService elecRoleService;

	/**  
	* @Name: menuHome
	* @Description: 跳转到系统登录首页
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-11-28 （创建日期）
	* @Parameters: 无
	* @Return: String：跳转到textAdd.jsp
	*/
	public String menuHome(){
		//获取用户名和密码
		String name = menuForm.getName();
		String password = menuForm.getPassword();
		/**验证码*/
		/**
		 * flag=true:验证成功
		 * flag=false:验证失败
		 */
		boolean flag = LogonUtils.checkNumber(request);
		if(!flag){
			this.addActionError("验证码输入有误！");
			return "logonError";//跳转到登录页面
		}
		/**一：验证用户名和密码输入是否正确*/
		//* 使用登录名作为查询条件，查询用户表，因为登录名是惟一值，获取ElecUser的对象
		ElecUser elecUser = iElecUserService.findUserByLoninName(name);
		//如果EleeUser对象为空，页面提示【用户名输入有误！】
		if(elecUser==null){
			this.addActionError("用户名输入有误！");
			return "logonError";//跳转到登录页面
		}
		//* 校验密码是否正确
		if(StringUtils.isBlank(password)){
			this.addActionError("密码不能为空！");
			return "logonError";//跳转到登录页面
		}
		else{
			MD5keyBean bean = new MD5keyBean();
			String md5password = bean.getkeyBeanofStr(password);
			//比对
			if(!md5password.equals(elecUser.getLoginPwd())){
				this.addActionError("密码输入有误！");
				return "logonError";//跳转到登录页面
			}
		}
		/**二：判断用户是否分配了角色，如果分配了角色，将角色的信息存放起来*/
		/**
		 * 存放角色信息
		 * key：角色ID
		 * value：角色名称
		 */
		Hashtable<String, String> ht = new Hashtable<String, String>();
		Set<ElecRole> elecRoles = elecUser.getElecRoles();
		//当前用户没有分配角色
		if(elecRoles==null || 0==elecRoles.size()){
			this.addActionError("当前用户没有分配角色，请与管理员联系！");
			return "logonError";//跳转到登录页面
		}
		//如果分配了角色，将角色的信息存放起来
		else{
			for(ElecRole elecRole:elecRoles){
				//一个用户可以对应多个角色
				ht.put(elecRole.getRoleID(), elecRole.getRoleName());
			}
		}
		/**三：判断用户对应的角色是否分配了权限，如果分配了权限，将权限的信息存放起来（aa）*/
		//将权限的信息存放到字符串中，存放的权限的mid（字符串的格式：aa@ab@ac@ad@ae） -----jquery的ztree动态数据加载
		String popedom = elecRoleService.findPopedomByRoleIDs(ht);
		if(StringUtils.isBlank(popedom)){
			this.addActionError("当前用户具有的角色没有分配权限，请与管理员联系！");
			return "logonError";//跳转到登录页面
		}
        /**记住我*/
		LogonUtils.remeberMe(name,password,request,response);

		//将ElecUser对象放置到Session中
		request.getSession().setAttribute("globle_user", elecUser);
		//将Hashtable中存放的角色信息，放置到Session中
		request.getSession().setAttribute("globle_role", ht);
		//将权限的字符串（格式：aa@ab@ac@ad@ae）存放到Session中
		request.getSession().setAttribute("globle_popedom", popedom);
		return "menuHome";
	}
	/**标题*/
	public String title(){
		return "title";
	}

	/**菜单*/
	public String left(){
		return "left";
	}

	/**框架大小改变*/
	public String change(){
		return "change";
	}

	/**
	 * @Name: loading
	 * @Description: 功能页面的显示
	 * @Author: 刘洋（作者）
	 * @Version: V1.00 （版本号）
	 * @Create Date: 2014-11-29 （创建日期）
	 * @Parameters: 无
	 * @Return: String：跳转到menu/loading.jsp
	 */
	public String loading(){
		//查询设备运行情况，放置到浮动框中
		//1：查询数据库运行监控表的数据，返回惟一ElecCommonMsg
		ElecCommonMsg commonMsg = iElecCommonMsgService.findCommonMsg();
		//2：将ElecCommonMsg对象压入栈顶，支持表单回显
		ValueUtils.putValueStack(commonMsg);
		return "loading";
	}

	/**
	 * @Name: logout
	 * @Description: 重新登录
	 * @Author: 刘洋（作者）
	 * @Version: V1.00 （版本号）
	 * @Create Date: 2014-11-29 （创建日期）
	 * @Parameters: 无
	 * @Return: String：重定向到index.jsp
	 */
	public String logout(){
		/**清空Session*/
		//指定Session的名称清空
		//request.getSession().removeAttribute(arg0);
		//清空所有Session
		request.getSession().invalidate();
		return "logout";
	}

	/**
	 * @Name: alermStation
	 * @Description: 显示站点运行情况
	 * @Author: 刘洋（作者）
	 * @Version: V1.00 （版本号）
	 * @Create Date: 2014-11-29 （创建日期）
	 * @Parameters: 无
	 * @Return: String：跳转到menu/alermStation.jsp
	 */
	public String alermStation(){
		//1：查询数据库运行监控表的数据，返回惟一ElecCommonMsg
		ElecCommonMsg commonMsg = iElecCommonMsgService.findCommonMsg();
		//2：将ElecCommonMsg对象压入栈顶，支持表单回显
		ValueUtils.putValueStack(commonMsg);
		return "alermStation";
	}

	/**
	 * @Name: alermDevice
	 * @Description: 显示设备运行情况
	 * @Author: 刘洋（作者）
	 * @Version: V1.00 （版本号）
	 * @Create Date: 2014-11-29 （创建日期）
	 * @Parameters: 无
	 * @Return: String：跳转到menu/alermDevice.jsp
	 */
	public String alermDevice(){
		//1：查询数据库运行监控表的数据，返回惟一ElecCommonMsg
		ElecCommonMsg commonMsg = iElecCommonMsgService.findCommonMsg();
		//2：将ElecCommonMsg对象压入栈顶，支持表单回显
		ValueUtils.putValueStack(commonMsg);
		return "alermDevice";
	}
	/**
	 * @Name: showMenu
	 * @Description: 使用ajax动态加载左侧的树型菜单
	 * @Author: 刘洋（作者）
	 * @Version: V1.00 （版本号）
	 * @Create Date: 2014-12-05 （创建日期）
	 * @Parameters: 无
	 * @Return: String：showMenu，使用struts2提供的json插件包
	 */
	public String showMenu(){
		//获取当前用户的角色
		Hashtable<String, String> ht = (Hashtable<String, String>)request.getSession().getAttribute("globle_role");
		//获取当前用户的对象
		ElecUser elecUser = (ElecUser)request.getSession().getAttribute("globle_user");
		//1:从Session获取当前用户具有的权限的字符串
		String popedom = (String)request.getSession().getAttribute("globle_popedom");
		//2:查询当前用户所对应的功能权限List<ElecPopedom>，将List集合转化成json
		List<ElecPopedom> list = elecRoleService.findPopedomListByString(popedom);

		/**使用角色控制系统的URL*/
		//不是系统管理员
		if(!ht.containsKey("1")){
			if(list!=null && list.size()>0){
				for(ElecPopedom elecPopedom:list){
					String mid = elecPopedom.getMid();
					String pid = elecPopedom.getPid();
					//改变用户管理的URL
					if("an".equals(mid) && "am".equals(pid)) {
						elecPopedom.setUrl("../system/elecUserAction_edit.do?userID="+elecUser.getUserID()+"&roleflag=1");
					}
				}
			}
		}

		//3:将List转化成json，只需要将list集合放置到栈顶
		ValueUtils.putValueStack(list);
		return "showMenu";
	}
	
}
