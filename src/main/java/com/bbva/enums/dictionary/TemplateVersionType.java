package com.bbva.enums.dictionary;

import java.util.HashMap;
import java.util.Map;

public enum TemplateVersionType {
    
    V_01("01");
	
	private String codigo;

	private static final Map<String, TemplateVersionType> lookup = new HashMap<>();

    static {
        for (TemplateVersionType e : TemplateVersionType.values()) {
            lookup.put(e.getCodigo(), e);
        }
    }
	
	TemplateVersionType(String codigo){
		this.codigo = codigo;
	}

	public String getCodigo() {
		return codigo;
	}

	public static Map<String, TemplateVersionType> getLookup() {
		return lookup;
	}

	public static TemplateVersionType get(String codigo){
		return lookup.get(codigo);
	}


}
