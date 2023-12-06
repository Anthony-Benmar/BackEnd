package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.MeshMapper;
import com.bbva.dto.mesh.request.MeshDtoRequest;
import com.bbva.dto.mesh.response.MeshRelationalDtoResponse;
import com.bbva.entities.mesh.JobExecution;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class MeshDao {
    private static final Logger LOGGER = Logger.getLogger(MeshDao.class.getName());

    private static MeshDao instance = null;

    public static synchronized MeshDao getInstance() {
        if (Objects.isNull(instance)) {
            instance = new MeshDao();
        }

        return instance;
    }

    public List<MeshRelationalDtoResponse> jobsdependencies(MeshDtoRequest dto) {
        List<MeshRelationalDtoResponse> result = new ArrayList<>();
        List<JobExecution> listJobExecutions;
        List<JobExecution> listStatusJobExecutions;
        try{
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                MeshMapper meshMapper = session.getMapper(MeshMapper.class);
                // INCOND = 1, OUTCOND = 2
                if (dto.type.equals("2")){
                    listJobExecutions = meshMapper.ListJobExecutionsLaters();
                }else{
                    listJobExecutions = meshMapper.ListJobExecutionsPrevious();
                }
                listStatusJobExecutions = meshMapper.ListStatusJobExecutions(dto.orderDate);
            }

            var filters = listJobExecutions.stream()
                    .filter(f->f.job_name.toUpperCase().equals(dto.jobName))
                    .findFirst().orElse(null);

            if (filters == null){
                return result;
            }

            var meshRelationalDtoResponse = new MeshRelationalDtoResponse(filters.id.toString(),filters.job_id.toString(),"",filters.job_name,
                    filters.json_name, filters.folder,filters.application, dto.orderDate,filters.frequency,filters.job_type,
                    filters.execution_date,filters.status);
            result.add(meshRelationalDtoResponse);

            var listJobExecutionDto = listJobExecutions.stream().map(c->{
                String fatherJobId = c.father_job_id != null ? c.father_job_id.toString() : "";
                return new MeshRelationalDtoResponse(c.id.toString(),c.job_id.toString(),fatherJobId,c.job_name,
                        c.json_name, c.folder,c.application, dto.orderDate,c.frequency,c.job_type,
                        c.execution_date,c.status);
            }).collect(Collectors.toList());

            var listJobsExecutionResponse = recursiveRelational(filters.job_id.toString(), listJobExecutionDto,null);
            findDetailJobExecution(listStatusJobExecutions,listJobsExecutionResponse);
            result.addAll(listJobsExecutionResponse);

        }
        catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }

    private List<MeshRelationalDtoResponse> recursiveRelational(String jobId, List<MeshRelationalDtoResponse> listjobExecutions, List<MeshRelationalDtoResponse> listAcumulado){
        List<MeshRelationalDtoResponse> result = new ArrayList<MeshRelationalDtoResponse>();
        var jobsChild = listjobExecutions.stream()
                .filter(f->f.parentId.equals(jobId) && !f.id.equals("0"))
                .collect(Collectors.toList());

        addUniquesJobsId(jobsChild, result, listAcumulado);

        jobsChild.forEach(j->{
            var newjobschilds = recursiveRelational(j.id, listjobExecutions, result);
            addUniquesJobsId(newjobschilds, result, result);
        });
        return  result;
    }

    private void findDetailJobExecution(List<JobExecution> listStatusJobExecutions, List<MeshRelationalDtoResponse> meshRelational){
        meshRelational.forEach(job -> {
            var jobStatus = listStatusJobExecutions.stream()
                    .filter(j->j.job_name.equals(job.jobName))
                    .findFirst().orElse(null);
            if (jobStatus != null){
                job.status = jobStatus.status.contains("Wait") ? "WAIT" : jobStatus.status;
                job.executionDate = jobStatus.execution_date;
            }else{
                job.status = "NA";
            }
        });
    }

    private void addUniquesJobsId (List<MeshRelationalDtoResponse> jobsChild, List<MeshRelationalDtoResponse> result, List<MeshRelationalDtoResponse> listAcumulado){
        if (listAcumulado == null) {
            result.addAll(jobsChild);
        }else{
            Set<String> listIdsChilds = jobsChild.stream().map(w->w.id)
                    .collect(Collectors.toSet());

            var listExistingJobsAcum = listAcumulado.stream().filter(t-> listIdsChilds.contains(t.id))
                    .collect(Collectors.toList());
            if (listExistingJobsAcum.stream().count() == 0){
                result.addAll(jobsChild);
            }else{
                Set<String> listExistingIdsAcum = listExistingJobsAcum.stream().map(w->w.id)
                        .collect(Collectors.toSet());
                // los jobs nuevos se insertan directamente
                var jobsChildUniques = jobsChild.stream()
                        .filter(j-> !listExistingIdsAcum.contains(j.id))
                        .collect(Collectors.toList());
                result.addAll(jobsChildUniques);

                // los jobs ya existentes pasan por este proceso para que se inserten una sola vez
                var jobsChildRepeated = jobsChild.stream().filter(j-> listExistingIdsAcum.contains(j.id))
                        .filter(ff-> {
                            var ffId = ff.id.indexOf("-")>0 ? ff.id.split("-")[1] : ff.id;
                            var ffParentId = ff.parentId.indexOf("-")>0 ? ff.parentId.split("-")[1] : ff.parentId;

                            var parentAlreadyExist= listAcumulado.stream()
                                    .filter(f->f.id.contains("-" + ffId) || f.id.contains("-" + ffParentId))
                                    .collect(Collectors.toList());
                            if (parentAlreadyExist.stream().count() > 0){
                                return false;
                            }else{
                                return true;
                            }
                        })
                        .map(c->{
                            return new MeshRelationalDtoResponse(c.rowNumber,c.rowNumber + "-" + c.id,c.parentId,c.jobName,
                                    c.jsonName, c.folder,c.application, c.orderDate,c.frequency,c.jobType,
                                    c.executionDate,c.status);
                        }).collect(Collectors.toList());

                result.addAll(jobsChildRepeated);
            }
        }
    }

}
