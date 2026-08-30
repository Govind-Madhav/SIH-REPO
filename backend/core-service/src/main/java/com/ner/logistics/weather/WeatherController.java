package com.ner.logistics.weather;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherIntegrationService weatherIntegrationService;

    @GetMapping
    public ResponseEntity<WeatherDataDto> getWeather(@RequestParam(defaultValue = "Dima Hasao") String district) {
        return ResponseEntity.ok(weatherIntegrationService.getDistrictWeather(district));
    }
}
