package com.bbva.util.ApiJiraMet;

import java.util.Map;

public class ValidatorValidateHUTType {
    private String jiraPADCode;
    private String boxClassesBorder;
    private String tipoDesarrollo;

//    public Map<String, Object> getValidatorValidateHUTType(String helpMessage, String tipoDesarrolloSummary, String group) {
//        String message;
//        boolean isValid;
//        boolean isWarning = false;
//
//        if ((jiraPADCode.equals("PAD3") || jiraPADCode.equals("PAD5")) && !tipoDesarrolloSummary.isEmpty()) {
//            this.tipoDesarrollo = tipoDesarrolloSummary;
//            if (this.tipoDesarrolloFormulario.toLowerCase().contains("scaffolder") && !this.tipoDesarrolloFormulario.toLowerCase().contains("despliegue")) {
//                this.tipoDesarrollo = "Scaffolder";
//            }
//
//            message = "<div class=\"" + boxClassesBorder + "\">Tipo de desarrollo</div> es <div class=\"" + boxClassesBorder + " bg-dark border border-dark\">" + this.tipoDesarrollo + "</div>";
//            isValid = true;
//        } else {
//            message = "No se pudo detectar el <div class=\"" + boxClassesBorder + "\">Tipo de desarrollo</div>";
//            isValid = false;
//        }
//        return Map.of("message", message, "isValid", isValid, "isWarning", isWarning, "helpMessage", helpMessage, "group", group);
//    }
}
