package com.bbva.dao;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.UserMapper;
import com.bbva.dto.User.Request.PaginationDtoRequest;
import com.bbva.dto.User.Request.ValidateDtoRequest;
import com.bbva.dto.User.Response.ValidateDtoResponse;
import com.bbva.dto.User.Response.ValidateRoleDtoResponse;
import com.bbva.dto.User.Response.ValidateRoleMenuDtoResponse;
import com.bbva.dto.User.Response.ValidateUserDtoResponse;
import com.bbva.dto.template.response.TemplatePaginationResponse;
import com.bbva.entities.User;
import com.bbva.entities.secu.RolMenu;
import com.bbva.entities.secu.RolMenuAction;
import com.bbva.entities.secu.UserRole;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;


public class UserDao {
    private static final Logger LOGGER = Logger.getLogger(UserDao.class.getName());

    private static UserDao instance = null;

    public static synchronized UserDao getInstance() {
        if (Objects.isNull(instance)) {
            instance = new UserDao();
        }

        return instance;
    }

    
    public List<User> list() {
        List<User> userList = null;
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            userList = mapper.list();
        }
        return userList;
    }
    
    public List<User> listPaginated(PaginationDtoRequest dto) {
        List<User> userList = null;
        // try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                UserMapper mapper = session.getMapper(UserMapper.class);
                userList = mapper.listarPaginado(dto);
            }
       // } catch (Exception e) {
       //     LOGGER.log(Level.SEVERE, e.getMessage(), e);
       // }
        return userList;
    }

    public int countAllPaginated(PaginationDtoRequest dto) {
        int countTotal = 0;
        // try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                UserMapper mapper = session.getMapper(UserMapper.class);
                countTotal = mapper.contarTotalPaginado(dto);
            }
       // } catch (Exception e) {
       //     LOGGER.log(Level.SEVERE, e.getMessage(), e);
       // }
        return countTotal;
    }

    public List<RolMenuAction> listPermissions(int roleId) {
        List<RolMenuAction> permissionsList = null;
        // try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                UserMapper mapper = session.getMapper(UserMapper.class);
                permissionsList = mapper.listPermissions(roleId);
            }
       // } catch (Exception e) {
       //     LOGGER.log(Level.SEVERE, e.getMessage(), e);
       // }
        return permissionsList;
    }

    public List<User> listByEmail(String email) {
        List<User> userList = null;
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            userList = mapper.listByEmail(email);
        }
        return userList;
    }

    public List<User> findByEmployeeId(String employeeId) {
        List<User> userList = null;
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            userList = mapper.findByEmployeeID(employeeId.trim());
        }
        return userList;
    }

    public List<User> findByUserId(int[] arrayUserId) {
        List<User> userList = null;
        SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
        try (SqlSession session = sqlSessionFactory.openSession()) {
            UserMapper mapper = session.getMapper(UserMapper.class);
            userList = mapper.findByUserID(arrayUserId);
        }
        return userList;
    }

    public boolean insertUser(User user) {

        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                UserMapper mapper = session.getMapper(UserMapper.class);
                mapper.insertUser(user);
                session.commit();
                return true;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    public boolean updateUserEmployeeId(User user) {

        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                UserMapper mapper = session.getMapper(UserMapper.class);
                mapper.updateUserEmployeeId(user);
                session.commit();
                return true;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    public boolean deleteUser(int id) {
        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                UserMapper mapper = session.getMapper(UserMapper.class);
                mapper.deleteUser(id);
                session.commit();
                return true;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    public boolean deleteRoles(int id) {
        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                UserMapper mapper = session.getMapper(UserMapper.class);
                mapper.deleteRoles(id);
                session.commit();
                return true;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    public boolean insertRoles(UserRole userRole) {

        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                UserMapper mapper = session.getMapper(UserMapper.class);
                mapper.insertRoles(userRole);
                session.commit();
                return true;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            return false;
        }
    }

    public ValidateDtoResponse validate(ValidateDtoRequest dto) {
        ValidateDtoResponse response = new ValidateDtoResponse();
        List<ValidateRoleDtoResponse> lista = new ArrayList<>();
        User userResponse = null;
        try {
            SqlSessionFactory sqlSessionFactory = MyBatisConnectionFactory.getInstance();
            try (SqlSession session = sqlSessionFactory.openSession()) {
                UserMapper mapper = session.getMapper(UserMapper.class);
                userResponse = mapper.listByEmail(dto.email).stream()
                        .findFirst().orElse(null);
   
                response.User = new ValidateUserDtoResponse(userResponse.getUserId(), userResponse.getGoogleId(),
                        userResponse.getFullName(), userResponse.getEmail(), dto.imageUrl);

                List<RolMenu> joinRoleMenu = mapper.roleMenuList(userResponse.getUserId());
                List<Integer> roles = joinRoleMenu.stream()
                        .map(rl ->rl.getRoleId()).distinct()
                        .collect(Collectors.toList());

                roles.forEach(r -> {
                    ValidateRoleDtoResponse role = new ValidateRoleDtoResponse();
                    var menuList = joinRoleMenu.stream()
                            .filter(jr -> jr.getRoleId() == r)
                            .collect(Collectors.toList());

                    var groupMenuParents  = menuList.stream()
                            .filter(f->f.getMenuParent()>0)
                            .collect(Collectors.groupingBy(p -> Arrays.asList(p.getMenuParent(), p.getMenuParentDesc(), p.getMenuIconParent(), p.getMenuOrderParent())));

                    var groupMenuUniques  = menuList.stream()
                            .filter(f->f.getMenuParent()==0)
                            .map( rs -> new ValidateRoleMenuDtoResponse(rs.getMenuId(),rs.getMenuDesc(),rs.getMenuIcon(),rs.getMenuUrl(),rs.getMenuOrder(),null))
                            .collect(Collectors.toList());

                    var menus = groupMenuParents.entrySet().stream()
                            .map(w -> {
                                var keys= w.getKey();
                                var options = w.getValue().stream()
                                        .map(d-> new ValidateRoleMenuDtoResponse(d.getMenuId(),d.getMenuDesc(),d.getMenuIcon(), d.getMenuUrl(), d.getMenuOrder(), null))
                                        .collect(Collectors.toList());
                                return new ValidateRoleMenuDtoResponse((Integer) keys.get(0), (String) keys.get(1), (String) keys.get(2),"",(Integer)keys.get(3),options);
                            }).collect(Collectors.toList());

                    role.Role = menuList.stream().map(ml ->ml.getRoleName())
                            .findFirst().orElse(null);

                    menus.addAll(groupMenuUniques);

                    role.Menus = menus.stream().sorted(Comparator.comparing(ValidateRoleMenuDtoResponse::getOrder))
                            .collect(Collectors.toList());
                    role.setRoleId(r);

                    lista.add(role);
                });

                response.Roles = lista;
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
        }
        return response;
    }



}
