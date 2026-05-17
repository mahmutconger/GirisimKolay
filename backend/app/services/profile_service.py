from __future__ import annotations

from dataclasses import dataclass, asdict
from typing import Dict, List, Optional


@dataclass
class ProfilingSnapshot:
    business_idea: Optional[str] = None
    business_sector: Optional[str] = None
    preferred_company_type: Optional[str] = None
    experience_level: Optional[str] = None
    funding_need: Optional[str] = None
    legal_concerns: List[str] = None

    def to_dict(self) -> Dict[str, object]:
        payload = asdict(self)
        payload["legal_concerns"] = self.legal_concerns or []
        return payload


def extract_snapshot_from_text(
    text: str,
    current_snapshot: Optional[Dict[str, object]] = None,
) -> ProfilingSnapshot:
    current_snapshot = current_snapshot or {}
    normalized = text.lower()
    legal_concerns = list(current_snapshot.get("legal_concerns", []))

    def pick(default: Optional[str], *options: tuple[bool, str]) -> Optional[str]:
        for condition, value in options:
            if condition:
                return value
        return default

    if any(keyword in normalized for keyword in ("vergi", "şirket", "sirket", "sgk", "mevzuat")):
        legal_concerns = sorted(set(legal_concerns + ["Vergi ve mevzuat uyumu"]))

    snapshot = ProfilingSnapshot(
        business_idea=text.strip() or current_snapshot.get("business_idea"),
        business_sector=pick(
            current_snapshot.get("business_sector"),
            ("ihracat" in normalized, "İhracat"),
            ("e-ticaret" in normalized or "eticaret" in normalized, "E-Ticaret"),
            ("yemek" in normalized, "Gıda"),
            ("danışman" in normalized or "danisman" in normalized, "Danışmanlık"),
        ),
        preferred_company_type=pick(
            current_snapshot.get("preferred_company_type"),
            ("limited" in normalized, "Limited Şirket"),
            ("şahıs" in normalized or "sahis" in normalized, "Şahıs Şirketi"),
        ),
        experience_level=pick(
            current_snapshot.get("experience_level"),
            ("yeni mezun" in normalized, "Yeni Mezun"),
            ("ev hanımı" in normalized or "ev hanimi" in normalized, "Evden Girişimci"),
        ),
        funding_need=pick(
            current_snapshot.get("funding_need"),
            ("hibe" in normalized or "kosgeb" in normalized, "Hibe / Teşvik"),
            ("yatırım" in normalized or "yatirim" in normalized, "Yatırım"),
        ),
        legal_concerns=legal_concerns,
    )
    return snapshot
