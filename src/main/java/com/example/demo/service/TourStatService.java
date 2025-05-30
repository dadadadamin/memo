package com.example.demo.service;

import com.google.api.client.util.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class TourStatService {

    @Value("${tourapi.key}")
    private String apiKey;

    @Value("${tourapi.datalab.url}")
    private String dataLabBaseUrl;

    @Value("${tourapi.tar.url}")
    private String tarBaseUrl;

    @Value("${tourapi.mobile-os}")
    private String mobileOs;

    @Value("${tourapi.mobile-app}")
    private String mobileApp;

    private final RestTemplate restTemplate = new RestTemplate();


    public String getVisitorStats(String areaCode, String date) {
        String url = UriComponentsBuilder.fromHttpUrl(dataLabBaseUrl + "/metcoRegnVisitrDDList")
                .queryParam("serviceKey", apiKey)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 10)
                .queryParam("MobileOS", mobileOs)
                .queryParam("MobileApp", mobileApp)
                .queryParam("_type", "json")
                .queryParam("startYmd", date)
                .queryParam("endYmd", date)
                .build(false).toUriString();

        return restTemplate.getForObject(url, String.class);
    }


    public String getTravelRecommendation(String areaCode) {
        String url = UriComponentsBuilder.fromHttpUrl(tarBaseUrl + "/areaBasedList")
                .queryParam("serviceKey", apiKey)
                .queryParam("pageNo", 1)
                .queryParam("numOfRows", 10)
                .queryParam("MobileOS", mobileOs)
                .queryParam("MobileApp", mobileApp)
                .queryParam("_type", "json")
                .queryParam("areaCode", areaCode)
                .build(false).toUriString();

        return restTemplate.getForObject(url, String.class);
    }
}
