package com.bbva.enums.dictionary;

import java.util.HashMap;
import java.util.Map;

public enum TemplateType {
    
    DICTAMEN("DICTAMEN"),
	DICCIONARIO("DICCIONARIO");
	
	private String codigo;

	private static final Map<String, TemplateType> lookup = new HashMap<>();

    static {
        for (TemplateType e : TemplateType.values()) {
            lookup.put(e.getCodigo(), e);
        }
    }
	
	TemplateType(String codigo){
		this.codigo = codigo;
	}

	public String getCodigo() {
		return codigo;
	}

	public static Map<String, TemplateType> getLookup() {
		return lookup;
	}

	public static TemplateType get(String codigo){
		return lookup.get(codigo);
	}

}
