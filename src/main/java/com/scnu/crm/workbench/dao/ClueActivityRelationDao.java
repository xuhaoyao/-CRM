package com.scnu.crm.workbench.dao;

import com.scnu.crm.workbench.domain.ClueActivityRelation;

import java.util.Map;

public interface ClueActivityRelationDao {


    int disssociation(String id);

    int association(ClueActivityRelation car);
}
