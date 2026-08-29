"""
Minimal multilingual dictionary for UI labels + canned alert phrasing.
A production system would run generated alert text through a translation
API (or pre-approved templates per language); this dictionary demonstrates
the same idea at prototype scale for English / Hindi / Assamese.
"""

LANGUAGES = ["en", "hi", "as"]

STRINGS = {
    "en": {
        "app_name": "NER LogiSense",
        "high_risk_alert": "High landslide risk detected",
        "route_diverted": "Route automatically diverted due to risk",
        "sos_received": "SOS received, help has been notified",
        "all_clear": "Route is now clear",
    },
    "hi": {
        "app_name": "एनईआर लॉजीसेंस",
        "high_risk_alert": "भूस्खलन का उच्च खतरा पाया गया",
        "route_diverted": "जोखिम के कारण मार्ग स्वतः बदला गया",
        "sos_received": "एसओएस प्राप्त हुआ, सहायता को सूचित कर दिया गया है",
        "all_clear": "मार्ग अब सुरक्षित है",
    },
    "as": {
        "app_name": "এনইআৰ লজিছেন্স",
        "high_risk_alert": "মাটি স্খলনৰ উচ্চ বিপদ ধৰা পৰিছে",
        "route_diverted": "বিপদৰ বাবে পথ সলনি কৰা হৈছে",
        "sos_received": "এছঅ'এছ পোৱা গৈছে, সহায় জনোৱা হৈছে",
        "all_clear": "পথ এতিয়া সুৰক্ষিত",
    },
}
