package com.bbva.util.policy;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static com.bbva.util.policy.TransferStatusPolicy.*;
import static org.junit.jupiter.api.Assertions.*;

class TransferStatusPolicyTest {


    @Test
    void canEdit_SM_enDevueltos_es1() {
        assertEquals(1, canEdit("SM", DEVUELTO_PO));
        assertEquals(1, canEdit("sm", DEVUELTO_RLB)); // case-insensitive
    }

    @Test
    void canEdit_SM_enOtrosEstados_es0() {
        assertEquals(0, canEdit("SM", APROBADO_RLB));
        assertEquals(0, canEdit("SM", APROBADO_PO));
        assertEquals(0, canEdit("SM", EN_PROGRESO));
        assertEquals(0, canEdit("SM", DESESTIMADO));
    }

    @Test
    void canEdit_KM_siempre0() {
        assertEquals(0, canEdit("KM", DEVUELTO_PO));
        assertEquals(0, canEdit("km", DEVUELTO_RLB));
        assertEquals(0, canEdit("KM", APROBADO_PO));
    }

    @Test
    void canEdit_statusNull_trataComo0() {
        assertEquals(0, canEdit("SM", null));
        assertEquals(0, canEdit("KM", null));
    }


    @Test
    void canEditComments_KM_enAprobadoPO_es1() {
        assertEquals(1, canEditComments("KM", APROBADO_PO));
        assertEquals(1, canEditComments("km", APROBADO_PO));
    }

    @Test
    void canEditComments_KM_enOtrosEstados_es0() {
        assertEquals(0, canEditComments("KM", APROBADO_RLB));
        assertEquals(0, canEditComments("KM", EN_PROGRESO));
        assertEquals(0, canEditComments("KM", DEVUELTO_PO));
        assertEquals(0, canEditComments("KM", DEVUELTO_RLB));
        assertEquals(0, canEditComments("KM", DESESTIMADO));
    }

    @Test
    void canEditComments_SM_siempre0() {
        assertEquals(0, canEditComments("SM", APROBADO_PO));
        assertEquals(0, canEditComments("sm", APROBADO_PO));
    }

    @Test
    void canEditComments_statusNull_es0() {
        assertEquals(0, canEditComments("KM", null));
    }


    @Test
    void toCsv_defaults_rolYTabNull_fallback_KM_EN_PROGRESO() {
        assertEquals("2,5", toCsv(null, null)); // KM + EN_PROGRESO => 2,5
    }

    @Test
    void toCsv_rolKM_tabs() {
        assertEquals("2,5", toCsv("KM", "EN_PROGRESO"));
        assertEquals("1",   toCsv("KM", "APROBADOS"));
    }

    @Test
    void toCsv_rolSM_tabs() {
        assertEquals("3,2,4,5", toCsv("SM", "EN_PROGRESO"));
        assertEquals("1",       toCsv("SM", "APROBADOS"));
    }

    @Test
    void toCsv_rolDesconocido_fallbackKM() {
        assertEquals("2,5", toCsv("???", "EN_PROGRESO"));
    }

    @Test
    void toCsv_tabDesconocida_fallback_EN_PROGRESO() {
        assertEquals("2,5", toCsv("KM", "NO_EXISTE"));
        assertEquals("3,2,4,5", toCsv("SM", "   ")); // blank -> EN_PROGRESO
    }

    @Test
    void toCsv_caseInsensitive_y_trim() {
        assertEquals("1", toCsv("  km ", " aprobados "));
        assertEquals("3,2,4,5", toCsv(" sm ", " en_progreso "));
    }

    @Test
    void allowed_KM_enAprobadoPO_approve_y_return() {
        Set<Action> a = allowedActions("KM", APROBADO_PO);
        assertTrue(a.contains(Action.APPROVE));
        assertTrue(a.contains(Action.RETURN));
        assertFalse(a.contains(Action.RESEND));
    }

    @Test
    void allowed_SM_enProgreso_approve_y_return() {
        Set<Action> a = allowedActions("SM", EN_PROGRESO);
        assertTrue(a.contains(Action.APPROVE));
        assertTrue(a.contains(Action.RETURN));
        assertFalse(a.contains(Action.RESEND));
    }

    @Test
    void allowed_SM_enDevueltos_resend() {
        assertTrue(allowedActions("SM", DEVUELTO_PO).contains(Action.RESEND));
        assertTrue(allowedActions("SM", DEVUELTO_RLB).contains(Action.RESEND));
    }

    @Test
    void allowed_roleNull_tratadoComoSM() {
        // Por implementación: !KM => SM/PO
        assertTrue(allowedActions(null, EN_PROGRESO).contains(Action.APPROVE));
    }


    @Test
    void compute_KM_APROBADO_PO_APPROVE_va_A_APROBADO_RLB() {
        assertEquals(APROBADO_RLB, computeNextStatusOrThrow("KM", APROBADO_PO, Action.APPROVE));
    }

    @Test
    void compute_KM_APROBADO_PO_RETURN_va_A_DEVUELTO_RLB() {
        assertEquals(DEVUELTO_RLB, computeNextStatusOrThrow("KM", APROBADO_PO, Action.RETURN));
    }

    @Test
    void compute_SM_EN_PROGRESO_APPROVE_va_A_APROBADO_PO() {
        assertEquals(APROBADO_PO, computeNextStatusOrThrow("SM", EN_PROGRESO, Action.APPROVE));
    }

    @Test
    void compute_SM_EN_PROGRESO_RETURN_va_A_DEVUELTO_PO() {
        assertEquals(DEVUELTO_PO, computeNextStatusOrThrow("SM", EN_PROGRESO, Action.RETURN));
    }

    @Test
    void compute_SM_DEVUELTO_PO_RESEND_va_A_EN_PROGRESO() {
        assertEquals(EN_PROGRESO, computeNextStatusOrThrow("SM", DEVUELTO_PO, Action.RESEND));
    }

    @Test
    void compute_SM_DEVUELTO_RLB_RESEND_va_A_EN_PROGRESO() {
        assertEquals(EN_PROGRESO, computeNextStatusOrThrow("SM", DEVUELTO_RLB, Action.RESEND));
    }

    @Test
    void compute_transicionInvalida_lanzaIllegalArgument() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> computeNextStatusOrThrow("KM", EN_PROGRESO, Action.APPROVE));
        assertTrue(ex.getMessage().contains("Transición no permitida"));
    }

    @Test
    void canWriteJobComment_SM_permitido_enEnProgreso_y_Devueltos() {
        assertEquals(1, canWriteJobComment("SM", EN_PROGRESO));
        assertEquals(1, canWriteJobComment("sm", DEVUELTO_PO));   // case-insensitive
        assertEquals(1, canWriteJobComment("SM", DEVUELTO_RLB));
    }

    @Test
    void canWriteJobComment_SM_noPermitido_enAprobados_oDesestimado() {
        assertEquals(0, canWriteJobComment("SM", APROBADO_PO));
        assertEquals(0, canWriteJobComment("SM", APROBADO_RLB));
        assertEquals(0, canWriteJobComment("SM", DESESTIMADO));
    }

    @Test
    void canWriteJobComment_noSM_siempre0() {
        assertEquals(0, canWriteJobComment("KM", EN_PROGRESO));
        assertEquals(0, canWriteJobComment("PO", DEVUELTO_PO));
        assertEquals(0, canWriteJobComment("otro", DEVUELTO_RLB));
    }

    @Test
    void canWriteJobComment_statusNull_es0() {
        assertEquals(0, canWriteJobComment("SM", null));
    }
}
