
package com.itdoors.haccp.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itdoors.haccp.model.InputType;

public final class InputTypeParser {

    private static ObjectMapper mapper = new ObjectMapper();

    private InputTypeParser() {
    }

    public static InputType parse(String json) throws JsonProcessingException, IOException {

        JsonNode root = mapper.readTree(json);
        String name = root.get("name").asText();
        JsonNode propertyJson = root.get("property");

        InputType.Range range = InputType.Range.fromString(name);
        InputType.Property property = null;
        switch (range) {

            case INT:
                property = new InputType.IntProperty();
                break;

            case STEP:
                int step = propertyJson.get("step").asInt();
                property = new InputType.StepProperty(step);
                break;

            case ARRAY:

                JsonNode arrayJson = propertyJson.get("array");
                int[] array = new int[arrayJson.size()];

                int i = 0;
                for (final JsonNode objNode : arrayJson)
                    array[i++] = objNode.asInt();

                property = new InputType.ArrayProperty(array);
                break;
        }

        return new InputType(range, property);

    }
}
