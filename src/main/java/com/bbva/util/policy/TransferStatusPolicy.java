package com.bbva.util.policy;

import java.util.*;

public class TransferStatusPolicy {

    public static final int APROBADO_RLB = 1;
    public static final int APROBADO_PO  = 2;
    public static final int EN_PROGRESO  = 3;
    public static final int DEVUELTO_PO  = 4;
    public static final int DEVUELTO_RLB = 5;
    public static final int DESESTIMADO  = 6;

    private static final String ROLE_KM = "KM";
    private static final String ROLE_SM = "SM";

    private static final String TAB_EN_PROGRESO = "EN_PROGRESO";
    private static final String TAB_APROBADOS   = "APROBADOS";

    private TransferStatusPolicy() {}

    private static String norm(String role){
        return role == null ? "" : role.trim().toUpperCase(Locale.ROOT);
    }
    private static boolean isKM(String role){ return ROLE_KM.equals(norm(role)); }

    private static boolean isSM(String role){ return !isKM(role); }

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
        km.put(TAB_EN_PROGRESO, "2,5");
        km.put(TAB_APROBADOS,   "1");
        MATRIX.put(ROLE_KM, km);

        var sm = new HashMap<String, String>();
        sm.put(TAB_EN_PROGRESO, "3,2,4,5");
        sm.put(TAB_APROBADOS,   "1");
        MATRIX.put(ROLE_SM, sm);
    }

    public static String toCsv(String role, String tab) {
        String r = (role == null || role.isBlank()) ? ROLE_KM : role.trim().toUpperCase(Locale.ROOT);
        String t = (tab  == null || tab.isBlank())  ? TAB_EN_PROGRESO : tab.trim().toUpperCase(Locale.ROOT);
        Map<String, String> perRole = MATRIX.getOrDefault(r, MATRIX.get(ROLE_KM));
        return perRole.getOrDefault(t, perRole.get(TAB_EN_PROGRESO));
    }

    public enum Action { APPROVE, RETURN, RESEND }

    public static Set<Action> allowedActions(String actorRole, int currentStatus){
        String r = actorRole == null ? "" : actorRole.trim().toUpperCase(Locale.ROOT);
        Set<Action> res = EnumSet.noneOf(Action.class);

        if (ROLE_KM.equals(r)) {
            if (currentStatus == APROBADO_PO) {
                res.add(Action.APPROVE); // -> 1
                res.add(Action.RETURN);  // -> 5
            }
        } else { // Rol Consulta = SM/PO
            if (currentStatus == EN_PROGRESO) {
                res.add(Action.APPROVE); // -> 2
                res.add(Action.RETURN);  // -> 4
            }
            if (currentStatus == DEVUELTO_PO || currentStatus == DEVUELTO_RLB) {
                res.add(Action.RESEND);  // -> 3
            }
        }
        return res;
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

        if (status == APROBADO_PO) {
            if (action == Action.APPROVE) return APROBADO_RLB;
            if (action == Action.RETURN)  return DEVUELTO_RLB;
        }
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
}