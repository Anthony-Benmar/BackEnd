package com.bbva.util.types;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.joda.time.DateTimeZone;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public final class FechaUtil {
    
    public static final String DATE_TIME_ZONE_AMERICA_LIMA = "America/Lima";

    private FechaUtil(){}

    public static Date ahora() {
		  return org.joda.time.LocalDateTime.now(DateTimeZone.forID(DATE_TIME_ZONE_AMERICA_LIMA)).toDate();
	  } 

    public static Date convertStringToDate(String cadenaFecha, String formato) throws ParseException {
      if(StringUtils.isNotEmpty(cadenaFecha)){
        return DateUtils.parseDate(cadenaFecha, formato);
      }else{
        return null;
      }
    }

    public static String convertDateToString(Date fecha, String formato){
      if(fecha != null){
        return new SimpleDateFormat(formato).format(fecha);
      }else{
        return null;
      }
  }
  
}
