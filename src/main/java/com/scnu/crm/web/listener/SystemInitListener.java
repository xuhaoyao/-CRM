package com.scnu.crm.web.listener;

import com.scnu.crm.settings.domain.DicValue;
import com.scnu.crm.settings.service.DicService;
import com.scnu.crm.settings.service.impl.DicServiceImpl;
import com.scnu.crm.utils.ServiceFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SystemInitListener implements ServletContextListener {
    public void contextInitialized(ServletContextEvent sce) {
        System.out.println("服务器缓存处理数据字典开始...");
        ServletContext application = sce.getServletContext();
        DicService ds = (DicService) ServiceFactory.getService(new DicServiceImpl());
        Map<String, List<DicValue>> map = ds.getAllDicValues();
        Set<String> set = map.keySet();
        for (String s : set) {
            application.setAttribute(s,map.get(s));
        }
        System.out.println("服务器缓存处理数据字典结束...");
    }
}
