package com.bbva.service;

import com.bbva.common.HttpStatusCodes;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.JobDao;
import com.bbva.dto.job.request.*;
import com.bbva.dto.job.response.JobBasicInfoDtoResponse;
import com.bbva.dto.job.response.JobBasicInfoFilterDtoResponse;
import com.bbva.dto.job.response.*;


import java.util.ArrayList;
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

    public IDataResult<JobAdditionalDtoResponse> updateAdditional(JobAdditionalDtoRequest dto) {
        try {
            if (dto.getJobId() == null || dto.getJobId().equals(0))
                return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "JobId must not be null or 0");

            if(dto.getCreatedProjectId() == null || dto.getCreatedProjectId().equals(0))
                return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "CreatedProjectId must not be null or 0");

            if (dto.getCreatedDevEmail() == null || dto.getCreatedDevEmail().isBlank())
                return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "CreatedDevEmail must not be null or empty");

            if (dto.getMonitoringProjectId() == null || dto.getMonitoringProjectId().equals(0))
                return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "MonitoringProjectId must not be null or 0");

            if (dto.getMonitoringDevEmail() == null || dto.getMonitoringDevEmail().isBlank())
                return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "MonitoringDevEmail must not be null or empty");

            if (dto.getClassificationType() == null || dto.getClassificationType() < 0)
                return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "ClassificationType must not be null or 0");

            if (dto.getCriticalRouteType() == null || dto.getCriticalRouteType() < 0)
                return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "CriticalRouteType must not be null or negative");

            if (dto.getCriticalRouteType() == 1 && (dto.getJobFunctionalDesc() == null || dto.getJobFunctionalDesc().isEmpty()))
                return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "JobFunctionalDesc must not be null or empty if CriticalRouteType is 1");

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

    public IDataResult<List<JobMonitoringUpdateDtoResponse>> getAllMonitoringRequest() {
        try {
            var result = jobDao.getAllMonitoringRequest();
            return new SuccessDataResult(result);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public IDataResult<JobMonitoringRequestFilterDtoResponse> filterMonitoringRequest(JobMonitoringRequestFilterDtoRequest dto) {
       try {
           var result = jobDao.filterMonitoringRequest(dto);
           return new SuccessDataResult(result);
       } catch (Exception e) {
           log.log(Level.SEVERE, e.getMessage(), e);
           return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
       }
    }

    public IDataResult<JobMonitoringUpdateDtoResponse> insertMonitoringRequest(JobMonitoringRequestInsertDtoRequest dto) {
        try {
            var result = jobDao.insertMonitoringRequest(dto);
            return new SuccessDataResult(result);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public IDataResult<JobMonitoringUpdateDtoResponse> updateMonitoringRequest(JobMonitoringUpdateDtoRequest dto) {
        try {
            if (dto.getMonitoringRequestId().equals(0))
                return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "MonitoringRequestId must to be not null");
            if (dto.getJobId().equals(0))
                return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "JobId must to be not null");
            if (dto.getFromSdatoolId().isEmpty())
                return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "FromSdatoolId must to be not null");
            if (dto.getFromDevEmail().isEmpty())
                return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "FromDevEmail must to be not null");
            if (dto.getToSdatoolId().isEmpty())
                return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "ToSdatoolId must to be not null");
            if (dto.getToDevEmail().isEmpty())
                return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "ToDevEmail must to be not null");
            if (dto.getStartDate() == null)
                return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "StartDate must to be not null");
            if (dto.getEndDate() == null)
                return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "EndDate must to be not null");
            //if (dto.getStatusType().equals(0))
              //  return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "StatusType must to be not null");
            if (dto.getStatusType() == null || dto.getStatusType() < 0 || dto.getStatusType() > 4)
                return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "StatusType must be between 0 and 4");
            if (dto.getCommentRequestDesc().isEmpty())
                return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "CommentRequestDesc must to be not null");
            jobDao.updateMonitoringRequest(dto);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return new SuccessDataResult(dto);
    }
}
