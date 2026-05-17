# GirişimKolay — iOS Firebase & KMP Entegrasyon Notları

## Bu Dosya Hakkında
Bu dosya, iOS geliştirici ortamındaki manuel adımları takip etmek için oluşturulmuştur.
Kod değişiklikleri otomatik uygulandı, aşağıdaki adımları **Xcode üzerinden** yapmanız gerekiyor.

---

## ✅ Tamamlanan (Kod Değişiklikleri)
- [x] `GoogleService-Info.plist` doğru dizine taşındı: `iosApp/iosApp/`
- [x] `iOSApp.swift` → `AppDelegate` + `FirebaseApp.configure()` eklendi
- [x] Push Notification izin isteme akışı eklendi
- [x] `initKoin()` bağlantı noktası (comment ile) hazır bırakıldı
- [x] `ContentView.swift` → Auth durumuna göre Login veya Ana Sekme gösteriyor
- [x] `AuthScreen.swift` → Login + Register ekranları (Firestore entegrasyonlu)
- [x] `UserRepository.swift` → Firestore okuma/yazma + Chat mesajı kaydetme katmanı

---

## ⏳ Sizin Yapmanız Gereken Manuel Xcode Adımları

### Adım 1: `GoogleService-Info.plist` Dosyasını Target'a Ekleyin
1. Xcode'u açın (`iosApp.xcodeproj` veya `iosApp.xcworkspace`)
2. Sol panel > `iosApp` grup → Sağ tık → **"Add Files to iosApp..."**
3. `iosApp/iosApp/GoogleService-Info.plist` dosyasını seçin
4. ⚠️ **"Add to targets: iosApp"** işaretli olduğundan emin olun → Finish

### Adım 2: Firebase SDK'yı Swift Package Manager ile Ekleyin
1. Xcode menü → **File > Add Package Dependencies...**
2. URL: `https://github.com/firebase/firebase-ios-sdk`
3. **En son stable versiyon**u seçin → Add Package
4. Çıkan listeden şunları seçin:
   - ✅ `FirebaseAuth`
   - ✅ `FirebaseFirestore`
   - ✅ `FirebaseAnalytics`
   - ✅ `FirebaseCrashlytics`
   - ✅ `FirebaseMessaging` (Push Notifications için)
5. → Add Package

### Adım 3: Push Notification Capability (Özellik) Ekleyin
1. Sol panelde `iosApp` (mavi ikon) → **Signing & Capabilities** sekmesi
2. Sol altta **"+ Capability"** → `Push Notifications` ekleyin
3. Aynı yere **"Background Modes"** ekleyip → `Remote notifications` işaretleyin

### Adım 4: Crashlytics Build Phase Script Ekleyin
1. **Build Phases** sekmesi → Sol üstteki **"+"** → **"New Run Script Phase"**
2. Aşağıdaki scripti yapıştırın:
```bash
"${BUILD_DIR%Build/*}/SourcePackages/checkouts/firebase-ios-sdk/Crashlytics/run"
```
3. **Input Files** kısmına şunu ekleyin:
```
$(SRCROOT)/$(BUILT_PRODUCTS_DIR)/$(INFOPLIST_PATH)
```

### Adım 5: Temiz Derleme (Clean Build)
`Cmd + Shift + K` → Clean Build Folder  
`Cmd + R` → Run

---

## 🧪 Beklenen Sonuç
- Uygulama simülatörde açılıyor ve çökmeye başlamıyor
- Xcode konsolunda `FirebaseApp.configure()` logu görünüyor
- Login ekranı karşılıyor (kayıtlı kullanıcı yoksa)
