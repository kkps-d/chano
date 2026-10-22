import ComposeApp
import SwiftUI

struct ContentView: View {
    @Environment(\.accessibilityReduceTransparency) private var reduceTransparency
    @State private var controller = AppController()

    private var apiBaseURL: String {
        Bundle.main.object(forInfoDictionaryKey: "ChanoAPIBaseURL") as? String
            ?? "http://localhost:3000"
    }

    var body: some View {
        ComposeView(apiBaseURL: apiBaseURL, controller: controller)
            .overlay(alignment: .bottom) {
                refreshControl
                    .padding(.horizontal, 24)
                    .padding(.bottom, 12)
            }
    }

    @ViewBuilder
    private var refreshControl: some View {
        if reduceTransparency {
            refreshButton
                .background(Color(uiColor: .secondarySystemBackground), in: Capsule())
        } else if #available(iOS 26.0, *) {
            // Native Apple rendering automatically follows system Liquid Glass settings.
            refreshButton.glassEffect(.regular.interactive(), in: Capsule())
        } else {
            refreshButton.background(.regularMaterial, in: Capsule())
        }
    }

    private var refreshButton: some View {
        Button(action: { controller.refresh() }) {
            Label("Refresh connection", systemImage: "arrow.clockwise")
                .font(.headline)
                .padding(.horizontal, 24)
                .padding(.vertical, 16)
                .frame(minHeight: 44)
                .contentShape(Capsule())
        }
        .buttonStyle(.plain)
        .accessibilityHint("Checks the connection to your Chano backend")
    }
}

private struct ComposeView: UIViewControllerRepresentable {
    let apiBaseURL: String
    let controller: AppController

    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(apiBaseUrl: apiBaseURL, controller: controller)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
