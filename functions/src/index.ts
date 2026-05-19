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
  invoker: "public" as const,
  secrets: [GEMINI_API_KEY],
  memory: "256MiB" as const,
  timeoutSeconds: 20,
  concurrency: 20,
  maxInstances: 5,
  minInstances: 0
};
const CHAT_RUNTIME = {
  region: REGION,
  invoker: "public" as const,
  secrets: [GEMINI_API_KEY],
  memory: "512MiB" as const,
  timeoutSeconds: 30,
  concurrency: 10,
  maxInstances: 5,
  minInstances: 0
};
const REPORT_RUNTIME = {
  region: REGION,
  invoker: "public" as const,
  secrets: [GEMINI_API_KEY],
  memory: "1GiB" as const,
  timeoutSeconds: 60,
  concurrency: 2,
  maxInstances: 3,
  minInstances: 0
};

const SYSTEM_PROMPT = `Sen GirişimKolay'ın yapay zeka iş danışmanısın. Türkiye'de girişimcilik, şirket kuruluşu, vergi, SGK, KOSGEB destekleri ve e-ticaret konularında uzman seviyesinde yardımcı oluyorsun.

Kurallar:
- Her zaman Türkçe cevap ver.
- Kamuya açık mevzuat bilgilerini (şirket kuruluş prosedürleri, KOSGEB program detayları, vergi eşikleri, SGK oranları, MERSİS süreci vb.) doğrudan ve güvenle paylaş.
- Genel prosedürler ve maliyetler için "uzman öner" deme — bu bilgiler herkesin erişebileceği kamuya açık bilgilerdir.
- Sadece kişiye özel vergi planlaması, hukuki temsil veya bireysel muhasebe danışmanlığı gerektiren durumlarda SMMM/avukat yönlendirmesi yap.
- Cevaplar somut, net ve uygulanabilir olsun. Mümkünse rakamlar, süreler ve adımlar ver.
- Kullanıcıya bir sonraki somut adımı mutlaka söyle.
- Asla boş cevap döndürme; her zaman yönlendirici ve işe yarar bir cevap üret.`;

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

type ChatMode = "NORMAL" | "ROADMAP" | "DEEP_RESEARCH";

type ChatLogContext = {
  uid: string;
  sessionId: string;
  mode: ChatMode;
  retrievedCount: number;
  confidence: number;
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

// ─── KNOWLEDGE BASE ───────────────────────────────────────────────────────────
// Her belge gerçek Türk mevzuatına ve resmi kaynaklara dayanmaktadır.
// Belge metinleri TF-IDF retrieval için yeterince uzun tutulmuştur.
const KNOWLEDGE_BASE: KnowledgeDocument[] = [
  {
    id: "sahis_sirketi_kurulusu",
    sourceName: "Türk Ticaret Kanunu & Vergi Usul Kanunu",
    documentType: "Şirket Kuruluşu",
    section: "Şahıs Şirketi Kurulum Süreci",
    publishedAt: "2026-01-05",
    sourceUrl: "https://www.resmigazete.gov.tr/",
    version: "2026.1",
    text: "Şahıs şirketi (ferdi işletme) Türkiye'de en hızlı ve düşük maliyetli kuruluş modelidir. Sermaye şartı yoktur. Kuruluş için önce vergi dairesine başvurulur, işe başlama bildirimi yapılır ve mükellefiyet açılır. Ticaret siciline tescil zorunlu değildir ancak esnaf sicil kaydı gerekebilir. Sahibi tüm borçlardan şahsen ve sınırsız sorumludur. E-ticaret yapan küçük girişimciler, serbest meslek erbabı ve tek kişilik hizmet işletmeleri için sıklıkla tercih edilir. Yıllık ciro belirli eşiği aşarsa gerçek usul vergilendirmeye geçiş zorunlu olur. Muhasebeci ya da mali müşavir zorunluluğu yoktur ancak defter tutma ve beyanname yükümlülüğü doğar."
  },
  {
    id: "limited_sirket_kurulusu",
    sourceName: "Türk Ticaret Kanunu Md.573-644",
    documentType: "Şirket Kuruluşu",
    section: "Limited Şirket Kurulum Süreci",
    publishedAt: "2026-01-05",
    sourceUrl: "https://www.resmigazete.gov.tr/",
    version: "2026.1",
    text: "Limited şirket kurmak için en az 10.000 TL sermaye gerekir. Bir veya birden fazla ortak olabilir. Kuruluş için önce MERSİS sistemi üzerinden başvuru yapılır, ardından noterden imza beyannamesi ve şirket sözleşmesi onaylatılır, ticaret siciline tescil ve ilan edilir. Ortakların sorumluluğu koydukları sermaye ile sınırlıdır. Kurumlar vergisine tabidir. Daha kurumsal yapı, ortaklı iş ve büyüme senaryoları için tercih edilir. Muhasebeci ya da mali müşavir zorunludur. Yönetim kurulu ya da müdür atanması gerekir. Tescil sonrası vergi dairesine ve SGK'ya bildirim yapılır."
  },
  {
    id: "anonim_sirket_genel",
    sourceName: "Türk Ticaret Kanunu Md.329-563",
    documentType: "Şirket Kuruluşu",
    section: "Anonim Şirket Özellikleri",
    publishedAt: "2026-01-05",
    sourceUrl: "https://www.resmigazete.gov.tr/",
    version: "2026.1",
    text: "Anonim şirket en az 50.000 TL sermaye ile kurulur. Halka arz edilebilir, pay senetleri çıkarılabilir. En az bir kurucu ortak yeterlidir. Yönetim kurulu zorunludur. Kurumlar vergisine tabidir. Büyük ölçekli işletmeler, yatırımcı alma ve halka arz planlayanlar için uygundur. Küçük girişimciler için genellikle limited şirket daha pratiktir. Kuruluş süreci limited şirkete benzer: MERSİS başvurusu, noter, ticaret sicili ve vergi dairesi kaydı."
  },
  {
    id: "mersis_ticaret_sicili",
    sourceName: "Gümrük ve Ticaret Bakanlığı MERSİS Sistemi",
    documentType: "Kuruluş Prosedürü",
    section: "MERSİS ve Ticaret Sicili",
    publishedAt: "2026-01-10",
    sourceUrl: "https://mersis.gtb.gov.tr/",
    version: "2026.1",
    text: "MERSİS (Merkezi Sicil Kayıt Sistemi), Türkiye'de şirket ve ticari işletme kuruluşlarının merkezi olarak yapıldığı online platformdur. Limited ve anonim şirket kuruluşları için MERSİS üzerinden başvuru zorunludur. Başvuruda şirket unvanı, faaliyet konusu, sermaye miktarı, ortak bilgileri ve yönetici bilgileri girilir. Onay sonrası ticaret siciline tescil edilir ve MERSİS numarası alınır. Bu numara şirketin resmi kimliğidir ve tüm ticari faaliyetlerde kullanılır. E-ticaret sitelerinde de MERSİS numarasının yayınlanması 6563 sayılı Kanun kapsamında zorunludur."
  },
  {
    id: "kdv_mukellefiyet",
    sourceName: "3065 Sayılı KDV Kanunu",
    documentType: "Vergi Kanunu",
    section: "KDV Mükellefiyet Türleri",
    publishedAt: "2026-01-01",
    sourceUrl: "https://www.gib.gov.tr/",
    version: "2026.1",
    text: "KDV (Katma Değer Vergisi) Türkiye'de mal ve hizmet teslimlerinde uygulanan en temel vergidir. Standart oran %20'dir; gıda, ilaç ve bazı temel ürünlerde %1 veya %10 indirimli oran uygulanır. Şirket kuran her girişimci KDV mükellefi olmak zorundadır ve her ay ya da üç ayda bir KDV beyannamesi vermek zorundadır. Basit usul mükellefler KDV'den muaf tutulabilir. Girişimcinin aldığı mal ve hizmetler için ödediği KDV'yi (yüklenilen KDV), müşterisine tahsil ettiği KDV'den mahsup edebilir. E-ticaret ve yazılım satışlarında da KDV uygulanır."
  },
  {
    id: "gelir_vergisi_girisimci",
    sourceName: "193 Sayılı Gelir Vergisi Kanunu",
    documentType: "Vergi Kanunu",
    section: "Gelir Vergisi Dilimleri ve Girişimciler",
    publishedAt: "2026-01-01",
    sourceUrl: "https://www.gib.gov.tr/",
    version: "2026.1",
    text: "Şahıs şirketi sahibi ve serbest meslek erbabı gerçek kişiler gelir vergisine tabidir. 2026 yılı için gelir vergisi dilimleri: 110.000 TL'ye kadar %15, 230.000 TL'ye kadar %20, 580.000 TL'ye kadar %27, 3.000.000 TL'ye kadar %35, üzeri %40 olarak uygulanmaktadır. Yıllık gelir vergisi beyannamesi Mart ayında verilir. Geçici vergi ise üç ayda bir ödenir. Giderler (kira, telefon, bilgisayar, ulaşım, reklam) vergi matrahından düşülebilir. İyi bir muhasebeci ile çalışmak vergi optimizasyonu açısından önemlidir."
  },
  {
    id: "kurumlar_vergisi",
    sourceName: "5520 Sayılı Kurumlar Vergisi Kanunu",
    documentType: "Vergi Kanunu",
    section: "Kurumlar Vergisi",
    publishedAt: "2026-01-01",
    sourceUrl: "https://www.gib.gov.tr/",
    version: "2026.1",
    text: "Limited şirket ve anonim şirket gibi sermaye şirketleri kurumlar vergisine tabidir. 2026 yılı için kurumlar vergisi oranı %25'tir. Yıllık kurumlar vergisi beyannamesi Nisan ayında verilir. Geçici vergi üç ayda bir ödenir. Şirketin giderleri (personel, kira, amortisman, faiz) matrahtan düşülebilir. Yatırım teşviklerinden yararlanan şirketler için indirimli kurumlar vergisi uygulanabilir. Kâr dağıtımında ayrıca %10 stopaj vergisi uygulanır."
  },
  {
    id: "kosgeb_mikro",
    sourceName: "KOSGEB 2026 Destek Programı Yönetmeliği",
    documentType: "Destek Programı",
    section: "Mikro ve Küçük İşletme Destekleri",
    publishedAt: "2026-02-10",
    sourceUrl: "https://www.kosgeb.gov.tr/",
    version: "2026.2",
    text: "KOSGEB (Küçük ve Orta Ölçekli İşletmeleri Geliştirme ve Destekleme İdaresi) mikro işletmelere çeşitli destekler sunar. E-ticaret altyapısı, web sitesi, yazılım geliştirme, pazaryeri entegrasyonu, sosyal medya ve dijital pazarlama giderleri için 100.000 TL'ye kadar hibe desteği sağlanabilir. Başvuru için KOSGEB sistemine kayıt, işletme belgesi ve iş planı gereklidir. Destek oranı %60-75 arasında değişir, kalan kısım girişimci tarafından karşılanır. Fiziki ürün üreticileri için makine-ekipman destekleri de mevcuttur."
  },
  {
    id: "kosgeb_girisimcilik",
    sourceName: "KOSGEB Girişimcilik Destek Programı",
    documentType: "Destek Programı",
    section: "Yeni İşletme Kurma Desteği",
    publishedAt: "2026-02-10",
    sourceUrl: "https://www.kosgeb.gov.tr/",
    version: "2026.2",
    text: "KOSGEB Girişimcilik Destek Programı, yeni işletme kurmak isteyen girişimcilere destek sağlar. Uygulamalı Girişimcilik Eğitimi (UGE) tamamlanması zorunludur. Eğitim sonrası iş planı hazırlanır ve değerlendirme kuruluna sunulur. Onaylanan projelere: işletme kuruluş desteği (10.000 TL'ye kadar), makine-ekipman desteği (100.000 TL'ye kadar), kira desteği (12 ay boyunca aylık 3.000 TL'ye kadar), mentörlük desteği sağlanır. Genç girişimcilere ve kadın girişimcilere ek destekler sunulabilir."
  },
  {
    id: "kosgeb_tekno",
    sourceName: "KOSGEB Ar-Ge, İnovasyon ve Teknolojik Üretim Destek Programı",
    documentType: "Destek Programı",
    section: "Teknogirişim ve Ar-Ge Destekleri",
    publishedAt: "2026-02-10",
    sourceUrl: "https://www.kosgeb.gov.tr/",
    version: "2026.2",
    text: "Teknoloji tabanlı işletmeler ve yazılım girişimleri için KOSGEB Ar-Ge ve İnovasyon Destek Programı kapsamında 750.000 TL'ye kadar destek sağlanabilir. Patent, faydalı model, marka tescili masrafları karşılanabilir. Prototip geliştirme, test ve belgelendirme giderleri desteklenir. TÜBİTAK 1512 programıyla birlikte kullanılabilir. Üniversite sanayi iş birliği projeleri için ek destekler mevcuttur. Başvuru için işletmenin KOBİ niteliği taşıması ve teknoloji/inovasyon odaklı proje sunması gerekir."
  },
  {
    id: "tubitak_1512",
    sourceName: "TÜBİTAK 1512 Girişimcilik Aşaması Programı",
    documentType: "Destek Programı",
    section: "Teknoloji Tabanlı Girişim Desteği",
    publishedAt: "2026-01-15",
    sourceUrl: "https://www.tubitak.gov.tr/",
    version: "2026.1",
    text: "TÜBİTAK 1512 Bireysel Girişimcilik Aşamalı Destek Programı, teknoloji tabanlı iş fikirleri olan girişimcilere destek sağlar. Üç aşamadan oluşur: Fizibilite (75.000 TL'ye kadar), Prototip Geliştirme (750.000 TL'ye kadar), Piyasalaştırma. Yazılım, yapay zeka, mobil uygulama, biyoteknoloji gibi teknoloji odaklı projeler desteklenir. Destekler geri ödemesizdir. Başvurular TÜBİTAK online sisteminden yapılır. Üniversite mezunu girişimciler ve spin-off şirketler öncelikli değerlendirilir."
  },
  {
    id: "genc_girisimci_istisnasi",
    sourceName: "GVK Mükerrer Madde 20/B",
    documentType: "Vergi Kanunu",
    section: "Genç Girişimci Kazanç İstisnası",
    publishedAt: "2026-01-01",
    sourceUrl: "https://www.gib.gov.tr/",
    version: "2026.1",
    text: "29 yaşını doldurmamış gerçek kişiler, ilk defa mükellefiyet tesis ettirmeleri ve faaliyete geçmeleri koşuluyla Genç Girişimci Kazanç İstisnasından yararlanabilir. 2026 yılı için istisna tutarı yıllık 230.000 TL civarındadır (her yıl güncellenir). Bu tutara kadar olan ticari, zirai veya serbest meslek kazancı gelir vergisinden istisnadır. Şahıs şirketleri ve serbest meslek erbabı yararlanabilir; limited şirket ortakları bu istisnadan yararlanamaz. Koşul: daha önce vergi mükellefi olmamak, işe devam etmek. Faaliyetin bizzat yürütülmesi gerekir."
  },
  {
    id: "sgk_bagkur_girisimci",
    sourceName: "5510 Sayılı SGK Kanunu 4/b Maddesi",
    documentType: "SGK Mevzuatı",
    section: "Girişimci SGK Primleri ve Yükümlülükleri",
    publishedAt: "2026-01-15",
    sourceUrl: "https://www.sgk.gov.tr/",
    version: "2026.1",
    text: "Türkiye'de şirket kuran veya ticari faaliyete başlayan kişiler 4/b (Bağ-Kur) kapsamında SGK'ya kayıt olmak ve prim ödemek zorundadır. 2026 yılı asgari Bağ-Kur primi aylık yaklaşık 4.000-5.000 TL aralığındadır (güncel rakam için SGK'yı kontrol edin). Primler, prim matrahının %34.5'i olarak hesaplanır (sağlık + emeklilik). Genç girişimcilere ilk 1-2 yıl için prim desteği verilebilir. Eş zamanlı 4/a (işçi) kapsamında çalışıyorsa 4/b muafiyeti doğabilir. SGK primleri geç ödenirse gecikme faizi işler."
  },
  {
    id: "sgk_prim_desteği",
    sourceName: "4447 Sayılı İşsizlik Sigortası Kanunu & SGK Teşvikleri",
    documentType: "SGK Mevzuatı",
    section: "İşveren SGK Prim Destekleri",
    publishedAt: "2026-01-15",
    sourceUrl: "https://www.sgk.gov.tr/",
    version: "2026.1",
    text: "Yeni işçi istihdam eden işverenlere SGK prim desteği sağlanabilir. Genç, kadın ve engelli bireyleri istihdam eden işverenler ek teşviklerden yararlanır. İşkur üzerinden teşvik bildirimi yapılması gerekir. 4447 sayılı Kanun kapsamındaki teşviklerde işveren payının bir kısmı Hazine tarafından karşılanır. Destek süreleri istihdam edilen kişinin niteliğine göre 6 ay ile 5 yıl arasında değişir. Girişimciler ilk çalışanlarını işe alırken bu teşvikleri incelemelidir."
  },
  {
    id: "e_ticaret_yukumlulukler",
    sourceName: "6563 Sayılı E-Ticaret Kanunu",
    documentType: "E-Ticaret Kanunu",
    section: "E-Ticaret Genel Yükümlülükleri",
    publishedAt: "2026-02-01",
    sourceUrl: "https://www.resmigazete.gov.tr/",
    version: "2026.1",
    text: "E-ticaret yapan işletmeler şu yükümlülükleri yerine getirmek zorundadır: Ticaret unvanı veya ad-soyadı, MERSİS numarası (şirket için), KEP adresi, iletişim bilgilerini site üzerinde yayınlamak. Müşterilere cayma hakkı (genellikle 14 gün) ve iade imkânı tanımak zorundadır. Mesafeli satış sözleşmesi düzenlemek ve onay almak zorunludur. Kişisel verilerin korunması için KVKK kapsamında aydınlatma yükümlülüğü vardır. Yurt dışına satış yapılıyorsa o ülkenin mevzuatı da geçerli olabilir. Elektronik fatura düzenleme zorunluluğu belirli ciro eşiği aşıldığında doğar."
  },
  {
    id: "e_ticaret_platform_gereklilikleri",
    sourceName: "Trendyol, Hepsiburada, Amazon Türkiye Satıcı Sözleşmeleri",
    documentType: "E-Ticaret Platformu",
    section: "Pazaryeri Satıcı Gereklilikleri",
    publishedAt: "2026-01-20",
    sourceUrl: "https://www.ticaret.gov.tr/",
    version: "2026.1",
    text: "Trendyol, Hepsiburada, Amazon Türkiye ve benzer pazaryerlerinde satıcı olmak için şirket kuruluşu zorunludur; şahıs şirketi de kabul edilir. Vergi levhası, imza sirküleri veya imza beyannamesi, banka hesabı ve ürün görselleri istenir. Platform komisyon oranları %8-25 arasında değişir. IBAN bilgisi doğrulanır. Platformlar kargo anlaşmaları yapılmasını zorunlu kılabilir. İade ve iptal oranları belirli eşiği aşarsa ceza puanı uygulanabilir. Satıcı paneli üzerinden envanter, sipariş ve fatura yönetimi yapılır."
  },
  {
    id: "efatura_edefter",
    sourceName: "Gelir İdaresi Başkanlığı - e-Fatura ve e-Defter",
    documentType: "Vergi Kanunu",
    section: "Elektronik Fatura ve Defter Zorunluluğu",
    publishedAt: "2026-01-01",
    sourceUrl: "https://www.efatura.gov.tr/",
    version: "2026.1",
    text: "Yıllık cirosu 3 milyon TL'yi aşan mükellefler e-Fatura kullanmak zorundadır. E-ticaret yapan mükellefler için bu eşik 500.000 TL'ye indirilmiştir (2026 rakamları için GİB'i kontrol edin). E-Defter zorunluluğu da belirli ciro eşiklerinde geçerlidir. e-Fatura için GİB'e başvuru yapılır ve özel entegratör veya GİB portalı üzerinden kullanım sağlanır. Kâğıt fatura yerine e-Fatura düzenlemek yasal zorunluluktur ve cezai yaptırımı vardır. Ayrıca e-Arşiv fatura zorunluluğu da farklı koşullarda uygulanabilir."
  },
  {
    id: "muhasebeci_zorunluluk",
    sourceName: "3568 Sayılı Serbest Muhasebeci ve Mali Müşavirlik Kanunu",
    documentType: "Muhasebe Mevzuatı",
    section: "Muhasebeci Zorunluluğu ve Maliyetler",
    publishedAt: "2026-01-01",
    sourceUrl: "https://www.turmob.org.tr/",
    version: "2026.1",
    text: "Limited ve anonim şirketlerin muhasebe kayıtlarını tutmak için Serbest Muhasebeci Mali Müşavir (SMMM) ile sözleşme zorunludur. Şahıs şirketi sahipleri ve serbest meslek erbabı da defter tutmak zorundadır; ancak küçük ölçekliler bunu kendileri yapabilir. Mali müşavir aylık ücretleri 2026'da yaklaşık 2.000-6.000 TL arasında değişmektedir (şehre ve iş hacmine göre). İyi bir mali müşavir; beyanname verme, vergi optimizasyonu, SGK bildirimleri ve iş kuruluşu sürecinde kritik rol oynar. Vergi cezalarından korunmak için nitelikli muhasebe desteği önemlidir."
  },
  {
    id: "home_office_vergi",
    sourceName: "GVK ve KVK - İşyeri ve Kira Giderleri",
    documentType: "Vergi Kanunu",
    section: "Home Office ve İşyeri Vergi Durumu",
    publishedAt: "2026-01-01",
    sourceUrl: "https://www.gib.gov.tr/",
    version: "2026.1",
    text: "Evden çalışan girişimciler home-office giderlerini vergi matrahından düşebilir; ancak işyeri kullanım oranı belgelenmelidir. Kiralık işyeri için ödenen kira, ticari gelirden düşülebilir bir giderdir; ev sahibine stopaj yükümlülüğü doğar (%20 stopaj). Şirket adına kiralanan ofis veya co-working alanı için ödenen kira gider yazılabilir. Kendi evinden çalışıyorsa ve evi iş adresi gösteriyorsa kat maliki onayı ve çevre temizlik vergisi dikkat edilmesi gereken konulardır. Vergi dairesinde iş adresi olarak ev adresi kullanılabilir ancak bazı iş kolları için fiziksel işyeri zorunlu olabilir."
  },
  {
    id: "yatirimci_hisse_devri",
    sourceName: "Türk Ticaret Kanunu & Sermaye Piyasası Kanunu",
    documentType: "Yatırım ve Finansman",
    section: "Yatırımcı Alma, Hisse Devri ve Melek Yatırım",
    publishedAt: "2026-01-10",
    sourceUrl: "https://www.spk.gov.tr/",
    version: "2026.1",
    text: "Türkiye'de girişim şirketleri melek yatırımcılardan veya risk sermayesi fonlarından finansman sağlayabilir. Limited şirketlerde hisse devri noter huzurunda yapılır. Yabancı yatırımcılar için çeşitli prosedürler uygulanabilir. Melek yatırımcılar, Hazine tarafından lisanslanan 'Bireysel Katılım Yatırımcısı (BKY)' statüsündeyse yatırımları üzerinden vergi indirimi alabilirler. Girişim sermayesi fonları (VC) aracılığıyla alınan yatırımlar genellikle convertible note veya tercihli hisse ile yapılır. Kitle fonlaması (crowdfunding) Türkiye'de SPK lisanslı platformlar üzerinden yapılabilir."
  },
  {
    id: "kvkk_veri_koruma",
    sourceName: "6698 Sayılı Kişisel Verilerin Korunması Kanunu (KVKK)",
    documentType: "Veri Koruma",
    section: "Girişimciler için KVKK Yükümlülükleri",
    publishedAt: "2026-01-01",
    sourceUrl: "https://www.kvkk.gov.tr/",
    version: "2026.1",
    text: "Müşteri verileri toplayan tüm işletmeler KVKK kapsamında yükümlüdür. Aydınlatma metni ve açık rıza beyanı alınması zorunludur. Veri ihlali durumunda KVKK'ya bildirim yükümlülüğü vardır. E-ticaret siteleri için gizlilik politikası ve çerez politikası zorunludur. VERBİS'e (Veri Sorumluları Sicili) kayıt, yıllık ciro ve çalışan sayısına göre zorunlu olabilir. KVKK ihlallerinde idari para cezaları 50.000 TL ile 1.000.000 TL arasında uygulanabilir. Veri işleme faaliyetleri için hukuki dayanak (rıza, sözleşme, meşru menfaat) belirlenmelidir."
  }
];

// ─── TOKENIZASYON ─────────────────────────────────────────────────────────────
// Türkçe stopword listesi: retrieval kalitesini artırmak için anlamsız
// yüksek-frekanslı kelimeler çıkarılır.
const TURKISH_STOPWORDS = new Set([
  "bir", "bu", "şu", "ile", "için", "den", "dan", "ten", "tan",
  "nin", "nın", "nun", "nün", "nde", "nda", "nde", "nda",
  "var", "yok", "ama", "veya", "gibi", "kadar", "daha", "çok",
  "her", "hem", "hiç", "nasıl", "olan", "olur", "oldu", "edilir",
  "olan", "olarak", "ayrıca", "ancak", "sadece", "yani", "göre",
  "sonra", "önce", "beri", "itibaren", "üzere", "dolayı",
  "eğer", "ise", "iken", "rağmen", "karşın", "hatta", "bile",
  "tüm", "bütün", "bazı", "hiçbir", "birçok", "birkaç"
]);

function parseChatMode(value?: string | null): ChatMode {
  if (value === "ROADMAP" || value === "DEEP_RESEARCH" || value === "NORMAL") {
    return value;
  }
  return "NORMAL";
}

function tokenize(text: string): string[] {
  return text
    .toLocaleLowerCase("tr-TR")
    .replace(/[^\p{L}\p{N}\s]/gu, " ")
    .split(/\s+/)
    .filter((token) => token.length >= 2)
    .filter((token) => !TURKISH_STOPWORDS.has(token));
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

function requireUid(uid?: string): string {
  if (!uid) {
    throw new HttpsError("unauthenticated", "Kimlik doğrulama gerekli.");
  }
  return uid;
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
  if (
    text.includes("e-ticaret") || text.includes("etsy") || text.includes("amazon") ||
    text.includes("trendyol") || text.includes("hepsiburada") || text.includes("online satış") ||
    text.includes("web sitesi") || text.includes("pazaryeri")
  ) return "E-Ticaret";
  if (
    text.includes("yazılım") || text.includes("uygulama") || text.includes("ai") ||
    text.includes("yapay zeka") || text.includes("mobil") || text.includes("saas") ||
    text.includes("teknoloji") || text.includes("platform") || text.includes("app")
  ) return "Yazılım / Teknoloji";
  if (text.includes("gıda") || text.includes("kafe") || text.includes("restoran") || text.includes("yemek")) return "Gıda";
  if (text.includes("tekstil") || text.includes("giyim") || text.includes("moda") || text.includes("butik")) return "Moda / Tekstil";
  if (text.includes("danışmanlık") || text.includes("freelance") || text.includes("hizmet")) return "Danışmanlık / Hizmet";
  if (text.includes("üretim") || text.includes("imalat") || text.includes("fabrika")) return "Üretim / İmalat";
  return null;
}

function inferCompanyType(text: string): string | null {
  if (
    text.includes("şahıs") || text.includes("ferdi") || text.includes("bireysel")
  ) return "Şahıs Şirketi";
  if (
    text.includes("limited") || text.includes("ltd") || text.includes("llc") ||
    text.includes("ortaklı") || text.includes("ortak")
  ) return "Limited Şirket";
  if (
    text.includes("anonim") || text.includes("aş") || text.includes("halka arz")
  ) return "Anonim Şirket";
  return null;
}

function inferExperience(text: string): string | null {
  if (
    text.includes("yeni mezun") || text.includes("ilk kez") || text.includes("hiç bilmiyorum") ||
    text.includes("başlangıç") || text.includes("yeni başlıyorum") || text.includes("fikir aşamasında")
  ) return "Başlangıç";
  if (
    text.includes("tecrübeli") || text.includes("deneyimliyim") || text.includes("yıldır") ||
    text.includes("sektördeyim") || text.includes("çalışıyorum")
  ) return "Orta";
  return null;
}

function inferFunding(text: string): string | null {
  if (
    text.includes("hibe") || text.includes("destek") || text.includes("sermaye") ||
    text.includes("yatırım") || text.includes("finansman") || text.includes("kredi") ||
    text.includes("kosgeb") || text.includes("tubitak") || text.includes("fon")
  ) return "Dış Finansman İhtiyacı";
  return null;
}

function inferLegalConcerns(text: string): string[] {
  const concerns: string[] = [];
  if (text.includes("vergi") || text.includes("kdv") || text.includes("beyanname")) concerns.push("Vergi yükümlülükleri");
  if (text.includes("sgk") || text.includes("bağkur") || text.includes("prim") || text.includes("sigorta")) concerns.push("SGK / Bağ-Kur");
  if (text.includes("iade") || text.includes("cayma") || text.includes("tüketici")) concerns.push("Tüketici yükümlülükleri");
  if (text.includes("muhasebe") || text.includes("mali müşavir") || text.includes("muhasebeci")) concerns.push("Muhasebe desteği");
  if (text.includes("sözleşme") || text.includes("anlaşma") || text.includes("hukuk")) concerns.push("Hukuki belgeler");
  return concerns;
}

function buildNextActions(profile: ProfilingSnapshot, query: string, mode: ChatMode = "NORMAL"): string[] {
  const normalized = query.toLocaleLowerCase("tr-TR");
  const actions: string[] = [];

  if (mode === "ROADMAP") {
    actions.push("İş fikrinize göre şirket tipi ve vergi yükümlülüklerini netleştirin");
    actions.push("Kuruluş öncesi mali müşavir görüşmesi planlayın");
  }
  if (mode === "DEEP_RESEARCH") {
    actions.push("Kaynak bağlantılarını açıp güncel metni kontrol edin");
    actions.push("Belgedeki tarih ve kapsam bilgilerini kendi durumunuzla karşılaştırın");
  }

  if (normalized.includes("kosgeb") || normalized.includes("hibe") || normalized.includes("destek")) {
    actions.push("KOSGEB'e online kayıt yapın ve Girişimcilik Eğitimi tarihlerini kontrol edin");
  }
  if (normalized.includes("şirket") || normalized.includes("kuruluş") || normalized.includes("açmak")) {
    actions.push("MERSİS sistemi üzerinden şirket unvanı sorgulayın ve kuruluş sürecini başlatın");
  }
  if (normalized.includes("vergi") || normalized.includes("kdv") || normalized.includes("gelir")) {
    actions.push("Bir Serbest Muhasebeci Mali Müşavir (SMMM) ile ön görüşme yapın");
  }
  if (normalized.includes("sgk") || normalized.includes("prim") || normalized.includes("bağkur")) {
    actions.push("SGK web sitesinden 4/b (Bağ-Kur) prim hesaplayıcıyı kullanın");
  }
  if (normalized.includes("e-ticaret") || normalized.includes("online") || normalized.includes("satış")) {
    actions.push("Hedef pazaryerinin satıcı başvuru gerekliliklerini inceleyin");
  }

  if (actions.length === 0) {
    actions.push("Şirket tipinizi (Şahıs / Limited) netleştirin");
    actions.push("Bir mali müşavir ile ön görüşme planlayın");
  }
  if (profile.fundingNeed) {
    actions.push("Size uygun hibe ve teşvik programlarını (KOSGEB, TÜBİTAK) karşılaştırın");
  }

  return [...new Set(actions)].slice(0, 3);
}

function modeInstruction(mode: ChatMode): string {
  if (mode === "DEEP_RESEARCH") {
    return [
      "MOD: Derin Tarama.",
      "ZORUNLU KURAL: Her iddia için köşeli parantez içinde kaynak numarası göster → [1], [2] vb.",
      "ZORUNLU KURAL: Kaynaklarda bulunmayan bilgi için tam olarak şunu yaz: 'Bu konuya ilişkin doğrulanmış veri bulunmamaktadır.' Asla tahmin etme.",
      "Cevabını tam olarak aşağıdaki 3 bölümle yaz:",
      "**Kaynakların Özeti:** (her madde [N] ile etiketlenmiş, kaynakta ne yazıyor)",
      "**Değerlendirme:** (kaynaklara dayalı, tarafsız yorum)",
      "**Doğrulama Gerektiren Noktalar:** (profesyonel teyit gereken hususlar, belirsiz kalan konular)"
    ].join("\n");
  }
  if (mode === "ROADMAP") {
    return [
      "MOD: Yol Haritası.",
      "Cevabını TAM OLARAK aşağıdaki 5 markdown başlığıyla yaz. Hiçbir başlığı atlama ve sırasını değiştirme:",
      "## Kısa Değerlendirme",
      "(1-2 cümle: kullanıcının mevcut durumu ve amacı nedir)",
      "## Adım Adım Yol Haritası",
      "(numaralı liste; her madde somut ve uygulanabilir — süre ve yaklaşık maliyet bilgisi varsa ekle)",
      "## Hazırlanacak Belgeler",
      "(madde listesi: belge adı — nereden / nasıl alınır)",
      "## Dikkat Edilecek Noktalar",
      "(madde listesi: mevzuat riskleri, dikkat gerektiren vergi veya yasal yükümlülükler)",
      "## Sonraki En Mantıklı Adım",
      "(tek cümle: şu anda yapılacak en önemli işlem)",
      "Prosedürler, maliyetler ve süreler hakkında somut bilgi ver. SMMM yönlendirmesini sadece gerçekten kişiye özel muhasebe/hukuk durumlarında ekle, genel bilgiler için ekleme."
    ].join("\n");
  }
  return [
    "MOD: Normal.",
    "Kullanıcının sorusuna doğrudan ve pratik cevap ver. Genel mevzuat bilgilerini güvenle paylaş.",
    "SMMM veya uzman yönlendirmesi yapma — sadece gerçekten kişiye özel muhasebe kararı gerektiren durumlarda ekle.",
    "Cevap formatı: kısa ve net özet, önemli noktalar (madde listesi), bir sonraki somut adım.",
    "Kullanıcıyı sadece belge okumaya bırakma; bilgiyi doğrudan ver."
  ].join("\n");
}

async function generateAnswer(
  query: string,
  citations: Citation[],
  profile: ProfilingSnapshot,
  insufficientEvidence: boolean,
  mode: ChatMode,
  logContext: ChatLogContext
): Promise<string> {
  const apiKey = GEMINI_API_KEY.value();
  if (!apiKey) {
    console.error("[Chat] Gemini API anahtarı bulunamadı. Fallback cevap dönülüyor.", {
      ...logContext,
      failureType: "missing_secret"
    });
    return fallbackAnswer(query, citations);
  }

  try {
    const client = new GoogleGenAI({ apiKey });

    const contextLines: string[] = citations.length > 0
      ? [
          "İlgili mevzuat bağlamı:",
          ...citations.map(
            (c, i) =>
              `${i + 1}. ${c.sourceName} — ${c.section ?? "Genel"}: ${c.snippet ?? ""}`
          )
        ]
      : [
          "Doğrudan eşleşen kaynak bulunamadı.",
          "Genel Türk girişimcilik mevzuatı ve kamu bilgisinden yararlanarak somut, uygulanabilir bir cevap ver."
        ];

    const evidenceNote = insufficientEvidence
      ? (mode === "DEEP_RESEARCH"
          ? "\nUYARI: Doğrudan eşleşen belge bulunamadı. Sadece emin olduğun kamuya açık bilgileri ver; belirsiz noktalarda bunu açıkça belirt."
          : "\nNOT: Doğrudan eşleşen belge bulunamadı. Genel Türk mevzuatı bilginden yararlanarak somut ve uygulanabilir cevap ver.")
      : "";

    const prompt = [
      ...contextLines,
      "",
      modeInstruction(mode),
      "",
      `Kullanıcı sorusu: ${query}`,
      `Kullanıcı profili: ${JSON.stringify(profile)}`,
      evidenceNote
    ].join("\n");

    const temperature =
      mode === "DEEP_RESEARCH" ? 0.1 :
      mode === "ROADMAP" ? 0.3 : 0.7;

    const response = await client.models.generateContent({
      model: MODEL_NAME,
      contents: prompt,
      config: {
        systemInstruction: SYSTEM_PROMPT,
        temperature
      }
    });

    const generatedText = response.text?.trim();
    if (!generatedText) {
      console.error("[Chat] Gemini boş cevap döndürdü. Fallback cevap dönülüyor.", {
        ...logContext,
        failureType: "empty_response"
      });
      return fallbackAnswer(query, citations);
    }
    return generatedText;
  } catch (error) {
    console.error("[Chat] Gemini cevap üretimi başarısız.", {
      ...logContext,
      failureType: "generation_failed",
      error
    });
    return fallbackAnswer(query, citations);
  }
}

function fallbackAnswer(query: string, citations: Citation[]): string {
  if (citations.length === 0) {
    return `"${query.slice(0, 60)}" sorunuz için doğrudan eşleşen mevzuat bağlamı bulunamadı. Şirket tipi, sektör ve hedeflerinizi belirtirseniz daha spesifik bir yol haritası sunabilirim. Ayrıca bir Serbest Muhasebeci Mali Müşavir (SMMM) ile ön görüşme yapmanızı öneririm.`;
  }
  return `Sorunuz için en ilgili kaynak: "${citations[0].sourceName} — ${citations[0].section ?? "Genel"}". Bu başlığı daha ayrıntılı incelemenizi ve bir mali müşavir ile konuyu doğrulamanızı öneririm. Şirket türü, vergi yükü ve destek uygunluğunu birlikte daraltabiliriz; lütfen durumunuzu biraz daha açar mısınız?`;
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

// ─── CALLABLE FUNCTIONS ───────────────────────────────────────────────────────

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
      mode?: string | null;
    };

    const text = data.text?.trim();
    const clientRequestId = data.clientRequestId?.trim();
    const mode = parseChatMode(data.mode);
    if (!text || !clientRequestId) {
      throw new HttpsError("invalid-argument", "Metin ve istek kimliği gerekli.");
    }

    const db = admin.firestore();
    const now = Date.now();
    const isNewSession = !data.sessionId?.trim();
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
        mode: payload.mode ?? mode,
        insufficientEvidence: (payload.confidence ?? 0) < 0.05
      };
    }

    // ── Retrieval ────────────────────────────────────────────────────────────
    const retrieved = retrieveDocuments(text, 3);
    const citations: Citation[] = retrieved.map((doc) => ({
      sourceName: doc.sourceName,
      section: doc.section,
      snippet: doc.text.slice(0, 400),
      sourceUrl: doc.sourceUrl
    }));

    const maxScore = retrieved.length
      ? Math.max(...retrieved.map((d) => d.score))
      : 0;
    const avgConfidence = retrieved.length
      ? Number((retrieved.reduce((sum, item) => sum + item.score, 0) / retrieved.length).toFixed(4))
      : 0;
    const insufficientEvidence = retrieved.length === 0 || maxScore < 0.05;

    // ── Gözlemlenebilirlik ───────────────────────────────────────────────────
    console.log("[Chat] Retrieval sonucu:", {
      uid: uid.slice(0, 8),
      sorguUzunlugu: text.length,
      bulunanBelgeSayisi: retrieved.length,
      belgePuanlari: retrieved.map((d) => ({ id: d.id, puan: d.score.toFixed(4) })),
      maxPuan: maxScore.toFixed(4),
      ortPuan: avgConfidence,
      yetersizKanit: insufficientEvidence,
      mode
    });

    // ── Profil ve Sonraki Adımlar ────────────────────────────────────────────
    const profileDelta = buildProfileSnapshot(text, null);
    const nextActions = buildNextActions(profileDelta, text, mode);

    // ── Gemini Cevap Üretimi ─────────────────────────────────────────────────
    const answer = await generateAnswer(text, citations, profileDelta, insufficientEvidence, mode, {
      uid: uid.slice(0, 8),
      sessionId,
      mode,
      retrievedCount: retrieved.length,
      confidence: avgConfidence
    });

    // ── Firestore Yazma ──────────────────────────────────────────────────────
    await sessionRef.set(
      {
        id: sessionId,
        uid,
        ...(isNewSession ? { title: text.slice(0, 60), createdAt: now } : {}),
        status: "active",
        updatedAt: now + 1,
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
      role: "user",
      mode
    });

    const aiMessage = {
      id: aiMessageId,
      sessionId,
      text: answer,
      isFromUser: false,
      timestamp: now + 1,
      citations,
      profileDelta,
      confidence: avgConfidence,
      nextActions,
      requestId: clientRequestId,
      role: "assistant",
      mode
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
      confidence: avgConfidence,
      nextActions,
      mode,
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
    const lastUserMessage = messages.filter((m) => m.isFromUser).at(-1)?.text ?? "";
    const nextActions = latestProfileSnapshot
      ? buildNextActions(latestProfileSnapshot as ProfilingSnapshot, lastUserMessage, "ROADMAP")
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
