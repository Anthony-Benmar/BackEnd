package com.bbva.util.policy;

import com.bbva.dto.reliability.response.ReliabilityPacksDtoResponse;

import java.util.*;
import java.util.function.Predicate;

public class TransferStatusPolicy {

    public static final int APROBADO_RLB = 1;
    public static final int APROBADO_PO  = 2;
    public static final int EN_PROGRESO  = 3;
    public static final int DEVUELTO_PO  = 4;
    public static final int DEVUELTO_RLB = 5;
    public static final int DESESTIMADO  = 6;

    private static final String ROLE_KM = "KM";
    private static final String ROLE_SM = "SM";
    private static final String ROLE_PO = "PO";

    private static final String TAB_EN_PROGRESO = "EN_PROGRESO";
    private static final String TAB_APROBADOS   = "APROBADOS";

    private static String norm(String role){
        return role == null ? "" : role.trim().toUpperCase(Locale.ROOT);
    }
    private static boolean isKM(String role){ return ROLE_KM.equals(norm(role)); }
    private static boolean isSM(String role){ return ROLE_SM.equals(norm(role)); }

    public static int canEdit(String role, Integer statusId){
        int st = statusId == null ? 0 : statusId;
        return (isSM(role) && (st == DEVUELTO_PO || st == DEVUELTO_RLB)) ? 1 : 0;
    }

    public static int canEditComments(String role, Integer statusId){
        int st = statusId == null ? 0 : statusId;
        return (isKM(role) && st == APROBADO_PO) ? 1 : 0;
    }

    private static final Map<String, Map<String, String>> MATRIX = new HashMap<>();
    static {
        var km = new HashMap<String, String>();
        km.put(TAB_EN_PROGRESO, "2,5,6");
        km.put(TAB_APROBADOS,   "1");
        MATRIX.put(ROLE_KM, km);

        var sm = new HashMap<String, String>();
        sm.put(TAB_EN_PROGRESO, "3,2,4,5");
        sm.put(TAB_APROBADOS,   "1");
        MATRIX.put(ROLE_SM, sm);

        var po = new HashMap<String, String>();
        po.put(TAB_EN_PROGRESO, "3,2,4");
        po.put(TAB_APROBADOS,   "1");
        MATRIX.put(ROLE_PO, po);
    }

    public static String toCsv(String role, String tab) {
        String r = (role == null || role.isBlank()) ? ROLE_KM : role.trim().toUpperCase(Locale.ROOT);
        String t = (tab  == null || tab.isBlank())  ? TAB_EN_PROGRESO : tab.trim().toUpperCase(Locale.ROOT);
        Map<String, String> perRole = MATRIX.getOrDefault(r, MATRIX.get(ROLE_KM));
        return perRole.getOrDefault(t, perRole.get(TAB_EN_PROGRESO));
    }

    public enum Action { APPROVE, RETURN, RESEND, DESESTIMAR }

    public static Set<Action> allowedActions(String actorRole, int currentStatus){
        String r = actorRole == null ? "" : actorRole.trim().toUpperCase(Locale.ROOT);
        Set<Action> res = EnumSet.noneOf(Action.class);

        if (ROLE_KM.equals(r)) {
            if (currentStatus == APROBADO_PO) {
                res.add(Action.APPROVE);
                res.add(Action.RETURN);
                res.add(Action.DESESTIMAR);
            }
            if (currentStatus == DESESTIMADO) {
                res.add(Action.RETURN);
            }
        } else { // Rol Consulta = SM/PO
            if (currentStatus == EN_PROGRESO) {
                res.add(Action.APPROVE);
                res.add(Action.RETURN);
            }
            if (currentStatus == DEVUELTO_PO || currentStatus == DEVUELTO_RLB) {
                res.add(Action.RESEND);
            }
        }
        return res;
    }

    public static int canWriteJobComment(String role, Integer statusId){
        int st = statusId == null ? 0 : statusId;
        return isSM(role) && (st == EN_PROGRESO || st == DEVUELTO_PO || st == DEVUELTO_RLB) ? 1 : 0;
    }

    public static int computeNextStatusOrThrow(String actorRole, int currentStatus, Action action) {
        ensureAllowed(actorRole, currentStatus, action);
        return isKM(actorRole)
                ? nextForKm(currentStatus, action)
                : nextForSm(currentStatus, action);
    }

    private static void ensureAllowed(String actorRole, int currentStatus, Action action) {
        Set<Action> allowed = allowedActions(actorRole, currentStatus);
        if (!allowed.contains(action)) {
            throw new IllegalArgumentException(
                    "Transición no permitida para rol=" + actorRole +
                            " desde estado=" + currentStatus + " con acción=" + action);
        }
    }

    private static int nextForKm(int status, Action action) {
        if (status == APROBADO_PO && action == Action.APPROVE)     return APROBADO_RLB;
        if (status == APROBADO_PO && action == Action.RETURN)      return DEVUELTO_RLB;
        if (status == APROBADO_PO && action == Action.DESESTIMAR)  return DESESTIMADO;

        if (status == DESESTIMADO && action == Action.RETURN)      return DEVUELTO_RLB;

        throw new IllegalStateException("No se pudo calcular la transición");
    }

    private static int nextForSm(int status, Action action) {
        if (status == EN_PROGRESO) {
            if (action == Action.APPROVE) return APROBADO_PO;
            if (action == Action.RETURN)  return DEVUELTO_PO;
        }
        if ((status == DEVUELTO_PO || status == DEVUELTO_RLB) && action == Action.RESEND) {
            return EN_PROGRESO;
        }
        throw new IllegalStateException("No se pudo calcular la transición");
    }

    public static int canWriteGeneralComment(String role, Integer statusId) {
        int st = statusId == null ? 0 : statusId;
        if (isKM(role) && st == APROBADO_PO) return 1;
        if (isSM(role) && st == EN_PROGRESO) return 1;
        return 0;
    }

    private static String lower(String s) { return (s == null) ? "" : s.trim().toLowerCase(Locale.ROOT); }
    private static String upper(String s) { return (s == null) ? "" : s.trim().toUpperCase(Locale.ROOT); }

    public static Predicate<ReliabilityPacksDtoResponse> buildPacksFilter(
            String role,
            String emailLower,
            Set<String> kmAllowed) {

        final String r = upper(role);
        final String e = lower(emailLower);
        final Set<String> allowed = (kmAllowed == null) ? Collections.emptySet() : kmAllowed;

        switch (r) {
            case ROLE_KM:
                return row -> allowed.contains(row.getDomainName());
            case ROLE_PO:
                if (e.isEmpty()) return row -> false;
                return row -> e.equals(lower(row.getProductOwnerEmail()));
            case ROLE_SM:
                if (e.isEmpty()) return row -> false;
                return row -> e.equals(lower(row.getCreatorUser()));
            default:
                return row -> true;
        }
    }

    public static int computeCambieditFlag(boolean readOnly, String role, Integer statusId) {
        return readOnly ? 0 : canEdit(role, statusId);
    }
}