import SwiftUI
import FirebaseFirestore
import FirebaseAuth

enum BackendConfiguration {
    static let baseURL = (Bundle.main.object(forInfoDictionaryKey: "BACKEND_BASE_URL") as? String) ?? "http://127.0.0.1:8000"
}

struct UserProfilePayload: Identifiable {
    let id: String
    let fullName: String
    let email: String
    let companyType: String
    let entrepreneurType: String
    let businessSector: String
    let onboardingCompleted: Bool
}

struct ChatCitationModel: Codable, Hashable, Identifiable {
    let sourceName: String
    let section: String?
    let snippet: String?
    let sourceURL: String?

    var id: String {
        [sourceName, section ?? "", sourceURL ?? ""].joined(separator: "::")
    }
}

struct ProfileDeltaModel: Codable {
    let businessIdea: String?
    let businessSector: String?
    let preferredCompanyType: String?
    let experienceLevel: String?
    let fundingNeed: String?
    let legalConcerns: [String]
}

struct ChatMessageModel: Codable, Identifiable {
    let id: String
    let sessionId: String
    let text: String
    let isFromUser: Bool
    let timestamp: Int64
    let citations: [ChatCitationModel]
    let profileDelta: ProfileDeltaModel?
    let confidence: Double?
    let nextActions: [String]
}

struct ChatSendResponse: Codable {
    let sessionId: String
    let message: ChatMessageModel
    let answer: String
    let citations: [ChatCitationModel]
    let profileDelta: ProfileDeltaModel?
    let confidence: Double
    let nextActions: [String]
    let insufficientEvidence: Bool
}

struct ChatSessionResponse: Codable {
    let sessionId: String
    let messages: [ChatMessageModel]
}

private struct ChatSendRequest: Codable {
    let sessionId: String?
    let text: String
    let clientRequestId: String
    let userId: String?
}

struct RoadmapStepModel: Identifiable {
    let id: String
    let title: String
    let description: String
    let isCompleted: Bool
    let isActive: Bool
}

struct RoadmapReportModel: Codable, Identifiable {
    let id: String
    let userId: String
    let sessionId: String
    let title: String
    let summary: String
    let fileUrl: String
    let generatedAt: Int64
    let approvalStatus: String
    let nextActions: [String]
}

private struct GenerateReportRequestPayload: Codable {
    let sessionId: String
    let userId: String?
}

private let decoder: JSONDecoder = {
    let decoder = JSONDecoder()
    decoder.keyDecodingStrategy = .convertFromSnakeCase
    return decoder
}()

private let encoder: JSONEncoder = {
    let encoder = JSONEncoder()
    encoder.keyEncodingStrategy = .convertToSnakeCase
    return encoder
}()

private func withFreshFirebaseIDToken(
    completion: @escaping (Result<String, Error>) -> Void
) {
    guard let user = Auth.auth().currentUser else {
        completion(.failure(URLError(.userAuthenticationRequired)))
        return
    }
    user.getIDTokenForcingRefresh(true) { token, error in
        if let error {
            completion(.failure(error))
            return
        }
        guard let token else {
            completion(.failure(URLError(.userAuthenticationRequired)))
            return
        }
        completion(.success(token))
    }
}

class UserRepository {
    private let db = Firestore.firestore()

    func saveUserProfile(uid: String, fullName: String, companyType: String, entrepreneurType: String, businessSector: String = "") {
        db.collection("users").document(uid).setData([
            "uid": uid,
            "fullName": fullName,
            "companyType": companyType,
            "entrepreneurType": entrepreneurType,
            "businessSector": businessSector,
            "onboardingCompleted": !companyType.isEmpty || !entrepreneurType.isEmpty,
            "updatedAt": FieldValue.serverTimestamp()
        ], merge: true)
    }

    func getUserProfile(uid: String, completion: @escaping (UserProfilePayload?) -> Void) {
        db.collection("users").document(uid).getDocument { snapshot, _ in
            guard let data = snapshot?.data() else {
                completion(nil)
                return
            }
            let payload = UserProfilePayload(
                id: uid,
                fullName: data["fullName"] as? String ?? "",
                email: data["email"] as? String ?? "",
                companyType: data["companyType"] as? String ?? "",
                entrepreneurType: data["entrepreneurType"] as? String ?? "",
                businessSector: data["businessSector"] as? String ?? "",
                onboardingCompleted: data["onboardingCompleted"] as? Bool ?? false
            )
            completion(payload)
        }
    }
}

final class LiveChatRepository {
    func sendMessage(
        sessionId: String?,
        text: String,
        completion: @escaping (Result<ChatSendResponse, Error>) -> Void
    ) {
        guard let url = URL(string: "\(BackendConfiguration.baseURL)/api/v1/chat/messages") else {
            completion(.failure(URLError(.badURL)))
            return
        }

        withFreshFirebaseIDToken { result in
            switch result {
            case .failure(let error):
                completion(.failure(error))
            case .success(let token):
                var request = URLRequest(url: url)
                request.httpMethod = "POST"
                request.setValue("application/json", forHTTPHeaderField: "Content-Type")
                request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
                request.httpBody = try? encoder.encode(
                    ChatSendRequest(
                        sessionId: sessionId,
                        text: text,
                        clientRequestId: UUID().uuidString,
                        userId: nil
                    )
                )

                URLSession.shared.dataTask(with: request) { data, _, error in
                    if let error {
                        completion(.failure(error))
                        return
                    }
                    guard let data else {
                        completion(.failure(URLError(.badServerResponse)))
                        return
                    }
                    do {
                        let response = try decoder.decode(ChatSendResponse.self, from: data)
                        completion(.success(response))
                    } catch {
                        completion(.failure(error))
                    }
                }.resume()
            }
        }
    }

    func loadSession(sessionId: String, completion: @escaping (Result<ChatSessionResponse, Error>) -> Void) {
        guard let url = URL(string: "\(BackendConfiguration.baseURL)/api/v1/chat/sessions/\(sessionId)") else {
            completion(.failure(URLError(.badURL)))
            return
        }
        withFreshFirebaseIDToken { result in
            switch result {
            case .failure(let error):
                completion(.failure(error))
            case .success(let token):
                var request = URLRequest(url: url)
                request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
                URLSession.shared.dataTask(with: request) { data, _, error in
                    if let error {
                        completion(.failure(error))
                        return
                    }
                    guard let data else {
                        completion(.failure(URLError(.badServerResponse)))
                        return
                    }
                    do {
                        let response = try decoder.decode(ChatSessionResponse.self, from: data)
                        completion(.success(response))
                    } catch {
                        completion(.failure(error))
                    }
                }.resume()
            }
        }
    }
}

final class LiveRoadmapRepository {
    private let defaultSteps = [
        RoadmapStepModel(id: "1", title: "Şirket Tipi Seçimi", description: "AI profil analiziyle şirket türünüz netleşir.", isCompleted: true, isActive: false),
        RoadmapStepModel(id: "2", title: "Vergi ve SGK Kontrolleri", description: "Mevzuat ve teşvikler iş modelinize göre listelenir.", isCompleted: true, isActive: false),
        RoadmapStepModel(id: "3", title: "Rapor Oluşturma", description: "Canlı oturumunuzdan profesyonel bir hazırlık raporu üretilir.", isCompleted: false, isActive: true),
        RoadmapStepModel(id: "4", title: "Uzman Onayı", description: "Mali müşavir onay simülasyonu ile akış kapanır.", isCompleted: false, isActive: false)
    ]

    func steps() -> [RoadmapStepModel] {
        defaultSteps
    }

    func generateReport(sessionId: String, completion: @escaping (Result<RoadmapReportModel, Error>) -> Void) {
        guard let url = URL(string: "\(BackendConfiguration.baseURL)/api/v1/reports") else {
            completion(.failure(URLError(.badURL)))
            return
        }
        withFreshFirebaseIDToken { result in
            switch result {
            case .failure(let error):
                completion(.failure(error))
            case .success(let token):
                var request = URLRequest(url: url)
                request.httpMethod = "POST"
                request.setValue("application/json", forHTTPHeaderField: "Content-Type")
                request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
                request.httpBody = try? encoder.encode(
                    GenerateReportRequestPayload(sessionId: sessionId, userId: nil)
                )
                URLSession.shared.dataTask(with: request) { data, _, error in
                    if let error {
                        completion(.failure(error))
                        return
                    }
                    guard let data else {
                        completion(.failure(URLError(.badServerResponse)))
                        return
                    }
                    do {
                        let response = try decoder.decode(RoadmapReportModel.self, from: data)
                        completion(.success(response))
                    } catch {
                        completion(.failure(error))
                    }
                }.resume()
            }
        }
    }

    func loadReport(reportId: String, completion: @escaping (Result<RoadmapReportModel, Error>) -> Void) {
        guard let url = URL(string: "\(BackendConfiguration.baseURL)/api/v1/reports/\(reportId)") else {
            completion(.failure(URLError(.badURL)))
            return
        }
        withFreshFirebaseIDToken { result in
            switch result {
            case .failure(let error):
                completion(.failure(error))
            case .success(let token):
                var request = URLRequest(url: url)
                request.setValue("Bearer \(token)", forHTTPHeaderField: "Authorization")
                URLSession.shared.dataTask(with: request) { data, _, error in
                    if let error {
                        completion(.failure(error))
                        return
                    }
                    guard let data else {
                        completion(.failure(URLError(.badServerResponse)))
                        return
                    }
                    do {
                        let response = try decoder.decode(RoadmapReportModel.self, from: data)
                        completion(.success(response))
                    } catch {
                        completion(.failure(error))
                    }
                }.resume()
            }
        }
    }
}

@MainActor
final class ProfileViewModel: ObservableObject {
    @Published var fullName: String = ""
    @Published var companyType: String = ""
    @Published var entrepreneurType: String = ""
    @Published var businessSector: String = ""
    @Published var isLoading: Bool = false

    private let repo = UserRepository()

    func loadProfile() {
        guard let uid = Auth.auth().currentUser?.uid else { return }
        isLoading = true
        repo.getUserProfile(uid: uid) { [weak self] data in
            DispatchQueue.main.async {
                self?.isLoading = false
                self?.fullName = data?.fullName ?? ""
                self?.companyType = data?.companyType ?? ""
                self?.entrepreneurType = data?.entrepreneurType ?? ""
                self?.businessSector = data?.businessSector ?? ""
            }
        }
    }

    func saveProfile() {
        guard let uid = Auth.auth().currentUser?.uid else { return }
        repo.saveUserProfile(
            uid: uid,
            fullName: fullName,
            companyType: companyType,
            entrepreneurType: entrepreneurType,
            businessSector: businessSector
        )
    }

    func signOut() {
        UserDefaults.standard.removeObject(forKey: "active_chat_session_id")
        UserDefaults.standard.removeObject(forKey: "latest_roadmap_report_id")
        try? Auth.auth().signOut()
    }
}

@MainActor
final class ChatViewModel: ObservableObject {
    @Published var messages: [ChatMessageModel] = []
    @Published var isTyping: Bool = false
    @Published var inputText: String = ""
    @Published var errorMessage: String?
    @Published var activeSessionId: String?

    private let repository = LiveChatRepository()

    init() {
        activeSessionId = UserDefaults.standard.string(forKey: "active_chat_session_id")
        let welcome = ChatMessageModel(
            id: UUID().uuidString,
            sessionId: activeSessionId ?? "local-welcome",
            text: "Merhaba! Ben GirişimKolay AI Danışmanınız. Şirket kuruluşu, teşvikler ve mevzuat konusunda yardımcı olabilirim.",
            isFromUser: false,
            timestamp: Int64(Date().timeIntervalSince1970 * 1000),
            citations: [],
            profileDelta: nil,
            confidence: nil,
            nextActions: []
        )
        messages = [welcome]
        if let activeSessionId {
            repository.loadSession(sessionId: activeSessionId) { [weak self] result in
                DispatchQueue.main.async {
                    if case .success(let session) = result {
                        self?.messages = session.messages
                    }
                }
            }
        }
    }

    func sendMessage() {
        let trimmed = inputText.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return }
        guard Auth.auth().currentUser?.uid != nil else {
            errorMessage = "Mesaj göndermek için giriş yapmalısınız."
            return
        }

        let optimistic = ChatMessageModel(
            id: UUID().uuidString,
            sessionId: activeSessionId ?? "pending",
            text: trimmed,
            isFromUser: true,
            timestamp: Int64(Date().timeIntervalSince1970 * 1000),
            citations: [],
            profileDelta: nil,
            confidence: nil,
            nextActions: []
        )
        messages.append(optimistic)
        inputText = ""
        isTyping = true
        errorMessage = nil

        repository.sendMessage(sessionId: activeSessionId, text: trimmed) { [weak self] result in
            DispatchQueue.main.async {
                self?.isTyping = false
                switch result {
                case .success(let response):
                    self?.activeSessionId = response.sessionId
                    UserDefaults.standard.set(response.sessionId, forKey: "active_chat_session_id")
                    if let index = self?.messages.indices.last {
                        self?.messages[index] = optimistic.withSession(response.sessionId)
                    }
                    self?.messages.append(response.message)
                case .failure(let error):
                    self?.errorMessage = error.localizedDescription
                    self?.messages.append(
                        ChatMessageModel(
                            id: UUID().uuidString,
                            sessionId: self?.activeSessionId ?? "error",
                            text: "Şu anda danışmana ulaşılamıyor. Lütfen tekrar deneyin.",
                            isFromUser: false,
                            timestamp: Int64(Date().timeIntervalSince1970 * 1000),
                            citations: [],
                            profileDelta: nil,
                            confidence: nil,
                            nextActions: []
                        )
                    )
                }
            }
        }
    }
}

@MainActor
final class RoadmapViewModelSwiftUI: ObservableObject {
    @Published var steps: [RoadmapStepModel] = []
    @Published var latestReport: RoadmapReportModel?
    @Published var isGeneratingPdf: Bool = false
    @Published var isSendingToExpert: Bool = false
    @Published var errorMessage: String?

    private let repository = LiveRoadmapRepository()

    init() {
        self.steps = repository.steps()
        if let reportId = UserDefaults.standard.string(forKey: "latest_roadmap_report_id") {
            repository.loadReport(reportId: reportId) { [weak self] result in
                DispatchQueue.main.async {
                    if case .success(let report) = result {
                        self?.latestReport = report
                    }
                }
            }
        }
    }

    func generatePdf(sessionId: String?) {
        guard Auth.auth().currentUser?.uid != nil else {
            errorMessage = "Rapor üretmek için giriş yapmalısınız."
            return
        }
        guard let sessionId else {
            errorMessage = "Önce AI sohbeti başlatın."
            return
        }

        isGeneratingPdf = true
        errorMessage = nil
        repository.generateReport(sessionId: sessionId) { [weak self] result in
            DispatchQueue.main.async {
                self?.isGeneratingPdf = false
                switch result {
                case .success(let report):
                    self?.latestReport = report
                    UserDefaults.standard.set(report.id, forKey: "latest_roadmap_report_id")
                case .failure(let error):
                    self?.errorMessage = error.localizedDescription
                }
            }
        }
    }

    func sendToExpert() {
        isSendingToExpert = true
        DispatchQueue.main.asyncAfter(deadline: .now() + 1.2) {
            self.isSendingToExpert = false
            if let report = self.latestReport {
                self.latestReport = RoadmapReportModel(
                    id: report.id,
                    userId: report.userId,
                    sessionId: report.sessionId,
                    title: report.title,
                    summary: report.summary,
                    fileUrl: report.fileUrl,
                    generatedAt: report.generatedAt,
                    approvalStatus: "SENT",
                    nextActions: report.nextActions
                )
            }
        }
    }
}

private extension ChatMessageModel {
    func withSession(_ sessionId: String) -> ChatMessageModel {
        ChatMessageModel(
            id: id,
            sessionId: sessionId,
            text: text,
            isFromUser: isFromUser,
            timestamp: timestamp,
            citations: citations,
            profileDelta: profileDelta,
            confidence: confidence,
            nextActions: nextActions
        )
    }
}
