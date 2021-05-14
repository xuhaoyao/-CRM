package com.scnu.crm.settings.web.controller;

import com.scnu.crm.settings.domain.User;
import com.scnu.crm.settings.service.UserService;
import com.scnu.crm.settings.service.impl.UserServiceImpl;
import com.scnu.crm.utils.MD5Util;
import com.scnu.crm.utils.PrintJson;
import com.scnu.crm.utils.ServiceFactory;

import javax.jws.soap.SOAPBinding;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        if("/settings/user/login.do".equals(path)){
            login(request,response);
        }else if("".equals(path)){
            ;
        }
    }

    private void login(HttpServletRequest request, HttpServletResponse response) {
        String loginAct = request.getParameter("loginAct");
        String loginPwd = request.getParameter("loginPwd");
        //将密码的明文形式转换为MD5的密文形式
        loginPwd = MD5Util.getMD5(loginPwd);
        //接受浏览器的ip地址
        String ip = request.getRemoteAddr();
        System.out.println(ip);
        try{
            //业务层开发,统一使用代理类形态的接口对象
            UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());

            /*
                注意此处: us.login 调用的是代理类的方法
                        因此要在代理类中,将在代理类中捕获的异常,抛出来
                        此处的异常才能被捕获 否则异常在代理类中就被截胡了。
                        因此 在代理类的catch块中 写上 throw e.getCause();
             */
            User user = us.login(loginAct,loginPwd,ip);


            request.getSession().setAttribute("user",user);
            PrintJson.printJsonFlag(response,true);
        }catch(Exception e){
            /*
                需要为ajax请求提供多项信息
                两种手段来提供:
                    map: 只有在这个需求才使用
                    vo : 对于展现的信息将来还会大量使用
                那么这里用map
             */
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("success",false);
            map.put("msg",e.getMessage());
            PrintJson.printJsonObj(response,map);
        }
    }
}
