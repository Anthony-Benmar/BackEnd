package com.bbva.service;

import com.bbva.common.HttpStatusCodes;
import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.JobDao;
import com.bbva.dto.job.request.JobAdditionalDtoRequest;
import com.bbva.dto.job.request.JobBasicInfoFilterDtoRequest;
import com.bbva.dto.job.request.JobDTO;
import com.bbva.dto.job.request.JobMonitoringDtoRequest;
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
            if (dto.getJobId().equals(0))
                return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "JonId must to be not null");
            if (dto.getCriticalRouteType() == 1 && dto.getJobFunctionalDesc().isEmpty())
                return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "JobFunctionalDesc must to be not null if CriticalRouteType is 1");

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

    public IDataResult<List<JobMonitoringDtoResponse>> getAllMonitoringRequest() {
        try {
            var result = jobDao.getAllMonitoringRequest();
            return new SuccessDataResult(result);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public IDataResult<JobMonitoringDtoResponse> insertMonitoringRequest(JobMonitoringDtoRequest dto) {
        try {
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
            if (dto.getStatusType().equals(0))
                return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "StatusType must to be not null");
            if (dto.getCommentRequestDesc().isEmpty())
                return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "CommentRequestDesc must to be not null");

            jobDao.insertMonitoringRequest(dto);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return new SuccessDataResult(dto);
    }

    public IDataResult<JobMonitoringDtoResponse> updateMonitoringRequest(JobMonitoringDtoRequest dto) {
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

            switch (dto.getStatusType()) {
                case 0: // solicitado
                    if (dto.getStatusType() == 1 || dto.getStatusType() == 2) { // aprobado o rechazado
                        jobDao.updateMonitoringRequest(dto);
                    } else {
                        throw new IllegalArgumentException("El estado solo puede ser aprobado o rechazado");
                    }
                    break;
                case 1: // aprobado
                    if (dto.getStatusType() == 3 || dto.getStatusType() == 4) { // finalizado o finalizado automático
                        jobDao.updateMonitoringRequest(dto);
                    } else {
                        throw new IllegalArgumentException("El estado solo puede ser finalizado o finalizado automático");
                    }
                    break;
                case 2: // rechazado
                    throw new IllegalArgumentException("La solicitud ha sido rechazada");
                case 3: // finalizado
                case 4: // finalizado automático
                    throw new IllegalArgumentException("La solicitud ya ha sido finalizada de manera automática");
                default:
                    throw new IllegalArgumentException("La solicitud ya ha sido finalizada");
            }
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return new SuccessDataResult(dto);
    }

    public IDataResult<JobMonitoringDtoResponse> deleteMonitoringRequest(Integer monitoringRequestId) {
        try {
            if (monitoringRequestId.equals(0))
                return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, "MonitoringRequestId must to be not null");

            jobDao.deleteMonitoringRequest(monitoringRequestId);
        } catch (Exception e) {
            log.log(Level.SEVERE, e.getMessage(), e);
            return new ErrorDataResult(null, HttpStatusCodes.HTTP_INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return new SuccessDataResult(null);
    }
}
