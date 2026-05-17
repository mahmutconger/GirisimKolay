import SwiftUI
import SharedLogic

class DashboardViewModel: ObservableObject {
    @Published var userStatus: UserStatus?
    @Published var newsFeed: [NewsArticle] = []
    
    init() {
        self.userStatus = UserStatus(name: "Mahmut Can", companyType: "Şahıs Şirketi", entrepreneurType: "Mikro İhracatçı Girişimi")
        self.newsFeed = [
            NewsArticle(id: "1", title: "KOSGEB Mikro İhracat Desteği", source: "KOSGEB", summary: "2026 yılı için mikro ihracatçı girişimlere 100.000 TL'ye kadar hibe desteği onaylandı.", sentimentScore: 1, sentimentText: "Fırsat", sourceTag: "KOSGEB", sentimentColorHex: "#4CAF50"),
            NewsArticle(id: "2", title: "E-Ticaret Mevzuat Güncellemesi", source: "GİB", summary: "E-ticaret yapan şahıs şirketlerinin KDV beyanname sürelerinde değişikliğe gidildi.", sentimentScore: 0, sentimentText: "Mevzuat Değişikliği", sourceTag: "GİB", sentimentColorHex: "#FFC107")
        ]
    }
}

struct DashboardNewsFeedScreen: View {
    @StateObject private var viewModel = DashboardViewModel()
    
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 20) {
                    if let status = viewModel.userStatus {
                        VStack(alignment: .leading, spacing: 4) {
                            Text("Merhaba \(status.name),")
                                .font(.title)
                                .fontWeight(.bold)
                            Text("Girişim serüveninizde bugünkü özetiniz.")
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                        }
                        .frame(maxWidth: .infinity, alignment: .leading)
                        
                        StatusCardSwiftUI(status: status)
                    }
                    
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Sektörel Haber Akışı")
                            .font(.title3)
                            .fontWeight(.bold)
                        
                        ForEach(viewModel.newsFeed, id: \.id) { article in
                            NewsArticleCardSwiftUI(article: article)
                        }
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                }
                .padding()
            }
            .navigationTitle("GirişimKolay")
            .navigationBarTitleDisplayMode(.inline)
        }
    }
}

struct StatusCardSwiftUI: View {
    let status: UserStatus
    
    var body: some View {
        HStack(spacing: 16) {
            ZStack {
                RoundedRectangle(cornerRadius: 12)
                    .fill(Color.blue)
                    .frame(width: 50, height: 50)
                Image(systemName: "briefcase.fill")
                    .foregroundColor(.white)
                    .font(.title2)
            }
            
            VStack(alignment: .leading, spacing: 4) {
                Text(status.companyType)
                    .font(.headline)
                    .fontWeight(.bold)
                Text(status.entrepreneurType)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }
            Spacer()
        }
        .padding()
        .background(Color(.systemBlue).opacity(0.1))
        .cornerRadius(16)
    }
}

struct NewsArticleCardSwiftUI: View {
    let article: NewsArticle
    
    var body: some View {
        VStack(alignment: .leading, spacing: 8) {
            HStack {
                Text(article.source)
                    .font(.caption)
                    .fontWeight(.bold)
                    .foregroundColor(.blue)
                
                Spacer()
                
                Text(article.sentimentText)
                    .font(.caption2)
                    .fontWeight(.bold)
                    .padding(.horizontal, 8)
                    .padding(.vertical, 4)
                    .background(article.sentimentScore == 1 ? Color.green : Color.yellow)
                    .foregroundColor(article.sentimentScore == 1 ? .white : .black)
                    .cornerRadius(8)
            }
            
            Text(article.title)
                .font(.headline)
                .fontWeight(.bold)
            
            Text(article.summary)
                .font(.subheadline)
                .foregroundColor(.secondary)
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(16)
        .shadow(color: Color.black.opacity(0.1), radius: 5, y: 2)
    }
}
