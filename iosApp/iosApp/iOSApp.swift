import SwiftUI
import FirebaseCore
import FirebaseAuth
import FirebaseFirestore
import FirebaseFunctions
import FirebaseStorage
import UserNotifications
// import SharedLogic  // Uncomment after KMP xcframework is linked in Xcode

// MARK: - App Delegate (Firebase + Koin initialization)
class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]? = nil
    ) -> Bool {
        
        // 1. Initialize Firebase (must be first)
        FirebaseApp.configure()
        if let useEmulator = Bundle.main.object(forInfoDictionaryKey: "USE_FIREBASE_EMULATOR") as? String,
           useEmulator.uppercased() == "YES" {
            Auth.auth().useEmulator(withHost: "127.0.0.1", port: 9099)
            Firestore.firestore().useEmulator(withHost: "127.0.0.1", port: 8080)
            Functions.functions(region: "europe-west1").useEmulator(withHost: "127.0.0.1", port: 5001)
            Storage.storage().useEmulator(withHost: "127.0.0.1", port: 9199)
        }
        
        // 2. Initialize Koin (Shared KMP Dependency Injection)
        // SharedLogicKt.initKoin()   // Uncomment after linking SharedLogic xcframework
        
        // 3. Request Push Notification permission (needed for FCM)
        UNUserNotificationCenter.current().delegate = self
        let authOptions: UNAuthorizationOptions = [.alert, .badge, .sound]
        UNUserNotificationCenter.current().requestAuthorization(
            options: authOptions,
            completionHandler: { _, _ in }
        )
        application.registerForRemoteNotifications()
        
        return true
    }
    
    // Called when APNs returns a device token — Firebase uses this for FCM
    func application(
        _ application: UIApplication,
        didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data
    ) {
        // Messaging.messaging().apnsToken = deviceToken  // Uncomment after adding FirebaseMessaging
    }
    
    // Handle foreground notifications
    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        completionHandler([.banner, .badge, .sound])
    }
}

// MARK: - App Entry Point
@main
struct iOSApp: App {
    
    // Connect AppDelegate to SwiftUI lifecycle
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    
    // Auth state drives root navigation
    @StateObject private var authState = AuthStateManager()
    
    var body: some Scene {
        WindowGroup {
            ContentView()
                .environmentObject(authState)
        }
    }
}
