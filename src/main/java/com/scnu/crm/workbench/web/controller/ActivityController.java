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
import com.scnu.crm.workbench.domain.ActivityRemark;
import com.scnu.crm.workbench.service.ActivityService;
import com.scnu.crm.workbench.service.impl.ActivityServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityController extends HttpServlet {
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getServletPath();
        if("/workbench/activity/getUserList.do".equals(path)){
            getUserList(request,response);
        }else if("/workbench/activity/save.do".equals(path)){
            save(request,response);
        }else if("/workbench/activity/pageList.do".equals(path)) {
            pageList(request, response);
        }else if("/workbench/activity/delete.do".equals(path)){
            delete(request,response);
        }else if("/workbench/activity/getUserListAndActivity.do".equals(path)){
            getUserListAndActivity(request,response);
        }else if("/workbench/activity/update.do".equals(path)){
            update(request,response);
        }else if("/workbench/activity/detail.do".equals(path)){
            detail(request,response);
        }else if("/workbench/activity/getRemarkList.do".equals(path)){
            getRemarkList(request,response);
        }else if("/workbench/activity/deleteRemark.do".equals(path)){
            deleteRemark(request,response);
        }else if("/workbench/activity/saveRemark.do".equals(path)){
            saveRemark(request,response);
        }else if("/workbench/activity/updateRemark.do".equals(path)){
            updateRemark(request,response);
        }else if("/workbench/activity/updateActivity.do".equals(path)){
            updateActivity(request,response);
        }
    }

    private void updateActivity(HttpServletRequest request, HttpServletResponse response) {
        Activity activity = new Activity();
        activity.setOwner(request.getParameter("owner"));
        activity.setName(request.getParameter("name"));
        activity.setStartDate(request.getParameter("startDate"));
        activity.setEndDate(request.getParameter("endDate"));
        activity.setCost(request.getParameter("cost"));
        activity.setDescription(request.getParameter("description"));
        activity.setId(request.getParameter("id"));
        String editTime = DateTimeUtil.getSysTime();
        activity.setEditTime(editTime);
        String editBy = ((User)request.getSession(false).getAttribute("user")).getName();
        activity.setEditBy(editBy);
        ActivityService as = (ActivityService)ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.update(activity);
        if(flag){
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("success",flag);
            map.put("a",activity);
            PrintJson.printJsonObj(response,map);
        }
        else
            PrintJson.printJsonObj(response,flag);

    }

    private void updateRemark(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        String noteContent = request.getParameter("noteContent");
        String editBy = ((User)request.getSession(false).getAttribute("user")).getName();
        String editTime = DateTimeUtil.getSysTime();
        String editFlag = "1";
        ActivityRemark ar = new ActivityRemark();
        ar.setId(id);
        ar.setNoteContent(noteContent);
        ar.setEditBy(editBy);
        ar.setEditTime(editTime);
        ar.setEditFlag(editFlag);
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.updateRemark(ar);
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("success",flag);
        map.put("ar",ar);
        PrintJson.printJsonObj(response,map);
    }

    private void saveRemark(HttpServletRequest request, HttpServletResponse response) {
        String noteContent = request.getParameter("noteContent");
        String activityId = request.getParameter("activityId");
        String id = UUIDUtil.getUUID();
        String createTime = DateTimeUtil.getSysTime();
        String createBy = ((User)request.getSession(false).getAttribute("user")).getName();
        String editFlag = "0";

        ActivityRemark ar = new ActivityRemark();
        ar.setId(id);
        ar.setNoteContent(noteContent);
        ar.setCreateTime(createTime);
        ar.setCreateBy(createBy);
        ar.setEditFlag(editFlag);
        ar.setActivityId(activityId);
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.saveRemark(ar);
        if(flag){
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("success",flag);
            map.put("ar",ar);
            PrintJson.printJsonObj(response,map);
        }
        else
            PrintJson.printJsonFlag(response,false);
    }

    private void deleteRemark(HttpServletRequest request, HttpServletResponse response) {
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.deleteRemark(request.getParameter("id"));
        PrintJson.printJsonFlag(response,flag);
    }

    private void getRemarkList(HttpServletRequest request, HttpServletResponse response) {
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        List<ActivityRemark> ActivityRemarks = as.getRemarkList(request.getParameter("activityId"));
        PrintJson.printJsonObj(response,ActivityRemarks);
    }

    private void detail(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        Activity a = as.detail(request.getParameter("id"));
        request.setAttribute("a",a);
        request.setAttribute("pageNo",request.getParameter("pageNo"));
        request.setAttribute("pageSize",request.getParameter("pageSize"));
        request.setAttribute("name",request.getParameter("name"));
        request.setAttribute("owner",request.getParameter("owner"));
        request.setAttribute("startDate",request.getParameter("startDate"));
        request.setAttribute("endDate",request.getParameter("endDate"));
        request.getRequestDispatcher("/workbench/activity/detail.jsp").forward(request,response);
    }

    private void update(HttpServletRequest request, HttpServletResponse response) {
        Activity activity = new Activity();
        activity.setOwner(request.getParameter("owner"));
        activity.setName(request.getParameter("name"));
        activity.setStartDate(request.getParameter("startDate"));
        activity.setEndDate(request.getParameter("endDate"));
        activity.setCost(request.getParameter("cost"));
        activity.setDescription(request.getParameter("description"));
        activity.setId(request.getParameter("id"));
        String currentDate = DateTimeUtil.getSysTime();
        activity.setEditTime(currentDate);
        String editBy = ((User)request.getSession(false).getAttribute("user")).getName();
        activity.setEditBy(editBy);
        ActivityService as = (ActivityService)ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.update(activity);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getUserListAndActivity(HttpServletRequest request, HttpServletResponse response) {
        String id = request.getParameter("id");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        Map<String,Object> map = as.getUserListAndActivity(id);
        PrintJson.printJsonObj(response,map);
    }

    private void delete(HttpServletRequest request, HttpServletResponse response) {
        String[] ids = request.getParameterValues("id");
        ActivityService as = (ActivityService) ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.delete(ids);
        PrintJson.printJsonFlag(response,flag);
    }

    private void pageList(HttpServletRequest request, HttpServletResponse response) {
        String name = request.getParameter("name");
        String owner = request.getParameter("owner");
        String startDate = request.getParameter("startDate");
        String endDate = request.getParameter("endDate");
        String pageNoStr = request.getParameter("pageNo");
        String pageSizeStr = request.getParameter("pageSize");
        int pageNo = Integer.valueOf(pageNoStr);
        int pageSize = Integer.valueOf(pageSizeStr);
        int skipCount = (pageNo - 1) * pageSize;
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("name",name);
        map.put("owner",owner);
        map.put("startDate",startDate);
        map.put("endDate",endDate);
        map.put("skipCount",skipCount);
        map.put("pageSize",pageSize);

        ActivityService as = (ActivityService)ServiceFactory.getService(new ActivityServiceImpl());
        /*
            前端要: 市场活动信息列表
                    查询的总条数

            这里用vo(泛型) 以后查询的东西通用。
                PaginationVo<T>
                    private int total;
                    private List<T> dataList;
            分页查询每个模块都有,所以采用一个通用vo
         */
        PaginationVo<Activity> vo = as.pageList(map);

        PrintJson.printJsonObj(response,vo);

    }

    private void save(HttpServletRequest request, HttpServletResponse response) {
        Activity activity = new Activity();
        activity.setOwner(request.getParameter("owner"));
        activity.setName(request.getParameter("name"));
        activity.setStartDate(request.getParameter("startDate"));
        activity.setEndDate(request.getParameter("endDate"));
        activity.setCost(request.getParameter("cost"));
        activity.setDescription(request.getParameter("description"));
        activity.setId(UUIDUtil.getUUID());
        String currentDate = DateTimeUtil.getSysTime();
        activity.setCreateTime(currentDate);
        String createBy = ((User)request.getSession(false).getAttribute("user")).getName();
        activity.setCreateBy(createBy);
        ActivityService as = (ActivityService)ServiceFactory.getService(new ActivityServiceImpl());
        boolean flag = as.save(activity);
        PrintJson.printJsonFlag(response,flag);
    }

    private void getUserList(HttpServletRequest request, HttpServletResponse response) {
        UserService us = (UserService) ServiceFactory.getService(new UserServiceImpl());
        List<User> users = us.getUserList();
        PrintJson.printJsonObj(response,users);
    }
}
