from __future__ import annotations

import os
from pathlib import Path
from typing import Iterable, Mapping

from reportlab.lib.pagesizes import A4
from reportlab.pdfgen import canvas


REPORTS_DIR = Path(__file__).resolve().parents[2] / "storage" / "reports"


def ensure_reports_dir() -> Path:
    REPORTS_DIR.mkdir(parents=True, exist_ok=True)
    return REPORTS_DIR


def generate_report_pdf(
    report_id: str,
    title: str,
    summary: str,
    next_actions: Iterable[str],
    session_messages: Iterable[Mapping[str, object]],
) -> str:
    reports_dir = ensure_reports_dir()
    file_path = reports_dir / f"{report_id}.pdf"

    pdf = canvas.Canvas(str(file_path), pagesize=A4)
    pdf.setTitle(title)
    pdf.setFont("Helvetica-Bold", 22)
    pdf.drawString(72, 790, "GirişimKolay")
    pdf.setFont("Helvetica", 14)
    pdf.drawString(72, 768, title)

    pdf.setFont("Helvetica", 11)
    text = pdf.beginText(72, 730)
    text.textLine("Yapay zeka destekli girişim hazırlık raporu")
    text.textLine("")
    text.textLine(summary)
    text.textLine("")
    text.textLine("Sonraki adımlar:")
    for action in next_actions:
        text.textLine(f"- {action}")

    text.textLine("")
    text.textLine("Sohbet özeti:")
    for message in list(session_messages)[-4:]:
        role = "Kullanıcı" if message.get("is_from_user") else "AI"
        snippet = str(message.get("text", ""))[:100]
        text.textLine(f"{role}: {snippet}")

    pdf.drawText(text)
    pdf.setFont("Helvetica-Oblique", 9)
    pdf.drawString(72, 48, "Bu belge GirişimKolay backend servisi tarafından üretilmiştir.")
    pdf.save()

    return os.fspath(file_path)
