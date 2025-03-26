package com.bbva.entities.jiravalidator;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class JiraValidatorLogEntityTest {

    @Test
    void testJiraValidatorLogEntityBuilderAndGetters() {
        Long id = 1L;
        LocalDateTime fecha = LocalDateTime.now();
        String usuario = "testUser";
        String nombre = "Test Name";
        String ticket = "TICKET-123";
        String regla1 = "Valid";
        String regla2 = "No Valid";
        String regla3 = "Valid";
        String regla4 = "Valid";
        String regla5 = "Valid";
        String regla6 = "Valid";
        String regla7 = "Valid";
        String regla8 = "Valid";
        String regla9 = "Valid";
        String regla10 = "Valid";
        String regla11 = "Valid";
        String regla12 = "Valid";
        String regla13 = "Valid";
        String regla14 = "Valid";
        String regla15 = "Valid";
        String regla16 = "Valid";
        String regla17 = "Valid";
        String regla18 = "Valid";
        String regla19 = "Valid";
        String regla20 = "Valid";
        String regla21 = "Valid";
        String regla22 = "Valid";
        String regla23 = "Valid";
        String regla24 = "Valid";
        String regla25 = "Valid";
        String regla26 = "Valid";
        String regla27 = "Valid";
        String regla28 = "Valid";
        String regla29 = "Valid";
        String regla30 = "Valid";
        String regla31 = "Valid";
        String regla32 = "Valid";
        String regla33 = "Valid";
        String regla34 = "Valid";
        String regla35 = "Valid";
        String regla36 = "Valid";
        String regla37 = "Valid";
        String regla38 = "Valid";
        String regla39 = "Valid";
        String regla40 = "Valid";

        JiraValidatorLogEntity entity = JiraValidatorLogEntity.builder()
                .id(id)
                .fecha(fecha)
                .usuario(usuario)
                .nombre(nombre)
                .ticket(ticket)
                .regla1(regla1)
                .regla2(regla2)
                .regla3(regla3)
                .regla4(regla4)
                .regla5(regla5)
                .regla6(regla6)
                .regla7(regla7)
                .regla8(regla8)
                .regla9(regla9)
                .regla10(regla10)
                .regla11(regla11)
                .regla12(regla12)
                .regla13(regla13)
                .regla14(regla14)
                .regla15(regla15)
                .regla16(regla16)
                .regla17(regla17)
                .regla18(regla18)
                .regla19(regla19)
                .regla20(regla20)
                .regla21(regla21)
                .regla22(regla22)
                .regla23(regla23)
                .regla24(regla24)
                .regla25(regla25)
                .regla26(regla26)
                .regla27(regla27)
                .regla28(regla28)
                .regla29(regla29)
                .regla30(regla30)
                .regla31(regla31)
                .regla32(regla32)
                .regla33(regla33)
                .regla34(regla34)
                .regla35(regla35)
                .regla36(regla36)
                .regla37(regla37)
                .regla38(regla38)
                .regla39(regla39)
                .regla40(regla40)
                .build();

        assertNotNull(entity);
        assertEquals(id, entity.getId());
        assertEquals(fecha, entity.getFecha());
        assertEquals(usuario, entity.getUsuario());
        assertEquals(nombre, entity.getNombre());
        assertEquals(ticket, entity.getTicket());
        assertEquals(regla1, entity.getRegla1());
        assertEquals(regla2, entity.getRegla2());
        assertEquals(regla3, entity.getRegla3());
        assertEquals(regla4, entity.getRegla4());
        assertEquals(regla5, entity.getRegla5());
        assertEquals(regla6, entity.getRegla6());
        assertEquals(regla7, entity.getRegla7());
        assertEquals(regla8, entity.getRegla8());
        assertEquals(regla9, entity.getRegla9());
        assertEquals(regla10, entity.getRegla10());
        assertEquals(regla11, entity.getRegla11());
        assertEquals(regla12, entity.getRegla12());
        assertEquals(regla13, entity.getRegla13());
        assertEquals(regla14, entity.getRegla14());
        assertEquals(regla15, entity.getRegla15());
        assertEquals(regla16, entity.getRegla16());
        assertEquals(regla17, entity.getRegla17());
        assertEquals(regla18, entity.getRegla18());
        assertEquals(regla19, entity.getRegla19());
        assertEquals(regla20, entity.getRegla20());
        assertEquals(regla21, entity.getRegla21());
        assertEquals(regla22, entity.getRegla22());
        assertEquals(regla23, entity.getRegla23());
        assertEquals(regla24, entity.getRegla24());
        assertEquals(regla25, entity.getRegla25());
        assertEquals(regla26, entity.getRegla26());
        assertEquals(regla27, entity.getRegla27());
        assertEquals(regla28, entity.getRegla28());
        assertEquals(regla29, entity.getRegla29());
        assertEquals(regla30, entity.getRegla30());
        assertEquals(regla31, entity.getRegla31());
        assertEquals(regla32, entity.getRegla32());
        assertEquals(regla33, entity.getRegla33());
        assertEquals(regla34, entity.getRegla34());
        assertEquals(regla35, entity.getRegla35());
        assertEquals(regla36, entity.getRegla36());
        assertEquals(regla37, entity.getRegla37());
        assertEquals(regla38, entity.getRegla38());
        assertEquals(regla39, entity.getRegla39());
        assertEquals(regla40, entity.getRegla40());
    }

    @Test
    void testSetters() {
        JiraValidatorLogEntity entity = new JiraValidatorLogEntity(
                2L, LocalDateTime.now(), "initialUser", "Initial Name", "TICKET-456",
                "Valid", "Invalid", "Valid", "Valid", null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,null
        );

        String updatedUser = "updatedUser";
        entity.setUsuario(updatedUser);

        assertEquals(updatedUser, entity.getUsuario());
    }
}
