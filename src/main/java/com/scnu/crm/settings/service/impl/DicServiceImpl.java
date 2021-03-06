package com.scnu.crm.settings.service.impl;

import com.scnu.crm.settings.dao.DicTypeDao;
import com.scnu.crm.settings.dao.DicValueDao;
import com.scnu.crm.settings.domain.DicValue;
import com.scnu.crm.settings.service.DicService;
import com.scnu.crm.utils.SqlSessionUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DicServiceImpl implements DicService {
    private DicTypeDao dicTypeDao = SqlSessionUtil.getSqlSession().getMapper(DicTypeDao.class);
    private DicValueDao dicValueDao = SqlSessionUtil.getSqlSession().getMapper(DicValueDao.class);

    public Map<String, List<DicValue>> getAllDicValues() {
        Map<String,List<DicValue>> map = new HashMap<String, List<DicValue>>();
        List<String> typeList = dicTypeDao.getAllTypes();
        for (String s : typeList) {
            List<DicValue> valueList = dicValueDao.getAllValues(s);
            map.put(s,valueList);
        }
        return map;
    }
}
