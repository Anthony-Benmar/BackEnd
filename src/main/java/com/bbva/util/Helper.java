package com.bbva.util;

public class Helper {
    public Integer parseIntegerOrDefault(String value, Integer defaultValue) {
        if (value != null && !value.isEmpty()) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                // Si la conversión falla, se utiliza el valor predeterminado
            }
        }
        return defaultValue;
    }

    public Integer parseInteger(String value) {
        if (value != null && !value.isEmpty()) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                // Si la conversión falla, se devuelve null
            }
        }
        return null;
    }
}
