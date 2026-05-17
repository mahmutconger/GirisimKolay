import SwiftUI
import SharedLogic

class CommunityViewModelSwiftUI: ObservableObject {
    @Published var networkPosts: [CommunityPost] = []
    @Published var expertPosts: [CommunityPost] = []
    
    init() {
        self.networkPosts = [
            CommunityPost(id: "1", authorName: "Ahmet Yılmaz", content: "E-ihracat gümrük beyannamesini nasıl çözdünüz? Özellikle mikro ihracat kapsamında ETGB işlemleri çok karışık geldi.", likes: 12, commentsCount: 4, isPinned: false, authorType: .entrepreneur, isVerifiedExpert: false),
            CommunityPost(id: "2", authorName: "Zeynep Kaya", content: "KOSGEB İş Planı sunumundan yeni çıktım. Püf noktası: Finansal tablolarınızı mutlaka yapay zeka ile önden simüle edin.", likes: 34, commentsCount: 8, isPinned: false, authorType: .entrepreneur, isVerifiedExpert: false)
        ]
        
        self.expertPosts = [
            CommunityPost(id: "3", authorName: "Mali Müşavir Ayşe K.", content: "KOSGEB İş Planı Onayı: İş planınızı sunarken gelir-gider dengenizi reel enflasyon beklentilerine göre 3 yıllık projeksiyon ile hazırlayın. Destekler doğrudan değil, fatura bazlı ödenmektedir.", likes: 156, commentsCount: 23, isPinned: true, authorType: .expert, isVerifiedExpert: true),
            CommunityPost(id: "4", authorName: "Hukukçu Mert A.", content: "Şahıs şirketi açarken 'Genç Girişimci İstisnası' şartlarını taşıyıp taşımadığınızı mutlaka kontrol edin. 3 yıl boyunca gelir vergisi muafiyeti büyük avantaj sağlar.", likes: 89, commentsCount: 11, isPinned: false, authorType: .expert, isVerifiedExpert: true)
        ]
    }
}

struct CommunityHubScreen: View {
    @StateObject private var viewModel = CommunityViewModelSwiftUI()
    @State private var selectedTab = 0
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                Picker("Tabs", selection: $selectedTab) {
                    Text("Girişimci Ağı").tag(0)
                    Text("Uzmanlara Sor").tag(1)
                }
                .pickerStyle(SegmentedPickerStyle())
                .padding()
                .background(Color(.systemBackground))
                
                ScrollView {
                    LazyVStack(spacing: 16) {
                        let posts = selectedTab == 0 ? viewModel.networkPosts : viewModel.expertPosts
                        
                        ForEach(posts, id: \.id) { post in
                            CommunityPostCardSwiftUI(post: post)
                        }
                    }
                    .padding()
                }
                .background(Color(.systemGroupedBackground))
            }
            .navigationTitle("Topluluk & Destek")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}

struct CommunityPostCardSwiftUI: View {
    let post: CommunityPost
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            if post.isPinned {
                HStack(spacing: 4) {
                    Image(systemName: "pin.fill")
                        .font(.caption)
                        .foregroundColor(.blue)
                    Text("Sabitlenmiş Yanıt")
                        .font(.caption)
                        .fontWeight(.bold)
                        .foregroundColor(.blue)
                }
            }
            
            HStack(alignment: .center, spacing: 12) {
                ZStack {
                    Circle()
                        .fill(Color.blue.opacity(0.1))
                        .frame(width: 40, height: 40)
                    Text(String(post.authorName.prefix(1)))
                        .fontWeight(.bold)
                        .foregroundColor(.blue)
                }
                
                VStack(alignment: .leading, spacing: 2) {
                    HStack(spacing: 4) {
                        Text(post.authorName)
                            .font(.headline)
                            .fontWeight(.bold)
                        if post.isVerifiedExpert {
                            Image(systemName: "checkmark.seal.fill")
                                .foregroundColor(.blue)
                                .font(.caption)
                        }
                    }
                    Text(post.isVerifiedExpert ? "Sistem Onaylı Uzman" : "Mikro Girişimci")
                        .font(.caption)
                        .foregroundColor(.secondary)
                }
                Spacer()
            }
            
            Text(post.content)
                .font(.subheadline)
                .lineSpacing(4)
                .padding(.top, 4)
            
            Divider()
                .padding(.vertical, 4)
            
            HStack(spacing: 24) {
                ActionItemSwiftUI(icon: "hand.thumbsup", text: "\(post.likes) Beğeni")
                ActionItemSwiftUI(icon: "bubble.right", text: "\(post.commentsCount) Yorum")
            }
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(16)
        .shadow(color: Color.black.opacity(0.05), radius: 5, y: 2)
    }
}

struct ActionItemSwiftUI: View {
    let icon: String
    let text: String
    
    var body: some View {
        HStack(spacing: 6) {
            Image(systemName: icon)
                .foregroundColor(.secondary)
            Text(text)
                .font(.footnote)
                .foregroundColor(.secondary)
        }
    }
}
