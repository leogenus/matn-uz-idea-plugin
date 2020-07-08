package org.example.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MatnUzService {

    private static final String BASE_URL = "https://matn.uz/api/v1/";
    private static final String API_TOKEN = "nPfvpjpE5iPbsIwxIhzVF1x0JmX4km47x1ijYyaoNpCBWtvORTICIiRIADUWDFTObnfOdNmdzwVVZmCX";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public String cyrilToLatin(String text) {
        HttpResponse<String> response = Unirest.post(BASE_URL + "latin")
                .header("Authorization", "Bearer " + API_TOKEN)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .body(textToJsonString(text)).asObject(String.class);

        if (response.getStatus() == 200)
            return response.getBody();

        throw new RuntimeException(loadErrorMessage(response));
    }

    public String latinToCyril(String text) {
        HttpResponse<String> response = Unirest.post(BASE_URL + "cyrillic")
                .header("Authorization", "Bearer " + API_TOKEN)
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .body(textToJsonString(text)).asObject(String.class);

        if (response.getStatus() == 200)
            return response.getBody();

        throw new RuntimeException(loadErrorMessage(response));
    }

    private String textToJsonString(String text) {
        try {
            return OBJECT_MAPPER.writeValueAsString(new HashMap<String, Object>() {{
                put("text", text);
            }});
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private String loadErrorMessage(HttpResponse<String> response) {
        return ((List<String>) (((Map<String, Object>) (response.mapError(HashMap.class).get("message"))).get("text"))).stream().collect(Collectors.joining(","));
    }

}
