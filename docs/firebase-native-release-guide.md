# Firebase-Native Yayına Alma Rehberi

Bu doküman, GirişimKolay projesini gerçek Firebase projelerine bağlamak, Cloud Functions'ı güvenli şekilde deploy etmek ve fiziksel cihazda acceptance test yapmak için tek referans noktasıdır.

## 1. Mobil istemci yapılandırmaları

### Android

Yapılacaklar:

1. Firebase Console'da `com.anlarsinsoftware.girisimkolay` package name'i ile Android uygulamasını ekle.
2. Gerekli SHA-1 ve SHA-256 fingerprint değerlerini gir.
3. `google-services.json` dosyasını indir.
4. Dosyayı şu yola koy:
   - `/Users/mahmutcanconger/GirisimKolay/androidApp/google-services.json`
5. Android Studio'da Gradle Sync çalıştır.
6. Şu komutla doğrula:
   - `./gradlew :androidApp:compileDebugKotlin`

Best practice:

- Staging ve production için ayrı Firebase projeleri kullan.
- İstersen debug ve release için farklı `google-services.json` dosyaları kullan:
  - `androidApp/src/debug/google-services.json`
  - `androidApp/src/release/google-services.json`

### iOS

Yapılacaklar:

1. Xcode'da gerçek bundle identifier değerini kontrol et.
2. Firebase Console'da aynı bundle identifier ile iOS uygulamasını ekle.
3. `GoogleService-Info.plist` dosyasını indir.
4. Xcode içinde `iosApp` target'ına ekle.
5. `Build Phases > Copy Bundle Resources` altında göründüğünü doğrula.
6. Dosyayı repoya koymayacaksan ignore et. Bu repo zaten şu satırla güvence altında:
   - `/iosApp/iosApp/GoogleService-Info.plist`

## 2. Cloud Functions kurulumu ve deploy

Repo içinde yardımcı komutlar hazır:

- Bağımlılık yükleme:
  - `make functions-install`
- Functions build:
  - `make functions-build`
- Staging rules deploy:
  - `make rules-deploy-staging`
- Production rules deploy:
  - `make rules-deploy-production`
- Staging functions deploy:
  - `make functions-deploy-staging`
- Production functions deploy:
  - `make functions-deploy-production`

Elle çalıştırmak istersen:

1. `cd /Users/mahmutcanconger/GirisimKolay/functions`
2. `npm ci`
3. `npm run build`
4. `cd /Users/mahmutcanconger/GirisimKolay`
5. `firebase login`
6. `firebase use staging`
7. `firebase deploy --only functions`

Production için:

1. `firebase use production`
2. `firebase deploy --only functions`

### Cost-efficiency için kodda tanımlı runtime limitleri

Kod içinde şu sınırlar tanımlandı:

- `extractProfileSnapshot`
  - `256MiB`
  - `20s`
  - `concurrency: 20`
  - `maxInstances: 5`
- `sendChatMessage`
  - `512MiB`
  - `30s`
  - `concurrency: 10`
  - `maxInstances: 5`
- `generateRoadmapReport`
  - `1GiB`
  - `60s`
  - `concurrency: 2`
  - `maxInstances: 3`

Amaç:

- Boşta maliyeti düşük tutmak
- Gemini kaynaklı patlayan eşzamanlı kullanım riskini sınırlamak
- PDF üretimini daha ağır ama kontrollü ayırmak

## 3. Secret Manager kurulumu

Bu projede plain environment variable kullanılmamalı.

Kurulum:

1. `firebase use staging`
2. `firebase functions:secrets:set GEMINI_API_KEY`
3. Secret değerini CLI istemine gir
4. Ardından:
   - `firebase deploy --only functions`

Production için aynı akış:

1. `firebase use production`
2. `firebase functions:secrets:set GEMINI_API_KEY`
3. `firebase deploy --only functions`

Not:

- Kod zaten `defineSecret("GEMINI_API_KEY")` ve `secrets: [GEMINI_API_KEY]` kullanıyor.
- Secret rotasyonu gerektiğinde aynı komutu tekrar çalıştırıp yeniden deploy et.

## 4. Firestore ve Storage rules deploy

Staging:

1. `firebase use staging`
2. `firebase deploy --only firestore:rules,storage`

Production:

1. `firebase use production`
2. `firebase deploy --only firestore:rules,storage`

Bu repo içindeki mevcut politika:

- Kullanıcı yalnız kendi `/users/{uid}` dokümanını okuyup güncelleyebilir
- `chatSessions` ve `reports` altına client write kapalıdır
- PDF dosyaları yalnız owner read ile açılır
- Client Storage write kapalıdır

## 5. Fiziksel cihaz acceptance test listesi

### Hazırlık

1. Uygulamayı temiz kurulumla yükle
2. Firebase projesinin staging ya da production olduğundan emin ol
3. Functions deploy edilmiş olsun
4. Rules deploy edilmiş olsun
5. Secret tanımlı olsun

### Auth

1. Yeni kullanıcı kaydı oluştur
2. Çıkış yap
3. Aynı kullanıcıyla tekrar giriş yap
4. Beklenen:
   - crash yok
   - giriş sonrası ana ekran açılıyor

### Profil

1. Profil alanlarını doldur
2. Kaydet
3. Uygulamayı kapatıp aç
4. Beklenen:
   - profil Firestore'dan geri geliyor

### Chat

1. Chat ekranında gerçek bir soru gönder
2. Beklenen:
   - kullanıcı mesajı hemen görünür
   - AI cevabı gelir
   - citation alanları dolu gelir
3. Uygulamayı kapatıp aç
4. Beklenen:
   - aynı oturum geçmişi geri yüklenir

### Profile Extraction

1. Serbest metin kullan:
   - `Yeni mezunum, Etsy üzerinden satış yapmak istiyorum, hibe arıyorum`
2. Beklenen:
   - anlamlı sektör ve finansman sinyali döner

### Report Generation

1. Chat oturumu varken rapor üret
2. Beklenen:
   - Firestore `reports` metadata oluşur
   - Storage'da PDF oluşur
   - `downloadUrl` açılır
3. Uygulamayı kapatıp aç
4. Beklenen:
   - rapor metadata'sı geri gelir

### Negatif testler

1. İnternet kapalıyken chat gönder
2. Girişsiz rapor üretmeye çalış
3. Başka kullanıcı verisine erişim denenirse görünmemeli

## 6. Senin manuel yapman gerekenler

### Firebase Console

1. `girisimkolay-staging` ve `girisimkolay-production` projelerini doğrula
2. Her iki projede:
   - Authentication
   - Firestore
   - Storage
   - Cloud Functions
   servislerini aç
3. Authentication içinde `Email/Password` provider'ı aktif et
4. Android app ekle
5. iOS app ekle
6. Android için `google-services.json` indir
7. iOS için `GoogleService-Info.plist` indir
8. Billing aktif mi kontrol et

### IDE / Dosya yerleşimi

1. `google-services.json` dosyasını `androidApp/` içine koy
2. `GoogleService-Info.plist` dosyasını Xcode target'ına ekle
3. Android Studio'da Gradle Sync yap
4. Xcode'da package resolve yap

### CLI

1. `make functions-install`
2. `make functions-build`
3. `firebase login`
4. `firebase use staging`
5. `firebase functions:secrets:set GEMINI_API_KEY`
6. `make rules-deploy-staging`
7. `make functions-deploy-staging`
8. Staging acceptance testleri geçince:
   - `firebase use production`
   - `firebase functions:secrets:set GEMINI_API_KEY`
   - `make rules-deploy-production`
   - `make functions-deploy-production`
