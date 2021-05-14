package com.scnu.crm.workbench.service.impl;

import com.scnu.crm.utils.SqlSessionUtil;
import com.scnu.crm.utils.UUIDUtil;
import com.scnu.crm.vo.PaginationVo;
import com.scnu.crm.workbench.dao.ClueActivityRelationDao;
import com.scnu.crm.workbench.dao.ClueDao;
import com.scnu.crm.workbench.domain.Clue;
import com.scnu.crm.workbench.domain.ClueActivityRelation;
import com.scnu.crm.workbench.service.ClueService;

import java.util.List;
import java.util.Map;

public class ClueServiceImpl implements ClueService {
    private ClueDao clueDao = SqlSessionUtil.getSqlSession().getMapper(ClueDao.class);
    private ClueActivityRelationDao clueActivityRelationDao = SqlSessionUtil.getSqlSession().getMapper(ClueActivityRelationDao.class);

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
}
