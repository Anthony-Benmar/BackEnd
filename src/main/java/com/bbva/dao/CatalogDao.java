package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.CatalogMapper;
import com.bbva.dto.catalog.request.ListByCatalogIdDtoRequest;
import com.bbva.dto.catalog.response.CatalogResponseDto;
import com.bbva.dto.catalog.response.ListByCatalogIdDtoResponse;
import com.bbva.dto.catalog.response.ListByCatalogIdGroupByCatalogDtoResponse;
import com.bbva.dto.catalog.response.ListByCatalogIdGroupByCatalogGroupByElementDtoResponse;
import com.bbva.entities.batch.GetCatalogEntity;
import com.bbva.entities.common.CatalogEntity;
import com.bbva.entities.common.PeriodEntity;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CatalogDao {

    private static final Logger LOGGER = Logger.getLogger(IssueTicketDao.class.getName());

    public ArrayList<GetCatalogEntity> getCatalog(
            Integer catalogId,
            Integer parentCatalogId,
            Integer parentElementId
    ) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        ArrayList<GetCatalogEntity> list;
        try (SqlSession session = sqlSessionFactory.openSession()) {
            CatalogMapper mapper = session.getMapper(CatalogMapper.class);
            list = mapper.getCatalog(catalogId, parentCatalogId, parentElementId);
        }
        return list;
    }

    public ListByCatalogIdDtoResponse getCatalogoByCatalogoId(ListByCatalogIdDtoRequest dto) {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        ArrayList<CatalogEntity> list;
        ListByCatalogIdDtoResponse listMainCatalog = new ListByCatalogIdDtoResponse();
        ArrayList<ListByCatalogIdGroupByCatalogDtoResponse> listCatalog = new ArrayList<>();

        try (SqlSession session = sqlSessionFactory.openSession()) {
            CatalogMapper mapper = session.getMapper(CatalogMapper.class);

            list = mapper
                    .getListByCatalog(dto.getCatalog());

            List<Integer> distinctCatalogId = list
                    .stream()
                    .map(m -> m.getCatalogId())
                    .distinct()
                    .collect(Collectors.toList());

            distinctCatalogId.forEach(catalogId -> {
                ListByCatalogIdGroupByCatalogDtoResponse objectCatalog = new ListByCatalogIdGroupByCatalogDtoResponse();
                ArrayList<ListByCatalogIdGroupByCatalogGroupByElementDtoResponse> listElement = new ArrayList<>();

                List<CatalogEntity> filterByCatalogId = list
                        .stream()
                        .filter(f -> f.getCatalogId() == catalogId)
                        .collect(Collectors.toList());

                filterByCatalogId
                        .stream()
                        .filter(f -> f.getElementId() != catalogId)
                        .forEach(element -> {
                            ListByCatalogIdGroupByCatalogGroupByElementDtoResponse objectElement = new ListByCatalogIdGroupByCatalogGroupByElementDtoResponse();

                            objectElement.setElementId(element.getElementId());
                            objectElement.setDescription(element.getElementName());

                            listElement.add(objectElement);
                        });

                String description = filterByCatalogId
                        .stream()
                        .filter(f -> f.getElementId() == catalogId)
                        .findFirst().orElse(new CatalogEntity(0,0,"",0))
                        .getElementName();

                objectCatalog.setCatalogId(catalogId);
                objectCatalog.setCatalogDescription(description);
                objectCatalog.setElement(listElement);

                listCatalog.add(objectCatalog);
            });

            listMainCatalog.setCatalog(listCatalog);
        }

        return listMainCatalog;
    }

    public List<PeriodEntity> listAllPeriods() {
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            CatalogMapper mapper = session.getMapper(CatalogMapper.class);
            List<PeriodEntity> listPeriods = mapper.listAllPeriods();
            return listPeriods;
        }catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return null;
        }
    }
}