package com.bbva.dao;

import com.bbva.entities.spp.Period;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SppDao {

    private static final Logger LOGGER = Logger.getLogger(SppDao.class.getName());

    public List<Period> listPeriodsForSelect() throws IOException, InterruptedException {
        //SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactorySpp.getInstance();
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                //.uri(URI.create("https://gapis.bbva.com/sisgestionproyecto/v1/period/all"))
                .uri(URI.create("https://dev-bbva-gateway.appspot.com/sisgestionproyecto/v1/period/current"))
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        LOGGER.log(Level.SEVERE, response.body());
        List<Period> periodEntityList = null;
        //try (SqlSession session = sqlSessionFactory.openSession()) {
        //    PeriodMapper mapper = session.getMapper(PeriodMapper.class);
            //List<Period> projectList = new ArrayList<>();
        //    List<Period> periodEntityList = mapper.getListPeriods();
            //projectEntityList.forEach(projectEntity->{
            //    ProjectListForSelectDtoResponse objectProject = new ProjectListForSelectDtoResponse();
            //    objectProject.setId(Integer.parseInt(projectEntity.getProject_id()));
            //    objectProject.setSdatool(projectEntity.getSdatool_id());
            //    objectProject.setName(projectEntity.getProject_name());
            //    projectList.add(objectProject);
            //});
            return periodEntityList;
        //}
    }
}
