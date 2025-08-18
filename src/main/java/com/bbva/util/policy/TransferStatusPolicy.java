package com.bbva.util.policy;

import java.util.*;

public class TransferStatusPolicy {
    public static final int APROBADO_RLB = 1;
    public static final int APROBADO_PO  = 2;
    public static final int EN_PROGRESO  = 3;
    public static final int DEVUELTO_PO  = 4;
    public static final int DEVUELTO_RLB = 5;
    public static final int DESESTIMADO  = 6;

    private TransferStatusPolicy() {}

    // --- helpers de rol
    private static String norm(String role){
        return role == null ? "" : role.trim().toUpperCase(Locale.ROOT);
    }
    private static boolean isKM(String role){ return "KM".equals(norm(role)); }
    private static boolean isSM(String role){ return !isKM(role); } // por ahora SM = Rol Consulta/PO

    /** 1/0 si el usuario puede editar TODO el traspaso (SM en devueltos) */
    public static int canEdit(String role, Integer statusId){
        int st = statusId == null ? 0 : statusId;
        return (isSM(role) && (st == DEVUELTO_PO || st == DEVUELTO_RLB)) ? 1 : 0;
    }

    /** 1/0 si (KM) puede editar SOLO comentarios (cuando está Aprobado por PO) */
    public static int canEditComments(String role, Integer statusId){
        int st = statusId == null ? 0 : statusId;
        return (isKM(role) && st == APROBADO_PO) ? 1 : 0;
    }

    // --- matriz para tabs por rol (filtro de la lista)
    private static final Map<String, Map<String, String>> MATRIX = new HashMap<>();
    static {
        var km = new HashMap<String, String>();
        km.put("EN_PROGRESO", "2,5");    // Aprobado PO, Devuelto RLB
        km.put("APROBADOS",   "1");      // Aprobado RLB
        MATRIX.put("KM", km);

        var sm = new HashMap<String, String>();
        sm.put("EN_PROGRESO", "3,2,4,5"); // En progreso, Aprob PO, Dev PO, Dev RLB
        sm.put("APROBADOS",   "1");
        MATRIX.put("SM", sm);
    }

    public static String toCsv(String role, String tab) {
        String r = (role == null || role.isBlank()) ? "KM" : role.trim().toUpperCase(Locale.ROOT);
        String t = (tab  == null || tab.isBlank())  ? "EN_PROGRESO" : tab.trim().toUpperCase(Locale.ROOT);
        Map<String, String> perRole = MATRIX.getOrDefault(r, MATRIX.get("KM"));
        return perRole.getOrDefault(t, perRole.get("EN_PROGRESO"));
    }

    // --- Transiciones de estado controladas en backend ---
    public enum Action { APPROVE, RETURN, RESEND }

    /** Acciones permitidas según rol y estado actual */
    public static Set<Action> allowedActions(String actorRole, int currentStatus){
        String r = actorRole == null ? "" : actorRole.trim().toUpperCase(Locale.ROOT);
        Set<Action> res = EnumSet.noneOf(Action.class);

        if ("KM".equals(r)) {
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

    /** Calcula el nuevo estado a partir de la acción y el rol (valida) */
    public static int computeNextStatusOrThrow(String actorRole, int currentStatus, Action action) {
        Set<Action> allowed = allowedActions(actorRole, currentStatus);
        if (!allowed.contains(action)) {
            throw new IllegalArgumentException(
                    "Transición no permitida para rol=" + actorRole +
                            " desde estado=" + currentStatus + " con acción=" + action);
        }
        String r = actorRole == null ? "" : actorRole.trim().toUpperCase(Locale.ROOT);

        if ("KM".equals(r)) {
            if (currentStatus == APROBADO_PO) {
                if (action == Action.APPROVE) return APROBADO_RLB;
                if (action == Action.RETURN)  return DEVUELTO_RLB;
            }
        } else { // SM/PO
            if (currentStatus == EN_PROGRESO) {
                if (action == Action.APPROVE) return APROBADO_PO;
                if (action == Action.RETURN)  return DEVUELTO_PO;
            }
            if (currentStatus == DEVUELTO_PO || currentStatus == DEVUELTO_RLB) {
                if (action == Action.RESEND)  return EN_PROGRESO;
            }
        }
        throw new IllegalStateException("No se pudo calcular la transición");
    }
}
