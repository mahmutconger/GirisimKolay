import SwiftUI
import SharedLogic

class CalendarViewModel: ObservableObject {
    @Published var events: [CalendarEvent] = []
    
    init() {
        self.events = [
            CalendarEvent(id: "1", title: "Geçici Vergi Beyannamesi", dateMillis: Int64(Date().timeIntervalSince1970 * 1000 + 86400000 * 2), type: .taxDeadline, isCritical: true, colorHex: "#F44336"),
            CalendarEvent(id: "2", title: "KOSGEB Kadın Girişimci Desteği Kapanış", dateMillis: Int64(Date().timeIntervalSince1970 * 1000 + 86400000 * 5), type: .grantWindow, isCritical: true, colorHex: "#4CAF50"),
            CalendarEvent(id: "3", title: "Mali Müşavirle Evrak Paylaşımı", dateMillis: Int64(Date().timeIntervalSince1970 * 1000), type: .dailyTask, isCritical: false, colorHex: "#FFC107")
        ]
    }
}

struct BusinessCalendarScreen: View {
    @StateObject private var viewModel = CalendarViewModel()
    
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(spacing: 20) {
                    MockCalendarWidgetSwiftUI(events: viewModel.events)
                        .padding()
                        .background(Color(.systemBackground))
                        .cornerRadius(24)
                        .shadow(color: Color.black.opacity(0.1), radius: 5, y: 2)
                        .padding(.horizontal)
                    
                    VStack(alignment: .leading, spacing: 12) {
                        Text("Kritik Görevler")
                            .font(.title3)
                            .fontWeight(.bold)
                            .padding(.horizontal)
                        
                        ForEach(viewModel.events, id: \.id) { event in
                            EventListItemSwiftUI(event: event)
                                .padding(.horizontal)
                        }
                    }
                }
                .padding(.vertical)
            }
            .navigationTitle("İşletme Takvimi")
            .navigationBarTitleDisplayMode(.inline)
            .background(Color(.systemGroupedBackground).edgesIgnoringSafeArea(.all))
        }
    }
}

struct MockCalendarWidgetSwiftUI: View {
    let events: [CalendarEvent]
    
    let daysOfWeek = ["Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz"]
    
    var body: some View {
        VStack(spacing: 16) {
            Text("Mayıs 2026")
                .font(.headline)
                .fontWeight(.bold)
            
            HStack {
                ForEach(daysOfWeek, id: \.self) { day in
                    Text(day)
                        .font(.caption)
                        .foregroundColor(.gray)
                        .frame(maxWidth: .infinity)
                }
            }
            
            VStack(spacing: 8) {
                // Mock days 1-28
                ForEach(0..<4) { row in
                    HStack {
                        ForEach(1..<8) { col in
                            let dayNumber = row * 7 + col
                            CalendarDayItemSwiftUI(day: "\(dayNumber)", events: events.filter {
                                // Mocking events to show up on 15, 20, 25
                                ($0.id == "1" && dayNumber == 15) || ($0.id == "2" && dayNumber == 20) || ($0.id == "3" && dayNumber == 25)
                            })
                            .frame(maxWidth: .infinity)
                        }
                    }
                }
            }
        }
    }
}

struct CalendarDayItemSwiftUI: View {
    let day: String
    let events: [CalendarEvent]
    
    var body: some View {
        VStack(spacing: 4) {
            Text(day)
                .font(.system(size: 14))
                .fontWeight(day == "15" ? .bold : .regular)
                .frame(width: 30, height: 30)
                .background(day == "15" ? Color.blue.opacity(0.2) : Color.clear)
                .clipShape(Circle())
            
            HStack(spacing: 2) {
                ForEach(events, id: \.id) { event in
                    Circle()
                        .fill(colorForType(event.type))
                        .frame(width: 4, height: 4)
                }
            }
        }
    }
    
    func colorForType(_ type: EventType) -> Color {
        switch type {
        case .taxDeadline: return .red
        case .grantWindow: return .green
        case .dailyTask: return .yellow
        default: return .gray
        }
    }
}

struct EventListItemSwiftUI: View {
    let event: CalendarEvent
    
    var body: some View {
        HStack(spacing: 16) {
            Circle()
                .fill(colorForType(event.type))
                .frame(width: 12, height: 12)
            
            VStack(alignment: .leading, spacing: 4) {
                Text(event.title)
                    .font(.headline)
                    .foregroundColor(event.isCritical ? .primary : .secondary)
                
                Text(formatDate(event.dateMillis))
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }
            Spacer()
        }
        .padding()
        .background(event.isCritical ? Color.blue.opacity(0.1) : Color(.systemBackground))
        .cornerRadius(16)
    }
    
    func colorForType(_ type: EventType) -> Color {
        switch type {
        case .taxDeadline: return .red
        case .grantWindow: return .green
        case .dailyTask: return .yellow
        default: return .gray
        }
    }
    
    func formatDate(_ millis: Int64) -> String {
        let date = Date(timeIntervalSince1970: TimeInterval(millis) / 1000.0)
        let formatter = DateFormatter()
        formatter.dateFormat = "dd MMM, HH:mm"
        formatter.locale = Locale(identifier: "tr_TR")
        return formatter.string(from: date)
    }
}
