package se.supernovait.doobypro.domain.model.support.faq

import se.supernovait.app.core.domain.model.faq.FAQ
import se.supernovait.app.core.domain.model.faq.StandardFAQCategory

object FAQData {
    val faqs = listOf(
        // Getting Started
        FAQ(
            id = "faq_1",
            category = StandardFAQCategory.GETTING_STARTED,
            question = "How do I create an account?",
            answer = "To create an account, open the app and follow the setup wizard on the first launch. Enter your name, email, and other required information. Your account will be created immediately."
        ),
        FAQ(
            id = "faq_2",
            category = StandardFAQCategory.GETTING_STARTED,
            question = "Can I change my account information later?",
            answer = "Yes, you can update your account information anytime. Go to Settings > Account and tap on the section you want to edit. Make your changes and save."
        ),

        // Orders
        FAQ(
            id = "faq_3",
            category = DoobyFAQCategory.ORDERS,
            question = "How do I create a new order?",
            answer = "Tap the + button on the dashboard to create a new order. You can either scan a customer QR code or manually enter their details. Then fill in the order information and submit."
        ),
        FAQ(
            id = "faq_4",
            category = DoobyFAQCategory.ORDERS,
            question = "Can I edit an order after creating it?",
            answer = "Yes, you can edit orders in the New, In Progress, and Ready statuses. Open the order details and tap Edit. Some fields like the delivery method are locked and can't be changed once the order reaches Ready status."
        ),
        FAQ(
            id = "faq_5",
            category = DoobyFAQCategory.ORDERS,
            question = "What should I do if I create an order by mistake?",
            answer = "You can delete orders that are in the New status. Open the order, tap Delete, and confirm. Orders in other statuses cannot be deleted."
        ),
        FAQ(
            id = "faq_6",
            category = DoobyFAQCategory.ORDERS,
            question = "How do I change an order's status?",
            answer = "Open the order details and use the next-status button. Orders move from New to In Progress to Ready. After that, pickup orders become Picked Up, and home delivery orders go to Out for Delivery and then Delivered."
        ),
        FAQ(
            id = "faq_7",
            category = DoobyFAQCategory.ORDERS,
            question = "How do I find a specific order?",
            answer = "On the Orders screen, use search and filters to find orders by customer name, order ID, service type, or date range."
        ),
        FAQ(
            id = "faq_8",
            category = DoobyFAQCategory.ORDERS,
            question = "Where can I see old or cancelled orders?",
            answer = "Open the menu on the Orders screen and choose the historical orders option. It lists cancelled orders and older completed orders that no longer appear in the main tabs."
        ),

        // Delivery
        FAQ(
            id = "faq_9",
            category = StandardFAQCategory.DELIVERY,
            question = "What are the delivery options?",
            answer = "We offer two delivery options: Standard and Express. Standard is the regular delivery option, while Express provides faster processing for customers who need their items urgently."
        ),
        FAQ(
            id = "faq_10",
            category = StandardFAQCategory.DELIVERY,
            question = "What's the difference between in-store pickup and home delivery?",
            answer = "In-store pickup allows customers to collect their items from your location. Home delivery means the items will be delivered to the customer's address. Choose the method that works best for each order."
        ),
        FAQ(
            id = "faq_11",
            category = StandardFAQCategory.DELIVERY,
            question = "How do I set delivery dates?",
            answer = "When creating an order, you can select any date you want as the delivery date. The app allows open date selection so you have full flexibility."
        ),

        // Account
        FAQ(
            id = "faq_12",
            category = StandardFAQCategory.ACCOUNT,
            question = "How do I update my profile?",
            answer = "Go to Account > User Profile to update your personal information. You can change your name, phone, email, and other details. Your user ID is permanent and cannot be changed."
        ),
        FAQ(
            id = "faq_13",
            category = StandardFAQCategory.ACCOUNT,
            question = "How do I update my company information?",
            answer = "Go to Account > Company Profile to update your company details including name, address, phone, email, and location. Company ID is permanent."
        ),
        FAQ(
            id = "faq_14",
            category = StandardFAQCategory.ACCOUNT,
            question = "Where can I find my license information?",
            answer = "Your license details are available in Account > License. This section is read-only and shows your license status, tier, and validity dates."
        ),

        // Technical
        FAQ(
            id = "faq_15",
            category = StandardFAQCategory.TECHNICAL,
            question = "The app is running slowly. What can I do?",
            answer = "Try these steps: 1) Close and reopen the app, 2) Clear the app cache in settings, 3) Make sure you have enough storage space, 4) Update to the latest version. If problems persist, contact support."
        ),
        FAQ(
            id = "faq_16",
            category = StandardFAQCategory.TECHNICAL,
            question = "I'm having trouble logging in.",
            answer = "First, check your internet connection. Then verify your email and password are correct. If you've forgotten your password, use the password reset option. For continued issues, contact our support team."
        ),
        FAQ(
            id = "faq_17",
            category = StandardFAQCategory.TECHNICAL,
            question = "How do I report a bug?",
            answer = "Go to Support > Support and select 'Bug Report' as the request type. Describe the issue in detail, including when it happens and what you were doing. Attach screenshots if possible."
        ),

        // Billing
        FAQ(
            id = "faq_18",
            category = StandardFAQCategory.BILLING,
            question = "How are customers charged for orders?",
            answer = "Customers can be charged based on your pricing settings. You can set prices for services and delivery options. The app calculates totals automatically based on the service selected and delivery method."
        ),
        FAQ(
            id = "faq_19",
            category = StandardFAQCategory.BILLING,
            question = "Can I change prices for services?",
            answer = "Yes, you can update service prices anytime. Open the Services from the menu, tap the service, and change its price. Changes apply to new orders created after the update."
        ),

        // Storage
        FAQ(
            id = "faq_20",
            category = DoobyFAQCategory.STORAGE,
            question = "What is a storage location?",
            answer = "A storage location is the shelf, rack, or bin where you keep a customer's items while their order is being processed. It helps your team find items quickly when the order is ready."
        ),
        FAQ(
            id = "faq_21",
            category = DoobyFAQCategory.STORAGE,
            question = "Do I have to choose a storage location for every order?",
            answer = "It depends on your storage allocation mode. In manual mode, you must select a storage location before creating an order. In automatic mode, the field is hidden and the app assigns a location for you."
        ),
        FAQ(
            id = "faq_22",
            category = DoobyFAQCategory.STORAGE,
            question = "When is a storage location released?",
            answer = "A storage location is released automatically when the order is completed: picked up, delivered, or cancelled. It then becomes available for new orders."
        ),
        FAQ(
            id = "faq_23",
            category = DoobyFAQCategory.STORAGE,
            question = "Can I print a storage location tag?",
            answer = "Yes. Open the order details and tap Print Storage Tag. Attach the tag to the customer's items so they are easy to match with the order. A connected printer is required."
        ),

        // Services
        FAQ(
            id = "faq_24",
            category = DoobyFAQCategory.SERVICES,
            question = "How do I add a new service?",
            answer = "Open the Services screen and tap the + button. Enter a title, description, and price, then save. The service will be available when creating new orders."
        ),
        FAQ(
            id = "faq_25",
            category = DoobyFAQCategory.SERVICES,
            question = "How do I edit a service?",
            answer = "Open the Services screen, tap the service you want to change, and update its title, description, or price. Changes apply to new orders only. Existing orders keep their original details."
        ),
        FAQ(
            id = "faq_26",
            category = DoobyFAQCategory.SERVICES,
            question = "Can I use a different service than the default for an order?",
            answer = "Yes. The default service is pre-selected when you create an order, but you can change it to match the customer's request."
        ),
        FAQ(
            id = "faq_27",
            category = DoobyFAQCategory.SERVICES,
            question = "How is the expected completion time calculated?",
            answer = "The expected completion time is generated automatically from your order settings and the selected service. You can override it manually when creating the order if the customer needs a different timeline."
        ),

        // Settings
        FAQ(
            id = "faq_28",
            category = StandardFAQCategory.SETTINGS,
            question = "How do I change the app theme?",
            answer = "Go to Settings > Theme and choose Light, Dark, or Device default. Device default follows your phone's system appearance."
        ),
        FAQ(
            id = "faq_29",
            category = StandardFAQCategory.SETTINGS,
            question = "How do I change the currency or date format?",
            answer = "Go to Settings > Common. There you can set the currency and date format used across orders, receipts, and reports."
        ),
        FAQ(
            id = "faq_30",
            category = StandardFAQCategory.SETTINGS,
            question = "Can I change the app language?",
            answer = "The app is currently available in English only. Language selection is already in Settings and more languages will be added in future updates."
        ),
        FAQ(
            id = "faq_31",
            category = StandardFAQCategory.SETTINGS,
            question = "How do I set default values for new orders?",
            answer = "Go to Settings > Order. You can set the default delivery option, delivery method, and delivery date, and configure the order number format. These values are pre-filled on every new order, and you can still change them per order."
        ),
        FAQ(
            id = "faq_32",
            category = StandardFAQCategory.SETTINGS,
            question = "How do I set my business hours?",
            answer = "Go to Settings > Business Hours and tap a day to edit it. You can mark a day as Closed, Open 24 hours, or set custom opening and closing times. Business hours are used for notification timing and shown on receipts."
        ),

        // Printing
        FAQ(
            id = "faq_33",
            category = DoobyFAQCategory.PRINTING,
            question = "How do I connect a printer?",
            answer = "Go to Settings > Printer and follow the steps to connect your printer. Make sure the printer is powered on and within range before connecting."
        ),
        FAQ(
            id = "faq_34",
            category = DoobyFAQCategory.PRINTING,
            question = "How do I print a receipt?",
            answer = "Open the order details and tap Print Receipt. The receipt prints directly, without a preview, so check your receipt settings beforehand."
        ),
        FAQ(
            id = "faq_35",
            category = DoobyFAQCategory.PRINTING,
            question = "How do I customize what appears on receipts?",
            answer = "Go to Settings > Receipt to choose the content and style. Your company name, logo, address, phone number, and license number come from your Company Profile."
        ),
        FAQ(
            id = "faq_36",
            category = DoobyFAQCategory.PRINTING,
            question = "My receipt is not printing. What should I do?",
            answer = "Check that the printer is on, has paper, and is connected under Settings > Printer. If it still doesn't print, reconnect the printer and try again. Contact support if the problem continues."
        ),

        // Notifications
        FAQ(
            id = "faq_37",
            category = StandardFAQCategory.NOTIFICATIONS,
            question = "What notifications does the app send?",
            answer = "The app sends automatic alerts for delayed orders, orders that haven't been started, and orders that haven't been picked up. Customers are also notified when their order is created and when its status changes."
        ),
        FAQ(
            id = "faq_38",
            category = StandardFAQCategory.NOTIFICATIONS,
            question = "How do I turn notifications on or off?",
            answer = "Go to Settings > Notifications and enable or disable the alerts you want to receive."
        ),
        FAQ(
            id = "faq_39",
            category = StandardFAQCategory.NOTIFICATIONS,
            question = "How often am I notified about delayed orders?",
            answer = "Delayed order notifications are sent once a day for each order that is past its expected completion time."
        ),

        // Customers
        FAQ(
            id = "faq_40",
            category = StandardFAQCategory.CUSTOMERS,
            question = "How do I add a new customer?",
            answer = "You can create a customer profile when creating an order by entering their details manually. If a customer shows you their QR code and they aren't in your system yet, a profile is created automatically when you scan it."
        ),
        FAQ(
            id = "faq_41",
            category = StandardFAQCategory.CUSTOMERS,
            question = "How does the customer QR code work?",
            answer = "Customers get a QR code in their Dooby app. Tap + on the dashboard and scan it. Their name and details are filled in automatically, so you only need to enter the order information."
        ),
        FAQ(
            id = "faq_42",
            category = StandardFAQCategory.CUSTOMERS,
            question = "How do I see a customer's phone number or email?",
            answer = "Contact details are hidden by default for privacy. Open the order and tap the phone or email field to reveal it."
        ),

        // Other
        FAQ(
            id = "faq_43",
            category = StandardFAQCategory.OTHER,
            question = "Is my data secure?",
            answer = "Yes, we take data security seriously. All communications are encrypted, and your information is stored securely. We comply with data protection regulations."
        ),
        FAQ(
            id = "faq_44",
            category = StandardFAQCategory.OTHER,
            question = "How do I contact support?",
            answer = "You can reach our support team through the Support section in the app. Fill out the support form with your question or issue, and our team will get back to you within 24-48 hours."
        )
    )
}
