package com.elec.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;

public class LogonUtils {

	/**验证码*/
	public static boolean checkNumber(HttpServletRequest request) {
		//获取页面的验证码
		String imageNumber = request.getParameter("checkNumber");
		if(StringUtils.isBlank(imageNumber)){
			return false;
		}
		//从Session中获取生成的验证码
		String CHECK_NUMBER_KEY = (String) request.getSession().getAttribute("CHECK_NUMBER_KEY");
		if(StringUtils.isBlank(CHECK_NUMBER_KEY)){
			return false;
		}
		return imageNumber.equalsIgnoreCase(CHECK_NUMBER_KEY);
	}
	/**
	 * Cookie技术
	 *    *将用户名和密码存放在Cookie中，获取Cookie中获取
	 *    *注意问题
	 *        有效时间
	 *        有效路径
	 */
	/**记住我*/
	public static void remeberMe(String name, String password,
			HttpServletRequest request, HttpServletResponse response) {
		//1：创建2个Cookie，分别存放用户名和密码
		//Cookie中不能存放中文
		try {
			name = URLEncoder.encode(name, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Cookie nameCookie = new Cookie("name",name);
		Cookie passwordCookie = new Cookie("password",password);
		//2：判断页面中的复选框是否被选中，选中设置，不选中就不设置
		String remeberMe = request.getParameter("remeberMe");
		//设置有效路径
		nameCookie.setPath(request.getContextPath()+"/");
		passwordCookie.setPath(request.getContextPath()+"/");
		//选中
		if(remeberMe!=null && remeberMe.equals("yes")){
			//设置有效时间
			nameCookie.setMaxAge(7*24*60*60);//1周
			passwordCookie.setMaxAge(7*24*60*60);//1周
		}
		else{
			//清空
			nameCookie.setMaxAge(0);//
			passwordCookie.setMaxAge(0);//
		}
		//3：将Cookie存放到response对象
		response.addCookie(nameCookie);
		response.addCookie(passwordCookie);
	}

}
