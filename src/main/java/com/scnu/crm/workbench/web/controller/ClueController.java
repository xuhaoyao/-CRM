package com.scnu.crm.workbench.web.controller;

import com.scnu.crm.settings.domain.User;
import com.scnu.crm.settings.service.UserService;
import com.scnu.crm.settings.service.impl.UserServiceImpl;
import com.scnu.crm.utils.DateTimeUtil;
import com.scnu.crm.utils.PrintJson;
import com.scnu.crm.utils.ServiceFactory;
import com.scnu.crm.utils.UUIDUtil;
import com.scnu.crm.vo.PaginationVo;
import com.scnu.crm.workbench.domain.Activity;
import com.scnu.crm.workbench.domain.Clue;
import com.scnu.crm.workbench.domain.Tran;
import com.scnu.crm.workbench.service.ActivityService;
import com.scnu.crm.workbench.service.ClueService;
import com.scnu.crm.workbench.service.impl.ActivityServiceImpl;
import com.scnu.crm.workbench.service.impl.ClueServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClueController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        if("/workbench/clue/getUserList.do".equals(path)){
            getUserList(request,response);
        }else if("/workbench/clue/save.do".equals(path)){
            save(request,response);
        }else if("/workbench/clue/detail.do".equals(path)){
            detail(request,response);
        }else if("/workbench/clue/pageList.do".equals(path)){
            pageList(request,response);
        }else if("/workbench/clue/getActivityListByClueId.do".equals(path)){
            getActivityListByClueId(request,response);
        }else if("/workbench/clue/Disassociation.do".equals(path)){
            disssociation(request,response);
        }else if("/workbench/clue/getActivityListByName.do".equals(path)){
            getActivityListByName(request, response);
        }else if("/workbench/clue/association.do".equals(path)){
            association(request,response);
        }else if("/workbench/clue/getActivityListByName1.do".equals(path)){
            getActivityListByName1(request,response);
        }else if("/workbench/clue/convert.do".equals(path)){
            convert(request,response);
        }
    }

    private void convert(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String method = request.getMethod();
        String clueId = request.getParameter("clueId");
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession(false).getAttribute("user")).getName();
        Tran t = null;
        if("POST".equals(method)){
            t = new Tran();
            t.setId(UUIDUtil.getUUID());
            t.setMoney(request.getParameter("money"));
            t.setName(request.getParameter("name"));
            t.setExpectedDate(request.getParameter("expectedDate"));
            t.setStage(request.getParameter("stage"));
            t.setActivityId(request.getParameter("activityId"));
            t.setCreateBy(createBy);
            t.setCreateTime(createTime);
        }
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = cs.convert(clueId,t,createBy,createTime);
        if(flag)
            response.sendRedirect(request.getContextPath() + "/workbench/clue/index.jsp");
        else
            System.out.println("出bug");

    }

    private void getActivityListByName1(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> aList = as.getActivityListByName1(name);
        PrintJson.printJsonObj(response,aList);
    }

    private void association(HttpServletRequest request, HttpServletResponse response) {
        String clueId = request.getParameter("clueId");
        String[] activityIds = request.getParameterValues("activityId");
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("clueId",clueId);
        map.put("activityIds",activityIds);
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = cs.association(map);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getActivityListByName(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        String clueId = request.getParameter("clueId");
        Map<String,String> map = new HashMap<String, String>();
        map.put("name",name);
        map.put("clueId",clueId);
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> aList = as.getActivityListByName(map);
        PrintJson.printJsonObj(response,aList);
    }

    private void disssociation(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = cs.disssociation(id);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getActivityListByClueId(HttpServletRequest request, HttpServletResponse response) {
        String clueId = request.getParameter("clueId");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<Activity> aList = as.getActivityListByClueId(clueId);
        PrintJson.printJsonObj(response,aList);
    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {
        String pageNoStr = request.getParameter("pageNo");
        String pageSizeStr = request.getParameter("pageSize");
        String fullname = request.getParameter("fullname");
        String owner  = request.getParameter("owner");
        String company = request.getParameter("company");
        String phone = request.getParameter("phone");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");
        String source = request.getParameter("source");
        int pageNo = Integer.valueOf(pageNoStr);
        int pageSize = Integer.valueOf(pageSizeStr);
        int skipCount = (pageNo - 1) * pageSize;

        Map<String,Object> map = new HashMap<String, Object>();
        map.put("fullname",fullname);
        map.put("owner",owner);
        map.put("company",company);
        map.put("phone",phone);
        map.put("mphone",mphone);
        map.put("state",state);
        map.put("source",source);
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());

        PaginationVo<Clue> vo = cs.pageList(map);

        PrintJson.printJsonObj(response,vo);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String id = request.getParameter("id");
        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        Clue clue = cs.findById(id);
        request.setAttribute("c",clue);
        request.getRequestDispatcher("/workbench/clue/detail.jsp").forward(request,response);
    }

    private void save(HttpServletRequest request, HttpServletResponse response) {
        String id = UUIDUtil.getUUID();
        String fullname = request.getParameter("fullname");
        String appellation = request.getParameter("appellation");
        String owner = request.getParameter("owner");
        String company = request.getParameter("company");
        String job = request.getParameter("job");
        String email = request.getParameter("email");
        String phone = request.getParameter("phone");
        String website = request.getParameter("website");
        String mphone = request.getParameter("mphone");
        String state = request.getParameter("state");
        String source = request.getParameter("source");
        String createBy = ((User)request.getSession(false).getAttribute("user")).getName();
        String createTime = DateTimeUtil.getSysTime();
        String description = request.getParameter("description");
        String contactSummary = request.getParameter("contactSummary");
        String nextContactTime = request.getParameter("nextContactTime");
        String address = request.getParameter("address");

        Clue clue = new Clue();
        clue.setAddress(address);
        clue.setAppellation(appellation);
        clue.setCompany(company);
        clue.setDescription(description);
        clue.setId(id);
        clue.setContactSummary(contactSummary);
        clue.setWebsite(website);
        clue.setOwner(owner);
        clue.setJob(job);
        clue.setEmail(email);
        clue.setPhone(phone);
        clue.setMphone(mphone);
        clue.setState(state);
        clue.setSource(source);
        clue.setCreateBy(createBy);
        clue.setCreateTime(createTime);
        clue.setNextContactTime(nextContactTime);
        clue.setFullname(fullname);

        ClueService cs = (ClueService) ServiceFactory.getService(new ClueServiceImpl());
        boolean flag = cs.save(clue);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> users = us.getUserList();
        PrintJson.printJsonObj(response,users);
    }
}
