package com.ner.logistics.i18n;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LanguageDto {
    private String code; // en, hi, as, bn
    private String name; // English, Hindi, Assamese, Bengali
    private String nativeName; // English, हिन्दी, অসমীয়া, বাংলা
    private Boolean defaultLanguage;
}
