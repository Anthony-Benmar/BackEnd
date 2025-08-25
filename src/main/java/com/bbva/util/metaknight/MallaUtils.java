package com.bbva.util.metaknight;

public class MallaUtils {
    //probablemente elimine todo luego jejeje

//    public String generarTransferName(String dfRawName) {
//        return "pppra_oraclehdfsplincampwincpndinf_0";
//    }

//    public String extraerUuaaRawDeTransfer(String transferName) {
//        // Por ahora retorna un valor por defecto
//        if (transferName != null && transferName.length() >= 4) {
//            // Intenta extraer los primeros 4 caracteres como UUAA
//            return transferName.substring(0, 4).toLowerCase();
//        }
//        return "ppra"; // Valor por defecto
//    }


//    public String construirNamespace(String uuaa, String appId) {
//        if (uuaa == null || uuaa.isEmpty()) {
//            throw new IllegalArgumentException("UUAA no puede ser nula o vacía");
//        }
//
//        String appIdPart = (appId != null && !appId.isEmpty()) ? appId : "20768";
//        return String.format("pe.%s.app-id-%s.pro", uuaa.toLowerCase(), appIdPart);
//    }
//
//    public String construirParentFolder(String uuaa, String suffix) {
//        if (uuaa == null || uuaa.isEmpty()) {
//            throw new IllegalArgumentException("UUAA no puede ser nula o vacía");
//        }
//
//        String suffixPart = (suffix != null && !suffix.isEmpty()) ? suffix : "DIA-T06";
//        return String.format("CR-%s%s", uuaa.toUpperCase(), suffixPart);
//    }
//
//    public String construirTeamEmail(String teamName, String domain) {
//        if (teamName == null || teamName.isEmpty()) {
//            teamName = "datos-locales-equipo-2";
//        }
//
//        if (domain == null || domain.isEmpty()) {
//            domain = "@bbva.com";
//        }
//
//        return teamName + ".group" + domain;
//    }
}