package com.hms.identity.audit.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class JsonDiffUtil {

    private final ObjectMapper mapper;

    public JsonDiffUtil(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public String toJson(Object object) {

        try {

            return mapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(object);

        } catch (Exception ex) {

            return "{}";
        }
    }
}