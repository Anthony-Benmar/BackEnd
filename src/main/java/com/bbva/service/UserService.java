package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.UserDao;
import com.bbva.dto.User.Request.PaginationDtoRequest;
import com.bbva.dto.User.Request.UpdateRolesRequest;
import com.bbva.dto.User.Response.PaginationDataResponse;
import com.bbva.dto.User.Response.PaginationResponse;
import com.bbva.entities.User;
import com.bbva.entities.secu.UserRole;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UserService {
    
    private static final Logger LOGGER = Logger.getLogger(UserService.class.getName());

    private final UserDao userDao = new UserDao();

    public IDataResult findByEmployeeId(String employeeId) {
        var result = userDao.findByEmployeeId(employeeId);
        return new SuccessDataResult(result);
    }

    public IDataResult<PaginationResponse> pagination(PaginationDtoRequest dto) {
        PaginationResponse response = new PaginationResponse();
        response.setCount(UserDao.getInstance().countAllPaginated(dto));
        response.setPages_amount((int) Math.ceil(response.getCount().floatValue() / dto.getRecords_amount().floatValue()));
        dto.setOffset(dto.getRecords_amount()*(dto.getPage() - 1));
        response.setData(UserDao.getInstance().listPaginated(dto).stream().map(user -> PaginationDataResponse.builder()
                                                                    .id(user.getUserId())
                                                                    .registro(user.getEmployeeId())
                                                                    .nombreCompleto(user.getFullName())
                                                                    .email(user.getEmail())
                                                                    .fechaHoraAlta(user.getOperationDate() != null ? 
                                                                                    new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(user.getOperationDate()) : null)
                                                                    .roles(user.getRolesNombre())
                                                                    .idsRoles(StringUtils.isNotEmpty(user.getRolesId()) ? Stream.of(user.getRolesId().split(","))
                                                                                    .map(String::trim)
                                                                                    .map(Integer::parseInt)
                                                                                    .collect(Collectors.toList()) : null)
                                                                    .build()).collect(Collectors.toList()));
        return new SuccessDataResult(response);
    }

    public void updateRoles(UpdateRolesRequest request){
        UserDao.getInstance().deleteRoles(request.getIdUser());
        if(request.getRoles() != null && request.getRoles().size() > 0){
            request.getRoles().stream().forEach(rol -> {
                UserRole newUserRole = new UserRole(0, request.getIdUser(), rol, 1, 1);
                UserDao.getInstance().insertRoles(newUserRole);
            });            
        }
        
    }
}
