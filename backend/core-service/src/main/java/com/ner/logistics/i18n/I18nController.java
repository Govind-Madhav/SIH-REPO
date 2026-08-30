package com.ner.logistics.i18n;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/i18n")
@RequiredArgsConstructor
public class I18nController {

    private final I18nService i18nService;

    @GetMapping("/languages")
    public ResponseEntity<List<LanguageDto>> getSupportedLanguages() {
        return ResponseEntity.ok(i18nService.getSupportedLanguages());
    }

    @GetMapping("/translations")
    public ResponseEntity<Map<String, String>> getTranslations(@RequestParam(required = false, defaultValue = "en") String lang) {
        return ResponseEntity.ok(i18nService.getTranslations(lang));
    }
}
