package com.bbva.dao;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

import com.bbva.database.MyBatisConnectionFactory;
import com.bbva.database.mappers.UserMapper;
import com.bbva.dto.User.Request.ValidateDtoRequest;
import com.bbva.dto.User.Response.ValidateDtoResponse;
import com.bbva.entities.User;
import com.bbva.entities.secu.RolMenu;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;

class UserDaoTest {

    private UserDao userDao;
    private SqlSessionFactory sqlSessionFactoryMock;
    private SqlSession sqlSessionMock;
    private UserMapper userMapperMock;
    private MockedStatic<MyBatisConnectionFactory> mockedFactory;


    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        sqlSessionFactoryMock = mock(SqlSessionFactory.class);
        sqlSessionMock = mock(SqlSession.class);
        userMapperMock = mock(UserMapper.class);

        mockedFactory = mockStatic(MyBatisConnectionFactory.class);
        mockedFactory.when(MyBatisConnectionFactory::getInstance).thenReturn(sqlSessionFactoryMock);

        when(sqlSessionFactoryMock.openSession()).thenReturn(sqlSessionMock);
        when(sqlSessionMock.getMapper(UserMapper.class)).thenReturn(userMapperMock);

        userDao = UserDao.getInstance();
    }

    @AfterEach
    void tearDown() {
        mockedFactory.close();
    }

    @Test
    void testGetUserSuccess() {
        String googleId = "testGoogleId";
        String email = "test@example.com";
        User user1 = new User(1,"googleId123","","userId123", "J", "googleId123", "test@example.com");
        User user2 = new User(2,"googleId1234","","userId1234", "J", "googleId1234", "test@example.com");
        List<User> expectedUsers = List.of(user1, user2);

        when(userMapperMock.getUser(googleId, email)).thenReturn(expectedUsers);
        List<User> actualUsers = userDao.getUser(googleId, email);

        assertNotNull(actualUsers);
        assertEquals(2, actualUsers.size());
        assertEquals(expectedUsers, actualUsers);

        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(UserMapper.class);
        verify(userMapperMock).getUser(googleId, email);
        verify(sqlSessionMock).close();
    }

    @Test
    void testGetUserEmptyList() {
        String googleId = "unknownId";
        String email = "unknown@example.com";

        when(userMapperMock.getUser(googleId, email)).thenReturn(Arrays.asList());

        List<User> actualUsers = userDao.getUser(googleId, email);
        assertNotNull(actualUsers);
        assertTrue(actualUsers.isEmpty());

        verify(sqlSessionFactoryMock).openSession();
        verify(sqlSessionMock).getMapper(UserMapper.class);
        verify(userMapperMock).getUser(googleId, email);
        verify(sqlSessionMock).close();
    }

    @Test
    void testValidateSuccess() {
        ValidateDtoRequest request = new ValidateDtoRequest();
        request.googleId = "googleId123";
        request.email = "test@example.com";
        request.imageUrl = "http://example.com/image.jpg";

        User mockUser = new User(1,"googleId123","","userId123", "J", "googleId123", "test@example.com");

        List<RolMenu> mockRoleMenuList = List.of(
                new RolMenu(1, 1, "Rol Consulta", "Rol Consulta", 1, "Fuentes", null, "/sources",2,83,21,"Bases únicas",null,12),
                new RolMenu(1, 1, "Rol Consulta", "Rol Consulta", 2, "Bases únicas", null, "",12,101,0,null,null,null),
                new RolMenu(1, 1, "Rol Consulta", "Rol Consulta", 24, "Data Quality Assurance", null, "",15,116,0,null,null,null),
                new RolMenu(1, 1, "Rol Consulta", "Rol Consulta", 25, "Generador de Documentación", null, "",15,114,24,"Data Quality Assurance",null,15),
                new RolMenu(1, 1, "Rol Consulta", "Rol Consulta", 27, "Documentacion de Mallas", null, "/generadordocumentos/mallas",15,109,25,"Generador de Documentación",null,15)
        );

        when(userMapperMock.getUser("googleId123", "test@example.com")).thenReturn(List.of(mockUser));
        when(userMapperMock.roleMenuList(1)).thenReturn(mockRoleMenuList);
        when(sqlSessionFactoryMock.openSession()).thenReturn(sqlSessionMock);
        when(sqlSessionMock.getMapper(UserMapper.class)).thenReturn(userMapperMock);

        ValidateDtoResponse response = userDao.validate(request);

        assertNotNull(response);

    }
}
