package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.MeshDao;
import com.bbva.dto.mesh.request.MeshDtoRequest;
import com.bbva.dto.mesh.response.MeshRelationalDtoResponse;

import java.util.List;

public class MeshService {
    private final MeshDao meshDao = new MeshDao();

    public IDataResult<List<MeshRelationalDtoResponse>> jobsdependencies(MeshDtoRequest dto)
    {
        var modelo =  meshDao.jobsdependencies(dto);
        if (modelo.stream().count() == 0){
            modelo.add(new MeshRelationalDtoResponse("","0","","NO ENCONTRADO","SIN DATOS", "",
                    "","","","","",""));
        }
        return new SuccessDataResult(modelo);
    }
}
