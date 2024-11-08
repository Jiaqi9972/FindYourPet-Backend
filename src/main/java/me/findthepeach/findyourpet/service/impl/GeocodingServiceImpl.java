package me.findthepeach.findyourpet.service.impl;

import me.findthepeach.findyourpet.service.GeocodingService;
import me.findthepeach.findyourpet.utils.GeocodingResponse;
import org.geolatte.geom.G2D;
import org.geolatte.geom.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class GeocodingServiceImpl implements GeocodingService {

    @Value("${google.maps.api.key}")
    private String apiKey;

    private static final String GEOCODING_API_URL = "https://maps.googleapis.com/maps/api/geocode/json";

    // 接收经纬度，返回地址字符串
    public String calculateAddressFromCoordinates(double latitude, double longitude) {
        // 构建请求 URL
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(GEOCODING_API_URL)
                .queryParam("latlng", latitude + "," + longitude)
                .queryParam("key", apiKey);

        // 发起 GET 请求
        RestTemplate restTemplate = new RestTemplate();
        GeocodingResponse response = restTemplate.getForObject(uriBuilder.toUriString(), GeocodingResponse.class);

        // 检查返回的结果并解析
        if (response != null && !response.getResults().isEmpty()) {
            return response.getResults().get(0).getFormattedAddress();
        }

        throw new RuntimeException("Unable to calculate address from coordinates.");
    }

    @Override
    public String calculateAddressFromLocation(Point<G2D> location) {
        G2D coordinates = location.getPosition();
        return calculateAddressFromCoordinates(coordinates.getLat(), coordinates.getLon());
    }
}