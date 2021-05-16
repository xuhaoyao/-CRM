package com.scnu.crm.workbench.service.impl;

import com.scnu.crm.utils.SqlSessionUtil;
import com.scnu.crm.utils.UUIDUtil;
import com.scnu.crm.workbench.dao.CustomerDao;
import com.scnu.crm.workbench.dao.TranDao;
import com.scnu.crm.workbench.dao.TranHistoryDao;
import com.scnu.crm.workbench.domain.Customer;
import com.scnu.crm.workbench.domain.Tran;
import com.scnu.crm.workbench.domain.TranHistory;
import com.scnu.crm.workbench.service.TranService;

import java.util.*;

public class TranServiceImpl implements TranService {
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    public boolean save(Tran t, String customerName) {
        boolean flag = true;
        //首先根据客户的名称精确查找客户,null则新建客户
        String customerId = customerDao.getCustomerIdByName(customerName);
        if(customerId == null){
            Customer c = new Customer();
            c.setId(UUIDUtil.getUUID());
            c.setContactSummary(t.getContactSummary());
            c.setDescription(t.getDescription());
            c.setCreateTime(t.getCreateTime());
            c.setCreateBy(t.getCreateBy());
            c.setName(customerName);
            c.setOwner(t.getOwner());
            c.setNextContactTime(t.getNextContactTime());
            if(1 != customerDao.save(c)) flag = false;
            else t.setCustomerId(c.getId());
        }
        else
            t.setCustomerId(customerId);
        //保存交易记录
        if(1 != tranDao.save(t)) flag = false;
        else{
            //保存成功则创建交易历史记录.
            TranHistory th = new TranHistory();
            th.setId(UUIDUtil.getUUID());
            th.setExpectedDate(t.getExpectedDate());
            th.setStage(t.getStage());
            th.setMoney(t.getMoney());
            th.setCreateTime(t.getCreateTime());
            th.setCreateBy(t.getCreateBy());
            th.setTranId(t.getId());
            if(1 != tranHistoryDao.save(th)) flag = false;
        }


        return flag;
    }

    public Tran detail(String id) {
        return tranDao.detail(id);
    }

    public List<TranHistory> getHistoryList(String tranId) {
        return tranHistoryDao.getHistoryList(tranId);
    }

    public boolean changeStage(Tran t) {
        boolean flag = true;
        if(1 != tranDao.changeStage(t))
            flag = false;
        else{
            TranHistory th = new TranHistory();
            th.setId(UUIDUtil.getUUID());
            th.setStage(t.getStage());
            th.setCreateBy(t.getEditBy());
            th.setCreateTime(t.getEditTime());
            th.setMoney(t.getMoney());
            th.setExpectedDate(t.getExpectedDate());
            th.setTranId(t.getId());
            flag = (1 == tranHistoryDao.save(th));
        }
        return flag;
    }

    public Map<String, Object> showEcharts() {
        List<Map<String,String>> dataList = tranDao.getDataList();
        int max = tranDao.getMaxRecord();
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("max",max);
        map.put("dataList",dataList);
        return map;
    }

    public Map<String, List<String>> showEcharts1() {
        Map<String,List<String>> map = new HashMap<String, List<String>>();
        List<Map<String,String>> dataList = tranDao.getDataList();
        List<String> name = new ArrayList<String>();
        List<String> value = new ArrayList<String>();
        for (Map<String, String> m : dataList) {
            Set<String> set = m.keySet();
            for (String s : set) {
                if("name".equals(s))
                    name.add(m.get(s));
                else
                    value.add(m.get(s));
            }
        }
        map.put("name",name);
        map.put("value",value);
        return map;
    }
}
