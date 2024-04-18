package com.bbva.service;

import com.bbva.common.HttpStatusCodes;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.JobBasicInfoDao;
import com.bbva.dto.job.response.JobBasicInfoDtoResponse;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobBasicInfoService {
    private final JobBasicInfoDao jobBasicInfoDao = new JobBasicInfoDao();
    private static final Logger log= Logger.getLogger(JobBasicInfoService.class.getName());
    public IDataResult<List<JobBasicInfoDtoResponse>> listAllJobs() {
        try {
            var result = jobBasicInfoDao.listAll();
            return new SuccessDataResult(result);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
