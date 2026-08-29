"""
Static topology of the NER road/logistics network used for the accessibility
dashboard and the AI rerouting demo. Each node is a district town that also
corresponds to a "sensor_node_id" from the ML training dataset (data/*.xlsx) --
that link is what lets the routing engine pull a live, model-predicted risk
score for every road segment.
"""

NODES = {
    "GHY": {"name": "Guwahati", "district": "Kamrup Metro", "state": "Assam", "lat": 26.1445, "lon": 91.7362, "sensor_node_id": "NER-A01"},
    "NGP": {"name": "Nongpoh", "district": "Ri-Bhoi", "state": "Meghalaya", "lat": 25.90, "lon": 91.88, "sensor_node_id": "NER-A02"},
    "SHL": {"name": "Shillong", "district": "East Khasi Hills", "state": "Meghalaya", "lat": 25.5788, "lon": 91.8933, "sensor_node_id": "NER-A03"},
    "JNT": {"name": "Jowai", "district": "West Jaintia Hills", "state": "Meghalaya", "lat": 25.43, "lon": 92.35, "sensor_node_id": "NER-A04"},
    "TEZ": {"name": "Tezpur", "district": "Sonitpur", "state": "Assam", "lat": 26.63, "lon": 92.80, "sensor_node_id": "NER-A05"},
    "ITN": {"name": "Itanagar", "district": "Papum Pare", "state": "Arunachal Pradesh", "lat": 27.10, "lon": 93.62, "sensor_node_id": "NER-A06"},
    "BOM": {"name": "Bomdila", "district": "West Kameng", "state": "Arunachal Pradesh", "lat": 27.29, "lon": 92.40, "sensor_node_id": "NER-A07"},
    "HAF": {"name": "Haflong", "district": "Dima Hasao", "state": "Assam", "lat": 25.48, "lon": 93.02, "sensor_node_id": "NER-A08"},
    "SLC": {"name": "Silchar", "district": "Cachar", "state": "Assam", "lat": 24.83, "lon": 92.78, "sensor_node_id": "NER-A09"},
    "AIZ": {"name": "Aizawl", "district": "Aizawl", "state": "Mizoram", "lat": 23.7271, "lon": 92.7176, "sensor_node_id": "NER-A10"},
    "KOH": {"name": "Kohima", "district": "Kohima", "state": "Nagaland", "lat": 25.6751, "lon": 94.1086, "sensor_node_id": "NER-A11"},
    "DMP": {"name": "Dimapur", "district": "Dimapur", "state": "Nagaland", "lat": 25.9091, "lon": 93.7267, "sensor_node_id": "NER-A12"},
    "IMP": {"name": "Imphal", "district": "Imphal West", "state": "Manipur", "lat": 24.8170, "lon": 93.9368, "sensor_node_id": "NER-A13"},
    "AGT": {"name": "Agartala", "district": "West Tripura", "state": "Tripura", "lat": 23.8315, "lon": 91.2868, "sensor_node_id": "NER-A14"},
    "GTK": {"name": "Gangtok", "district": "East Sikkim", "state": "Sikkim", "lat": 27.3389, "lon": 88.6065, "sensor_node_id": "NER-A15"},
    "MNG": {"name": "Mangan", "district": "North Sikkim", "state": "Sikkim", "lat": 27.72, "lon": 88.57, "sensor_node_id": "NER-A16"},
    "ZRO": {"name": "Ziro", "district": "Lower Subansiri", "state": "Arunachal Pradesh", "lat": 27.10, "lon": 93.83, "sensor_node_id": "NER-A17"},
    "DPH": {"name": "Diphu", "district": "Karbi Anglong", "state": "Assam", "lat": 25.85, "lon": 93.44, "sensor_node_id": "NER-A18"},
}

# (node_a, node_b, distance_km, base_time_hr, highway_ref, sensor_node_id governing this segment)
EDGES = [
    ("GHY", "NGP", 60, 1.5, "NH-6", "NER-A02"),
    ("NGP", "SHL", 40, 1.2, "NH-6", "NER-A03"),
    ("SHL", "JNT", 65, 1.8, "NH-6", "NER-A04"),
    ("JNT", "SLC", 150, 4.0, "NH-6", "NER-A09"),
    ("GHY", "TEZ", 175, 4.0, "NH-15", "NER-A05"),
    ("TEZ", "BOM", 140, 4.5, "NH-13", "NER-A07"),
    ("BOM", "ITN", 120, 4.0, "NH-13", "NER-A06"),
    ("TEZ", "ITN", 155, 3.5, "NH-15", "NER-A06"),
    ("ITN", "ZRO", 115, 3.5, "NH-13", "NER-A17"),
    ("GHY", "DPH", 215, 5.0, "NH-27", "NER-A18"),
    ("DPH", "ZRO", 140, 4.0, "NH-13", "NER-A17"),
    ("DPH", "DMP", 75, 2.0, "NH-27", "NER-A12"),
    ("DMP", "KOH", 75, 2.5, "NH-29", "NER-A11"),
    ("KOH", "IMP", 140, 4.0, "NH-2", "NER-A13"),
    ("DPH", "HAF", 150, 4.5, "NH-27", "NER-A08"),
    ("HAF", "SLC", 90, 2.5, "NH-27", "NER-A09"),
    ("SLC", "AIZ", 180, 5.0, "NH-306", "NER-A10"),
    ("SLC", "AGT", 220, 5.5, "NH-8", "NER-A14"),
    ("GHY", "GTK", 220, 6.0, "NH-10", "NER-A15"),
    ("GTK", "MNG", 65, 2.5, "NH-310", "NER-A16"),
]

def edge_key(a: str, b: str) -> str:
    return "-".join(sorted([a, b]))
