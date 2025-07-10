package com.nasa.apis.apod.service;

import com.nasa.apis.apod.dto.ApodResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApodService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${API_KEY}")
    private String apiKey;

    public ApodResponse fetchApodData(){

        // API URL
        String apiUrl = "https://api.nasa.gov/planetary/apod?api_key=" + apiKey;

        // Call API and return the response
        return restTemplate.getForObject(apiUrl, ApodResponse.class);
    }
}
