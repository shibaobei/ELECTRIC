package com.elec.service.impl;

import java.util.*;
import java.util.Map.Entry;

import javax.annotation.Resource;

import com.elec.dao.IElecPopedomDao;
import com.elec.dao.IElecRoleDao;
import com.elec.dao.IElecRolePopedomDao;
import com.elec.dao.IElecUserDao;
import com.elec.entity.ElecPopedom;
import com.elec.entity.ElecRole;
import com.elec.entity.ElecRolePopedom;
import com.elec.entity.ElecUser;
import com.elec.service.IElecRoleService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;



@Service(IElecRoleService.SERVICE_NAME)
@Transactional(readOnly=true)
public class ElecRoleServiceImpl implements IElecRoleService {

	/**用户表Dao*/
	@Resource(name= IElecUserDao.SERVICE_NAME)
	IElecUserDao elecUserDao;
	
	/**角色表Dao*/
	@Resource(name= IElecRoleDao.SERVICE_NAME)
	IElecRoleDao elecRoleDao;
	
	/**权限表Dao*/
	@Resource(name= IElecPopedomDao.SERVICE_NAME)
	IElecPopedomDao elecPopedomDao;
	
	
	/**角色权限表Dao*/
	@Resource(name= IElecRolePopedomDao.SERVICE_NAME)
	IElecRolePopedomDao elecRolePopedomDao;
	

	/**  
	* @Name: findAllRoleList
	* @Description: 查询系统中所有的角色
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-12-03（创建日期）
	* @Parameters: 无
	* @Return: List<ElecRole>：角色集合
	*/
	public List<ElecRole> findAllRoleList() {
		Map<String, String> orderby = new LinkedHashMap<String, String>();
		orderby.put("o.roleID", "asc");
		List<ElecRole> list = elecRoleDao.findCollectionByConditionNoPage("", null, orderby);
		return list;
	}
	
	/**  
	* @Name: findAllPopedomList
	* @Description: 查询系统中所有的权限（满足父中包含子的集合）
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-12-03（创建日期）
	* @Parameters: 无
	* @Return: List<ElecPopedom>：权限集合
	*/
	public List<ElecPopedom> findAllPopedomList() {
		//查询权限的集合，存放所有的tr，也就是pid=0的集合，父集合（SELECT * FROM elec_popedom o WHERE 1=1 AND o.pid='0'）
		//组织查询条件
		String condition = " and o.pid=?";
		Object [] params = {"0"};
		Map<String, String> orderby = new LinkedHashMap<String, String>();
		orderby.put("o.mid", "asc");
		List<ElecPopedom> list = elecPopedomDao.findCollectionByConditionNoPage(condition, params, orderby);
		if(list!=null && list.size()>0){
			for(ElecPopedom elecPopedom:list){
				//获取权限id的值，这个值是下一个pid所对应的值（父子关系）
				String mid = elecPopedom.getMid();
				String condition1 = " and o.pid=?";
				Object [] params1 = {mid};
				Map<String, String> orderby1 = new LinkedHashMap<String, String>();
				orderby.put("o.mid", "asc");
				List<ElecPopedom> list1 = elecPopedomDao.findCollectionByConditionNoPage(condition1, params1, orderby1);
				//将所有子的集合，添加到父中的集合属性
				elecPopedom.setList(list1);
			}
		}
		return list;
	}
	
	/**  
	* @Name: findAllPopedomListByRoleID
	* @Description: 使用当前角色ID，查询系统中所有的权限，并显示（匹配）
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-12-03（创建日期）
	* @Parameters: String：角色ID
	* @Return: List<ElecPopedom>：权限集合
	*/
	public List<ElecPopedom> findAllPopedomListByRoleID(String roleID) {
		//1：查询系统中所有的权限，父权限用于遍历tr，父权限对应的子集合，用来遍历td
		List<ElecPopedom> list = this.findAllPopedomList();
		//2：使用角色ID，组织查询条件，查询角色权限关联表，返回List<ElecRolePopedom>
		String condition = " and o.roleID=?";
		Object [] params = {roleID};
		List<ElecRolePopedom> popedomList = elecRolePopedomDao.findCollectionByConditionNoPage(condition, params, null);
		/**
		 * 3：匹配，向ElecPopedom对象中设置flag属性值的操作
		    * 如果匹配成功，设置1
		    * 如果匹配不成功，设置2
		    使用包含的技术来匹配（List集合、String，contains()）
		 */
		//定义一个权限的字符串
		StringBuffer popedomBuffer = new StringBuffer("");
		//遍历popedomList，将每个mid的值获取，组织成一个字符串
		if(popedomList!=null && popedomList.size()>0){
			for(ElecRolePopedom elecRolePopedom:popedomList){
				//获取mid的值
				String mid = elecRolePopedom.getMid();
				//组织成一个权限的字符串（格式aa@ab@ac@ad)
				popedomBuffer.append(mid).append("@");
			}
			//去掉最后一个@
			popedomBuffer.deleteCharAt(popedomBuffer.length()-1);
		}
		//存放权限，该权限就是当前角色具有的权限（格式aa@ab@ac@ad)
		String popedom = popedomBuffer.toString();
		//定义一个方法，完成匹配，迭代
		this.findPopedomResult(popedom,list);
		return list;
	}

	//定义一个方法，完成匹配，迭代
	private void findPopedomResult(String popedom, List<ElecPopedom> list) {
		//遍历所有的权限，权限中只定义tr的List
		if(list!=null && list.size()>0){
			for(ElecPopedom elecPopedom :list){
				//获取每个权限的mid
				String mid = elecPopedom.getMid();
				//匹配成功
				if(popedom.contains(mid)){
					elecPopedom.setFlag("1");
				}
				else{
					elecPopedom.setFlag("2");
				}
				//获取父对应的子的集合
				List<ElecPopedom> childList = elecPopedom.getList();
				if(childList!=null && childList.size()>0){
					this.findPopedomResult(popedom,childList);
				}
			}
		}
	}
	
	/**  
	* @Name: findAllUserListByRoleID
	* @Description: 使用当前角色ID，查询系统中所有的用户，并显示（匹配）
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-12-03（创建日期）
	* @Parameters: String：角色ID
	* @Return: List<ElecUser>：用户集合
	*/
	public List<ElecUser> findAllUserListByRoleID(String roleID) {
		//1：查询系统中所有的用户List<ElecUser>
		Map<String, String> orderby = new LinkedHashMap<String, String>();
		orderby.put("o.onDutyDate", "asc");
		List<ElecUser> list = elecUserDao.findCollectionByConditionNoPage("", null, orderby);
		// 2：使用角色ID，查询角色对象，返回ElecRole对象，通过Set集合获取当用户具有的角色
		ElecRole elecRole = elecRoleDao.findObjectByID(roleID);
		Set<ElecUser> elecUsers = elecRole.getElecUsers();
		
		/**方案一*/
//		//遍历elecUsers，组织List<String>，存放ID
//		List<String> idList = new ArrayList<String>();
//		if(elecUsers!=null && elecUsers.size()>0){
//			for(ElecUser elecUser:elecUsers){
//				//用户ID
//				String userID = elecUser.getUserID();
//				idList.add(userID);
//			}
//		}
//		/** 3：匹配，向ElecUser对象中设置flag属性值的操作
//		    * 如果匹配成功，设置1
//		    * 如果匹配不成功，设置2
//		    */
//		//遍历所有的用户集合
//		if(list!=null && list.size()>0){
//			for(ElecUser elecUser:list){
//				//获取用户ID
//				String userID = elecUser.getUserID();
//				//匹配成功
//				if(idList.contains(userID)){
//					elecUser.setFlag("1");
//				}
//				else{
//					elecUser.setFlag("2");
//				}
//			}
//		}
		/**方案二*/
		if(list!=null && list.size()>0){
			for(ElecUser elecUser:list){
				//获取用户ID
				String userID = elecUser.getUserID();
				if(elecUsers!=null && elecUsers.size()>0){
					for(ElecUser elecUser2:elecUsers){
						String userID2 = elecUser2.getUserID();
						//匹配成功
						if(userID.equals(userID2)){
							elecUser.setFlag("1");
							break;
						}
						//匹配不成功
						else{
							elecUser.setFlag("2");
						}
					}
				}
			}
		}
		return list;
	}
	
	/**  
	* @Name: saveRole
	* @Description: 保存用户角色关联表，保存角色权限关联表
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-12-03（创建日期）
	* @Parameters: ElecPopedom：VO对象
	* @Return: 无
	*/
	@Transactional(isolation=Isolation.DEFAULT,propagation=Propagation.REQUIRED,readOnly=false)
	public void saveRole(ElecPopedom elecPopedom) {
		//获取到角色ID
		String roleID = elecPopedom.getRoleID();
		//获取到权限的主键数组（格式pid_mid，例如0_aa）
		String [] selectopers = elecPopedom.getSelectoper();
		//获取到用户ID数组
		String [] selectusers = elecPopedom.getSelectuser();
		/**一：操作角色权限关联表*/
		this.saveRolePopedom(roleID,selectopers);
		/**二：操作用户角色关联表*/
		this.saveUserRole(roleID,selectusers);
		
	}

	/**二：操作用户角色关联表*/
	private void saveUserRole(String roleID, String[] selectusers) {
		//1：使用角色ID，查询角色对象ElecRole，获取到该角色对应的用户的Set集合
		ElecRole elecRole = elecRoleDao.findObjectByID(roleID);
		/**方案一*/
//		Set<ElecUser> elecUsers = elecRole.getElecUsers();
//		//2：解除Set集合的关联关系（删除中间表）
//		elecUsers.clear();
//		//3：遍历用户ID的数组，重新建立Set集合的关联关系
//		if(selectusers!=null && selectusers.length>0){
//			for(String userID:selectusers){
//				ElecUser elecUser = new ElecUser();
//				elecUser.setUserID(userID);//建立关联关系
//				elecUsers.add(elecUser);
//			}
//		}
//		/**快照*/
		/**方案二*/
		Set<ElecUser> elecUsers = new HashSet<ElecUser>();
		if(selectusers!=null && selectusers.length>0){
			for(String userID:selectusers){
				ElecUser elecUser = new ElecUser();
				elecUser.setUserID(userID);//建立关联关系
				elecUsers.add(elecUser);
			}
		}
		elecRole.setElecUsers(elecUsers);
	}

	/**一：操作角色权限关联表*/
	private void saveRolePopedom(String roleID, String[] selectopers) {
		//1：使用角色ID，组织查询条件，查询角色权限关联表，返回List<ElecRolePopedom>
		String condition = " and o.roleID=?";
		Object [] params = {roleID};
		List<ElecRolePopedom> list = elecRolePopedomDao.findCollectionByConditionNoPage(condition, params, null);
		//2：删除之前的数据List
		elecRolePopedomDao.deleteObjectByCollection(list);
		//3：遍历权限主键ID的数组，组织PO对象，执行保存
		if(selectopers!=null && selectopers.length>0){
			for(String ids:selectopers){
				//ids（格式pid_mid，例如0_aa）
				String [] arrays = ids.split("_");
				//组织PO对象，执行保存
				ElecRolePopedom elecRolePopedom = new ElecRolePopedom();
				elecRolePopedom.setRoleID(roleID);
				elecRolePopedom.setMid(arrays[1]);
				elecRolePopedom.setPid(arrays[0]);
				elecRolePopedomDao.save(elecRolePopedom);
			}
		}
	}

	/**  
	* @Name: findPopedomByRoleIDs
	* @Description: 使用角色ID的Hashtable的集合，获取角色对应的权限并集
	* @Author: 刘洋（作者）
	* @Version: V1.00 （版本号）
	* @Create Date: 2014-12-03（创建日期）
	* @Parameters: Hashtable：角色ID的集合
	* @Return: String：表示权限的字符串：
	* 存放的权限的mid（字符串的格式：aa@ab@ac@ad@ae）
	* 
	* 使用hql或者是sql语句：
	* SELECT DISTINCT o.mid FROM elec_role_popedom o WHERE 1=1 AND o.roleID IN ('1','2');
	*/
	public String findPopedomByRoleIDs(Hashtable<String, String> ht) {
		//组织查询条件
		StringBuffer buffercondition = new StringBuffer("");
		//遍历Hashtable
		if(ht!=null && ht.size()>0){
			for(Iterator<Entry<String, String>> ite = ht.entrySet().iterator();ite.hasNext();){
				Entry<String, String> entry = ite.next();
				buffercondition.append("'").append(entry.getKey()).append("'").append(",");
			}
			//删除最后一个逗号
			buffercondition.deleteCharAt(buffercondition.length()-1);
		}
		//查询条件
		String condition = buffercondition.toString();
		//组织查询
		List<Object> list = elecRolePopedomDao.findPopedomByRoleIDs(condition);
		//组织权限封装的字符串：（字符串的格式：aa@ab@ac@ad@ae）
		StringBuffer buffer = new StringBuffer("");
		if(list!=null && list.size()>0){
			for(Object o:list){
				buffer.append(o.toString()).append("@");
			}
			//删除最后一个@
			buffer.deleteCharAt(buffer.length()-1);
		}
		return buffer.toString();
	}
	/**
	 * @Name: findPopedomListByString
	 * @Description: 使用权限的字符串，查询当前权限（当前用户）对应的权限集合
	 * @Author: 刘洋（作者）
	 * @Version: V1.00 （版本号）
	 * @Create Date: 2014-12-05（创建日期）
	 * @Parameters: String：表示权限的字符串：（字符串的格式：aa@ab@ac@ad@ae）
	 * @Return: List<ElecPopedom>：权限的集合
	 *  SELECT * FROM elec_popedom o WHERE 1=1 AND  o.ismenu = TRUE AND o.MID IN ('aa','ab','ac','ad','ae')
	 */
	public List<ElecPopedom> findPopedomListByString(String popedom) {
		String condition = " and o.isMenu=? and o.mid in('"+popedom.replace("@", "','")+"')";
		Object [] params = {true};
		Map<String, String> orderby = new LinkedHashMap<String, String>();
		orderby.put("o.mid", "asc");
		List<ElecPopedom> list = null;
		try {
			list = elecPopedomDao.findCollectionByConditionNoPage(condition, params, orderby);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	/**
	 * @Name: findRolePopedomByID
	 * @Description: 使用角色ID，权限的code，父级权限的code查询角色权限关联表，判断当前操作是否可以访问Action上的方法
	 * @Author: 刘洋（作者）
	 * @Version: V1.00 （版本号）
	 * @Create Date: 2014-12-05（创建日期）
	 * @Parameters: String roleID：角色ID
	 * 			 , String mid：权限code
	 * 			 , String pid：父级权限的code
	 * @Return: boolean
	 * 			true：可以访问
	 * 			false：没有权限，此时拒绝访问
	 */
	public boolean findRolePopedomByID(String roleID, String mid, String pid) {
		//组织查询条件
		String condition = "";
		List<Object> paramsList = new ArrayList<Object>();
		//角色ID
		if(StringUtils.isNotBlank(roleID)){
			condition += " and o.roleID = ?";
			paramsList.add(roleID);
		}
		//子权限名称
		if(StringUtils.isNotBlank(mid)){
			condition += " and o.mid = ?";
			paramsList.add(mid);
		}
		//父权限名称
		if(StringUtils.isNotBlank(pid)){
			condition += " and o.pid = ?";
			paramsList.add(pid);
		}
		Object [] params = paramsList.toArray();
		//查询对应的角色权限信息
		List<ElecRolePopedom> list = elecRolePopedomDao.findCollectionByConditionNoPage(condition, params, null);
		boolean flag = false;
		if(list!=null && list.size()>0){
			flag = true;
		}
		return flag;

	}
}
