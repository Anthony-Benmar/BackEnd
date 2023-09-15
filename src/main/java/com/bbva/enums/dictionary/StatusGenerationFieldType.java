package com.bbva.enums.dictionary;

import java.util.HashMap;
import java.util.Map;

public enum StatusGenerationFieldType {
	
	SIN_OBSERVACION("S","Sin Observación"),
	OBSERVADO("O","Observado"),
	NO_ENCONTRADO("N","No Encontrado"),
	RESUELTO("R","Resuelto");
	
	private String codigo;
	private String descripcion;

	
	private static final Map<String, StatusGenerationFieldType> lookup = new HashMap<>();

    static {
        for (StatusGenerationFieldType e : StatusGenerationFieldType.values()) {
            lookup.put(e.getCodigo(), e);
        }
    }
	
	StatusGenerationFieldType(String codigo, String descripcion){
		this.codigo = codigo;
		this.descripcion = descripcion;
	}

	public String getCodigo() {
		return codigo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public static Map<String, StatusGenerationFieldType> getLookup() {
		return lookup;
	}

	public static StatusGenerationFieldType get(String codigo){
		return lookup.get(codigo);
	}

}
