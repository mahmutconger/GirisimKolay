import SwiftUI

struct ContentView: View {
    @EnvironmentObject var authState: AuthStateManager
    
    var body: some View {
        Group {
            if authState.isSignedIn {
                MainTabView()
            } else {
                LoginScreen()
            }
        }
        .animation(.easeInOut, value: authState.isSignedIn)
    }
}

struct MainTabView: View {
    var body: some View {
        TabView {
            AIProfileChatScreen()
                .tabItem {
                    Label("AI Danışman", systemImage: "bubble.left.and.bubble.right.fill")
                }
            
            DashboardNewsFeedScreen()
                .tabItem {
                    Label("Dashboard", systemImage: "squareshape.split.2x2.fill")
                }
            
            BusinessCalendarScreen()
                .tabItem {
                    Label("Takvim", systemImage: "calendar")
                }
            
            AnalyticsDashboardScreen()
                .tabItem {
                    Label("Analiz", systemImage: "chart.pie.fill")
                }
            
            CommunityHubScreen()
                .tabItem {
                    Label("Topluluk", systemImage: "person.3.fill")
                }
            
            RoadmapDocumentCenterScreen()
                .tabItem {
                    Label("Yol Haritası", systemImage: "map.fill")
                }
        }
        .tint(.blue)
    }
}