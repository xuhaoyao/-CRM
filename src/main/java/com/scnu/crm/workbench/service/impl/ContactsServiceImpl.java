package com.scnu.crm.workbench.service.impl;

import com.scnu.crm.utils.SqlSessionUtil;
import com.scnu.crm.workbench.dao.ContactsDao;
import com.scnu.crm.workbench.domain.Contacts;
import com.scnu.crm.workbench.service.ContactsService;

import java.util.List;

public class ContactsServiceImpl implements ContactsService {
    private ContactsDao contactsDao = SqlSessionUtil.getSqlSession().getMapper(ContactsDao.class);

    public List<Contacts> getContactsListByName(String fullname) {
        return contactsDao.getContactsListByName(fullname);
    }
}
