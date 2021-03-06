package com.scnu.crm.workbench.service.impl;

import com.scnu.crm.utils.SqlSessionUtil;
import com.scnu.crm.utils.UUIDUtil;
import com.scnu.crm.vo.PaginationVo;
import com.scnu.crm.workbench.dao.*;
import com.scnu.crm.workbench.domain.*;
import com.scnu.crm.workbench.service.ClueService;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ClueServiceImpl implements ClueService {

    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueActivityRelationDao clueActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);
    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);
    private ClueRemarkDao clueRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ClueRemarkDao.class);
    private CustomerRemarkDao customerRemarkDao = SqlSessionUtil.getSqlSession().getMapper(CustomerRemarkDao.class);
    private ContactsRemarkDao contactsRemarkDao = SqlSessionUtil.getSqlSession().getMapper(ContactsRemarkDao.class);
    private ContactsActivityRelationDao contactsActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ContactsActivityRelationDao.class);
    private TranDao tranDao = SqlSessionUtil.getSqlSession().getMapper(TranDao.class);
    private TranHistoryDao tranHistoryDao = SqlSessionUtil.getSqlSession().getMapper(TranHistoryDao.class);

    public boolean save(Clue clue) {
        return (1 == clueDao.save(clue));
    }

    public Clue findById(String id) {
        return clueDao.findById(id);
    }

    public PaginationVo<Clue> pageList(Map<String, Object> map) {
        PaginationVo<Clue> vo = new PaginationVo<Clue>();
        int total = clueDao.getTotalByCondition(map);
        List<Clue> dataList = clueDao.getDataListByCondition(map);
        vo.setTotal(total);
        vo.setDataList(dataList);
        return vo;
    }

    public boolean disssociation(String id) {
        return (1 == clueActivityRelationDao.disssociation(id));
    }

    public boolean association(Map<String, Object> map) {
        String clueId = (String)map.get("clueId");
        String[] activityIds = (String[]) map.get("activityIds");
        int count = 0;
        for (String activityId : activityIds) {
            String id = UUIDUtil.getUUID();
            ClueActivityRelation car = new ClueActivityRelation();
            car.setId(id);
            car.setClueId(clueId);
            car.setActivityId(activityId);
            count += clueActivityRelationDao.association(car);
        }
        return (count == activityIds.length);
    }

    public boolean convert(String clueId, Tran t, String createBy, String createTime) {

        boolean flag = true;

        //1??????????????????id??????????????????,????????????????????????????????????????????????
        Clue c = clueDao.getClueById(clueId);
        //2???????????????????????????????????????(??????????????????????????????),?????????????????????,??????
        String company = c.getCompany();
        Customer cus = customerDao.getCustomerByName(company);
        //??????cus = null ????????????
        if(cus == null){
            cus = new Customer();
            cus.setId(UUIDUtil.getUUID());
            cus.setOwner(c.getOwner());
            cus.setAddress(c.getAddress());
            cus.setName(c.getCompany());
            cus.setWebsite(c.getWebsite());
            cus.setPhone(c.getPhone());
            cus.setCreateBy(createBy);
            cus.setCreateTime(createTime);
            cus.setDescription(c.getDescription());
            cus.setNextContactTime(c.getNextContactTime());
            cus.setContactSummary(c.getContactSummary());
            if(customerDao.save(cus) != 1) flag = false;
        }
        //3??????????????????????????????????????????,???????????????
        Contacts con = new Contacts();
        con.setId(UUIDUtil.getUUID());
        con.setAddress(c.getAddress());
        con.setAppellation(c.getAppellation());
        con.setContactSummary(c.getContactSummary());
        con.setCreateBy(createBy);
        con.setCreateTime(createTime);
        con.setCustomerId(cus.getId());
        con.setFullname(c.getFullname());
        con.setEmail(c.getEmail());
        con.setSource(c.getSource());
        con.setOwner(c.getOwner());
        con.setNextContactTime(c.getNextContactTime());
        con.setMphone(c.getMphone());
        con.setJob(c.getJob());
        con.setDescription(c.getDescription());
        if(contactsDao.save(con) != 1) flag = false;

        //4?????????????????????????????????????????????????????????
        List<ClueRemark> clueRemarks = clueRemarkDao.getByClueId(clueId);
        for (ClueRemark clueRemark : clueRemarks) {
            CustomerRemark cr = new CustomerRemark();
            cr.setId(UUIDUtil.getUUID());
            cr.setCreateBy(createBy);
            cr.setCreateTime(createTime);
            cr.setEditFlag("0");
            cr.setNoteContent(clueRemark.getNoteContent());
            cr.setCustomerId(cus.getId());
            if(customerRemarkDao.save(cr) != 1)flag = false;

            ContactsRemark cor = new ContactsRemark();
            cor.setId(UUIDUtil.getUUID());
            cor.setCreateBy(createBy);
            cor.setCreateTime(createTime);
            cor.setEditFlag("0");
            cor.setNoteContent(clueRemark.getNoteContent());
            cor.setContactsId(con.getId());
            if (contactsRemarkDao.save(cor) != 1) flag = false;

        }

        //5???????????????????????????????????????????????????????????????????????????
        List<String> activityIds = clueActivityRelationDao.getByClueId(clueId);
        for (String activityId : activityIds) {
            ContactsActivityRelation car = new ContactsActivityRelation();
            car.setId(UUIDUtil.getUUID());
            car.setContactsId(con.getId());
            car.setActivityId(activityId);
            if(contactsActivityRelationDao.association(car) != 1) flag = false;
        }

        //6??????????????????????????????????????????????????????
        //?????????????????????????????????????????????
        if(t != null){
            t.setNextContactTime(c.getNextContactTime());
            t.setOwner(c.getOwner());
            t.setContactsId(con.getId());
            t.setCustomerId(cus.getId());
            t.setSource(c.getSource());
            t.setDescription(c.getDescription());
            t.setContactSummary(c.getContactSummary());
            if(tranDao.save(t) != 1)flag = false;

            //7???????????????????????????????????????????????????
            TranHistory th = new TranHistory();
            th.setId(UUIDUtil.getUUID());
            th.setCreateBy(createBy);
            th.setCreateTime(createTime);
            th.setMoney(t.getMoney());
            th.setStage(t.getStage());
            th.setTranId(t.getId());
            th.setExpectedDate(t.getExpectedDate());
            if(tranHistoryDao.save(th) != 1) flag = false;
        }
        //8?????????????????????
        if(clueRemarks.size() != clueRemarkDao.deleteByClueId(clueId)) flag = false;

        //9???????????????????????????????????????
        if(activityIds.size() != clueActivityRelationDao.deleteByClueId(clueId)) flag = false;

        //10???????????????
        if(1 != clueDao.deleteByClueId(clueId)) flag = false;

        return flag;
    }
}
