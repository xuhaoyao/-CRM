package com.scnu.crm.workbench.service.impl;

import com.scnu.crm.utils.SqlSessionUtil;
import com.scnu.crm.workbench.dao.CustomerDao;
import com.scnu.crm.workbench.service.CustomerService;

import java.util.List;

public class CustomerServiceImpl implements CustomerService {
    private CustomerDao customerDao = SqlSessionUtil.getSqlSession().getMapper(CustomerDao.class);

    public List<String> getCustomerName(String name) {
        return customerDao.getCustomerByName1(name);
    }
}
