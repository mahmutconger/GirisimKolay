import SwiftUI
import FirebaseFirestore
import FirebaseAuth

// MARK: - Auth State Manager
// A global ObservableObject that listens for Firebase Auth state changes
// and drives the root navigation (Login vs. Main App).
class AuthStateManager: ObservableObject {
    @Published var isSignedIn: Bool = false
    private var handle: AuthStateDidChangeListenerHandle?

    init() {
        handle = Auth.auth().addStateDidChangeListener { [weak self] _, user in
            DispatchQueue.main.async {
                self?.isSignedIn = user != nil
            }
        }
    }

    deinit {
        if let handle = handle {
            Auth.auth().removeStateDidChangeListener(handle)
        }
    }
}

// MARK: - Login Screen (Production Template)
struct LoginScreen: View {
    @State private var email: String = ""
    @State private var password: String = ""
    @State private var isLoading: Bool = false
    @State private var errorMessage: String? = nil

    var body: some View {
        NavigationView {
            VStack(spacing: 32) {
                Spacer()
                
                // Logo / Brand
                VStack(spacing: 12) {
                    Image(systemName: "chart.line.uptrend.xyaxis.circle.fill")
                        .resizable()
                        .frame(width: 72, height: 72)
                        .foregroundColor(.blue)
                    
                    Text("GirişimKolay")
                        .font(.largeTitle)
                        .fontWeight(.black)
                    
                    Text("Yapay Zeka Destekli Girişimcilik")
                        .font(.subheadline)
                        .foregroundColor(.secondary)
                }
                
                // Form Fields
                VStack(spacing: 16) {
                    TextField("E-posta adresiniz", text: $email)
                        .keyboardType(.emailAddress)
                        .autocapitalization(.none)
                        .padding()
                        .background(Color(.systemGray6))
                        .cornerRadius(12)
                    
                    SecureField("Şifreniz", text: $password)
                        .padding()
                        .background(Color(.systemGray6))
                        .cornerRadius(12)
                    
                    if let error = errorMessage {
                        Text(error)
                            .font(.caption)
                            .foregroundColor(.red)
                            .multilineTextAlignment(.center)
                    }
                }
                
                // Login Button
                Button(action: signIn) {
                    if isLoading {
                        ProgressView()
                            .progressViewStyle(CircularProgressViewStyle(tint: .white))
                            .frame(maxWidth: .infinity)
                            .padding()
                    } else {
                        Text("Giriş Yap")
                            .fontWeight(.bold)
                            .frame(maxWidth: .infinity)
                            .padding()
                    }
                }
                .background(Color.blue)
                .foregroundColor(.white)
                .cornerRadius(14)
                .disabled(isLoading)
                
                // Register link
                NavigationLink("Hesabınız yok mu? Kayıt olun →", destination: RegisterScreen())
                    .font(.subheadline)
                    .foregroundColor(.blue)
                
                Spacer()
            }
            .padding(.horizontal, 28)
            .navigationBarHidden(true)
        }
    }
    
    private func signIn() {
        guard !email.isEmpty, !password.isEmpty else {
            errorMessage = "E-posta ve şifre alanlarını doldurunuz."
            return
        }
        isLoading = true
        errorMessage = nil
        Auth.auth().signIn(withEmail: email, password: password) { _, error in
            DispatchQueue.main.async {
                isLoading = false
                if let error = error {
                    errorMessage = error.localizedDescription
                }
                // On success, AuthStateManager listener fires automatically → navigates to main app
            }
        }
    }
}

// MARK: - Register Screen (Production Template)
struct RegisterScreen: View {
    @State private var email: String = ""
    @State private var password: String = ""
    @State private var fullName: String = ""
    @State private var isLoading: Bool = false
    @State private var errorMessage: String? = nil
    @Environment(\.dismiss) private var dismiss
    
    var body: some View {
        VStack(spacing: 24) {
            Text("Hesap Oluştur")
                .font(.title)
                .fontWeight(.bold)
                .frame(maxWidth: .infinity, alignment: .leading)
            
            VStack(spacing: 16) {
                TextField("Ad Soyad", text: $fullName)
                    .padding()
                    .background(Color(.systemGray6))
                    .cornerRadius(12)
                
                TextField("E-posta adresiniz", text: $email)
                    .keyboardType(.emailAddress)
                    .autocapitalization(.none)
                    .padding()
                    .background(Color(.systemGray6))
                    .cornerRadius(12)
                
                SecureField("Şifreniz (en az 6 karakter)", text: $password)
                    .padding()
                    .background(Color(.systemGray6))
                    .cornerRadius(12)
                
                if let error = errorMessage {
                    Text(error)
                        .font(.caption)
                        .foregroundColor(.red)
                }
            }
            
            Button(action: register) {
                if isLoading {
                    ProgressView()
                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                        .frame(maxWidth: .infinity)
                        .padding()
                } else {
                    Text("Kayıt Ol ve Başla")
                        .fontWeight(.bold)
                        .frame(maxWidth: .infinity)
                        .padding()
                }
            }
            .background(Color.green)
            .foregroundColor(.white)
            .cornerRadius(14)
            .disabled(isLoading)
            
            Spacer()
        }
        .padding()
    }
    
    private func register() {
        guard !email.isEmpty, !password.isEmpty, !fullName.isEmpty else {
            errorMessage = "Tüm alanları doldurunuz."
            return
        }
        isLoading = true
        errorMessage = nil
        Auth.auth().createUser(withEmail: email, password: password) { result, error in
            DispatchQueue.main.async {
                isLoading = false
                if let error = error {
                    errorMessage = error.localizedDescription
                    return
                }
                // Save user profile to Firestore after successful registration
                if let uid = result?.user.uid {
                    let db = Firestore.firestore()
                    db.collection("users").document(uid).setData([
                        "uid": uid,
                        "fullName": fullName,
                        "email": email,
                        "createdAt": FieldValue.serverTimestamp(),
                        "updatedAt": FieldValue.serverTimestamp(),
                        "companyType": "",
                        "entrepreneurType": "",
                        "businessSector": "",
                        "onboardingCompleted": false
                    ])
                }
                // AuthStateManager listener handles navigation automatically
            }
        }
    }
}
