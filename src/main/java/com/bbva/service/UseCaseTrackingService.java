package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.UseCaseTrackingDao;
import com.bbva.dto.usecasetracking.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UseCaseTrackingService {

    private final UseCaseTrackingDao useCaseTrackingDao = new UseCaseTrackingDao();

    public IDataResult<List<UseCaseDto>> getUseCaseByDomainId(Integer domainId) {
        List<UseCaseDto> result = useCaseTrackingDao.getUseCaseByDomainId(domainId);
        return new SuccessDataResult(result, "Succesfull");
    }

    public IDataResult<GetUseCaseTrackingJobsResponseDto> getUseCaseTrackingJobs(GetUseCaseTrackingJobsRequestDto dto) {
        List<GetUseTrackingJobsSPResponse> result = useCaseTrackingDao.getUseCaseTrackingJobs(dto);

        GetUseCaseTrackingJobsResponseDto getUseCaseTrackingJobsResponseDto = new GetUseCaseTrackingJobsResponseDto();

        List<UseCaseTrackingData> useCaseTrackingDataList = new ArrayList<>();
        Map<Integer, List<GetUseTrackingJobsSPResponse>> groupedJobs = result.stream()
                .collect(Collectors.groupingBy(GetUseTrackingJobsSPResponse::getUse_case_id));

        for (List<GetUseTrackingJobsSPResponse> group : groupedJobs.values()) {
            UseCaseTrackingData useCaseTrackingData = new UseCaseTrackingData();
            List<UseCaseJobs> useCaseJobsList = new ArrayList<>();

            GetUseTrackingJobsSPResponse newParent = group.get(0);
            useCaseTrackingData.setName(newParent.getName());
            useCaseTrackingData.setUse_case_id(newParent.getUse_case_id());
            useCaseTrackingData.setSubgroup_use_case(newParent.getSubgroup_use_case());
            useCaseTrackingData.setDomain_id(newParent.getDomain_id());

            for (GetUseTrackingJobsSPResponse useCase : group) {
                UseCaseJobs newJob = new UseCaseJobs();
                newJob.setJob_name(useCase.getJob_name());
                newJob.setEnd_time(useCase.getEnd_time());
                newJob.setOrder_date(useCase.getOrder_date());
                newJob.setStart_time(useCase.getStart_time());
                newJob.setExecution_status(useCase.getExecution_status());
                newJob.setFunctional_description(useCase.getFunctional_description());
                newJob.setAdditional_functional_description(useCase.getAdditional_functional_description());
                useCaseJobsList.add(newJob);
            }

            StatisticsData statisticsData = new StatisticsData();
            int total = useCaseJobsList.size();
            int canceled = countJobsWithExecutionStatus(useCaseJobsList, "NOTOK");
            int successful = countJobsWithExecutionStatus(useCaseJobsList, "OK");
            int inProgress = countJobsWithExecutionStatus(useCaseJobsList, "IN PROGRESS");

            statisticsData.setTotal(total);
            statisticsData.setCanceled(canceled);
            statisticsData.setSuccessful(successful);
            statisticsData.setInProgress(inProgress);

            useCaseTrackingData.setUseCaseJobs(useCaseJobsList);
            useCaseTrackingData.setStatistics(statisticsData);
            useCaseTrackingDataList.add(useCaseTrackingData);
        }
        Integer recordsCount = 0;
        Integer pagesAmount = 0;

        recordsCount = (result.size() > 0) ? result.get(0).getRecords_count() : 0;
        pagesAmount = (int) Math.ceil(recordsCount.floatValue() / dto.getRecords_amount().floatValue());

        getUseCaseTrackingJobsResponseDto.setCount(recordsCount);
        getUseCaseTrackingJobsResponseDto.setPages_amount(pagesAmount);
        getUseCaseTrackingJobsResponseDto.setUseCaseTracking(useCaseTrackingDataList);

        // Calcular estadísticas globales
        int countOK = 0;
        int countNOTOK = 0;
        int countInProgress = 0;

        for (GetUseTrackingJobsSPResponse job : result) {
            String executionStatus = job.getExecution_status();
            if (executionStatus.equals("OK")) {
                countOK++;
            } else if (executionStatus.equals("NOTOK")) {
                countNOTOK++;
            } else if (executionStatus.equals("IN PROGRESS")) {
                countInProgress++;
            }
        }

        StatisticsData globalStatisticsData = new StatisticsData();
        globalStatisticsData.setTotal(result.size());
        globalStatisticsData.setSuccessful(countOK);
        globalStatisticsData.setCanceled(countNOTOK);
        globalStatisticsData.setInProgress(countInProgress);

        getUseCaseTrackingJobsResponseDto.setGlobalStatistics(globalStatisticsData);

        return new SuccessDataResult<>(getUseCaseTrackingJobsResponseDto, "Successful");
    }

    private int countJobsWithExecutionStatus(List<UseCaseJobs> useCaseJobsList, String status) {
        return (int) useCaseJobsList.stream()
                .filter(job -> job.getExecution_status().equals(status))
                .count();
    }
}