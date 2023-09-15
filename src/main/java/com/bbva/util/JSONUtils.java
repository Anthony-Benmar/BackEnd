package com.bbva.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JSONUtils {
    private static final Logger LOG = Logger.getLogger(JSONUtils.class.getName());

    private JSONUtils() {
        super();
    }

    // convert JSON into List of Objects
    public static <T> List<T> convertFromJsonToList(String json, TypeReference<List<T>> var) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, var);
    }

    // Generic Type Safe Method – convert JSON into Object
    public static <T> T convertFromJsonToObject(String json, Class<T> var) {
        T obj = null;
        ObjectMapper mapper = new ObjectMapper();
        try {
            obj = mapper.readValue(json, var);
        } catch (IOException e) {
            LOG.log(Level.WARNING, e.getMessage(), e);
        }
        return obj;
    }

    // convert Object into JSON
    public static String convertFromObjectToJson(Object obj) {
        String json = "";
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            json = mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            LOG.log(Level.WARNING, e.getMessage(), e);
        }
        return json;
    }
}
