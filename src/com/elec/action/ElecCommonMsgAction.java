package com.elec.action;

import com.elec.entity.ElecCommonMsg;
import com.elec.service.IElecCommonMsgService;
import com.elec.utils.ValueUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by Administrator on 2017-10-30.
 */
@Controller("elecCommonMsgAction")
@Scope("prototype")
public class ElecCommonMsgAction extends BaseAction<ElecCommonMsg>{
    ElecCommonMsg elecCommonMsg = this.getModel();
    /**注入service*/
    @Resource(name = IElecCommonMsgService.SERVICE_NAME)
    private  IElecCommonMsgService iElecCommonMsgService;

    /**
     * 运行监控的首页显示
     * @return
     */
    public String home(){
        //1.将查询数库运行监控表的数据，返回唯一ElecCommonMsg
        ElecCommonMsg commonMsg = iElecCommonMsgService.findCommonMsg();
        //2.将ElecCommonMsg对象压入栈顶，支持表单回显
        // ActionContext.getContext().put("commonMsg",commonMsg);
        // ServletActionContext.getContext().getValueStack().push(commonMsg);
        ValueUtils.putValueStack(commonMsg);
        return "home";
    }
    /**
     * 保存运行监控数据
     * @return
     */
    public String save(){
        //模拟保存150次，方便计算百分比
        for(int i=1;i<=150;i++){
            iElecCommonMsgService.saveCommonMsg(this.elecCommonMsg);
            request.getSession().setAttribute("percent", (double)i/150*100);//存放计算的百分比
        }
        //线程结束时，清空当前session
        request.getSession().removeAttribute("percent");
        return "save";
    }

    /**
     * 在页面执ajax计算执行的百分比情况，将结果显示情况，将结果显示在页面上
     * @return
     */
    /**
     *结论：百分比进度条，实质上是用ajax在保存过程中开启多个线程实现的
     *      其中一个线程执行保存的操作
     *         *将百分比计算出来放置到session中
     *         *在线程结束的时候，将session清空
     *      另一个线程，从session中获取百分比的内容
     *          *使用ajax将结束的结果返回到页面上，并显示在页面上
     *
     */
    public String progressBar() throws IOException {
        //从session中获取操作方法中计算的百分比
        Double percent = (Double) request.getSession().getAttribute("percent");
        String res = "";
        //此时说明操作的业务方法仍然继续在执行
        if(percent!=null){
            //计算的小数，四舍五入取整
            int percentInt = (int) Math.rint(percent);
            res = "<percent>" + percentInt + "</percent>";
        }
        //此时说明操作的业务方法已经执行完毕，session中的值已经被清空
        else{
            //存放百分比
            res = "<percent>" + 100 + "</percent>";
        }
        //定义ajax的返回结果是XML的形式
        PrintWriter out = response.getWriter();
        response.setContentType("text/xml");
        response.setHeader("Cache-Control", "no-cache");
        //存放结果数据，例如：<response><percent>88</percent></response>
        out.println("<response>");
        out.println(res);
        out.println("</response>");
        out.close();
        return null;//如果不指定页面则返回null或NONE
    }

}
