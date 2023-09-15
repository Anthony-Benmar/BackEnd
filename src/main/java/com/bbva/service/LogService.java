package com.bbva.service;

import com.bbva.dao.LogDao;
import com.bbva.entities.secu.LogEntity;

import java.util.Date;
import java.util.List;

public class LogService {
    private final LogDao logDao = new LogDao();

    public List<LogEntity> logList() {
        return logDao.list();
    }

    public void inserLogError(int processId, String period, String processName, Exception e) {
        String message = "Error: "+ processName+": "+e.getLocalizedMessage();
        LogEntity entity = new LogEntity(processId, period, processName,  message);
        entity.setOperationDate(new Date());
        entity.setStatusType(1);
        entity.setOperationUser(1);
        logDao.insertLog(entity);
    }

}
