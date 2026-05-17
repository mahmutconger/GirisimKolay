import SwiftUI

struct RoadmapDocumentCenterScreen: View {
    @StateObject private var viewModel = RoadmapViewModelSwiftUI()
    
    var body: some View {
        NavigationView {
            ScrollView {
                VStack(alignment: .leading, spacing: 24) {
                    Text("Girişim Serüveniniz")
                        .font(.title2)
                        .fontWeight(.bold)
                        .padding(.horizontal)
                    
                    VStack(spacing: 0) {
                        ForEach(Array(viewModel.steps.enumerated()), id: \.element.id) { index, step in
                            StepperItemSwiftUI(step: step, isLast: index == viewModel.steps.count - 1)
                        }
                    }
                    .padding(.horizontal)

                    if let report = viewModel.latestReport {
                        VStack(alignment: .leading, spacing: 8) {
                            Text(report.title)
                                .font(.headline)
                                .fontWeight(.bold)
                            Text(report.summary)
                                .font(.subheadline)
                                .foregroundColor(.secondary)
                            Text("Durum: \(report.approvalStatus)")
                                .font(.caption)
                                .foregroundColor(.blue)
                        }
                        .padding()
                        .background(Color(.systemBackground))
                        .cornerRadius(18)
                        .padding(.horizontal)
                    }
                    
                    VStack(spacing: 16) {
                        Button(action: {
                            viewModel.generatePdf(sessionId: UserDefaults.standard.string(forKey: "active_chat_session_id"))
                        }) {
                            HStack {
                                if viewModel.isGeneratingPdf {
                                    ProgressView()
                                        .progressViewStyle(CircularProgressViewStyle(tint: .white))
                                    Text("PDF Üretiliyor...")
                                        .fontWeight(.bold)
                                } else {
                                    Image(systemName: "square.and.arrow.down.fill")
                                    Text("Girişim Hazırlık Raporunu İndir")
                                        .fontWeight(.bold)
                                }
                            }
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(viewModel.isGeneratingPdf ? Color.gray : Color.blue)
                            .foregroundColor(.white)
                            .cornerRadius(16)
                        }
                        .disabled(viewModel.isGeneratingPdf)
                        
                        Button(action: {
                            viewModel.sendToExpert()
                        }) {
                            HStack {
                                if viewModel.isSendingToExpert {
                                    ProgressView()
                                        .progressViewStyle(CircularProgressViewStyle(tint: .blue))
                                    Text("Uzmana İletiliyor...")
                                        .fontWeight(.bold)
                                } else {
                                    Image(systemName: "paperplane.fill")
                                    Text("Mali Müşavir Onayına Gönder")
                                        .fontWeight(.bold)
                                }
                            }
                            .frame(maxWidth: .infinity)
                            .padding()
                            .background(Color.clear)
                            .foregroundColor(.blue)
                            .overlay(
                                RoundedRectangle(cornerRadius: 16)
                                    .stroke(Color.blue, lineWidth: 2)
                            )
                        }
                        .disabled(viewModel.isSendingToExpert)
                    }
                    .padding()
                }
                .padding(.vertical)
            }
            .navigationTitle("Yol Haritası & Belgeler")
            .navigationBarTitleDisplayMode(.inline)
            .background(Color(.systemBackground))
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

struct StepperItemSwiftUI: View {
    let step: RoadmapStepModel
    let isLast: Bool
    
    var body: some View {
        HStack(alignment: .top, spacing: 16) {
            VStack(spacing: 0) {
                ZStack {
                    Circle()
                        .fill(step.isCompleted ? Color.green : (step.isActive ? Color.blue : Color(.systemGray5)))
                        .frame(width: 24, height: 24)
                    
                    if step.isCompleted {
                        Image(systemName: "checkmark")
                            .font(.system(size: 12, weight: .bold))
                            .foregroundColor(.white)
                    } else if step.isActive {
                        Circle()
                            .fill(Color.white)
                            .frame(width: 8, height: 8)
                    }
                }
                
                if !isLast {
                    Rectangle()
                        .fill(step.isCompleted ? Color.green : Color(.systemGray5))
                        .frame(width: 2)
                        .padding(.vertical, 4)
                }
            }
            
            VStack(alignment: .leading, spacing: 4) {
                Text(step.title)
                    .font(.headline)
                    .foregroundColor(step.isActive ? .blue : .primary)
                Text(step.description)
                    .font(.subheadline)
                    .foregroundColor(.secondary)
            }
            .padding(.bottom, isLast ? 0 : 24)
            .padding(.top, 2)
            
            Spacer()
        }
    }
}
