package com.bbva.service;

import com.bbva.core.abstracts.IDataResult;
import com.bbva.core.results.SuccessDataResult;
import com.bbva.dao.AccionDao;
import com.bbva.dao.MenuDao;
import com.bbva.dao.RolDao;
import com.bbva.dto.rol.request.RolDataAccionPermissionRequest;
import com.bbva.dto.rol.request.RolDataDetailPermissionsRequest;
import com.bbva.dto.rol.request.RolDataRequest;
import com.bbva.dto.rol.response.AccionDataResponse;
import com.bbva.dto.rol.response.MenuDataResponse;
import com.bbva.dto.rol.response.RolDataDetailPermissionsResponse;
import com.bbva.dto.rol.response.RolDataResponse;
import com.bbva.entities.secu.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class RolService {
    
    private static final Logger LOGGER = Logger.getLogger(RolService.class.getName());

    public IDataResult<List<RolDataResponse>> list(){

        List<RolDataResponse> response = RolDao.getInstance().list().stream().map(rol -> 
                                            RolDataResponse.builder()
                                                    .idRole(rol.getIdRole())
                                                    .nombre(rol.getNombre())
                                                    .descripcion(rol.getDescripcion()).build()).collect(Collectors.toList());

        return new SuccessDataResult(response);

    }

    public IDataResult<RolDataDetailPermissionsResponse> listDetailPermissions(RolDataDetailPermissionsRequest request){

        List<Menu> listaMenu = MenuDao.getInstance().list();
        List<Accion> listaAccion = AccionDao.getInstance().list();

        RolDataDetailPermissionsResponse response = new RolDataDetailPermissionsResponse();
        response.setMenu(new ArrayList<>());
        MenuDataResponse menuDataResponse = null;
        AccionDataResponse accionDataResponse = null;
        for(Menu menu : listaMenu){
            menuDataResponse = new MenuDataResponse();
            menuDataResponse.setIdMenu(menu.getMenuId());
            menuDataResponse.setNombre(menu.getMenuDesc());
            if(listaAccion != null && !listaAccion.isEmpty()){
                menuDataResponse.setAcciones(new ArrayList<>());
                for(Accion accion : listaAccion){
                    accionDataResponse = new AccionDataResponse();
                    accionDataResponse.setIdCompuesto(String.format("%s-%s", menu.getMenuId(), accion.getAccionId()));
                    accionDataResponse.setNombre(accion.getAccionDesc());
                    menuDataResponse.getAcciones().add(accionDataResponse);
                }
            }
            response.getMenu().add(menuDataResponse);
        }

        if(request.getIdRole() != null && request.getIdRole() > 0){
            List<Menu> listaMenuSeleccionado = RolDao.getInstance().listIdsMenu(request.getIdRole());
            if(listaMenuSeleccionado != null && !listaMenuSeleccionado.isEmpty()){
                response.setMenuSeleccionado(new ArrayList<>());
                for(Menu menu : listaMenuSeleccionado){          
                    response.getMenuSeleccionado().add(menu.getMenuId());
                }
            }
            
            List<Accion> listaAccionSeleccionado = RolDao.getInstance().listIdsMenuAccion(request.getIdRole());
            if(listaAccionSeleccionado != null && !listaAccionSeleccionado.isEmpty()){
                response.setAccionSeleccionado(new ArrayList<>());
                for(Accion accion : listaAccionSeleccionado){         
                    response.getAccionSeleccionado().add(String.format("%s-%s", accion.getMenuId(), accion.getAccionId()));
                }
            }
        }
        return new SuccessDataResult(response);
    }

    public IDataResult<RolDataResponse> registrar(RolDataRequest request){

        Rol newRol = Rol.builder().nombre(request.getNombre()).descripcion(request.getDescripcion()).operationUser(1).build();
        RolDao.getInstance().insertRol(newRol);

        this.asignarMenuAcciones(newRol, request);
        
        return new SuccessDataResult(RolDataResponse.builder().idRole(newRol.getIdRole()).nombre(request.getNombre()).descripcion(request.getDescripcion()).build());
    }

    public IDataResult<RolDataResponse> actualizar(RolDataRequest request){

        Rol updRol = Rol.builder().idRole(request.getIdRole()).nombre(request.getNombre()).descripcion(request.getDescripcion()).build();
        RolDao.getInstance().updateRol(updRol);
        RolDao.getInstance().deleteAcciones(updRol);
        RolDao.getInstance().deleteMenus(updRol);
        
        this.asignarMenuAcciones(updRol, request);

        return new SuccessDataResult(RolDataResponse.builder().idRole(updRol.getIdRole()).nombre(request.getNombre()).descripcion(request.getDescripcion()).build());
    }

    private void asignarMenuAcciones(Rol rol, RolDataRequest request){
        if(request.getIdsMenu() != null && !request.getIdsMenu().isEmpty()){
            for(Integer idMenu : request.getIdsMenu()){
                RolMenu rolMenu = RolMenu.builder().roleId(rol.getIdRole()).menuId(idMenu).build();
                RolDao.getInstance().insertRolMenu(rolMenu);
                if(request.getIdsAcciones() != null && !request.getIdsAcciones().isEmpty()){
                    List<RolDataAccionPermissionRequest> listaMenuAcciones =  request.getIdsAcciones().stream().filter(ma -> ma.getIdMenu() == idMenu).collect(Collectors.toList());
                    if(!listaMenuAcciones.isEmpty()){
                        for(RolDataAccionPermissionRequest rolDataAccion : listaMenuAcciones){
                            RolDao.getInstance().insertRolMenuAccion(RolMenuAction.builder().rolMenuId(rolMenu.getRoleMenuId()).actionId(rolDataAccion.getIdAccion()).build());
                        }
                    }
                }
                
            }
        }
    }

}
