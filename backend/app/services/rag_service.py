"""
RAG service with an optional Chroma/LangChain adapter and a built-in TF-IDF fallback.

The fallback keeps the repo self-contained for local development while the interface is
shaped for production metadata and citation generation.
"""

import math
import os
import re
from dataclasses import dataclass
from typing import List, Tuple

try:
    from langchain_chroma import Chroma  # type: ignore
    from langchain_google_genai import GoogleGenerativeAIEmbeddings  # type: ignore
except Exception:  # pragma: no cover - optional dependency
    Chroma = None
    GoogleGenerativeAIEmbeddings = None


@dataclass
class KnowledgeDocument:
    id: str
    source_name: str
    document_type: str
    section: str
    published_at: str
    source_url: str
    version: str
    text: str


KNOWLEDGE_BASE: List[KnowledgeDocument] = [
    KnowledgeDocument(
        id="gvk_genc_girisimci",
        source_name="GVK Mükerrer Madde 20/B",
        document_type="Vergi Kanunu",
        section="Madde 20/B",
        published_at="2026-01-01",
        source_url="https://www.gib.gov.tr/",
        version="2026.1",
        text=(
            "Genç Girişimci Kazanç İstisnası: 29 yaşını doldurmamış tam mükellef gerçek kişilerin, "
            "faaliyete başladıkları takvim yılından itibaren üç vergi dönemi boyunca elde ettikleri "
            "kazançların 150.000 Türk lirasına kadar olan kısmı, bu Kanunun 94. maddesi uyarınca "
            "tevkif suretiyle vergilendirilmez ve yıllık beyanname ile de beyan edilmez. "
            "Bu istisnadan yararlanmak için Esnaf ve Sanatkarlar Odası'na kayıtlı olmak şarttır."
        ),
    ),
    KnowledgeDocument(
        id="kosgeb_mikro",
        source_name="KOSGEB 2026 Destek Programı Yönetmeliği",
        document_type="Destek Programı",
        section="Mikro İşletme Desteği",
        published_at="2026-02-10",
        source_url="https://www.kosgeb.gov.tr/",
        version="2026.2",
        text=(
            "KOSGEB Mikro İşletme Destek Programı: 10 kişiden az çalışanı olan ve yıllık net satış "
            "hasılatı 25 milyon TL'yi aşmayan işletmeler mikro işletme sayılır. Bu işletmelere "
            "e-ticaret altyapısı, yazılım, web sitesi ve pazaryeri entegrasyonu için 100.000 TL'ye "
            "kadar geri ödemesiz hibe desteği verilmektedir. Başvuru: girisim.kosgeb.gov.tr "
            "üzerinden 3 yıllık iş planı ile yapılır."
        ),
    ),
    KnowledgeDocument(
        id="kosgeb_teknogirişim",
        source_name="KOSGEB Teknogirişim Destek Programı",
        document_type="Destek Programı",
        section="Teknogirişim",
        published_at="2026-02-10",
        source_url="https://www.kosgeb.gov.tr/",
        version="2026.2",
        text=(
            "Teknogirişim Sermaye Desteği: Üniversite mezunu ya da son sınıf öğrencileri, "
            "teknoloji tabanlı iş fikri ile başvurarak 350.000 TL'ye kadar hibe alabilir. "
            "Yapay zeka, yazılım, mobil uygulama, SaaS ve teknoloji ürünleri önceliklidir. "
            "Proje süresi 24 aydır. İş planı, prototip ve sunum gereklidir."
        ),
    ),
    KnowledgeDocument(
        id="şahis_sirketi",
        source_name="Türk Ticaret Kanunu & Vergi Usul Kanunu",
        document_type="Şirket Kuruluşu",
        section="Şahıs Şirketi",
        published_at="2026-01-05",
        source_url="https://www.resmigazete.gov.tr/",
        version="2026.1",
        text=(
            "Şahıs Şirketi (Tek Şahıs İşletmesi) kurulumu: Türkiye'de en hızlı ve en ucuz şirket "
            "kuruluş türüdür. Vergi dairesine mükellefiyet tesisi ve esnaf odasına kayıt yeterlidir. "
            "Sermaye şartı yoktur. Sahibi tüm şirket borçlarından şahsen sorumludur. "
            "Gelir vergisi oranı %15'ten başlayıp %40'a kadar çıkabilir. "
            "E-ticarete uygun, platform entegrasyonları kolaydır."
        ),
    ),
    KnowledgeDocument(
        id="limited_sirketi",
        source_name="Türk Ticaret Kanunu Md.573-644",
        document_type="Şirket Kuruluşu",
        section="Limited Şirket",
        published_at="2026-01-05",
        source_url="https://www.resmigazete.gov.tr/",
        version="2026.1",
        text=(
            "Limited Şirketi kurulumu: En az 10.000 TL sermaye gerektirir. "
            "Ortaklar şirket borçlarından kişisel olarak sorumlu değildir (limited sorumluluk). "
            "Kurumlar vergisi oranı %25'tir (2024 itibarıyla). "
            "Anonim şirkete kıyasla yönetim daha basittir. Yatırımcı almak için anonim şirkete "
            "dönüşüm kolayca yapılabilir. İhracat yapan KOBİ'ler için tercih edilir."
        ),
    ),
    KnowledgeDocument(
        id="kdv_muafiyet",
        source_name="KDV Kanunu Md.17/4-a ve Md.11",
        document_type="Vergi Kanunu",
        section="KDV İstisnaları",
        published_at="2026-01-03",
        source_url="https://www.gib.gov.tr/",
        version="2026.1",
        text=(
            "KDV İstisnaları: İhracat teslimleri %0 KDV'ye tabidir (tam istisna). "
            "Basit usulde vergilendirilen mükellefler KDV'den muaftır. "
            "Yıllık 3 milyon TL altında ciro yapan ve sadece mal satan işletmeler "
            "basit usulden yararlanabilir. E-ihracatta 150 EUR altı işlemler ETGB "
            "yerine özel beyanname ile kolaylaştırılmış gümrük işlemine tabidir."
        ),
    ),
    KnowledgeDocument(
        id="mikro_ihracat",
        source_name="Gümrük Yönetmeliği Md.225 & Ticaret Bakanlığı Tebliği",
        document_type="Gümrük Tebliği",
        section="Mikro İhracat",
        published_at="2026-01-20",
        source_url="https://ticaret.gov.tr/",
        version="2026.1",
        text=(
            "Mikro İhracat (E-ihracat): 15.000 EUR veya 300 kg altındaki bireysel gönderiler "
            "Elektronik Ticaret Gümrük Beyannamesi (ETGB) ile ihraç edilebilir. "
            "PTT, MNG Kargo, UPS ve DHL bu hizmeti sağlar. "
            "Yurt dışı pazaryerleri (Amazon, Etsy, eBay) üzerinden satış yapan girişimciler "
            "TIM'in sağladığı ücretsiz e-ihracat danışmanlığından yararlanabilir. "
            "KDV iadesi avantajı bulunmaktadır."
        ),
    ),
    KnowledgeDocument(
        id="e_ticaret_mevzuat",
        source_name="E-Ticaret Kanunu 6563 & Yönetmelik",
        document_type="E-Ticaret Kanunu",
        section="Genel Yükümlülükler",
        published_at="2026-02-01",
        source_url="https://www.resmigazete.gov.tr/",
        version="2026.1",
        text=(
            "E-Ticaret Mevzuatı (6563 Sayılı Kanun): Aracı hizmet sağlayıcılar (pazaryerleri) "
            "ticaret unvanını ve MERSİS numarasını web sitesinde göstermek zorundadır. "
            "Kampanya ve promosyonlarda referans fiyat belirtilmesi zorunludur. "
            "Tüketicinin cayma hakkı 14 gündür ve iade kargo bedeli satıcıya aittir. "
            "Net satış hasılatı 10 milyar TL üzerindeki aracı hizmet sağlayıcılar "
            "Ek Yükümlülükler kapsamına girer."
        ),
    ),
    KnowledgeDocument(
        id="tubitak_destek",
        source_name="TÜBİTAK 1512 BiGG Programı",
        document_type="Destek Programı",
        section="BiGG",
        published_at="2026-03-01",
        source_url="https://tubitak.gov.tr/",
        version="2026.1",
        text=(
            "TÜBİTAK BiGG Programı: Teknoloji tabanlı iş fikri olan girişimcilere "
            "kuluçka merkezleri aracılığıyla 200.000 TL'ye kadar destek verir. "
            "AI, yazılım, biyoteknoloji, temiz enerji önceliklidir. "
            "Başarılı projelere TÜBİTAK 1507 KOBİ Ar-Ge desteğine geçiş imkânı tanınır. "
            "Yıllık 2 dönem başvuru alınmaktadır: Nisan ve Ekim ayları."
        ),
    ),
    KnowledgeDocument(
        id="sgk_girişimci",
        source_name="SGK 4/b (Bağ-Kur) Mevzuatı",
        document_type="SGK Mevzuatı",
        section="Prim Yükümlülükleri",
        published_at="2026-01-15",
        source_url="https://www.sgk.gov.tr/",
        version="2026.1",
        text=(
            "Girişimci SGK Yükümlülükleri: Şirket kuran her ortak/sahip 4/b (Bağ-Kur) sigortalısı "
            "sayılır. Aylık prim miktarı 2025 yılında minimum ücretin %34,5'i olarak hesaplanır "
            "(yaklaşık 5.000-6.000 TL/ay). Genç girişimciler ilk 12 ay için Sosyal Güvenlik Kurumu "
            "tarafından prim desteği alabilir. KOSGEB başvurusu yapılmışsa SGK prim desteği de "
            "otomatik olarak aktive edilebilir."
        ),
    ),
]


def _tokenize(text: str) -> List[str]:
    """Lowercases and splits text into word tokens."""
    text = text.lower()
    # Remove punctuation
    text = re.sub(r'[^\w\s]', ' ', text)
    return [w for w in text.split() if len(w) > 2]


def _tf(tokens: List[str]) -> dict:
    """Term Frequency for a list of tokens."""
    freq = {}
    for t in tokens:
        freq[t] = freq.get(t, 0) + 1
    total = len(tokens) or 1
    return {t: count / total for t, count in freq.items()}


def _cosine_similarity(vec_a: dict, vec_b: dict) -> float:
    """Dot product / (|a| * |b|) between two TF dicts."""
    keys = set(vec_a) & set(vec_b)
    dot = sum(vec_a[k] * vec_b[k] for k in keys)
    mag_a = math.sqrt(sum(v ** 2 for v in vec_a.values()))
    mag_b = math.sqrt(sum(v ** 2 for v in vec_b.values()))
    return dot / (mag_a * mag_b) if (mag_a * mag_b) else 0.0


# Pre-compute TF vectors for all knowledge base documents at startup
_KB_VECTORS: List[Tuple[KnowledgeDocument, dict]] = [
    (doc, _tf(_tokenize(doc.text)))
    for doc in KNOWLEDGE_BASE
]


def _maybe_query_chroma(query: str, top_k: int) -> List[dict]:
    chroma_dir = os.getenv("CHROMA_PERSIST_DIR", "")
    api_key = os.getenv("GEMINI_API_KEY", "")
    if not chroma_dir or not api_key or Chroma is None or GoogleGenerativeAIEmbeddings is None:
        return []

    try:  # pragma: no cover - depends on optional local setup
        vector_store = Chroma(
            persist_directory=chroma_dir,
            embedding_function=GoogleGenerativeAIEmbeddings(
                model="models/text-embedding-004",
                google_api_key=api_key,
            ),
        )
        results = vector_store.similarity_search_with_score(query, k=top_k)
        structured = []
        for document, score in results:
            metadata = document.metadata
            structured.append(
                {
                    "id": metadata.get("id", metadata.get("source_name", "unknown")),
                    "source_name": metadata.get("source_name", "Bilinmeyen Kaynak"),
                    "document_type": metadata.get("document_type", ""),
                    "section": metadata.get("section", ""),
                    "published_at": metadata.get("published_at", ""),
                    "source_url": metadata.get("source_url", ""),
                    "version": metadata.get("version", ""),
                    "text": document.page_content,
                    "score": max(0.0, 1.0 - float(score)),
                }
            )
        return structured
    except Exception:
        return []


def retrieve(query: str, top_k: int = 3) -> List[dict]:
    chroma_results = _maybe_query_chroma(query, top_k)
    if chroma_results:
        return chroma_results

    query_vec = _tf(_tokenize(query))
    scored = [
        (doc, _cosine_similarity(query_vec, vec))
        for doc, vec in _KB_VECTORS
    ]
    scored.sort(key=lambda x: x[1], reverse=True)
    return [
        {
            "id": doc.id,
            "source_name": doc.source_name,
            "document_type": doc.document_type,
            "section": doc.section,
            "published_at": doc.published_at,
            "source_url": doc.source_url,
            "version": doc.version,
            "text": doc.text,
            "score": score,
        }
        for doc, score in scored[:top_k]
        if score > 0
    ]


def build_context(query: str) -> Tuple[str, List[str]]:
    """
    Returns (context_block, citations) for injecting into a Gemini prompt.
    """
    results = retrieve(query, top_k=3)
    if not results:
        return "", []

    lines = ["İlgili mevzuat bilgisi:"]
    sources = []
    for r in results:
        lines.append(
            "\n".join(
                [
                    "",
                    f"[Kaynak: {r['source_name']}]",
                    f"[Bölüm: {r['section']}]",
                    r["text"],
                ]
            )
        )
        sources.append(
            {
                "source_name": r["source_name"],
                "section": r["section"],
                "snippet": r["text"][:240],
                "source_url": r["source_url"],
                "score": r["score"],
            }
        )

    return "\n".join(lines), sources
