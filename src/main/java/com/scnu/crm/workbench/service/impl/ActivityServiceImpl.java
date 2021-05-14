package com.scnu.crm.workbench.service.impl;

import com.scnu.crm.settings.dao.UserDao;
import com.scnu.crm.settings.domain.User;
import com.scnu.crm.utils.SqlSessionUtil;
import com.scnu.crm.vo.PaginationVo;
import com.scnu.crm.workbench.dao.ActivityDao;
import com.scnu.crm.workbench.dao.ActivityRemarkDao;
import com.scnu.crm.workbench.domain.Activity;
import com.scnu.crm.workbench.domain.ActivityRemark;
import com.scnu.crm.workbench.service.ActivityService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ActivityServiceImpl implements ActivityService {
    private ActivityDao activityDao = SqlSessionUtil.getSqlSession().getMapper(ActivityDao.class);
    private ActivityRemarkDao activityRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ActivityRemarkDao.class);
    private UserDao userDao = SqlSessionUtil.getSqlSession().getMapper(UserDao.class);

    public boolean save(Activity activity) {
        return (activityDao.save(activity) == 1);
    }

    public PaginationVo<Activity> pageList(Map<String, Object> map) {
        //取得total
        int total = activityDao.getTotalByCondition(map);
        //取dataList
        List<Activity> dataList = activityDao.getDataListByCondition(map);
        //封装到vo
        PaginationVo<Activity> vo = new PaginationVo<Activity>();
        vo.setTotal(total);
        vo.setDataList(dataList);
        return vo;
    }

    public boolean delete(String[] ids) {
        boolean flag = false;
        //删除市场活动之前 首先删除关联表的数据
        int count1 = activityRemarkDao.getCountByIds(ids);
        int count2 = activityRemarkDao.deleteByIds(ids);
        flag = (count1 == count2);
        if(flag){
            int count3 = activityDao.deleteByIds(ids);
            flag = (count3 == ids.length);
        }
        return flag;
    }

    public Map<String, Object> getUserListAndActivity(String id) {
        Map<String,Object> map = new HashMap<String, Object>();
        List<User> users = userDao.getUserList();
        Activity a = activityDao.getActivityById(id);
        map.put("users",users);
        map.put("a",a);
        return map;
    }

    public boolean update(Activity activity) {
        return (1 == activityDao.update(activity));
    }

    public Activity detail(String id) {
        return activityDao.detail(id);
    }

    public List<ActivityRemark> getRemarkList(String activityId) {
        return activityRemarkDao.getRemarkList(activityId);
    }

    public boolean deleteRemark(String id) {
        return (1 == activityRemarkDao.deleteRemark(id));
    }

    public boolean saveRemark(ActivityRemark ar) {
        return (1 == activityRemarkDao.saveRemark(ar));
    }

    public boolean updateRemark(ActivityRemark ar) {
        return (1 == activityRemarkDao.updateRemark(ar));
    }

    public List<Activity> getActivityListByClueId(String clueId) {
        return activityDao.getActivityListByClueId(clueId);
    }

    public List<Activity> getActivityListByName(Map<String, String> map) {
        return activityDao.getActivityListByName(map);
    }

    public List<Activity> getActivityListByName1(String name) {
        return activityDao.getActivityListByName1(name);
    }
}
