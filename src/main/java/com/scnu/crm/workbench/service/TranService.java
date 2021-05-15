package com.scnu.crm.workbench.service;

import com.scnu.crm.workbench.domain.Tran;
import com.scnu.crm.workbench.domain.TranHistory;

import java.util.List;

public interface TranService {
    boolean save(Tran t, String customerName);

    Tran detail(String id);

    List<TranHistory> getHistoryList(String tranId);

    boolean changeStage(Tran t);
}
