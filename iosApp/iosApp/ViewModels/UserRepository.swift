import SwiftUI
import FirebaseFirestore
import FirebaseAuth
import FirebaseFunctions

private enum FirebaseServiceConfiguration {
    static let region = "europe-west1"
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

struct ChatCitationModel: Hashable, Identifiable {
    let sourceName: String
    let section: String?
    let snippet: String?
    let sourceURL: String?

    var id: String {
        [sourceName, section ?? "", sourceURL ?? ""].joined(separator: "::")
    }
}

struct ProfileDeltaModel {
    let businessIdea: String?
    let businessSector: String?
    let preferredCompanyType: String?
    let experienceLevel: String?
    let fundingNeed: String?
    let legalConcerns: [String]
}

struct ChatMessageModel: Identifiable {
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

struct ChatSendResponse {
    let sessionId: String
    let message: ChatMessageModel
    let answer: String
    let citations: [ChatCitationModel]
    let profileDelta: ProfileDeltaModel?
    let confidence: Double
    let nextActions: [String]
    let insufficientEvidence: Bool
}

struct RoadmapStepModel: Identifiable {
    let id: String
    let title: String
    let description: String
    let isCompleted: Bool
    let isActive: Bool
}

struct RoadmapReportModel: Identifiable {
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

private enum RepositoryError: LocalizedError {
    case unauthenticated
    case malformedResponse
    case missingSession
    case missingReport

    var errorDescription: String? {
        switch self {
        case .unauthenticated:
            return "Kimlik doğrulama gerekli."
        case .malformedResponse:
            return "Sunucudan beklenen veri alınamadı."
        case .missingSession:
            return "Önce bir sohbet oturumu başlatın."
        case .missingReport:
            return "Rapor bulunamadı."
        }
    }
}

private func mapProfile(_ raw: Any?) -> ProfileDeltaModel? {
    guard let data = raw as? [String: Any] else { return nil }
    return ProfileDeltaModel(
        businessIdea: data["businessIdea"] as? String,
        businessSector: data["businessSector"] as? String,
        preferredCompanyType: data["preferredCompanyType"] as? String,
        experienceLevel: data["experienceLevel"] as? String,
        fundingNeed: data["fundingNeed"] as? String,
        legalConcerns: data["legalConcerns"] as? [String] ?? []
    )
}

private func mapCitations(_ raw: Any?) -> [ChatCitationModel] {
    guard let items = raw as? [[String: Any]] else { return [] }
    return items.map {
        ChatCitationModel(
            sourceName: $0["sourceName"] as? String ?? "Kaynak",
            section: $0["section"] as? String,
            snippet: $0["snippet"] as? String,
            sourceURL: $0["sourceUrl"] as? String
        )
    }
}

private func mapChatMessage(_ raw: [String: Any]) throws -> ChatMessageModel {
    guard
        let id = raw["id"] as? String,
        let sessionId = raw["sessionId"] as? String
    else {
        throw RepositoryError.malformedResponse
    }

    return ChatMessageModel(
        id: id,
        sessionId: sessionId,
        text: raw["text"] as? String ?? "",
        isFromUser: raw["isFromUser"] as? Bool ?? false,
        timestamp: (raw["timestamp"] as? NSNumber)?.int64Value ?? 0,
        citations: mapCitations(raw["citations"]),
        profileDelta: mapProfile(raw["profileDelta"]),
        confidence: (raw["confidence"] as? NSNumber)?.doubleValue,
        nextActions: raw["nextActions"] as? [String] ?? []
    )
}

private func mapRoadmapReport(_ raw: [String: Any]) throws -> RoadmapReportModel {
    guard
        let id = raw["id"] as? String,
        let userId = raw["userId"] as? String,
        let sessionId = raw["sessionId"] as? String
    else {
        throw RepositoryError.malformedResponse
    }

    return RoadmapReportModel(
        id: id,
        userId: userId,
        sessionId: sessionId,
        title: raw["title"] as? String ?? "Girişim Hazırlık Raporu",
        summary: raw["summary"] as? String ?? "",
        fileUrl: raw["downloadUrl"] as? String ?? raw["fileUrl"] as? String ?? "",
        generatedAt: (raw["generatedAt"] as? NSNumber)?.int64Value ?? 0,
        approvalStatus: raw["approvalStatus"] as? String ?? "IDLE",
        nextActions: raw["nextActions"] as? [String] ?? []
    )
}

final class UserRepository {
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
            completion(
                UserProfilePayload(
                    id: uid,
                    fullName: data["fullName"] as? String ?? "",
                    email: data["email"] as? String ?? "",
                    companyType: data["companyType"] as? String ?? "",
                    entrepreneurType: data["entrepreneurType"] as? String ?? "",
                    businessSector: data["businessSector"] as? String ?? "",
                    onboardingCompleted: data["onboardingCompleted"] as? Bool ?? false
                )
            )
        }
    }
}

final class LiveChatRepository {
    private let db = Firestore.firestore()
    private let functions = Functions.functions(region: FirebaseServiceConfiguration.region)

    func sendMessage(
        sessionId: String?,
        text: String,
        completion: @escaping (Result<ChatSendResponse, Error>) -> Void
    ) {
        guard Auth.auth().currentUser != nil else {
            completion(.failure(RepositoryError.unauthenticated))
            return
        }

        functions.httpsCallable("sendChatMessage").call([
            "sessionId": sessionId as Any,
            "text": text,
            "clientRequestId": UUID().uuidString
        ]) { result, error in
            if let error {
                completion(.failure(error))
                return
            }
            guard
                let payload = result?.data as? [String: Any],
                let responseSessionId = payload["sessionId"] as? String,
                let rawMessage = payload["message"] as? [String: Any]
            else {
                completion(.failure(RepositoryError.malformedResponse))
                return
            }

            do {
                let message = try mapChatMessage(rawMessage)
                completion(
                    .success(
                        ChatSendResponse(
                            sessionId: responseSessionId,
                            message: message,
                            answer: payload["answer"] as? String ?? message.text,
                            citations: mapCitations(payload["citations"]),
                            profileDelta: mapProfile(payload["profileDelta"]),
                            confidence: (payload["confidence"] as? NSNumber)?.doubleValue ?? 0,
                            nextActions: payload["nextActions"] as? [String] ?? [],
                            insufficientEvidence: payload["insufficientEvidence"] as? Bool ?? false
                        )
                    )
                )
            } catch {
                completion(.failure(error))
            }
        }
    }

    func loadSession(sessionId: String, completion: @escaping (Result<[ChatMessageModel], Error>) -> Void) {
        guard let uid = Auth.auth().currentUser?.uid else {
            completion(.failure(RepositoryError.unauthenticated))
            return
        }

        db.collection("users")
            .document(uid)
            .collection("chatSessions")
            .document(sessionId)
            .collection("messages")
            .order(by: "timestamp")
            .getDocuments { snapshot, error in
                if let error {
                    completion(.failure(error))
                    return
                }
                let messages = (snapshot?.documents ?? []).compactMap { document in
                    try? mapChatMessage(document.data())
                }
                completion(.success(messages))
            }
    }
}

final class LiveRoadmapRepository {
    private let db = Firestore.firestore()
    private let functions = Functions.functions(region: FirebaseServiceConfiguration.region)
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
        guard Auth.auth().currentUser != nil else {
            completion(.failure(RepositoryError.unauthenticated))
            return
        }

        functions.httpsCallable("generateRoadmapReport").call(["sessionId": sessionId]) { result, error in
            if let error {
                completion(.failure(error))
                return
            }
            guard let payload = result?.data as? [String: Any] else {
                completion(.failure(RepositoryError.malformedResponse))
                return
            }
            do {
                completion(.success(try mapRoadmapReport(payload)))
            } catch {
                completion(.failure(error))
            }
        }
    }

    func loadReport(reportId: String, completion: @escaping (Result<RoadmapReportModel, Error>) -> Void) {
        guard let uid = Auth.auth().currentUser?.uid else {
            completion(.failure(RepositoryError.unauthenticated))
            return
        }

        db.collection("users")
            .document(uid)
            .collection("reports")
            .document(reportId)
            .getDocument { snapshot, error in
                if let error {
                    completion(.failure(error))
                    return
                }
                guard let data = snapshot?.data() else {
                    completion(.failure(RepositoryError.missingReport))
                    return
                }
                do {
                    completion(.success(try mapRoadmapReport(data)))
                } catch {
                    completion(.failure(error))
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
                    switch result {
                    case .success(let history):
                        if !history.isEmpty {
                            self?.messages = history
                        }
                    case .failure:
                        break
                    }
                }
            }
        }
    }

    func sendMessage() {
        let trimmed = inputText.trimmingCharacters(in: .whitespacesAndNewlines)
        guard !trimmed.isEmpty else { return }
        guard Auth.auth().currentUser != nil else {
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
                    self?.repository.loadSession(sessionId: response.sessionId) { loadResult in
                        DispatchQueue.main.async {
                            switch loadResult {
                            case .success(let history):
                                self?.messages = history
                            case .failure:
                                self?.messages.append(response.message)
                            }
                        }
                    }
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
        steps = repository.steps()
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
        guard Auth.auth().currentUser != nil else {
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
