import SwiftUI
import UIKit

struct AIProfileChatScreen: View {
    @StateObject private var viewModel = ChatViewModel()
    
    var body: some View {
        NavigationView {
            VStack(spacing: 0) {
                ScrollView {
                    VStack(spacing: 12) {
                        ForEach(viewModel.messages, id: \.id) { message in
                            MessageBubble(message: message)
                        }
                        if viewModel.isTyping {
                            TypingIndicator()
                        }
                    }
                    .padding()
                }
                
                ChatInputBar(inputText: $viewModel.inputText) {
                    viewModel.sendMessage()
                }
            }
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .principal) {
                    HStack {
                        Circle()
                            .fill(Color.green)
                            .frame(width: 10, height: 10)
                        Text("GirişimKolay AI Danışmanı")
                            .font(.headline)
                    }
                }
            }
            .overlay(alignment: .top) {
                if let error = viewModel.errorMessage {
                    Text(error)
                        .font(.caption)
                        .padding(.horizontal, 12)
                        .padding(.vertical, 8)
                        .background(Color.red.opacity(0.12))
                        .foregroundColor(.red)
                        .cornerRadius(12)
                        .padding(.top, 8)
                }
            }
        }
    }
}

struct MessageBubble: View {
    let message: ChatMessageModel
    
    var body: some View {
        HStack {
            if message.isFromUser { Spacer() }
            
            VStack(alignment: message.isFromUser ? .trailing : .leading, spacing: 4) {
                Text(message.text)
                    .padding(12)
                    .background(message.isFromUser ? Color.blue : Color(.systemGray5))
                    .foregroundColor(message.isFromUser ? .white : .primary)
                    .cornerRadius(16)
                    .clipShape(RoundedCornerShape(
                        topLeft: 16,
                        topRight: 16,
                        bottomLeft: message.isFromUser ? 16 : 4,
                        bottomRight: message.isFromUser ? 4 : 16
                    ))
                
                if !message.citations.isEmpty {
                    ForEach(message.citations, id: \.id) { citation in
                        let hasUrl = !(citation.sourceURL ?? "").isEmpty
                        HStack(spacing: 3) {
                            if hasUrl {
                                Image(systemName: "arrow.up.right.square")
                                    .font(.system(size: 9))
                                    .foregroundColor(.blue)
                            }
                            Text(citation.sourceName)
                                .font(.caption2)
                                .foregroundColor(hasUrl ? .blue : .secondary)
                        }
                        .padding(.horizontal, 8)
                        .padding(.vertical, 4)
                        .background(hasUrl ? Color.blue.opacity(0.1) : Color(.systemGray6))
                        .cornerRadius(10)
                        .onTapGesture {
                            guard hasUrl,
                                  let urlString = citation.sourceURL,
                                  let url = URL(string: urlString) else { return }
                            UIApplication.shared.open(url)
                        }
                    }
                }
            }
            
            if !message.isFromUser { Spacer() }
        }
    }
}

struct RoundedCornerShape: Shape {
    var topLeft: CGFloat
    var topRight: CGFloat
    var bottomLeft: CGFloat
    var bottomRight: CGFloat
    
    func path(in rect: CGRect) -> Path {
        var path = Path()
        // custom rounded rect implementation or just use standard cornerRadius for simplicity
        path.addRoundedRect(in: rect, cornerSize: CGSize(width: 16, height: 16))
        return path
    }
}

struct TypingIndicator: View {
    @State private var scale: CGFloat = 0.5
    
    var body: some View {
        HStack {
            HStack(spacing: 4) {
                Circle().frame(width: 6, height: 6).scaleEffect(scale)
                    .animation(Animation.easeInOut(duration: 0.6).repeatForever().delay(0), value: scale)
                Circle().frame(width: 6, height: 6).scaleEffect(scale)
                    .animation(Animation.easeInOut(duration: 0.6).repeatForever().delay(0.2), value: scale)
                Circle().frame(width: 6, height: 6).scaleEffect(scale)
                    .animation(Animation.easeInOut(duration: 0.6).repeatForever().delay(0.4), value: scale)
            }
            .padding(12)
            .background(Color(.systemGray5))
            .cornerRadius(16)
            
            Spacer()
        }
        .onAppear { scale = 1.0 }
    }
}

struct ChatInputBar: View {
    @Binding var inputText: String
    var onSend: () -> Void
    
    var body: some View {
        HStack {
            TextField("Sorunuzu yazın...", text: $inputText)
                .padding(.horizontal, 16)
                .padding(.vertical, 10)
                .background(Color(.systemGray6))
                .cornerRadius(20)
            
            Button(action: {
                if !inputText.isEmpty { onSend() }
            }) {
                Image(systemName: inputText.isEmpty ? "mic.circle.fill" : "arrow.up.circle.fill")
                    .resizable()
                    .frame(width: 36, height: 36)
                    .foregroundColor(inputText.isEmpty ? .gray : .blue)
            }
        }
        .padding(12)
        .background(Color(.systemBackground))
        .shadow(color: Color.black.opacity(0.05), radius: 5, y: -5)
    }
}
