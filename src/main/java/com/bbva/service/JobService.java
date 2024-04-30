package com.bbva.service;

import com.bbva.common.HttpStatusCodes;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.JobDao;
import com.bbva.dto.job.request.JobAdditionalDtoRequest;
import com.bbva.dto.job.request.JobBasicInfoFilterDtoRequest;
import com.bbva.dto.job.request.JobDTO;
import com.bbva.dto.job.response.JobBasicInfoDtoResponse;
import com.bbva.dto.job.response.JobBasicInfoFilterDtoResponse;
import com.bbva.dto.job.response.*;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JobService {
    private final JobDao jobDao = new JobDao();
    private static final Logger log= Logger.getLogger(JobService.class.getName());
    public IDataResult<List<JobBasicInfoDtoResponse>> listAllJobs() {
        try {
            var result = jobDao.listAll();
            return new SuccessDataResult(result);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public IDataResult<JobBasicInfoFilterDtoResponse> jobBasicInfoFilter(JobBasicInfoFilterDtoRequest dto) {
        var result = jobDao.jobBasicInfoFilter(dto);
        return new SuccessDataResult(result);
    }

    public IDataResult<JobBasicInfoByIdDtoResponse> jobBasicDetail(Integer jobId) {
        var result = jobDao.jobBasicDetail(jobId);
        return new SuccessDataResult(result);
    }
    public IDataResult<JobAdditionalDtoResponse> getAdditional(Integer jobId) {
        var result = jobDao.getAdditional(jobId);
        return new SuccessDataResult(result);
    }
    public IDataResult<JobAdditionalDtoResponse> updateAdditional(JobAdditionalDtoRequest dto) {
        try {
            if (dto.getJobId().equals(0))
                return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "JonId must to be not null");

            jobDao.updateAdditional(dto);

        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null,HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return new SuccessDataResult(dto);
    }


    public IDataResult<List<JobDTO>> getJobById(int jobId) {
        List<JobDTO> JobList = new ArrayList<>();
        try {
            JobDTO job = jobDao.getJobById(jobId);
            JobList.add(job);
            return new SuccessDataResult(JobList);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
