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
    private String code;
    private String name;
    private String nativeName;
    private boolean defaultLanguage;
}
