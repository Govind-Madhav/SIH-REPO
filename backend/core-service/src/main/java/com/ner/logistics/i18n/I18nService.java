package com.ner.logistics.i18n;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class I18nService {

    public List<LanguageDto> getSupportedLanguages() {
        return List.of(
                LanguageDto.builder().code("en").name("English").nativeName("English").defaultLanguage(true).build(),
                LanguageDto.builder().code("hi").name("Hindi").nativeName("हिन्दी").defaultLanguage(false).build(),
                LanguageDto.builder().code("as").name("Assamese").nativeName("অসমীয়া").defaultLanguage(false).build(),
                LanguageDto.builder().code("bn").name("Bengali").nativeName("বাংলা").defaultLanguage(false).build()
        );
    }

    public Map<String, String> getTranslations(String langCode) {
        String lang = langCode != null ? langCode.toLowerCase() : "en";

        switch (lang) {
            case "hi":
                return Map.of(
                        "app_title", "उत्तर-पूर्व लॉजिस्टिक्स और आपातकालीन कमांड",
                        "sos_button", "आपातकालीन एसओएस सिग्नल (SOS)",
                        "login_phone", "मोबाइल नंबर से लॉगिन करें",
                        "enter_otp", "6-अंकों का ओटीपी दर्ज करें",
                        "send_otp", "ओटीपी भेजें",
                        "report_incident", "घटना की रिपोर्ट करें",
                        "landslide_warning", "भूस्खलन की चेतावनी",
                        "risk_level", "जोखिम स्तर",
                        "reroute_recommendation", "वैकल्पिक मार्ग की सिफारिश"
                );
            case "as":
                return Map.of(
                        "app_title", "উ উত্তৰ-পূব পৰিবহণ আৰু জৰুৰীকালীন নিৰ্দেশনা",
                        "sos_button", "জৰুৰীকালীন এচ-অ-এচ (SOS)",
                        "login_phone", "মোবাইল নম্বৰৰ দ্বাৰা লগইন কৰক",
                        "enter_otp", "৬-অংকৰ অ'টিপি দিয়ক",
                        "send_otp", "অ'টিপি প্ৰেৰণ কৰক",
                        "report_incident", "দুৰ্যোগৰ খবৰ দিয়ক",
                        "landslide_warning", "ভূমিস্খলনৰ সঁহাৰি",
                        "risk_level", "আশংকাৰ মাত্ৰা",
                        "reroute_recommendation", "বিকল্প পথৰ পৰামৰ্শ"
                );
            case "bn":
                return Map.of(
                        "app_title", "উত্তর-পূর্ব লজিস্টিক ও জরুরি কমান্ড সেন্টার",
                        "sos_button", "জরুরি এসওএস সিগন্যাল (SOS)",
                        "login_phone", "মোবাইল নম্বর দিয়ে লগইন করুন",
                        "enter_otp", "৬-সংখ্যার ওটিপি দিন",
                        "send_otp", "ওটিপি পাঠান",
                        "report_incident", "দুর্ঘটনার রিপোর্ট করুন",
                        "landslide_warning", "ভূমিধসের সতর্কবার্তা",
                        "risk_level", "ঝুঁকির মাত্রা",
                        "reroute_recommendation", "বিকল্প রুটের পরামর্শ"
                );
            default: // "en"
                return Map.of(
                        "app_title", "NER Logistics & Emergency Command",
                        "sos_button", "Emergency SOS Signal",
                        "login_phone", "Login with Mobile Number",
                        "enter_otp", "Enter 6-Digit OTP",
                        "send_otp", "Send OTP",
                        "report_incident", "Report Incident",
                        "landslide_warning", "Landslide Warning",
                        "risk_level", "Risk Level",
                        "reroute_recommendation", "Reroute Recommendation"
                );
        }
    }
}
