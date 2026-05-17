Bu, Android ve iOS platformlarını hedefleyen bir Kotlin Multiplatform projesidir.

* **[/iosApp](https://www.google.com/search?q=./iosApp/iosApp)** bir iOS uygulaması içerir. Kullanıcı arayüzünüzü (UI) Compose Multiplatform ile paylaşıyor olsanız bile, iOS uygulamanız için bu giriş noktasına (entry point) ihtiyacınız vardır. Projeniz için SwiftUI kodunu eklemeniz gereken yer de burasıdır.
* **[/sharedLogic](https://www.google.com/search?q=./sharedLogic/src)** projedeki uygulama hedefleri (targets) arasında paylaştırılacak kodlar içindir. En önemli alt klasör **[commonMain](https://www.google.com/search?q=./sharedLogic/src/commonMain/kotlin)** klasörüdür. Tercih ederseniz, buradaki platforma özel klasörlere de kod ekleyebilirsiniz.
* **[/sharedUI](https://www.google.com/search?q=./sharedUI/src)** Compose Multiplatform uygulamalarınız arasında paylaşılacak kodlar içindir. Birkaç alt klasör içerir:
* **[commonMain](https://www.google.com/search?q=./sharedUI/src/commonMain/kotlin)** tüm hedefler için ortak olan kodlar içindir.
* Diğer klasörler, yalnızca klasör adında belirtilen platform için derlenecek Kotlin kodları içindir. Örneğin, Kotlin uygulamanızın iOS kısmı için Apple'ın CoreCrypto kütüphanesini kullanmak istiyorsanız, bu tür çağrılar için doğru yer **[iosMain](https://www.google.com/search?q=./sharedUI/src/iosMain/kotlin)** klasörüdür. Benzer şekilde, Masaüstü (JVM) uygulamasına özel kısmı düzenlemek istiyorsanız, **[jvmMain](https://www.google.com/search?q=./sharedUI/src/jvmMain/kotlin)** klasörü uygun konumdur.



### Uygulamaları çalıştırma

IDE'nizin araç çubuğundaki çalıştırma widget'ı (run widget) tarafından sağlanan çalıştırma yapılandırmalarını (run configurations) kullanın. Ayrıca şu komutları ve seçenekleri de kullanabilirsiniz:

* **Android uygulaması:** `./gradlew :androidApp:assembleDebug`
* **iOS uygulaması:** [/iosApp](https://www.google.com/search?q=./iosApp) dizinini Xcode'da açın ve oradan çalıştırın.
* **Yerel arka uç (Backend) + Firebase emülatör yığını:** `make dev`

### Testleri çalıştırma

IDE'nizin editör kenar boşluğundaki (gutter) çalıştırma butonunu kullanın veya testleri Gradle görevlerini (tasks) kullanarak çalıştırın:

* **Android testleri:** `./gradlew :sharedUI:testAndroidHostTest :sharedLogic:testAndroidHostTest`
* **iOS testleri:** `./gradlew :sharedLogic:iosSimulatorArm64Test`

---

[Kotlin Multiplatform]() hakkında daha fazla bilgi edinin…
