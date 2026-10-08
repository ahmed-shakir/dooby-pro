import SwiftUI
import UserNotifications
import BackgroundTasks
import FirebaseCore
import FirebaseCrashlytics
import FirebaseAnalytics
import Shared

class AppDelegate: NSObject, UIApplicationDelegate, UNUserNotificationCenterDelegate {
    func application(
        _ application: UIApplication,
        didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil
    ) -> Bool {
        FirebaseApp.configure()
        setupCrashlyticsBridge()
        setupAnalyticsBridge()

        UNUserNotificationCenter.current().delegate = self
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            if let error = error {
                print("Notification authorization error: \(error.localizedDescription)")
            }
        }

        registerBackgroundTasks()
        scheduleAppRefresh()

        return true
    }

    func registerBackgroundTasks() {
        BGTaskScheduler.shared.register(forTaskWithIdentifier: "se.supernovait.doobypro.ordercheck", using: nil) { task in
            self.handleAppRefresh(task: task as! BGAppRefreshTask)
        }
    }

    func scheduleAppRefresh() {
        let request = BGAppRefreshTaskRequest(identifier: "se.supernovait.doobypro.ordercheck")
        request.earliestBeginDate = Date(timeIntervalSinceNow: 4 * 3600) // 4 hours interval

        do {
            try BGTaskScheduler.shared.submit(request)
        } catch {
            print("Failed to schedule background refresh: \(error)")
        }
    }

    func handleAppRefresh(task: BGAppRefreshTask) {
        scheduleAppRefresh() // Reschedule for next time

        KoinHelper().checkAndNotifyOrderAlerts()

        task.setTaskCompleted(success: true)
    }

    func userNotificationCenter(
        _ center: UNUserNotificationCenter,
        willPresent notification: UNNotification,
        withCompletionHandler completionHandler: @escaping (UNNotificationPresentationOptions) -> Void
    ) {
        completionHandler([.banner, .sound, .badge, .list])
    }
}

private func setupCrashlyticsBridge() {
    // 1. Record Non-Fatal Exceptions
    IosCrashReporterBridge.shared.onRecordException = { message, stackTrace, attributes in
        let error = NSError(
            domain: "se.supernovait.doobypro",
            code: -1,
            userInfo: [
                NSLocalizedDescriptionKey: message,
                "KotlinStackTrace": stackTrace ?? ""
            ]
        )
        Crashlytics.crashlytics().record(error: error, userInfo: attributes)
    }

    // 2. Log Breadcrumbs
    IosCrashReporterBridge.shared.onLog = { message in
        Crashlytics.crashlytics().log(message)
    }

    // 3. Custom Metadata
    IosCrashReporterBridge.shared.onSetCustomKey = { key, value in
        Crashlytics.crashlytics().setCustomValue(value, forKey: key)
    }

    // 4. User ID Attribution
    IosCrashReporterBridge.shared.onSetUserId = { userId in
        Crashlytics.crashlytics().setUserID(userId ?? "")
    }
}

private func setupAnalyticsBridge() {
    // 1. Track Events
    IosAnalyticsBridge.shared.onTrackEvent = { name, properties in
        Analytics.logEvent(name, parameters: properties)
    }

    // 2. Track Screen Views
    IosAnalyticsBridge.shared.onTrackScreenView = { screenName, screenClass in
        var parameters: [String: Any] = [
            AnalyticsParameterScreenName: screenName
        ]
        if let screenClass = screenClass {
            parameters[AnalyticsParameterScreenClass] = screenClass
        }
        Analytics.logEvent(AnalyticsEventScreenView, parameters: parameters)
    }

    // 3. Set User Properties
    IosAnalyticsBridge.shared.onSetUserProperty = { key, value in
        Analytics.setUserProperty(value, forName: key)
    }

    // 4. User ID Attribution
    IosAnalyticsBridge.shared.onSetUserId = { userId in
        Analytics.setUserID(userId)
    }
}

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var appDelegate

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    KoinHelper().handleDeepLink(url: url.absoluteString)
                }
        }
    }
}
