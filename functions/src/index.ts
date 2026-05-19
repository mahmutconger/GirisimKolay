import { GoogleGenAI } from "@google/genai";
import * as admin from "firebase-admin";
import { onCall, HttpsError } from "firebase-functions/v2/https";
import { defineSecret } from "firebase-functions/params";
import { PDFDocument, StandardFonts } from "pdf-lib";

admin.initializeApp();

const GEMINI_API_KEY = defineSecret("GEMINI_API_KEY");
const REGION = "europe-west1";
const MODEL_NAME = "gemini-1.5-pro";
const EXTRACT_PROFILE_RUNTIME = {
  region: REGION,
  secrets: [GEMINI_API_KEY],
  memory: "256MiB" as const,
  timeoutSeconds: 20,
  concurrency: 20,
  maxInstances: 5,
  minInstances: 0
};
const CHAT_RUNTIME = {
  region: REGION,
  secrets: [GEMINI_API_KEY],
  memory: "512MiB" as const,
  timeoutSeconds: 30,
  concurrency: 10,
  maxInstances: 5,
  minInstances: 0
};
const REPORT_RUNTIME = {
  region: REGION,
  secrets: [GEMINI_API_KEY],
  memory: "1GiB" as const,
  timeoutSeconds: 60,
  concurrency: 2,
  maxInstances: 3,
  minInstances: 0
};
const SYSTEM_PROMPT = `Sen GirişimKolay'ın yapay zeka iş danışmanısın.
Daima Türkçe cevap ver. Bağlam dışı bilgi uydurma.
Yalnız doğrulanabilir mevzuat ve destek bağlamına dayalı, kısa ve uygulanabilir öneri üret.
Eğer delil zayıfsa bunu açıkça söyle ve güvenli bir sonraki adım öner.`;

type ProfilingSnapshot = {
  businessIdea?: string | null;
  businessSector?: string | null;
  preferredCompanyType?: string | null;
  experienceLevel?: string | null;
  fundingNeed?: string | null;
  legalConcerns: string[];
};

type Citation = {
  sourceName: string;
  section?: string | null;
  snippet?: string | null;
  sourceUrl?: string | null;
};

type KnowledgeDocument = {
  id: string;
  sourceName: string;
  documentType: string;
  section: string;
  publishedAt: string;
  sourceUrl: string;
  version: string;
  text: string;
};

const KNOWLEDGE_BASE: KnowledgeDocument[] = [
  {
    id: "sahis_sirketi",
    sourceName: "Türk Ticaret Kanunu & Vergi Usul Kanunu",
    documentType: "Şirket Kuruluşu",
    section: "Şahıs Şirketi",
    publishedAt: "2026-01-05",
    sourceUrl: "https://www.resmigazete.gov.tr/",
    version: "2026.1",
    text: "Şahıs şirketi Türkiye'de hızlı ve düşük maliyetli kuruluş modelidir. Sermaye şartı yoktur. Sahibi şirket borçlarından şahsen sorumludur. E-ticaret yapan küçük girişimciler için sık tercih edilir."
  },
  {
    id: "limited_sirket",
    sourceName: "Türk Ticaret Kanunu Md.573-644",
    documentType: "Şirket Kuruluşu",
    section: "Limited Şirket",
    publishedAt: "2026-01-05",
    sourceUrl: "https://www.resmigazete.gov.tr/",
    version: "2026.1",
    text: "Limited şirket için en az 10.000 TL sermaye gerekir. Ortakların sorumluluğu sınırlıdır. Daha kurumsal yapı, ortaklı iş ve büyüme senaryoları için tercih edilir."
  },
  {
    id: "kosgeb_mikro",
    sourceName: "KOSGEB 2026 Destek Programı Yönetmeliği",
    documentType: "Destek Programı",
    section: "Mikro İşletme Desteği",
    publishedAt: "2026-02-10",
    sourceUrl: "https://www.kosgeb.gov.tr/",
    version: "2026.2",
    text: "Mikro işletmelere e-ticaret altyapısı, yazılım, web sitesi ve pazaryeri entegrasyonu için 100.000 TL'ye kadar hibe desteği sağlanabilir. Başvuru iş planı ile yapılır."
  },
  {
    id: "genc_girisimci",
    sourceName: "GVK Mükerrer Madde 20/B",
    documentType: "Vergi Kanunu",
    section: "Genç Girişimci İstisnası",
    publishedAt: "2026-01-01",
    sourceUrl: "https://www.gib.gov.tr/",
    version: "2026.1",
    text: "29 yaşını doldurmamış gerçek kişiler belirli şartlarla genç girişimci vergi istisnasından yararlanabilir. Faaliyete başlangıç sonrası ilk dönemler için önemli vergi avantajı sağlayabilir."
  },
  {
    id: "sgk_girisimci",
    sourceName: "SGK 4/b Mevzuatı",
    documentType: "SGK Mevzuatı",
    section: "Prim Yükümlülükleri",
    publishedAt: "2026-01-15",
    sourceUrl: "https://www.sgk.gov.tr/",
    version: "2026.1",
    text: "Şirket kuran kişi 4/b kapsamında Bağ-Kur prim yükümlüsü olabilir. Genç girişimciler için belirli dönemlerde prim destekleri söz konusu olabilir."
  },
  {
    id: "e_ticaret",
    sourceName: "6563 Sayılı E-Ticaret Kanunu",
    documentType: "E-Ticaret Kanunu",
    section: "Genel Yükümlülükler",
    publishedAt: "2026-02-01",
    sourceUrl: "https://www.resmigazete.gov.tr/",
    version: "2026.1",
    text: "E-ticaret yapan işletmeler ticaret unvanı, MERSİS numarası, cayma hakkı ve iade yükümlülükleri konusunda kanuni gereklilikleri yerine getirmelidir."
  }
];

function requireUid(uid?: string): string {
  if (!uid) {
    throw new HttpsError("unauthenticated", "Kimlik doğrulama gerekli.");
  }
  return uid;
}

function tokenize(text: string): string[] {
  return text
    .toLocaleLowerCase("tr-TR")
    .replace(/[^\p{L}\p{N}\s]/gu, " ")
    .split(/\s+/)
    .filter((token) => token.length > 2);
}

function tf(tokens: string[]): Map<string, number> {
  const map = new Map<string, number>();
  for (const token of tokens) {
    map.set(token, (map.get(token) ?? 0) + 1);
  }
  const size = tokens.length || 1;
  for (const [key, value] of map.entries()) {
    map.set(key, value / size);
  }
  return map;
}

function cosineSimilarity(a: Map<string, number>, b: Map<string, number>): number {
  let dot = 0;
  let normA = 0;
  let normB = 0;

  for (const value of a.values()) {
    normA += value * value;
  }
  for (const value of b.values()) {
    normB += value * value;
  }
  for (const [key, value] of a.entries()) {
    dot += value * (b.get(key) ?? 0);
  }

  if (normA === 0 || normB === 0) {
    return 0;
  }
  return dot / (Math.sqrt(normA) * Math.sqrt(normB));
}

function retrieveDocuments(query: string, topK = 3): Array<KnowledgeDocument & { score: number }> {
  const queryVector = tf(tokenize(query));
  return KNOWLEDGE_BASE
    .map((doc) => ({
      ...doc,
      score: cosineSimilarity(queryVector, tf(tokenize(doc.text)))
    }))
    .filter((doc) => doc.score > 0)
    .sort((left, right) => right.score - left.score)
    .slice(0, topK);
}

function buildProfileSnapshot(text: string, currentProfile?: Partial<ProfilingSnapshot> | null): ProfilingSnapshot {
  const normalized = text.toLocaleLowerCase("tr-TR");
  const profile: ProfilingSnapshot = {
    businessIdea: currentProfile?.businessIdea ?? inferBusinessIdea(text),
    businessSector: currentProfile?.businessSector ?? inferSector(normalized),
    preferredCompanyType: currentProfile?.preferredCompanyType ?? inferCompanyType(normalized),
    experienceLevel: currentProfile?.experienceLevel ?? inferExperience(normalized),
    fundingNeed: currentProfile?.fundingNeed ?? inferFunding(normalized),
    legalConcerns: Array.from(
      new Set([...(currentProfile?.legalConcerns ?? []), ...inferLegalConcerns(normalized)])
    )
  };
  return profile;
}

function inferBusinessIdea(text: string): string | null {
  const trimmed = text.trim();
  return trimmed.length > 12 ? trimmed.slice(0, 180) : null;
}

function inferSector(text: string): string | null {
  if (text.includes("e-ticaret") || text.includes("etsy") || text.includes("amazon")) return "E-Ticaret";
  if (text.includes("yazılım") || text.includes("uygulama") || text.includes("ai")) return "Yazılım";
  if (text.includes("gıda") || text.includes("kafe")) return "Gıda";
  return null;
}

function inferCompanyType(text: string): string | null {
  if (text.includes("şahıs")) return "Şahıs Şirketi";
  if (text.includes("limited")) return "Limited Şirket";
  return null;
}

function inferExperience(text: string): string | null {
  if (text.includes("yeni mezun") || text.includes("ilk kez")) return "Başlangıç";
  if (text.includes("tecrübeli") || text.includes("deneyimliyim")) return "Orta";
  return null;
}

function inferFunding(text: string): string | null {
  if (text.includes("hibe") || text.includes("destek") || text.includes("sermaye")) return "Dış Finansman İhtiyacı";
  return null;
}

function inferLegalConcerns(text: string): string[] {
  const concerns: string[] = [];
  if (text.includes("vergi")) concerns.push("Vergi yükümlülükleri");
  if (text.includes("sgk") || text.includes("bağkur")) concerns.push("SGK / Bağ-Kur");
  if (text.includes("iade") || text.includes("cayma")) concerns.push("Tüketici yükümlülükleri");
  return concerns;
}

function buildNextActions(profile: ProfilingSnapshot): string[] {
  const actions = ["Şirket tipinizi netleştirin", "Vergi ve SGK yükümlülüklerinizi listeleyin"];
  if (profile.fundingNeed) {
    actions.push("Size uygun hibe ve teşvik başlıklarını önceliklendirin");
  }
  return actions.slice(0, 3);
}

async function generateAnswer(query: string, citations: Citation[], profile: ProfilingSnapshot): Promise<string> {
  const apiKey = GEMINI_API_KEY.value();
  if (!apiKey) {
    console.error("Gemini API anahtari bulunamadi. Fallback cevap donuluyor.");
    return fallbackAnswer(query, citations);
  }

  try {
    const client = new GoogleGenAI({ apiKey });
    const prompt = [
      "Bağlam:",
      ...citations.map((citation, index) => `${index + 1}. ${citation.sourceName} - ${citation.section ?? "Genel"}: ${citation.snippet ?? ""}`),
      "",
      `Kullanıcı sorusu: ${query}`,
      `Profil özeti: ${JSON.stringify(profile)}`
    ].join("\n");

    const response = await client.models.generateContent({
      model: MODEL_NAME,
      contents: prompt,
      config: {
        systemInstruction: SYSTEM_PROMPT
      }
    });

    const generatedText = response.text?.trim();
    if (!generatedText) {
      console.error("Gemini bos cevap dondurdu. Fallback cevap donuluyor.");
      return fallbackAnswer(query, citations);
    }
    return generatedText;
  } catch (error) {
    console.error("Gemini cevap uretimi basarisiz oldu.", error);
    return fallbackAnswer(query, citations);
  }
}

function fallbackAnswer(query: string, citations: Citation[]): string {
  if (citations.length === 0) {
    return "Sorunuz için elimde yeterli doğrulanmış mevzuat bağlamı yok. İş fikrinizi, sektörünüzü ve şirket tipi tercihinizi biraz daha net yazarsanız güvenli bir yol haritası çıkarabilirim.";
  }
  return `Sorunuz için en ilgili kaynaklara göre ilk öncelik, ${citations[0].sourceName} başlığındaki yükümlülükleri ve avantajları netleştirmek. Sonraki adımda şirket türü, vergi yükü ve destek uygunluğunu birlikte daraltabiliriz.`;
}

function reportDownloadUrl(bucketName: string, storagePath: string, token: string): string {
  const emulatorHost = process.env.FIREBASE_STORAGE_EMULATOR_HOST;
  const encodedPath = encodeURIComponent(storagePath);
  if (emulatorHost) {
    return `http://${emulatorHost}/v0/b/${bucketName}/o/${encodedPath}?alt=media&token=${token}`;
  }
  return `https://firebasestorage.googleapis.com/v0/b/${bucketName}/o/${encodedPath}?alt=media&token=${token}`;
}

async function buildPdf(reportTitle: string, reportSummary: string, nextActions: string[], messages: Array<Record<string, unknown>>): Promise<Uint8Array> {
  const pdfDoc = await PDFDocument.create();
  const page = pdfDoc.addPage([595, 842]);
  const font = await pdfDoc.embedFont(StandardFonts.Helvetica);
  let cursorY = 790;

  const drawLine = (text: string, size = 12) => {
    page.drawText(text, {
      x: 48,
      y: cursorY,
      size,
      font
    });
    cursorY -= size + 10;
  };

  drawLine("GirişimKolay", 18);
  drawLine(reportTitle, 16);
  drawLine(`Oluşturulma: ${new Date().toLocaleString("tr-TR")}`, 10);
  cursorY -= 8;
  drawLine("Özet", 14);
  for (const chunk of splitText(reportSummary, 82)) {
    drawLine(chunk, 11);
  }
  cursorY -= 8;
  drawLine("Sonraki Adımlar", 14);
  nextActions.forEach((action, index) => drawLine(`${index + 1}. ${action}`, 11));
  cursorY -= 8;
  drawLine("Sohbet Özeti", 14);
  messages.slice(-6).forEach((message) => {
    const role = message.role === "assistant" ? "AI" : "Kullanıcı";
    const text = String(message.text ?? "");
    for (const chunk of splitText(`${role}: ${text}`, 82)) {
      drawLine(chunk, 10);
    }
  });

  return pdfDoc.save();
}

function splitText(text: string, limit: number): string[] {
  const words = text.split(/\s+/);
  const lines: string[] = [];
  let current = "";
  for (const word of words) {
    const next = current ? `${current} ${word}` : word;
    if (next.length > limit) {
      if (current) lines.push(current);
      current = word;
    } else {
      current = next;
    }
  }
  if (current) lines.push(current);
  return lines;
}

export const extractProfileSnapshot = onCall(
  EXTRACT_PROFILE_RUNTIME,
  async (request) => {
    requireUid(request.auth?.uid);
    const data = request.data as { text?: string; currentProfile?: Partial<ProfilingSnapshot> | null };
    const text = data.text?.trim();
    if (!text) {
      throw new HttpsError("invalid-argument", "Analiz edilecek metin gerekli.");
    }
    return {
      snapshot: buildProfileSnapshot(text, data.currentProfile ?? null)
    };
  }
);

export const sendChatMessage = onCall(
  CHAT_RUNTIME,
  async (request) => {
    const uid = requireUid(request.auth?.uid);
    const data = request.data as {
      sessionId?: string | null;
      text?: string;
      clientRequestId?: string;
    };

    const text = data.text?.trim();
    const clientRequestId = data.clientRequestId?.trim();
    if (!text || !clientRequestId) {
      throw new HttpsError("invalid-argument", "Metin ve istek kimliği gerekli.");
    }

    const db = admin.firestore();
    const now = Date.now();
    const sessionId = data.sessionId?.trim() || `session_${uid}_${now}`;
    const sessionRef = db.collection("users").doc(uid).collection("chatSessions").doc(sessionId);
    const userMessageId = `user_${clientRequestId}`;
    const aiMessageId = `ai_${clientRequestId}`;
    const aiMessageRef = sessionRef.collection("messages").doc(aiMessageId);
    const existingAiMessage = await aiMessageRef.get();

    if (existingAiMessage.exists) {
      const payload = existingAiMessage.data() ?? {};
      return {
        sessionId,
        message: payload,
        answer: payload.text ?? "",
        citations: payload.citations ?? [],
        profileDelta: payload.profileDelta ?? null,
        confidence: payload.confidence ?? 0,
        nextActions: payload.nextActions ?? [],
        insufficientEvidence: (payload.confidence ?? 0) < 0.12
      };
    }

    const retrieved = retrieveDocuments(text, 3);
    const citations: Citation[] = retrieved.map((doc) => ({
      sourceName: doc.sourceName,
      section: doc.section,
      snippet: doc.text.slice(0, 220),
      sourceUrl: doc.sourceUrl
    }));
    const confidence = retrieved.length
      ? Number((retrieved.reduce((sum, item) => sum + item.score, 0) / retrieved.length).toFixed(2))
      : 0.1;
    const insufficientEvidence = retrieved.length === 0 || confidence < 0.12;
    const profileDelta = buildProfileSnapshot(text, null);
    const nextActions = buildNextActions(profileDelta);
    const answer = await generateAnswer(text, citations, profileDelta);

    await sessionRef.set(
      {
        id: sessionId,
        uid,
        title: text.slice(0, 80),
        status: "active",
        createdAt: now,
        updatedAt: now,
        lastMessageAt: now,
        latestProfileSnapshot: profileDelta
      },
      { merge: true }
    );

    await sessionRef.collection("messages").doc(userMessageId).set({
      id: userMessageId,
      sessionId,
      text,
      isFromUser: true,
      timestamp: now,
      citations: [],
      profileDelta: null,
      confidence: null,
      nextActions: [],
      requestId: clientRequestId,
      role: "user"
    });

    const aiMessage = {
      id: aiMessageId,
      sessionId,
      text: answer,
      isFromUser: false,
      timestamp: now,
      citations,
      profileDelta,
      confidence,
      nextActions,
      requestId: clientRequestId,
      role: "assistant"
    };
    await aiMessageRef.set(aiMessage);
    await sessionRef.set(
      {
        updatedAt: now,
        lastMessageAt: now,
        latestProfileSnapshot: profileDelta
      },
      { merge: true }
    );

    return {
      sessionId,
      message: aiMessage,
      answer,
      citations,
      profileDelta,
      confidence,
      nextActions,
      insufficientEvidence
    };
  }
);

export const generateRoadmapReport = onCall(
  REPORT_RUNTIME,
  async (request) => {
    const uid = requireUid(request.auth?.uid);
    const data = request.data as { sessionId?: string };
    const sessionId = data.sessionId?.trim();
    if (!sessionId) {
      throw new HttpsError("invalid-argument", "Oturum kimliği gerekli.");
    }

    const db = admin.firestore();
    const sessionRef = db.collection("users").doc(uid).collection("chatSessions").doc(sessionId);
    const sessionSnapshot = await sessionRef.get();
    if (!sessionSnapshot.exists) {
      throw new HttpsError("not-found", "Sohbet oturumu bulunamadı.");
    }

    const reportId = `report_${sessionId}`;
    const reportRef = db.collection("users").doc(uid).collection("reports").doc(reportId);
    const existingReport = await reportRef.get();
    if (existingReport.exists) {
      return existingReport.data();
    }

    const messagesSnapshot = await sessionRef.collection("messages").orderBy("timestamp").get();
    const messages = messagesSnapshot.docs.map((doc) => doc.data());
    const aiMessages = messages.filter((message) => !message.isFromUser);
    const summary = typeof aiMessages.at(-1)?.text === "string"
      ? String(aiMessages.at(-1)?.text)
      : "Henüz doğrulanmış AI çıktısı oluşmadı.";
    const latestProfileSnapshot = sessionSnapshot.data()?.latestProfileSnapshot ?? null;
    const nextActions = latestProfileSnapshot
      ? buildNextActions(latestProfileSnapshot as ProfilingSnapshot)
      : ["Şirket tipinizi kesinleştirin", "Vergi ve SGK yükümlülüklerinizi gözden geçirin"];
    const pdfBytes = await buildPdf("Girişim Hazırlık Raporu", summary, nextActions, messages);

    const bucket = admin.storage().bucket();
    const storagePath = `reports/${uid}/${reportId}.pdf`;
    const token = `${uid}-${reportId}-${Date.now()}`;
    await bucket.file(storagePath).save(Buffer.from(pdfBytes), {
      contentType: "application/pdf",
      metadata: {
        metadata: {
          firebaseStorageDownloadTokens: token
        }
      }
    });

    const generatedAt = Date.now();
    const payload = {
      id: reportId,
      userId: uid,
      sessionId,
      title: "Girişim Hazırlık Raporu",
      summary,
      generatedAt,
      approvalStatus: "IDLE",
      nextActions,
      storagePath,
      downloadUrl: reportDownloadUrl(bucket.name, storagePath, token),
      fileUrl: reportDownloadUrl(bucket.name, storagePath, token)
    };

    await reportRef.set(payload);
    await sessionRef.set(
      {
        updatedAt: generatedAt,
        status: "report_generated",
        relatedReportId: reportId
      },
      { merge: true }
    );

    return payload;
  }
);
