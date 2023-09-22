package com.bbva.enums.dictionary;

import java.util.HashMap;
import java.util.Map;

public enum FolderType {
    
    DICTAMEN("DICTAMEN"),
	DICCIONARIO("DICCIONARIO"),
	PLANTILLAS("PLANTILLAS");
	
	private String nombre;

	private static final Map<String, FolderType> lookup = new HashMap<>();

    static {
        for (FolderType e : FolderType.values()) {
            lookup.put(e.getNombre(), e);
        }
    }
	
	FolderType(String nombre){
		this.nombre = nombre;
	}

	public String getNombre() {
		return nombre;
	}

	public static Map<String, FolderType> getLookup() {
		return lookup;
	}

	public static FolderType get(String nombre){
		return lookup.get(nombre);
	}

}
