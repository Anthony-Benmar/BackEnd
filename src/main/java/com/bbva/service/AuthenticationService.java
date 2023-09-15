package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.ErrorDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.UserDao;
import com.bbva.dto.User.Request.ValidateDtoRequest;
import com.bbva.dto.User.Response.ValidateDtoResponse;
import com.bbva.entities.User;
import com.bbva.entities.secu.RolMenuAction;
import com.bbva.entities.secu.UserRole;
import net.minidev.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.logging.Logger;

public class AuthenticationService {

    private static final Logger LOGGER = Logger.getLogger(AuthenticationService.class.getName());

    private final UserDao userDao = new UserDao();

    public IDataResult<ValidateDtoResponse> Validate(ValidateDtoRequest dto) {

        List<User> lstUsers = userDao.listByEmail(dto.email);
        if (lstUsers.size() == 0) {
            User newUser = new User(0,dto.googleId,dto.name,dto.email, dto.employeeId, null, null);
            newUser.setStatusType(1);
            newUser.setOperationUser(1);
            userDao.insertUser(newUser);
            UserRole newUserRole = new UserRole(0,newUser.getUserId(),1,newUser.getStatusType(), newUser.getOperationUser());
            userDao.insertRoles(newUserRole);
        } else {           
            User updUser = lstUsers.get(0);
            if(StringUtils.isEmpty(updUser.getEmployeeId())){
                updUser.setEmployeeId(dto.getEmployeeId());
                userDao.updateUserEmployeeId(updUser);
            }            
        }
        var userValidate = userDao.validate(dto);
        if (userValidate ==  null) {
            return new ErrorDataResult(userValidate,"401","Usuario no encontrado");
        }
        var result = new SuccessDataResult(userValidate, "Succesfull");
        return result;
    }

    public JSONObject permissions(ValidateDtoRequest dto){

        List<RolMenuAction> listRoleMenuAction = userDao.listPermissions(dto.getRoleId());
        JSONObject objResultado = new JSONObject(); 
        JSONObject objData = new JSONObject();        
        JSONObject objPermisos = new JSONObject();
        objData.appendField("permisos", objPermisos);
        JSONObject objMenu = null;
        for(RolMenuAction rolMenuAction : listRoleMenuAction){
            if(!objPermisos.containsKey(rolMenuAction.getMenuPropiedad())){
                objMenu = new JSONObject();
                objPermisos.appendField(rolMenuAction.getMenuPropiedad(), objMenu);
            } else{
                objMenu = (JSONObject) objPermisos.get(rolMenuAction.getMenuPropiedad());
            }

            if(objMenu != null){
                JSONObject objPermitido = new JSONObject();
                objPermitido.appendField("autorizado", rolMenuAction.isAutorizado());
                objMenu.appendField(rolMenuAction.getAccionPropiedad(), objPermitido);
            }            
        }
        objResultado.appendField("data", objData);
        objResultado.appendField("success", true);
        return objResultado;
    }
}
