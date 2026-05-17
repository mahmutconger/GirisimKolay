import SwiftUI

struct AnalyticsSnapshotModel {
    let income: Double
    let expenses: Double
    let taskCompletionRate: Int
    let aiInsight: String
}

class AnalyticsViewModelSwiftUI: ObservableObject {
    @Published var data: AnalyticsSnapshotModel?
    
    init() {
        self.data = AnalyticsSnapshotModel(
            income: 125000.0,
            expenses: 45000.0,
            taskCompletionRate: 85,
            aiInsight: "Bu ay operasyonel kaynaklarınızı %15 daha verimli kullandınız. Kargo giderleriniz optimize edildi."
        )
    }
}

struct AnalyticsDashboardScreen: View {
    @StateObject private var viewModel = AnalyticsViewModelSwiftUI()
    
    var body: some View {
        NavigationView {
            ScrollView {
                if let data = viewModel.data {
                    VStack(spacing: 24) {
                        FinanceSectionSwiftUI(data: data)
                        TaskCompletionSectionSwiftUI(data: data)
                        InsightCardSwiftUI(insight: data.aiInsight)
                    }
                    .padding()
                } else {
                    ProgressView()
                }
            }
            .navigationTitle("Analiz ve Raporlar")
            .background(Color(.systemGroupedBackground).edgesIgnoringSafeArea(.all))
        }
    }
}

struct FinanceSectionSwiftUI: View {
    let data: AnalyticsSnapshotModel
    
    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("Gelir / Gider Analizi")
                .font(.headline)
                .fontWeight(.bold)
            
            HStack(spacing: 32) {
                // Simple Pie Chart
                ZStack {
                    Circle()
                        .stroke(Color.red, lineWidth: 20)
                    
                    let incomeRatio = data.income / (data.income + data.expenses)
                    Circle()
                        .trim(from: 0, to: CGFloat(incomeRatio))
                        .stroke(Color.green, style: StrokeStyle(lineWidth: 20, lineCap: .round))
                        .rotationEffect(.degrees(-90))
                }
                .frame(width: 100, height: 100)
                
                VStack(alignment: .leading, spacing: 12) {
                    LegendItemSwiftUI(title: "Gelir", amount: "₺\(String(format: "%.0f", data.income))", color: .green)
                    LegendItemSwiftUI(title: "Gider", amount: "₺\(String(format: "%.0f", data.expenses))", color: .red)
                }
            }
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(24)
        .shadow(color: Color.black.opacity(0.1), radius: 5, y: 2)
    }
}

struct LegendItemSwiftUI: View {
    let title: String
    let amount: String
    let color: Color
    
    var body: some View {
        HStack(spacing: 8) {
            RoundedRectangle(cornerRadius: 4)
                .fill(color)
                .frame(width: 12, height: 12)
            
            VStack(alignment: .leading) {
                Text(title)
                    .font(.caption)
                    .foregroundColor(.gray)
                Text(amount)
                    .font(.subheadline)
                    .fontWeight(.bold)
            }
        }
    }
}

struct TaskCompletionSectionSwiftUI: View {
    let data: AnalyticsSnapshotModel
    
    var body: some View {
        VStack(alignment: .leading, spacing: 12) {
            Text("Haftalık Görev Tamamlama Oranı")
                .font(.headline)
                .fontWeight(.bold)
            
            HStack(spacing: 16) {
                Text("%\(data.taskCompletionRate)")
                    .font(.title)
                    .fontWeight(.black)
                    .foregroundColor(.blue)
                
                GeometryReader { geometry in
                    ZStack(alignment: .leading) {
                        Capsule()
                            .fill(Color(.systemGray5))
                            .frame(height: 16)
                        
                        Capsule()
                            .fill(Color.blue)
                            .frame(width: geometry.size.width * CGFloat(data.taskCompletionRate) / 100, height: 16)
                    }
                }
                .frame(height: 16)
            }
        }
        .padding()
        .background(Color(.systemBackground))
        .cornerRadius(16)
    }
}

struct InsightCardSwiftUI: View {
    let insight: String
    
    var body: some View {
        HStack(alignment: .top, spacing: 12) {
            Image(systemName: "sparkles")
                .foregroundColor(.yellow)
                .font(.title2)
            
            Text(insight)
                .font(.subheadline)
                .foregroundColor(.primary)
                .lineSpacing(4)
        }
        .padding()
        .background(Color.yellow.opacity(0.1))
        .cornerRadius(16)
        .overlay(
            RoundedRectangle(cornerRadius: 16)
                .stroke(Color.yellow.opacity(0.3), lineWidth: 1)
        )
    }
}
