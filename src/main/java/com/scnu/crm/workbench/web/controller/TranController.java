package com.scnu.crm.workbench.web.controller;

import com.scnu.crm.settings.domain.User;
import com.scnu.crm.settings.service.UserService;
import com.scnu.crm.settings.service.impl.UserServiceImpl;
import com.scnu.crm.utils.DateTimeUtil;
import com.scnu.crm.utils.PrintJson;
import com.scnu.crm.utils.ServiceFactory;
import com.scnu.crm.utils.UUIDUtil;
import com.scnu.crm.workbench.domain.Activity;
import com.scnu.crm.workbench.domain.Contacts;
import com.scnu.crm.workbench.domain.Tran;
import com.scnu.crm.workbench.domain.TranHistory;
import com.scnu.crm.workbench.service.ActivityService;
import com.scnu.crm.workbench.service.ContactsService;
import com.scnu.crm.workbench.service.CustomerService;
import com.scnu.crm.workbench.service.TranService;
import com.scnu.crm.workbench.service.impl.ActivityServiceImpl;
import com.scnu.crm.workbench.service.impl.ContactsServiceImpl;
import com.scnu.crm.workbench.service.impl.CustomerServiceImpl;
import com.scnu.crm.workbench.service.impl.TranServiceImpl;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TranController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        if("/workbench/tran/getUserList.do".equals(path)){
            getUserList(request,response);
        }else if("/workbench/tran/getCustomerName.do".equals(path)){
            getCustomerName(request,response);
        }else if("/workbench/tran/getActivityListByName1.do".equals(path)){
            getActivityListByName1(request,response);
        }else if("/workbench/tran/getContactsListByName.do".equals(path)){
            getContactsListByName(request,response);
        }else if("/workbench/tran/save.do".equals(path)){
            save(request,response);
        }else if("/workbench/tran/detail.do".equals(path)){
            detail(request,response);
        }else if("/workbench/tran/getHistoryList.do".equals(path)){
            getHistoryList(request,response);
        }else if("/workbench/tran/changeStage.do".equals(path)){
            changeStage(request,response);
        }
    }

    private void changeStage(HttpServletRequest request, HttpServletResponse response) {
        String tranId = request.getParameter("tranId");
        String stage = request.getParameter("stage");
        String money = request.getParameter("money");
        String expectedDate = request.getParameter("expectedDate");
        String editTime = DateTimeUtil.getSysTime();
        String editBy = ((User)request.getSession(false).getAttribute("user")).getName();
        Tran t = new Tran();
        t.setId(tranId);
        t.setStage(stage);
        t.setMoney(money);
        t.setExpectedDate(expectedDate);
        t.setEditBy(editBy);
        t.setEditTime(editTime);
        Map<String,String> pMap = (Map<String, String>) this.getServletContext().getAttribute("pMap");
        String possibility = pMap.get(stage);
        t.setPossibility(possibility);
        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean flag = ts.changeStage(t);
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("success",flag);
        map.put("t",t);
        PrintJson.printJsonObj(response,map);
    }

    private void getHistoryList(HttpServletRequest request, HttpServletResponse response) {
        String tranId = request.getParameter("tranId");
        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        List<TranHistory> tranHistories = ts.getHistoryList(tranId);
        Map<String,String> pMap = (Map<String, String>) this.getServletContext().getAttribute("pMap");
        for (TranHistory tranHistory : tranHistories) {
            String stage = tranHistory.getStage();
            String possibility = pMap.get(stage);
            tranHistory.setPossibility(possibility);
        }
        PrintJson.printJsonObj(response,tranHistories);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        Tran t = ts.detail(id);
        Map<String,String> pMap = (Map<String, String>) this.getServletContext().getAttribute("pMap");
        String possibility = pMap.get(t.getStage());
        t.setPossibility(possibility);
        request.setAttribute("t",t);
        request.getRequestDispatcher("/workbench/transaction/detail.jsp").forward(request, response);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = UUIDUtil.getUUID();
        String owner = request.getParameter("owner");
        String money = request.getParameter("money");
        String name = request.getParameter("name");
        String expectedDate = request.getParameter("expectedDate");
        String customerName = request.getParameter("customerName");
        String stage = request.getParameter("stage");
        String type = request.getParameter("type");
        String source = request.getParameter("source");
        String activityId = request.getParameter("activityId");
        String contactsId = request.getParameter("contactsId");
        String createBy = ((User)request.getSession(false).getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");

        Tran t = new Tran();
        t.setContactSummary(contactSummary);
        t.setDescription(description);
        t.setSource(source);
        t.setOwner(owner);
        t.setId(id);
        t.setMoney(money);
        t.setName(name);
        t.setContactsId(contactsId);
        t.setNextContactTime(nextContactTime);
        t.setCreateTime(createTime);
        t.setCreateBy(createBy);
        t.setActivityId(activityId);
        t.setType(type);
        t.setStage(stage);
        t.setExpectedDate(expectedDate);

        TranService ts = (TranService) ServiceFactory.getService(new TranServiceImpl());
        boolean flag = ts.save(t,customerName);
        if(flag)
            response.sendRedirect(request.getContextPath() + "/workbench/transaction/index.jsp");
        else
            System.out.println("出bug");
    }

    private void getContactsListByName(HttpServletRequest request, HttpServletResponse response) {
        String fullname = request.getParameter("fullname");
        ContactsService cs = (ContactsService) ServiceFactory.getService(new ContactsServiceImpl());
        List<Contacts> cList = cs.getContactsListByName(fullname);
        PrintJson.printJsonObj(response,cList);
    }

    private void getActivityListByName1(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> aList = as.getActivityListByName1(name);
        PrintJson.printJsonObj(response,aList);
    }

    private void getCustomerName(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        CustomerService cs = (CustomerService) ServiceFactory.getService(new CustomerServiceImpl());
        List<String> customerNames = cs.getCustomerName(name);
        PrintJson.printJsonObj(response,customerNames);
    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> users = us.getUserList();
        request.setAttribute("users",users);
        request.getRequestDispatcher("/workbench/transaction/save.jsp").forward(request,response);
    }
}
