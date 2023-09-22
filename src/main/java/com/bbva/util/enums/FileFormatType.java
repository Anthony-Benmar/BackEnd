package com.bbva.util.enums;

import java.util.HashMap;
import java.util.Map;

public enum FileFormatType {
    
    XLSX("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "xlsx");

	private String tipo;
    private String extension;
	
	private static final Map<String, FileFormatType> lookup = new HashMap<>();
	
	static {
        for (FileFormatType e : FileFormatType.values()) {
            lookup.put(e.getTipo(), e);
        }
    }
	
	FileFormatType(String tipo, String extension) {
		this.tipo = tipo;
        this.extension = extension;
	}
		
	public String getTipo() {
		return this.tipo;
	}

    public String getExtension() {
		return this.extension;
	}

	public static FileFormatType get(String nombre){
		return lookup.get(nombre);
	}

}
