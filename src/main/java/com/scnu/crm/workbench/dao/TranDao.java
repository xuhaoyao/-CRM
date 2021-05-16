package com.scnu.crm.workbench.dao;

import com.scnu.crm.workbench.domain.Tran;

import java.util.List;
import java.util.Map;

public interface TranDao {

    int save(Tran t);

    Tran detail(String id);

    int changeStage(Tran t);

    List<Map<String, String>> getDataList();

    int getMaxRecord();

}
