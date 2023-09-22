package com.bbva.enums.dictionary;

import java.util.HashMap;
import java.util.Map;

public enum StatusGenerationType {
	
	COMPLETADO("C","Completado"),
	EN_PROGRESO("P","En progreso"),
	INACTIVO("I","Inactivo");
	
	private String codigo;
	private String descripcion;

	
	private static final Map<String, StatusGenerationType> lookup = new HashMap<>();

    static {
        for (StatusGenerationType e : StatusGenerationType.values()) {
            lookup.put(e.getCodigo(), e);
        }
    }
	
	StatusGenerationType(String codigo, String descripcion){
		this.codigo = codigo;
		this.descripcion = descripcion;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public static Map<String, StatusGenerationType> getLookup() {
		return lookup;
	}

	public static StatusGenerationType get(String codigo){
		return lookup.get(codigo);
	}

}
